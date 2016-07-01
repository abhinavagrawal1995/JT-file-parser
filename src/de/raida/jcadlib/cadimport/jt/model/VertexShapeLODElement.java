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

import java.util.List;

import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.2 Vertex Shape LOD Element</h>
 * Object Type ID: <code>0x10dd10b0, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>Vertex Shape LOD Element represents LODs defined by collections of vertices.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class VertexShapeLODElement {
	/** Object type ID */
	public final static String ID = "10dd10b0-2ac8-11d1-9b-6b-00-80-c7-bb-59-97";

	/** Vertex shape LOD data */
	private VertexShapeLODData _vertexShapeLODData;

	/**
	 * Constructor.
	 * @param baseShapeLODData   Base shape LOD data
	 * @param vertexShapeLODData Vertex shape LOD data
	 */
	public VertexShapeLODElement(BaseShapeLODData baseShapeLODData, VertexShapeLODData vertexShapeLODData){
		_vertexShapeLODData = vertexShapeLODData;
	}

	/**
	 * Returns the indices.
	 * @return Indices (vertex and normal)
	 */
	public List<List<Integer>> getIndices(){
		TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1 = getTopoMeshCompressedRepDataV1();
		if(topoMeshCompressedRepDataV1 != null){
			throw new RuntimeException("Unimplemented code block found!");
			//return topoMeshCompressedRepDataV1.getCompressedVertexNormalArray().getNormals();
			//return null;
		} else {
			TopologicallyCompressedRepData topologicallyCompressedRepData = getTopologicallyCompressedRepData();
			if(topologicallyCompressedRepData != null){
				return topologicallyCompressedRepData.getIndices();
			}
		}
		
		return null;
	}

	/**
	 * Returns the geometry vertices (without texture coordinates, normals, ...).
	 * @return Geometry vertices
	 */
	public List<Double> getVertices(){
		TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1 = getTopoMeshCompressedRepDataV1();
		if(topoMeshCompressedRepDataV1 != null){
			return topoMeshCompressedRepDataV1.getCompressedVertexCoordinateArray().getVertices();
	
		} else {
			TopologicallyCompressedVertexRecords topologicallyCompressedVertexRecords = getTopologicallyCompressedVertexRecords();
			if(topologicallyCompressedVertexRecords != null){
				return topologicallyCompressedVertexRecords.getCompressedVertexCoordinateArray().getVertices();
			}
		}
	
		return null;
	}

	/**
	 * Returns the normals.
	 * @return Normals
	 */
	public List<Double> getNormals(){
		TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1 = getTopoMeshCompressedRepDataV1();
		if(topoMeshCompressedRepDataV1 != null){
			return topoMeshCompressedRepDataV1.getCompressedVertexNormalArray().getNormals();
	
		} else {
			TopologicallyCompressedVertexRecords topologicallyCompressedVertexRecords = getTopologicallyCompressedVertexRecords();
			if(topologicallyCompressedVertexRecords != null){
				return topologicallyCompressedVertexRecords.getCompressedVertexNormalArray().getNormals();
			}
		}
	
		return null;
	}

	/**
	 * Returns the colors.
	 * @return Colors
	 */
	public List<Double> getColors(){
		TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1 = getTopoMeshCompressedRepDataV1();
		if(topoMeshCompressedRepDataV1 != null){
			return topoMeshCompressedRepDataV1.getCompressedVertexColorArray().getColors();
	
		} else {
			TopologicallyCompressedVertexRecords topologicallyCompressedVertexRecords = getTopologicallyCompressedVertexRecords();
			if(topologicallyCompressedVertexRecords != null){
				CompressedVertexColorArray compressedVertexColorArray = topologicallyCompressedVertexRecords.getCompressedVertexColorArray();
				if(compressedVertexColorArray != null){
					return compressedVertexColorArray.getColors();
				}
			}
		}
	
		return null;
	}

	/**
	 * Returns the TopoMeshCompressedRepDataV1.
	 * @return TopoMeshCompressedRepDataV1
	 */
	private TopoMeshCompressedRepDataV1 getTopoMeshCompressedRepDataV1(){
		TopoMeshCompressedLODData topoMeshCompressedLODData = _vertexShapeLODData.getTopoMeshCompressedLODData();
		if(topoMeshCompressedLODData == null){
			return null;
		}

		TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1 = topoMeshCompressedLODData.getTopoMeshCompressedRepDataV1();
		if(topoMeshCompressedRepDataV1 != null){
			return topoMeshCompressedRepDataV1;
		} else {
			return topoMeshCompressedLODData.getTopoMeshCompressedRepDataV2().getTopoMeshCompressedRepDataV1();
		}
	}

	/**
	 * Returns the TopoMeshCompressedRepDataV1.
	 * @return TopoMeshCompressedRepDataV1
	 */
	private TopologicallyCompressedRepData getTopologicallyCompressedRepData(){
		TopoMeshTopologicallyCompressedLODData topoMeshTopologicallyCompressedLODData = _vertexShapeLODData.getTopoMeshTopologicallyCompressedLODData();
		if(topoMeshTopologicallyCompressedLODData == null){
			return null;
		}

		return topoMeshTopologicallyCompressedLODData.getTopologicallyCompressedRepData();
	}

	/**
	 * Returns the TopologicallyCompressedVertexRecords.
	 * @return TopologicallyCompressedVertexRecords
	 */
	private TopologicallyCompressedVertexRecords getTopologicallyCompressedVertexRecords(){
		TopologicallyCompressedRepData topologicallyCompressedRepData = getTopologicallyCompressedRepData();
		if(topologicallyCompressedRepData == null){
			return null;
		}

		return topologicallyCompressedRepData.getTopologicallyCompressedVertexRecords();
	}

	/**
	 * Reads a VertexShapeLODElement object.
	 * @param  workingContext                 Working context
	 * @param  fromTriStripSetShapeLODElement Called from TriStripSetShapeLODElement?
	 * @param  fromPolyLineShape              Called from PolylineShapeElement?
	 * @param  fromPointSetShape              Called from PointSetShapeElement?
	 * @return                                VertexShapeLODElement instance
	 * @throws UnsupportedCodecException      Thrown, when an unsupported codec has been found
	 */
	public static VertexShapeLODElement read(WorkingContext workingContext, boolean fromTriStripSetShapeLODElement, boolean fromPolyLineShape, boolean fromPointSetShape) throws UnsupportedCodecException {
		return new VertexShapeLODElement(	BaseShapeLODData.read(workingContext),
											VertexShapeLODData.read(workingContext, fromTriStripSetShapeLODElement, fromPolyLineShape, fromPointSetShape));
	}
}
