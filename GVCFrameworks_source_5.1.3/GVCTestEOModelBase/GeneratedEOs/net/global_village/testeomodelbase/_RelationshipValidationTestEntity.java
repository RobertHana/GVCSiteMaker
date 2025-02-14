
package net.global_village.testeomodelbase;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to RelationshipValidationTestEntity.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */  
public class _RelationshipValidationTestEntity extends com.webobjects.eocontrol.EOGenericRecord 
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

    public Number theID() 
    {
        return (Number)storedValueForKey("theID");
    }



    public void setTheID(Number aValue) 
    {
        takeStoredValueForKey(aValue, "theID");
    }




    public net.global_village.testeomodelbase.ValidationBareEntity requiredBareEntity() 
    {
        return (net.global_village.testeomodelbase.ValidationBareEntity)storedValueForKey("requiredBareEntity");
    }



    public void setRequiredBareEntity(net.global_village.testeomodelbase.ValidationBareEntity aValue) 
    {
        takeStoredValueForKey(aValue, "requiredBareEntity");
    }




    public net.global_village.testeomodelbase.EntityWithDecimalPK optionalDecimalEntity() 
    {
        return (net.global_village.testeomodelbase.EntityWithDecimalPK)storedValueForKey("optionalDecimalEntity");
    }



    public void setOptionalDecimalEntity(net.global_village.testeomodelbase.EntityWithDecimalPK aValue) 
    {
        takeStoredValueForKey(aValue, "optionalDecimalEntity");
    }




    public NSArray requiredBareEntities() 
    {
        return (NSArray)storedValueForKey("requiredBareEntities");
    }



    public void setRequiredBareEntities(NSMutableArray aValue) 
    {
        takeStoredValueForKey(aValue, "requiredBareEntities");
    }



    public void addToRequiredBareEntities(net.global_village.testeomodelbase.ValidationBareEntity object) 
    {
        NSMutableArray array = (NSMutableArray)requiredBareEntities();

        willChange();
        array.addObject(object);
    }



    public void removeFromRequiredBareEntities(net.global_village.testeomodelbase.ValidationBareEntity object) 
    {
        NSMutableArray array = (NSMutableArray)requiredBareEntities();

        willChange();
        array.removeObject(object);
    }




    public NSArray optionalDecimalEntities() 
    {
        return (NSArray)storedValueForKey("optionalDecimalEntities");
    }



    public void setOptionalDecimalEntities(NSMutableArray aValue) 
    {
        takeStoredValueForKey(aValue, "optionalDecimalEntities");
    }



    public void addToOptionalDecimalEntities(net.global_village.testeomodelbase.EntityWithDecimalPK object) 
    {
        NSMutableArray array = (NSMutableArray)optionalDecimalEntities();

        willChange();
        array.addObject(object);
    }



    public void removeFromOptionalDecimalEntities(net.global_village.testeomodelbase.EntityWithDecimalPK object) 
    {
        NSMutableArray array = (NSMutableArray)optionalDecimalEntities();

        willChange();
        array.removeObject(object);
    }



}
