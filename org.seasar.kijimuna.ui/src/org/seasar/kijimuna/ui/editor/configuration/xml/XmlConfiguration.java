/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.kijimuna.ui.editor.configuration.xml;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.seasar.kijimuna.ui.editor.configuration.ColorManager;
import org.seasar.kijimuna.ui.editor.configuration.SimpleAnnotationHover;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.editor.hyperlink.HyperlinkDetector;
import org.seasar.kijimuna.ui.editor.scanner.xml.DocColorScanner;
import org.seasar.kijimuna.ui.editor.scanner.xml.TagColorScanner;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class XmlConfiguration extends SourceViewerConfiguration
		implements XmlConsts {
	
    private IEditorPart editor;
	private XmlDoubleClickStrategy doubleClickStrategy;
	private ColorManager colorManager;
	private ITokenScanner docColorScanner;
	private ITokenScanner tagColorScanner;

	public XmlConfiguration(IEditorPart editor, ColorManager colorManager) {
		this.editor = editor;
		this.colorManager = colorManager;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			TYPE_COMMENT,
			TYPE_XML_DECL,
			TYPE_DOC_DECL,
			TYPE_TAG,
			IDocument.DEFAULT_CONTENT_TYPE
		};
	}
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new XmlDoubleClickStrategy();
		return doubleClickStrategy;
	}

	private ITokenScanner getTagColorScanner() {
		if (tagColorScanner == null) {
		    tagColorScanner = new TagColorScanner(colorManager);
		}
		return tagColorScanner;
	}

	private ITokenScanner getDocColorScanner() {
		if (docColorScanner == null) {
		    docColorScanner = new DocColorScanner(colorManager);
		}
		return docColorScanner;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = null;

		dr = new DefaultDamagerRepairer(getTagColorScanner());
		reconciler.setDamager(dr, TYPE_TAG);
		reconciler.setRepairer(dr, TYPE_TAG);
		
		dr = new DefaultDamagerRepairer(getTagColorScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getDocColorScanner());
		reconciler.setDamager(dr, TYPE_COMMENT);
		reconciler.setRepairer(dr, TYPE_COMMENT);

		dr = new DefaultDamagerRepairer(getDocColorScanner());
		reconciler.setDamager(dr, TYPE_XML_DECL);
		reconciler.setRepairer(dr, TYPE_XML_DECL);
		
		dr = new DefaultDamagerRepairer(getDocColorScanner());
		reconciler.setDamager(dr, TYPE_DOC_DECL);
		reconciler.setRepairer(dr, TYPE_DOC_DECL);
		
		return reconciler;
	}

	protected IFile getFile() {
	    IEditorInput input = editor.getEditorInput();
	    if(input instanceof IFileEditorInput) {
	        return ((IFileEditorInput)input).getFile();
	    }
	    return null;
	}
	
	protected XmlAssistProcessor createAssistProcessor() {
	    IFile file = getFile();
	    if(file != null) {
		    return new XmlAssistProcessor(file);
	    }
	    return null;
	}
	
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = null;
		XmlAssistProcessor processor = createAssistProcessor();
		if(processor != null) {
		    assistant = new ContentAssistant();
			assistant.setContentAssistProcessor(processor, TYPE_XML_DECL);
			assistant.setContentAssistProcessor(processor, TYPE_DOC_DECL);
			assistant.setContentAssistProcessor(processor, TYPE_TAG);
			assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			assistant.enableAutoActivation(true);
			assistant.setAutoActivationDelay(500);
		}
		return assistant;
	}

	public ITextHover getTextHover(
	        ISourceViewer sourceViewer, String contentType, int stateMask) {
		return null;
	}
	
	public int[] getConfiguredTextHoverStateMasks(
	        ISourceViewer sourceViewer, String contentType) {
		// TODO: CTRL or MOD1 ?
		return new int[]{ SWT.CTRL };
	}
	
	public ITextHover getTextHover(
	        ISourceViewer sourceViewer, String contentType) {
		return null;
	}
	
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new SimpleAnnotationHover(editor);
	}

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null)
			return null;

		XmlAssistProcessor processor = createAssistProcessor();
		return new IHyperlinkDetector[] {new URLHyperlinkDetector(sourceViewer), new HyperlinkDetector(sourceViewer, processor)};
	}

//	static class TraceAnnotationHover implements IAnnotationHover {
//		public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
//			return null;
//		}
//	}
//
//	static class TraceTextHover implements ITextHover {
//		public String getHoverInfo(final ITextViewer textViewer, final IRegion hoverRegion) {
//			final StyledText styledText = textViewer.getTextWidget();
//			styledText.getDisplay().asyncExec(new Runnable() {
//				public void run() {
//				    Display display = textViewer.getTextWidget().getDisplay();
//				    Color color = display.getSystemColor(SWT.COLOR_RED);
//				    StyleRange styleRange = new StyleRange();
//				    styleRange.start  = hoverRegion.getOffset();
//				    styleRange.length = hoverRegion.getLength();
//				    styleRange.fontStyle = SWT.BOLD;
//				    styleRange.foreground = color;
//				    textViewer.getTextWidget().setStyleRange(styleRange);
//					
//					Point start = styledText.getLocationAtOffset(hoverRegion.getOffset());
//					Point end = styledText.getLocationAtOffset(
//					        hoverRegion.getOffset() + hoverRegion.getLength());
//					StyleRange styleRange2 = styledText.getStyleRangeAtOffset(
//					        hoverRegion.getOffset());
//					display = styledText.getDisplay();
//					styleRange2.background = display.getSystemColor(SWT.COLOR_RED);
//					GC gc = new GC(styledText);
//					gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
//					gc.drawLine(start.x,start.y + 20,end.x,end.y + 20);
//				}				
//			});
//			return "test hover";
//		}
//
//		public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
//			ITypedRegion region;
//			try {
//				region = textViewer.getDocument().getPartition(offset);
//				return region;
//			} catch (BadLocationException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//	}
	
}