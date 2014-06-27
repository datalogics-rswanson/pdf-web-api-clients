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
using Datalogics.PdfWebApi.Client;
using Datalogics.PdfWebApi.Request;
using Datalogics.PdfWebApi.Response;
using Datalogics.PdfWebApi.RenderPagesOptions;

namespace Datalogics.PdfWebApi.Client.ConsoleTestDriver
{
    /// <summary>
    /// This program demonstrates how to use the PdfWebApiClient to make requests to
    /// the PDF WebAPI server and how to process the responses.
    /// </summary>
    class Program
    {
        static void Main(string[] args)
        {
            // Application id and key
            const string id = "<ENTER YOUR APPLICATION ID HERE>";
            const string key = "<ENTER YOUR APPLICATION KEY HERE>";

            // Create a new PDF Web API Client with this id and key
            PdfWebApiClient client = new PdfWebApiClient(id, key);

            // Display the version
            Console.WriteLine("C# PdfWebApiClient V." + PdfWebApiClient.Version);

            // Create a RenderPages Request
            RenderPagesRequest renderPagesRequest = client.CreateRenderPagesRequest();

            // Setup the Request
            renderPagesRequest.SetInputFile("..\\..\\input\\ducky.pdf");
            renderPagesRequest.SetColorModel(ColorModel.RGB);
            renderPagesRequest.SetImageHeight(300);
            renderPagesRequest.SetSmoothing(SmoothingOptions.Line | SmoothingOptions.Text);
           
            // Get the response
            PdfWebApiResponse renderPagesResponse = renderPagesRequest.GetResponse();

            // Check if the request succeeded
            if (renderPagesResponse.Succeeded)
            {
                Console.WriteLine("Success - writing output file \"output\\image.jpg\"");
                bool imageWritten = renderPagesResponse.SaveProcFile("..\\..\\output\\image.jpg").Result;
            }
            else
            {
                Console.WriteLine("Failure");
                Console.WriteLine("ErrorCode: " + renderPagesResponse.ErrorCode);
                Console.WriteLine("ErrorMessage: " + renderPagesResponse.ErrorMessage);
            }
            
            FillFormRequest fillFormRequest = client.CreateFillFormRequest();

            // Setup the FillForm Request
            fillFormRequest.SetInputFile("..\\..\\input\\FruitForm_1_AcroForm.pdf");
            fillFormRequest.SetFormData("..\\..\\input\\FruitForm_1_AcroForm_data.xfdf");
            fillFormRequest.SetFlattening(true);

            // Get the response
            PdfWebApiResponse fillFormResponse = fillFormRequest.GetResponse();

            // Check if the request succeeded
            if (fillFormResponse.Succeeded)
            {
                Console.WriteLine("Success - writing output file \"output\\FruitForm_1_AcroForm.pdf\"");
                bool pdfWritten = fillFormResponse.SaveProcFile("..\\..\\output\\FruitForm_1_AcroForm.pdf").Result;
            }
            else
            {
                Console.WriteLine("Failure");
                Console.WriteLine("ErrorCode: " + fillFormResponse.ErrorCode);
                Console.WriteLine("ErrorMessage: " + fillFormResponse.ErrorMessage);
            }
            
            // Keep console open
            Console.WriteLine("\nHit any key to exit");
            Console.ReadKey();
        }
    }
}
