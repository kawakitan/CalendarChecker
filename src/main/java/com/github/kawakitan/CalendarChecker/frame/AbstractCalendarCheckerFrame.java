/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kawakitan.CalendarChecker.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.github.kawakitan.CalendarChecker.component.CSVFilter;
import com.github.kawakitan.CalendarChecker.component.ExcelFilter;
import com.github.kawakitan.CalendarChecker.component.NoWrapEditorKit;
import com.github.kawakitan.CalendarChecker.entity.CheckCondition;
import com.github.kawakitan.CalendarChecker.utils.Utility;

/**
 * 
 * @author kawakitan
 */
public abstract class AbstractCalendarCheckerFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = -6558941935447875690L;

	protected final JLabel lblWareki;
	protected final JLabel lblSeireki;
	protected final JLabel lblInput;
	protected final JLabel lblOutput;
	protected final JLabel lblGengo;
	protected final JTextField txtWareki;
	protected final JTextField txtSeireki;
	protected final JTextField txtGengo;
	protected final JTextField txtInput;
	protected final JTextField txtOutput;
	protected final JComboBox<String> cmbInput;
	protected final JComboBox<String> cmbOutput;
	protected final JComboBox<String> cmbGengo;
	protected final JButton btnInput;
	protected final JButton btnOutput;
	protected final JButton btnGengo;

	/** メッセージラベル */
	protected final JLabel lblMessage;
	/** レポート */
	protected final JTextPane txtReport;
	/** コンソール */
	protected final JTextPane txtConsole;
	/** スクロール */
	protected final JScrollPane scrlReport;
	protected final JScrollPane scrlConsole;
	/** プログレスバー */
	protected final JProgressBar barProgress;
	/** OKボタン */
	protected final JButton btnOk;
	/** キャンセルボタン */
	protected final JButton btnCancel;

	private Thread thread;

	private boolean cancelFlag;

	private final MutableAttributeSet attrFail;
	private final MutableAttributeSet attrWarn;

	public AbstractCalendarCheckerFrame() {
		setTitle("和西暦チェッカー ver.1.2.0");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(null);

		attrFail = new SimpleAttributeSet();
		StyleConstants.setForeground(attrFail, Color.RED);
		attrWarn = new SimpleAttributeSet();
		StyleConstants.setForeground(attrWarn, Color.ORANGE);

		lblWareki = new JLabel("和暦カラム名：");
		lblSeireki = new JLabel("西暦カラム名：");
		lblInput = new JLabel("入力ファイル：");
		lblOutput = new JLabel("出力ファイル：");
		lblGengo = new JLabel("元号ファイル：");

		txtWareki = new JTextField("");
		txtSeireki = new JTextField("");
		txtInput = new JTextField("");
		txtOutput = new JTextField("");
		txtGengo = new JTextField("gengo.csv");

		cmbInput = new JComboBox<String>(new String[] { "Windows-31J", "UTF-8" });
		cmbOutput = new JComboBox<String>(new String[] { "Windows-31J", "UTF-8" });
		cmbGengo = new JComboBox<String>(new String[] { "Windows-31J", "UTF-8" });
		btnInput = new JButton("選択");
		btnOutput = new JButton("選択");
		btnGengo = new JButton("選択");

		lblMessage = new JLabel();

		txtReport = new JTextPane();
		txtReport.setEditable(false);
		final Font font = new Font(Font.MONOSPACED, txtReport.getFont().getStyle(), txtReport.getFont().getSize());
		txtReport.setFont(font);

		txtConsole = new JTextPane();
		txtConsole.setEditable(false);
		txtConsole.setFont(font);
		txtConsole.setEditorKit(new NoWrapEditorKit());

		scrlReport = new JScrollPane(txtReport);
		scrlConsole = new JScrollPane(txtConsole);
		barProgress = new JProgressBar();

		btnOk = new JButton("OK");
		btnCancel = new JButton("Cancel");

		add(lblWareki);
		add(lblSeireki);
		add(lblInput);
		add(lblOutput);
		add(lblGengo);
		add(txtWareki);
		add(txtSeireki);
		add(txtInput);
		add(txtOutput);
		add(txtGengo);
		add(cmbInput);
		add(cmbOutput);
		add(cmbGengo);
		add(btnInput);
		add(btnOutput);
		add(btnGengo);
		add(lblMessage);
		add(scrlReport);
		add(scrlConsole);
		add(barProgress);
		add(btnOk);
		add(btnCancel);

		((AbstractDocument) txtConsole.getDocument()).setDocumentFilter(new DocumentFilter() {
			private static final int MAX_LINES = 100;

			@Override
			public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				fb.insertString(offset, string, attr);
				Element root = fb.getDocument().getDefaultRootElement();
				if (root.getElementCount() > MAX_LINES) {
					fb.remove(0, root.getElement(0).getEndOffset());
				}
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				doOpened();
			}

			public void windowClosing(WindowEvent e) {
				close();
			}

			public void windowClosed(WindowEvent e) {
				doClosed();
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				doResize();
			}
		});

		btnInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectInputFile();
			}
		});
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectOutputFile();
			}
		});
		btnGengo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectGengoFile();
			}
		});

		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		setEnableControl(true);

		setLocation(60, 60);
		setSize(900, 600);
	}

	private void selectInputFile() {
		final JFileChooser filechooser = new JFileChooser();
		filechooser.addChoosableFileFilter(new CSVFilter());
		int selected = filechooser.showOpenDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();

			String srcPath = file.getAbsolutePath();
			txtInput.setText(srcPath);

			int index = srcPath.lastIndexOf(".");

			if (-1 != index) {
				// String destPath = srcPath.substring(0, index) + ".out" + srcPath.substring(index);
				String destPath = srcPath.substring(0, index) + ".out.xlsx";
				txtOutput.setText(destPath);
			}
		}
	}

	private void selectOutputFile() {
		final JFileChooser filechooser = new JFileChooser();
		filechooser.addChoosableFileFilter(new CSVFilter());
		filechooser.addChoosableFileFilter(new ExcelFilter());
		int selected = filechooser.showSaveDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			txtOutput.setText(file.getAbsolutePath());
		}
	}

	private void selectGengoFile() {
		final JFileChooser filechooser = new JFileChooser();
		filechooser.addChoosableFileFilter(new CSVFilter());
		int selected = filechooser.showOpenDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			txtGengo.setText(file.getAbsolutePath());
		}
	}

	private CheckCondition getCheckCondition() {
		final CheckCondition condition = new CheckCondition();
		condition.setWarekiColumnName(txtWareki.getText());
		condition.setSeirekiColumnName(txtSeireki.getText());
		condition.setSrcFile(new File(txtInput.getText()));
		condition.setDestFile(new File(txtOutput.getText()));
		condition.setGengoFile(new File(txtGengo.getText()));
		condition.setSrcCharset(Charset.forName(cmbInput.getSelectedItem().toString()));
		condition.setDestCharset(Charset.forName(cmbOutput.getSelectedItem().toString()));
		condition.setGengoCharset(Charset.forName(cmbGengo.getSelectedItem().toString()));
		return condition;
	}

	private void setEnableControl(final boolean flag) {
		if (flag) {
			txtWareki.setEnabled(true);
			txtSeireki.setEnabled(true);
			txtInput.setEnabled(true);
			txtOutput.setEnabled(true);
			txtGengo.setEnabled(true);
			cmbInput.setEnabled(true);
			cmbOutput.setEnabled(true);
			cmbGengo.setEnabled(true);
			btnInput.setEnabled(true);
			btnOutput.setEnabled(true);
			btnGengo.setEnabled(true);

			btnOk.setEnabled(true);
			btnCancel.setEnabled(false);
			barProgress.setIndeterminate(false);
		} else {
			txtWareki.setEnabled(false);
			txtSeireki.setEnabled(false);
			txtInput.setEnabled(false);
			txtOutput.setEnabled(false);
			txtGengo.setEnabled(false);
			cmbInput.setEnabled(false);
			cmbOutput.setEnabled(false);
			cmbGengo.setEnabled(false);
			btnInput.setEnabled(false);
			btnOutput.setEnabled(false);
			btnGengo.setEnabled(false);

			btnOk.setEnabled(false);
			btnCancel.setEnabled(true);
			barProgress.setIndeterminate(true);
		}
	}

	private void ok() {
		final String input = txtInput.getText();
		final String output = txtOutput.getText();
		if (Utility.isEmpty(input) || Utility.isEmpty(output)) {
			return;
		}

		final CheckCondition condition = getCheckCondition();

		setEnableControl(false);
		cancelFlag = false;

		thread = new Thread(new Runnable() {
			public void run() {
				execute(condition);

				setEnableControl(true);
			}
		});
		thread.start();
	}

	protected final void debug(final String message) {
		log(message, null);
	}

	protected final void warn(final String message) {
		log(message, attrWarn);
	}

	protected final void fail(final String message) {
		log(message, attrFail);
	}

	private void log(final String message, final AttributeSet attr) {
		final Document doc = txtConsole.getDocument();
		final int length = doc.getLength();
		try {
			doc.insertString(length, message + "\n", attr);
			txtConsole.setCaretPosition(doc.getLength());
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	protected final boolean isCancel() {
		return cancelFlag;
	}

	private void cancel() {
		btnCancel.setEnabled(false);
		cancelFlag = true;
	}

	private void close() {
		if (null == thread || !thread.isAlive()) {
			dispose();
		}
	}

	private void execute(final CheckCondition condition) {
		doExecute(condition);
	}

	protected abstract void doExecute(final CheckCondition condition);

	private void doOpened() {

	}

	private void doClosed() {

	}

	private void doResize() {
		final Insets insets = getInsets();
		final int width = getWidth() - (insets.left + insets.right);
		final int height = getHeight() - (insets.top + insets.bottom);

		int margin = 6;
		int widthLabel = 100;
		int widthCombo = 140;
		int widthButton = 100;
		int x = margin;
		int y = margin;
		int heightComponent = 24;
		int space = 4;

		lblWareki.setBounds(x, y, widthLabel, heightComponent);
		txtWareki.setBounds(x + widthLabel, y, 260, heightComponent);

		y += heightComponent + space;

		lblSeireki.setBounds(x, y, widthLabel, heightComponent);
		txtSeireki.setBounds(x + widthLabel, y, 260, heightComponent);

		y += heightComponent + space;

		lblInput.setBounds(x, y, widthLabel, heightComponent);
		txtInput.setBounds(x + widthLabel, y, width - (margin * 2 + widthLabel + widthCombo + space + widthButton), heightComponent);
		cmbInput.setBounds(width - (widthButton + space + widthCombo + margin), y, widthCombo, heightComponent);
		btnInput.setBounds(width - (widthButton + margin), y, widthButton, heightComponent);

		y += heightComponent + space;

		lblOutput.setBounds(x, y, widthLabel, heightComponent);
		txtOutput.setBounds(x + widthLabel, y, width - (margin * 2 + widthLabel + widthCombo + space + widthButton), heightComponent);
		cmbOutput.setBounds(width - (widthButton + space + widthCombo + margin), y, widthCombo, heightComponent);
		btnOutput.setBounds(width - (widthButton + margin), y, widthButton, heightComponent);

		y += heightComponent + space;

		lblGengo.setBounds(x, y, widthLabel, heightComponent);
		txtGengo.setBounds(x + widthLabel, y, width - (margin * 2 + widthLabel + widthCombo + space + widthButton), heightComponent);
		cmbGengo.setBounds(width - (widthButton + space + widthCombo + margin), y, widthCombo, heightComponent);
		btnGengo.setBounds(width - (widthButton + margin), y, widthButton, heightComponent);

		y += heightComponent + space + 6;

		btnCancel.setBounds(width - 210, y, 100, 24);
		btnOk.setBounds(width - 106, y, 100, 24);

		y += heightComponent + space + 10;

		barProgress.setBounds(x, y, width - 12, 24);

		y += heightComponent + space;

		lblMessage.setBounds(x, y, width - 12, 24);

		y += heightComponent + space;

		scrlReport.setBounds(x, y, 200, height - (y + margin));
		scrlConsole.setBounds(x + 200 + space, y, width - (200 + space + margin * 2), height - (y + margin));
	}
}
