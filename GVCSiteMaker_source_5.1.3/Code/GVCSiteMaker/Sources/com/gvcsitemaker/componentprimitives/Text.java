// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.componentprimitives;

import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.pagecomponent.TextImageLayout;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.pages.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/*
 * This is used to display and edit the TextComponent sub-class of
 * PageComponent.
 */
public class Text extends ComponentPrimitive
{
    // For editing mode
    protected String aFont, aSize;
    public boolean shouldCreateNewVersion;

    protected boolean isBrowserRTECompliant;

    public final String savedText = "Saved...";


    /**
     * Designated constructor.
     */
    public Text(WOContext aContext)
    {
        super(aContext);
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
     * Overridden to refresh component object in edit mode.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        // This is a hack.  In editing mode, when the TextImageEditor is used on the EditSection page, 
        // new versions can be created and this page re-used.  If this is not done, the Text ComponentPrimitive
        // will continue to display the previous version.  If this is needed in a wider scope, we need to reconsider
        // caching of componentObject (done for performance) and review the archtecture of how this works.
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
     * Returns the string to show in display mode
     * 
     * @return the string to show in display mode
     */
    public String displayText()
    {
        com.gvcsitemaker.core.pagecomponent.Text text = (com.gvcsitemaker.core.pagecomponent.Text) componentObject();

        return text.shouldConvertCR() ? HTMLFormatting.replaceFormattingWithHTML(text.text()) : text.text();

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
     * Returns the LayoutComponent for this Text / Image section.
     *
     * @return the LayoutComponent for this Text / Image section.
     */
    protected com.gvcsitemaker.core.pagecomponent.TextImageLayout layout()
    {
        TextImageLayout layout = (TextImageLayout) section().component();

        if (section().isVersionable() && section().isVersioning().booleanValue())
        {
            layout = (TextImageLayout) version().component();
        }

        return layout;

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns true if RTE is disabled
     *
     * @return true if anEditorMode is disabled
     */
    protected boolean isDisabled()
    {
        return isDisabled(TextImageLayout.RichTextMode);
    }


    /**
     * Returns true if mode is in RichTextMode and not browser compliant.
     *
     * @return true if mode is in RichTextMode and not browser compliant.
     */
    protected boolean isDisabled(String mode)
    {
        return (mode.equals(TextImageLayout.RichTextMode) && (!isBrowserRTECompliant()));
    }


    /**
     * If true displays this Text component using Rich Text editor, else displays it in regular TextArea.
     */
    public boolean shouldUseRichTextEditor()
    {
        return (layout().isInRichTextMode() && (!isDisabled(layout().editorMode())));
    }


    /**
     * The action method for the "Edit..." link.  Changes to edit mode or takes 
     * the user to the section version management page, depending on their access.
     */
    public void editPage()
    {
        /** require [can_showEditLink] showEditLink();  **/

        // Editing this version is not permitted if the user is a contributor and viewing the current version
        if (section().isContributor(currentUser()) && version().isCurrent() && !userHasConfigureAccess())
        {
            VersionMgtPage sectionVersionManagementPage = (VersionMgtPage)pageWithName("VersionMgtPage");
            sectionVersionManagementPage.setSection(section());
            sectionVersionManagementPage.setReturnUrl(SMActionURLFactory.sectionURL(website(), section(), context().request()));
            sectionVersionManagementPage.setShouldShowUneditableWarning(true);
            AjaxUtils.redirectTo(sectionVersionManagementPage);
        }
        else
        {
            currentMode = EDIT_MODE;
            AjaxUpdateContainer.updateContainerWithID("TextImageEditInPlaceAjaxUpdateContainer", context());
        }
    }



    /**
     * Cancel changes.
     */
    public void resetPage()
    {
        ((com.gvcsitemaker.core.pagecomponent.Text) componentObject()).editingContext().revert();
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
        layout().layoutComponents();
        website().setDateLastModified(new NSTimestamp());

        if (version().globalID().isTemporary()
                || (isVersioningEnabled() && (shouldCreateNewVersion || (!canBeAltered()) || section().isAutoVersioning().isTrue())))
        {
            SectionVersion newVersion = null;

            // If we are not editing a new EO, copy the displayed version as a new version
            if (!version().globalID().isTemporary())
            {
                // Grab what we need to create the version
                NSDictionary layoutDictionaryOfChanges = layout().changesFromSnapshot(editingContext().committedSnapshotForObject(layout()));
                NSDictionary imageDictionaryOfChanges = layout().imageComponent().changesFromSnapshot(
                        editingContext().committedSnapshotForObject(layout().imageComponent()));
                NSDictionary textDictionaryOfChanges = layout().textComponent().changesFromSnapshot(
                        editingContext().committedSnapshotForObject(layout().textComponent()));

                boolean hasChanges = (!(layoutDictionaryOfChanges.isEmpty() && imageDictionaryOfChanges.isEmpty() && textDictionaryOfChanges.isEmpty()));

                if (section().isAutoVersioning().isFalse() || (section().isAutoVersioning().isTrue() && hasChanges))
                {
                    // Discard changes to version being displayed
                    editingContext().revert();

                    // Create the new version
                    newVersion = section().newVersion(version());
                    TextImageLayout newLayout = (TextImageLayout) newVersion.component();
                    NSDictionary originalcomponentOrder = newLayout.componentOrderDictionary(); //backup original component order so it won't be replaced

                    newLayout.updateFromSnapshot(layoutDictionaryOfChanges);
                    newLayout.setComponentOrderDictionary(originalcomponentOrder);
                    newLayout.imageComponent().updateFromSnapshot(imageDictionaryOfChanges);
                    newLayout.textComponent().updateFromSnapshot(textDictionaryOfChanges);

                    if (section().isAutoVersioning().isTrue())
                    {
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
                ((com.gvcsitemaker.core.pagecomponent.Text) componentObject()).editingContext().saveChanges();
                currentMode = DISPLAY_MODE;
                AjaxUpdateContainer.updateContainerWithID("TextImageEditInPlaceAjaxUpdateContainer", context());
            }
        }
        else
        {
            if (section().isVersionable())
            {
                version().setModifiedDate(new NSTimestamp());
                version().setModifiedBy(localCurrentUser());
            }

            ((com.gvcsitemaker.core.pagecomponent.Text) componentObject()).editingContext().saveChanges();
            currentMode = DISPLAY_MODE;
            AjaxUpdateContainer.updateContainerWithID("TextImageEditInPlaceAjaxUpdateContainer", context());
        }
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
     * Returns true if the Edit link should be displayed.  It should be displayed if there is a user logged in and has permissions to configure this Section's Website.
     * 
     * @return true if the Edit link should be displayed
     */
    public boolean showEditLink()
    {
        return isBrowserRTECompliant() && (userHasConfigureAccess() || userCanContribute());
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



    public boolean isBrowserRTECompliant()
    {
        return isBrowserRTECompliant;
    }



    /**
     * Returns true if non-current version note should be displayed, false otherwise.
     * 
     * @return true if non-current version note should be displayed, false otherwise.
     */
    public boolean showNonCurrentVersionNote()
    {
        return (section().isVersionable() && componentObject().sectionUsedIn().isVersioning().booleanValue() && (!version().isCurrent()));
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
     * Returns true if there is CSS to be used by the RichText Editor, false otherwise
     */
    public boolean hasEditorCSS()
    {
        return !StringAdditions.isEmpty(editorCSS());
    }


    /**
     * The CSS to be used by the RichText Editor
     */
    public String editorCSS()
    {
        return (String) valueForBinding("editorCSS");
    }



    /**
     * The value of the text component.
     *
     * @return the value of the text component
     */
    public String value()
    {
        return ((com.gvcsitemaker.core.pagecomponent.Text) componentObject()).text();
    }


    /**
     * Set the value of the text component, escape HTML characters on public users
     * to avoid cross-site scripting attacks
     *
     * @param newValue the new value of the text component
     */
    public void setValue(String newValue)
    {
        if (!((SMSession) session()).isUserAuthenticated())
        {
            // a public user must have his input escape of HTML
            newValue = WOMessage.stringByEscapingHTMLString(newValue);
        }

        ((com.gvcsitemaker.core.pagecomponent.Text) componentObject()).setText(newValue);
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
        return ((com.gvcsitemaker.core.pagecomponent.Text) componentObject()).editingContext();
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
        return isVersioningEnabled() && version().isCurrent() && version().section().isContributor(localCurrentUser())
                && !userHasConfigureAccess();
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
        return SMApplication.appProperties().stringPropertyForKey("tinymce-init-inlinetext.js");
    }



}
