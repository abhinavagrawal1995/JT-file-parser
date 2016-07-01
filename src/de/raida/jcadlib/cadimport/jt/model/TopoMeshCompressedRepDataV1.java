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

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP2;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizationParameters;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.2.7 TopoMesh Compressed Rep Data V1</h>
 * Object Type ID: <code>---</code>
 * <br>TopoMesh Compressed Rep Data V1 contains the geometric shape definition data (e.g. vertices, colors,
 * normals, etc.) in a lossy or lossless compressed formed.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TopoMeshCompressedRepDataV1 {
	/** Primitive list indices */
	private List<Integer> _primitiveListIndices;

	/** Vertex list indices */
	private List<Integer> _vertexListIndices;

	/** Compressed vertex coordinate array */
	private CompressedVertexCoordinateArray _compressedVertexCoordinateArray;

	/** Compressed vertex normal array */
	private CompressedVertexNormalArray _compressedVertexNormalArray;

	/** Compressed vertex color array */
	private CompressedVertexColorArray _compressedVertexColorArray;

	/**
	 * Constructor.
	 * @param numberOfFaceGroupListIndices Number of face group list indices
	 * @param numberOfPrimitiveListIndices Number of primitive list indices
	 * @param numberOfVertexListIndices    Number of vertex list indices
	 * @param faceGroupListIndices         Face group list indices
	 * @param primitiveListIndices         Primitive list indices
	 * @param vertexListIndices            Vertex list indices
	 * @param fgpvListIndicesHash          FGPV list indices hash
	 * @param vertexBindings               Vertex bindings
	 * @param quantizationParameters       Quantization parameters
	 * @param numberOfVertexRecords        Number of vertex records
	 */
	public TopoMeshCompressedRepDataV1(	int numberOfFaceGroupListIndices, int numberOfPrimitiveListIndices, int numberOfVertexListIndices,
										List<Integer> faceGroupListIndices, List<Integer> primitiveListIndices, List<Integer> vertexListIndices,
										int fgpvListIndicesHash, long vertexBindings, QuantizationParameters quantizationParameters,
										int numberOfVertexRecords){
		this(	numberOfFaceGroupListIndices, numberOfPrimitiveListIndices, numberOfVertexListIndices, faceGroupListIndices, primitiveListIndices,
				vertexListIndices, fgpvListIndicesHash, vertexBindings, quantizationParameters, numberOfVertexRecords, -1, null, -1, null, null,
				null, null, null);
	}

	/**
	 * Constructor.
	 * @param numberOfFaceGroupListIndices            Number of face group list indices
	 * @param numberOfPrimitiveListIndices            Number of primitive list indices
	 * @param numberOfVertexListIndices               Number of vertex list indices
	 * @param faceGroupListIndices                    Face group list indices
	 * @param primitiveListIndices                    Primitive list indices
	 * @param vertexListIndices                       Vertex list indices
	 * @param fgpvListIndicesHash                     FGPV list indices hash
	 * @param vertexBindings                          Vertex bindings
	 * @param quantizationParameters                  Quantization parameters
	 * @param numberOfVertexRecords                   Number of vertex records
	 * @param numberOfUniqueVertexCoordinates         Number of unique vertex coordinates
	 * @param uniqueVertexCoordinateLengthList        Unique vertex coordinate length list
	 * @param uniqueVertexListMapHash                 Unique vertex list map hash
	 * @param compressedVertexCoordinateArray         Compressed vertex coordinate array
	 * @param compressedVertexNormalArray             Compressed vertex normal array
	 * @param compressedVertexColorArray              Compressed vertex color array
	 * @param compressedVertexTextureCoordinateArrays Compressed vertex texture coordinate arrays
	 * @param compressedVertexFlagArray               Compressed vertex flag array
	 */
	public TopoMeshCompressedRepDataV1(	int numberOfFaceGroupListIndices, int numberOfPrimitiveListIndices, int numberOfVertexListIndices,
										List<Integer> faceGroupListIndices, List<Integer> primitiveListIndices, List<Integer> vertexListIndices,
										int fgpvListIndicesHash, long vertexBindings, QuantizationParameters quantizationParameters,
										int numberOfVertexRecords, int numberOfUniqueVertexCoordinates, List<Integer> uniqueVertexCoordinateLengthList,
										int uniqueVertexListMapHash, CompressedVertexCoordinateArray compressedVertexCoordinateArray,
										CompressedVertexNormalArray compressedVertexNormalArray, CompressedVertexColorArray compressedVertexColorArray,
										CompressedVertexTextureCoordinateArray[] compressedVertexTextureCoordinateArrays,
										CompressedVertexFlagArray compressedVertexFlagArray){
		_primitiveListIndices = primitiveListIndices;
		_vertexListIndices = vertexListIndices;
		_compressedVertexCoordinateArray = compressedVertexCoordinateArray;
		_compressedVertexNormalArray = compressedVertexNormalArray;
		_compressedVertexColorArray = compressedVertexColorArray;
	}

	/**
	 * Returns the CompressedVertexCoordinateArray
	 * @return CompressedVertexCoordinateArray
	 */
	public CompressedVertexCoordinateArray getCompressedVertexCoordinateArray(){
		return _compressedVertexCoordinateArray;
	}

	/**
	 * Returns the CompressedVertexNormalArray
	 * @return CompressedVertexNormalArray
	 */
	public CompressedVertexNormalArray getCompressedVertexNormalArray(){
		return _compressedVertexNormalArray;
	}

	/**
	 * Returns the CompressedVertexColorArray
	 * @return CompressedVertexColorArray
	 */
	public CompressedVertexColorArray getCompressedVertexColorArray(){
		return _compressedVertexColorArray;
	}

	/**
	 * Returns the primitive list indices.
	 * @return Primitive list indices
	 */
	public List<Integer> getPrimitiveListIndices(){
		return _primitiveListIndices;
	}

	/**
	 * Returns the vertex list indices.
	 * @return Vertex list indices
	 */
	public List<Integer> getVertexListIndices(){
		return _vertexListIndices;
	}

	/**
	 * Reads a TopoMeshCompressedRepDataV1 object.
	 * @param  workingContext            Working context
	 * @param  fromPolyLineShape         Called from a PolyLineSHape instance?
	 * @return                           TopoMeshCompressedRepDataV1 instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static TopoMeshCompressedRepDataV1 read(WorkingContext workingContext, boolean fromPolyLineShape) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int numberOfFaceGroupListIndices = -1;
		if(fromPolyLineShape){
			numberOfFaceGroupListIndices = Helper.readI32(byteBuffer);
		}

		int numberOfPrimitiveListIndices = Helper.readI32(byteBuffer);
		int numberOfVertexListIndices = Helper.readI32(byteBuffer);

		List<Integer> faceGroupListIndices = null;
		if(fromPolyLineShape){
			faceGroupListIndices = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
		}

		List<Integer> primitiveListIndices = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
		List<Integer> vertexListIndices = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);

		int fgpvListIndicesHash = Helper.readI32(byteBuffer);
		long vertexBindings = Helper.readU64(byteBuffer);

		QuantizationParameters quantizationParameters = QuantizationParameters.read(workingContext);

		int numberOfVertexRecords = Helper.readI32(byteBuffer);
		if(numberOfVertexRecords == 0){
			return new TopoMeshCompressedRepDataV1(	numberOfFaceGroupListIndices,
													numberOfPrimitiveListIndices,
													numberOfVertexListIndices,
													faceGroupListIndices,
													primitiveListIndices,
													vertexListIndices,
													fgpvListIndicesHash,
													vertexBindings,
													quantizationParameters,
													numberOfVertexRecords);
		}

		int numberOfUniqueVertexCoordinates = Helper.readI32(byteBuffer);

		List<Integer> uniqueVertexCoordinateLengthList = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);

		int uniqueVertexListMapHash = Helper.readI32(byteBuffer);

		CompressedVertexCoordinateArray compressedVertexCoordinateArray = null;
		if((vertexBindings & 0x07) != 0){					// Check for bits 1-3
			compressedVertexCoordinateArray = CompressedVertexCoordinateArray.read(workingContext);
		}

		CompressedVertexNormalArray compressedVertexNormalArray = null;
		if((vertexBindings & 0x08) != 0){					// Check for bit 4
			compressedVertexNormalArray = CompressedVertexNormalArray.read(workingContext);
		}

		CompressedVertexColorArray compressedVertexColorArray = null;
		if((vertexBindings & 0x30) != 0){					// Check for bits 5-6
			compressedVertexColorArray = CompressedVertexColorArray.read(workingContext);
		}

		CompressedVertexTextureCoordinateArray[] compressedVertexTextureCoordinateArrays = new CompressedVertexTextureCoordinateArray[8];
		if((vertexBindings & 0xf00) != 0){					// Check for bits 9-12
			compressedVertexTextureCoordinateArrays[0] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}
		if((vertexBindings & 0xf000) != 0){					// Check for bits 13-16
			compressedVertexTextureCoordinateArrays[1] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}
		if((vertexBindings & 0xf0000) != 0){				// Check for bits 17-20
			compressedVertexTextureCoordinateArrays[2] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}
		if((vertexBindings & 0xf00000) != 0){				// Check for bits 21-24
			compressedVertexTextureCoordinateArrays[3] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}
		if((vertexBindings & 0xf000000) != 0){				// Check for bits 25-28
			compressedVertexTextureCoordinateArrays[4] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}
		if((vertexBindings & 0xf0000000) != 0){				// Check for bits 29-32
			compressedVertexTextureCoordinateArrays[5] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}
		if((vertexBindings & 0xf00000000l) != 0){			// Check for bits 33-36
			compressedVertexTextureCoordinateArrays[6] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}
		if((vertexBindings & 0xf000000000l) != 0){			// Check for bits 37-40
			compressedVertexTextureCoordinateArrays[7] = CompressedVertexTextureCoordinateArray.read(workingContext);
		}

		CompressedVertexFlagArray compressedVertexFlagArray = null;
		if((vertexBindings & 0x8000000000000000l) != 0){	// Check for bit 64
			compressedVertexFlagArray = CompressedVertexFlagArray.read(workingContext);
		}

		return new TopoMeshCompressedRepDataV1(	numberOfFaceGroupListIndices,
												numberOfPrimitiveListIndices,
												numberOfVertexListIndices,
												faceGroupListIndices,
												primitiveListIndices,
												vertexListIndices,
												fgpvListIndicesHash,
												vertexBindings,
												quantizationParameters,
												numberOfVertexRecords,
												numberOfUniqueVertexCoordinates,
												uniqueVertexCoordinateLengthList,
												uniqueVertexListMapHash,
												compressedVertexCoordinateArray,
												compressedVertexNormalArray,
												compressedVertexColorArray,
												compressedVertexTextureCoordinateArrays,
												compressedVertexFlagArray);
	}
}
