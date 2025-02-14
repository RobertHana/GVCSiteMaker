/**
 * Implementation of DataAccessListMode common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import com.gvcsitemaker.core.utility.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * DataAccessListMode represents the UI and configuration specific to the List of Records mode of a DataAccess PageComponent.  It controls the specific bindings, template, etc.  See corresponding ComponentPrimitive DataAccessListMode for UI.
 */
public class DataAccessListMode extends _DataAccessListMode
{


    /**
     * Factory method to create new instances of DataAccessListMode.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessListMode or a subclass.
     */
    public static DataAccessListMode newDataAccessListMode()
    {
        return (DataAccessListMode) SMEOUtils.newInstanceOf("DataAccessListMode");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessListMode");

    }



    /**
     * Returns the symbolic name of this mode this mode.
     *
     * @return the symbolic name of this mode this mode.
     */
    public String mode()
    {
        return DataAccessMode.LIST_MODE;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the default custom HTML WOComponent template for this mode.
     *
     * @return the default custom HTML WOComponent template for this mode.
     */
    public String defaultCustomTemplate()
{
        StringBuffer template = new StringBuffer(2048);

        addFormOpen(template);

        template.append("    <tr>\n");

        // Titles
        Enumeration componentEnum = visibleFields().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            template.append("      <th class=\"dataAccessTableHeading\">\n");
            template.append("        ");
            template.append(aComponent.fieldName());
            template.append('\n');
            template.append("      </th>\n");

        }

        // Details and Delete flag titles
        template.append("      <th class=\"dataAccessTableHeading\">\n");
        template.append("        Show\n");
        template.append("      </th>\n");
        template.append("      <WebObject name=DataAccess_CanDeleteRecords><th class=\"dataAccessTableHeading\">\n");
        template.append("        Delete\n");
        template.append("      </th></WebObject name=DataAccess_CanDeleteRecords>\n");
        template.append("    </tr>\n");

        // Field Values
        template.append("    <WebObject name=DataAccess_RecordsInGroup><tr>\n");

        componentEnum = visibleFields().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            template.append("      <td class=\"dataAccessTableCell\">\n");
            template.append("        <WebObject name=" + aComponent.nameInTemplate() + "></WebObject>&nbsp;\n");
            template.append("      </td>\n");
        }

        // Details link and Delete flag
        template.append("      <td class=\"dataAccessTableCell\">\n");
        template.append("        <WebObject name=DataAccess_ShowDetails>Details...</WebObject>&nbsp;\n");
        template.append("      </td>\n");
        template.append("      <WebObject name=DataAccess_CanDeleteRecords><td class=\"dataAccessTableCell\">\n");
        template.append("        <WebObject name=DataAccess_DeleteRecordCheckbox></WebObject>&nbsp;\n");
        template.append("      </td></WebObject name=DataAccess_CanDeleteRecords>\n");
        template.append("    </tr>\n");
        template.append("    </WebObject name=DataAccess_RecordsInGroup>\n");

        addFormClose(template);


