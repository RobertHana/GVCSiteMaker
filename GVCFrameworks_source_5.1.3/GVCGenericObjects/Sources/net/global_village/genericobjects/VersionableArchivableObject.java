package net.global_village.genericobjects;

import java.lang.reflect.Constructor;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.EOEditingContextAdditions;
import net.global_village.foundation.ExceptionConverter;
import net.global_village.foundation.JassAdditions;


/**
 * VersionableArchivableObject supports a limited versioning of objects, with only one 
 * version of an object marked as current (<code>isActive</code>).  Under this scheme, 
 * when (one or more of) the attributes of an object need to be changed, a new version 
 * is created and this new version edited.  The old version is retained intact thus 
 * preserving the integrity of any objects related to it.  This is important, for example, 
 * with objects that hold financial data that must not change for historical consistency.
 * <p>
 * To activate versioning, override the <code>shouldVersion()</code> method to return 
 * <code>true</code>.  New objects can be created in the usual manner.  When an object 
 * needs to be edited, call <code>newVersion()</code> and edit the object returned.  
 * For example: <p>
 * <pre>
 * public void editTaxRate( TaxRate taxRateToEdit )
 * {
 *     this.setTaxRate( taxRateToEdit.newVersion() );
 * }
 * </pre>
 * <p>
 * <b>NOTE</b>
 * <br><br>
 * This code assumes that the primary key is not a class property.  It will fail it this is 
 * not the case.  This can be coded around, but that has not been done.
 * This code makes shallow copies of all relationships.  Beware.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */  
public class VersionableArchivableObject extends _VersionableArchivableObject
{

    /**
     * Instance of NameComparator to be used when sorting objects.
     */
    static final public NSComparator ActiveComparator = new ActiveComparator();



    /**
     * Override this and return YES if you want your subclass to be versioned.
     * Note that every instance must return the same value from this method!  Subclasses returning
     * <code>false</code> will be archivable, but not versionable.
     *
     * @return returns whether or not versioning is turned on for this class
     */
    public boolean shouldVersion()
    {
        return false;
    }



    /**
     * Fetchs all active objects (of the Entity specified by <code>entityName</code>), qualified by aQualifier.  
     * This method should be used for fetching to avoid returning inactive objects. Subclasses may want to wrap 
     * this method to pass in their own entity name. 
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName the kind of objects you want to fetch with this method
     * @param aQualifier the qualifier to use for the fetch
     * @return all active objects from entity <code>entityName</code>, qualified by aQualifier
     */
    static public NSArray activeObjectsWithQualifier(EOEditingContext editingContext, String entityName, EOQualifier aQualifier)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_editingContext_param] editingContext != null; **/

        EOQualifier activeQualifier = EOQualifier.qualifierWithQualifierFormat("isActive = %@", new NSArray(net.global_village.foundation.GVCBoolean.trueBoolean()));
        EOQualifier combinedQualifier;

        if (aQualifier != null)
        {
            combinedQualifier = new EOAndQualifier(new NSArray(new EOQualifier[] {activeQualifier, aQualifier}));
        }
        else
        {
            combinedQualifier = activeQualifier;
        }

        return EOEditingContextAdditions.objectsWithQualifier(editingContext, entityName, combinedQualifier);

        /** ensure [valid_result] Result != null; **/
    }

    
    
    /**
     * Fetchs all active objects (of the Entity specified by <code>entityName</code>), 
     * qualified by aQualifier, sorted by comparator.  This method should be used for 
     * fetching to avoid returning inactive objects. Throws a <code>NSGenericException</code> 
     * if the class corresponding to <code>entityName</code> cannot be found.  Subclasses 
     * may want to wrap this method to pass in their own entity name. 
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName the kind of objects you want to fetch with this method
     * @param aQualifier the qualifier to use for the fetch
     * @return all active objects from entity <code>entityName</code>, qualified by aQualifier, sorted by comparator
     */
    static public NSArray orderedActiveObjectsWithQualifier(EOEditingContext editingContext,
                                                            String entityName,
                                                            EOQualifier aQualifier,
                                                            NSComparator comparator)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_editingContext_param] editingContext != null; **/

        NSArray activeObjects = VersionableArchivableObject.activeObjectsWithQualifier(editingContext, entityName, aQualifier );
        NSArray orderedList;
