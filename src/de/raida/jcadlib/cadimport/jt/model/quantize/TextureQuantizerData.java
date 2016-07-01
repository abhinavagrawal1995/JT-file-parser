//################################################################################
//	The MIT License
//
//	Copyright (c) 2014 Johannes Raida
//
//	Permission is hereby granted, free of charge, to any person obtaining a copy
//	of this software and associated documentation files (the "Software"), to deal
//	in the Software without restriction, including without limitation the rights
//	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//	copies of the Software, and to permit persons to whom the Software is
//	furnished to do so, subject to the following conditions:
//
//	The above copyright notice and this permission notice shall be included in
//	all copies or substantial portions of the Software.
//
//	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//	THE SOFTWARE.
//################################################################################

package de.raida.jcadlib.cadimport.jt.model.quantize;

import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.5 Texture Quantizer Data</h>
 * Object Type ID: <code>---</code>
 * <br>A Texture Quantizer Data collection is made up of two Uniform Quantizer Data
 * collections; there is a separate Uniform Quantizer Data collection for the U,
 * and V values of texture coordinates.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TextureQuantizerData {
	/**
	 * Constructor.
	 * @param uUniformQuantizerData U uniform quantizer data
	 * @param vUniformQuantizerData V uniform quantizer data
	 */
	public TextureQuantizerData(UniformQuantizerData uUniformQuantizerData, UniformQuantizerData vUniformQuantizerData){
	}

	/**
	 * Reads a TextureQuantizerData object.
	 * @param  workingContext Working context
	 * @return                TextureQuantizerData instance
	 */
	public static TextureQuantizerData read(WorkingContext workingContext){
		return new TextureQuantizerData(UniformQuantizerData.read(workingContext),
										UniformQuantizerData.read(workingContext));
	}
}
