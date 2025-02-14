// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.SectionStyle;
import com.gvcsitemaker.core.components.ErrorPage;
import com.gvcsitemaker.core.components.SMAuthComponent;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;


/**
 * This page displays a list of basic information on all SectionStyles that the logged in User can configure.  A SectionStyles can be selected for configuration or a new SectionStyle can be created.  The User is forced to authenticate before accessing this page.
 */
public class SectionStyleManagementPage extends SMAuthComponent
{

    /** @TypeInfo SectionStyle */
    protected NSArray allStyles;		// All SectionStyles that this User can configure.
    public SectionStyle aStyle;		// Item in WORepetition over allStyles, public for JASS


    /**
     * Designated constructor.
     */
    public SectionStyleManagementPage(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to handle authentication.
     */
    public void appendToResponse( WOResponse aResponse, WOContext aContext)
    {
        // The first time through the user may not be authenticated.  Don't do this until after login.
        if ( ((Session)session()).isUserAuthenticated())
        {
            if ( ! ((Session)session()).currentUser().canAdminStyles())
            {
                ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
                errorPage.setMessage("Access Refused.");
                errorPage.setReason("This area is for template and system administrators only.");

                aResponse.setContent(errorPage.generateResponse().content());
            }
            else
            {
                // Get the styles that this user can configure.
                if (allStyles == null)
                {
                    allStyles = SectionStyle.stylesAdvailableToAdmin(((Session)session()).currentUser());
                }

                super.appendToResponse(aResponse, aContext);
            }
        }
        else
        {
            super.appendToResponse(aResponse, aContext);
        }
    }



    /**
     * Returns a new instance of ConfigSectionStylePage set up to create a new SectionStyle.
     *
     * @return a new instance of ConfigSectionStylePage set up to create a new SectionStyle.
     */
    public WOComponent createNewStyle()
    {
        ConfigSectionStylePage nextPage = (ConfigSectionStylePage)pageWithName("ConfigSectionStylePage");
        nextPage.createNewStyle();

        return nextPage;
    }



    /**
     * Returns a new instance of ConfigSectionStylePage set up to configure aStyle.
     *
     * @return a new instance of ConfigSectionStylePage set up to configure aStyle.
     */
    public WOComponent configureStyle()
    {
        ConfigSectionStylePage nextPage = (ConfigSectionStylePage)pageWithName("ConfigSectionStylePage");
        nextPage.editStyle(aStyle);

        return nextPage;
    }


  
    /**
     * Translates aStyle.isPublished() into "Yes" or "No".
     *
     * @return "Yes" or "No"
     */
    public String isPublishedString()
    {
        /** require [a_style_not_null] aStyle != null; **/
        
        return aStyle.isPublished() ? "Yes" : "No";
    }



    /* Generic setters and getters ***************************************/
    
    /** @TypeInfo SectionStyle */
    public NSArray allStyles() {
        return allStyles;
    }

}
