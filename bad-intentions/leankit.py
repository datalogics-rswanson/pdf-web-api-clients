#!/bin/python

# Copyright (c) 2014, Datalogics, Inc. All rights reserved.

# This agreement is between Datalogics, Inc. 101 N. Wacker Drive, Suite 1800,
# Chicago, IL 60606 ("Datalogics") and you, an end user who downloads
# source code examples for integrating to the Datalogics (R) PDF WebAPI (TM)
# ("the Example Code"). By accepting this agreement you agree to be bound
# by the following terms of use for the Example Code.
#
# LICENSE
# -------
# Datalogics hereby grants you a royalty-free, non-exclusive license to
# download and use the Example Code for any lawful purpose. There is no charge
# for use of Example Code.
#
# OWNERSHIP
# ---------
# The Example Code and any related documentation and trademarks are and shall
# remain the sole and exclusive property of Datalogics and are protected by
# the laws of copyright in the U.S. and other countries.
#
# Datalogics and Datalogics PDF WebAPI are trademarks of Datalogics, Inc.
#
# TERM
# ----
# This license is effective until terminated. You may terminate it at any
# other time by destroying the Example Code.
#
# WARRANTY DISCLAIMER
# -------------------
# THE EXAMPLE CODE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER
# EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
#
# DATALOGICS DISCLAIM ALL OTHER WARRANTIES, CONDITIONS, UNDERTAKINGS OR
# TERMS OF ANY KIND, EXPRESS OR IMPLIED, WRITTEN OR ORAL, BY OPERATION OF
# LAW, ARISING BY STATUTE, COURSE OF DEALING, USAGE OF TRADE OR OTHERWISE,
# INCLUDING, WARRANTIES OR CONDITIONS OF MERCHANTABILITY, FITNESS FOR A
# PARTICULAR PURPOSE, SATISFACTORY QUALITY, LACK OF VIRUSES, TITLE,
# NON-INFRINGEMENT, ACCURACY OR COMPLETENESS OF RESPONSES, RESULTS, AND/OR
# LACK OF WORKMANLIKE EFFORT. THE PROVISIONS OF THIS SECTION SET FORTH
# SUBLICENSEE'S SOLE REMEDY AND DATALOGICS'S SOLE LIABILITY WITH RESPECT
# TO THE WARRANTY SET FORTH HEREIN. NO REPRESENTATION OR OTHER AFFIRMATION
# OF FACT, INCLUDING STATEMENTS REGARDING PERFORMANCE OF THE EXAMPLE CODE,
# WHICH IS NOT CONTAINED IN THIS AGREEMENT, SHALL BE BINDING ON DATALOGICS.
# NEITHER DATALOGICS WARRANT AGAINST ANY BUG, ERROR, OMISSION, DEFECT,
# DEFICIENCY, OR NONCONFORMITY IN ANY EXAMPLE CODE.

import requests
import json
import StringIO
from datetime import datetime
from datetime import date

from xml.etree import ElementTree
from xml.etree.ElementTree import Element
from xml.etree.ElementTree import SubElement

baseURL = "https://datalogics.leankit.com/Kanban/Api/"
hostname = "datalogics"
username = "" #TODO: Paste
password = "" #TODO: Paste
boardid = '' #TODO: Paste

def GetBoardDetails(username, password, boardid):
	url = "%s%s%s"% (baseURL,"Boards/",boardid)
	request = requests.get(url, auth=(username, password))
	return json.loads(request.content)

def GetArchiveDetails(username, password, boardid):
	url = "%s%s%s%s"% (baseURL,"Board/",boardid,"/Archive")
	request = requests.get(url, auth=(username, password))
	return json.loads(request.content)


#print details["ReplyData"] [0].keys()

#############
#Active Lanes
#############
def boardTitle(details):
	return details["ReplyData"][0]["Title"]

def boardLaneNum(details):
	return len(boardLanes(details))

def boardLanes(details):
	return details["ReplyData"][0]["Lanes"]
	
def boardLaneNames(details):
	lanesFound = []
	for lane in boardLanes(details):
		lanesFound.append(lane["Title"])	
	
	return lanesFound

#############
#Active Cards
#############
def boardCardsInLane(details):
	for name in boardLanes(details):
		print name["Title"]
		print name["Cards"]
		print "\n\n"

