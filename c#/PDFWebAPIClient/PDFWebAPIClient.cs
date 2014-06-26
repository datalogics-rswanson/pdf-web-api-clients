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
using System.Reflection;

using Datalogics.PdfWebApi.RenderPagesOptions;

namespace Datalogics.PdfWebApi.Client
{
    /// <summary>
    /// This client is the responsible for generating request objects that interface
    /// with the PDF Web API server.
    /// </summary>
    public class PdfWebApiClient
    {
        private string id;  //The application id string
        private string key; //The application key string
        public static string Version { private set; get; }

        // Enumerated request types available
        private enum RequestType {ExportFormData, FillForm, FlattenForm,
            RenderPages, DecorateDocument };

        ///<summary>
        ///This constructor is used to instantiate a PdfWebApiClient with the given
        ///application id and key to be used for authentication of the client with
        ///PDF Web API server.
        ///</summary>
        ///<param name="id">The application id registered with PdfWebApi</param>
        ///<param name="key">The application key registered with PdfWebApi</param>
        public PdfWebApiClient(string id, string key)
        {
            // Store the application id and key so they can be passed to the request
            // objects when they are created.
            this.id = id;
            this.key = key;
            // Get the version number
            Version = Assembly.GetAssembly(this.GetType()).GetName().Version.ToString();
        }

        ///<summary>
        /// This method creates a RenderPagesRequest object that is to be used to
        /// request rendering of an image from a pdf.
        /// </summary>
        /// <returns>A RenderPagesRequest object</returns>
        public RenderPagesRequest CreateRenderPagesRequest()
        {
            return new RenderPagesRequest(id, key,GetRequestURL(RequestType.RenderPages));
        }

        /// <summary>
        /// This method creates a FillFormRequest object that is to be used to
        /// request filling of the forms within a pdf using a form data file.
        /// </summary>
        /// <returns>A FillFormRequest object</returns>
        public FillFormRequest CreateFillFormRequest()
        {
            return new FillFormRequest(id, key, GetRequestURL(RequestType.FillForm));
        }

        /// <summary>
        /// Gets the URL of the requested service on the PDF WebAPI server. Each
        /// request type must be submitted to its own unique URL for processing.
        /// </summary>
        /// <param name="requestType">The RequestType to retrieve the Url for</param>
        /// <returns>A Url for the specified RequestType as a Uri</returns>
        private static Uri GetRequestURL(RequestType requestType) {
            // The PDF Web API server base Url
            const string BASE_URL = "https://pdfprocess.datalogics-cloud.com/api/actions/";
            // Request Url to return
            string url = null;
            // Get request specific Url
            switch (requestType) {
                case RequestType.ExportFormData:
                    url = BASE_URL + "export/form-data";
                    break;
                case RequestType.FillForm:
                    url = BASE_URL + "fill/form";
                    break;
                case RequestType.FlattenForm:
                    url = BASE_URL + "flatten/form";
                    break;
                case RequestType.RenderPages:
                    url = BASE_URL + "render/pages";
                    break;
                case RequestType.DecorateDocument:
                    url = BASE_URL + "decorate/document";
                    break;
            }
            return new Uri(url);
        }
    }
}
