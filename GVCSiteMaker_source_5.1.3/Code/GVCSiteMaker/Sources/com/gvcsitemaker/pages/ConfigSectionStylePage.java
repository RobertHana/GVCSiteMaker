// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.Application;
import com.gvcsitemaker.core.OrgUnit;
import com.gvcsitemaker.core.SectionStyle;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.support.SectionDisplay;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableDictionary;


/**
 * Suports creation and editing of a single SectionStyle by both Template Administrators and System Administrators.
 */
public class ConfigSectionStylePage extends com.gvcsitemaker.core.components.SMAuthComponent 
{
    public SectionStyle style;				    // The SectionStyle being configured
    protected EOEditingContext editingContext;	// Peer editing context used for editing
    protected NSMutableDictionary errors;		// Holds errors from (non-EOF) validation failures
    protected NSArray rootUnitsForStyle;		// Highest unit that can be selected for this style (user dependant)
    protected User styleAdmin;					// Shortcut to logged in user

    public String templateFilePath;			// Path of uploaded template
    public NSData templateData;				// Uploaded template data

    /** @TypeInfo Website */
    protected NSArray sitesUsingStyle;			// List of Websites using this SectionStyle
    public Website aWebsite;					// WORepetition item for sitesUsingStyle

    public String previewSiteID;				// siteID of Website to preview this SectionStyle on
    protected String previewURL;				// URL for Website to preview this SectionStyle on



    /**
     * Designated constructor
     */
    public ConfigSectionStylePage(WOContext context)
    {
        super(context);

        // Everything is done in a peer EC the way it should be done on all pages!
        editingContext = ((SMSession)session()).newEditingContext();

        // Get the logged in user as the style admin as a convenience.
        styleAdmin = (User)EOUtilities.localInstanceOfObject(editingContext, ((SMSession)session()).currentUser());

        // Get the user dependant list of Org Units that this user can associate a SectionStyle with.
        if (styleAdmin.isSystemAdmin())
        {
            rootUnitsForStyle = new NSArray(OrgUnit.rootUnit(editingContext));
        }
        else
        {
            rootUnitsForStyle = new NSArray(styleAdmin.orgUnitToAdminStylesFor());
        }

        errors = new NSMutableDictionary();
    }



    /**
     * Call this to indicate that a new SectionStyle should be created.
     */
    public void createNewStyle()
    {
        setStyle(new SectionStyle());
        editingContext().insertObject(style());
        style().setCreatedBy(styleAdmin());

        /** ensure
        [style_not_null] style() != null;
        [style_created_by_style_admin] style().createdBy().equals(styleAdmin()); **/
    }


  
    /**
     * Call this to indicate that an existing SectionStyle should be edited.
     *
     * @param theStyle SectionStyle to edit
     */
    public void editStyle(SectionStyle theStyle)
    {
        /** require [the_style_not_null] theStyle != null; **/
        
        setStyle((SectionStyle) EOUtilities.localInstanceOfObject(editingContext(), theStyle));
        style().setModifiedBy(styleAdmin());

        /** ensure
        [style_not_null] style() != null;
        [style_modified_by_equals_style_admin] style().modifiedBy().equals(styleAdmin()); **/
    }

    

    /**
     * Handles template download and resets page state.
     */
     public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
         super.appendToResponse(aResponse, aContext);

