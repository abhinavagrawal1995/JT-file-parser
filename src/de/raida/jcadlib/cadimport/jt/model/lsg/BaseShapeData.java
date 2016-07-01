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

package de.raida.jcadlib.cadimport.jt.model.lsg;

import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.10.1.1 Base Shape Data</h>
 * Object Type ID: <code>---</code>
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class BaseShapeData {
	/** Base node data */
	private BaseNodeData _baseNodeData;

	/** Bounding box */
	private float[][] _transformedBoundingBox;

	/** Bounding box (untransformed) */
	private float[][] _untransformedBoundingBox;

	/** Area */
	private float _area;

	/** Vertex count range */
	private int[] _vertexCountRange;

	/** Node count range */
	private int[] _nodeCountRange;

	/** Polygon count range */
	private int[] _polygonCountRange;

	/** Size */
	private int _size;

	/** Compression level */
	private float _compressionLevel;

	/**
	 * Constructor.
	 * @param baseNodeData             Base node data
	 * @param transformedBoundingBox   Bounding box
	 * @param untransformedBoundingBox Bounding box (untransformed)
	 * @param area                     Area
	 * @param vertexCountRange         Vertex count range
	 * @param nodeCountRange           Node count range
	 * @param polygonCountRange        Polygon count range
	 * @param size                     Length in bytes of the associated/referenced Shape LOD Element
	 * @param compressionLevel         Compression level
	 */
	public BaseShapeData(BaseNodeData baseNodeData, float[][] transformedBoundingBox, float[][] untransformedBoundingBox, float area, int[] vertexCountRange, int[] nodeCountRange, int[] polygonCountRange, int size, float compressionLevel){
		_baseNodeData = baseNodeData;
		_transformedBoundingBox = transformedBoundingBox;
		_untransformedBoundingBox = untransformedBoundingBox;
		_area = area;
		_vertexCountRange = vertexCountRange;
		_nodeCountRange = nodeCountRange;
		_polygonCountRange = polygonCountRange;
		_size = size;
		_compressionLevel = compressionLevel;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _baseNodeData.getObjectID();
	}

	/**
	 * Returns the child node object IDs.
	 * @return Child node object IDs
	 */
	public int[] getChildNodeObjectIDs(){
		return new int[0];
	}

	/**
	 * Returns the attribute object IDs.
	 * @return Attribute object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _baseNodeData.getAttributeObjectIDs();
	}

	/**
	 * Returns a copy of the current class.
	 * @return Copy of the current class
	 */
	public BaseShapeData copy(){
		return new BaseShapeData(_baseNodeData.copy(), Helper.copy(_transformedBoundingBox), Helper.copy(_untransformedBoundingBox), _area, Helper.copy(_vertexCountRange), Helper.copy(_nodeCountRange), Helper.copy(_polygonCountRange), _size, _compressionLevel);
	}

	/**
	 * Reads a BaseShapeData object.
	 * @param  workingContext Working context
	 * @return                BaseShapeData instance
	 */
	public static BaseShapeData read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		BaseNodeData baseNodeData = BaseNodeData.read(workingContext);

		int versionNumber = -1;
		if(workingContext.getJTFileVersion() >= 9.0){
			versionNumber = Helper.readI16(byteBuffer);
			if(versionNumber != 1){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}
		}

		float[][] transformedBoundingBox = Helper.readBBoxF32(byteBuffer);
		float[][] untransformedBoundingBox = Helper.readBBoxF32(byteBuffer);
		float area = Helper.readF32(byteBuffer);
		int[] vertexCountRange = Helper.readRange(byteBuffer);
		int[] nodeCountRange = Helper.readRange(byteBuffer);
		int[] polygonCountRange = Helper.readRange(byteBuffer);
		int size = Helper.readI32(byteBuffer);
		float compressionLevel = Helper.readF32(byteBuffer);

		if((compressionLevel < 0.0f) || (compressionLevel > 1.0f)){
			throw new IllegalArgumentException("Found invalid compression level: " + compressionLevel);
		}

		return new BaseShapeData(	baseNodeData,
									transformedBoundingBox,
									untransformedBoundingBox,
									area,
									vertexCountRange,
									nodeCountRange,
									polygonCountRange,
									size,
									compressionLevel);
	}
}
