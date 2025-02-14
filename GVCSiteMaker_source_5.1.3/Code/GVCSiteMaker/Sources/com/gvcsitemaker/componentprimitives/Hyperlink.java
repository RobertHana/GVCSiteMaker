// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;


import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.pages.FileReplace;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;


/**
 * This is used to display and edit the HyperlinkComponent sub-class of PageComponent.
 */
public class Hyperlink extends ComponentPrimitive
{
    public SiteFile aFile;
    

    /**
     * Designated constructor.
     */
    public Hyperlink(WOContext aContext)
    {
        super(aContext);
    }


    
    /**
     * @return the domain specifc URL for this hyperlink.  The domain can change for uploaded files, but
     * not for external links.
     */
    public String url()
    {
        return ((com.gvcsitemaker.core.pagecomponent.Hyperlink)componentObject()).urlForRequest(context().request());
    }
      
    
    
    /**
     * Action method to replace a SiteFile.
     *
     * @return an instance of the FileUpload page
     */
    public WOComponent replaceFile()
    {
        FileReplace nextPage = (FileReplace)pageWithName("FileReplace");
        nextPage.setCurrentFile(componentObject().relatedFile());

        return nextPage;
    }  
    
    
    
    /**
     * Returns true if replace link should be shown, false otherwise
     *
     * @return true if replace link should be shown, false otherwise
     */    
    public boolean shouldShowReplaceLink()
    {
        return componentObject().relatedFile() != null;
    }    
}
