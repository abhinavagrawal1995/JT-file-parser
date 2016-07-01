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

package de.raida.jcadlib.cadimport.jt.model;

import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.1.3.1 Segment Header</h>
 * Segment Header contains information that determines how the remainder
 * of the Segment is interpreted by the loader.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class SegmentHeader {
	/** Segment ID */
	private GUID _segmentID;

	/** Segment type */
	private SegmentType _segmentType;

	/**
	 * Constructor.
	 * @param segmentID     Segment ID
	 * @param segmentType   Segment type
	 * @param segmentLength Segment length
	 */
	public SegmentHeader(GUID segmentID, int segmentType, int segmentLength){
		_segmentID = segmentID;
		_segmentType = SegmentType.get(segmentType);
	}

	/**
	 * Returns the ID of the segment.
	 * @return ID of the segment
	 */
	public GUID getSegmentID(){
		return _segmentID;
	}

	/**
	 * Returns the segment type.
	 * @return Segment type
	 */
	public SegmentType getSegmentType(){
		return _segmentType;
	}

	/**
	 * Reads a segment header.
	 * @param  workingContext Working context
	 * @return                SegmentHeader instance
	 */
	public static SegmentHeader read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		return new SegmentHeader(	GUID.read(workingContext),
									Helper.readI32(byteBuffer),
									Helper.readI32(byteBuffer));
	}
}
