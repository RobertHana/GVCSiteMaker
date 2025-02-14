// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;


/**
 * Implements the coloured tabs used on the Edit Data Access Section pages.
 */
public class ColouredTab extends WOComponent 
{

    protected final String DOCROOT_IMAGE_PATH = "/GVCSiteMaker/Images/";


    /**
     * Designated constructor.
     */
    public ColouredTab(WOContext context) 
    {
        super(context);
    }



    /**
     * This component is stateless.
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * This component does use binding synchronization.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Returns <code>true</code> if the tab should be displayed in the active (selected) state.
     *
     * @return <code>true</code> if the tab should be displayed in the active (selected) state.
     */
    public boolean isTabSelected()
    {
        return ((Boolean) valueForBinding("selected")).booleanValue();
    }



    /**
     * Returns the result of clicking on this tab if it is not active.  The active tab cannot be clicked.
     *
     * @return the result of clicking on this tab if it is not active.  
     */
    public WOComponent selectTab()
    {
        return (WOComponent) valueForBinding("action");
    }



    /**
     * Returns the absolute path of the image to display for this button.  The path depends on isTabSelected().
     *
     * @return the absolute path of the image to display for this button.
     */
    public String imageName()
    {
        return DOCROOT_IMAGE_PATH + valueForBinding("image") + (isTabSelected() ? "OnTab.gif" : "OffTab.gif");
    }


}
