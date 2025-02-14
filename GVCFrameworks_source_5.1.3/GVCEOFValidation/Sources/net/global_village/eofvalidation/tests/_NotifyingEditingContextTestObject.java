
package net.global_village.eofvalidation.tests;




/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to NotifyingEditingContextTestObject.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */  
public class _NotifyingEditingContextTestObject extends net.global_village.eofvalidation.EOGenericNotificationRecord 
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

    public String stringValue() 
    {
        return (String)storedValueForKey("stringValue");
    }



    public void setStringValue(String aValue) 
    {
        takeStoredValueForKey(aValue, "stringValue");
    }




    public net.global_village.eofvalidation.tests.NotifyingEditingContextTestObject relatedObject() 
    {
        return (net.global_village.eofvalidation.tests.NotifyingEditingContextTestObject)storedValueForKey("relatedObject");
    }



    public void setRelatedObject(net.global_village.eofvalidation.tests.NotifyingEditingContextTestObject aValue) 
    {
        takeStoredValueForKey(aValue, "relatedObject");
    }



}
