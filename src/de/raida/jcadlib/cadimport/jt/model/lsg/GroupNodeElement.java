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

import de.raida.jcadlib.cadimport.jt.model.LSGNode;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.3 Group Node Element</h>
 * Object Type ID: <code>0x10dd101b, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>Group Nodes contain an ordered list of references to other nodes, called the
 * group's children. Group nodes may contain zero or more children; the children may
 * be of any node type. Group nodes may not contain references to themselves or their
 * ancestors.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class GroupNodeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "10dd101b-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Group node data */
	private GroupNodeData _groupNodeData;

	/**
	 * Constructor.
	 * @param groupNodeData Group node data
	 */
	public GroupNodeElement(GroupNodeData groupNodeData){
		_groupNodeData = groupNodeData;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _groupNodeData.getObjectID();
	}

	/**
	 * Returns the child node object IDs.
	 * @return Child node object IDs
	 */
	public int[] getChildNodeObjectIDs(){
		return _groupNodeData.getChildNodeObjectIDs();
	}

	/**
	 * Returns the attribute object IDs.
	 * @return Attribute object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _groupNodeData.getAttributeObjectIDs();
	}

	/**
	 * Returns a copy of the current class.
	 * @param  lsgNode Parent node
	 * @return         Copy of the current class
	 */
	public LSGNode copy(LSGNode lsgNode){
		GroupNodeElement groupNodeElement = new GroupNodeElement(_groupNodeData.copy());
		groupNodeElement.setAttributeNodes(getAttributeNodes());
		groupNodeElement.setPropertyNodes(getPropertyNodes());
		groupNodeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			groupNodeElement.addChildLSGNode(childNode.copy(groupNodeElement));
		}
		return groupNodeElement;
	}

	/**
	 * Reads a GroupNodeElement object.
	 * @param  workingContext Working context
	 * @return                GroupNodeElement instance
	 */
	public static GroupNodeElement read(WorkingContext workingContext){
		return new GroupNodeElement(GroupNodeData.read(workingContext));
	}
}
