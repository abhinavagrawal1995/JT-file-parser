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
 * <h>7.2.2.1.2.4 TopoMesh Topologically Compressed LOD Data</h>
 * Object Type ID: <code>---</code>
 * <br>TopoMesh Topologically Compressed LOD Data collection contains the common items to all TopoMesh
 * Topologically Compressed LOD data elements.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TopoMeshTopologicallyCompressedLODData {
	/** Topologically compressed rep data */
	private TopologicallyCompressedRepData _topologicallyCompressedRepData;

	/**
	 * Constructor.
	 * @param topoMeshLODData                Topomesh LOD data
	 * @param versionNumber                  Version number
	 * @param topologicallyCompressedRepData Topologically compressed rep data
	 */
	public TopoMeshTopologicallyCompressedLODData(TopoMeshLODData topoMeshLODData, int versionNumber, TopologicallyCompressedRepData topologicallyCompressedRepData){
		_topologicallyCompressedRepData = topologicallyCompressedRepData;
	}

	/**
	 * Returns the TopologicallyCompressedRepData
	 * @return TopologicallyCompressedRepData
	 */
	public TopologicallyCompressedRepData getTopologicallyCompressedRepData(){
		return _topologicallyCompressedRepData;
	}

	/**
	 * Reads a TopoMeshTopologicallyCompressedLODData object.
	 * @param  workingContext            Working context
	 * @param  fromPolyLineShape         Called from PolylineShapeElement?
	 * @return                           TopoMeshTopologicallyCompressedLODData instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static TopoMeshTopologicallyCompressedLODData read(WorkingContext workingContext, boolean fromPolyLineShape) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		TopoMeshLODData topoMeshLODData = TopoMeshLODData.read(workingContext);

		int versionNumber = Helper.readI16(byteBuffer);
		if((versionNumber != 1) && (versionNumber != 2)){
			throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
		}

		TopologicallyCompressedRepData topologicallyCompressedRepData = TopologicallyCompressedRepData.read(workingContext);

		return new TopoMeshTopologicallyCompressedLODData(topoMeshLODData, versionNumber, topologicallyCompressedRepData);
	}
}