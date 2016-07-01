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

package de.raida.jcadlib.cadimport.jt.model.property;

import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.model.GUID;
import de.raida.jcadlib.cadimport.jt.model.JTNode;
import de.raida.jcadlib.cadimport.jt.model.SegmentType;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.2.4 Floating Point Property Atom Element</h>
 * Object Type ID: <code>0xe0b05be5, 0xfbbd, 0x11d1, 0xa3, 0xa7, 0x00, 0xaa, 0x00, 0xd1, 0x09, 0x54</code>
 * <br>Late Loaded Property Atom Element is a property atom type used to reference an
 * associated piece of atomic data in a separate addressable segment of the JT file.
 * The 'Late Loaded' connotation derives from the associated data being stored in a
 * separate addressable segment of the JT file, and thus a JT file reader can be
 * structured to support the 'best practice' of delaying the loading/reading of the
 * associated data until it is actually needed.
 * <br>Late Loaded Property Atom Elements are used to store a variety of data,
 * including, but not limited to, Shape LOD Segments and B-Rep Segments (see 7.2.2
 * Shape LOD Segment and 7.2.3 JT B-Rep Segment).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class LateLoadedPropertyAtomElement implements JTNode {
	/** Object type ID */
	public final static String ID = "e0b05be5-fbbd-11d1-a3-a7-0-aa-0-d1-9-54";

	/** Base property atom data */
	private BasePropertyAtomData _basePropertyAtomData;

	/** Segment ID */
	private GUID _segmentID;

	/** Segment type */
	private int _segmentType;

	/**
	 * Constructor.
	 * @param basePropertyAtomData Base property atom data
	 * @param segmentID       Segment ID
	 * @param segmentType     Segment type
	 * @param payLoadObjectID Payload object ID
	 */
	public LateLoadedPropertyAtomElement(BasePropertyAtomData basePropertyAtomData, GUID segmentID, int segmentType, int payLoadObjectID){
		_basePropertyAtomData = basePropertyAtomData;
		_segmentID = segmentID;
		_segmentType = segmentType;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _basePropertyAtomData.getObjectID();
	}

	/**
	 * Returns the segement ID.
	 * @return Segment ID
	 */
	public String getSegmentID(){
		return _segmentID.toString();
	}

	/**
	 * Returns the segement type.
	 * @return Segment type
	 */
	public int getSegmentType(){
		return _segmentType;
	}

	/**
	 * Reads a LateLoadedPropertyAtomElement object.
	 * @param  workingContext Working context
	 * @return                LateLoadedPropertyAtomElement instance
	 */
	public static LateLoadedPropertyAtomElement read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		BasePropertyAtomData basePropertyAtomData = BasePropertyAtomData.read(workingContext);

		GUID segmentID = GUID.read(workingContext);
		int segmentType = Helper.readI32(byteBuffer);

		// Check segment type
		SegmentType.get(segmentType);

		int payLoadObjectID = -1;
		if(workingContext.getJTFileVersion() >= 9.0){
			payLoadObjectID = Helper.readI32(byteBuffer);
			Helper.readI32(byteBuffer);	// Reserved
		}

		return new LateLoadedPropertyAtomElement(	basePropertyAtomData,
													segmentID,
													segmentType,
													payLoadObjectID);
	}
}
