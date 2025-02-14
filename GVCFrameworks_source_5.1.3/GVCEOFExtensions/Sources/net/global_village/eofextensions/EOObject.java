package net.global_village.eofextensions;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Methods added to extend the functionality of EOObject
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 16$
 */
public class EOObject
{

    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private EOObject()
    {
        super();
    }



    /**
     * Returns the <code>EOEntity</code> associated with the object.
     *
     * @param theObject the object to be evaluated
     * @return the <code>EOEntity</code> associated with the object.
     */
    public static EOEntity entityForSelf(EOEnterpriseObject theObject)
    {
        /** require [valid_param] theObject != null; **/

        return EOUtilities.entityNamed(theObject.editingContext(), theObject.entityName());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Convenience method that returns the global ID of the object.
     *
     * @param theObject the object to be evaluated
     * @return the globalID of the object.
     */
    public static EOGlobalID globalID(EOEnterpriseObject theObject)
    {
        /** require [valid_param] theObject != null; **/
        JassAdditions.pre("EOObject", "globalID", theObject.editingContext() != null);

        return theObject.editingContext().globalIDForObject(theObject);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the <code>EOAttribute</code> (from the object's associated <code>EOEntity</code>) which corresponds to the passed name.
     *
     * @param theObject the object to be evaluated
     * @param attributeName the attribute name of the <code>EOAttribute</code> to return
     * @return the <code>EOAttribute</code> (from the object's associated <code>EOEntity</code>) which corresponds to the passed name.
     */
    public static EOAttribute attributeWithName(EOEnterpriseObject theObject,
                                                String attributeName)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_attributeName_param] attributeName != null;
        [object_is_entity] EOObject.entityForSelf(theObject) != null; **/

        return EOObject.entityForSelf(theObject).attributeNamed(attributeName);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the <code>EORelationship</code> (from the object's associated <code>EOEntity</code>) which corresponds to the passed name.
     *
     * @param theObject the object to be evaluated
     * @param relationshipName the name of the relationship to be fetched
     * @return the <code>EORelationship</code> (from the object's associated <code>EOEntity</code>) which corresponds to the passed name.
     */
    public static EORelationship relationshipWithName(EOEnterpriseObject theObject,
                                                      String relationshipName)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_relationshipName_param] relationshipName != null;
        [object_is_entity] entityForSelf(theObject) != null; **/

        return  EOObject.entityForSelf(theObject).relationshipNamed(relationshipName);

        /** ensure [valid_result] Result != null; **/
    }




    /**
     * Returns the <code>EOAttribute</code> or <code>EORelationship</code> (from the object's associated <code>EOEntity</code>) which corresponds to the passed name.
     *
     * @param theObject the object to be evaluated
     * @param propertyName the property name of the EOAttribute or EORelationship
     * @return the <code>EOAttribute</code> or <code>EORelationship</code> (from the object's associated <code>EOEntity</code>) which corresponds to the passed name.
     */
    public static EOProperty propertyWithName(EOEnterpriseObject theObject,
                                              String propertyName)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_propertyName_param] propertyName != null;
        [object_is_entity] entityForSelf(theObject) != null;
        [property_exists] EOObject.entityForSelf(theObject).attributeNamed(propertyName) != null ||
                          EOObject.entityForSelf(theObject).relationshipNamed(propertyName) != null;            **/

        EOProperty theProperty = EOObject.entityForSelf(theObject).attributeNamed(propertyName);

        if (theProperty == null)
        {
            theProperty = EOObject.entityForSelf(theObject).relationshipNamed(propertyName);
        }

        return theProperty;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Private method to support <code>keyPathHasEntity()</code> and <code>entityFromKeyPath()</code>
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the entity
     * @return the EOEntity associated with the object and key path
     */
    protected static EOEntity _findEntityFromKeyPath(Object theObject,
                                                   String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath); **/

        EOEntity entity = null;

        String objectKeyPath = StringAdditions.objectKeyPathFromKeyPath(aKeyPath);
        // valueForKeyPath raises if keypath not found
        try
        {
            EOEnterpriseObject object = (EOEnterpriseObject)NSKeyValueCodingAdditions.Utility.valueForKeyPath(theObject, objectKeyPath);
            entity = entityForSelf(object);
        }
        catch (Exception e)
        {
            entity = null;
        }

        return entity;
    }



    /**
     * Returns true if <code>StringAdditions.objectKeyPathFromKeyPath(aKeyPath)</code> points to an object associated with an <code>EOEntity</code>.
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the entity
     * @return true if <code>StringAdditions.objectKeyPathFromKeyPath(aKeyPath)</code> points to an object associated with an <code>EOEntity</code>.
     */
    public static boolean keyPathHasEntity(Object theObject,
                                           String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath); **/

        return _findEntityFromKeyPath(theObject, aKeyPath) != null;
    }



