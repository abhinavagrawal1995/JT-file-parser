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

/**
 * Class for reading a ByteBuffer object bitwise.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class BitBuffer {
	/** Total size of bit buffer */
	private long _bitBufferSize;

	/** Current position in the buffer */
	private long _position;

	/** Underlying byte buffer */
	private ByteBuffer _byteBuffer;

	/** Byte order: Big or little endian */
	private ByteOrder _byteOrder;

	/**
	 * Constructor.
	 * @param byteBuffer underlying byte buffer
	 */
	public BitBuffer(ByteBuffer byteBuffer){
		this(byteBuffer, (((long)(byteBuffer.capacity())) << 3));	// Byte buffer size * 8
	}

	/**
	 * Constructor.
	 * @param byteBuffer Underlying byte buffer
	 * @param bufferSize Buffer size
	 */
	public BitBuffer(ByteBuffer byteBuffer, long bufferSize){
		_byteBuffer = byteBuffer;
		_bitBufferSize = bufferSize;
		_position = 0;
		_byteOrder = ByteOrder.BIG_ENDIAN;
	}

	/**
	 * Sets the byte order.
	 * @param byteOrder ByteOrder.BIG_ENDIAN or ByteOrder.LITTLE_ENDIAN
	 */
	public void setByteOrder(ByteOrder byteOrder){
		_byteOrder = byteOrder;
	}

	/**
	 * Reads the given number of bits as an unsignhed int value.
	 * @param  numberOfBits Number of bits to read
	 * @return              Read bits as unsigned int value
	 */
	public int readAsUnsignedInt(int numberOfBits){
		if(numberOfBits <= 0){
			return 0;
		}

		int result = 0;
		if(getNrNecessaryBytes(_position, numberOfBits) > 4){
			result = (int)getResultAsLong(_position, numberOfBits, 32);
		} else {
			result = getResultAsInt(_position, numberOfBits, 32);
		}

		return result;
	}

	/**
	 * Reads the given number of bits as an nsignhed int value.
	 * @param  numberOfBits Number of bits to read
	 * @return              Read bits as signed int value
	 */
	public int readAsSignedInt(int numberOfBits){
		int result = readAsUnsignedInt(numberOfBits);

		result <<= (32 - numberOfBits);
		result >>= (32 - numberOfBits);

		return result;
	}

	/**
	 * Returns the current position in the bit buffer.
	 * @return Current position in the bit buffer
	 */
	public long getPosition(){
		return _position;
	}

	/**
	 * Set the current position in the bit buffer.
	 * @param position New position in the bit buffer
	 */
	public void setPosition(long position){
		_position = position;
	}

	/**
	 * Returns the string representation of the next <numberOfBits> bits.
	 * @param  numberOfBits Number of bits to print. If -1, all bits are printed.
	 * @return              String representation of the next <numberOfBits> bits
	 */
	public String toString(long numberOfBits){
		long position = _position;
		StringBuffer stringBuffer = new StringBuffer();

		long startPosition = _position;
		numberOfBits += startPosition;
		if(numberOfBits == -1){
			numberOfBits = _bitBufferSize;
			startPosition = 0;
		}
		
		for(long i = startPosition; i < numberOfBits; i++){
			if((i > 0) && (((i - startPosition) % 8) == 0)){
				stringBuffer.append(" ");
			}
			stringBuffer.append((getResultAsInt(i, 1, 1) == 1) ? "1" : "0");
		}
		_position = position;
		return stringBuffer.toString();
	}

	/**
     * Returns an integral mask with a given number of 1's at least significant
     * bit positions.
     * @param  numberOfBits Number of bits
     * @return              int value of the mask
     */
	private int getMaskAsInt(int numberOfBits){
		return 0xFFFFFFFF >>> (32 - numberOfBits);
	}

	/**
	 * Return an integral mask with a given number of 1's at least significant
	 * bit positions.
	 * @param  numberOfBits Number of bits
     * @return              long value of the mask
	 */
	private long getMaskAsLong(int numberOfBits){
		return 0xFFFFFFFFFFFFFFFFL >>> (64 - numberOfBits);
	}

	/**
	 * Return the minimum number of bytes that are necessary to be read in order
	 * to read specified bits
	 * @param  bitPosition  Position of the first bit to read in the bit buffer
	 * @param  numberOfBits Number of bits
	 * @return              Number of bytes to read
	 */
	private int getNrNecessaryBytes(long bitPosition, int numberOfBits){
		return (int)(((bitPosition % 8) + numberOfBits + 7) / 8);
	}

	/**
	 * This method shifts to the right the integer buffer containing the
	 * specified bits, so that the last specified bit is the last bit in the
	 * buffer.
	 * @param  numberBuffer int buffer containing specified bits
	 * @param  bitPosition  Position of the first bit to read in the bit buffer
	 * @param  numberOfBits Number of bits
	 * @return              Shifted int buffer
	 */
	private int getRightShiftedNumberBufAsInt(int numberBuffer, long bitPosition, int numberOfBits){
		long shiftBits;
		if(_byteOrder == ByteOrder.BIG_ENDIAN){
			shiftBits = 7 - ((numberOfBits + bitPosition + 7) % 8);
		} else {
			shiftBits = bitPosition % 8;
		}

		return numberBuffer >> shiftBits;
	}

	/**
	 * This method shifts to the right the integer buffer containing the
	 * specified bits, so that the last specified bit is the last bit in the
	 * buffer.
	 * @param  numberBuffer long buffer containing specified bits
	 * @param  bitPosition  Position of the first bit to read in the bit buffer
	 * @param  numberOfBits Number of bits
	 * @return              Shifted int buffer
	 */
	private long getRightShiftedNumberBufAsLong(long numberBuffer, long bitPosition, int numberOfBits){
		long shiftBits;
		if(_byteOrder == ByteOrder.BIG_ENDIAN){
			shiftBits = 7 - ((numberOfBits + bitPosition + 7) % 8);
		} else {
			shiftBits = bitPosition % 8;
		}

		return numberBuffer >> shiftBits;
	}

	/**
	 * Check if all input parameters are correct.
	 * @param bitPosition   Position of the first bit to read in the bit buffer
	 * @param bitsToRead    Number of bits to read
	 * @param maxBitsToRead Maximum number of bits allowed to read, based on the
	 *                      method return type
	 */
	private void validateInputParams(long bitPosition, int bitsToRead, int maxBitsToRead){
		if(bitsToRead < 1){
			throw new IllegalArgumentException("Number of bits to read (" + bitsToRead + ") should greater than zero.");
		}

		if(bitPosition < 0){
			throw new IllegalArgumentException("Bit position (" + bitPosition + ") should be positive.");
		}

		if((maxBitsToRead != 1) && (maxBitsToRead != 8) && (maxBitsToRead != 16) && (maxBitsToRead != 32) && (maxBitsToRead != 64)){
			throw new IllegalArgumentException("Max number of bits to read (" + maxBitsToRead + ") should be either 1, 8, 16, 32 or 64.");
		}

		if(bitsToRead > maxBitsToRead){
			throw new IllegalArgumentException("Cannot read " + bitsToRead + " bits using " + maxBitsToRead + " bit long numberBuf (bitPos=" + bitPosition + ").");
		}

		if((bitPosition + bitsToRead) > _bitBufferSize){
			throw new IllegalArgumentException("Requested more bits than available: " + (bitPosition + bitsToRead) + " / " + _bitBufferSize);
		}
	}

	/**
	 * Return a value of integer type representing the buffer storing specified bits.
	 * @param bytesToRead       Number of bytes that are necessary to be read in
	 *                          order to read specific bits
	 * @param firstBytePosition Position of the first byte that is necessary to be read
	 * @return                  Value of all read bytes, containing specified bits
	 */
	private int getNumberBufAsInt(int bytesToRead, int firstBytePosition){
		int result = 0;
		int bytePortion = 0;

		for(int i = 0; i < bytesToRead; i++){
			bytePortion = 0xFF & (_byteBuffer.get(firstBytePosition++));

			if(_byteOrder == ByteOrder.LITTLE_ENDIAN){
				result = result | (bytePortion << (i << 3));
			} else {
				result = (bytePortion << ((bytesToRead - i - 1) << 3)) | result;
			}
		}

		return result;
	}

	/**
	 * Return a value of long type representing the buffer storing specified
	 * bits.
	 * @param bytesToRead       Number of bytes that are necessary to be read in
	 *                          order to read specific bits
	 * @param firstBytePosition Position of the first byte that is necessary to be read
	 * @return                  Value of all read bytes, containing specified bits
	 */
	private long getNumberBufAsLong(int bytesToRead, int firstBytePosition){
		long result = 0;
		long bytePortion = 0;

		for (int i = 0; i < bytesToRead; i++){
			bytePortion = 0xFF & (_byteBuffer.get(firstBytePosition++));

			if(_byteOrder == ByteOrder.LITTLE_ENDIAN){
				result = result | (bytePortion << (i << 3));
			} else {
				result = (bytePortion << ((bytesToRead - i - 1) << 3)) | result;
			}
		}

		return result;
	}

	/**
	 * Calculates the value represented by the given bits.
	 * @param bitPosition   Position of the first bit to read in the bit buffer
	 * @param bitsToRead    Number of bits to read
	 * @param maxBitsToRead Maximum number of bits allowed to read, based on the
	 *                      method return type
	 * @return              Integer value represented by the given bits
	 */
	private int getResultAsInt(long bitPosition, int bitsToRead, int maxBitsToRead){
		validateInputParams(bitPosition, bitsToRead, maxBitsToRead);
		int nrReadBytes = getNrNecessaryBytes(bitPosition, bitsToRead);
		int numberBuf = getNumberBufAsInt(nrReadBytes, (int) (bitPosition >> 3));
		int mask = getMaskAsInt(bitsToRead);
		int result = mask & getRightShiftedNumberBufAsInt(numberBuf, bitPosition, bitsToRead);
		_position = bitPosition + bitsToRead;

		return result;
	}

	/**
	 * Calculates the value represented by the given bits.
	 * @param bitPosition   Position of the first bit to read in the bit buffer
	 * @param bitsToRead    Number of bits to read
	 * @param maxBitsToRead Maximum number of bits allowed to read, based on the
	 *                      method return type
	 * @return              Long value represented by the given bits
	 */
	private long getResultAsLong(long bitPosition, int bitsToRead, int maxBitsToRead){
		validateInputParams(bitPosition, bitsToRead, maxBitsToRead);
		int nrReadBytes = getNrNecessaryBytes(bitPosition, bitsToRead);
		long numberBuf = getNumberBufAsLong(nrReadBytes, (int)(bitPosition >> 3));
		long mask = getMaskAsLong(bitsToRead);
		long result = mask & getRightShiftedNumberBufAsLong(numberBuf, bitPosition, bitsToRead);
		_position = bitPosition + bitsToRead;

		return result;
	}

	/**
	 * Returns the total size in bits.
	 * @return Total size in bits
	 */
	public long getSize(){
		return _bitBufferSize;
	}
}
