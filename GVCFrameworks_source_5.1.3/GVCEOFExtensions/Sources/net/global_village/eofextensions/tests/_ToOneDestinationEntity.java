
package net.global_village.eofextensions.tests;




/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to ToOneDestinationEntity.java instead.
 *
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 2$
 */  
public class _ToOneDestinationEntity extends net.global_village.eofextensions.GenericRecord 
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



}
