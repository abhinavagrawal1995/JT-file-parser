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

package de.raida.jcadlib.cadimport.jt.model.quantize;

import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.10.2.1.1 Quantization Parameters</h>
 * Quantization Parameters specifies for each shape data type grouping (i.e.
 * Vertex, Normal, Texture Coordinates, Color) the number of quantization bits
 * used for given qualitative compression level. Although these Quantization
 * Parameters values are saved in the associated/referenced Shape LOD Element,
 * they are also saved here so that a JT File loader/reader does not have to
 * load the Shape LOD Element in order to determine the Shape quantization level.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class QuantizationParameters {
	/** Bits per vertex */
	private int _bitsPerVertex;

	/** Normal bits factor */
	private int _normalBitsFactor;

	/** Bits per texture coordinate */
	private int _bitsPerTextureCoord;

	/** Bits per color */
	private int _bitsPerColor;

	/**
	 * Constructor.
	 * @param bitsPerVertex       Bits per vertex
	 * @param normalBitsFactor    Normal bits factor
	 * @param bitsPerTextureCoord Bits per texture coordinate
	 * @param bitsPerColor        Bits per color
	 */
	public QuantizationParameters(int bitsPerVertex, int normalBitsFactor, int bitsPerTextureCoord, int bitsPerColor){
		_bitsPerVertex = bitsPerVertex;
		_normalBitsFactor = normalBitsFactor;
		_bitsPerTextureCoord = bitsPerTextureCoord;
		_bitsPerColor = bitsPerColor;
	}

	/**
	 * Returns the bits per vertex.
	 * @return Bits per vertex
	 */
	public int getBitsPerVertex(){
		return _bitsPerVertex;
	}

	/**
	 * Returns a copy of the current class.
	 * @return Copy of the current class
	 */
	public QuantizationParameters copy(){
		return new QuantizationParameters(_bitsPerVertex, _normalBitsFactor, _bitsPerTextureCoord, _bitsPerColor);
	}

	/**
	 * Reads a QuantizationParameters object.
	 * @param  workingContext Working context
	 * @return                QuantizationParameters instance
	 */
	public static QuantizationParameters read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		return new QuantizationParameters(	Helper.readU8(byteBuffer),
											Helper.readU8(byteBuffer),
											Helper.readU8(byteBuffer),
											Helper.readU8(byteBuffer));
	}
}
