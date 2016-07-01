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
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.1.3 Property Table</h>
 * Object Type ID: <code>---</code>
 * <br>The Property Table is where the data connecting Nodes with their associated Properties is stored. The
 * Property Table contains a Node Property Table for each Node in the JT File which has associated Properties.
 * A Node Property Table is a list of key/value Property Atom Element pairs for all Properties associated with
 * a particular Node Element Object.
 * <br>For a reference compliant JT File all Node Elements and Property Atom Elements contained in a JT file
 * should have been read by the time a JT file reader reaches the Property Table section of the file. This
 * means that all Node Objects and Property Atom Objects referenced in the Property Table (through Object IDs),
 * should have already been read, and if not, then the file is corrupt (i.e. not reference compliant).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class PropertyTable {
	/** List of node property tables */
	private HashMap<Integer, NodePropertyTable> _nodePropertyTables;

	/**
	 * Constructor.
	 * @param versionNumber      Version number
	 * @param nodePropertyTables List of node property tables
	 */
	public PropertyTable(int versionNumber, HashMap<Integer, NodePropertyTable> nodePropertyTables){
		_nodePropertyTables = nodePropertyTables;
	}

	/**
	 * Returns the node property table of the given node object ID.
	 * @param  nodeObjectID Node object ID
	 * @return              Node property table of the given node object ID or<br>
	 *                      <b>null</b> if no table is available
	 */
	public NodePropertyTable getNodePropertyTable(int nodeObjectID){
		if(_nodePropertyTables.containsKey(nodeObjectID)){
			return _nodePropertyTables.get(nodeObjectID);
		}

		return null;
	}

	/**
	 * Reads a BasePropertyAtomData object.
	 * @param  workingContext Working context
	 * @return                BasePropertyAtomData instance
	 */
	public static PropertyTable read(WorkingContext workingContext){
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		HashMap<Integer, NodePropertyTable> nodePropertyTables = new HashMap<Integer, NodePropertyTable>();

		int versionNumber = Helper.readI16(byteBuffer);
		if(versionNumber != 1){
			throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
		}

		int nodePropertyTableCount = Helper.readI32(byteBuffer);

		for(int i = 0; i < nodePropertyTableCount; i++){
			int nodeObjectID = Helper.readI32(byteBuffer);
			nodePropertyTables.put(nodeObjectID, NodePropertyTable.read(byteBuffer));
		}

		return new PropertyTable(	versionNumber,
									nodePropertyTables);
	}
}
