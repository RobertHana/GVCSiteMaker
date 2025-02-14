// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.editors;

import java.util.*;

import net.global_village.eofextensions.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.mixedmedia.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.pages.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/*
 * This class is used to handle the editing of mixed media pane. A call to
 * <code>setPane</code> should be made before the page is displayed.</p>
 */
public class MixedMediaPaneEditor extends BaseEditor
{
    protected MixedMediaPane pane; // The MixedMediaPane being edited.
    public SiteFile aFile;
    public Section selectedSection;
    public Section aSection;
    protected MixedMediaContentConfiguration configurableContent;
    protected String currentParameter;
    public MixedMediaParameter aParameter;
    protected String sourceValidationMessage;
    public String heightValidationError;

    protected boolean shouldCreateNewVersion;


    /**
     * Designated constructor. 
     */
    public MixedMediaPaneEditor(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to clear validation messages.
     */
    public void awake()
    {
        super.awake();
        sourceValidationMessage = null;
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
     * Checks that the source URL provided can be accessed.
     */
    public WOComponent validateSource()
    {
        /** require [pane_is_MixedMediaConfigurableContentPane] pane().isMixedMediaConfigurableContentPane(); **/

        com.gvcsitemaker.core.pagecomponent.MixedMediaConfigurableContentPane thePane = (com.gvcsitemaker.core.pagecomponent.MixedMediaConfigurableContentPane) pane();
        String sourceURL = thePane.sourceURL();

        if (sourceURL == null)
        {
            setSourceValidationMessage("Select a file, section or enter the URL before validating.");
        }
        // only need to validate for external URLs
        else if (thePane.sourceType().equals(MixedMediaConfigurableContentPane.URLSourceTypeName))
        {
            if (thePane.isSourceValid(sourceURL))
            {
                setSourceValidationMessage("Source validated successfully.");
            }
            else
            {
                setSourceValidationMessage("Source cannot be validated. The URL may be incorrect or the server containing the source may be temporarily unavailable.");
            }
        }
        else
        {
            setSourceValidationMessage("Source validated successfully.");
        }

        return context().page();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns true if sourceValidationMessage is null
     * 
     * @return true if sourceValidationMessage is null
     */
    public boolean hasSourceValidationMessage()
    {
        return sourceValidationMessage() != null;
    }



    /**
     * Validates the contents of the Section's component(s) and saves the changes to this Section if they are valid.  The page containing this editor is returned if the save takes place, otherwise the ErrorPage is returned with a validation error message.  If the save takes place and shouldPreview is <code>true</code> a preview of the Section is displayed in another browser window.
     *
     * @return the page containing the editor if the save succeeded, or the ErrorPage if it did not.
     * @param shouldPreview - <code>true</code> a preview of the Section should be displayed in another browser window.
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        WOComponent resultPage = context().page();

        try
        {
            website().setDateLastModified(new NSTimestamp());

            // handle media panes
            if (pane().isMixedMediaConfigurableContentPane())
            {
                ((com.gvcsitemaker.core.pagecomponent.MixedMediaConfigurableContentPane) pane()).validateConfigurationRequirements();
                resultPage = super._doUpdateWithPreview(shouldPreview);
            }

            // handle text panes
            else if (pane().isMixedMediaTextContentPane())
            {
                if (version().globalID().isTemporary() ||
                        (isVersioningEnabled() && (shouldCreateNewVersion() || ( ! canBeAltered()) || section().isAutoVersioning().isTrue())))
                {
                    // If we are not editing a new EO, copy the displayed version as a new version
                    if ( ! version().globalID().isTemporary())
                    {
                        // Grab what we need to create the version
                        NSDictionary textPaneDictionaryOfChanges = pane().changesFromSnapshot(editingContext().committedSnapshotForObject(pane()));

                        boolean hasChanges = ( ! (textPaneDictionaryOfChanges.isEmpty()));

                        if (section().isAutoVersioning().isFalse() || (section().isAutoVersioning().isTrue() && hasChanges))
                        {
                            // Discard changes to version being displayed
                            editingContext().revert();

                            // Create the new version
                            SectionVersion newVersion = SectionVersion.newVersion(version());
                            MixedMediaPaneArrangement arrangement = (MixedMediaPaneArrangement)version().component();

                            MixedMediaPaneArrangement newArrangement = (MixedMediaPaneArrangement)EOCopying.Utility.shallowCopyWithoutRelationships(arrangement);
                            newArrangement.setToChildren(new NSMutableArray());  // these are populated with unknown panes by default, so clear the way for the copies
                            newArrangement.clearCachedValues();

                            newVersion.addObjectToBothSidesOfRelationshipWithKey(newArrangement, "component");

                            Enumeration childEnumerator = arrangement.orderedPanes().objectEnumerator();
                            MixedMediaTextContentPane newTextPane = null;
                            while (childEnumerator.hasMoreElements())
                            {
                                MixedMediaPane pane = (MixedMediaPane)childEnumerator.nextElement();
                                MixedMediaPane paneCopy = (MixedMediaPane)EOCopying.Utility.shallowCopy(pane);
                                editingContext().insertObject(paneCopy);
                                if (newTextPane == null && paneCopy instanceof MixedMediaTextContentPane)
                                {
                                    newTextPane = (MixedMediaTextContentPane)paneCopy;
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

                            setVersion(newVersion);
                            setPane(newTextPane);
                        }
                    }

                    if (section().isAutoVersioning().isFalse())
                    {
                        VersionPropertiesPage versionPage = (VersionPropertiesPage)pageWithName("VersionPropertiesPage");
                        versionPage.setVersion(version());
                        versionPage.setSection(section());
                        versionPage.setComponent(pane());
                        if (inPlaceEditingMode())
                        {
                            versionPage.setReturnUrl(SMActionURLFactory.sectionURL(website(), section(), version(), context().request()));
                        }

                        // Set this for when we return from the VersionPropertiesPage
                        if (shouldPreview)
                        {
                            versionPage.showPreview();
                            showPreview();
                        }

                        // This is just so that the UI the user returns to does not have this set
                        setShouldCreateNewVersion(false);

                        resultPage = versionPage;
                    }
                    else
                    {
                        resultPage = super._doUpdateWithPreview(shouldPreview);
                    }
                }
                else
                {
                    if (section().isVersionable())
                    {
                        version().setModifiedDate(new NSTimestamp());
                        version().setModifiedBy(localCurrentUser());
                    }

                    resultPage = super._doUpdateWithPreview(shouldPreview);
                }
            }

            // handle unknown panes
            else
            {
                resultPage = super._doUpdateWithPreview(shouldPreview);
            }
        }
        catch (NSValidation.ValidationException e)
        {
            resultPage = error(e.getMessage());
            ((ErrorPage)resultPage).setReturnPage(context().page());
        }

        return resultPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Updates the changes to the page if there are no errors and returns to the EditSection page.
     * 
     * @return EditSection if no errors, this page otherwise
     */
    public WOComponent updateAndClose()
    {
        WOComponent nextPage = updateSettings();

        if (nextPage.name().indexOf("ErrorPage") == -1)
        {
            EditSection editSectionPage = (EditSection) pageWithName("EditSection");
            editSectionPage.setSection(section());
            nextPage = editSectionPage;
        }

        return nextPage;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Action method to cancel changes in the editor.  Removes all changes from the editing context and displays the EditSection page.
     *
     * @return the EditSection page without saving the changes
     */
    public WOComponent resetPage()
    {
        editingContext().revert();

        EditSection editSectionPage = (EditSection)pageWithName("EditSection");
        editSectionPage.setSection(section());

        return editSectionPage;
        /** ensure [result_valid] Result != null; [ec_has_no_changes] !editingContext().hasChanges();  **/
    }



    /**
     * Returns true if the IFrame options should be displayed, false otherwise
     *
     * @return true if the IFrame options should be displayed, false otherwise
     */
    public boolean displayIFrameOptions()
    {
        return (pane().contentConfiguration().usesIFrame() && (!pane().contentConfiguration().requiresIFrame()));
    }



    /**
     * Overridden to clear validation messages
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        heightValidationError = null;
    }



    /**
     * Translates validation faliure expections into UI error messages.
     */
    public void validationFailedWithException(Throwable exception, Object value, String keyPath)
    {
        if (keyPath.equals("height") || keyPath.equals("pane.height"))
        {
            if (pane().displayInIFrame() || (pane().isMixedMediaConfigurableContentPane() && pane().contentConfiguration().requiresIFrame()))
            {
                heightValidationError = "A height greater than zero is required.";
            }
            else
            {
                heightValidationError = "The height must be blank, or a number larger than zero.";
            }
        }
        else
            super.validationFailedWithException(exception, value, keyPath);
    }



    /**
     * Returns <code>true</code> if the a new version of this section should be created
     *
     * @return <code>true</code> if the a new version of this section should be created
     */
    public boolean shouldCreateNewVersion()
    {
        return shouldCreateNewVersion;
    }



    public void setShouldCreateNewVersion(boolean newValue)
    {
        shouldCreateNewVersion = newValue;
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
     * Returns <code>true</code> if user is a contributor.
     *
     * @return <code>true</code> if user is a contributor.
     */
    public boolean isContributorEditingCurrentVersion()
    {
        return isVersioningEnabled() && version().isCurrent() && section().isContributor(localCurrentUser()) && !userHasConfigureAccess();
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
     * Returns <code>true</code> if non-current versions can be accessed.
     *
     * @return <code>true</code> if non-current versions can be accessed
     */
    public boolean canViewNonCurrentVersions()
    {
        return (isVersioningEnabled() && section().hasMultipleVersions() && version().canViewNonCurrentVersions(localCurrentUser()));
    }



    /**
     * Returns <code>true</code> if the currentUser() can access the configure page for this section
     *
     * @return <code>true</code> if the currentUser() can access the configure page for this section
     */
    public boolean userHasConfigureAccess()
    {
        return section().website().canBeConfiguredByUser(localCurrentUser()) || section().isEditor(localCurrentUser());
    }



    /**
     * Displays the VersionMgtPage. Reverts the changes to the editingContext before this page is displayed, in case the text displayed is a new, unsaved version.
     *
     * @return VersionMgtPage
     */
    public WOComponent viewVersions()
    {
        //Revert in case, this page displays a  new unsaved version
        editingContext().revert();

        VersionMgtPage versionsPage = (VersionMgtPage) pageWithName("VersionMgtPage");
        versionsPage.setSection(section());
        versionsPage.setVersion(version());
        if (inPlaceEditingMode())
        {
            versionsPage.setReturnUrl(returnUrl());
        }

        return versionsPage;
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
     * Return a mailto: url to send a link to view the displayed version
     *
     * @return  a mailto: url to send a link to view the displayed version
     */
    public String previewMailToHyperlink()
    {
        StringBuffer mailToURL = new StringBuffer("mailto:?Subject=Preview%20page%20in%20UM.SiteMaker&body=");
        mailToURL.append(URLUtils.urlEncode(SMActionURLFactory.sectionURL(website(), section(), version(), context().request())));

        return mailToURL.toString();
        /** ensure [valid_result] Result != null;
                   [valid_url] Result.startsWith("mailto:");   **/
    }



    /* Generic setters and getters ***************************************/
    public String sourceValidationMessage()
    {
        return sourceValidationMessage;
    }


    public void setSourceValidationMessage(String newValue)
    {
        sourceValidationMessage = newValue;
    }


    public MixedMediaPane pane()
    {
        return pane;

        /** ensure [result_not_null] Result != null; **/
    }


    public void setPane(MixedMediaPane newPane)
    {
        /** require [newPane_not_null] newPane != null; **/

        pane = newPane;
    }
}
