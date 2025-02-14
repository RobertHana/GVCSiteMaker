// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/**
 * This page allows a new Access Group to be created and deleted and provides
 * a link to configure exisiting groups. A present the Website's Configure Group
 * is excluded from display.
 */
public class ManageAccessGroups extends com.gvcsitemaker.core.components.WebsiteContainerBase implements SMSecurePage
{
    protected Group selectedGroup; // for the repetition
    public int selectedGroupIndex;

    protected Group groupToDelete;
    protected Group groupToEdit;
    protected NSMutableSet groupsBeingEdited = new NSMutableSet();
    protected NSMutableArray orderedGroups;

    protected String validationErrorMessage = "";
    protected Group errorGroup;

    protected final NSArray nameOrdering = new NSArray(EOSortOrdering.sortOrderingWithKey("name", EOSortOrdering.CompareAscending));

    protected NSMutableDictionary groupMembers = new NSMutableDictionary();

    public boolean hasLDAPGroups;


    /**
     * Designated constructor.
     */
    public ManageAccessGroups(WOContext aContext)
    {
        super(aContext);
        editingContext().revert(); // Just in case!
        hasLDAPGroups = LDAPBranch.orderedObjects(editingContext()).count() > 0;
    }



    /**
     * Overridden to setup state before display and reset afterward.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        updateOrderedGroups();

        super.appendToResponse(aResponse, aContext);

        errorGroup = null;
        setValidationErrorMessage("");
    }



    /**
     * Action method to jump to configuring the remote participants group.
     *
     * @return the ConfigAccessGroup page set to configure the remote participants group
     */
    public WOComponent configureRemoteParticipantsGroup()
    {
        ConfigAccessGroup nextPage = (ConfigAccessGroup) pageWithName("ConfigAccessGroup");
        nextPage.setCurrentGroup(website().remoteParticipantGroup());

        return nextPage;
    }



    /**
     * Returns </code>true</code> if the current group is not a configure group.  This is an 
     * obsolete restriction which could be removed.  Hence it is implemented at the UI layer.
     *
     * @return </code>true</code> if the current group is not a configure group
     */
    public String conditionallyEditAccessGroupOnOpen()
    {
        if (selectedGroup().equals(groupToEdit))
        {
            return editInPlaceID() + "Edit();";
        }
        return "";
    }



    /**
     * Returns </code>true</code> if the current group is not a configure group.  This is an 
     * obsolete restriction which could be removed.  Hence it is implemented at the UI layer.
     *
     * @return </code>true</code> if the current group is not a configure group
     */
    public boolean canEditGroupName()
    {
        return ! (selectedGroup() instanceof ConfigureGroup || selectedGroup() instanceof RemoteParticipantGroup);
    }



    /**
     * Creates a new <code>LocalGroup</code>.
     */
    public void createLocalGroup()
    {
        /** require [valid_name] newGroupName != null; **/

        if ( ! atLeastOneGroupBeingEdited())
        {
            EOEditingContext ec = smSession().newEditingContext();
            Website localWebsite = (Website)EOUtilities.localInstanceOfObject(ec, website());
            LocalGroup newGroup = localWebsite.newLocalGroupNamed(localWebsite.newGroupName());

            newGroup.editingContext().saveChanges();

            newGroup = (LocalGroup)EOUtilities.localInstanceOfObject(website().editingContext(), newGroup);
            orderedGroups.addObject(newGroup);
        }
        /** ensure [currentGroup_set] currentGroup() != null; **/
    }


    /**
     * Creates a new <code>LocalGroup</code>.
     */
    public void createDirectoryGroup()
    {
        /** require [valid_name] newGroupName != null; **/

        if ( ! atLeastOneGroupBeingEdited())
        {
            LDAPBranch ldapBranch = (LDAPBranch)LDAPBranch.orderedObjects(editingContext()).objectAtIndex(0);
            LDAPGroup newGroup = website().newLDAPGroup(website().newGroupName(), ldapBranch);
            orderedGroups.addObject(newGroup);
            groupsBeingEdited.add(newGroup);

            // saveChanges();  can't save changes yet due to partially configured group
        }

        /** ensure [currentGroup_set] currentGroup() != null; **/
    }



