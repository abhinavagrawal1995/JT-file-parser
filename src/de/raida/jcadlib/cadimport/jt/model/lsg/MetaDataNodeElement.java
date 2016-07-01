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
 * <h>7.2.1.1.1.6 Meta Data Node Element</h>
 * Object Type ID: <code>0xce357245, 0x38fb, 0x11d1, 0xa5, 0x6, 0x0, 0x60, 0x97, 0xbd, 0xc6, 0xe1</code>
 * <br>The Meta Data Node Element is a node type used for storing references to
 * specific 'late loaded' meta-data (e.g. properties, PMI). The referenced meta-data
 * is stored in a separate addressable segment of the JT File (see 7.2.6 Meta Data
 * Segment) and thus the use of this Meta Data Node Element is in support of the JT
 * file loader/reader 'best practice' of late loading data (i.e. storing the referenced
 * meta-data in separate addressable segment of the JT file allows a JT file loader/
 * reader to ignore this node's meta-data on initial load and instead late-load the
 * node's meta-data upon demand so that the associated meta-data does not consume memory
 * until needed).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class MetaDataNodeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "ce357245-38fb-11d1-a5-6-0-60-97-bd-c6-e1";

	/** Meta data node data */
	private MetaDataNodeData _metaDataNodeData;

	/**
	 * Constructor.
	 * @param metaDataNodeData Meta data node data
	 */
	public MetaDataNodeElement(MetaDataNodeData metaDataNodeData){
		_metaDataNodeData = metaDataNodeData;
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
		MetaDataNodeElement metaDataNodeElement = new MetaDataNodeElement(_metaDataNodeData.copy());
		metaDataNodeElement.setAttributeNodes(getAttributeNodes());
		metaDataNodeElement.setPropertyNodes(getPropertyNodes());
		metaDataNodeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			metaDataNodeElement.addChildLSGNode(childNode.copy(metaDataNodeElement));
		}
		return metaDataNodeElement;
	}

	/**
	 * Reads a MetaDataNodeElement object.
	 * @param  workingContext Working context
	 * @return                MetaDataNodeElement instance
	 */
	public static MetaDataNodeElement read(WorkingContext workingContext){
		return new MetaDataNodeElement(MetaDataNodeData.read(workingContext));
	}
}
