// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.editors.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


/**
 * The page that allows users to edit properties of a specific version of a
 * Section.
 */
public class VersionPropertiesPage extends BaseEditor implements WOSMConfirmable
{
    protected WOComponent previousPage;
    protected boolean shouldMakeCurrentVersion;
    protected PageComponent component;


    /**
     * Designated constructor.
     */
    public VersionPropertiesPage(WOContext context)
    {
        super(context);

        shouldMakeCurrentVersion = false;
        previousPage = context().page();
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Returns the version being edited.
     * 
     * @return the version being edited.
     */
    public SectionVersion version()
    {
        return version;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the version being edited.
     * 
     * @return the version being edited.
     */
    public WOComponent previousPage()
    {
        if (returnUrl() == null)
        {
            previousPage.ensureAwakeInContext(context());
            return previousPage;
        }
        else
        {
            WORedirect redirect = new WORedirect(context());
            redirect.setUrl(returnUrl());
            return redirect;
        }

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Returns the previous page and reverts the changes if version is not yet saved.
     * 
     * @return the previous page and reverts the changes if version is not yet saved.
     */
    public WOComponent goBack()
    {
        WOComponent nextPage = previousPage();

        //Do not revert if not yet saved or else previous page will have an unsaved version without an ec
        if (!version().globalID().isTemporary())
        {
            editingContext().revert();
        }

        return nextPage;

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Overriden to set version modification fields and create new versions if required.
     * 
     * @return the EditSection or previous page
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        if (shouldMakeCurrentVersion())
        {
            /*if (section().component() instanceof MixedMediaPaneArrangement)
            {
                int order = version().componentOrder().intValue();

                MixedMediaPaneArrangement originalArrangement = (MixedMediaPaneArrangement)EOUtilities.localInstanceOfObject(editingContext(), version().section().component());
                MixedMediaTextContentPane originalComponent = (MixedMediaTextContentPane)originalArrangement.orderedPanes().objectAtIndex(order);

                MixedMediaPaneArrangement replacementArrangement = (MixedMediaPaneArrangement)EOUtilities.localInstanceOfObject(editingContext(), version().component());
                MixedMediaPane componentToReplace = (MixedMediaPane)replacementArrangement.orderedPanes().objectAtIndex(order);

                replacementArrangement.replaceChild(componentToReplace, originalComponent, true);

                if (previousPage instanceof MixedMediaPaneEditor)
                {
                    ((MixedMediaPaneEditor)previousPage).setPane((MixedMediaTextContentPane)replacementArrangement.orderedPanes().objectAtIndex(order));
                }
            }
            else */
            {
                section().makeCurrentVersion(version());
            }
        }

        version().setModifiedDate(new NSTimestamp());
        version().setModifiedBy(localCurrentUser());

        super._doUpdateWithPreview(shouldPreview);

        // Use shouldShowPreview instead of shouldPreview as this page does not have a Save / Save&View button pair
        if (shouldShowPreview && inPlaceEditingMode())
        {
            // Return to viewing section that was edited
            WORedirect redirectToDestination = new WORedirect(context());
            redirectToDestination.setUrl(returnUrl() + SectionDisplay.configFlagForSession(session()));
            return redirectToDestination;
        }
        else if (isNewVersion())
        {
            if (section().component() instanceof MixedMediaPaneArrangement)
            {
                MixedMediaPaneEditor nextPage = (MixedMediaPaneEditor) pageWithName("MixedMediaPaneEditor");
                nextPage.setSection(section());
                nextPage.setVersion(version());

                int order = version().componentOrder().intValue();
                MixedMediaPaneArrangement arrangement = (MixedMediaPaneArrangement)section().component();
                MixedMediaTextContentPane textPane = (MixedMediaTextContentPane)arrangement.orderedPanes().objectAtIndex(order);
                nextPage.setPane(textPane);

                if (inPlaceEditingMode())
                {
                    nextPage.setReturnUrl(returnUrl());
                }
                return nextPage;
            }
            else
            {
                // We can only get here from the EditSection page.  Go back to the same instance
                // so that any requested preview happens
                EditSection editSectionPage = (EditSection) previousPage();

                editSectionPage.setSection(section());
                editSectionPage.setVersion(version());

                if (inPlaceEditingMode())
                {
                    editSectionPage.setReturnUrl(returnUrl());
                }
                return editSectionPage;
            }
        }

        return previousPage();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns true if current user has rights to make this version current, false otherwise
     * 
     * @return true if current user has rights to make this version version current, false otherwise
     */
    public boolean canMakeVersionCurrent()
    {
        return ((!version().isCurrent()) && version().canUserMakeCurrent(((Session) session()).currentUser()));
    }



    /**
     * Returns true if current user has rights to change this version's lock status, false otherwise
     * 
     * @return true if current user has rights to change this version's lock status, false otherwise
     */
    public boolean canChangeLockStatus()
    {
        return version().canChangeLockStatus(((Session) session()).currentUser());
    }



    /**
     * Returns true if this version is a new version, not yet saved.
     * 
     * @return true if this version is a new version, not yet saved.
     */
    public boolean isNewVersion()
    {
        return version().globalID().isTemporary();
    }



    /**
     * Returns true if page is in add mode.
     * 
     * @return true if page is in add mode.
     */
    public boolean addMode()
    {
        return editingContext().globalIDForObject(version()).isTemporary();
    }



    /**
     * Invokes ConfirmAction page before deleting this version.
     * 
     * @return ConfirmAction
     */
    public WOComponent delete()
    {
        ConfirmAction confirmation = (ConfirmAction) pageWithName("ConfirmAction");
        confirmation.setCallingComponent(this);
        confirmation.setActionName("Delete");
        confirmation.setCustomMessage("Are you sure you want to delete this version?");

        return confirmation;

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Performs actual deletion of version based on value.
     * 
     * @return ConfigPage or previous page
     */
    public WOComponent confirmAction(boolean value)
    {
        WOComponent nextPage = this;

        if (value)
        {
            section().removeObjectFromBothSidesOfRelationshipWithKey(version(), "versions");
            editingContext().deleteObject(version());
            updateSettings();

            if (section().hasMultipleVersions())
            {
                if (previousPage() instanceof VersionMgtPage)
                {
                    ((VersionMgtPage) previousPage()).discardSortedVersionList();
                }
                nextPage = previousPage(); //VersionMgtPage
            }
            else if (inPlaceEditingMode())
            {
                WORedirect redirectToDestination = new WORedirect(context());
                redirectToDestination.setUrl(urlForSection() + SectionDisplay.configFlagForSession(session()));
                nextPage = redirectToDestination;
            }
            else
            {
                nextPage = pageWithName("ConfigTabSet");
            }
        }

        return nextPage;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns true if page is inPlaceEditingMode, false otherwise. It is inPlaceEditingMode if its returnURL is not null.
     *
     * @return true if page is inPlaceEditingMode, false otherwise
     */
    public boolean inPlaceEditingMode()
    {
        return returnUrl() != null;
    }



    /**
     * Returns true if current user can delete this version.
     *
     * @return true if current user can delete this version.
     */
    public boolean canDelete()
    {
        return ((!version().isCurrent()) && version().canDelete(localCurrentUser()));
    }



    /**
     * Returns true if this version is locked, false otherwise
     *
     * @return true if this version is locked, false otherwise
     */
    public boolean isLocked()
    {
        return version().isLocked().booleanValue();
    }



    /**
     * Sets the isLocked attribute of version() with the passed value
     *
     * @param value new value for isLocked
     */
    public void setIsLocked(boolean value)
    {
        version().setIsLocked(new net.global_village.foundation.GVCBoolean(value));
    }



    /**
     * Returns URL of to show either a section of a site, or a section of a site embedded within another site.  See SMActionURLFactory.sectionURL(Website, Section, WORequest) for more details.
     *
     * @return URL of to show either a section of a site, or a section of a site embedded within another site.
     */
    public String urlForSection()
    {
        /** require
        [website_not_null] website() != null;
        [a_section_not_null] section() != null;
        [context_request_not_null] context().request() != null;  **/

        return SMActionURLFactory.sectionURL(website(), section(), context().request());

        /** ensure [result_not_null] Result != null; **/
    }



    /*** Generic getters/setters ****/
    public boolean shouldMakeCurrentVersion()
    {
        return shouldMakeCurrentVersion;
    }


    public void setShouldMakeCurrentVersion(boolean newValue)
    {
        shouldMakeCurrentVersion = newValue;
    }

    public PageComponent component() {
        return component;
    }
    public void setComponent(PageComponent aComponent) {
        component = aComponent;
    }    


}
