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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/** This class is intended to facilitate parsing of a request response from the
 * PDF WebAPI server.  A PDFWebAPIResponse object is instantiated using the 
 * result from a POST request to the PDF WebAPI server and the the methods 
 * provided herein should be used to examine the response and access any 
 * returned data.
 */
public class PDFWebAPIResponse {

	// Member Variables

	private JsonObject json_error_message = null; ///< JSON error message
	private final HttpResponse webapi_response; ///< Response from PDF WebAPI
	private boolean succeeded = false; ///< Request succeeded HTTP Status == 200

	// Constructor(s)

	/** This constructor takes an HttpResponse from the PDF WebAPI server and
	 * attempts to convert the return content body into a possible JSON error
	 * message if the response code is not 200.
	 * @param webapi_response - An HttpResponse from the PDF WebAPI server.
	 */
	public PDFWebAPIResponse(HttpResponse webapi_response) {
		this.webapi_response = webapi_response;
		// Check if the request was not successful
		if(webapi_response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			// Attempt to read a JSON object error message
			try {
				JsonReader json_reader = 
						Json.createReader(webapi_response.
								getEntity().
								getContent());
				json_error_message = json_reader.readObject();
			} catch (Exception e) {
				System.out.println("Failed to read response from server");
			}
		} else { succeeded = true; }
	}

	/** Checks if the request succeeded.
	 * @return A boolean indicating if the PDF WebAPI request succeeded.
	 */
	public boolean succeeded() {
		return succeeded;
	}

	/** Checks if the request failed.
	 * @return A boolean indicating if the PDF WebAPI request failed.
	 */
	public boolean failed() {
		return !succeeded;
	}

	/** If the request failed, returns the PDF WebAPI error code.
	 * @return A PDF WebAPI error code or 0 if there was no error.
	 */
	public int getErrorCode() {
		int error_code = 0;
		if(failed())
			error_code = json_error_message.getInt("errorCode");
		return error_code;
	}

	/** If the request failed, returns a string associate with the PDF WebAPI
	 * error code.
	 * @return A string describing the PDF WebAPI error, otherwise null.
	 */
	public String getErrorMessage() {
		String error_msg = null;
		if(failed())
			error_msg = json_error_message.getString("errorMessage");
		return error_msg;
	} 

	/** If the request succeeds, returns the InputStream of the result's content
	 * body.
	 * @return The InputStream of the result's content body upon a successful
	 * request, otherwise null.
	 * @throws Exception
	 */
	public InputStream getProcFileInputStream() throws Exception {
		InputStream proc_file_inputstream = null;
		if(succeeded())
			proc_file_inputstream = webapi_response.getEntity().getContent();
		return proc_file_inputstream;
	}

	/** If the request succeeds, this method saves the processed output file to
	 * the requested file path.
	 * @param file - The file path to save the output file.
	 * @return A boolean indicating successful saving of the result data to the
	 * requested output file.
	 */
	public boolean saveProcFile(String file) {
		try {
			InputStream is = webapi_response.getEntity().getContent();
			OutputStream os = new FileOutputStream(file);      
			byte[] buffer = new byte[1024];
			int bytesRead;
			// Read from inputstream to buffer
			while((bytesRead = is.read(buffer)) !=-1){
				// Write from buffer to outputstream/file
				os.write(buffer, 0, bytesRead);
			}
			// Important to close input stream
			is.close(); 
			// Flush OutputStream to write any buffered data to file
			os.flush();
			os.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
