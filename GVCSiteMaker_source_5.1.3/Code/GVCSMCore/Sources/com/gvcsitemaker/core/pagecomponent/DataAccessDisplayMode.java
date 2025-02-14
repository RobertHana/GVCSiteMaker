/**
 * Implementation of DataAccessDisplayMode common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */
package com.gvcsitemaker.core.pagecomponent;



/**
 * DataAccessDisplayMode adds support methods for the Single Record and List of Records data display modes.
 */
public class DataAccessDisplayMode extends _DataAccessDisplayMode
{

    /**
     * Adds the start of form and table for the Single Record and List of Records template.
     *
     * @param template - StringBuffer containing template to add the start of form and table to
     */
    protected void addFormOpen(StringBuffer template)
    {
        /** require [valid_template] template != null;     **/

        template.append("  <WebObject name=DataAccess_IsViewingData>\n");
        addDataAccessNavigation(template);
        template.append("  <WebObject name=DataAccess_TableHasData>\n");
        template.append("  <hr>\n");
        template.append("  <table border=\"1\" cellpadding=\"4\" cellspacing=\"0\" align=\"center\" id=\"dataAccessTable\">\n");
    }



    /**
     * Adds the end of form and table for the Single Record and List of Records template.
     *
     * @param template - StringBuffer containing template to add the end of form and table to
     */
    protected void addFormClose(StringBuffer template)
    {
        /** require [valid_template] template != null;     **/

        template.append("  </table>\n");
        template.append("  <hr>\n");
        template.append("  </WebObject name=DataAccess_TableHasData>\n");
        template.append("  <WebObject name=DataAccess_TableIsEmpty>\n");
        template.append("    <br><br><div align=\"center\">There are no data to display.</div><br><br>\n");
        template.append("  </WebObject name=DataAccess_TableIsEmpty>\n");

        addDataAccessActions(template);
        addConfirmationActions(template);
    }



