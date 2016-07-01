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
 * <h>7.2.1.1.1.10.5 Point Set Shape Node Element</h>
 * Object Type ID: <code>0x98134716, 0x0010, 0x0818, 0x19, 0x98, 0x08, 0x00, 0x09, 0x83, 0x5d, 0x5a</code>
 * <br>A Point Set Shape Node Element defines a collection of independent
 * and unconnected points. Each point constitutes one primitive of the set
 * and is defined by one vertex coordinate.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PointSetShapeNodeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "98134716-10-818-19-98-8-0-9-83-5d-5a";

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
	public PointSetShapeNodeElement(VertexShapeData vertexShapeData, int versionNumber, float areaFactor, long vertexBinding){
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
	 * Reads a PointSetShapeNodeElement object.
	 * @param  workingContext Working context
	 * @return                PointSetShapeNodeElement instance
	 */
	public static PointSetShapeNodeElement read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		VertexShapeData vertexShapeData = VertexShapeData.read(workingContext, true);

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

		return new PointSetShapeNodeElement(	vertexShapeData,
												versionNumber,
												areaFactor,
												vertexBindings);
	}
}
