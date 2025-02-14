// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;

import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.SectionVersion;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.components.ErrorPage;
import com.gvcsitemaker.core.support.SMActionURLFactory;
import com.gvcsitemaker.core.support.SectionDisplay;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORedirect;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;


/**
 * This class forms the base for all "section" editors.  Editor components are used in the EditSection 
 * page to allow users to configure the contents of Section. In reality, these do not actually edit the 
 * contents of a Section object (EditSection itself does that) but rather the contents of the Section's 
 * component.  This component can be any sub-class of PageComponent including composite ones like the 
 * Layout components.
 * 
 * <p>This component also handles in place editing (editing from site viewing) mode.  In this situation, 
 * it will redirect to returnUrl when a preview is requested or editing is ended (cancelled).</p>
 */
public class BaseEditor extends com.gvcsitemaker.core.components.WebsiteContainerBase
{
    protected Section section;
    protected boolean shouldShowPreview;
    protected boolean inPlaceEditingMode;    
    protected String returnUrl;
    protected SectionVersion version;


    /**
     * Designated constructor.
     */
    public BaseEditor(WOContext aContext)
    {
        super(aContext);
        shouldShowPreview = false;
        inPlaceEditingMode = false;
    }



    /**
     * Action method to cancel changes in the editor.  Removes all changes from the
     * editing context and displays the ConfigPage if it is not from editMode,
     * otherwise redirects it to the website's URL
     *
     * @return the ConfigPage without saving the changes
     */
    public WOComponent resetPage()
    {
        editingContext().revert();

        WOComponent resetPage;
        if ( ! inPlaceEditingMode())
        {
            resetPage = pageWithName("ConfigTabSet");
        }
        else
        {
            // Return to viewing section that was edited
            WORedirect redirectToDestination = new WORedirect(context());
            redirectToDestination.setUrl(returnUrl() + SectionDisplay.configFlagForSession(session()));
            resetPage = redirectToDestination;
        }

        return resetPage;
        /** ensure [result_valid] Result != null; [ec_has_no_changes] !editingContext().hasChanges();  **/
    }



    /**
     * Uses _doUpdateWithPreview to save changes to this Section's components and returns the result of that method as the next page to display.
     *
     * @return the page to show after saving.
     */
    public WOComponent updateSettings()
    {
        return _doUpdateWithPreview(false);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Uses _doUpdateWithPreview to save changes to this Section's components and returns the result of that method as the next page to display.  A preview of the Section is displayed in another browser window.
     *
     * @return the page to show after saving.
     */
    public WOComponent updateSettingsAndPreview()
    {
        return _doUpdateWithPreview(true);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Attempts to save the changes to this Section.  Sub-classes should validate the changes before calling this method.  The page containing this editor is returned.  Sub-classes may return a different page.  If shouldPreview is <code>true</code> a preview of the Section is displayed in another browser window.
     *
     * @return the page containing the editor
     * @param shouldPreview - <code>true</code> a preview of the Section should be displayed in another browser window.
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        editingContext().saveChanges();
 
        if (shouldPreview && inPlaceEditingMode())
        {
           // Return to viewing section that was edited
            WORedirect redirectToDestination = new WORedirect(context());
            redirectToDestination.setUrl(returnUrl());
            return redirectToDestination;
        }
            
        if (shouldPreview)
        {
            showPreview();
        }

        return context().page();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the ErrorPage displaying the given message.  The ErrorPage is configured to return the page holding this editior.
     *
     * @param message - the error message to display on the error page.
     * @return the ConfigPage without saving the changes
     */
    protected WOComponent error(String message)
    {
        /** require [valid_param] message != null; **/
        ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
        errorPage.setReturnPage(parent());
        errorPage.setMessage(message);
        errorPage.setReason("Invalid submission.");

        return errorPage;
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the URL to preview the section being edited or null if showPreview() has not been called in this R-R loop.  This is used by the PreviewJavaScript component.  If the returned URL is null the section will not be previewed.
     *
     * @return the URL to preview the section being edited or null.
     */
    public String previewURL()
    {
        String previewURL = section().sectionURL(context().request());
        
        //Show non-current version if version currently being edited is not current
        if ((version() != null) && (! version().isCurrent()))
        {
            previewURL = SMActionURLFactory.sectionURL(website(), section(), version(), context().request());
        }
        
        previewURL = shouldShowPreview ? previewURL : null;
        shouldShowPreview = false;	// Reset this so the section is not previewed on future submits.

        return previewURL;
    }



    /**
     * Dummy method to satisfy binding synchronization.
     */
    public void setPreviewURL(String dummyValue)
    {
    }



    /**
     * Call this method to preview the section being edited the next time a response is generated.
     */
    public void showPreview()
    {
        shouldShowPreview = true;
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
     * Returns the Section whose component is being edited in this editor.
     *
     * @return the Section whose component is being edited in this editor.
     */
    public Section section()
    {
        return section;
    }



    /**
     * Sets the Section whose component will be edited in this editor.
     *
     * @param newSection - the Section whose component will be edited in this editor.
     */
    public void setSection( Section newSection )
    {
        section = newSection;
    }



    /**
     * Returns the EOEditingContext of the objects being edited.
     *
     * @return the EOEditingContext of the objects being edited.
     */
    public EOEditingContext editingContext()
    {
        /** require [has_section] section() != null; **/
        return section().editingContext();
        /** ensure [result_valid] Result != null; **/
    }
    
    
    public boolean inPlaceEditingMode() {
        return inPlaceEditingMode;
    }
    public void setInPlaceEditingMode( boolean newMode ) {
    		inPlaceEditingMode = newMode;
    }    
    
    public String returnUrl() {
        return returnUrl;
    }
    public void setReturnUrl(String newUrl ) {
        returnUrl = newUrl;
    }
    public SectionVersion version() {
        return version;
    }
    public void setVersion(SectionVersion aVersion) {
        version = aVersion;
    }    
}
