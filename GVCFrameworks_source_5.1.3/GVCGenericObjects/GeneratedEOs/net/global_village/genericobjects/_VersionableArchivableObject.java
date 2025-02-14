
package net.global_village.genericobjects;




/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to VersionableArchivableObject.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class _VersionableArchivableObject extends net.global_village.eofextensions.GenericRecord
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

    public net.global_village.foundation.GVCBoolean isActive()
    {
        return (net.global_village.foundation.GVCBoolean)storedValueForKey("isActive");
    }



    public void setIsActive(net.global_village.foundation.GVCBoolean aValue)
    {
        takeStoredValueForKey(aValue, "isActive");
    }



}
