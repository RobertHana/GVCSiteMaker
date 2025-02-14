/**
 * Implementation of DataAccessAddMode common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import com.gvcsitemaker.core.utility.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.virtualtables.*;



/**
 * DataAccessAddMode represents the UI and configuration specific to the Add mode of a DataAccess PageComponent.  It controls the specific bindings, template, etc.  See corresponding ComponentPrimitive DataAccessAddMode for UI.
 */
public class DataAccessAddMode extends _DataAccessAddMode
{


    /**
     * Factory method to create new instances of DataAccessAddMode.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessAddMode or a subclass.
     */
    public static DataAccessAddMode newDataAccessAddMode()
    {
        return (DataAccessAddMode) SMEOUtils.newInstanceOf("DataAccessAddMode");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessAddMode");

    }



    /**
     * Returns the symbolic name of this mode this mode.
     *
     * @return the symbolic name of this mode this mode.
     */
    public String mode()
    {
        return DataAccessMode.ADD_MODE;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> as this mode will allow edits to the underlying data.
     *
     * @return <code>true</code> as this mode will allow edits to the underlying data.
     */
    public boolean isEditable()
    {
        return true;
    }



    /**
     * Synchronizes the DataAccessColumns in this mode (toChildren()) with the Columns in databaseTable(): creates a
     * DataAccessColumn for each Column in databaseTable() and which does not already have a corresponding
     * DataAccessColumn in this mode and removes any DataAccessColumns which do not have a corresponding Column in
     * databaseTable().
     */
    public void synchronizeColumnsWithTable()
    {
        /** require [has_table] databaseTable() != null;  **/

        // Don't synch based on cached info!
        clearCachedValues();

        addColumns(databaseTable().editableColumns());
        removeColumnsNotIn(databaseTable().editableColumns());

        /** ensure
        [all_columns_captured] toChildren().count() == databaseTable().editableColumns().count();
        [each_column_added] (forall i : {0 .. databaseTable().editableColumns().count() - 1} # hasDataAccessColumnForColumn((net.global_village.virtualtables.Column)databaseTable().editableColumns().objectAtIndex(i)));
        [extra_columns_removed] (forall i : {0 .. toChildren().count() - 1} # databaseTable().columns().containsObject(((DataAccessColumn) toChildren().objectAtIndex(i)).column()));
        **/
    }



    /**
     * Overridden to make editable by default as non-editable columns are not useful in add mode.
     *
     * @param dataAccessColumn - the column to set defaults for.
     */
    public void setDefaultsForNewColumn(DataAccessColumn aDataAccessColumn)
    {
        /** require [valid_param] aDataAccessColumn != null;  [not_system_column] ! aDataAccessColumn.isSystemColumn();  **/
        super.setDefaultsForNewColumn(aDataAccessColumn);
        aDataAccessColumn.setIsEditable(true);
    }



    /**
     * Returns the default custom HTML WOComponent template for this mode.
     *
     * @return the default custom HTML WOComponent template for this mode.
     */
    public String defaultCustomTemplate()
    {
        StringBuffer template = new StringBuffer(2048);
        template.append("  <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"left\" valign=\"middle\">\n");
        template.append("         <span class=\"dataAccessSubtitle\">Add Record to Database Table:</span> <span class=\"dataAccessColoredSubtitle\">\"<WebObject name=DataAccess_TableName></WebObject>\"</span>\n");
        template.append("      </td>\n");
        template.append("    </tr>\n");
        template.append("  </table>\n");
        template.append("  <hr>\n");
        template.append("  <table border=\"1\" cellpadding=\"4\" cellspacing=\"0\" align=\"center\" id=\"dataAccessTable\">\n");

        // Add Tags for the editable fields (columns) in this table
        Enumeration componentEnum = editableFields().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();
            template.append("    <tr>\n");
            template.append("      <th class=\"dataAccessTableHeading\">\n");
            template.append("        " + aComponent.fieldName() + "\n");
            template.append("      </th>\n");
            template.append("      <td class=\"dataAccessTableCell\">\n");
            template.append("        <WebObject name=" + aComponent.nameInTemplate() + "></WebObject>&nbsp;\n");
            template.append("      </td>\n");
            template.append("    </tr>\n");
        }

        template.append("  </table>\n");
        template.append("  <hr>\n");
        template.append("  <WebObject name=DataAccess_CanEditRecords>\n");
        template.append("    <span id=\"dataAccessActions\" style=\"text-align: right;\">\n");
        template.append("      <input type=\"submit\" value=\"Cancel\" name=\"<WebObject name=DataAccess_CancelName></WebObject>\">\n");
        template.append("      <img src=\"/GVCSiteMaker/Images/spacer.gif\" width=\"5\" height=\"2\">\n");
        template.append("      <input type=\"submit\" value=\"Submit\" name=\"<WebObject name=DataAccess_SaveName></WebObject>\">\n");
        template.append("      <img src=\"/GVCSiteMaker/Images/spacer.gif\" width=\"5\" height=\"2\">\n");
        template.append("      <input type=\"submit\" value=\"Save & Add Another\" name=\"<WebObject name=DataAccess_SaveAndAddName></WebObject>\">\n");
        template.append("    </span>\n");
        template.append("  </WebObject name=DataAccess_CanEditRecords>\n");

        template.append("  <WebObject name=DataAccess_ShowNotificationNotNeededCheckBox>\n");
        template.append("    <span style=\"text-align: right;\">\n");
        template.append("      <WebObject name=DataAccess_ShouldNotSendNotification></WebObject> No notification needed\n");
        template.append("    </span>\n");
        template.append("  </WebObject name=DataAccess_ShowNotificationNotNeededCheckBox>\n");

        return template.toString();
    }