    /**
     * Adds the bindings common to all modes, and the Single Record and List of Records templates.
     *
     * @param wodFile - StringBuffer containing bindings to add to.
     */
    protected void addCommonBindings(StringBuffer wodFile)
    {
        /** require [valid_wodFile] wodFile != null;     **/

        super.addCommonBindings(wodFile);

        // Collection specific to Single Record and List of Records modes
        wodFile.append("DataAccess_VisibleFields: WORepetition {\n");
        wodFile.append("    item = aDataAccessColumn;\n");
        wodFile.append("    list = visibleFields;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Confirmation of deletion specific to Single Record and List of Records modes
        wodFile.append("DataAccess_AcceptDeletion: SubmitName {\n");
        wodFile.append("    inputName = \"DataAccess_AcceptDeletion\";\n");
        wodFile.append("    action = acceptDeletion;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_RejectDeletion: SubmitName {\n");
        wodFile.append("    inputName = \"DataAccess_RejectDeletion\";\n");
        wodFile.append("    action = rejectDeletion;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
    }



    /**
     * Adds the navigation bar part to template.
     *
     * @param template - StringBuffer containing template to add navigation to
     */
    protected void addDataAccessNavigation(StringBuffer template)
    {
        /** require  [valid_template] template != null;  [handled_mode] mode().equals(LIST_MODE) || mode().equals(SINGLE_MODE);   **/

        template.append("  <WebObject name=DataAccess_CanBrowseOrSearchRecords>\n");
        template.append("  <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"left\" valign=\"middle\" colspan=\"2\">\n");
        template.append("        <span class=\"dataAccessSubtitle\">Navigate Database Table:</span> <span class=\"dataAccessColoredSubtitle\">\"<WebObject name=DataAccess_TableName></WebObject>\"</span>\n");
        template.append("      </td>\n");
        template.append("    </tr>\n");
        template.append("    <WebObject name=DataAccess_TableHasData>\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"left\" valign=\"middle\" style=\"font-weight: bold;\" colspan=\"2\">\n");
        template.append("        Current record is <span class=\"dataAccessAlternateText\">#");
        template.append("<WebObject name=DataAccess_RecordOrRecordRangeBeingViewed></WebObject>");
        template.append("</span> out of <span class=\"dataAccessAlternateText\">");
        template.append("<WebObject name=DataAccess_NumberOfRecordsInFoundSet></WebObject>");
        template.append("</span> in found set (<span class=\"dataAccessAlternateText\">");
        template.append("<WebObject name=DataAccess_NumberOfRecordsInTable></WebObject>");
        template.append("</span> total records in table)\n");
        template.append("      </td>\n");
        template.append("    </tr>\n");
        template.append("    </WebObject name=DataAccess_TableHasData>\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"left\" valign=\"middle\">\n");
        template.append("        <table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" class=\"dataAccessNavigation\">\n");
        template.append("          <tr>\n");
        template.append("            <td align=\"center\">\n");
        template.append("              <WebObject name=DataAccess_ToggleView>" +
                        (isMode(LIST_MODE) ? "Single" : "List") + " View</WebObject>\n");
        template.append("            </td>\n");
        template.append("            <td>&nbsp;&nbsp;</td>\n");
        template.append("            <td align=\"center\">\n");
        template.append("              <WebObject name=DataAccess_SearchForRecords>Search</WebObject name=DataAccess_SearchForRecords>\n");
        template.append("            </td>\n");
        template.append("            <td>&nbsp;&nbsp;</td>\n");
        template.append("            <td align=\"center\">\n");
        template.append("              <WebObject name=DataAccess_ShowAllRecords>Find All</WebObject>\n");
        template.append("            </td>\n");
        template.append("          </tr>\n");
        template.append("        </table>\n");
        template.append("     </td>\n");
        template.append("      <td align=\"right\" valign=\"middle\">\n");
        template.append("        <table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" class=\"dataAccessNavigation\">\n");
        template.append("          <tr>\n");
        template.append("            <td align=\"center\">\n");
        template.append("              <WebObject name=DataAccess_GoFirst>&lt;&lt;First</WebObject>\n");
        template.append("           </td>\n");
        template.append("           <td align=\"center\">\n");
        template.append("             <WebObject name=DataAccess_GoPrevious>&lt;Prev</WebObject>\n");
        template.append("           </td>\n");
        template.append("           <td align=\"center\">\n");
        template.append("             <WebObject name=DataAccess_GoNext>Next&gt;</WebObject>\n");
        template.append("           </td>\n");
        template.append("           <td align=\"center\">\n");
        template.append("             <WebObject name=DataAccess_GoLast>Last&gt;&gt;</WebObject>\n");
        template.append("           </td>\n");
        template.append("         <tr>\n");
        template.append("       </table>\n");
        template.append("     </td>\n");
        template.append("   </tr>\n");
        template.append("  </table>\n");
        template.append("  </WebObject name=DataAccess_CanBrowseOrSearchRecords>\n");
    }



    /**
     * Adds the database table actions bar part to template.
     *
     * @param template - StringBuffer containing template to add navigation to
     */
    protected void addDataAccessActions(StringBuffer template)
    {
        /** require [valid_template] template != null;  **/

        template.append("    <WebObject name=DataAccess_CanModifyRecords>\n");
        template.append("    <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
        template.append("      <tr>\n");
        template.append("        <td align=\"left\" valign=\"middle\">\n");
        template.append("          <span class=\"dataAccessSubtitle\">Modify Database Table:</span> ");
        template.append("          <span class=\"dataAccessColoredSubtitle\">\"<WebObject name=DataAccess_TableName></WebObject>\"</span>\n");
        template.append("            <WebObject name=DataAccess_ShowNotificationNotNeededCheckBox>\n");
        template.append("              <span style=\"text-align: right;\"><WebObject name=DataAccess_ShouldNotSendNotification></WebObject> No notification needed</span>\n");
        template.append("            </WebObject name=DataAccess_ShowNotificationNotNeededCheckBox>\n");
        template.append("        </td>\n");
        template.append("      </tr>\n");
        template.append("      <tr>\n");
        template.append("        <td>\n");
        template.append("          <table border=\"0\" cellpadding=\"4\"  cellspacing=\"0\" id=\"dataAccessActions\">\n");
        template.append("            <tr>\n");

        addModeSpecificDataAccessActions(template);

        template.append("              <WebObject name=DataAccess_CanEditRecords>\n");
        template.append("                <td align=\"center\" valign=\"middle\">\n");
        template.append("                  <input type=\"submit\" value=\"Save\" name=\"<WebObject name=DataAccess_SaveName></WebObject>\">\n");
        template.append("                </td>\n");
        template.append("              </WebObject name=DataAccess_CanEditRecords>\n");
        template.append("            </tr>\n");
        template.append("          </table>\n");
        template.append("        </td>\n");
        template.append("      </tr>\n");
        template.append("    </table>\n");
        template.append("    </WebObject name=DataAccess_CanModifyRecords>\n");
        template.append("  </WebObject name=DataAccess_IsViewingData>\n");
    }



    /**
     * Adds the mode specific actions to template.
     *
     * @param template - StringBuffer containing template to add mode specific actions to
     */
    protected void addModeSpecificDataAccessActions(StringBuffer template)
    {
    }



    /**
     * Adds the confirmation dialog for deleting records for the Single Record and List of Records template.
     *
     * @param template - StringBuffer containing template to add the confirmation dialog to.
     */
    protected void addConfirmationActions(StringBuffer template)
    {
        throw new RuntimeException("Sub-class responsibility");
    }



    /**
     * Adds the bindings for the tags in addDataAccessNavigation to the end of passed StringBuffer.
     *
     * @param wodFile - the StringBuffer to add the bindings to
     */
    protected void addDataAccessNavigationBindings(StringBuffer wodFile)
    {
        /** require [valid_param] wodFile != null;  **/

        wodFile.append("DataAccess_IsFirst: WOConditional {\n");
        wodFile.append("    condition = isFirst;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_IsNotFirst: WOConditional {\n");
        wodFile.append("    condition = isFirst;\n");
        wodFile.append("    negate = true;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_IsLast: WOConditional {\n");
        wodFile.append("    condition = isLast;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_IsNotLast: WOConditional {\n");
        wodFile.append("    condition = isLast;\n");
        wodFile.append("    negate = true;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_GoFirst: WOHyperlink {\n");
        wodFile.append("    href = goFirstUrl;\n");
        wodFile.append("    disabled = isFirst;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_GoPrevious: WOHyperlink {\n");
        wodFile.append("    href = goPreviousUrl;\n");
        wodFile.append("    disabled = isFirst;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_GoNext: WOHyperlink {\n");
        wodFile.append("    href = goNextUrl;\n");
        wodFile.append("    disabled = isLast;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_GoLast: WOHyperlink {\n");
        wodFile.append("    href = goLastUrl;\n");
        wodFile.append("    disabled = isLast;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
    }



    /**
     * Adds the bindings for the tags in addDataAccessActions to the end of passed StringBuffer.
     *
     * @param wodFile - the StringBuffer to add the bindings to
     */
    protected void addDataAccessActionBindings(StringBuffer wodFile)
    {
        /** require [valid_param] wodFile != null;  **/

        wodFile.append("DataAccess_CanDeleteRecords: WOConditional {\n");
        wodFile.append("    condition = canDeleteRecords;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CanAddRecords: WOConditional {\n");
        wodFile.append("    condition = canAddRecords;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_AddRecord: WOHyperlink {\n");
        wodFile.append("    href = addRecordUrl;\n");
        wodFile.append("    disabled = cannotAddRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_IsViewingData: WOConditional {\n");
        wodFile.append("    condition = isConfirmingDeletion;\n");
        wodFile.append("    negate = true;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_IsConfirmingDeletion: WOConditional {\n");
        wodFile.append("    condition = isConfirmingDeletion;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CanModifyRecords: WOConditional {\n");
        wodFile.append("    condition = canModifyRecords;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

    }


}

