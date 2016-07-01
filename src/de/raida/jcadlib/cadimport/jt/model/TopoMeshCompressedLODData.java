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

package de.raida.jcadlib.cadimport.jt.model;

import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.2.3 TopoMesh Compressed LOD Data</h>
 * Object Type ID: <code>---</code>
 * <br> TopoMesh Compressed LOD Data collection contains the common items to all
 * TopoMesh Compressed LOD data elements.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TopoMeshCompressedLODData {
	/** Topomesh compressed REP data V1 */
	private TopoMeshCompressedRepDataV1 _topoMeshCompressedRepDataV1;

	/** Topomesh compressed REP data V2 */
	private TopoMeshCompressedRepDataV2 _topoMeshCompressedRepDataV2;

	/**
	 * Constructor.
	 * @param topoMeshLODData             Topomesh LOD data
	 * @param topoMeshCompressedRepDataV1 Topomesh compressed REP data V1
	 * @param topoMeshCompressedRepDataV2 Topomesh compressed REP data V2
	 */
	public TopoMeshCompressedLODData(TopoMeshLODData topoMeshLODData, TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1, TopoMeshCompressedRepDataV2 topoMeshCompressedRepDataV2){
		_topoMeshCompressedRepDataV1 = topoMeshCompressedRepDataV1;
		_topoMeshCompressedRepDataV2 = topoMeshCompressedRepDataV2;
	}

	/**
	 * Returns the TopoMeshCompressedRepDataV1
	 * @return TopoMeshCompressedRepDataV1
	 */
	public TopoMeshCompressedRepDataV1 getTopoMeshCompressedRepDataV1(){
		return _topoMeshCompressedRepDataV1;
	}

	/**
	 * Returns the TopoMeshCompressedRepDataV2
	 * @return TopoMeshCompressedRepDataV2
	 */
	public TopoMeshCompressedRepDataV2 getTopoMeshCompressedRepDataV2(){
		return _topoMeshCompressedRepDataV2;
	}

	/**
	 * Reads a TopoMeshCompressedLODData object.
	 * @param  workingContext            Working context
	 * @param  fromPolyLineShape         Called from a PolyLineSHape instance?
	 * @return                           TopoMeshCompressedLODData instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static TopoMeshCompressedLODData read(WorkingContext workingContext, boolean fromPolyLineShape) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		TopoMeshLODData topoMeshLODData = TopoMeshLODData.read(workingContext);

		int versionNumber = Helper.readI16(byteBuffer);
		if(versionNumber == 1){
			return new TopoMeshCompressedLODData(topoMeshLODData, TopoMeshCompressedRepDataV1.read(workingContext, fromPolyLineShape), null);

		} else if(versionNumber >= 2){
			return new TopoMeshCompressedLODData(topoMeshLODData, null, TopoMeshCompressedRepDataV2.read(workingContext, fromPolyLineShape));

		} else {
			throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
		}
	}
}