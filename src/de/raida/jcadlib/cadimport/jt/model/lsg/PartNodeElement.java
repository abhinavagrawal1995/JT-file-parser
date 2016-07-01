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
 * <h>7.2.1.1.1.5 Part Node Element</h>
 * Object Type ID: <code>0xce357244, 0x38fb, 0x11d1, 0xa5, 0x6, 0x0, 0x60, 0x97, 0xbd, 0xc6, 0xe1</code>
 * <br>A Part Node Element represents the root node for a particular Part within a
 * LSG structure. Every unique Part represented within a LSG structure should have
 * a corresponding Part Node Element. A Part Node Element typically references (using
 * Late Loaded Property Atoms) additional Part specific geometric data and/or
 * properties (e.g. B-Rep data, PMI data).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PartNodeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "ce357244-38fb-11d1-a5-6-0-60-97-bd-c6-e1";

	/** Meta data node data */
	private MetaDataNodeData _metaDataNodeData;

	/** Version number */
	private int _versionNumber;

	/** Reserved Field is a data field reserved for future JT format expansion */
	private int _reservedField;

	/**
	 * Constructor.
	 * @param metaDataNodeData Meta data node data
	 * @param versionNumber    Version number
	 * @param reservedField    Reserved Field is a data field reserved for future JT format expansion
	 */
	public PartNodeElement(MetaDataNodeData metaDataNodeData, int versionNumber, int reservedField){
		_metaDataNodeData = metaDataNodeData;
		_versionNumber = versionNumber;
		_reservedField = reservedField;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _metaDataNodeData.getObjectID();
	}

	/**
	 * Returns the child node object IDs.
	 * @return Child node object IDs
	 */
	public int[] getChildNodeObjectIDs(){
		return _metaDataNodeData.getChildNodeObjectIDs();
	}

	/**
	 * Returns the attribute object IDs.
	 * @return Attribute object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _metaDataNodeData.getAttributeObjectIDs();
	}

	/**
	 * Returns a copy of the current class.
	 * @param  lsgNode Parent node
	 * @return         Copy of the current class
	 */
	public LSGNode copy(LSGNode lsgNode){
		PartNodeElement partNodeElement = new PartNodeElement(_metaDataNodeData.copy(), _versionNumber, _reservedField);
		partNodeElement.setAttributeNodes(getAttributeNodes());
		partNodeElement.setPropertyNodes(getPropertyNodes());
		partNodeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			partNodeElement.addChildLSGNode(childNode.copy(partNodeElement));
		}
		return partNodeElement;
	}

	/**
	 * Reads a PartNodeElement object.
	 * @param  workingContext Working context
	 * @return                PartNodeElement instance
	 */
	public static PartNodeElement read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		return new PartNodeElement(	MetaDataNodeData.read(workingContext),
									Helper.readI16(byteBuffer),
									Helper.readI32(byteBuffer));
	}
}
