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
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.2.2 TopoMesh LOD Data</h>
 * TopoMesh LOD Data collection contains the common items to all TopoMesh LOD elements.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TopoMeshLODData {
	/**
	 * Constructor.
	 * @param versionNumber         Version number
	 * @param vertexRecordsObjectID Vertex Records Object ID
	 */
	public TopoMeshLODData(int versionNumber, int vertexRecordsObjectID){
	}

	/**
	 * Reads a TopoMeshLODData object.
	 * @param  workingContext Working context
	 * @return                TopoMeshLODData instance
	 */
	public static TopoMeshLODData read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int versionNumber = Helper.readI16(byteBuffer);
		if((versionNumber != 1) && (versionNumber != 2)){
			throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
		}

		int vertexRecordsObjectID = Helper.readI32(byteBuffer);

		return new TopoMeshLODData(	versionNumber,
									vertexRecordsObjectID);
	}
}