         // Reset these so they do not affect the next R-R loop
         setErrors(new NSMutableDictionary());
         templateData = null;
         setPreviewURL("");
    }

    

    /**
     * Cancels all pending changes and returns new SectionStyleManagementPage instance.  This is also used for the Done button to maintain consistency with the rest of the SiteMaker UI.
     *
     * @return new SectionStyleManagementPage instance
     */
    public WOComponent cancelChanges()
    {
        // editingContext().revert() is not called for new styles as there seems to be little point on a peer ec.  Also we would have to re-create the new object any way so little would be achieved.
        if ( ! isNewStyle())
        {
            editingContext().revert(); 
        }

        return pageWithName("SectionStyleManagementPage");
    }



    /**
     * Attempts to save changes and then redisplays this page.  
     *
     * @return this page
     */
    public WOComponent saveChanges()
    {
        // Only update style().template() if something was actually uploaded.
        if ( ! ((templateData == null) || templateData.isEqualToData(NSData.EmptyData)) )
        {
            style().setTemplate(new String(templateData.bytes()));
        }

        // Create a default for the resource path so the person creating the template does not have to think about it.
        if (isNewStyle() && style().styleID() != null)
        {
            String defaultResourcePath = ((Application)application()).properties().stringPropertyForKey("SectionStyleResourcePath");
            style().setResourceDirectory(defaultResourcePath + style().styleID());
        }

        // SiteMaker non-EOF validation!
        setErrors(style().validate());
        
        if (errors().count() == 0)
        {
            editingContext.saveChanges();
            style().setModifiedBy(styleAdmin());  // As the page is re-displayed set this to record edits to newly created styles.
        }

        return context().page();
    }



    /**
     * Makes this SectionStyle the default style for new Websites after first saving it.  If there are validation errors the default status is not changed.
     *
     * @return this page
     */
    public WOComponent makeDefaultStyle()
    {
        /** require
        [style_not_null] style() != null;
        [style_not_default] ! style().isDefaultStyle(); **/
        
        saveChanges();
        
        if (errors().count() == 0)
        {
            style().makeDefaultStyle();
            editingContext.saveChanges();
        }

        return context().page();

        /** ensure [style_made_default] ((errors().count() == 0) || style().isDefaultStyle()); **/
    }



    /**
     * Returns this page after setting sitesUsingStyle() to the list of Websites using this SectionStyle.
     *
     * @return this page which results (via code in appendToResponse) in the template for this style being returned
     */
    public WOComponent showSitesUsingStyle()
    {
        /** require [style_not_null] style() != null; **/
        
        setSitesUsingStyle(style().websitesUsingStyle());

        return context().page();

        /** ensure [sites_using_style_not_null] sitesUsingStyle() != null; **/
    }


    /**
     * Finds the Website for the siteID <code>previewSiteID</code> and sets previewURL() to the URL to preview that site with style() instead of the style specified on that Website.  This results (via the PreviewJavaScript component) in this site being displayed in a separate browser window.  If the styleAdmin() can configure this Website it can be previewed even if non published.
     *
     * @return this page with previewURL() set to preview <code>previewSiteID</code> with this style provided that <code>previewSiteID</code> exists
     */
    public WOComponent previewSiteWithStyle()
    {
        /** require [style_not_null] style() != null; **/
        
        Website siteToPreview = null;

        // Treat previewSiteID == null the same as an invalid siteID
        if (previewSiteID != null)
        {
            siteToPreview = Website.websiteForSiteID(previewSiteID, editingContext());
        }


        if (siteToPreview == null)
        {
            errors.setObjectForKey("No site was found with this ID",
                                   "SiteIDNotFound");
        }
        else
        {
            // Allow the template admin to preview unpublished sites that they can configure.
            String configFlag = siteToPreview.canBeConfiguredByUser(styleAdmin()) ? SectionDisplay.configFlagForSession(session()) : "";

            // Add the special flag which overrides the style set on a Website.
            String previewStyleFlag = "&" + SectionDisplay.SECTION_STYLE_OVERRIDE_FLAG + "=" + style().styleID();
            
            setPreviewURL(siteToPreview.siteURL() + configFlag + previewStyleFlag);
        }

        return context().page();
    }



    /**
     * Returns <code>true</code> if this is a newly created style which has not yet been saved.
     *
     * @return <code>true</code> if this is a newly created style which has not yet been saved
     */
    public boolean isNewStyle()
    {
        /** require [style_not_null] style() != null; **/
        
        return editingContext().insertedObjects().containsObject(style());
    }

    

    /**
     * Returns <code>true</code> if the styleAdmin() can change the publication status of this Style.
     *
     * @return <code>true</code> if the styleAdmin() can change the publication status of this Style
     */
    public boolean canChangePublicationStatus()
    {
        /** require [style_not_null] style() != null; **/
        
        return style().canChangePublicationStatus(styleAdmin());
    }



    /**
     * Translates style().isPublished() into "Yes" or "No".
     *
     * @return "Yes" or "No"
     */
    public String isPublishedString()
    {
        /** require [style_not_null] style() != null; **/
        
        return style().isPublished() ? "Yes" : "No";
    }



    /**
     * Translates style().isDefaultStyle() into "Yes" or "No".
     *
     * @return "Yes" or "No"
     */
    public String isDefaultString()
    {
        /** require [style_not_null] style() != null; **/
        
        return style().isDefaultStyle() ? "Yes" : "No";
    }

   
    
    /* Generic setters and getters ***************************************/


    public SectionStyle style() {
        return style;
    }
    public void setStyle(SectionStyle newStyle) {
        style = newStyle;
    }

    public EOEditingContext editingContext() {
        return editingContext;
    }
    public void setEditingContext(EOEditingContext newEditingContext) {
        editingContext = newEditingContext;
    }
    
    public NSArray rootUnitsForStyle()
    {
        return rootUnitsForStyle;
    }

    public User styleAdmin()
    {
        return styleAdmin;
    }

    /** @TypeInfo Website */
    public NSArray sitesUsingStyle() {
        return sitesUsingStyle;
    }
    public void setSitesUsingStyle(NSArray newSitesUsingStyle) {
        sitesUsingStyle = newSitesUsingStyle;
    }

    public NSMutableDictionary errors() {
        return errors;
    }
    public void setErrors(NSMutableDictionary newErrors) {
        errors = newErrors;
    }

    public String previewURL() {
        return previewURL;
    }
    public void setPreviewURL(String newPreviewURL) {
        previewURL = newPreviewURL;
    }


    /** invariant
    [editing_context_not_null] editingContext != null;
    [style_admin_not_null] styleAdmin != null;
    [root_unit_for_style_not_null] rootUnitsForStyle != null;
    [errors_not_null] errors != null; **/

}
