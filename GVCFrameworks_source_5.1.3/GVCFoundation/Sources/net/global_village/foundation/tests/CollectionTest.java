package net.global_village.foundation.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import junit.framework.TestCase;
import net.global_village.foundation.Collection;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSSet;


/*
 * Test the Class functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class CollectionTest extends TestCase
{
    Vector aVector, anotherVector;
    NSArray anArray;
    Hashtable aHashtable;
    Stack aStack;
    NSArray sourceObjects;
    NSArray notObjects;
    NSArray stringNumbers;
    NSArray hetrogeneousArray;


    public CollectionTest(String name)
    {
        super(name);
    }



    /**
     * Sets up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        aVector = new Vector();
        aVector.addElement("a string");
        aVector.addElement(this);

        anArray = new NSArray(this);

        aHashtable = new Hashtable();
        aHashtable.put("string", "the string");

        aStack = new Stack();
        aStack.push(Boolean.TRUE);
        aStack.push(Boolean.TRUE);

        anotherVector = new Vector();
        anotherVector.addElement(Boolean.TRUE);
        anotherVector.addElement(Boolean.FALSE);

        sourceObjects = new NSArray(new Object[] {"test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8"});
        notObjects = new NSArray(new Object[] {"test3", "test6", "test9"});

        stringNumbers = new NSArray(new Object[] {"4", "99", "12", "123"});

        hetrogeneousArray = new NSArray(new Object[] {new Integer(4), Boolean.FALSE, "12", "123"});
    }



    /**
     * Test enumerationForCollection().
     */
    public void testEnumerationForCollection()
    {
        Enumeration vectorEnumerator = Collection.enumerationForCollection(aVector);
        assertTrue(vectorEnumerator != null);
        assertTrue(vectorEnumerator.nextElement().equals("a string"));
        assertTrue(vectorEnumerator.nextElement() == this);
        assertTrue( ! vectorEnumerator.hasMoreElements());

        Enumeration arrayEnumerator = Collection.enumerationForCollection(anArray);
        assertTrue(arrayEnumerator != null);
        assertTrue(arrayEnumerator.nextElement().equals(this));
        assertTrue( ! arrayEnumerator.hasMoreElements());

        Enumeration hashtableEnumerator = Collection.enumerationForCollection(aHashtable);
        assertTrue(hashtableEnumerator != null);
        assertTrue(hashtableEnumerator.nextElement().equals("the string"));
        assertTrue( ! hashtableEnumerator.hasMoreElements());

        Enumeration stackEnumerator = Collection.enumerationForCollection(aStack);
        assertTrue(stackEnumerator != null);
        assertTrue(stackEnumerator.nextElement().equals(Boolean.TRUE));
        assertTrue(stackEnumerator.nextElement().equals(Boolean.TRUE));
        assertTrue( ! stackEnumerator.hasMoreElements());

        // Special case for empty NSArray
        Enumeration emptyEnumerator = Collection.enumerationForCollection(NSArray.EmptyArray);
        assertTrue(emptyEnumerator != null);
        assertTrue( ! emptyEnumerator.hasMoreElements());

        try
        {
            Collection.enumerationForCollection("test");
            fail("Expected exception did not occur");
        }
        catch (RuntimeException e) { }
    }



    /**
     * Test collectionToNSMutableArray().
     */
    public void testCollectionToNSMutableArray()
    {
        NSMutableArray theArray = Collection.collectionToNSMutableArray(aStack);
        Enumeration stackEnumerator = Collection.enumerationForCollection(aStack);
        assertTrue(theArray.count() == 2);
        assertTrue(theArray.containsObject(stackEnumerator.nextElement()));
        assertTrue(theArray.containsObject(stackEnumerator.nextElement()));
    }



    /**
     * Test collectionToNSMutableSet().
     */
    public void testCollectionToNSMutableSet()
    {
        NSMutableSet theSet = Collection.collectionToNSMutableSet(aStack);
        Enumeration stackEnumerator = Collection.enumerationForCollection(aStack);
        assertTrue(theSet.count() == 1);  // Only 1 item because both items in aStack are TRUE booleans
        assertTrue(theSet.containsObject(stackEnumerator.nextElement()));
    }



    /**
     * Returns a pathname after copying the resource to a temporary file.
     */
    protected String makeTempFileWithResource(String resourceName)
    {
        NSBundle localBundle = NSBundle.bundleForClass(getClass());
        String tablePath = localBundle.resourcePathForLocalizedResourceNamed(resourceName, null);
        byte[] tableBytes = localBundle.bytesForResourcePath(tablePath);
        String tableString = new String(tableBytes);

        File tableFile;
        try
        {
            tableFile = File.createTempFile(resourceName, null);
            tableFile.deleteOnExit();
            FileWriter fw = new FileWriter(tableFile);
            fw.write(tableString);
            fw.close();
        }
        catch (IOException exception)
        {
            throw new Error("Unexpected exception!");
        }
        
        return tableFile.getPath();
    }


    /**
     * Test collectionWithContentsOfFile().
     */
    public void testCollectionWithContentsOfFile()
    {
        String dictionaryPath = makeTempFileWithResource("Dictionary.plist");
        String arrayPath = makeTempFileWithResource("Array.plist");

        try
        {
            NSDictionary myDictionary = (NSDictionary) Collection.collectionWithContentsOfFile(dictionaryPath);
            assertTrue(myDictionary.objectForKey("testString").equals("A test string"));
            assertTrue(myDictionary.objectForKey("testInteger").equals("7"));
    
            NSArray myArray = (NSArray) Collection.collectionWithContentsOfFile(arrayPath);
            assertTrue(myArray.objectAtIndex(2).equals("Sacha"));
            assertTrue(myArray.objectAtIndex(3).equals("2.25"));
        }
        catch (java.io.IOException exception)
        {
            assertTrue(false);
        }
    }



    /**
     * Test collectionContainsInstancesOf().
     */
    public void testCollectionContainsInstancesOf()
    {
        assertTrue(Collection.collectionContainsInstancesOf(aHashtable, String.class));
        assertTrue(Collection.collectionContainsInstancesOf(anArray, CollectionTest.class));

        assertTrue( ! Collection.collectionContainsInstancesOf(aVector, String.class));
        assertTrue( ! Collection.collectionContainsInstancesOf(aVector, CollectionTest.class));
    }



    /**
     * Test collectionContainsInstancesRespondingTo()
     */
    public void testCollectionContainsInstancesRespondingTo()
    {
        NSSelector booleanSelector = new NSSelector("booleanValue", new Class[] {});
        NSSelector stringSelector = new NSSelector("length", new Class[] {});

        assertTrue(Collection.collectionContainsInstancesRespondingTo(anotherVector, booleanSelector));
        assertTrue(Collection.collectionContainsInstancesRespondingTo(aHashtable, stringSelector));

        assertTrue( ! Collection.collectionContainsInstancesRespondingTo(aVector, booleanSelector));
        assertTrue( ! Collection.collectionContainsInstancesRespondingTo(aVector, stringSelector));
    }



    /**
     * Test doAnyObjectsRespond().
     */
    public void testDoAnyObjectsRespond()
    {
        NSSelector booleanSelector = new NSSelector("booleanValue", new Class[] {});
        NSSelector stringSelector = new NSSelector("length", new Class[] {});

        try
        {
            assertTrue(Collection.doAnyObjectsRespond(aStack, Boolean.TRUE, booleanSelector, null));
            assertTrue( ! Collection.doAnyObjectsRespond(aStack, Boolean.FALSE, booleanSelector, null));
            assertTrue(Collection.doAnyObjectsRespond(anotherVector, Boolean.TRUE, booleanSelector, null));

            assertTrue(Collection.doAnyObjectsRespond(aHashtable, new Integer(10), stringSelector, null));
        }
        catch (IllegalAccessException e) { fail("IllegalAccessException"); }
        catch (java.lang.reflect.InvocationTargetException e) { fail("InvocationTargetException"); }

        try
        {
            assertTrue(Collection.doAnyObjectsRespond(aVector, new Integer(8), stringSelector, null));
            fail("expected DBC failure did not occur");
        }
        catch (RuntimeException e) { }
        catch (IllegalAccessException e) { fail("IllegalAccessException"); }
        catch (java.lang.reflect.InvocationTargetException e) { fail("InvocationTargetException"); }


        NSSelector booleanSelectorWithOneArg = new NSSelector("equals", new Class[] {Object.class});
        NSSelector stringSelectorWithOneArg = new NSSelector("indexOf", new Class[] {String.class});

        try
        {
            assertTrue(Collection.doAnyObjectsRespond(aHashtable,
                                                         new Integer(4),
                                                         stringSelectorWithOneArg,
                                                         new Object[] {"string"}));
            assertTrue(Collection.doAnyObjectsRespond(aVector,
                                                         Boolean.TRUE,
                                                         booleanSelectorWithOneArg,
                                                         new Object[] {this}));
            assertTrue( ! Collection.doAnyObjectsRespond(aStack,
                                                            Boolean.TRUE,
                                                            booleanSelectorWithOneArg,
                                                            new Object[] {this}));
        }
        catch (IllegalAccessException e) { fail("IllegalAccessException"); }
        catch (java.lang.reflect.InvocationTargetException e) { fail("InvocationTargetException"); }
    }



    /**
     * Test doAllObjectsRespond().
     */
    public void testDoAllObjectsRespond()
    {
        NSSelector booleanSelector = new NSSelector("booleanValue", new Class[] {});
        NSSelector stringSelector = new NSSelector("length", new Class[] {});

        try
        {
            assertTrue(Collection.doAllObjectsRespond(aStack, Boolean.TRUE, booleanSelector, null));
            assertTrue( ! Collection.doAllObjectsRespond(aStack, Boolean.FALSE, booleanSelector, null));
            assertTrue( ! Collection.doAllObjectsRespond(anotherVector, Boolean.TRUE, booleanSelector, null));
            assertTrue( ! Collection.doAllObjectsRespond(anotherVector, Boolean.FALSE, booleanSelector, null));

            assertTrue(Collection.doAllObjectsRespond(aHashtable, new Integer(10), stringSelector, null));
        }
        catch (IllegalAccessException e) { fail("IllegalAccessException"); }
        catch (java.lang.reflect.InvocationTargetException e) { fail("InvocationTargetException"); }
    }



    /**
     * Test objectsRespondingTo().
     */
    public void testObjectsRespondingTo()
    {
        NSArray resultArray;
        NSSelector selector = new NSSelector("length", new Class[] {});

        resultArray = Collection.objectsRespondingTo(hetrogeneousArray,
                                                     selector);
        assertTrue(resultArray.count() == 2);
        assertTrue(resultArray.objectAtIndex(0).equals("12"));
        assertTrue(resultArray.objectAtIndex(1).equals("123"));
    }



    /**
     * Test objectsResponding().
     */
    public void testObjectsResponding()
    {
        NSArray resultArray;
        NSSelector booleanSelectorWithOneArg = new NSSelector("equals", new Class[] {Object.class});
        NSSelector stringSelectorWithOneArg = new NSSelector("indexOf", new Class[] {String.class});

        try
        {
            resultArray = Collection.objectsResponding(aHashtable,
                                                       new Integer(4),
                                                       stringSelectorWithOneArg,
                                                       new Object[] {"string"});
            assertTrue(resultArray.count() == 1);
            assertTrue(resultArray.objectAtIndex(0).equals("the string"));

            resultArray = Collection.objectsResponding(anotherVector,
                                                       Boolean.TRUE,
                                                       booleanSelectorWithOneArg,
                                                       new Object[] {Boolean.FALSE});
            assertTrue(resultArray.count() == 1);
            assertTrue(resultArray.objectAtIndex(0).equals(Boolean.FALSE));
        }
        catch (IllegalAccessException e) { fail("IllegalAccessException"); }
        catch (java.lang.reflect.InvocationTargetException e) { fail("InvocationTargetException"); }
    }



    /**
     * Test convertCollectionStringElements().
     */
    public void testConvertCollectionStringElements()
    {
        NSArray intNumbers = null;

        try {
            intNumbers = Collection.convertCollectionStringElements(stringNumbers, Integer.class.getConstructor(new Class[] {String.class}));
        }
        catch (java.lang.Exception e) { fail(e.toString()); }

        assertTrue(Collection.collectionContainsInstancesOf(stringNumbers, String.class));
        assertTrue(Collection.collectionContainsInstancesOf(intNumbers, Integer.class));

        assertEquals(((Integer)intNumbers.objectAtIndex(0)).intValue(), 4);
        assertEquals(((Integer)intNumbers.objectAtIndex(1)).intValue(), 99);
        assertEquals(((Integer)intNumbers.objectAtIndex(2)).intValue(), 12);
        assertEquals(((Integer)intNumbers.objectAtIndex(3)).intValue(), 123);
    }



    /**
     * Test randomObjects().
     */
    public void testRandomObjects()
    {
        NSArray result = Collection.randomObjects(sourceObjects, notObjects, 4);

        NSSet sourceSet = Collection.collectionToNSMutableSet(sourceObjects);
        NSSet notSet = Collection.collectionToNSMutableSet(notObjects);
        NSSet resultSet = Collection.collectionToNSMutableSet(result);

        assertTrue(result.count() == 4);
        assertTrue(resultSet.isSubsetOfSet(sourceSet));
        assertTrue( ! resultSet.intersectsSet(notSet));
    }



    /**
     * Test firstObjects().
     */
    public void testFirstObjects()
    {
        NSArray result = Collection.firstObjects(sourceObjects, notObjects, 4);

        NSSet sourceSet = Collection.collectionToNSMutableSet(sourceObjects);
        NSSet notSet = Collection.collectionToNSMutableSet(notObjects);
        NSSet resultSet = Collection.collectionToNSMutableSet(result);

        assertTrue(result.count() == 4);
        assertTrue(resultSet.isSubsetOfSet(sourceSet));
        assertTrue( ! resultSet.intersectsSet(notSet));

        Enumeration resultEnumerator = Collection.enumerationForCollection(result);
        assertTrue(resultEnumerator.nextElement().equals("test1"));
        assertTrue(resultEnumerator.nextElement().equals("test2"));
        assertTrue(resultEnumerator.nextElement().equals("test4"));
        assertTrue(resultEnumerator.nextElement().equals("test5"));
        assertTrue( ! resultEnumerator.hasMoreElements());
    }



    /**
     * Test divideObjects().
     */
    public void testDivideObjects()
    {
        NSArray result = Collection.divideObjects(sourceObjects, notObjects, 4);
        assertTrue(result.count() == 2);

        assertTrue(((NSArray)result.objectAtIndex(0)).count() == 4);
        assertTrue(((NSArray)result.objectAtIndex(0)).objectAtIndex(0).equals("test1"));
        assertTrue(((NSArray)result.objectAtIndex(0)).objectAtIndex(1).equals("test2"));
        assertTrue(((NSArray)result.objectAtIndex(0)).objectAtIndex(2).equals("test4"));
        assertTrue(((NSArray)result.objectAtIndex(0)).objectAtIndex(3).equals("test5"));
        assertTrue(((NSArray)result.objectAtIndex(1)).count() == 2);
        assertTrue(((NSArray)result.objectAtIndex(1)).objectAtIndex(0).equals("test7"));
        assertTrue(((NSArray)result.objectAtIndex(1)).objectAtIndex(1).equals("test8"));
    }



    /**
     * Test arrayByCollapsingSubCollections().
     */
    public void testArrayByCollapsingSubCollections()
    {
        NSArray collectionOfCollections = new NSArray(new Object[]
                                                      {new NSArray(new Object[] {"test1", "test2"}),
                                                          new NSArray(new Object[] {"test3", "test4"}),
                                                          new NSArray(new Object[] {"test5", "test7"}),
                                                          new NSArray(new Object[] {"test7", "test8"})});
        NSArray collectionOfCollections2 = new NSArray(new Object[]
                                                      {new NSArray(new Object[] {"test1", "test2"}),
                                                          new NSArray(new Object[] {"test3", "test4"}),
                                                          new NSArray(),
                                                          new NSArray("test2"),
                                                          "test8"});

        NSArray result = Collection.arrayByCollapsingSubCollections(collectionOfCollections, false);
        assertTrue(result.count() == 8);
        assertTrue(result.objectAtIndex(0).equals("test1"));
        assertTrue(result.objectAtIndex(1).equals("test2"));
        assertTrue(result.objectAtIndex(2).equals("test3"));
        assertTrue(result.objectAtIndex(3).equals("test4"));
        assertTrue(result.objectAtIndex(4).equals("test5"));
        assertTrue(result.objectAtIndex(5).equals("test7"));
        assertTrue(result.objectAtIndex(6).equals("test7"));
        assertTrue(result.objectAtIndex(7).equals("test8"));

        result = Collection.arrayByCollapsingSubCollections(collectionOfCollections, true);
        assertTrue(result.count() == 7);
        assertTrue(result.objectAtIndex(0).equals("test1"));
        assertTrue(result.objectAtIndex(1).equals("test2"));
        assertTrue(result.objectAtIndex(2).equals("test3"));
        assertTrue(result.objectAtIndex(3).equals("test4"));
        assertTrue(result.objectAtIndex(4).equals("test5"));
        assertTrue(result.objectAtIndex(5).equals("test7"));
        assertTrue(result.objectAtIndex(6).equals("test8"));

        result = Collection.arrayByCollapsingSubCollections(collectionOfCollections2, false);
        assertTrue(result.count() == 6);
        assertTrue(result.objectAtIndex(0).equals("test1"));
        assertTrue(result.objectAtIndex(1).equals("test2"));
        assertTrue(result.objectAtIndex(2).equals("test3"));
        assertTrue(result.objectAtIndex(3).equals("test4"));
        assertTrue(result.objectAtIndex(4).equals("test2"));
        assertTrue(result.objectAtIndex(5).equals("test8"));

        result = Collection.arrayByCollapsingSubCollections(collectionOfCollections2, true);
        assertTrue(result.count() == 5);
        assertTrue(result.objectAtIndex(0).equals("test1"));
        assertTrue(result.objectAtIndex(1).equals("test2"));
        assertTrue(result.objectAtIndex(2).equals("test3"));
        assertTrue(result.objectAtIndex(3).equals("test4"));
        assertTrue(result.objectAtIndex(4).equals("test8"));
    }



    /**
     * Test collectionIsSubsetOfCollection().
     */
    public void testCollectionIsSubsetOfCollection()
    {
        NSArray yesObjects = new NSArray(new Object[] {"test3", "test6"});
        assertTrue(Collection.collectionIsSubsetOfCollection(sourceObjects, yesObjects));
        assertTrue( ! Collection.collectionIsSubsetOfCollection(sourceObjects, notObjects));
    }



}
