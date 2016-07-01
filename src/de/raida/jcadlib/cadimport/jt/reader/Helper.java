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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import de.raida.jcadlib.cadimport.jt.JTImporter;

/**
 * Helper class providing static helper functions.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class Helper {
	/** Field type data */
	private static String[][] _fieldTypeData = new String[][]{
		{"U8",  "1"}, {"U8",  "2"}, {"U8",  "3"},  {"U8",  "4"},
		{"I8",  "1"}, {"I8",  "2"}, {"I8",  "3"},  {"I8",  "4"},
		{"U16", "1"}, {"U16", "2"}, {"U16", "3"},  {"U16", "4"},
		{"I16", "1"}, {"I16", "2"}, {"I16", "3"},  {"I16", "4"},
		{"U32", "1"}, {"U32", "2"}, {"U32", "3"},  {"U32", "4"},
		{"I32", "1"}, {"I32", "2"}, {"I32", "3"},  {"I32", "4"},
		{"U64", "1"}, {"U64", "2"}, {"U64", "3"},  {"U64", "4"},
		{"I64", "1"}, {"I64", "2"}, {"I64", "3"},  {"I64", "4"},
		{"F32", "1"}, {"F32", "2"}, {"F32", "3"},  {"F32", "4"},
		{"F32", "4"}, {"F32", "9"}, {"F32", "16"}, {"F64", "1"},
		{"F64", "2"}, {"F64", "3"}, {"F64", "4"},  {"F64", "4"},
		{"F64", "9"}, {"F64", "16"}
	};

	/**
	 * Reads the given number of bytes and returns the string representation.
	 * @param  byteBuffer    Byte buffer to read from
	 * @param  numberOfBytes Number of bytes to read
	 * @return               String representation
	 */
	public static String readStringByLength(ByteBuffer byteBuffer, int numberOfBytes){
		StringBuffer stringBuffer = new StringBuffer(numberOfBytes);
		for(int i = 0; i < numberOfBytes; i++){
			stringBuffer.append((char)readU8(byteBuffer));
		}
		return stringBuffer.toString();
	}

	/**
	 * Reads a multi byte string (every character has two bytes)..
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read string
	 */
	public static String readMultiByteString(ByteBuffer byteBuffer){
		int stringLength = readI32(byteBuffer);
		if(stringLength == 0){
			return null;
		}

		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < stringLength; i++){
			stringBuffer.append((char)readI16(byteBuffer));
		}
		return stringBuffer.toString();
	}

	/**
	 * Reads a string (every character has one byte)..
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read string
	 */
	public static String readString(ByteBuffer byteBuffer){
		int stringLength = readI32(byteBuffer);
		if(stringLength == 0){
			return null;
		}

		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < stringLength; i++){
			stringBuffer.append((char)readU8(byteBuffer));
		}
		return stringBuffer.toString();
	}

	/**
	 * Read 8 byte double value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Double value
	 */
	public static double readF64(ByteBuffer byteBuffer){
		JTImporter.updateProgress(8);
		return byteBuffer.getDouble();
	}

	/**
	 * Read 4 byte float value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Float value
	 */
	public static float readF32(ByteBuffer byteBuffer){
		JTImporter.updateProgress(4);
		return byteBuffer.getFloat();
	}

	/**
	 * Reads a direction vector of floats.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read float values
	 */
	public static float[] readDirF32(ByteBuffer byteBuffer){
		return new float[]{	readF32(byteBuffer),
							readF32(byteBuffer),
							readF32(byteBuffer)};
	}

	/**
	 * Reads a coordinate of floats.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read float values
	 */
	public static float[] readCoordF32(ByteBuffer byteBuffer){
		return new float[]{	readF32(byteBuffer),
							readF32(byteBuffer),
							readF32(byteBuffer)};
	}

	/**
	 * Reads a coordinate of doubles.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read double values
	 */
	public static double[] readCoordF64(ByteBuffer byteBuffer){
		return new double[]{readF64(byteBuffer),
							readF64(byteBuffer),
							readF64(byteBuffer)};
	}

	/**
	 * Reads a red, green, blue and alpha value of floats.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read float values
	 */
	public static float[] readRGBA(ByteBuffer byteBuffer){
		return new float[]{	readF32(byteBuffer),
							readF32(byteBuffer),
							readF32(byteBuffer),
							readF32(byteBuffer),};
	}

	/**
	 * Read 8 byte integer value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Long value
	 */
	public static long readI64(ByteBuffer byteBuffer){
		JTImporter.updateProgress(8);
		return byteBuffer.getLong();
	}

	/**
	 * Read 4 byte integer value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Integer value
	 */
	public static int readI32(ByteBuffer byteBuffer){
		JTImporter.updateProgress(4);
		return byteBuffer.getInt();
	}

	/**
	 * Read 2 byte short value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Short value
	 */
	public static int readI16(ByteBuffer byteBuffer){
		JTImporter.updateProgress(2);
		return byteBuffer.getShort();
	}

	/**
	 * Read unsigned 8 byte integer value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Unsigned integer value
	 */
	public static long readU64(ByteBuffer byteBuffer){
		return (readI64(byteBuffer) & 0xffffffffffffffffL);
	}

	/**
	 * Read unsigned 4 byte integer value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Unsigned integer value
	 */
	public static long readU32(ByteBuffer byteBuffer){
		return (readI32(byteBuffer) & 0xffffffffL);
	}

	/**
	 * Read unsigned 2 byte short value (big or little endian).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Unsigned short value
	 */
	public static int readU16(ByteBuffer byteBuffer){
		return (readI16(byteBuffer) & 0xffff);
	}

	/**
	 * Read a byte value (1 byte).
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read value
	 */
	public static int readU8(ByteBuffer byteBuffer){
		JTImporter.updateProgress(1);
		return ((short)(byteBuffer.get() & 0xff));
	}

	/**
	 * Converts a signed byte to an unsigned byte.
	 * @param  signedByte Signed byte
	 * @return            Unsigned byte
	 */
	public static short convertSignedByteToUnsigned(byte signedByte){
		return (short)(signedByte & 0xFF);
	}

	/**
	 * Converts a signed int to an unsigned int.
	 * @param  signedInt Signed int
	 * @return           Unsigned int
	 */
	public static long convertSignedIntToUnsigned(int signedInt){
		return (signedInt & 0xffffffffL);
	}

	/**
	 * Reads a date (12 bytes)
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read date
	 */
	public static Date read12ByteDate(ByteBuffer byteBuffer){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, readI16(byteBuffer));
		calendar.set(Calendar.MONTH, readI16(byteBuffer));
		calendar.set(Calendar.DAY_OF_MONTH, readI16(byteBuffer));
		calendar.set(Calendar.HOUR_OF_DAY, readI16(byteBuffer));
		calendar.set(Calendar.MINUTE, readI16(byteBuffer));
		calendar.set(Calendar.SECOND, readI16(byteBuffer));
		return calendar.getTime();
	}

	/**
	 * Reads the given number of bytes and returns a byte array.
	 * @param  byteBuffer          Byte buffer to read from
	 * @param  numberOfBytesToRead Number of bytes to read
	 * @return                     Read bytes
	 */
	public static byte[] readBytes(ByteBuffer byteBuffer, int numberOfBytesToRead){
		JTImporter.updateProgress(numberOfBytesToRead);
		byte[] bytes = new byte[numberOfBytesToRead];
		byteBuffer.get(bytes);
		return bytes;
	}

	/**
	 * Reads a list of integer values.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read integers
	 */
	public static int[] readVecI32(ByteBuffer byteBuffer){
		int[] ints = new int[readI32(byteBuffer)];
		for(int i = 0; i < ints.length; i++){
			ints[i] = readI32(byteBuffer);
		}
		return ints;
	}

	/**
	 * Reads a list of long values.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read longs
	 */
	public static long[] readVecU32(ByteBuffer byteBuffer){
		long[] ints = new long[readI32(byteBuffer)];
		for(int i = 0; i < ints.length; i++){
			ints[i] = readU32(byteBuffer);
		}
		return ints;
	}

	/**
	 * Reads a list of float values.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read floats
	 */
	public static float[] readVecF32(ByteBuffer byteBuffer){
		float[] floats = new float[readI32(byteBuffer)];
		for(int i = 0; i < floats.length; i++){
			floats[i] = readF32(byteBuffer);
		}
		return floats;
	}

	/**
	 * Reads a bounding box of float values.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read floats
	 */
	public static float[][] readBBoxF32(ByteBuffer byteBuffer){
		return new float[][]{	{	readF32(byteBuffer),
									readF32(byteBuffer),
									readF32(byteBuffer)},
								{	readF32(byteBuffer),
									readF32(byteBuffer),
									readF32(byteBuffer)}};
	}

	/**
	 * Reads a range of int values.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            Read integers
	 */
	public static int[] readRange(ByteBuffer byteBuffer){
		return new int[]{readI32(byteBuffer), readI32(byteBuffer)};
	}

	/**
	 * Copy the given array.
	 * @param  source Array to copy
	 * @return        Copied array
	 */
	public static int[] copy(int[] source){
		int[] destination = new int[source.length];
		System.arraycopy(source, 0, destination, 0, source.length);
		return destination;
	}

	/**
	 * Copy the given array.
	 * @param  source Array to copy
	 * @return        Copied array
	 */
	public static float[] copy(float[] source){
		float[] destination = new float[source.length];
		System.arraycopy(source, 0, destination, 0, source.length);
		return destination;
	}

	/**
	 * Copy the given array.
	 * @param  source Array to copy
	 * @return        Copied array
	 */
	public static float[][] copy(float[][] source){
		float[][] destination = new float[source.length][];
		for(int i = 0; i < source.length; i++){
			destination[i] = copy(source[i]);
		}
		return destination;
	}

	/**
	 * Uncompresses the given byte array by the ZLIB algorithm.
	 * @param  compressedBytes Compressed bytes
	 * @return                 Uncompressed bytes
	 */
	public static byte[] decompressByZLIB(byte[] compressedBytes){
		try {
			Inflater inflater = new Inflater();
			inflater.setInput(compressedBytes);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(compressedBytes.length);
			byte[] buffer = new byte[1024];
			while(!inflater.finished()){
				int count = inflater.inflate(buffer);
				byteArrayOutputStream.write(buffer, 0, count);
			}
			byteArrayOutputStream.close();
			return byteArrayOutputStream.toByteArray();

		} catch(Exception exception){
			throw new IllegalStateException(exception.getMessage());
		}
	}

	/**
	 * Returns the field type data.
	 * @param  fieldType Field type
	 * @return           Field type data
	 */
	public static String getFieldTypeData(int fieldType){
		return _fieldTypeData[fieldType - 1][0];
	}

	/**
	 * Returns the field type components.
	 * @param  fieldType Field type
	 * @return           Field type components
	 */
	public static int getFieldTypeComponents(int fieldType){
		return Integer.parseInt(_fieldTypeData[fieldType - 1][1]);
	}

	/**
	 * Dequantizes the given vertex coordinates by the given ranges and the bit count.
	 * @param  vertexCoordinates Vertex coordinates
	 * @param  vertexRange       Vertex ranges (min and max)
	 * @param  numberOfBits      Number of bits
	 * @return                   Dequantized vertices
	 */
	public static List<Double> dequantize(List<Integer> vertexCoordinates, float[] vertexRange, int numberOfBits){
		float minimum = vertexRange[0];
		float maximum = vertexRange[1];
		long maxCode = 0xffffffff;

		if(numberOfBits < 32){
			maxCode = 0x1 << numberOfBits;
		}

		double encodeMultiplier = (double)maxCode / (maximum - minimum);

		List<Double> dequantizesVertices = new ArrayList<Double>();
		for(int i = 0; i < vertexCoordinates.size(); i++){
			dequantizesVertices.add((((vertexCoordinates.get(i) - 0.5) / encodeMultiplier + minimum)));
		}
		return dequantizesVertices;
	}

	/**
	 * Converts the int representing bits into a double value.
	 * @param  intValue Integer value
	 * @return          Double value
	 */
	public static double convertIntToFloat(int intValue){
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.putInt(intValue);
		byteBuffer.position(0);
		return byteBuffer.getFloat();
	}

	/**
	 * Converts a list of integer into an array of long.
	 * @param  intValues List of integer
	 * @return           Array of long
	 */
	public static long[] convertToLongArray(List<Integer> intValues){
		long[] longValues = new long[intValues.size()];
		for(int i = 0; i < longValues.length; i++){
			longValues[i] = intValues.get(i);
		}
		return longValues;
	}

	/**
	 * Converts a list of integer into an array of int.
	 * @param  intValues List of integer
	 * @return           Array of int
	 */
	public static int[] convertToIntArray(List<Integer> intValues){
		int[] intValuesAsArray = new int[intValues.size()];
		for(int i = 0; i < intValuesAsArray.length; i++){
			intValuesAsArray[i] = intValues.get(i);
		}
		return intValuesAsArray;
	}
}
