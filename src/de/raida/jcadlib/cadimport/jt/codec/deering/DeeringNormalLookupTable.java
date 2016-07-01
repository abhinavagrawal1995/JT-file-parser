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
 * Class for a deering normal lookup table.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class DeeringNormalLookupTable {
	/** List of CosTheta's */
	private double[] _cosTheta;

	/** List of SinTheta's */
	private double[] _sinTheta;

	/** List of CosPsi's */
	private double[] _cosPsi;

	/** List of SinPsi's */
	private double[] _sinPsi;

	/**
	 * Constructor.
	 */
	public DeeringNormalLookupTable(){
		int tableSize = 256;
		_cosTheta = new double[tableSize + 1];
		_sinTheta = new double[tableSize + 1];
		_cosPsi = new double[tableSize + 1];
		_sinPsi = new double[tableSize + 1];

		double psiMax = 0.615479709;

		for(int i = 0; i <= tableSize; i++){
			double theta = Math.asin(Math.tan(psiMax * (tableSize - i) / tableSize));
			double psi = psiMax * (i / tableSize);
			_cosTheta[i] = Math.cos(theta);
			_sinTheta[i] = Math.sin(theta);
			_cosPsi[i] = Math.cos(psi);
			_sinPsi[i] = Math.sin(psi);
		}
	}

	/**
	 * Returns a deering lookup entry.
	 * @param  theta        Theta
	 * @param  psi          Psi
	 * @param  numberOfBits Number of bits
	 * @return              Deering lookup entry
	 */
	public DeeringLookupEntry lookupThetaPsi(long theta, long psi, long numberOfBits){
		long offset = 8 - numberOfBits;
		long offTheta = (theta << offset) & 0xFFFFFFFFL;
		long offPsi = (psi << offset) & 0xFFFFFFFFL;

		return new DeeringLookupEntry(_cosTheta[(int)offTheta], _sinTheta[(int)offTheta], _cosPsi[(int)offPsi], _sinPsi[(int)offPsi]);
    }
}
