// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.interfaces;



/**
 * This interface describes a class that can record validation error messages and later return them.  Multiple messages are handled.  Although there may be other uses for this functionality, the concept was created to handle EO aware stateless WOComponents.  This interface is intended to be implemented by a stateful component which contains stateless components.<br/>
 *
 * EO Validation in stateless components is problematic.  A stateless component's instance variables need to be reset after each phase (takeValues, invokeAction, appendToResponse) so that it can be used in repetitions.  If values are validated using the standard validate value method (<code>public Object validateKey(Object aValue) throws NSValidation.ValidationException</code>), then during the takeValues phase any validation failures will be reported to <code>public void validationFailedWithException(Throwable t, Object value, String keyPath)</code> in the stateless WOCompoent sub-class.  At this point it could notify its parent of the invalid data.  However, it cannot retain any knowledge of the invalid data itself as it is stateless and retaining the knowledge requires state.  This rules out solutions such as setting a flag during the takeValues phase to display a validation error message during appendToResponse.  Further, since the values are detected as being invalid they are not set into the EO.  The result of this is that during the appendToResponse phase the data is no longer invalid and so validation errors can not be redetected at that point.<br/>
 * One possible solution to accomodate these needs and problems is to implement this in the EO and stateless WOComponent:<ul>
 * <li>Use validation methods with non-standard names to prevent automatic detection of validation errors during takeValues and also to allow these <b>invalid</b> values to be set into the EO.</li>
 * <li>Override takeValues to manually check for validation errors and notify our parent.</li>
 * <li>Override appendToResponse to manually check for validation errors and generate validation error messages.</li>
 * <li>Add manual validation to the validateForSave() method of the EO.</li>
 * </ul>
 * This solution has two drawbacks.  First it is complex and requires non-standard validation.  The second, and more serious, is that it does not handle situations where the invalid data cannot be set into the EO due to type mismatch problems (e.g. invalid dates where the invalid value is not a date).  This interface was created to address these issues and present a complete solution.<br>
 * When taking user input any validation errors are recorded in a parent() component which implements this interface and which is not stateless.  During the takeValues phase any validation failures caused by formatter failures etc will be reported to <code>public void validationFailedWithException(Throwable t, Object value, String keyPath)</code> in the stateless component.  The stateless component should implement this method and notify its parent of the validation failure by calling registerValidationFailureForKey(String validationFailure, String key as it cannot retain the knowledge.  During the appendToResponse the stateless component will query its parent by calling validationFailureForKey(String aKey) to see if there is a validation failure message.
  */
public interface ValidationMessageStore
{

    /**
     * Records the validation failure for later retrieval in validationFailureForKey(String aKey).  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param validationFailure - message to display in the UI for this validation failure.
     * @param key - a unique key to associate this failure with
     */
    public void registerValidationFailureForKey(String validationFailure, String key);

    

    /**
     * Returns the registered validation error for aKey or null if no validation error was registered.  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param key - a unique key associated with this failure
     * @return the appropriate registered validation error or null if no validation error was registered.
     */
    public String validationFailureForKey(String aKey);



    /**
     * Returns true if any validation errors have been registered.
     *
     * @return true if any validation errors have been registered.
     */
    public boolean hasValidationFailures();



    /**
     * Removes any validation errors that have been registered.  Implementing classes should call this at an appropriate time (e.g. after calling super.appendToResponse in a WOComponent).
     */
    public void clearValidationFailures();

}
