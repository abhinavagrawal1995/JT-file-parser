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

package de.raida.jcadlib.cadimport.jt.codec.bitlength;

import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.CodecDriver;
import de.raida.jcadlib.cadimport.jt.reader.BitBuffer;

/**
 * Class for decoding bytes with the bitlength codec.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class BitlengthDecoder {
	/**
	 * Decodes the given bytes by the bitlength algorithm.
	 * @param  codecDriver Codec driver
	 * @return             Decoded symbols
	 */
	public static List<Integer> decode(CodecDriver codecDriver){
		BitBuffer encodedBits = codecDriver.getBitBuffer();
		int bitFieldWith = 0;
		ArrayList<Integer> decodedSymbols = new ArrayList<Integer>();
		while((encodedBits.getSize() - encodedBits.getPosition()) > 0){
			if(encodedBits.readAsUnsignedInt(1) == 0){
				// Decode symbol with same bit field length
				int decodedSymbol = -1;
				if(bitFieldWith == 0){
					decodedSymbol = 0;
				} else {
					decodedSymbol = encodedBits.readAsUnsignedInt(bitFieldWith);
                    decodedSymbol <<= (32 - bitFieldWith);
                    decodedSymbol >>= (32 - bitFieldWith);
				}
				decodedSymbols.add(decodedSymbol);

			} else {
				// Adjust bit field length
				int adjustmentBit = encodedBits.readAsUnsignedInt(1);
				do {
					if(adjustmentBit == 1){
						bitFieldWith += 2;
					} else {
						bitFieldWith -= 2;
					}
				} while(encodedBits.readAsUnsignedInt(1) == adjustmentBit);

				// Decode symbol with new bit field length
				int decodedSymbol = -1;
				if(bitFieldWith == 0){
					decodedSymbol = 0;
				} else {
					decodedSymbol = encodedBits.readAsUnsignedInt(bitFieldWith);
                    decodedSymbol <<= (32 - bitFieldWith);
                    decodedSymbol >>= (32 - bitFieldWith);
				}
				decodedSymbols.add(decodedSymbol);
			}
		}

		return decodedSymbols;
	}

	/**
	 * Decodes the given bytes by the bitlength algorithm.
	 * @param  codecDriver Codec driver
	 * @return             Decoded symbols
	 */
	public static List<Integer> decode2(CodecDriver codecDriver){
		BitBuffer encodedBits = codecDriver.getBitBuffer();
		ArrayList<Integer> decodedSymbols = new ArrayList<Integer>();

		int expectedValues = codecDriver.getValueElementCount();
		int totalNumberOfBits = codecDriver.getCodeTextLengthInBits();

		// Handle fixed width
		if(encodedBits.readAsUnsignedInt(1) == 0){
			// Read the min and max symbols from the stream
			int numberOfBitsFromMinSymbol = encodedBits.readAsUnsignedInt(6);
			int numberOfBitsFromMaxSymbol = encodedBits.readAsUnsignedInt(6);
			int minSymbol = encodedBits.readAsSignedInt(numberOfBitsFromMinSymbol);
			int maxSymbol = encodedBits.readAsSignedInt(numberOfBitsFromMaxSymbol);

			int bitFieldWith = getBitFieldWidth(maxSymbol - minSymbol);

			// Read each fixed-width field and output the value
			while((encodedBits.getPosition() < totalNumberOfBits) || (decodedSymbols.size() < expectedValues)){
				int decodedSymbol = encodedBits.readAsUnsignedInt(bitFieldWith);
				decodedSymbol += minSymbol;
				decodedSymbols.add(decodedSymbol);
			}

		// Handle variable width
		} else {
			// Write out the mean value
			int iMean = encodedBits.readAsSignedInt(32);
			int cBlkValBits = encodedBits.readAsUnsignedInt(3);
			int cBlkLenBits = encodedBits.readAsUnsignedInt(3);

			// Set the initial field-width
			int cMaxFieldDecr = -(1 << (cBlkValBits - 1));		// -ve number
			int cMaxFieldIncr = (1 << (cBlkValBits - 1)) - 1;	// +ve number
			int cCurFieldWidth = 0;
			int cDeltaFieldWidth;
			int cRunLen;

			for(int i = 0 ; i < expectedValues;){
				// Adjust the current field width to the target field width
				do {
					cDeltaFieldWidth = encodedBits.readAsSignedInt(cBlkValBits);
					cCurFieldWidth += cDeltaFieldWidth;
				} while((cDeltaFieldWidth == cMaxFieldDecr) || (cDeltaFieldWidth == cMaxFieldIncr));

				// Read in the run length
				cRunLen = encodedBits.readAsUnsignedInt(cBlkLenBits);

				// Read in the data bits for the run
				for(int j = i ; j < i + cRunLen ; j++){
					decodedSymbols.add(encodedBits.readAsSignedInt(cCurFieldWidth) + iMean);
				}

				// Advance to the end of the run
				i += cRunLen;
			}
		}

		if((encodedBits.getPosition() != totalNumberOfBits) || (decodedSymbols.size() != expectedValues)){
			throw new IllegalArgumentException("BithlengthCodec2 didn't consume all bits!");
		}
		return decodedSymbols;
	}

	/**
	 * Returns the bit field width, which has to be used.
	 * @param  symbol Symbol
	 * @return        Bit field width
	 */
	private static int getBitFieldWidth(int symbol){
		// Note: This calculation assumes that iSymbol is positive!
		symbol = Math.abs(symbol);

		// Zero is the only number that can be encoded in a single bit with this scheme, so this
		// method returns 0 bits for a symbol value of zero!
		if(symbol == 0){
			return 0;
		}

		int i;
		int bitFieldWidth;
		for(i = 1, bitFieldWidth = 0; (i <= symbol) && (bitFieldWidth < 31); i += i, bitFieldWidth++);
		return bitFieldWidth;
	}
}
