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

from xml.etree import ElementTree
from xml.etree.ElementTree import Element
from xml.etree.ElementTree import SubElement
from datetime import datetime
from datetime import date

from nose.tools import (assert_equal, assert_not_equal, assert_is_none, assert_is_not_none)

import requests

import pdfclient

import filehandler #import FileHandler
import responsehandler #import ResponseHandler

# ========================================================================================

app_id = '' #TODO: Paste
app_key = '' #TODO: Paste
api_client = pdfclient.Application(app_id, app_key)

# ========================================================================================

def DoFillForm(xfdf_path_in, pdf_url_in):
	fh = filehandler.FileHandler(xfdf_path_in)
	#print fh.files
	#print fh.data # should be {}
	
	options = {'disableCalculation': False, 'disableGeneration': False, 'flatten' : False}
	api_request = api_client.make_request('FillForm')
	api_response = api_request(fh.files, inputURL=pdf_url_in, inputName='BadIntentions DoFillForm', options=options)
	
	print 'HTTP Response:', api_response.http_code
	if api_response.ok:
		assert_equal(api_response.http_code, requests.codes.ok)
		# api_response.output is the requested document or image.
		assert_is_none(api_response.error_code)
		assert_is_none(api_response.error_message)
		
		# intermediate file (returned from WebAPI and saved to local storage)
		pdf_path_out = 'tmp.pdf' # todo: random token
		
		rh = responsehandler.ResponseHandler(api_response, pdf_path_out)
		rh.save_output()
		
		#print '---------- BadIntentions DoFillForm ----------'
		#print rh
		#print '----------------------------------------------'
	else:
		print responsehandler.ResponseHandler(api_response, pdf_path_out)
		exit
	
	return pdf_path_out

# ========================================================================================

def DoFlattenForm(pdf_path_in, pdf_path_out):
	fh = filehandler.FileHandler(pdf_path_in)
	#print fh.files
	#print fh.data # should be {}
	
	api_request = api_client.make_request('FlattenForm')
	api_response = api_request(fh.files, inputName='BadIntentions DoFlattenForm')
	
	print 'HTTP Response:', api_response.http_code
	if api_response.ok:
		assert_equal(api_response.http_code, requests.codes.ok)
		# api_response.output is the requested document or image.
		assert_is_none(api_response.error_code)
		assert_is_none(api_response.error_message)
		
		rh = responsehandler.ResponseHandler(api_response, pdf_path_out)
		rh.save_output()
		
		#print '---------- BadIntentions DoFlattenForm ----------'
		#print rh
		#print '-------------------------------------------------'
	else:
		print responsehandler.ResponseHandler(api_response, pdf_path_out)
		exit
	
	return pdf_path_out

# ========================================================================================

def DoExportFormData(pdf_path_in, fdf_path_out):
	fh = filehandler.FileHandler(pdf_path_in)
	#print fh.files
	#print fh.data # should be {}
	
	options = {'exportXFDF': False}
	api_request = api_client.make_request('ExportFormData')
	api_response = api_request(fh.files, inputName='BadIntentions DoExportFormData', options=options)
	
	print 'HTTP Response:', api_response.http_code
	if api_response.ok:
		assert_equal(api_response.http_code, requests.codes.ok)
		# api_response.output is the requested document or image.
		assert_is_none(api_response.error_code)
		assert_is_none(api_response.error_message)
		
		rh = responsehandler.ResponseHandler(api_response, fdf_path_out)
		rh.save_output()
		
		#print '---------- BadIntentions DoExportFormData ----------'
		#print rh
		#print '----------------------------------------------------'
	else:
		print responsehandler.ResponseHandler(api_response, fdf_path_out)
		exit
	
	return fdf_path_out

# ========================================================================================