//FIXME Comments
        try
        {
            orderedList = activeObjects.sortedArrayUsingComparator( comparator );
        }
        catch (com.webobjects.foundation.NSComparator.ComparisonException e)
        {
            throw new ExceptionConverter(e);
        }

        return orderedList;

        /** ensure [valid_result] Result != null; **/
   }


    
    /**
     *  Fetches all active objects sorted by comparator. Throws a <code>NSGenericException</code> 
     *  if the class corresponding to <code>entityName</code> cannot be found.  Subclasses 
     *  may want to wrap this method to pass in their own entity name. 
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName the kind of objects you want to fetch with this method
     * @return NSArray of the desired objects
     */
    static public NSArray orderedActiveObjects(EOEditingContext editingContext,
                                               String entityName,
                                               NSComparator comparator)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_editingContext_param] editingContext != null; **/

        NSArray objects = VersionableArchivableObject.orderedActiveObjectsWithQualifier(editingContext, entityName, null, comparator);
        return objects;

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Fetchs all objects sorted by comparator. Throws a <code>NSGenericException</code> 
     * if the class corresponding to <code>entityName</code> cannot be found. Subclasses 
     * may want to wrap this method to pass in their own entity name. 
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param entityName the kind of objects you want to fetch with this method
     * @return NSArray of the desired objects
     */
    static public NSArray orderedObjects(EOEditingContext editingContext, String entityName, NSComparator comparator)
    {
        /** require
        [valid_entityName_param] entityName != null;
        [valid_editingContext_param] editingContext != null; **/

        NSArray orderedList = EOUtilities.objectsForEntityNamed(editingContext, entityName );

        try
        {
            orderedList = orderedList.sortedArrayUsingComparator( comparator );
        }
        catch (com.webobjects.foundation.NSComparator.ComparisonException e)
        {
            throw new ExceptionConverter(e);
        }
        
        return orderedList;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Comparator to sort objects by <code>active</code>.
     */
    static protected class ActiveComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            return (((VersionableArchivableObject)object1)).isActive().compareTo(((VersionableArchivableObject)object2).isActive());
        }
    }



    /**
     * Set defaults.
     */
    public void awakeFromInsertion(EOEditingContext anEditingContext)
    {
        super.awakeFromInsertion(anEditingContext);
        setIsActive(net.global_village.foundation.GVCBoolean.trueBoolean());

        /** ensure [is_active] isActive().isTrue(); **/
    }



    /**
     * Returns a copy of this VersionableArchivableObject object as a reference to this 
     * object: objects should only be duplicated by calling newVersion().
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a reference to this object
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return this;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Call this method to create a new version of a versioned object.
     *
     * @return VersionableArchivableObject the new version of the versioned object
     */
    public VersionableArchivableObject newVersion()
    {
        /** require
        [has_editing_context] editingContext() != null;
        [is_active] isActive().isTrue(); **/
        // Use this method instead of jass's contract to get around a problem with the internal method generated...
        JassAdditions.pre("VersionableArchivableObject", "newVersion", shouldVersion());

        VersionableArchivableObject newVersion;

        try
        {
            Class[] parameterTypes = new Class[] {};
            Class[] parameters = new Class[] {};
            Constructor constructor = getClass().getConstructor( parameterTypes );

            newVersion = (VersionableArchivableObject)constructor.newInstance( (java.lang.Object[]) parameters );
        }
        catch ( java.lang.Exception ex ) // These exceptions "shouldn't happen"
        {
            String message = getClass().getName() + ".newVersion() - ";
            message = message + "exception raised trying to construct object.  ";
            message = message + "there may be something wrong with your constructor.  ";
            message = message + "exception Name is: " + ex.getClass().getName() + " ";
            message = message + "exception description is: " + ex.toString() + " " ;
            message = message + "exception detailed message is: " + ex.getMessage();

            throw new RuntimeException(message);
        }

        editingContext().insertObject(newVersion);
        newVersion.takeValuesFromDictionary( this.editingContext().committedSnapshotForObject(this) );
        setIsActive(net.global_village.foundation.GVCBoolean.falseBoolean());

        return newVersion;

        /** ensure
        [no_longer_active] isActive().isFalse();
        [valid_result] Result != null;
        [result_is_active] Result.isActive().isTrue(); **/
    }



    /**
     * Check to ensure that invalid changes are not made to a versioning object.
     *
     * @exception com.apple.yellow.eocontrol.EOValidation.Exception thrown when the object is not valid for saving
     */
    public void validateForSave() throws com.webobjects.foundation.NSValidation.ValidationException 
    {
        com.webobjects.foundation.NSValidation.ValidationException validationException = null;
        NSMutableArray validationExceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch (com.webobjects.foundation.NSValidation.ValidationException ex)
        {
            validationExceptions.addObject(ex);
            validationExceptions.addObjectsFromArray(ex.additionalExceptions());
        }

        // globalID() wrapper method might exist in GVCEOFExtensions
        if ( editingContext().globalIDForObject(this).isTemporary())
        {
            // New Objects must have isActive == YES
            if ( ! isActive().booleanValue())
            {
                validationExceptions.addObject(new Exception("New version of class " + this.getClass().getName()
                                                             + " is marked as isActive == false"));
            }
        }
        else if (shouldVersion())
        {
            NSDictionary dictionaryOfChanges = changesFromSnapshot(editingContext().committedSnapshotForObject(this));
            boolean isStillActive = this.isActive().booleanValue();

            boolean didChangeOtherAttributes = (dictionaryOfChanges.valueForKey("isActive") == null) ||
                (dictionaryOfChanges.count() > 1);

            // Old Objects must have isActive == NO
            if (isStillActive)
            {
                validationExceptions.addObject(new Exception("Old version of class " + this.getClass().getName()
                                                               + " is still marked as isActive == true"));
            }

            // After an object is changed, only the isActive flag may be changed.
            if (didChangeOtherAttributes)
            {
                validationExceptions.addObject( new Exception( "Attempt to edit versioning class " + this.getClass().getName() + ".  " + "Changes made to " + dictionaryOfChanges ) );
            }
        }

        validationException = validationExceptions.count() == 0 ? null :
            com.webobjects.foundation.NSValidation.ValidationException.aggregateExceptionWithExceptions(validationExceptions);

        if ( validationException != null)
        {
            throw validationException;
        }
    }



    /**
     * Check to ensure that a versioned object is never deleted.
     *
     * @exception com.apple.yellow.eocontrol.EOValidation.Exception thrown when the object is not valid for deleting
     */
    public void validateForDelete() throws com.webobjects.foundation.NSValidation.ValidationException
    {
        com.webobjects.foundation.NSValidation.ValidationException exception = null;
        NSMutableArray validationExceptions = new NSMutableArray();

        try
        {
            super.validateForDelete();
        }
        catch (com.webobjects.foundation.NSValidation.ValidationException ex)
        {
            validationExceptions.addObject(ex); // not sure if this can happen
            validationExceptions.addObjectsFromArray(ex.additionalExceptions());
        }

        // Only check if this object should be versioned.
        if (shouldVersion())
        {
            validationExceptions.addObject(new com.webobjects.foundation.NSValidation.ValidationException("Attempt to delete versioned object of class " + getClass().getName()));
        }

        exception = validationExceptions.count() == 0 ? null :
            com.webobjects.foundation.NSValidation.ValidationException.aggregateExceptionWithExceptions(validationExceptions);

        if ( exception != null)
        {
            throw exception;
        }
    }



}
