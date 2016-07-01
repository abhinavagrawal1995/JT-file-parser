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

import de.raida.jcadlib.cadimport.jt.JTImporter;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.1.3.2.1 Element Header</h>
 * Element Header contains data defining the length in bytes of the Element along
 * with information describing the object type contained in the Element.
 * <h>7.1.3.2.2 Element Header ZLIB</h>
 * Element Header ZLIB data collection is the format of Element Header data used by
 * all Elements within Segment Types that support ZLIB compression on all data in
 * the Segment.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class ElementHeader {
	/** Element length */
	private int _elementLength;

	/** Element ID */
	private GUID _elementID;

	/** Byte buffer to read from */
	private ByteBuffer _byteBuffer;

	/** Length of the compressed data */
	private int _compressedDataLength;

	/**
	 * Constructor for uncompressed elements.
	 * @param elementLength   Element length
	 * @param elementID       Element ID
	 * @param elementBaseType Element type
	 * @param byteBuffer      Byte buffer to read from
	 */
	public ElementHeader(int elementLength, GUID elementID, int elementBaseType, ByteBuffer byteBuffer){
		JTImporter.updateProgress(elementLength);

		_elementLength = elementLength;
		_elementID = elementID;
		_byteBuffer = byteBuffer;
	}

	/**
	 * Constructor for compressed elements.
	 * @param elementLength        Element length
	 * @param elementID            Element ID
	 * @param elementBaseType      Element type
	 * @param compressionFlag      Compression flag
	 * @param compressionAlgorithm Compression algorithm
	 * @param compressedDataLength Length of the compressed data
	 * @param byteBuffer           Byte buffer to read from
	 */
	public ElementHeader(int elementLength, GUID elementID, int elementBaseType, long compressionFlag, int compressionAlgorithm, int compressedDataLength, ByteBuffer byteBuffer){
		JTImporter.updateProgress(compressedDataLength);

		_elementLength = elementLength;
		_elementID = elementID;

		_compressedDataLength = compressedDataLength;
		_byteBuffer = byteBuffer;
	}

	/**
	 * Returns the ID of the element.
	 * @return ID of the element
	 */
	public GUID getElementID(){
		return _elementID;
	}

	/**
	 * Returns the length of the element.
	 * @return Length of the element
	 */
	public int getElementLength(){
		return _elementLength;
	}

	/**
	 * Returns the length of the compressed data.
	 * @return Length of the compressed data
	 */
	public int getCompressedDataLength(){
		return _compressedDataLength;
	}

	/**
	 * Returns the byte buffer of the element.
	 * @return Byte buffer of the element
	 */
	public ByteBuffer getByteBuffer(){
		return _byteBuffer;
	}

	/**
	 * Reads an element header.
	 * @param  workingContext Working context
	 * @param  firstElement   Is this the first
	 * @return                ElementHeader instance
	 * @throws Exception      Thrown, when decompression fails
	 */
	public static ElementHeader read(WorkingContext workingContext, boolean firstElement) throws Exception {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		// Read uncompressed ElementHeader
		if(!workingContext.getSegmentType().isZipped() || !firstElement){
			return new ElementHeader(	Helper.readI32(byteBuffer),
										GUID.read(workingContext),
										Helper.readU8(byteBuffer),
										byteBuffer);
		}

		// Read compressed ElementHeader
		int compressionFlag = Helper.readI32(byteBuffer);
		int compressedDataLength = Helper.readI32(byteBuffer) - 1;	// Remove one byte for the compression algorithm
		int compressionAlgorithm = Helper.readU8(byteBuffer);

		// Compression disabled
		if((compressionFlag != 2) || (compressionAlgorithm != 2)){
			return new ElementHeader(	Helper.readI32(byteBuffer),
										GUID.read(workingContext),
										Helper.readU8(byteBuffer),
										byteBuffer);
		}

		// Uncompress ElementHeader and data section
		byte[] compressedBytes = new byte[compressedDataLength];
		byteBuffer.get(compressedBytes);
		byte[] uncompressedBytes = Helper.decompressByZLIB(compressedBytes);

		ByteBuffer uncompressedData = ByteBuffer.wrap(uncompressedBytes);
		uncompressedData.order(byteBuffer.order());

		WorkingContext workingContext2 = workingContext.clone();
		workingContext2.setByteBuffer(uncompressedData);

		int elementLength = Helper.readI32(uncompressedData);
		GUID guid = GUID.read(workingContext2);
		int elementBaseType = Helper.readU8(uncompressedData);

		return new ElementHeader(	elementLength,
									guid,
									elementBaseType,
									compressionFlag,
									compressionAlgorithm,
									compressedDataLength,
									uncompressedData);
	}
}
