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

package de.raida.jcadlib.cadimport.jt.codec;

import java.nio.ByteBuffer;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.reader.BitBuffer;

/**
 * A class that deals with the conversions from SYMBOL to VALUE and provides end-consumer
 * APIs for using the codecs.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class CodecDriver {
	/** Bytes to decompress */
	private byte[] _codeTextBytes;

	/** Bits to interpret from code text bytes */
	private Integer _codeTextLengthInBits;

	/** Value element count */
	private Integer _valueElementCount;

	/** Symbol count */
	private Integer _symbolCount;

	/** Probability contexts */
	private Int32ProbabilityContexts _int32ProbabilityContexts;

	/** Bit buffer */
	private BitBuffer _bitBuffer;

	/** Number of read bits */
	private int _bitsRead;

	/** Out of band values */
	private List<Integer> _outOfBandValues;

	/**
	 * Constructor.
	 * @param codeTextBytes            Bytes to decompress
	 * @param codeTextLengthInBits     Bits to interpret from code text bytes 
	 * @param valueElementCount        Value element count
	 * @param symbolCount              Symbol count
	 * @param int32ProbabilityContexts Probability contexts
	 * @param outOfBandValues          Out of band values
	 */
	public CodecDriver(byte[] codeTextBytes, Integer codeTextLengthInBits, Integer valueElementCount, Integer symbolCount, Int32ProbabilityContexts int32ProbabilityContexts, List<Integer> outOfBandValues){
		_codeTextBytes = codeTextBytes;
		_codeTextLengthInBits = codeTextLengthInBits;
		_valueElementCount = valueElementCount;
		_symbolCount = symbolCount;
		_int32ProbabilityContexts = int32ProbabilityContexts;
		_bitBuffer = new BitBuffer(ByteBuffer.wrap(_codeTextBytes), _codeTextLengthInBits.intValue());
		_bitsRead = 0;
		_outOfBandValues = outOfBandValues;
	}

	/**
	 * Returns the number of symbols.
	 * @return Number of symbols
	 */
	public int getSymbolCount(){
		// JT v9 doesn't know a symbol count
		if(_symbolCount == null){
			return _valueElementCount;
		}

		return (_int32ProbabilityContexts.getProbabilityContextCount() <= 1) ? _valueElementCount : _symbolCount;
	}

	/**
	 * Returns the next code text and the number of read bits.
	 * @return Next code text and the number of read bits
	 */
	public int[] getNextCodeText(){
		int nBits = Math.min(32, (_codeTextLengthInBits - _bitsRead));
		int uCodeText = _bitBuffer.readAsUnsignedInt(nBits);

		// Fill up the trailing positions with "0"
		if(nBits < 32){
			uCodeText = uCodeText << (32 - nBits);
		}

		_bitsRead += nBits;
		return new int[]{uCodeText, nBits};
	}

	/**
	 * Returns the bit buffer.
	 * @return Bit buffer
	 */
	public BitBuffer getBitBuffer(){
		return _bitBuffer;
	}

	/**
	 * Returns the probability contexts
	 * @return Probability contexts
	 */
	public Int32ProbabilityContexts getInt32ProbabilityContexts(){
		return _int32ProbabilityContexts;
	}

	/**
	 * Returns the total bit count of the code text.
	 * @return Total bit count of the code text
	 */
	public int getCodeTextLengthInBits(){
		return _codeTextLengthInBits;
	}

	/**
	 * Returns the out-of-band values.
	 * @return Out-of-band values
	 */
	public List<Integer> getOutOfBandValues(){
		return _outOfBandValues;
	}

	/**
	 * Returns the value element count.
	 * @return Value element count
	 */
	public int getValueElementCount(){
		return _valueElementCount;
	}
}
