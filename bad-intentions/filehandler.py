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

class FileHandler(object):
	PART_NAME_FILE_FORMATS = {'formsData': ('FDF', 'XFDF', 'XML')}
	def __init__(self, args):
		self._data, self._files = {}, {}
		
		files = [args] #[arg for arg in args if '=' not in arg]
		urls = [file for file in files if FileHandler._is_url(file)]
		
		if len(urls) > 1:
			raise Exception('invalid input: {} URLs'.format(len(urls)))
		
		if urls:
			files.remove(urls[0])
			self.data['inputURL'] = urls[0]
		
		for file in files:
			self.files[FileHandler._part_name(file)] = open(file, 'rb')
	
	def __del__(self):
		for file in self.files.values():
			file.close()
	
	@classmethod
	def _is_url(cls, filename):
		name = filename.lower()
		if name.startswith('http://') or name.startswith('https://'):
			return filename
	
	@classmethod
	def _part_name(cls, filename):
		data_format = os.path.splitext(filename)[1][1:].upper()
		
		for part_name in FileHandler.PART_NAME_FILE_FORMATS:
			if data_format in FileHandler.PART_NAME_FILE_FORMATS[part_name]:
				return part_name
		
		return 'input'
	
	@property
	## form parts that will be passed to requests.post
	def data(self):
		return self._data
	
	@property
	## files that will be passed to requests.post
	def files(self): 
		return self._files

#eof

