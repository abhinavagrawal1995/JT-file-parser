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
import de.raida.jcadlib.cadimport.jt.reader.Helper;
import de.raida.jcadlib.cadimport.jt.reader.UnsupportedCodecException;
import de.raida.jcadlib.cadimport.jt.reader.WorkingContext;

/**
 * <h>8.1.2 Int32 Compressed Data Packet Mk. 2</h>
 * Object Type ID: <code>---</code>
 * <br> The Int32 Compressed Data Packet Mk. 2 collection represents an enhanced form
 * of the original Int32 Compressed Data Packet. Note that the Int32 Compressed Data
 * Packet Mk. 2 collection can in itself contain another Int32 Compressed Data Packet
 * Mk. 2 collection if there are any 'Out-Of-Band data.' In the context of the JT format
 * data compression algorithms and Int32 Compressed Data Packet Mk. 2, 'out-of-band data'
 * has the meaning described below.
 * Entropy CODECs (e.g. Arithmetic) exploit the statistics present in the relative
 * frequencies of the values being encoded. Values that occur frequently enough allow
 * these methods to encode each of the values as a 'symbol' in fewer bits that it would
 * take to encode the value itself. Values that occur too infrequently to take advantage
 * of this property are written aside into the 'out-of-band data' array to be encoded
 * separately. An 'escape' symbol is encoded in their place as a placeholder in the primal
 * CODEC (note, see 'Symbol' data field definition in 8.1.2.1.1 Int32 Probability Context
 * Table Entry Mk. 2 for further details on the representation of 'escape' symbol).
 * Essentially the 'out-of-band data' is the high-entropy residue left over after the
 * CODEC has squeezed all the advantage out of the original data stream that it can.
 * However, this 'out-of-band data' is sent back around for another pass because sometimes
 * there are new or different statistics to be exploited.
 * The Int32 Compressed Data Packet Mk. 2 brings the new Chopper pseudo-CODEC to the table.
 * Its job is to identify fields of bits in a sequence of otherwise incompressible data
 * that may be hiding low-entropy statistics that can be profitably exploited. In other
 * words, it "chops" the input data up into bit fields, and then encodes them separately
 * using the Arithmetic or BitLength CODECs, or in some cases, another round of chopping.
 * The Chopper also removes value bias from the original input data array. Some input data
 * arrays may contain values that are clustered around a certain central value. In these
 * cases, it is profitable to first subtract out a bias value from the original input data.
 * In some cases, this simple expedient may dramatically reduce the apparent field width
 * necessary to code the variation in the original sequence.
 * In some cases, all values may be written as "out of band" when the Codec cannot perform
 * any useful compression. In this case, the encoded I32 : CodeText Length field will be 0,
 * and the I32 : Out-Of-Band Value Count will be equal to I32 : Value Element Count. The
 * implied action in this case is to merely copy the Out-Of-Band value data into the output
 * Value Element array instead of invoking the Codec.
 * When all other coding options have been exhausted, the Bitlength CODEC is invoked. The
 * Bitlength CODEC directly encodes all values given to it, does not require a probability
 * context, and hence never produces additional 'out-of-band data'. The byte stops there,
 * in other words.
 * Note that in the diagram below, encoding can loop back recursively for Out-Of-Band data
 * and chopper fields. For JT v9 files, the maximum recursion depth may not exceed three.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class Int32CDP2 {
	/** Codec type: Null-Codec */
	private final static int CODECTYPE_NULL       = 0;

	/** Codec type: Bitlength-Codec */
	private final static int CODECTYPE_BITLENGTH  = 1;

	/** Codec type: Arithmetic-Codec */
	private final static int CODECTYPE_ARITHMETIC = 3;

	/** Codec type: Chopper-Codec */
	private final static int CODECTYPE_CHOPPER    = 4;

	/**
	 * Constructor.
	 */
	public Int32CDP2(){
	}

	/**
	 * Decodes the following compressed bytes.
	 * @param  workingContext            Working context
	 * @return                           Decoded symbols
	 * @throws UnsupportedCodecException Thrown, when an unsupported codec has been found
	 */
	private static List<Integer> decodeBytes(WorkingContext workingContext) throws UnsupportedCodecException {
		ByteBuffer byteBuffer = workingContext.getByteBuffer();

		int valueCount = Helper.readI32(byteBuffer);
		if(valueCount <= 0){
			return new ArrayList<Integer>();
		}

		int codecType = Helper.readU8(byteBuffer);
		if((codecType != 0) && (codecType != 1) && (codecType != 3) && (codecType != 4)){
			throw new IllegalArgumentException("Found invalid codec type: " + codecType);
		}

		if(codecType == CODECTYPE_CHOPPER){
			int chopBits = Helper.readU8(byteBuffer);
			if(chopBits == 0){
				return decodeBytes(workingContext);

			} else {
				int valueBias = Helper.readI32(byteBuffer);
				int valueSpanBits = Helper.readU8(byteBuffer);
				List<Integer> choppedMSBData = decodeBytes(workingContext);
				List<Integer> choppedLSBData = decodeBytes(workingContext);

				List<Integer> decodedSymbols = new ArrayList<Integer>();
				for(int i = 0; i < choppedMSBData.size(); i++){
					decodedSymbols.add((choppedLSBData.get(i) | (choppedMSBData.get(i) << (valueSpanBits - chopBits))) + valueBias);
				}
				return decodedSymbols;
			}
		}

		// Handle "null codec"
		if(codecType == CODECTYPE_NULL){
			List<Integer> decodedSymbols = new ArrayList<Integer>();

			int intsToRead = Helper.readI32(byteBuffer) / 4;
            for(int i = 0; i < intsToRead; i++){
            	decodedSymbols.add(Helper.readI32(byteBuffer));
            }

            return decodedSymbols;
		}

		int codeTextLength = Helper.readI32(byteBuffer);
		int intsToRead = (int)((codeTextLength / 32.0) + 0.99);

		byte[] codeTextWords = new byte[intsToRead * 4];
		for(int i = 0; i < intsToRead; i++){
			byte[] buffer = Helper.readBytes(byteBuffer, 4);
			if(byteBuffer.order() == ByteOrder.LITTLE_ENDIAN){
				codeTextWords[i * 4] = buffer[3];
				codeTextWords[(i * 4) + 1] = buffer[2];
				codeTextWords[(i * 4) + 2] = buffer[1];
				codeTextWords[(i * 4) + 3] = buffer[0];
			} else {
				codeTextWords[i * 4] = buffer[0];
				codeTextWords[(i * 4) + 1] = buffer[1];
				codeTextWords[(i * 4) + 2] = buffer[2];
				codeTextWords[(i * 4) + 3] = buffer[3];
			}
		}

		Int32ProbabilityContexts int32ProbabilityContexts = null;
		List<Integer> outOfBandValues = null;

		if(codecType == CODECTYPE_ARITHMETIC){
			int32ProbabilityContexts = Int32ProbabilityContexts.read(workingContext);
			outOfBandValues = Int32CDP2.decodeBytes(workingContext);
			if((codeTextLength == 0) && (outOfBandValues.size() == valueCount)){
				return outOfBandValues;
			}
		}

		CodecDriver codecDriver = new CodecDriver(codeTextWords, codeTextLength, valueCount, null, int32ProbabilityContexts, outOfBandValues);

		// Decode the bytes
		List<Integer> decodedSymbols = new ArrayList<Integer>();
		switch(codecType){
			case CODECTYPE_BITLENGTH:
				decodedSymbols = BitlengthDecoder.decode2(codecDriver);
				break;

			case CODECTYPE_ARITHMETIC:
				decodedSymbols = ArithmeticDecoder.decode(codecDriver);
				break;
		}

		if(decodedSymbols.size() != valueCount){
			throw new IllegalArgumentException("Codec produced wrong number of symbols: " + decodedSymbols.size() + " / " + valueCount);
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
		List<Integer> unpackedSymbols = Int32CDP.unpackResiduals(decodedSymbols, predictorType);
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
		List<Integer> unpackedList = Int32CDP.unpackResiduals(decodedSymbols, predictorType);

		for(int i = 0; i < unpackedList.size(); i++){
			unpackedList.set(i, (unpackedList.get(i)& 0xffff));
		}

		return unpackedList;
	}

}
