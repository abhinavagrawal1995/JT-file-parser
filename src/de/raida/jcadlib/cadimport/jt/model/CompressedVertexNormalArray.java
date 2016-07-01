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

import javax.vecmath.Point3d;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP2;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.codec.deering.DeeringNormalCodec;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.5 Compressed Vertex Normal Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Compressed Vertex Normal Array data collection contains the compressed data/representation for a set of
 * vertex normals. Compressed Vertex Normal Array data collection is only present if previously read vertex bindings
 * denote normals are presents (See Vertex Shape LOD Data U64 : Vertex Bindings for complete explanation of the
 * vertex bindings).
 * <br>A variation of the CODEC developed by Michael Deering at Sun Microsystems is used to encode the normals when
 * quantization is enabled. The variation being that the 'Sextants' are arranged differently than in Deering's
 * scheme [6], for better delta encoding. See 8.2.4 Deering Normal CODEC for a complete explanation on the Deering
 * CODEC used.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class CompressedVertexNormalArray {
	/** Final normal coordinates */
	private List<Double> _normalCoordinates;

	/**
	 * Constructor.
	 * @param normalCount                Normal count
	 * @param numberComponents           Number of components
	 * @param quantizationBits           Quantization bits
	 * @param vertexNormalExponentsLists Vertex normal exponents lists
	 * @param vertexNormalMantissaeLists Vertex normal mantissae lists
	 * @param sextantCodes               Sextant codes
	 * @param octantCodes                Octant codes
	 * @param thetaCodes                 Theta codes
	 * @param psiCodes                   Psi codes
	 * @param vertexNormalHash           Vertex normal hash
	 * @param normalVectorLists          Normal vector lists
	 * @param normalCoordinates          Final normal coordinates
	 */
	public CompressedVertexNormalArray(int normalCount, int numberComponents, int quantizationBits, List<List<Integer>> vertexNormalExponentsLists,
			List<List<Integer>> vertexNormalMantissaeLists, List<Integer> sextantCodes, List<Integer> octantCodes, List<Integer> thetaCodes,
			List<Integer> psiCodes, long vertexNormalHash, List<List<Integer>> normalVectorLists, List<Double> normalCoordinates){
		_normalCoordinates = normalCoordinates;
	}

	/**
	 * Returns the normal data.
	 * @return Normal data
	 */
	public List<Double> getNormals(){
		return _normalCoordinates;
	}

	/**
	 * Reads a CompressedVertexNormalArray object.
	 * @param  workingContext            Working context
	 * @return                           CompressedVertexNormalArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static CompressedVertexNormalArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int normalCount = Helper.readI32(byteBuffer);
		int numberComponents = Helper.readU8(byteBuffer);
		int quantizationBits = Helper.readU8(byteBuffer);

		List<List<Integer>> vertexNormalExponentsLists = new ArrayList<List<Integer>>();
		List<List<Integer>> vertexNormalMantissaeLists = new ArrayList<List<Integer>>();
		List<Integer> sextantCodes = new ArrayList<Integer>();
		List<Integer> octantCodes = new ArrayList<Integer>();
		List<Integer> thetaCodes = new ArrayList<Integer>();
		List<Integer> psiCodes = new ArrayList<Integer>();
		List<Double> normalCoordinates = new ArrayList<Double>();

		List<List<Integer>> normalVectorLists = new ArrayList<List<Integer>>();
		if(quantizationBits == 0){
			for(int i = 0; i < numberComponents; i++){
				List<Integer> exponents = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
				List<Integer> mantissae = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);

				List<Integer> normalVectorData = new ArrayList<Integer>();
				for(int j = 0; j < exponents.size(); j++){
					normalVectorData.add((exponents.get(j) << 23) | mantissae.get(j));
				}

				normalVectorLists.add(normalVectorData);
				vertexNormalExponentsLists.add(exponents);
				vertexNormalMantissaeLists.add(mantissae);
			}

			List<Integer> xCodeData = normalVectorLists.get(0);
			List<Integer> yCodeData = normalVectorLists.get(1);
			List<Integer> zCodeData = normalVectorLists.get(2);
			for(int i = 0; i < xCodeData.size(); i++){
				normalCoordinates.add(Helper.convertIntToFloat(xCodeData.get(i)));
				normalCoordinates.add(Helper.convertIntToFloat(yCodeData.get(i)));
				normalCoordinates.add(Helper.convertIntToFloat(zCodeData.get(i)));
			}

		} else if(quantizationBits > 0){
			sextantCodes = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
			octantCodes = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
			thetaCodes = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
			psiCodes = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);

			DeeringNormalCodec deeringCodec = new DeeringNormalCodec(quantizationBits);
			for(int i = 0; i < psiCodes.size(); i++){
				Point3d normal = deeringCodec.convertCodeToVec(sextantCodes.get(i), octantCodes.get(i), thetaCodes.get(i), psiCodes.get(i));
				normalCoordinates.add(normal.x);
				normalCoordinates.add(normal.y);
				normalCoordinates.add(normal.z);
			}

		} else {
			throw new IllegalArgumentException("ERROR: Negative number of quantized bits: " + quantizationBits);
		}

		long readHash = Helper.readU32(byteBuffer);

		return new CompressedVertexNormalArray(normalCount, numberComponents, quantizationBits,
				vertexNormalExponentsLists, vertexNormalMantissaeLists, sextantCodes, octantCodes, thetaCodes,
				psiCodes, readHash, normalVectorLists, normalCoordinates);
	}
}
