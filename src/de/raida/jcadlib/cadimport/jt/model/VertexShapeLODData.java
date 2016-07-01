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
 * <h>7.2.2.1.1.1 Vertex Shape LOD Data</h>
 * Object Type ID: <code>0x10dd10b0, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br> Vertex Shape LOD Element represents LODs defined by collections
 * of vertices.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class VertexShapeLODData {
	/** Object type ID */
	public final static String ID = "10dd10b0-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Common items to all compressed LOD data elements */
	private TopoMeshCompressedLODData _topoMeshCompressedLODData;

	/** Common items to all topo mesh topologically compressed LOD data elements */
	private TopoMeshTopologicallyCompressedLODData _topoMeshTopologicallyCompressedLODData;

	/**
	 * Constructor (up to JT version 8).
	 * @param versionNumber          Version number (normally 0x0001)
	 * @param bindingAttributes      Normal, texture and color bindung
	 * @param quantizationParameters Quantization parameters
	 */
	public VertexShapeLODData(int versionNumber, int bindingAttributes, QuantizationParameters quantizationParameters){
	}

	/**
	 * Constructor (since JT version 9).
	 * @param versionNumber                          Version number
	 * @param vertexBinding                          Vertex binding
	 * @param topoMeshCompressedLODData              Common items to all topo mesh compressed LOD data elements
	 * @param topoMeshTopologicallyCompressedLODData Common items to all topo mesh topologically compressed LOD data elements
	 */
	public VertexShapeLODData(int versionNumber, long vertexBinding, TopoMeshCompressedLODData topoMeshCompressedLODData, TopoMeshTopologicallyCompressedLODData topoMeshTopologicallyCompressedLODData){
		_topoMeshCompressedLODData = topoMeshCompressedLODData;
		_topoMeshTopologicallyCompressedLODData = topoMeshTopologicallyCompressedLODData;
	}

	/**
	 * Returns the TopoMeshCompressedLODData.
	 * @return TopoMeshCompressedLODData
	 */
	public TopoMeshCompressedLODData getTopoMeshCompressedLODData(){
		return _topoMeshCompressedLODData;
	}

	/**
	 * Returns the TopoMeshTopologicallyCompressedLODData.
	 * @return TopoMeshTopologicallyCompressedLODData
	 */
	public TopoMeshTopologicallyCompressedLODData getTopoMeshTopologicallyCompressedLODData(){
		return _topoMeshTopologicallyCompressedLODData;
	}

	/**
	 * Reads a VertexShapeLODData object.
	 * @param  workingContext            Working context
	 * @return                           VertexShapeLODData instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static VertexShapeLODData read(WorkingContext workingContext) throws UnsupportedCodecException {
		return read(workingContext, false, false, false);
	}

	/**
	 * Reads a VertexShapeLODData object.
	 * @param  workingContext                 Working context
	 * @param  fromTriStripSetShapeLODElement Called from TriStripSetShapeLODElement?
	 * @param  fromPolyLineShape              Called from PolylineShapeElement?
	 * @param  fromPointSetShape              Called from PointSetShapeElement?
	 * @return                                VertexShapeLODData instance
	 * @throws UnsupportedCodecException      Thrown, when an unsupported codec has been found
	 */
	public static VertexShapeLODData read(WorkingContext workingContext, boolean fromTriStripSetShapeLODElement, boolean fromPolyLineShape, boolean fromPointSetShape) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		if(workingContext.getJTFileVersion() >= 9.0){
			int versionNumber = Helper.readI16(byteBuffer);
			if(versionNumber != 1){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}

			long vertexBinding = Helper.readU64(byteBuffer);

			TopoMeshCompressedLODData topoMeshCompressedLODData = null;
			TopoMeshTopologicallyCompressedLODData topoMeshTopologicallyCompressedLODData = null;

			if(fromTriStripSetShapeLODElement){
				topoMeshTopologicallyCompressedLODData = TopoMeshTopologicallyCompressedLODData.read(workingContext, fromPolyLineShape);
			} else {
				// Skip two unknown bytes
				Helper.readBytes(byteBuffer, 2);

				topoMeshCompressedLODData = TopoMeshCompressedLODData.read(workingContext, fromPolyLineShape);
			}

			return new VertexShapeLODData(	versionNumber,
											vertexBinding,
											topoMeshCompressedLODData,
											topoMeshTopologicallyCompressedLODData);

		} else {
			int versionNumber = Helper.readI16(byteBuffer);
			if(versionNumber != 1){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}

			int bindingAttributes = Helper.readI32(byteBuffer);
			QuantizationParameters quantizationParameters = QuantizationParameters.read(workingContext);

			return new VertexShapeLODData(	versionNumber,
											bindingAttributes,
											quantizationParameters);
		}		
	}
}
