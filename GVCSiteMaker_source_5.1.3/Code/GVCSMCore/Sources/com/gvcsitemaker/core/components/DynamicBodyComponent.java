// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;

/**
 *  The dynamic body component lays out the page, it contains the HTML header and body tag.
 */
public class DynamicBodyComponent extends net.global_village.woextensions.WOComponent
{


    /**
     * Designated constructor.
     */
    public DynamicBodyComponent(WOContext context)
    {
        super(context);
    }



    /**
     * This method is overridden so variables are not synchronized with bindings.
     *
     * @return <code>false</code>
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    public String pageType()
    {
        return (String)valueForBinding("pageType");
    }


    public void setPageType(String newType)
    {
        setValueForBinding(newType, "pageType");
    }



    public String headerString()
    {
        return (String)valueForBinding("headerString");
    }


    public void setHeaderString(String newHeader)
    {
        setValueForBinding(newHeader, "headerString");
    }



    /**
     * Accessor for pageTitle.
     *
     * @return the title of the page.
     */
    public String pageTitle()
    {
        return (String)valueForBinding("pageTitle");
    }



    /**
     * Returns true if this page needs to use the previewURL javascript.  The page must also include the javascript component itself, this only modifies the body tag.
     *
     * @return the value of needsPreviewURLJavaScript binding, false if no binding
     */
    public boolean needsPreviewURLJavaScript()
    {
        boolean needsPreviewURLJavaScript = booleanValueForBinding("needsPreviewURLJavaScript", false);

        return needsPreviewURLJavaScript;
    }



    /**
     * Accessor to indicate whether the page displayed is from edit mode.
     * 
     * @return the passed in boolean value for inPlaceEditingMode
     */
    public boolean inPlaceEditingMode()
    {
        return booleanValueForBinding("inPlaceEditingMode", false);
    }



}
