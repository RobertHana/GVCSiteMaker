package net.global_village.woextensions;

import java.util.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * NOTE: THIS IS INCOMPLETE AND SOME OF IT WILL HAVE TO BE RE-WRITTEN!!!<br><br>  	
 * ValidatingPage should be used for any pages that allow the user to create or edit enterprise object.  It was written to control a single entity.  If the page is altering multiple entities, it will likely not be suitable.<br>
 * ValidatingPage uses the EOFValidation framework to produce localized exception messages for any exceptions raised, either at the UI (e.g. formatter failures) or EOF layer (e.g. EOModel constraint violations.<br><br><br>
 *
 * <b>How to Use ValidatingPage</b><br>
 * 1. Change the header file for the page to inherit from <code>ValidatingPage()</code> instead of <code>WOComponent</code><br>
 * 2. Override <code>pageToProceedToAfterSave()<code><br>
 * 3. Use <code>editingContext()</code> NOT <code>session().defaultEditingContext()</code><br>
 * 4. Add <code>privateCopyOfObject(theObject)</code> to all methods that take an Enterprise Object parameter and which may be called from outside this object.<br>
 * 5. Add <code>publicCopyOfObject(((WOSession) session()).defaultEditingContext(), theObject)</code> to all methods that pass an Enterprise Object to another page.<br>
 * 7. Call <code>attemptSave()</code> to save any changes and return the result as the next page to show.<br>
 * 8. Add components to your page to show the contents of errors if <code>hasErrors()</code> is <code>true</code>.<br>
 * 9. Optionally override <code>willSubclassHandleException()</code> and call <code>addError()<code> if you need special handling of certain exceptions.  This is usually not needed.<br>
 *
 *
 * <br><br>TO DO<br>
 * - handle multiple entities<br>
 * - handle formatter failures better (pass off to EOFValidation)<br>
 * - handle deletes<br>
 * - handle case where there is no takeValues... but an invokeAction... instead (status change from hyper link for example).<br>
 * - convert handling of optimistic locking  <br>
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 9$
 */
public class ValidatingPage extends ValidatingComponent
{
    private static final long serialVersionUID = -6157559122633076991L;

    protected EOEditingContext privateEditingContext;
    protected NSMutableArray _errors;


    /**
     * Designated constructor.
     */
    public ValidatingPage(WOContext aContext)
    {
        super(aContext);

        // Don't use the setters so that the invariant works...
        privateEditingContext = gvcSession().newEditingContext();
        privateEditingContext.setStopsValidationAfterFirstError(false);
        _errors = new NSMutableArray();

        /** ensure
        privateEditingContext != null;
        _errors != null; **/
    }



    /**
     * Sets the editing context to use for this page.  This should only be called in exceptional circumstances,
     * usually the one created by the page is sufficient.
     *
     * @param editingContext the editing context to be set
     */
    public synchronized void setEditingContext(EOEditingContext editingContext)
    {
        /** require [valid_param] editingContext != null; **/

        if (privateEditingContext != editingContext)
        {
            if (privateEditingContext != null)
            {
                gvcSession().unregisterEditingContext(privateEditingContext);
            }
            privateEditingContext = editingContext;
            privateEditingContext.setStopsValidationAfterFirstError(false); // Allow aggregating of exceptions
            gvcSession().registerEditingContext(privateEditingContext);
        }

        /** ensure
        [has_private_editing_context] privateEditingContext != null;
        [private_editing_context_continues_validation_after_first_error] privateEditingContext.stopsValidationAfterFirstError() == false; **/
    }



    /**
     * Returns the editing context to be used with objects on this page.  Do not use [[self session] defaultEditingContext with this page!
     *
     * @return the editing context to be used with objects in this page.
     */
    public EOEditingContext editingContext()
    {
        return privateEditingContext;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Subclasses should override this if they want some say in how an exception is handled.  The subclass  should return YES if it handled the exception.  If a subclass handles an exception, it is responsible for calling addError: if it wants a message displayed.  This allows the subclass to veto an exception, alter the exception reported, or simple take special actions for certain exceptions.
     */
    public boolean willSubclassHandleException(Throwable anException)
    {
        return false;
    } 



    /**
     * Adds the message to the list of errors.  Subclasses can call this if they need to add
     * a validation error message.
     *
     * @param anErrorMessage the String to be added to the list of errors
     */
    public void addError(String anErrorMessage)
    {
        /** require [valid_param] anErrorMessage != null; **/

        // Errors detected during <code>takeValuesFromRequest()</code> can duplicate errors from validateForSave.  The easiest way to deal with ths is to not add the message if it is already there.
        if ( ! (_errors.containsObject(anErrorMessage)))
        {
            _errors.addObject(anErrorMessage);
        }

        /** ensure [has_errors] hasErrors(); **/
    }



    /**
     * Adds the reason from the exception to the list of errors if willSubclassHandleException: returns false.  Also handles the exception being an NSValidation.ValidationException with additionalExceptions().
     *
     * @param anException the exception to be added to the list of errors
     */
    public void addError(Throwable anException)
    {
        /** require [valid_param] anException != null; **/

        if ( ! willSubclassHandleException(anException))
        {
            // Find the additional exceptions, if any
            NSArray additionalExceptions = null;
            if (anException instanceof com.webobjects.foundation.NSValidation.ValidationException)
            {
                additionalExceptions = ((com.webobjects.foundation.NSValidation.ValidationException)anException).additionalExceptions();
            }
            else if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("Non-validation exception: " + NSLog.throwableAsString(anException));
            }

            // Unwrap the exceptions and start again if there are additional exceptions.
            if ((additionalExceptions != null) && (additionalExceptions.count() > 0))
            {
                addError(additionalExceptions);
            }

            addError(((WOSession)session()).localizedMessage(anException));
        }

        /** ensure [has_errors] hasErrors(); **/
    }



    /**
     * Calls addErrorFromException: once for every element of the passed array.
     *
     * @param someExceptions the array of exceptions to be added to the list of errors
     */
    public void addError(NSArray someExceptions)
    {
        /** require [valid_param] someExceptions != null; **/

        Enumeration exceptionEnumerator = someExceptions.objectEnumerator();
        while (exceptionEnumerator.hasMoreElements())
        {
            Throwable thisException = (Throwable)exceptionEnumerator.nextElement();
            addError(thisException);
        }

        /** ensure [has_errors] hasErrors(); **/
    }



    /**
     * Returns true if errors have been detected.
     *
     * @return true if errors have been detected, false otherwise
     */
    public boolean hasErrors()
    {
        return _errors.count() > 0;
    }



    /**
     * Removes all errors from the list of errors.
     */
    public void clearErrors()
    {
        _errors.removeAllObjects();
        /** ensure [has_no_errors] ! hasErrors(); **/
    }



    /**
     * Returns any error messages occured during <code>takeValuesFromRequest()</code>, attemptSave: or from optimistic locking failures.
     *
     * @return an array of error messages
     */
    public NSArray errors()
    {
        return _errors;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This method actually does the save, but does not handle any exceptions that arise.  Sub-classes can override this if extra processing is needed, but they should not trap any exceptions.  Generally, sub-classes should be overriding attemptSave and not this method.
     */
    public void doSave()
    {
        if ( ! hasErrors())
        {
            if ( ! (EOEditingContext.class.isInstance(editingContext().parentObjectStore())))
            {
                WOSnapshotErrorFix bugFix = new WOSnapshotErrorFix(editingContext());
                editingContext().saveChanges();
                bugFix.postprocessOnSave();
            }
            else
            {
                editingContext().saveChanges();                
            }
        }
    }



    /**
     * Subclasses must call this method to save any changes they have made.
     * If there are no validation or optimistic locking problems, attempts to save the changes in the editing context by calling doSave.  If there were errors either before or during the save, returns nil to indicate the save failed (and so the page will be shown to the user again).  If the save succeeded, calls pageToProceedToAfterSave to determine which page to show next.
     *
     * @return if the save was successful, the page returned by <code>pageToProceedToAfterSave()</code>, this page otherwise
     */
    public com.webobjects.appserver.WOComponent attemptSave()
    {
        try
        {
            doSave();
        }
        catch (Throwable t) //for other exceptions
        {
            addError(t);
        }

        return hasErrors() ? context().page() : pageToProceedToAfterSave();
    }



    /**
     * Subclasses override this to return an already set up page to be displayed after a successful save.  Default implementation returns null, which causes a postcondition exception.
     *
     * @return the WOComponent to be displayed after a successful save
     */
    public com.webobjects.appserver.WOComponent pageToProceedToAfterSave()
    {
        return null;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Subclasses can call this to "reset" the page: the editing context is reverted and any errors are cleared.  No other actions are taken: guarded objects remain guarded etc.
     *
     * @return the WOComponent to be displayed after reset is called
     */
    public net.global_village.woextensions.WOComponent resetPage()
    {
        clearErrors();
        editingContext().revert();

        return (net.global_village.woextensions.WOComponent)context().page();

        /** ensure
        [has_no_errors] ! hasErrors();
        [editing_context_has_no_changes] ! editingContext().hasChanges(); **/
    }



    /**
     * Convenience method useful when working with pages which have their own editing context.  Given an object in an unknown editing context, this method ensures that it exists in this page's privateEditingContext.  This should be used in all set... methods and anywhere else that an object is taken from outside this page.
     *
     * @param someObject the object
     * @return the private copy of someObject
     */
    public EOEnterpriseObject privateCopyOfObject(EOEnterpriseObject someObject)
    {
        /** require [valid_param] someObject != null; **/
        return EOUtilities.localInstanceOfObject(editingContext(), (EOEnterpriseObject) someObject);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Convenience method useful when working with pages which have their own editing context.  Given an object in the page's editing context, returns the object in <code>anotherEditingContext</code>.  This should be used in all methods that set an object in another object (outside this page) e.g. when passing an object from this page to another page.<br>
     * Note: The equivalent method for this in Obj-C returns the object in the session's defaultEditingContext.  Since in this framework, there is no way to get the instance of the application's session, the editingContext will just have to be passed in order for it to work, eg.  publicCopyOfObject(theSession.defaultEditingContext, aUser);
     *
     * @param anotherEditingContext the editing context to put <code>someObject</code> into
     * @param someObject the object
     * @return the public copy of someObject
     */
    public EOEnterpriseObject publicCopyOfObject(EOEditingContext anotherEditingContext, EOEnterpriseObject someObject)
    {
        /** require
        [valid_anotherEditingContext_param] anotherEditingContext != null;
        [valid_someObject_param] someObject != null; **/
        return EOUtilities.localInstanceOfObject(anotherEditingContext, someObject);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This is the start of the takeValuesFromRequest:inContext: phase.  It clears any errors
     * accumulated from the last request-response cycle.  If there are any validation errors,
     * the next method that will get called is validationFailedWithException().<br>
     *
     * To handle this will also need to over ride invoke action and add a flag to clearErrors
     * there if not already done.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        clearErrors();
        super.takeValuesFromRequest(aRequest, aContext);
    }



    /**
     * Work-around for intermittent crashing problem.  Somehow, the editingContext() of some pages inheriting from ValidatingPage is not being freed even if the page is already freed. When a notification is sent to this editingContext(), it still has a reference to its delegate which is the freed page, thus, delegate should be set to null when finalize() is called to avoid this problem.
     */
    protected void finalize() throws Throwable
    {
        editingContext().setDelegate(null);
        super.finalize();
    }



    /** invariant
    [has_editing_context] editingContext() != null;
    [has_errors_array] errors() != null; **/



}
