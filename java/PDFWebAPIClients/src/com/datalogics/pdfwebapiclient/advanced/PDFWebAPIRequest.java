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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.Header;

/** This class serves as the base class for all PDF WebAPI requests.  It 
 * contains members and methods common to all current request types.  For each
 * request type, a sub-class should be derived and that class should be 
 * responsible for setting request-specific multi-body request parts using
 * the methods in this class.
 */
public abstract class PDFWebAPIRequest {

	// Constants

	/** The pool of ASCII chars to be used for generating a multipart boundary.
	 * @NOTE This array is taken from the Apache Java MultipartEnityBuilder
	 * class where it is also used for boundary generation.
	 */
	private final static char[] MULTIPART_CHARS =
			"-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	// Member Variables

	/** Holds the multiform parts that will be sent to the PDF WebAPI server.  
	 * Here a part refers to a name and content-body pairing for each part of 
	 * a multipart form request.  A part name may or may not be unique depending
	 * on which set/add method is used.  See addPart() and setUniquePart() for 
	 * more information.
	 */
	private MultiValueMap<String, ContentBody> parts = 
			new MultiValueMap<String, ContentBody>();

	/** The JSONObjectBuilder that holds options for the request.  Prior to the
	 * POST request the JsonObjectBuilder::build() method is called to create
	 * a JsonObject that will be converted to a string and sent with the request
	 * as the body to the "options" part name.
	 **/
	private JsonObjectBuilder options = Json.createObjectBuilder();

	/** The PDF WebAPI Server URL to submit the request to. */
	private String url = null;

	// Constructor(s)

	/** Base constructor that builds the JSON "application" object from the 
	 * given id and key required by all PDF WebAPI requests and sets it as the
	 * first common part to be sent with the request POST.  Also, the PDF WebAPI
	 * request URL is set for the specific request type.
	 * @param id - The application id string.  This is passed in from the 
	 * PDFWebAPIClient object when it creates request classes derived from this
	 * class.
	 * @param key - The application key string.  This is passed in from the
	 * PDFWebAPIClient object when it creates request classes derived from
	 * this class.
	 * @param url - A string containing the PDF WebAPI URL to submit the request
	 * to.
	 */
	protected PDFWebAPIRequest(String id, String key, String url) {
		/* Create the JSON application object common to all requests and put it
		 * into the key-value pairs after converting it to a JSON string
		 */
		setUniquePart("application", new StringBody(
				Json.createObjectBuilder().add("id", id)
				.add("key", key)
				.build()
				.toString(),
				ContentType.TEXT_PLAIN));
		// Copy over the request URL.
		this.url = new String(url);
	}

	// Public Methods

