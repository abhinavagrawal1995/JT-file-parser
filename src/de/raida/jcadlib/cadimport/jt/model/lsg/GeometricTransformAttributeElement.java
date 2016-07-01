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

import javax.vecmath.Matrix4d;

import de.raida.jcadlib.cadimport.jt.model.LSGNode;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.2.10 Geometric Transform Attribute Element</h>
 * Object Type ID: <code>0x10dd1083, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>Geometric Transform Attribute Element contains a 4x4 matrix that positions the
 * associated LSG node's coordinate system relative to its parent LSG node. JT format
 * LSG traversal semantics dictate that geometric transform attributes accumulate down
 * the LSG through matrix multiplication as follows:
 * <br>p' = PAM
 * <br>Where p is a point of the model, p' is the transformed point, M is the current
 * modeling transformation matrix inherited from ancestor LSG nodes and previous
 * Geometric Transform Attribute Element, and A is the transformation matrix of this
 * Geometric Transform Attribute Element.
 * <br>Geometric Transform Attribute Element does not have any Field Inhibit flag
 * (see 7.2.1.1.2.1.1 Base Attribute Data) bit assignments.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class GeometricTransformAttributeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "10dd1083-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Base attribute data */
	private BaseAttributeData _baseAttributeData;

	/** Version number */
	private int _versionNumber;

	/** Transformation matrix */
	private Matrix4d _transformationMatrix;

	/**
	 * Constructor.
	 * @param baseAttributeData    Base attribute data
	 * @param versionNumber        Version number
	 * @param transformationMatrix Transformation matrix
	 */
	public GeometricTransformAttributeElement(BaseAttributeData baseAttributeData, int versionNumber, Matrix4d transformationMatrix){
		_baseAttributeData = baseAttributeData;
		_versionNumber = versionNumber;
		_transformationMatrix = transformationMatrix;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _baseAttributeData.getObjectID();
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
		return new int[0];
	}

	/**
	 * Returns the transformation matrix.
	 * @return Transformation matrix
	 */
	public Matrix4d getTransformationMatrix(){
		return _transformationMatrix;
	}

	/**
	 * Returns a copy of the current class.
	 * @param  lsgNode Parent node
	 * @return         Copy of the current class
	 */
	public LSGNode copy(LSGNode lsgNode){
		GeometricTransformAttributeElement geometricTransformAttributeElement = new GeometricTransformAttributeElement(_baseAttributeData.copy(), _versionNumber, (Matrix4d)_transformationMatrix.clone());
		geometricTransformAttributeElement.setAttributeNodes(getAttributeNodes());
		geometricTransformAttributeElement.setPropertyNodes(getPropertyNodes());
		geometricTransformAttributeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			geometricTransformAttributeElement.addChildLSGNode(childNode.copy(geometricTransformAttributeElement));
		}
		return geometricTransformAttributeElement;
	}

	/**
	 * Reads a GeometricTransformAttributeElement object.
	 * @param  workingContext   Working context
	 * @param  totalSizeInBytes Size of element in bytes
	 * @return                  GeometricTransformAttributeElement instance
	 */
	public static GeometricTransformAttributeElement read(WorkingContext workingContext, int totalSizeInBytes){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();
		int startPosition = byteBuffer.position();

		BaseAttributeData baseAttributeData = BaseAttributeData.read(workingContext);

		int versionNumber = -1;
		if(workingContext.getJTFileVersion() >= 9.5){
			versionNumber = Helper.readI16(byteBuffer);
			if(versionNumber != 1){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}
		}

		int storedValuesMask = Helper.readU16(byteBuffer);

		float[] transformationMatrix = new float[]{	1.0f, 0.0f, 0.0f, 0.0f,
													0.0f, 1.0f, 0.0f, 0.0f,
													0.0f, 0.0f, 1.0f, 0.0f,
													0.0f, 0.0f, 0.0f, 1.0f};

		// Detect whether to read 32 or 64 bit floats (since version 9)
		int bytesLeft = totalSizeInBytes - (byteBuffer.position() - startPosition);
		int numberOfValues = 0;
		boolean read64Bit = false;
		int tmpStoredValuesMask = storedValuesMask;
		for(int i = 0; i < transformationMatrix.length; i++){
			if((tmpStoredValuesMask & 0x8000) != 0){
				numberOfValues++;
			}
			tmpStoredValuesMask <<= 1;
		}
		if(bytesLeft == (8 * numberOfValues)){
			read64Bit = true;
		}

		// Read the transformation matrix components
		for(int i = 0; i < transformationMatrix.length; i++){
			if((storedValuesMask & 0x8000) != 0){
				if(read64Bit){
					transformationMatrix[i] = (float)Helper.readF64(byteBuffer);
				} else {
					transformationMatrix[i] = Helper.readF32(byteBuffer);
				}
			}
			storedValuesMask <<= 1;
		}

		Matrix4d matrix = new Matrix4d(	transformationMatrix[0], transformationMatrix[4], transformationMatrix[8],  transformationMatrix[12],
										transformationMatrix[1], transformationMatrix[5], transformationMatrix[9],  transformationMatrix[13],
										transformationMatrix[2], transformationMatrix[6], transformationMatrix[10], transformationMatrix[14],
										transformationMatrix[3], transformationMatrix[7], transformationMatrix[11], transformationMatrix[15]);

		return new GeometricTransformAttributeElement(baseAttributeData, versionNumber, matrix);
	}
}
