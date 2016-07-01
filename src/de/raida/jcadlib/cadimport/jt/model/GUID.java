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
 * The GUID type is a 16 byte (128-bit) number. GUID is stored/written to the
 * JT file using a four-byte word (U32), 2 two-byte words (U16), and 8 one-byte
 * words (U8) such as: {3F2504E0-4F89-11D3-9A-0C-03-05-E8-2C-33-01}. In the JT
 * format GUIDs are used as unique identifiers (e.g. Data Segment ID, Object
 * Type ID, etc.).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class GUID {
	/** Object type ID */
	public final static String END_OF_ELEMENTS = "ffffffff-ffff-ffff-ff-ff-ff-ff-ff-ff-ff-ff";

	/** Value 1 */
	private long _a;

	/** Value 2 */
	private int _b;

	/** Value 3 */
	private int _c;

	/** Value 4 */
	private int _d;

	/** Value 5 */
	private int _e;

	/** Value 6 */
	private int _f;

	/** Value 7 */
	private int _g;

	/** Value 8 */
	private int _h;

	/** Value 9 */
	private int _i;

	/** Value 10 */
	private int _j;

	/** Value 11 */
	private int _k;

	/**
	 * Constructor.
	 * @param a Value 1
	 * @param b Value 2
	 * @param c Value 3
	 * @param d Value 4
	 * @param e Value 5
	 * @param f Value 6
	 * @param g Value 7
	 * @param h Value 8
	 * @param i Value 9
	 * @param j Value 10
	 * @param k Value 11
	 */
	public GUID(long a, int b, int c, int d, int e, int f, int g, int h, int i, int j, int k){
		_a = a;
		_b = b;
		_c = c;
		_d = d;
		_e = e;
		_f = f;
		_g = g;
		_h = h;
		_i = i;
		_j = j;
		_k = k;
	}

	/**
	 * Constructor.
	 * @param guid GUID as string
	 */
	public GUID(String guid){
		String[] guidComponents = guid.split("-");
		_a = Long.parseLong(guidComponents[0], 16);
		_b = Integer.parseInt(guidComponents[1], 16);
		_c = Integer.parseInt(guidComponents[2], 16);
		_d = Integer.parseInt(guidComponents[3], 16);
		_e = Integer.parseInt(guidComponents[4], 16);
		_f = Integer.parseInt(guidComponents[5], 16);
		_g = Integer.parseInt(guidComponents[6], 16);
		_h = Integer.parseInt(guidComponents[7], 16);
		_i = Integer.parseInt(guidComponents[8], 16);
		_j = Integer.parseInt(guidComponents[9], 16);
		_k = Integer.parseInt(guidComponents[10], 16);
	}

	/**
	 * Creates and returns the string representation of the GUID.
	 * @return String representation of the GUID
	 */
	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append(Long.toHexString(_a)).append("-");
		stringBuffer.append(Integer.toHexString(_b)).append("-");
		stringBuffer.append(Integer.toHexString(_c)).append("-");
		stringBuffer.append(Integer.toHexString(_d & 0xFF)).append("-");
		stringBuffer.append(Integer.toHexString(_e & 0xFF)).append("-");
		stringBuffer.append(Integer.toHexString(_f & 0xFF)).append("-");
		stringBuffer.append(Integer.toHexString(_g & 0xFF)).append("-");
		stringBuffer.append(Integer.toHexString(_h & 0xFF)).append("-");
		stringBuffer.append(Integer.toHexString(_i & 0xFF)).append("-");
		stringBuffer.append(Integer.toHexString(_j & 0xFF)).append("-");
		stringBuffer.append(Integer.toHexString(_k & 0xFF));

		return stringBuffer.toString();
	}
	
	/**
	 * Reads a Global Unique ID.
	 * @param  workingContext Working context
	 * @return                GUID instance
	 */
	public static GUID read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

        return new GUID(Helper.readU32(byteBuffer),
        				Helper.readU16(byteBuffer),
        				Helper.readU16(byteBuffer),
        				Helper.readU8(byteBuffer),
		        		Helper.readU8(byteBuffer),
		        		Helper.readU8(byteBuffer),
		        		Helper.readU8(byteBuffer),
		        		Helper.readU8(byteBuffer),
		        		Helper.readU8(byteBuffer),
		        		Helper.readU8(byteBuffer),
		        		Helper.readU8(byteBuffer));
	}
}
