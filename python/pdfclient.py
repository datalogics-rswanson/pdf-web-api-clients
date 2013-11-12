# Copyright (c) 2013, Datalogics, Inc. All rights reserved.

"Sample pdfprocess client module"

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

import sys
import inspect

import requests
import simplejson as json


STRING_TYPES = (str, unicode) if sys.version_info.major < 3 else (str,)

## %Request factory
class Application(object):
    BASE_URL = 'https://pdfprocess.datalogics-cloud.com'
    ## @param id from our [developer portal](http://api.datalogics-cloud.com/)
    #  @param key from our [developer portal](http://api.datalogics-cloud.com/)
    def __init__(self, id, key):
        self._id, self._key = id, key

    def __str__(self):
        return json.dumps({'id': self.id, 'key': self.key})

    ## Create a request for the specified request type
    # @return a Request object
    # @param request_type e.g. 'FlattenForm'
    def make_request(self, request_type, base_url=BASE_URL):
        return Application._request_class(request_type)(self, base_url)

    @classmethod
    def _request_class_predicate(cls, request_type):
        return lambda m: inspect.isclass(m) and m.__name__ == request_type
    @classmethod
    def _request_class(cls, request_type):
        is_request_class = cls._request_class_predicate(request_type)
        members = inspect.getmembers(sys.modules[__name__], is_request_class)
        return members[0][1]
    @property
    ## ID property (string)
    def id(self): return self._id
    @property
    ## Key property (string)
    def key(self): return self._key


## Service request
class Request(object):
    def __init__(self, application, base_url):
        self._data = {'application': str(application)}
        self._output_format = None
        self._url = '{}/api/actions{}'.format(base_url, self._url_suffix())

    ## Send request
    #  @return a Response object
    #  @param input input document URL or file object
    #  @param data dict with keys in ('inputName', 'password', 'options')
    def __call__(self, input, **data):
        files = None
        data.update(self._data)
        if type(input) in STRING_TYPES:
            data['inputURL'] = input
        else:
            input.seek(0)
            files = {'input': input}
            data['inputName'] = input.name
        if 'options' in data:
            data['options'] = json.dumps(data['options'])
        return Response(
            requests.post(self.url, verify=False, data=data, files=files))

    def _url_suffix(self):
        result = ''
        for char in self.__class__.__name__:
            if char.isupper(): result += '/'
            result += char.lower()
        return result
    @property
    ## Output filename extension property (string)
    def output_format(self): return self._output_format
    @property
    ## %Request URL property (string)
    def url(self): return self._url


## Service response
class Response(object):
    def __init__(self, request_response):
        self._response = request_response
        self._error_code, self._error_message = None, None
        if not self:
            try:
                json = request_response.json()
                self._error_code = json['errorCode']
                self._error_message = json['errorMessage']
            except:
                pass
    def __str__(self):
        return self.output or \
            '{}: {}'.format(self.error_code, self.error_message)
    def __bool__(self):
        return self.http_code == requests.codes.ok
    __nonzero__ = __bool__
    def __getattr__(self, key):
        return getattr(self._response, key)
    @property
    ## HTTP status code (int)
    def http_code(self): return self._response.status_code
    @property
    ## Document or image data (bytes) if request was successful, otherwise None
    def output(self): return self._response.content if self else None
    @property
    ## None if successful, otherwise API
    #   [error code](https://api.datalogics-cloud.com/#errorCode) (int)
    def error_code(self): return self._error_code
    @property
    ## None if successful, otherwise information (string) about the error
    #   [error](https://api.datalogics-cloud.com/#errorMessage)
    def error_message(self): return self._error_message


## API error codes
class ErrorCode:
    AuthorizationError = 1
    InvalidSyntax = 2
    InvalidInput = 3
    InvalidPassword = 4
    MissingPassword = 5
    UnsupportedSecurityProtocol = 6
    InvalidOutputFormat = 7
    InvalidPage = 8
    RequestTooLarge = 9
    UsageLimitExceeded = 10
    UnknownError = 20


## Flatten form fields and other annotations
class FlattenForm(Request):
    ## %FlattenForm has no request options
    OPTIONS = []
    ## Error codes for %FlattenForm requests
    class ErrorCode(ErrorCode):
        NoAnnotations = 21
    def __init__(self, application, base_url):
        Request.__init__(self, application, base_url)
        self._output_format = 'pdf'


## Create raster image representation
class RenderPages(Request):
    ## %RenderPages request options:
    #  * [colorModel](https://api.datalogics-cloud.com/docs#colorModel):
    #     rgb (default), gray, rgba, or cmyk
    #  * [compression](https://api.datalogics-cloud.com/docs#compression):
    #     lzw (default) or jpg
    #  * [disableColorManagement]
    #     (https://api.datalogics-cloud.com/docs#disableColorManagement):
    #     for downstream color management (rarely used)
    #  * [disableThinLineEnhancement]
    #     (https://api.datalogics-cloud.com/docs#disableThinLineEnhancement)
    #     for high-resolution output (rarely used)
    #  * [imageHeight](https://api.datalogics-cloud.com/docs#imageHeight):
    #     pixels
    #  * [imageWidth](https://api.datalogics-cloud.com/docs#imageWidth):
    #     pixels
    #  * [OPP](https://api.datalogics-cloud.com/docs#OPP): overprint preview
    #  * [outputFormat](https://api.datalogics-cloud.com/docs#outputFormat):
    #     png (default), git, jpg, or tif
    #  * [pages](https://api.datalogics-cloud.com/docs#pages):
    #     default = 1
    #  * [pdfRegion](https://api.datalogics-cloud.com/docs#pdfRegion):
    #     crop (default), art, bleed, bounding, media, or trim
    #  * [printPreview](https://api.datalogics-cloud.com/docs#printPreview):
    #     ignored if suppressAnnotations is true
    #  * [resolution](https://api.datalogics-cloud.com/docs#resolution):
    #     12 to 2400 (default = 150)
    #  * [smoothing](https://api.datalogics-cloud.com/docs#smoothing):
    #     all (default), none, or text
    #  * [suppressAnnotations]
    #     (https://api.datalogics-cloud.com/docs#suppressAnnotations):
    #     draw only actual page contents
    OPTIONS = ['colorModel', 'compression',
               'disableColorManagement', 'disableThinLineEnhancement',
               'imageHeight', 'imageWidth',
               'OPP', 'outputFormat',
               'pages', 'pdfRegion',
               'printPreview', 'resolution',
               'smoothing', 'suppressAnnotations']
    ## Error codes for %RenderPages requests
    class ErrorCode(ErrorCode):
        InvalidColorModel = 31
        InvalidCompression = 32
        InvalidRegion = 33
        InvalidResolution = 34
    ## Send request
    #  @return a Response object
    #  @param input input document URL or file object
    #  @param data dict with keys in ('inputName', 'password', 'options')
    def __call__(self, input, **data):
        self._output_format = data.get('outputFormat', 'png')
        return Request.__call__(self, input, **data)
