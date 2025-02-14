// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;

import com.gvcsitemaker.core.SectionVersion;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.interfaces.WOSMConfirmable;
import com.gvcsitemaker.core.interfaces.WebsiteContainer;
import com.gvcsitemaker.core.pagecomponent.TextImageLayout;
import com.gvcsitemaker.core.support.SMActionURLFactory;
import com.gvcsitemaker.core.utility.RequestUtils;
import com.gvcsitemaker.core.utility.URLUtils;
import com.gvcsitemaker.pages.ConfirmAction;
import com.gvcsitemaker.pages.FileUpload;
import com.gvcsitemaker.pages.VersionMgtPage;
import com.gvcsitemaker.pages.VersionPropertiesPage;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import net.global_village.woextensions.HTMLFormatting;


/**
 * This editor used by EditSection to edit a Text / Image LayoutComponent.
 */
public class TextImageEditor extends BaseEditor implements WOSMConfirmable, WebsiteContainer
{
    public String aLayout;		// Used in a WORepetition for to display the alternative layouts for a Text / Image section.
	public NSArray layouts;		// Alternative layouts for a Text / Image section.

    public String anEditorMode;		// Used in a WORepetition for to display the alternative modes for a Text / Image section.
	public NSArray editorModes;		// Alternative modes for a Text / Image section.

	public String currentEditorMode;
	public WOComponent parentComponent;

    protected  boolean isBrowserRTECompliant;
    protected boolean shouldCreateNewVersion;


    /**
     * Designated constructor
     */
    public TextImageEditor(WOContext aContext)
    {
        super(aContext);

        // Create the list of alternative layouts for a Text / Image section.
        layouts = new NSMutableArray( new String[] {
            TextImageLayout.TextAfterImageLayout,
            TextImageLayout.TextBeforeImageLayout,
            TextImageLayout.TextOverImageLayout,
            TextImageLayout.TextUnderImageLayout
        } );

        // Create the list of alternative editor modes for a Text / Image section.
        editorModes = new NSMutableArray( new String[] {
            TextImageLayout.RichTextMode,
            TextImageLayout.PlainTextMode
        } );

        parentComponent = aContext.page();
        isBrowserRTECompliant = false;

        shouldCreateNewVersion = false;
    }



    /**
     * Returns the URL for the images for the layout
     *
     * @return the URL for the images for the layout
     */
    public String imageURLForLayout()
    {
        String imageURLForLayout = "/GVCSiteMaker/Application/CTImages/";

        if (aLayout.equals(TextImageLayout.TextAfterImageLayout))
        {
            imageURLForLayout += "TextImageAlignLeft.gif";
        }
        else         if (aLayout.equals(TextImageLayout.TextBeforeImageLayout))
        {
            imageURLForLayout += "TextImageAlignRight.gif";
        }
        else         if (aLayout.equals(TextImageLayout.TextOverImageLayout))
        {
            imageURLForLayout += "TextImageAlignBottom.gif";
        }
        else         if (aLayout.equals(TextImageLayout.TextUnderImageLayout))
        {
            imageURLForLayout += "TextImageAlignMiddle.gif";
        }

        return imageURLForLayout;
    }



