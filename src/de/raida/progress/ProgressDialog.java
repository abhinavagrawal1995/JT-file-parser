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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * Dialog, showing that the user has to wait a little bit, realized as
 * a singelton class.
 * <br>(c) 2014 by <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @author  <a href="mailto:j.raida@gmx.net">Johannes Raida</a>
 * @version 1.0
 */
public class ProgressDialog extends JWindow {
	/** ID of the serializable class */
	private final static long serialVersionUID = 20L;

	/** The only instance of the progress dialog */
	private static ProgressDialog _progressDialog;

	/** Reference of the parent window */
	private static JFrame _parent;

	/** Label with the dynamic title */
	private JLabel _title;

	/** Label with the dynamic subtitle */
	private JLabel _subTitle;

	/** Progress bar, visualizing the progress */
	private JProgressBar _progressBar;

	/**
	 * Private constructor, which is only accessable by the static methods.
	 * @param parentWindow Reference of the parent window
	 * @param imageIcon    Image icon to show
	 * @param title        Dynamic title of the window
	 * @param subTitle     Dynamic subtitle of the window
	 */
	private ProgressDialog(final JFrame parentWindow, final ImageIcon imageIcon, final String title, final String subTitle){
		super(parentWindow);

		// Instantiate some attributes
		_parent = parentWindow;
		_title = new JLabel(title);
		_subTitle = new JLabel(subTitle);
		_progressBar = new JProgressBar();
		_progressBar.setStringPainted(true);

		compressSubTitle();

		// Create the layout and ...
		createLayout(imageIcon);

		// ... show the initialized window
		setSize(400, 120);
		setLocationRelativeTo(parentWindow);
		setVisible(true);
	}

	/**
	 * Creates the layout of the window.
	 * @param imageIcon Image icon to show
	 */
	private void createLayout(ImageIcon imageIcon){
		// Create the title label
		_title.setPreferredSize(new Dimension(320, 30));
		_title.setHorizontalAlignment(SwingConstants.LEFT);
		_title.setFont(new Font(null, Font.PLAIN, 18));

		// Create the main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		mainPanel.add(_title,                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,       new Insets( 0, 0, 0, 0), 0, 0));
		mainPanel.add(new JLabel(imageIcon), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,       new Insets( 0, 0, 0, 0), 0, 0));
		mainPanel.add(_subTitle,             new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,       new Insets(10, 0, 0, 0), 0, 0));
		mainPanel.add(_progressBar,          new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));

		// Insert the main panel into the base layout
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(mainPanel, BorderLayout.CENTER);
	}

	/**
	 * If the subtitle is too long, cut off the first characters and replace
	 * them by "..."
	 */
	private void compressSubTitle(){
		int textWidth = getFontMetrics(_subTitle.getFont()).stringWidth(_subTitle.getText());
		if(textWidth > 300){
			_subTitle.setText("..." + _subTitle.getText().substring(((textWidth - 300) / (textWidth / _subTitle.getText().length())) + 3));
		}
	}

	/**
	 * Updates the dynamic title
	 * @param newTitle New title
	 */
	private void setTitle(final String newTitle){
		_title.setText(newTitle);
	}

	/**
	 * Updates the dynamic subtitle
	 * @param newSubTitle New subtitle
	 */
	private void setSubTitle(final String newSubTitle){
		_subTitle.setText(newSubTitle);
	}

	/**
	 * Updates the progress bar.
	 * @param progress New progress state (should be between 0 and 100) 
	 */
	private void setProgress(int progress){
		_progressBar.setValue(progress);
	}

	/**
	 * De- / activate intermediate mode
	 * @param intermediateMode Intermediate mode
	 */
	public void setIntermediate(boolean intermediateMode){
		_progressBar.setIndeterminate(intermediateMode);
	}

	/**
	 * If not existing, create the window.
	 * @param parent    Reference of the parent window
	 * @param imageIcon Image icon to show
	 * @param title     Title of the window
	 * @param subTitle  Variable subtitle of the window
	 */
	public static void openDialog(final JFrame parent, final ImageIcon imageIcon, final String title, final String subTitle){
		if(_progressDialog == null){
			// Create the dialog
			_progressDialog = new ProgressDialog(parent, imageIcon, title, subTitle);
			_progressDialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			_parent.setEnabled(false);
		}
	}

	/**
	 * Updates the dynamic title
	 * @param newTitle New title
	 */
	public static void updateTitle(final String newTitle){
		if(_progressDialog != null){
			_progressDialog.setTitle(newTitle);
		}
	}

	/**
	 * Updates the dynamic subtitle
	 * @param newSubTitle New subtitle
	 */
	public static void updateSubTitle(final String newSubTitle){
		if(_progressDialog != null){
			_progressDialog.setSubTitle(newSubTitle);
		}
	}

	/**
	 * Updates the progress bar.
	 * @param progress New progress state (should be between 0 and 100) 
	 */
	public static void updateProgressBar(int progress){
		if(_progressDialog != null){
			_progressDialog.setIntermediate(false);
			_progressDialog.setProgress(progress);
		}
	}

	/**
	 * Close the dialog.
	 */
	public static void closeDialog(){
		if(_progressDialog != null){
			_parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			_parent.setEnabled(true);
			_progressDialog.dispose();
			_progressDialog = null;
		}
	}

	/**
	 * De- / activate intermediate mode
	 * @param intermediateMode Intermediate mode
	 */
	public static void setIntermediateMode(boolean intermediateMode){
		if(_progressDialog != null){
			_progressDialog.setIntermediate(intermediateMode);
		}
	}
}