        return template.toString();
    }



    /**
     * Adds the confirmation dialog for deleting records for the Single Record and List of Records template.
     *
     * @param template - StringBuffer containing template to add the confirmation dialog to.
     */
    protected void addConfirmationActions(StringBuffer template)
    {
        /** require [valid_template] template != null;     **/
        template.append("  <WebObject name=DataAccess_IsConfirmingDeletion>\n");
        template.append("    <table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\">\n");
        template.append("      <tr>\n");
        template.append("        <td align=\"center\" valign=\"middle\" colspan=\"3\">\n");
        template.append("          <WebObject name=DataAccess_DeletedRecordCount></WebObject> record(s) selected for deletion.<br><br>Proceed with deletion?\n");
        template.append("        </td>\n");
        template.append("      </tr>\n");
        template.append("      <tr>\n");
        template.append("        <td align=\"right\" valign=\"middle\">\n");
        template.append("          <input type=\"submit\" value=\"Yes\" name=\"<WebObject name=DataAccess_AcceptDeletion></WebObject>\">\n");
        template.append("        </td>\n");
        template.append("        <td align=\"center\" valign=\"middle\">&nbsp;</td>\n");
        template.append("        <td align=\"left\" valign=\"middle\">\n");
        template.append("          <input type=\"submit\" value=\"No!\" name=\"<WebObject name=DataAccess_RejectDeletion></WebObject>\">\n");
        template.append("        </td>\n");
        template.append("      </tr>\n");
        template.append("      <tr>\n");
        template.append("        <td align=\"center\" valign=\"middle\" colspan=\"3\">\n");
        template.append("          If you do not proceed with the deletion, you will be returned to the previous page with your edits intact, but unsaved.\n");
        template.append("        </td>\n");
        template.append("      </tr>\n");
        template.append("    </table>\n");
        template.append("  </WebObject name=DataAccess_IsConfirmingDeletion>\n");
    }



    /**
     * Returns the WOD file bindings to use with this mode.
     *
     * @return the WOD file bindings to use with this mode.
     */
    public String generatedBindings()
    {
        StringBuffer wodFile = new StringBuffer(2048);

        addDataAccessNavigationBindings(wodFile);
        addDataAccessActionBindings(wodFile);
        addFieldBindings(wodFile, visibleFields());
        addCommonBindings(wodFile);

        // Records in Group collection specific to List of Records mode
        wodFile.append("DataAccess_RecordsInGroup: WORepetition {\n");
        wodFile.append("    item = currentRow;\n");
        wodFile.append("    list = recordsInCurrentGroup;\n");
        wodFile.append("    index = index;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Show Details link specific to List of Records mode
        wodFile.append("DataAccess_ShowDetails: WOHyperlink {\n");
        wodFile.append("    href = singleRecordUrl;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Show Delete checkbox specific to List of Records mode
        wodFile.append("DataAccess_DeleteRecordCheckbox: WOCheckBox {\n");
        wodFile.append("    value = currentRowPK;\n");
        wodFile.append("    selection = markRecordForDeletion;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Count of deleted records specific to deletion confirmation in List of Records mode
        wodFile.append("DataAccess_DeletedRecordCount: WOString {\n");
        wodFile.append("    value = numberOfRecordsMarkedForDeletion;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // The currently selected record range
        wodFile.append("DataAccess_RecordOrRecordRangeBeingViewed: WOString {\n");
        wodFile.append("    value = recordRangeInGroup;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Count of records in the found set
        wodFile.append("DataAccess_NumberOfRecordsInFoundSet: WOString {\n");
        wodFile.append("    value = numberOfRecordsInFoundSet;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Count of records in the table, possibly restricted by the default search
        wodFile.append("DataAccess_NumberOfRecordsInTable: WOString {\n");
        wodFile.append("    value = numberOfRecordsInTable;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        return wodFile.toString();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Adds the mode specific actions to template.
     *
     * @param template - StringBuffer containing template to add mode specific actions to
     */
    protected void addModeSpecificDataAccessActions(StringBuffer template)
    {
        template.append("            <WebObject name=DataAccess_CanAddRecords>\n");
        template.append("              <td align=\"center\" valign=\"middle\">\n");
        template.append("                <WebObject name=DataAccess_AddRecord>Add</WebObject>\n");
        template.append("              </td>\n");
        template.append("            </WebObject name=DataAccess_CanAddRecords>\n");

        template.append("            <WebObject name=DataAccess_CanImportRecords>\n");
        template.append("              <td align=\"left\" valign=\"middle\">\n");
        template.append("                <WebObject name=DataAccess_ImportRecords>Import</WebObject>\n");
        template.append("              </td>\n");
        template.append("            </WebObject name=DataAccess_CanImportRecords>\n");
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