    /**
     * Action method to display the modal dialog to confirm deletion of a group.
     */
    public void confirmDeleteGroup()
    {
        groupToDelete = selectedGroup;
        AjaxModalDialog.open(context(), "ConfirmDeleteAccessGroupModalDialog");
    }


    /**
     * Action method to initiate the deletion of an Access Group.  The deletion is confirmed on a ConfirmAction page before being processed by confirmAction() in this class.
     */
    public ConfirmAction deleteGroup()
    {
        DebugOut.println(3, "Deleting group " + groupToDelete.name() + " from site " + website().siteID());
        website().removeGroup(groupToDelete);
        orderedGroups.removeObject(groupToDelete);
        editingContext().saveChanges();
        groupToDelete = null;

        return null;
    }



    /**
     * Creates and sorts the list of Access Groups to display
     */
    public void updateOrderedGroups()
    {
        setOrderedGroups(new NSMutableArray(website().childGroups()));
        EOSortOrdering.sortArrayUsingKeyOrderArray(orderedGroups(), nameOrdering);

        Group remoteParticipantsGroup = website().remoteParticipantGroup();
        if (remoteParticipantsGroup != null)
        {
            orderedGroups().removeObject(remoteParticipantsGroup);
        }
        /** ensure [has_ordered_groups] orderedGroups() != null;  **/
    }



    /**
     * Returns the string to display in the view mode of the Membership column.
     *
     * @return the group membership name
     */
    public String membershipViewString()
    {
        String groupName = null;

        if (selectedGroup.type().equals("LDAP"))
        {
            groupName = ((LDAPGroup) selectedGroup).ldapGroupName();
            if (groupName == null)
            {
                groupName = "<i>(enter name of UM Directory group)</i>";
            }
        }
        else if (selectedGroup.type().equals("Local") || selectedGroup.type().equals("Configure"))
        {
            int numberOfUsers = ((WebsiteGroup)selectedGroup).users().count();
            groupName = numberOfUsers == 0 ? "<i>(enter User IDs, one per line)</i>" : numberOfUsers + " members";
        }

        return groupName;
    }



    /**
     * Returns true iff the selected group is an LDAP group.
     *
     * @return true iff the selected group is an LDAP group
     */
    public boolean isLDAPGroup()
    {
        return selectedGroup.type().equals("LDAP");
    }



    /**
     * Returns true iff the user can edit the current group.
     *
     * @return true iff the user can edit the current group
     */
    public boolean canEditGroup()
    {
        return (groupsBeingEdited.count() == 0 || groupsBeingEdited.contains(selectedGroup));
    }


    /**
     * Returns true iff the user can edit the current group.
     *
     * @return true iff the user can edit the current group
     */
    public boolean atLeastOneGroupBeingEdited()
    {
        return (groupsBeingEdited.count() > 0);
    }



