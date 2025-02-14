// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.pages.FileReplace;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

/**
 * This is used to display and edit the ListLinkComponent sub-class of PageComponent.
 */
public class ListLink extends ComponentPrimitive
{

    public SiteFile aFile;

    /**
     * Designated constructor.
     */
    public ListLink(WOContext aContext)
    {
        super(aContext);
    }
    


    /**
     * Returns true, this component is stateless.
     *
     * @return true, this component is stateless.
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Returns <code>componentObject()</code> downcast to the <code>ListLink</code>
     * page component.
     *
     * @return <code>componentObject()</code> downcast to the <code>ListLink</code>
     * page component
     */
    public com.gvcsitemaker.core.pagecomponent.ListLink listLinkComponent()
    {
        /** require [has_componentObject] componentObject() != null;  **/
        return (com.gvcsitemaker.core.pagecomponent.ListLink)componentObject();
        /** ensure [valid_result] Result != null;  **/
    }
    

        
    /**
     * Returns the URI for an internally held file.
     *
     * @return the URI for an internally held file.
     */
    public String linkURI()
    {
        /** require [has_componentObject] componentObject() != null;  **/
        return listLinkComponent().linkURI(context().request());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the target for this link, null to open in the same window, or
     * a target name to open in a different window.
     *  
     * @return the target for this link
     */
    public String linkTarget()
    {
        /** require [has_componentObject] componentObject() != null;  **/
        return listLinkComponent().shouldOpenInNewWindow() ? "_blank" : null;
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
