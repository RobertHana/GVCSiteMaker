// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;


public class NavigationComponent extends WebsiteContainerBase implements WebsiteContainer
{
    public String pageType;


    /**
     * Designated constructor.
     */
     public NavigationComponent(WOContext aContext)
    {
        super(aContext);
    }



     /**
      * Returns true iff the user is an admin user.
      *
      * @return true iff the user is an admin user
      */
     public boolean isAdminUser()
     {
         return ((SMSession)session()).currentUser().isUnitAdmin();
     }



     /**
      * Returns true iff the user is an admin user and we are the admin app.
      *
      * @return true iff the user is an admin user and we are the admin app
      */
     public boolean showWebsiteRequestsLink()
     {
         return isAdminUser() && SMApplication.smApplication().isAdminApplication();
     }



     /**
      * Returns true iff the page type is "My Websites".
      *
      * @return true iff the page type is "My Websites"
      */
     public boolean myWebsiteSelected()
     {
         return "My Websites".equals(pageType);
     }


     /**
      * Returns true iff the page type is "Administration".
      *
      * @return true iff the page type is "Administration"
      */
     public boolean administrationSelected()
     {
         return "Administration".equals(pageType);
     }


     /**
      * Returns true iff the page type is "My User Info".
      *
      * @return true iff the page type is "My User Info"
      */
     public boolean userInfoSelected()
     {
         return "My User Info".equals(pageType);
     }


     /**
      * Returns true iff the page type is "Website Request".
      *
      * @return true iff the page type is "Website Request"
      */
     public boolean websiteRequestSelected()
     {
         return "Website Request".equals(pageType);
     }


     /**
      * Returns true iff the page type is "Support".
      *
      * @return true iff the page type is "Support"
      */
     public boolean supportSelected()
     {
         return "Support".equals(pageType);
     }



     /**
      * Returns a URL to the list of websites available to configure.
      *
      * @return a URL to the list of websites available to configure
      */
     public String returnToMainConfigurationPage()
     {
         return SMActionURLFactory.configSiteURL(context().request());
     }



     /**
      * Returns the main admin page.
      *
      * @return the main admin page
      */
     public WOActionResults adminMainPage()
     {
         session().defaultEditingContext().revert();
         if (SMApplication.smApplication().isAdminApplication())
         {
             return pageWithName("Main");
         }
         else
         {
             WORedirect redirect = new WORedirect(context());
             redirect.setUrl(SMActionURLFactory.adminMainPageURL());
             return redirect;
         }
     }



     /**
      * Returns the user info page after reverting the default editing context to avoid any potential problems with saving partial changes in the editing context.
      *
      * @return the user info page after reverting the default editing context to avoid any potential problems with saving partial changes in the editing context
      */
     public WOComponent myUserInfoPage()
     {
         session().defaultEditingContext().revert();
         if (SMApplication.smApplication().isAdminApplication())
         {
             WORedirect redirect = new WORedirect(context());
             redirect.setUrl(SMActionURLFactory.userInfoPageURL(context().request()));
             return redirect;
         }
         else
         {
             return pageWithName("UserInfoPage");
         }
     }



     /**
      * Returns the main admin page.
      *
      * @return the main admin page
      */
     public WOComponent websiteRequestPage()
     {
         session().defaultEditingContext().revert();
         return pageWithName("ManagePendingWebsiteRequests");
     }



     /**
      * Returns the user support page after reverting the default editing context to avoid any potential problems with saving partial changes in the editing context.
      *
      * @return the user support page after reverting the default editing context to avoid any potential problems with saving partial changes in the editing context
      */
     public WOComponent supportPage()
     {
         session().defaultEditingContext().revert();
         if (SMApplication.smApplication().isAdminApplication())
         {
             WORedirect redirect = new WORedirect(context());
             redirect.setUrl(SMActionURLFactory.supportPageURL(context().request()));
             return redirect;
         }
         else
         {
             return pageWithName("SupportPage");
         }
     }



     /**
      * Logs the user out and returns the LogoutPage.
      *
      * @return the LogoutPage.
      *
      * post return not null
      */
     public WOComponent logoutPage()
     {
         return pageWithName("LogoutPage");
     }



}