    /**
     * Returns the valid user IDs from newUserIDs().  If there are any invalid user IDs, a validation 
     * failure message is added to userValidationErrors() for that user ID and the user ID is <b>not</b> 
     * in the returned list.
     *
     * @return list of the valid user IDs from newUserIDs()
     */
    public NSArray validatedUserIDs(String userIDs)
    {
        /** require [userIDs_valid] (userIDs != null) && (userIDs.length() > 0);  **/

        UserIDTokenizer  userIDTokenizer = new UserIDTokenizer(userIDs);

        NSArray invalidIDs = userIDTokenizer.invalidUserIDs();
        for (int i = 0; i < invalidIDs.count(); i++)
        {
            String anID = (String) invalidIDs.objectAtIndex(i);
            validationErrorMessage += (anID + ": " + 
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
     * @param ec the editing context to return users in
     * @param validatedUserIDs list of user IDs to return Users for
     */
    public NSArray usersForUserIDs(EOEditingContext ec, NSArray validatedUserIDs)
    {
        /** require [valid_validatedUserIDs] validatedUserIDs != null; **/
        
        NSMutableArray usersForUserIDs =  new NSMutableArray(validatedUserIDs.count());
        Enumeration userIDEnumerator = validatedUserIDs.objectEnumerator();
        while (userIDEnumerator.hasMoreElements())
        {
            String validatedUserID = (String)userIDEnumerator.nextElement();

            User userForID = User.userForUserID(validatedUserID, ec);

            // Users are created on demand when adding and replacing
            if (userForID == null)
            {
                userForID = User.createUser(ec, validatedUserID);
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
     * Returns a string representing the list of group members.
     *
     * @return a string representing the list of group members
     */
    public String groupMembersString()
    {
        //return ((LocalGroup)selectedGroup).membersEmailAddresses().componentsJoinedByString("\n");
        if (groupMembers.objectForKey(selectedGroup) == null)
        {
            NSMutableArray sortedEmailAddresses;
            NSArray emailAddresses = ((LocalGroup)selectedGroup).membersEmailAddresses();
            try
            {
                sortedEmailAddresses = emailAddresses.sortedArrayUsingComparator(NSComparator.AscendingCaseInsensitiveStringComparator).mutableClone();
                sortedEmailAddresses.removeObject(website().owner().emailAddress());
                sortedEmailAddresses.insertObjectAtIndex(website().owner().emailAddress(), 0);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            groupMembers.setObjectForKey(sortedEmailAddresses.componentsJoinedByString("\n"), selectedGroup);
        }
        return (String)groupMembers.objectForKey(selectedGroup);
    }


    /**
     * Returns a string representing the list of group members.
     *
     * @return a string representing the list of group members
     */
    public void setGroupMembersString(String newGroupMembers)
    {
        if (newGroupMembers == null)
        {
            newGroupMembers = "";
        }
        groupMembers.setObjectForKey(newGroupMembers, selectedGroup);
    }



    /**
     * Used by the AJAX control on this page to revert changes.
     *
     * @return null, this value is ignored
     */
    public void revertChanges()
    {
        website().editingContext().revert();
        groupMembers = new NSMutableDictionary();

        errorGroup = null;
        setValidationErrorMessage("");
        groupsBeingEdited.removeAllObjects();
    }


    /**
     * Used by the AJAX control on this page to save changes.
     */
    public void saveNameChange()
    {
        errorGroup = null;
        setValidationErrorMessage("");

        try
        {
            website().editingContext().saveChanges();
        }
        catch (Exception e)
        {
            errorGroup = selectedGroup;
            setValidationErrorMessage(e.getLocalizedMessage());
        }
    }



    /**
     * Used by the AJAX control on this page to save changes.
     *
     * @return null, this value is ignored
     */
    public void saveChanges()
    {
        errorGroup = null;
        setValidationErrorMessage("");

        if (selectedGroup instanceof LocalGroup)
        {
            EOEditingContext ec = smSession().newEditingContext();

            NSArray users = usersForUserIDs(ec, validatedUserIDs((String)groupMembers.objectForKey(selectedGroup)));
            if (validationErrorMessage() != null && ! validationErrorMessage().equals(""))
            {
                errorGroup = selectedGroup;
                groupsBeingEdited.add(selectedGroup);
                //context().response().setStatus(500);
                return;
            }

            users = EOUtilities.localInstancesOfObjects(ec, users);
            User owner = (User)EOUtilities.localInstanceOfObject(ec, website().owner());
            if ( ! users.contains(owner))
            {
                errorGroup = selectedGroup;
                setValidationErrorMessage("Cannot remove the owner/co-owner from any group");
                groupsBeingEdited.add(selectedGroup);
                //context().response().setStatus(500);
                return;
            }

            LocalGroup saveGroup = (LocalGroup)EOUtilities.localInstanceOfObject(ec, selectedGroup);
            saveGroup.setUsers(users.mutableClone());

            try
            {
                saveGroup.editingContext().saveChanges();

                // once saved without error, set the group back to the default format
                groupMembers.removeObjectForKey(selectedGroup);
                groupsBeingEdited.removeAllObjects();
            }
            catch (Exception e)
            {
                errorGroup = selectedGroup;
                setValidationErrorMessage(e.getLocalizedMessage());
                groupsBeingEdited.add(selectedGroup);
                //context().response().setStatus(500);
            }
        }
        else
        {
            try
            {
                ((LDAPGroup)selectedGroup).validateLdapGroupName(((LDAPGroup)selectedGroup).ldapGroupName());
            }
            catch (Exception e)
            {
                errorGroup = selectedGroup;
                setValidationErrorMessage(e.getLocalizedMessage());
                groupsBeingEdited.add(selectedGroup);
                return;
            }

            try
            {
                website().editingContext().saveChanges();
                groupsBeingEdited.removeAllObjects();
            }
            catch (Exception e)
            {
                errorGroup = selectedGroup;
                setValidationErrorMessage(e.getLocalizedMessage());
                groupsBeingEdited.add(selectedGroup);
                //context().response().setStatus(500);
            }
        }
        //AjaxUtils.createResponse(context().request(), context()).setStatus(409);
    }



    /**
     * Overriden to handle errors in LDAP group names.
     */
    @Override
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        super.validationFailedWithException(t, value, keyPath);

        if (keyPath.equals("selectedGroup.ldapGroupName"))
        {
            //errorGroup = selectedGroup;
            //setValidationErrorMessage(t.getLocalizedMessage());
            //groupsBeingEdited.add(selectedGroup);

            // force the value into the eo
            ((LDAPGroup)selectedGroup).setLdapGroupName((String)value);
        }
    }



    /**
     * Returns </code>true</code> if there are any validation errors.
     *
     * @return </code>true</code> if there are any validation errors
     */
    public boolean hasValidationError()
    {
        return ((validationErrorMessage() != null) && (selectedGroup.equals(errorGroup)));
    }



    public String editLDAPGroupNameInPlaceFieldID()
    {
        return "editLDAPGroupNameInPlaceField" + selectedGroupIndex;
    }


    public String editGroupNameInPlaceID()
    {
        return "editGroupName" + selectedGroupIndex;
    }


    public String editInPlaceID()
    {
        return "editMembers" + selectedGroupIndex;
    }


    public String editInPlaceEdit()
    {
        return "editMembers" + selectedGroupIndex + "Edit()";
    }


    public String editInPlaceSave()
    {
        return "editMembers" + selectedGroupIndex + "Save()";
    }


    public String editInPlaceCancel()
    {
        return "editMembers" + selectedGroupIndex + "Cancel()";
    }


    public String onClickMembershipEditInPlace()
    {
        return "function () { AIP.cleanupView('" + editInPlaceID() + "'); ManageAccessGroupsUpdate(); }";
    }



    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Configure Access Groups";
    }


    /* Generic setters and getters ***************************************/

    public Group selectedGroup()
    {
        return selectedGroup;
    }


    public void setSelectedGroup(Group newSelectedGroup)
    {
        selectedGroup = newSelectedGroup;
    }


    public NSMutableArray orderedGroups()
    {
        return orderedGroups;
    }


    public void setOrderedGroups(NSMutableArray newOrderedGroups)
    {
        orderedGroups = newOrderedGroups;
    }


    public String validationErrorMessage()
    {
        return validationErrorMessage;
    }


    public void setValidationErrorMessage(String newValidationErrorMessage)
    {
        validationErrorMessage = newValidationErrorMessage;
    }


    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
    }



    public Group accessGroupToEdit()
    {
        return groupToEdit;
    }

    public void setAccessGroupToEdit(Group newAccessGroupToEdit)
    {
        groupToEdit = newAccessGroupToEdit;
    }



    public boolean isEditingGroup()
    {
        return (groupsBeingEdited.contains(selectedGroup) || selectedGroup.equals(errorGroup));
    }

    public void setIsEditingGroup(boolean isEditing)
    {
        // only turn editing on for the first one
        if (isEditing && groupsBeingEdited.count() == 0)
        {
            groupsBeingEdited.add(selectedGroup);
        }
    }



}
