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
 * Wrapper class for DualVFMesh to fix the wrong method names.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class DualVFMeshWrapper {
	/**  */
	private DualVFMesh _dualVFMesh;

	/**
	 * 
	 * @param dualVFMesh
	 */
	public DualVFMeshWrapper(DualVFMesh dualVFMesh){
		_dualVFMesh = dualVFMesh;
	}

	/**
	 * 
	 * @return ---
	 */
	public int numFaces(){
		return _dualVFMesh.numVts();
	}

	/**
	 * 
	 * @return ---
	 */
	public int numVts(){
		return _dualVFMesh.numFaces();
	}

	/**
	 * 
	 * @return ---
	 */
	public int numAttrs(){
		return _dualVFMesh.numAttrs();
	}

	/**
	 * 
	 * @return ---
	 */
	public int getVVBAttrMasksSize(){
		return _dualVFMesh.getVVBAttrMasksSize();
	}

	/**
	 * 
	 * @param  index
	 * @return       ---
	 */
	public FaceEnt getFaceEnt(int index){
		return _dualVFMesh.getFaceEnt(index);
	}

	/**
	 * 
	 * @param  iFace
	 * @return       ---
	 */
	public int faceFlag(int iFace){
		return _dualVFMesh.vtxFlags(iFace);
	}

	/**
	 * 
	 * @param  iFace
	 * @return       ---
	 */
	public int faceGrp(int iFace){
		return _dualVFMesh.vtxGrp(iFace);
	}

	/**
	 * 
	 * @param  iFace
	 * @return       ---
	 */
	public int faceNumVts(int iFace){
		return _dualVFMesh.valence(iFace);
	}

	/**
	 * 
	 * @param  iFace
	 * @param  iVSlot
	 * @return        ---
	 */
	public int faceVtx(int iFace, int iVSlot){
		return _dualVFMesh.face(iFace, iVSlot);
	}

	/**
	 * 
	 * @param  iFace
	 * @param  iVtx
	 * @return       ---
	 */
	public int faceVtxAttr(int iFace, int iVtx){
		return _dualVFMesh.vtxFaceAttr(iFace, iVtx);
	}

	/**
	 * 
	 * @param  iVtx
	 * @return      ---
	 */
	public int vtxFlags(int iVtx){
		return _dualVFMesh.vtxFlags(iVtx);
	}

	/**
	 * 
	 * @param  iVtx
	 * @return      ---
	 */
	public int valence(int iVtx){
		return _dualVFMesh.valence(iVtx);
	}

	/**
	 * 
	 * @param  iVtx
	 * @param  iFaceSlot
	 * @return           ---
	 */
	public int face(int iVtx, int iFaceSlot){
		return _dualVFMesh.face(iVtx, iFaceSlot);
	}

	/**
	 * 
	 * @param  iVtx
	 * @param  iFace
	 * @return       ---
	 */
	public int vtxFaceAttr(int iVtx, int iFace){
		return _dualVFMesh.vtxFaceAttr(iVtx, iFace);
	}
}