    /**
     * Validates the contents of the Section's component(s) and saves the changes to this Section if they are valid.  The page containing this editor is returned if the save takes place, otherwise the ErrorPage is returned with a validation error message.  If the save takes place and shouldPreview is <code>true</code> a preview of the Section is displayed in another browser window.
     *
     * @return the page containing the editor if the save succeeded, or the ErrorPage if it did not.
     * @param shouldPreview - <code>true</code> a preview of the Section should be displayed in another browser window.
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        WOComponent resultPage;

        // Validation.  This would be better done in the LayoutComponent / one of the Primitive Components.
        if ( ! layout().imageComponent().isUrlValid())
        {
            resultPage = invalidHrefError();
        }
        else
        {
            layout().layoutComponents();
            website().setDateLastModified(new NSTimestamp());

            if (version().globalID().isTemporary() ||
                (isVersioningEnabled() && (shouldCreateNewVersion() || (! canBeAltered()) || section().isAutoVersioning().isTrue())) )
            {
                // If we are not editing a new EO, copy the displayed version as a new version
                if ( ! version().globalID().isTemporary())
                {
                    // Grab what we need to create the version
                    NSDictionary layoutDictionaryOfChanges = layout().changesFromSnapshot(editingContext().committedSnapshotForObject(layout()));
                    NSDictionary imageDictionaryOfChanges = layout().imageComponent().changesFromSnapshot(editingContext().committedSnapshotForObject(layout().imageComponent()));
                    NSDictionary textDictionaryOfChanges = layout().textComponent().changesFromSnapshot(editingContext().committedSnapshotForObject(layout().textComponent()));

                    boolean hasChanges = (! (layoutDictionaryOfChanges.isEmpty() && imageDictionaryOfChanges.isEmpty() && textDictionaryOfChanges.isEmpty()));

                    if (section().isAutoVersioning().isFalse() ||
                        (section().isAutoVersioning().isTrue() && hasChanges))
                    {
                        // Discard changes to version being displayed
                        editingContext().revert();

                        // Create the new version
                        SectionVersion newVersion = section().newVersion(version());
                        TextImageLayout newLayout = (TextImageLayout) newVersion.component();
                        NSDictionary originalcomponentOrder = newLayout.componentOrderDictionary(); //backup original component order so it won't be replaced

                        newLayout.updateFromSnapshot(layoutDictionaryOfChanges);
                        newLayout.setComponentOrderDictionary(originalcomponentOrder);
                        newLayout.imageComponent().updateFromSnapshot(imageDictionaryOfChanges);
                        newLayout.textComponent().updateFromSnapshot(textDictionaryOfChanges);
                        setVersion(newVersion);

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
                    versionPage.setVersion(version());
                    versionPage.setSection(section());
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

        return resultPage;

        /** ensure [result_not_null] Result != null; **/
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
     * Saves any pending changes, sets up FileUpload page to carry on the inPlaceEditingMode setting.
     * This is intended to use with the Rich text editor.  It may cause problems with validation if it
     * is used for the link in plain text editing mode.
     *
     * @return FileUplaod page
     */
    public WOComponent uploadFile()
    {
        super._doUpdateWithPreview(false);
        FileUpload fileUpload = (FileUpload) pageWithName("FileUpload");

        // We need to find a better way to handle carrying this setting around
        fileUpload.setInPlaceEditingMode(inPlaceEditingMode());

        return fileUpload;
    }



