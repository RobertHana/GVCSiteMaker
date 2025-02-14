// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;

import java.util.Enumeration;

import com.gvcsitemaker.core.LocalGroup;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.support.UserIDTokenizer;
import com.gvcsitemaker.core.utility.DebugOut;
import com.gvcsitemaker.pages.ConfigAccessGroup;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;


/** 
 * Editor used by the ConfigureAccessGroup page for LocalGroup and ConfigureGroups. 
 */
public class LocalAccessGroupEditor extends WOComponent 
{
    public final static String ADD_ACTION = "Add the users in the list below";
    public final static String REMOVE_ACTION = "Remove the users in the list below";
    public final static String REPLACE_ACTION = "Replace group members with the users in the list below";
    public final NSArray USER_ACTIONS = new NSArray(new String[] {ADD_ACTION, REMOVE_ACTION, REPLACE_ACTION});
    
    protected User aUser;
    protected String newUserIDs;
    protected NSArray sortedUsers;
    protected String selectedAction;
    protected NSMutableArray userValidationErrors;
    protected LocalGroup currentGroup;
    public String anError;
    

    /**
     * Designated constructor. 
     */
    public LocalAccessGroupEditor(WOContext context) 
    {
        super(context);
        userValidationErrors = new NSMutableArray();
    }
    


    /**
     * Overridden to perform update of users in group.
     */
    public void takeValuesFromRequest(WORequest request, WOContext context)
    {
        super.takeValuesFromRequest(request, context);
        updateUsersInCurrentGroup();  // Errors in the group name prevent any changes
    }



    /**
     * Overridden to setup state before display and reset afterward.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
         if (! parentEditor().hasValidationErrors())
        {
            setSelectedAction(ADD_ACTION);
        }
            
        sortUsers();
        
        // If there were any validation errors then these users have not been processed.  Don't blank
        // them or the user will need to re-enter them. 
        if ( ! parentEditor().hasValidationErrors())
        {
            setNewUserIDs(null);
        }

        super.appendToResponse(aResponse, aContext);

        clearUserValidationErrors();
    }

    

    /**
     * Updates the group members from newUserIDs() as indicated by selectedAction() if the contents 
     * of newUserIDs() is valid and our parent editor has no validation errors.  If any user IDs are 
     * not valid, a validation failure message is added to userValidationErrors() for each failure.
     */
    public void updateUsersInCurrentGroup()
    {
        // Ignore the User ID if nothing was entered.  It is not an error, just the Group Name is being updated.
        if ((newUserIDs() != null) && (newUserIDs().length() > 0))
        {
            NSArray validatedUserIDs = validatedUserIDs();
            
            // Our parent needs to know this so that it does not try to save.
            parentEditor().setEditorHasErrors(hasInvalidUserIDs());

            if (! parentEditor().hasValidationErrors())
            {
                NSArray usersFromUserIDs = usersForUserIDs(validatedUserIDs);
                
                if (selectedAction().equals(REMOVE_ACTION))
                {
                    currentGroup().removeUsers(usersFromUserIDs);
                }
                else if (selectedAction().equals(ADD_ACTION))
                {
                    currentGroup().addUsers(usersFromUserIDs);
                }
                else if (selectedAction().equals(REPLACE_ACTION))
                {
                    currentGroup().removeUsers(currentGroup().users());
                    currentGroup().addUsers(usersFromUserIDs);
                }
            }
        }
    }



    /**
     * Returns the valid user IDs from newUserIDs().  If there are any invalid user IDs, a validation 
     * failure message is added to userValidationErrors() for that user ID and the user ID is <b>not</b> 
     * in the returned list.
     *
     * @return list of the valid user IDs from newUserIDs()
     */
    public NSArray validatedUserIDs()
    {
        /** require [newUserIDs_valid] (newUserIDs() != null) && (newUserIDs().length() > 0);  **/
        
        UserIDTokenizer  userIDTokenizer = new UserIDTokenizer(newUserIDs());
        
        NSArray invalidIDs = userIDTokenizer.invalidUserIDs();
        for (int i = 0; i < invalidIDs.count(); i++)
        {
            String anID = (String) invalidIDs.objectAtIndex(i);
            userValidationErrors().addObject(anID + ": " + 
                userIDTokenizer.errorsFromInvalidUserID(anID).componentsJoinedByString(", "));
        }

        return userIDTokenizer.validUserIDs();

        /** ensure [valid_result] Result != null;  **/
    }

    

