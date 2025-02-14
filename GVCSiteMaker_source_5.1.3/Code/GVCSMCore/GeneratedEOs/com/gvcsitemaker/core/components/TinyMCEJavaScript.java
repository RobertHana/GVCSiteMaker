// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;


/**
 * The TinyMCE JavaScript. 
 */
public class TinyMCEJavaScript extends SMWOTextComponent
{


    /**
     * Designated constructor
     */     
    public TinyMCEJavaScript(WOContext context) 
    {
        super(context);
    }



    /**
     * Returns the direct action URL for TinyMCE external link list which includes the siteID to determine which sections and uploaded files to display in the list.
     * 
     * @return the direct action URL for TinyMCE external link list which includes the siteID to determine which sections and uploaded files to display in the list.
     */    
    public String linkListURL()
    {
        return SMActionURLFactory.linkListURL(context().request(), website(), section());
        /** ensure [return_not_null] Result != null; **/            
    }    



    /**
     * Returns the direct action URL for TinyMCE external image list which includes the siteID to determine which uploaded image files to display in the list.
     * 
     * @return the direct action URL for TinyMCE external image list which includes the siteID to determine which uploaded image files to display in the list.
     */        
    public String imageListURL()
    {
        return SMActionURLFactory.imageListURL(context().request(), website(), section());
        /** ensure [return_not_null] Result != null; **/                
    }    



    /**
     * Returns the editorSelector for the text area in this component.  This editorSelector is bound to the class of the text area wh
     *
     * @return the Website displaying this component.
     */    
    public String editorSelector()
    {
        return wysiwygEditorMode().equals(DataAccessStringColumn.SimpleMode) ? "simpleEditor" : "advancedEditor";
    }



}
