
package net.global_village.testeomodelbase;


import java.math.*;

import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to AttributeValidationTestEntity.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */  
public class _AttributeValidationTestEntity extends com.webobjects.eocontrol.EOGenericRecord 
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

    public String requiredString() 
    {
        return (String)storedValueForKey("requiredString");
    }



    public void setRequiredString(String aValue) 
    {
        takeStoredValueForKey(aValue, "requiredString");
    }




    public String requiredMemo() 
    {
        return (String)storedValueForKey("requiredMemo");
    }



    public void setRequiredMemo(String aValue) 
    {
        takeStoredValueForKey(aValue, "requiredMemo");
    }




    public NSTimestamp requiredTimestamp() 
    {
        return (NSTimestamp)storedValueForKey("requiredTimestamp");
    }



    public void setRequiredTimestamp(NSTimestamp aValue) 
    {
        takeStoredValueForKey(aValue, "requiredTimestamp");
    }




    public Number requiredInteger() 
    {
        return (Number)storedValueForKey("requiredInteger");
    }



    public void setRequiredInteger(Number aValue) 
    {
        takeStoredValueForKey(aValue, "requiredInteger");
    }




    public BigDecimal requiredDecimalNumber() 
    {
        return (BigDecimal)storedValueForKey("requiredDecimalNumber");
    }



    public void setRequiredDecimalNumber(BigDecimal aValue) 
    {
        takeStoredValueForKey(aValue, "requiredDecimalNumber");
    }




    public String optionalString() 
    {
        return (String)storedValueForKey("optionalString");
    }



    public void setOptionalString(String aValue) 
    {
        takeStoredValueForKey(aValue, "optionalString");
    }




    public String optionalMemo() 
    {
        return (String)storedValueForKey("optionalMemo");
    }



    public void setOptionalMemo(String aValue) 
    {
        takeStoredValueForKey(aValue, "optionalMemo");
    }




    public NSTimestamp optionalTimestamp() 
    {
        return (NSTimestamp)storedValueForKey("optionalTimestamp");
    }



    public void setOptionalTimestamp(NSTimestamp aValue) 
    {
        takeStoredValueForKey(aValue, "optionalTimestamp");
    }




    public NSData requiredData() 
    {
        return (NSData)storedValueForKey("requiredData");
    }



    public void setRequiredData(NSData aValue) 
    {
        takeStoredValueForKey(aValue, "requiredData");
    }




    public Number requiredDouble() 
    {
        return (Number)storedValueForKey("requiredDouble");
    }



    public void setRequiredDouble(Number aValue) 
    {
        takeStoredValueForKey(aValue, "requiredDouble");
    }




    public Number optionalDouble() 
    {
        return (Number)storedValueForKey("optionalDouble");
    }



    public void setOptionalDouble(Number aValue) 
    {
        takeStoredValueForKey(aValue, "optionalDouble");
    }




    public BigDecimal optionalDecimalNumber() 
    {
        return (BigDecimal)storedValueForKey("optionalDecimalNumber");
    }



    public void setOptionalDecimalNumber(BigDecimal aValue) 
    {
        takeStoredValueForKey(aValue, "optionalDecimalNumber");
    }




    public Number optionalInteger() 
    {
        return (Number)storedValueForKey("optionalInteger");
    }



    public void setOptionalInteger(Number aValue) 
    {
        takeStoredValueForKey(aValue, "optionalInteger");
    }




    public net.global_village.foundation.GVCBoolean requiredBoolean() 
    {
        return (net.global_village.foundation.GVCBoolean)storedValueForKey("requiredBoolean");
    }



    public void setRequiredBoolean(net.global_village.foundation.GVCBoolean aValue) 
    {
        takeStoredValueForKey(aValue, "requiredBoolean");
    }




    public net.global_village.foundation.GVCBoolean optionalBoolean() 
    {
        return (net.global_village.foundation.GVCBoolean)storedValueForKey("optionalBoolean");
    }



    public void setOptionalBoolean(net.global_village.foundation.GVCBoolean aValue) 
    {
        takeStoredValueForKey(aValue, "optionalBoolean");
    }




    public Number theID() 
    {
        return (Number)storedValueForKey("theID");
    }



    public void setTheID(Number aValue) 
    {
        takeStoredValueForKey(aValue, "theID");
    }




    public NSData shortData() 
    {
        return (NSData)storedValueForKey("shortData");
    }



    public void setShortData(NSData aValue) 
    {
        takeStoredValueForKey(aValue, "shortData");
    }




    public String shortString() 
    {
        return (String)storedValueForKey("shortString");
    }



    public void setShortString(String aValue) 
    {
        takeStoredValueForKey(aValue, "shortString");
    }



}
