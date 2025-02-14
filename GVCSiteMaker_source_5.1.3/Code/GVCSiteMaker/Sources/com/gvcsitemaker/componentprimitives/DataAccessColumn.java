// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;

import com.gvcsitemaker.core.interfaces.ValidationMessageStore;
import com.gvcsitemaker.core.utility.MIMEUtils;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import er.extensions.components.ERXComponentUtilities;

import net.global_village.eofvalidation.EOFValidation;
import net.global_village.virtualtables.VirtualField;
import net.global_village.woextensions.Response;


/**
 * Provides common functionality for the UI for com.gvcsitemaker.core.pagecomponent.DataAccessColumns of all types.  The actual UI is provided by sub-classes.<br/><br/>
 *
 * <b>Validation</b><br/>
 * DataAccessColumns have unusual validation their stateless nature.  See the interface com.gvcsitemaker.core.ValidationMessageStore (GVCSMCore), DataAccessSubEditor (Editors.subproj), and DataAccessMode (ComponentPrimitives.subproj) as sub-classes for details on how this works.
 */
public class DataAccessColumn extends ComponentPrimitive
{

    public NSArray comparisonTypes = com.gvcsitemaker.core.support.DataAccessSearchCriterion.COMPARISON_TYPES;
    public String aComparisonType;



    /**
     * Designated constructor
     */
    public DataAccessColumn(WOContext context)
    {
        super(context);
    }



    /**
     * Returns true, this component is stateless.
     *
     * @return true, this component is stateless.
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Returns componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessColumn.
     *
     * @return componentObject() downcast to com.gvcsitemaker.core.pagecomponent.DataAccessColumn.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessColumn dataAccessColumn()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessColumn) componentObject();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns parent() cast to com.gvcsitemaker.core.ValidationMessageStore.
     *
     * @return parent() cast to com.gvcsitemaker.core.ValidationMessageStore.
     */
    public ValidationMessageStore validationMessageStore()
    {
        return (ValidationMessageStore)parent();
    }



    /**
     * Returns <code>true</code> if we are configuring a DataAccessColumn for Add mode.  This allows
     * control over UI elements specific to this situation.
     *
     * @return <code>true</code> if we are configuring a DataAccessColumn for Add mode
     */
    public boolean isConfiguringAddMode()
    {
        Object currentDataAccessMode = valueForBinding("currentDataAccessMode");

        return (currentDataAccessMode != null) && (currentDataAccessMode.equals(com.gvcsitemaker.core.pagecomponent.DataAccessMode.ADD_MODE));
    }



    /**
     * Returns <code>true</code> if we are configuring a DataAccessColumn for Search mode.
     * This allows control over UI elements specific to this situation.
     *
     * @return <code>true</code> if we are configuring a DataAccessColumn for Search mode
     */
    public boolean isConfiguringSearchMode()
    {
        Object currentDataAccessMode = valueForBinding("currentDataAccessMode");

        return (currentDataAccessMode != null) && (currentDataAccessMode.equals(com.gvcsitemaker.core.pagecomponent.DataAccessMode.SEARCH_MODE));
    }



    /**
     * Returns the validation error caused by an attempt to delete a value that is used in a lookup list.  Returns null if no validation error occured.
     *
     * @return the validation error caused by an attempt to delete a value that is used in a lookup list.  Returns null if no validation error occured.
     */
    public String deleteValidationFailure()
    {
        /** require
        [display_mode] isInDisplayMode();
        [parent_is_ValidationMessageStore] parent() instanceof ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(dataAccessColumn().normalizedFieldName() + ".cantDelete");
    }



    /**
     * Returns the validation error caused by an optimistic locking failure while saving.  Returns null if no error occured.
     *
     * @return the validation error caused by an optimistic locking failure while saving.  Returns null if no error occured
     */
    public String optimisticLockingFailure()
    {
        /** require
        [display_mode] isInDisplayMode();
        [parent_is_ValidationMessageStore] parent() instanceof ValidationMessageStore;
        **/
        return validationMessageStore().validationFailureForKey(dataAccessColumn().normalizedFieldName() + "." + EOFValidation.OptimisticLockingFailure);
    }



    /**
     * Returns <code>true</code> if values in the column should be XML escaped.  This is
     * the case when the section's MIME type is text/xml and ! xmlEncodingOff().
     *
     * @see #xmlEncodingOff()
     * @return <code>true</code> if values in the column should be XML escaped
     */
    public boolean shouldXMLEncodeData()
    {
        return MIMEUtils.isXMLMimeType(context().response().headerForKey(Response.ContentTypeHeaderKey)) && ! xmlEncodingOff();
    }



    //************* Generic Accessors and Mutators below here *****************
    public VirtualField field()
    {
        return (VirtualField)valueForBinding("field");
    }
    public void setField(VirtualField newValue)
    {
        setValueForBinding(newValue, "field");
    }

    public Object fieldValue()
    {
        return valueForBinding("fieldValue");
    }
    public void setFieldValue(Object newValue)
    {
        setValueForBinding(newValue, "fieldValue");
    }

    public int index()
    {
        return ((Integer) valueForBinding("index")).intValue();
    }
    public void setIndex(Object newIndex)
    {
        setValueForBinding(newIndex, "index");
    }



    /**
     * Should be bound to <code>true</code> if values in the column should <b>not</b> be XML escaped
     * when the section's MIME type is text/xml
     *
     * @return <code>true</code> if values in the column should <b>not</b> be XML escaped
     * when the section's MIME type is text/xml
     */
    public boolean xmlEncodingOff()
    {
        return ERXComponentUtilities.booleanValueForBinding(this, "xmlEncodingOff", false);
    }
    public void setXmlEncodingOff(Object newIndex)
    {
       // Not settable
    }


    public String currentDataAccessMode()
    {
        return (String) valueForBinding("currentDataAccessMode");
    }
}
