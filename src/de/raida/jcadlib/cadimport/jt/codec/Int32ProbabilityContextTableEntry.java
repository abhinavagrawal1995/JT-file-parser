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

import de.raida.jcadlib.cadimport.jt.reader.BitBuffer;

/**
 * <h>8.1.1.1.1 Int32 Probability Context Table Entry</h>
 * Object Type ID: <code>---</code>
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class Int32ProbabilityContextTableEntry {
	/** Small integer number associated with a specific value in the context table */
	private int _symbol;

	/** Relative frequency of the value */
	private int _occurrenceCount;

	/** Associated Value is the value (from the input data) that the symbol represents.
	 * The CODECs don't directly encode values, they encode symbols. Symbols, then, are
	 * associated with specific values, so when the CODEC decodes an array of symbols,
	 * you can reconstruct the array of values that was intended by looking up the symbols
	 * in the Probability Context Table. This value is stored with 'Min Value' subtracted
	 * from the value. Complete descriptions for 'Min Value' and Number Value Bits can be
	 * found in 8.1.1.1 Int32 Probability Contexts. */
	private int _associatedValue;

	/** ID of the next context (index of the next context table entry) */
	private int _nextContext;

	/**
	 * Constructor.
	 * @param symbol          Small integer number associated with a specific value in the context table
	 * @param occurrenceCount Relative frequency of the value
	 * @param associatedValue Value (from the input data) that the symbol represents
	 * @param nextContext     Number of bits used for the next context field in 8.1.1.1.1 Int32 Probability Context Table Entry
	 */
	public Int32ProbabilityContextTableEntry(int symbol, int occurrenceCount, int associatedValue, int nextContext){
		_symbol = symbol;
		_occurrenceCount = occurrenceCount;
		_associatedValue = associatedValue;
		_nextContext = nextContext;
	}

	/**
	 * Returns the symbol.
	 * @return Symbol
	 */
	public int getSymbol(){
		return _symbol;
	}

	/**
	 * Returns the occurence count.
	 * @return Occurence count
	 */
	public int getOccurrenceCount(){
		return _occurrenceCount;
	}

	/**
	 * Returns the associated value.
	 * @return Associated value
	 */
	public int getAssociatedValue(){
		return _associatedValue;
	}

	/**
	 * Sets the associated value.
	 * @param associatedValue Associated value
	 */
	public void setAssociatedValue(int associatedValue){
		_associatedValue = associatedValue;
	}

	/**
	 * Returns the next context.
	 * @return Next context
	 */
	public int getNextContext(){
		return _nextContext;
	}

	/**
	 * Reads a Int32ProbabilityContextTableEntry object.
	 * @param  bitBuffer                 Bit buffer to read from
	 * @param  numberSymbolBits          Number of bits used to encode the Symbol range
	 * @param  numberOccurrenceCountBits Number of bits used to encode the Occurrence Count range
	 * @param  numberValueBits           Number of bits used to encode the Associated Value range
	 * @param  numberNextContextBits     Number of bits used for the next context field in 8.1.1.1.1 Int32 Probability Context Table Entry
	 * @param  minimumValue              Minimum value
	 * @return                           Int32ProbabilityContextTableEntry instance
	 */
	public static Int32ProbabilityContextTableEntry read(BitBuffer bitBuffer, int numberSymbolBits, int numberOccurrenceCountBits, int numberValueBits, int numberNextContextBits, int minimumValue){
		return new Int32ProbabilityContextTableEntry(	bitBuffer.readAsUnsignedInt(numberSymbolBits) - 2,
														bitBuffer.readAsUnsignedInt(numberOccurrenceCountBits),
														bitBuffer.readAsUnsignedInt(numberValueBits) + minimumValue,
														(numberNextContextBits != -1) ? bitBuffer.readAsUnsignedInt(numberNextContextBits) : 0);
	}
}
