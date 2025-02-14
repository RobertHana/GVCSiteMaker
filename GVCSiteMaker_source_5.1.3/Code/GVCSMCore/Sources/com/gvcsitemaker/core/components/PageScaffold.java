// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;

import net.global_village.woextensions.*;


/*
 * * PageScaffold is the concrete base class for all components displaying a
 * Section of a Website. The PageScaffold controls access, holds all the
 * elements and the Style used which controls what components are used to
 * display the various bits of the page: Banner, NavBar, Footer, and Section
 * contents. TODO There is code in this class that is very similar to
 * DisplayFile (appendToResponse etc.). This should be refactored into a
 * superclass.
 */
public class PageScaffold extends WebsiteContainerBase
{

    protected Section sectionToDisplay; // The Section whose contents are being displayed.
    protected Style activeSectionStyle; // The Style being used.  May not be the Website's Style
    protected boolean saveInPageCache; // True if this Section has state and needs to be cached
    protected SMSectionHierarchy sectionHierarchy; // Used NavBar, NavPopups , prev/next section etc
    protected SectionVersion versionToDisplay;


    /**
     * Designated constructor.
     */
    public PageScaffold(WOContext aContext)
    {
        super(aContext);
        setSaveInPageCache(false); // Most Sections have no state
    }



    /**
     * Overridden to handle authentication for access group protected sections.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        /** require [same_ec] ((SMSession)session()).authenticatedRemoteParticipantGroup() == null ||
                              ((SMSession)session()).authenticatedRemoteParticipantGroup().editingContext().equals(sectionToDisplay().editingContext());
         **/
        SMSession session = (SMSession) session();
        DebugOut.println(1, "Displaying: " + context.request().uri());
        DebugOut.println(16, "currentUserID is: " + session.currentUser().userID());

        // TODO Ideally this would get pushed into RemoteParticipationGroup.isMember(User), BUT that needs session to verify
        // We might want to start putting the session as the EC's delegate.  I have code in HH to do this sort of thing
        boolean canRemoteParticipationUserViewSection = session.isRemoteParticipantSession() && session.authenticatedRemoteParticipantGroup() != null
                && session.authenticatedRemoteParticipantGroup().parentWebsite().globalID().equals(sectionToDisplay().website().globalID())
                && sectionToDisplay().groups().containsObject(session.authenticatedRemoteParticipantGroup());
        DebugOut.println(40, "session.isRemoteParticipantSession: " + session.isRemoteParticipantSession());
        DebugOut.println(40, "section to display includes remote participant group: "
                + sectionToDisplay().groups().containsObject(session.authenticatedRemoteParticipantGroup()));
        DebugOut.println(40, "session.authenticatedRemoteParticipantGroup: " + session.authenticatedRemoteParticipantGroup());
        if (session.authenticatedRemoteParticipantGroup() != null)
        {
            DebugOut.println(40, "Parent website is the same as sectionToDisplay: "
                    + session.authenticatedRemoteParticipantGroup().parentWebsite().globalID().equals(sectionToDisplay().website().globalID()));
        }

        if ((canRemoteParticipationUserViewSection || sectionToDisplay().isViewableByUser(session.currentUser()))
                && ((versionToDisplay() == null) || ((versionToDisplay() != null) && (versionToDisplay().canViewNonCurrentVersions(session.currentUser())))))
        {
            // User is authenticated and has permission to view, or section has public access.
            DebugOut.println(16, "==== ACCESS ALLOWED for user " + session.currentUser().userID());

            // The section determines the actual MIME type of the response returned
            response.setHeader(sectionToDisplay().actualMimeType(), Response.ContentTypeHeaderKey);

            super.appendToResponse(response, context);

            // Once the section has been displayed, update the list of recently visited sites.
            session.recordVisitToWebsite(sectionToDisplay().website(), context());
        }
        else if (session.isUserAuthenticated())
        {
            // User is authenticated but does not have permission to view.  Show them the Denied! page.
            DebugOut.println(16, "User authenticated but not allowed to see section ");
            DebugOut.println(16, "homeSection is: " + website().homeSection());

            response.setContent(sectionError().generateResponse().content());
        }
        else
        // Access is not public and the user has not yet authenticated.  Send them to the login page.
        {
            DebugOut.println(16, "==== User not authenticated, going to login page");
            DebugOut.println(16, "Section's groups are: " + sectionToDisplay().groups());

            SMAuthComponent.redirectToLogin(response, context, "Authentication is required to access this Area.");
        }

