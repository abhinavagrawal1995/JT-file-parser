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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.JTImporter;

/**
 * Test class for the JT importer.
 */
public class TestJTImporter {
	/**
	 * Test export.
	 * @param jtImporter    Instance of the JT importer
	 * @param testDirectory Base directory with the JT files
	 */
	private void testImport(JTImporter jtImporter, String testDirectory){
		File sourceDirectory = new File(testDirectory);

		for(String fileName : sourceDirectory.list()){
			try {
				// Load the JT file
				System.out.println("\nLoading: " + fileName);
				jtImporter.loadFile(new File(sourceDirectory + File.separator + fileName).toURI().toURL());

				// Print all available information
				printInformation(jtImporter);

			} catch(Exception exception){
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Prints information, available after loading the file.
	 * @param  jtImporter JT importer
	 * @throws Exception  Thrown when something happens
	 */
	@SuppressWarnings("unchecked")
	private void printInformation(JTImporter jtImporter) throws Exception {
		System.out.println("\nLoad information:");
		System.out.println("--------------------------------------------------");
		ArrayList<String[]> loadInformation = jtImporter.getLoadInformation();
		if(loadInformation.size() == 0){
			System.out.println("   ---");
		} else {
			for(String[] information : loadInformation){
				System.out.println("   " + information[0] + ": " + information[1]);
			}
		}

		System.out.println("\nModel information:");
		System.out.println("--------------------------------------------------");
		for(String[] modelInformation : jtImporter.getModelInformation()){
			if(!modelInformation[0].equals("") && !modelInformation[1].equals("")){
				System.out.println("   " + modelInformation[0] + ": " + modelInformation[1]);
			} else if(!modelInformation[0].equals("")){
				System.out.println("   " + modelInformation[0]);
			} else if(!modelInformation[1].equals("")){
				System.out.println("   " + modelInformation[1]);
			} else {
				System.out.println();
			}
		}

		System.out.println("\nUnsupported entities:");
		System.out.println("--------------------------------------------------");
		for(String unsupportedEntity : jtImporter.getUnsupportedEntities()){
			System.out.println("   " + unsupportedEntity);
		}

		System.out.println("\nMeta data:");
		System.out.println("--------------------------------------------------");
		double[][] boundingBox = jtImporter.getExtremeValues();
		System.out.println(" ... BBox min: " + boundingBox[0][0] + ", " + boundingBox[0][1] + ", " + boundingBox[0][2]);
		System.out.println(" ... BBox max: " + boundingBox[1][0] + ", " + boundingBox[1][1] + ", " + boundingBox[1][2]);
		HashMap<String, Boolean> layerMetadata = jtImporter.getLayerMetaData();
		System.out.println(" ... Number of layers: " + layerMetadata.size());
		for(String layerName : layerMetadata.keySet()){
			System.out.println("     ... visibility: " + layerName + " => " + layerMetadata.get(layerName));
		}

		System.out.println("\nPoints:");
		System.out.println("--------------------------------------------------");
		HashMap<String, ArrayList<Object[]>> pointEntities = jtImporter.getPoints();
		System.out.println(" ... # layers with points: " + pointEntities.size());
		for(Iterator<String> iterator = pointEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> points = pointEntities.get(layerName);
			System.out.println("         ... # entities: " + points.size());
			for(Object[] point : points){
				List<Double> vertices = (List<Double>)point[0];
				System.out.println("             ... [entity 1] vertices: " + (vertices.size() / 3) + " => (showing 1) " + vertices.subList(0, 3));

				List<Float> colors = (List<Float>)point[1];
				System.out.println("             ... [entity 1] colors: " + (colors.size() / 3) + " => (showing 1) " + colors.subList(0, 3));

				if(points.size() > 1){
					System.out.println("             ...");
				}
				break;
			}
		}

		System.out.println("\nPolylines:");
		System.out.println("--------------------------------------------------");
		HashMap<String, ArrayList<Object[]>> polylineEntities = jtImporter.getPolylines();
		System.out.println(" ... # layers with polylines: " + polylineEntities.size());
		for(Iterator<String> iterator = polylineEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> polylines = polylineEntities.get(layerName);
			System.out.println("         ... # entities: " + polylines.size());
			for(Object[] polyline : polylines){
				List<Double[]> vertices = (List<Double[]>)polyline[0];
				System.out.println("             ... [entity 1] vertices: " + vertices.size() + " => (showing 1) " + Arrays.toString(vertices.get(0)));

				List<Double[]> colors = (List<Double[]>)polyline[1];
				System.out.println("             ... [entity 1] colors: " + colors.size() + " => (showing 1) " + Arrays.toString(colors.get(0)));

				if(polylines.size() > 1){
					System.out.println("             ...");
				}
				break;
			}
		}

		System.out.println("\nFaces:");
		System.out.println("--------------------------------------------------");
		HashMap<String, ArrayList<Object[]>> faceEntities = jtImporter.getFaces();
		System.out.println(" ... # layers with faces: " + faceEntities.size());
		for(Iterator<String> iterator = faceEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> faces = faceEntities.get(layerName);
			System.out.println("         ... # entities: " + faces.size());
			for(Object[] faceList : faces){
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				System.out.println("             ... [entity 1] vertices: " + vertices.length + " => (showing 1) [" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]");
				System.out.println("             ... [entity 1] indices: " + indices.length + " => (showing 3) [" + indices[0] + ", " + indices[1] + ", " + indices[2] + "]");
				System.out.println("             ... [entity 1] colors: " + colors.length + " => (showing 1) [" + colors[0] + ", " + colors[1] + ", " + colors[2] + "]");
				System.out.println("             ... [entity 1] normals: " + normals.length + " => (showing 1) [" + normals[0] + ", " + normals[1] + ", " + normals[2] + "]");

				if(faces.size() > 1){
					System.out.println("             ...");
				}
				break;
			}
		}
	}

	/**
	 * Main entry point.
	 * @param arguments Arguments of the command line
	 */
	public static void main(String[] arguments){
		TestJTImporter testJTImporter = new TestJTImporter();
		testJTImporter.testImport(new JTImporter(), "data");
	}
}
