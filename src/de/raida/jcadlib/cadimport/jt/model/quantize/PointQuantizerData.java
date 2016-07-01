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

import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.4 Point Quantizer Data</h>
 * Object Type ID: <code>---</code>
 * <br>A Point Quantizer Data collection is made up of three Uniform Quantizer
 * Data collections; there is a separate Uniform Quantizer Data collection for
 * the X, Y, and Z values of point coordinates.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PointQuantizerData {
	/** X uniform quantizer data */
	private UniformQuantizerData _xUniformQuantizerData;

	/** Y uniform quantizer data */
	private UniformQuantizerData _yUniformQuantizerData;

	/** Z uniform quantizer data */
	private UniformQuantizerData _zUniformQuantizerData;

	/**
	 * Constructor.
	 * @param xUniformQuantizerData X uniform quantizer data
	 * @param yUniformQuantizerData Y uniform quantizer data
	 * @param zUniformQuantizerData Z uniform quantizer data
	 */
	public PointQuantizerData(UniformQuantizerData xUniformQuantizerData, UniformQuantizerData yUniformQuantizerData, UniformQuantizerData zUniformQuantizerData){
		_xUniformQuantizerData = xUniformQuantizerData;
		_yUniformQuantizerData = yUniformQuantizerData;
		_zUniformQuantizerData = zUniformQuantizerData;
	}

	/**
	 * Returns the quantized range for the x coordinates.
	 * @return Quantized range for the x coordinates
	 */
	public float[] getXRange(){
		return new float[]{_xUniformQuantizerData.getMin(), _xUniformQuantizerData.getMax()};
	}

	/**
	 * Returns the quantized range for the y coordinates.
	 * @return Quantized range for the y coordinates
	 */
	public float[] getYRange(){
		return new float[]{_yUniformQuantizerData.getMin(), _yUniformQuantizerData.getMax()};
	}

	/**
	 * Returns the quantized range for the z coordinates.
	 * @return Quantized range for the z coordinates
	 */
	public float[] getZRange(){
		return new float[]{_zUniformQuantizerData.getMin(), _zUniformQuantizerData.getMax()};
	}

	/**
	 * Number of bits.
	 * @return Number of bits
	 */
	public int getNumberOfBits(){
		int numberOfBitsX = _xUniformQuantizerData.getNumberOfBits();
		int numberOfBitsY = _yUniformQuantizerData.getNumberOfBits();
		int numberOfBitsZ = _zUniformQuantizerData.getNumberOfBits();

		if((numberOfBitsX != numberOfBitsY) || (numberOfBitsX != numberOfBitsZ)){
			throw new IllegalArgumentException("ERROR: Number of quantized bits differs!");
		}

		return numberOfBitsX;
	}

	/**
	 * Reads a PointQuantizerData object.
	 * @param  workingContext Working context
	 * @return                PointQuantizerData instance
	 */
	public static PointQuantizerData read(WorkingContext workingContext){
		return new PointQuantizerData(	UniformQuantizerData.read(workingContext),
										UniformQuantizerData.read(workingContext),
										UniformQuantizerData.read(workingContext));
	}
}
