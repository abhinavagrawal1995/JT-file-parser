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

package de.raida.jcadlib.cadimport.jt.codec.deering;

/**
 * Class for a deering lookup entry.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class DeeringLookupEntry {
	/** cosTheta */
	private double _cosTheta;

	/** sinTheta */
	private double _sinTheta;

	/** cosPsi */
	private double _cosPsi;

	/** sinPsi */
	private double _sinPsi;

	/**
	 * Constructor.
	 * @param cosTheta cosTheta
	 * @param sinTheta sinTheta
	 * @param cosPsi   cosPsi
	 * @param sinPsi   sinPsi
	 */
	public DeeringLookupEntry(double cosTheta, double sinTheta, double cosPsi, double sinPsi){
		_cosTheta = cosTheta;
		_sinTheta = sinTheta;
		_cosPsi = cosPsi;
		_sinPsi = sinPsi;
	}

	/**
	 * Returns the CosTheta.
	 * @return CosTheta
	 */
	public double getCosTheta(){
		return _cosTheta;
	}

	/**
	 * Returns the SinTheta.
	 * @return SinTheta
	 */
	public double getSinTheta(){
		return _sinTheta;
	}

	/**
	 * Returns the CosPsi.
	 * @return CosPsi
	 */
	public double getCosPsi(){
		return _cosPsi;
	}

	/**
	 * Returns the SinPsi.
	 * @return SinPsi
	 */
	public double getSinPsi(){
		return _sinPsi;
	}
}
