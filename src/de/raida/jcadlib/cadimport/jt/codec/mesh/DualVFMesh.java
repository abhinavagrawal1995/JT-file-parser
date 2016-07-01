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
import java.util.List;

/**
 * The DualVFMesh (Dual Vertex-Facet Mesh) is a support class paired with the topology decoder itself, and
 * represents a closed two-manifold polygon mesh. The topology decoder reconstructs the encoded dual mesh
 * into a DualVFMesh, building it one vertex and one facet at a time. When the decoder is finished, it will
 * have visited each vertex and each face of the dual mesh exactly once. DualVFMesh is not intended as a
 * work horse in-memory storage container because its way of encoding the topological connections between
 * faces and vertices is memory-intensive.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class DualVFMesh {
	/**  */
	public final static int cMBits = 64;

	/**
	 * Subscripted by atom number, the entry contains the vtx valence and points to the location in
	 * _viVtxFaceIndices of valence consecutive integers that in turn contain the indices of the incident
	 * faces in _vFaceRecs to the vtx.
	 */
	private List<VtxEnt> _vVtxEnts;

	/**
	 * Subscripted by unique vertex record number, the entry contains the face degree and points to the
	 * location in _viFaceVtxIndices of cDeg consecutive integers that in turn contain the indices of the
	 * vertices indicent upon the face, in CCW order, in _vVtxRecs.
	 */
	private List<FaceEnt> _vFaceEnts;

	/** Combined storage for all vtxs */
	private List<Integer> _viVtxFaceIndices;

	/** Combined storage for all faces */
	private List<Integer> _viFaceVtxIndices;

	/** Combined storage for all face attribute record identifiers */
	private List<Integer> _viFaceAttrIndices;

	/**  */
	private List<BitVector> _vvbAttrMasks;

	/**
	 * Constructor.
	 */
	public DualVFMesh(){
		_vVtxEnts = new ArrayList<VtxEnt>();
		_vFaceEnts = new ArrayList<FaceEnt>();
		_viVtxFaceIndices = new ArrayList<Integer>();
		_viFaceVtxIndices = new ArrayList<Integer>();
		_viFaceAttrIndices = new ArrayList<Integer>();
		_vvbAttrMasks = new ArrayList<BitVector>();
	}

	/**
	 * 
	 */
	public void clear(){
		_vVtxEnts.clear();
		_vFaceEnts.clear();
		_viVtxFaceIndices.clear();
		_viFaceVtxIndices.clear();
		_viFaceAttrIndices.clear();
	}

	/**
	 * 
	 * @return BitVector
	 */
	public BitVector newAttrMaskBitVector(){
		BitVector bitVector = new BitVector();
		_vvbAttrMasks.add(bitVector);
		return bitVector;
	}

	/**
	 * 
	 * @param  iVtx
	 * @return      Valence
	 */
	public int valence(int iVtx){
		return _vVtxEnts.get(iVtx).cVal;
	}

	/**
	 * 
	 * @param  iFace
	 * @return       Degree
	 */
	public int degree(int iFace){
		return _vFaceEnts.get(iFace).cDeg;
	}

	/**
	 * 
	 * @param  iVtx
	 * @param  iFaceSlot
	 * @return           Face
	 */
	public int face(int iVtx, int iFaceSlot){
		return _viVtxFaceIndices.get((_vVtxEnts.get(iVtx)).iVFI + iFaceSlot);
	}

	/**
	 * 
	 * @param  iFace
	 * @param  iVtxSlot
	 * @return          Vertex
	 */
	public int vtx(int iFace, int iVtxSlot){
		return _viFaceVtxIndices.get(_vFaceEnts.get(iFace).iFVI + iVtxSlot);
	}

	/**
	 * 
	 * @return Number of vertices
	 */
	public int numVts(){
		return _vVtxEnts.size();
	}

	/**
	 * 
	 * @return Number of faces
	 */
	public int numFaces(){
		return _vFaceEnts.size();
	}

	/**
	 * 
	 * @return Number of attributes
	 */
	public int numAttrs(){
		return _viFaceAttrIndices.size();
	}

	/**
	 * 
	 * @param  iFace
	 * @return       Empty face slots
	 */
	public int emptyFaceSlots(int iFace){
		return _vFaceEnts.get(iFace).cEmptyDeg;
	}

	/**
	 * 
	 * @param  iVtx
	 * @param  iValence
	 * @param  uFlags
	 * @return          New vertex
	 */
	public boolean newVtx(int iVtx, int iValence, int uFlags){
		VtxEnt rFE = new VtxEnt();
		_vVtxEnts.add(rFE);
		if(rFE.cVal != iValence){
			rFE.cVal = iValence;
			rFE.uFlags = uFlags;
			rFE.iVFI = _viVtxFaceIndices.size();

			while((rFE.iVFI + iValence) > _viVtxFaceIndices.size()){
				_viVtxFaceIndices.add(0);
			}

			for(int i = rFE.iVFI; i < (rFE.iVFI + iValence); i++){
				_viVtxFaceIndices.set(i, -1);
			}
		}
		return true;
	}

	/**
	 * 
	 * @param iVtx
	 * @param iVGrp
	 */
	public void setVtxGrp(int iVtx, int iVGrp){
		VtxEnt rFE = _vVtxEnts.get(iVtx);
		rFE.iVGrp = iVGrp;
	}

	/**
	 * 
	 * @param iVtx
	 * @param uFlags
	 */
	public void setVtxFlags(int iVtx,int uFlags){
		VtxEnt rFE = _vVtxEnts.get(iVtx);
		rFE.uFlags = uFlags;
	}

	/**
	 * 
	 * @param  iVtx
	 * @return      ---
	 */
	public int vtxGrp(int iVtx){
		int u = -1;
		if((iVtx >= 0) && (iVtx < _vVtxEnts.size())){
			VtxEnt rFE = _vVtxEnts.get(iVtx);
			u = rFE.iVGrp;
		}
		return u;
	}

	/**
	 * 
	 * @param  iFace
	 * @return       Is the face valid
	 */
	public boolean isValidFace(int iFace){
		boolean bRet = false;
		if(iFace >= 0 && iFace < _vFaceEnts.size()){
			FaceEnt rVE = _vFaceEnts.get(iFace);
			bRet = (rVE.cDeg != 0);
		}
		return bRet;
	}

	/**
	 * 
	 * @param iFace
	 * @param cDegree
	 * @param cFaceAttrs
	 * @param uFaceAttrMask
	 * @param uFlags
	 */
	public void newFace(int iFace, int cDegree, int cFaceAttrs, long uFaceAttrMask, int uFlags){
		FaceEnt rVE = new FaceEnt();
		_vFaceEnts.add(rVE);
		if(rVE.cDeg != cDegree){
			rVE.cDeg = cDegree;
			rVE.cEmptyDeg = cDegree;
			rVE.cFaceAttrs = cFaceAttrs;
			rVE.uFlags = uFlags;
			rVE.uAttrMask = uFaceAttrMask;
			rVE.iFVI = _viFaceVtxIndices.size();
			rVE.iFAI = _viFaceAttrIndices.size();

			while((rVE.iFVI + cDegree) > _viFaceVtxIndices.size()){
				_viFaceVtxIndices.add(0);
			}

			if(cFaceAttrs > 0){
				while((rVE.iFAI + cFaceAttrs) > _viFaceAttrIndices.size()){
					_viFaceAttrIndices.add(0);
				}
			}

			for(int i = rVE.iFVI; i < rVE.iFVI + cDegree; i++){
				_viFaceVtxIndices.set(i, -1);
			}

			for(int i = rVE.iFAI; i < rVE.iFAI + cFaceAttrs; i++){
				_viFaceAttrIndices.set(i, -1);
			}
		}
	}

	/**
	 * 
	 * @param iFace
	 * @param cDegree
	 * @param cFaceAttrs
	 * @param pvbFaceAttrMask
	 * @param uFlags
	 */
	public void newFace(int iFace, int cDegree, int cFaceAttrs, BitVector pvbFaceAttrMask, int uFlags){
		while(_vFaceEnts.size() <= iFace){
			_vFaceEnts.add(new FaceEnt());
		}
		FaceEnt rVE = _vFaceEnts.get(iFace);
		if (rVE.cDeg != cDegree) {
			rVE.cDeg = cDegree;
			rVE.cEmptyDeg = cDegree;
			rVE.cFaceAttrs = cFaceAttrs;
			rVE.uFlags = uFlags;
			rVE.pvbAttrMask = new BitVector(pvbFaceAttrMask);
			rVE.iFVI = _viFaceVtxIndices.size();
			rVE.iFAI = _viFaceAttrIndices.size();

			while((rVE.iFVI + cDegree) > _viFaceVtxIndices.size()){
				_viFaceVtxIndices.add(0);
			}

			if(cFaceAttrs > 0){
				while((rVE.iFAI + cFaceAttrs) > _viFaceAttrIndices.size()){
					_viFaceAttrIndices.add(0);
				}
			}

			for(int i = rVE.iFVI; i < (rVE.iFVI + cDegree); i++){
				_viFaceVtxIndices.set(i, -1);
			}

			for(int i = rVE.iFAI; i < (rVE.iFAI + cFaceAttrs); i++){
				_viFaceAttrIndices.set(i, -1);
			}
		}
	}

	/**
	 * 
	 * @param iFace
	 * @param iAttrSlot
	 * @param iFaceAttr
	 */
	public void setFaceAttr(int iFace, int iAttrSlot, int iFaceAttr){
		FaceEnt rVE = _vFaceEnts.get(iFace);
		_viFaceAttrIndices.set(rVE.iFAI + iAttrSlot, iFaceAttr);
	}
 
	/**
	 * Attaches VF face iFace to VF vertex iVtx in the vertex's face slot iFaceSlot.
	 * @param  iVtx
	 * @param  iFaceSlot
	 * @param  iFace
	 * @return           ---
	 */
	public boolean setVtxFace(int iVtx, int iFaceSlot, int iFace){
		VtxEnt rFE = _vVtxEnts.get(iVtx);
		_viVtxFaceIndices.set((rFE.iVFI + iFaceSlot), iFace);
		return true;
	}

	/**
	 * Attaches VF vertex iVtx to VF face iFace in the face's vertex slot iVtxSlot.
	 * @param  iFace
	 * @param  iVtxSlot
	 * @param  iVtx
	 * @return          ---
	 */
	public boolean setFaceVtx(int iFace, int iVtxSlot, int iVtx){
		FaceEnt rVE = _vFaceEnts.get(iFace);

		if(_viFaceVtxIndices.get(rVE.iFVI + iVtxSlot) != iVtx){
			rVE.cEmptyDeg -= 1;
		}

		_viFaceVtxIndices.set((rVE.iFVI + iVtxSlot), iVtx);

		return true;
	}

	/**
	 * Searches the list of incident vts to face iFace for iTargVtx and returns the vtx slot at which it is found
	 * or -1 if iTargVtx is not found.
	 * @param  iFace
	 * @param  iTargVtx
	 * @return          Vertex slot
	 */
	public int findVtxSlot(int iFace, int iTargVtx){
		FaceEnt rVE = _vFaceEnts.get(iFace);
		int cDeg = rVE.cDeg;
		int iSlot = -1;
		for(int iVtxSlot = 0; iVtxSlot < cDeg; iVtxSlot++){
			if(_viFaceVtxIndices.get(iVtxSlot + rVE.iFVI) == iTargVtx){
				iSlot = iVtxSlot;
				break;
			}
		}
		return iSlot;
	}

	/**
	 * Searches the list of incident faces to vertex iVtx for iTargFace and returns the face slot at which it is found
	 * or -1 if iTargFace is not found.
	 * @param  iVtx
	 * @param  iTargFace
	 * @return           Face slot
	 */
	public int findFaceSlot(int iVtx, int iTargFace){
		VtxEnt rFE = _vVtxEnts.get(iVtx);
		for(int iFaceSlot = 0; iFaceSlot < rFE.cVal; iFaceSlot++){
			if(_viVtxFaceIndices.get(iFaceSlot + rFE.iVFI) == iTargFace){
				return iFaceSlot;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param  iVtx
	 * @return      Vertex flags
	 */
	public int vtxFlags(int iVtx){
		int u = 0;
		if(iVtx >= 0 && iVtx < _vVtxEnts.size()){
			VtxEnt rFE = _vVtxEnts.get(iVtx);
			u = rFE.uFlags;
		}
		return u;
	}

	/**
	 * 
	 * @param  iVtx
	 * @param  iFace
	 * @return       Vertex face attribute
	 */
	public int vtxFaceAttr(int iVtx, int iFace){
		FaceEnt rVE = _vFaceEnts.get(iFace);
		if(rVE.cFaceAttrs <= 0){
			return -1;
		}

		int cDeg = rVE.cDeg;
		int iAttrSlot = -1;
		for(int iVtxSlot = 0; iVtxSlot < cDeg; iVtxSlot++){
			int iSlot = iVtxSlot;
			if(cDeg <= DualVFMesh.cMBits){
				if((rVE.uAttrMask & ((long)1 << iSlot)) != 0){
					iAttrSlot++;
				}
			} else {
				if(rVE.pvbAttrMask.test(iSlot)){
					iAttrSlot++;
				}
			}

			while(iAttrSlot < 0){
				iAttrSlot += rVE.cFaceAttrs;
			}

			if (_viFaceVtxIndices.get(rVE.iFVI + iVtxSlot) == iVtx){
				return _viFaceAttrIndices.get(rVE.iFAI + (iAttrSlot % rVE.cFaceAttrs));
			}
		}

		return -1;
	}

	/**
	 * 
	 * @param  index
	 * @return      FaceEnt
	 */
	public FaceEnt getFaceEnt(int index){
		return _vFaceEnts.get(index);
	}

	/**
	 * 
	 * @return ---
	 */
	public int getVVBAttrMasksSize(){
		return _vvbAttrMasks.size();
	}
}
