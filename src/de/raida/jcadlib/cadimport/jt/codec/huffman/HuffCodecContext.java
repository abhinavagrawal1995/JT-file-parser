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

package de.raida.jcadlib.cadimport.jt.codec.huffman;

import java.util.ArrayList;

/**
 * HuffCodecContext is a class that defines the Huffman context.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class HuffCodecContext {
	/** Length of Huffman code currently */
	private int _length; // 

	/** Code */
	private long _code;

	/** Codes */
	private ArrayList<HuffCodeData> _huffCodeDatas;

	/**
	 * Constructor.
	 */
	public HuffCodecContext(){
		_length = 0;
		_code = 0;
		_huffCodeDatas = new ArrayList<HuffCodeData>();
	}

	/**
	 * Returns the length.
	 * @return Length
	 */
	public int getLength(){
		return _length;
	}

	/**
	 * Sets the length.
	 * @param length Length
	 */
	public void setLength(int length){
		_length	= length;
	}

	/**
	 * Returns the code.
	 * @return Code
	 */
	public long getCode(){
		return _code;
	}

	/**
	 * Sets the code
	 * @param code Code
	 */
	public void setCode(long code){
		_code = code;
	}

	/**
	 * Returns the codes.
	 * @return Codes
	 */
	public ArrayList<HuffCodeData> getCodes(){
		return _huffCodeDatas;
	}
}
