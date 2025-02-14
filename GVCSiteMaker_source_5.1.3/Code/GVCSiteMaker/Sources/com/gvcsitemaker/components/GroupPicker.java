// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.components;
import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.Group;
import com.gvcsitemaker.core.InternalUsersGroup;
import com.gvcsitemaker.core.PublicGroup;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.interfaces.SMAccessible;
import com.gvcsitemaker.core.interfaces.WebsiteContainer;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;


/**
 * Component to manage access Groups for an SMAccessible EO.
 */
public class GroupPicker extends WOComponent
{

    protected SMAccessible accessibleObject;			// Object being protected
    protected String typeOfAccess;					// Binding, what is being protected: File, Section etc.
    protected boolean wasPublicAccessSelected;		// True if Public Access radio button was selected
    protected EOEditingContext editingContext;		// The editing context accessibleObject is in
    protected Website website;						// The Website that accessibleObject is / will be in
    protected Group internalUsersGroup;				// Local copy of Internal Users Group
    protected Group publicGroup;						// Local copy of Public Group

    /** @TypeInfo Group */
    protected NSArray allGroups;						// All access Groups user can choose from, used by WORepetition
    protected Group aGroup;							// Item in WORepetition over allGroups

 
    /**
     * Designated constructor.
     */
    public GroupPicker(WOContext aContext)
   {
        super(aContext);
        setWasPublicAccessSelected(false);
    }

  

    /**
     * Sets the Accessible Object (object implemtenting the SMAccessible interface) whose access Groups will be set by this component.  This method gives the Accessible Object Public access if it has nothing previously set, and also intializes this component based on this object and the Website in our parent WebsiteContainer.
     *
     * @param newAccessibleObject - object implemtenting the SMAccessible interface whose access Groups are to be set
     */
    public void setAccessibleObject(SMAccessible newAccessibleObject)
    {
        /** require
        [new_access0ble_object_not_null] newAccessibleObject != null;
        [parent_not_null] parent() != null;
        [parent_is_website_container] parent() instanceof WebsiteContainer; **/

        
        if (accessibleObject != newAccessibleObject)
        {
            accessibleObject = newAccessibleObject;

            // If the accessibleObject is in an editing context, then use that context.  Othewise all we can use is use the default one in the session
            editingContext = accessibleObject().editingContext();
            if ( editingContext == null)
            {
                editingContext = session().defaultEditingContext();
            }

            // The Website from our parent WebsiteContainer.  This is assumed to be the Website that accessibleObject is part of.
            website = (Website) EOUtilities.localInstanceOfObject(editingContext(),
                                                                  ((WebsiteContainer) parent()).website());
            createGroupsList();

            internalUsersGroup = InternalUsersGroup.group(editingContext());
            publicGroup = PublicGroup.group(editingContext());

            // Ensure the accessibleObject has Public access if it has nothing previously set
            if ( accessibleObject().groups().count() == 0)
            {
                protectWithGroup(true, publicGroup());
            }

            setWasPublicAccessSelected(isAccessibleObjectProtectedByGroup(publicGroup));
        }

        /** ensure
        [accessible_object_not_null] accessibleObject() != null;
        [accessible_object_groups_count_not_zero] accessibleObject().groups().count() != 0;
        [editing_context_not_null] editingContext() != null;
        [all_groups_not_null] allGroups() != null;
        [internal_user_groups_not_null] internalUsersGroup() != null;
        [public_group_not_null] publicGroup() != null; **/  
    }



    /**
     * Returns the Accessible Object (object implemtenting the SMAccessible interface) whose access Groups is being set by this component.
     *
     * @return the Accessible Object (object implemtenting the SMAccessible interface) whose access Groups is being set by this component.
     */
    public SMAccessible accessibleObject()
    {
        return accessibleObject;
    }



