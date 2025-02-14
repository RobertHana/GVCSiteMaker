// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.components;
import com.gvcsitemaker.core.support.SectionDisplay;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

/**
 * This component provides a simple reusable bit for previewing sitemaker websites.
 * Just make certain that your <BODY> tag includes onLoad="previewURL()" like this:
 *
 *  <BODY onLoad="previewURL()">
 *
 * This is easily accomplished through a binding on the DynamicBodyComponent in SMCore.
 *
 * Oh, and you need to bind to previewURL. NOTE: bind "" or null to
 * previewURL to NOT preview the page. That way you can set it when
 * you want the page to pop up. I recommend resetting previewURL to ""
 * in awake and only set it to a value when you want to preview the
 * URL
 */
public class PreviewJavaScript extends WOComponent
{

    protected String previewURL;

    
    public PreviewJavaScript(WOContext aContext)
    {
           super(aContext);
    }



    /**
     * Sets the preview URL and adds the publication override key to the end.
     */
    public void setPreviewURL(String newPreviewURL) {
        previewURL = newPreviewURL;

        // Add config value to preview URL so that isPublishable can be overidden when previewing.
        if ((newPreviewURL != null) && (newPreviewURL.length() > 0))
        {
            previewURL = previewURL + SectionDisplay.configFlagForSession(session());
        }
    }


    public String previewURL() {
        return previewURL;
    }


    /**
     * Sets the preview URL without adding the publication override key to the end.  This is needed for previewing external sites.
     */
    public void setPreviewURLWithoutConfig(String newPreviewURL) {
        previewURL = newPreviewURL;
    }


    public String previewURLWithoutConfig() {
        return previewURL;
    }


}
