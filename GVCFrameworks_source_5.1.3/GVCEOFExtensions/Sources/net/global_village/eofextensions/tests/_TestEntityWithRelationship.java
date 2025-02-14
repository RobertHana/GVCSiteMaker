
package net.global_village.eofextensions.tests;


import com.webobjects.foundation.*;


/**
 * Created by eogenerator
 * DO NOT EDIT.  Make changes to TestEntityWithRelationship.java instead.
 *
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 2$
 */  
public class _TestEntityWithRelationship extends net.global_village.eofextensions.GenericRecord 
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




    public net.global_village.eofextensions.tests.ToOneDestinationEntity mandatoryToOne() 
    {
        return (net.global_village.eofextensions.tests.ToOneDestinationEntity)storedValueForKey("mandatoryToOne");
    }



    public void setMandatoryToOne(net.global_village.eofextensions.tests.ToOneDestinationEntity aValue) 
    {
        takeStoredValueForKey(aValue, "mandatoryToOne");
    }




    public net.global_village.eofextensions.tests.ToOneDestinationEntity optionalToOne() 
    {
        return (net.global_village.eofextensions.tests.ToOneDestinationEntity)storedValueForKey("optionalToOne");
    }



    public void setOptionalToOne(net.global_village.eofextensions.tests.ToOneDestinationEntity aValue) 
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



    public void addToMandatoryToMany(net.global_village.eofextensions.tests.ToManyDestinationEntity object) 
    {
        NSMutableArray array = (NSMutableArray)mandatoryToMany();

        willChange();
        array.addObject(object);
    }



    public void removeFromMandatoryToMany(net.global_village.eofextensions.tests.ToManyDestinationEntity object) 
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



    public void addToOptionalToMany(net.global_village.eofextensions.tests.ToManyDestinationEntity object) 
    {
        NSMutableArray array = (NSMutableArray)optionalToMany();

        willChange();
        array.addObject(object);
    }



    public void removeFromOptionalToMany(net.global_village.eofextensions.tests.ToManyDestinationEntity object) 
    {
        NSMutableArray array = (NSMutableArray)optionalToMany();

        willChange();
        array.removeObject(object);
    }



}