	/** This function sets either the "input" or "inputURL" part of
	 * the request POST based upon the location of the pdfFile.  If the pdfFile
	 * contains a web-URL then the "inputURL" part is used, otherwise the 
	 * "input" part is used and the pdfFile is assumed to be local to the host.
	 * In the latter case, the pdfFile assuming it is found, is sent as a binary
	 * body with the "input" part. <br>
	 * Subsequent calls to this function will overwrite the
	 * previous input method.
	 * @param pdfFile - A string containing the web URL or local path to the
	 * PDF file for the PDF WebAPI server to process.
	 * @param passWord - A string containing the password if one is necessary
	 * to access the pdfFile.  If no password is necessary, an empty or <code>
	 * null</code> string should be used.
	 * @param name - An optional name that the PDF WebAPI server uses when
	 * logging information regarding the processing of the pdfFile.  This 
	 * string may be empty or <code>null</code> if no name is desired.  If the
	 * same name as the pdfFile is desired, use the text string "same".
	 */
	public void setInputFile(String pdfFile, String passWord, String name) {
		// Check if the pdfFile is accessed via the web
		if(pdfFile.toLowerCase().startsWith("http://") ||
				pdfFile.toLowerCase().startsWith("https://")) {
			/* Ensure parts doesn't contain "input" since "input" and
			 *  "inputURL" are mutually exclusive keys
			 */
			parts.remove("input");
			// Associate the pdfFile with the "inputURL" part
			setUniquePart("inputURL", new StringBody(pdfFile, 
					ContentType.TEXT_PLAIN));
		}
		else
		{
			/* Ensure parts doesn't contain "inputURL" since "input" and
			 * "inputURL" are mutually exclusive parts
			 */
			parts.remove("inputURL");
			// Associate the pdfFile with the "input" part
			setUniquePart("input", new FileBody(new File(pdfFile),
					ContentType.DEFAULT_BINARY,
					pdfFile));
		}
		/* If the passWord parameter is not null or empty store the "password"
		 * part and body text
		 */
		if(passWord != null && !passWord.isEmpty()) {
			setUniquePart("password", new StringBody(passWord, 
					ContentType.TEXT_PLAIN));
		}
		// This may be a subsequent call so remove the "password" part if found
		else {
			parts.remove("password");
		}
		/* If the name parameter is not null or empty store the "inputName"
		 * part and text body
		 */
		if(name != null && !name.isEmpty()) {
			// First check if the same name as the pdfFile has been requested
			if(name.compareToIgnoreCase("same") == 0)
				name = pdfFile; // Use the same name to identify the pdfFile
			setUniquePart("inputName", new StringBody(name,
					ContentType.TEXT_PLAIN));
		}
		// This may be a subsequent call so remove the "inputName" part if found
		else {
			parts.remove("inputName");
		}
	}

	/** This method is identical to the setInputFile(String, String, String)
	 * method with the <code>name</code> parameter set to empty or 
	 * <code>null</code>.
	 * @param pdfFile - A string containing the web URL or local path to the
	 * PDF file for the PDF WebAPI server to process.
	 * @param passWord - A string containing the password if one is necessary
	 * to access the pdfFile.  If no password is necessary, an empty or <code>
	 * null</code> string should be used.
	 */
	public void setInputFile(String pdfFile, String passWord) {
		setInputFile(pdfFile, passWord, null);
	}

	/** This method is identical to the setInputFile(String, String, String)
	 * method with both the <code>passWord</code> and <code>name</code> 
	 * parameters set to empty or <code>null</code>.
	 * @param pdfFile - A string containing the web URL or local path to the
	 * PDF file for the PDF WebAPI server to process.
	 */
	public void setInputFile(String pdfFile) {
		setInputFile(pdfFile, null, null);
	}

	/** This method removes all options from the request **/
	public void clearOptions() {
		options = Json.createObjectBuilder();
	}

	/** This method builds a multipart HttpPost request for submission to the
	 * PDF WebAPI server by adding the parts and the options to a 
	 * multipart entity and setting the necessary request headers.
	 * @return An HttpPost multipart request for the PDF WebAPI server.
	 */
	public HttpPost buildRequest() {
		// Create a MultipartEntityBuilder to create the request parts
		MultipartEntityBuilder mp_entity_builder = 
				MultipartEntityBuilder.create();
		// Set the WebAPI URL
		HttpPost post = new HttpPost(url);
		// Iterate over parts adding them to the mp_entity_builder
		Iterator<Map.Entry<String, ContentBody>> it = parts.iterator();
		while(it.hasNext()) {
			Map.Entry<String, ContentBody> entry = it.next();
			mp_entity_builder.addPart(entry.getKey(), entry.getValue());
		}
		/* Build a JSON object from options, convert to string, and add as a
		 * StringBody to the mp_entity_builder
		 */
		String options_json = options.build().toString();
		if(!options_json.equals("{}"))
			mp_entity_builder.addTextBody("options", options_json);
		// Generate a random part boundary for multipart request
		String boundary = generateBoundary();
		mp_entity_builder.setBoundary(boundary);
		// Build the multipart HttpEntity and add it to the post
		post.setEntity(mp_entity_builder.build());
		// Set the required headers
		post.setHeader(HTTP.CONTENT_TYPE,"multipart/form-data; boundary=" + 
				boundary);
		post.setHeader(HTTP.USER_AGENT, "PDFWebApiClient/1.0.0");
		post.setHeader("Accept-Encoding", "gzip, deflate, compress");
		post.setHeader("Accept","");
		return post;
	}

