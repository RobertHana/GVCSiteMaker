package net.global_village.eofvalidation;

import com.webobjects.foundation.*;


/**
 * The main controlling class for EOFValidation.
 * <p>
 * <b>Introduction</b><br>
 * The purpose of the EOFValidation framework is to enhance standard EOF validation to provide:
 * <ul>
 * <li>more extensive validations
 * <li>code-free, customizable, localizable validation messages
 * <li>client code access to customizable, localizable validation messages
 * <li>more accessible information about failures
 * <li>better handling for adaptor level errors (exceptions that the db engine raises).
 * <li>better support for validation in WebObjects (support for GVCWOExtensions framework)
 * </ul>
 * <p>
 * <b>Using the EOFValidation Framework</b><br>
 * The validation framework must be explicitly activated by the client application.  This must be done before the first call to any of the EOF classes.  This is best done in the WOApplication constructor.
 * <p> <pre>
 *     import net.global_village.eofvalidation.*;
 *
 *     public Application()
 *     {
 *         super();
 *         EOFValidation.installValidation();
 *         ...
 * </pre> <p>
 * Once this is done, there is no more to do, unless there is a need to trap specific exceptions and handle them.  For most cases, simply showing the exception reason should be sufficient.
 * <p><pre>
 * try {
 *     ec.saveChanges();
 * }
 * catch (EOFValidationException e) {
 *     addMessages(e.getLocalizedMessageForUnaggregatedExceptions());
 * }
 * </pre><p>
 * <p>
 * <p>
 * <b>Location of Validation Error Messages</b><br>
 * The validation error messages are taken from localized strings files which can be located in several places.  The choice of location depends on the organizational goals of the project, there are no functional differences.  See <a href="net.global_village.eofvalidation#LocalizationEngine#localizedStringFromBundle">LocalizationEngine.localizedStringFromBundle</a> for details.<br>
 * <p>
 * <p>
 * <p>
 * <b>Customzing Message Content</b><br>
 * All of the messages can be customized by adding special substitution tags.  The substitution tags available are documented in <a href="net.global_village.eofvalidation.EOFValidationException#validationFailureDictionary">net.global_village.eofvalidation.EOFValidationException.validationFailureDictionary</a>.
 * <p>Futhermore, the names displayed for entities, attributes and relationships can be customized and localized by defining an entity display name as:
 * <p>&lt;entityName&gt;.&lt;EOFValidation.DisplayName&gt;		                  e.g. WorkOrder.displayName="Work Requisition";
 * <p>&lt;entityName&gt;.&lt;propertyName&gt;.&lt;EOFValidation.DisplayName&gt;     e.g. WorkOrder.orderDate.displayName="order submission date";
 * <p>
 * <p>A more detailed descriptioin can also be added for entities, attributes and relationships can be customized and localized by defining a description as:
 * <p>&lt;entityName&gt;.&lt;EOFValidation.Description&gt;                        e.g. WorkOrder.description="The work requisition issued to perform this task.";
 * <p>&lt;entityName&gt;.&lt;propertyName&gt;.&lt;EOFValidation.Description&gt;     e.g. WorkOrder.orderDate.description="The date that the order requistion was filled out.";
 * <p>
 * <p>
 * <b>Default Validation Messages</b><br>
 * These defaults are supplied in the EOFValidation Framework.  They will be used if you do not provide a corresponding customized message.
 * <ul>
 * <li>Entity.nullNotAllowed = "You must enter a &lt;&lt;propertyName&gt;&gt; for this &lt;&lt;entityName&gt;&gt;";
 * <li>Entity.tooLong = "The value you entered, &lt;&lt;failedValue&gt;&gt;, is longer than the &lt;&lt;attribute.width&gt;&gt; characters allowed for &lt;&lt;propertyName&gt;&gt; on &lt;&lt;entityName&gt;&gt;.";
 * <li>Entity.invalidValue = "&lt;&lt;failedValue&gt;&gt; is not an acceptable &lt;&lt;propertyName&gt;&gt; for &lt;&lt;entityName&gt;&gt;.";
 * <li>Entity.belowMinimum = "&lt;&lt;failedValue&gt;&gt; is below the acceptable limit for &lt;&lt;propertyName&gt;&gt; for &lt;&lt;entityName&gt;&gt;.";
 * <li>Entity.aboveMaximum = "&lt;&lt;failedValue&gt;&gt; is above the acceptable limit for &lt;&lt;propertyName&gt;&gt; for &lt;&lt;entityName&gt;&gt;.";
 * <li>Entity.notUnique = "&lt;&lt;failedValue&gt;&gt; has already been used as a &lt;&lt;propertyName&gt;&gt; for &lt;&lt;entityName&gt;&gt;.";
 * </ul>
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * version $Revision: 10$
 */
public class EOFValidation extends Object
{
 //TODO: Add EOEditingContext support for saveChanges failures
 //TODO: Add session based localization engine for WebObjects
 //TODO: Modify the GVCValidating page to (optionally) retain invalid form values so that user can edit.
 

