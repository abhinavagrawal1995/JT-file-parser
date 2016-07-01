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
 * <h>7.2.1.1.1.3.1 Group Node Data</h>
 * Object Type ID: <code>---</code>
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class GroupNodeData {
	/** Base node data */
	private BaseNodeData _baseNodeData;

	/** Child object IDs */
	private int[] _childObjectIDs;

	/**
	 * Constructor.
	 * @param baseNodeData   Base node data
	 * @param childObjectIDs Child object IDs
	 */
	public GroupNodeData(BaseNodeData baseNodeData, int[] childObjectIDs){
		_baseNodeData = baseNodeData;
		_childObjectIDs = childObjectIDs;
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
		return _childObjectIDs;
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
	 * @return Copy of the current class
	 */
	public GroupNodeData copy(){
		return new GroupNodeData(_baseNodeData.copy(), Helper.copy(_childObjectIDs));
	}

	/**
	 * Reads a GroupNodeData object.
	 * @param workingContext Working context
	 * @return               GroupNodeData instance
	 */
	public static GroupNodeData read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		BaseNodeData baseNodeData = BaseNodeData.read(workingContext);

		int versionNumber = -1;
		if(workingContext.getJTFileVersion() >= 9.0){
			versionNumber = Helper.readI16(byteBuffer);
			if(versionNumber != 1){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}
		}

		int[] childObjectIDs = new int[Helper.readI32(byteBuffer)];
		for(int i = 0; i < childObjectIDs.length; i++){
			childObjectIDs[i] = Helper.readI32(byteBuffer);
		}

		return new GroupNodeData(	baseNodeData,
									childObjectIDs);
	}
}
