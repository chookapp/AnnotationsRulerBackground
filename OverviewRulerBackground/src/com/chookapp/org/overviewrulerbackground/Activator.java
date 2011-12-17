package com.chookapp.org.overviewrulerbackground;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.chookapp.org.overviewrulerbackground.core.EditorEvents;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin 
{

	// The plug-in ID
	public static final String PLUGIN_ID = "OverviewRulerBackground"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	    super.start(context);
	    plugin = this;
	    Display.getDefault().asyncExec(new Runnable() {
	        public void run() {
	            EditorEvents.getInstance().install();
	        }
	    });
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception 
	{
	    try
	    {
	    	EditorEvents.getInstance().uninstall();
	        plugin = null;
	    }
	    finally
	    {
	        super.stop(context);
	    }
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * @param e
	 */
	public static void log(Throwable e) 
	{
		getDefault().getLog().log(getStatus(e));
	}

	public static void log(String message) 
	{
		getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID, message));
	}
	
	/**
	 * @param e
	 * @return
	 */
	public static IStatus getStatus(Throwable e) 
	{
		return new Status(Status.WARNING, PLUGIN_ID, e.getLocalizedMessage(), e);
	}	
}
