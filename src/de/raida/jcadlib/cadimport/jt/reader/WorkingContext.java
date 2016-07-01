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

package de.raida.jcadlib.cadimport.jt.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.raida.jcadlib.cadimport.jt.model.SegmentType;


/**
 * Helper class providing ofen used values, like JT file version.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class WorkingContext {
	/** Byte buffer to read from */
	private ByteBuffer _byteBuffer;

	/** Version of the JT file */
	private float _jtFileVersion;

	/** Segment type */
	private SegmentType _segmentType;

	/** PMI version */
	private int _pmiVersionNumber;

	/** File name */
	private String _fileName;

	/** Byte order */
	private ByteOrder _byteOrder;

	/**
	 * Constructor.
	 */
	public WorkingContext(){
	}

	/**
	 * Returns a copy of the current working context.
	 * @return Copy of the current working context
	 */
	public WorkingContext clone(){
		WorkingContext workingContext = new WorkingContext();
		workingContext.setByteBuffer(_byteBuffer);
		workingContext.setJTFileVersion(_jtFileVersion);
		workingContext.setSegmentType(_segmentType);
		return workingContext;
	}

	/**
	 * Sets the byte buffer to read from.
	 * @param byteBuffer Byte buffer to read from
	 */
	public void setByteBuffer(ByteBuffer byteBuffer){
		_byteBuffer = byteBuffer;
	}

	/**
	 * Returns the byte buffer to read from.
	 * @return Byte buffer to read from
	 */
	public ByteBuffer getByteBuffer(){
		return _byteBuffer;
	}

	/**
	 * Sets the version of the JT file.
	 * @param jtFileVersion Version of the JT file
	 */
	public void setJTFileVersion(float jtFileVersion){
		_jtFileVersion = jtFileVersion;
	}

	/**
	 * Returns the version of the JT file.
	 * @return Version of the JT file
	 */
	public float getJTFileVersion(){
		return _jtFileVersion;
	}

	/**
	 * Set the segment type.
	 * @param segmentType Segment type
	 */
	public void setSegmentType(SegmentType segmentType){
		_segmentType = segmentType;
	}

	/**
	 * Returns the segment type.
	 * @return Segment type
	 */
	public SegmentType getSegmentType(){
		return _segmentType;
	}

	/**
	 * Sets the PMI version number.
	 * @param pmiVersionNumber PMI version number
	 */
	public void setPMIVersionNumber(int pmiVersionNumber){
		_pmiVersionNumber = pmiVersionNumber;
	}

	/**
	 * Returns the PMI version number.
	 * @return PMI version number
	 */
	public int getPMIVersionNumber(){
		return _pmiVersionNumber;
	}

	/**
	 * Sets the file name.
	 * @param fileName File name
	 */
	public void setFileName(String fileName){
		_fileName = fileName;
	}

	/**
	 * Returns the file name.
	 * @return File name
	 */
	public String getFileName(){
		return _fileName;
	}

	/**
	 * Sets the byte order.
	 * @param byteOrder Byte order
	 */
	public void setByteOrder(ByteOrder byteOrder){
		_byteOrder = byteOrder;
	}

	/**
	 * Returns the byte order.
	 * @return Byte order
	 */
	public ByteOrder getByteOrder(){
		return _byteOrder;
	}
}
