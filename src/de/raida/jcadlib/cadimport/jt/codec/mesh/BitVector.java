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

package de.raida.jcadlib.cadimport.jt.codec.mesh;

import java.util.ArrayList;

/**
 * Class BitVector.<br>
 * Vector that operates on individual bits in an unsigned integer vector, where as the ith bit can
 * be referenced by word(i>>cBitsLog2) and bit(i&0x1F)
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class BitVector extends ArrayList<Long> {
	/** ID of the serializable class */
	private final static long serialVersionUID = 73647484L;

	/** Number of bits per word */
	public final static int cBitsLog2 = 5;

	/** 2^n Bits */
	public final static int cWordBits = 32;

	/**
	 * Constructor.
	 */
	public BitVector(){
	}

	/**
	 * Constructor.
	 * @param bitVector BitVector
	 */
	public BitVector(BitVector bitVector){
	}

	/**
	 * Sets the length of the vector (not implemented).
	 * @param length Length of vector
	 */
	public void setLength(int length){
		for(int i = 0; i < length; i++){
			add(0L);
		}
	}

	/**
	 * Test whether the bit at the given index is set.
	 * @param  pos Index of the requested bit
	 * @return     Is the bit set?
	 */
	public boolean test(int pos){
		int vpos = pos >> cBitsLog2;
		if(vpos < size()){
			long value1 = get(pos >> cBitsLog2);
			long value2 = (long)1 << (pos % cWordBits);
			boolean result = (0 != (value1 & value2));
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Sets the bit at the given index.
	 * @param pos Index of the bit to set
	 */
	public void set(int pos){
		int vpos = pos >> cBitsLog2;
		if(vpos >=  size()){
			while(size() < (vpos + 1)){
				super.add(0L);
			}
		}

		long oldValue = get(vpos);
		long newValue = ((long)1 << ((pos % cWordBits)));
		set(vpos, (oldValue | newValue));
	}
}
