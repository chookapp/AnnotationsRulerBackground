package com.chookapp.org.overviewrulerbackground.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.chookapp.org.overviewrulerbackground.preferences.messages"; //$NON-NLS-1$
	public static String OverviewRulerBackgroundPref_Color;
	public static String OverviewRulerBackgroundPref_Description;
	public static String OverviewRulerBackgroundPref_SysDefault;
	public static String OverviewRulerBackgroundPref_Warning;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
