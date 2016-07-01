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

package de.raida.progress;

/**
 * Class for progress events.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class ProgressEvent {
	/** Progress percentage (should be between 0 and 100) */
	private byte _progressPercentage;

	/** Sub title */
	private String _subTitle;

	/**
	 * Constructor.
	 * @param progressPercentage Progress percentage (should be between 0 and 100)
	 */
	public ProgressEvent(byte progressPercentage){
		_progressPercentage = progressPercentage;
		_subTitle           = null;
	}

	/**
	 * Constructor.
	 * @param subTitle         New sub title
	 * @param intermediateMode Intermediate mode
	 */
	public ProgressEvent(String subTitle, boolean intermediateMode){
		_progressPercentage = intermediateMode ? (byte)-1 : (byte)0;
		_subTitle           = null;
	}

	/**
	 * Returns the progress percentage.
	 * @return Progress percentage (should be between 0 and 100)
	 */
	public byte getProgressPercentage(){
		return _progressPercentage;
	}

	/**
	 * Returns the sub title.
	 * @return Sub title
	 */
	public String getSubTitle(){
		return _subTitle;
	}
}