    /**
     * Creates the list of Groups, allGroups.  The order of this list is:<br/>
     * <ol>
     * <li>the Internal Users Group (if Internal Users are in use)</li>
     * <li>the Site Owner and Co-owners's group</li>
     * <li>all other groups in alphabetical order</li>
     * </ol>
     */
    public void createGroupsList()
    {
        NSMutableArray groups = new NSMutableArray(website().orderedWebsiteGroups());
        DebugOut.println(3,"Got childGroups: " + groups.valueForKey("name"));

        // Move the site owners' group to the first position
        groups.removeObject(website().ownersGroup());
        groups.insertObjectAtIndex(website().ownersGroup(), 0);

        // Add the Internal Users Group if there is one.  This needs to be added to the list of groups 
        // manually as it is not attached to a Website.
        if (((SMApplication)application()).properties().booleanPropertyForKey("HasInternalUsers"))
        {
            // Smack the InternalUsersGroup onto the very beginning
            groups.insertObjectAtIndex(((Session)session()).internalUsersGroup(), 0);
            DebugOut.println(3,"Added InternalUsersGroup.");
        }

        // This really ought not be needed.  One day, after refactoring this crap away...
        allGroups = EOUtilities.localInstancesOfObjects(editingContext(), groups);

        /** ensure
        [all_groups_not_null] allGroups() != null;
        [valid_group_count] allGroups().count() <= (website().childGroups().count() + 1); **/
    }



    /**
     * Returns either "Groups" or "Public" for the Access Groups / Public Access radio buttons.
     *
     * @return either "Groups" or "Public" for the Access Groups / Public Access radio buttons.
     */
    public String selectedButton()
    {
        String selectedButton = null;

        DebugOut.println(3,"====Getting selected button ");

        // This check is just a guard against bugs in lieu of proper DBC
        if (isAccessibleObjectProtectedByGroup(publicGroup) && (accessibleObject().groups().count() == 1))
        {
            selectedButton =  "Public";
        }
        else if (isAccessibleObjectProtectedByGroup(publicGroup))
        {
            throw new RuntimeException("Group Picker found multiple groups with public access: " +
                                       accessibleObject().groups().valueForKey("name"));      
        }
        else if (accessibleObject().groups().firstObjectCommonWithArray(website().childGroups()) != null)
        {
            selectedButton =  "Groups";
        }
        else if (isAccessibleObjectProtectedByGroup(internalUsersGroup()) )
        {
            selectedButton =  "Groups";
        }
        else 
        {
            throw new RuntimeException("Group Picker found no matching conditions for groups: " +
                                       accessibleObject().groups().valueForKey("name"));      
        }

        DebugOut.println(3,"==== Selected button is " + selectedButton);
        DebugOut.println(1, "wasPublicAccessSelected() " + wasPublicAccessSelected());

        return selectedButton;

        /** ensure
            [result_not_null] Result != null;
        [valid_result] Result.equals("Groups") || Result.equals("Public"); **/
    }



    /**
     * Sets the selection value for the Access Groups / Public Access radio buttons.  If value is "Public", it removes all access Groups and adds the Public access Group to accessibleObject().  If value is "Groups", updating the access Groups is deferred to the setIsGroupSelected() method.
     */
    public void setSelectedButton(String value)
    {
        /** require
        [value_not_null] value != null;
        [value_valid] value.equals("Groups") || value.equals("Public"); **/
        
        DebugOut.println(3,"====Setting selected button to " + value);

        if (value.equals("Public"))
        {
            protectWithGroup(true, publicGroup());
            setWasPublicAccessSelected(true);
        }
        else
        {
            setWasPublicAccessSelected(false);
        }

        /** ensure [value_valid] value.equals("Groups") || value.equals("Public"); **/
    }



