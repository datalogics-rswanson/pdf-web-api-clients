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

/** This class extends the PDFWebAPIRequest class and is used to facilitate the
 * sending of RenderPages requests to the PDFWeb API server.  An object of 
 * this class is instantiated through the 
 * PDFWebAPIClient.createRenderPagesRequest() method.
 */
public final class RenderPagesRequest extends PDFWebAPIRequest{

	// Public Enumerations

	/** colorModel enumerations */
	public enum ColorModel {RGB, Gray, RGBA, CMYK}
	/** compression enumerations */
	public enum Compression {LZW, JPG}
	/** outputFormat enumerations */
	public enum OutputFormat {PNG, BMP, GIF, JPG, TIF}
	/** pdfRegion enumerations */
	public enum PDFRegion {Crop, Art, Bleed, Bounding, Media, Trim}
	/** resampler enumerations */
	public enum Resampler {Auto, Bicubic, None}
	/** smoothing enumerations */
	public enum Smoothing {All, None, Text, Line, Image}

	/** RenderPagesRequest constructor that takes the application id, key, and
	 * required URL of the FillForm service.
	 * @param id - A string containing the application id.
	 * @param key - A string containing the application key.
	 * @param url - A string containing the URL of the RenderPages request 
	 * service running on the PDF WebAPI server.
	 */
	public RenderPagesRequest(String id, String key, String url) {
		super(id, key, url);
	}

	/** Sets the desired color model applied to the requested rendering.
	 * @param color_model - A color model of type ColorModel.
	 */
	public void setColorModel(ColorModel color_model) {
		String[] options = {"rgb", "gray", "rgba", "cmyk"};
		setOption("colorModel", options[color_model.ordinal()]);
	}

	/** Sets the image compression to be used during the rendering.
	 * @param compression - A compression of type Compression.
	 */
	public void setCompression(Compression compression) {
		String[] options = {"lzw", "jpg"};
		setOption("compression", options[compression.ordinal()]);
	}

	/** Enables or disables color management of the rendering.
	 * @param disable_color_management - A boolean enabling or disabling the
	 * color management.
	 */
	public void setDisableColorManagement(boolean disable_color_management) {
		setOption("disableColorManagement", disable_color_management);
	}

	/** Enables or disables thin line enhancement of the rendered images.
	 * @param disable_tle - A boolean enabling or disabling thin line
	 * enhancment.
	 */
	public void setDisableThinLineEnhancement(boolean disable_tle) {
		setOption("disableThinLineEnhancement", disable_tle);
	}

	/** Set the image height of the output image in pixels.
	 * @param image_height - The image height.
	 */
	public void setImageHeight(int image_height) {
		setOption("imageHeight", image_height);
	}

	/** Set the image width of the output image in pixels.
	 * @param image_width - The image width.
	 */
	public void setImageWidth(int image_width) {
		setOption("imageWidth", image_width);
	}

	/** Sets if overprinting preview simulation shoould be used.
	 * @param opp - A boolean indicating if overprinting preview simulation
	 * should be used.
	 */
	public void setOPP(boolean opp) {
		setOption("OPP", opp);
	}

	/** Sets the output format of the rendered image.
	 * @param output_format - A format of the type OutputFormat.
	 */
	public void setOutputFormat(OutputFormat output_format) {
		String[] options = {"png", "bmp", "gif", "jpg", "tif"};
		setOption("outputFormat", options[output_format.ordinal()]);
	}

	/** Sets the page that should be rendered.
	 * @param page - The desired pdf page to render.
	 */
	public void setExportPage(int page) {
		setOption("pages", Integer.toString(page));
	}

	/** Sets the desired page range to render if the output format supports
	 * rendering multiple pages to a single output image.
	 * @param page_from - The page to start rendering from (inclusive)
	 * @param page_to - The page to render to (inclusive)
	 */
	public void setExportPages(int page_from, int page_to) {
		setOption("pages", Integer.toString(page_from) + "-" +
				Integer.toString(page_to));
	}

	/** Sets the last page as the page to be rendered. */
	public void setExportLastPage() {
		setOption("pages", "last");
	}

	/** Sets the region of the pdf pages to render.
	 * @param pdf_region - A region of the type PDFRegion.
	 */
	public void setPDFRegion(PDFRegion pdf_region) {
		String[] options = {"crop", "art", "bleed", "bounding", "media",
		"trim"};
		setOption("pdfRegion", options[pdf_region.ordinal()]);
	}

	/** Determines if the image should be rendered as a print preview.
	 * @param print_preview - A boolean indicating if the image should be
	 * rendered as a print preview.
	 */
	public void setPrintPreview(boolean print_preview) {
		setOption("printPreview", print_preview);
	}

	/** Sets the type of image sampling to use on the image.
	 * @param resampler                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
	 */
	public void setResampler(Resampler resampler) {
		String[] options = {"auto", "bicubic", "none"};
		setOption("resampler", options[resampler.ordinal()]);
	}

	/** Sets the resolution of the image in dpi.
	 * @param resolution - The resolution of the image in dpi.
	 */
	public void setResolution(int resolution) {
		setOption("resolution", resolution);
	}

	/** Sets the smoothing if any to be applied to the output image.
	 * @param smoothing - An smoothing option of the type Smoothing.
	 */
	public void setSmoothing(Smoothing smoothing) {
		String[] options = {"all", "none", "text", "line", "image"};
		setOption("smoothing", options[smoothing.ordinal()]);
	}

	/** Enables and disables the rendering of annotations.
	 * @param suppress_annotations - A boolean indicating if annotations should
	 * be suppressed or rendered.
	 */
	public void setSuppressAnnotations(boolean suppress_annotations) {
		setOption("supressAnnotations", suppress_annotations);
	}

}
