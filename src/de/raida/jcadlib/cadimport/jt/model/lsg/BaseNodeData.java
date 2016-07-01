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
 * <h>7.2.1.1.1.1.1 Base Node Data</h>
 * Object Type ID: <code>---</code>
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class BaseNodeData {
	/** Object ID */
	private int _objectID;

	/** Node flags */
	private long _nodeFlags;

	/** Attribute object IDs */
	private int[] _attributeObjectIDs;

	/**
	 * Constructor.
	 * @param objectID           Object ID
	 * @param nodeFlags          Node flags
	 * @param attributeObjectIDs Attribute object IDs
	 */
	public BaseNodeData(int objectID, long nodeFlags, int[] attributeObjectIDs){
		_objectID = objectID;
		_nodeFlags = nodeFlags;
		_attributeObjectIDs = attributeObjectIDs;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _objectID;
	}

	/**
	 * Returns the attribute node object IDs.
	 * @return Attribute node object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _attributeObjectIDs;
	}

	/**
	 * Returns a copy of the current class.
	 * @return Copy of the current class
	 */
	public BaseNodeData copy(){
		return new BaseNodeData(_objectID, _nodeFlags, Helper.copy(_attributeObjectIDs));
	}

	/**
	 * Reads a BaseNodeData object.
	 * @param  workingContext Working context
	 * @return                BaseNodeData instance
	 */
	public static BaseNodeData read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int objectID = Helper.readI32(byteBuffer);

		int versionNumber = -1;
		if(workingContext.getJTFileVersion() >= 9.0){
			versionNumber = Helper.readI16(byteBuffer);
			if(versionNumber != 1){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}
		}

		long nodeFlags = Helper.readU32(byteBuffer);

		int[] attributeObjectIDs = new int[Helper.readI32(byteBuffer)];
		for(int i = 0; i < attributeObjectIDs.length; i++){
			attributeObjectIDs[i] = Helper.readI32(byteBuffer);
		}

		return new BaseNodeData(objectID,
								nodeFlags,
								attributeObjectIDs);
	}
}
