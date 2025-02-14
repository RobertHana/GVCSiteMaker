package net.global_village.eofextensions;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;

import net.global_village.foundation.*;

/*
 * * This class provides an interface for flexible copying of
 * EOEnterpriseObjects and a default implementation for doing the actual
 * copying. This default implementation would be most easily used by creating a
 * sub-class of EOCustomObject or EOGenericRecord and using that as the super
 * class of your EOEnterpriseObjects.
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights
 * reserved. This software is published under the terms of the Educational
 * Community License (ECL) version 1.0, a copy of which has been included with
 * this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 */
public interface EOCopying
{

    /** Key into dictionary of copiedObjects to include a context for context specific copy procedures.  */
    public static final String COPY_CONTEXT = "EOCopyingContext";


    /**
     * Returns a copy of this object, copying related objects as well.  The actual copy mechanism (by reference, shallow, deep, or custom) for each object is up to the object being copied.  If a copy already exists in <code>copiedObjects</code>, then that is returned instead of making a new copy.  This allows complex graphs of objects, including those with cycles, to be copied without producing duplicate objects.  The grapch of copied objects will be the same regardless of where copy is started with two exceptions: it is started on a reference copied object or a reference copied object is the only path between two disconnected parts of the graph.  In these cases the reference copied object prevents the copy from following the graph further.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this object
     */
    public EOEnterpriseObject copy(NSMutableDictionary copiedObjects);


    //** require [copiedObjects_not_null] copiedObjects != null;  **/
    //** ensure [copy_made] Result != null;  
    //          [copy_recorded] copiedObjects.objectForKey(editingContext().globalIDForObject(this)) != null;  **/



    /**
     * Returns a copy of this object.  Each EOEnterpriseObject should implement this to produce the actual copy by an appropriate mechanism (reference, shallow, deep, or custom).
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this object
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects);

    //** require [copiedObjects_not_null] copiedObjects != null;  **/
    //** ensure [copy_made] Result != null;   **/



    /**
     * This class provides a default implementation of EOCopying that handles the most common situations encountered copying EO objects.  This default implementation would be most easily used by creating a sub-class of EOCustomObject or EOGenericRecord and using that as the super class of your EOEnterpriseObjects.  For example:<br>
     * <pre>
     * public class CopyableGenericRecord extends EOGenericRecord
     * {
     *     public CopyableGenericRecord()
     *     {
     *         super();
     *     }
     *
     *     public EOEnterpriseObject copy(NSMutableDictionary copiedObjects)
     *     { 
     *         return EOCopying.DefaultImplementation.copy(copiedObjects, this);
     *     }
     *
     *     // Sub-classes can override this to copy via a different mechanism.  This can be anything from EOCopying.Utility.referenceCopy(this) to a fully customized copy.
     *     public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
     *     {
     *         return EOCopying.Utility.deepCopy(copiedObjects, this);
     *     }
     * }
     * </pre>
     * <b>Notes</b><br>
        * Debugging information can be turned on with <code>NSLog.setAllowedDebugLevel(NSLog.DebugLevelDetailed);</code>.<br>
        * If you implement your own deep copy of relationships you should register the new object before copying its relationships to so that circular relationships will be copied correctly.  For example:<br>
        * <pre>
        * EOGlobalID globalID = editingContext().globalIDForObject(this);
    * copiedObjects.setObjectForKey(copy, globalID);
    * </pre>
    */
    public static class DefaultImplementation
    {


        /**
         * Returns a copy of this object.  The actual copy mechanism (by reference, shallow, deep, or custom) is up to the object being copied.  If a copy already exists in <code>copiedObjects</code>, then that is returned instead of making a new copy.  This allows complex graphs of objects, including those with cycles, to be copied without producing duplicate objects.
         *
         * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
         * @param source - the EOEnterpriseObject to copy
         * @return a copy of this object
         */
        public static EOEnterpriseObject copy(NSMutableDictionary copiedObjects, EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.DefaultImplementation", "copy [copiedObjects_not_null]", copiedObjects != null);
            JassAdditions.pre("EOCopying.DefaultImplementation", "copy [valid_source]", source != null);
            JassAdditions.pre("EOCopying.DefaultImplementation", "deepCopy [source_eocopying]", source instanceof EOCopying);

            EOGlobalID globalID = source.editingContext().globalIDForObject(source);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Copying object '" + source.entityName() + "' ID: " + Utility.prettyPrintPK(source));
            }

