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
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import de.raida.jcadlib.cadimport.jt.JTImporter;
import de.raida.jcadlib.cadimport.jt.reader.BitBuffer;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.1.1 Int32 Probability Contexts</h>
 * Object Type ID: <code>---</code>
 * <br>Int32 Probability Contexts data collection is a list of Probability Context
 * Tables. The Int32 Probability Contexts data collection is only present for
 * Huffman and Arithmetic CODEC Types. A Probability Context Table is a trimmed and
 * scaled histogram of the input values. It tallies the frequencies of the several
 * most frequently occurring values. It is central to the operation of the
 * arithmetic CODEC, and gives all the information necessary to reconstruct the
 * Huffman codes for the Huffman CODEC.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class Int32ProbabilityContexts {
	/** List of Int32ProbabilityContextTableEntry objects */
	private ArrayList<Int32ProbabilityContextTableEntry>[] _int32ProbabilityContextTableEntries;

	/**
	 * Constructor.
	 * @param int32ProbabilityContextTableEntries List of Int32ProbabilityContextTableEntry objects
	 */
	public Int32ProbabilityContexts(ArrayList<Int32ProbabilityContextTableEntry>[] int32ProbabilityContextTableEntries){
		_int32ProbabilityContextTableEntries = int32ProbabilityContextTableEntries;
	}

	/**
	 * Returns the number of probability context tables.
	 * @return Number of probability context tables
	 */
	public int getProbabilityContextCount(){
		return _int32ProbabilityContextTableEntries.length;
	}

	/**
	 * Returns the number of probability context tables.
	 * @param  context Context index
	 * @return         Number of probability context tables
	 */
	public int getProbabilityContextEntryCount(int context){
		return _int32ProbabilityContextTableEntries[context].size();
	}

	/**
	 * Returns the requested probability context.
	 * @param  context Context index
	 * @return         Probability context
	 */
	public ArrayList<Int32ProbabilityContextTableEntry> getProbabilityContext(int context){
		return _int32ProbabilityContextTableEntries[context];
	}

	/**
	 * Returns the requested probability context table entry.
	 * @param  context Context index
	 * @param  index   Index of the table entry
	 * @return         Probability context table entry
	 */
	public Int32ProbabilityContextTableEntry getProbabilityContextEntry(int context, int index){
		return _int32ProbabilityContextTableEntries[context].get(index);
	}

	/**
	 * Returns the number of all symbols.
	 * @param  tableIndex Table index
	 * @return            Number of all symbols
	 */
	public int getTotalSymbolCount(int tableIndex){
		int totalSymbolCount = 0;
		for(int i = 0; i < _int32ProbabilityContextTableEntries[tableIndex].size(); i++){
			Int32ProbabilityContextTableEntry int32ProbabilityContextTableEntry = _int32ProbabilityContextTableEntries[tableIndex].get(0);
			totalSymbolCount += int32ProbabilityContextTableEntry.getOccurrenceCount();
		}
		return totalSymbolCount;
	}

	/**
	 * Reads a Int32ProbabilityContexts object.
	 * @param  workingContext Working context
	 * @return                Int32ProbabilityContexts instance
	 */
	@SuppressWarnings("unchecked")
	public static Int32ProbabilityContexts read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		if(workingContext.getJTFileVersion() < 9.0){
			HashMap<Integer, Integer> symbol2AssociatedValueMap = new HashMap<Integer, Integer>();

			int probabilityContextTableCount = Helper.readU8(byteBuffer);
			if((probabilityContextTableCount != 1) && (probabilityContextTableCount != 2)){
				throw new IllegalArgumentException("Found invalid table count: " + probabilityContextTableCount);
			}

			int numberSymbolBits = -1;
			int numberOccurrenceCountBits = -1;
			int numberValueBits = -1;
			int numberNextContextBits = -1;
			int minimumValue = -1;

			BitBuffer bitBuffer = new BitBuffer(byteBuffer);
			bitBuffer.setPosition(byteBuffer.position() << 3);	// Fast way for "position in bytes * 8"
			bitBuffer.setByteOrder(ByteOrder.BIG_ENDIAN);

			ArrayList<Int32ProbabilityContextTableEntry>[] int32ProbabilityContextTableEntries = new ArrayList[probabilityContextTableCount];
			for(int i = 0; i < probabilityContextTableCount; i++){
				int32ProbabilityContextTableEntries[i] = new ArrayList<Int32ProbabilityContextTableEntry>();

				long probabilityContextTableEntryCount = Helper.convertSignedIntToUnsigned(bitBuffer.readAsUnsignedInt(32));

				// First run
				if(i == 0){
					numberSymbolBits = bitBuffer.readAsUnsignedInt(6);
					numberOccurrenceCountBits = bitBuffer.readAsUnsignedInt(6);
					numberValueBits = bitBuffer.readAsUnsignedInt(6);
					numberNextContextBits = bitBuffer.readAsUnsignedInt(6);
					minimumValue = bitBuffer.readAsUnsignedInt(32);

				// All other runs
				} else {
					numberSymbolBits = bitBuffer.readAsUnsignedInt(6);
					numberOccurrenceCountBits = bitBuffer.readAsUnsignedInt(6);
					numberValueBits = 0;
					numberNextContextBits = bitBuffer.readAsUnsignedInt(6);
				}

				for(int j = 0; j < probabilityContextTableEntryCount; j++){
					Int32ProbabilityContextTableEntry int32ProbabilityContextTableEntry = Int32ProbabilityContextTableEntry.read(
							bitBuffer,
							numberSymbolBits,
							numberOccurrenceCountBits,
							numberValueBits,
							numberNextContextBits,
							minimumValue);

					if(i == 0){
						symbol2AssociatedValueMap.put(int32ProbabilityContextTableEntry.getSymbol(), int32ProbabilityContextTableEntry.getAssociatedValue());
					} else {
						int32ProbabilityContextTableEntry.setAssociatedValue(symbol2AssociatedValueMap.get(int32ProbabilityContextTableEntry.getSymbol()));
					}

					int32ProbabilityContextTableEntries[i].add(int32ProbabilityContextTableEntry);
				}
			}

			// Discard alignment bits
			int bitsToSkip = (int)bitBuffer.getPosition() % 8;
			if(bitsToSkip > 0){
				bitBuffer.readAsUnsignedInt((8 - bitsToSkip));
			}

			// Update progress of bytes
			int readBytes = (int)((bitBuffer.getPosition() >> 3) - byteBuffer.position());
			JTImporter.updateProgress(readBytes);

			// Update byte buffer position
			byteBuffer.position(byteBuffer.position() + readBytes);

			return new Int32ProbabilityContexts(int32ProbabilityContextTableEntries);

		// Version 9
		} else {
			BitBuffer bitBuffer = new BitBuffer(byteBuffer);
			bitBuffer.setPosition(byteBuffer.position() << 3);	// Fast way for "position in bytes * 8"
			bitBuffer.setByteOrder(ByteOrder.BIG_ENDIAN);

			int probabilityContextTableEntryCount = bitBuffer.readAsUnsignedInt(16);
			int numberSymbolBits = bitBuffer.readAsUnsignedInt(6);
			int numberOccurrenceCountBits = bitBuffer.readAsUnsignedInt(6);
			int numberValueBits = bitBuffer.readAsUnsignedInt(6);
			int minValue = bitBuffer.readAsUnsignedInt(32);

			ArrayList<Int32ProbabilityContextTableEntry>[] int32ProbabilityContextTableEntries = new ArrayList[1];
			for(int i = 0; i < int32ProbabilityContextTableEntries.length; i++){
				int32ProbabilityContextTableEntries[i] = new ArrayList<Int32ProbabilityContextTableEntry>();

				for(int j = 0; j < probabilityContextTableEntryCount; j++){
					Int32ProbabilityContextTableEntry int32ProbabilityContextTableEntry = Int32ProbabilityContextTableEntry.read(
							bitBuffer,
							numberSymbolBits,
							numberOccurrenceCountBits,
							numberValueBits,
							-1,
							minValue);
	
					int32ProbabilityContextTableEntries[i].add(int32ProbabilityContextTableEntry);
				}
			}

			int bitsToSkip = (int)bitBuffer.getPosition() % 8;
			if(bitsToSkip > 0){
				bitBuffer.readAsUnsignedInt((8 - bitsToSkip));
			}

			// Update progress of bytes
			int readBytes = (int)((bitBuffer.getPosition() >> 3) - byteBuffer.position());
			JTImporter.updateProgress(readBytes);

			// Update byte buffer position
			byteBuffer.position(byteBuffer.position() + readBytes);

			return new Int32ProbabilityContexts(int32ProbabilityContextTableEntries);
		}
	}
}
