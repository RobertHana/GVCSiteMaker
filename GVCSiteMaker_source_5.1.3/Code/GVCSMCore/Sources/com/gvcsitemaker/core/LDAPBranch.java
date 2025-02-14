// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import net.global_village.eofextensions.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * An LDAP Branch represents groups in a sub-tree of a single LDAP repository.  It handles locating
 * the LDAP groups by name and checking membership queries.
 */
public class LDAPBranch extends _LDAPBranch
{
    protected static boolean isDebugging = false;
    protected static long numAuthorizations = 0;
    protected static long totalAuthorizationTime = 0;
    
    

    /**
     * Activates or deactivates debugging mode.
     * @param shouldDebug <code>true</code> if LDAPBranch debugging should be activated
     */
    public static void setDebugMode(boolean shouldDebug)
    {
        isDebugging = shouldDebug;
    }
    
    
    /**
     * Returns <code>true</code> if LDAPBranch debugging is enabled. 
     * @return <code>true</code> if LDAPBranch debugging is enabled
     */
    protected static boolean isDebuggingEnabled()
    {
        return isDebugging;
    }
    
    
    public static NSArray orderedObjects(EOEditingContext editingContext)
    {
        /** require [valid_ec] editingContext != null;  **/
        return EOEditingContextAdditions.orderedObjects(editingContext,
               "LDAPBranch", "displayOrder", EOSortOrdering.CompareAscending);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * LDAPBranches are copied by reference.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a reference to this LDAPBranch
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return this;

        /** ensure [copy_made] Result != null;   **/
    }
    
    
    
    /**
     * Returns <code>true</code> if <code>aUser</code> is a member of this LDAP Group.
     *  
     * @param aUser the <code>User</code> to test for membership in this Group.
     * @return <code>true</code> if <code>aUser</code> is a member of this Group
     */
    public boolean isMember(LDAPGroup ldapGroup, User aUser)
    {
        /** require [valid_group] ldapGroup != null;
                    [valid_user] aUser != null;                     **/
    
        // For debugging   
        NSTimestamp start = null;
        NSTimestamp stop = null;
        
        if (isDebuggingEnabled())
        {
            start = new NSTimestamp();
            
            if (isDebuggingEnabled())
           {
               DebugOut.println(1, "Checking " + aUser.userID() + "'s membership in LDAP group " + 
                ldapGroup.ldapGroupName() + "," + groupSubTree() + " on " + displayName());
           }
        }
        
        boolean isMember;
        try
        {
            LDAPGroupRecord ldapRecord = groupNamed(ldapGroup.ldapGroupName());
            isMember = isMember(new NSArray(ldapRecord), ldapRecord.ldapName(this, aUser));
        }
        catch (EOObjectNotAvailableException e)
        {
            isMember = false;
        }
        catch (Throwable t)
        {
            // This is here in case the server cannot be contacted etc. so that the rest of the app
            // keeps functioning.
            DebugOut.println(1, "Failed to fetch LDAP Group because" + t.getMessage());
            t.printStackTrace();
            isMember = false;
        }

        if (isDebuggingEnabled())
        {
            stop = new NSTimestamp();
            DebugOut.println(1, "Checked user " + aUser.userID() + " in " + 
                ((stop.getTime()-start.getTime())/1000.0) + " seconds.");
                
            numAuthorizations++;
            totalAuthorizationTime += (stop.getTime()-start.getTime());
            DebugOut.println(1, "Performed " + numAuthorizations + " authorization, averaging " +
             (totalAuthorizationTime/1000.0/numAuthorizations) + " seconds.");
        }
        
        return isMember;
    }



    /**
     * Returns <code>true</code> if <code>ldapUserID</code> is a member of <code>ldapGroups</code> 
     * or any of their sub-groups.
     *  
     * @param ldapGroups list of LDAPGroupRecords to check membership in
     * @param ldapUserID the User ID, in the form the LDAP server uses, to test for membership in 
     * these groups
     * @return <code>true</code> if <code>ldapUserID</code> is a member of <code>ldapGroups</code> 
     * or any of their sub-groups
     */
    protected boolean isMember(NSArray ldapGroups, String ldapUserID)
    {
        /** require [valid_group] ldapGroups != null;
                    [valid_ldapUserID] ldapUserID != null;          **/
        
        boolean isMember = false;

        // Check for direct membership first as this is the fastest test
        for (int i = 0; i < ldapGroups.count() && ! isMember; i++)
        {
            LDAPGroupRecord aGroup = (LDAPGroupRecord)ldapGroups.objectAtIndex(i);
            isMember = aGroup.isDirectMember(ldapUserID);
            
            if (isDebuggingEnabled())
            {
                DebugOut.println(1, "   is " + ldapUserID + " a member of " +  aGroup.name() + "? " + isMember);
            }
        } 

        // If direct memebership fails, then bulk check the sub-groups
        // Sub-groups are fetched in a batch to avoid repeated trips to the server
        if ( ! isMember)
        {
            NSMutableArray subGroupQualifiers = new NSMutableArray();
            for (int groupIndex = 0; groupIndex < ldapGroups.count() && ! isMember; groupIndex++)
            {
                LDAPGroupRecord aGroup = (LDAPGroupRecord)ldapGroups.objectAtIndex(groupIndex);
                subGroupQualifiers.addObjectsFromArray(aGroup.qualifiersForSubGroups(groupSubTree()));
            }
                            
            if (subGroupQualifiers.count() > 0)
            {
                try
                {
                    EOFetchSpecification fetchSpec = new EOFetchSpecification(ldapEntityName(), new EOOrQualifier(subGroupQualifiers), NSArray.EmptyArray);
                    NSArray subGroups = editingContext().objectsWithFetchSpecification(fetchSpec);
                    isMember = isMember(subGroups, ldapUserID);
                }
                catch (Throwable t)
                {
                    // This is here in case the server cannot be contacted etc. so that the rest of the app
                    // keeps functioning.
                    DebugOut.println(1, "Failed to fetch LDAP Group because" + t.getMessage());
                    t.printStackTrace();
                    isMember = false;
                }
            }
        }
        
        return isMember;
    }



    /**
     * Returns the LDAP record for the group named <code>ldapGroupName</code> or throws 
     * EOObjectNotAvailableException is no such group exists in this LDAP branch.
     *  
     * @param ldapGroupName the name of the group to return
     * @exception EOObjectNotAvailableException is no group with this name exists
     * @return the LDAP record for the group named <code>ldapGroupName</code> 
     */
    public LDAPGroupRecord groupNamed(String ldapGroupName) throws EOObjectNotAvailableException
    {
        /** require [valid_name] ldapGroupName != null;  **/
        return (LDAPGroupRecord) EOUtilities.objectMatchingKeyAndValue(editingContext(), 
                                                                       ldapEntityName(), "name", 
                                                                       ldapGroupName);
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Returns <code>true</code> if a group named <code>ldapGroupName</code> exists in this LDAP branch.
     *  
     * @param ldapGroupName the name of the group whose existance is to be checked
     * @return <code>true</code> if a group named <code>ldapGroupName</code> exists in this LDAP branch
     */
    public boolean groupExists(String ldapGroupName)
    {
        /** require [valid_name] ldapGroupName != null;  **/
        
        boolean groupExists;

        try
        {
            groupNamed(ldapGroupName);
            groupExists = true;
        }
        catch (EOObjectNotAvailableException e)
        {
            groupExists = false;
        } 
        
        return groupExists;
    }
    
    
}

