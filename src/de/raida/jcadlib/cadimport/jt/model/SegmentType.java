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

/**
 * <h>7.1.3.1 Segment Type</h>
 * Segment Type defines a broad classification of the segment contents. For example,
 * a Segment Type of '1' denotes that the segment contains Logical Scene Graph
 * material; '2' denotes contents of a B-Rep, etc.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public enum SegmentType {
	/** Predefined class: Logical_Scene_Graph */
	LOGICAL_SCENE_GRAPH(1, true),

	/** Predefined class: JT_BRep */
	JT_BREP(2, true),

	/** Predefined class: PMI_Data */
	PMI_DATA(3, true),

	/** Predefined class: Meta_Data */
	META_DATA(4, true),

	/** Predefined class: Shape */
	SHAPE(6, false),

	/** Predefined class: Shape_LOD0 */
	SHAPE_LOD0(7, false),

	/** Predefined class: Logical_Scene_Graph */
	SHAPE_LOD1(8, false),

	/** Predefined class: Shape_LOD2 */
	SHAPE_LOD2(9, false),

	/** Predefined class: Shape_LOD3 */
	SHAPE_LOD3(10, false),

	/** Predefined class: Shape_LOD4 */
	SHAPE_LOD4(11, false),

	/** Predefined class: Shape_LOD5 */
	SHAPE_LOD5(12, false),

	/** Predefined class: Shape_LOD6 */
	SHAPE_LOD6(13, false),

	/** Predefined class: Shape_LOD7 */
	SHAPE_LOD7(14, false),

	/** Predefined class: Shape_LOD8 */
	SHAPE_LOD8(15, false),

	/** Predefined class: Shape_LOD9 */
	SHAPE_LOD9(16, false),

	/** Predefined class: XT_BRep */
	XT_BREP(17, true),

	/** Predefined class: Wireframe_Representation */
	WIREFRAME_REPRESENTATION(18, true),

	/** Predefined class: ULP */
	ULP(20, true),

	/** Predefined class: LWPA */
	LWPA(24, true);

	/** Segment type */
	private int _type;

	/** Is this type compressed? */
	private boolean _isZipped;

	/**
	 * Constructor.
	 * @param type     Segment type
	 * @param isZipped Is this type compressed?
	 */
	private SegmentType(int type, boolean isZipped){
		_type = type;
		_isZipped = isZipped;
	}

	/**
	 * Returns a flag, telling whether the segment type is compressed.
	 * @return Is this segment type compressed?
	 */
	public boolean isZipped(){
		return _isZipped;
	}

	/**
	 * Returns the type of the segment.
	 * @return Segment type
	 */
	public int getType(){
		return _type;
	}

	/**
	 * Returns the matching SegmentType instance for the given segment type value.
	 * @param  type Segment type value
	 * @return      Corresponding SegmentType instance
	 */
	public static SegmentType get(int type){
		for(int i = 0; i < SegmentType.values().length; i++){
			SegmentType segmentType = SegmentType.values()[i];
			if(segmentType.getType() == type){
				return segmentType;
			}
		}

		throw new IllegalArgumentException("Found invalid segment type: " + type);
	}
}
