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

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.3 Polyline Set Shape LOD Element</h>
 * Object Type ID: <code>0x10dd10a1, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>A Polyline Set Shape LOD Element contains the geometric shape definition
 * data (e.g. vertices, normals, etc.) for a single LOD of a collection of
 * independent and unconnected polylines. Each polyline constitutes one
 * primitive of the set.
 * <br>A Polyline Set Shape LOD Element is typically referenced by a Polyline
 * Set Shape Node Element using Late Loaded Property Atom Elements (see
 * 7.2.1.1.1.10.5 Polyline Set Shape Node Element and 7.2.1.2.7 Late Loaded
 * Property Atom Element respectively).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PolylineSetShapeLODElement {
	/** Object type ID */
	public final static String ID = "10dd10a1-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Vertex shape data */
	private VertexShapeLODData _vertexShapeLODData;

	/**
	 * Constructor.
	 * @param logicalElement                    Logical element
	 * @param vertexShapeLODData                Vertex shape LOD data
	 * @param versionNumber                     Version number
	 * @param vertexBasedShapeCompressedRepData Vertex based shape compressed rep data
	 */
	public PolylineSetShapeLODElement(LogicalElement logicalElement, VertexShapeLODData vertexShapeLODData, int versionNumber, VertexBasedShapeCompressedRepData vertexBasedShapeCompressedRepData){
		_vertexShapeLODData = vertexShapeLODData;
	}

	/**
	 * Returns the geometry information
	 * @return Geometry information
	 */
	public VertexShapeLODData getVertexShapeLODData(){
		return _vertexShapeLODData;
	}

	/**
	 * Reads a PolylineSetShapeLODElement object.
	 * @param  workingContext            Working context
	 * @return                           PolylineSetShapeLODElement instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static PolylineSetShapeLODElement read(WorkingContext workingContext) throws UnsupportedCodecException {
		LogicalElement logicalElement = null;
		if(workingContext.getJTFileVersion() >= 9.0){
			logicalElement = LogicalElement.read(workingContext);
		}

		VertexShapeLODData vertexShapeLODData = VertexShapeLODData.read(workingContext, false, true, false);
		int versionNumber = Helper.readI16(workingContext.getByteBuffer());
		if(versionNumber != 1){
			throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
		}
		VertexBasedShapeCompressedRepData vertexBasedShapeCompressedRepData = null;
		if(workingContext.getJTFileVersion() < 9.0){
			vertexBasedShapeCompressedRepData = VertexBasedShapeCompressedRepData.read(workingContext);
		}

		return new PolylineSetShapeLODElement(	logicalElement,
												vertexShapeLODData,
												versionNumber,
												vertexBasedShapeCompressedRepData);
	}
}