    /**
     * Returns the <code>EOEntity</code> instance associated with the object pointed to by <code>StringAdditions.objectKeyPathFromKeyPath(aKeyPath)</code>
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the entity
     * @return the <code>EOEntity</code> associated with the object and key path
     */
    public static EOEntity entityFromKeyPath(Object theObject,
                                             String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath);
        [keypath_has_entity] keyPathHasEntity(theObject, aKeyPath); **/

        return _findEntityFromKeyPath(theObject, aKeyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the <code>EOEnterpriseObject</code> pointed to by <code>StringAdditions.objectKeyPathFromKeyPath(aKeyPath)</code>
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the entity
     * @return the EOEnterpriseObject indicated by the object and key path, or null if there is no EO at that path
     * @throws IllegalArgumentException if aKeyPath does not indicate an EO
     */
    public static EOEnterpriseObject eoFromKeyPath(Object theObject,
                                                   String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath); **/


        String objectKeyPath = StringAdditions.objectKeyPathFromKeyPath(aKeyPath);
        // valueForKeyPath raises if keypath not found
        try
        {
            return(EOEnterpriseObject)NSKeyValueCodingAdditions.Utility.valueForKeyPath(theObject, objectKeyPath);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("", e);
        }
   }



    /**
     * Private method to support <code>attributeFromKeyPath</code> and <code>isKeyPathToAttribute</code>
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the entity
     * @return the <code>EOAttribute</code> associated with the object and key path
     */
    protected static EOAttribute _findAttributeFromKeyPath(Object theObject,
                                                         String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath);
        [keypath_has_entity] keyPathHasEntity(theObject, aKeyPath); **/

