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

import de.raida.jcadlib.cadimport.jt.model.lsg.GroupNodeData;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.2 Partition Node Element</h>
 * Object Type ID: <code>0x10dd103e, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>A Partition Node represents an external JT file reference and provides a means to partition a model
 * into multiple physical JT files (e.g. separate JT file per part in an assembly). When the referenced JT
 * file is opened, the Partition Node's children are really the children of the LSG root node for the
 * underlying JT file. Usage of Partition Nodes in LSG also aids in supporting JT file loader/reader 'best
 * practice' of late loading data (i.e. can delay opening and loading the externally referenced JT file until
 * the data is needed).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PartitionNode {
	/** Object type ID */
	public final static String ID = "10dd103e-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Untransformed bounding box */
	float[][] _untransformedBoundingBox;

	/**
	 * Constructor.
	 * @param groupNodeData            Group node data
	 * @param partitionFlags           Partition flags
	 * @param fileName                 Name of referenced file
	 * @param boundingBox              Bounding box
	 * @param area                     Area
	 * @param vertexCountRange         Vertex count range
	 * @param nodeCountRange           Node count range
	 * @param polygonCountRange        Polygon count range
	 * @param untransformedBoundingBox Bounding box (untransformed)
	 */
	public PartitionNode(GroupNodeData groupNodeData, int partitionFlags, String fileName, float[][] boundingBox, float area, int[] vertexCountRange, int[] nodeCountRange, int[] polygonCountRange, float[][] untransformedBoundingBox){
		_untransformedBoundingBox = untransformedBoundingBox;
	}

	/**
	 * Reads a PartitionNode object.
	 * @param  workingContext Working context
	 * @return                PartitionNode instance
	 */
	public static PartitionNode read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		GroupNodeData groupNodeData = GroupNodeData.read(workingContext);
		int partitionFlags = Helper.readI32(byteBuffer);
		String fileName = Helper.readMultiByteString(byteBuffer);

		float[][] boundingBox = Helper.readBBoxF32(byteBuffer);

		if((partitionFlags & 0x00000001) == 0){
			// "boundingBox" = transformed bounding box
		} else {
			// "boundingBox" = reserverd field
		}

		float area = Helper.readF32(byteBuffer);
		int[] vertexCountRange = Helper.readRange(byteBuffer);
		int[] nodeCountRange = Helper.readRange(byteBuffer);
		int[] polygonCountRange = Helper.readRange(byteBuffer);

		float[][] untransformedBoundingBox = null;
		if((partitionFlags & 0x00000001) != 0){
			// Untransformed bounding box
			untransformedBoundingBox = Helper.readBBoxF32(byteBuffer);
		}

		return new PartitionNode(	groupNodeData,
									partitionFlags,
									fileName,
									boundingBox,
									area,
									vertexCountRange,
									nodeCountRange,
									polygonCountRange,
									untransformedBoundingBox);
	}
}
