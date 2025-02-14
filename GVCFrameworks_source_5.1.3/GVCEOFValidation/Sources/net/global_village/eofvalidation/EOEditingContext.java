package net.global_village.eofvalidation;

import java.lang.reflect.*;

import org.apache.log4j.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;


/**
 *  EOEditingContext overrides the saveChanges method of com.apple.yellow.eocontrol.EOEditingContext so that custom, localized validation messages can be produced for adaptor level exceptions.
 * <br>
 *  TODO:
 *  - consider if this functionality could be better (or more consistently) handled by subclassing one of the EOAdaptor... or EODatabase... classes.  The problem with this EOEditingContext approach is that not all adpators raise the proper exceptions, and that the exceptions raised may not have the needed information in them.  Subclassing at a lower level may get around this.
 * <br>
 *    Notes:
 *      (1) Violations of width / scale / precision limits (assuming EOModel does not match db)
 *          generate a NSInternalInconsistencyException which is not caught by this poser.  Observed with FrontBase.
 * <br>
 *      (2) An object having a FK reference that does not exist causes EOF to attempt to fetch that object which results
 *          in an EOAdaptorOptimisticLockingFailure or an NSInternalInconsistencyException.  Observed with FrontBase
 * <br>
 *      (3) Oracle returns the proper type of exception, but with an ORA error code rather than a more standard and
 *          useful SQLSTATE code.  This will make interpreting the exceptions more difficult.
 * <br>
 *      (4) FrontBase raises an NSInternalInconsistencyException instead of an EOGeneralAdaptorException for everything
 *          other than optimistic locking failures.  These exceptions contain very little in the way of useful information,
 *          other than the name and reason.  The userInfo consists of a list of EODatabaseOperation and EODatabaseContext
 *          which give no indication of what has failed.  The exception reason contains the actual exception as SQLSTATE
 *          values.  For example a reason will look like:
 *              Exception <FrontBase exception #> (SQLSTATE class:SQLSTATE code). <Text message>
 *          For example:
 *              Exception 357 (23:000). Integrity constraint violation...
 *          The (23:000) indicates that is it an integrity constraint violation and could be used to manufacture a more
 *          explicit exception.  FrontBase has promised to address this issue in a future release.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 23$
 *
 * TODO: Add a delegate to interpret exceptions?
 */
public class EOEditingContext extends net.global_village.eofextensions.DeleteFiresFaultWorkAroundEditingContext
{
    /** This can be set to false to disable use of the DeletePrefetcher.  */
    public static boolean useDeletePrefetcher = true;
    protected boolean shouldInstanceUseDeletePrefetcher;


    // For WO 5.1/5.2/5.3 Bug Detection
    protected Field private_processingChanges = null;

    // For WO 5.1/5.2/5.3 Bug Work around
    protected DeletePrefetcher deletePrefetcher = new DeletePrefetcher();

    private Logger logger = LoggerFactory.makeLogger();


    /**
     * Designated constructor.  Creates a new EOEditingContext object with anObjectStore as its parent object store.
     *
     * @param anObjectStore	parent object store
     */
    public EOEditingContext(EOObjectStore anObjectStore)
    {
        super(anObjectStore);
        initializePrivateFieldAccess();
        shouldInstanceUseDeletePrefetcher = EOEditingContext.useDeletePrefetcher;
    }



    /**
     * Creates a new EOEditingContext object with the default parent object store as its parent object store.
     */
    public EOEditingContext()
    {
        super();
        initializePrivateFieldAccess();
        shouldInstanceUseDeletePrefetcher = EOEditingContext.useDeletePrefetcher;
    }



    /**
     * Overridden to hack around bug in EOF.  If processing delete rules while saving fires a fault
     * that requires a trip to the DB, firing this fault scrambles the editing context's internal
     * state. This methods ensures that all faults are pre-fired.
     */
    public void deleteObject(EOEnterpriseObject object)
    {
        /** require [valid_object] object != null;  **/

        // Check for bugs in calling code
        /** check [same_ec] object.editingContext() == this;  **/

        // HACK around bug in EOF
        if (shouldInstanceUseDeletePrefetcher)
        {
            deletePrefetcher.reset();
            deletePrefetcher.prefetchDeletionPathsFrom(object);
        }

        super.deleteObject(object);
    }



