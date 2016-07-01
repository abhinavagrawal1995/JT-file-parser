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
 * <h>7.2.1.1.1.4 Instance Node Element</h>
 * Object Type ID: <code>0x10dd102a, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>An Instance Node contains a single reference to another node. Their purpose is
 * to allow sharing of nodes and assignment of instance-specific attributes for the
 * instanced node. Instance Nodes may not contain references to themselves or their
 * ancestors.
 * <br>For example, a Group Node could use Instance Nodes to instance the same Shape
 * Node several times, applying different material properties and matrix transformations
 * to each instance. Note that this could also be done by using Group Nodes instead of
 * Instance Nodes, but Instance Nodes require fewer resources.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class InstanceNodeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "10dd102a-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Base node data */
	private BaseNodeData _baseNodeData;

	/** Child node object ID */
	private int _childNodeObjectID;

	/**
	 * Constructor.
	 * @param baseNodeData      Base node data
	 * @param childNodeObjectID Child node object ID
	 */
	public InstanceNodeElement(BaseNodeData baseNodeData, int childNodeObjectID){
		_baseNodeData = baseNodeData;
		_childNodeObjectID = childNodeObjectID;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _baseNodeData.getObjectID();
	}

	/**
	 * Returns the child node object IDs.
	 * @return Child node object IDs
	 */
	public int[] getChildNodeObjectIDs(){
		return new int[]{_childNodeObjectID};
	}

	/**
	 * Returns the attribute object IDs.
	 * @return Attribute object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _baseNodeData.getAttributeObjectIDs();
	}

	/**
	 * Returns a copy of the current class.
	 * @param  lsgNode Parent node
	 * @return         Copy of the current class
	 */
	public LSGNode copy(LSGNode lsgNode){
		InstanceNodeElement instanceNodeElement = new InstanceNodeElement(_baseNodeData.copy(), _childNodeObjectID);
		instanceNodeElement.setAttributeNodes(getAttributeNodes());
		instanceNodeElement.setPropertyNodes(getPropertyNodes());
		instanceNodeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			instanceNodeElement.addChildLSGNode(childNode.copy(instanceNodeElement));
		}
		return instanceNodeElement;
	}

	/**
	 * Reads a InstanceNodeElement object.
	 * @param  workingContext Working context
	 * @return                InstanceNodeElement instance
	 */
	public static InstanceNodeElement read(WorkingContext workingContext){
		BaseNodeData baseNodeData = BaseNodeData.read(workingContext);

		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int versionNumber = -1;
		if(workingContext.getJTFileVersion() >= 9.0){
			versionNumber = Helper.readI16(byteBuffer);
			if(versionNumber != 1){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}
		}

		int childNodeObjectID = Helper.readI32(workingContext.getByteBuffer());

		return new InstanceNodeElement(	baseNodeData,
										childNodeObjectID);
	}
}
