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
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.CodecDriver;
import de.raida.jcadlib.cadimport.jt.codec.Int32ProbabilityContextTableEntry;
import de.raida.jcadlib.cadimport.jt.reader.BitBuffer;

/**
 * Class for decoding bytes with the huffman codec.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class HuffmanDecoder {
	/**
	 * Decodes the given bytes by the huffman algorithm.
	 * @param  codecDriver Codec driver
	 * @return             Decoded symbols
	 */
	public static List<Integer> decode(CodecDriver codecDriver){
		// Build a Huffman tree for each probability context
		ArrayList<HuffTreeNode> huffmanRootNodes = new ArrayList<HuffTreeNode>();
		int numberOfProbabilityContexts = codecDriver.getInt32ProbabilityContexts().getProbabilityContextCount();
		ArrayList<HuffCodecContext> vHuffCntx = new ArrayList<HuffCodecContext>();
		for(int i = 0; i < numberOfProbabilityContexts; i++){
			// Get the i'th probability context
			ArrayList<Int32ProbabilityContextTableEntry> probabilityContextEntries = codecDriver.getInt32ProbabilityContexts().getProbabilityContext(i);

			// Create Huffman tree from probability context
			HuffTreeNode rootNode = buildHuffmanTree(probabilityContextEntries);

			// Assign Huffman codes
			vHuffCntx.add(new HuffCodecContext());
			assignCodeToTree(rootNode, vHuffCntx.get(i));

			// Store the completed Huffman tree
			huffmanRootNodes.add(i, rootNode);
		}

		// Convert codetext to symbols
		return codeTextToSymbols(codecDriver, huffmanRootNodes);
	}

	/**
	 * Creates the huffman tree for probabilities.
	 * @param  int32ProbabilityContextTableEntries Probabilities
	 * @return                                     Root node of the huffman tree
	 */
	private static HuffTreeNode buildHuffmanTree(ArrayList<Int32ProbabilityContextTableEntry> int32ProbabilityContextTableEntries){
		HuffHeap huffHeap = new HuffHeap();

		HuffTreeNode huffTreeNode = null;

		// Initialize all the nodes and add them to the heap.
		int numberOfEntries = int32ProbabilityContextTableEntries.size();
		for(int i = 0; i < numberOfEntries; i++){
			Int32ProbabilityContextTableEntry int32ProbabilityContextTableEntry = int32ProbabilityContextTableEntries.get(i);
			huffTreeNode = new HuffTreeNode();
			huffTreeNode.getHuffCodeData().setSymbol(int32ProbabilityContextTableEntry.getSymbol());
			huffTreeNode.setSymCounts(int32ProbabilityContextTableEntry.getOccurrenceCount());
			huffTreeNode.setAssociatedValue(int32ProbabilityContextTableEntry.getAssociatedValue());

			huffTreeNode.setLeft(null);
			huffTreeNode.setRight(null);

			huffHeap.add(huffTreeNode);
		}

		HuffTreeNode newNode1 = null;
		HuffTreeNode newNode2 = null;

		while(huffHeap.size() > 1){
			// Get the two lowest-frequency nodes.
			newNode1 = huffHeap.getTop();
			newNode2 = huffHeap.getTop();

			//Combine the low-freq nodes into one node.
			huffTreeNode = new HuffTreeNode();
			huffTreeNode.getHuffCodeData().setSymbol(0xdeadbeef);
			huffTreeNode.setLeft(newNode1);
			huffTreeNode.setRight(newNode2);
			huffTreeNode.setSymCounts(newNode1.getSymCounts() + newNode2.getSymCounts());

			//Add the new node to the node list
			huffHeap.add(huffTreeNode);
		}

		// Set the root node
		return huffHeap.getTop();
	}

	/**
	 * Assigns the codes to the tree.
	 * @param huffTreeNode     Huff tree node
	 * @param huffCodecContext Huff codec context
	 */
	private static void assignCodeToTree(HuffTreeNode huffTreeNode, HuffCodecContext huffCodecContext){
		if(huffTreeNode.getLeft() != null){
			huffCodecContext.setCode((huffCodecContext.getCode() << 1) & 0xffff);
			huffCodecContext.setCode((huffCodecContext.getCode() | 1) & 0xffff);
			huffCodecContext.setLength(huffCodecContext.getLength() + 1);
			assignCodeToTree(huffTreeNode.getLeft(), huffCodecContext);
			huffCodecContext.setLength(huffCodecContext.getLength() - 1);
			huffCodecContext.setCode(huffCodecContext.getCode() >>> 1);
		}

		if(huffTreeNode.getRight() != null){
			huffCodecContext.setCode((huffCodecContext.getCode() << 1) & 0xffff);
			huffCodecContext.setLength(huffCodecContext.getLength() + 1);
			assignCodeToTree(huffTreeNode.getRight(), huffCodecContext);
			huffCodecContext.setLength(huffCodecContext.getLength() - 1);
			huffCodecContext.setCode(huffCodecContext.getCode() >>> 1);
		}

		if(huffTreeNode.getRight() != null){
			return;
		}

		// Set the code and its length for the node.
		huffTreeNode.getHuffCodeData().setBitCode(huffCodecContext.getCode());
		huffTreeNode.getHuffCodeData().setCodeLength(huffCodecContext.getLength());

		// Setup the internal symbol look-up table.
		huffCodecContext.getCodes().add(0, new HuffCodeData(huffTreeNode.getHuffCodeData().getSymbol(),
															huffTreeNode.getHuffCodeData().getBitCode(),
															huffTreeNode.getHuffCodeData().getCodeLength()));
	}

	/**
	 * Convert the code text to the symbols.
	 * @param  codecDriver   Codec driver
	 * @param  huffTreeNodes List of huff tree root nodes
	 * @return               Decoded symbols
	 */
	private static ArrayList<Integer> codeTextToSymbols(CodecDriver codecDriver, ArrayList<HuffTreeNode> huffTreeNodes){
		ArrayList<Integer> decodedSymbols = new ArrayList<Integer>();

		BitBuffer encodedBits = codecDriver.getBitBuffer();
		int outOfBandDataCounter = 0;
		List<Integer> outOfBandValues = codecDriver.getOutOfBandValues();

		for(HuffTreeNode huffTreeRootNode : huffTreeNodes){
			HuffTreeNode huffTreeNode = huffTreeRootNode;
			while(encodedBits.getPosition() < codecDriver.getCodeTextLengthInBits()){
				huffTreeNode = (encodedBits.readAsUnsignedInt(1) == 1) ? huffTreeNode.getLeft() : huffTreeNode.getRight();

				// If the node is a leaf, output a symbol and restart
				if(huffTreeNode.isLeaf()){
					int symbol = huffTreeNode.getHuffCodeData().getSymbol();
					if(symbol == -2){
						if(outOfBandDataCounter < outOfBandValues.size()){
							decodedSymbols.add(outOfBandValues.get(outOfBandDataCounter++));
						} else {
							throw new IllegalArgumentException("'Out-Of-Band' data missing!");
						}
					} else {
						decodedSymbols.add(huffTreeNode.getAssociatedValue());
					}
					huffTreeNode = huffTreeRootNode;
				}
			}
		}

		return decodedSymbols;
	}
}
