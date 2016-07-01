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
 * <h>7.2.1.1.1.8 Range LOD Node Element</h>
 * Object Type ID: <code>0x10dd104c, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>Range LOD Nodes hold a list of alternate representations and the ranges over
 * which those representations are appropriate. Range Limits indicate the distance
 * between a specified center point and the eye point, within which the corresponding
 * alternate representation is appropriate. Traversers of LSG consult these range
 * limit values when making an alternative representation selection.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class RangeLODNodeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "10dd104c-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** LOD node data */
	private LODNodeData _lodNodeData;

	/** Range limits */
	private float[] _rangeLimits;

	/** Center */
	private float[] _center;

	/**
	 * Constructor.
	 * @param lodNodeData LOD node data
	 * @param rangeLimits Range limits
	 * @param center      center
	 */
	public RangeLODNodeElement(LODNodeData lodNodeData, float[] rangeLimits, float[] center){
		_lodNodeData = lodNodeData;
		_rangeLimits = rangeLimits;
		_center = center;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _lodNodeData.getObjectID();
	}

	/**
	 * Returns the child node object IDs.
	 * @return Child node object IDs
	 */
	public int[] getChildNodeObjectIDs(){
		return _lodNodeData.getChildNodeObjectIDs();
	}

	/**
	 * Returns the attribute object IDs.
	 * @return Attribute object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _lodNodeData.getAttributeObjectIDs();
	}

	/**
	 * Returns a copy of the current class.
	 * @param  lsgNode Parent node
	 * @return         Copy of the current class
	 */
	public LSGNode copy(LSGNode lsgNode){
		RangeLODNodeElement rangeLODNodeElement = new RangeLODNodeElement(_lodNodeData.copy(), Helper.copy(_rangeLimits), Helper.copy(_center));
		rangeLODNodeElement.setAttributeNodes(getAttributeNodes());
		rangeLODNodeElement.setPropertyNodes(getPropertyNodes());
		rangeLODNodeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			rangeLODNodeElement.addChildLSGNode(childNode.copy(rangeLODNodeElement));
		}
		return rangeLODNodeElement;
	}

	/**
	 * Reads a RangeLODNodeElement object.
	 * @param  workingContext Working context
	 * @return                RangeLODNodeElement instance
	 */
	public static RangeLODNodeElement read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		return new RangeLODNodeElement(	LODNodeData.read(workingContext),
										Helper.readVecF32(byteBuffer),
										Helper.readCoordF32(byteBuffer));
	}
}
