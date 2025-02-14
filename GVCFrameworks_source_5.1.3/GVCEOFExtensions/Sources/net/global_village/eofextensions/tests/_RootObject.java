
package net.global_village.eofextensions.tests;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to RootObject.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */  
public class _RootObject extends net.global_village.eofextensions.CopyableGenericRecord 
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




    public Number oid() 
    {
        return (Number)storedValueForKey("oid");
    }



    public void setOid(Number aValue) 
    {
        takeStoredValueForKey(aValue, "oid");
    }




    public Number aValue() 
    {
        return (Number)storedValueForKey("aValue");
    }



    public void setAValue(Number aValue) 
    {
        takeStoredValueForKey(aValue, "aValue");
    }




    public NSArray ownedObjects() 
    {
        return (NSArray)storedValueForKey("ownedObjects");
    }



    public void setOwnedObjects(NSMutableArray aValue) 
    {
        takeStoredValueForKey(aValue, "ownedObjects");
    }



    public void addToOwnedObjects(net.global_village.eofextensions.tests.OwnedObject object) 
    {
        NSMutableArray array = (NSMutableArray)ownedObjects();

        willChange();
        array.addObject(object);
    }



    public void removeFromOwnedObjects(net.global_village.eofextensions.tests.OwnedObject object) 
    {
        NSMutableArray array = (NSMutableArray)ownedObjects();

        willChange();
        array.removeObject(object);
    }




    public NSArray unOwnedObjects() 
    {
        return (NSArray)storedValueForKey("unOwnedObjects");
    }



    public void setUnOwnedObjects(NSMutableArray aValue) 
    {
        takeStoredValueForKey(aValue, "unOwnedObjects");
    }



    public void addToUnOwnedObjects(net.global_village.eofextensions.tests.UnOwnedObject object) 
    {
        NSMutableArray array = (NSMutableArray)unOwnedObjects();

        willChange();
        array.addObject(object);
    }



    public void removeFromUnOwnedObjects(net.global_village.eofextensions.tests.UnOwnedObject object) 
    {
        NSMutableArray array = (NSMutableArray)unOwnedObjects();

        willChange();
        array.removeObject(object);
    }



}
