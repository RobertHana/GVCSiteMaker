
package net.global_village.security;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to User.java instead.
 *
 * @author Copyright (c) 2006 Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 6$
 */
public class _User extends net.global_village.eofextensions.GenericRecord
{

/*
    // If you add instance variables to store property values you
    // should add empty implementions of the Serialization methods
    // to avoid unnecessary overhead (the properties will be
    // serialized for you in the superclass).
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException
    {
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException
    {
    }
*/

    public String userName()
    {
        return (String)storedValueForKey("userName");
    }



    public void setUserName(String aValue)
    {
        takeStoredValueForKey(aValue, "userName");
    }




    public String password()
    {
        return (String)storedValueForKey("password");
    }



    public void setPassword(String aValue)
    {
        takeStoredValueForKey(aValue, "password");
    }




    public net.global_village.foundation.GVCBoolean mustChangePassword()
    {
        return (net.global_village.foundation.GVCBoolean)storedValueForKey("mustChangePassword");
    }



    public void setMustChangePassword(net.global_village.foundation.GVCBoolean aValue)
    {
        takeStoredValueForKey(aValue, "mustChangePassword");
    }




    public net.global_village.foundation.GVCBoolean canChangePassword()
    {
        return (net.global_village.foundation.GVCBoolean)storedValueForKey("canChangePassword");
    }



    public void setCanChangePassword(net.global_village.foundation.GVCBoolean aValue)
    {
        takeStoredValueForKey(aValue, "canChangePassword");
    }




    public net.global_village.foundation.GVCBoolean isAccountDisabled()
    {
        return (net.global_village.foundation.GVCBoolean)storedValueForKey("isAccountDisabled");
    }



    public void setIsAccountDisabled(net.global_village.foundation.GVCBoolean aValue)
    {
        takeStoredValueForKey(aValue, "isAccountDisabled");
    }




    public NSArray groups()
    {
        return (NSArray)storedValueForKey("groups");
    }



    public void setGroups(NSMutableArray aValue)
    {
        takeStoredValueForKey(aValue, "groups");
    }



    public void addToGroups(net.global_village.security.UserGroup object)
    {
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.addObject(object);
    }



    public void removeFromGroups(net.global_village.security.UserGroup object)
    {
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.removeObject(object);
    }



}
