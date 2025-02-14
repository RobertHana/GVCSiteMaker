// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.user;
import net.global_village.foundation.*;

import com.gvcsitemaker.admin.unit.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * This page handles the management of a single user (change ID, delete, change admin status) and also selecting a different user to manage.
 */
public class ManageUserPage extends WOComponent implements SMSecurePage
{
    protected User _user;

    /** @TypeInfo java.lang.String */
    protected NSArray errorMessages;
    public String anErrorMessage;

    public String userIDToManage;
    protected String invalidManageUserIDErrorMessage;
    protected String userID;
    protected NSArray rootUnit;
    protected NSArray adminedOrgUnits;

    /** @TypeInfo OrgUnit */
    public OrgUnit anOrgUnit;
    
    
    /**
     * Designated constructor.
     */
    public ManageUserPage(WOContext aContext)
    {
        super(aContext);
        errorMessages = NSArray.EmptyArray;
    }



    /**
     * Overridden to reset validation error messages and entered userID.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        setUserID(user().userID());
        super.appendToResponse(aResponse, aContext);
        setErrorMessages(NSArray.EmptyArray);
        setInvalidManageUserIDErrorMessage(null);
    }
    

    public void setUser( User newUser ) {
        if (newUser != _user)
        {
            _user = newUser;
        }
        
        rootUnit = new NSArray(OrgUnit.rootUnit(editingContext()));
        updateAdminedOrgUnits();
    }



    /**
     * Action method to validate and save updates to a user.  If the changes are invalid an error message is displayed.
     *
     * @return this page
     */
    public WOComponent updateProperties()
    {
        /** require [has_user] user() != null;  **/

        user().setUserID(userID());
        setErrorMessages(user().validateUser());

        if( hasErrorMessages())
        {
            editingContext().revert();
        }
        else
        {
            editingContext().saveChanges();
            updateAdminedOrgUnits();
        }

        return context().page();
    }

    

    /**
     * Action method to change which user is being managed.  If the entered userID is valid this page is redisplayed for that user.  If the User ID was invalid, redisplays this page with an error message.
     *
     * @return this page
     */
    public WOComponent manageUser()
    {
        WOComponent nextPage = context().page();
        
        User userToManage = User.userForUserID( userIDToManage, editingContext() );

        if ( userToManage == null )
        {
            setInvalidManageUserIDErrorMessage("The User ID you entered could not be found. Please try again.");
        }
        else
        {
            nextPage = pageWithName("ManageUserPage");
            ((ManageUserPage)nextPage).setUser( userToManage );
        }

        return nextPage;

    }



    /**
     * Action method to delete the user being managed.  
     *
     * @return the Main page
     */
    public WOComponent removeUser()
    {
        /** require [has_user] user() != null;  [can_remove_user]  canDeleteUser();  **/
        editingContext().deleteObject( user() );
        editingContext().saveChanges();

        return pageWithName("Main");
    }



    /**
     * Action method to manage selected unit.  
     *
     * @return the Main page
     */
    public ManageUnitPage manageUnit() 
    {
        ManageUnitPage nextPage = (ManageUnitPage)pageWithName("ManageUnitPage");
        nextPage.setUnit(anOrgUnit);
        return nextPage;
    }


    
    /**
     * Returns <code>true</code> if the logged in user can change the System Administrator state of this user.  This is the case if the logged in user is a System Administrator themself, is not the user being managed, and if the user being managed is not the last System Administrator.
     *
     * @return <code>true</code> if the logged in user can change the System Administrator state of this user
     */
    public boolean canChangeUsersSysAdminState()
    {
        User loggedInUser = ((SMSession)session()).currentUser();

        // You have to be a sys admin to change this
        boolean editorIsSysAdmin = loggedInUser.isSystemAdmin();

        // You cannot take away the admin status for the logged in user (or bad things might happen in the UI, better to not take chances!)
        boolean isNotLoggedInUser = ! isUserLoggedInUser();

        //  You cannot remove the system last admin.  This is somewhat redundant as editorIsSysAdmin && isNotLoggedInUser implies isNotLastSysAdmin. 
        boolean isNotLastSysAdmin = ! user().isLastSystemAdmin();

        return editorIsSysAdmin && isNotLoggedInUser && isNotLastSysAdmin;
    }



    /**
     * Returns <code>true</code> if the user being managed can be deleted.  This is the case if the logged in user is not the user being managed and if the user being managed is not the last System Administrator.
     *
     * @return <code>true</code> if the user being managed can be deleted
     */
    public boolean canDeleteUser()
    {
        // You cannot delete the logged in user (or bad things might happen in the UI, better to not take chances!)
        boolean isNotLoggedInUser = ! isUserLoggedInUser();

        //  You cannot delete the system last admin
        boolean isNotLastSysAdmin = ! user().isLastSystemAdmin();

        return isNotLoggedInUser && isNotLastSysAdmin;
    }

    
    
    
    public boolean isUserLoggedInUser()
    {
        EOEnterpriseObject localAuthenticatedUser = EOUtilities.localInstanceOfObject(editingContext(),
                                                                                      ((SMSession)session()).currentUser());
        return editingContext().globalIDForObject(user()).equals(editingContext().globalIDForObject(localAuthenticatedUser));
    }



    /**
     * Updates the list of units that this user can administer. 
     */
    public void updateAdminedOrgUnits()
    {
        /** require [valid_user] user() != null;  **/
        NSLog.out.appendln("updateAdminedOrgUnits for " + user().userID());
        adminedOrgUnits = NSArrayAdditions.sortedArrayUsingComparator(user().orgUnitsToAdmin(), 
                                                                      OrgUnit.HierarchicalComparator);
        /** ensure [valid_result] (adminedOrgUnits() != null) && 
                                  (adminedOrgUnits().count() == user().orgUnitsToAdmin().count());  **/
    }



    /**
     * Returns <code>true</code> if there have been any validation errors recorded.
     *
     * @return <code>true</code> if there have been any validation errors recorded
     */
    public boolean hasErrorMessages()
    {
        return errorMessages().count() > 0;
    }

    

    /* ********** Generic setters and getters ************** */
    public User user() {
        return _user;
    }


    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
    }

    public NSArray rootUnit() {
        return rootUnit;
    }

    /** @TypeInfo java.lang.String */
    public NSArray errorMessages() {
        return errorMessages;
    }
    public void setErrorMessages(Object newErrorMessages) {
        // Temporary measure until all methods returning dictionaries can be updated.
        if (newErrorMessages instanceof NSArray)
        {
            errorMessages = (NSArray)newErrorMessages;
        }
        else if (newErrorMessages instanceof NSDictionary)
        {
            errorMessages = (NSArray)((NSDictionary)newErrorMessages).allValues().objectAtIndex(0);
        }
        else
        {
            throw new RuntimeException("Called setErrorMessages with " + newErrorMessages);
        }
    }

    public String invalidManageUserIDErrorMessage() {
        return invalidManageUserIDErrorMessage;
    }
    public void setInvalidManageUserIDErrorMessage(String newInvalidManageUserIDErrorMessage) {
        invalidManageUserIDErrorMessage = newInvalidManageUserIDErrorMessage;
    }

    public String userID() {
        return userID;
    }
    public void setUserID(String newUserID) {
        userID = newUserID;
    }
    


    public NSArray adminedOrgUnits()
    {
        return adminedOrgUnits;
    }


    /** invariant [errorMessages_set]  errorMessages != null;  **/

}
