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

package de.raida.jcadlib.cadimport.jt.codec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.codec.arithmetic.ArithmeticDecoder;
import de.raida.jcadlib.cadimport.jt.codec.bitlength.BitlengthDecoder;
import de.raida.jcadlib.cadimport.jt.codec.huffman.HuffmanDecoder;
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.1 Int32 Compressed Data Packet</h>
 * Object Type ID: <code>---</code>
 * <br>The Int32 Compressed Data Packet collection represents the format used to
 * encode/compress a collection of data into a series of Int32 based symbols. Note
 * that the Int32 Compressed Data Packet collection can in itself contain another
 * Int32 Compressed Data Packet collection if there are any "Out-Of-Band data". In
 * the context of the JT format data compression algorithms and Int32 Compressed
 * Data Packet, "out-of-band data" has the following meaning.
 * <br>CODECs like arithmetic and Huffman (see 8.2 Encoding Algorithms for technical
 * description) exploit the statistics present in the relative frequencies of the
 * values being encoded. Values that occur frequently enough allow both of these
 * methods to encode each of the values as a "symbol" in fewer bits that it would
 * take to encode the value itself. Values that occur too infrequently to take
 * advantage of this property are written aside into the "out-of-band data" array
 * to be encoded separately. An "escape" symbol is encoded in their place as a
 * placeholder in the primal CODEC (note, see "Symbol" data field definition in
 * 8.1.1.1.1 Int32 Probability Context Table Entry for futher details on the
 * representation of "escape" symbol).
 * <br>Essentially the "out-of-band data" is the high-entropy junk/residue/slag
 * left over after the CODECs have squeezed all the advantage out that they can.
 * However, this "out-of-band data" is sent back around because sometimes there
 * are different statistics to be taken advantage of. When you're down to the
 * really icky "outof-band" slag, the bitlength CODEC is invoked. The bitlength
 * CODEC directly encodes all values given to it, does not require a probability
 * context, and hence never produces additional "out-of-band data". The byte stops
 * there, in other words.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class Int32CDP {
	/** Codec type: Null-Codec */
	private final static int CODECTYPE_NULL       = 0;

	/** Codec type: Bitlength-Codec */
	private final static int CODECTYPE_BITLENGTH  = 1;

	/** Codec type: Huffman-Codec */
	private final static int CODECTYPE_HUFFMAN    = 2;

	/** Codec type: Arithmetic-Codec */
	private final static int CODECTYPE_ARITHMETIC = 3;

	/**
	 * Constructor.
	 */
	public Int32CDP(){
	}

	/**
	 * Decodes the following compressed bytes.
	 * @param  workingContext            Working context
	 * @return                           Decoded symbols
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	private static List<Integer> decodeBytes(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int codecType = Helper.readU8(byteBuffer);
		if((codecType < 0) || (codecType > 3)){
			throw new IllegalArgumentException("Found invalid codec type: " + codecType);
		}

		Int32ProbabilityContexts int32ProbabilityContexts = null;
		Integer outOfBandValueCount = null;
		Integer codeTextLength = null;
		Integer valueElementCount = null;
		List<Integer> outOfBandValues = null;
		Integer symbolCount = null;

		// Huffman or arithmetic codec
		if((codecType == CODECTYPE_HUFFMAN) || (codecType == CODECTYPE_ARITHMETIC)){
			int32ProbabilityContexts = Int32ProbabilityContexts.read(workingContext);
			outOfBandValueCount = Helper.readI32(byteBuffer);
			if(outOfBandValueCount > 0){
				outOfBandValues = Int32CDP.decodeBytes(workingContext);
			}
		}

		// Codec != "null codec"
		if(codecType != CODECTYPE_NULL){
			codeTextLength = Helper.readI32(byteBuffer);
			valueElementCount = Helper.readI32(byteBuffer);
			symbolCount = valueElementCount;
			if((int32ProbabilityContexts != null) && (int32ProbabilityContexts.getProbabilityContextCount() > 1)){
				symbolCount = Helper.readI32(byteBuffer);
			}
		}

		// Handle "null codec"
		if(codecType == CODECTYPE_NULL){
			List<Integer> decodedSymbols = new ArrayList<Integer>();

			int intsToRead = Helper.readI32(byteBuffer);
            for(int i = 0; i < intsToRead; i++){
            	decodedSymbols.add(Helper.readI32(byteBuffer));
            }

            return decodedSymbols;
		}

		// Read the compressed and encoded code text
		int intsToRead = Helper.readI32(byteBuffer);
		byte[] codeText = new byte[intsToRead * 4];
		for(int i = 0; i < intsToRead; i++){
			byte[] buffer = Helper.readBytes(byteBuffer, 4);
			if(byteBuffer.order() == ByteOrder.LITTLE_ENDIAN){
				codeText[i * 4] = buffer[3];
				codeText[(i * 4) + 1] = buffer[2];
				codeText[(i * 4) + 2] = buffer[1];
				codeText[(i * 4) + 3] = buffer[0];
			} else {
				codeText[i * 4] = buffer[0];
				codeText[(i * 4) + 1] = buffer[1];
				codeText[(i * 4) + 2] = buffer[2];
				codeText[(i * 4) + 3] = buffer[3];
			}
		}

		CodecDriver codecDriver = new CodecDriver(codeText, codeTextLength, valueElementCount, symbolCount, int32ProbabilityContexts, outOfBandValues);

		// Decode the bytes
		List<Integer> decodedSymbols = new ArrayList<Integer>();
		switch(codecType){
			case CODECTYPE_BITLENGTH:
				decodedSymbols = BitlengthDecoder.decode(codecDriver);
				break;

			case CODECTYPE_HUFFMAN:
				decodedSymbols = HuffmanDecoder.decode(codecDriver);
				break;

			case CODECTYPE_ARITHMETIC:
				decodedSymbols = ArithmeticDecoder.decode(codecDriver);
				break;
		}

		if(decodedSymbols.size() != valueElementCount){
			throw new IllegalArgumentException("Codec produced wrong number of symbols: " + decodedSymbols.size() + " / " + valueElementCount);
		}

		return decodedSymbols;
	}

	/**
	 * Decodes some values.
	 * @param  workingContext            Working context
	 * @param  predictorType             Predictor type
	 * @return                           Decoded values
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static List<Integer> readVecI32(WorkingContext workingContext, PredictorType predictorType) throws UnsupportedCodecException {
		List<Integer> decodedSymbols = decodeBytes(workingContext);
		List<Integer> unpackedSymbols = unpackResiduals(decodedSymbols, predictorType);
		return unpackedSymbols;
		
	}

	/**
	 * Decodes some values.
	 * @param  workingContext            Working context
	 * @param  predictorType             Predictor type
	 * @return                           Decoded values
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	public static List<Integer> readVecU32(WorkingContext workingContext, PredictorType predictorType) throws UnsupportedCodecException {
		List<Integer> decodedSymbols = decodeBytes(workingContext);
		List<Integer> unpackedList = unpackResiduals(decodedSymbols, predictorType);

		for(int i = 0; i < unpackedList.size(); i++){
			unpackedList.set(i, (unpackedList.get(i)& 0xffff));
		}

		return unpackedList;
	}

	/**
	 * Unpacks the list of decoded symbols.
	 * @param  residuals     List of decoded symbols
	 * @param  predictorType Predictor type
	 * @return               List of unpackages integer values
	 */
	public static List<Integer> unpackResiduals(List<Integer> residuals, PredictorType predictorType){
		int iPredicted;
		int len = residuals.size();

		List<Integer> indexList = new ArrayList<Integer>();

		for(int i = 0; i < len; i++){
			if(predictorType == PredictorType.PredNULL){
				indexList.add(residuals.get(i));

			} else {
				// The first four values are not handeled
				if(i < 4){
					indexList.add(residuals.get(i));
				} else {
					// Get a predicted value
					iPredicted = predictValue(indexList, i, predictorType);
	
					// Decode the residual as the current value XOR predicted
					if((predictorType == PredictorType.PredXor1) || (predictorType == PredictorType.PredXor2)){
						indexList.add(residuals.get(i) ^ iPredicted);
	
					// Decode the residual as the current value plus predicted
					} else {
						indexList.add(residuals.get(i) + iPredicted);
					}
				}
			}
		}

		return indexList;
	}

	/**
	 * Calculates the predictzion value.
	 * @param values        Unpacked values
	 * @param index         Index of the position for which the prediction value shall be calculated
	 * @param predictorType Predictor type
	 * @return              Predicted value
	 */
	private static int predictValue(List<Integer> values, int index, PredictorType predictorType){
		int v1 = values.get(index - 1);
		int v2 = values.get(index - 2);
		int v4 = values.get(index - 4);

		switch(predictorType){
			default:
			case PredLag1:
			case PredXor1:
				return v1;

			case PredLag2:
			case PredXor2:
				return v2;

			case PredStride1:
            	return (v1 + (v1 - v2));

			case PredStride2:
				return (v2 + (v2 - v4));

			case PredStripIndex:
				if(((v2 - v4) < 8) && ((v2 - v4) > -8)){
					return (v2 + (v2 - v4));
				} else {
					return (v2 + 2);
				}

			case PredRamp:
				return index;
		}
	}
}
