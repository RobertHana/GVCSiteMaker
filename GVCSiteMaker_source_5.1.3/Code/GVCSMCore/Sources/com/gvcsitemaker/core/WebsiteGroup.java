/**
 * Implementation of WebsiteGroup common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */  
package com.gvcsitemaker.core;

import java.util.*;

import com.webobjects.foundation.*;


/**
 * Abstract Group sub-class used for functionality common to all types of Groups that are specific to 
 * a single Website.  These groups are created and managed by Website owners and co-owners. 
 */
public abstract class WebsiteGroup extends _WebsiteGroup
{



    /**
     * Returns <code>true</code> if this Group is being used to protect a Section, SiteFile, as
     * the target of data access notifcations), or as an Editor or Contributors group. 
     *
     * @return <code>true</code> if this Group is in active use
     */
    public boolean isInUse()
    {
        return (sections().count() > 0) || 
               (editorSections().count() > 0) || 
               (contributorSections().count() > 0) || 
               (files().count() > 0) || 
               (dataAccessNotifications().count() > 0);
    }

    
    
    /**
     * WebSite groups know which users are in them.
     * 
     * @return the list of users in this group
     */
    public NSArray users()
    {
        throw new RuntimeException("Subclass must implement users()");
    }
    
    
    
    /**
     * Returns list of sections using this group to control Editors
     * 
     * @return list of sections using this group to control Editors
     */
    public NSArray editorSections()
    {
        Enumeration sectionEnum = parentWebsite().sections().objectEnumerator();
        NSMutableSet editorSections = new NSMutableSet();
        while (sectionEnum.hasMoreElements())
        {
            Section aSection = (Section)sectionEnum.nextElement();
            if (aSection.editorGroups().containsObject(this))
            {
                editorSections.addObject(aSection);
            }
        }
        
        return editorSections.allObjects();
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Returns list of sections using this group to control Contributors
     * 
     * @return list of sections using this group to control Contributors
     */
    public NSArray contributorSections()
    {
        Enumeration sectionEnum = parentWebsite().sections().objectEnumerator();
        NSMutableSet contributorSections = new NSMutableSet();
        while (sectionEnum.hasMoreElements())
        {
            Section aSection = (Section)sectionEnum.nextElement();
            if (aSection.contributorGroups().containsObject(this))
            {
                contributorSections.addObject(aSection);
            }
        }
        
        return contributorSections.allObjects();
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Overridden to trim name before validating it.  Empty strings are made null. 
     * Validates that the name is not null and not already in use.
     */
    public Object validateName(Object aValue) 
        throws NSValidation.ValidationException
    {
        // Check to ensure this group does not already exist
        Group existingGroup = parentWebsite().groupNamed((String)aValue);
        if ((existingGroup != null) && ! this.equals(existingGroup))
        {
            throw new NSValidation.ValidationException("There is already an Access Group with this name. Please choose a different name.");
        }

        return aValue;
    }
}

