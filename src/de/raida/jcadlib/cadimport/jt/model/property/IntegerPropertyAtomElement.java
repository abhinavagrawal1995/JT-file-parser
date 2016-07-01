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

package de.raida.jcadlib.cadimport.jt.model.property;

import de.raida.jcadlib.cadimport.jt.model.JTNode;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.2.3 Integer Property Atom Element</h>
 * Object Type ID: <code>0x10dd102b, 0x2ac8, 0x11d1, 0x9b, 0x6b, 0x00, 0x80, 0xc7, 0xbb, 0x59, 0x97</code>
 * <br>Integer Property Atom Element represents a property atom whose value is of
 * I32 data type.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class IntegerPropertyAtomElement implements JTNode {
	/** Object type ID */
	public final static String ID = "10dd102b-2ac8-11d1-9b-6b-0-80-c7-bb-59-97";

	/** Base property atom data */
	private BasePropertyAtomData _basePropertyAtomData;

	/**
	 * Constructor.
	 * @param basePropertyAtomData Base property atom data
	 * @param value                Value
	 */
	public IntegerPropertyAtomElement(BasePropertyAtomData basePropertyAtomData, int value){
		_basePropertyAtomData = basePropertyAtomData;
	}

	/**
	 * Returns the object ID.
	 * @return Object ID
	 */
	public int getObjectID(){
		return _basePropertyAtomData.getObjectID();
	}

	/**
	 * Reads a IntegerPropertyAtomElement object.
	 * @param  workingContext Working context
	 * @return                IntegerPropertyAtomElement instance
	 */
	public static IntegerPropertyAtomElement read(WorkingContext workingContext){
		return new IntegerPropertyAtomElement(	BasePropertyAtomData.read(workingContext),
												Helper.readI32(workingContext.getByteBuffer()));
	}
}
