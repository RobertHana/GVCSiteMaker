package net.global_village.eofextensions;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.eof.*;
import er.extensions.foundation.*;



/**
 * Extension of EOGenericRecord to add additional EO functionality. Also implements EOCopying,
 * HandleOptimisticLockingConflict, and implements methods to reset (clear) values cached in
 * instance variables.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 10$
 */
public class GenericRecord extends EOGenericRecord implements EOCopying
{


    /**
     * @return primary key value for this object
     */
    public static Object primaryKeyValue(com.webobjects.eocontrol.EOGenericRecord record)
    {
        /** require [is_in_editingContext] record.editingContext() != null;  **/
        NSDictionary pkDictionary = EOUtilities.primaryKeyForObject(record.editingContext(), record);
        if (pkDictionary.count() > 1)
        {
            throw new RuntimeException("primaryKeyValue called for entity with compound key: " + record.entityName());
        }

        return pkDictionary.allValues().lastObject();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Type coercing version of EOUtilities.objectMatchingKeyAndValue.  Uses EOAttribute.validateValue
     * to convert value into the correct type (e.g. String to Number) if it is not of the correct type.
     * FrontBase does this in the plugin, but other databases do not (MySQL, MSSQL).
     *
     * @param ec editing context to return EO in
     * @param entityName name of the EOEntity to fetch object for
     * @param attributeName name of attribute corresponding to value
     * @param value the value of the attribute to qualify on
     *
     * @return EO matching value for attributeName
     * @throws EOObjectNotAvailableException if product with given ID does not exist
     */
    public static EOEnterpriseObject objectMatchingKeyAndValue(EOEditingContext ec,
                                                               String entityName,
                                                               String attributeName,
                                                               Object value)
    {
        /** require [valid_ec] ec != null;
                    [valid_entityName] entityName != null && EOUtilities.entityNamed(ec, entityName) != null;
                    [valid_ec] attributeName != null && EOUtilities.entityNamed(ec, entityName).attributeNamed(attributeName) != null;
         **/
        EOEntity entity = EOUtilities.entityNamed(ec, entityName);
        value = entity.attributeNamed(attributeName).validateValue(value);
        return EOUtilities.objectMatchingKeyAndValue(ec, entityName, attributeName, value);
    }



    /**
     * Designated constuctor.
     */
    public GenericRecord()
    {
        super();
    }



    /**
     * @return the <code>EOEntity</code> associated with this object
     */
    public EOEntity entity()
    {
        return EOObject.entityForSelf(this);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return the globalID of this object
     */
    public EOGlobalID globalID()
    {
        /** require [in_ec] editingContext() != null;  **/

        return editingContext().globalIDForObject(this);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @param attributeName the attribute name of the <code>EOAttribute</code> to return
     * @return the <code>EOAttribute</code> (from this object's associated <code>EOEntity</code>) which corresponds
     * to the passed name or null if there is no matching attribute
     */
    public EOAttribute attributeNamed(String attributeName)
    {
        /** require [valid_attributeName_param] attributeName != null; **/

        return entity().attributeNamed(attributeName);
    }



    /**
     * @param relationshipName the name of the relationship to return
     * @return the <code>EORelationship</code> (from this object's associated <code>EOEntity</code>) which corresponds
     * to the passed name or null if there is no matching relationship
     */
    public EORelationship relationshipNamed(String relationshipName)
    {
        /** require [valid_relationshipName_param] relationshipName != null; **/

        return  entity().relationshipNamed(relationshipName);
    }




    /**
     * @param propertyName the property name of the EOAttribute or EORelationship
     * @return the <code>EOAttribute</code> or <code>EORelationship</code> (from this object's associated
     * <code>EOEntity</code>) which corresponds to the passed name or null if there is no matching property
     */
    public EOProperty propertyNamed(String propertyName)
    {
        /** require [valid_propertyName_param] propertyName != null; **/

        return EOObject.propertyWithName(this, propertyName);
    }



    /**
     * Detecting WO 5 dummy fault EOs.  The code below is a translation of code posted here:
     * From http://wodeveloper.com/omniLists/eof/2001/November/msg00023.html
     *
     * As of WO4.5, EOF resolves faults for objects that don't exist in the database to a dummy object with mostly empty attributes (except those
    that are set in -init), whereas earlier versions of EOF threw a _fireFault exception.  This method detects whether the receiver is such a dummy object.
     *
     *  A distinguishing feature of these dummy EOs is that they don't have a corresponding database context snapshot, so we use this to detect its dummy-ness.  This seems more robust than checking if all or most attributes are empty, since these can be changed by client code.
     *
     *  Dummy fault EOs should be removed from the editingContext (using forgetObject) before it is invalidated (invalidateAllObjects or invalidateObjectWithGlobalID), otherwise an unrecoverable "decrementSnapshotCountForGlobalID - unable to decrement snapshot count for object with global ID" exception is thrown by EOF.<br><br>
     *
     * See also: http://www.omnigroup.com/mailman/archive/eof/2001-May/001988.html andfollowing.
     */
    public boolean isDummyFaultEO()
    {
        return EOObject.isDummyFaultEO(this);
    }



    /**
     * EOCopying interface. Returns a copy of this object, copying related objects as well.  The actual copy mechanism (by reference, shallow, deep, or custom) for each object is up to the object being copied.  If a copy already exists in <code>copiedObjects</code>, then that is returned instead of making a new copy.  This allows complex graphs of objects, including those with cycles, to be copied without producing duplicate objects.  The grapch of copied objects will be the same regardless of where copy is started with two exceptions: it is started on a reference copied object or a reference copied object is the only path between two disconnected parts of the graph.  In these cases the reference copied object prevents the copy from following the graph further.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this object
     */
    public EOEnterpriseObject copy(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

       return EOCopying.DefaultImplementation.copy(copiedObjects, this);

        /** ensure [copy_made] Result != null; [copy_recorded] copiedObjects.objectForKey(editingContext().globalIDForObject(this)) != null;  **/
    }



    /**
     * EOCopying interface. Convenience cover method for copy(NSMutableDictionary) that creates the dictionary internally.  You can use this to start the copying of a graph if you have no need to reference the dictionary.
     *
     * @return a copy of this object
     */
    public EOEnterpriseObject copy()
    {
        return copy(new NSMutableDictionary());

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * EOCopying interface. Returns a copy of this object.  Each EOEnterpriseObject can override this this to produce the actual copy by an appropriate mechanism (reference, shallow, deep, or custom).  The default is a deep copy.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this object
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return EOCopying.Utility.deepCopy(copiedObjects, this);

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Overriden to clear cached values when object is saved in another EC, the snapshot is updated.
     */
    public void turnIntoFault(EOFaultHandler faultHandler)
    {
        super.turnIntoFault(faultHandler);
        clearCachedValues();
    }



    /**
     * Overridden to clear cached values when object reverted in ec.
     */
    public void updateFromSnapshot(NSDictionary aSnapshot)
    {
        super.updateFromSnapshot(aSnapshot);
        clearCachedValues();
    }



    /**
     * Clears all cached values.  This method should be overridden in subclasses to clear the actual cached values.
     */
    public void clearCachedValues()
    {
    }



    /**
     * Forces this object to be re-read from the database; the snapshot and all editing contexts
     * holding this object are updated. Merging or overwriting of the changes is then handled by
     * the editing context delegate. This method takes no action if the object is pending insertion.
     */
    public void refreshObject()
    {
        /** require [in_ec] editingContext() != null;  **/
        EOObject.refreshObject(this);
    }



    /**
     * Forces the contents of the named to-many relationship to be re-read from the database; all
     * editing contexts holding this relationship are updated.
     */
    public void refreshRelationshipNamed(String name)
    {
        /** require [in_ec] editingContext() != null;
                    [valid_name] name != null;
                    [is_relationship] relationshipNamed(name) != null;
                    [is_to_many_relationship] relationshipNamed(name).isToMany();    **/
        ERXEOControlUtilities.clearSnapshotForRelationshipNamed(this, name);
    }



    /**
     * Reverts any changes in this objects values back to the state last fetched.  This will not refetch the
     * data from the database.  This method takes no action if the object is pending insertion or deletion.
     */
    public void revertToSaved()
    {
        /** require [in_ec] editingContext() != null;  **/
        EOObject.revertToSaved(this);
         /** ensure [snapshots_match] (editingContext().insertedObjects().containsObject(this) || editingContext().deletedObjects().containsObject(this)) ||
                                     snapshot().equals(editingContext().committedSnapshotForObject(this)); **/
    }





    /**
     * Returns true if the first object is equal to the second object.  This method treats an object with a value NSKeyValueCoding.NullValue to be equal to null.
     * They are equal if they are either both null or both are not null and first.equals(second) evaluates to true.
     *
     * @param first the first object to compare
     * @param second the second object to compare
     * @return true if the first object is equal to the second object
     */
    public static boolean equals(Object first, Object second)
    {
        boolean firstIsNull = first == null || first.equals(NSKeyValueCoding.NullValue);
        boolean secondIsNull = second == null || second.equals(NSKeyValueCoding.NullValue);

        return ((firstIsNull && secondIsNull) || ((first != null) && (first.equals(second))));
    }



    /**
     * Returns <code>true</code> if the current value for the property named propertyName is not
     * the same as the committed value.
     *
     * @see #equals(Object, Object)
     * @param propertyName the name of the property to compare
     * @return <code>true</code> if the current value for the property named propertyName is not
     * the same as the committed value
     */
    public boolean hasChanged(String propertyName)
    {
        /** require [valid_name] propertyName != null;
                    [has_property] entity().anyAttributeNamed(propertyName) != null || entity().anyRelationshipNamed(propertyName) != null;
         **/
        return ! equals(valueForKey(propertyName), committedValueFor(propertyName));
    }



    /**
     * @return primary key value for this object
     */
    public Object primaryKeyValue()
    {
        /** require [is_in_editingContext] editingContext() != null; **/
        return primaryKeyValue(this);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Return true if this EO is a new, unsaved object.  This is determined by
     * <code>globalID().isTemporary()</code>.
     *
     * @return true if this EO is a new, unsaved object
     */
    public boolean isNewEO()
    {
        /** require [is_in_editingContext] editingContext() != null;  **/
        return globalID().isTemporary();
    }



    /**
     * Returns the committed snapshot for this EO.  The committed snapshot is the values
     * last fetched from or saved to the database and do not include any local, unsaved
     * edits.
     *
     * This method exists because
     * {@link com.webobjects.eocontrol.EOEditingContext#committedSnapshotForObject EOEditingContext.committedSnapshotForObject()}
     * gives unexpected results for newly inserted objects if
     * {@link com.webobjects.eocontrol.EOEditingContext#processRecentChanges() EOEditingContext.processRecentChanges()}
     * has been called. This method always returns a dictionary whose values are
     * all NSKeyValueCoding.NullValue in the case of a newly inserted object.
     *
     * @return the committed snapshot for this EO
     */
    public NSDictionary committedSnapshot() {
        if (!isNewEO()) {
            return editingContext().committedSnapshotForObject(this);
        }

        NSArray keys = allPropertyKeys();
        NSMutableDictionary allNullDict = new NSMutableDictionary(keys.count());
        ERXDictionaryUtilities.setObjectForKeys(allNullDict, NSKeyValueCoding.NullValue, keys);
        return allNullDict;
    }



    /**
     * Returns the committed value associated with <code>key</code> based from its committed snapshot.
     *
     * @param key the key to be searched for
     * @return the value for <code>key</code>, or <code>null</code> if no value is associated with key
     */
    public Object committedValueFor(String key)
    {
        /** require [key_not_null] key != null;  **/
        Object value = committedSnapshot().objectForKey(key);
        return value.equals(NSKeyValueCoding.NullValue) ? null : value;
    }



    /**
     * Overridden to allow bindings like eo.committedValueFor.name.
     *
     * @see #committedValueFor(String)
     * @see com.webobjects.eocontrol.EOCustomObject#valueForKeyPath(java.lang.String)
     *
     * @param keyPath keyPath to return value for
     * @return committed value for key if keyPath starts with "committedValueFor."
     * @throws IllegalArgumentException if keyPath starts with "committedValueFor." and has a key path after that (not a simple key)
     */
    public Object valueForKeyPath(String keyPath)
    {
        if (keyPath.startsWith("committedValueFor."))
        {
            String key = keyPath.substring(keyPath.indexOf('.') + 1);
            if (key.indexOf('.') > -1)
            {
                throw new IllegalArgumentException(keyPath + " contains a key path, not a simple key");
            }
            return committedValueFor(key);
        }

        return super.valueForKeyPath(keyPath);
    }



    /**
     * Overrides validateForSave to call validateForSave(NSMutableArray) and add the result of that
     * to what the super class produces.  Subclasses should implement validateForSave(NSMutableArray)
     * rather than the standard EOF method.  Doing so makes validateForSave(NSMutableArray) much
     * shorter and easier to write.
     *
     * @see com.webobjects.eocontrol.EOCustomObject#validateForSave()
     * @see #validateForSave(NSMutableArray)
     * @throws ValidationException if validation error are found
     */
    public void validateForSave() throws ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObject(e);
        }

        validateForSave(exceptions);

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Override this method to perform custom validations.  Calling super's implementation
     * is a good idea, it is needed when using EO inheritance if the super class has any
     * custom  validations.
     *
     * @see #validateForSave()
     *
     * @param exceptions list of exceptions that can be added to
     * @throws ValidationException if validation error are found
     */
    protected void validateForSave(NSMutableArray exceptions)
    {
        /** require [valid_list] exceptions != null;  **/
    }


}
