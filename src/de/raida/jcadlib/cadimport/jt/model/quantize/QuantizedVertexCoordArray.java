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

import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.3.2.1 Quantized Vertex Coord Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Quantized Vertex Coord Array data collection contains the 
 * quantization data/representation for a set of vertex coordinates.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class QuantizedVertexCoordArray {
	/** Point quantizer data */
	private PointQuantizerData _pointQuantizerData;

	/** Vertex x coordinates */
	private List<Integer> _xVertexCoordinates;

	/** Vertex y coordinates */
	private List<Integer> _yVertexCoordinates;

	/** Vertex z coordinates */
	private List<Integer> _zVertexCoordinates;

	/**
	 * Constructor.
	 * @param pointQuantizerData Point quantizer data
	 * @param vertexCount        Number of vertices
	 * @param xVertexCoordinates Vertex x coordinates
	 * @param yVertexCoordinates Vertex y coordinates
	 * @param zVertexCoordinates Vertex z coordinates
	 */
	public QuantizedVertexCoordArray(PointQuantizerData pointQuantizerData, int vertexCount, List<Integer> xVertexCoordinates, List<Integer> yVertexCoordinates, List<Integer> zVertexCoordinates){
		_pointQuantizerData = pointQuantizerData;
		_xVertexCoordinates = xVertexCoordinates;
		_yVertexCoordinates = yVertexCoordinates;
		_zVertexCoordinates = zVertexCoordinates;
	}

	/**
	 * Returns the vertex data.
	 * @return Vertex data
	 */
	public List<Double> getVertices(){
		List<Double> xVertices = Helper.dequantize(_xVertexCoordinates, _pointQuantizerData.getXRange(), _pointQuantizerData.getNumberOfBits());
		List<Double> yVertices = Helper.dequantize(_yVertexCoordinates, _pointQuantizerData.getYRange(), _pointQuantizerData.getNumberOfBits());
		List<Double> zVertices = Helper.dequantize(_zVertexCoordinates, _pointQuantizerData.getZRange(), _pointQuantizerData.getNumberOfBits());

		List<Double> vertices = new ArrayList<Double>();
		for(int i = 0; i < _xVertexCoordinates.size(); i++){
			vertices.add(xVertices.get(i));
			vertices.add(yVertices.get(i));
			vertices.add(zVertices.get(i));
		}

		return vertices;
	}

	/**
	 * Reads a QuantizedVertexCoordArray object.
	 * @param  workingContext            Working context
	 * @return                           QuantizedVertexCoordArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static QuantizedVertexCoordArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		return new QuantizedVertexCoordArray(	PointQuantizerData.read(workingContext),
												Helper.readI32(workingContext.getByteBuffer()),
												// J. Raida: The next values have to be read as signed values. The
                								// specification up to version 8.1d is wrong (is says, that unsigned
                								// values have to be read)!
												Int32CDP.readVecI32(workingContext, PredictorType.PredLag1),
												Int32CDP.readVecI32(workingContext, PredictorType.PredLag1),
												Int32CDP.readVecI32(workingContext, PredictorType.PredLag1));
	}
}