def DoRenderPages(pdf_path_or_url_in, jpg_path_out):
	fh = filehandler.FileHandler(pdf_path_or_url_in)
	#print fh.files
	#print fh.data
	
	options = {'outputFormat': 'jpg', 'printPreview': True}
	api_request = api_client.make_request('RenderPages')
	
	if len(fh.data) > 0:
		api_response = api_request(fh.files, inputURL=fh.data['inputURL'], inputName='BadIntentions RenderPages', options=options)
	elif len(fh.files) > 0:
		api_response = api_request(fh.files, inputName='BadIntentions RenderPages', options=options)
	else:
		print 'data set not suitable for WebAPI'
		exit
	
	print 'HTTP Response:', api_response.http_code
	if api_response.ok:
		assert_equal(api_response.http_code, requests.codes.ok)
		# api_response.output is the requested document or image.
		assert_is_none(api_response.error_code)
		assert_is_none(api_response.error_message)
		
		rh = responsehandler.ResponseHandler(api_response, jpg_path_out)
		rh.save_output()
		
		#print '---------- BadIntentions RenderPages ----------'
		#print rh
		#print '----------------------------------------------'
	else:
		print responsehandler.ResponseHandler(api_response, jpg_path_out)
		exit
	
	return jpg_path_out

# ========================================================================================

def DoDecorateDocument(pdf_path_or_url_in, xml_path_in, pdf_path_out):
	fh = filehandler.FileHandler(pdf_path_or_url_in)
	#print fh.files
	#print fh.data
	
	api_request = api_client.make_request('DecorateDocument')
	
	if len(fh.files) > 0:
		xml_file_handle = open(xml_path_in, 'r')
		fh.files['decorationData']=xml_file_handle
		#print fh.files
		api_response = api_request(fh.files, inputName='BadIntentions DecorateDocument')
	else:
		print 'data set not suitable for WebAPI'
		exit
	
	print 'HTTP Response:', api_response.http_code
	if api_response.ok:
		assert_equal(api_response.http_code, requests.codes.ok)
		# api_response.output is the requested document or image.
		assert_is_none(api_response.error_code)
		assert_is_none(api_response.error_message)
		
		rh = responsehandler.ResponseHandler(api_response, pdf_path_out)
		rh.save_output()
		
		#print '---------- BadIntentions DecorateDocument ----------'
		#print rh
		#print '----------------------------------------------------'
	else:
		print responsehandler.ResponseHandler(api_response, pdf_path_out)
		exit
	
	return pdf_path_out

# ========================================================================================

def CreateHeaderFooterDocSettings(settings_dict, xml_path_out):
	Root = Element('HeaderFooterSettings', attrib = {'version':"8.0"})

	font_type = settings_dict.get('font_type', "TrueType")
	font_size = settings_dict.get('font_size', "12.0")
	font_name = settings_dict.get('font_name', "Arial")
	SubElement(Root, 'Font', size = font_size, name = font_name)
	
	rv, gv, bv = settings_dict.get('color', [0,0,0])
	SubElement(Root, 'Color', r = str(rv), g = str(gv), b = str(bv))
	
	margin_left, margin_bottom, margin_top, margin_right = settings_dict.get('margin', [36.0, 36.0, 36.0, 36.0])
	SubElement(Root, 'Margin', right = str(margin_right), left = str(margin_left), bottom = str(margin_bottom), top = str(margin_top))
	
	header = SubElement(Root, 'Header')
	header_left = SubElement(header, 'Left')
	header_center = SubElement(header, 'Center')
	header_right = SubElement(header, 'Right')
	footer = SubElement(Root, 'Footer')
	footer_left = SubElement(footer, 'Left')
	footer_center = SubElement(footer, 'Center')
	footer_right = SubElement(footer, 'Right')
	
	if settings_dict.has_key('TopLeftText'):
		header_left.text = settings_dict['TopLeftText']
	
	if settings_dict.has_key('TopCenterText'):
		header_center.text = settings_dict['TopCenterText']
		
	if settings_dict.has_key('TopRightText'):
		header_right.text = settings_dict['TopRightText']
	
	if settings_dict.has_key('BottomLeftText'):
		footer_left.text = settings_dict['BottomLeftText']
	
	if settings_dict.has_key('BottomCenterText'):
		footer_center.text = settings_dict['BottomCenterText']
	
	if settings_dict.has_key('BottomRightText'):
		footer_right.text = settings_dict['BottomRightText']
	
	xml_file = open(xml_path_out, 'w')
	xml_file.write('<?xml version="1.0" encoding = "UTF-8"?>')
	xml_file.write(ElementTree.tostring(Root))
	xml_file.write('\r\n')
	xml_file.flush()
	xml_file.close()
	
	return xml_path_out

