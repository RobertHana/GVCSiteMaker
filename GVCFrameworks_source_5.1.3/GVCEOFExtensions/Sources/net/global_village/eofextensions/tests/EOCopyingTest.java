package net.global_village.eofextensions.tests;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.gvcjunit.*;


/**
 * Tests EOCopying / CopyableGenericRecord.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 */  
public class EOCopyingTest extends GVCJUnitEOTestCase
{
    protected RootObject rootObject;
    protected OwnedObject ownedObject1;
    protected OwnedObject ownedObject2;
    protected UnOwnedObject unOwnedObject1;
    protected UnOwnedObject unOwnedObject2;

    protected final String ROOT_OBJECT_NAME = "Root Object";
    protected final String OWNED_OBJECT1_NAME = "First Owned Object";
    protected final String OWNED_OBJECT2_NAME = "Second Owned Object";
    protected final String UNOWNED_OBJECT1_NAME = "Lookup One";
    protected final String UNOWNED_OBJECT2_NAME = "Lookup Two";
    
    
    /**
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
    public EOCopyingTest(String name)
     {
         super(name);
     }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        rootObject = new RootObject();
        ownedObject1 = new OwnedObject();
        ownedObject2 = new OwnedObject();
        unOwnedObject1 = new UnOwnedObject();
        unOwnedObject2 = new UnOwnedObject();

        editingContext().insertObject(rootObject);
        editingContext().insertObject(ownedObject1);
        editingContext().insertObject(ownedObject2);
        editingContext().insertObject(unOwnedObject1);
        editingContext().insertObject(unOwnedObject2);

        rootObject.setName(ROOT_OBJECT_NAME);

        ownedObject1.setName(OWNED_OBJECT1_NAME);
        ownedObject2.setName(OWNED_OBJECT2_NAME);
        unOwnedObject1.setName(UNOWNED_OBJECT1_NAME);
        unOwnedObject2.setName(UNOWNED_OBJECT2_NAME);

        rootObject.addObjectToBothSidesOfRelationshipWithKey(ownedObject1, "ownedObjects");
        rootObject.addObjectToBothSidesOfRelationshipWithKey(ownedObject2, "ownedObjects");
        rootObject.addObjectToBothSidesOfRelationshipWithKey(unOwnedObject1, "unOwnedObjects");
        rootObject.addObjectToBothSidesOfRelationshipWithKey(unOwnedObject2, "unOwnedObjects");

        editingContext().saveChanges();
    }



    /**
     * Test graph equivalence, regardless of where copy is started.
     */
    public void testGraphEquivalence()
    {
        NSMutableDictionary copyCache = new NSMutableDictionary();
        rootObject.copy(copyCache);

        NSMutableDictionary copyCache2 = new NSMutableDictionary();
        ownedObject1.copy(copyCache2);

        assertTrue("number of keys matches", copyCache.count() == copyCache2.count());
        assertTrue("Global IDs matches", new NSSet(copyCache.allKeys()).equals(new NSSet(copyCache2.allKeys())));
        
        editingContext().revert();
    }



    /**
     * Test that all objects were copied
     */
    public void testAllObjectCopied()
    {
        NSMutableDictionary copyCache = new NSMutableDictionary();
        rootObject.copy(copyCache);

        assertTrue("root object copied", copyCache.objectForKey(editingContext().globalIDForObject(rootObject)) != null);
        assertTrue("first owned object copied", copyCache.objectForKey(editingContext().globalIDForObject(ownedObject1)) != null);
        assertTrue("second owned object copied", copyCache.objectForKey(editingContext().globalIDForObject(ownedObject2)) != null);
        assertTrue("first un-owned object copied", copyCache.objectForKey(editingContext().globalIDForObject(unOwnedObject1)) != null);
        assertTrue("second un-owned object copied", copyCache.objectForKey(editingContext().globalIDForObject(unOwnedObject2)) != null);
        editingContext().revert();
    }



