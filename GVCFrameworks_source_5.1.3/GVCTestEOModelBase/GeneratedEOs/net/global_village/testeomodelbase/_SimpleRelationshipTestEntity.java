
package net.global_village.testeomodelbase;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to SimpleRelationshipTestEntity.java instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */  
public class _SimpleRelationshipTestEntity extends com.webobjects.eocontrol.EOGenericRecord 
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




    public net.global_village.testeomodelbase.ToOneEntity mandatoryToOne() 
    {
        return (net.global_village.testeomodelbase.ToOneEntity)storedValueForKey("mandatoryToOne");
    }



    public void setMandatoryToOne(net.global_village.testeomodelbase.ToOneEntity aValue) 
    {
        takeStoredValueForKey(aValue, "mandatoryToOne");
    }




    public net.global_village.testeomodelbase.ToOneEntity optionalToOne() 
    {
        return (net.global_village.testeomodelbase.ToOneEntity)storedValueForKey("optionalToOne");
    }



    public void setOptionalToOne(net.global_village.testeomodelbase.ToOneEntity aValue) 
    {
        takeStoredValueForKey(aValue, "optionalToOne");
    }




    public NSArray mandatoryToMany() 
    {
        return (NSArray)storedValueForKey("mandatoryToMany");
    }



    public void setMandatoryToMany(NSMutableArray aValue) 
    {
        takeStoredValueForKey(aValue, "mandatoryToMany");
    }



    public void addToMandatoryToMany(net.global_village.testeomodelbase.ToManyEntity object) 
    {
        NSMutableArray array = (NSMutableArray)mandatoryToMany();

        willChange();
        array.addObject(object);
    }



    public void removeFromMandatoryToMany(net.global_village.testeomodelbase.ToManyEntity object) 
    {
        NSMutableArray array = (NSMutableArray)mandatoryToMany();

        willChange();
        array.removeObject(object);
    }




    public NSArray optionalToMany() 
    {
        return (NSArray)storedValueForKey("optionalToMany");
    }



    public void setOptionalToMany(NSMutableArray aValue) 
    {
        takeStoredValueForKey(aValue, "optionalToMany");
    }



    public void addToOptionalToMany(net.global_village.testeomodelbase.ToManyEntity object) 
    {
        NSMutableArray array = (NSMutableArray)optionalToMany();

        willChange();
        array.addObject(object);
    }



    public void removeFromOptionalToMany(net.global_village.testeomodelbase.ToManyEntity object) 
    {
        NSMutableArray array = (NSMutableArray)optionalToMany();

        willChange();
        array.removeObject(object);
    }



}
