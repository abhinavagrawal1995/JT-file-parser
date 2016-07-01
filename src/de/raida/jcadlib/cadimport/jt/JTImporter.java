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

package de.raida.jcadlib.cadimport.jt;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.raida.jcadlib.cadimport.jt.model.ElementHeader;
import de.raida.jcadlib.cadimport.jt.model.GUID;
import de.raida.jcadlib.cadimport.jt.model.JTNode;
import de.raida.jcadlib.cadimport.jt.model.LSGNode;
import de.raida.jcadlib.cadimport.jt.model.PointSetShapeLODElement;
import de.raida.jcadlib.cadimport.jt.model.PolylineSetShapeLODElement;
import de.raida.jcadlib.cadimport.jt.model.SegmentHeader;
import de.raida.jcadlib.cadimport.jt.model.TOCEntry;
import de.raida.jcadlib.cadimport.jt.model.TopoMeshCompressedLODData;
import de.raida.jcadlib.cadimport.jt.model.TopoMeshCompressedRepDataV1;
import de.raida.jcadlib.cadimport.jt.model.TriStripSetShapeLODElement;
import de.raida.jcadlib.cadimport.jt.model.VertexBasedShapeCompressedRepData;
import de.raida.jcadlib.cadimport.jt.model.VertexShapeLODData;
import de.raida.jcadlib.cadimport.jt.model.VertexShapeLODElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.GeometricTransformAttributeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.GroupNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.InstanceNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.MaterialAttributeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.MetaDataNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.PartNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.PartitionNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.PointSetShapeNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.PolylineSetShapeNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.RangeLODNodeElement;
import de.raida.jcadlib.cadimport.jt.model.lsg.TriStripSetShapeNodeElement;
import de.raida.jcadlib.cadimport.jt.model.property.DatePropertyAtomElement;
import de.raida.jcadlib.cadimport.jt.model.property.FloatingPointPropertyAtomElement;
import de.raida.jcadlib.cadimport.jt.model.property.IntegerPropertyAtomElement;
import de.raida.jcadlib.cadimport.jt.model.property.LateLoadedPropertyAtomElement;
import de.raida.jcadlib.cadimport.jt.model.property.NodePropertyTable;
import de.raida.jcadlib.cadimport.jt.model.property.PropertyTable;
import de.raida.jcadlib.cadimport.jt.model.property.StringPropertyAtomElement;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;
import de.raida.progress.ProgressEvent;
import de.raida.progress.ProgressListenerInterface;