    /**
     * Test that objects were copied as designated (copied or by reference)
     */
    public void testObjectsCopiedCorrectly()
    {
        NSMutableDictionary copyCache = new NSMutableDictionary();
        rootObject.copy(copyCache);

        assertTrue("root object copied", copyCache.objectForKey(editingContext().globalIDForObject(rootObject)) != rootObject);
        assertTrue("first owned object copied", copyCache.objectForKey(editingContext().globalIDForObject(ownedObject1)) != ownedObject1);
        assertTrue("second owned object copied", copyCache.objectForKey(editingContext().globalIDForObject(ownedObject2)) != ownedObject2);
        assertTrue("first un-owned object referenced", copyCache.objectForKey(editingContext().globalIDForObject(unOwnedObject1)) == unOwnedObject1);
        assertTrue("second un-owned object referenced", copyCache.objectForKey(editingContext().globalIDForObject(unOwnedObject2)) == unOwnedObject2);
        editingContext().revert();
    }


    
    /**
     * Test that the relationships are not duplicated or ommited.
     */
    public void testRelationshipsCopiedCorrectly()
    {
        NSMutableDictionary copyCache = new NSMutableDictionary();
        RootObject copyOfRoot = (RootObject) rootObject.copy(copyCache);


        Enumeration relationshipEnumerator = rootObject.toManyRelationshipKeys().objectEnumerator();
        while (relationshipEnumerator.hasMoreElements())
        {
            String relationshipName = (String)relationshipEnumerator.nextElement();
            NSArray originalObjects = (NSArray)rootObject.valueForKey(relationshipName);
            NSArray copiedObjects = (NSArray)copyOfRoot.valueForKey(relationshipName);

            assertTrue("to many relationship has same # of objects: " + relationshipName,
                       originalObjects.count() == copiedObjects.count());
        }

        relationshipEnumerator =  rootObject.toOneRelationshipKeys().objectEnumerator();
        while (relationshipEnumerator.hasMoreElements())
        {
            String relationshipName = (String)relationshipEnumerator.nextElement();
            EOEnterpriseObject originalObject = (EOEnterpriseObject)rootObject.valueForKey(relationshipName);
            EOEnterpriseObject copiedObject = (EOEnterpriseObject)copyOfRoot.valueForKey(relationshipName);

            assertTrue("to-one relationship has compatible objects: " + relationshipName,
                       ((originalObject == null) &&  (copiedObject == null) ||
                        (originalObject != null) &&  (copiedObject != null)));
        }

        editingContext().revert();
    }

    

    /**
     * Test that the relationships are not duplicated or ommited.
     */
    public void testAttributesCopiedCorrectly()
    {
        NSMutableDictionary copyCache = new NSMutableDictionary();
        rootObject.setAValue(null);	// Check that null values which are set to non-null in awakeFromInsertion are properly copied.
        RootObject copyOfRoot = (RootObject) rootObject.copy(copyCache);


        Enumeration attributeEnumerator = rootObject.attributeKeys().objectEnumerator();
        while (attributeEnumerator.hasMoreElements())
        {
            String attributeName = (String)attributeEnumerator.nextElement();

            Object originalValue = rootObject.valueForKey(attributeName);
            Object copiedValue = copyOfRoot.valueForKey(attributeName);

            if ( ! attributeName.equals("oid"))
            {
                assertTrue("attribute has same value: " + attributeName,
                           (originalValue == copiedValue) || originalValue.equals(copiedValue));
            }
            else
            {
                assertTrue("exposed PK not copied",
                           (originalValue != null) && (copiedValue == null));
            }
        }

        editingContext().revert();
    }



    /**
     * Test that copied objects inserted correctly.
     */
    public void testCopiedObjectsInserted()
    {
        NSMutableDictionary copyCache = new NSMutableDictionary();
        rootObject.copy(copyCache);
        EOEditingContext ec = editingContext();
        NSArray insertedObjects = ec.insertedObjects();

        assertTrue("root object inserted",
                   insertedObjects.containsObject(copyCache.objectForKey(ec.globalIDForObject(rootObject))));
        assertTrue("first owned object inserted", 	insertedObjects.containsObject(copyCache.objectForKey(ec.globalIDForObject(ownedObject1))));
        assertTrue("second owned object inserted", 	insertedObjects.containsObject(copyCache.objectForKey(ec.globalIDForObject(ownedObject2))));

        assertTrue("first un-owned object not inserted",
                   ! insertedObjects.containsObject(copyCache.objectForKey(ec.globalIDForObject(unOwnedObject1))));
        assertTrue("second un-owned object not inserted",
                   ! insertedObjects.containsObject(copyCache.objectForKey(ec.globalIDForObject(unOwnedObject2))));
        
        editingContext().revert();
    }



    /**
     * Test that copied objects can be saved and deleted.
     */
    public void testCanSave()
    {
        NSMutableDictionary copyCache = new NSMutableDictionary();
        RootObject copyOfRoot = (RootObject) rootObject.copy(copyCache);
        saveChangesShouldntThrow("Failed to save copied objects");

        editingContext().deleteObject(copyOfRoot);
        saveChangesShouldntThrow("Failed to delete copied objects");
    }
    

    /**
     * Delete inserted objects
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().deleteObject(rootObject);
        editingContext().deleteObject(unOwnedObject1);
        editingContext().deleteObject(unOwnedObject2);
        editingContext().saveChanges();
        super.tearDown();
    }



}
