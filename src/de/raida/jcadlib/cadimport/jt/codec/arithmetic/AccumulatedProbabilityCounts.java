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
import java.util.TreeMap;

import de.raida.jcadlib.cadimport.jt.codec.Int32ProbabilityContextTableEntry;
import de.raida.jcadlib.cadimport.jt.codec.Int32ProbabilityContexts;

/**
 * Accumulated probabilities.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class AccumulatedProbabilityCounts {
	/** Probability contexts */
	private Int32ProbabilityContexts _int32ProbabilityContexts;

	/** Symbols counts */
	private ArrayList<Integer> _symbolsCounts;

	/** Accumulated occurence counts */
	private ArrayList<TreeMap<Integer, Integer>> _entryByAccumCountPerContext;

	/**
	 * Constructor.
	 * @param int32ProbabilityContexts Probability contexts
	 */
	public AccumulatedProbabilityCounts(Int32ProbabilityContexts int32ProbabilityContexts){
		_int32ProbabilityContexts = int32ProbabilityContexts;
		_symbolsCounts = new ArrayList<Integer>();
		_entryByAccumCountPerContext = new ArrayList<TreeMap<Integer,Integer>>();

		for(int i = 0 ; i < _int32ProbabilityContexts.getProbabilityContextCount(); i++){
			int accumulatedCount = 0;
			_entryByAccumCountPerContext.add(new TreeMap<Integer, Integer>());
			for(int j = 0 ; j < _int32ProbabilityContexts.getProbabilityContextEntryCount(i); j++){
				Int32ProbabilityContextTableEntry int32ProbabilityContextTableEntry = _int32ProbabilityContexts.getProbabilityContextEntry(i, j);
				accumulatedCount += int32ProbabilityContextTableEntry.getOccurrenceCount();
				_entryByAccumCountPerContext.get(i).put((accumulatedCount - 1), j);
			}
			_symbolsCounts.add(accumulatedCount);
		}
	}

	/**
	 * Returns the entry and symbol range.
	 * @param  contextIndex   Context index
	 * @param  rescaledCode   Rescaled code
	 * @param  newSymbolRange New symbol range
	 * @return                Matching probability context table entry
	 */
	public Int32ProbabilityContextTableEntry getEntryAndSymbolRangeByRescaledCode(int contextIndex, int rescaledCode, int[] newSymbolRange){
		TreeMap<Integer, Integer> treeMap = _entryByAccumCountPerContext.get(contextIndex);
		
		int key = treeMap.higherKey(rescaledCode - 1);
		int value = _entryByAccumCountPerContext.get(contextIndex).get(key);

		Int32ProbabilityContextTableEntry int32ProbabilityContextTableEntry = _int32ProbabilityContexts.getProbabilityContextEntry(contextIndex, value);

		newSymbolRange[0] = (key + 1 - int32ProbabilityContextTableEntry.getOccurrenceCount());
		newSymbolRange[1] = key + 1;
		newSymbolRange[2] = _symbolsCounts.get(contextIndex);

		return int32ProbabilityContextTableEntry;
	}

	/**
	 * Returns the total symbol count
	 * @param  contextIndex Context index
	 * @return              Total symbol count of the given context
	 */
	public int getTotalSymbolCount(int contextIndex){
		return _symbolsCounts.get(contextIndex);
	}
}
