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

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.3.1 Lossless Compressed Raw Vertex Data</h>
 * Object Type ID: <code>---</code>
 * <br>The Lossless Compressed Raw Vertex Data collection contains all the per-vertex
 * information (i.e. UV texture coordinates, color, normal vector, XYZ coordinate) 
 * stored in a 'lossless' compression format for all primitives of the shape. The
 * Lossless Compressed Raw Vertex Data collection is only present when the
 * Quantization Parameters Bits Per Vertex data field equals '0'
 * (See 7.2.1.1.1.10.2.1.1 Quantization Parameters for complete description).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class LosslessCompressedRawVertexData {
	/** List of colors [r, g, b] */
	private List<Float> _colors;

	/** List of normals [nx, ny, nz] */
	private List<Double> _normals;

	/** List of vertices [x, y, z] */
	private List<Double> _vertices;

	/**
	 * Constructor.
	 * @param textureCoordinates List of texture coordinates [u, v]
	 * @param colors             List of colors [r, g, b]
	 * @param normals            List of normals [nx, ny, nz]
	 * @param vertices           List of vertices [x, y, z]
	 */
	public LosslessCompressedRawVertexData(List<Float> textureCoordinates, List<Float> colors, List<Double> normals, List<Double> vertices){
		_colors = colors;
		_normals = normals;
		_vertices = vertices;
	}

	/**
	 * Returns the vertex data.
	 * @return Vertex data
	 */
	public List<Double> getVertices(){
		return _vertices;
	}

	/**
	 * Returns the normal data.
	 * @return Normal data
	 */
	public List<Double> getNormals(){
		return _normals;
	}

	/**
	 * Returns the colors.
	 * @return Colors
	 */
	public List<Float> getColors(){
		return _colors;
	}

	/**
	 * Reads a LosslessCompressedRawVertexData object.
	 * @param  workingContext      Working context
	 * @param  textureCoordBinding Texture coordinate binding
	 * @param  colorBinding        Color binding
	 * @param  normalBinding       Normal binding
	 * @return                     LosslessCompressedRawVertexData instance
	 */
	public static LosslessCompressedRawVertexData read(WorkingContext workingContext, int textureCoordBinding, int colorBinding, int normalBinding){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int uncompressedDataSize = Helper.readI32(byteBuffer);
		int compressedDataSize = Helper.readI32(byteBuffer);
		float[] rawVertexData = null;

		// Uncompressed raw data
		if(compressedDataSize < 0){
			compressedDataSize *= -1;
			rawVertexData = new float[compressedDataSize / 4];
			for(int i = 0; i < rawVertexData.length; i++){
				rawVertexData[i] = Helper.readF32(byteBuffer);
			}

		// ZLIB compressed raw data
		} else if(compressedDataSize > 0){
			byte[] compressedBytes = Helper.readBytes(byteBuffer, compressedDataSize);
			byte[] uncompressedBytes = Helper.decompressByZLIB(compressedBytes);

			if(uncompressedBytes.length != uncompressedDataSize){
				throw new IllegalStateException("ZLIB decompression seems to be failed! Expected length: " + uncompressedDataSize + " -> resulting length: " + uncompressedBytes.length);
			}

			ByteBuffer uncompressedData = ByteBuffer.wrap(uncompressedBytes);
			uncompressedData.order(byteBuffer.order());

			rawVertexData = new float[uncompressedBytes.length / 4];
			for(int i = 0; i < rawVertexData.length; i++){
				rawVertexData[i] = Helper.readF32(uncompressedData);
			}

		} else {
			throw new IllegalArgumentException("Found invalid compressed data size: " + compressedDataSize);
		}

		// Create derived lists
		List<Float> textureCoordinates = new ArrayList<Float>();
		List<Float> colors = new ArrayList<Float>();
		List<Double> normals = new ArrayList<Double>();
		List<Double> vertices = new ArrayList<Double>();

		boolean readTextureCoordinate = textureCoordBinding == VertexBasedShapeCompressedRepData.BINDING_PER_VERTEX;
		boolean readColor             = colorBinding == VertexBasedShapeCompressedRepData.BINDING_PER_VERTEX;
		boolean readNormal            = normalBinding == VertexBasedShapeCompressedRepData.BINDING_PER_VERTEX;

		for(int i = 0; i < rawVertexData.length;){
			if(readTextureCoordinate){
				// Read U, V
				textureCoordinates.add(Float.valueOf(rawVertexData[i++]));
				textureCoordinates.add(Float.valueOf(rawVertexData[i++]));
            }

			if(readColor){
				// Read R, G, B
				colors.add(Float.valueOf(rawVertexData[i++]));
				colors.add(Float.valueOf(rawVertexData[i++]));
				colors.add(Float.valueOf(rawVertexData[i++]));
            }

			if(readNormal){
				// Read NX, NY, NZ
				normals.add(Double.valueOf(rawVertexData[i++]));
				normals.add(Double.valueOf(rawVertexData[i++]));
				normals.add(Double.valueOf(rawVertexData[i++]));
			}

			// Read X, Y, Z
			vertices.add(Double.valueOf(rawVertexData[i++]));
			vertices.add(Double.valueOf(rawVertexData[i++]));
			vertices.add(Double.valueOf(rawVertexData[i++]));
		}

		return new LosslessCompressedRawVertexData(textureCoordinates, colors, normals, vertices);
	}
}
