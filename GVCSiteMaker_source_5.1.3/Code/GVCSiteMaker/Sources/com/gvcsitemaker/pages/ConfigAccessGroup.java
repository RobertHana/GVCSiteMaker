// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.ConfigureGroup;
import com.gvcsitemaker.core.LDAPBranch;
import com.gvcsitemaker.core.LDAPGroup;
import com.gvcsitemaker.core.LocalGroup;
import com.gvcsitemaker.core.RemoteParticipantGroup;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.WebsiteGroup;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.interfaces.SMSecurePage;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSValidation;


/**
 * This page allows an Access Group to be created, renamed and controls the editor for type specific
 * access group editors.
 */
public class ConfigAccessGroup extends com.gvcsitemaker.core.components.WebsiteContainerBase
    implements SMSecurePage
{
    protected WebsiteGroup currentGroup;
    protected String validationErrorMessage;
    protected WOComponent previousPage;
    protected EOEditingContext editingContext;
    protected Website localWebsite;
    protected boolean editorHasErrors;
    

    /**
     * Designated constructor.
     */
    public ConfigAccessGroup (WOContext aContext)
    {
        super(aContext);
        
        // Get local website for local editing context
        editingContext = smSession().newEditingContext();
        localWebsite = (Website)EOUtilities.localInstanceOfObject(editingContext, website());
        
        // Remember where to go back to
        previousPage = context().page();
    }
    
    

    /**
     * Creates a new <code>LocalGroup</code> named <code>newGroupName</code>
     * 
     * @param newGroupName the name of the new LocalGroup
     */
    public void createLocalGroupNamed(String newGroupName)
    {
        /** require [valid_name] newGroupName != null;   **/
        setCurrentGroup(localWebsite.newLocalGroupNamed(newGroupName));
        /** ensure [currentGroup_set] currentGroup() != null;   **/
    }

    
    
    /**
     * Creates a new <code>LDAPGroup</code> named <code>newGroupName</code> and associates with
     * <code>ldapBranch</code>.
     * 
     * @param newGroupName the name of the new LocalGroup
     * @param ldapBranch the <code>LDAPBranch/code> this groups resides in
     */
    public void createLDAPGroup(String newGroupName, LDAPBranch ldapBranch)
    {
        /** require [valid_name] newGroupName != null;  [valid_branch] ldapBranch != null;   **/
        LDAPBranch localBranch = (LDAPBranch)EOUtilities.localInstanceOfObject(editingContext(), ldapBranch);
        setCurrentGroup(localWebsite.newLDAPGroup(newGroupName, localBranch));
        /** ensure [currentGroup_set] currentGroup() != null;   **/
    }



    /**
     * Overridden to clear validation error message.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);

        // Reset for next cycle
        setValidationErrorMessage(null);
        setEditorHasErrors(false);
    }
    
    
    
    /**
     * Action method to save changes and redisplay this page.
     *
     * @return this page
     */
    public WOComponent saveChangesToGroup()
    {
        if ( ! hasValidationErrors())
        {
            editingContext().saveChanges();
        }
            
        return context().page();
    }

    

    /**
     * Action method to save changes goe back to the previous page if the update was successful, 
     * otherwise returns this page with error messages.
     *
     * @return the previous page if the update was successful, otherwise returns this page 
     */
    public WOComponent doneModifyingGroup()
    {
        WOComponent nextPage = saveChangesToGroup();

        if ( ! hasValidationErrors())
        {
            nextPage = previousPage;
        }

        return nextPage;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Action method to cancel all changes and return to the page we were at before.
     */
    public WOComponent cancelRevision()
    {
        editingContext().revert();

        return previousPage;
    }



    /**
     * Overridden to perform page specific UI updates from validation.  This 
     * method is called if the Access Group name is invalid (missing) or is in 
     * use by another group.  validationErrorMessage() is set to an appropriate 
     * error message.
     * 
     * @see com.webobjects.appserver.WOComponent#validationFailedWithException(java.lang.Throwable, java.lang.Object, java.lang.String)
     */
    public void validationFailedWithException(Throwable failure,
                                              Object value,
                                              String keyPath)
    {
        if (keyPath.equals("currentGroup.name"))
        {
            // Nasty hack to get our validation message instead of EOF's.
            // REFACTOR Use GVCValidation for all this
            try
            {
                currentGroup().validateName(value);
            }
            catch (NSValidation.ValidationException e)
            {
                setValidationErrorMessage(e.getMessage());
            }
            
            // Force in the invalid value so that the user can see it.
            takeValueForKeyPath(value, keyPath);
        }
        else
        {
            super.validationFailedWithException(failure, value, keyPath);
        }
    }
    


    /**
     * Returns </code>true</code> if there are any validation errors.
     *
     * @return </code>true</code> if there are any validation errors
     */
    public boolean hasValidationErrors()
    {
        return (validationErrorMessage() != null) || editorHasErrors();
    }



    /**
     * Returns </code>true</code> if currentGroup() is new (unsaved).
     *
     * @return </code>true</code> if currentGroup() is new (unsaved)
     */
    public boolean isNewGroup()
    {
        return editingContext().globalIDForObject(currentGroup()).isTemporary();
    }



    /**
     * Returns </code>true</code> if the current group is not a configure group.  This is an 
     * obsolete restriction which could be removed.  Hence it is implemented at the UI layer.
     *
     * @return </code>true</code> if the current group is not a configure group
     */
    public boolean canEditGroupName()
    {
        // TODO Push this down to Group
        return ! (currentGroup() instanceof ConfigureGroup || currentGroup() instanceof RemoteParticipantGroup);
    }



    /**
     * Returns the name of the WOComponent used to edit this particular type of Group.  This is used
     * in a WOSwitchComponent.
     * 
     * @return the name of the WOComponent used to edit this particular type of Group
     */
    public String groupEditorComponent() 
    {
        // At present there are only editors for Local, LDAP, and Configure groups.
        if (! (currentGroup() instanceof LDAPGroup ||
        		currentGroup() instanceof LocalGroup ||
        		currentGroup() instanceof RemoteParticipantGroup))
        {
            throw new RuntimeException("No editor implemented for Group of type " + currentGroup().type());
        }

        String groupType = currentGroup().type();

        // The configure and local group types are so similar that we edit them'
        // with the same editor.
        if (currentGroup() instanceof ConfigureGroup)
        {
            groupType = "Local";
        }
        // Remote Participants group type name has a space
        else if (currentGroup() instanceof RemoteParticipantGroup)
        {
            groupType = "RemotePart";
        }

        return groupType + "AccessGroupEditor";
        /** [valid_result] Result != null;  **/
    }



    public String pageTitle() {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Configure Access Groups";
    }

    /* Generic setters and getters ***************************************/

    public WebsiteGroup  currentGroup() {
        return currentGroup;
    }
    public void setCurrentGroup(WebsiteGroup  newCurrentGroup) {
        if (newCurrentGroup.editingContext() != editingContext())
        {
            newCurrentGroup = (WebsiteGroup)EOUtilities.localInstanceOfObject(editingContext(), newCurrentGroup);
        }
        currentGroup = newCurrentGroup;
    }

    public String validationErrorMessage() {
        return validationErrorMessage;
    }
    public void setValidationErrorMessage(String newValidationErrorMessage) {
        validationErrorMessage = newValidationErrorMessage;
    }

    public EOEditingContext editingContext()
    {
        return editingContext;
    }

    public Website localWebsite() {
        return localWebsite;
    }

    public boolean editorHasErrors() {
        return editorHasErrors;
    }
    public void setEditorHasErrors(boolean b) {
        editorHasErrors = b;
    }


}
