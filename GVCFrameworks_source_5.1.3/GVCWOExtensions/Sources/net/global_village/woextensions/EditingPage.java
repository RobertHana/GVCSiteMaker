package net.global_village.woextensions;

import org.apache.log4j.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import er.ajax.*;
import er.extensions.appserver.ajax.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;


/**
 * EditingPage is intended to be used as the super class of top level pages on which may contain editable data.  It adds:
 * <ul>
 * <li>a page / component specific editing context</li>
 * <li>methods for recording and managing validation failures</li>
 * <li>a page specific editing context</li>
 * <li>concepts of previous and next pages</li>
 * <li>handling of optimistic locking failures</li>
 * </ul>
 *
 * <h3>Handling Optimistic Locking Failures</h3>
 * <p>
 * Often you can just use this class as-is to handle these.  All you need to do is to check for a failure and show an
 * error message:
 * <pre>
 * <webobject name="HasOptimisticLockingFailure">
 *     <div class="errorMessage"><webobject name="OptimisticLockingFailure"/></div>
 * </webobject name="HasOptimisticLockingFailure">
 * ---
 * HasOptimisticLockingFailure: WOConditional {
 *     condition = hasOptimisticLockingFailures;
 * }
 *
 * OptimisticLockingFailure: WOString {
 *     value = optimisticLockingFailure;
 * }
 * </pre>
 * The two most common methods to override are <code>shouldHandleObjectChangedInStore(EOEnterpriseObject)</code>
 * and <code>optimisticLockingFailureMessage(EOEnterpriseObject)</code>.
 * </p
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 24$
 */
public class EditingPage extends net.global_village.woextensions.WOComponent
{

    /** Key into validationFailures() for failures resulting from saveChanges() and not specific to any one control. */
    public static final String saveChangesExceptionsKey = "saveChangesExceptions";

    /** Key into validationFailures() for the object causing failures in saveChanges(). */
    public static final String saveChangesExceptionObjectKey = "saveChangesExceptionObject";

    /** Key into validationFailures() for failures resulting from optimistic locking exception. */
    public static final String optimisticLockingExceptionKey = "optimisticLockingException";

    /** Key into validationFailures() for the object causing failures resulting from optimistic locking exception. */
    public static final String optimisticLockingExceptionObjectKey = "optimisticLockingExceptionObject";

    /** Holds validations failure messages by key. */
    private NSMutableDictionary validationFailures;

    /** True if clearing validation failures should be delayed until the next request to this page. */
    private boolean delayClearingValidationFailures;

    /** Holds lazy initialized instance of EOEditingContext to use for Enterprise Objects on this page.  */
    private EOEditingContext editingContext;

    /** Holds lazy initialized instance of EOEditingContext to use for Enterprise Objects on this page.  */
    private NSMutableSet changedGlobalIDs = new NSMutableSet();

    /** Holds page that created this page, if known.  Set in constructor from <code>context().page()</code>. */
    private com.webobjects.appserver.WOComponent previousPage;

    /** Used to prevent bogus reports of optimistic locking exceptions.  */
    private String owningSessionID;

    private final static Logger logger = LoggerFactory.makeLogger();


    /**
     * Constructor. Sets previousPage from <code>context().page()</code>. Registers this page for the
     * EOObjectStore.ObjectsChangedInStoreNotification.
     *
     * @param aContext
     *            WOContext this page is being created in
     */
    public EditingPage(WOContext aContext)
    {
        super(aContext);
        /** require [valid_context] aContext != null;  **/

        previousPage = context().page();
        owningSessionID = session().sessionID();

        /* Register for EOObjectStore.ObjectsChangedInStoreNotification and filter the notifications on editingContext().rootObjectStore().
           so that we can detect optimistic locking conflicts within the same EOF stack
        */
        NSSelector objectsChangedInStore = new NSSelector("objectsChangedInStore", new Class[] {NSNotification.class});
        NSNotificationCenter.defaultCenter().addObserver(this, objectsChangedInStore, EOObjectStore.ObjectsChangedInStoreNotification, editingContext().rootObjectStore());
        if (logger.isTraceEnabled()) logger.trace("Created " + name() + " <" + hashCode() + ">");
    }