def laneCardInfo(details):
	cardDict = {}
	for name in boardLanes(details):
		for card in name["Cards"]:
			cardDict[card["Id"]] = card["Title"]
	
	return cardDict

def totalActiveCards(details):
	sum = 0;
	for lane in boardLanes(details):
		sum += len(lane["Cards"])
	
	return sum


#############
#Backlog
#############
def boardBacklogNum(details):
	return len(boardBacklogLanes(details))

def boardBacklogLanes(details):
	return details["ReplyData"][0]["Backlog"]

def totalBacklogCards(details):
	sum = 0;
	for lane in boardBacklogLanes(details):
		sum += len(lane["Cards"])
	return sum


################
#Archive Section
################
def boardArchiveDict(archiveDetails):
	return archiveDetails["ReplyData"][0][0]

def boardArchiveLane(details):
	return boardArchiveDict(details)["Lane"]

def boardArchiveLaneCards(details):
	return boardArchiveLane(details)['Cards']

def totalArchiveCards(details):
	return len(boardArchiveLaneCards(details))


##############
#Total Numbers
##############   
def numTotalLanes(details):
	#excluding archive lanes for now because there aren't any sub-lanes
	return boardBacklogNum(details) + boardLaneNum(details)

def numTotalCards(details, archiveDetails):
	return totalArchiveCards(archiveDetails) + totalBacklogCards(details) + totalActiveCards(details)

def getBiggestLane(details):
	laneSize = {}
	
	for lane in boardLanes(details):
		laneSize[lane["Title"]] = len(lane["Cards"])
		
	for lane in boardBacklogLanes(details):
		laneTitle = lane["Title"]
		if laneSize.has_key(laneTitle):
			laneTitle = "backlog:" + laneTitle
		
		laneSize[laneTitle] = len(lane["Cards"])
	
	return sorted(laneSize, key=laneSize.get, reverse = True)[0]


def create_xfdf(field_dict, xfdf_path_out):
	Root = Element('xfdf',attrib={'xmlns':"http://ns.adobe.com/xfdf/","xml:space":"preserve"})
	fields = SubElement(Root, "fields")
	for k,v in field_dict.items():
		a_field = SubElement(fields,"field", name=k)
		field_val=SubElement(a_field,"value")
		field_val.text = str(v)
	
#	out_file = StringIO.StringIO()
#	out_file.write('<?xml version="1.0"?>')
#	out_file.write(ElementTree.tostring(Root))
#	out_file.seek(0)
#	return out_file
	
	xfdf_file = open(xfdf_path_out, 'w')
	xfdf_file.write('<?xml version="1.0"?>')
	xfdf_file.write(ElementTree.tostring(Root))
	xfdf_file.write('\r\n')
	xfdf_file.flush()
	xfdf_file.close()
	
	return xfdf_path_out

	


def getOldestCard(cardInfo):
	cardIDs = cardInfo.keys()
	cardDates = {}
	laneNames = {}
	for ID in cardIDs:
		cardRequest = requests.get("%s%s"%("https://datalogics.leankit.com/Kanban/Api/Board/27651413/GetCard/", ID), auth=(username, password))
		
		replyData = json.loads(cardRequest.content)["ReplyData"]
		cardDates[ID] = datetime.strptime(replyData[0]["CreateDate"], "%m/%d/%Y")
		laneNames[ID] = replyData[0]["LaneTitle"]
	
	sortedDates = sorted(cardDates, key=cardDates.get)
	
	oldestID = sortedDates[0]
	
	return cardDates[oldestID], cardInfo[oldestID], laneNames[oldestID]


#if __name__ == "__main__":
#	dict = {}
#	dict["BoardName"] = boardTitle(details)
#	dict["NumLanes"] = numTotalLanes(details)
#	dict["NumActiveCards"] = totalActiveCards(details) 
#	dict["NumArchivedCards"] = totalArchiveCards(archiveDetails)
#	dict["NumBacklogCards"] = totalBacklogCards(details)
#	dict["BiggestLane"] = getBiggestLane(details)
#	
#	oldestCardInfo = getOldestCard(laneCardInfo(details))
#	daysOld = (datetime.now() - oldestCardInfo[0]).days
#	dict["OldestCardAge"] = daysOld
#	dict["OldestCardName"] = oldestCardInfo[1]
#	dict["OldestCardLane"] = oldestCardInfo[2]
#	out = create_xfdf(dict)
#	print out.read()
