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
    public sealed class RenderPagesRequest : PDFWebAPIRequest
    {
        /** colorModel enumerations */
        public enum ColorModel { RGB, Gray, RGBA, CMYK }
        /** compression enumerations */
        public enum Compression { LZW, JPG }
        /** outputFormat enumerations */
        public enum OutputFormat { PNG, BMP, GIF, JPG, TIF }
        /** pdfRegion enumerations */
        public enum PDFRegion { Crop, Art, Bleed, Bounding, Media, Trim }
        /** resampler enumerations */
        public enum Resampler { Auto, Bicubic, None }
        /** smoothing enumerations */
        public enum Smoothing { All, None, Text, Line, Image }

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
            public string PDFRegion { get; set; }
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

        public RenderPagesRequest(string id, string key, string url) : base(id, key, url)
        {
        }

        // Sets the desired color model applied to the requested rendering.
        // color_model - A color model of type ColorModel.
        //
        public void SetColorModel(ColorModel colorModel)
        {
            string[] colorModelOptions = { "rgb", "gray", "rgba", "cmyk" };
            options.ColorModel = colorModelOptions[(int)colorModel];
        }

        /** Sets the image compression to be used during the rendering.
	    * @param compression - A compression of type Compression.
	    */
        public void SetCompression(Compression compression)
        {
            string[] compressionOptions = { "lzw", "jpg" };
            options.Compression = compressionOptions[(int)compression];
        }

        /** Enables or disables color management of the rendering.
	    * @param disable_color_management - A boolean enabling or disabling the
	    * color management.
	    */
        public void SetDisableColorManagement(bool disableColorManagement)
        {
            options.DisableColorManagement = disableColorManagement;
        }

        /** Enables or disables thin line enhancement of the rendered images.
	    * @param disable_tle - A boolean enabling or disabling thin line
	    * enhancment.
	    */
        public void SetDisableThinLineEnhancement(bool disableThinLineEnhancement)
        {
            options.DisableThinLineEnhance = disableThinLineEnhancement;
        }

        /** Set the image height of the output image in pixels.
	    * @param image_height - The image height.
	    */
        public void SetImageHeight(int imageHeight)
        {
            options.ImageHeight = imageHeight;
        }

        /** Set the image width of the output image in pixels.
	    * @param image_width - The image width.
	    */
        public void SetImageWidth(int imageWidth)
        {
            options.ImageWidth = imageWidth;
        }

        /** Sets if overprinting preview simulation shoould be used.
	    * @param opp - A boolean indicating if overprinting preview simulation
	    * should be used.
	    */
        public void SetOverPrintingPreview(bool overPrintingPreview)
        {
            options.OverPrintingPreview = overPrintingPreview;
        }

        /** Sets the output format of the rendered image.
	    * @param output_format - A format of the type OutputFormat.
	    */
        public void SetOutputFormat(OutputFormat outputFormat)
        {
            string[] formats = { "png", "bmp", "gif", "jpg", "tif" };
            options.OutputFormat = formats[(int)outputFormat];
        }

        /** Sets the page that should be rendered.
	    * @param page - The desired pdf page to render.
	    */
        public void SetExportPage(int page)
        {
            options.ExportPages = page.ToString();
        }

        /** Sets the desired page range to render if the output format supports
	    * rendering multiple pages to a single output image.
	    * @param page_from - The page to start rendering from (inclusive)
	    * @param page_to - The page to render to (inclusive)
	    */
        public void SetExportPages(int pageFrom, int pageTo)
        {
            options.ExportPages = pageFrom.ToString() + "-" + pageTo.ToString();
        }

        /** Sets the last page as the page to be rendered. */
        public void SetExportLastPage()
        {
            options.ExportPages = "last";
        }

        /** Sets the region of the pdf pages to render.
	    * @param pdf_region - A region of the type PDFRegion.
	    */
        public void SetPDFRegion(PDFRegion pdfRegion)
        {
            string[] regionOptions = { "crop", "art", "bleed", "bounding", "media", "trim" };
            options.PDFRegion = regionOptions[(int)pdfRegion];
        }

        /** Determines if the image should be rendered as a print preview.
	    * @param print_preview - A boolean indicating if the image should be
	    * rendered as a print preview.
	    */
        public void SetPrintPreview(bool printPreview)
        {
            options.PrintPreview = printPreview;
        }

        /** Sets the type of image sampling to use on the image.
	    * @param resampler                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
	    */
        public void SetResampler(Resampler resampler) {
	        string[] resamplerOptions = {"auto", "bicubic", "none"};
            options.Resampler = resamplerOptions[(int)resampler];
        }

        /** Sets the resolution of the image in dpi.
	    * @param resolution - The resolution of the image in dpi.
	    */
        public void SetResolution(int resolution)
        {
            options.Resolution = resolution;
        }

        /** Sets the smoothing if any to be applied to the output image.
	    * @param smoothing - An smoothing option of the type Smoothing.
	    */
        public void SetSmoothing(Smoothing smoothing)
        {
            string[] smoothingOptions = { "all", "none", "text", "line", "image" };
            options.Smoothing = smoothingOptions[(int)smoothing];
        }

        /** Enables and disables the rendering of annotations.
	    * @param suppress_annotations - A boolean indicating if annotations should
	    * be suppressed or rendered.
	    */
        public void SetSuppressAnnotations(bool suppressAnnotations)
        {
            options.SupressAnnotations = suppressAnnotations;
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
