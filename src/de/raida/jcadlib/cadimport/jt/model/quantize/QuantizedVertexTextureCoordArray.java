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

package de.raida.jcadlib.cadimport.jt.model.quantize;

import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.Int32CDP;
import de.raida.jcadlib.cadimport.jt.codec.PredictorType;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.3.2.3 Quantized Vertex Texture Coord Array</h>
 * Object Type ID: <code>---</code>
 * <br>The Quantized Vertex Texture Coord Array data collection contains the
 * quantization data/representation for a set of vertex texture coordinates.
 * Quantized Vertex Texture Coord Array data collection is only present if
 * previously read Texture Coord Binding value is not equal to zero (See 8.1.3
 * Vertex Based Shape Compressed Rep Data for complete explanation of Texture
 * Coord Binding data field).
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class QuantizedVertexTextureCoordArray {
	/**
	 * Constructor.
	 * @param textureQuantizerData  Texture quantizer data
	 * @param suggestedNumberOfBits Suggested number of bits
	 * @param uTextureCoordCodes    U texture coordinate codes
	 * @param vTextureCoordCodes    V texture coordinate codes
	 */
	public QuantizedVertexTextureCoordArray(TextureQuantizerData textureQuantizerData, int suggestedNumberOfBits, List<Integer> uTextureCoordCodes, List<Integer> vTextureCoordCodes){
	}

	/**
	 * Reads a QuantizedVertexTextureCoordArray object.
	 * @param  workingContext            Working context
	 * @return                           QuantizedVertexTextureCoordArray instance
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static QuantizedVertexTextureCoordArray read(WorkingContext workingContext) throws UnsupportedCodecException {
		TextureQuantizerData textureQuantizerData = TextureQuantizerData.read(workingContext);
		int suggestedNumberOfBits = Helper.readU8(workingContext.getByteBuffer());
		if((suggestedNumberOfBits < 0) || (suggestedNumberOfBits > 24)){
			throw new IllegalArgumentException("Found invalid suggested number of bits: " + suggestedNumberOfBits);
		}

		List<Integer> uTextureCoordCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);
		List<Integer> vTextureCoordCodes = Int32CDP.readVecU32(workingContext, PredictorType.PredLag1);

		return new QuantizedVertexTextureCoordArray(textureQuantizerData,
													suggestedNumberOfBits,
													uTextureCoordCodes,
													vTextureCoordCodes);
	}
}