/**
 * Imports the JT file, creates the model and gives access to the models attributes.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class JTImporter {
	/** Regular expression of the JT signature (version 8) */
	private final String JT_SIGNATURE_REG_EXP_V8 = "Version \\d\\.\\d.{64} {5}";

	/** Regular expression of the JT signature (version 9) */
	private final String JT_SIGNATURE_REG_EXP_V9 = "Version \\d\\.\\d.{64} \n\r\n ";

	/** Color of the geometry */
	public final static Color DEFAULT_COLOR = Color.WHITE;

	/** Layer name */
	public final static String DEFAULT_LAYER = "0";

	/** List of load informations */
	private static ArrayList<String[]> _loadInformation;

	/** List of unsupported entities */
	private ArrayList<String> _unsupportedEntities;

	/** JT model */
	private JTModel _jtModel;

	/** List of progress listener */
	private static ArrayList<ProgressListenerInterface> _progressListener;

	/** Root node of the LSG */
	private LSGNode _rootNode;

	/** JT nodes, indices by their object ID's */
	private HashMap<Integer, JTNode> _jtNodes;

	/** List of all XSetShapeLODElements bytebuffer positions */
	private HashMap<String, Integer> _xSetShapeLODElements;

	/** Property table */
	private PropertyTable _propertyTable;

	/** Base URL name */
	private static URL _baseURLName;

	/** Current URL name */
	private static URL _currentURLName;

	/** Length of file in bytes */
	private static HashMap<URL, Integer> _fileLength;

	/** Number of read bytes */
	private static HashMap<URL, Integer> _readBytes;

	/** Number of read bytes for progress intervall */
	private static HashMap<URL, Integer> _progressIntervall;

	/** Mapping of unsupported GUID's: GUID -> Name */
	private HashMap<String, String> _guidMapping;

	/**
	 * Constructor.
	 */
	public JTImporter(){
		_progressIntervall = new HashMap<URL, Integer>();
		_readBytes = new HashMap<URL, Integer>();
		_fileLength = new HashMap<URL, Integer>();

		_loadInformation = new ArrayList<String[]>();
		_unsupportedEntities = new ArrayList<String>();
		_jtNodes = new HashMap<Integer, JTNode>();
		_xSetShapeLODElements = new HashMap<String, Integer>();
		_guidMapping = new HashMap<String, String>();
		_guidMapping.put("873a70c0-2ac8-11d1-9b-6b-0-80-c7-bb-59-97", "JT B-Rep Element");
		_guidMapping.put("ce357249-38fb-11d1-a5-6-0-60-97-bd-c6-e1",  "PMI Manager Meta Data");
		_guidMapping.put("10dd1083-2ac8-11d1-9b-6b-0-80-c7-bb-59-97", "Geometric Transform Attribute Element");
		_guidMapping.put("10dd1073-2ac8-11d1-9b-6b-0-80-c7-bb-59-97", "Texture Image Attribute Element");
		_guidMapping.put("10dd1014-2ac8-11d1-9b-6b-0-80-c7-bb-59-97", "Draw Style Attribute Element");
		_guidMapping.put("10dd10c4-2ac8-11d1-9b-6b-0-80-c7-bb-59-97", "Linestyle Attribute Element");
		_guidMapping.put("ce357247-38fb-11d1-a5-6-0-60-97-bd-c6-e1",  "Property Proxy Meta Data Element");
		_guidMapping.put("873a70e0-2ac9-11d1-9b-6b-0-80-c7-bb-59-97", "XT B-Rep Element");
		_guidMapping.put("873a70d0-2ac8-11d1-9b-6b-0-80-c7-bb-59-97", "Wireframe Rep Element");
	}

	/**
	 * Imports the given file and creates the model.
	 * @param  fileName  Name of the file to load
	 * @throws Exception Thrown if something failed
	 */
	public void importFile(String fileName) throws Exception {
		File file = new File(fileName);
		if(!file.exists() || (file.length() == 0)){
			throw new Exception("ERROR: File '" + fileName + "' doesn't exists or is empty!");
		}
		loadFile(file.toURI().toURL());
	}

	/**
	 * Loads the given url and creates the model.
	 * @param  url       Name of the file to load
	 * @throws Exception Thrown if something failed
	 */
	public void loadFile(URL url) throws Exception {
		loadFile(url, false);
	}

	/**
	 * Parses the given file and creates the model.
	 * @param  url            URL of the file to load
	 * @param  referencedFile Is it a referenced file?
	 * @throws Exception      Thrown if something failed
	 */
	private void loadFile(URL url, boolean referencedFile) throws Exception {
		if(!referencedFile){
			_baseURLName = url;
		}

		_currentURLName = url;
		_jtModel = new JTModel();

		_progressIntervall.put(_currentURLName, 0);
		_readBytes.put(_currentURLName, 0);
		_fileLength.put(_currentURLName, url.openConnection().getContentLength());

		try {
			InputStream inputStream = url.openStream();

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(_fileLength.get(_currentURLName));
			byte[] buffer = new byte[1024];
			int readBytes = 0;
			while((readBytes = inputStream.read(buffer)) != -1){
				byteArrayOutputStream.write(buffer, 0, readBytes);
			}
			ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
			inputStream.close();

			WorkingContext workingContext = new WorkingContext();
			workingContext.setByteBuffer(byteBuffer);

			// Check the signature
			String signature = Helper.readStringByLength(byteBuffer, 80);
			if(	!Pattern.compile(JT_SIGNATURE_REG_EXP_V8).matcher(signature).matches() &&
				!Pattern.compile(JT_SIGNATURE_REG_EXP_V9).matcher(signature).matches()){
				throw new Exception("Wrong signature! File doesn't seem to be a JT file!\n'" + signature + "'");
			}

			// Extract some information from the valid signature
			_jtModel.setVersion(signature.substring(8, 11));
			_jtModel.setComment(signature.substring(11));
			workingContext.setJTFileVersion(_jtModel.getJTFileVersion());

			// Continue only if the major version is supported
			if((_jtModel.getJTFileVersion() < 8.0) || (_jtModel.getJTFileVersion() >= 10.0)){
				addLoadInformation("ERROR", "Found unsupported JT major version: " + signature.substring(8, 11));
				return;
			}

			// Get the byte order (default of ByteBuffer is BIG_ENDIAN)
			if(Helper.readU8(byteBuffer) == 0){
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			}

			// Reserved field
			int reservedField = Helper.readI32(byteBuffer);

			// TOC offset
			int tocOffset = Helper.readI32(byteBuffer);

			// Read the segment ID of the root Logical Scene Graph (LSG)
			GUID rootLSG = GUID.read(workingContext);
			if(reservedField != 0){
				rootLSG = null;
			}

			// Go to the TOC
			byteBuffer.position(tocOffset);

			// Get all TOC entries
			ArrayList<TOCEntry> tocEntries = new ArrayList<TOCEntry>();
			int tocEntryCount = Helper.readI32(byteBuffer);
			for(int i = 0; i < tocEntryCount; i++){
				tocEntries.add(TOCEntry.read(workingContext));				
			}

			// Iterate over elements referenced from TOC
			for(Iterator<TOCEntry> tocIterator = tocEntries.iterator(); tocIterator.hasNext();){
				workingContext.setByteBuffer(byteBuffer);

				TOCEntry tocEntry = tocIterator.next();
				byteBuffer.position(tocEntry.getSegmentOffSet());

				SegmentHeader segmentHeader = SegmentHeader.read(workingContext);

				String segmentID = segmentHeader.getSegmentID().toString();
				workingContext.setSegmentType(segmentHeader.getSegmentType());

				ElementHeader elementHeader = ElementHeader.read(workingContext, true);
				String elementID = elementHeader.getElementID().toString();
				workingContext.setByteBuffer(elementHeader.getByteBuffer());

				// Extract the geometry information
				if(elementID.equals(TriStripSetShapeLODElement.ID)){
					// Store the position for later reading
					_xSetShapeLODElements.put(segmentID, workingContext.getByteBuffer().position());

				// Extract the assembly information
				} else if(elementID.equals(PartitionNodeElement.ID)){
					PartitionNodeElement y = PartitionNodeElement.read(workingContext);
					_jtNodes.put(y.getObjectID(), y);
					if(segmentID.equals(rootLSG.toString())){
						_rootNode = y;
					}

					// Read GraphElements
					//--------------------
					while(true){
						int beforeHeader = workingContext.getByteBuffer().position();
						ElementHeader elementHeader2 = ElementHeader.read(workingContext, false);
						int headerSize = workingContext.getByteBuffer().position() - beforeHeader;
						String elementID2 = elementHeader2.getElementID().toString();

						if(elementID2.equals(PartNodeElement.ID)){
							PartNodeElement x = PartNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(RangeLODNodeElement.ID)){
							RangeLODNodeElement x = RangeLODNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(GroupNodeElement.ID)){
							GroupNodeElement x = GroupNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(TriStripSetShapeNodeElement.ID)){
							TriStripSetShapeNodeElement x = TriStripSetShapeNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(MaterialAttributeElement.ID)){
							MaterialAttributeElement x = MaterialAttributeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(MetaDataNodeElement.ID)){
							MetaDataNodeElement x = MetaDataNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(InstanceNodeElement.ID)){
							InstanceNodeElement x = InstanceNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(GeometricTransformAttributeElement.ID)){
							GeometricTransformAttributeElement x = GeometricTransformAttributeElement.read(workingContext, elementHeader2.getElementLength() - headerSize + 4);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(PartitionNodeElement.ID)){
							PartitionNodeElement x = PartitionNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(PolylineSetShapeNodeElement.ID)){
							PolylineSetShapeNodeElement x = PolylineSetShapeNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(PointSetShapeNodeElement.ID)){
							PointSetShapeNodeElement x = PointSetShapeNodeElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						// Skip unevaluated element
						} else {
							addUnsupportedEntity(elementID2);
							int bytesToSkip = elementHeader2.getElementLength() - headerSize + 4;
							Helper.readBytes(workingContext.getByteBuffer(), bytesToSkip);
						}

						// Check whether a next element is available
						int currentPosition = workingContext.getByteBuffer().position();
						int elementLength = elementHeader2.getElementLength();
						int bytesToSkip = (currentPosition - 4 - elementLength - beforeHeader) * -1;
						if(bytesToSkip > 0){
							Helper.readBytes(workingContext.getByteBuffer(), bytesToSkip);
							currentPosition = workingContext.getByteBuffer().position();
						}

						// Skip next elements length
						Helper.readI32(workingContext.getByteBuffer());

						// Possibly break endless loop
						GUID nextGUID = GUID.read(workingContext);
						if(nextGUID.toString().equals(GUID.END_OF_ELEMENTS)){
							break;
						}

						workingContext.getByteBuffer().position(currentPosition);
					}

					// Read Property Atom Elements
					//-----------------------------
					while(true){
						int beforeHeader = workingContext.getByteBuffer().position();
						ElementHeader elementHeader2 = ElementHeader.read(workingContext, false);
						int headerSize = workingContext.getByteBuffer().position() - beforeHeader;
						String elementID2 = elementHeader2.getElementID().toString();

						if(elementID2.equals(StringPropertyAtomElement.ID)){
							StringPropertyAtomElement x = StringPropertyAtomElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(FloatingPointPropertyAtomElement.ID)){
							FloatingPointPropertyAtomElement x = FloatingPointPropertyAtomElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(LateLoadedPropertyAtomElement.ID)){
							LateLoadedPropertyAtomElement x = LateLoadedPropertyAtomElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(DatePropertyAtomElement.ID)){
							DatePropertyAtomElement x = DatePropertyAtomElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						} else if(elementID2.equals(IntegerPropertyAtomElement.ID)){
							IntegerPropertyAtomElement x = IntegerPropertyAtomElement.read(workingContext);
							_jtNodes.put(x.getObjectID(), x);

						// Skip unevaluated element
						} else {
							addUnsupportedEntity(elementID2);
							int bytesToSkip = elementHeader2.getElementLength() - headerSize + 4;
							Helper.readBytes(workingContext.getByteBuffer(), bytesToSkip);
						}

						// Check whether a next element is available
						int currentPosition = workingContext.getByteBuffer().position();

						// Skip next elements length
						Helper.readI32(workingContext.getByteBuffer());

						// Possibly break endless loop
						GUID nextGUID = GUID.read(workingContext);
						if(nextGUID.toString().equals(GUID.END_OF_ELEMENTS)){
							break;
						}

						workingContext.getByteBuffer().position(currentPosition);
					}

					// Read Property Table
					//---------------------
					_propertyTable = PropertyTable.read(workingContext);

				// Extract the point shape definition data
				} else if(elementID.equals(PointSetShapeLODElement.ID)){
					// Store the position for later reading
					_xSetShapeLODElements.put(segmentID, workingContext.getByteBuffer().position());

				// Extract the polyline shape definition data
				} else if(elementID.equals(PolylineSetShapeLODElement.ID)){
					// Store the position for later reading
					_xSetShapeLODElements.put(segmentID, workingContext.getByteBuffer().position());

				// Skip unevaluated element
				} else {
					addUnsupportedEntity(elementID);
				}
			}

			// Create the LSG tree
			createLSG(_rootNode.getObjectID(), _jtNodes);

			// Extract the geometry and fill the JTModel
			walkLSGTree(null, byteBuffer, workingContext);

		} catch(Exception exception){
			addLoadInformation("ERROR", exception.getMessage());
			throw exception;
		}
	}

	/**
	 * Create the tree structure.
	 * @param objectID LSG root node ID
	 * @param nodes    Map of all nodes (object ID -> node)
	 */
	private void createLSG(int objectID, HashMap<Integer, JTNode> nodes){
		LSGNode parentNode = (LSGNode)nodes.get(objectID);

		// Attach attributes to node
		int[] attributeObjectIDs = parentNode.getAttributeObjectIDs();
		for(int i = 0; i < attributeObjectIDs.length; i++){
			JTNode attributeNode = nodes.get(attributeObjectIDs[i]);
			parentNode.addAttributeNode(attributeNode);
		}

		// Attach properties to node
		NodePropertyTable nodePropertyTable = _propertyTable.getNodePropertyTable(objectID);
		if(nodePropertyTable != null){
			HashMap<Integer, Integer> keyValuePairs = nodePropertyTable.getKeyValuePairs();
			for(Iterator<Entry<Integer, Integer>> iterator = keyValuePairs.entrySet().iterator(); iterator.hasNext();){
				Map.Entry<Integer, Integer> entry = iterator.next();
				parentNode.addPropertyNode(nodes.get(entry.getKey()), nodes.get(entry.getValue()));
			}
		}

		// Attach child nodes to node
		int[] childNodeIDs = parentNode.getChildNodeObjectIDs();
		for(int i = 0; i < childNodeIDs.length; i++){
			LSGNode childNode = (LSGNode)nodes.get(childNodeIDs[i]);
			if(childNode == null){
				addLoadInformation("WARNING", "Object " + parentNode.getObjectID() + " (" + parentNode.getClass().getName() + ") references a not existing / unsupported child node: " + childNodeIDs[i]);
				continue;
			}

			// For multiple instances, add a copy of the sub tree
			if(childNode.getParentLSGNode() != null){
				LSGNode clonedChildNode = childNode.copy(parentNode);
				parentNode.addChildLSGNode(clonedChildNode);
				break;
			}

			parentNode.addChildLSGNode(childNode);
			childNode.setParentLSGNode(parentNode);

			// Continue the recursion
			createLSG(childNodeIDs[i], nodes);
		}
	}

	/**
	 * Walks down the LSG and creates the geometry.
	 * @param lsgNode        LSG node to process
	 * @param byteBuffer     Byte buffer
	 * @param workingContext Working context
	 */
	private void walkLSGTree(LSGNode lsgNode, ByteBuffer byteBuffer, WorkingContext workingContext){
		if(lsgNode == null){
			lsgNode = _rootNode;
		}

		// Create local defined geometry
		String nodeName = "";
		if(	(lsgNode instanceof TriStripSetShapeNodeElement) ||
			(lsgNode instanceof PolylineSetShapeNodeElement) ||
			(lsgNode instanceof PointSetShapeNodeElement)){
			// Get the color
			Color color = getColorFromParentNodes(lsgNode);

			// Get the transformation matrix
			Matrix4d transformation = getTransformationFromParentNodes(lsgNode);

			// Get the layer name
			nodeName = getLayerName(lsgNode);

			// Get the geometry
			boolean foundLateLoadedPropertyAtomElement = false;
			Map<JTNode, JTNode> keyValuePairs = lsgNode.getPropertyNodes();
			for(Iterator<Entry<JTNode, JTNode>> iterator = keyValuePairs.entrySet().iterator(); iterator.hasNext();){
				Map.Entry<JTNode, JTNode> entry = iterator.next();
				if(entry.getValue() instanceof LateLoadedPropertyAtomElement){
					if(!foundLateLoadedPropertyAtomElement){
						foundLateLoadedPropertyAtomElement = true;
						LateLoadedPropertyAtomElement lateLoadedPropertyAtomElement = (LateLoadedPropertyAtomElement)entry.getValue();
						String segmentID = lateLoadedPropertyAtomElement.getSegmentID();

						// Faces
						if(lsgNode instanceof TriStripSetShapeNodeElement){
							int currentPosition = byteBuffer.position();
							byteBuffer.position(_xSetShapeLODElements.get(segmentID));
							TriStripSetShapeLODElement triStripSetShapeLODElement = null;
							try {
								workingContext.setByteBuffer(byteBuffer);
								triStripSetShapeLODElement = TriStripSetShapeLODElement.read(workingContext);
	
							} catch(UnsupportedCodecException exception){
								addLoadInformation("WARNING", exception.getMessage());
							}
							byteBuffer.position(currentPosition);
	
							if(triStripSetShapeLODElement != null){
								prepareGeometry(lsgNode.getObjectID(), triStripSetShapeLODElement, null, null, transformation, color, nodeName);
							}

						// Polylines
						} else if(lsgNode instanceof PolylineSetShapeNodeElement){
							int currentPosition = byteBuffer.position();
							byteBuffer.position(_xSetShapeLODElements.get(segmentID));
							PolylineSetShapeLODElement polylineSetShapeLODElement = null;
							try {
								workingContext.setByteBuffer(byteBuffer);
								polylineSetShapeLODElement = PolylineSetShapeLODElement.read(workingContext);
	
							} catch(UnsupportedCodecException exception){
								addLoadInformation("WARNING", exception.getMessage());
							}
							byteBuffer.position(currentPosition);
	
							if(polylineSetShapeLODElement != null){
								prepareGeometry(lsgNode.getObjectID(), null, polylineSetShapeLODElement, null, transformation, color, nodeName);
							}

						// Points
						} else if(lsgNode instanceof PointSetShapeNodeElement){
							int currentPosition = byteBuffer.position();
							byteBuffer.position(_xSetShapeLODElements.get(segmentID));
							PointSetShapeLODElement pointSetShapeLODElement = null;
							try {
								workingContext.setByteBuffer(byteBuffer);
								pointSetShapeLODElement = PointSetShapeLODElement.read(workingContext);
	
							} catch(UnsupportedCodecException exception){
								addLoadInformation("WARNING", exception.getMessage());
							}
							byteBuffer.position(currentPosition);
	
							if(pointSetShapeLODElement != null){
								prepareGeometry(lsgNode.getObjectID(), null, null, pointSetShapeLODElement, transformation, color, nodeName);
							}
						}

					} else {
						addLoadInformation("WARNING", "Object " + lsgNode.getObjectID() + " has multiple LateLoadedPropertyAtomElement assignments!");
					}
				}
			}

		// Load external referenced geometry
		} else if(lsgNode instanceof PartitionNodeElement){
			PartitionNodeElement partitionNodeElement = (PartitionNodeElement)lsgNode;
			if(lsgNode.getParentLSGNode() != null){
				String urlAsString = _baseURLName.toString();

				// Get the absolute external reference file name
				URL externalReference = null;
				try {
					urlAsString = urlAsString.substring(0, urlAsString.lastIndexOf("/")) + File.separator + partitionNodeElement.getFileName();
					externalReference = new URL(urlAsString);
				} catch(Exception exception){
					_jtModel.addExternalReference(partitionNodeElement.getFileName(), false);
					addLoadInformation("WARNING", "Found malformed external reference: " + urlAsString);
				}

				if(!existsURL(externalReference.toString())){
					_jtModel.addExternalReference(partitionNodeElement.getFileName(), false);
					addLoadInformation("WARNING", "Found missing external reference: " + externalReference);

				} else {
					_jtModel.addExternalReference(partitionNodeElement.getFileName(), true);

					// Get the transformation matrix
					Matrix4d transformation = getTransformationFromParentNodes(lsgNode);

					// Extract the rotation from the transformation
					Matrix4d rotation = (Matrix4d)transformation.clone();
					rotation.setTranslation(new Vector3d());

					// Load the referenced file
					try {
						URL oldURLName = _currentURLName;
						JTImporter jtImporter = new JTImporter();
						jtImporter.loadFile(externalReference, true);

						// Transfer the load information
						for(String[] loadInformation : jtImporter.getLoadInformation()){
							addLoadInformation(loadInformation[0], loadInformation[1]);
						}

						// Transfer the unsupported entities
						for(String unsupportedEntity : jtImporter.getUnsupportedEntities()){
							addUnsupportedEntity(unsupportedEntity);
						}

						HashMap<String, ArrayList<Object[]>> jtEntities = jtImporter.getFaces();
						for(Iterator<String> iterator = jtEntities.keySet().iterator(); iterator.hasNext();){
							String layerName = iterator.next();
							ArrayList<Object[]> faces = jtEntities.get(layerName);
							for(Object[] faceList : faces){
								double[] vertices = (double[])faceList[0];
								int[] indices = (int[])faceList[1];
								double[] colors = (double[])faceList[2];
								double[] normals = (double[])faceList[3];

								// Apply the transformation to all vertices
								for(int i = 0; i < vertices.length; i += 3){
									Point3d vertex = new Point3d(vertices[i], vertices[i + 1], vertices[i + 2]);
									transformation.transform(vertex);
									vertices[i]     = vertex.getX();
									vertices[i + 1] = vertex.getY();
									vertices[i + 2] = vertex.getZ();
								}

								// Apply the transformation to all normals
								for(int i = 0; i < normals.length; i += 3){
									Point3d normal = new Point3d(normals[i], normals[i + 1], normals[i + 2]);
									rotation.transform(normal);
									normals[i]     = normal.getX();
									normals[i + 1] = normal.getY();
									normals[i + 2] = normal.getZ();
								}

								// Add the new positioned face
								_jtModel.addTriangles(vertices, indices, colors, normals, layerName);
							}
						}

						_currentURLName = oldURLName;

					} catch(Exception exception){
						addLoadInformation("WARNING", "Failed loading external reference: " + externalReference.toString());
					}
				}
			}
		}

		for(LSGNode childNode : lsgNode.getChildLSGNodes()){
			walkLSGTree(childNode, byteBuffer, workingContext);

			// Skip all other LOD's
			if(lsgNode instanceof RangeLODNodeElement){
				break;
			}
		}
	}

	/**
	 * Verifies, whether the given URL points to a valid target. If the URL points to
	 * a HTTP folder, some server deny the access and return a HTTP_FORBIDDEN (403).
	 * @param  urlAsString Full URL
	 * @return             Does the target of the URL exists?
	 */
	private boolean existsURL(final String urlAsString){
		// The URL points to a local file
		if(urlAsString.startsWith("file:")){
			File file = new File(urlAsString.substring(5));
			return file.exists();
		}

		// The URL points to a protocol file
		try {
			URLConnection connection = new URL(urlAsString).openConnection();
			if(connection instanceof HttpURLConnection){
				HttpURLConnection.setFollowRedirects(false);
				HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
				httpURLConnection.setRequestMethod("HEAD");
				return (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK);

			} else {
				System.err.println("URLConnection '" + connection + "' not supported!");
				return false;
			}

		} catch(ConnectException exception){
			return false;

		} catch(UnknownHostException exception){
			return false;

		} catch(Exception exception){
			exception.printStackTrace();
			return false;
		}
	}

	/**
	 * Detects the color for the given node.
	 * @param  lsgNode LSG node to examine
	 * @return         Detected color or<br>
	 *                 <b>null</b> if the color couldn't be found
	 */
	private Color getColorFromParentNodes(LSGNode lsgNode){
		Color color = null;
		boolean foundMaterialAttributeElement = false;
		for(JTNode jtNode : lsgNode.getAttributeNodes()){
			if(jtNode instanceof MaterialAttributeElement){
				if(!foundMaterialAttributeElement){
					foundMaterialAttributeElement = true;
					color = ((MaterialAttributeElement)jtNode).getDiffuseColor();
				}
			}
		}
		if(color == null){
			if(lsgNode.getParentLSGNode() != null){
				color = getColorFromParentNodes(lsgNode.getParentLSGNode());
			}
		}
		if(color == null){
			color = DEFAULT_COLOR;
		}
		return color;
	}

	/**
	 * Transformation of the given node.
	 * @param  lsgNode LSG node to examine
	 * @return         Detected transformation
	 */
	private Matrix4d getTransformationFromParentNodes(LSGNode lsgNode){
		Matrix4d transformation = new Matrix4d();
		transformation.setIdentity();

		do {
			for(JTNode jtNode : lsgNode.getAttributeNodes()){
				if(jtNode instanceof GeometricTransformAttributeElement){
					Matrix4d tmp = (Matrix4d)((GeometricTransformAttributeElement)jtNode).getTransformationMatrix().clone();
					tmp.mul(transformation);
					transformation = tmp;
				}
			}
			lsgNode = lsgNode.getParentLSGNode();
		} while(lsgNode.getParentLSGNode() != null);

		return transformation;
	}

	/**
	 * Layer name of the given node.
	 * @param  lsgNode LSG node to examine
	 * @return         Detected layer name or<br>
	 *                 <b>JTImporter.DEFAULT_LAYER</b> if the layer name couldn't be found
	 */
	private String getLayerName(LSGNode lsgNode){
		List<String> nodeNameList = new ArrayList<String>();
		while(lsgNode != null){
			if(	(lsgNode instanceof MetaDataNodeElement) || (lsgNode instanceof InstanceNodeElement) ||
				(lsgNode instanceof PartNodeElement) || (lsgNode instanceof PartitionNodeElement)){
				String nodeName = getNodeName(lsgNode);
				if(nodeName != null){
					if((nodeNameList.size() == 0) || !nodeNameList.get(0).equals(nodeName)){
						if(lsgNode instanceof InstanceNodeElement){
							if(nodeName.endsWith("_SOLIDS")){
								nodeName = nodeName.substring(0, nodeName.length() - 7);
							} else if(nodeName.endsWith("_FACETS")){
								nodeName = nodeName.substring(0, nodeName.length() - 7);
							} else if(nodeName.endsWith("_WF")){
								nodeName = nodeName.substring(0, nodeName.length() - 3);
							}
						}
						nodeNameList.add(0, nodeName);
					}
				}
			}
			lsgNode = lsgNode.getParentLSGNode();
		}

		StringBuffer stringBuffer = new StringBuffer();
		Iterator<String> iterator = nodeNameList.iterator();
		while(iterator.hasNext()){
			stringBuffer.append(iterator.next());
			if(iterator.hasNext()){
				stringBuffer.append("#");
			}
		}
		return stringBuffer.length() > 0 ? stringBuffer.toString() : JTImporter.DEFAULT_LAYER;
	}

	/**
	 * Returns the node name.
	 * @param  lsgNode LSG node
	 * @return         Name of the node
	 */
	private String getNodeName(LSGNode lsgNode){
		String layerName = null;

		if(lsgNode != null){
			Map<JTNode, JTNode> propertyNodes = lsgNode.getPropertyNodes();
			for(Iterator<JTNode> iterator = propertyNodes.keySet().iterator(); iterator.hasNext();){
				JTNode propertyNode = iterator.next();
				if(propertyNode instanceof StringPropertyAtomElement){
					String value = ((StringPropertyAtomElement)propertyNode).getValue();
					if(value.equals("JT_PROP_NAME")){

						if((layerName == null) || layerName.equals(JTImporter.DEFAULT_LAYER)){
							layerName = ((StringPropertyAtomElement)propertyNodes.get(propertyNode)).getValue();
							int index = layerName.indexOf(".part");
							if(index != -1){
								layerName = layerName.substring(0, index);

							}
							index = layerName.indexOf(".asm");
							if(index != -1){
								layerName = layerName.substring(0, index);
							}
						}


					// If set, overwrite the JT_PROP_NAME
					} else if(value.equals("Name::")){
						layerName = ((StringPropertyAtomElement)propertyNodes.get(propertyNode)).getValue();
					}
				}
			}
		}

		return layerName;
	}

	/**
	 * Fills the JT models with the triangulated faces.
	 * @param parentNodeObjectID         Object ID of the parent LSG node
	 * @param triStripSetShapeLODElement TriStripSetShapeLODElement
	 * @param polylineSetShapeLODElement PolylineSetShapeLODElement
	 * @param pointSetShapeLODElement    PointSetShapeLODElement
	 * @param transformation             Transformation
	 * @param globalColor                Default color
	 * @param layerName                  Layer name
	 */
	private void prepareGeometry(int parentNodeObjectID, TriStripSetShapeLODElement triStripSetShapeLODElement, PolylineSetShapeLODElement polylineSetShapeLODElement, PointSetShapeLODElement pointSetShapeLODElement, Matrix4d transformation, Color globalColor, String layerName){
		try {
			if(_jtModel.getJTFileVersion() < 9.0){
				if(triStripSetShapeLODElement != null){
					VertexBasedShapeCompressedRepData vertexBasedShapeCompressedRepData = triStripSetShapeLODElement.getVertexBasedShapeCompressedRepData();
					List<Double> normalsAsList = vertexBasedShapeCompressedRepData.getNormals();
					List<Float> colorsAsList = vertexBasedShapeCompressedRepData.getColors();
					List<Integer> indicesAsList = vertexBasedShapeCompressedRepData.getIndices();
					List<Double> verticesAsList = vertexBasedShapeCompressedRepData.getVertices();

					if((verticesAsList == null) || (verticesAsList.size() == 0)){
						return;
					}

					// Extract the rotation from the transformation
					Matrix4d rotation = (Matrix4d)transformation.clone();
					rotation.setTranslation(new Vector3d());

					// Calculate the number of vertices and faces
					int vertexCount = 0;
					int faceCount = 0;
					for(int i = 0; i < (indicesAsList.size() - 1); i++){
						int startIndex = indicesAsList.get(i);
						int endIndex = indicesAsList.get(i + 1);
						vertexCount += (endIndex - startIndex);
						faceCount += (endIndex - startIndex - 2);
					}

					// Possibly create color list
					double[] colors = null;
					if((colorsAsList == null) || (colorsAsList.size() == 0)){
						float[] color = globalColor.getColorComponents(null);
						colors = new double[faceCount * 3];
						for(int i = 0; i < colors.length; i += 3){
							colors[i]     = color[0];
							colors[i + 1] = color[1];
							colors[i + 2] = color[2];
						}
					} else {
						colors = new double[colorsAsList.size()];
						for(int i = 0; i < colors.length; i++){
							colors[i] = colorsAsList.get(i);
						}
					}

					// Create the vertex and index list
					double[] vertices = new double[vertexCount * 3];
					double[] normals = new double[vertexCount * 3];
					int[] faceIndices = new int[faceCount * 3];

					int l = 0;
					for(int i = 0; i < (indicesAsList.size() - 1); i++){
						int startIndex = indicesAsList.get(i);
						int endIndex = indicesAsList.get(i + 1);

						// Fill the vertex list
						for(int j = startIndex; j < endIndex; j++){
							int k = j * 3;

							// Apply the transformation to each vertex
							Point3d vertex = new Point3d(verticesAsList.get(k), verticesAsList.get(k + 1), verticesAsList.get(k + 2));
							transformation.transform(vertex);
							vertices[k]     = vertex.getX();
							vertices[k + 1] = vertex.getY();
							vertices[k + 2] = vertex.getZ();

							// Apply the rotation to each normal 
							Point3d normal = new Point3d(normalsAsList.get(k), normalsAsList.get(k + 1), normalsAsList.get(k + 2));
							rotation.transform(normal);
							normals[k]     = normal.getX();
							normals[k + 1] = normal.getY();
							normals[k + 2] = normal.getZ();
						}

						// Fill the index list
						for(int j = startIndex; j < (endIndex - 2); j++){
							faceIndices[l]     = j;
							faceIndices[l + 1] = j + 1;
							faceIndices[l + 2] = j + 2;
							l += 3;
						}
					}
					_jtModel.addTriangles(vertices, faceIndices, colors, normals, layerName);

				} else if(pointSetShapeLODElement != null){
					VertexBasedShapeCompressedRepData vertexBasedShapeCompressedRepData = pointSetShapeLODElement.getVertexBasedShapeCompressedRepData();
					List<Double> vertices = vertexBasedShapeCompressedRepData.getVertices();
					List<Float> colors = vertexBasedShapeCompressedRepData.getColors();
					if((colors == null) || (colors.size() == 0)){
						colors = new ArrayList<Float>();
						float[] color = globalColor.getColorComponents(null);
						for(int i = 0; i < vertices.size(); i += 3){
							colors.add(color[0]);
							colors.add(color[1]);
							colors.add(color[2]);
						}
					}
					_jtModel.addPoints(vertices, colors, layerName);
				}

			// JT version 9+
			} else {
				VertexShapeLODElement vertexShapeLODElement = null;
				if(triStripSetShapeLODElement != null){
					vertexShapeLODElement = triStripSetShapeLODElement.getVertexShapeLODElement();
					List<Double> normalsAsList = vertexShapeLODElement.getNormals();
					List<Double> colorsAsList = vertexShapeLODElement.getColors();
					List<List<Integer>> indexLists = vertexShapeLODElement.getIndices();
					List<Double> verticesAsList = vertexShapeLODElement.getVertices();

					if((verticesAsList == null) || (verticesAsList.size() == 0) || (indexLists.get(0).size() == 0)){
						addLoadInformation("WARNING", "Found empty element!");
						return;
					}

					// Extract the rotation from the transformation
					Matrix4d rotation = (Matrix4d)transformation.clone();
					rotation.setTranslation(new Vector3d());

					List<Integer> vertexIndicesList = indexLists.get(0);
					List<Integer> normalIndicesList = indexLists.get(1);

					double[] verticesNew = new double[vertexIndicesList.size() * 3];
					int[] indicesNew = new int[vertexIndicesList.size()];
					double[] normalsNew = new double[vertexIndicesList.size() * 3];
					int lastNormalIndex = -1;
					for(int i = 0, vertexCount = 0, normalCount = 0; i < (vertexIndicesList.size() / 3); i++){
						int baseIndex = (i * 3);

						int faceIndex1 = vertexIndicesList.get(baseIndex);
						int faceIndex2 = vertexIndicesList.get(baseIndex + 1);
						int faceIndex3 = vertexIndicesList.get(baseIndex + 2);

						int normalIndex1 = normalIndicesList.get(baseIndex);
						int normalIndex2 = normalIndicesList.get(baseIndex + 1);
						int normalIndex3 = normalIndicesList.get(baseIndex + 2);

						if(normalIndex1 == -1){
							normalIndex1 = lastNormalIndex;
						}
						lastNormalIndex = normalIndex1;
						if(normalIndex2 == -1){
							normalIndex2 = lastNormalIndex;
						}
						lastNormalIndex = normalIndex2;
						if(normalIndex3 == -1){
							normalIndex3 = lastNormalIndex;
						}
						lastNormalIndex = normalIndex3;

						indicesNew[baseIndex]     = baseIndex;
						indicesNew[baseIndex + 1] = baseIndex + 1;
						indicesNew[baseIndex + 2] = baseIndex + 2;

						// Apply the transformation to each vertex
						Point3d vertex = new Point3d(	verticesAsList.get((faceIndex1 * 3)),
														verticesAsList.get((faceIndex1 * 3) + 1),
														verticesAsList.get((faceIndex1 * 3) + 2));
						transformation.transform(vertex);
						verticesNew[vertexCount++] = vertex.getX();
						verticesNew[vertexCount++] = vertex.getY();
						verticesNew[vertexCount++] = vertex.getZ();

						vertex = new Point3d(	verticesAsList.get((faceIndex2 * 3)),
												verticesAsList.get((faceIndex2 * 3) + 1),
												verticesAsList.get((faceIndex2 * 3) + 2));
						transformation.transform(vertex);
						verticesNew[vertexCount++] = vertex.getX();
						verticesNew[vertexCount++] = vertex.getY();
						verticesNew[vertexCount++] = vertex.getZ();

						vertex = new Point3d(	verticesAsList.get((faceIndex3 * 3)),
												verticesAsList.get((faceIndex3 * 3) + 1),
												verticesAsList.get((faceIndex3 * 3) + 2));
						transformation.transform(vertex);
						verticesNew[vertexCount++] = vertex.getX();
						verticesNew[vertexCount++] = vertex.getY();
						verticesNew[vertexCount++] = vertex.getZ();

						// Apply the rotation to each normal 
						Point3d normal = new Point3d(	normalsAsList.get((normalIndex1 * 3)),
														normalsAsList.get((normalIndex1 * 3) + 1),
														normalsAsList.get((normalIndex1 * 3) + 2));
						rotation.transform(normal);
						normalsNew[normalCount++] = normal.getX();
						normalsNew[normalCount++] = normal.getY();
						normalsNew[normalCount++] = normal.getZ();

						normal = new Point3d(	normalsAsList.get((normalIndex2 * 3)),
												normalsAsList.get((normalIndex2 * 3) + 1),
												normalsAsList.get((normalIndex2 * 3) + 2));
						rotation.transform(normal);
						normalsNew[normalCount++] = normal.getX();
						normalsNew[normalCount++] = normal.getY();
						normalsNew[normalCount++] = normal.getZ();

						normal = new Point3d(	normalsAsList.get((normalIndex3 * 3)),
												normalsAsList.get((normalIndex3 * 3) + 1),
												normalsAsList.get((normalIndex3 * 3) + 2));
						rotation.transform(normal);
						normalsNew[normalCount++] = normal.getX();
						normalsNew[normalCount++] = normal.getY();
						normalsNew[normalCount++] = normal.getZ();
					}

					// Possibly create color list
					double[] colors = null;
					if((colorsAsList == null) || (colorsAsList.size() == 0)){
						float[] color = globalColor.getColorComponents(null);
						colors = new double[vertexIndicesList.size()];
						for(int i = 0; i < colors.length; i += 3){
							colors[i]     = color[0];
							colors[i + 1] = color[1];
							colors[i + 2] = color[2];
						}
					} else {
						colors = new double[colorsAsList.size()];
						for(int i = 0; i < colors.length; i++){
							colors[i] = colorsAsList.get(i);
						}
					}

					_jtModel.addTriangles(verticesNew, indicesNew, colors, normalsNew, layerName);

				} else if(polylineSetShapeLODElement != null) {
					VertexShapeLODData vertexShapeLODData = polylineSetShapeLODElement.getVertexShapeLODData();
					TopoMeshCompressedLODData topoMeshCompressedLODData = vertexShapeLODData.getTopoMeshCompressedLODData();
					TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1 = topoMeshCompressedLODData.getTopoMeshCompressedRepDataV1();
					if(topoMeshCompressedRepDataV1 == null){
						topoMeshCompressedRepDataV1 = topoMeshCompressedLODData.getTopoMeshCompressedRepDataV2().getTopoMeshCompressedRepDataV1();
					}

					List<Double> colorsAsList = null;
					if(topoMeshCompressedRepDataV1.getCompressedVertexColorArray() != null){
						colorsAsList = topoMeshCompressedRepDataV1.getCompressedVertexColorArray().getColors();
					}

					List<Integer> vertexIndicesList = topoMeshCompressedRepDataV1.getVertexListIndices();
					List<Integer> primitiveIndicesList = topoMeshCompressedRepDataV1.getPrimitiveListIndices();
					if(topoMeshCompressedRepDataV1.getCompressedVertexCoordinateArray() == null){
						return;
					}
					List<Double> verticesAsList = topoMeshCompressedRepDataV1.getCompressedVertexCoordinateArray().getVertices();

					if(colorsAsList == null){
						colorsAsList = new ArrayList<Double>();
						float[] color = globalColor.getColorComponents(null);
						for(int i = 0; i < verticesAsList.size(); i += 3){
							colorsAsList.add((double)color[0]);
							colorsAsList.add((double)color[1]);
							colorsAsList.add((double)color[2]);
						}
					}

					// Extract the rotation from the transformation
					Matrix4d rotation = (Matrix4d)transformation.clone();
					rotation.setTranslation(new Vector3d());

					for(int i = 0; i < (primitiveIndicesList.size() - 1); i++){
						int startIndex = primitiveIndicesList.get(i);
						int endIndex = primitiveIndicesList.get(i + 1);

						// Fill the vertex list
						List<Double[]> polylineVertices = new ArrayList<Double[]>();
						List<Double[]> polylineColors = new ArrayList<Double[]>();
						for(int j = startIndex; j < endIndex; j++){
							int vertexIndex = vertexIndicesList.get(j) * 3;
							double x = verticesAsList.get(vertexIndex);
							double y = verticesAsList.get(vertexIndex + 1);
							double z = verticesAsList.get(vertexIndex + 2);

							// Apply the transformation to each vertex
							Point3d vertex = new Point3d(x, y, z);
							transformation.transform(vertex);

							// Add the transformed vertex
							polylineVertices.add(new Double[]{vertex.getX(), vertex.getY(), vertex.getZ()});
							polylineColors.add(new Double[]{colorsAsList.get(vertexIndex),
															colorsAsList.get(vertexIndex + 1),
															colorsAsList.get(vertexIndex + 2)});
						}
						_jtModel.addPolyline(polylineVertices, polylineColors, layerName);
					}
				}
			}
		} catch(Exception exception){
			exception.printStackTrace();
			addLoadInformation("WARNING", "Failed decoding node element: " + layerName + " (" + exception.getMessage() + ")");
		}
	}

	/**
	 * Adds an unique load information message.
	 * @param type    Message type
	 * @param message Message text
	 */
	public static void addLoadInformation(String type, String message){
		for(String[] information : _loadInformation){
			if(information[0].equals(type) && information[1].equals(message)){
				return;
			}
		}
		_loadInformation.add(new String[]{type, message});
	}

	/**
	 * Adds an unique unsupprted element.
	 * @param elementID Element ID
	 */
	private void addUnsupportedEntity(String elementID){
		String newUnsupportedEntityString = _guidMapping.containsKey(elementID) ? elementID + " (" + _guidMapping.get(elementID) + ")" : elementID;
		for(String unsupportedEntityString : _unsupportedEntities){
			if(unsupportedEntityString.equals(newUnsupportedEntityString)){
				return;
			}
		}
		_unsupportedEntities.add(newUnsupportedEntityString);
	}

	/**
	 * Returns the list of faces.
	 * @return List of faces, sorted by their layer
	 */
	public HashMap<String, ArrayList<Object[]>> getFaces(){
		return _jtModel.getFaces();
	}

	/**
	 * Returns the list of polylines.
	 * @return List of polylines sorted by their layers
	 */
	public HashMap<String, ArrayList<Object[]>> getPolylines(){
		return _jtModel.getPolylines();
	}

	/**
	 * Returns the list of points.
	 * @return List of points sorted by their layers
	 */
	public HashMap<String, ArrayList<Object[]>> getPoints(){
		return _jtModel.getPoints();
	}

	/**
	 * Returns a list of layer names with their visibility. 
	 * @return List of layer names with their visibility
	 */
	public HashMap<String, Boolean> getLayerMetaData(){
		return _jtModel.getLayerMetaData();
	}

	/**
	 * Returns the initial transformation matrix, stored by the manufacturer in the
	 * file. It specifies how the camera is directed to the model. 
	 * @return 4x4 matrix with the initial transformation or<br>
	 *         <b>null</b> if no such information has been stored
	 */
	public double[] getInitialTransformationMatrix(){
		return new double[]{1.0, 0.0, 0.0, 0.0,
							0.0, 1.0, 0.0, 0.0,
							0.0, 0.0, 1.0, 0.0,
							0.0, 0.0, 0.0, 1.0};
	}

	/**
	 * Returns the flag, telling whether the paper space is used or not.
	 * @return Is the paper space used?
	 */
	public boolean isPaperSpaceIsUsed(){
		return false;
	}

	/**
	 * Returns a flag, telling whether the loaded model is 2D (or 3D).
	 * @return Is the loaded model 2D?
	 */
	public boolean is2D(){
		double[][] extremeValues = _jtModel.getExtremeValues();
		return ((extremeValues[0][0] == extremeValues[0][1]) ||
				(extremeValues[1][0] == extremeValues[1][1]) ||
				(extremeValues[2][0] == extremeValues[2][1]));
	}

	/**
	 * Returns a flag, telling whether the loaded model contains faces.
	 * @return Does the loaded model contains faces?
	 */
	public boolean isShadingPossible(){
		return true;
	}

	/**
	 * Returns the extreme values.
	 * @return Extreme values (double[2][3] [x1, y1, z1] and [x2, y2, z2])
	 */
	public double[][] getExtremeValues(){
		return _jtModel.getExtremeValues();
	}

	/**
	 * Returns a list of infos and errors, occured while reading the file and
	 * building the model.
	 * @return List of string[2] with the infos and errors
	 */
	public ArrayList<String[]> getLoadInformation(){
		return _loadInformation;
	}

	/**
	 * Returns specific information about the file and the model.
	 * @return List of string[2] containing all information
	 */
	public ArrayList<String[]> getModelInformation(){
		return _jtModel.getModelInformation();
	}

	/**
	 * Called every time when a group code has been read.
	 * @param readBytes Number of read bytes
	 */
	public static void updateProgress(int readBytes){
		if(_readBytes == null){
			return;
		}

		_readBytes.put(_currentURLName, _readBytes.get(_currentURLName) + readBytes);
		_progressIntervall.put(_currentURLName, _progressIntervall.get(_currentURLName) + readBytes);

		if(_progressListener != null){
			if(_progressIntervall.get(_currentURLName) > ProgressListenerInterface.PROGRESS_UPDATER_FREQUENCY_PARSER){
				_progressIntervall.put(_currentURLName, _progressIntervall.get(_currentURLName) - ProgressListenerInterface.PROGRESS_UPDATER_FREQUENCY_PARSER);
				for(int i = 0; i < _progressListener.size(); i++){
					ProgressListenerInterface progressListenerInterface = _progressListener.get(i);
					if(progressListenerInterface != null){
						progressListenerInterface.progressChanged(new ProgressEvent((byte)((_readBytes.get(_currentURLName) * 100.0) / _fileLength.get(_currentURLName))));
					}
				}
			}
		}
	}

	/**
	 * Adds a progress listener, called when the progress has changed.
	 * @param progressListenerInterface Progress listener
	 */
	public void addProgressListener(ProgressListenerInterface progressListenerInterface){
		if(_progressListener == null){
			_progressListener = new ArrayList<ProgressListenerInterface>();
		}

		_progressListener.add(progressListenerInterface);
	}

	/**
	 * Returns the unsupported entities.
	 * @return           List of unsupported entities
	 * @throws Exception Thrown if something failed
	 */
	public ArrayList<String> getUnsupportedEntities() throws Exception {
		return _unsupportedEntities;
	}
}