    /**
    * Calls super.saveChanges() and interprets any exceptions raised.<br><br>
    * This EOEditingContext also clears the undo stack after a successful save.  This is done as a work around for a bug in EOF. There is a rather serious bug when validateForDelete() fails to allow a deletion.  This will occur if you are using the Deny delete rule, the relationship is mandatory, or you have a custom validateForDelete() method.  The error occurs in this scenario:<br>
    * 1. The editing context has multiple generations, meaning that saveChanges() has been called one or more times after one or more EOs has been created / inserted / updated.<br>
    * 2. An EO is deleted from the editing context by deleteObject().<br>
    * 3. saveChanges() fails due to an NSValidation.ValidationException raised in validateForDelete.<br>
    *
    * This result of this appears to be that undo() is called on the editing context's undo manager too many times.  Instead of rolling back to the state when saveChanges() was called it rolls back past several of previous saveChanges()!  The result of this is that the editing context shows a historical state that does not match the object store or the database.
     */
    public void saveChanges()
    {
        try
        {
            super.saveChanges();
        }
        catch (EOFValidationException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Save failed with EOFValidationException: ", e);
                logger.trace("    userInfo: " + e.userInfo());
            }
            throw e;
        }
        catch (EOGeneralAdaptorException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Save failed with EOGeneralAdaptorException: ", e);
                logger.trace("    userInfo: " + e.userInfo());
            }
            RuntimeException interpretedException =  interpretEOGeneralAdaptorException(e);

            if (logger.isTraceEnabled())
            {
                logger.trace("    interpreted to : " + interpretedException.getLocalizedMessage());
            }

