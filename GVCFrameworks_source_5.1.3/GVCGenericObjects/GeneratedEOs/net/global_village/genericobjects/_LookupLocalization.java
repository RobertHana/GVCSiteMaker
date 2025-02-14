
package net.global_village.genericobjects;




/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to LookupLocalization.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */
public class _LookupLocalization extends net.global_village.eofextensions.GenericRecord
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

    public String localizedName()
    {
        return (String)storedValueForKey("localizedName");
    }



    public void setLocalizedName(String aValue)
    {
        takeStoredValueForKey(aValue, "localizedName");
    }




    public net.global_village.genericobjects.Locale locale()
    {
        return (net.global_village.genericobjects.Locale)storedValueForKey("locale");
    }



    public void setLocale(net.global_village.genericobjects.Locale aValue)
    {
        takeStoredValueForKey(aValue, "locale");
    }




    public net.global_village.genericobjects.Lookup lookup()
    {
        return (net.global_village.genericobjects.Lookup)storedValueForKey("lookup");
    }



    public void setLookup(net.global_village.genericobjects.Lookup aValue)
    {
        takeStoredValueForKey(aValue, "lookup");
    }



}
