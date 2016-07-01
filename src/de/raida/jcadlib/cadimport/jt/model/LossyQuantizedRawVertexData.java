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

import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizedVertexColorArray;
import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizedVertexCoordArray;
import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizedVertexNormalArray;
import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizedVertexTextureCoordArray;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.3.2 Lossy Quantized Raw Vertex Data</h>
 * Object Type ID: <code>---</code>
 * <br>The Lossy Quantized Raw Vertex Data collection contains all the per-vertex
 * information (i.e. UV texture coordinates, color, normal vector, XYZ coordinate)
 * stored in a 'lossy' encoding/compression format for all primitives of the shape.
 * The Lossy Quantized Raw Vertex Data collection is only present when the
 * Quantization Parameters Bits Per Vertex data field is NOT equal to '0' (See
 * 7.2.1.1.1.10.2.1.1 Quantization Parameters for compete description).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class LossyQuantizedRawVertexData {
	/** Quantized vertex coordinate array */
	private QuantizedVertexCoordArray _quantizedVertexCoordArray;

	/** Quantized vertex normal array */
	private QuantizedVertexNormalArray _quantizedVertexNormalArray;

	/** Quantized vertex color array */
	private QuantizedVertexColorArray _quantizedVertexColorArray;

	/** Vertex data indices */
	private List<Integer> _vertexDataIndices;

	/**
	 * Constructor.
	 * @param quantizedVertexCoordArray        Quantized vertex coordinate array
	 * @param quantizedVertexNormalArray       Quantized vertex normal array
	 * @param quantizedVertexTextureCoordArray Quantized vertex texture coordinate array
	 * @param quantizedVertexColorArray        Quantized vertex color array
	 * @param vertexDataIndices                Vertex data indices
	 */
	public LossyQuantizedRawVertexData(QuantizedVertexCoordArray quantizedVertexCoordArray, QuantizedVertexNormalArray quantizedVertexNormalArray, QuantizedVertexTextureCoordArray quantizedVertexTextureCoordArray, QuantizedVertexColorArray quantizedVertexColorArray, List<Integer> vertexDataIndices){
		_quantizedVertexCoordArray = quantizedVertexCoordArray;
		_quantizedVertexNormalArray = quantizedVertexNormalArray;
		_quantizedVertexColorArray = quantizedVertexColorArray;
		_vertexDataIndices = vertexDataIndices;
	}

	/**
	 * Returns the vertex data.
	 * @return Vertex data
	 */
	public List<Double> getVertices(){
		List<Double> unsortedVertices = _quantizedVertexCoordArray.getVertices();
		List<Double> sortedVertices = new ArrayList<Double>();
		for(int vertexIndex : _vertexDataIndices){
			sortedVertices.add(unsortedVertices.get(vertexIndex * 3));
			sortedVertices.add(unsortedVertices.get((vertexIndex * 3) + 1));
			sortedVertices.add(unsortedVertices.get((vertexIndex * 3) + 2));
		}
		return sortedVertices;
	}

	/**
	 * Returns the normal data.
	 * @return Normal data
	 */
	public List<Double> getNormals(){
		List<Double> unsortedNormals = _quantizedVertexNormalArray.getNormals();
		List<Double> sortedNormals = new ArrayList<Double>();
		for(int vertexIndex : _vertexDataIndices){
			sortedNormals.add(unsortedNormals.get(vertexIndex * 3));
			sortedNormals.add(unsortedNormals.get((vertexIndex * 3) + 1));
			sortedNormals.add(unsortedNormals.get((vertexIndex * 3) + 2));
		}
		return sortedNormals;
	}

	/**
	 * Returns the colors.
	 * @return Colors
	 */
	public List<Float> getColors(){
		if(_quantizedVertexColorArray == null){
			return null;
		}

		List<Float> unsortedColors = _quantizedVertexColorArray.getColors();
		List<Float> sortedColors = new ArrayList<Float>();
		for(int vertexIndex : _vertexDataIndices){
			sortedColors.add(unsortedColors.get(vertexIndex * 3));
			sortedColors.add(unsortedColors.get((vertexIndex * 3) + 1));
			sortedColors.add(unsortedColors.get((vertexIndex * 3) + 2));
		}
		return sortedColors;
	}

	/**
	 * Reads a LossyQuantizedRawVertexData object.
	 * @param  workingContext            Working context
	 * @param  textureCoordBinding       Texture coordinate binding
	 * @param  colorBinding              Color binding
	 * @param  normalBinding             Normal binding
	 * @return                           LossyQuantizedRawVertexData instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static LossyQuantizedRawVertexData read(WorkingContext workingContext, int textureCoordBinding, int colorBinding, int normalBinding) throws UnsupportedCodecException {
		return new LossyQuantizedRawVertexData(	QuantizedVertexCoordArray.read(workingContext),
												(normalBinding != 0) ? QuantizedVertexNormalArray.read(workingContext) : null,
												(textureCoordBinding != 0) ? QuantizedVertexTextureCoordArray.read(workingContext) : null,
												(colorBinding != 0) ? QuantizedVertexColorArray.read(workingContext) : null,
												Int32CDP.readVecI32(workingContext, PredictorType.PredStripIndex));
	}
}
