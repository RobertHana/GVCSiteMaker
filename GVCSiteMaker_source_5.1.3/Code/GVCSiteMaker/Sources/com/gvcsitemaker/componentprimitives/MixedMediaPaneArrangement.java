// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import com.gvcsitemaker.core.support.*;

import com.webobjects.appserver.*;

import net.global_village.foundation.*;

/**
 * Manage pane arrangement layout, handles CSS styles for the table, rows and each pane
 */
public class MixedMediaPaneArrangement extends ComponentPrimitive
{
    /**
     * Designated constructor.
     */
    public MixedMediaPaneArrangement(WOContext context)
    {
        super(context);
    }



    /**
     * Convenience method to return the MixedMediaPaneArrangement PageComponent that it is displaying.
     */
    public com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement arrangement()
    {
    		return (com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement) componentObject();

        /** ensure [valid_result] Result != null;  **/
    }





    /**
     * Returns the direct action URL for TinyMCE external link list which includes the siteID to determine which sections and uploaded files to display in the list.
     *
     * @return the direct action URL for TinyMCE external link list which includes the siteID to determine which sections and uploaded files to display in the list.
     */
    public String linkListURL()
    {
        return SMActionURLFactory.linkListURL(context().request(), website(), arrangement().section());
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns the direct action URL for TinyMCE external image list which includes the siteID to determine which uploaded image files to display in the list.
     *
     * @return the direct action URL for TinyMCE external image list which includes the siteID to determine which uploaded image files to display in the list.
     */
    public String imageListURL()
    {
        return SMActionURLFactory.imageListURL(context().request(), website(), arrangement().section());
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns true if there is CSS to be used by the RichText Editor, false otherwise
     */
    public boolean hasEditorCSS()
    {
        return ! StringAdditions.isEmpty(editorCSS());
    }



    /**
     * The CSS to be used by the RichText Editor
     */
    public String editorCSS()
    {
            return (String) valueForBinding("editorCSS");
    }

}