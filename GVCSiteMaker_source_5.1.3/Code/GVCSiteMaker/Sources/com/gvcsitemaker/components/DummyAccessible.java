// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.components;

import java.util.Enumeration;

import com.gvcsitemaker.core.Group;
import com.gvcsitemaker.core.PublicGroup;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.interfaces.SMAccessible;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * This simple class is an adaptor for GroupPicker that stands in for an EO implementing SMAccessible.  
 * This is needed when a single GroupPicker is used for multiple EOs. 
 */
public class DummyAccessible implements SMAccessible 
    {
        protected NSMutableArray groups = new NSMutableArray();
        protected EOEditingContext editingContext;
        
        public DummyAccessible(EOEditingContext ec)
        {
            super();
            editingContext = ec;
            groups.addObject(PublicGroup.group(editingContext()));
        }
        
        /**
         * @return the groups selected in the GroupPicker
         */
        public NSArray groups() { return groups;  }
        
        
        /**
         * SMAccessible method.  Permits access to this resource to members of the group.
         * 
         * @param aGroup the Group to allow access to
         */
        public void allowAccessForGroup(Group newGroup)
        {
            /** require [valid_group] newGroup != null; 
                        [same_ec] editingContext() == newGroup.editingContext();      **/
            if ( ! groups().containsObject(newGroup))
            {
                groups.addObject(newGroup);        

                Enumeration anEnum = new NSArray(groups()).objectEnumerator();
                while (anEnum.hasMoreElements())
                {
                    Group currentGroup = (Group)anEnum.nextElement();
                    if (newGroup instanceof PublicGroup) 
                    {
                        revokeAccessForGroup(currentGroup);
                    }
                    else if (currentGroup instanceof PublicGroup)
                    {
                        revokeAccessForGroup(currentGroup);
                    }
                }
            }
        }
        
        
        /**
         * SMAccessible method.  Removes permission access to this resource by members of the group.
         * 
         * @param aGroup the Group to revoke access to
         */
        public void revokeAccessForGroup(Group aGroup)
        {
            /** require [valid_group] aGroup != null; 
                        [same_ec] editingContext() == aGroup.editingContext();      **/
            
            if (groups().containsObject(aGroup))
            {
                groups.removeObject(aGroup);        
                
                // We just removed a Group.  Ensure that accessibleObject() has at least the Public Group.
                if ( groups().count() == 0)
                {
                    allowAccessForGroup(PublicGroup.group(editingContext()));
                }
            }
        }
        
        public boolean isPublic() {return true; }
        public boolean isViewableByUser(User aUser) { return true;  }
        public boolean requiresSSLConnection() { return true; }
        public EOEditingContext editingContext() { return editingContext;  }
    }

