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

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;

/** This class extends the PDFWebAPIRequest class and is used to facilitate the
 * sending of FillForm requests to the PDFWeb API server.  An object of this 
 * class is instantiated through the PDFWebAPIClient.createFillFormRequest()
 * method.
 */
public final class FillFormRequest extends PDFWebAPIRequest {

	// Constructor

	/** FillFormRequest constructor that takes the application id, key, and the
	 * required URL of the FillForm service.
	 * @param id - A string containing the application id.
	 * @param key - A string containing the application key.
	 * @param url - A string containing the URL of the FillForm request service 
	 * running on the PDF WebAPI server.
	 */
	public FillFormRequest(String id, String key, String url) {
		super(id, key, url);
	}

	/** Sets the form data that will be sent to the FillForm request and applied
	 * to the pdf input file.
	 * @param forms_data_file - A string containing the path to the form data
	 * file, *.fdf.
	 */
	public void setFormsData(String forms_data_file) {
		setUniquePart("formsData", new FileBody(new File(forms_data_file),
				ContentType.DEFAULT_BINARY,
				forms_data_file));
	}

	/** Enables or disables calculations in the pdf form.
	 * @param disable_calculation - A boolean to enable or disable calculations.
	 */
	public void setDisableCalculation(boolean disable_calculation) {
		setOption("disableCalculation", disable_calculation);
	}

	/** Enables or disables generation for a form field annotation stream to 
	 * ensure it is displayed properly after being populated with form data.
	 * @param disable_generation - A boolean enabling or disabling the 
	 * generation of the field annotation streams.
	 */
	public void setDisableGeneration(boolean disable_generation) {
		setOption("disableGeneration", disable_generation);
	}

	/** Sets the option to flatten the form after populating the form data.
	 * @param flatten - A boolean indicating if the form should be flattened or
	 * not after processing.
	 */
	public void setFlatten(boolean flatten) {
		setOption("flatten", flatten);
	}
}