        EOEntity entity = entityFromKeyPath(theObject, aKeyPath);
        String anAttributeName = StringAdditions.propertyNameFromKeyPath(aKeyPath);
        return entity.attributeNamed(anAttributeName);
    }



    /**
     * Returns true if <code>_findAttributeFromKeyPath(theObject, aKeyPath)</code> points to an object associated with an EOAttribute.
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the attribute
     * @return true if the passed key path is a key path to the attribute
     */
    public static boolean isKeyPathToAttribute(Object theObject,
                                               String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath); **/

        if (keyPathHasEntity(theObject, aKeyPath))
        {
            return _findAttributeFromKeyPath(theObject, aKeyPath) != null;
        }
        else
        {
            return false;
        }
    }



    /**
     * Given a keypath to an attribute of an object on a page, returns the corresponding <code>EOAttribute</code>.
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the attribute
     * @return the <code>EOAttribute</code> associated with the key path
     */
    public static EOAttribute attributeFromKeyPath(Object theObject,
                                                   String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath);
        [keypath_has_entity] keyPathHasEntity(theObject, aKeyPath);
        [is_keypath_to_attribute] isKeyPathToAttribute(theObject, aKeyPath); **/

        return _findAttributeFromKeyPath(theObject, aKeyPath);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Private method to support <code>relationshipFromKeyPath()</code>
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the relationship
     * @return the <code>EORelationship</code>
     */
    protected static EORelationship _findRelationshipFromKeyPath(Object theObject,
                                                               String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath);
        [keypath_has_entity] keyPathHasEntity(theObject, aKeyPath); **/

        EOEntity entity = entityFromKeyPath(theObject, aKeyPath);
        String aRelationshipName = StringAdditions.propertyNameFromKeyPath(aKeyPath);
        return entity.relationshipNamed(aRelationshipName);
    }



    /**
     * Returns true if <code>objectKeyPathFromKeyPath(theObject, aKeyPath)</code> points to an object associated with an <code>EORelationship</code>.
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the relationship
     * @return true if <code>objectKeyPathFromKeyPath(theObject, aKeyPath)</code> points to an object associated with an <code>EORelationship</code>
     */
    public static boolean isKeyPathToRelationship(Object theObject,
                                                  String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath); **/

        if (keyPathHasEntity(theObject, aKeyPath))
        {
            return _findRelationshipFromKeyPath(theObject, aKeyPath) != null;
        }
        else
        {
            return false;
        }
    }



    /**
     * Given a keypath to a relationship of an object on a page, returns the corresponding <code>EORelationship</code>.
     *
     * @param theObject the object to be evaluated
     * @param aKeyPath the key path of the relationship
     * @return the <code>EORelationship</code> associated with the key path
     */
    public static EORelationship relationshipFromKeyPath(Object theObject,
                                                         String aKeyPath)
    {
        /** require
        [valid_theObject_param] theObject != null;
        [valid_aKeyPath_param] aKeyPath != null;
        [keypath_is_valid] StringAdditions.isValidPropertyKeyPath(aKeyPath);
        [keypath_has_entity] keyPathHasEntity(theObject, aKeyPath);
        [is_keypath_to_relationship] isKeyPathToRelationship(theObject, aKeyPath); **/

        return EOObject._findRelationshipFromKeyPath(theObject, aKeyPath);

        /** ensure [valid_result] Result != null; **/
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
    public static boolean isDummyFaultEO(EOEnterpriseObject anObject)
    {
        /** require [valid_param] anObject != null;  **/
        JassAdditions.pre("EOObject", "isDummyFaultEO [object_in_ec]", anObject.editingContext() != null);
        JassAdditions.pre("EOObject", "isDummyFaultEO [object_has_global_id]", anObject.editingContext().globalIDForObject(anObject) != null);

        boolean isDummyFaultEO = false;

        // This method will fail if the object is still a fault.  We fire the fault so that the correct result is returned.
        if (anObject.isFault())
        {
            try
            {
                anObject.willRead();
            }
            catch (EOObjectNotAvailableException e)
            {
                // The DatabaseContextDeletgate restores the "throw on failure to fetch" functionality and throws EOObjectNotAvailableException
                // That is caught here and true returned so that this method will return the expected result even when this delegate is used
                return true;
            }
        }

        EOEditingContext ec = anObject.editingContext();
        EOGlobalID globalID = ec.globalIDForObject(anObject);

        // NB. objects with temporary globalIDs legitimately have no DB snapshots, since these are by definition not yet saved to the database.
        if (! globalID.isTemporary())
        {
            // Find the EODatabaseContext instance associated with anObject, or null if no databaseContext association can be found.
            EOObjectStoreCoordinator rootStore = (EOObjectStoreCoordinator)ec.rootObjectStore();
            rootStore.lock();
            try
            {
                EODatabaseContext dbContext = (EODatabaseContext)rootStore.objectStoreForObject(anObject);
                if (dbContext == null)
                {
                    throw new RuntimeException("Could not locate EODatabaseContext for object '" +
                                               anObject + "' in editingContext: " + ec);
                }

                isDummyFaultEO = (dbContext.snapshotForGlobalID(globalID) == null);
            }
            finally
            {
                rootStore.unlock();
            }
        }

        return isDummyFaultEO;
    }





    /**
     * Forces this object to be re-read from the database; the snapshot and all editing contexts
     * holding this object are updated. Merging or overwriting of the changes is then handled by
     * the editing context delegate. This method takes no action if the object is pending insertion.
     *
     * @param anObject the object to be evaluated
     * @throws EOObjectNotAvailableException if the object being refreshed has been deleted
     */
    public static void refreshObject(EOEnterpriseObject anObject) throws EOObjectNotAvailableException
    {
        /** require [valid_theObject_param] anObject != null;
                    [in_ec] anObject.editingContext() != null;            **/

        // We can't just call editingContext().refreshObject() or editingContext().refaultObject() the object here.
        // That will result in fresh data only if the object is in a single editing context.
        // This seems like the safest way to get fresh data and inform all of the other
        // editing contexts that they need to update.
        EOEditingContext ec = anObject.editingContext();
        if ( ! ec.insertedObjects().containsObject(anObject))
        {
            EOQualifier thisObject = EOUtilities.qualifierForEnterpriseObject(ec, anObject);
            EOFetchSpecification fetchSpec = new EOFetchSpecification(entityForSelf(anObject).name(), thisObject, NSArray.EmptyArray);
            fetchSpec.setRefreshesRefetchedObjects(true);
            NSArray results = ec.objectsWithFetchSpecification(fetchSpec);

            // If we did not fetch any objects, then it has been deleted.  This EOF stack does not know
            // this yet, so we need to invalidate the object and then fire the fault to trigger an exception
            // (if the dbCtxt delegate throws), otherwise anObject becomes a Dummy Fault EO
            if (results.count() == 0)
            {
                ec.invalidateObjectsWithGlobalIDs(new NSArray(globalID(anObject)));
                anObject.willRead();
            }
        }
    }



    /**
     * Reverts any changes in this object's values back to the state last fetched.  This will not refetch the
     * data from the database.  This method takes no action if the object is pending insertion or deletion.
     *
     * @param anObject the object to be evaluated
     */
    public static void revertToSaved(EOEnterpriseObject anObject)
    {
        /** require [valid_theObject_param] anObject != null;
                    [in_ec] anObject.editingContext() != null;            **/
        EOEditingContext ec = anObject.editingContext();
        if ( ! (ec.insertedObjects().containsObject(anObject) || ec.deletedObjects().containsObject(anObject)))
        {
            // Calling editingContext().refaultObject(this) will also works but may result in a trip to the database
            // This is less work
            anObject.updateFromSnapshot(ec.committedSnapshotForObject(anObject));
        }

        /** ensure [snapshots_match] (anObject.editingContext().insertedObjects().containsObject(anObject) ||
                                      anObject.editingContext().deletedObjects().containsObject(anObject)) ||
                                     anObject.snapshot().equals(anObject.editingContext().committedSnapshotForObject(anObject)); **/
    }

}
