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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Model, holding the attributes of a JT file.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class JTModel {
	/** List of model information */
	private ArrayList<String[]> _modelInformation;

	/** List of existing external references */
	private ArrayList<String> _existingReferences;

	/** List of missing external references */
	private ArrayList<String> _missingReferences;

	/** Smallest and biggest X, Y and Z value */
	private double[][] _extremeValues;

	/** Version of the JT file */
	private float _jtFileVersion;

	/** Coment of the JT file */
	private String _comment;

	/** List of faces, sorted by layer */
	private HashMap<String, ArrayList<Object[]>> _coloredFacesOnLayers;

	/** List of polylines, sorted by layer */
	private HashMap<String, ArrayList<Object[]>> _coloredPolylinesOnLayers;

	/** List of points, sorted by layer */
	private HashMap<String, ArrayList<Object[]>> _coloredPointsOnLayers;

	/**
	 * Constructor.
	 */
	public JTModel(){
		_coloredFacesOnLayers = new HashMap<String, ArrayList<Object[]>>();
		_coloredPolylinesOnLayers = new HashMap<String, ArrayList<Object[]>>();
		_coloredPointsOnLayers = new HashMap<String, ArrayList<Object[]>>();
		_existingReferences = new ArrayList<String>();
		_missingReferences = new ArrayList<String>();
	}

	/**
	 * Sets the version.
	 * @param version Version string
	 */
	public void setVersion(String version){
		_jtFileVersion = Float.parseFloat(version);
	}

	/**
	 * Returns the version of the JT file.
	 * @return Version of the JT file
	 */
	public float getJTFileVersion(){
		return _jtFileVersion;
	}

	/**
	 * Sets the comment.
	 * @param comment Comment string
	 */
	public void setComment(String comment){
		_comment = comment.trim();
	}

	/**
	 * Returns specific information about the file and the model.
	 * @return List of string[2] containing all information
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String[]> getModelInformation(){
		if(_modelInformation == null){
			int faceCount = 0;
			int polylineCount = 0;
			int pointCount = 0;

			for(Iterator<String> iterator = _coloredFacesOnLayers.keySet().iterator(); iterator.hasNext();){
				String layerName = iterator.next();
				ArrayList<Object[]> faces = _coloredFacesOnLayers.get(layerName);
				for(Object[] faceList : faces){
					int[] indices = (int[])faceList[1];
					faceCount += (indices.length / 3);
				}
			}

			for(Iterator<String> iterator = _coloredPolylinesOnLayers.keySet().iterator(); iterator.hasNext();){
				String layerName = iterator.next();
				ArrayList<Object[]> polylines = _coloredPolylinesOnLayers.get(layerName);
				polylineCount += polylines.size();
			}

			for(Iterator<String> iterator = _coloredPointsOnLayers.keySet().iterator(); iterator.hasNext();){
				String layerName = iterator.next();
				ArrayList<Object[]> points = _coloredPointsOnLayers.get(layerName);
				for(Object[] point : points){
					List<Double> vertices = (List<Double>)point[0];
					pointCount += (vertices.size() / 3);
				}
			}

			_modelInformation = new ArrayList<String[]>();
			_modelInformation.add(new String[]{"File format", "Jupiter Tesselation (JT)"});
			_modelInformation.add(new String[]{"Version", Float.toString(_jtFileVersion)});
			_modelInformation.add(new String[]{"Comment", _comment});
			_modelInformation.add(new String[]{"", ""});
			_modelInformation.add(new String[]{"Number of faces", Integer.toString(faceCount)});
			_modelInformation.add(new String[]{"Number of polylines", Integer.toString(polylineCount)});
			_modelInformation.add(new String[]{"Number of points", Integer.toString(pointCount)});
			_modelInformation.add(new String[]{"Number of existing references", Integer.toString(_existingReferences.size())});
			for(String reference : _existingReferences){
				_modelInformation.add(new String[]{"", reference});
			}
			_modelInformation.add(new String[]{"Number of missing references", Integer.toString(_missingReferences.size())});
			for(String reference : _missingReferences){
				_modelInformation.add(new String[]{"", reference});
			}
		}

		return _modelInformation;
	}

	/**
	 * Returns a list of layer names with their visibility. 
	 * @return List of layer names with their visibility
	 */
	public HashMap<String, Boolean> getLayerMetaData(){
		HashMap<String, Boolean> layerMetaData = new HashMap<String, Boolean>();
		for(Iterator<String> iterator = _coloredFacesOnLayers.keySet().iterator(); iterator.hasNext();){
			layerMetaData.put(iterator.next(), Boolean.TRUE);
		}
		for(Iterator<String> iterator = _coloredPolylinesOnLayers.keySet().iterator(); iterator.hasNext();){
			layerMetaData.put(iterator.next(), Boolean.TRUE);
		}
		for(Iterator<String> iterator = _coloredPointsOnLayers.keySet().iterator(); iterator.hasNext();){
			layerMetaData.put(iterator.next(), Boolean.TRUE);
		}
		return layerMetaData;
	}

	/**
	 * Returns the extreme values.
	 * @return Extreme values (double[2][3] [x1, y1, z1] and [x2, y2, z2])
	 */
	public double[][] getExtremeValues(){
		return _extremeValues;
	}

	/**
	 * Returns the list of faces.
	 * @return List of faces, sorted by their layers
	 */
	public HashMap<String, ArrayList<Object[]>> getFaces(){
		return _coloredFacesOnLayers;
	}

	/**
	 * Returns the list of polylines.
	 * @return List of polylines sorted by their layers
	 */
	public HashMap<String, ArrayList<Object[]>> getPolylines(){
		return _coloredPolylinesOnLayers;
	}

	/**
	 * Returns the list of points.
	 * @return List of points sorted by their layers
	 */
	public HashMap<String, ArrayList<Object[]>> getPoints(){
		return _coloredPointsOnLayers;
	}

	/**
	 * Adds many triangles.
	 * @param vertices  List of vertices
	 * @param indices   List of indices
	 * @param colors    List of color components
	 * @param normals   List of normals
	 * @param layerName Layer name
	 */
	public void addTriangles(double[] vertices, int[] indices, double[] colors, double[] normals, String layerName){
		for(int i = 0; i < vertices.length; i += 3){
			registerVertex(vertices[i + 0], vertices[i + 1], vertices[i + 2]);
		}

		// Possibly create layer
		if(!_coloredFacesOnLayers.containsKey(layerName)){
			_coloredFacesOnLayers.put(layerName, new ArrayList<Object[]>());
		}

		// Add the colored faces to their layer
		_coloredFacesOnLayers.get(layerName).add(new Object[]{vertices, indices, colors, normals});
	}

	/**
	 * Adds a polylines.
	 * @param vertices  List of vertices
	 * @param colors    List of color components
	 * @param layerName Layer name
	 */
	public void addPolyline(List<Double[]> vertices, List<Double[]> colors, String layerName){
		for(Double[] vertex : vertices){
			registerVertex(vertex[0], vertex[1], vertex[2]);
		}

		// Possibly create layer
		if(!_coloredPolylinesOnLayers.containsKey(layerName)){
			_coloredPolylinesOnLayers.put(layerName, new ArrayList<Object[]>());
		}

		// Add the colored polyline to it's layer
		_coloredPolylinesOnLayers.get(layerName).add(new Object[]{vertices, colors});
	}

	/**
	 * Adds many triangles.
	 * @param vertices  List of vertices
	 * @param colors    List of color components
	 * @param layerName Layer name
	 */
	public void addPoints(List<Double> vertices, List<Float> colors, String layerName){
		for(int i = 0; i < vertices.size(); i += 3){
			registerVertex(vertices.get(i), vertices.get(i + 1), vertices.get(i + 2));
		}

		// Possibly create layer
		if(!_coloredPointsOnLayers.containsKey(layerName)){
			_coloredPointsOnLayers.put(layerName, new ArrayList<Object[]>());
		}

		// Add the colored points to their layer
		_coloredPointsOnLayers.get(layerName).add(new Object[]{vertices, colors});
	}

	/**
	 * Adds a existing / missing reference.
	 * @param reference External file reference
	 * @param existing  Is the reference existing?
	 */
	public void addExternalReference(String reference, boolean existing){
		if(existing){
			if(!_existingReferences.contains(reference)){
				_existingReferences.add(reference);
			}
		} else {
			if(!_missingReferences.contains(reference)){
				_missingReferences.add(reference);
			}
		}
	}

	/**
	 * Register a vertex for detecting the extreme values.
	 * @param x X value
	 * @param y Y value
	 * @param z Z value
	 */
	private void registerVertex(double x, double y, double z){
		if(_extremeValues == null){
			_extremeValues = new double[][]{{x, y, z},
											{x, y, z}};
			return;
		}

		if(x < _extremeValues[0][0]){
			_extremeValues[0][0] = x;
		} else if(x > _extremeValues[1][0]){
			_extremeValues[1][0] = x;
		}

		if(y < _extremeValues[0][1]){
			_extremeValues[0][1] = y;
		} else if(y > _extremeValues[1][1]){
			_extremeValues[1][1] = y;
		}

		if(z < _extremeValues[0][2]){
			_extremeValues[0][2] = z;
		} else if(z > _extremeValues[1][2]){
			_extremeValues[1][2] = z;
		}
	}

	/**
	 * Checks whether the given layer name is already available.
	 * @param  layerName Layer name to check
	 * @return           Is the layer name already used?
	 */
	public boolean isLayerAvailable(String layerName){
		return (	_coloredFacesOnLayers.containsKey(layerName) ||
					_coloredPolylinesOnLayers.containsKey(layerName) ||
					_coloredPointsOnLayers.containsKey(layerName));
	}
}
