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

import javax.vecmath.Point3d;

/**
 * Class for decoding normals.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class DeeringNormalCodec {
	/** Number of bits */
	private long _numberOfBits;

	/** Deering normal lookup table */
	private static DeeringNormalLookupTable _deeringNormalLookupTable;

	/**
	 * Constructor.
	 * @param numberOfBits Number of bits
	 */
	public DeeringNormalCodec(long numberOfBits){
		if(_deeringNormalLookupTable == null){
			_deeringNormalLookupTable = new DeeringNormalLookupTable();
		}
		_numberOfBits = numberOfBits;
	}

	/**
	 * Converts a code to a normal vector.
	 * @param  sextant Sextant
	 * @param  octant  Octant
	 * @param  theta   Theta
	 * @param  psi     Psi
	 * @return         Normal vector
	 */
	public Point3d convertCodeToVec(long sextant, long octant, long theta, long psi){
		theta += (sextant & 1);

		DeeringLookupEntry lookupEntry = _deeringNormalLookupTable.lookupThetaPsi(theta, psi, _numberOfBits);

		double x, y, z;
		double xx = x = lookupEntry.getCosTheta() * lookupEntry.getCosPsi();
		double yy = y = lookupEntry.getSinPsi();
		double zz = z = lookupEntry.getSinTheta() * lookupEntry.getCosPsi();

		switch((int)sextant){
			case 0:
				break;
			case 1:
				z = xx;
				x = zz;
				break;
			case 2:
				z = xx;
				x = yy;
				y = zz;
				break;
			case 3:
				y = xx;
				x = yy;
				break;
			case 4:
				y = xx;
				z = yy;
				x = zz;
				break;
			case 5:
				z = yy;
				y = zz;
				break;
		}

        if((octant & 0x4) == 0){
        	x = -x;
        }

        if((octant & 0x2) == 0){
        	y = -y;
        }

        if((octant & 0x1) == 0){
        	z = -z;
        }

        return new Point3d(x, y, z);
	}
}
