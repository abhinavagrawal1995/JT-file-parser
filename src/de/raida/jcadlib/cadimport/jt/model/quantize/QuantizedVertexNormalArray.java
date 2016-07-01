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
import java.util.List;

import javax.vecmath.Point3d;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.codec.deering.DeeringNormalCodec;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.3.2.2 Quantized Vertex Normal Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Quantized Vertex Normal Array data collection contains the quantization
 * data/representation for a set of vertex normals. Quantized Vertex Normal Array 
 * data collection is only present if previously read Normal Binding value is not
 * equal to zero (See 8.1.3Vertex Based Shape Compressed Rep Data for complete
 * explanation of Normal Binding data field).
 * <br>A variation of the CODEC developed by Michael Deering at Sun Microsystems is
 * used to encode the normals. The variation being that the 'Sextants' are arranged
 * differently than in Deering's scheme [6], for better delta encoding. See 8.2.5
 * Deering Normal CODEC for a complete explanation on the Deering CODEC used.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class QuantizedVertexNormalArray {
	/** Quantized size */
	private int _numberOfBits;

	/** Sectant codes */
	private List<Integer> _sextantCodes;

	/** Octant codes */
	private List<Integer> _octantCodes;

	/** Theta codes */
	private List<Integer> _thetaCodes;

	/** Psi codes */
	private List<Integer> _psiCodes;

	/** List of normals */
	private List<Double> _normals;

	/**
	 * Constructor.
	 * @param numberOfBits Quantized size
	 * @param normalCount  Normal count
	 * @param sextantCodes Sextant code
	 * @param octantCodes  Octant codes
	 * @param thetaCodes   Theta codes
	 * @param psiCodes     Psi codes
	 */
	public QuantizedVertexNormalArray(int numberOfBits, int normalCount, List<Integer> sextantCodes, List<Integer> octantCodes, List<Integer> thetaCodes, List<Integer> psiCodes){
		_numberOfBits = numberOfBits;
		_sextantCodes = sextantCodes;
		_octantCodes = octantCodes;
		_thetaCodes = thetaCodes;
		_psiCodes = psiCodes;

		_normals = new ArrayList<Double>();
		DeeringNormalCodec deeringCodec = new DeeringNormalCodec(_numberOfBits);

		for(int i = 0; i < _psiCodes.size(); i++){
			Point3d normal = deeringCodec.convertCodeToVec(_sextantCodes.get(i), _octantCodes.get(i), _thetaCodes.get(i), _psiCodes.get(i));
			_normals.add(normal.x);
			_normals.add(normal.y);
			_normals.add(normal.z);
		}
	}

	/**
	 * Returns the list of normals.
	 * @return List of normals
	 */
	public List<Double> getNormals(){
		return _normals;
	}

	/**
	 * Reads a QuantizedVertexNormalArray object.
	 * @param  workingContext            Working context
	 * @return                           QuantizedVertexNormalArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static QuantizedVertexNormalArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int numberOfBits = Helper.readU8(byteBuffer);
		int normalCount = Helper.readI32(byteBuffer);
		List<Integer> sextantCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
		List<Integer> octantCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
		List<Integer> thetaCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
		List<Integer> psiCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);

		return new QuantizedVertexNormalArray(	numberOfBits,
												normalCount,
												sextantCodes,
												octantCodes,
												thetaCodes,
												psiCodes);
	}
}
