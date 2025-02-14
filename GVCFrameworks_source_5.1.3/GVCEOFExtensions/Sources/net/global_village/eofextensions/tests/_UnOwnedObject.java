
package net.global_village.eofextensions.tests;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to UnOwnedObject.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */  
public class _UnOwnedObject extends net.global_village.eofextensions.CopyableGenericRecord 
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




    public NSArray rootObjects() 
    {
        return (NSArray)storedValueForKey("rootObjects");
    }



    public void setRootObjects(NSMutableArray aValue) 
    {
        takeStoredValueForKey(aValue, "rootObjects");
    }



    public void addToRootObjects(net.global_village.eofextensions.tests.RootObject object) 
    {
        NSMutableArray array = (NSMutableArray)rootObjects();

        willChange();
        array.addObject(object);
    }



    public void removeFromRootObjects(net.global_village.eofextensions.tests.RootObject object) 
    {
        NSMutableArray array = (NSMutableArray)rootObjects();

        willChange();
        array.removeObject(object);
    }



}
