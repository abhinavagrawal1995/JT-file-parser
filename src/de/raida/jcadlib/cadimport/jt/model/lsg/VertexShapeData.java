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

import de.raida.jcadlib.cadimport.jt.model.quantize.QuantizationParameters;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.1.10.2.1 Vertex Shape Data</h>
 * Object Type ID: <code>---</code>
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class VertexShapeData {
	/** Base shape data */
	private BaseShapeData _baseShapeData;

	/** Normal binding */
	private int _normalBinding;

	/** Texture coordinate binding */
	private int _textureCoordBinding;

	/** Color binding */
	private int _colorBinding;

	/** Quantization parameters */
	private QuantizationParameters _quantizationParameters;

	/**
	 * Constructor (JT version < 9).
	 * @param baseShapeData          Base shape data
	 * @param normalBinding          Normal binding
	 * @param textureCoordBinding    Texture coordinate binding
	 * @param colorBinding           Color binding
	 * @param quantizationParameters Quantization parameters
	 */
	public VertexShapeData(BaseShapeData baseShapeData, int normalBinding, int textureCoordBinding, int colorBinding, QuantizationParameters quantizationParameters){
		_baseShapeData = baseShapeData;
		_normalBinding = normalBinding;
		_textureCoordBinding = textureCoordBinding;
		_colorBinding = colorBinding;
		_quantizationParameters = quantizationParameters;
	}

	/**
	 * Constructor (JT version >= 9).
	 * @param baseShapeData 
	 * @param versionNumber          Version number
	 * @param vertexBinding          Vertex binding
	 * @param quantizationParameters Quantization parameters
	 * @param vertexBinding2         Vertex binding 2
	 */
	public VertexShapeData(BaseShapeData baseShapeData, int versionNumber, long vertexBinding, QuantizationParameters quantizationParameters, long vertexBinding2){
		_baseShapeData = baseShapeData;
		_quantizationParameters = quantizationParameters;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _baseShapeData.getObjectID();
	}

	/**
	 * Returns the child node object IDs.
	 * @return Child node object IDs
	 */
	public int[] getChildNodeObjectIDs(){
		return _baseShapeData.getChildNodeObjectIDs();
	}

	/**
	 * Returns the attribute object IDs.
	 * @return Attribute object IDs
	 */
	public int[] getAttributeObjectIDs(){
		return _baseShapeData.getAttributeObjectIDs();
	}

	/**
	 * Returns a copy of the current class.
	 * @return Copy of the current class
	 */
	public VertexShapeData copy(){
		return new VertexShapeData(_baseShapeData.copy(), _normalBinding, _textureCoordBinding, _colorBinding, (_quantizationParameters != null) ? _quantizationParameters.copy() : null);
	}

	/**
	 * Reads a VertexShapeData object.
	 * @param  workingContext               Working context
	 * @param  fromPointSetShapeNodeElement Called from PointSetShapeNodeElement?
	 * @return                              VertexShapeData instance
	 */
	public static VertexShapeData read(WorkingContext workingContext, boolean fromPointSetShapeNodeElement){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		BaseShapeData baseShapeData = BaseShapeData.read(workingContext);

		if(workingContext.getJTFileVersion() >= 9.0){
			int versionNumber = Helper.readI16(byteBuffer);
			if((versionNumber < 0) || (versionNumber > 2)){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}

			long vertexBinding = Helper.readU64(byteBuffer);
			QuantizationParameters quantizationParameters = QuantizationParameters.read(workingContext);
			long vertexBinding2 = -1;
			if(versionNumber != 1){
				vertexBinding2 = Helper.readU64(byteBuffer);
			}
			return new VertexShapeData(	baseShapeData,
										versionNumber,
										vertexBinding,
										quantizationParameters,
										vertexBinding2);

		} else {
			int normalBinding = -1;
			int textureCoordBinding = -1;
			int colorBinding = -1;
			QuantizationParameters quantizationParameters = null;

			if(!fromPointSetShapeNodeElement){
				normalBinding = Helper.readI32(byteBuffer);
				if((normalBinding < 0) || (normalBinding > 3)){
					throw new IllegalArgumentException("Found invalid normal binding: " + normalBinding);
				}

				textureCoordBinding = Helper.readI32(byteBuffer);
				if((textureCoordBinding < 0) || (textureCoordBinding > 3)){
					throw new IllegalArgumentException("Found invalid texture coordinate binding: " + textureCoordBinding);
				}

				colorBinding = Helper.readI32(byteBuffer);
				if((colorBinding < 0) || (colorBinding > 3)){
					throw new IllegalArgumentException("Found invalid color binding: " + colorBinding);
				}

				quantizationParameters = QuantizationParameters.read(workingContext);
			}

			return new VertexShapeData(	baseShapeData,
										normalBinding,
										textureCoordBinding,
										colorBinding,
										quantizationParameters);
		}
	}
}
