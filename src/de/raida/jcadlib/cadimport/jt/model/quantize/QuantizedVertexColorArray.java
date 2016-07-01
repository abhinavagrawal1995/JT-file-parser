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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.3.2.4 Quantized Vertex Color Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Quantized Vertex Color Array data collection contains the
 * quantization data/representation for a set of vertex colors. Quantized
 * Vertex Color Array data collection is only present if previously read
 * Color Binding value is not equal to zero (See 8.1.3Vertex Based Shape
 * Compressed Rep Data for complete explanation of Color Binding data field).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class QuantizedVertexColorArray {
	/** Color quantizer data */
	private ColorQuantizerData _colorQuantizerData;

	/** Hue / red codes */
	private List<Integer> _hueRedCodes;

	/** Vertex x coordinates */
	private List<Integer> _satGreenCodes;

	/** Vertex y coordinates */
	private List<Integer> _valueBlueCodes;

	/**
	 * Constructor.
	 * @param colorQuantizerData  Color quantizer data
	 * @param numberOfBits        Number of bits
	 * @param numberOfColorFloats Number of color floats
	 * @param hueRedCodes         Hue / red codes
	 * @param satGreenCodes       Saturation / green codes
	 * @param valueBlueCodes      Value / blue codes
	 * @param alphaCodes          Alpha codes
	 * @param colorCodes          Color codes
	 */
	public QuantizedVertexColorArray(ColorQuantizerData colorQuantizerData, int numberOfBits, int numberOfColorFloats, List<Integer> hueRedCodes, List<Integer> satGreenCodes, List<Integer> valueBlueCodes, List<Integer> alphaCodes, List<Integer> colorCodes){
		_colorQuantizerData = colorQuantizerData;
		_hueRedCodes = hueRedCodes;
		_satGreenCodes = satGreenCodes;
		_valueBlueCodes = valueBlueCodes;
	}

	/**
	 * Returns the colors.
	 * @return Colors
	 */
	public List<Float> getColors(){
		float[] redRange = _colorQuantizerData.getRedRange();
		float deltaRedRange = redRange[1] - redRange[0];
		float minRed = Collections.min(_hueRedCodes);
		float maxRed = Collections.max(_hueRedCodes);
		float deltaRedValue = maxRed - minRed;
		float mRed = deltaRedRange / deltaRedValue;
		float bRed = redRange[0] - (mRed * minRed);

		float[] greenRange = _colorQuantizerData.getGreenRange();
		float deltaGreenRange = greenRange[1] - greenRange[0];
		float minGreen = Collections.min(_satGreenCodes);
		float maxGreen = Collections.max(_satGreenCodes);
		float deltaGreenValue = maxGreen - minGreen;
		float mGreen = deltaGreenRange / deltaGreenValue;
		float bGreen = greenRange[0] - (mGreen * minGreen);

		float[] blueRange = _colorQuantizerData.getBlueRange();
		float deltaBlueRange = blueRange[1] - blueRange[0];
		float minBlue = Collections.min(_valueBlueCodes);
		float maxBlue = Collections.max(_valueBlueCodes);
		float deltaBlueValue = maxBlue - minBlue;
		float mBlue = deltaBlueRange / deltaBlueValue;
		float bBlue = blueRange[0] - (mBlue * minBlue);

		List<Float> redValues = new ArrayList<Float>();
		for(int i = 0; i < _hueRedCodes.size(); i++){
			redValues.add((mRed * _hueRedCodes.get(i)) + bRed);
		}

		List<Float> greenValues = new ArrayList<Float>();
		for(int i = 0; i < _satGreenCodes.size(); i++){
			greenValues.add((mGreen * _satGreenCodes.get(i)) + bGreen);
		}

		List<Float> blueValues = new ArrayList<Float>();
		for(int i = 0; i < _valueBlueCodes.size(); i++){
			blueValues.add((mBlue * _valueBlueCodes.get(i)) + bBlue);
		}

		List<Float> colors = new ArrayList<Float>();
		for(int i = 0; i < _hueRedCodes.size(); i++){
			colors.add(redValues.get(i));
			colors.add(greenValues.get(i));
			colors.add(blueValues.get(i));
		}

		return colors;
	}

	/**
	 * Reads a QuantizedVertexColorArray object.
	 * @param  workingContext            Working context
	 * @return                           QuantizedVertexColorArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static QuantizedVertexColorArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		ColorQuantizerData colorQuantizerData = ColorQuantizerData.read(workingContext);
		int numberOfBits = Helper.readU8(byteBuffer);
		int numberOfColorFloats = Helper.readU8(byteBuffer);
		int componentArraysFlag = Helper.readU8(byteBuffer);
		if((componentArraysFlag != 0) && (componentArraysFlag != 1)){
			throw new IllegalArgumentException("Found invalid component arrays flag: " + componentArraysFlag);
		}

		List<Integer> hueRedCodes = null;
		List<Integer> satGreenCodes = null;
		List<Integer> valueBlueCodes = null;
		List<Integer> alphaCodes = null;
		List<Integer> colorCodes = null;

		if(componentArraysFlag == 0){
			colorCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredNULL);

		} else {
			hueRedCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
			satGreenCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
			valueBlueCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
			alphaCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
		}

		return new QuantizedVertexColorArray(	colorQuantizerData,
												numberOfBits,
												numberOfColorFloats,
												hueRedCodes,
												satGreenCodes,
												valueBlueCodes,
												alphaCodes,
												colorCodes);
	}
}