    /**
     * Returns the WOD file bindings to use with this mode.
     *
     * @return the WOD file bindings to use with this mode.
     */
    public String generatedBindings()
    {
        StringBuffer wodFile = new StringBuffer(2048);

        addCommonBindings(wodFile);
        addFieldBindings(wodFile, editableFields());

        // Add and add more function specific to Add Record Mode
        wodFile.append("DataAccess_SaveAndAddButton: WOSubmitButton {\n");
        wodFile.append("    action = addMoreRecords;\n");
        wodFile.append("    value = \"Save & Add Another\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_SaveAndAddName: SubmitName {\n");
        wodFile.append("    action = addMoreRecords;\n");
        wodFile.append("    inputName = \"DataAccess_SaveAndAddName\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Cancel function specific to Add Record Mode
        wodFile.append("DataAccess_CancelButton: WOSubmitButton {\n");
        wodFile.append("    action = cancelChanges;\n");
        wodFile.append("    value = \"Cancel\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_CancelName: SubmitName {\n");
        wodFile.append("    action = cancelChanges;\n");
        wodFile.append("    inputName = \"DataAccess_CancelName\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");


        // Editable fields collection specific to Add Record Mode
        wodFile.append("DataAccess_EditableFields: WORepetition {\n");
        wodFile.append("    item = aDataAccessColumn;\n");
        wodFile.append("    list = editableFields;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        return wodFile.toString();
        /** ensure [valid_result] Result != null;  **/
    }




    /**
     * Returns a newly created row in databaseTable() with all of the default values set.
     *
     * @return a newly created row in databaseTable() with all of the default values set.
     */
    public EOEnterpriseObject newRow()
    {
        /** require [can_add_row]  databaseTable() instanceof VirtualTable;  **/
        EOEnterpriseObject newRow = ((VirtualTable)databaseTable()).newRow();

        Enumeration componentEnum = toChildren().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            if ( ! aComponent.isSystemColumn())
            {
                aComponent.setDefaultFor(newRow);
            }
        }

        return newRow;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Performs some extra validations, paritcularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }



    //************** Binding Cover Methods **************\\



}

