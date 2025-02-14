
package net.global_village.testeomodelbase;


import java.math.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to EntityWithConstraints.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */  
public class _EntityWithConstraints extends com.webobjects.eocontrol.EOGenericRecord 
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

    public BigDecimal quantity() 
    {
        return (BigDecimal)storedValueForKey("quantity");
    }



    public void setQuantity(BigDecimal aValue) 
    {
        takeStoredValueForKey(aValue, "quantity");
    }



}
