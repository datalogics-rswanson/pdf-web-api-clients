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
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.IO;

namespace Datalogics.PDFWebAPI
{
    public abstract class PDFWebAPIRequest
    {
        public string Id { get; private set; } //Auto-implemented Property
        public string Key { get; private set; } //Auto-implemented Property
        public string Url { get; private set; } //Auto-implemented Property
        private Dictionary<String, List<HttpContent>> partLists = new Dictionary<string, List<HttpContent>>();

        public PDFWebAPIRequest(string id, string key, string url)
        {
            Id = id;
            Key = key;
            Url = url;
        }

        protected void SetUniquePart(HttpContent content, string name)
        {
            // Remove any existing part(s) with this name
            partLists.Remove(name);
            // Insert the unique part into the part map
            partLists[name] = new List<HttpContent>();
            partLists[name].Add(content);
        }

        protected void AddPart(HttpContent content, string name)
        {
            if (!partLists.ContainsKey(name))
                partLists[name] = new List<HttpContent>();
            partLists[name].Add(content);
        }

        protected void AddFilePart(string file, string name, string contentType = "application/octet-stream", bool isUnique = true)
        {
            FileInfo fileInfo = new FileInfo(file);
            if(fileInfo.Exists)
            {
                StreamContent streamContent = new StreamContent(fileInfo.OpenRead());
                
                ContentDispositionHeaderValue contentDisposition = new ContentDispositionHeaderValue("form-data");
                contentDisposition.Name = name;
                contentDisposition.FileName = fileInfo.Name;
                streamContent.Headers.ContentType = new MediaTypeHeaderValue(contentType);
                streamContent.Headers.ContentDisposition = contentDisposition;
                
                if(isUnique)
                    SetUniquePart(streamContent, name);
                else
                    AddPart(streamContent, name);
            }
        }

        public void SetInputFile(string pdfFile, string inputName = null, string passWord = null)
        {
            // Check if the pdfFile is accessed via the web
            if (pdfFile.StartsWith("http://", true, null) || pdfFile.StartsWith("https://", true, null))
            {
                /* Ensure parts doesn't contain "input" since "input" and
                 *  "inputURL" are mutually exclusive keys
                 */
                partLists.Remove("input");
                // Associate the pdfFile with the "inputURL" part
                SetUniquePart(new StringContent(pdfFile), "inputURL");
            }
            else
            {
                /* Ensure parts doesn't contain "inputURL" since "input" and
                 * "inputURL" are mutually exclusive parts
                 */
                partLists.Remove("inputURL");
                // Associate the pdfFile with the "input" part
                AddFilePart(pdfFile, "input");
            }
            /* If the passWord parameter is not null or empty store the "password"
             * part and body text
             */
            if (!String.IsNullOrEmpty(passWord))
            {
                SetUniquePart(new StringContent(passWord), "password");
            }
            // This may be a subsequent call so remove the "password" part if found
            else
            {
                partLists.Remove("password");
            }
            /* If the name parameter is not null or empty store the "inputName"
             * part and text body
             */
            if (!String.IsNullOrEmpty(inputName))
            {
                SetUniquePart(new StringContent(inputName), "inputName");
            }
            // This may be a subsequent call so remove the "inputName" part if found
            else
            {
                partLists.Remove("inputName");
            }
        }

        public void SetFormData(string formDataFile, string formDataName = null)
        {
            // Associate the pdfFile with the "input" part
            AddFilePart(formDataFile, "formsData");

            /* If the name parameter is not null or empty store the "formDataName"
             * part and text body
             */
            if (!String.IsNullOrEmpty(formDataName))
            {
                SetUniquePart(new StringContent(formDataName), "formDataName");
            }
            // This may be a subsequent call so remove the "formDataName" part if found
            else
            {
                partLists.Remove("formDataName");
            }
        }

        /// <summary>
        /// Blocking method
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public PDFWebAPIResponse GetResponse()
        {
            using (HttpClient httpClient = new HttpClient())
            {
                // May need to set some httpClient.DefaultHeaders here
                return new PDFWebAPIResponse(httpClient.PostAsync(Url, BuildRequestContent()).Result);
            }
        }

        /// <summary>
        /// Asynchronous method
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public async Task<PDFWebAPIResponse> GetResponseAsync()
        {
            using (HttpClient httpClient = new HttpClient()) 
            {
                // May need to set some httpClient.DefaultHeaders here
                return new PDFWebAPIResponse(await httpClient.PostAsync(Url, BuildRequestContent()));
            }
        } 

        protected virtual HttpContent BuildRequestContent()
        {
            // Create a new multi-part content container with a random 128-bit boundary
            MultipartFormDataContent requestContent = new MultipartFormDataContent(System.Guid.NewGuid().ToString());

            // Create the "application" JSON object { "id" : "<id>", "key" : "<key>" } part
            string jsonApplicationString = String.Format(@"{{""id"" : ""{0}"", ""key"" : ""{1}""}}", Id, Key);
            StringContent stringContent = new StringContent(jsonApplicationString, Encoding.UTF8, "application/json");
            requestContent.Add(stringContent, "application");

            // Iterate over partLists and add the part(s) from each list
            // Note: outer var's type is KeyValuePair<String, List<HttpContent>>
            //       inner var's type is HttpContent
            foreach(var partList in partLists)
            {
                // Add each HttpContent part
                // Note: partList.Key is the name common to all parts in this list
                foreach(var part in partList.Value)
                {
                    requestContent.Add(part, partList.Key);
                }
            }

            return requestContent;
        }

        public override string ToString()
        {
            return BuildRequestContent().ReadAsStringAsync().Result;
        }
    }
}
