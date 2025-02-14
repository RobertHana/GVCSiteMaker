// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/**
 * This page allows a column (a.k.a. field in GVC.SiteMaker) name and / or description to be edited.
 */
public class EditCalculationPane extends WOComponent
{
    protected EOEditingContext editingContext;
    protected String invalidExpressionError;

    /** @TypeInfo Column */
    protected Column column;
    protected String expressionResult;
    protected String expressionResultType;

    public Column availableColumn;



    /**
     * Designated constructor.
     */
    public EditCalculationPane(WOContext context)
    {
        super(context);
    }



    /**
     * Sets the Column to edit on this page.
     *
     * @param newColumn - the column to edit.
     */
    public void setColumn(Column newColumn)
    {
        /** require
        [valid_param] newColumn != null;
        [valid_ec] newColumn.editingContext() != null; **/

        setEditingContext(newColumn.editingContext());
        column = newColumn;
    }



    /**
     * Validates the new column information and save changes, returning to the revise table page, if it is valid.  If it is not valid, redisplays this page with error messages.
     */
    public void saveChanges()
    {
        if (! hasErrors())
        {
            // It would be nice to not have to do this manually.
            databaseTable().setModifiedBy((User) EOUtilities.localInstanceOfObject(editingContext(),
                                                                                   ((SMSession)session()).currentUser()));
            databaseTable().setDateModified(Date.now());

            try
            {
                editingContext().saveChanges();
                AjaxModalDialog.close(context());
            }
            catch ( NSValidation.ValidationException e)
            {
                editingContext().revert();

                AjaxModalDialog.update(context(), null);  // This is to update the page if we display errors later on

                // The only errors here should be programming errors.  Just raise and let the error logger deal with it.
                throw e;
            }
        }
        else
        {
            AjaxModalDialog.update(context(), null);
        }
    }



    /**
     * Action used to cancel changes and return to AddEditDatabaseTable.
     *
     * @return the <code>AddEditDatabaseTableColumns</code> page set for databaseTable().
     */
    public void cancelChanges()
    {
        editingContext().revert();
        AjaxModalDialog.close(context());
    }



    /**
     * Returns <code>true></code> if there are any validation errors which would prevent the creation of the column.
     *
     * @return <code>true></code> if there are any validation errors which would prevent the creation of the column.
     */
    public boolean hasErrors()
    {
        return (invalidExpressionError() != null);
    }



    /**
     * Submitting the page tests the expression for validity.  If it is valid, this method also gets an example
     * result and type and shows it on the page.
     *
     * @return this page
     */
    public void testExpression()
    {
        if (invalidExpressionError() == null)
        {
            Object exampleValue = calculatedColumn().exampleValue();
            setExpressionResult(exampleValue.toString());
            setExpressionResultType(calculatedColumn().expressionType(exampleValue));
        }
        AjaxModalDialog.update(context(), null);
    }



    /**
     * @return <code>true</code> if column() instanceof VirtualCalculatedColumn
     */
    public boolean isCalculatedColumn()
    {
        return column() instanceof VirtualCalculatedColumn;
    }



    /**
     * @return column() downcast to VirtualCalculatedColumn
     */
    public VirtualCalculatedColumn calculatedColumn()
    {
        /** require [is_VirtualCalculatedColumn] isCalculatedColumn();  **/
        return (VirtualCalculatedColumn) column();
    }



    /**
     * Clears validation messages after page is rendered
     *
     * @see net.global_village.woextensions.ClickToOpenComponent#appendToResponse(com.webobjects.appserver.WOResponse, com.webobjects.appserver.WOContext)
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        setInvalidExpressionError(null);
        setExpressionResult(null);
        setExpressionResultType(null);
    }



    /**
     * Catches expression validation exceptions to display in the UI.
     *
     * @see com.webobjects.appserver.WOComponent#validationFailedWithException(java.lang.Throwable, java.lang.Object, java.lang.String)
     */
    public void validationFailedWithException(Throwable exception, Object badValue, String keyPath)
    {
        if ("calculatedColumn.expression".equals(keyPath))
        {
            calculatedColumn().setExpression((String)badValue);
            setInvalidExpressionError(exception.getLocalizedMessage());
        }
        else
        {
            super.validationFailedWithException(exception, badValue, keyPath);
        }
    }



    /**
     * Returns the appropriate page title for the mode of this page (add/edit).
     *
     * @return the appropriate page title for the mode of this page (add/edit).
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Revise Field in Database Table";
    }


    //****************************  Generic Accessors Below Here  ****************************\\

    public SMVirtualTable databaseTable() {
        return (SMVirtualTable) column().table();
    }

    public EOEditingContext editingContext() {
        return editingContext;
    }
    public void setEditingContext(EOEditingContext newEditingContext) {
        editingContext = newEditingContext;
    }

    /** @TypeInfo Column */
    public Column column() {
        return column;
    }


    public String invalidExpressionError() {
        return invalidExpressionError;
    }
    public void setInvalidExpressionError(String message) {
        invalidExpressionError = message;
    }

    public String expressionResult() {
        return expressionResult;
    }
    public void setExpressionResult(String message) {
        expressionResult = message;
    }

    public String expressionResultType() {
        return expressionResultType;
    }
    public void setExpressionResultType(String message) {
        expressionResultType = message;
    }


}
