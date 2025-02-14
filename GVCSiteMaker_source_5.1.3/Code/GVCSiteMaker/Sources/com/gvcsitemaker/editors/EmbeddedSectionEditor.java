// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;


/**
* This editor used by EditSection to edit an Embedded Section section implemented as a EmbeddedSection PageComponent.  This page needs to handle these situations:<br><br>
 *<ol>
 *  <li>The Section was just added, or the embedded (linked) Section has been deleted - show only the input for the embedded site ID</li>
 *  <li>The section has already been created and the embedded (linked) Section exists - show the input for the embedded site ID (prepopulated) and the popup for selection a Section (prepopulated).</li>
 *  <li>The Emebedded Site ID has been changed and Find Site clicked - update the list of Sections which can be embedded and refresh page.</li>
 *  <li>The Emebedded Site ID has been changed and Upate and Done clicked - update the list of Sections which can be embedded, show the "no Section selected" error message, and refresh page.</li>
 *  <li>An invalid or missing Emebedded Site ID has been entered and either Find Site for Update and Done has been clicked - show the site ID error message and redisplay the page.</li>
 *</ol>
 */
public class EmbeddedSectionEditor extends BaseEditor 
{

    protected String embeddedSiteID;		// siteID of the Website embedded in this Section
    public String siteErrorMessage;		// Error message if the selected Website cannot be embedded in this Section

    /** @TypeInfo Section */
    protected NSArray embeddableSections;	// The sections in selectedWebsite which can be emebedded

    /** @TypeInfo Section */
    public Section aSection;			// For WORepetition over embeddableSections
    public String dummyValue;		// For WOHiddenField
    protected Section selectedSection;		// The Section selected in the popup
    protected Website selectedWebsite;		// The Website corresponding to emebddedSiteID
    protected boolean isMissingSelectedSection;	// Flag to determine if user failed to select a Section


    
    /**
     * Designated constructor
     */
    public EmbeddedSectionEditor(WOContext context)
    {
        super(context);
        setEmbeddedSiteID(null);
        setSelectedWebsite(null);
    }



    /**
     * Clears error conditions / messages.
     */
    public void resetErrors()
    {
        siteErrorMessage = null;
        setIsMissingSelectedSection(false);
    }



