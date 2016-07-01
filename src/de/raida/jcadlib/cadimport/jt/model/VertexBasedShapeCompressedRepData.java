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
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizationParameters;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.3 Vertex Based Shape Compressed Rep Data</h>
 * The Vertex Based Shape Compressed Rep Data collection is the compressed and/or
 * encoded representation of the vertex coordinates, normal, texture coordinate,
 * and color data for a vertex based shape. All vertex based shape elements (e.g.
 * Tri-Strip Set Shape LOD Element, Polyline Set Shape LOD Element) use this data
 * collection format to compress/encode their geometric data.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class VertexBasedShapeCompressedRepData {
	/** Binding: None */
	public final static byte BINDING_NONE          = 0;

	/** Binding: Per vertex */
	public final static byte BINDING_PER_VERTEX    = 1;

	/** Binding: Per facet */
	public final static byte BINDING_PER_FACET     = 2;

	/** Binding: Per primitive */
	public final static byte BINDING_PER_PRIMITIVE = 3;

	/** Indices */
	private List<Integer> _indices;

	/** Lossless compressed raw vertex data */
	private LosslessCompressedRawVertexData _losslessCompressedRawVertexData;

	/** Lossy compressed raw vertex data */
	private LossyQuantizedRawVertexData _lossyQuantizedRawVertexData;

	/**
	 * Constructor.
	 * @param versionNumber                   Version of 'Vertex Based Shape Rep Data'
	 * @param normalBinding                   Granularity of normal vector data
	 * @param textureCoordBinding             Granularity of texture coordinate data
	 * @param colorBinding                    Granularity of color data
	 * @param quantizationParameters          Quantization paramters
	 * @param indices                         List of indices
	 * @param losslessCompressedRawVertexData Lossless compressed raw vertex data
	 * @param lossyQuantizedRawVertexData     Lossy compressed raw vertex data
	 */
	public VertexBasedShapeCompressedRepData(int versionNumber, int normalBinding, int textureCoordBinding, int colorBinding, QuantizationParameters quantizationParameters, List<Integer> indices, LosslessCompressedRawVertexData losslessCompressedRawVertexData, LossyQuantizedRawVertexData lossyQuantizedRawVertexData){
		_indices = indices;
		_losslessCompressedRawVertexData = losslessCompressedRawVertexData;
		_lossyQuantizedRawVertexData = lossyQuantizedRawVertexData;
	}

	/**
	 * Returns the indices.
	 * @return Indices
	 */
	public List<Integer> getIndices(){
		return _indices;
	}

	/**
	 * Returns the geometry vertices (without texture coordinates, normals, ...).
	 * @return Geometry vertices
	 */
	public List<Double> getVertices(){
		if(_losslessCompressedRawVertexData != null){
			return _losslessCompressedRawVertexData.getVertices();
		} else {
			return _lossyQuantizedRawVertexData.getVertices();
		}
	}

	/**
	 * Returns the normals.
	 * @return Normals
	 */
	public List<Double> getNormals(){
		if(_losslessCompressedRawVertexData != null){
			return _losslessCompressedRawVertexData.getNormals();
		} else {
			return _lossyQuantizedRawVertexData.getNormals();
		}
	}

	/**
	 * Returns the colors.
	 * @return Colors
	 */
	public List<Float> getColors(){
		if(_losslessCompressedRawVertexData != null){
			return _losslessCompressedRawVertexData.getColors();
		} else {
			return _lossyQuantizedRawVertexData.getColors();
		}
	}

	/**
	 * Reads a VertexBasedShapeCompressedRepData object.
	 * @param  workingContext            Working context
	 * @return                           VertexBasedShapeCompressedRepData instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static VertexBasedShapeCompressedRepData read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int versionNumber = Helper.readI16(byteBuffer);
		int normalBinding = Helper.readU8(byteBuffer);
		if((normalBinding < 0) || (normalBinding > 3)){
			throw new IllegalArgumentException("Found illegal normal binding: " + normalBinding);
		}

		int textureCoordBinding = Helper.readU8(byteBuffer);
		if((textureCoordBinding < 0) || (textureCoordBinding > 3)){
			throw new IllegalArgumentException("Found illegal texture coordinate binding: " + normalBinding);
		}

		int colorBinding = Helper.readU8(byteBuffer);
		if((colorBinding < 0) || (colorBinding > 3)){
			throw new IllegalArgumentException("Found illegal color binding: " + normalBinding);
		}

		QuantizationParameters quantizationParameters = QuantizationParameters.read(workingContext);

		// Read the index list
		//---------------------
		List<Integer> indices = Int32CDP.readVecI32(workingContext, PredictorType.PredStride1);

		// Read the vertex list
		//----------------------
		LosslessCompressedRawVertexData losslessCompressedRawVertexData = null;
		LossyQuantizedRawVertexData lossyQuantizedRawVertexData = null;
		if(quantizationParameters.getBitsPerVertex() == 0){
			losslessCompressedRawVertexData = LosslessCompressedRawVertexData.read(workingContext, textureCoordBinding, colorBinding, normalBinding);
		} else {
			lossyQuantizedRawVertexData = LossyQuantizedRawVertexData.read(workingContext, textureCoordBinding, colorBinding, normalBinding);
		}

		return new VertexBasedShapeCompressedRepData(	versionNumber,
														normalBinding,
														textureCoordBinding,
														colorBinding,
														quantizationParameters,
														indices,
														losslessCompressedRawVertexData,
														lossyQuantizedRawVertexData);
	}
}
