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

import de.raida.jcadlib.cadimport.jt.JTImporter;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.7 Uniform Quantizer Data</h>
 * Object Type ID: <code>---</code>
 * <br>The Uniform Quantizer Data collection contains information that defines
 * a scalar quantizer/dequantizer (encoder/decoder) whose range is divided into
 * levels of equal spacing.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class UniformQuantizerData {
	/** Minimum of the quantized range */
	private float _min;

	/** Maximum of the quantized range */
	private float _max;

	/** Quantized size */
	private int _numberOfBits;

	/**
	 * Constructor.
	 * @param min          Minimum of the quantized range
	 * @param max          Maximum of the quantized range
	 * @param numberOfBits Quantized size
	 */
	public UniformQuantizerData(float min, float max, int numberOfBits){
		_min = min;
		_max = max;
		_numberOfBits = numberOfBits;
	}

	/**
	 * Minimum of quantized range.
	 * @return Minimum of quantized range
	 */
	public float getMin(){
		return _min;
	}

	/**
	 * Maximum of quantized range.
	 * @return Maximum of quantized range
	 */
	public float getMax(){
		return _max;
	}

	/**
	 * Number of bits.
	 * @return Number of bits
	 */
	public int getNumberOfBits(){
		return _numberOfBits;
	}

	/**
	 * Reads a UniformQuantizerData object.
	 * @param  workingContext Working context
	 * @return                UniformQuantizerData instance
	 */
	public static UniformQuantizerData read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		float min = Helper.readF32(byteBuffer);
		float max = Helper.readF32(byteBuffer);
		int numberOfBits = Helper.readU8(byteBuffer);
		if((numberOfBits < 0) || (numberOfBits > 32)){
			JTImporter.addLoadInformation("WARNING", "Found unexpected number of bits: " + numberOfBits);
		}

		return new UniformQuantizerData(min,
										max,
										numberOfBits);
	}
}
