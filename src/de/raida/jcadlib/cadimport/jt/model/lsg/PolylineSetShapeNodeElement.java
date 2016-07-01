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

package de.raida.jcadlib.cadimport.jt.model.lsg;

import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.model.LSGNode;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.10.4 Polyline Set Shape Node Element</h>
 * Object Type ID: <code>0x10dd1046, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>A Polyline Set Shape Node Element defines a collection of independent
 * and unconnected polylines. Each polyline constitutes one primitive of the
 * set and is defined by one list of vertex coordinates.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PolylineSetShapeNodeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "10dd1046-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Vertex shape data */
	private VertexShapeData _vertexShapeData;

	/** Version number */
	private int _versionNumber;

	/** Area factor */
	private float _areaFactor;

	/** Vertex bindings */
	private long _vertexBindings;

	/**
	 * Constructor.
	 * @param vertexShapeData Vertex shape data
	 * @param versionNumber   Version number
	 * @param areaFactor      Area factor
	 * @param vertexBinding   Vertex binding
	 */
	public PolylineSetShapeNodeElement(VertexShapeData vertexShapeData, int versionNumber, float areaFactor, long vertexBinding){
		_vertexShapeData = vertexShapeData;
		_versionNumber = versionNumber;
		_areaFactor = areaFactor;
		_vertexBindings = vertexBinding;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _vertexShapeData.getObjectID();
	}

	/**
	 * Returns the child node object IDs.
	 * @return Child node object IDs
	 */
	public int[] getChildNodeObjectIDs(){
		return _vertexShapeData.getChildNodeObjectIDs();
	}

	/**
	 * Returns the attribute object IDs.
	 * @return Attribute object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _vertexShapeData.getAttributeObjectIDs();
	}

	/**
	 * Returns a copy of the current class.
	 * @param  lsgNode Parent node
	 * @return         Copy of the current class
	 */
	public LSGNode copy(LSGNode lsgNode){
		PolylineSetShapeNodeElement polylineSetShapeNodeElement = new PolylineSetShapeNodeElement(_vertexShapeData.copy(), _versionNumber, _areaFactor, _vertexBindings);
		polylineSetShapeNodeElement.setAttributeNodes(getAttributeNodes());
		polylineSetShapeNodeElement.setPropertyNodes(getPropertyNodes());
		polylineSetShapeNodeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			polylineSetShapeNodeElement.addChildLSGNode(childNode.copy(polylineSetShapeNodeElement));
		}
		return polylineSetShapeNodeElement;
	}

	/**
	 * Reads a PolylineSetShapeNodeElement object.
	 * @param  workingContext Working context
	 * @return                PolylineSetShapeNodeElement instance
	 */
	public static PolylineSetShapeNodeElement read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		VertexShapeData vertexShapeData = VertexShapeData.read(workingContext, false);

		int versionNumber = -1;
		if(workingContext.getJTFileVersion() >= 9.0){
			versionNumber = Helper.readI16(byteBuffer);
			if((versionNumber < 0) || (versionNumber > 2)){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}
		}

		float areaFactor = Helper.readF32(byteBuffer);

		long vertexBindings = -1;
		if((workingContext.getJTFileVersion() >= 9.0) && (versionNumber != 1)){
			vertexBindings = Helper.readU64(byteBuffer);
		}
	
		return new PolylineSetShapeNodeElement(	vertexShapeData,
												versionNumber,
												areaFactor,
												vertexBindings);
	}
}