	// DEBUG method to view post headers and parts
	/** This is a DEBUG method used for printing the given POST to the std out
	 * stream so the parts and headers of the multipart request may be examined.
	 * @param post - The post to print out.
	 * @throws IOException
	 */
	public void printPost(HttpPost post) throws IOException {
		// Get headers
		Header[] headers = post.getAllHeaders();
		/* Create a byte array output stream large enough for the parts, fill
		 * it, and convert to string for output.
		 */
		java.io.ByteArrayOutputStream out = 
				new java.io.ByteArrayOutputStream((int)post.
						getEntity().
						getContentLength());
		post.getEntity().writeTo(out);		
		String entityContentAsString = new String(out.toByteArray());		
		// Print POST, headers, and parts
		System.out.println(post.toString());
		for (Header header : headers) {
			System.out.println(header.getName() + ": " + header.getValue());
		}
		System.out.println();
		System.out.println(entityContentAsString);
	}

	/** 
	 * This method sends the multiform POST request to the PDFWebAPI server and
	 * returns a PDFWebAPIResponse object containing the result of the requested
	 * operation or error information if the request failed.
	 * @TODO Remove AllowAllHostNameVerifier once certs issue has been resolved.
	 * @throws Exception
	 */
	public PDFWebAPIResponse getResponse(HttpPost post) throws Exception {
		// Create an HttpClient to handle the request
		HttpClient client = HttpClientBuilder.create().build();
		// Send the request and return the response
		return new PDFWebAPIResponse(client.execute(post));
	}

	// Protected Methods

	/** Sets a unique multiform part in the POST request.  If the request 
	 * contains any part(s) with this name, they will be replaced by this 
	 * part.
	 * @param name - The name of the part.
	 * @param content_body - The content body to set for this part.
	 */
	protected void setUniquePart(String name, ContentBody content_body) {
		// Remove any existing part(s) with this name
		parts.remove(name);
		// Insert the unique part into the part map
		parts.put(name, content_body);
	}

	/** Adds a multiform part that may not have a unique name to the POST
	 *  request.
	 * @param name - The name of the part.
	 * @param content_body - The content body to set for this part.
	 */
	protected void addPart(String name, ContentBody content_body) {
		parts.put(name, content_body);
	}

	/** Adds the given boolean to the options list.  Subsequent calls with the
	 * same option name will replace the previous value in the options list.
	 * @param option - A string identifying the unique option name.
	 * @param value - A boolean to be associated with this option.
	 */
	protected void setOption(String option, boolean value) {
		options.add(option, value);
	}

	/** Adds the given string to the options list.  Subsequent calls with the
	 * same option name will replace the previous value in the options list.
	 * @param option - A string identifying the unique option name.
	 * @param value - A string to be associated with this option.
	 */
	protected void setOption(String option, String value) {
		options.add(option, value);
	}

	/** Adds the given integer to the options list.  Subsequent calls with the
	 * same option name will replace the previous value in the options list.
	 * @param option - A string identifying the unique option name.
	 * @param value - An integer to be associated with this option.
	 */
	protected void setOption(String option, int value) {
		options.add(option, value);
	}

	// Private Methods

	/** This method generates a random multipart boundary of 30 to 40 characters
	 * from the MULTIPART_CHARS set and returns it as a string.
	 * @return A randomly generated multipart request boundary string.
	 */
	private String generateBoundary() {
		final StringBuilder buffer = new StringBuilder();
		final Random rand = new Random();
		final int count = rand.nextInt(11) + 30; // a random size from 30 to 40
		for (int i = 0; i < count; i++) {
			buffer.append(
					MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		return buffer.toString();
	}

}
