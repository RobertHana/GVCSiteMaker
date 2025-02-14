// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;


/**
 * The text editor component that uses the TinyMCE.
 */
public class TinyMCEComponent extends RichTextComponent
{
    public boolean isBrowserCompliant;


    /**
     * Designated constructor
     */
    public TinyMCEComponent(WOContext context)
    {
        super(context);
        isBrowserCompliant = false;
    }


    /**
     * Returns true if the TinyMCE init javascript need to be included in the source or not.  
     * There should only be one and only one TinyMCE init javascript included in a page.  
     * Therefore, based on the passed index for this component, it will only include the javascript 
     * if it is the first component in the page and if it isBrowserCompliant. If this component is displaying
     * a DataAccessStringColumn, it will only display the javascript if it is the first DataAccessStringColumn column in the displayed row.
     * 
     * @return true if the TinyMCE init javascript need to be included in the source or not.  There should only be one and only one TinyMCE init javascript included in a page.  Therefore, based on the passed index for this component, it will only include the javascript if it is the first component in the page and if it isBrowserCompliant.
     */
    public boolean shouldIncludeJavascript()
    {
        boolean isFirst = true;

        //Check if this is not the first string column displayed in a DA section.
        if (section().component() instanceof DataAccess)
        {
            DataAccessMode mode = (DataAccessMode) ((DataAccess) section().component()).componentForMode(currentDataAccessMode());
            int stringIndex = -1;
            java.util.Enumeration objectEnum = mode.visibleFields().objectEnumerator();
            while (objectEnum.hasMoreElements())
            {
                Object visibleField = (Object) objectEnum.nextElement();
                if ((visibleField instanceof DataAccessStringColumn)
                        && (!((DataAccessStringColumn) visibleField).wysiwygEditorMode().equals(DataAccessStringColumn.OffMode)))
                {
                    stringIndex++;
                }
                if ((visibleField instanceof DataAccessStringColumn) && (((DataAccessStringColumn) visibleField).normalizedFieldName().equals(columnName())))
                {
                    isFirst = (stringIndex == 0);
                }
            }
        }

        System.out.println("shouldIncludeJavascript: " + (isBrowserCompliant && (index() == 0) && isFirst));
        return (isBrowserCompliant && (index() == 0) && isFirst);
    }



    /**
     * Overridden to  check browser compliance once
     */
    public void awake()
    {
        super.awake();
        isBrowserCompliant = (((SMApplication) application()).properties().booleanPropertyForKey("CheckBrowserCompliance") && (RequestUtils
                .isBrowserEditorCompliant(context().request(), ((SMApplication) application()).properties().arrayPropertyForKey("CompliantBrowsers"))));
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