    /**
     * Returns <code>true</code> if accessibleObject is protected by someGroup.
     *
     * @return <code>true</code> if accessibleObject is protected by someGroup.
     */
    protected boolean isAccessibleObjectProtectedByGroup(Group someGroup)
    {
        /** require
        [accessible_object_not_null] accessibleObject() != null;
        [some_group_not_null] someGroup != null; **/
        
        return accessibleObject().groups().containsObject(someGroup);
    }



    /**
     * Adds someGroup (if protectWithGroup == true) to the Groups protecting accessibleObject().  Removes someGroup (if protectWithGroup == false) from the Groups protecting accessibleObject().  It ensures that accessibleObject() has at least the Public Group.
     */
    protected void protectWithGroup(boolean protectWithGroup, Group someGroup)
    {
        /** require
        [accessible_object_not_null] accessibleObject() != null;
        [some_group_not_null] someGroup != null; **/
        
        // If true and group IS NOT set for object, then add it!
        if (protectWithGroup && ! accessibleObject().groups().containsObject(someGroup))
        {
            DebugOut.println(3,"Adding group " + someGroup.name() + " to Object");
            accessibleObject().allowAccessForGroup(someGroup);
        }
        // if false and group IS set for object, then remove it.
        else if (( ! protectWithGroup) && accessibleObject().groups().containsObject(someGroup))
        {
            if (accessibleObject().groups().count() > 1)
            {
                DebugOut.println(3,"Removing group " + someGroup.name() + " from Object");
                accessibleObject().revokeAccessForGroup(someGroup);
            }
            else
            {
                DebugOut.println(3,"Removing last group " + someGroup.name() + " from Object by granting public access");
                accessibleObject().allowAccessForGroup( publicGroup());
            }
        }

        /** ensure [protected_with_group] (protectWithGroup && isAccessibleObjectProtectedByGroup(someGroup)) || ( ! (protectWithGroup || isAccessibleObjectProtectedByGroup(someGroup))); **/
    }



    /**
     * Returns <code>true</code> if aGroup is in the list of Groups protecting accessibleObject().
     *
     * @return <code>true</code> if aGroup is in the list of Groups protecting accessibleObject().
     */
    public boolean isGroupSelected()
    {
        /** require [accessible_object_not_null] accessibleObject() != null;  **/
        
        return isAccessibleObjectProtectedByGroup(aGroup);
    }



    /**
     * Sets whether aGroup  is in the list of Groups protecting accessibleObject() or not.  If Public access was selected via the radio button this is ignored as no access Groups should be added in this situation.
     */
    public void setIsGroupSelected(boolean isSelected)
    {
        /** require [accessible_object_not_null] accessibleObject() != null;  **/
        
        DebugOut.println(1, "wasPublicAccessSelected() " + wasPublicAccessSelected());
        if (! wasPublicAccessSelected())
        {
            protectWithGroup(isSelected, aGroup);
        }
    }



    /**
     * @return <code>true</code> if what is being protected ends in an s.  For UI niceness
     */
    public boolean isPlural() 
    {
        return typeOfAccess().endsWith("s");
    }
    
    

    /* Generic setters and getters ***************************************/

    public String typeOfAccess() {
        return typeOfAccess;
    }
    public void setTypeOfAccess(String newTypeOfAccess) {
        typeOfAccess = newTypeOfAccess;
    }

    public Group aGroup() {
        return aGroup;
    }
    public void setAGroup(Group newAGroup) {
        aGroup = newAGroup;
    }

    public NSArray allGroups()
    {
        return allGroups;
    }

    public EOEditingContext editingContext() {
        return editingContext;
    }

    public Website website() {
        return website;
    }

    public Group internalUsersGroup() {
        return internalUsersGroup;
    }

    public Group publicGroup() {
        return publicGroup;
    }

    public boolean wasPublicAccessSelected()
    {
        return wasPublicAccessSelected;
    }
    public void setWasPublicAccessSelected(boolean newWasPublicAccessSelected) {
        wasPublicAccessSelected = newWasPublicAccessSelected;
    }

}

