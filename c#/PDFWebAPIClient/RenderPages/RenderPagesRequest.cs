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
using System.Globalization;

using Datalogics.PdfWebApi.RenderPagesOptions;

namespace Datalogics.PdfWebApi.Request
{
    /// <summary>
    /// This class is derived from the PdfWebApiRequest base class and is responsible for
    /// sending RenderPage requests to the PDF WebAPI server.
    /// </summary>
    public sealed class RenderPagesRequest : PdfWebApiRequest
    {
        /// <summary>
        /// This class specifies the data contract required to serialize the RenderPages
        /// options to and from a JSON string.
        /// </summary>
        [DataContract]
        private class Options
        {
            [DataMember(Name = "colorModel", EmitDefaultValue = false)]
            public string ColorModel { get; set; }
            [DataMember(Name = "compression", EmitDefaultValue = false)]
            public string Compression { get; set; }
            [DataMember(Name = "disableColorManagement", EmitDefaultValue = false)]
            public bool DisableColorManagement { get; set; }
            [DataMember(Name = "disableThinLineEnhancement", EmitDefaultValue = false)]
            public bool DisableThinLineEnhance { get; set; }
            [DataMember(Name = "imageHeight", EmitDefaultValue = false)]
            public int ImageHeight { get; set; }
            [DataMember(Name = "imageWidth", EmitDefaultValue = false)]
            public int ImageWidth { get; set; }
            [DataMember(Name = "OPP", EmitDefaultValue = false)]
            public bool OverPrintingPreview { get; set; }
            [DataMember(Name = "outputFormat", EmitDefaultValue = false)]
            public string OutputFormat { get; set; }
            [DataMember(Name = "pages", EmitDefaultValue = false)]
            public string ExportPages { get; set; }
            [DataMember(Name = "pdfRegion", EmitDefaultValue = false)]
            public string PdfRegion { get; set; }
            [DataMember(Name = "printPreview", EmitDefaultValue = false)]
            public bool PrintPreview { get; set; }
            [DataMember(Name = "resampler", EmitDefaultValue = false)]
            public string Resampler { get; set; }
            [DataMember(Name = "resolution", EmitDefaultValue = false)]
            public int Resolution { get; set; }
            [DataMember(Name = "smoothing", EmitDefaultValue = false)]
            public string Smoothing { get; set; }
            [DataMember(Name = "supressAnnotations", EmitDefaultValue = false)]
            public bool SupressAnnotations { get; set; }
        }

        private Options options = new Options();

        /// <summary>
        /// Sets the reqiured authorization values and the Url that the request needs.
        /// </summary>
        /// <param name="id">The application id</param>
        /// <param name="key">The application key</param>
        /// <param name="url">The Url for the request</param>
        public RenderPagesRequest(string id, string key, Uri url) : base(id, key, url) { }

        ///<summary>
        /// Sets the desired color model applied to the requested rendering.
        /// color_model - A color model of type ColorModel.
        /// <param name="colorModel">The color model to use for the rendering</param>
        public void SetColorModel(ColorModel colorModel)
        {
            options.ColorModel = colorModel.ToString();
        }
 
        /// <summary>
        /// Sets the desired image compression for TIF images.
        /// </summary>
        /// <param name="compression">The compression type to use</param>
        /// <remarks>This option is ignored if the output format is not TIF</remarks>
        public void SetTiffCompression(CompressionType compression)
        {
            options.Compression = compression.ToString();
        }

        /// <summary>
        /// Enable or disables color management of the rendering.
        /// </summary>
        /// <param name="enableColorManagement">A boolean enabling or disabling color managment</param>
        public void SetEnableColorManagement(bool enableColorManagement)
        {
            // Method name should imply enabling to conform to best practices so complement option
            options.DisableColorManagement = !enableColorManagement;
        }

        /// <summary>
        /// Enable or disable thin line enhancement.
        /// </summary>
        /// <param name="enableThinLineEnhancement">A boolean enabling or disabling thin line
        /// enhancement</param>
        public void SetEnableThinLineEnhancement(bool enableThinLineEnhancement)
        {
            // Method name should imply enabling to conform to best practices so complement option
            options.DisableThinLineEnhance = !enableThinLineEnhancement;
        }

