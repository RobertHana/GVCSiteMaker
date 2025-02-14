package net.global_village.genericobjects;


import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.util.*;
import java.math.BigDecimal;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to KeyedLookup.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */  
public class _OrderedKeyedLookup extends net.global_village.genericobjects.KeyedLookup 
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

    public static final String ORDERING = "ordering";
    public static final String LOCALIZATIONS = "localizations";
    public static final String KEY = "key";
    public static final String NAME = "name";



    public Number ordering() 
    {
        return (Number)storedValueForKey("ordering");
    }



    public void setOrdering(Number aValue) 
    {
        takeStoredValueForKey(aValue, "ordering");
    }



}
