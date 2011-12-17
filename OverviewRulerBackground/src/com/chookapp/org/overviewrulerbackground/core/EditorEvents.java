/*******************************************************************************
 * Copyright (c) Gil Barash - chookapp@yahoo.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gil Barash - initial API and implementation
 *    
 * Thanks to:
 *    emil.crumhorn@gmail.com - Some of the code was coped from the 
 *    "eclipsemissingfeatrues" plugin. 
 *******************************************************************************/
package com.chookapp.org.overviewrulerbackground.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.chookapp.org.overviewrulerbackground.Activator;
import com.chookapp.org.overviewrulerbackground.preferences.PreferenceConstants;

public class EditorEvents implements IWindowListener, IPartListener2
{

	private static EditorEvents sInstance = new EditorEvents();
	private Collection<IWorkbenchWindow> fWindows = new HashSet<IWorkbenchWindow>();
	  
	   
    public static EditorEvents getInstance()
    {
        return sInstance;
    }  
    
	
    public void install() 
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
        {
            Activator.log("can't find workbanch");
            return;
        }

        // listen for new windows
        workbench.addWindowListener(this);
        IWorkbenchWindow[] wnds= workbench.getWorkbenchWindows();
        for (int i = 0; i < wnds.length; i++) 
        {
            IWorkbenchWindow window = wnds[i];
            register(window);
        }
        // register open windows
        //            IWorkbenchWindow ww= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        //            if (ww != null) {
        //                IWorkbenchPage activePage = ww.getActivePage();
        //                if (activePage != null) {
        //                    IWorkbenchPartReference part= activePage.getActivePartReference();
        //                    if (part != null) {
        //                        partActivated(part);
        //                    }
        //                }
        //            }

    }

    public void uninstall() 
    {
        for (Iterator<IWorkbenchWindow> iterator = fWindows.iterator(); iterator.hasNext();) 
        {
            IWorkbenchWindow window = iterator.next();
            unregister(window);
        }      
    }

    private void register(IWorkbenchWindow wnd) 
    {
        wnd.getPartService().addPartListener(this);
        fWindows.add(wnd);
        IWorkbenchPage[] pages = wnd.getPages();
        for (IWorkbenchPage page : pages)
        {
            IEditorReference[] editorRefs = page.getEditorReferences();
            for (IEditorReference editorRef : editorRefs)
            {
                partActivated(editorRef);
            }
        }
        
        IWorkbenchPage page = wnd.getActivePage();
        if( page != null )
        {
            activated(page.getActivePartReference());
        }
    }
    
    /*
     * This function is expected to be closed when a window is closed (including 
     *  when eclipse closes), so the parts have already been closed.
     * This is because I don't dispose the higlighers in this function... 
     */
    private void unregister(IWorkbenchWindow wnd) 
    {
        wnd.getPartService().removePartListener(this);
        fWindows.remove(wnd);
    }
    
    
    /* window events */

    @Override
    public void windowActivated(IWorkbenchWindow window)
    {
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow window)
    {
    }

    @Override
    public void windowOpened(IWorkbenchWindow window) 
    {
        register(window);
    }

    @Override
    public void windowClosed(IWorkbenchWindow window) 
    {
        unregister(window);
    }
    
    
    /* part events */
    
    @Override
    public void partActivated(IWorkbenchPartReference partRef)
    {
        activated(partRef);
    }   

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef)
    {
    	activated(partRef);
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef)
    {
    }

	
	private void activated(IWorkbenchPartReference partRef) 
	{
		try {
	        if( partRef == null )
	            return;
	       
			if( Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_SYS_DEF))
				return;
			
			RGB rgb = PreferenceConverter.getColor(
							Activator.getDefault().getPreferenceStore(),
							PreferenceConstants.P_COLOR);

			IEditorPart editorPart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if( editorPart == null )
				return;
			
			SourceViewer sv = (SourceViewer) callGetSourceViewer((AbstractTextEditor) editorPart);
			OverviewRuler ruler = (OverviewRuler) getOverviewRuler(sv);
			ruler.getControl().setBackground(new Color(Display.getDefault(),rgb));
		} catch (Exception e) {
			System.err.println(e);
		}		
	}	

	/**
	 * Calls AbstractTextEditor.getSourceViewer() through reflection, as that method is normally protected (for some
	 * ungodly reason).
	 * 
	 * @param AbstractTextEditor to run reflection on
	 */
	private ITextViewer callGetSourceViewer(AbstractTextEditor editor) throws Exception {
		try {
			Method method = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer"); //$NON-NLS-1$
			method.setAccessible(true);

			return (ITextViewer) method.invoke(editor);
		} catch (NullPointerException npe) {
			return null;
		}
	}
	
    /**
     * Gets the overview ruler off the SourceViewer by using Java Reflection. Only way as far as I can tell.
     * 
     * @param viewer SourceViewer
     * @return overview ruler or null if errors were encountered
     */
    private IOverviewRuler getOverviewRuler(SourceViewer viewer) {
        try {
            Field f = SourceViewer.class.getDeclaredField("fOverviewRuler"); //$NON-NLS-1$
            f.setAccessible(true);
            return (IOverviewRuler) f.get(viewer);
        } catch (Exception err) {
            return null;
        }
    }	
	  
}
