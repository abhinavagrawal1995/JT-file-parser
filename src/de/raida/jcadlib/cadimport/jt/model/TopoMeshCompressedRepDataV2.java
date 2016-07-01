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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP2;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>7.2.2.1.2.8 TopoMesh Compressed Rep Data V2</h>
 * Object Type ID: <code>---</code>
 * <br>TopoMesh Compressed Rep Data V2 data contains additional geometric shape data (auxiliary vertex
 * fields) that were not included in V1. Each Auxiliary field contains data that is parallel to the
 * existing vertex record fields in order capture additional information about each vertex ( e.g. Vertex
 * Identifiers, Weights, ... ).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class TopoMeshCompressedRepDataV2 {
	/** Topo mesh compressed rep data v1 */
	private TopoMeshCompressedRepDataV1 _topoMeshCompressedRepDataV1;

	/**
	 * Constructor.
	 * @param topoMeshCompressedRepDataV1 Topo mesh compressed rep data v1
	 * @param versionNumber               Version number
	 * @param vertexBindings              Vertex bindings
	 */
	public TopoMeshCompressedRepDataV2(TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1, int versionNumber, long vertexBindings){
		this(topoMeshCompressedRepDataV1, versionNumber, vertexBindings, -1, null, null, null, null, null, null, null, null, null);
	}

	/**
	 * 
	 * @param topoMeshCompressedRepDataV1 Topo mesh compressed rep data v1
	 * @param versionNumber               Version number
	 * @param vertexBindings              Vertex bindings
	 * @param numberOfAuxiliaryFields     Number of auxiliary fields
	 * @param uniqueFieldIdentifiers      Unique field identifiers
	 * @param fieldTypes                  Field types
	 * @param dataExponentsLists          Data exponents lists
	 * @param dataUpperMantissaeLists     Data upper mantissae lists
	 * @param dataLowerMantissaeLists     Data lower mantissae lists
	 * @param dataU320Lists               Data U32 0 lists
	 * @param dataU321Lists               Data U32 1 lists
	 * @param dataU322Lists               Data U32 2 lists
	 * @param auxiliaryDataHashs          Auxiliary data hashs
	 */
	public TopoMeshCompressedRepDataV2(TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1, int versionNumber, long vertexBindings,
			int numberOfAuxiliaryFields, GUID[] uniqueFieldIdentifiers, int[] fieldTypes, List<List<Integer>> dataExponentsLists,
			List<List<Integer>> dataUpperMantissaeLists, List<List<Integer>> dataLowerMantissaeLists, List<List<Integer>> dataU320Lists,
			List<List<Integer>> dataU321Lists, List<List<Integer>> dataU322Lists, int[] auxiliaryDataHashs){
		_topoMeshCompressedRepDataV1 = topoMeshCompressedRepDataV1;
	}

	/**
	 * Returns the TopoMeshCompressedRepDataV1
	 * @return TopoMeshCompressedRepDataV1
	 */
	public TopoMeshCompressedRepDataV1 getTopoMeshCompressedRepDataV1(){
		return _topoMeshCompressedRepDataV1;
	}

	/**
	 * Reads a TopoMeshCompressedRepDataV2 object.
	 * @param  workingContext            Working context
	 * @param  fromPolyLineShape         Called from a PolyLineSHape instance?
	 * @return                           TopoMeshCompressedRepDataV2 instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static TopoMeshCompressedRepDataV2 read(WorkingContext workingContext, boolean fromPolyLineShape) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		TopoMeshCompressedRepDataV1 topoMeshCompressedRepDataV1 = TopoMeshCompressedRepDataV1.read(workingContext, fromPolyLineShape);
		
		int versionNumber = Helper.readI16(byteBuffer);
		if(versionNumber != 1){
			throw new IllegalArgumentException("Found invalid version number: " + versionNumber);
		}

		long vertexBindings = Helper.readU64(byteBuffer);
		if((vertexBindings & 0x8000000000000000l) == 0){
			return new TopoMeshCompressedRepDataV2(topoMeshCompressedRepDataV1, versionNumber, vertexBindings);
		}

		int numberOfAuxiliaryFields = (int)Helper.readU32(byteBuffer);

		GUID[] uniqueFieldIdentifiers = new GUID[numberOfAuxiliaryFields];
		int[] fieldTypes = new int[numberOfAuxiliaryFields];
		List<List<Integer>> dataExponentsLists = new ArrayList<List<Integer>>();
		List<List<Integer>> dataUpperMantissaeLists = new ArrayList<List<Integer>>();
		List<List<Integer>> dataLowerMantissaeLists = new ArrayList<List<Integer>>();
		List<List<Integer>> dataU320Lists = new ArrayList<List<Integer>>();
		List<List<Integer>> dataU321Lists = new ArrayList<List<Integer>>();
		List<List<Integer>> dataU322Lists = new ArrayList<List<Integer>>();
		int[] auxiliaryDataHashs = new int[numberOfAuxiliaryFields];

		for(int i = 0; i < numberOfAuxiliaryFields; i++){
			uniqueFieldIdentifiers[i] = GUID.read(workingContext);
			fieldTypes[i] = Helper.readU8(byteBuffer);
			int fieldTypeComponents = Helper.getFieldTypeComponents(fieldTypes[i]);

			for(int j = 0; j < fieldTypeComponents; j++){
				String fieldTypeData = Helper.getFieldTypeData(fieldTypes[i]);
				if(fieldTypeData.charAt(0) == 'F'){
					dataExponentsLists.add(Int32CDP2.readVecU32(workingContext, PredictorType.PredNULL)); 

					if(fieldTypeData.equals("F64")){
						dataUpperMantissaeLists.add(Int32CDP2.readVecU32(workingContext, PredictorType.PredNULL));
					}

					if(fieldTypeData.equals("F32") || fieldTypeData.equals("F64")){
						dataLowerMantissaeLists.add(Int32CDP2.readVecU32(workingContext, PredictorType.PredNULL));
					}

				} else {
					dataU320Lists.add(Int32CDP2.readVecU32(workingContext, PredictorType.PredNULL)); 

					if(	fieldTypeData.equals("U32") || fieldTypeData.equals("I32") ||
						fieldTypeData.equals("U64") || fieldTypeData.equals("I64")){
						dataU321Lists.add(Int32CDP2.readVecU32(workingContext, PredictorType.PredNULL));
					}

					if(	fieldTypeData.equals("U64") || fieldTypeData.equals("I64")){
						dataU322Lists.add(Int32CDP2.readVecU32(workingContext, PredictorType.PredNULL));
					}
				}
			}
			auxiliaryDataHashs[i] = Helper.readI32(byteBuffer); 
		}

		return new TopoMeshCompressedRepDataV2(topoMeshCompressedRepDataV1, versionNumber, vertexBindings,
				numberOfAuxiliaryFields, uniqueFieldIdentifiers, fieldTypes, dataExponentsLists,
				dataUpperMantissaeLists, dataLowerMantissaeLists, dataU320Lists, dataU321Lists, dataU322Lists,
				auxiliaryDataHashs);
	}
}
