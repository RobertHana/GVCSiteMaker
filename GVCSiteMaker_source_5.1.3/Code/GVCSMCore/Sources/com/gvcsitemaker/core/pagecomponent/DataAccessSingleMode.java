/**
 * Implementation of DataAccessSingleMode common to all installations.
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
 * DataAccessSingleMode represents the UI and configuration specific to the Single Record mode of a DataAccess PageComponent.  It controls the specific bindings, template, etc.  See corresponding ComponentPrimitive DataAccessSingleMode for UI.
 */
public class DataAccessSingleMode extends _DataAccessSingleMode
{


    /**
     * Factory method to create new instances of DataAccessSingleMode.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessSingleMode or a subclass.
     */
    public static DataAccessSingleMode newDataAccessSingleMode()
    {
        return (DataAccessSingleMode) SMEOUtils.newInstanceOf("DataAccessSingleMode");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessSingleMode");

    }



    /**
     * Returns the symbolic name of this mode this mode.
     *
     * @return the symbolic name of this mode this mode.
     */
    public String mode()
    {
        return DataAccessMode.SINGLE_MODE;
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

        // Titles and field values
        Enumeration componentEnum = visibleFields().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            template.append("    <tr>\n");
            template.append("      <th class=\"dataAccessTableHeading\">\n");
            template.append("        " + aComponent.fieldName() + "\n");
            template.append("      </th>\n");
            template.append("      <td class=\"dataAccessTableCell\">\n");
            template.append("          <WebObject name=" + aComponent.nameInTemplate() + "></WebObject>&nbsp;\n");
            template.append("      </td>\n");
            template.append("    </tr>\n");
        }

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
        template.append("<WebObject name=DataAccess_IsConfirmingDeletion>\n");
        template.append("  <table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\">\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"center\" valign=\"middle\" colspan=\"3\">\n");
        template.append("                Proceed with deletion?\n");
        template.append("      </td>\n");
        template.append("    </tr>\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"right\" valign=\"middle\">\n");
        template.append("        <input type=\"submit\" value=\"Yes\" name=\"<WebObject name=DataAccess_AcceptDeletion></WebObject>\">\n");
        template.append("      </td>\n");
        template.append("      <td align=\"center\" valign=\"middle\">&nbsp;</td>\n");
        template.append("      <td align=\"left\" valign=\"middle\">\n");
        template.append("        <input type=\"submit\" value=\"No!\" name=\"<WebObject name=DataAccess_RejectDeletion></WebObject>\">\n");
        template.append("      </td>\n");
        template.append("    </tr>\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"center\" valign=\"middle\" colspan=\"3\">\n");
        template.append("        If you do not proceed with the deletion, you will be returned to the previous page with your edits intact, but unsaved.\n");
        template.append("      </td>\n");
        template.append("    </tr>\n");
        template.append("  </table>\n");
        template.append("</WebObject name=DataAccess_IsConfirmingDeletion>\n");
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
        addDataAccessNavigationBindings(wodFile);
        addDataAccessActionBindings(wodFile);
        addFieldBindings(wodFile, visibleFields());

        // Delete action specific to Single Record
        wodFile.append("DataAccess_DeleteRecord: WOHyperlink {\n");
        wodFile.append("    action = deleteRecord;\n");
        wodFile.append("    disabled = cannotDeleteRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_DeleteName: SubmitName {\n");
        wodFile.append("    action = deleteRecord;\n");
        wodFile.append("    inputName = \"DataAccess_DeleteName\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Duplicate action specific to Single Record
        wodFile.append("DataAccess_CanDuplicateRecord: WOConditional {\n");
        wodFile.append("    condition = canDuplicateRecord;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_DuplicateRecord: WOHyperlink {\n");
        wodFile.append("    href = duplicateRecordUrl;\n");
        wodFile.append("    disabled = cannotDuplicateRecord;\n");
        wodFile.append("}\n");

        // The currently selected record number
        wodFile.append("DataAccess_RecordOrRecordRangeBeingViewed: WOString {\n");
        wodFile.append("    value = recordNumberSelected;\n");
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
        template.append("            <WebObject name=DataAccess_CanDeleteRecords>\n");
        template.append("              <td align=\"center\" valign=\"middle\">\n");
        template.append("                <WebObject name=DataAccess_DeleteRecord>Delete</WebObject>\n");
        template.append("              </td>\n");
        template.append("            </WebObject name=DataAccess_CanDeleteRecords>\n");

        template.append("            <WebObject name=DataAccess_CanAddRecords>\n");
        template.append("              <td align=\"center\" valign=\"middle\">\n");
        template.append("                <WebObject name=DataAccess_AddRecord>Add</WebObject>\n");
        template.append("              </td>\n");
        template.append("            </WebObject name=DataAccess_CanAddRecords>\n");

        template.append("            <WebObject name=DataAccess_CanDuplicateRecord>\n");
        template.append("              <td align=\"center\" valign=\"middle\">\n");
        template.append("                <WebObject name=DataAccess_DuplicateRecord>Duplicate</WebObject>\n");
        template.append("              </td>\n");
        template.append("            </WebObject name=DataAccess_CanDuplicateRecord>\n");

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

