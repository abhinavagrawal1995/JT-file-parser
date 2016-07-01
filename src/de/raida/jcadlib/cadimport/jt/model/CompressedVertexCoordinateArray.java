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
import de.raida.jcadlib.cadimport.jt.model.quantize.PointQuantizerData;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.4 Compressed Vertex Coordinate Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Compressed Vertex Coordinate Array data collection contains the quantization data/representation for a set of
 * vertex coordinates.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class CompressedVertexCoordinateArray {
	/** Final vertex coordinates */
	private List<Double> _vertexCoordinates;

	/**
	 * Constructor.
	 * @param uniqueVertexCount         Unique vertex count
	 * @param numberComponents          Number of components
	 * @param pointQuantizerData        Point quantizer data
	 * @param vertexCoordExponentLists  Vertex coordinate exponent lists
	 * @param vertexCoordMantissaeLists Vertex coordinate mantissae lists
	 * @param vertexCoordCodeLists      Vertex coordinate code lists
	 * @param vertexCoordinateHash      Vertex coordinate hash
	 * @param vertexCoordinates         Final vertex coordinates
	 */
	public CompressedVertexCoordinateArray(int uniqueVertexCount, int numberComponents, PointQuantizerData pointQuantizerData,
			List<List<Integer>> vertexCoordExponentLists, List<List<Integer>> vertexCoordMantissaeLists,
			List<List<Integer>> vertexCoordCodeLists, long vertexCoordinateHash, List<Double> vertexCoordinates){
		_vertexCoordinates = vertexCoordinates;
	}

	/**
	 * Returns the geometry vertices.
	 * @return Geometry vertices
	 */
	public List<Double> getVertices(){
		return _vertexCoordinates;
	}

	/**
	 * Reads a CompressedVertexCoordinateArray object.
	 * @param  workingContext            Working context
	 * @return                           CompressedVertexCoordinateArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static CompressedVertexCoordinateArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int uniqueVertexCount = Helper.readI32(byteBuffer);
		int numberComponents = Helper.readU8(byteBuffer);
		PointQuantizerData pointQuantizerData = PointQuantizerData.read(workingContext);

		List<List<Integer>> vertexCoordExponentLists = new ArrayList<List<Integer>>();
		List<List<Integer>> vertexCoordMantissaeLists = new ArrayList<List<Integer>>();
		List<List<Integer>> vertexCoordCodeLists = new ArrayList<List<Integer>>();
		List<Double> vertexCoordinates = new ArrayList<Double>();
		int numberOfBits = pointQuantizerData.getNumberOfBits();
		if(numberOfBits == 0){
			for(int i = 0; i < numberComponents; i++){
				List<Integer> exponents = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);
				List<Integer> mantissae = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);
				List<Integer> codeData = new ArrayList<Integer>();
				for(int j = 0; j < exponents.size(); j++){
					codeData.add((exponents.get(j) << 23) | mantissae.get(j));
				}

				vertexCoordExponentLists.add(exponents);
				vertexCoordMantissaeLists.add(mantissae);
				vertexCoordCodeLists.add(codeData);
			}

			List<Integer> xCodeData = vertexCoordCodeLists.get(0);
			List<Integer> yCodeData = vertexCoordCodeLists.get(1);
			List<Integer> zCodeData = vertexCoordCodeLists.get(2);
			for(int i = 0; i < xCodeData.size(); i++){
				vertexCoordinates.add(Helper.convertIntToFloat(xCodeData.get(i)));
				vertexCoordinates.add(Helper.convertIntToFloat(yCodeData.get(i)));
				vertexCoordinates.add(Helper.convertIntToFloat(zCodeData.get(i)));
			}

		} else if(numberOfBits > 0){
			vertexCoordCodeLists.add(Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1));
			vertexCoordCodeLists.add(Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1));
			vertexCoordCodeLists.add(Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1));

			List<Double> xValues = Helper.dequantize(vertexCoordCodeLists.get(0), pointQuantizerData.getXRange(), numberOfBits);
			List<Double> yValues = Helper.dequantize(vertexCoordCodeLists.get(1), pointQuantizerData.getYRange(), numberOfBits);
			List<Double> zValues = Helper.dequantize(vertexCoordCodeLists.get(2), pointQuantizerData.getZRange(), numberOfBits);
			for(int i = 0; i < xValues.size(); i++){
				vertexCoordinates.add(xValues.get(i));
				vertexCoordinates.add(yValues.get(i));
				vertexCoordinates.add(zValues.get(i));
			}

		} else {
			throw new IllegalArgumentException("ERROR: Negative number of quantized bits: " + numberOfBits);
		}

		long readHash = Helper.readU32(byteBuffer);

		return new CompressedVertexCoordinateArray(	uniqueVertexCount,
													numberComponents,
													pointQuantizerData,
													vertexCoordExponentLists,
													vertexCoordMantissaeLists,
													vertexCoordCodeLists,
													readHash,
													vertexCoordinates);
	}
}
