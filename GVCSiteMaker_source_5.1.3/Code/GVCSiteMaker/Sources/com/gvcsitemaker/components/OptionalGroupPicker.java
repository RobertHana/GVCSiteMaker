//Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
//This software is published under the terms of the Educational Community License (ECL) version 1.0,
//a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.components;
import java.util.Enumeration;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.Group;
import com.gvcsitemaker.core.PublicGroup;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.interfaces.WebsiteContainer;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;


/**
 * Component to manage access Groups for an SMAccessible EO.  Functions the same way as GroupPicker except that it provides a "None" option to be selected.
 */
public class OptionalGroupPicker extends WOComponent
{
    static final String PublicGroupKey = "Public";
    static final String InternalGroupKey = "Internal";
    static final String NoGroupKey = "None";
    
    protected String mainLabel;
    protected String labelDescription;
    protected String name;
 
    protected NSArray allAccessGroups;              // All access Groups user can choose from, used by WORepetition
    public Group aGroup;                            // Item in WORepetition over allGroups
    
    protected Group publicGroup;
    protected Website website;
    protected NSMutableArray selectedInternalGroups;

    public boolean hasSelectedInternalGroups;
    
    protected EOEnterpriseObject eo;
    protected String groupsKeyPath;
    
    
    /**
     * Designated constructor.
     */
    public OptionalGroupPicker(WOContext aContext)
    {
        super(aContext);
        selectedInternalGroups = new NSMutableArray();
        hasSelectedInternalGroups = false;
        
        /** ensure [selectedInternalGroups_not_null] selectedInternalGroups != null; **/
    }
    
    
    
    /**
     * Returns the string representing the button that is currently selected.
     * 
     * @return the string representing the button that is currently selected.
     */    
    public String selectedButton()
    {
        String selectedButton = null;
        
        if (groups().count() == 0)
        {
            selectedButton = NoGroupKey;
        }        
        else if (groups().containsObject(publicGroup()))
        {
            selectedButton = PublicGroupKey;
        }
        else
        {
            selectedButton = InternalGroupKey;
        }
        
        return selectedButton;

        /** ensure [Result_not_null] Result != null; **/
    }    

    
    
    /**
     * Sets the newSelection as the currently selected button.
     * 
     * @param newSelection the new selection
     */    
    public void setSelectedButton(String newSelection)
    {
        removeAllGroups();
        if (newSelection.equals(NoGroupKey))
        {
            hasSelectedInternalGroups = false;
        }
        else if (newSelection.equals(PublicGroupKey))
        {
            eo().addObjectToBothSidesOfRelationshipWithKey(publicGroup(), groupsKeyPath());
            hasSelectedInternalGroups = false;
        }
        else
        {
            hasSelectedInternalGroups = true;
        }
    }    
    
    
   
    /**
     * The editingContext used by this component which is the EO's editingContext
     * 
     * @return editingContext used by this component which is the EO's editingContext
     */     
    public EOEditingContext editingContext()
    {
        return eo().editingContext();
        
        /** ensure [Result_not_null] Result != null; **/
    }

    
    
    /**
     * Returns <code>true</code> if aGroup is in the list of Groups protecting accessibleObject().
     *
     * @return <code>true</code> if aGroup is in the list of Groups protecting accessibleObject().
     */
    public boolean isGroupSelected()
    {
        return groups().containsObject(aGroup);
    }



    /**
     * Sets whether aGroup  is in the list of Groups protecting accessibleObject() or not.  If Public access was selected via the radio button this is ignored as no access Groups should be added in this situation.
     */
    public void setIsGroupSelected(boolean isSelected)
    {
        if  (hasSelectedInternalGroups || selectedButton().equals(InternalGroupKey))
        {
            if (isSelected)
            {
                eo().addObjectToBothSidesOfRelationshipWithKey(aGroup, groupsKeyPath());
            }
            else
            {
                eo().removeObjectFromBothSidesOfRelationshipWithKey(aGroup, groupsKeyPath());
            }
        }
    }

    
    
    /**
     * Returns cached PublicGroup.  
     * 
     * @return cached PublicGroup
     */    
    public Group publicGroup()
    {
        if (publicGroup == null)
        {
            publicGroup = PublicGroup.group(editingContext());
        }

        return publicGroup;
        
        /** ensure [Result_not_null] Result != null; **/
    }

    
    
    /**
     * Returns cached Website that this component is contained.  
     * 
     * @return cached Website that this component is contained
     */    
    public Website website() 
    {
        if (website == null)
        {
            website = (Website) EOUtilities.localInstanceOfObject(editingContext(),
                    ((WebsiteContainer) parent()).website());            
        }
        
        return website;
        
        /** ensure [Result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Creates the list of Groups, allAccessGroups.  The order of this list is:<br/>
     * <ol>
     * <li>the Internal Users Group (if Internal Users are in use)</li>
     * <li>the Site Owner and Co-owners's group</li>
     * <li>all other groups in alphabetical order</li>
     * </ol>
     */
    public NSArray allAccessGroups()
    {
        if (allAccessGroups == null)
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
            allAccessGroups = EOUtilities.localInstancesOfObjects(editingContext(), groups);            
        }

        return allAccessGroups;

        /** ensure
        [all_groups_not_null] Result != null;
        [valid_group_count] Result.count() <= (website().childGroups().count() + 1); **/
    }    
    
    
    
    /**
     * Script set for the radio button's onChange binding
     * 
     * @return script set for the radio button's onChange binding
     */     
    public String onChangeScript()
    {
        return "form. " + name() + "[2].checked=true;";
        
        /** ensure [Result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Removes all groups in preparation for setting the user's selection.
     */
    protected void removeAllGroups()
    {
        NSArray allGroups = new NSArray(groups());
        Enumeration groups = allGroups.objectEnumerator();
        while (groups.hasMoreElements())
        {
            eo().removeObjectFromBothSidesOfRelationshipWithKey((EOEnterpriseObject)groups.nextElement(), groupsKeyPath());
        }
        /** ensure [no_groups_selected] groups().count() == 0; **/    
    }

    
    
    /* Generic setters and getters ***************************************/


    public String mainLabel() {
        return mainLabel;
    }
    public void setMainLabel(String newMainLabel) {
        mainLabel = newMainLabel;
    }

    public String labelDescription() {
        return labelDescription;
    }
    public void setLabelDescription(String newLabelDescription) {
        labelDescription = newLabelDescription;
    }
    
    public String name() {
        return name;
    }
    public void setName(String newName) {
        name = newName;
    }   

    public NSArray groups() {
        return (NSArray) eo.valueForKeyPath(groupsKeyPath());
    }

    public EOEnterpriseObject eo() {
        return eo;
    }
    public void setEo(EOEnterpriseObject newEo) {
        eo = newEo;
    }

    public String groupsKeyPath() {
        return groupsKeyPath;
    }
    public void setGroupsKeyPath(String newGroupsKeyPath) {
        groupsKeyPath = newGroupsKeyPath;
    }
    
    

}

