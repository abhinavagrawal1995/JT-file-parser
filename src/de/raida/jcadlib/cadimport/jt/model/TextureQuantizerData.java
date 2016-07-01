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

package de.raida.jcadlib.cadimport.jt.model;

import de.raida.jcadlib.cadimport.jt.model.quantize.UniformQuantizerData;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.10 Texture Quantizer Data</h>
 * Object Type ID: <code>---</code>
 * <br>A Texture Quantizer Data collection is made up of n Uniform Quantizer Data collections; there is a separate
 * Uniform Quantizer Data collection for each component of the texture coordinates. The number of components is not
 * specified within the quantizer, but rather is determined by the number of texture components present in the
 * underlying data (See Compressed Vertex Texture Coordinate Arrays U8 : Number Components).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TextureQuantizerData {
	/** List of uniform quantizer datas */
	private UniformQuantizerData[] _uniformQuantizerDatas;

	/**
	 * Constructor.
	 * @param uniformQuantizerDatas List of uniform quantizer datas-
	 */
	public TextureQuantizerData(UniformQuantizerData[] uniformQuantizerDatas){
		_uniformQuantizerDatas = uniformQuantizerDatas;
	}

	/**
	 * Returns the quantized range for the x coordinates.
	 * @return Quantized range for the x coordinates
	 */
	public float[] getURange(){
		return new float[]{_uniformQuantizerDatas[0].getMin(), _uniformQuantizerDatas[0].getMax()};
	}

	/**
	 * Returns the quantized range for the y coordinates.
	 * @return Quantized range for the y coordinates
	 */
	public float[] getVRange(){
		return new float[]{_uniformQuantizerDatas[1].getMin(), _uniformQuantizerDatas[1].getMax()};
	}

	/**
	 * Reads a TextureQuantizerData object.
	 * @param  workingContext   Working context
	 * @param  numberComponents Number of components
	 * @return                  TextureQuantizerData instance
	 */
	public static TextureQuantizerData read(WorkingContext workingContext, int numberComponents){
		UniformQuantizerData[] uniformQuantizerDatas = new UniformQuantizerData[numberComponents];

		for(int i = 0; i < numberComponents; i++){
			uniformQuantizerDatas[i] = UniformQuantizerData.read(workingContext);
		}

		return new TextureQuantizerData(uniformQuantizerDatas);
	}
}
