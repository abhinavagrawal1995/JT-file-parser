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

/**
 * Predictor types.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public enum PredictorType {
	/** Predictor type 'Lag1' */
	PredLag1(0),

	/** Predictor type 'Lag2' */
	PredLag2(1),

	/** Predictor type 'Stride1' */
	PredStride1(2),

	/** Predictor type 'Stride2' */
	PredStride2(3),

	/** Predictor type 'StripIndex' */
	PredStripIndex(4),

	/** Predictor type 'Ramp' */
	PredRamp(5),

	/** Predictor type 'Xor1' */
	PredXor1(6),

	/** Predictor type 'Xor2' */
	PredXor2(7),

	/** Predictor type 'NULL' */
	PredNULL(8);

	/**
	 * Constructor.
	 * @param type Predictor type
	 */
	private PredictorType(int type){
	}
}