            EOEnterpriseObject copy = (EOEnterpriseObject) copiedObjects.objectForKey(globalID);
            if (copy == null)
            {
                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Creating duplicate of object '" + source.entityName() + "' ID: " + Utility.prettyPrintPK(source));
                }

                copy = ((EOCopying) source).duplicate(copiedObjects);
                copiedObjects.setObjectForKey(copy, globalID);
            }

            JassAdditions.post("EOCopying.DefaultImplementation", "copy [copy_made]", copy != null);
            JassAdditions.post("EOCopying.DefaultImplementation", "copy [copy_cached]", copiedObjects.objectForKey(source.editingContext().globalIDForObject(
                    source)) != null);

            return copy;
        }



        /**
         * Returns a deep copy of this object.  
         *
         * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
         * @return a deep copy of this object
         */
        public static EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects, EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.DefaultImplementation", "duplicate [copiedObjects_not_null]", copiedObjects != null);
            JassAdditions.pre("EOCopying.DefaultImplementation", "duplicate [valid_source]", source != null);

            EOEnterpriseObject duplicate = Utility.deepCopy(copiedObjects, source);

            JassAdditions.post("EOCopying.DefaultImplementation", "duplicate [duplicate_made]", duplicate != null);

            return duplicate;
        }

    }



    /**
     * This class provides utility methods for use implementing EOCopying.  They handle the most common situations encountered copying EO objects.  The DefaultImplementation uses them internally.  The implementations of referenceCopy, shallowCopy(), and deepCopy() should be suitable for most EO objects to use for their duplicate(NSMutableDictionary) method.  However there are some situations that can not be handled with this generic code:<br>
     * <ol>
     * <li>An attribute or relationship must not be copied (e.g. order numbers).</li>
     * <li>An attribute or relationship needs special handling (e.g. dateModified should reflect when the copy was made, not when the original object was created).</li>
     * <li>An EO object should not be copied the same way in all situations (e.g. the relationship from one object should be copied deeply, but from another object should be a reference copy).</li>
     * <li>The relationships must be copied in a certain order (e.g. due to side effects in the methods setting the relationships).</li>
     * </ol>
     * In this situations you will need to write a custom implementation of the duplicate(NSMutableDictionary) method.  This can be as simple as invoking the default implementation and then cleaning up the result to as complex as doing it all by hand.  Utility also provides lower-level methods that you can use when creating a custom duplicate(NSMutableDictionary) method.  These are: newInstance(), copyAttributes(), exposedKeyAttributeNames(), shallowCopyRelatedObjects(), and deepCopyRelatedObjects(), cleanRelationships(), and deepCopyRelationship.  Debugging information can be turned on with <code>NSLog.setAllowedDebugLevel(NSLog.DebugLevelDetailed);</code>.<br>
    */
    public static class Utility
    {
        protected static NSMutableDictionary exposedKeyAttributeDictionary = null;


        /**
         * Formats the PK for pretty printing. Handles NSData PKs and composite PKs.
         *
         * @param source the object who's Pk we are formatting
         * @return the PK ready for printing
         */
        public static Object prettyPrintPK(EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.Utility", "prettyPrintPK [valid_source]", source != null);

            NSDictionary rawDict = EOUtilities.primaryKeyForObject(source.editingContext(), source);
            if (rawDict != null)
            {
                NSMutableDictionary pkDict = rawDict.mutableClone();

                Enumeration keyEnumerator = pkDict.keyEnumerator();
                while (keyEnumerator.hasMoreElements())
                {
                    String key = (String) keyEnumerator.nextElement();
                    Object pk = pkDict.valueForKey(key);
                    if (pk instanceof NSData)
                    {
                        pk = ERXStringUtilities.byteArrayToHexString(((NSData) pk).bytes());
                        pkDict.setObjectForKey(pk, key);
                    }
                }

                if (pkDict.count() == 1)
                {
                    return pkDict.allValues().lastObject();
                }
                else
                {
                    return pkDict;
                }
            }
            else
            {
                return null;
            }
        }



        /**
         * Returns a copy of this object by reference.  This is equivalent to <code>return this;</code> on an EOEnterpriseObject.  This method of copying is suitable for lookup list items and other objects which should never be duplicated.
         *
         * @param source - the EOEnterpriseObject to copy
         * @return a copy of this object
         */
        public static EOEnterpriseObject referenceCopy(EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.Utility", "referenceCopy [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopy [source_eocopying]", source instanceof EOCopying);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Reference copying '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            return source;
        }



        /**
         * Returns a shallow copy of this object, the attribute values are copied
         * and the relationships are copied by reference.  This method of copying
         * is suitable for things like an order item where duplication of the
         * product is not wanted and where the order will no tbe changed (the
         * copied order item will be on the orginal order, not a copy of it).
         *
         * @param source the EOEnterpriseObject to copy
         * @return a copy of this object
         */
        public static EOEnterpriseObject shallowCopyWithoutRelationships(EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.Utility", "shallowCopy [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "shallowCopy [source_eocopying]", source instanceof EOCopying);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Making shallow copy without relationships of '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            EOEnterpriseObject copy = newInstance(source);
            copyAttributes(source, copy);

            JassAdditions.post("EOCopying.Utility", "shallowCopy [copy_made]", copy != null);

            return copy;
        }



        /**
         * Returns a shallow copy of this object, the attribute values are copied
         * and the relationships are copied by reference.  This method of copying
         * is suitable for things like an order item where duplication of the
         * product is not wanted and where the order will no tbe changed (the
         * copied order item will be on the orginal order, not a copy of it).
         *
         * @param source the EOEnterpriseObject to copy
         * @return a copy of this object
         */
        public static EOEnterpriseObject shallowCopy(EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.Utility", "shallowCopy [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "shallowCopy [source_eocopying]", source instanceof EOCopying);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Making shallow copy of '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            EOEnterpriseObject copy = shallowCopyWithoutRelationships(source);
            shallowCopyRelatedObjects(source, copy);

            JassAdditions.post("EOCopying.Utility", "shallowCopy [copy_made]", copy != null);

            return copy;
        }



        /**
         * Returns a deep copy of this object, the attribute values are copied and the relationships are copied by calling copy(NSMutableDictionary) on them.  Thus each related will be copied by its own reference, shallow, deep, or custom duplicate(NSMutableDictionary) method.  The copy is registered with copiedObjects as soon as it is created so that circular relationships can be accomodated.  This method of copying is suitable for duplicating complex graphs of objects.
         *
         * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
         * @param source - the EOEnterpriseObject to copy
         * @return a copy of this object
         */
        public static EOEnterpriseObject deepCopy(NSMutableDictionary copiedObjects, EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.Utility", "deepCopy [copiedObjects_not_null]", copiedObjects != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopy [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopy [source_eocopying]", source instanceof EOCopying);
            JassAdditions.pre("EOCopying.Utility", "deepCopy [not_copied]",
                    copiedObjects.objectForKey(source.editingContext().globalIDForObject(source)) == null);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Making deep copy of '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            EOEnterpriseObject copy = newInstance(source);

            // Handle circular relationships by registering this object right away.
            EOGlobalID globalID = source.editingContext().globalIDForObject(source);
            copiedObjects.setObjectForKey(copy, globalID);

            copyAttributes(source, copy);
            deepCopyRelatedObjects(copiedObjects, source, copy);

            JassAdditions.post("EOCopying.Utility", "deepCopy [copy_made]", copy != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopy [copy_recorded]",
                    copiedObjects.objectForKey(source.editingContext().globalIDForObject(source)) != null);

            return copy;
        }



        /**
         * This creates and returns a new instance of the same Entity as source.  When an EO object is created it can already have some relationships and attributes set.  These can come from to one relationships that are marked as 'owns destination' and also from the effects of awakeFromInsertion().  Preset attributes should be overwritten when all attributes are copied, but the relationships need some special handling.  See the method cleanRelationships(NSMutableDictionary,EOEnterpriseObject, EOEnterpriseObject) for details on what is done.  This method can be used when creating custom implementations of the duplicate() method in EOCopying.  
         *
         * @param source the EOEnterpriseObject to copy
         * @return a new instance of the same Entity as source
         */
        public static EOEnterpriseObject newInstance(EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.Utility", "newInstance [valid_source]", source != null);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Making new instance of '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            EOEnterpriseObject newInstance = EOUtilities.createAndInsertInstance(source.editingContext(), source.entityName());

            cleanRelationships(source, newInstance);

            JassAdditions.post("EOCopying.Utility", "newInstance [newInstance_made]", newInstance != null);

            return newInstance;
        }



        /**
          * When an EO object is created it can already have some relationships set.  This can come from to one relationships that are marked as 'owns destination' and also from the effects of awakeFromInsertion() and need some special handling prior to making a copy.
         * <ol>
         * <li>All objects are disconnected from the relationship.</li>
         * <li>If a disconnected object has a temporary EOGlobalID it is deleted.</li>
         * </ol>
         *
         * @param source the EOEnterpriseObject that copy was created from
         * @param copy the newly instantiated copy of source that needs to have its relationships cleaned
         */
        public static void cleanRelationships(EOEnterpriseObject source, EOEnterpriseObject copy)
        {
            JassAdditions.pre("EOCopying.Utility", "newInstance [valid_copy]", copy != null);
            JassAdditions.pre("EOCopying.Utility", "newInstance [valid_source]", source != null);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Cleaning related objects in copy of '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            EOEditingContext ec = source.editingContext();

            // To-Many relationships
            Enumeration relationshipEnumerator = copy.toManyRelationshipKeys().objectEnumerator();
            while (relationshipEnumerator.hasMoreElements())
            {
                String relationshipName = (String) relationshipEnumerator.nextElement();
                NSArray relatedObjects = (NSArray) copy.valueForKey(relationshipName);

                if (relatedObjects.count() > 0)
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("Removing objects in to-many relationship " + relationshipName);
                    }

                    // TODO Investigate whether a to-many with owns destination will auto create objects.  I have no idea how to handle this other that to remove and delete them.
                    Enumeration relatedObjectEnumerator = new NSArray(relatedObjects).objectEnumerator();
                    while (relatedObjectEnumerator.hasMoreElements())
                    {
                        EOEnterpriseObject relatedObject = (EOEnterpriseObject) relatedObjectEnumerator.nextElement();

                        copy.removeObjectFromBothSidesOfRelationshipWithKey(relatedObject, relationshipName);
                        EOGlobalID globalID = ec.globalIDForObject(relatedObject);
                        if (globalID.isTemporary())
                        {
                            ec.deleteObject(relatedObject);
                        }
                    }
                }
            }

            // To-one relationships
            relationshipEnumerator = copy.toOneRelationshipKeys().objectEnumerator();
            while (relationshipEnumerator.hasMoreElements())
            {
                String relationshipName = (String) relationshipEnumerator.nextElement();
                EOEnterpriseObject relatedObject = (EOEnterpriseObject) copy.valueForKey(relationshipName);
                if (relatedObject != null)
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("Removing object in to-one relationship " + relationshipName);
                    }

                    copy.removeObjectFromBothSidesOfRelationshipWithKey(relatedObject, relationshipName);
                    EOGlobalID globalID = source.editingContext().globalIDForObject(relatedObject);
                    if (globalID.isTemporary())
                    {
                        source.editingContext().deleteObject(relatedObject);
                    }
                }
            }
        }



        /**
          * This copies the attributes from the source EOEnterpriseObject to the destination.  Only attributes which are class properties are copied.  However if an attribute is a class property and also used in a relationship it is assumed to be an exposed primary or forign key and not copied.  Such attributes are set to null.  See exposedKeyAttributeNames for details on how this is determined.  It can be used when creating custom implementations of the duplicate() method in EOCopying.  
          *
          * @param source - the EOEnterpriseObject to copy attribute values from
          * @param destination - the EOEnterpriseObject to copy attribute values to
          */
        public static void copyAttributes(EOEnterpriseObject source, EOEnterpriseObject destination)
        {
            JassAdditions.pre("EOCopying.Utility", "copyAttributes [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "copyAttributes [valid_destination]", destination != null);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Copying attributes for '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            // Note that we can not just do this: destination.takeValuesFromDictionary(source.valuesForKeys(source.attributeKeys())) as it will fail to make a true copy where the source contains null values and the destination has had those corresponding values set in awakeFromInsertion.

            NSArray exposedKeyAttributeNames = exposedKeyAttributeNames(source);
            Enumeration attributeNameEnumerator = source.attributeKeys().objectEnumerator();
            while (attributeNameEnumerator.hasMoreElements())
            {
                String attributeName = (String) attributeNameEnumerator.nextElement();

                if (exposedKeyAttributeNames.containsObject(attributeName))
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("Nulling exposed key " + attributeName);
                    }

                    destination.takeStoredValueForKey(null, attributeName);
                }
                else
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("Copying attribute " + attributeName + ", value " + source.valueForKey(attributeName));
                    }

                    destination.takeStoredValueForKey(source.storedValueForKey(attributeName), attributeName);
                }
            }
        }



        /**
          * Returns an array of attribute names from the EOEntity of source that are used in the primary key, or in forming relationships.  These can be presumed to be exposed primary or foreign keys and handled accordingly when copying an object.
          *
          * @param source - the EOEnterpriseObject to copy attribute values from
          * @return an array of attribute names from the EOEntity of source that are used in forming relationships.
          *
          **/
        public static NSArray exposedKeyAttributeNames(EOEnterpriseObject source)
        {
            JassAdditions.pre("EOCopying.Utility", "copyAttributes [valid_source]", source != null);

            EOEditingContext ec = source.editingContext();
            EOEntity entity = EOUtilities.entityForObject(ec, source);

            // These are cached on EOEntity name as an optimization.
            if (exposedKeyAttributeDictionary == null)
            {
                exposedKeyAttributeDictionary = new NSMutableDictionary();
            }

            NSMutableArray exposedKeyAttributeNames = (NSMutableArray) exposedKeyAttributeDictionary.objectForKey(entity.name());

            if (exposedKeyAttributeNames == null)
            {
                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Checking " + entity.name() + " for exposed keys");
                }

                exposedKeyAttributeNames = new NSMutableArray(entity.primaryKeyAttributeNames());
                NSArray publicAttributeNames = source.attributeKeys();

                Enumeration relationshipEnumerator = entity.relationships().objectEnumerator();
                while (relationshipEnumerator.hasMoreElements())
                {
                    EORelationship relationship = (EORelationship) relationshipEnumerator.nextElement();

                    Enumeration attributeEnumerator = relationship.sourceAttributes().objectEnumerator();
                    while (attributeEnumerator.hasMoreElements())
                    {
                        EOAttribute attribute = (EOAttribute) attributeEnumerator.nextElement();
                        String attributeName = attribute.name();
                        if (publicAttributeNames.containsObject(attributeName))
                        {
                            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                            {
                                NSLog.debug.appendln("--> Attribute " + attributeName + " is exposed, excluding...");
                            }

                            exposedKeyAttributeNames.addObject(attributeName);
                        }
                    }
                }
                exposedKeyAttributeDictionary.setObjectForKey(exposedKeyAttributeNames, entity.name());
            }

            JassAdditions.post("EOCopying.Utility", "exposedKeyAttributeNames [valid_result]", exposedKeyAttributeNames != null);

            return exposedKeyAttributeNames;
        }



        /**
         * This copies related objects from the source EOEnterpriseObject to the destination by reference.  Only relationships which are class properties are copied.  It can be used when creating custom implementations of the duplicate() method in EOCopying.  
         *
         * @param source - the EOEnterpriseObject to copy attribute values from
         * @param destination - the EOEnterpriseObject to copy attribute values to
         */
        public static void shallowCopyRelatedObjects(EOEnterpriseObject source, EOEnterpriseObject destination)
        {
            JassAdditions.pre("EOCopying.Utility", "shallowCopyRelatedObjects [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "shallowCopyRelatedObjects [source_eocopying]", source instanceof EOCopying);
            JassAdditions.pre("EOCopying.Utility", "shallowCopyRelatedObjects [valid_destination]", destination != null);
            JassAdditions.pre("EOCopying.Utility", "shallowCopyRelatedObjects [destination_eocopying]", destination instanceof EOCopying);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Shallow copying relationships for '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            Enumeration relationshipEnumerator = source.toManyRelationshipKeys().objectEnumerator();
            while (relationshipEnumerator.hasMoreElements())
            {
                String relationshipName = (String) relationshipEnumerator.nextElement();
                NSArray originalObjects = (NSArray) source.valueForKey(relationshipName);

                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Copying " + originalObjects.count() + " objects for relationship " + relationshipName);
                }

                for (int i = 0, count = originalObjects.count(); i < count; i++)
                {
                    EOEnterpriseObject originalRelated = (EOEnterpriseObject) originalObjects.objectAtIndex(i);
                    destination.addObjectToBothSidesOfRelationshipWithKey(referenceCopy(originalRelated), relationshipName);
                }
            }

            relationshipEnumerator = source.toOneRelationshipKeys().objectEnumerator();
            while (relationshipEnumerator.hasMoreElements())
            {
                String relationshipName = (String) relationshipEnumerator.nextElement();
                EOEnterpriseObject originalRelated = (EOEnterpriseObject) source.valueForKey(relationshipName);
                if (originalRelated != null)
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("Copying object for relationship " + relationshipName);
                    }

                    destination.addObjectToBothSidesOfRelationshipWithKey(referenceCopy(originalRelated), relationshipName);
                }
            }
        }



        /**
         * This copies related objects from the source EOEnterpriseObject to the destination by calling copy(NSMutableDictionary) on them.  Thus each related will be copied by its own reference, shallow, deep, or custom duplicate(NSMutableDictionary) method.  This method of copying is suitable for duplicating complex graphs of objects.  Only relationships which are class properties are copied.  It can be used when creating custom implementations of the duplicate() method in EOCopying.  
         *
         * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from
         * @param source - the EOEnterpriseObject to copy attribute values from
         * @param destination - the EOEnterpriseObject to copy attribute values to
         */
        public static void deepCopyRelatedObjects(NSMutableDictionary copiedObjects, EOEnterpriseObject source, EOEnterpriseObject destination)
        {
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [source_eocopying]", source instanceof EOCopying);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [valid_destination]", destination != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [destination_eocopying]", destination instanceof EOCopying);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [valid_copiedObjects]", copiedObjects != null);

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Deep copying relationships for '" + source.entityName() + "' ID: " + prettyPrintPK(source));
            }

            EOEntity entity = EOUtilities.entityForObject(source.editingContext(), source);

            Enumeration relationshipEnumerator = entity.relationships().objectEnumerator();
            while (relationshipEnumerator.hasMoreElements())
            {
                EORelationship relationship = (EORelationship) relationshipEnumerator.nextElement();
                if (entity.classProperties().containsObject(relationship))
                {
                    deepCopyRelationship(copiedObjects, source, destination, relationship);
                }
            }
        }



        /**
          * This copies the object(s) for the named relationship from the source EOEnterpriseObject to the destination by calling copy(NSMutableDictionary) on them.  Thus each related will be copied by its own reference, shallow, deep, or custom duplicate(NSMutableDictionary) method.  It can be used when creating custom implementations of the duplicate() method in EOCopying.  
         *
         * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from
         * @param source - the EOEnterpriseObject to copy attribute values from
         * @param destination - the EOEnterpriseObject to copy attribute values to
         * @param relationship - the EORelationship to copy
         */
        public static void deepCopyRelationship(NSMutableDictionary copiedObjects, EOEnterpriseObject source, EOEnterpriseObject destination,
                EORelationship relationship)
        {
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [valid_copiedObjects]", copiedObjects != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [valid_source]", source != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [source_eocopying]", source instanceof EOCopying);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [valid_destination]", destination != null);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [destination_eocopying]", destination instanceof EOCopying);
            JassAdditions.pre("EOCopying.Utility", "deepCopyRelatedObjects [valid_relationship]", relationship != null);

            String relationshipName = relationship.name();
            if (relationship.isToMany())
            {
                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Copying 2-M '" + relationshipName + "' from '" + source.entityName() + "' ID: " + prettyPrintPK(source));
                }

                NSArray originalObjects = (NSArray) source.valueForKey(relationshipName);
                NSArray destinationObjects = (NSArray) destination.valueForKey(relationshipName);

                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Copying " + originalObjects.count() + " for relationship " + relationshipName);
                }

                for (int i = 0, count = originalObjects.count(); i < count; i++)
                {
                    EOEnterpriseObject original = (EOEnterpriseObject) originalObjects.objectAtIndex(i);

                    EOEnterpriseObject originalCopy = ((EOCopying) original).copy(copiedObjects);

                    // This is a tricky part.  Making the copy in the previous line can set the relationship that we are about to set.  We need to check for this so that we do not create duplicated relationships.
                    if (!destinationObjects.containsObject(originalCopy))
                    {
                        destination.addObjectToBothSidesOfRelationshipWithKey(originalCopy, relationshipName);
                    }
                }
            }
            else
            {
                EOEnterpriseObject original = (EOEnterpriseObject) source.valueForKey(relationshipName);

                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Copying 2-1 '" + relationshipName + "' from '" + source.entityName() + "' ID: " + prettyPrintPK(source));
                }

                if (original != null)
                {
                    if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                    {
                        NSLog.debug.appendln("Copying object for relationship " + relationshipName);
                    }

                    destination.addObjectToBothSidesOfRelationshipWithKey(((EOCopying) original).copy(copiedObjects), relationshipName);
                }
            }
        }
    }



}
