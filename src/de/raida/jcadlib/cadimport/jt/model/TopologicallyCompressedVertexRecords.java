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

import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizationParameters;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.2.6 Topologically Compressed Vertex Records</h>
 * Object Type ID: <code>---</code>
 * <br>Documented here is the format of the vertex data written by the topological encoder from Appendix E:
 * Some additional explanation is necessary, however, because only the unique vertex coordinates are written to
 * the JT file, while the remaining vertex attributes (normals, colors, texture coordinates, vertex flags) may
 * not be unique.
 * <br>Vertex coordinates are written to the file in the order that they were visited by the topology encoder.
 * Note that this means that the number of vertex coordinates written is equal to the number of topological
 * vertices in the mesh (i.e. all vertex coordinates are unique).
 * <br>By contrast one set of vertex attribute records is written to the file corresponding to each 1 bit across
 * all encoded dual Face Attribute Masks. The vertex attribute records are written in the order that the topology
 * encoder visited them. The reader must then use the topology decoder's output to correctly associate each vertex
 * attribute record to the correct vertex coordinate using the dual Face Attribute Masks.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TopologicallyCompressedVertexRecords {
	/** Compressed vertex coordinate array */
	private CompressedVertexCoordinateArray _compressedVertexCoordinateArray;

	/** Compressed vertex normal array */
	private CompressedVertexNormalArray _compressedVertexNormalArray;

	/** Compressed vertex color array */
	private CompressedVertexColorArray _compressedVertexColorArray;

	/**
	 * Constructor.
	 * @param vertexBindings              Vertex bindings
	 * @param quantizationParameters      Quantization parameters
	 * @param numberOfTopologicalVertices Number of topological vertices
	 */
	public TopologicallyCompressedVertexRecords(long vertexBindings, QuantizationParameters quantizationParameters, int numberOfTopologicalVertices){
		this(vertexBindings, quantizationParameters, numberOfTopologicalVertices, -1, null, null, null, null, null);
	}

	/**
	 * Constructor.
	 * @param vertexBindings                          Vertex bindings
	 * @param quantizationParameters                  Quantization parameters
	 * @param numberOfTopologicalVertices             Number of topological vertices
	 * @param numberOfVertexAttributes                Number of vertex attributes
	 * @param compressedVertexCoordinateArray         Compressed vertex coordinate array
	 * @param compressedVertexNormalArray             Compressed vertex normal array
	 * @param compressedVertexColorArray              Compressed vertex color array
	 * @param compressedVertexTextureCoordinateArrays Compressed vertex texture coordinate arrays
	 * @param compressedVertexFlagArray               Compressed vertex flag array
	 */
	public TopologicallyCompressedVertexRecords(long vertexBindings, QuantizationParameters quantizationParameters, int numberOfTopologicalVertices,
			int numberOfVertexAttributes, CompressedVertexCoordinateArray compressedVertexCoordinateArray, CompressedVertexNormalArray compressedVertexNormalArray,
			CompressedVertexColorArray compressedVertexColorArray, CompressedVertexTextureCoordinateArray[] compressedVertexTextureCoordinateArrays,
			CompressedVertexFlagArray compressedVertexFlagArray){
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
	 * Returns the CompressedVertexNormalArray.
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
	 * Reads a TopologicallyCompressedVertexRecords object.
	 * @param  workingContext            Working context
	 * @return                           TopologicallyCompressedVertexRecords instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static TopologicallyCompressedVertexRecords read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		long vertexBindings = Helper.readU64(byteBuffer);
		QuantizationParameters quantizationParameters = QuantizationParameters.read(workingContext);
		int numberOfTopologicalVertices = Helper.readI32(byteBuffer);

		if(numberOfTopologicalVertices <= 0){
			return new TopologicallyCompressedVertexRecords(vertexBindings, quantizationParameters, numberOfTopologicalVertices);
		}

		int numberOfVertexAttributes = Helper.readI32(byteBuffer);

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
		if((vertexBindings & 0x40) != 0){					// Check for bit 7
			compressedVertexFlagArray = CompressedVertexFlagArray.read(workingContext);
		}

		return new TopologicallyCompressedVertexRecords(vertexBindings, quantizationParameters, numberOfTopologicalVertices,
				numberOfVertexAttributes, compressedVertexCoordinateArray, compressedVertexNormalArray, compressedVertexColorArray,
				compressedVertexTextureCoordinateArrays, compressedVertexFlagArray);
	}
}