    /**
     * Returns a User object for each user ID in validatedUserIDs.  If a user does not already exist 
     * for the user ID, and the selectedAction() is not to remove users as group members, a new user 
     * is created.
     *
     * @param validatedUserIDs list of user IDs to return Users for
     *
     * @return list Users corresponding to validatedUserIDs
     */
    public NSArray usersForUserIDs(NSArray validatedUserIDs)
    {
        /** require [valid_validatedUserIDs] validatedUserIDs != null; **/
        
        NSMutableArray usersForUserIDs =  new NSMutableArray(validatedUserIDs.count());
        Enumeration userIDEnumerator = validatedUserIDs.objectEnumerator();
        while (userIDEnumerator.hasMoreElements())
        {
            String validatedUserID = (String)userIDEnumerator.nextElement();

            User userForID = User.userForUserID(validatedUserID, editingContext());

            // Users are created on demand when adding and replacing
            if ((userForID == null) && ! selectedAction().equals(REMOVE_ACTION))
            {
                userForID = User.createUser(editingContext(), validatedUserID);
            }

            // aUser can be null if the action is delete and the userID does not match an existing user
            if (userForID != null)
            {
                usersForUserIDs.addObject(userForID);
            }
        }

        return usersForUserIDs;

        /** ensure [valid_result] (Result.count() == validatedUserIDs.count()) || selectedAction().equals(REMOVE_ACTION);  **/
    }

    
    
    /**
     * Returns </code>true</code> if currentGroup() has any members.
     *
     * @return </code>true</code> if currentGroup() contains any groups
     */
    public boolean currentGroupHasUsers()
    {
        return currentGroup().users().count() > 0;
    }



    /**
     * Returns </code>true</code> if aUser() can be removed from currentGroup().
     *
     * @return </code>true</code> if aUser() can be removed from currentGroup()
     */
    public boolean canRemoveUser()
    {
        return currentGroup().canRemoveUser(aUser());
    }



    /**
     * Action method to remove a single user from the Access Group.
     */
    public WOComponent removeUserFromCurrentGroup()
    {
        /** require [has_user] aUser() != null;  
                    [not_system_owner]  ! currentGroup().parentWebsite().owner().equals(aUser());  **/

        DebugOut.println(3,"Deleting User " + aUser().userID());
        currentGroup().removeObjectFromBothSidesOfRelationshipWithKey(aUser, "users");
        editingContext().saveChanges();
        return context().page();

        /** ensure [user_not_in_group]  ! currentGroup().users().containsObject(aUser);  **/
    }
    
    
    
    /**
     * Returns the list of users in this group, sorted, but with the Website owner first in the list.
     *
     * @return the list of users in this group, sorted, but with the Website owner first in the list
     */
    public void sortUsers()
    {
        /** require [has_website]  currentGroup().parentWebsite() != null;  **/

        User owner = currentGroup().parentWebsite().owner();
        setSortedUsers(owner.sortUsers(currentGroup().users()));

        /**     ensure [sorted_users_set] sortedUsers() != null;  **/
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
     * Returns </code>true</code> if any of the entered User IDs could not be validated.
     *
     * @return </code>true</code> if any of the entered User IDs could not be validated
     */
    public boolean hasInvalidUserIDs()
    {
        return userValidationErrors().count() > 0;
    }

     

    /* Generic setters and getters ***************************************/
    
    public ConfigAccessGroup parentEditor() {
        return (ConfigAccessGroup) parent();
    }
    
    public LocalGroup currentGroup() {
        return currentGroup;
    }
    public void setCurrentGroup(LocalGroup aGroup) {
        currentGroup = aGroup;
    }
    
    public EOEditingContext editingContext() {
        return currentGroup().editingContext();
    }
    
    public User aUser() {
        return aUser;
    }
    public void setAUser(User newAUser) {
        aUser = newAUser;
    }

    public String newUserIDs() {
        return newUserIDs;
    }
    public void setNewUserIDs(String newNewUserIDs) {
        newUserIDs = newNewUserIDs;
    }

    public NSArray sortedUsers()
    {
        return sortedUsers;
    }
    public void setSortedUsers(NSArray newSortedUsers)
    {
        sortedUsers = newSortedUsers;
    }

    public String selectedAction() {
        return selectedAction;
    }
    public void setSelectedAction(String newSelectedAction) {
        selectedAction = newSelectedAction;
    }

    public NSMutableArray userValidationErrors() {
        return userValidationErrors;
    }
    public void clearUserValidationErrors() {
        userValidationErrors.removeAllObjects();
    }

}
