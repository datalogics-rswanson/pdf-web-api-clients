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

import os
import sys

from datetime import datetime
from datetime import date

import leankit
import support

# ========================================================================================
#  CONFIGURATION
# ========================================================================================

#
# NOTE: there are other configuration values in support.py
#

# url to (base) pdf (shared on dropbox)
pdf_url = '' #TODO: Paste

# flatten document paths
flat_xfdf_path = 'flatten.xfdf'
flat_pdf_path = 'flatten.pdf'
flat_jpg_path = 'flatten.jpg'

# final form data file
fdf_path = 'formdata.fdf'

# decorate document paths
deco_xml_path = 'deco.xml'
deco_pdf_path = 'deco.pdf'
deco_jpg_path = 'deco.jpg'

deco_HeaderFooters = {"TopCenterText":"LeanKit Board Metada", "BottomCenterText":date.today().strftime("%d/%m/%Y"), "color":[0.0, 0.0, 1.0]}

hostname = "datalogics"
username = "" #TODO: Paste
password = "" #TODO: Paste
boardid = '' #TODO: Paste

# ========================================================================================
#  MAIN
# ========================================================================================

details = leankit.GetBoardDetails(username, password, boardid)
archiveDetails = leankit.GetArchiveDetails(username, password, boardid)

dict = {}
dict["BoardName"] = leankit.boardTitle(details)
dict["NumLanes"] = leankit.numTotalLanes(details)
dict["NumActiveCards"] = leankit.totalActiveCards(details) 
dict["NumArchivedCards"] = leankit.totalArchiveCards(archiveDetails)
dict["NumBacklogCards"] = leankit.totalBacklogCards(details)
dict["BiggestLane"] = leankit.getBiggestLane(details)

oldestCardInfo = leankit.getOldestCard(leankit.laneCardInfo(details))
daysOld = (datetime.now() - oldestCardInfo[0]).days
dict["OldestCardAge"] = daysOld
dict["OldestCardName"] = oldestCardInfo[1]
dict["OldestCardLane"] = oldestCardInfo[2]
#out = 
leankit.create_xfdf(dict, flat_xfdf_path)
#print out.read()

tmp_pdf = support.DoFillForm(flat_xfdf_path, pdf_url)
support.DoExportFormData(tmp_pdf, fdf_path)

support.DoFlattenForm(tmp_pdf, flat_pdf_path)
support.DoRenderPages(flat_pdf_path, flat_jpg_path)

support.CreateHeaderFooterDocSettings(deco_HeaderFooters, deco_xml_path)
deco_pdf = support.DoDecorateDocument(flat_pdf_path, deco_xml_path, deco_pdf_path)
support.DoRenderPages(deco_pdf, deco_jpg_path)

