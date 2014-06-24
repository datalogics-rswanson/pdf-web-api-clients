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

namespace Datalogics.PDFWebAPI
{
    /// <summary>
    /// This program demonstrates how to use the PDFWebAPIClient to make requests to
    /// the PDF Web API server
    /// <author> R. Swanson - rswanson@datalogics.com </author>
    /// </summary>
    class Program
    {
        static void Main(string[] args)
        {
            const string id = "54208b6e";
		    const string key = "020042be31edb98a32596a5530e11a97";

            // Create a new PDF Web API Client with this id and key
            PDFWebAPIClient client = new PDFWebAPIClient(id, key);

            // Create a RenderPages Request
            RenderPagesRequest request = client.CreateRenderPagesRequest();

            // Setup the Request
            request.SetInputFile("..\\..\\input\\ducky.pdf");
            request.SetColorModel(RenderPagesRequest.ColorModel.RGB);
            request.SetImageHeight(300);
            request.SetOutputFormat(RenderPagesRequest.OutputFormat.JPG);

            // Get the response
            PDFWebAPIResponse response = request.GetResponse();

            // Celebrate or Cry
            if(response.Succeeded)
            {
                Console.WriteLine("Success - writing output file \"output\\image.jpg\"");
                bool imageWritten = response.SaveProcFile("..\\..\\output\\image.jpg").Result;
            }
            else
            {
                Console.WriteLine("Failure");
                Console.WriteLine("ErrorCode: " + response.GetErrorCode());
                Console.WriteLine("ErrorMessage: " + response.GetErrorMessage());
            }

            FillFormRequest fillFormRequest = client.CreateFillFormRequest();

            // Setup the FillForm Request
            fillFormRequest.SetInputFile("..\\..\\input\\FruitForm_1_AcroForm.pdf");
            fillFormRequest.SetFormData("..\\..\\input\\FruitForm_1_AcroForm_data.xfdf");
            fillFormRequest.SetFlattening(true);          

            // Get the response
            PDFWebAPIResponse fillFormResponse = fillFormRequest.GetResponse();

            // Celebrate or Cry
            if (fillFormResponse.Succeeded)
            {
                Console.WriteLine("Success - writing output file \"output\\FruitForm_1_AcroForm.pdf\"");
                bool pdfWritten = fillFormResponse.SaveProcFile("..\\..\\output\\FruitForm_1_AcroForm.pdf").Result;
            }
            else
            {
                Console.WriteLine("Failure");
                Console.WriteLine("ErrorCode: " + fillFormResponse.GetErrorCode());
                Console.WriteLine("ErrorMessage: " + fillFormResponse.GetErrorMessage());
            }

            // Keep console open
            Console.WriteLine("\nHit any key to exit");
            Console.ReadKey();
        }
    }
}
