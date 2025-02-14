// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.interfaces.ValidationMessageStore;
import com.gvcsitemaker.core.support.DataAccessSearchParameters;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSValidation;

import net.global_village.foundation.ExceptionConverter;
import net.global_village.virtualtables.Column;
import net.global_village.virtualtables.Table;



/**
 * Implements a component that users can use in their custom search templates.  Allows validation and 
 * other features desirable to users when searching.
 *
 * Note: this component is no longer used, due to the added complextity that it would add to custom 
 * search templates. In the future, we may put this back into production, with some changes (such as 
 * removing the joinCondition and comparisonType params from the component so that the user can 
 * customize these).
 *
 * @deprecated You shouldn't use this class.  Users will use custom HTML to form the search template instead.
 */
public class CustomSearchComponent extends ComponentPrimitive
{
    public Object value;
    protected String errorMessage = null;


    /**
     * Designated constructor.
     */
    public CustomSearchComponent(WOContext context)
    {
        super(context);
    }



    /**
     * Overridden to clear validation error messages.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        errorMessage = null;
    }



    /**
     * Gets the column that this component uses from the bindings.
     *
     * @return the column to use
     */
    public Column column()
    {
        return ((Table)valueForBinding("table")).columnNamed((String)valueForBinding("columnName"));
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Gets the index that this component uses from the bindings.
     *
     * @return the column to use
     */
    public Integer index()
    {
        return (Integer)valueForBinding("index");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if <code>index()</code> returns 0, <code>false</code> otherwise.  Used to determine if the join condition should be added to the form for this index.
     *
     * @return <code>true</code> if <code>index()</code> returns 0, <code>false</code> otherwise
     */
    public boolean isFirstCriteria()
    {
        return index().intValue() == 0;
    }



    /**
     * Returns <code>true</code> if there is an error in this component, <code>false</code> otherwise.
     *
     * @return <code>true</code> if there is an error in this component, <code>false</code> otherwise
     */
    public boolean hasError()
    {
        return errorMessage != null;
    }



    /**
     * Returns the error message or <code>null</code> if there is none.
     *
     * @return the error message or <code>null</code> if there is none
     */
    public String errorMessage()
    {
        return errorMessage;
    }



    /**
     * Returns the join condition param key with the index number appended to it.
     *
     * @return the join condition param key with the index number appended to it
     */
    public String joinConditionParamName()
    {
        return DataAccessSearchParameters.JOIN_CONDITION_CRITERION_KEY + index().toString();
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the column param key with the index number appended to it.
     *
     * @return the column param key with the index number appended to it
     */
    public String columnParamName()
    {
        return DataAccessSearchParameters.COLUMN_CRITERION_KEY + index().toString();
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the comparison type param key with the index number appended to it.
     *
     * @return the comparison type param key with the index number appended to it
     */
    public String comparisonTypeParamName()
    {
        return DataAccessSearchParameters.COMPARISON_TYPE_CRITERION_KEY + index().toString();
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the value param key with the index number appended to it.
     *
     * @return the value param key with the index number appended to it
     */
    public String valueParamName()
    {
        return DataAccessSearchParameters.VALUE_CRITERION_KEY + index().toString();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * EOF Validation method for the criterionValue binding.
     */
    public Object validateValue(Object newValue) throws NSValidation.ValidationException
    {
        try
        {
            Class validatingColumnClass = Class.forName("com.gvcsitemaker.core.pagecomponent.DataAccess" + column().type().name() + "Column");
            return NSSelector.invoke("validateColumnSearchValue", Column.class, String.class, validatingColumnClass, column(), newValue);
        }
        catch (NSValidation.ValidationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
    }



    /**
     * Overridden to handle validation errors caused by data input when searching.
     */
    public void validationFailedWithException(Throwable t, Object aValue, String keyPath)
    {
        if (keyPath.equals(DataAccessSearchParameters.VALUE_CRITERION_KEY))
        {
            // Force taking the value
            value = aValue;

            errorMessage = t.getMessage();
            // Notify the parent
            ((ValidationMessageStore)parent()).registerValidationFailureForKey(t.getMessage(), index().toString());
        }
        else
        {
            super.validationFailedWithException(t, aValue, keyPath);
        }
    }


}