    /**
     * Calls <code>session().newEditingContext()</code> if no editing context has been set and
     * sets its delegate to editingContextDelegate(). This method can be overridden to create /
     * return a different editing context if desired.  The editing context is also set to
     * not stop validating after the first error (setStopsValidationAfterFirstError(false)).
     *
     * @return the EOEditingContext to use in this page / component
     */
    public EOEditingContext editingContext()
    {
        if (editingContext == null)
        {
            editingContext = ((net.global_village.woextensions.WOSession)session()).newEditingContext();
            editingContext.setDelegate(editingContextDelegate());
            editingContext.setStopsValidationAfterFirstError(false);
        }
        return editingContext;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns this page as the EOEditingContext.Delegate to use for editingContext(). The delegate is used to handle
     * optimistic locking processing. This method can be overridden to return a different delegate if desired.
     *
     * @return the EOEditingContext.Delegate to use in this page / component
     */
    protected Object editingContextDelegate()
    {
        return this;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Records a specific error message (intended for validation failures) against a key for later retrieval with validationFailureForKey.
     *
     * @param error the localized error message or other object to record
     * @param key the key to record error under
     */
    public void recordValidationFailureForKey(Object error, String key)
    {
        /** require [valid_error] error != null;  [valid_key] key != null;  **/
        validationFailures().setObjectForKey(error, key);
        /** ensure [was_recorded] hasValidationFailureForKey(key);
                   [error_set] validationFailureForKey(key).equals(error);   **/
    }



    /**
     * Removes a specific error message by key
     *
     * @param key the key to remove error message for
     */
    public void removeValidationFailureForKey(String key)
    {
        /** require [valid_key] key != null;  **/
        validationFailures().removeObjectForKey(key);
        /** ensure [was_removed] ! hasValidationFailureForKey(key);   **/
    }



    /**
     * Overridden to catch validation errors thrown during the takeValues phase and record
     * them under the currentComponentIdentifier().
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        /** require [valid_context] context() != null;
                    [valid_elementID] context().elementID() != null;  **/

        recordValidationFailedWithException(t, value, keyPath);

        /** ensure [was_recorded] hasValidationFailureForKey(currentComponentIdentifier());  **/
    }



    /**
     * Records validation errors thrown during the takeValues phase under the currentComponentIdentifier().  This is intended
     * to be called from @link{validationFailedWithException(Throwable, Object, String)}.\
     *
     * @see EditingPage#validationFailedWithException(Throwable, Object, String)
     */
    public void recordValidationFailedWithException(Throwable t, Object value, String keyPath)
    {
        /** require [valid_context] context() != null;
                    [valid_elementID] context().elementID() != null;  **/
        if (t instanceof EOFValidationException)
        {
            recordValidationFailureForKey(((EOFValidationException)t).getLocalizedMessage(), currentComponentIdentifier());
        }
        else
        {
            recordValidationFailureForKey(t.getMessage(), currentComponentIdentifier());
        }

        /** ensure [was_recorded] hasValidationFailureForKey(currentComponentIdentifier());  **/
    }



    /**
     * Returns the identifier associated with the current WOComponent.  This will be the unqiueIdentifier() for
     * components that extend EditingPageComponent and context().elementID() for those that don't.
     *
     * @return identifier associated with the current WOComponent
     */
    public String currentComponentIdentifier()
    {
        // If a WOComponent is used inside of a EditingPageComponent, we need to go up the hierarchy
        // to locate the EditingPageComponent that validation failed in.
        com.webobjects.appserver.WOComponent currentComponent = context().component();
        while (currentComponent != null && ! (currentComponent instanceof EditingPageComponent))
        {
            currentComponent = currentComponent.parent();
        }

        if (currentComponent != null)
        {
            return ((EditingPageComponent)currentComponent).uniqueIdentifier();
        }

        return context().elementID();
        /** ensure [identifier_returned] Result != null;  **/
    }



    /**
     * @param key the key lookup the validation failure for
     * @return true if recordValidationFailureForKey has been called for key
     */
    public boolean hasValidationFailureForKey(String key)
    {
        /** require [valid_key] key != null;  **/

        return (validationFailures() != null) && (validationFailures().objectForKey(key) != null);
    }



    /**
     * @return true if any validation failures have been recorded
     */
    public boolean hasValidationFailures()
    {
        return (validationFailures != null) && (validationFailures().count() > 0);
    }



    /**
     * @param key the key lookup the validation failure for
     * @return the String message previously passed to recordValidationFailureForKey for key
     */
    public String validationFailureMessageForKey(String key)
    {
        /** require [has_error] hasValidationFailureForKey(key);
                    [is_string] validationFailureForKey(key) instanceof String;  **/
        return (String) validationFailures().objectForKey(key);
        /** ensure [error_returned] Result != null;  **/
    }



    /**
     * @param key the key lookup the validation failure for
     * @return the object previously passed to recordValidationFailureForKey for key
     */
    public Object validationFailureForKey(String key)
    {
        /** require [has_error_for_key] hasValidationFailureForKey(key);  **/
        return validationFailures().objectForKey(key);
        /** ensure [error_returned] Result != null;  **/
    }



    /**
     * @return true if there were validation failures recorded during saveChanges()
     */
    public boolean hadFailuresDuringSaveChanges()
    {
        return (validationFailures() != null) && (validationFailures().objectForKey(saveChangesExceptionsKey) != null);
    }



    /**
     * @return validation failures recorded during saveChanges()
     */
    public NSArray saveChangesFailures()
    {
        /** require [has_error] hadFailuresDuringSaveChanges();  **/
        return (NSArray) validationFailures().objectForKey(saveChangesExceptionsKey);
        /** ensure [error_returned] Result != null;  **/
    }



    /**
     * @return the dictionary used internally to hold registered validation errors
     */
    public NSMutableDictionary validationFailures()
    {
        if (validationFailures == null) {
            validationFailures = new NSMutableDictionary();
        }

        return validationFailures;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Return the message to display to the user when an optimistic locking exception is thrown for <code>eo</code>.
     *
     * @param eo the EOEnterpriseObject that caused the exception
     * @return the message to display to the user when an optimistic locking exception is thrown
     */
    public String optimisticLockingFailureMessage(EOEnterpriseObject eo)
    {
        return "Someone else has updated this information. Please verify the information and save again.";
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * The implementation here returns <code>hasValidationFailureForKey(optimisticLockingExceptionKey)</code>.
     * This can be re-implemented if needed.
     *
     * @return <code>true</code> if an optimistic locking conflict was detected within this EOF stack
     */
    public boolean hasOptimisticLockingFailures()
    {
        return (validationFailures() != null) && (validationFailures().objectForKey(optimisticLockingExceptionKey) != null);
    }



    /**
     * @return optimistic locking failure message
     */
    public String optimisticLockingFailure()
    {
        /** require [had_error] hasValidationFailureForKey(optimisticLockingExceptionKey);  **/
        return (String) validationFailures().objectForKey(optimisticLockingExceptionKey);
        /** ensure [error_returned] Result != null;  **/
    }



    /**
     * This method is intended as a bridge for older code and pages that only want to show a list
     * of validation messages at the top.  It groups the relevant validation errors from takeValues
     * and from saveChanges() failures into one list.  They keys from validationFailures() are discarded.
     *
     * @return unordered NSArray of validation error messages
     */
    public NSArray validationFailuresAsList()
    {
        if (hasOptimisticLockingFailures())
        {
            return new NSArray(optimisticLockingFailure());
        }

        if (hadFailuresDuringSaveChanges())
        {
            return saveChangesFailures();
        }

        return validationFailures().allValues();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Removes all accumulated validation failures, unless clearing them has been delayed to the next
     * RR loop.
     *
     * @see #delayClearingValidationFailures()
     */
    public void clearValidationFailures(String source)
    {
        if (logger.isDebugEnabled() && delayClearingValidationFailures()) logger.debug("Delay skipping clearValidationFailures from " + source);
        if ( ! delayClearingValidationFailures())
        {
            if (logger.isDebugEnabled()) logger.debug("Clearing ValidationFailures from " + source);
            if (logger.isDebugEnabled() && hasValidationFailures()) logger.debug(validationFailures());

            validationFailures = null;
        }
        /** ensure [no_failures] delayClearingValidationFailures() || ! hasValidationFailures();   **/
    }



    /**
     * Returns <code>true</code> if clearing validation failures should be delayed until the next request to this page.
     * This is useful when the current request is a component action request and the next request an Ajax request that will
     * retrieve these failure message for display (e.g. in an AjaxModalDialog).
     *
     * @return <code>true</code> if clearing validation failures should be delayed until the next request to this page
     */
    public boolean delayClearingValidationFailures()
    {
        return  delayClearingValidationFailures;
    }



    /**
     * Sets if clearing validation failures should be delayed until the next request to this page.
     * @see #delayClearingValidationFailures()
     *
     * @param shouldDelay <code>true</code> if clearing validation failures should be delayed until the next request to this page
     */
    public void setDelayClearingValidationFailures(boolean shouldDelay)
    {
        if (logger.isDebugEnabled() && shouldDelay) logger.debug("Choosing to delay");
        delayClearingValidationFailures = shouldDelay;
    }



    /**
     * Overridden to clear validation failures after they have been returned to the user
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);

        // Ajax requests that are not updating an AjaxUpdateContainer or the AjaxModalDialog should NOT clear these messages
        // as a later Ajax request will need them to update the UI.  This will still be a problem if two different update
        // containers need to be updated (e.g. validation and save failures).
        if (AjaxUtils.isAjaxRequest(context.request()) &&
            ! ( AjaxUpdateContainer.hasUpdateContainerID(context.request()) ||
                context.request().stringFormValueForKey(ERXAjaxApplication.KEY_UPDATE_CONTAINER_ID) != null ||
                context.senderID().endsWith(AjaxModalDialog.Open_ElementID_Suffix) ))
        {
            setDelayClearingValidationFailures(true);
        }

        clearValidationFailures("appendToResponse");  // Does nothing if delayClearingValidationFailures()
        setDelayClearingValidationFailures(false);
    }



    /**
     * Overridden to clear validation failures after they have been returned to the user and validation failures lying around
     * after an Ajax submit for a partial page update.
     */
    public void takeValuesFromRequest(WORequest request, WOContext context)
    {
        if ( ! ERXAjaxApplication.isAjaxUpdate(request))
        {
            // HACK!
            // The problem is that Ajax submits at the top of the form can leave messages in validationFailures() from
            // inputs further down that don't have a value yet.  We want to discard those before taking values.  But we want
            // to retain any optimistic locking failures.
            if (hasOptimisticLockingFailures())
            {
                EOEnterpriseObject eo = (EOEnterpriseObject)validationFailures().objectForKey(optimisticLockingExceptionObjectKey);
                clearValidationFailures("takeValuesFromRequest (hasOptimisticLockingFailures)");
                if (shouldHandleObjectChangedInStore(eo))
                {
                    handleObjectChangedInStore(eo);
                }
            }
            // Always clear if this is a component or direct action request
            else if (! AjaxUtils.isAjaxRequest(context.request()))
            {
                clearValidationFailures("takeValuesFromRequest");
            }
            // Clear before processing an Ajax form submission
            else if (AjaxSubmitButton.isAjaxSubmit(request))
            {
                clearValidationFailures("takeValuesFromRequest (isAjaxSubmit)");
            }
        }

        super.takeValuesFromRequest(request, context);
    }



    /**
     * Return the page to "go back" to, either the real previous page or a new instance of this page
     * if the previous page is not known. This is intended for use when processing a cancel operation.
     *
     * @return the page to "go back" to,
     */
    public com.webobjects.appserver.WOComponent previousPage()
    {
        if (previousPage == null)
        {
            previousPage = pageWithName(context().page().name());
        }

        previousPage._awakeInContext(context());
        return previousPage;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Return the page to show after processing is successfully completed on this page. The default is
     * a new instance of this page. This is intended for use when processing a successful save operation.
     * In most cases it will be necessary to override this to initialize the returned page.
     *
     * @return the page to show after processing is successfully completed on this page
     */
    public com.webobjects.appserver.WOComponent nextPage()
    {
        return pageWithName(context().page().name());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Generic action method to cancel (ec.revert()) any outstanding changes and display the previous page.
     *
     * @return previousPage()
     */
    public com.webobjects.appserver.WOComponent cancelAndGoBack()
    {
        editingContext().revert();
        clearValidationFailures("cancelAndGoBack");
        return previousPage();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Generic action method to save any outstanding changes and display the next page.
     *
     * @return current page or nextPage()
     */
    public com.webobjects.appserver.WOComponent saveAndGoForward()
    {
        logger.debug("in saveAndGoForward");
        // Take no action if there is a lock conflict or there are validation messages that need to be shown to the user
        if (hasOptimisticLockingFailures() || hasValidationFailures())
        {
            logger.debug("won't save, has validationFailures() " + validationFailures());
            return context().page();
        }

        try
        {
            saveChanges();
            logger.debug("saveChanges() succeeded");

            return nextPage();
        }
        // The EOFValidationException catch will be used when an editing context from GVCEOFValidation is used
        // The other catch clauses are to handle other EOEditingContext classes
        catch (EOFValidationException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Save failed with EOFValidationException: ", e);
                logger.trace("    userInfo: " +  e.userInfo());
            }
            if (e.originalException() != null && e.originalException() instanceof EOGeneralAdaptorException)
            {
                // Handle EOFValidationExceptions raises as result of an EOGeneralAdaptorException
                // but keep the customized error message
                handleEOGeneralAdaptorException((EOGeneralAdaptorException)e.originalException(),
                                                e.getLocalizedMessageForUnaggregatedExceptions());
            }
            else
            {
                recordValidationFailureForKey(e.getLocalizedMessageForUnaggregatedExceptions(), saveChangesExceptionsKey);
            }
        }
        catch (NSValidation.ValidationException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Save failed with NSValidation.ValidationException: ", e);
                logger.trace("    userInfo: " +  e.userInfo());
            }
            recordValidationFailureForKey(ValidationExceptionAdditions.unaggregateExceptions(e).valueForKey("getLocalizedMessage"),
                                          saveChangesExceptionsKey);
        }
        catch (com.webobjects.eoaccess.EOGeneralAdaptorException adaptorException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Save failed with EOGeneralAdaptorException: ", adaptorException);
                logger.trace("    userInfo: " +  adaptorException.userInfo());
            }

            handleEOGeneralAdaptorException(adaptorException, new NSArray(adaptorException.getMessage()));
        }

        logger.debug("saveChanges() failed, generated validationFailures() " + validationFailures());
        return context().page();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Calls <code>saveChanges()</code> on <code>editingContext()</code>.
     * This can be overridden if child or peer editing contexts also need to be saved.
     */
    protected void saveChanges()
    {
        editingContext().saveChanges();
    }



    /**
     * Helper method for various catch clauses in saveChanges().
     *
     * @param adaptorException EOGeneralAdaptorException that caused the save to fail
     * @param exceptionMessages list of messages to be recorded under saveChangesExceptionsKey
     */
    protected void handleEOGeneralAdaptorException(EOGeneralAdaptorException adaptorException, NSArray exceptionMessages)
    {
        if (NSExceptionAdditions.isOptimisticLockingFailure(adaptorException))
        {
            EOEnterpriseObject eo = NSExceptionAdditions.objectSaveFailedOn(adaptorException);
            recordValidationFailureForKey(optimisticLockingFailureMessage(eo), optimisticLockingExceptionKey);
            if (eo != null)
            {
                recordValidationFailureForKey(eo, optimisticLockingExceptionObjectKey);
                recoverFromOptimisticLockingFailure(eo);
            }
        }
        else
        {
            // Add the object that caused this failure to the validation failures
            if (NSExceptionAdditions.isAdaptorOperationFailureException(adaptorException) &&
                NSExceptionAdditions.objectSaveFailedOn(adaptorException) != null)
            {
                recordValidationFailureForKey(NSExceptionAdditions.objectSaveFailedOn(adaptorException), saveChangesExceptionObjectKey);
            }

            recordValidationFailureForKey(exceptionMessages, saveChangesExceptionsKey);
        }
    }



    /**
     * Catch the ObjectsChangedInStoreNotification so that we can handle concurrent edits
     * within one EOF stack. This is not, strictly speaking, an optimistic locking failure
     * but as it has the same cause and same effect to the user we handle it the same.
     * <br/>
     * <b>If you override this method, you are responsible for locking any editing contexts that you touch!</b>
     *
     * @param notification NSNotification to check
     */
    public void objectsChangedInStore(NSNotification notification)
    {
        if (logger.isTraceEnabled()) logger.trace("In component " + name() + " <" + hashCode() + ">");
        NSArray updatedObjects = (NSArray)notification.userInfo().objectForKey(EOObjectStore.UpdatedKey);
        if (updatedObjects == null || updatedObjects.count() == 0)
        {
            logger.trace("No updates to process");
            return;
        }


        // If this comes from the same session just ignore this notification.  We don't need to report the user's changes back to them
        if (WOSession.session() != null && owningSessionID().equals(WOSession.session().sessionID()))
        {
            logger.trace("Ignoring changes from our own session");
            return;
        }



        // If the thread sending the notification can lock our editing context, the notification
        // can be processed immediately.  If a lock can't be obtained, the globalIDs are queued
        // for later processing
        if (editingContext().tryLock())
        {
            try
            {
                logger.trace("Obtained EC lock, processing updated objects");
                processUpdatedObjects(updatedObjects);
            }
            finally
            {
                editingContext().unlock();
            }
        }
        else
        {
            logger.trace("Failed to obtain EC lock, queuing updated objects");
            queuedChangedGlobalIDs().addObjectsFromArray(updatedObjects);
        }
    }



    /**
     * Processes anything in queuedChangedGlobalIDs.
     */
    public void awake()
    {
        super.awake();
        processQueuedChangedGlobalIDs();
    }



    /**
     * Calls <code>processUpdatedObjects()</code> with the contents of <code>queuedChangedGlobalIDs()</code> and clears
     * <code>queuedChangedGlobalIDs()</code>.  The can be called whenever you want to process the queued changes (if any).
     *
     */
    protected void processQueuedChangedGlobalIDs()
    {
        processUpdatedObjects(queuedChangedGlobalIDs().allObjects());
        queuedChangedGlobalIDs().removeAllObjects();
        /** ensure [all_ids_processed] queuedChangedGlobalIDs().count() == 0;  **/
    }



    /**
     * Turns the passed list of globalIDs into EOs checks to see which are registered in our <code>editingContext()</code>.
     * Registered, changed objects are passed to <code>shouldHandleObjectChangedInStore()</code> and if they should
     * be handled are then passed to <code>handleObjectChangedInStore() </code>.
     *
     * @param updatedObjects
     */
    protected void processUpdatedObjects(NSArray updatedObjects)
    {
        /** require [valid_list] updatedObjects != null;  **/
        for (int i = 0; i < updatedObjects.count(); i++)
        {
            EOGlobalID globalID = (EOGlobalID) updatedObjects.objectAtIndex(i);
            logger.trace("EOGlobalID " + globalID);
            // objectForGlobalID() returns null if object for globalID has not been faulted into this EC
            EOEnterpriseObject eo = editingContext().objectForGlobalID(globalID);
            if ((eo != null) && shouldHandleObjectChangedInStore(eo))
            {
                logger.trace("Calling handleObjectChangedInStore");
                handleObjectChangedInStore(eo);
            }
        }
    }



    /**
     * This method is only called if eo is already registered in this page's editing context.
     * Override this to return <code>true</code> if the page is affected by changes in the passed object
     * that has been saved from another editing context in this same application instance.  This
     * default implementation returns <code>true</code>.
     *
     * @param eo the changed object
     * @return <code>true</code> if the page is affected by changes to <code>eo</code>
     */
    protected boolean shouldHandleObjectChangedInStore(EOEnterpriseObject eo)
    {
        /** require [valid_object] eo != null;  **/
        return true;
    }



    /**
     * Override this method to change what happens when an object is updated in the Object Store (when
     * shouldHandleObjectChangedInStore(eo) returns true).  This implementation calls records the object
     * causing the failure and a message under the validation failure keys optimisticLockingExceptionKey
     * and optimisticLockingExceptionObjectKey; and calls <code>setOptimisticLockingConflictDetected(true)</code>
     * This should be fine for most cases.
     *
     * @param eo EOEnterpriseObject that was changed outside this page
     */
    protected void handleObjectChangedInStore(EOEnterpriseObject eo)
    {
        /** require [valid_object] eo != null;  **/
        if (logger.isTraceEnabled())
        {
            logger.trace("Recording optimistic locking error for " + eo);
        }
        recordValidationFailureForKey(optimisticLockingFailureMessage(eo), optimisticLockingExceptionKey);
        recordValidationFailureForKey(eo, optimisticLockingExceptionObjectKey);
        /** ensure [has_optimisiticLockingFailure] hasOptimisticLockingFailures();  **/
    }



    /**
     * <p>Override this method to take appropriate action to recover from an optimistic locking exception.
     * This implementation calls <code>EOObject.refreshObject(eo)</code> / <code>eo.refreshObject()</code> so that
     * fresh data is fetched from the external store and notifications about this new data broadcast.</p>
     *
     * <p>The delegate for editingContext() (usually this page) determines if the refreshed changes are merged
     * into existing changes in the EO or if they overwrite them.  It does this by implementing the
     * <code>EOEditingContext.Delegate</code> method <code>editingContextShouldMergeChangesForObject</code>.  See the documentation
     * for <code>EOEditingContext.Delegate</code> for information on performing a custom merge by also implementing
     * the <code>EOEditingContext.Delegate</code> method <code>editingContextDidMergeChanges</code>.
     * </p>
     *
     * @param eo the object that caused the optimistic locking failure exception
     */
    protected void recoverFromOptimisticLockingFailure(EOEnterpriseObject eo)
    {
        /** require [valid_eo] eo != null;  **/
        if (eo instanceof GenericRecord)
        {
            ((GenericRecord)eo).refreshObject();
        }
        else
        {
            EOObject.refreshObject(eo);
        }
    }



    /**
     * Return true if all of the uncommitted changes should be merged into the object after the update is applied. This will
     * preserve the uncommitted changes.  This is the the default behavior for EOEditingContext and the implementation of this
     * method.  This method can be implemented to return false, in which case no uncommitted changes are applied. That is,
     * the object is updated to reflect the values from the database exactly.
     *
     * @param ec the editing context containing eo
     * @param eo the updated object with changes to be merged
     * @return true if all uncommitted changes should be merged into object after the update
     */
    protected boolean editingContextShouldMergeChangesForObject(EOEditingContext ec, EOEnterpriseObject eo)
    {
        /** require [valid_ec] ec != null;
                    [same_ec] ec == editingContext();
                    [valid_eo] eo != null;  **/
        return true;
    }



    /**
     * @return set of globalIDs of objects that have been saved in other editing context's since the last time we awoke
     */
    protected NSMutableSet queuedChangedGlobalIDs()
    {
        return changedGlobalIDs;
        /** ensure [list_returned] Result != null;  **/
    }



    /**
     * Returns the session ID of the WOSession that created this page.
     *
     * @return the session ID of the WOSession that created this page
     */
    public String owningSessionID()
    {
        return owningSessionID;
    }

}