        DebugOut.println(16, "Leaving...");
    }



    /**
     * Returns the error page to display if the user has authenticated, but does not have sufficient permissions to view this section.
     *
     * @return the error page to display if the user has authenticated, but does not have sufficient permissions to view this section.
     *
     * @return != null
     */
    protected WOComponent sectionError()
    {
        // We can't just use the referer header to determine where to go back to as this is not useful
        // if the user just passed through a login screen.  The easiest, consistent thing to do is to
        // go back to the site's home page.
        WORedirect redirectToHomePage = (WORedirect) pageWithName("WORedirect");
        redirectToHomePage.setUrl(website().homeSection().sectionURL(context().request()));

        ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage");
        errorPage.setReturnPage(redirectToHomePage);
        errorPage.setMessage("Sorry, you don't have permission to view that section.");
        errorPage.setReason("Insufficient privileges.");

        return errorPage;
    }



    /**
     * This method is what allows sites to have different styles.  The HTML for a site (could even be at a section level) is taken from htmlString().  This is merged with the component bindings taken from bindingsString() to form the final template used.
     *
     * @return the WOElement object corresponding to section style template for this website.
     */
    public WOElement template()
    {
        return HtmlTemplateUtils.elementsFromTemplateWithBindings(htmlString(), bindingsString(), context().session().languages());
    }



    /**
     * Returns a String containing the bindings to use for display of this section.  This is the WOD file for the PageScaffold WOComponent, the HTML template comes from the Style.  This can be overridden in sub-classes to return a different value.
     *
     * @return a String containing the bindings to use for display of this section.
     */
    public String bindingsString()
    {
        return SMApplication.appProperties().stringPropertyForKey("PageScaffold.wod");
    }



    /**
     * Returns a String containing the HTML template to use for display of this section.  This is the HTML file for the PageScaffold WOComponent, the WOD file is loaded as resource.  This can be overridden in sub-classes to return a different value.
     *
     * @return a String containing the  HTML template to use for display of this section.
     */
    public String htmlString()
    {
        String htmlString = activeSectionStyle().template();

        // Null templates cause problems.
        if (htmlString == null)
        {
            htmlString = "";
        }

        boolean isHTMLSection = MIMEUtils.isHTMLMimeType(sectionToDisplay().actualMimeType());

        // All HTML sections get the Powered by GVC.SiteMaker link unless it is deactivated.  The injecting of the Powered By link below only needs to happen once per update of a template, and so could be cached.  This would improve performance somewhat.  Injection of the Powered By link is not done when a template is uploaded as it would then be present when the template was downloaded, edited, and uploaded again.  This would cause problems in determining if the link was already there, allow for the template writer to alter the link, and also make it more cumbersome to update the link (we would need to manually update each template).
        if (isHTMLSection && ((SMApplication) application()).properties().booleanPropertyForKey("ShowPoweredByLink"))
        {
            // Best attempt at finding the close body tag.  Invalid HTML will result in incorrect positioning of the link.
            int bodyCloseIndex = htmlString.toLowerCase().lastIndexOf("body");
            int angleBracketIndex = htmlString.lastIndexOf("<", bodyCloseIndex);

            // If the HTML is really broken, place the link at the very end.
            if ((bodyCloseIndex == -1) || (angleBracketIndex == -1))
            {
                angleBracketIndex = htmlString.length();
            }

            // Stick the Powered By link just before </body>
            StringBuffer htmlBuffer = new StringBuffer(htmlString);
            htmlBuffer.insert(angleBracketIndex, ((SMApplication) application()).properties().stringPropertyForKey("PoweredByLink"));
            htmlString = htmlBuffer.toString();
        }

        return htmlString;
    }



    /**
     * Returns the Style to use when displaying this Section.  This can be set by the object creating this PageScaffold to override the style defined for the Section.
     *
     * @return the Style to use when displaying this Section.
     */
    public Style activeSectionStyle()
    {
        return ((activeSectionStyle != null) ? activeSectionStyle : sectionToDisplay().activeSectionStyle());
    }



    /**
     * Returns the base URL for the Section being displayed in the context of website().  This should be used for all non-Data Access URLs generated to refer to this Section.
     */
    public String activeSectionUrl()
    {
        /** require [has_website] website() != null; [has_section] sectionToDisplay() != null; **/
        return SMActionURLFactory.sectionURL(website(), sectionToDisplay(), context().request());
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the secure login URL for the section being viewed.
     */
    public String loginUrl()
    {
        return SMActionURLFactory.secureLoginURLForRequest(context().request(), "Please login.");
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the logout URL
     */
    public String logoutUrl()
    {
        return SMActionURLFactory.logoutURL(context().request(), website().siteURL(context().request()));
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the base URL for the Section being displayed in the context of website().  This should be used for all Data Access URLs generated to refer to this Section.
     */
    public String activeDataAccessSectionUrl()
    {
        /** require [has_website] website() != null; [has_section] sectionToDisplay() != null; **/
        return SMDataAccessActionURLFactory.sectionURL(website(), sectionToDisplay(), context().request());
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return <code>true</code> if this is the last section
     */
    public boolean isLastSection()
    {
        return !sectionHierarchy().hasNextNavigableSection(sectionToDisplay());
    }



    /**
     * @return the URL for the next visible section
     */
    public String nextSectionUrl()
    {
        return SMActionURLFactory.sectionURL(website(), sectionHierarchy().nextNavigableSection(sectionToDisplay()).section(), context().request());
    }



    /**
     * @return <code>true</code> if this is the fist section
     */
    public boolean isFirstSection()
    {
        return !sectionHierarchy().hasPreviousNavigableSection(sectionToDisplay());
    }



    /**
    * @return the URL for the preceeding visible section
    */
    public String previousSectionUrl()
    {
        return SMActionURLFactory.sectionURL(website(), sectionHierarchy().previousNavigableSection(sectionToDisplay()).section(), context().request());
    }


    /* Generic setters and getters ***************************************/

    public void setActiveSectionStyle(Style newActiveSectionStyle)
    {
        activeSectionStyle = newActiveSectionStyle;
    }


    public Section sectionToDisplay()
    {
        return sectionToDisplay;
    }


    public void setSectionToDisplay(Section newSectionToDisplay)
    {
        /** require [valid_section] newSectionToDisplay != null; **/
        if (sectionToDisplay != newSectionToDisplay)
        {
            sectionToDisplay = newSectionToDisplay;
            sectionHierarchy = new SMSectionHierarchy(website(), sectionToDisplay);
        }
        /** ensure  [section_hierarchy] sectionHierarchy != null; **/
    }


    public void setSaveInPageCache(boolean value)
    {
        saveInPageCache = value;
    }


    public boolean saveInPageCache()
    {
        return saveInPageCache;
    }


    public SMSectionHierarchy sectionHierarchy()
    {
        return sectionHierarchy;
    }


    public SectionVersion versionToDisplay()
    {
        return versionToDisplay;
    }


    public void setVersionToDisplay(SectionVersion sectionVersion)
    {
        versionToDisplay = sectionVersion;
    }


}