        /// <summary>
        /// Set the image height of the output image in pixels.
        /// </summary>
        /// <param name="imageHeight">The height in pixels</param>
        public void SetImageHeight(int imageHeight)
        {
            options.ImageHeight = imageHeight;
        }

        /// <summary>
        /// Set the image width of the output image in pixels.
        /// </summary>
        /// <param name="imageWidth">The width in pixels</param>
        public void SetImageWidth(int imageWidth)
        {
            options.ImageWidth = imageWidth;
        }

        /// <summary>
        /// Sets if the overprinting preview simulation should be used.
        /// </summary>
        /// <param name="overprintingPreview">A boolean indicating if overprinting preview
        /// simulation should be used.</param>
        public void SetOverprintingPreview(bool overprintingPreview)
        {
            options.OverPrintingPreview = overprintingPreview;
        }

        /// <summary>
        /// Sets the output format of the image.
        /// </summary>
        /// <param name="outputFormat">An OutputFormat type</param>
        public void SetOutputFormat(OutputFormat outputFormat)
        {
            options.OutputFormat = outputFormat.ToString();
        }

        /// <summary>
        /// Sets the single pdf page that should be rendered.
        /// </summary>
        /// <param name="page">The index of the page to render.</param>
        public void SetExportPage(int page)
        {
            options.ExportPages = page.ToString(CultureInfo.InvariantCulture);
        }

        /// <summary>
        /// Sets the desired page range to render if the output format supports
        /// rendering multiple pages to a single output image.
        /// </summary>
        /// <param name="pageFrom"> The page to start rendering from (inclusive)</param>
        /// <param name="pageTo">The page to render to (inclusive)</param>
        public void SetExportPages(int pageFrom, int pageTo)
        {
            options.ExportPages = pageFrom.ToString(CultureInfo.InvariantCulture) + 
                "-" + pageTo.ToString(CultureInfo.InvariantCulture);
        }

        /// <summary>
        /// Sets the last page as the page to be rendered.
        /// </summary>
        public void SetExportLastPage()
        {
            options.ExportPages = "last";
        }

        /// <summary>
        /// Sets the region of the pdf to be rendered.
        /// </summary>
        /// <param name="pdfRegion">An enumeration of the type pdfRegion</param>
        public void SetPdfRegion(PdfRegion pdfRegion)
        {
            options.PdfRegion = pdfRegion.ToString();
        }

        /// <summary>
        /// Sets whether or not the image should be rendered as a print preview.
        /// </summary>
        /// <param name="printPreview">A boolean indicating if the image should be
        /// rendered as a print preview</param>
        public void SetPrintPreview(bool printPreview)
        {
            options.PrintPreview = printPreview;
        }

        /// <summary>
        /// Sets the type of sampling to use on the image.
        /// </summary>
        /// <param name="resampler">An enumeration of the Resample type</param>
        public void SetResampler(Resampler resampler)
        {
            options.Resampler = resampler.ToString();
        }

        /// <summary>
        /// Sets the resolution of the image in dpi.
        /// </summary>
        /// <param name="resolution">The resolution in dpi</param>
        public void SetResolution(int resolution)
        {
            options.Resolution = resolution;
        }

        /// <summary>
        /// Sets the smoothing/aliasing to be applied to the output image.
        /// </summary>
        /// <param name="smoothing">Any combination of the smoothing flags</param>
        /// <remarks>The None option will have no effect if combined with other flags
        /// using a bitwise OR</remarks>
        public void SetSmoothing(SmoothingOptions smoothing)
        {
            options.Smoothing = smoothing.ToString();
        }

        /// <summary>
        /// Sets whether or not annotations should be supressed.
        /// </summary>
        /// <param name="suppressAnnotations">A boolean indicating if annotations
        /// should be supressed</param>
        public void SetSuppressAnnotations(bool suppressAnnotations)
        {
            options.SupressAnnotations = suppressAnnotations;
        }

        /// <summary>
        /// This method builds the multiform content for the RenderPagesRequest
        /// </summary>
        protected override HttpContent BuildRequestContent()
        {
            MultipartFormDataContent content = (MultipartFormDataContent)base.BuildRequestContent();
            content.Add(new StringContent(WriteJsonToString(options), Encoding.UTF8, "application/json"), "options");

            return content;
        }
    }   
}
