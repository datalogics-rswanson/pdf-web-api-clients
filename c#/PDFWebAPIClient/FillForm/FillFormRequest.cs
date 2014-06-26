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

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using System.Runtime.Serialization;
using System.IO;

namespace Datalogics.PdfWebApi.Client
{
    /// <summary>
    /// This class is derived from the PdfWebApiRequest base class and is responsible for
    /// sending FillForm requests to the PDF WebAPI server.
    /// </summary>
    public sealed class FillFormRequest : PdfWebApiRequest
    {
        /// <summary>
        /// This class specifies the data contract required to serialize the FillForm
        /// options to and from a JSON string.
        /// </summary>
        [DataContract]
        private class Options
        {
            [DataMember(Name = "disableCalculation", EmitDefaultValue = false)]
            public bool DisableCalculations { get; set; }
            [DataMember(Name = "disableGeneration", EmitDefaultValue = false)]
            public bool DisableGeneration { get; set; }
            [DataMember(Name = "flatten", EmitDefaultValue = false)]
            public bool Flatten { get; set; }
        }

        private Options options = new Options();

        /// <summary>
        /// Sets the reqiured authorization values and the Url that the request needs.
        /// </summary>
        /// <param name="id">The application id</param>
        /// <param name="key">The application key</param>
        /// <param name="url">The Url for the request</param>
        public FillFormRequest(string id, string key, Uri url) : base(id, key, url) { }

        /// <summary>
        /// This utility method sets the input form-data file for the request.
        /// Derived classes should only use this method if their request requires an input form-data file. 
        /// </summary>
        /// <param name="formDataFile">The form-data file</param>
        public void SetFormData(string formDataFile)
        {
            AddFilePart(formDataFile, "formsData");
        }

        /// <summary>
        /// Enables or disables the running of calculations on numeric form fields
        /// </summary>
        /// <param name="disableCalculations">A boolean specifying if numeric form
        /// fields should have calculations run on them</param>
        public void SetEnableRunCalculations(bool enableCalculations)
        {
            // Method name should imply enabling to conform to best practices so complement option
            options.DisableCalculations = !enableCalculations;
        }

        /// <summary>
        /// Enables or disables the generation of appearances on form fields.
        /// </summary>
        /// <param name="enableGeneration">A boolean specifying if appearances
        /// should be enabled or disabled on form fields</param>
        /// <example>A currency form field would auto-generate a dollar sign
        /// before the value of currency (eg. 6.78 -> $6.78)</example>
        public void SetEnableGenerateAppearances(bool enableGeneration)
        {
            options.DisableGeneration = !enableGeneration;
        }

        /// <summary>
        /// Enables or disables the flattening of a pdf's page content.
        /// If flattening is turned on, a page's interactivity is
        /// removed. That is, fields can no longer be edited.
        /// </summary>
        /// <param name="flatten">A boolean flag indicating if the form
        /// field should be flattened or not</param>
        public void SetFlattening(bool flatten)
        {
            options.Flatten = flatten;
        }

        /// <summary>
        /// This method builds the multiform content for the FillFormRequest
        /// </summary>
        protected override HttpContent BuildRequestContent()
        {
            MultipartFormDataContent content = (MultipartFormDataContent)base.BuildRequestContent();
            content.Add(new StringContent(WriteJsonToString(options), Encoding.UTF8, "application/json"), "options");

            return content;
        }
    }
}