    /**
     * Substitution key for value which failed validation.  This can be used in customized validation template messages.  An example: <br>
     * "The value you entered, &lt;&lt;failedValue&gt;&gt;, is longer than the &lt;&lt;attribute.width&gt;&gt; characters allowed for &lt;&lt;propertyName&gt;&gt; on &lt;&lt;entityName&gt;&gt;";
     */
    public static final String  FailedValue = "failedValue";


    /**
     * Substitution key for the localized (DisplayName) name of the attribute which failed validation.  This can be used in customized validation template messages.  An example: <br>
     * "The value you entered, &lt;&lt;failedValue&gt;&gt;, is longer than the &lt;&lt;attribute.width&gt;&gt; characters allowed for &lt;&lt;propertyName&gt;&gt; on &lt;&lt;entityName&gt;&gt;";
     */
    public static final String  PropertyName = "propertyName";


    /**
     * Substitution key for the localized (DisplayName) name of the entity containing the attribute which failed validation.  This can be used in customized validation template messages.  An example: <br>
     * "The value you entered, &lt;&lt;failedValue&gt;&gt;, is longer than the &lt;&lt;attribute.width&gt;&gt; characters allowed for &lt;&lt;propertyName&gt;&gt; on &lt;&lt;entityName&gt;&gt;";
     */
    public static final String  EntityName = "entityName";


    /**
     * Substitution key for the actual entity of the object which failed to validate or save (for exceptions raised during saveChanges()).  This can be used in customized validation template messages, though it is mostly for locating the template to be customized.  An example: <br>
     * "&lt;&lt;entity.userInfo.couldNotSave&gt;&gt;";
     * <br>This example takes the entire validation failure message from the key couldNotSave in the userInfo dictionary of the entity.
     */
    public static final String  EntityFailedOn = "entity";


    /**
     * Substitution key for the actual attribute of the object which failed to validate or save.  This can be used in customized validation template messages.  An example: <br>
     * "The value you entered is longer than &lt;&lt;attribute.width&gt;&gt;";
     */
    public static final String  AttributeFailedOn = "attribute";


    /**
     * Substitution key for the actual object which failed to save (for exceptions raised during saveChanges() only).  This can be used in customized validation template messages.  An example: <br>
     * "Your changes to the &lt;&lt;entityName&gt;&gt; \"&lt;&lt;object.toString&gt;&gt;\" could not be saved as another user has modified it.";
     */
    public static final String  ObjectFailedOn = "object";



    /**
     * Message key for nullity validation failure.  Thes is to be used in localization keys in strings files.  An example: <br>
     * Employee.firstName.nullNotAllowed = "You must enter a &lt;&lt;propertyName&gt;&gt; for this &lt;&lt;entityName&gt;&gt;.";
     */
    public static final String  NullNotAllowed = "nullNotAllowed";


    /**
     * Message key for excessive length validation failure.  Thes is to be used in localization keys in strings files.  An example: <br>
     * Employee.firstName.tooLong = "The value you entered, &lt;&lt;failedValue&gt;&gt;, is longer than the &lt;&lt;attribute.width&gt;&gt; characters allowed for &lt;&lt;propertyName&gt;&gt; on &lt;&lt;entityName&gt;&gt;";
     */
    public static final String  TooLong = "tooLong";


    /**
     * Message key for an invalid value (type mismatch, formatter failure, but not null) validation failure.  Thes is to be used in localization keys in strings files.  An example: <br>
     * Employee.age.invalidValue = "&lt;&lt;failedValue&gt;&gt; is not an acceptable &lt;&lt;propertyName&gt;&gt; for this &lt;&lt;entityName&gt;&gt;.";
     */
    public static final String  InvalidValue = "invalidValue";


    /**
     * Future use message keys for a value below a minimum value validation failure.  This is to be used in localization keys in strings files.  It should also be used in any custom code checking for these validation failures.  An example: <br>
     * Employee.age.belowMinimum = "&lt;&lt;entityName&gt;&gt; must be above 16 years of age.";
     */
    public static final String  BelowMinimum = "belowMinimum";


    /**
     * Future use message keys for a value above a maximum value validation failure.  This is to be used in localization keys in strings files.  It should also be used in any custom code checking for these validation failures.  An example: <br>
     * Employee.age.aboveMaximum = "&lt;&lt;entityName&gt;&gt; must be younger than 99 years of age.";
     */
    public static final String  AboveMaximum = "aboveMaximum";


    /**
     * Future use message keys for a value which is a unique validation failure.  This is to be used in localization keys in strings files.  It should also be used in any custom code checking for these validation failures.  An example: <br>
     * Employee.idNumber.notUnique = "The &lt;&lt;entityName&gt;&gt;'s ID number has already been used.";
     * 
     * @deprecated This functionality is handled by IntegrityConstraintViolation
     */
    public static final String  NotUnique = "notUnique";


