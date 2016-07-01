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
 * <h>7.2.2.1.4 Point Set Shape LOD Element</h>
 * Object Type ID: <code>0x98134716, 0x0011, 0x0818, 0x19, 0x98, 0x08, 0x00, 0x09, 0x83, 0x5d, 0x5a</code>
 * <br>A Point Set Shape LOD Element contains the geometric shape definition data
 * (e.g. coordinates, normals, etc.) for a collection of independent and unconnected
 * points. Each point constitutes one primitive of the set.
 * <br>A Point Set Shape LOD Element is typically referenced by a Point Set Shape
 * Node Element using Late Loaded Property Atom Elements (see 7.2.1.1.1.10.5 Point
 * Set Shape Node Element and 7.2.1.2.7 Late Loaded Property Atom Element
 * respectively).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PointSetShapeLODElement {
	/** Object type ID */
	public final static String ID = "98134716-11-818-19-98-8-0-9-83-5d-5a";

	/** Vertex based shape compressed rep data */
	private VertexBasedShapeCompressedRepData _vertexBasedShapeCompressedRepData;

	/**
	 * Constructor.
	 * @param logicalElement                    Logical element
	 * @param vertexShapeLODData                Vertex shape LOD data
	 * @param versionNumber                     Version number
	 * @param vertexBasedShapeCompressedRepData Vertex based shape compressed rep data
	 */
	public PointSetShapeLODElement(LogicalElement logicalElement, VertexShapeLODData vertexShapeLODData, int versionNumber, VertexBasedShapeCompressedRepData vertexBasedShapeCompressedRepData){
		_vertexBasedShapeCompressedRepData = vertexBasedShapeCompressedRepData;

	}

	/**
	 * Returns the VertexBasedShapeCompressedRepData
	 * @return VertexBasedShapeCompressedRepData
	 */
	public VertexBasedShapeCompressedRepData getVertexBasedShapeCompressedRepData(){
		return _vertexBasedShapeCompressedRepData;
	}

	/**
	 * Reads a PointSetShapeLODElement object.
	 * @param  workingContext            Working context
	 * @return                           PointSetShapeLODElement instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static PointSetShapeLODElement read(WorkingContext workingContext) throws UnsupportedCodecException {
		if(workingContext.getJTFileVersion() >= 9.0){
			return new PointSetShapeLODElement(	LogicalElement.read(workingContext),
												VertexShapeLODData.read(workingContext),
												Helper.readI16(workingContext.getByteBuffer()),
												null);
			
		} else {
			return new PointSetShapeLODElement(	null,
												VertexShapeLODData.read(workingContext),
												Helper.readI16(workingContext.getByteBuffer()),
												VertexBasedShapeCompressedRepData.read(workingContext));
		}
	}
}