    /**
     * Returns the LayoutComponent for this Text / Image section.
     *
     * @return the LayoutComponent for this Text / Image section.
     */
    public com.gvcsitemaker.core.pagecomponent.TextImageLayout layout()
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
     * Returns an error page for an invalid URL on the image component.
     *
     * @return an error page for an invalid URL on the image component.
     */
    protected WOComponent invalidHrefError()
    {
        return error(URLUtils.invalidURLErrorMessage(layout().imageComponent().url()) );

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Handle page changes and confirmation of data changes when switching editorMode.
     *
     * @return this page or ConfirmAction page if data will be modified due to the switch
     */
    public WOComponent switchMode()
    {
    		WOComponent nextPage = context().page();

    		//handle if old TextImageLayout and editorMode() is still null
    		if ((currentEditorMode() != null) && currentEditorMode().equals(TextImageLayout.RichTextMode)
    				&& ((layout().editorMode() != null) && (! layout().editorMode().equals(currentEditorMode())) || (layout().editorMode() == null)))
    		{
    			if ((layout().imageComponent().hasImageToShow() || (layout().textComponent().shouldConvertCR())))
    			{
        	        ConfirmAction confirmation = (ConfirmAction)pageWithName("ConfirmAction");
        	        confirmation.setCallingComponent(this);
        	        confirmation.setActionName("Change");

        	        if ((layout().imageComponent().hasImageToShow() && ( ! (layout().textComponent().shouldConvertCR()))))
        	        {
            	        confirmation.setCustomMessage("The specified Image will be converted to an HTML tag in the text area. This is not reversible in any other way, once done.");
        	        }
        	        else if (( ! layout().imageComponent().hasImageToShow()) && (layout().textComponent().shouldConvertCR()))
        	        {
        	        		confirmation.setCustomMessage("The carriage returns will be converted to HTML line breaks in the text area. This is not reversible in any other way, once done.");
        	        }
        	        else
        	        {
        	        		confirmation.setCustomMessage("The specified Image will be converted to an HTML tag and the carriage returns will be converted to HTML line breaks in the text area. These are not reversible in any other way, once done.");
        	        }
        	        confirmation.setCustomMessage(confirmation.customMessage() + " Do you want to continue?");

        	        nextPage = confirmation;
    			}
    			else
    			{
    				layout().setEditorMode(TextImageLayout.RichTextMode);
    			}
    		}
    		else
    		{
    			layout().setEditorMode(TextImageLayout.PlainTextMode);
    		}

    		return nextPage;

		/** ensure [result_not_null] Result != null; **/
    }



    /**
     * Called after selecting an option to the ConfirmAction page.  If confirmed, performs necessary data and UI changes.  If not goes back to Plain mode.
     *
     * @return this page
     */
	public WOComponent confirmAction(boolean value)
	{
		if (value)
		{
			if (layout().textComponent().shouldConvertCR())
			{
				layout().textComponent().setText(HTMLFormatting.replaceFormattingWithHTML(layout().textComponent().text()));
			}

			if (layout().imageComponent().hasImageToShow())
			{
				layout().textComponent().setText(layout().imageComponent().displayHTML()+ "<br/>" + layout().textComponent().text() );
			}

			layout().textComponent().setShouldConvertCR(false);
			layout().imageComponent().setRelatedFile(null);
			layout().imageComponent().setUrl(null);
			layout().imageComponent().setHSize(null);

			layout().setEditorMode(TextImageLayout.RichTextMode);

			editingContext().saveChanges();
		}
		else
		{
			setCurrentEditorMode(TextImageLayout.PlainTextMode);
		}

		return parentComponent;

		/** ensure [result_not_null] Result != null; **/
	}




    /**
     * Returns the current editorMode.
     *
     * @return the current editorMode
     */
	public String currentEditorMode()
	{
		if (currentEditorMode == null)
		{
			currentEditorMode = layout().editorMode();
		}

		return currentEditorMode;

		/** ensure [result_not_null] Result != null; **/
	}



    /**
     * Sets current editorMode.
     *
     * @param newMode newMode to set
     */
	public void setCurrentEditorMode(String newMode)
	{
		currentEditorMode = newMode;
	}



    /**
     * Returns true if RTE is disabled
     *
     * @return true if anEditorMode is disabled
     */
	public boolean isDisabled()
	{
		return isDisabled(TextImageLayout.RichTextMode);
	}



    /**
     * Returns true if mode is in RichTextMode and not browser compliant.
     *
     * @return true if mode is in RichTextMode and not browser compliant.
     */
	public boolean isDisabled(String mode)
	{
		return  (mode.equals(TextImageLayout.RichTextMode) && ( ! isBrowserRTECompliant()));
	}



    /**
     * Returns true if this editor should display the RichText editor.  It should display the RichText editor if it is in RichTextMode and it is not disabled.
     *
     * @return ttrue if this editor should display the RichText editor
     */
	public boolean shouldDisplayInRichTextMode()
	{
		return (layout().isInRichTextMode() && (! isDisabled(layout().editorMode())));
	}



    /**
     * Overridden to  check browser compliance once
     */
    public void awake()
    {
        super.awake();
        isBrowserRTECompliant = (((SMApplication)application()).properties().booleanPropertyForKey("CheckBrowserCompliance") &&
                (RequestUtils.isBrowserEditorCompliant(context().request(), ((SMApplication)application()).properties().arrayPropertyForKey("CompliantBrowsers"))));
    }



    /**
     * Returns <code>true</code> if the user's browser can display the RTE
     *
     * @return <code>true</code> if the user's browser can display the RTE
     */
    public  boolean isBrowserRTECompliant()
    {
        return isBrowserRTECompliant;
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
        return (isVersioningEnabled() && (! version().isLocked().booleanValue()) && version().canCreateNewVersion(localCurrentUser()));
    }



    /**
     * Returns <code>true</code> if user is a contributor.
     *
     * @return <code>true</code> if user is a contributor.
     */
    public boolean isContributorEditingCurrentVersion()
    {
        return isVersioningEnabled() && version().isCurrent() &&
               version.section().isContributor(localCurrentUser()) && ! userHasConfigureAccess();
    }



    /**
     * Returns <code>true</code> if version can  be altered.
     *
     * @return <code>true</code> if version can  be altered.
     */
    public boolean canBeAltered()
    {
        return isVersioningEnabled() &&  ! (isContributorEditingCurrentVersion() || version().isLocked().booleanValue());
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
        return version.section().website().canBeConfiguredByUser(localCurrentUser()) || version.section().isEditor(localCurrentUser());
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
    public String PreviewMailToHyperlink()
    {
        StringBuffer mailToURL = new StringBuffer("mailto:?Subject=Preview%20page%20in%20UM.SiteMaker&body=");
        mailToURL.append(URLUtils.urlEncode(SMActionURLFactory.sectionURL(website(), section(), version(), context().request())));

        return mailToURL.toString();
        /** ensure [valid_result] Result != null;
                   [valid_url] Result.startsWith("mailto:");   **/
    }



}
