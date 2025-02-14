// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.componentprimitives;

import java.util.*;

import net.global_village.eofextensions.*;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.pagecomponent.MixedMediaPane;
import com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.pages.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/*
 * * Uses Text for edit and display of contents
 */
public class MixedMediaTextContentPane extends ComponentPrimitive
{
    public boolean shouldCreateNewVersion;
    protected boolean isBrowserRTECompliant;


    /**
     * Designated constructor.
     */
    public MixedMediaTextContentPane(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to refresh component object in edit mode.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        // This is a hack.  In editing mode, when the TextImageEditor is used on the EditSection page,
        // new versions can be created and this page re-used.  If this is not done, the Text ComponentPrimitive
        // will continue to display the previous version.  If this is needed in a wider scope, we need to reconsider
        // caching of componentObject (done for performance) and review the architecture of how this works.
        if (isInEditMode())
        {
            setComponentObject(null);
        }

        // if we can edit, save the page in the cache so that the AJAX works
        if (showEditLink())
        {
            ((PageScaffold) context().page()).setSaveInPageCache(true);
        }

        super.appendToResponse(response, context);
    }



    /**
     * Overridden to check browser compliance once
     */
    public void awake()
    {
        super.awake();
        isBrowserRTECompliant = (((SMApplication) application()).properties().booleanPropertyForKey("CheckBrowserCompliance") && (RequestUtils
                .isBrowserEditorCompliant(context().request(), ((SMApplication) application()).properties().arrayPropertyForKey("CompliantBrowsers"))));
    }



    /**
     * Returns the content URL based on current request
     *
     * @return the content URL based on current request
     */
    public String contentURL()
    {
        return ((com.gvcsitemaker.core.pagecomponent.MixedMediaPane) componentObject()).contentURL(context().request());
    }



    /**
     * Returns the Section this Text is a part of
     *
     * @return the Section this Text is a part of
     */
    public Section section()
    {
        return componentObject().sectionUsedIn();
    }


    /**
     * Returns the SectionVersion this Text is from
     *
     * @return the SectionVersion this Text is from
     */
    public SectionVersion version()
    {
        return componentObject().toParent().version();
    }



    /**
     * @return <code>true</code> if version() is the current version (or is null indicating no other versions)
     */
    public boolean isCurrentVersion()
    {
        return version() == null || version().isCurrent();
    }



    public boolean isBrowserRTECompliant()
    {
        return isBrowserRTECompliant;
    }


    /**
     * Returns true if mode is in RichTextMode and not browser compliant.
     *
     * @return true if mode is in RichTextMode and not browser compliant.
     */
    protected boolean isDisabled(String mode)
    {
        return (mode.equals(com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement.RichTextMode) && (!isBrowserRTECompliant()));
    }


