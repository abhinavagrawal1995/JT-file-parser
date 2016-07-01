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
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.6 Compressed Vertex Texture Coordinate Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Compressed Vertex Texture Coordinate Array data collection contains the quantization data/representation
 * for a set of vertex texture coordinates. Compressed Vertex Texture Coordinate Array data collection is only
 * present if previously read vertex bindings denote texture coordinates are presents (See Vertex Shape LOD
 * Data U64 : Vertex Bindings for complete explanation of the vertex bindings).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class CompressedVertexTextureCoordinateArray {
	/** Texture coordinates */
	private List<Double> _textureCoordinates;

	/**
	 * Constructor.
	 * @param textureCoordCount                Number of texture coordinates
	 * @param numberComponents                 Number of components
	 * @param quantizationBits                 Quantization bits
	 * @param vertexTextureCoordExponentLists  Vertex texture coordinate exponent lists
	 * @param vertexTextureCoordMantissaeLists Vertex texture coordinate mantissae lists
	 * @param textureCoordCodesLists           Texture coordinate codes lists
	 * @param textureQuantizerData             Texture quantizer data
	 * @param textureCoordinates               Final texture coordinates
	 */
	public CompressedVertexTextureCoordinateArray(int textureCoordCount, int numberComponents, int quantizationBits,
			List<List<Integer>> vertexTextureCoordExponentLists, List<List<Integer>> vertexTextureCoordMantissaeLists,
			List<List<Integer>> textureCoordCodesLists, TextureQuantizerData textureQuantizerData, List<Double> textureCoordinates){
		_textureCoordinates = textureCoordinates;
	}

	/**
	 * Returns the texture coordinates.
	 * @return Texture coordinates
	 */
	public List<Double> getTextureCoordinates(){
		return _textureCoordinates;
	}

	/**
	 * Reads a CompressedVertexTextureCoordinateArray object.
	 * @param  workingContext            Working context
	 * @return                           CompressedVertexTextureCoordinateArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static CompressedVertexTextureCoordinateArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int textureCoordCount = Helper.readI32(byteBuffer);
		int numberComponents = Helper.readU8(byteBuffer);
		int quantizationBits = Helper.readU8(byteBuffer);

		List<List<Integer>> vertexTextureCoordExponentLists = new ArrayList<List<Integer>>();
		List<List<Integer>> vertexTextureCoordMantissaeLists = new ArrayList<List<Integer>>();
		List<List<Integer>> vertexTextureCodeLists = new ArrayList<List<Integer>>();
		List<List<Integer>> textureCoordCodesLists = new ArrayList<List<Integer>>();
		TextureQuantizerData textureQuantizerData = null;
		List<Double> textureCoordinates = new ArrayList<Double>();

		if(quantizationBits == 0){
			for(int i = 0; i < numberComponents; i++){
				List<Integer> exponents = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
				List<Integer> mantissae = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);

				List<Integer> codeData = new ArrayList<Integer>();
				for(int j = 0; j < exponents.size(); j++){
					codeData.add((exponents.get(j) << 23) | mantissae.get(j));
				}

				vertexTextureCoordExponentLists.add(exponents);
				vertexTextureCoordMantissaeLists.add(mantissae);
				vertexTextureCodeLists.add(codeData);
			}

			List<Integer> uCodeData = vertexTextureCodeLists.get(0);
			List<Integer> vCodeData = vertexTextureCodeLists.get(1);
			for(int i = 0; i < uCodeData.size(); i++){
				textureCoordinates.add(Helper.convertIntToFloat(uCodeData.get(i)));
				textureCoordinates.add(Helper.convertIntToFloat(vCodeData.get(i)));
			}

		} else if(quantizationBits > 0){
			textureQuantizerData = TextureQuantizerData.read(workingContext, numberComponents);

			for(int i = 0; i < numberComponents; i++){
				textureCoordCodesLists.add(Int32CDP2.readVecU32(workingContext, PredictorType.PredLag1));
			}

			List<Double> uValues = Helper.dequantize(textureCoordCodesLists.get(0), textureQuantizerData.getURange(), quantizationBits);
			List<Double> vValues = Helper.dequantize(textureCoordCodesLists.get(1), textureQuantizerData.getVRange(), quantizationBits);
			for(int i = 0; i < uValues.size(); i++){
				textureCoordinates.add(uValues.get(i));
				textureCoordinates.add(vValues.get(i));
			}

			// Ignore the hash value
			Helper.readU32(byteBuffer);

		} else {
			throw new IllegalArgumentException("ERROR: Negative number of quantized bits: " + quantizationBits);
		}

		return new CompressedVertexTextureCoordinateArray(textureCoordCount, numberComponents, quantizationBits, vertexTextureCoordExponentLists,
				vertexTextureCoordMantissaeLists, textureCoordCodesLists, textureQuantizerData, textureCoordinates);
	}
}