            throw interpretedException;
        }
    }



    /**
     * Examines the exception and dispatches it to one of the more specific interpret...Exception methods.
     *
     * @param theException the exception as raised from saveChanges()
     * @return an exception with a localized error message and a customized userInfo dictionary
     */
    public RuntimeException interpretEOGeneralAdaptorException(EOGeneralAdaptorException theException)
    {
        /** require [valid_param] theException != null; **/

        // In case we can not interpret this exception, ensure that it is returned as is.
        RuntimeException interpretedException = theException;

        if (NSExceptionAdditions.isOptimisticLockingFailure(theException))
        {
            interpretedException = interpretOptimisticLockingFailure(theException);
        }
        else if (NSExceptionAdditions.isIntegrityConstraintViolation(theException))
        {
            interpretedException = interpretIntegrityConstraintViolation(theException);
        }
        else if (NSExceptionAdditions.isAdaptorOperationFailureException(theException))
        {
            int failedOperator = NSExceptionAdditions.failedAdaptorOperator(theException);

            switch (failedOperator)
            {
                // case EODatabaseOperation.EOAdaptorLockOperator : interpretedException = interpretLockingFailureFromException(theException); break;

                // This should never happen with integrity constraint violations handled above and barring model exception
                case EODatabaseOperation.AdaptorInsertOperator :
                    interpretedException = interpretInsertFailureFromException(theException);
                    break;

                // case EODatabaseOperation.EOAdaptorUpdateOperator : interpretedException = interpretUpdateFailureFromException(theException); break;

                // case  EODatabaseOperation.EOAdaptorDeleteOperator : interpretedException = interpretDeleteFailureFromException(theException); break;

                // case  EODatabaseOperation.EOAdaptorStoredProcedureOperator : interpretedException = interpretStoredProcedureFailureFromException(theException); break;
                default:
                    // Later, once the other cases are implemented we can do this:
                    //                default :
                    //                    interpretedException = new RuntimeException("Unknown Adaptor Operator (" + failedOperator +")");
            }
        }

        if (interpretedException instanceof EOFValidationException)
        {
            ((EOFValidationException)interpretedException).setOriginalException(theException);
        }

        return interpretedException;
    }



    /**
     * Interprets the passed exception as an optimistic locking failure (concurrent update error) and returns an exception with a localized error message and a customized userInfo dictionary.  Note that the operation which caused the exception could have been an update or a delete.  The same message is returned in either case.  If you need to customize the message, look at <code>NSExceptionAdditions.failedAdaptorOperation(exception)</code> to determine if it is an EOAdaptorUpdateOperator or an EOAdaptorDeleteOperator.
     *
     * @param theException the exception as raised from saveChanges()
     * @return an exception with a localized error message and a customized userInfo dictionary
     */
    public EOFValidationException interpretOptimisticLockingFailure(EOGeneralAdaptorException theException)
    {
        /** require
        [valid_param] theException != null;
        [exception_is_optimistic_locking_failure] NSExceptionAdditions.isOptimisticLockingFailure(theException); **/

        EOEntity entity = NSExceptionAdditions.entitySaveFailedOn(theException, this);
        EOEnterpriseObject object = NSExceptionAdditions.objectSaveFailedOn(theException);
        return new EOFValidationException(object, entity.name(), null, EOFValidation.OptimisticLockingFailure, null);
    }



    /**
     * Interprets the passed exception as an optimistic locking failure (concurrent update error) and returns an exception with a localized error message and a customized userInfo dictionary.  Note that the operation which caused the exception could have been an update or a delete.  The same message is returned in either case.  If you need to customize the message, look at <code>NSExceptionAdditions.failedAdaptorOperation(exception)</code> to determine if it is an EOAdaptorUpdateOperator or an EOAdaptorDeleteOperator.
     *
     * @param theException the exception as raised from saveChanges()
     * @return an exception with a localized error message and a customized userInfo dictionary
     */
    public EOFValidationException interpretIntegrityConstraintViolation(EOGeneralAdaptorException theException)
    {
        /** require
        [valid_param] theException != null;
        [exception_is_optimistic_locking_failure] NSExceptionAdditions.isIntegrityConstraintViolation(theException); **/

        EOEntity entity = NSExceptionAdditions.entitySaveFailedOn(theException, this);
        EOEnterpriseObject object = NSExceptionAdditions.isAdaptorOperationFailureException(theException) ?
                                        NSExceptionAdditions.objectSaveFailedOn(theException) : null;
        String violatedIntegrityConstraintName = NSExceptionAdditions.violatedIntegrityConstraintName(theException);
        return new EOFValidationException(object, entity.name(), violatedIntegrityConstraintName, EOFValidation.IntegrityConstraintViolation, NSExceptionAdditions.violatedIntegrityConstraintName(theException));
    }



    /**
    * Interprets the passed exception as an insertion failure and returns an exception with a localized error message and a customized userInfo dictionary.  If specific handling is needed for specific failures, this is the place to return a customized exception.  Inserts may fail due to a duplicated a unique column, violation of a nullity or other check constraint, etc.
     *
     * @param theException the exception as raised from saveChanges()
     * @return an exception with a localized error message and a customized userInfo dictionary
     */
    public EOFValidationException interpretInsertFailureFromException(EOGeneralAdaptorException theException)
    {
        /** require
        [valid_param] theException != null;
        [exception_is_optimistic_locking_failure] NSExceptionAdditions.isAdaptorOperationFailureException(theException); **/

        // TODO Add special handling of database exception message to detect constraint violation and and create a customized message, or pass a custom key to EOFValidationException's constructor
        EOEntity entity = NSExceptionAdditions.entitySaveFailedOn(theException, this);
        EOEnterpriseObject object = NSExceptionAdditions.objectSaveFailedOn(theException);
        return new EOFValidationException(object, entity.name(), null, EOFValidation.FailedToInsert, null);
    }



    /**
     * <p>Throws an exception if a specific, dangerous situation is present. The problematic
     * situation occurs when ObjectStore changes for registered objects are processed while the
     * method <code>processRecentChanges()</code> is running. If this happens, the editing context state
     * for inserted, deleted, and updated objects is lost.  This has unpredictable results including
     * attempting to save as updates those records being deleted which then fails validateForSave.</p>
     *
     * <p>The request to process ObjectStore changes can come as a result of faults firing during
     * processRecentChanged() (see stack trace below), and triggering a refetch of data as the
     * snapshot is older than what the editing context permits and the fetched row being different
     * that what was in the ObjectStore.  To get around this bug pre-fire any faults which may get
     * fired in this manner.  For example, using net.global_village.eofextensions.DeletePrefetcher.</p>
     *
     * <p>This situation is detected by checking if the instance variable <code>_processingChanges</code>
     * of <code>com.webobjects.eocontrol.EOEditingContext</code> is set and if the changes in the
     * notification affect the objects registered in this editing context.</p>
     * <pre>
     * Stack Trace from 5.1.3
     * at net.global_village.eofvalidation.EOEditingContext._processObjectStoreChanges(EOEditingContext.java:221)
     * at java.lang.reflect.Method.invoke(Native Method)
     * at com.webobjects.foundation.NSSelector.invoke(NSSelector.java:296)
     * at com.webobjects.foundation.NSSelector._safeInvokeSelector(NSSelector.java:59)
     * at com.webobjects.eocontrol.EOEditingContext._sendOrEnqueueNotification(EOEditingContext.java:2904)
     * at com.webobjects.eocontrol.EOEditingContext._objectsChangedInStore(EOEditingContext.java:2196)
     * at java.lang.reflect.Method.invoke(Native Method)
     * at com.webobjects.foundation.NSSelector._safeInvokeMethod(NSSelector.java:71)
     * at com.webobjects.foundation.NSNotificationCenter$_Entry.invokeMethod(NSNotificationCenter.java:580)
     * at com.webobjects.foundation.NSNotificationCenter.postNotification(NSNotificationCenter.java:522)
     * at com.webobjects.foundation.NSNotificationCenter.postNotification(NSNotificationCenter.java:554)
     * at com.webobjects.eocontrol.EOObjectStoreCoordinator._objectsChangedInSubStore(EOObjectStoreCoordinator.java:642)
     * at java.lang.reflect.Method.invoke(Native Method)
     * at com.webobjects.foundation.NSSelector._safeInvokeMethod(NSSelector.java:71)
     * at com.webobjects.foundation.NSNotificationCenter$_Entry.invokeMethod(NSNotificationCenter.java:580)
     * at com.webobjects.foundation.NSNotificationCenter.postNotification(NSNotificationCenter.java:522)
     * at com.webobjects.foundation.NSNotificationCenter.postNotification(NSNotificationCenter.java:554)
     * at com.webobjects.eoaccess.EODatabaseChannel.cancelFetch(EODatabaseChannel.java:577)
     * at com.webobjects.eoaccess.EODatabaseContext._objectsWithFetchSpecificationEditingContext(EODatabaseContext.java:3037)
     * at com.webobjects.eoaccess.EODatabaseContext.objectsWithFetchSpecification(EODatabaseContext.java:3148)
     * at com.webobjects.eoaccess.EODatabaseContext._followToManyRelationshipWithFetchSpecification(EODatabaseContext.java:2501)
     * at com.webobjects.eoaccess.EODatabaseContext._followFetchSpecification(EODatabaseContext.java:2643)
     * at com.webobjects.eoaccess.EODatabaseContext._batchFetchRelationshipForSourceObjectsEditingContext(EODatabaseContext.java:3758)
     * at com.webobjects.eoaccess.EODatabaseContext.objectsForSourceGlobalID(EODatabaseContext.java:4042)
     * at com.webobjects.eocontrol.EOObjectStoreCoordinator.objectsForSourceGlobalID(EOObjectStoreCoordinator.java:588)
     * at com.webobjects.eocontrol.EOEditingContext.objectsForSourceGlobalID(EOEditingContext.java:2428)
     * at com.webobjects.eoaccess.EODatabaseContext._fireArrayFault(EODatabaseContext.java:4286)
     * at com.webobjects.eoaccess.EOAccessArrayFaultHandler.completeInitializationOfObject(EOAccessArrayFaultHandler.java:60)
     * at com.webobjects.eocontrol._EOCheapCopyMutableArray.willRead(_EOCheapCopyMutableArray.java:37)
     * at com.webobjects.eocontrol._EOCheapCopyMutableArray.count(_EOCheapCopyMutableArray.java:87)
     * at com.webobjects.foundation.NSMutableArray.removeObject(NSMutableArray.java:493)
     * at net.global_village.virtualtables._VirtualRow.removeFromFields(_VirtualRow.java:106)
     * at java.lang.reflect.Method.invoke(Native Method)
     * at com.webobjects.foundation.NSSelector._safeInvokeMethod(NSSelector.java:71)
     * at com.webobjects.eocontrol.EOCustomObject.removeObjectFromPropertyWithKey(EOCustomObject.java:687)
     * at com.webobjects.eocontrol.EOClassDescription.propagateDeleteForObject(EOClassDescription.java:419)
     * at com.webobjects.eocontrol.EOCustomObject.propagateDeleteWithEditingContext(EOCustomObject.java:435)
     * at com.webobjects.eocontrol.EOClassDescription.propagateDeleteForObject(EOClassDescription.java:464)
     * at com.webobjects.eocontrol.EOCustomObject.propagateDeleteWithEditingContext(EOCustomObject.java:435)
     * at com.webobjects.eocontrol.EOEditingContext.propagateDeletesUsingTable(EOEditingContext.java:1331)
     * at com.webobjects.eocontrol.EOEditingContext._processDeletedObjects(EOEditingContext.java:1296)
     * at com.webobjects.eocontrol.EOEditingContext._processRecentChanges(EOEditingContext.java:917)
     * at com.webobjects.eocontrol.EOEditingContext.processRecentChanges(EOEditingContext.java:1089)
     * </pre>
     *
     * @exception RuntimeException if the instance variable <code>_processingChanges</code> of <code>com.webobjects.eocontrol.EOEditingContext</code> is set
     */
    public void _processObjectStoreChanges(NSDictionary changes)
    {
        try
        {
            // If private_processingChanges then a notification was received after calling super's constructor and
            // before our constructor set this.  There are no registered objects yet, so we can ignore this
            if (private_processingChanges != null && private_processingChanges.getBoolean(this))
            {
                NSDictionary changedObjects = _objectBasedChangeInfoForGIDInfo(changes);
                if (changedObjects.count() > 0)
                {
                    // Reset this flag so this exception does not get raised again and again until
                    // this editing context is garbage collected.
                    private_processingChanges.setBoolean(this, false);
                    throw new RuntimeException("While processingRecentChanges(), objects refetched " +
                        "and _processObjectStoreChanges called for: " + changedObjects);
                }
            }
        }
        catch (IllegalAccessException access)
        {
            throw new ExceptionConverter(access);
        }

        super._processObjectStoreChanges(changes);
    }




    /**
     * Sets instance variables to access private fields of of
     * <code>com.webobjects.eocontrol.EOEditingContext</code>.  This flag is set while the method processRecentChanges() is running.  We need access to this to detect a bug in EOF in WO 5.1.
     */
    protected void initializePrivateFieldAccess()
    {
        try
        {
            private_processingChanges = com.webobjects.eocontrol.EOEditingContext.class.getDeclaredField("_processingChanges");
            private_processingChanges.setAccessible(true);
        }
        catch (NoSuchFieldException noField)
        {
            throw new ExceptionConverter(noField);
        }
        catch (SecurityException security)
        {
            throw new ExceptionConverter(security);
        }

        /** ensure [_processingChanges_field_set] private_processingChanges != null;
                   [_processingChanges_field_accessible] private_processingChanges.isAccessible();
         **/
    }



}
