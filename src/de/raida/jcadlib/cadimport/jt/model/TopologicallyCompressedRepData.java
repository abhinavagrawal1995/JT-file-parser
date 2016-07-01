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
import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP2;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.codec.mesh.MeshCoderDriver;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.2.5 Topologically Compressed Rep Data</h>
 * Object Type ID: <code>---</code>
 * <br>JT v9 represents triangle strip data very differently than it does in the JT v8 format. The new scheme stores
 * the triangles from a TriStripSet as a topologically-connected triangle mesh. Even though more information is
 * stored to the JT file, the additional structure provided by storing the full topological adjacency information
 * actually provides a handsome reduction in the number of bytes needed to encode the triangles. More importantly,
 * however, the topological information aids us in a more significant respect -- that of only storing the unique
 * vertex records used by the TriStripSet. Combined, these two effects reduce the typical storage footprint of
 * TriStripSet data by approximately half relative to the JT v8 format.
 * <br>The tristrip information itself is no longer stored in the JT file -- only the triangles themselves. The
 * reader is expected to re-tristrip (or not) as it sees fit, as tristrips may no longer provide a performance
 * advantage during rendering. There may, however, remain some memory savings for tristripping, and so the decision
 * to tristrip is left to the user.
 * <br>To begin the decoding process, first read the compressed data fields shown in Figure 89. These fields provide
 * all the information necessary to reconstruct the per face-group organized sets of triangles. The first 22 fields
 * represent the topological information, and the remaining fields constitute the set of unique vertex records to be
 * used. The next step is to run the topological decoder algorithm detailed in Appendix E: Polygon Mesh Topology
 * Coder on this data to reconstruct the topologically connected representation of the triangle mesh in a so-called
 * "dual VFMesh.' The triangles in this heavy-weight data structure can then be exported to a lighter-weight form,
 * and the dual VFMesh discarded if desired.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TopologicallyCompressedRepData {
	/** Face degree lists */
	private List<List<Integer>> _faceDegrees;

	/** Vertex valences lists */
	private List<Integer> _vertexValences;

	/** Vertex groups lists */
	private List<Integer> _vertexGroups;

	/** Vertex flags lists */
	private List<Integer> _vertexFlags;

	/** Face attribute masks lists */
	private List<List<Integer>> _faceAttributeMasks;

	/** Face attribute masks lists */
	private List<Integer> _faceAttributeMask8_30;

	/** Face attribute masks lists */
	private List<Integer> _faceAttributeMask8_4;

	/** High degree face attribute masks */
	private long[] _highDegreeFaceAttributeMasks;

	/** Split face syms lists */
	private List<Integer> _splitFaceSyms;

	/** Split face positions lists */
	private List<Integer> _splitFacePositions;

	/** Topologically compressed vertex records */
	private TopologicallyCompressedVertexRecords _topologicallyCompressedVertexRecords;

	/**
	 * Constructor.
	 * @param faceDegrees                          Face degree lists
	 * @param vertexValences                       Vertex valences lists
	 * @param vertexGroups                         Vertex groups lists
	 * @param vertexFlags                          Vertex flags lists
	 * @param faceAttributeMasks                   Face attribute masks lists
	 * @param faceAttributeMask8_30                Face attribute masks lists
	 * @param faceAttributeMask8_4                 Face attribute masks lists
	 * @param highDegreeFaceAttributeMasks         High degree face attribute masks
	 * @param splitFaceSyms                        Split face syms lists
	 * @param splitFacePositions                   Split face positions lists
	 * @param compositeHash                        Composite hash
	 * @param topologicallyCompressedVertexRecords Topologically compressed vertex records
	 */
	public TopologicallyCompressedRepData(List<List<Integer>> faceDegrees, List<Integer> vertexValences,
			List<Integer> vertexGroups, List<Integer> vertexFlags, List<List<Integer>> faceAttributeMasks,
			List<Integer> faceAttributeMask8_30, List<Integer> faceAttributeMask8_4, long[] highDegreeFaceAttributeMasks,
			List<Integer> splitFaceSyms, List<Integer> splitFacePositions, long compositeHash,
			TopologicallyCompressedVertexRecords topologicallyCompressedVertexRecords){
		_faceDegrees = faceDegrees;
		_vertexValences = vertexValences;
		_vertexGroups = vertexGroups;
		_vertexFlags = vertexFlags;
		_faceAttributeMasks = faceAttributeMasks;
		_faceAttributeMask8_30 = faceAttributeMask8_30;
		_faceAttributeMask8_4 = faceAttributeMask8_4;
		_highDegreeFaceAttributeMasks = highDegreeFaceAttributeMasks;
		_splitFaceSyms = splitFaceSyms;
		_splitFacePositions = splitFacePositions;
		_topologicallyCompressedVertexRecords = topologicallyCompressedVertexRecords;
	}

	/**
	 * Returns the indices.
	 * @return Indices (vertex and normal)
	 */
	public List<List<Integer>> getIndices(){
		MeshCoderDriver meshCoderDriver = new MeshCoderDriver();
		meshCoderDriver.setInputData(	_vertexValences, _faceDegrees, _vertexGroups, _vertexFlags,
										_faceAttributeMasks, _faceAttributeMask8_30, _faceAttributeMask8_4,
										_highDegreeFaceAttributeMasks, _splitFaceSyms, _splitFacePositions);
		return meshCoderDriver.decode();
	}

	/**
	 * Return the TopologicallyCompressedVertexRecords.
	 * @return TopologicallyCompressedVertexRecords
	 */
	public TopologicallyCompressedVertexRecords getTopologicallyCompressedVertexRecords(){
		return _topologicallyCompressedVertexRecords;
	}

	/**
	 * Reads a TopologicallyCompressedRepData object.
	 * @param  workingContext            Working context
	 * @return                           TopologicallyCompressedRepData instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static TopologicallyCompressedRepData read(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		List<List<Integer>> faceDegrees = new ArrayList<List<Integer>>();
		for(int i = 0; i < 8; i++){
			faceDegrees.add(Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL));
		}

		List<Integer> vertexValences = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
		List<Integer> vertexGroups = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
		List<Integer> vertexFlags = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);

		List<List<Integer>> faceAttributeMasks = new ArrayList<List<Integer>>();
		for(int i = 0; i < 8; i++){
			faceAttributeMasks.add(Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL));
		}

		List<Integer> faceAttributeMask8_30 = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
		List<Integer> faceAttributeMask8_4 = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);
		long[] highDegreeFaceAttributeMasks = Helper.readVecU32(byteBuffer);
		List<Integer> splitFaceSyms = Int32CDP2.readVecI32(workingContext, PredictorType.PredLag1);
		List<Integer> splitFacePositions = Int32CDP2.readVecI32(workingContext, PredictorType.PredNULL);

		long readHash = Helper.readU32(byteBuffer);

		TopologicallyCompressedVertexRecords topologicallyCompressedVertexRecords = TopologicallyCompressedVertexRecords.read(workingContext);

		return new TopologicallyCompressedRepData(faceDegrees, vertexValences, vertexGroups, vertexFlags,
				faceAttributeMasks, faceAttributeMask8_30, faceAttributeMask8_4, highDegreeFaceAttributeMasks,
				splitFaceSyms, splitFacePositions, readHash, topologicallyCompressedVertexRecords);
	}
}