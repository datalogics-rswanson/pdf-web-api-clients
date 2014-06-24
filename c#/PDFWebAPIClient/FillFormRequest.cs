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
using System.Runtime.Serialization.Json;
using System.IO;

namespace Datalogics.PDFWebAPI
{
    public sealed class FillFormRequest : PDFWebAPIRequest
    {

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

        public FillFormRequest(string id, string key, string url) : base(id, key, url)
        {
        }

        /**
        * Enables or disables the running of calcuations on numeric form fields.
        * @param disable_calculations - A boolean enabling or disabling the 
        * running of calculations.
        */
        public void SetRunCalculations(bool disableCalculations)
        {
            options.DisableCalculations = disableCalculations;
        }

        /**
        * Enables or disables the generation of appearances on form fields.
        * @param disable_generation - A boolean enabling or disabling the 
        * generation of appearances.
        * 
        * Example: A currency form field would auto-generate a dollar sign
        * before the value of currency (ie: 6.78 -> $6.78)
        */
        public void SetGenerateAppearances(bool disableGeneration)
        {
            options.DisableGeneration = disableGeneration;
        }

        /**
        * Enables or disables the flattening of a pdf's page content.
        * If flattening is turned on, a page's interactivity is
        * removed. That is, fields can no longer be edited. 
        * 
        * @param flatten - A boolean enabling or disabling the 
        * flattening of a PDF.
        */
        public void SetFlattening(bool flatten)
        {
            options.Flatten = flatten;
        }

        protected override HttpContent BuildRequestContent()
        {
            MultipartFormDataContent content = (MultipartFormDataContent)base.BuildRequestContent();
            DataContractJsonSerializer jsonSerializer = new DataContractJsonSerializer(typeof(Options));

            using (MemoryStream memoryStream = new MemoryStream())
            {
                jsonSerializer.WriteObject(memoryStream, options);
                content.Add(new StringContent(Encoding.Default.GetString(memoryStream.ToArray())), "options");
            }

            return content;
        }        
    }
}

