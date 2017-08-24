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
package com.github.kawakitan.CalendarChecker;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.github.kawakitan.CalendarChecker.entity.CheckCondition;
import com.github.kawakitan.CalendarChecker.entity.Gengo;

public class CalendarCheckerFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 181840455171874578L;

	private final JLabel lblWareki;
	private final JLabel lblSeireki;
	private final JLabel lblInput;
	private final JLabel lblOutput;
	private final JLabel lblGengo;
	private final JTextField txtWareki;
	private final JTextField txtSeireki;
	private final JTextField txtGengo;
	private final JTextField txtInput;
	private final JTextField txtOutput;
	private final JComboBox<String> cmbInput;
	private final JComboBox<String> cmbOutput;
	private final JComboBox<String> cmbGengo;
	private final JButton btnInput;
	private final JButton btnOutput;
	private final JButton btnGengo;

	/** メッセージラベル */
	private final JLabel lblMessage;
	/** メッセージ詳細テキスト */
	private final JTextPane txtMessageDetail;
	/** スクロール */
	private final JScrollPane pnlScroll;
	/** プログレスバー */
	private final JProgressBar barProgress;
	/** OKボタン */
	private final JButton btnOk;
	/** キャンセルボタン */
	private final JButton btnCancel;

	private Thread thread;

	private boolean cancelFlag;

	public CalendarCheckerFrame() {
		setTitle("和西暦チェッカー ver.1.0.0");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(null);

		lblWareki = new JLabel("和暦カラム名：");
		lblSeireki = new JLabel("西暦カラム名：");
		lblInput = new JLabel("入力ファイル：");
		lblOutput = new JLabel("出力ファイル：");
		lblGengo = new JLabel("元号ファイル：");
		txtWareki = new JTextField("");
		// txtWareki = new JTextField("和暦");
		txtSeireki = new JTextField("");
		// txtSeireki = new JTextField("西暦");
		txtInput = new JTextField("");
		// txtInput = new JTextField("sample.csv");
		txtOutput = new JTextField("");
		// txtOutput = new JTextField("sample.out.csv");
		txtGengo = new JTextField("gengo.csv");
		cmbInput = new JComboBox<String>(new String[] { "Windows-31J", "UTF-8" });
		cmbOutput = new JComboBox<String>(new String[] { "Windows-31J", "UTF-8" });
		cmbGengo = new JComboBox<String>(new String[] { "Windows-31J", "UTF-8" });
		btnInput = new JButton("選択");
		btnOutput = new JButton("選択");
		btnGengo = new JButton("選択");

		lblMessage = new JLabel();
		txtMessageDetail = new JTextPane();
		txtMessageDetail.setEditable(false);
		final Font font = new Font(Font.MONOSPACED, txtMessageDetail.getFont().getStyle(), txtMessageDetail.getFont().getSize());
		txtMessageDetail.setFont(font);
		pnlScroll = new JScrollPane(txtMessageDetail);
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
		add(pnlScroll);
		add(barProgress);
		add(btnOk);
		add(btnCancel);

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
		setSize(800, 400);
	}

	private void selectInputFile() {
		final JFileChooser filechooser = new JFileChooser();
		filechooser.addChoosableFileFilter(new CSVFilter());
		int selected = filechooser.showOpenDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			txtInput.setText(file.getAbsolutePath());
		}
	}

	private void selectOutputFile() {
		final JFileChooser filechooser = new JFileChooser();
		filechooser.addChoosableFileFilter(new CSVFilter());
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

	private void cancel() {
		btnCancel.setEnabled(false);
		cancelFlag = true;
	}

	private void close() {
		if (null == thread || !thread.isAlive()) {
			dispose();
		}
	}

	private Map<String, Gengo> getGengo(final File file, final Charset charset) {
		final Map<String, Gengo> gengos = new HashMap<String, Gengo>();

		CSVReader reader = null;
		try {
			reader = new CSVReader(file, charset);

			List<String> data = reader.readCSVLine();
			while (null != (data = reader.readCSVLine())) {
				if (3 != data.size()) {
					continue;
				}

				final String name = data.get(0);
				final Integer startYear = Utility.toInteger(data.get(1));
				final Integer endYear = Utility.toInteger(data.get(2));

				final Gengo gengo = new Gengo(name, startYear, endYear);

				gengos.put(gengo.getName(), gengo);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			Utility.release(reader);
		}

		return gengos;
	}

	private void execute(final CheckCondition condition) {
		CSVReader reader = null;
		CSVWriter writer = null;

		try {
			lblMessage.setText(String.format("処理を開始します。"));
			long tmStart = System.nanoTime();

			final Map<String, Gengo> gengos = getGengo(condition.getGengoFile(), condition.getGengoCharset());

			final CalendarConvertor convertor = new CalendarConvertor(gengos);

			reader = new CSVReader(condition.getSrcFile(), condition.getSrcCharset());
			writer = new CSVWriter(condition.getDestFile(), condition.getDestCharset());

			List<String> data = reader.readCSVLine();

			int warekiColumnNum = -1;
			int seirekiColumnNum = -1;
			for (int i = 0; i < data.size(); i++) {
				if (condition.getWarekiColumnName().equals(data.get(i))) {
					warekiColumnNum = i;
				} else if (condition.getSeirekiColumnName().equals(data.get(i))) {
					seirekiColumnNum = i;
				}
			}

			data.add("可否");
			data.add("想定");
			writer.writeCSVLine(data);

			int cntTotal = 0;
			int cntMatch = 0;
			int cntUnMatch = 0;
			int cntUnknown = 0;

			while (null != (data = reader.readCSVLine())) {
				if (cancelFlag) {
					break;
				}

				String s1 = data.get(warekiColumnNum);
				String s2 = data.get(seirekiColumnNum);

				String s3 = convertor.convert(s1);

				cntTotal++;
				if (null == s3) {
					cntUnknown++;
					data.add("？");
				} else if (s2.equals(s3)) {
					cntMatch++;
					data.add("○");
				} else {
					cntUnMatch++;
					data.add("×");
				}

				if (null != s3) {
					data.add(s3);
				} else {
					data.add("未知のフォーマット");
				}

				writer.writeCSVLine(data);

				if (0 == cntTotal % 100) {
					long tmEnd = System.nanoTime();
					double tmInterval = (double) (tmEnd - tmStart) / (double) (1000000000f);

					lblMessage.setText(String.format("%d 件処理しました。[%.2f sec]", cntTotal, tmInterval));
					txtMessageDetail.setText(String.format("一致件数　 : %7d 件\n不一致件数 : %7d 件\n不明件数　 : %7d 件", cntMatch, cntUnMatch, cntUnknown));
				}
			}

			if (cancelFlag) {
				lblMessage.setText("処理がキャンセルされました。");
			} else {
				long tmEnd = System.nanoTime();
				double tmInterval = (double) (tmEnd - tmStart) / (double) (1000000000f);

				lblMessage.setText(String.format("%d 件処理しました。[%.2f sec]", cntTotal, tmInterval));
				txtMessageDetail.setText(String.format("一致件数　 : %7d 件\n不一致件数 : %7d 件\n不明件数　 : %7d 件", cntMatch, cntUnMatch, cntUnknown));
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			Utility.release(writer);
			Utility.release(reader);
		}
	}

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

		pnlScroll.setBounds(x, y, width - (margin * 2), height - (y + margin));
	}
}
