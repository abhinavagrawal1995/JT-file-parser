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
 * HuffTreeNode is a helper class used in the construction of the Huffman
 * tree. It contains the symbol, its frequency, the Huffman code and its
 * length, and pointers to the 'left' and 'right' nodes.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class HuffTreeNode {
	/** Symbol count */
	private int _symbolCount;

	/** Left child node */
	private HuffTreeNode _leftChildNode;

	/** Right child node */
	private HuffTreeNode _rightChildNode;

	/** Huff code data */
	private HuffCodeData _huffCodeData;

	/** Associated value */
	private int _associatedValue;

	/**
	 * Constructor.
	 */
	public HuffTreeNode(){
		_huffCodeData = new HuffCodeData();
		_symbolCount = 0;
		_associatedValue = 0;
	}

	/**
	 * Checks, whether the current node is lesser than the given node.
	 * @param  huffTreeNode Huff tree node to compare to
	 * @return              Is the current node lesser than the given node?
	 */
	public boolean isLesser(HuffTreeNode huffTreeNode){
		if(_symbolCount < huffTreeNode.getSymCounts()){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the symbol count.
	 * @return Symbol count
	 */
	public int getSymCounts(){
		return _symbolCount;
	}

	/**
	 * Sets the symbol count.
	 * @param symbolCount Symbol count
	 */
	public void setSymCounts(int symbolCount){
		_symbolCount = symbolCount;
	}

	/**
	 * Sets the left child node.
	 * @param leftChildNode Left child node
	 */
	public void setLeft(HuffTreeNode leftChildNode){
		_leftChildNode = leftChildNode;
	}

	/**
	 * Returns the left child node.
	 * @return Left child node
	 */
	public HuffTreeNode getLeft(){
		return _leftChildNode;
	}

	/**
	 * Sets the right child node.
	 * @param rightChildNode Right child node
	 */
	public void setRight(HuffTreeNode rightChildNode){
		_rightChildNode = rightChildNode;
	}

	/**
	 * Returns the right child node.
	 * @return Right child node
	 */
	public HuffTreeNode getRight(){
		return _rightChildNode;
	}

	/**
	 * Returns a flag, telling whether this node is a leaf node.
	 * @return Is this node a leaf node?
	 */
	public boolean isLeaf() {
        return ((_leftChildNode == null) && (_rightChildNode == null));
    }

	/**
	 * Returns the huff code data.
	 * @return Huff code data
	 */
	public HuffCodeData getHuffCodeData(){
		return _huffCodeData;
	}

	/**
	 * Sets the associated value.
	 * @param associatedValue Associated value
	 */
	public void setAssociatedValue(int associatedValue){
		_associatedValue = associatedValue;
	}

	/**
	 * Returns the associated value.
	 * @return Associated value
	 */
	public int getAssociatedValue(){
		return _associatedValue;
	}
}