    /**
     * Overridden to handle changes to embeddedSiteID and update dependant items when it changes.
     *
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        String previousEmbeddedSiteID = embeddedSiteID();
        resetErrors();
        
        super.takeValuesFromRequest(aRequest, aContext);

        // If embeddedSiteID() changes as a result of what the user submitted we need to update the selectedWebsite etc.
        if ( ((embeddedSiteID() != null) && ! embeddedSiteID().equals(previousEmbeddedSiteID)) ||
             (embeddedSiteID() == null) )
        {
            setSelectedWebsite(null);
            validateSelectedWebsite();
            if (isSiteIDValid())
            {
                Website siteForSiteID = Website.websiteForSiteID(embeddedSiteID(), editingContext());
                setSelectedWebsite(siteForSiteID);
            }
        }

        /** ensure
            [selected_website_matches_id] (selectedWebsite() == null) || selectedWebsite().siteID().toLowerCase().equals(embeddedSiteID());
        [selected_section_is_valid] (selectedSection() == null) || selectedWebsite().sections().containsObject(selectedSection());
         **/
    }


    

    /**
     * Method bound to the "Update & Done" button. Saves the changes and goes back to the ConfigTabSet or redisplays this page if the changes cannot be saved.
     *
     * @return the current page
     */
    public WOComponent updateDoneAction()
    {
        WOComponent resultPage;

        if( ! (isSiteIDValid() && isSelectedSectionValid()) )
        {
            setIsMissingSelectedSection( ! isSelectedSectionValid());
            resultPage = context().page();
        }
        else
        {
            embeddedSectionComponent().linkToSection(selectedSection());
            editingContext().saveChanges();

            resultPage = pageWithName("ConfigTabSet");
        }

        return resultPage;

        /** ensure [result_is_valid] Result != null;  **/
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
     * Finds validates what was entered for the Site ID of the site to embed and updates the page accordingly, showing either a list of sections which can be embedded, or an error message.
     */
    public WOComponent findSite()
    {
        // Everything happens in takeValues!
        return context().page();

        /** ensure [result_is_valid] Result != null;  **/
    }




    /**
     * Returns <code>true</code> if the Site ID entereed is not valid for embedding in this Section and there is an error message to display to the user.
     *
     * @return <code>true</code> if the Site ID entereed is not valid for embedding in this Section and there is an error message to display to the user.
     */
    public boolean isSiteIDValid()
    {
        return (siteErrorMessage == null);
    }



    /**
     * Returns <code>true</code> if validation failed for embedding the site in this Section and there is an error message to display to the user.
     *
     * @return <code>true</code> if validation failed for embedding the site in this Section and there is an error message to display to the user.
     */
    public boolean isSelectedSectionValid()
    {
        // is just != null sufficient?
        return (selectedSection() != null) && selectedSection().website().equals(selectedWebsite());
    }



    /**
     * Sets the Section being edited.  Overridden so that the embedded siteID can be driven into the UI.
     *
     * @param aSection - the  Section to be edited.
     */
    public void setSection(Section newSection)
    {
        /** require
        [parameter_is_valid] newSection != null;
        [has_component] newSection.component() != null;
        **/

        if ( ! newSection.equals(section()))
        {
            super.setSection(newSection);

            // If the section already has a site embedded in it, record the siteID of the emebedded site so that it can be displayed in the UI.
            if ( ! embeddedSectionComponent().hasTargetBeenDeleted() )
            {
                // Set the Site ID for the UI
                setEmbeddedSiteID( embeddedSectionComponent().linkedSection().website().siteID() );

                // Set the Website and list of sections
                setSelectedWebsite(embeddedSectionComponent().linkedSection().website());

                // Select the current choice
                setSelectedSection(embeddedSectionComponent().linkedSection());
            }

            // This is a check constraint as it only holds true when the section is changed.  Changing the emebedded website or selected section in the UI will make this not hold when binding synchronization pushes the section down again.
            /** check [page_correctly_initialized]
            embeddedSectionComponent().hasTargetBeenDeleted() ||
            (selectedSection().equals(embeddedSectionComponent().linkedSection()) &&
             selectedWebsite().equals(embeddedSectionComponent().linkedSection().website()) &&
             embeddedSiteID().equalsIgnoreCase(embeddedSectionComponent().linkedSection().website().siteID()) );
             **/
        }

        /** ensure  [section_was_set] section().equals(newSection);  **/
    }



    
    /**
     * Determines if embeddedSiteID() is valid for embedding into this section.  If it is not then siteErrorMessage is set to a user readable message about why it is not valid.
     */
    public void validateSelectedWebsite()
    {
        DebugOut.println( 20, " = = = = = Requested Embedded Site ID: " + embeddedSiteID() );

        if (embeddedSiteID()  == null )
        {
            siteErrorMessage = "ERROR: You must enter a SiteID.";
        }
        else
        {
            Website siteForSiteID = Website.websiteForSiteID(embeddedSiteID(), editingContext());
            if (siteForSiteID  == null )
            {
                siteErrorMessage = "ERROR: Web site with ID: [" + embeddedSiteID() +
                "] could not be found, please double check spelling and punctuation.";
            }
            else if ( website().equals(siteForSiteID) )
            {
                siteErrorMessage = "ERROR: You cannot embed sections of a website within itself.";
            }
        }

        // Ensures that the site ID is valid or the error message is set.
    }



    /**
     * Sets the selected Website (the Website corresponding to emebeddedSiteID() from which the Section to emebed can be selected).  When the selected Website is changed, embeddableSections is updates, and the selectedSection is cleared.
     *
     * @param newSelectedWebsite - the Website to make the selected Website, or null to remove the selected Website.
     */
    public void setSelectedWebsite(Website newSelectedWebsite)
    {
        if (newSelectedWebsite != selectedWebsite())
        {
            selectedWebsite = newSelectedWebsite;
            setSelectedSection(null);

            if (selectedWebsite != null)
            {
                setEmbeddableSections(selectedWebsite.embeddableSections());
            }
            else
            {
                setEmbeddableSections(NSArray.EmptyArray);
            }

        }
    }



    /**
     * Returns the EmbeddedSection PageComponent which implements the section being edited.
     *
     * @return the EmbeddedSection PageComponent which implements the section being edited.
     */
    public com.gvcsitemaker.core.pagecomponent.EmbeddedSection embeddedSectionComponent()
    {
        return ((com.gvcsitemaker.core.pagecomponent.EmbeddedSection)section().component());
    }



    /**
     * Convenience method for accessing the editing context for this page.
     */
    public EOEditingContext editingContext()
    {
        return section().editingContext();
    }


    /**  Generic accessor and mutator methods below here  **/

    /** @TypeInfo Section */
    public NSArray embeddableSections() {
        return embeddableSections;
    }
    public void setEmbeddableSections(NSArray newEmbeddableSections) {
        embeddableSections = newEmbeddableSections;
    }

    public String embeddedSiteID() {
        return embeddedSiteID;
    }
    public void setEmbeddedSiteID( String newEmbeddedSiteID ) {
        embeddedSiteID = newEmbeddedSiteID;
        if (embeddedSiteID != null)
        {
            embeddedSiteID = embeddedSiteID.toLowerCase();
        }
    }


    public boolean hasFoundWebsite() {
        return selectedWebsite() != null;
    }
    public void setHasFoundWebsite(boolean newHasFoundWebsite) {
        // dummy
    }

    public Website selectedWebsite() {
        return selectedWebsite;
    }

    public Section selectedSection() {
        return selectedSection;
    }
    public void setSelectedSection(Section newSelectedSection) {
        selectedSection = newSelectedSection;
    }

    public boolean isMissingSelectedSection() {
        return isMissingSelectedSection;
    }
    public void setIsMissingSelectedSection(boolean newIsMissingSelectedSection) {
        isMissingSelectedSection = newIsMissingSelectedSection;
    }

}
