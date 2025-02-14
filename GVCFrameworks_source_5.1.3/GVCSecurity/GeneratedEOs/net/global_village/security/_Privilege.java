
package net.global_village.security;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to Privilege.java instead.
 *
 * @author Copyright (c) 2006 Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 6$
 */
public class _Privilege extends net.global_village.eofextensions.GenericRecord
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

    public String name()
    {
        return (String)storedValueForKey("name");
    }



    public void setName(String aValue)
    {
        takeStoredValueForKey(aValue, "name");
    }




    public NSArray includedPrivileges()
    {
        return (NSArray)storedValueForKey("includedPrivileges");
    }



    public void setIncludedPrivileges(NSMutableArray aValue)
    {
        takeStoredValueForKey(aValue, "includedPrivileges");
    }



    public void addToIncludedPrivileges(net.global_village.security.Privilege object)
    {
        NSMutableArray array = (NSMutableArray)includedPrivileges();

        willChange();
        array.addObject(object);
    }



    public void removeFromIncludedPrivileges(net.global_village.security.Privilege object)
    {
        NSMutableArray array = (NSMutableArray)includedPrivileges();

        willChange();
        array.removeObject(object);
    }



}
