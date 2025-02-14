// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;


import com.gvcsitemaker.core.utility.URLUtils;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;


/**
 * This editor used by EditSection to edit an External URL section implemented as a HyperlinkComponent.
 */
public class ExternalURLEditor extends BaseEditor
{


    /**
     * Designated constructor
     */
    public ExternalURLEditor(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Work horse method for updating. Saves the changes, optionally shows a preview of the section in another browser window, and goes back to the ConfigPage.
     *
     * @param shouldPrview <code>true</code> if the Section preview should be shown.`
     * @return the current page
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        WOComponent resultPage = context().page();
        
        // Validate before saving
        if ( ! hyperLink().hasAllRequiredBindings())
        {
            if (hyperLink().linkType() == null)
            {
                resultPage = error("You must select the type and either provide a URL or select an uploaded file.");
            }
            else if (hyperLink().isExternalLink())
            {
                resultPage = error("You must provide a URL.");
            }
            else
            {
                resultPage = error("You must select an uploaded file.");
            }
        }
        else if ( ! hyperLink().isHrefValid())
        {
            resultPage = error(URLUtils.invalidURLErrorMessage(hyperLink().href()));
        }
        else
        {
            editingContext().saveChanges();

            if (shouldPreview)
            {
                showPreview();
            }
            else
            {
                resultPage = pageWithName("ConfigTabSet");
            }
        }

        return resultPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        return pageWithName("ConfigTabSet");
    }



    /**
     * Convenience method returning the Hyperlink implementing this Section.
     *
     * @return the Hyperlink implementing this Section.
     */
    public com.gvcsitemaker.core.pagecomponent.Hyperlink hyperLink()
    {
        /** require [section_not_null] section() != null; **/
        
        return (com.gvcsitemaker.core.pagecomponent.Hyperlink) section().component();

        /** ensure [result_not_null] Result != null; **/
    }
}
