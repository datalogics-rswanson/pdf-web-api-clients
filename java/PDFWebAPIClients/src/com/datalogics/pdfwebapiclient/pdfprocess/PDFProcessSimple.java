//Copyright (c) 2014, Datalogics, Inc. All rights reserved.
//
//This agreement is between Datalogics, Inc. 101 N. Wacker Drive, Suite 1800,
//Chicago, IL 60606 ("Datalogics") and you, an end user who downloads
//source code examples for integrating to the Datalogics (R) PDF WebAPI (TM)
//("the Example Code"). By accepting this agreement you agree to be bound
//by the following terms of use for the Example Code.
//
//LICENSE
//-------
//Datalogics hereby grants you a royalty-free, non-exclusive license to
//download and use the Example Code for any lawful purpose. There is no charge
//for use of Example Code.
//
//OWNERSHIP
//---------
//The Example Code and any related documentation and trademarks are and shall
//remain the sole and exclusive property of Datalogics and are protected by
//the laws of copyright in the U.S. and other countries.
//
//Datalogics and Datalogics PDF WebAPI are trademarks of Datalogics, Inc.
//
//TERM
//----
//This license is effective until terminated. You may terminate it at any
//other time by destroying the Example Code.
//
//WARRANTY DISCLAIMER
//-------------------
//THE EXAMPLE CODE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER
//EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES
//OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
//
//DATALOGICS DISCLAIM ALL OTHER WARRANTIES, CONDITIONS, UNDERTAKINGS OR
//TERMS OF ANY KIND, EXPRESS OR IMPLIED, WRITTEN OR ORAL, BY OPERATION OF
//LAW, ARISING BY STATUTE, COURSE OF DEALING, USAGE OF TRADE OR OTHERWISE,
//INCLUDING, WARRANTIES OR CONDITIONS OF MERCHANTABILITY, FITNESS FOR A
//PARTICULAR PURPOSE, SATISFACTORY QUALITY, LACK OF VIRUSES, TITLE,
//NON-INFRINGEMENT, ACCURACY OR COMPLETENESS OF RESPONSES, RESULTS, AND/OR
//LACK OF WORKMANLIKE EFFORT. THE PROVISIONS OF THIS SECTION SET FORTH
//SUBLICENSEE'S SOLE REMEDY AND DATALOGICS'S SOLE LIABILITY WITH RESPECT
//TO THE WARRANTY SET FORTH HEREIN. NO REPRESENTATION OR OTHER AFFIRMATION
//OF FACT, INCLUDING STATEMENTS REGARDING PERFORMANCE OF THE EXAMPLE CODE,
//WHICH IS NOT CONTAINED IN THIS AGREEMENT, SHALL BE BINDING ON DATALOGICS.
//NEITHER DATALOGICS WARRANT AGAINST ANY BUG, ERROR, OMISSION, DEFECT,
//DEFICIENCY, OR NONCONFORMITY IN ANY EXAMPLE CODE.

package com.datalogics.pdfwebapiclient.pdfprocess;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.datalogics.pdfwebapiclient.simple.Application;
import com.datalogics.pdfwebapiclient.simple.Application.PDFWebAPIRequest;
import com.datalogics.pdfwebapiclient.simple.Application.RequestType;

/**
 * This class demonstrates the usage of the advanced PDFWebAPIClient class in
 * interfacing with the PDFWebAPI server.  The general protocol is to create a
 * PDFWebAPIClient object with the application id and key required to access the
 * PDFWebAPI server.  From this client, a request object that is
 * request-specific must be created.  The request object is configured using its
 * request-specific methods and then an HttpPost is built from the object via
 * the <code>buildRequest()</code> method.  Finally the request object's
 * <code>getResponse()</code> method is called and a PDFWebAPIResponse object
 * is returned which may be used to determine the result of the request or save
 * returned data to file.
 */
public class PDFProcessSimple {

