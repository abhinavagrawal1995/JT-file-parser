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

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.7.1 LOD Node Data</h>
 * Object Type ID: <code>---</code>
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class LODNodeData {
	/** Group node data */
	private GroupNodeData _groupNodeData;

	/** Reserved field */
	private float[] _reservedField;

	/** Reserved field */
	private int _reservedField2;

	/**
	 * Constructor.
	 * @param groupNodeData  Group node data
	 * @param reservedField  Reserved field
	 * @param reservedField2 Reserved field
	 */
	public LODNodeData(GroupNodeData groupNodeData, float[] reservedField, int reservedField2){
		_groupNodeData = groupNodeData;
		_reservedField = reservedField;
		_reservedField2 = reservedField2;
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
	public LODNodeData copy(){
		return new LODNodeData(_groupNodeData.copy(), Helper.copy(_reservedField), _reservedField2);
	}

	/**
	 * Reads a LODNodeData object.
	 * @param  workingContext Working context
	 * @return                LODNodeData instance
	 */
	public static LODNodeData read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		return new LODNodeData(	GroupNodeData.read(workingContext),
								Helper.readVecF32(byteBuffer),
								Helper.readI32(byteBuffer));
	}
}
