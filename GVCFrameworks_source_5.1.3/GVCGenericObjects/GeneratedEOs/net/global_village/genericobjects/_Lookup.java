
package net.global_village.genericobjects;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to Lookup.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class _Lookup extends net.global_village.eofextensions.GenericRecord
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




    public NSArray localizations()
    {
        return (NSArray)storedValueForKey("localizations");
    }



    public void setLocalizations(NSMutableArray aValue)
    {
        takeStoredValueForKey(aValue, "localizations");
    }



    public void addToLocalizations(net.global_village.genericobjects.LookupLocalization object)
    {
        NSMutableArray array = (NSMutableArray)localizations();

        willChange();
        array.addObject(object);
    }



    public void removeFromLocalizations(net.global_village.genericobjects.LookupLocalization object)
    {
        NSMutableArray array = (NSMutableArray)localizations();

        willChange();
        array.removeObject(object);
    }



}
