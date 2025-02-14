package net.global_village.foundation;

import java.io.FileInputStream;
import java.lang.reflect.*;
import java.util.*;

import com.webobjects.foundation.*;


/**
 * Collection utilities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 10$
 */
public class Collection
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private Collection()
    {
        super();
    }



    /**
     * Converts a java array (including its contents) to a string useful for debugging. The format is unspecified, so don't expect anything specific out of here.
     *
     * @param array the array to print
     * @return a string representation of the java array
     */
    public static String javaArrayToString(Object[] array)
    {
        /** require [valid_param] array != null; **/

        String javaArrayToString = "";

        for (int i = 0; i < array.length; i++)
        {
            javaArrayToString = javaArrayToString + array[i].toString() + "\n";
        }

        return javaArrayToString;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> iff the given object is a collection type, as defined by this class (other objects may strictly be collections, but only the shown ones here are useful for this class), <code>false</code> otherwise.
     *
     * @param collection the object to test
     * @return <code>true</code> iff the given object is a collection type, <code>false</code> otherwise
     */
    public static boolean isCollection(Object collection)
    {
        /** require [valid_param] collection != null; **/

        return NSSet.class.isInstance(collection) || NSArray.class.isInstance(collection) || NSDictionary.class.isInstance(collection) || java.util.Collection.class.isInstance(collection) || Dictionary.class.isInstance(collection);
    }



    /**
     * Point of single choice for abstracting away the differences in enumerations in the various collections.  Returns an <code>Enumeration</code> object for the given collection.
     *
     * @param collection the collection to return the enumeration for
     * @return the <code>Enumeration</code> for the given collection
     */
    public static Enumeration enumerationForCollection(Object collection)
    {
        /** require [valid_param] collection != null; [is_collection] isCollection(collection); **/

        Enumeration enumerationForCollection = null;

        if (NSSet.class.isInstance(collection) || NSArray.class.isInstance(collection) || NSDictionary.class.isInstance(collection))
        {
            NSSelector countSelector = new NSSelector("count");
            NSSelector enumeratorSelector = new NSSelector("objectEnumerator");
            try
            {
                // Apparently you can't call objectEnumerator on an empty array
                if (((Number)countSelector.invoke(collection)).intValue() > 0)
                {
                    enumerationForCollection = (Enumeration)enumeratorSelector.invoke(collection);
                }
                else
                {
                    // So return an empty linked list one
                    enumerationForCollection = Collection.enumerationForCollection(new LinkedList());
                }
            }
            // These should never happen...
            catch (NoSuchMethodException e) { throw new ExceptionConverter(e); }
            catch (java.lang.reflect.InvocationTargetException e) { throw new ExceptionConverter(e); }
            catch (IllegalAccessException e) { throw new ExceptionConverter(e); }
            catch (IllegalArgumentException e) { throw new ExceptionConverter(e); }
        }
        else if (Dictionary.class.isInstance(collection))
        {
            NSSelector sel = new NSSelector("elements");
            try
            {
                enumerationForCollection = (Enumeration)sel.invoke(collection);
            }
            // These should never happen...
            catch (NoSuchMethodException e) { throw new ExceptionConverter(e); }
            catch (java.lang.reflect.InvocationTargetException e) { throw new ExceptionConverter(e); }
            catch (IllegalAccessException e) { throw new ExceptionConverter(e); }
            catch (IllegalArgumentException e) { throw new ExceptionConverter(e); }
        }
        else
        {
            enumerationForCollection = Collections.enumeration((java.util.Collection)collection);
        }

        return enumerationForCollection;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Given an arbitrary collection, returns an <code>NSMutableArray</code> with the same objects.
     *
     * @param collection the collection to change
     * @return an <code>NSMutableArray</code> filled with the same objects as in <code>collection</code>
     */
    public static NSMutableArray collectionToNSMutableArray(Object collection)
    {
        /** require [valid_param] collection != null; [is_collection] isCollection(collection); **/

        NSMutableArray theArray = new NSMutableArray();
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            Object theElement = collectionEnumerator.nextElement();
            theArray.addObject(theElement);
        }
        return theArray;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Given an arbitrary collection, returns an <code>NSMutableSet</code> with the same objects. If <code>collection</code> contains duplicate objects, only one of the objects will be included in the set, as per normal set semantics.
     *
     * @param collection the collection to change
     * @return an <code>NSMutableSet</code> filled with the same objects as in <code>collection</code>
     */
    public static NSMutableSet collectionToNSMutableSet(Object collection)
    {
        /** require [valid_param] collection != null; [is_collection] isCollection(collection); **/

        NSMutableSet theSet = new NSMutableSet();
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            Object theElement = collectionEnumerator.nextElement();
            theSet.addObject(theElement);
        }
        return theSet;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSArray</code> or an <code>NSDictionary</code> created from the contents of <code>filePath</code>.
     *
     * @param filePath the path to the file
     * @return an array or dictionary
     */
    public static Object collectionWithContentsOfFile(String filePath) throws java.io.IOException
    {
        /** require [valid_param] filePath != null; **/

        FileInputStream fis = new FileInputStream(filePath);
        NSData aData = new NSData(fis, 1000);
        return NSPropertyListSerialization.propertyListFromData(aData, "ASCII");

        /** ensure
        [valid_result] Result != null;
        [is_collection_class] (Result instanceof NSArray) || (Result instanceof NSDictionary); **/
    }



    /**
     * Tells if a given collection contains all objects of type instanceOf (as given by Class.isInstance()).
     *
     * @param collection the collection to check
     * @param instancesOf the class that the collection should contain
     * @return <code>true</code> iff all objects in the collection are of type <code>instancesOf</code>, <code>false</code> otherwise
     */
    public static boolean collectionContainsInstancesOf(Object collection, java.lang.Class instancesOf)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_instancesOf_param] instancesOf != null;
        [is_collection] isCollection(collection); **/

        boolean collectionContainsInstancesOf = true;
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            Object theElement = collectionEnumerator.nextElement();
            if ( ! instancesOf.isInstance(theElement))
            {
                collectionContainsInstancesOf = false;
                break;
            }
        }
        return collectionContainsInstancesOf;
    }



    /**
     * Tells if the given collection contains objects that all respond to <code>selector</code>.
     *
     * @param collection the collection to check
     * @param selector the selector that each object should respond to
     * @return <code>true</code> iff all objects in the collection respond to <code>selector</code>, <code>false</code> otherwise
     */
    public static boolean collectionContainsInstancesRespondingTo(Object collection, NSSelector selector)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_selector_param] selector != null;
        [is_collection] isCollection(collection); **/

        boolean collectionContainsInstancesRespondingTo = true;
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            Object theElement = collectionEnumerator.nextElement();
            if ( ! selector.implementedByObject(theElement))
            {
                collectionContainsInstancesRespondingTo = false;
                break;
            }
        }
        return collectionContainsInstancesRespondingTo;
    }



    /**
     * Invokes <code>toSelector</code> with <code>arguments</code> on the elements of <code>collection</code> to see if any return <code>response</code>.  The method is <em>not</em> guaranteed to be called on all elements of the collection.
     *
     * @param collection the collection to check
     * @param response the expected response from the collection
     * @param toSelector the selector to invoke
     * @param arguments the arguments to send to the method
     * @return <code>true</code> iff any objects in the collection return <code>response</code>, <code>false</code> otherwise
     * @exception java.lang.reflect.InvocationTargetException thrown when the method called by the selector itself raises an exception
     * @exception IllegalAccessException thrown when the method called by the selector cannot be reached from this
     */
    public static boolean doAnyObjectsRespond(Object collection,
                                              Object response,
                                              NSSelector toSelector,
                                              Object[] arguments) throws java.lang.reflect.InvocationTargetException, IllegalAccessException
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_response_param] response != null;
        [valid_toSelector_param] toSelector != null;
        [is_collection] isCollection(collection);
        [collection_items_respond_to_selector] collectionContainsInstancesRespondingTo(collection, toSelector); **/

        boolean doAnyObjectsRespond = false;
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            try
            {
                Object theElement = collectionEnumerator.nextElement();
                Object result = toSelector.invoke(theElement, arguments);
                if (result.equals(response))
                {
                    doAnyObjectsRespond = true;
                    break;
                }
            }
            catch (NoSuchMethodException e) { throw new Error(e); }
        }
        return doAnyObjectsRespond;
    }



    /**
     * Invokes <code>toSelector</code> with <code>arguments</code> on the elements of <code>collection</code> to see if all return <code>response</code>.  The method is <em>not</em> guaranteed to be called on all elements of the collection.
     *
     * @param collection the collection to check
     * @param response the expected response from the collection
     * @param toSelector the selector to invoke
     * @param arguments the arguments to send to the method
     * @return <code>true</code> iff all objects in the collection return <code>response</code>, <code>false</code> otherwise
     * @exception java.lang.reflect.InvocationTargetException thrown when the method called by the selector itself raises an exception
     * @exception IllegalAccessException thrown when the method called by the selector cannot be reached from this
     */
    public static boolean doAllObjectsRespond(Object collection,
                                              Object response,
                                              NSSelector toSelector,
                                              Object[] arguments) throws java.lang.reflect.InvocationTargetException, IllegalAccessException
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_response_param] response != null;
        [valid_toSelector_param] toSelector != null;
        [is_collection] isCollection(collection);
        [collection_items_respond_to_selector] collectionContainsInstancesRespondingTo(collection, toSelector); **/

        boolean doAllObjectsRespond = true;
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            try
            {
                Object theElement = collectionEnumerator.nextElement();
                Object result = toSelector.invoke(theElement, arguments);
                if ( ! result.equals(response))
                {
                    doAllObjectsRespond = false;
                    break;
                }
            }
            catch (NoSuchMethodException e) { }
        }
        return doAllObjectsRespond;
    }



    /**
     * Returns those objects that respond to <code>toSelector</code>.
     *
     * @param collection the collection to check
     * @param toSelector the selector to test
     * @return any objects in the collection that responde to <code>toSelector</code>
     */
    public static NSArray objectsRespondingTo(Object collection,
                                              NSSelector toSelector)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_toSelector_param] toSelector != null;
        [is_collection] isCollection(collection); **/

        NSMutableArray objectsRespondingTo = new NSMutableArray();
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            Object theElement = collectionEnumerator.nextElement();
            if (toSelector.implementedByObject(theElement))
            {
                objectsRespondingTo.addObject(theElement);
            }
        }
        return objectsRespondingTo;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Invokes <code>toSelector</code> with <code>arguments</code> on the elements of <code>collection</code>.  All items in the collection that return <code>response</code> (as determined by <code>equals()</code>) are returned.
     *
     * @param collection the collection to check
     * @param response the expected response from the collection
     * @param toSelector the selector to invoke
     * @param arguments the arguments to send to the method
     * @return any objects in the collection that returned <code>response</code>
     * @exception java.lang.reflect.InvocationTargetException thrown when the method called by the selector itself raises an exception
     * @exception IllegalAccessException thrown when the method called by the selector cannot be reached from this
     */
    public static NSArray objectsResponding(Object collection,
                                            Object response,
                                            NSSelector toSelector,
                                            Object[] arguments) throws java.lang.reflect.InvocationTargetException, IllegalAccessException
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_response_param] response != null;
        [valid_toSelector_param] toSelector != null;
        [is_collection] isCollection(collection);
        [collection_items_respond_to_selector] collectionContainsInstancesRespondingTo(collection, toSelector); **/

        NSMutableArray objectsResponding = new NSMutableArray();
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            try
            {
                Object theElement = collectionEnumerator.nextElement();
                Object result = toSelector.invoke(theElement, arguments);
                if (result.equals(response))
                {
                    objectsResponding.addObject(theElement);
                }
            }
            catch (NoSuchMethodException e) { }
        }
        return objectsResponding;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSArray</code> with the collection entries passed into the Constructor provided before being set in the array.
     *
     * @param collection the collection whose elements we will convert
     * @param constructor the constructor that will create from <code>String</code>s the types that should be in the array
     * @return the array with converted elements
     */
    public static NSArray convertCollectionStringElements(Object collection, Constructor constructor) throws InvocationTargetException, IllegalAccessException
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_constructor_param] constructor != null;
        [is_collection] isCollection(collection); **/
        JassAdditions.pre("Collection", "convertCollectionElements", constructor.getParameterTypes().length == 1);
        JassAdditions.pre("Collection", "convertCollectionElements", constructor.getParameterTypes()[0].isAssignableFrom(String.class));
        JassAdditions.pre("Collection", "convertCollectionElements", ! Modifier.isAbstract(constructor.getDeclaringClass().getModifiers()));

        NSMutableArray resultArray = new NSMutableArray();
        Enumeration collectionEnumerator = enumerationForCollection(collection);
        while (collectionEnumerator.hasMoreElements())
        {
            Object oldElement = (String)collectionEnumerator.nextElement();
            Object newElement = null;
            try
            {
                newElement = constructor.newInstance(new Object[] {oldElement});
            }
            // Never happens
            catch (IllegalArgumentException e) {}
            catch (InstantiationException e) {}

            resultArray.addObject(newElement);
        }

        return resultArray;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSArray</code> whose objects are randomly selected from <code>collection</code> that are not in <code>notInCollection</code>.  It will select <em>up to</em> <code>numberToSelect</code> objects (if there are enough objects remaining in the collection after removing <code>notInCollection</code>).  An ordered collection will remain in the same order.
     *
     * @param collection the collection to select objects from
     * @param notInCollection a collection of objects not to select from <code>collection</code>
     * @param numberToSelect the number of objects to randomly select
     * @return the randomly selected objects
     */
    public static NSArray randomObjects(Object collection,
                                        Object notInCollection,
                                        int numberToSelect)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_notInCollection_param] notInCollection != null;
        [valid_numberToSelect_param] numberToSelect > 0;
        [collection_is_collection] isCollection(collection);
        [notInCollection_is_collection] isCollection(notInCollection); **/

        NSMutableArray randomObjects = collectionToNSMutableArray(collection);
        randomObjects.removeObjectsInArray(collectionToNSMutableArray(notInCollection));
        Random random = new Random();

        while (randomObjects.count() > numberToSelect)
        {
            randomObjects.removeObjectAtIndex((int)Math.abs(random.nextInt()) % randomObjects.count());
        }
        return randomObjects;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSArray</code> whose objects are randomly selected from <code>collection</code>.  It will select <em>up to</em> <code>numberToSelect</code> objects (if there are enough objects).  An ordered collection will remain in the same order.
     *
     * @param collection the collection to select objects from
     * @param numberToSelect the number of objects to randomly select
     * @return the randomly selected objects
     */
    public static NSArray randomObjects(Object collection,
                                        int numberToSelect)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_numberToSelect_param] numberToSelect > 0;
        [collection_is_collection] isCollection(collection); **/

        return randomObjects(collection, new NSArray(), numberToSelect);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSArray</code> whose objects are the first <code>numberToSelect</code> objects in <code>collection</code> that are not also in <code>notInCollection</code>.  It will select <em>up to</em> <code>numberToSelect</code> objects (if there are enough objects remaining in the collection after removing <code>notInCollection</code>).  An ordered collection will remain in the same order.
     *
     * @param collection the collection to select objects from
     * @param notInCollection a collection of objects not to select from <code>collection</code>
     * @param numberToSelect the number of objects to select
     * @return the selected objects
     */
    public static NSArray firstObjects(Object collection,
                                       Object notInCollection,
                                       int numberToSelect)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_notInCollection_param] notInCollection != null;
        [valid_numberToSelect_param] numberToSelect > 0;
        [collection_is_collection] isCollection(collection);
        [notInCollection_is_collection] isCollection(notInCollection); **/

        NSMutableArray firstObjects = collectionToNSMutableArray(collection);
        firstObjects.removeObjectsInArray(collectionToNSMutableArray(notInCollection));

        NSRange objectRange = new NSRange(0, (int)Math.min(numberToSelect, firstObjects.count()));
        return firstObjects.subarrayWithRange(objectRange);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an <code>NSArray</code> whose objects are the first <code>numberToSelect</code> objects in <code>collection</code>.  It will select <em>up to</em> <code>numberToSelect</code> objects (if there are enough objects).  An ordered collection will remain in the same order.
     *
     * @param collection the collection to select objects from
     * @param numberToSelect the number of objects to select
     * @return the selected objects
     */
    public static NSArray firstObjects(Object collection,
                                       int numberToSelect)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_numberToSelect_param] numberToSelect > 0;
        [collection_is_collection] isCollection(collection); **/

        return firstObjects(collection, new NSArray(), numberToSelect);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Removes objects in <code>notInCollection</code> from and then divides <code>collection</code> into sub-arrays, each with at most <code>numberPerSubarray</code> objects (the last subarray may contain less if not evenly divisible by the number of objects in the collection), and returns an array containing those subarrays.  An ordered collection will remain in the same order.
     *
     * @param collection the collection to select objects from
     * @param notInCollection a collection of objects not to select from <code>collection</code>
     * @param numberPerSubarray the number of objects to select
     * @return the selected objects
     */
    public static NSArray divideObjects(Object collection,
                                        Object notInCollection,
                                        int numberPerSubarray)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_notInCollection_param] notInCollection != null;
        [valid_numberPerSubarray_param] numberPerSubarray > 0;
        [collection_is_collection] isCollection(collection);
        [notInCollection_is_collection] isCollection(notInCollection); **/

        NSMutableArray divideObjects = new NSMutableArray();
        NSMutableArray objectsForDivision = collectionToNSMutableArray(collection);
        objectsForDivision.removeObjectsInArray(collectionToNSMutableArray(notInCollection));

        int numberOfSubarrays = (int)Math.ceil(((double)objectsForDivision.count()) / ((double)numberPerSubarray));
        for (int i = 0; i < numberOfSubarrays; i++)
        {
            int lengthOfSubarray;
            if (i == numberOfSubarrays - 1)
            {
                // The last time around is special...
                lengthOfSubarray = objectsForDivision.count() % numberPerSubarray;
            }
            else
            {
                lengthOfSubarray = numberPerSubarray;
            }
            NSRange rangeOfSubarray = new NSRange(i * numberPerSubarray, lengthOfSubarray);
            divideObjects.addObject(objectsForDivision.subarrayWithRange(rangeOfSubarray));
        }

        return divideObjects;

        /** ensure [valid_result] Result != null; **/
    }



    /**
    * Divides <code>collection</code> into sub-arrays, each with at most <code>numberPerSubarray</code> objects (the last subarray may contain less if not evenly divisible by the number of objects in the collection), and returns an array containing those subarrays.  An ordered collection will remain in the same order.
     *
     * @param collection the collection to select objects from
     * @param numberPerSubarray the number of objects to select
     * @return the selected objects
     */
    public static NSArray divideObjects(Object collection,
                                        int numberPerSubarray)
    {
        /** require
        [valid_collection_param] collection != null;
        [valid_numberPerSubarray_param] numberPerSubarray > 0;
        [collection_is_collection] isCollection(collection); **/

        return divideObjects(collection, new NSArray(), numberPerSubarray);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Given a collection of subcollections, returns an <code>NSArray</code> containing all the objects in each of the subcollections.  Duplicate objects are removed if <code>removeDuplicates</code> is <code>true</code>.  Non-collection sub-entries are added correctly without complaint and sub-sub-collections are added without collapsing.
     *
     * @param collection the collection to collapse
     * @param removeDuplicates <code>true</code> if this should remove duplicate objects from the array before returning
     * @return an array containing objects from the collection of arrays
     */
    public static NSArray arrayByCollapsingSubCollections(Object collection,
                                                          boolean removeDuplicates)
    {
        NSMutableArray arrayByCollapsingSubCollections = new NSMutableArray();
        Enumeration collectionEnumeration = enumerationForCollection(collection);
        while (collectionEnumeration.hasMoreElements())
        {
            Object subCollection = collectionEnumeration.nextElement();
            if ( ! Collection.isCollection(subCollection))
            {
                if (((removeDuplicates) &&
                     ( ! arrayByCollapsingSubCollections.containsObject(subCollection))) ||
                    ( ! removeDuplicates))
                {
                    arrayByCollapsingSubCollections.addObject(subCollection);
                }
            }
            else
            {
                Enumeration subCollectionEnumeration = enumerationForCollection(subCollection);
                while (subCollectionEnumeration.hasMoreElements())
                {
                    Object subObject = subCollectionEnumeration.nextElement();
                    if (((removeDuplicates) &&
                         ( ! arrayByCollapsingSubCollections.containsObject(subObject))) ||
                        ( ! removeDuplicates))
                    {
                        arrayByCollapsingSubCollections.addObject(subObject);
                    }
                }
            }
        }
        return arrayByCollapsingSubCollections;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code if all the objects in <code>otherCollection</code> are in <code>collection</code>.
     *
     * @param supersetCollection the collection to select objects from
     * @param subsetCollection a collection of objects to check for in <code>collection</code>
     * @return <code>true</code> iff all elements of <code>otherCollection</code> exist in <code>collection</code>, <code>false</code> otherwise
     */
    public static boolean collectionIsSubsetOfCollection(Object supersetCollection,
                                                         Object subsetCollection)
    {
        /** require
        [valid_supersetCollection_param] supersetCollection != null;
        [valid_subsetCollection_param] subsetCollection != null;
        [supersetCollection_is_collection] isCollection(supersetCollection);
        [subsetCollection_is_collection] isCollection(subsetCollection); **/

        NSSet supersetCollectionAsSet = collectionToNSMutableSet(supersetCollection);
        NSSet subsetCollectionAsSet = collectionToNSMutableSet(subsetCollection);
        return subsetCollectionAsSet.isSubsetOfSet(supersetCollectionAsSet);
    }



}
