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

import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.6 Color Quantizer Data</h>
 * Object Type ID: <code>---</code>
 * <br>A Color Quantizer Data collection contains the quantizer information
 * for each of the color components. The Color Quantizer utilizes a separate
 * Uniform Quantizer Data collection for each of the 4 color components, but
 * if the HSV color model is being used, then it is not necessary to store a
 * complete Uniform Quantizer Data Collection.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class ColorQuantizerData {
	/** Red uniform quantizer data */
	private UniformQuantizerData _redUniformQuantizerData;

	/** Green uniform quantizer data */
	private UniformQuantizerData _greenUniformQuantizerData;

	/** Blue uniform quantizer data */
	private UniformQuantizerData _blueUniformQuantizerData;

	/**
	 * Constructor.
	 * @param numberOfHueBits           Number of hue bits
	 * @param numberOfSaturationBits    Number of saturation bits
	 * @param numberOfValueBits         Number of value bits
	 * @param numberOfAlphaBits         Number of alpha bits
	 * @param redUniformQuantizerData   Red uniform quantizer data
	 * @param greenUniformQuantizerData Green uniform quantizer data
	 * @param blueUniformQuantizerData  Blue uniform quantizer data
	 * @param alphaUniformQuantizerData Alpha uniform quantizer data
	 */
	public ColorQuantizerData(Integer numberOfHueBits, Integer numberOfSaturationBits, Integer numberOfValueBits, Integer numberOfAlphaBits, UniformQuantizerData redUniformQuantizerData, UniformQuantizerData greenUniformQuantizerData, UniformQuantizerData blueUniformQuantizerData, UniformQuantizerData alphaUniformQuantizerData){
		_redUniformQuantizerData = redUniformQuantizerData;
		_greenUniformQuantizerData = greenUniformQuantizerData;
		_blueUniformQuantizerData = blueUniformQuantizerData;
	}

	/**
	 * Returns the quantized range for the red values.
	 * @return Quantized range for the red values
	 */
	public float[] getRedRange(){
		return new float[]{_redUniformQuantizerData.getMin(), _redUniformQuantizerData.getMax()};
	}

	/**
	 * Returns the quantized range for the green values.
	 * @return Quantized range for the green values
	 */
	public float[] getGreenRange(){
		return new float[]{_greenUniformQuantizerData.getMin(), _greenUniformQuantizerData.getMax()};
	}

	/**
	 * Returns the quantized range for the blue values.
	 * @return Quantized range for the blue values
	 */
	public float[] getBlueRange(){
		return new float[]{_blueUniformQuantizerData.getMin(), _blueUniformQuantizerData.getMax()};
	}

	/**
	 * Reads a ColorQuantizerData object.
	 * @param  workingContext Working context
	 * @return                ColorQuantizerData instance
	 */
	public static ColorQuantizerData read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		Integer numberOfHueBits = null;
		Integer numberOfSaturationBits = null;
		Integer numberOfValueBits = null;
		Integer numberOfAlphaBits = null;

		UniformQuantizerData redUniformQuantizerData = null;
		UniformQuantizerData greenUniformQuantizerData = null;
		UniformQuantizerData blueUniformQuantizerData = null;
		UniformQuantizerData alphaUniformQuantizerData = null;

		int hsvFlag = Helper.readU8(byteBuffer);
		if((hsvFlag != 0) && (hsvFlag != 1)){
			throw new IllegalArgumentException("Found invalid HSV flag: " + hsvFlag);
		}

		if(hsvFlag == 1){
			numberOfHueBits = Helper.readU8(byteBuffer);
			numberOfSaturationBits = Helper.readU8(byteBuffer);
			numberOfValueBits = Helper.readU8(byteBuffer);
			numberOfAlphaBits = Helper.readU8(byteBuffer);

		} else {
			redUniformQuantizerData = UniformQuantizerData.read(workingContext);
			greenUniformQuantizerData = UniformQuantizerData.read(workingContext);
			blueUniformQuantizerData = UniformQuantizerData.read(workingContext);
			alphaUniformQuantizerData = UniformQuantizerData.read(workingContext);
		}

		return new ColorQuantizerData(	numberOfHueBits,
										numberOfSaturationBits,
										numberOfValueBits,
										numberOfAlphaBits,
										redUniformQuantizerData,
										greenUniformQuantizerData,
										blueUniformQuantizerData,
										alphaUniformQuantizerData);
	}
}
