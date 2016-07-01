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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP2;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.model.quantize.ColorQuantizerData;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.7 Compressed Vertex Color Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Compressed Vertex Color Array data collection contains the quantization data/representation for a
 * set of vertex colors. Compressed Vertex Color Array data collection is only present if previously read Color
 * Binding value is not equal to zero (See Vertex Shape LOD Data for complete explanation of Color Binding data
 * field).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class CompressedVertexColorArray {
	/** Final color values */
	private List<Double> _colorValues;

	/**
	 * Constructor.
	 * @param colorCount                Color count
	 * @param numberComponents          Number of components
	 * @param quantizationBits          Quantization bits
	 * @param vertexColorExponentsLists Vertex color exponents lists
	 * @param vertexColorMantissaeLists Vertex color mantissae lists
	 * @param colorQuantizerData        Color quantizer data
	 * @param hueRedCodes               Hue/red codes
	 * @param satGreenCodes             Sat/green codes
	 * @param valueBlueCodes            Value/blue codes
	 * @param alphaCodes                Alpha codes
	 * @param vertexColorCodeLists      Vertex color code lists
	 * @param vertexColorHash           Vertex color hash
	 * @param colorValues               Final color values
	 */
	public CompressedVertexColorArray(int colorCount, int numberComponents, int quantizationBits, List<List<Integer>> vertexColorExponentsLists,
			List<List<Integer>> vertexColorMantissaeLists, ColorQuantizerData colorQuantizerData, List<Integer> hueRedCodes, List<Integer> satGreenCodes,
			List<Integer> valueBlueCodes, List<Integer> alphaCodes, List<List<Integer>> vertexColorCodeLists, long vertexColorHash,
			List<Double> colorValues){
		_colorValues = colorValues;
	}

	/**
	 * Returns the colors.
	 * @return Colors
	 */
	public List<Double> getColors(){
		return _colorValues;
	}

	/**
	 * Reads a CompressedVertexColorArray object.
	 * @param  workingContext            Working context
	 * @return                           CompressedVertexColorArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static CompressedVertexColorArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int colorCount = Helper.readI32(byteBuffer);
		int numberComponents = Helper.readU8(byteBuffer);
		int quantizationBits = Helper.readU8(byteBuffer);

		List<List<Integer>> vertexColorExponentsLists = new ArrayList<List<Integer>>();
		List<List<Integer>> vertexColorMantissaeLists = new ArrayList<List<Integer>>();
		List<List<Integer>> vertexColorCodeLists = new ArrayList<List<Integer>>();
		ColorQuantizerData colorQuantizerData = null;
		List<Integer> hueRedCodes = new ArrayList<Integer>();
		List<Integer> satGreenCodes = new ArrayList<Integer>();
		List<Integer> valueBlueCodes = new ArrayList<Integer>();
		List<Integer> alphaCodes = new ArrayList<Integer>();
		List<Double> colorValues = new ArrayList<Double>();

		if(quantizationBits == 0){
			for(int i = 0; i < numberComponents; i++){
				List<Integer> exponents = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);
				List<Integer> mantissae = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);

				List<Integer> codeData = new ArrayList<Integer>();
				for(int j = 0; j < exponents.size(); j++){
					codeData.add((exponents.get(j) << 23) | mantissae.get(j));
				}

				vertexColorExponentsLists.add(exponents);
				vertexColorMantissaeLists.add(mantissae);
				vertexColorCodeLists.add(codeData);
			}

			List<Integer> redCodeData = vertexColorCodeLists.get(0);
			List<Integer> greenCodeData = vertexColorCodeLists.get(1);
			List<Integer> blueCodeData = vertexColorCodeLists.get(2);
			for(int i = 0; i < redCodeData.size(); i++){
				colorValues.add(Helper.convertIntToFloat(redCodeData.get(i)));
				colorValues.add(Helper.convertIntToFloat(greenCodeData.get(i)));
				colorValues.add(Helper.convertIntToFloat(blueCodeData.get(i)));
			}

		} else if(quantizationBits > 0){
			colorQuantizerData = ColorQuantizerData.read(workingContext);

			hueRedCodes    = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);
			satGreenCodes  = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);
			valueBlueCodes = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);
			alphaCodes     = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);

			List<Double> redValues = Helper.dequantize(hueRedCodes, colorQuantizerData.getRedRange(), quantizationBits);
			List<Double> greenValues = Helper.dequantize(satGreenCodes, colorQuantizerData.getGreenRange(), quantizationBits);
			List<Double> blueValues = Helper.dequantize(valueBlueCodes, colorQuantizerData.getBlueRange(), quantizationBits);
			for(int i = 0; i < redValues.size(); i++){
				colorValues.add(redValues.get(i));
				colorValues.add(greenValues.get(i));
				colorValues.add(blueValues.get(i));
			}

		} else {
			throw new IllegalArgumentException("ERROR: Negative number of quantized bits: " + quantizationBits);
		}

		long readHash = Helper.readU32(byteBuffer);

		return new CompressedVertexColorArray(colorCount, numberComponents, quantizationBits, vertexColorExponentsLists,
				vertexColorMantissaeLists, colorQuantizerData, hueRedCodes, satGreenCodes, valueBlueCodes, alphaCodes,
				vertexColorCodeLists, readHash, colorValues);
	}
}
