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

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.raida.jcadlib.cadimport.jt.reader.Helper;

/**
 * <h>7.2.1.3.1 Node Property Table</h>
 * Object Type ID: <code>---</code>
 * <br>The Node Property Table is a list of key/value Property Atom Element pairs for all properties
 * associated with a particular Node Element Object. The list is terminated by a '0' value for Key
 * Property Atom Object ID.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class NodePropertyTable {
	/** List of key / values */
	private HashMap<Integer, Integer> _keyValuePairs;

	/**
	 * Constructor.
	 * @param keyValuePairs List of key / values
	 */
	public NodePropertyTable(HashMap<Integer, Integer> keyValuePairs){
		_keyValuePairs = keyValuePairs;
	}

	/**
	 * Returns the key / value pairs.
	 * @return Key / value pairs.
	 */
	public HashMap<Integer, Integer> getKeyValuePairs(){
		return _keyValuePairs;
	}

	/**
	 * Adds the given key / value pair.
	 * @param key   Key
	 * @param value Value
	 */
	public void addKeyValuePair(int key, int value){
		_keyValuePairs.put(key, value);
	}

	/**
	 * Reads a BasePropertyAtomData object.
	 * @param  byteBuffer Byte buffer to read from
	 * @return            BasePropertyAtomData instance
	 */
	public static NodePropertyTable read(ByteBuffer byteBuffer){
		HashMap<Integer, Integer> keyValuePairs = new HashMap<Integer, Integer>();

		while(true){
			int keyPropertyAtomObjectID = Helper.readI32(byteBuffer);
			if(keyPropertyAtomObjectID == 0){
				break;
			}

			keyValuePairs.put(keyPropertyAtomObjectID, Helper.readI32(byteBuffer));
		}

		return new NodePropertyTable(keyValuePairs);
	}
}
