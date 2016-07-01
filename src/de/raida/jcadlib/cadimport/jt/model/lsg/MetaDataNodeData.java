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

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.6.1 Meta Data Node Data</h>
 * Object Type ID: <code>---</code>
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class MetaDataNodeData {
	/** Group node data */
	private GroupNodeData _groupNodeData;

	/** Version number */
	private int _versionNumber;

	/**
	 * Constructor.
	 * @param groupNodeData Group node data      
	 * @param versionNumber Version number
	 */
	public MetaDataNodeData(GroupNodeData groupNodeData, int versionNumber){
		_groupNodeData = groupNodeData;
		_versionNumber = versionNumber;
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
	 * @return Copy of the current class
	 */
	public MetaDataNodeData copy(){
		return new MetaDataNodeData(_groupNodeData.copy(), _versionNumber);
	}

	/**
	 * Reads a MetaDataNodeData object.
	 * @param  workingContext Working context
	 * @return                MetaDataNodeData instance
	 */
	public static MetaDataNodeData read(WorkingContext workingContext){
		return new MetaDataNodeData(GroupNodeData.read(workingContext),
									Helper.readI16(workingContext.getByteBuffer()));
	}
}
