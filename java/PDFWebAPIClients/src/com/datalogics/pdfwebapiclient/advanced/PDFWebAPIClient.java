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

package com.datalogics.pdfwebapiclient.advanced;

/**
 * The PDFWebAPIClient class is the starting point for an application that
 * desires to make requests of the PDF WebAPI server. The class itself is a
 * factory for instantiating objects derived from the base class,
 * PDFWebAPIRequest, that in turn are used to make class-specific requests to
 * the PDF WebAPI server. <br>
 * A PDFWebAPIClient object must be created with the application id and key
 * assigned to the developer when registering to use the PDF WebAPI. When a PDF
 * WebAPI request is desired, the application must call the desired
 * create<request type>Request() method in order to create a request-specific
 * object derived from the PDFWebAPIRequest class and use its methods to make
 * the desired request of the server.
 */
public final class PDFWebAPIClient {

	// Member variables
	private String id; ///<The application id string.
	private String key; ///<The application key string.

	// Private Member Enumerations
	/** These are the enumerated request types available. **/
	private enum RequestType {
		ExportFormData, FillForm, FlattenForm, RenderPages, DecorateDocument
	}

	// Constructor(s)
	/**
	 * This constructor is used to instantiate a PDFWebAPIClient with the given
	 * application id and key to be used for authentication of the client with
	 * the PDF WebAPI server.
	 * 
	 * @param id - A string containing the application id assigned when
	 *             registering for use of the PDF WebAPI.
	 * @param key - A string containing the application key used to authenticate
	 *              PDF WebAPI requests.
	 */
	public PDFWebAPIClient(String id, String key) {
		/*
		 * Store the application id and key so they can be passed to the request
		 * objects when they are created.
		 */
		this.id = id;
		this.key = key;
	}

	// Public Methods

	/**
	 * This method creates an ExportFormDataRequest object that is to be used to
	 * request form data extraction from a pdf.
	 * 
	 * @return An ExportFormDataRequest object.
	 */
	public ExportFormDataRequest createExportFormDataRequest() {
		return new ExportFormDataRequest(id, key,
				getWebAPIRequestURL(RequestType.ExportFormData));
	}

	/**
	 * This method creates a FillFormRequest object that is to be used to fill
	 * form data in a pdf via an external fdf file.
	 * 
	 * @return A FillFormRequest object.
	 */
	public FillFormRequest createFillFormRequest() {
		return new FillFormRequest(id, key,
				getWebAPIRequestURL(RequestType.FillForm));
	}

	/**
	 * This method creates a FlattenFormRequest object that is to be used to
	 * request a pdf with form data be flattened.
	 * 
	 * @return A FlattenFormRequest object.
	 */
	public FlattenFormRequest createFlattenFormRequest() {
		return new FlattenFormRequest(id, key,
				getWebAPIRequestURL(RequestType.FlattenForm));
	}

	/**
	 * This method creates a RenderPagesRequest object that is to be used to
	 * request rendering of an image from a pdf.
	 * 
	 * @return A RenderPagesRequest object.
	 */
	public RenderPagesRequest createRenderPagesRequest() {
		return new RenderPagesRequest(id, key,
				getWebAPIRequestURL(RequestType.RenderPages));
	}

	/**
	 * This method creates a DecorateDocumentRequest object that is to be used
	 * to add headers and footers to a pdf document using an external xml file.
	 * 
	 * @return A DecorateDocumentRequest object.
	 */
	public DecorateDocumentRequest createDecorateDocument() {
		return new DecorateDocumentRequest(id, key,
				getWebAPIRequestURL(RequestType.DecorateDocument));
	}

	// Private Methods

	/**
	 * Gets the URL of the requested service on the PDF WebAPI server. Each
	 * request type must be submitted to its own unique URL for processing.
	 * 
	 * @param requestType
	 *            - One of the enumerated request types.
	 * @return A string containing the URL of the service that the request
	 *         should be sent to.
	 */
	private String getWebAPIRequestURL(RequestType requestType) {
		// The PDF WebAPI server base URL
		final String BASE_URL =
				"https://pdfprocess.datalogics-cloud.com/api/actions/";
		// Request URL to return
		String url = null;
		// Get request specific URL
		switch (requestType) {
		case ExportFormData:
			url = BASE_URL + "export/form-data";
			break;
		case FillForm:
			url = BASE_URL + "fill/form";
			break;
		case FlattenForm:
			url = BASE_URL + "flatten/form";
			break;
		case RenderPages:
			url = BASE_URL + "render/pages";
			break;
		case DecorateDocument:
			url = BASE_URL + "decorate/document";
		}
		return url;
	}

}
