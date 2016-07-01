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

/**
 * HuffCodeData is a helper class for keeping track of a given symbol and
 * the bits used to describe it.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class HuffCodeData {
	/** Symbol */
	private int _symbol;

	/** Code length */
	private int _codeLength;

	/** Bit code */
	private long _bitCode;

	/**
	 * Constructor.
	 */
	public HuffCodeData(){
		_symbol = 0;
		_codeLength = 0;
		_bitCode = 0;
	}

	/**
	 * Constructor.
	 * @param symbol     Symbol
	 * @param bitCode    Bit code
	 * @param codeLength Code length
	 */
	public HuffCodeData(int symbol, long bitCode, int codeLength){
		_symbol = symbol;
		_codeLength = codeLength;
		_bitCode = bitCode;
	}

	/**
	 * Returns the symbol.
	 * @return Symbol
	 */
	public int getSymbol(){
		return _symbol;
	}

	/**
	 * Sets the symbol.
	 * @param symbol Symbol
	 */
	public void setSymbol(int symbol){
		_symbol = symbol;
	}

	/**
	 * Returns the code length.
	 * @return Code length
	 */
	public int getCodeLength(){
		return _codeLength;
	}

	/**
	 * Sets the code length.
	 * @param codeLength Code length
	 */
	public void setCodeLength(int codeLength){
		_codeLength = codeLength;
	}

	/**
	 * Returns the bit code.
	 * @return Bit code
	 */
	public long getBitCode(){
		return _bitCode;
	}

	/**
	 * Sets the bit code.
	 * @param bitCode Bit code
	 */
	public void setBitCode(long bitCode){
		_bitCode = bitCode;
	}
}