    /**
     * Message key for deletion validation failure.  Thes is to be used in localization keys in strings files.  An example: <br>
     * Employee.deletionNotAllowed = "Current Employeess can't be deleted.";
     */
    public static final String  DeletionNotAllowed = "deletionNotAllowed";


    /**
     * Message key for optimistic locking save failure.  This is to be used in localization keys in strings files.  An example: <br>
     * Employee.optimisticLockingFailure = "Another user has changed this &lt;&lt;entityName&gt;&gt;, check your changes and re-submit.";
     */
    public static final String  OptimisticLockingFailure = "optimisticLockingFailure";

    
    /**
     * Message key for integrity constraint violation save failure.  This is to be used in localization keys in strings files.  
     * &lt;&lt;failedValue&gt;&gt; is set to the name of the constraint that failed to validate. An example: <br>
     * Employee.UniqueLoginName.integrityConstraintViolation = "This login name is already in use, please choose a different one.";
     */
    public static final String IntegrityConstraintViolation = "integrityConstraintViolation";
    

    /**
     * Message keys for delete failure.  This is to be used in localization keys in strings files.  An example: <br>
     * Employee.failedToDelete = "This &lt;&lt;entityName&gt;&gt; is still employed and can not be deleted.";
     */
    public static final String  FailedToDelete = "failedToDelete";


    /**
     * Message keys for insertion failure.  This is to be used in localization keys in strings files.  An example: <br>
     * Employee.failedToInsert = "Database has been disconnected.";
     */
    public static final String  FailedToInsert = "failedToInsert";


    /**
     * Message keys for failed to update.  This is to be used in localization keys in strings files.  An example: <br>
     * Employee.failedToUpdate = "Database has been disconnected.";
     */
    public static final String  FailedToUpdate = "failedToUpdate";


    /**
     * Message keys for failed to lock.  This is to be used in localization keys in strings files.  An example: <br>
     * Employee.failedToLock = "Another user is changing this &lt;&lt;entityName&gt;&gt;, please try again in a few minutes.";
     */
    public static final String  FailedToLock = "failedToLock";


    /**
     * Message keys for a stored procedure failure.  This is to be used in localization keys in strings files.  An example: <br>
     * Employee.failedOnStoredProcedure = "Call the DBA!  :-).";
     */
    public static final String  FailedOnStoredProcedure = "failedOnStoredProcedure";


    /**
     * Message key for localized display name for entities and properties.  This are to be used in localization keys in strings files. An example: <br>
     * Employee.firstName.displayName = "given name";<br>
     * Employee.displayName = "client";
     */
    public static final String  DisplayName = "displayName";


    /**
     * Message key for a more detailed description for entities, attributes and relationships.  This is intended for
     * future use in help dialogs and on screen hints. These are to be used in localization keys in strings files.  An example: <br>
     * <p>WorkOrder.description="The work requisition issued to perform this task.";</p>
     * <p>WorkOrder.orderDate.description="The date that the order requistion was filled out.";</p>
     */
    public static final String  Description = "description";


    /**
     * String used when forming generic (i.e. without the actual entity name) key paths for localization.  An example: <br>
     * Entity.nullNotAllowed = "You must enter a &lt;&lt;propertyName&gt;&gt; for this &lt;&lt;entityName&gt;&gt;.";
     */
    public static final String EntityGenericName = "Entity";


    /**
     * A set of all failure keys.
     */
    public static final NSSet setOfAllFailureKeys = new NSSet(new Object[] {NullNotAllowed, TooLong, InvalidValue, BelowMinimum, AboveMaximum, NotUnique, OptimisticLockingFailure, IntegrityConstraintViolation, FailedToDelete, FailedToInsert, FailedToUpdate, FailedToLock, FailedOnStoredProcedure});


    static protected boolean shouldTreatEmptyStringsAsNull = true;
    static protected boolean hasInstalled = false;



    /**
     * Main validation method.  Call this once to install validation.
     */
    public static synchronized void installValidation()
    {
        if ( ! hasInstalled)
        {
            EOModelNotificationProxy.listenForAddedModels();
            hasInstalled = true;
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelInformational))
                NSLog.debug.appendln("*** EOFValidation installed");
        }
    }


    /**
     * Returns <code>true</code> if empty / all white space strings are to be considered null for the purposes of validation.
     * The default is <code>true</code>.
     *
     * @return <code>true</code> if empty strings are to be considered null for the purposes of validation 
     */
    public static boolean shouldTreatEmptyStringsAsNull()
    {
        return shouldTreatEmptyStringsAsNull;
    }



    /**
     * Use to indicate whether empty / all white space strings are to be considered null for the purposes of validation.
     *
     * @param treatAsNull <code>true</code> if empty strings are to be considered null when validating.
     */
    public static void setShouldTreatEmptyStringsAsNull(boolean treatAsNull)
    {
        shouldTreatEmptyStringsAsNull = treatAsNull;
    }



    /**
     * Private constructor.  
     */
    private EOFValidation()
    {
        super();
    }



}
