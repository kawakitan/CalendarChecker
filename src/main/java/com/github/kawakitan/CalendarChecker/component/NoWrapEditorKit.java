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
package com.github.kawakitan.CalendarChecker.component;

import java.awt.FontMetrics;

import javax.swing.JEditorPane;
import javax.swing.SizeRequirements;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * 
 * @author kawakitan
 */
public class NoWrapEditorKit extends StyledEditorKit {

	/** serialVersionUID */
	private static final long serialVersionUID = 340410453232315893L;

	private final SimpleAttributeSet attrs = new SimpleAttributeSet();

	@Override
	public void install(final JEditorPane c) {
		FontMetrics fm = c.getFontMetrics(c.getFont());
		int tabLength = fm.charWidth('m') * 4;
		TabStop[] tabs = new TabStop[100];
		for (int j = 0; j < tabs.length; j++) {
			tabs[j] = new TabStop((j + 1) * tabLength);
		}
		TabSet tabSet = new TabSet(tabs);
		StyleConstants.setTabSet(attrs, tabSet);
		super.install(c);
	}

	@Override
	public Document createDefaultDocument() {
		Document d = super.createDefaultDocument();
		if (d instanceof StyledDocument) {
			((StyledDocument) d).setParagraphAttributes(0, d.getLength(), attrs, false);
		}
		return d;
	}

	@Override
	public ViewFactory getViewFactory() {
		return new NoWrapViewFactory();
	}

	private static class NoWrapViewFactory implements ViewFactory {
		@Override
		public View create(Element elem) {
			String kind = elem.getName();
			if (null != kind) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new LabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new NoWrapParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new LabelView(elem);
		}
	}

	private static class NoWrapParagraphView extends ParagraphView {
		public NoWrapParagraphView(Element elem) {
			super(elem);
		}

		@Override
		protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
			SizeRequirements req = super.calculateMinorAxisRequirements(axis, r);
			req.minimum = req.preferred;
			return req;
		}

		@Override
		public int getFlowSpan(int index) {
			return Integer.MAX_VALUE;
		}
	}
}