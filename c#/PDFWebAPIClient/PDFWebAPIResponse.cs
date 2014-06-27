﻿//Copyright (c) 2014, Datalogics, Inc. All rights reserved.
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

namespace Datalogics.PdfWebApi.Response
{
    /// <summary>
    /// This class wraps the response from the PDF WebAPI server after submitting a request.
    /// If an error occurs, the response will contain an error code and an error message specifying
    /// the details of the error.  Upon a successful request, if a processed file has been returned,
    /// it may be saved off with the SaveProcFile() method.
    /// </summary>
    public class PdfWebApiResponse
    {
        /// <summary>
        /// This class specifies the data contract required to serialize a JSON object containing
        /// an integer "errorCode" and a string "errorMessage" to and from a JSON string.
        /// </summary>
        /// <remarks>PDF Web API returns errors in this JSON string format</remarks>
        [DataContract]
        private class ErrorInfo
        {
            [DataMember(Name = "errorCode")]
            public int ErrorCode { get; set; }
            [DataMember(Name = "errorMessage")]
            public string ErrorMessage { get; set; }
        }

        private readonly HttpResponseMessage httpResponseMessage;
        private readonly ErrorInfo errorInfo = new ErrorInfo();
        public bool Succeeded { get; private set; }
        public bool Failed { get{ return !Succeeded; } }
        public int ErrorCode { get { return errorInfo.ErrorCode; } }
        public string ErrorMessage { get { return errorInfo.ErrorMessage; } }

        /// <summary>
        /// This constructor takes an HttpResponse from the PDF WebAPI server and
        /// attempts to convert the return content body into a possible JSON error
        /// message if the response code is not OK (200).
        /// </summary>
        /// <param name="httpResponseMessage">An HttpResponseMessage from the PDF WebAPI server</param>
        public PdfWebApiResponse(HttpResponseMessage httpResponseMessage)
        {
            this.httpResponseMessage = httpResponseMessage;
            // Check if the request was not successful
            if (httpResponseMessage.StatusCode != System.Net.HttpStatusCode.OK){
                // Attempt to read a JSON object error message
                DataContractJsonSerializer dataContractJsonSerializer = 
                    new DataContractJsonSerializer(typeof(ErrorInfo));
                errorInfo = (ErrorInfo)dataContractJsonSerializer.ReadObject(httpResponseMessage.Content.ReadAsStreamAsync().Result);
            }
            else 
            { 
                errorInfo.ErrorCode = 0;
                errorInfo.ErrorMessage = "";
                Succeeded = true; 
            }
        }

        /// <summary>
        /// If the request succeeds, this method saves the processed output file to
        /// the requested file path.
        /// </summary>
        /// <param name="fileName">The file path to save the processed return file to</param>
        /// <returns>A boolean indicating successful saving of the file</returns>
        public async Task<bool> SaveProcFile(string fileName)
        {
            if (Failed)
                return false;

            using (Stream stream = await httpResponseMessage.Content.ReadAsStreamAsync())
            {
                using (FileStream fileStream = File.Open(fileName, FileMode.Create))
                {
                    await stream.CopyToAsync(fileStream);
                    //Note: This may not be the best way to test for and return success (handle case where file already exists)
                    return File.Exists(fileName);
                }
            }
        }
    }
}