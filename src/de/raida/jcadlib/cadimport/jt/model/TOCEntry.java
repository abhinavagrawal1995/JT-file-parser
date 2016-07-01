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
 * <h>7.1.2.1 TOC Entry</h>
 * Each TOC Entry represents a Data Segment within the JT File. The essential
 * function of a TOC Entry is to map a Segment ID to an absolute byte offset
 * within the file.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TOCEntry {
	/** ID of the referenced segment */
	private GUID _segmentID;

	/** Offset of the referenced segment */
	private int _segmentOffSet;

	/** Length of the referenced segment */
	private int _segmentLength;

	/**
	 * Constructor.
	 * @param segmentID         GUID of the referenced segment
	 * @param segmentOffSet     Offset of the referenced segment
	 * @param segmentLength     Length of the referenced segment
	 * @param segmentAttributes Attributes of the referenced segment
	 */
	public TOCEntry(GUID segmentID, int segmentOffSet, int segmentLength, int segmentAttributes){
		_segmentID = segmentID;
		_segmentOffSet = segmentOffSet;
		_segmentLength = segmentLength;
	}

	/**
	 * Returns the ID of the segment.
	 * @return ID of the segment
	 */
	public GUID getSegmentID(){
		return _segmentID;
	}

	/**
	 * Returns the offset of the referenced segment.
	 * @return Offset of the referenced segment
	 */
	public int getSegmentOffSet(){
		return _segmentOffSet;
	}

	/**
	 * Returns the length of the segment.
	 * @return length of the segment
	 */
	public int getSegmentLength(){
		return _segmentLength;
	}

	/**
	 * Reads a TOC entry.
	 * @param  workingContext Working context
	 * @return                TOCEntry instance
	 */
	public static TOCEntry read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		return new TOCEntry(GUID.read(workingContext),
							Helper.readI32(byteBuffer),
							Helper.readI32(byteBuffer),
							Helper.readI32(byteBuffer));

	}
}