    /**
     * Returns the LayoutComponent for this Text / Image section.
     *
     * @return the LayoutComponent for this Text / Image section.
     */
    protected com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement layout()
    {
        com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement layout =
            (com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement) section().component();

        if (section().isVersionable() && section().isVersioning().booleanValue() && version() != null)
        {
            layout = (com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement) version().component();
        }

        return layout;

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * If true displays this Text component using Rich Text editor, else displays it in regular TextArea.
     */
    public boolean shouldUseRichTextEditor()
    {
        return (layout().isInRichTextMode() && (!isDisabled(layout().editorMode())));
    }



    /**
     * Returns the URL for viewing the versions of this Section.
     *
     * @return the URL for viewing the versions of this Section.
     */
    public String viewVersionsUrl()
    {
        String returnUrl = SMActionURLFactory.sectionURL(website(), section(), context().request());
        return SMActionURLFactory.viewSectionVersionsURLWithReturnURL(section(), false, context(), returnUrl);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the current user, authenticated or not
     *
     * @return the current user, authenticated or not
     */
    public User currentUser()
    {
        return ((Session) session()).currentUser();
    }


    /**
     * Returns <code>true</code> if the currentUser() can access the configure page for this section
     *
     * @return <code>true</code> if the currentUser() can access the configure page for this section
     */
    public boolean userHasConfigureAccess()
    {
        return (section().website().canBeConfiguredByUser(currentUser()) || section().isEditor(currentUser()));
    }


    /**
     * Returns  <code>true</code> if the currentUser() is a contributor for this section
     *
     * @return <code>true</code> if the currentUser() is a contributor for this section
     */
    public boolean userCanContribute()
    {
        return (section().isVersionable() && section().isContributor(currentUser()));
    }


    /**
     * Returns true if the Edit link should be displayed.  It should be displayed if there is a user logged in and has permissions to configure this Section's Website.
     *
     * @return true if the Edit link should be displayed
     */
    public boolean showEditLink()
    {
        return isBrowserRTECompliant() && (userHasConfigureAccess() || userCanContribute());
    }



    /**
     * Returns the URL to show the EditSection page to edit this Text section
     *
     * @return the URL to show the EditSection page to edit this Text section
     */
    public void editPage()
    {
        /** require [can_showEditLink] showEditLink();  **/

        // Editing this version is not permitted if the user is a contributor and viewing the current version
        if (section().isContributor(currentUser()) && isCurrentVersion() && !userHasConfigureAccess())
        {
            //editPageUrl = SMActionURLFactory.viewSectionVersionsURL(section(), true, context());
        }
        else
        {
            currentMode = EDIT_MODE;
            //editPageUrl = SMActionURLFactory.editSectionURL(version(), context());
        }
    }



    /**
     * Returns true if non-current version note should be displayed, false otherwise.
     *
     * @return true if non-current version note should be displayed, false otherwise.
     */
    public boolean showNonCurrentVersionNote()
    {
        return (section().isVersionable() && componentObject().sectionUsedIn().isVersioning().booleanValue() && (!isCurrentVersion()));
    }



    /**
     * Cancel changes.
     */
    public void resetPage()
    {
        ((com.gvcsitemaker.core.pagecomponent.MixedMediaTextContentPane) componentObject()).editingContext().revert();
        currentMode = DISPLAY_MODE;
    }



    /**
     * Validates the contents of the Section's component(s) and saves the changes to this Section if they are valid.  The page containing this editor is returned if the save takes place, otherwise the ErrorPage is returned with a validation error message.  If the save takes place and shouldPreview is <code>true</code> a preview of the Section is displayed in another browser window.
     *
     * @return the page containing the editor if the save succeeded, or the ErrorPage if it did not.
     * @param shouldPreview - <code>true</code> a preview of the Section should be displayed in another browser window.
     */
    public void updateSettings()
    {
        website().setDateLastModified(new NSTimestamp());

        if (version().globalID().isTemporary()
                || (isVersioningEnabled() && (shouldCreateNewVersion || (!canBeAltered()) || section().isAutoVersioning().isTrue())))
        {
            SectionVersion newVersion = null;

            // If we are not editing a new EO, copy the displayed version as a new version
            if (!version().globalID().isTemporary())
            {
                // Grab what we need to create the version
                NSDictionary textPaneDictionaryOfChanges = componentObject()
                        .changesFromSnapshot(editingContext().committedSnapshotForObject(componentObject()));

                boolean hasChanges = (!(textPaneDictionaryOfChanges.isEmpty()));

                if (section().isAutoVersioning().isFalse() || (section().isAutoVersioning().isTrue() && hasChanges))
                {
                    // Discard changes to version being displayed
                    editingContext().revert();

                    // Create the new version
                    newVersion = SectionVersion.newVersion(version());
                    MixedMediaPaneArrangement arrangement = (MixedMediaPaneArrangement) version().component();

                    MixedMediaPaneArrangement newArrangement = (MixedMediaPaneArrangement) EOCopying.Utility.shallowCopyWithoutRelationships(arrangement);
                    newArrangement.setToChildren(new NSMutableArray()); // these are populated with unknown panes by default, so clear the way for the copies
                    newArrangement.clearCachedValues();

                    newVersion.addObjectToBothSidesOfRelationshipWithKey(newArrangement, "component");

                    Enumeration childEnumerator = arrangement.orderedPanes().objectEnumerator();
                    com.gvcsitemaker.core.pagecomponent.MixedMediaTextContentPane newTextPane = null;
                    while (childEnumerator.hasMoreElements())
                    {
                        MixedMediaPane pane = (MixedMediaPane) childEnumerator.nextElement();
                        MixedMediaPane paneCopy = (MixedMediaPane) EOCopying.Utility.shallowCopy(pane);
                        editingContext().insertObject(paneCopy);
                        if (newTextPane == null && paneCopy instanceof com.gvcsitemaker.core.pagecomponent.MixedMediaTextContentPane)
                        {
                            newTextPane = (com.gvcsitemaker.core.pagecomponent.MixedMediaTextContentPane) paneCopy;
                        }
                        newVersion.component().addChild(paneCopy);
                    }

                    newTextPane.updateFromSnapshot(textPaneDictionaryOfChanges);

                    if (section().isAutoVersioning().isTrue())
                    {
                        //((MixedMediaPaneArrangement)section().component()).replaceChild(pane(), newTextPane, false);
                        section().makeCurrentVersion(newVersion);
                        newVersion.setDetails(SectionVersion.AutoCreatedDetailsString);
                    }
                }
            }

            if (section().isAutoVersioning().isFalse())
            {
                VersionPropertiesPage versionPage = (VersionPropertiesPage) pageWithName("VersionPropertiesPage");
                versionPage.setReturnUrl(SMActionURLFactory.sectionURL(website(), section(), context().request()));
                versionPage.setSection(section());
                versionPage.setComponent(componentObject());
                if (newVersion != null)
                {
                    versionPage.setVersion(newVersion);
                }
                else
                {
                    versionPage.setVersion(version());
                }

                // This is just so that the UI the user returns to does not have this set
                shouldCreateNewVersion = false;

                AjaxUtils.redirectTo(versionPage);
            }
            else
            {
                componentObject().editingContext().saveChanges();
                currentMode = DISPLAY_MODE;
                AjaxUpdateContainer.updateContainerWithID(updateContainerID(), context());
            }
        }
        else
        {
            if (section().isVersionable())
            {
                version().setModifiedDate(new NSTimestamp());
                version().setModifiedBy(localCurrentUser());
            }

            componentObject().editingContext().saveChanges();
            currentMode = DISPLAY_MODE;
            AjaxUpdateContainer.updateContainerWithID(updateContainerID(), context());
        }
    }



    public String styleString()
    {
        if (isInEditMode())
        {
            return "";
        }
        else
        {
            return "style=\"display: none;\"";
        }
    }



    /**
     * Returns true if the Edit different version link should be displayed.  This link will only be shown if the section is versionable; being versioned; and there are alternate versions of this section.
     *
     * @return true if the Edit different version link  should be displayed
     */
    public boolean showEditADifferentVersionLink()
    {
        return userHasConfigureAccess() && section().isVersionable() && section().isVersioning().booleanValue() && section().hasMultipleVersions()
                && showEditLink();
    }



    /**
     * The pane number is needed to form unique HTML IDs for each pane.  This value comes from a binding
     * in MixedMediaPaneArrangement.
     *
     * @see MixedMediaPaneArrangement
     *
     * @return the number of this pane, from 1 to 4
     */
    public Integer paneNumber()
    {
        return (Integer) valueForBinding("pane");
    }



    /**
     * This is used to avoid lots of WOGNL evaluation in the WOD file.
     *
     * @return HTML ID for AjaxUpdatContainer
     */
    public String updateContainerID()
    {
        return "MixedMediaEditInPlaceAjaxUpdateContainer_" + paneNumber();
    }



    /**
     * This is used to avoid lots of WOGNL evaluation in the WOD file.
     *
     * @return HTML ID for ChangesSaved div
     */
    public String changesSavedID()
    {
        return "ChangesSaved_" + paneNumber();
    }



    /**
     * Returns the main config tab set with the file management tab opened.
     *
     * @return the main config tab set
     */
    public WOComponent configSiteFileMgmt()
    {
        // don't do versioning when the user clicks the file management button
        componentObject().editingContext().saveChanges();

        FileUpload fileUploadPage = (FileUpload) pageWithName("FileUpload");
        return fileUploadPage;
    }



    /**
     * Returns the session cast to an SMSession.
     *
     * @return the session cast to an SMSession
     */
    public SMSession smSession()
    {
        return (SMSession) session();
    }


    /**
     * Returns the EOEditingContext of the objects being edited.
     *
     * @return the EOEditingContext of the objects being edited.
     */
    public EOEditingContext editingContext()
    {
        return componentObject().editingContext();
    }


    /**
     * Returns the current user in our editingContext().
     *
     * @return the current user in our editingContext().
     */
    public User localCurrentUser()
    {
        User currentUser = null;

        if (smSession().currentUser() != null)
        {
            return (User) EOUtilities.localInstanceOfObject(editingContext(), smSession().currentUser());
        }

        return currentUser;
    }


    /**
     * Returns <code>true</code> if versioning is enabled for this section and sections is versionable.
     *
     * @return  <code>true</code> if versioning is enabled for this section and sections is versionable.
     */
    public boolean isVersioningEnabled()
    {
        return section().isVersionable() && section().isVersioning().booleanValue();
    }



    /**
     * Returns <code>true</code> if user is a contributor.
     *
     * @return <code>true</code> if user is a contributor.
     */
    public boolean isContributorEditingCurrentVersion()
    {
        return isVersioningEnabled() && version().isCurrent() && version().section().isContributor(localCurrentUser()) && !userHasConfigureAccess();
    }


    /**
     * Returns <code>true</code> if version can  be altered.
     *
     * @return <code>true</code> if version can  be altered.
     */
    public boolean canBeAltered()
    {
        return isVersioningEnabled() && !(isContributorEditingCurrentVersion() || version().isLocked().booleanValue());
    }


    /**
     * Returns <code>true</code> if a new version can be created. A new version can be created if versioning is enabled, version displayed is not locked and current user has rights.
     *
     * @return <code>true</code> if a new version can be created. A new version can be created if versioning is enabled, version displayed is not locked and current user has rights.
     */
    public boolean canCreateANewVersion()
    {
        return (isVersioningEnabled() && (!version().isLocked().booleanValue()) && version().canCreateNewVersion(localCurrentUser()));
    }



    /**
     * Returns the string to use to init TinyMCE.
     *
     * @return the string to use to init TinyMCE
     */
    public String customJSEditorInitString()
    {
        return SMApplication.appProperties().stringPropertyForKey("tinymce-init-inlinemixedmedia.js");
    }



}
