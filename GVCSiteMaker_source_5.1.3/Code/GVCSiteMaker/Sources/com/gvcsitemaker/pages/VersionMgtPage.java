// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import net.global_village.foundation.*;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.editors.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * The page that allows users to manage all the versions of a Section. This
 * page can also serve as a page with 2 parts: top page displays a note that the
 * current version can not be edited and provides a link to create a new one and
 * the bottom part has all the versions of a Section. To make this this page
 * display as to 2 parts, just invoke setShouldShowUneditableWarning(true) on
 * the page.
 */
public class VersionMgtPage extends BaseEditor implements WOSMConfirmable
{
    public static final String NameSorting = "Name";
    public static final String LastModifiedDateAscendingSorting = "Last Modified Date (ascending)";
    public static final String LastModifiedDateDescendingSorting = "Last Modified Date (descending)";
    public static final String VersionNumberAscendingSorting = "Version Number (ascending)";
    public static final String VersionNumberDescendingSorting = "Version Number (descending)";

    protected NSArray versionList;
    protected NSArray sortedVersionList;
    public SectionVersion aVersion;

    protected NSMutableArray versionsToBeDeleted;

    protected NSArray sortings;
    protected String selectedSorting;
    protected boolean shouldShowUneditableWarning;

    public Object activeSectionStyle;  // these are getting set from another AJAX page for some reason
    public String componentName = "anything";


