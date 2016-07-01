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
 * Special heap for the huffman codec. Original C++ code by Daniel Dammann (ddammann@gmail.com).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class HuffHeap {
	/** Internal list of nodes */
	private ArrayList<HuffTreeNode> _heap;

	/**
	 * Constructor.
	 */
	public HuffHeap(){
		_heap = new ArrayList<HuffTreeNode>();
	}

	/**
	 * Returns the size of the heap.
	 * @return Size of the heap
	 */
	public int size(){
		return _heap.size();
	}

	/**
	 * Add a new tree node in a sorted way.
	 * @param huffTreeNode Tree node to add
	 */
	public void add(HuffTreeNode huffTreeNode){
		_heap.add(huffTreeNode);

		int i = _heap.size();

		// As long, as it isn't the root (1) and parent of i (i / 2 - 1) "bigger" than the new element is ...
		while((i != 1) && (_heap.get((i / 2) - 1).getSymCounts() > huffTreeNode.getSymCounts())){
			// overwrite i with the parent of i
			_heap.set((i - 1), _heap.get((i / 2) - 1));

			// Parent of i is a new i
			i = i / 2;
		}

		// Overwrite current position (i) with the new element
		_heap.set((i - 1), huffTreeNode);
	}

	/**
	 * Remove the root node.
	 */
	private void remove(){
		if(_heap.size() == 0){
			return;
		}

		int size = _heap.size();
		HuffTreeNode y = _heap.get(size - 1);	// Re-insert the last element, because the list will be shortned by one
		int i = 1;								// i is current "parent", which shall be removed / overwritten
		int ci = 2;								// ci is current "child"
		size -= 1;								// The new size is decremented by one

		while(ci <= size){
			// Go to the left or to the right? Use the "smaller" element
			if((ci < size) && (_heap.get(ci - 1).getSymCounts() > _heap.get(ci).getSymCounts())){
				ci += 1;
			}

			// If the new "last" element already fits (it has to be smaller than the smallest
			// childs of i), than break the loop
			if(y.getSymCounts() < _heap.get(ci - 1).getSymCounts()){
				break;

			// Otherwise move the "child" up to the "parent" and continue with i at ci
			} else {
				_heap.set((i - 1), _heap.get(ci - 1));
				i = ci;
				ci *= 2;
			}
		}

		// Set "last" element to the current position i
		_heap.set((i - 1), y);

		// Resize node list by -1
		_heap.remove(_heap.size() - 1);
	}

	/**
	 * Returns the "smallest" node (root node) and decrease the heap.
	 * @return Root node ("smallest" node)
	 */
	public HuffTreeNode getTop(){
		if(_heap.size() == 0){
			return null;
		}

		HuffTreeNode huffTreeNode = _heap.get(0);
		remove();
		return huffTreeNode;
	}
}
