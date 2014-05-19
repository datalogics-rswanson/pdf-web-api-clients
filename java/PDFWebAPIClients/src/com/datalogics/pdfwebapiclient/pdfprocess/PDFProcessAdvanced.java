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

import org.apache.http.client.methods.HttpPost;
import com.datalogics.pdfwebapiclient.advanced.*;
import com.datalogics.pdfwebapiclient.advanced.RenderPagesRequest.ColorModel;
import com.datalogics.pdfwebapiclient.advanced.RenderPagesRequest.OutputFormat;

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
public class PDFProcessAdvanced {

	public static void main(String[] args) throws Exception {

		// Application "id" and "key" for accessing PDFWebAPI server
		/**@TODO CHANGE THESE PRIOR TO PUBLISHING **/
		final String id = "54208b6e";
		final String key = "020042be31edb98a32596a5530e11a97";

		// Set file paths for samples
		/**@TODO CHANGE THESE PRIOR TO PUBLISHING **/
		final String exportFormData_In_PDF = "/Users/rswanson/Documents/Filled-in_AcroForm_and_XFA_form/AcroForm.pdf";
		final String exportFormData_Out_FormData = "/Users/rswanson/Documents/exportform_out_adv.fdf";
		final String fillForm_In_PDF = "/Users/rswanson/Documents/PDFWebAPI_Data/FillForm/BlankAcroform.pdf";
		final String fillForm_In_FormData = "/Users/rswanson/Documents/PDFWebAPI_Data/FillForm/fdfin.fdf";
		final String fillForm_Out_PDF = "/Users/rswanson/Documents/fillform_out.pdf";
		final String flattenForm_In_PDF = "/Users/rswanson/Documents/Filled-in_AcroForm_and_XFA_form/AcroForm.pdf";
		final String flattenForm_Out_PDF = "/Users/rswanson/Documents/flattenform_out.pdf";
		final String renderPages_In_PDF = "http://www.datalogics.com/pdf/doc/pdf2img.pdf";
		final String renderPages_Out_JPG = "/Users/rswanson/Documents/renderpages_out.jpg";
		final String decorateDocument_In_PDF = "/Users/rswanson/Documents/PDFWebAPI_Data/DecorateDocument/HeaderFooterGridInput.pdf";
		final String decorateDocument_In_XML1 = "/Users/rswanson/Documents/PDFWebAPI_Data/DecorateDocument/AllEvenPages.xml";
		final String decorateDocument_In_XML2 = "/Users/rswanson/Documents/PDFWebAPI_Data/DecorateDocument/AllOddPages.xml";
		final String decorateDocument_Out_PDF = "/Users/rswanson/Documents/decoratedocument_out.pdf";

		// Initialize PDFWebAPIClient with application id and key
		PDFWebAPIClient client = new PDFWebAPIClient(id, key);
		HttpPost post = null;
		PDFWebAPIResponse response = null;

		//----------------- ExportFormDataRequest Example --------------------//

		// Create an ExportFormDataRequest object
		ExportFormDataRequest exportform_request = 
				client.createExportFormDataRequest();
		// Set input pdf file to export formdata from
		exportform_request.setInputFile(exportFormData_In_PDF);
		// Build the HTTP multi-part request to be sent to the PDFWebAPI server
		post = exportform_request.buildRequest();
		// Send the request and check if the response failed or not
		response = exportform_request.getResponse(post);
		if(response.failed()) {
			System.out.println("ExportFormData Request Failed:");
			System.out.println(response.getErrorCode());
			System.out.println(response.getErrorMessage());
		}
		else {
			System.out.println("ExportFormData Request Succeeded");		
			// Succeeded so save the returned formdata file
			response.saveProcFile(exportFormData_Out_FormData);
		}

		//-------------------- FillFormRequest Example -----------------------//

		// Create a FillFormRequest object
		FillFormRequest fillform_request = client.createFillFormRequest();
		// Set the blank form to be filled and the form data to use
		fillform_request.setInputFile(fillForm_In_PDF);
		fillform_request.setFormsData(fillForm_In_FormData);
		// Optionally flatten the output file
		fillform_request.setFlatten(true);
		// Build the HTTP multi-part request to be sent to the PDFWebAPI server
		post = fillform_request.buildRequest();
		// Send the request and check if the response failed or not
		response = fillform_request.getResponse(post);
		if(response.failed()) {
			System.out.println("FillForm Request Failed");
			System.out.println(response.getErrorCode());
			System.out.println(response.getErrorMessage());
		}
		else {
			System.out.println("FillForm Request Succeeded");		
			// Succeeded so save the returned PDF file
			response.saveProcFile(fillForm_Out_PDF);
		}

		//------------------ FlattenFormRequest Example ----------------------//

		// Create a FlattenFormRequest object
		FlattenFormRequest flattenform_request = 
				client.createFlattenFormRequest();
		// Set the PDF form to be flattened
		flattenform_request.setInputFile(flattenForm_In_PDF);
		// Build the HTTP multi-part request to be sent to the PDFWebAPI server
		post = flattenform_request.buildRequest();
		// Send the request and check if the response failed or not
		response = flattenform_request.getResponse(post);
		if(response.failed()) {
			System.out.println("FlattenForm Request Failed");
			System.out.println(response.getErrorCode());
			System.out.println(response.getErrorMessage());
		}
		else {
			System.out.println("FlattenForm Request Succeeded");
			// Succeeded so save the returned PDF file
			response.saveProcFile(flattenForm_Out_PDF);
		}

		//------------------ RenderPagesRequest Example ----------------------//

		// Create a RenderPagesRequest object
		RenderPagesRequest renderpages_request =
				client.createRenderPagesRequest();
		// Set the PDF file to be rendered
		renderpages_request.setInputFile(renderPages_In_PDF);
		// Set the color-model to produce a grayscale image
		renderpages_request.setColorModel(ColorModel.Gray);
		// Set the output format to be jpg
		renderpages_request.setOutputFormat(OutputFormat.JPG);
		// Build the HTTP multi-part request to be sent to the PDFWebAPI server
		post = renderpages_request.buildRequest();
		// Send the request and check if the response failed or not
		response = renderpages_request.getResponse(post);
		if(response.failed()) {
			System.out.println("RenderPages Request Failed");
			System.out.println(response.getErrorCode());
			System.out.println(response.getErrorMessage());
		}
		else {
			System.out.println("RenderPages Request Succeeded");		
			// Succeeded so save the returned JPG file
			response.saveProcFile(renderPages_Out_JPG);
		}

		//----------------- DecorateDocumentRequest Example ------------------//

		// Create a DecorateDocumentRequest object
		DecorateDocumentRequest decoratedocument_request =
				client.createDecorateDocument();
		// Set the input PDF document to apply decorations to and the decoration
		// XML files
		decoratedocument_request.setInputFile(decorateDocument_In_PDF);
		decoratedocument_request.addDecorationData(decorateDocument_In_XML1);
		decoratedocument_request.addDecorationData(decorateDocument_In_XML2);
		// Build the HTTP multi-part request to be sent to the PDFWebAPI server
		post = decoratedocument_request.buildRequest();
		// Send the request and check if the response failed or not
		response = decoratedocument_request.getResponse(post);
		if(response.failed()) {
			System.out.println("DecorateDocument Request Failed");
			System.out.println(response.getErrorCode());
			System.out.println(response.getErrorMessage());
		}
		else {
			System.out.println("DecorateDocument Request Succeeded");		
			// Succeeded so save the returned PDF file
			response.saveProcFile(decorateDocument_Out_PDF);
		}	
	}
}
