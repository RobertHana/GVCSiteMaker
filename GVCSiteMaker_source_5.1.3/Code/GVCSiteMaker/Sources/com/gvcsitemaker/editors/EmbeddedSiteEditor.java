// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;

import net.global_village.foundation.StringAdditions;


/**
 * This editor used by EditSection to edit an Embedded Site section implemented as a EmbeddedSite PageComponent.
 */
public class EmbeddedSiteEditor extends BaseEditor 
{
    public String embeddedSiteID;       // siteID of the Website embedded in this Section
    public String siteIDErrorMessage;   // Error message if the selected Website cannot be embedded in this Section
    public String sectionNameErrorMessage;  // Error message if link text is missing



    /**
     * Designated constructor
     */
    public EmbeddedSiteEditor(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Overridden to clear siteIDErrorMessage.
     */
    public void awake()
    {
        super.awake();
        siteIDErrorMessage = null;
        sectionNameErrorMessage = null;
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
     * Method bound to the "Update & Done" button. Saves the changes and goes back to the ConfigTabSet.
     *
     * @return the current page
     */
    public WOComponent updateDoneAction()
    {
        WOComponent resultPage;

        validateEmbedding();

        if( hasError() )
        {
            resultPage = context().page();
        }
        else
        {
            Website siteToEmbed = Website.websiteForSiteID( getEmbeddedSiteID(), editingContext() );
            embeddedSiteComponent().linkToWebsite(siteToEmbed);
            editingContext().saveChanges();

            resultPage = pageWithName("ConfigTabSet");
        }

        return resultPage;
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns <code>true</code> if validation failed for embedding the site in this Section and there is an error message to display to the user.
     *
     * @return <code>true</code> if validation failed for embedding the site in this Section and there is an error message to display to the user.
     */
    public boolean hasError()
    {
        return (siteIDErrorMessage != null) || (sectionNameErrorMessage != null);
    }
    
  

    /**
     * Sets the Section being edited.  Overridden so that the embedded siteID can be driven into the UI.
     *
     * @param aSection - the  Section to be edited.
     */
    public void setSection(Section newSection)
    {
        /** require [a_section_not_null] newSection != null; **/
        
        super.setSection(newSection);

        // If the section already has a site embedded in it and we have not already done this, record the siteID of the emebedded site so that it can be displayed in the UI.

        if ((section() != null) && (section().component() != null))
        {

            if ( ( ! embeddedSiteComponent().hasTargetBeenDeleted()) &&
                 (getEmbeddedSiteID() == null) )
            {
                setEmbeddedSiteID( embeddedSiteComponent().linkedWebsite().siteID() );
            }
        }

        /** ensure [section_is_set] section() == newSection; **/
    }


    

    /**
     * Checks user input and sets error messages.
     */
    public void validateEmbedding()
    {
        siteIDErrorMessage = null;
        DebugOut.println( 20, " = = = = = Requested Embedded Site ID: " + embeddedSiteID );
        
        // Ensure the Link Text (which is actually the section name) is not null or Bad Things (tm) happen!
        if (StringAdditions.isEmpty(section().name()))
        {
            sectionNameErrorMessage = "ERROR: The Link Text is required.";
        }
        
        if (StringAdditions.isEmpty(getEmbeddedSiteID()))
        {
            siteIDErrorMessage = "ERROR: The Embedded Site Name (SiteID) is required.";
        }
        else
        {
            Website siteToEmbed = Website.websiteForSiteID( getEmbeddedSiteID(), editingContext() );

            if (siteToEmbed  == null )
            {
                siteIDErrorMessage = "ERROR: Web site with id: [" + getEmbeddedSiteID() +
                    "] could not be found, please double check spelling and punctuation.";
            }
            else if ( website().siteID().equalsIgnoreCase( getEmbeddedSiteID() ) )
            {
                siteIDErrorMessage = "ERROR: You cannot embed a website within itself.";
            }
            else if ( siteToEmbed.containsSite( website() ) )
            {
                siteIDErrorMessage = "ERROR: Web site with id: [" + getEmbeddedSiteID() +
                    "] contains an embedded section that links to this site.  Recursive embedding of web sites is not permitted.";
            }
        }
    }


    
    /**
     * Returns the siteID for the Website to be embedded in this section.  This is not bound directly to the Section as the Section has an embeddedSite(), not siteID.  This is translated into a Website and recorded in the Section when the changes are saved.
     *
     * @return the siteID for the Website to be embedded in this section.
     */
    public String getEmbeddedSiteID()
    {
        return embeddedSiteID;
    }



    /**
     * Sets the siteID for the Website to be embedded in this section.  This is not bound directly to the Section as the Section has an embeddedSite(), not siteID.  This is translated into a Website and recorded in the Section when the changes are saved.
     *
     * @param newEmbeddedSiteID - the siteID for the Website to be embedded in this section.
     */    public void setEmbeddedSiteID( String newEmbeddedSiteID )
    {
        embeddedSiteID = newEmbeddedSiteID;
    }



    /**
     * Returns the EmbeddedSite PageComponent which implements the section being edited.
     *
     * @return the EmbeddedSite PageComponent which implements the section being edited.
     */
    public com.gvcsitemaker.core.pagecomponent.EmbeddedSite embeddedSiteComponent()
    {
        /** require
        [has_section] section() != null;
        [has_component] section().component() != null;
         **/

        return ((com.gvcsitemaker.core.pagecomponent.EmbeddedSite)section().component());

        /** ensure [return_valid] Result != null; **/
    }


    public EOEditingContext editingContext()
    {
        return section().editingContext();
    }
    
}
