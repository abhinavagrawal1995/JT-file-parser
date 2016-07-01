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

import java.awt.Color;
import java.nio.ByteBuffer;

import de.raida.jcadlib.cadimport.jt.model.LSGNode;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.1.2.2 Material Attribute Element</h>
 * Object Type ID: <code>0x10dd1030, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>A Property Proxy Meta Data Element serves as a 'proxy' for all meta-data properties associated with a
 * particular Meta Data Node Element (see 7.2.1.1.1.6 Meta Data Node Element). The proxy is in the form of
 * a list of key/value property pairs where the key identifies the type and meaning of the value. Although
 * the property key is always in the form of a String data type, the value can be one of many several data
 * types.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class MaterialAttributeElement extends LSGNode {
	/** Object type ID */
	public final static String ID = "10dd1030-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Data flag: Shall the pattern flags be used? */
	private final static int USE_PATTERN_FLAGS = 0x0001;

	/** Data flag: Ambient color */
	private final static int AMBIENT_PATTERN_FLAG = 0x0002;

	/** Data flag: Emission color */
	private final static int EMISSION_PATTERN_FLAG = 0x0004;

	/** Data flag: Specular color */
	private final static int SPECULAR_PATTERN_FLAG = 0x0008;

	/** Base attribute data */
	private BaseAttributeData _baseAttributeData;

	/** Version number */
	private int _versionNumber;

	/** Data flags */
	private int _dataFlags;

	/** Ambient color (RGBA) */
	private float[] _ambientColor;

	/** Diffuse color (RGBA) */
	private float[] _diffuseColor;

	/** Specular color (RGBA) */
	private float[] _specularColor;

	/** Emission color (RGBA) */
	private float[] _emissionColor;

	/** Shininess */
	private float _shininess;

	/** Reflectivity */
	private float _reflectivity;

	/**
	 * Constructor.
	 * @param baseAttributeData Base attribute data
	 * @param versionNumber     Version number
	 * @param dataFlags         Data flags
	 * @param ambientColor      Ambient color (RGBA)
	 * @param diffuseColor      Diffuse color (RGBA)
	 * @param specularColor     Specular color (RGBA)
	 * @param emissionColor     Emission color (RGBA)
	 * @param shininess         Shininess
	 * @param reflectivity      Reflectivity
	 */
	public MaterialAttributeElement(BaseAttributeData baseAttributeData, int versionNumber, int dataFlags, float[] ambientColor, float[] diffuseColor, float[] specularColor, float[] emissionColor, float shininess, float reflectivity){
		_baseAttributeData = baseAttributeData;
		_versionNumber = versionNumber;
		_dataFlags = dataFlags;
		_ambientColor = ambientColor;
		_diffuseColor = diffuseColor;
		_specularColor = specularColor;
		_emissionColor = emissionColor;
		_shininess = shininess;
		_reflectivity = reflectivity;
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
	 * Returns a flag, telling whether the RGBA value shall be read.
	 * @return Shall the RGBA value be read?
	 */
	public boolean isAmbientColorRGBASet(){
		return true;
	}

	/**
	 * Returns the diffuse color.
	 * @return Diffuse color
	 */
	public Color getDiffuseColor(){
		return new Color(_diffuseColor[0], _diffuseColor[1], _diffuseColor[2], _diffuseColor[3]);
	}

	/**
	 * Returns a copy of the current class.
	 * @param  lsgNode Parent node
	 * @return         Copy of the current class
	 */
	public LSGNode copy(LSGNode lsgNode){
		MaterialAttributeElement materialAttributeElement = new MaterialAttributeElement(_baseAttributeData.copy(), _versionNumber, _dataFlags, Helper.copy(_ambientColor), Helper.copy(_diffuseColor), Helper.copy(_specularColor), Helper.copy(_emissionColor), _shininess, _reflectivity);
		materialAttributeElement.setAttributeNodes(getAttributeNodes());
		materialAttributeElement.setPropertyNodes(getPropertyNodes());
		materialAttributeElement.setParentLSGNode(lsgNode);
		for(LSGNode childNode : getChildLSGNodes()){
			materialAttributeElement.addChildLSGNode(childNode.copy(materialAttributeElement));
		}
		return materialAttributeElement;
	}

	/**
	 * Reads a MaterialAttributeElement object.
	 * @param  workingContext Working context
	 * @return                MaterialAttributeElement instance
	 */
	public static MaterialAttributeElement read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		BaseAttributeData baseAttributeData = BaseAttributeData.read(workingContext);

		int versionNumber = -1;
		if(workingContext.getJTFileVersion() >= 9.5){
			versionNumber = Helper.readI16(byteBuffer);
			if((versionNumber != 1) && (versionNumber != 2)){
				throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
			}
		}

		int dataFlags = Helper.readU16(byteBuffer);

		boolean usePatternFlags = ((dataFlags & USE_PATTERN_FLAGS) != 0);

		float[] ambientColor = null;
		if(usePatternFlags && ((dataFlags & AMBIENT_PATTERN_FLAG) != 0) && (workingContext.getJTFileVersion() < 9.5)){
			float colorValue = Helper.readF32(byteBuffer);
			ambientColor = new float[]{colorValue, colorValue, colorValue, 1.0f};
		} else {
			ambientColor = Helper.readRGBA(byteBuffer);
		}

		float[] diffuseColor = Helper.readRGBA(byteBuffer);

		float[] specularColor = null;
		if(usePatternFlags && ((dataFlags & SPECULAR_PATTERN_FLAG) != 0) && (workingContext.getJTFileVersion() < 9.5)){
			float colorValue = Helper.readF32(byteBuffer);
			specularColor = new float[]{colorValue, colorValue, colorValue, 1.0f};
		} else {
			specularColor = Helper.readRGBA(byteBuffer);
		}

		float[] emissionColor = null;
		if(usePatternFlags && ((dataFlags & EMISSION_PATTERN_FLAG) != 0) && (workingContext.getJTFileVersion() < 9.5)){
			float colorValue = Helper.readF32(byteBuffer);
			emissionColor = new float[]{colorValue, colorValue, colorValue, 1.0f};
		} else {
			emissionColor = Helper.readRGBA(byteBuffer);
		}

		float shininess = Helper.readF32(byteBuffer);
		if((shininess < 1.0f) || (shininess > 128.0f)){
			throw new IllegalArgumentException("Found invalid shininess: " + shininess);
		}

		float reflectivity = -1;
		if(versionNumber == 2){
			reflectivity = Helper.readF32(byteBuffer);
		}

		return new MaterialAttributeElement(baseAttributeData,
											versionNumber,
											dataFlags,
											ambientColor,
											diffuseColor,
											specularColor,
											emissionColor,
											shininess,
											reflectivity);
	}
}
