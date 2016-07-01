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

/**
 * Class for representing a face entity.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class FaceEnt {
	/** Face degree */
	public int cDeg;

	/** Empty degrees (opt for emptyFaceSlots()) */
	public int cEmptyDeg;

	/** Idx into _viFaceVtxIndices of cDeg incident vts */
	public int iFVI;

	/** Idx into _viFaceAttrIndices of cAttr attributes */
	public int iFAI;

	/** Number of face attributes */
	public int cFaceAttrs;

	/** User flags */
	public int uFlags;

	/** Degree-ring attr mask as a UInt64 */
	public long uAttrMask;

	/** Degree-ring attr mask as a BitVec */
	public BitVector pvbAttrMask;

	/**
	 * Constructor.
	 */
	public FaceEnt(){
		cDeg = 0;
		uFlags = 0;
		cEmptyDeg = 0;
		cFaceAttrs = 0;
		iFVI = -1;
		iFAI = -1;
		uAttrMask = 0;
	}
}
