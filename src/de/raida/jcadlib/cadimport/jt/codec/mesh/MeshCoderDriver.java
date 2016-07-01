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
 * This class serves as a coordinating driver for mesh coding and decoding.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class MeshCoderDriver {
	/**  */
	private List<Integer> _vviOutValSyms;

	/**  */
	private List<List<Integer>> _viOutDegSyms;

	/**  */
	private List<Integer> _viOutFGrpSyms;

	/**  */
	private List<Integer> _vuOutFaceFlags;

	/**  */
	private List<List<Integer>> _vvuOutAttrMasks;

	/**  */
	private List<Integer> _faceAttributeMask8_30;

	/**  */
	private List<Integer> _faceAttributeMask8_4;

	/**  */
	private long[] _vuOutAttrMasksLrg;

	/**  */
	private List<Integer> _viOutSplitVtxSyms;

	/**  */
	private List<Integer> _viOutSplitPosSyms;

	/**  */
	private int[] _iValReadPos;

	/**  */
	private int _iDegReadPos;

	/**  */
	private int _iVGrpReadPos;

	/**  */
	private int _iFFlagReadPos;

	/**  */
	private int[] _iAttrMaskReadPos;

	/**  */
	private int _iAttrMaskLrgReadPos;

	/**  */
	private int _iSplitFaceReadPos;

	/**  */
	private int _iSplitPosReadPos;

	/**  */
	private MeshDecoder _pMeshDecoder;

	/**
	 * Constructor.
	 */
	public MeshCoderDriver(){
	}

	/**
	 * 
	 * @param vviOutValSyms
	 * @param viOutDegSyms
	 * @param viOutFGrpSyms
	 * @param vuOutFaceFlags
	 * @param vvuOutAttrMasks
	 * @param faceAttributeMask8_30
	 * @param faceAttributeMask8_4
	 * @param vuOutAttrMasksLrg
	 * @param viOutSplitVtxSyms
	 * @param viOutSplitPosSyms
	 */
	public void setInputData(List<Integer> vviOutValSyms, List<List<Integer>> viOutDegSyms,
			List<Integer> viOutFGrpSyms, List<Integer> vuOutFaceFlags, List<List<Integer>> vvuOutAttrMasks,
			List<Integer> faceAttributeMask8_30, List<Integer> faceAttributeMask8_4,
			long[] vuOutAttrMasksLrg, List<Integer> viOutSplitVtxSyms, List<Integer> viOutSplitPosSyms){
		_vviOutValSyms = vviOutValSyms;
		_viOutDegSyms = viOutDegSyms;
		_viOutFGrpSyms = viOutFGrpSyms;
		_vuOutFaceFlags = vuOutFaceFlags;
		_vvuOutAttrMasks = vvuOutAttrMasks;
		_faceAttributeMask8_30 = faceAttributeMask8_30;
		_faceAttributeMask8_4 = faceAttributeMask8_4;
		_vuOutAttrMasksLrg = vuOutAttrMasksLrg;
		_viOutSplitVtxSyms = viOutSplitVtxSyms;
		_viOutSplitPosSyms = viOutSplitPosSyms;
	}

	/**
	 * Decodes the mesh.
	 * @return List of lists: vertex and normal indices
	 */
	public List<List<Integer>> decode(){
		// Allocate a coder
		if(_pMeshDecoder == null){
			_pMeshDecoder = new MeshDecoder(this);
		}

		// Reset the symbol counters
		_iValReadPos = new int[8];
		_iAttrMaskReadPos = new int[8];
		_iDegReadPos = 0;
		_iVGrpReadPos = 0;
		_iFFlagReadPos = 0;
		_iAttrMaskLrgReadPos = 0;
		_iSplitFaceReadPos = 0;
		_iSplitPosReadPos = 0;

		// Run the decoder
		_pMeshDecoder.run();

		// Assert that ALL symbols have been consumed
		for(int i = 0; i < 8; i++){
			if(	(_iValReadPos[i] != _viOutDegSyms.get(i).size()) ||
				(_iAttrMaskReadPos[i] != _vvuOutAttrMasks.get(i).size())){
				throw new IllegalArgumentException("ERROR: Not all symbols have been consumed!");
			}
		}

		if(	(_iDegReadPos != _vviOutValSyms.size()) ||
			(_iVGrpReadPos != _viOutFGrpSyms.size()) ||
			(_iFFlagReadPos != _vuOutFaceFlags.size()) ||
			(_iAttrMaskLrgReadPos != _vuOutAttrMasksLrg.length) ||
			(_iSplitFaceReadPos != _viOutSplitVtxSyms.size()) ||
			(_iSplitPosReadPos != _viOutSplitPosSyms.size())){
			throw new IllegalArgumentException("ERROR: Not all symbols have been consumed!");
		}

		// Set output VFMesh (wrapper)
		DualVFMeshWrapper dualVFMeshWrapper = new DualVFMeshWrapper(_pMeshDecoder.vfm());

		List<Integer> vertexIndices = new ArrayList<Integer>();
		List<Integer> normalIndices = new ArrayList<Integer>();

		int numFaces = dualVFMeshWrapper.numFaces();
		for(int iFace = 0; iFace < numFaces; iFace++){
			// Show only visible faces
			//if(dualVFMeshWrapper.vtxFlags(iFace) == 0){
			//if(dualVFMeshWrapper.faceFlag(iFace) == 0){
			if(dualVFMeshWrapper.faceGrp(iFace) >= 0){
				for(int iVSlot = 0; iVSlot < dualVFMeshWrapper.valence(iFace); iVSlot++){
					int vertexIndex = dualVFMeshWrapper.face(iFace, iVSlot);
					vertexIndices.add(vertexIndex);

					int iAttr = dualVFMeshWrapper.vtxFaceAttr(iFace, vertexIndex);
					normalIndices.add(iAttr);
				}
			}
		}

		List<List<Integer>> indexLists = new ArrayList<List<Integer>>();
		indexLists.add(vertexIndices);
		indexLists.add(normalIndices);
		return indexLists;
	}

	/**
	 * 
	 * @param  iCCntx
	 * @return        Next degree symbol
	 */
	public int _nextDegSymbol(int iCCntx){
		int eSym = -1;
		if(_iValReadPos[iCCntx] < _viOutDegSyms.get(iCCntx).size()){
			eSym = _viOutDegSyms.get(iCCntx).get(_iValReadPos[iCCntx]++);
		}
		return eSym;
	}

	/**
	 * 
	 * @return Next value symbol
	 */
	public int _nextValSymbol(){
		int eSym = -1;
		if(_iDegReadPos < _vviOutValSyms.size()){
			eSym = _vviOutValSyms.get(_iDegReadPos++);
		}
		return eSym;
	}

	/**
	 * 
	 * @return Next face group symbol
	 */
	public int _nextFGrpSymbol(){
		int eSym = -1;
		if(_iVGrpReadPos < _viOutFGrpSyms.size()){
			eSym = _viOutFGrpSyms.get(_iVGrpReadPos++);
		}
		return eSym;
	}

	/**
	 * 
	 * @return Next vertex flag symbol
	 */
	public int _nextVtxFlagSymbol(){
		int eSym = 0;
		if(_iFFlagReadPos < _vuOutFaceFlags.size()){
			eSym = _vuOutFaceFlags.get(_iFFlagReadPos++);
		}
		return eSym;
	}

	/**
	 * 
	 * @param  iCCntx
	 * @return        Next attr mask symbol
	 */
	public long _nextAttrMaskSymbol(int iCCntx){
		long eSym = 0;
		int readpos = _iAttrMaskReadPos[iCCntx];
		if (readpos < _vvuOutAttrMasks.get(iCCntx).size()){
			eSym = _vvuOutAttrMasks.get(iCCntx).get(readpos);
		}
		if(iCCntx == 7){
			eSym |= (	(((long)_faceAttributeMask8_4.get(readpos)) << 30) +
						(((long)_faceAttributeMask8_30.get(readpos)) << 30)
					);
		}
		_iAttrMaskReadPos[iCCntx]++;
		return eSym;
	}

	/**
	 * 
	 * @param iopvbAttrMask
	 * @param cDegree
	 */
	public void _nextAttrMaskSymbol(BitVector iopvbAttrMask, int cDegree){
		if(_iAttrMaskLrgReadPos < _vuOutAttrMasksLrg.length){
			iopvbAttrMask.setLength(cDegree);
			int nWords = (cDegree + BitVector.cWordBits - 1) >> BitVector.cBitsLog2;

			for(int i = 0; i < nWords; i++){
				iopvbAttrMask.set(i, _vuOutAttrMasksLrg[_iAttrMaskLrgReadPos + i]);
			}

			_iAttrMaskLrgReadPos += nWords;
		} else {
			iopvbAttrMask.setLength(0);
		}
	}

	/**
	 * 
	 * @return Split face symbol
	 */
	public int _nextSplitFaceSymbol(){
		int eSym = -1;
		if(_iSplitFaceReadPos < _viOutSplitVtxSyms.size()){
			eSym = _viOutSplitVtxSyms.get(_iSplitFaceReadPos++);
		}
		return eSym;
	}

	/**
	 * 
	 * @return Split position symbol
	 */
	public int _nextSplitPosSymbol(){
		int eSym = -1;
		if(_iSplitPosReadPos < _viOutSplitPosSyms.size()){
			eSym = _viOutSplitPosSyms.get(_iSplitPosReadPos++);
		}
		return eSym;
	}

	/**
	 * Computes a "compression context" from 0 to 7 inclusive for faces on vertex iVtx. The context
	 * is based on the vertex's valence, and the total _known_ degree of already-coded faces on the
	 * vertex at the time of the call.
	 * @param  iVtx
	 * @param  pVFM
	 * @return      Compression context
	 */
	public int _faceCntxt(int iVtx, DualVFMesh pVFM){
		// Here, we are going to gather data to be used to determine a
		// compression contest for the face degree.
		int cVal = pVFM.valence(iVtx);
		int nKnownFaces = 0;
		int cKnownTotDeg = 0;
		for(int i = 0; i < cVal; i++){
			int iTmpFace = pVFM.face(iVtx, i);
			if(!pVFM.isValidFace(iTmpFace)){
				continue;
			}
			nKnownFaces++;
			cKnownTotDeg += pVFM.degree(iTmpFace);
		}

		int iCCntxt = 0;
		if(cVal == 3){
			// Regular tristrip-like meshes tend to have degree 6 faces
			iCCntxt = (cKnownTotDeg < nKnownFaces * 6) ? 0 : (cKnownTotDeg == nKnownFaces * 6) ? 1 : 2;
		} else if(cVal == 4){
			// Regular quadstrip-like meshes tend to have degree 4 faces
			iCCntxt = (cKnownTotDeg < nKnownFaces * 4) ? 3 : (cKnownTotDeg == nKnownFaces * 4) ? 4 : 5;
		} else if(cVal == 5){
			// Pentagons are all lumped into context 6
			iCCntxt = 6;
		} else {
			// All other polygons are lumped into context 7
			iCCntxt = 7;
		}

		return iCCntxt;
	}
}