    /**
     * Designated constructor.
     */
    public VersionMgtPage(WOContext context)
    {
        super(context);

        versionsToBeDeleted = new NSMutableArray();
        versionList = new NSArray();

        sortings = new NSArray(new Object[]
        { NameSorting, LastModifiedDateAscendingSorting, LastModifiedDateDescendingSorting, VersionNumberAscendingSorting, VersionNumberDescendingSorting });
        selectedSorting = LastModifiedDateDescendingSorting;

        shouldShowUneditableWarning = false;

        /** ensure
        [versionsToBeDeleted_not_null] versionsToBeDeleted != null; 
        [versionList_not_null] versionList != null;
        [sortings_not_null] sortings != null;
        [selectedSorting_not_null] selectedSorting != null;                        
        **/
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
     * Returns the versions sorted based on selected sorting
     *
     * @return the versions sorted based on selected sorting
     */
    public NSArray sortedVersionList()
    {
        if (sortedVersionList == null)
        {
            if (selectedSorting().equals(NameSorting))
            {
                sortedVersionList = NSArrayAdditions.sortedArrayUsingComparator(versionList(), SectionVersion.NameComparator);
            }
            else if (selectedSorting().equals(LastModifiedDateAscendingSorting))
            {
                sortedVersionList = NSArrayAdditions.sortedArrayUsingComparator(versionList(), SectionVersion.LastModifiedDateAscendingComparator);
            }
            else if (selectedSorting().equals(LastModifiedDateDescendingSorting))
            {
                sortedVersionList = NSArrayAdditions.sortedArrayUsingComparator(versionList(), SectionVersion.LastModifiedDateDescendingComparator);
            }
            else if (selectedSorting().equals(VersionNumberAscendingSorting))
            {
                sortedVersionList = NSArrayAdditions.sortedArrayUsingComparator(versionList(), SectionVersion.VersionNumberAscendingComparator);
            }
            else if (selectedSorting().equals(VersionNumberDescendingSorting))
            {
                sortedVersionList = NSArrayAdditions.sortedArrayUsingComparator(versionList(), SectionVersion.VersionNumberDescendingComparator);
            }
        }

        return sortedVersionList;

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Discards the sortedVersionList by setting it to null.
     */
    public void discardSortedVersionList()
    {
        sortedVersionList = null;
    }



    /**
     * Returns the list of versions displayed in the page.
     *
     * @return the list of versions displayed in the page.
     */
    public NSArray versionList()
    {
        return versionList;
    }



    /**
     * Sets the versionList with the passed list and discards the existing sortedVersionList.
     */
    public void setVersionList(NSArray list)
    {
        versionList = list;
        discardSortedVersionList();
    }



    /**
     * Overriden to set versionList
     */
    public void setSection(Section newSection)
    {
        super.setSection(newSection);
        setVersionList(section().versions());
    }



    /**
     * Returns true if current aVersion should be deleted, false otherwise
     *
     * @return true if current aVersion should be deleted, false otherwise
     */
    public boolean shouldDelete()
    {
        return versionsToBeDeleted().containsObject(aVersion);
    }



    /**
     * Sets the versions to be deleted. 
     */
    public void setShouldDelete(boolean shouldDelete)
    {
        if (shouldDelete)
        {
            if (!versionsToBeDeleted().containsObject(aVersion))
            {
                versionsToBeDeleted().addObject(aVersion);
            }
        }
        else
        {
            versionsToBeDeleted().removeObject(aVersion);
        }
    }



    /**
     * Deletes the selected versions and updates the list.
     */
    public void deleteVersions()
    {
        java.util.Enumeration enumerator = versionsToBeDeleted().objectEnumerator();

        while (enumerator.hasMoreElements())
        {
            SectionVersion versionToDelete = (SectionVersion) enumerator.nextElement();
            editingContext().deleteObject(versionToDelete);
        }

        updateSettings();

        versionsToBeDeleted().removeAllObjects();
        setVersionList(section().versions());

        /** ensure
            [correct_deleted_count] versionsToBeDeleted().count() == 0;
            **/
    }



    /**
     * TODO: this might not be needed
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        //verify delete
        return super._doUpdateWithPreview(shouldPreview);

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns true if aVersion has details.
     * 
     * @return true if aVersion has details.
     */
    public boolean hasDetails()
    {
        return (!StringAdditions.isEmpty(aVersion.details()));
    }



    /**
     * Invokes  the ConfirmPage if there are versions to be deleted, this page otherwise
     * 
     * @return the ConfirmPage if there are versions to be deleted, this page otherwise
     */
    public WOComponent deleteSelectedVersions()
    {
        WOComponent nextPage = context().page();

        if (versionsToBeDeleted().count() > 0)
        {
            ConfirmAction confirmation = (ConfirmAction) pageWithName("ConfirmAction");
            confirmation.setCallingComponent(this);
            confirmation.setActionName("Delete");
            confirmation.setCustomMessage("Are you sure you want to delete the selected versions?");

            nextPage = confirmation;
        }

        return nextPage;

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Performs actual deletion of selected versions based on value.
     * 
     * @return this page if not inPlaceEditingMode is false, ConfigPage otherwise
     */
    public WOComponent confirmAction(boolean value)
    {
        WOComponent nextPage = this;

        if (value)
        {
            deleteVersions();
            if (!section().hasMultipleVersions())
            {
                if (inPlaceEditingMode())
                {
                    WORedirect redirectToDestination = new WORedirect(context());
                    redirectToDestination.setUrl(returnUrl() + SectionDisplay.configFlagForSession(session()));
                    nextPage = redirectToDestination;
                }
                else
                {
                    nextPage = pageWithName("ConfigTabSet");
                }
            }
        }

        return nextPage;

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Returns true if current user has rights to make a version current, false otherwise
     * 
     * @return true if current user has rights to make a version current, false otherwise
     */
    public boolean canMakeVersionCurrent()
    {
        return ((!aVersion.isCurrent()) && aVersion.canUserMakeCurrent(localCurrentUser()));
    }



    /**
     * Makes the aVersion as the current version.  Updates the displayed page accordingly.
     */
    public WOComponent makeVersionCurrent()
    {
        /*if (section().component() instanceof MixedMediaPaneArrangement)
        {
            ((MixedMediaPaneArrangement)section().component()).replaceChild(version().component(), aVersion.component(), false);
        }
        else*/
        {
            section().makeCurrentVersion(aVersion);
        }

        discardSortedVersionList();
        updateSettings();
        return context().page();

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Displays the appropriate edit page based on the user's privileges.  Public users get the insecure EditSectionPublicly page, authenticated users get the standard EditSection.
     */
    public WOComponent editVersion()
    {
        WOComponent nextPage = null;

        if (aVersion.section().isContributor(localCurrentUser()) && aVersion.isCurrent() && !userHasConfigureAccess())
        {
            VersionMgtPage versionPage = (VersionMgtPage) pageWithName("VersionMgtPage");
            versionPage.setSection(aVersion.section());

            if (inPlaceEditingMode())
            {
                versionPage.setReturnUrl(urlForVersion());
            }
            versionPage.setReturnUrl(returnUrl);
            versionPage.setShouldShowUneditableWarning(true);
            versionPage.setWebsite(section.website());

            nextPage = versionPage;

        }
        else
        {
            if (aVersion.component() instanceof MixedMediaTextContentPane)
            {
                MixedMediaPaneEditor editPage = (MixedMediaPaneEditor)pageWithName("MixedMediaPaneEditor");
                editPage.setSection(section());
                editPage.setPane((MixedMediaTextContentPane)aVersion.component());
                editPage.setVersion(aVersion);

                nextPage = editPage;
            }
            else
            {
                // Public users get the insecure EditSectionPublicly page, authenticated users get the standard EditSection
                EditSectionPublicly editPage = (EditSectionPublicly) pageWithName(localCurrentUser().isPublicUser() ? "EditSectionPublicly" : "EditSection");
                editPage.setSection(aVersion.section());
                editPage.setVersion(aVersion);
                if (inPlaceEditingMode())
                {
                    editPage.setReturnUrl(urlForVersion());
                }

                nextPage = editPage;
            }
        }

        return nextPage;

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Invokes the VersionPropertiesPage with aVersion.
     * 
     * @return VersionPropertiesPage
     */
    public WOComponent editProperties()
    {
        VersionPropertiesPage versionPage = (VersionPropertiesPage) pageWithName("VersionPropertiesPage");
        versionPage.setSection(aVersion.section());
        versionPage.setVersion(aVersion);
        if (inPlaceEditingMode())
        {
            versionPage.setReturnUrl(SMActionURLFactory.displayedURL(context().request()));
        }

        return versionPage;

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Returns the url for aVersion.
     * 
     * @return  the url for aVersion.
     */
    public String urlForVersion()
    {
        return SMActionURLFactory.sectionURL(website(), section(), aVersion, context().request());

        /** ensure [Result_not_null] Result != null; **/
    }



    public NSArray sortings()
    {
        return sortings;

        /** ensure [Result_not_null] Result != null; **/
    }



    public String selectedSorting()
    {
        return selectedSorting;
    }


    /**
     * Sets the selectedSorting with newSorting and discards the sortedVersionList.
     */
    public void setSelectedSorting(String newSorting)
    {
        selectedSorting = newSorting;
        DebugOut.println(1, "selectedSorting" + selectedSorting);
        discardSortedVersionList();
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
     * Returns true if user can delete aVersion.
     *
     * @return true if user can delete aVersion.
     */
    public boolean canDelete()
    {
        return ((!aVersion.isCurrent()) && aVersion.canDelete(localCurrentUser()));
    }



    /**
     * Returns true if user can delete versions.
     *
     * @return true if user can delete versions.
     */
    public boolean canDeleteVersions()
    {
        return (website().canBeConfiguredByUser(localCurrentUser()) || section().isEditor(localCurrentUser()));
    }



    /**
     * Returns the EditSection with a new, unsaved version of the currrent version.
     *
     * @return  the EditSection page
     */
    public WOComponent createNewVersion()
    {
        aVersion = section().component().version();
        return editNewVersion();

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Returns true if user can alter aVersion.
     *
     * @return true if user can alter aVersion.
     */
    public boolean canAlterVersion()
    {
        return aVersion.canAlterVersion(localCurrentUser()) && !aVersion.isLocked().booleanValue();
    }



    /**
     * Returns the EditSection with a new, unsaved version of aVersion.
     *
     * @return  the EditSection page
     */
    public WOComponent editNewVersion()
    {
        if (aVersion.component() instanceof MixedMediaTextContentPane)
        {
            MixedMediaPaneEditor nextPage = (MixedMediaPaneEditor)pageWithName("MixedMediaPaneEditor");
            nextPage.setSection(aVersion.section());
            SectionVersion newVersion = SectionVersion.newVersion(aVersion);
            nextPage.setVersion(newVersion);
            nextPage.setPane((MixedMediaTextContentPane)newVersion.component());

            if (inPlaceEditingMode())
            {
                nextPage.setReturnUrl(SMActionURLFactory.sectionURL(website(), section(), aVersion, context().request()));
            }

            return nextPage;
        }
        else
        {
            // Public users get the insecure EditSectionPublicly page, authenticated users get the standard EditSection
            EditSectionPublicly nextPage = (EditSectionPublicly)pageWithName(localCurrentUser().isPublicUser() ? "EditSectionPublicly" : "EditSection");
            nextPage.setSection(section());
            nextPage.setVersion(section().newVersion(aVersion));

            if (inPlaceEditingMode())
            {
                nextPage.setReturnUrl(SMActionURLFactory.sectionURL(website(), section(), aVersion, context().request()));
            }

            return nextPage;
        }

        /** ensure [Result_not_null] Result != null; **/
    }



    /**
     * Returns true if user can change aVersion's lock status.
     *
     * @return true if user can change aVersion's lock status
     */
    public boolean canChangeLockStatus()
    {
        return aVersion.canChangeLockStatus(((Session) session()).currentUser());
    }



    /**
     * Locks aVersion.
     *
     * @return current page
     */
    public WOComponent lock()
    {
        /** require [not_locked] canChangeLockStatus() && ! aVersion.isLocked().booleanValue();  **/
        aVersion.setIsLocked(net.global_village.foundation.GVCBoolean.trueBoolean());
        updateSettings();

        return context().page();
        /** ensure [locked] aVersion.isLocked().booleanValue();  **/
    }



    /**
     * Unlocks aVersion.
     *
     * @return current page
     */
    public WOComponent unlock()
    {
        /** require [not_locked] canChangeLockStatus() && aVersion.isLocked().booleanValue();  **/
        aVersion.setIsLocked(net.global_village.foundation.GVCBoolean.falseBoolean());
        updateSettings();

        return context().page();
        /** ensure [not_locked] ! aVersion.isLocked().booleanValue();  **/
    }



    /**
     * Return  <code>true</code> if the currentUser() can access the configure page for this section
     * 
     * @return <code>true</code> if the currentUser() can access the configure page for this section
     */
    public boolean userHasConfigureAccess()
    {
        return (aVersion.section().website().canBeConfiguredByUser(localCurrentUser()) || aVersion.section().isEditor(localCurrentUser()));
    }



    /**
     * Returns the URL to be used for the mail icon to send the url for the selected version.
     *
     * @return the URL to be used for the mail icon to send the url for the selected version.
     */
    public String mailVersionUrl()
    {
        return "mailto:?Subject=Preview%20page%20in%20UM.SiteMaker&Body=" + URLUtils.urlEncode(urlForVersion());

        /** ensure [Result_not_null] Result != null; **/
    }


    /*** Generic getters/setters ****/

    public NSMutableArray versionsToBeDeleted()
    {
        return versionsToBeDeleted;

        /** ensure [Result_not_null] Result != null; **/
    }


    public void setVersionsToBeDeleted(NSMutableArray newVersionsToBeDeleted)
    {
        versionsToBeDeleted = newVersionsToBeDeleted;
    }


    public boolean shouldShowUneditableWarning()
    {
        return shouldShowUneditableWarning;
    }


    public void setShouldShowUneditableWarning(boolean aValue)
    {
        shouldShowUneditableWarning = aValue;
    }
}
