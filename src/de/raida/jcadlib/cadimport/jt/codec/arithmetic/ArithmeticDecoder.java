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

package de.raida.jcadlib.cadimport.jt.codec.arithmetic;

import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.CodecDriver;
import de.raida.jcadlib.cadimport.jt.codec.Int32ProbabilityContextTableEntry;

/**
 * Class for decoding bytes with the arithmetic codec.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class ArithmeticDecoder {
	/**
	 * Decodes the given bytes by the arithmetic algorithm.
	 * @param  codecDriver Codec driver
	 * @return             Decoded symbols
	 */
	public static List<Integer> decode(CodecDriver codecDriver){
		ArrayList<Integer> decodedSymbols = new ArrayList<Integer>();
		AccumulatedProbabilityCounts accumProbCounts = new AccumulatedProbabilityCounts(codecDriver.getInt32ProbabilityContexts());

		int code = 0x0000;
		int low = 0x0000;
		int high = 0xffff;
		int bitBuffer = 0;
		int bits = 0;
		int symbolCount = codecDriver.getSymbolCount();
		int currentContext = 0;
		int[] newSymbolRange = new int[3];
		int outOfBandDataCounter = 0;

		List<Integer> outOfBandValues = codecDriver.getOutOfBandValues();

		int[] results = codecDriver.getNextCodeText();
		if(results == null){
			throw new IllegalArgumentException("ERROR: No more code bytes available!");
		} else {
			bitBuffer = results[0];
			bits = results[1];
		}

		code = (bitBuffer >> 16) & 0xffff;
		bitBuffer <<= 16;
		bits = 16;

		for(int i = 0; i < symbolCount; i++){
			int rescaledCode = (((((code - low) + 1) * accumProbCounts.getTotalSymbolCount(currentContext) - 1)) / ((high - low) + 1));
			Int32ProbabilityContextTableEntry int32ProbabilityContextTableEntry = accumProbCounts.getEntryAndSymbolRangeByRescaledCode(currentContext, rescaledCode, newSymbolRange);

			int range = high - low + 1;
			high = low + ((range * newSymbolRange[1]) / newSymbolRange[2] - 1);
			low  = low + ((range * newSymbolRange[0]) / newSymbolRange[2]);

			for(;;){
				if(((~(high^low)) & 0x8000) > 0){
					//Shift both out if most sign.

				} else if(((low & 0x4000) > 0) && ((high & 0x4000) == 0)){
					code ^= 0x4000;
					code = code & 0xffff;
					low  &= 0x3fff;
					low = low & 0xffff;
					high |= 0x4000;
					high = high & 0xffff;
						
				} else {
					// Nothing to shift out any more
					break;
				}

				low  = (low << 1) & 0xffff;
				high = (high << 1) & 0xffff;
				high = (high | 1) & 0xffff;;
				code = (code << 1) & 0xffff;

				if(bits == 0){
					results = codecDriver.getNextCodeText();
					if(results == null){
						throw new IllegalArgumentException("ERROR: No more code bytes available!");
					} else {
						bitBuffer = results[0];
						bits = results[1];
					}
				}

				code = (code | ((bitBuffer >> 31) & 0x00000001));
				bitBuffer <<= 1;
				bits--;
			}

			if((int32ProbabilityContextTableEntry.getSymbol() != -2) || (currentContext <= 0)){
				if((int32ProbabilityContextTableEntry.getSymbol() == -2) && (outOfBandDataCounter >= outOfBandValues.size())){
					throw new IllegalArgumentException("'Out-Of-Band' data missing! Read values: " + i + " / " + symbolCount);
				}
				decodedSymbols.add(((int32ProbabilityContextTableEntry.getSymbol() == -2) && outOfBandDataCounter < outOfBandValues.size() ? outOfBandValues.get(outOfBandDataCounter++) : int32ProbabilityContextTableEntry.getAssociatedValue()));
			}
			currentContext = int32ProbabilityContextTableEntry.getNextContext();
		}

		return decodedSymbols;
	}
}