	public static void main(String[] args) throws Exception {

		// Application "id" and "key" for accessing PDFWebAPI server
		/**@TODO CHANGE THESE PRIOR TO PUBLISHING **/
		final String id = "54208b6e";
		final String key = "020042be31edb98a32596a5530e11a97";

		// Set file paths for samples
		/**@TODO CHANGE THESE PRIOR TO PUBLISHING
		 * @NOTE In the RenderPage request example, "inputURL" should be changed
		 * to "input" unless renderPages_In_PDF remains an web (http) resource
		 */
		final String exportFormData_In_PDF = "/Users/rswanson/Documents/Filled-in_AcroForm_and_XFA_form/AcroForm.pdf";
		final String fillForm_In_PDF = "/Users/rswanson/Documents/PDFWebAPI_Data/FillForm/BlankAcroform.pdf";
		final String fillForm_In_FormData = "/Users/rswanson/Documents/PDFWebAPI_Data/FillForm/fdfin.fdf";
		final String flattenForm_In_PDF = "/Users/rswanson/Documents/Filled-in_AcroForm_and_XFA_form/AcroForm.pdf";
		final String renderPages_In_PDF = "http://www.datalogics.com/pdf/doc/pdf2img.pdf";
		final String decorateDocument_In_PDF = "/Users/rswanson/Documents/PDFWebAPI_Data/DecorateDocument/HeaderFooterGridInput.pdf";
		final String decorateDocument_In_XML1 = "/Users/rswanson/Documents/PDFWebAPI_Data/DecorateDocument/AllEvenPages.xml";
		final String decorateDocument_In_XML2 = "/Users/rswanson/Documents/PDFWebAPI_Data/DecorateDocument/AllOddPages.xml";
		// Initialize PDFWebAPIClient with application id and key
		Application app = null;
		PDFWebAPIRequest request = null;
		HttpResponse response = null;

		//----------------- ExportFormDataRequest Example --------------------//

		// Create the application with the id and key
		app = new Application(id, key);
		// Create a request for ExportFormData
		request = app.makeRequest(RequestType.ExportFormData);
		// Set the input file and filename
		request.addPart("input", new FileBody(new File(exportFormData_In_PDF),
				ContentType.DEFAULT_BINARY));
		request.addPart("inputName", new StringBody(exportFormData_In_PDF,
				ContentType.TEXT_PLAIN));
		// Send the request and get the response from the PDF WebAPI server
		response = request.getResponse();
		System.out.print("ExportForm Response: ");
		System.out.println(response.getStatusLine().getStatusCode());
		// Upon success, the output form data may be extracted from the
		// response body

		//-------------------- FillFormRequest Example -----------------------//

		// Create the application with the id and key
		app = new Application(id, key);
		// Create a request for FillForm
		request = app.makeRequest(RequestType.FillForm);
		// Set the input file(s) and filename
		request.addPart("input", new FileBody(new File(fillForm_In_PDF),
				ContentType.DEFAULT_BINARY));
		request.addPart("inputName", new StringBody(fillForm_In_PDF,
				ContentType.TEXT_PLAIN));
		request.addPart("formsData", 
				new FileBody(new File(fillForm_In_FormData),
						ContentType.DEFAULT_BINARY));
		// Set flatten option
		request.addOption("flatten", true);
		// Send the request and get the response from the PDF WebAPI server
		response = request.getResponse();
		System.out.print("FillForm Response: ");
		System.out.println(response.getStatusLine().getStatusCode());
		// Upon success, the output pdf data may be extracted from the
		// response body


		//------------------ FlattenFormRequest Example ----------------------//

		// Create the application with the id and key
		app = new Application(id, key);
		// Create a request for FlattenForm
		request = app.makeRequest(RequestType.FlattenForm);
		// Set the input file(s) and filename
		request.addPart("input", new FileBody(new File(flattenForm_In_PDF),
				ContentType.DEFAULT_BINARY));
		request.addPart("inputName", new StringBody(flattenForm_In_PDF,
				ContentType.TEXT_PLAIN));
		// Send the request and get the response from the PDF WebAPI server
		response = request.getResponse();
		System.out.print("FlattenForm Response: ");
		System.out.println(response.getStatusLine().getStatusCode());
		// Upon success, the output pdf data may be extracted from the
		// response body

		//------------------ RenderPagesRequest Example ----------------------//

		// Create the application with the id and key
		app = new Application(id, key);
		// Create a request for RenderPages
		request = app.makeRequest(RequestType.RenderPages);
		// Set the input file(s) and filename
		request.addPart("inputURL", new StringBody(renderPages_In_PDF,
				ContentType.TEXT_PLAIN));
		request.addPart("inputName", new StringBody(renderPages_In_PDF,
				ContentType.TEXT_PLAIN));
		// Set some rendering options
		request.addOption("colorModel", "gray");
		request.addOption("outputFormat", "jpg");
		// Send the request and get the response from the PDF WebAPI server
		response = request.getResponse();
		System.out.print("RenderPage Response: ");
		System.out.println(response.getStatusLine().getStatusCode());
		// Upon success, the output image data may be extracted from the
		// response body

		//----------------- DecorateDocumentRequest Example ------------------//

		// Create the application with the id and key
		app = new Application(id, key);
		// Create a request for DecorateDocument
		request = app.makeRequest(RequestType.DecorateDocument);
		// Set the input file(s) and filename
		request.addPart("input", new FileBody(new File(decorateDocument_In_PDF),
				ContentType.DEFAULT_BINARY));
		request.addPart("inputName", new StringBody(decorateDocument_In_PDF,
				ContentType.TEXT_PLAIN));
		request.addPart("decorationData", 
				new FileBody(new File(decorateDocument_In_XML1),
						ContentType.DEFAULT_BINARY));
		request.addPart("decorationData", 
				new FileBody(new File(decorateDocument_In_XML2),
						ContentType.DEFAULT_BINARY));
		// Send the request and get the response from the PDF WebAPI server
		response = request.getResponse();
		System.out.print("DecorateDocument Response: ");
		System.out.println(response.getStatusLine().getStatusCode());
		// Upon success, the output image data may be extracted from the
		// response body

	}
}
