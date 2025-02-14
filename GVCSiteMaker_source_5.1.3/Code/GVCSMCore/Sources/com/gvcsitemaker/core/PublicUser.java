// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;


public class PublicUser extends _PublicUser
{

    protected static EOGlobalID publicUserGID = null;
    
    
    /**
     * Returns the userID of the Public (a.k.a. unauthenticated) User.
     *
     * @return the userID of the Public (a.k.a. unauthenticated) User
     */
    public static String publicUserID()
    {
        return SMApplication.appProperties().stringPropertyForKey("PublicUserID");
        /** ensure [valid_result] ! StringAdditions.isEmpty(Result); **/
    }  
    
    
    /**
     * Returns the Public (a.k.a. unauthenticated) User in the passed editing context.
     *
     * @return the Public (a.k.a. unauthenticated) User in the passed editing context.
     */
    public static PublicUser publicUser(EOEditingContext editingContext)
    {
        /** require [editing_context_not_null] editingContext != null; **/
        
        if (publicUserGID == null) 
        {
            PublicUser publicUser = (PublicUser)EOUtilities.
                objectMatchingKeyAndValue(editingContext, "PublicUser", "userID", publicUserID());
            publicUserGID = publicUser.globalID();
        }

        return (PublicUser) editingContext.faultForGlobalID(publicUserGID, editingContext);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of PublicUser.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of PublicUser or a subclass.
     */
    public static PublicUser newPublicUser()
    {
        return (PublicUser) SMEOUtils.newInstanceOf("PublicUser");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Factory method to create new instances of User given the userID, inserted into an editing context.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of User or a subclass.
     */
    public static PublicUser newPublicUser(EOEditingContext ec )
    {
        /** require [ec_not_null] ec != null;  **/
        
        PublicUser newPublicUser = newPublicUser();
        ec.insertObject( newPublicUser );
        newPublicUser.setUserID( publicUserID() ); 

        JassAdditions.post("User", "newUser()", newPublicUser.userID().equals(publicUserID() ));
        JassAdditions.post("User", "newUser()", ec.insertedObjects().containsObject(newPublicUser));
        
        return newPublicUser;

        /** ensure [result_not_null] Result != null; **/
     }
    
    
    
    public static boolean isPublicUserID(String aUserID)
    {
        return aUserID.equalsIgnoreCase(publicUserID());
    }

    



    /**
     * Returns <code>true</code> if this user is the Public user.
     *
     * @return <code>true</code> if this user is the Public user.
     */
    public boolean isPublicUser() 
    {
        return true;
    }
}

