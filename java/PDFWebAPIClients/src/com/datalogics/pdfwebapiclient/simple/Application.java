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
package com.datalogics.pdfwebapiclient.simple;

import java.util.Random;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;

/**
 * The Application class is the starting point for an application that
 * desires to make requests of the PDF WebAPI server. The class itself contains
 * mostly utility classes and methods to create HTTP multi-part POST requests
 * to be sent to PDF WebAPI server to process specific request types.
 */
public final class Application {

	// Private Member Enumerations
	/** These are the enumerated request types available. **/
	public enum RequestType {
		ExportFormData, FillForm, FlattenForm, RenderPages, DecorateDocument
	}

	// Inner Classes

	/**
	 * This inner class is a utility class for creating PDF WebAPI server 
	 * requests.  A request is created by passing the application key and id
	 * required by the PDF WebAPI server and the action URL into the 
	 * constructor.  Request parts and options may be set using the 
	 * <code>addPart()</code> and <code>addOption()</code> methods.  The server
	 * response may be obtained by the <code>getResponse()</code> method.
	 */
	public final class PDFWebAPIRequest {

		/** The MultiPartEntityBuilder object to combine form parts. */
		private final MultipartEntityBuilder mpe_builder = 
				MultipartEntityBuilder.create();
		/** The JsonObjectBuilder for building the options JSON object */
		private final JsonObjectBuilder options = Json.createObjectBuilder();
		/** The URL of the PDF WebAPI service to call */
		private final String url;

		/** 
		 * This constructor creates a request object catered to the PDF WebAPI
		 * server using the given application id and key and action URL.
		 * @param id - A string containing the application id assigned when
		 *             registering for use of the PDF WebAPI.
		 * @param key - A string containing the application key used to 
		 * 				authenticate PDF WebAPI requests.
		 * @param url - The PDF WebAPI server action URL for the request type
		 */
		public PDFWebAPIRequest(String id, String key, String url) {
			// Add the application id and key part
			addPart("application",new StringBody(Json.createObjectBuilder()
					.add("id", id)
					.add("key", key)
					.build()
					.toString(),
					ContentType.TEXT_PLAIN));
			this.url = url;
		}

		/**
		 * Adds a part with the given name to the multi-part HTTP Post
		 * @param name - The name of the part
		 * @param content - The content body of the part to add
		 */
		public void addPart(String name, ContentBody content) {
			// Add the part to the MultipartEntityBuilder object
			mpe_builder.addPart(name, content);
		}

		/**
		 * Adds the given boolean option to the request
		 * @param option - The name of the option
		 * @param value - The boolean value of the option
		 */
		public void addOption(String option, boolean value) {
			options.add(option, value);
		}

		/**
		 * Adds the given string option to the request
		 * @param option - The name of the option
		 * @param value - The string value of the option
		 */
		public void addOption(String option, String value) { 
			options.add(option, value);
		}

		/**
		 * Adds the given integer option to the request
		 * @param option - The name of the option
		 * @param value - The integer value of the option
		 */
		public void addOption(String option, int value) {
			options.add(option, value);
		}

		/**
		 * Attempts to send the request to the PDF WebAPI server and returns the
		 * HttpResponse from the server.
		 * @return An HttpResponse from the PDF WebAPI server or possibly a 
		 * proxy server unable to reach the PDF WebAPI server
		 * @throws Exception
		 */
		public HttpResponse getResponse() throws Exception {
			// Set the WebAPI URL
			HttpPost post = new HttpPost(url);
			// Add options part to the form request
			String options_json = options.build().toString();
			if(!options_json.equals("{}"))
				mpe_builder.addTextBody("options", options_json);
			// Generate a random part boundary for multipart request
			String boundary = generateBoundary();
			mpe_builder.setBoundary(boundary);
			// Build the multipart HttpEntity and add it to the post
			post.setEntity(mpe_builder.build());
			// Set the required headers
			post.setHeader(HTTP.CONTENT_TYPE,"multipart/form-data; boundary=" + 
					boundary);
			post.setHeader(HTTP.USER_AGENT, "PDFWebApiClient/1.0.0");
			post.setHeader("Accept-Encoding", "gzip, deflate, compress");
			post.setHeader("Accept","");
			// Create an HttpClient to handle the request
			HttpClient client = HttpClientBuilder.create().build();
			// Send the request and return the response
			return client.execute(post);
		}

		/**
		 * This method generates an HTTP multi-part POST boundary for use in the
		 * request
		 * @return A randomly generated boundary string 30-40 characters in 
		 * length
		 */
		private String generateBoundary() {
			final char[] MULTIPART_CHARS =
					("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR"
							+ "STUVWXYZ").toCharArray();
			final StringBuilder buffer = new StringBuilder();
			final Random rand = new Random();
			final int count = rand.nextInt(11) + 30; //random size from 30 to 40
			for (int i = 0; i < count; i++) {
				buffer.append(
						MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
			}
			return buffer.toString();
		}
	}

	// Member variables
	private String id;  ///<The application id string.
	private String key; ///<The application key string.

	// Constructor(s)

	/** This constructor is used to instantiate a PDF WebAPI Application with
	 * the given application id and key to be used for authentication of the
	 * client with the PDF WebAPI server.
	 * @param id - A string containing the application id assigned when 
	 * registering for use of the PDF WebAPI.
	 * @param key - A string containing the application key used to authenticate
	 * PDF WebAPI requests.
	 */
	public Application(String id, String key) {
		/* Store the application id and key so they can be passed to the
		 * request objects when they are created.
		 */
		this.id = id;
		this.key = key;
	}

	// Public Methods

	/**
	 * This method selects the required action URL for the request type and
	 * creates a PDFWebAPIRequest object with the Application id, key, and URL.
	 * @param request_type - A RequestType enumeration of the desired request
	 * @return A PDFWebAPIRequest object initialized for a 
	 * specific request type or null if the request_type is invalid/unsupported.
	 */
	public PDFWebAPIRequest makeRequest(RequestType request_type) {

		final String BASE_URL = 
				"https://pdfprocess.datalogics-cloud.com/api/actions/";
		String url = BASE_URL;
		// Create request-specific service URL
		if(request_type == RequestType.ExportFormData)
			url += "export/form-data";
		else if(request_type == RequestType.FillForm)
			url += "fill/form";
		else if(request_type == RequestType.FlattenForm)
			url += "flatten/form";
		else if(request_type == RequestType.DecorateDocument)
			url += "decorate/document";
		else if(request_type == RequestType.RenderPages)
			url += "render/pages";
		else {
			return null;
		}
		return new PDFWebAPIRequest(id, key, url);
	}

}
