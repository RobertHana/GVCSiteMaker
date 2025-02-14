/**
 * Implementation of DataAccessSearchMode common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * DataAccessSearchMode represents the UI and configuration specific to the Default Search and related
 * configuration of a DataAccess PageComponent.  It is not quite the same as the other modes as the list
 * of fields is created on the fly.  See corresponding ComponentPrimitive DataAccessSearchMode for UI.
 * <br /><br />
 * <b>Note:</b> This mode is a bit odd in that it has three discrete kinds of children: the normal DataAccessColum
 * children which represent the fields in the table, and the special DataAccessColumnSearchValue and
 * DataAccessColumnSearchSortOrder children which are used for the stored search.  These sets of children
 * are accessed via orderedDataAccessColumns(), orderedCriteria(), and orderedSortOrders().
 */
public class DataAccessSearchMode extends _DataAccessSearchMode
{
	private static final long serialVersionUID = 2498103954560575979L;

	public static final String QUERY_BUILDER_FORM = "queryBuilder";
    public static final String SEARCH_ALL_FORM = "searchAll";
    public static final String SIMPLE_SEARCH_FORM = "simpleSearch";
    public static final String SEARCH_FORM_TYPE_BINDING = "searchFormType";



    /**
     * Factory method to create new instances of DataAccessSearchMode.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessSearchMode or a subclass
     */
    public static DataAccessSearchMode newDataAccessSearchMode()
    {
        return (DataAccessSearchMode) SMEOUtils.newInstanceOf("DataAccessSearchMode");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessSearchMode");
        setSearchFormType(QUERY_BUILDER_FORM);
    }



    /**
     * Returns the symbolic name of this mode this mode.
     *
     * @return the symbolic name of this mode this mode
     */
    public String mode()
    {
        return DataAccessMode.SEARCH_MODE;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> as search mode uses component actions.
     *
     * @return <code>true</code> as search mode uses component actions
     */
    public boolean usesComponentActions()
    {
        return true;
    }



    /**
     * Returns a dictionary of the DataAccessColumn children keyed on their Column's normalized name.
     * Excludes the DataAccessColumnSearchValue and DataAccessColumnSearchSortOrder which are used for the stored search.
     *
     * @return a dictionary of the DataAccessColumn children keyed on their Column's normalized name.
     */
    public NSDictionary columnsByName()
    {
        if (isFault() || (columnsByName == null))
        {
            columnsByName = new NSMutableDictionary();

            Enumeration componentEnumerator = toChildren().objectEnumerator();
            while (componentEnumerator.hasMoreElements())
            {
                DataAccessColumn child = (DataAccessColumn)componentEnumerator.nextElement();

                if ( ! ((child instanceof DataAccessColumnSearchValue) ||
                        (child instanceof DataAccessColumnSearchSortOrder)  ))
                {
                    columnsByName.setObjectForKey(child, child.column().normalizedName());
                }
            }
        }

        return columnsByName;

        // Badness: can't enforce [has_all_columns] Result.count() == orderedDataAccessColumns().count();
        /** ensure [valid_result] Result != null;    **/
    }



    /**
     * Returns the normal DataAccessColumn children, in order.  Columns that are not searchable
     * (e.g. DataAccessSiteFileColumn) are excluded. Also excluded are the DataAccessColumnSearchValue
     * and DataAccessColumnSearchSortOrder which are used for the stored search.
     *
     * @return the normal DataAccessColumn children, in order
     */
    public NSArray orderedDataAccessColumns()
    {
        NSMutableArray orderedDataAccessColumns = new NSMutableArray();

        Enumeration componentEnumerator = orderedComponents().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            PageComponent child = (PageComponent)componentEnumerator.nextElement();

            if ( ! ((child instanceof DataAccessColumnSearchValue) ||
                    (child instanceof DataAccessColumnSearchSortOrder) ||
                    (child instanceof DataAccessSiteFileColumn) ))
            {
                orderedDataAccessColumns.addObject(child);
            }
        }

        return orderedDataAccessColumns;

        /** ensure
        [valid_result] Result != null;
        [result_contains_childTypes] forall i : {0 .. Result.count() - 1} #
            ! ((Result.objectAtIndex(i) instanceof DataAccessSiteFileColumn) ||
               (Result.objectAtIndex(i) instanceof DataAccessColumnSearchValue) ||
               (Result.objectAtIndex(i) instanceof DataAccessColumnSearchSortOrder)); **/
    }



    /**
     * Returns an ordered array of search criteria.
     *
     * @return an ordered array of search criteria
     */
    public NSArray orderedCriteria()
    {
        return orderedChildrenOfType(DataAccessColumnSearchValue.class);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an ordered array of search sort orders.
     *
     * @return an ordered array of search sort orders
     */
    public NSArray orderedSortOrders()
    {
        return orderedChildrenOfType(DataAccessColumnSearchSortOrder.class);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an ordered array of child components that are of the given type.  This method could be moved up if others can use it...
     *
     * @param childType the type of children to return
     * @return an ordered array of child components that are of the given type
     */
    protected NSArray orderedChildrenOfType(Class childType)
    {
        /** require [valid_param] childType != null; **/

        NSMutableArray orderedChildrenOfType = new NSMutableArray();

        Enumeration componentEnumerator = orderedComponents().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            PageComponent child = (PageComponent)componentEnumerator.nextElement();

            if (childType.isInstance(child))
            {
                orderedChildrenOfType.addObject(child);
            }
        }

        return orderedChildrenOfType;

        /** ensure
        [valid_result] Result != null;
        [result_contains_childTypes] forall i : {0 .. Result.count() - 1} # childType.isInstance(Result.objectAtIndex(i)); **/
    }



    /**
     * Returns the list of children (excluding sort orders and criteria) which are visible, in their
     * set order.  This should be used for display.
     *
     * @return the list of children (excluding sort orders and criteria) which are visible, in their set order.
     */
    public NSArray visibleFields()
    {
        NSMutableArray visibleFields = new NSMutableArray();
        Enumeration componentEnum = orderedDataAccessColumns().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            if (aComponent.showField())
            {
                visibleFields.addObject(aComponent);
            }
        }

        return visibleFields;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the qualifier that this search mode represents.
     *
     * @param dataAccessContext the <code>DataAccessParameters</code> that contains the context in which we make the search
     * @return the qualifier that this search mode represents. Returns <code>null</code> if there is no stored search qualifier
     */
    public DataAccessSearchCriteria searchCriteriaWithParameters(DataAccessParameters dataAccessContext)
    {
        /** require [valid_param] dataAccessContext != null; **/

        DataAccessSearchCriteria searchCriteria = new DataAccessSearchCriteria(dataAccessContext.dataAccessComponent());
        Enumeration criteriaEnumerator = orderedCriteria().objectEnumerator();
        while (criteriaEnumerator.hasMoreElements())
        {
            DataAccessColumnSearchValue searchValueParameter = (DataAccessColumnSearchValue)criteriaEnumerator.nextElement();
            searchCriteria.addSearchCriterion(searchValueParameter.joinCondition(),
                                              searchValueParameter.column(),
                                              searchValueParameter.comparisonType(),
                                              searchValueParameter.value());
        }
        return searchCriteria;
    }


    /**
     * Returns the qualifier that this search mode represents.
     *
     * @param dataAccessContext the <code>DataAccessParameters</code> that contains the context in which we make the search
     * @return the qualifier that this search mode represents. Returns <code>null</code> if there is no stored search qualifier
     */
    public EOQualifier storedSearchQualifierWithParameters(DataAccessParameters dataAccessContext)
    {
        /** require [valid_param] dataAccessContext != null; **/

        DataAccessSearchCriteria searchCriteria = searchCriteriaWithParameters(dataAccessContext);
        EOQualifier qualifier = searchCriteria.searchQualifier(dataAccessContext.session().currentUser());
        DebugOut.println(20, "Stored search qualifier: " + qualifier);

        return qualifier;
    }


    /**
     * Returns the rows in the table that restricted by the default search.
     *
     * @param dataAccessContext the <code>DataAccessParameters</code> that contains the context in which we make the search
     * @return the rows in the table that restricted by the default search
     */
    public NSArray storedSearchRowsWithParameters(DataAccessParameters dataAccessContext)
    {
        /** require [valid_param] dataAccessContext != null; **/
        return databaseTable().objectsWithQualifier(storedSearchQualifierWithParameters(dataAccessContext));
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the sort orderings that this search mode represents as an array of DataAccessSearchSortOrders.
     *
     * @return the sort orderings that this search mode represents
     */
    public Enumeration sortOrderEnumerator()
    {
        DataAccessSearchCriteria sortCriteria = new DataAccessSearchCriteria(dataAccessComponent());
        Enumeration sortOrderEnumerator = orderedSortOrders().objectEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessColumnSearchSortOrder sortOrder = (DataAccessColumnSearchSortOrder)sortOrderEnumerator.nextElement();
            sortCriteria.addSortOrder(sortOrder.column(), sortOrder.sortDirection());
        }

        return sortCriteria.sortOrders().objectEnumerator();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the sort orderings that this search mode represents as an array of EOSortOrderings.
     *
     * @return the sort orderings that this search mode represents
     */
    public NSArray storedSortOrder()
    {
        DataAccessSearchCriteria sortCriteria = new DataAccessSearchCriteria(dataAccessComponent());
        Enumeration sortOrderEnumerator = orderedSortOrders().objectEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessColumnSearchSortOrder sortOrder = (DataAccessColumnSearchSortOrder)sortOrderEnumerator.nextElement();
            sortCriteria.addSortOrder(sortOrder.column(), sortOrder.sortDirection());
        }

        NSArray sortOrderings = sortCriteria.sortOrdering();
        DebugOut.println(20, "Stored sort ordering: " + sortOrderings);

        return sortOrderings;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a summary of the ordering settings presently congfigured.
     *
     * @return a summary of the ordering settings presently congfigured
     */
    public String sortOrderSummary()
    {
        StringBuffer sortOrderSummary = new StringBuffer();
        boolean firstSortOrder = true;
        sortOrderSummary.append("Records will be sorted by ");

        Enumeration sortOrderEnumerator = orderedSortOrders().objectEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessColumnSearchSortOrder searchSortOrder = (DataAccessColumnSearchSortOrder)sortOrderEnumerator.nextElement();

            if ( ! firstSortOrder)
            {
                sortOrderSummary.append(", then by ");
            }
            else
            {
                firstSortOrder = false;
            }

            sortOrderSummary.append("\"");
            sortOrderSummary.append(searchSortOrder.column().name());
            sortOrderSummary.append("\" (");
            sortOrderSummary.append(searchSortOrder.sortDirection());
            sortOrderSummary.append(")");
        }

        return sortOrderSummary.toString();

        /** ensure [valid_result] Result != null; **/
    }



	/**
	 * Returns <code>true</code> as Search Mode has editable fields by definition.
	 *
	 * @return <code>true</code>  as Search Mode has editable fields by definition
	 */
	public boolean hasEditableFields()
	{
		return true;
	}



    /**
     * Returns the default HTML WOComponent template for this mode and search form type.
     *
     * @return the default HTML WOComponent template for this mode.
     */
    public String defaultTemplate()
    {
        return SMApplication.appProperties().stringPropertyForKey("DA_" + mode() + "ModeDefault" + searchFormType() + "Template");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the default custom HTML WOComponent template for this mode.
     *
     * @return the default custom HTML WOComponent template for this mode
     */
    public String defaultCustomTemplate()
    {
        if (isUsingSearchForm(QUERY_BUILDER_FORM))
        {
            return defaultQueryBuilderCustomTemplate();
        }
        else if (isUsingSearchForm(SEARCH_ALL_FORM))
        {
            return defaultSearchAllCustomTemplate();
        }
        else if (isUsingSearchForm(SIMPLE_SEARCH_FORM))
        {
            return defaultSimpleSearchCustomTemplate();
        }
        else
        {
            throw new RuntimeException("Unkown search form type: " + searchFormType());
        }
    }



    /**
     * @return the default custom template for the query builder search form
     */
    protected String defaultQueryBuilderCustomTemplate()
    {
        StringBuffer template = new StringBuffer(2048);

        template.append("  <input type=\"hidden\" name=\"mode\" value=\"list\">\n");
        template.append("  <input type=\"hidden\" name=\"sortColumn0\" value=\"RecordID\">\n");
        template.append("  <input type=\"hidden\" name=\"sortDirection0\" value=\"Ascending\">\n");
        template.append("  <input type=\"hidden\" name=\"groupSize\" value=\"5\">\n");
        template.append("  <hr>\n");
        template.append("  <table width=\"100%\" border=\"0\" cellpadding=\"2\">\n");
        template.append("    <tr>\n");
        template.append("      <td><span class=\"dataAccessSubtitle\">Find Records in Database Table:</span> <span class=\"dataAccessColoredSubtitle\">\"");
        template.append(databaseTable().name());
        template.append("\"</span></td>\n");
        template.append("      <td align=\"Right\" valign=\"Top\"><WebObject name=DataAccess_SearchButton></WebObject></td>\n");
        template.append("    </tr>\n");
        template.append("  </table>\n");
        template.append("  <table width=\"100%\" border=\"0\" cellpadding=\"4\" align=\"center\">\n");

        // Note: this component is no longer used, due to the added complextity that it would add to custom search templates. In the future, we may put this back into production, with some changes (such as removing the joinCondition and comparisonType params from the component so that the user can customize these).
        /* for (int i = 0; i < ((SMVirtualTable)databaseTable()).searchableColumns().count(); i++)
        {
            Column column = (Column)((SMVirtualTable)databaseTable()).searchableColumns().objectAtIndex(i);
            template.append("      <WebObject name=DataAccess_SearchTerm_" + column.normalizedName() + "></WebObject>\n");
        } */

        // Search fields
        for (int i = 0; i < ((SMVirtualTable)databaseTable()).searchableColumns().count(); i++)
        {
            Column column = (Column)((SMVirtualTable)databaseTable()).searchableColumns().objectAtIndex(i);

            template.append("    <tr>\n");
            template.append("      <th class=\"dataAccessTableHeading\">\n");
            template.append("        " + column.name() + "\n");
            template.append("      </th>\n");
            template.append("      <td class=\"dataAccessTableCell\">\n");
            template.append("        <input type=\"hidden\" name=\"column" + i + "\" value=\"");
            template.append(column.normalizedName());
            template.append("\">\n");

            if (column.isType(ColumnType.StringColumnType) || column.isType(ColumnType.MemoColumnType))
            {
                template.append("        <input type=\"hidden\" name=\"comparisonType" + i + "\" value=\"contains (text only)\">\n");
            }
            else
            {
                template.append("        <input type=\"hidden\" name=\"comparisonType" + i + "\" value=\"is equal to\">\n");
            }

            if (i != 0)
            {
                template.append("        <input type=\"hidden\" name=\"joinCondition" + i + "\" value=\"AND\">\n");
            }
            template.append("        <input type=\"text\" name=\"value" + i + "\" value=\"\">\n");
            template.append("      </td>\n");
            template.append("    </tr>\n");
        }

        template.append("  </table>\n");

        return template.toString();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return the default custom template for the search all search form
     */
    protected String defaultSearchAllCustomTemplate()
    {
        StringBuffer template = new StringBuffer(2048);

        template.append("  <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"left\" valign=\"middle\">\n");
        template.append("        <span class=\"dataAccessSubtitle\">Find Records in Database Table:</span> <span class=\"dataAccessColoredSubtitle\">\"<WebObject name=DataAccess_TableName></WebObject>\"</span>\n");
        template.append("      </td>\n");
        template.append("      <td id=\"dataAccessActions\" style=\"text-align: right;\">\n");
        template.append("        <WebObject name=DataAccess_SearchButton></WebObject>\n");
        template.append("     </td>\n");
        template.append("    </tr>\n");
        template.append("  </table>\n");
        template.append("  \n");
        template.append("  <div style=\"clear: left; padding-top: 20px; padding-bottom: 20px;\">\n");
        template.append("  Find records where any text field contains:<br /><WebObject name=DataAccess_SearchAllTextInput></WebObject>\n");
        template.append("  </div>\n");
        template.append("  \n");
        template.append("  <div style=\"clear: left;\">\n");
        template.append("    <p class=\"dataAccessInstruction\">Show <WebObject name=DataAccess_GroupSizePopup></WebObject> record(s) per page.</p>\n");
        template.append("    \n");
        template.append("    <p class=\"dataAccessInstruction\">Sort found records:<br>\n");
        template.append("      <WebObject name=DataAccess_SortOrderings>\n");
        template.append("          <WebObject name=DataAccess_IsFirstSortColumnConditional>first by</WebObject>\n");
        template.append("          <WebObject name=DataAccess_IsNotFirstSortColumnConditional>then by</WebObject>\n");
        template.append("          <WebObject name=DataAccess_SortByColumnsPopUp></WebObject>&nbsp;&nbsp;in: <WebObject name=DataAccess_SortDirectionPopUp></WebObject> order<br>\n");
        template.append("      </WebObject name=DataAccess_SortOrderings>\n");
        template.append("      \n");
        template.append("      <WebObject NAME=DataAccess_SortOrderSameColumnTwiceInvalidConditional>\n");
        template.append("          <span class=\"warningText\">You cannot choose the same column twice for sorting.</span><br>\n");
        template.append("      </WebObject>\n");
        template.append("      \n");
        template.append("      <WebObject NAME=DataAccess_AddSortOrderingButton></WebObject><br>\n");
        template.append("    </p>\n");
        template.append("  </div>\n");

        return template.toString();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * @return the default custom template for the simple search form
     */
    protected String defaultSimpleSearchCustomTemplate()
    {
        StringBuffer template = new StringBuffer(2048);

        template.append("  <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
        template.append("    <tr>\n");
        template.append("      <td align=\"left\" valign=\"middle\">\n");
        template.append("        <span class=\"dataAccessSubtitle\">Find Records in Database Table:</span> <span class=\"dataAccessColoredSubtitle\">\"<WebObject name=DataAccess_TableName></WebObject>\"</span>\n");
        template.append("      </td>\n");
        template.append("      <td id=\"dataAccessActions\" style=\"text-align: right;\">\n");
        template.append("        <WebObject name=DataAccess_SearchWithDefaultTemplateButton></WebObject>\n");
        template.append("      </td>\n");
        template.append("    </tr>\n");
        template.append("  </table>\n");
        template.append("  \n");
        template.append("  <div style=\"clear: left; padding-top: 20px; padding-bottom: 20px;\">\n");
        template.append("    <WebObject name=DataAccess_SearchFindAll></WebObject>\n");
        template.append("    Find records that fit <b>all</b> of these criteria (AND)<br />\n");
        template.append("    <WebObject name=DataAccess_SearchFindAny></WebObject>\n");
        template.append("    Find records that fit <b>any</b> of these criteria (OR)</p>\n");
        template.append("  </div>\n");
        template.append("  \n");
        template.append("  <table border=\"1\" cellpadding=\"4\" cellspacing=\"0\" align=\"center\" id=\"dataAccessTable\">\n");
        template.append("    <tr>\n");
        template.append("      <th class=\"dataAccessTableHeading\">Field Name</th>\n");
        template.append("      <th class=\"dataAccessTableHeading\">Comparison</th>\n");
        template.append("      <th class=\"dataAccessTableHeading\">\n");
        template.append("        Value<br>\n");
        template.append("        <div class=\"dataAccessInstruction\">\n");
        template.append("          Enter dates as <WebObject name=DataAccess_InputDateHint></WebObject>\n");
        template.append("       </div>\n");
        template.append("      </th>\n");
        template.append("    </tr>\n");
        template.append("  \n");

        // Components for the custom, simple search form
        NSArray visibleFields = visibleFields();
        for (int i = 0; i < visibleFields.count(); i++)
        {
            DataAccessColumn daColumn =(DataAccessColumn) visibleFields.objectAtIndex(i);
            template.append("      <tr>\n");
            template.append("        <td class=\"dataAccessTableCell\">" + daColumn.column().name() + "</td>\n");
            template.append("        <td class=\"dataAccessTableCell\"><WebObject name=" + daColumn.nameInTemplate() + "_ComparisonPopup></WebObject>&nbsp;</td>\n");
            template.append("        <td class=\"dataAccessTableCell\"><WebObject name=" + daColumn.nameInTemplate() + "></WebObject>&nbsp;</td>\n");
            template.append("      </tr>\n");
            template.append("      <WebObject name=" + daColumn.nameInTemplate() + "_CriterionHasError>\n");
            template.append("      <tr>\n");
            template.append("        <td align=\"center\" valign=\"middle\" colspan=\"4\">\n");
            template.append("          <span class=\"dataAccessException\"><WebObject name=" + daColumn.nameInTemplate() + "_CriterionErrorMessage></WebObject></span>\n");
            template.append("        </td>\n");
            template.append("      </tr>\n");
            template.append("      </WebObject name=" + daColumn.nameInTemplate() + "_CriterionHasError>\n");
        }

        template.append("  </table>\n");
        template.append("  \n");
        template.append("  \n");
        template.append("  <div style=\" padding-top: 20px; clear: left;\">\n");
        template.append("    <p class=\"dataAccessInstruction\">Show <WebObject name=DataAccess_GroupSizePopup></WebObject> record(s) per page.</p>\n");
        template.append("  \n");
        template.append("    <p class=\"dataAccessInstruction\">Sort found records:<br>\n");
        template.append("      <WebObject name=DataAccess_SortOrderings>\n");
        template.append("          <WebObject name=DataAccess_IsFirstSortColumnConditional>first by</WebObject>\n");
        template.append("          <WebObject name=DataAccess_IsNotFirstSortColumnConditional>then by</WebObject>\n");
        template.append("          <WebObject name=DataAccess_SortByColumnsPopUp></WebObject>&nbsp;&nbsp;in: <WebObject name=DataAccess_SortDirectionPopUp></WebObject> order<br>\n");
        template.append("      </WebObject name=DataAccess_SortOrderings>\n");
        template.append("  \n");
        template.append("      <WebObject NAME=DataAccess_SortOrderSameColumnTwiceInvalidConditional>\n");
        template.append("          <span class=\"warningText\">You cannot choose the same column twice for sorting.</span><br>\n");
        template.append("      </WebObject>\n");
        template.append("  \n");
        template.append("      <WebObject NAME=DataAccess_AddSortOrderingButton></WebObject><br>\n");
        template.append("    </p>\n");
        template.append("  </div>\n");

        return template.toString();

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the WOD file bindings to use with this mode.
     *
     * @return the WOD file bindings to use with this mode
     */
    public String generatedBindings()
    {
        StringBuffer wodFile = new StringBuffer(2048);

        addCommonBindings(wodFile);

        wodFile.append("DataAccess_InputDateHint: WOString {\n");
        wodFile.append("    value = application.properties.InputDateFormatHint;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_AllRecordsRadioButton: WORadioButton {\n");
        wodFile.append("    selection = showAllRecords;\n");
        wodFile.append("    value = true;\n");
        wodFile.append("    name = \"showAllOrActuallySearch\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_RecordsInCriteriaRadioButton: WORadioButton {\n");
        wodFile.append("    selection = showAllRecords;\n");
        wodFile.append("    value = false;\n");
        wodFile.append("    name = \"showAllOrActuallySearch\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SearchCriteria: WORepetition {\n");
        wodFile.append("    list = criteria.searchCriteria;\n");
        wodFile.append("    item = aCriterion;\n");
        wodFile.append("    index = criteriaIndex;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_JoinConditionsPopup: WOPopUpButton {\n");
        wodFile.append("    list = joinConditions;\n");
        wodFile.append("    item = aJoinCondition;\n");
        wodFile.append("    noSelectionString = \"(unused)\";\n");
        wodFile.append("    selection = aCriterion.joinCondition;\n");
        wodFile.append("    name = joinConditionURLName;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_ShouldShowJoinConditionsPopup: WOConditional {\n");
        wodFile.append("    condition = shouldShowJoinConditions;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionColumnsPopup: WOPopUpButton {\n");
        wodFile.append("    list = searchableAndVisibleColumns;\n");
        wodFile.append("    item = aColumn;\n");
        wodFile.append("    displayString = aColumn.name;\n");
        wodFile.append("    noSelectionString = \"Choose field...\";\n");
        wodFile.append("    selection = aCriterion.column;\n");
        wodFile.append("    name = columnURLName;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionColumnName: WOString {\n");
        wodFile.append("    value = aCriterion.column.name;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionComparisonTypesPopup: WOPopUpButton {\n");
        wodFile.append("    list = comparisonTypes;\n");
        wodFile.append("    item = aComparisonType;\n");
        wodFile.append("    noSelectionString = comparisionNoSelectionString;\n");
        wodFile.append("    selection = aCriterion.comparisonType;\n");
        wodFile.append("    name = comparisonTypeURLName;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionValueField: WOTextField {\n");
        wodFile.append("    value = aCriterion.value;\n");
        wodFile.append("    name = valueURLName;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionIsLookup: WOConditional {\n");
        wodFile.append("    condition = isLookupColumn;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionIsntLookup: WOConditional {\n");
        wodFile.append("    condition = isLookupColumn;\n");
        wodFile.append("    negate = true;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionValuePopup: WOPopUpButton {\n");
        wodFile.append("    list = criterionlookupValues;\n");
        wodFile.append("    noSelectionString = \"(none)\";\n");
        wodFile.append("    selection = aCriterion.value;\n");
        wodFile.append("    name = valueURLName;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SearchButton: WOSubmitButton {\n");
        wodFile.append("    action = search;\n");
        wodFile.append("    value = \"Search\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SearchWithDefaultTemplateButton: WOSubmitButton {\n");
        wodFile.append("    action = searchWithDefaultTemplate;\n");
        wodFile.append("    value = \"Search\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_AddCriterionButton: WOSubmitButton {\n");
        wodFile.append("    action = addCriterion;\n");
        wodFile.append("    value = \"Use more criteria...\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_GroupSizePopup: WOPopUpButton {\n");
        wodFile.append("    list = groupSizes;\n");
        wodFile.append("    item = aGroupSize;\n");
        wodFile.append("    selection = groupSize;\n");
        wodFile.append("    name = \"" + DataAccessParameters.GROUP_SIZE_KEY + "\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionHasError: WOConditional {\n");
        wodFile.append("    condition = criterionHasError;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CriterionErrorMessage: WOString {\n");
        wodFile.append("    value = criterionErrorMessage;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SortOrderings: WORepetition {\n");
        wodFile.append("    list = criteria.sortOrders;\n");
        wodFile.append("    item = aSortOrdering;\n");
        wodFile.append("    index = sortOrderingIndex;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_IsFirstSortColumnConditional: WOConditional {\n");
        wodFile.append("    condition = aSortOrdering.isFirstSortOrder;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_IsNotFirstSortColumnConditional: WOConditional {\n");
        wodFile.append("    condition = aSortOrdering.isFirstSortOrder;\n");
        wodFile.append("    negate = true;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SortByColumnsPopUp: WOPopUpButton {\n");
        wodFile.append("    selection = aSortOrdering.sortColumn;\n");
        wodFile.append("    list = orderableAndVisibleColumns;\n");
        wodFile.append("    item = aColumn;\n");
        wodFile.append("    displayString = aColumn.name;\n");
        wodFile.append("    noSelectionString = sortOrderingColumnNoSelectionString;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SortDirectionPopUp: WOPopUpButton {\n");
        wodFile.append("    list = orderDirections;\n");
        wodFile.append("    selection = aSortOrdering.sortDirection;\n");
        wodFile.append("    noSelectionString = sortOrderingDirectionNoSelectionString;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SortOrderSameColumnTwiceInvalidConditional: WOConditional {\n");
        wodFile.append("    condition = hasDuplicateSortColumns;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_AddSortOrderingButton: WOSubmitButton {\n");
        wodFile.append("    action = addSortOrder;\n");
        wodFile.append("    value = \"Additional Sort\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Note: this component is no longer used, due to the added complextity that it would add to custom search templates. In the future, we may put this back into production, with some changes (such as removing the joinCondition and comparisonType params from the component so that the user can customize these).
        /*
        for (int i = 0; i < ((SMVirtualTable)databaseTable()).searchableColumns().count(); i++)
        {
            Column column = (Column)((SMVirtualTable)databaseTable()).searchableColumns().objectAtIndex(i);

            wodFile.append("DataAccess_SearchTerm_" + column.normalizedName() + ": CustomSearchComponent {\n");
            wodFile.append("    componentObject = componentObject;\n");
            wodFile.append("    table = databaseTable;\n");
            wodFile.append("    columnName = \"" + column.normalizedName() + "\";\n");
            wodFile.append("    index = " + i + ";\n");
            wodFile.append("}\n");
            wodFile.append("\n");
        }
        */

        // Components for the custom, simple search form
        NSArray visibleFields = visibleFields();
        for (int i = 0; i < visibleFields.count(); i++)
        {
            DataAccessColumn daColumn = (DataAccessColumn)visibleFields.objectAtIndex(i);

            if (daColumn instanceof DataAccessLookupColumn)
            {
                wodFile.append(daColumn.nameInTemplate() + ": WOPopUpButton {\n");
                wodFile.append("    list = lookupValues." + daColumn.column().normalizedName() + ";\n");
                wodFile.append("    noSelectionString = \"(none)\";\n");
                wodFile.append("    selection = criteriaByField." + daColumn.column().normalizedName() + ".value;\n");
                wodFile.append("    name = \"value" + i + "\";\n");
                wodFile.append("}\n");
                wodFile.append("\n");
            }
            else
            {
                wodFile.append(daColumn.nameInTemplate());
                wodFile.append(": WOTextField {\n");
                wodFile.append("    name = \"value" + i + "\";\n");
                wodFile.append("    value = criteriaByField." + daColumn.column().normalizedName() + ".value;\n");
                wodFile.append("    size = \"60\";\n");
                wodFile.append("}\n");
                wodFile.append("\n");
            }

            wodFile.append(daColumn.nameInTemplate() + "_ComparisonPopup: WOPopUpButton {\n");
            wodFile.append("    list = comparisonTypes;\n");
            wodFile.append("    item = aComparisonType;\n");
            wodFile.append("    selection = criteriaByField." + daColumn.column().normalizedName() + ".comparisonType;\n");
            wodFile.append("    name = \"comparisonType" + i + "\";\n");
            wodFile.append("}\n");
            wodFile.append("\n");

            wodFile.append(daColumn.nameInTemplate() + "_CriterionHasError: WOConditional {\n");
            wodFile.append("    condition = validationFailureByIndex." + i + ";\n");
            wodFile.append("    negate = false;\n");
            wodFile.append("}\n");
            wodFile.append("\n");

            wodFile.append(daColumn.nameInTemplate() + "_CriterionErrorMessage: WOString {\n");
            wodFile.append("    value = validationFailureByIndex." + i + ";\n");
            wodFile.append("}\n");
            wodFile.append("\n");
        }

        wodFile.append("DataAccess_SearchAllTextInput: WOTextField {\n");
        wodFile.append("    name = \"searchAllText\";\n");
        wodFile.append("    value = criteria.searchAllText;\n");
        wodFile.append("    size = \"60\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // And/Or for joinAll
        wodFile.append("DataAccess_SearchFindAll: WORadioButton {\n");
        wodFile.append("    selection = joinAllCondition;");
        wodFile.append("    value = \"AND\";");
        wodFile.append("    name = \"DataAccess_SearchAllAny\";");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_SearchFindAny: WORadioButton {\n");
        wodFile.append("    selection = joinAllCondition;");
        wodFile.append("    value = \"OR\";");
        wodFile.append("    name = \"DataAccess_SearchAllAny\";");
        wodFile.append("}\n");
        wodFile.append("\n");

        return wodFile.toString();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Adds a sort order to the list of sort orders.
     *
     * @param withDefaults should this initialize the sort order with some defaults?
     */
    public void addSortOrder(boolean withDefaults)
    {
        /** require [has_table] databaseTable() != null; **/

        DataAccessColumnSearchSortOrder searchSortOrder = DataAccessColumnSearchSortOrder.newDataAccessColumnSearchSortOrder();
        addChild(searchSortOrder);
        if (withDefaults)
        {
            searchSortOrder.setColumn(databaseTable().columnNamed(DataAccessRecordIDColumn.ColumnTypeName));
            searchSortOrder.setSortDirection(DataAccessColumnSearchSortOrder.ORDER_DIRECTION_ASCENDING);
        }
    }



    /**
     * Synchronizes the DataAccessColumns in this mode (toChildren()) with the Columns in databaseTable(): removes any DataAccessColumnSearchValues which do not have a corresponding Column in databaseTable().
     */
    public void synchronizeColumnsWithTable()
    {
        /** require [has_table] databaseTable() != null; **/

        // Don't synch based on cached info!
        clearCachedValues();

        // Don't call super as we can't meet the post condition as three sets of columns are in toChildren()
        // Eeewww!  What a mess that is!
        addColumns(databaseTable().userColumns());
        addColumns(databaseTable().systemColumns());
        removeColumnsNotIn(databaseTable().columns());

        // If the user has deleted all the columns we are sorting by, then create a default one
        if (orderedSortOrders().count() == 0)
        {
            DebugOut.println(30, "Last order by column deleted. Reseting to default.");
            addSortOrder(true);
        }

        /** ensure
        [columns_in_search_exist_in_table] (forall i: {0 .. orderedCriteria().count() - 1} # databaseTable().columns().containsObject(((DataAccessColumn)orderedCriteria().objectAtIndex(i)).column()));
        [columns_in_sort_exist_in_table] (forall i: {0 .. orderedSortOrders().count() - 1} # databaseTable().columns().containsObject(((DataAccessColumn)orderedSortOrders().objectAtIndex(i)).column()));
        [columns_in_normal_children_exist_in_table] (forall i: {0 .. orderedDataAccessColumns().count() - 1} # databaseTable().columns().containsObject(((DataAccessColumn)orderedDataAccessColumns().objectAtIndex(i)).column())); **/
    }



    /**
     * Returns <code>true</code> if any column appears more than once in the sorting columns, <code>false</code> otherwise.
     *
     * @return <code>true</code> if any column appears more than once in the sorting columns, <code>false</code> otherwise
     */
    public boolean hasDuplicateSortColumns()
    {
        NSArray sortOrderColumns = (NSArray)orderedSortOrders().valueForKey("column");
        NSSet sortOrderColumnSet = new NSSet(sortOrderColumns);
        return sortOrderColumnSet.count() != sortOrderColumns.count();
    }



    /**
     * @param formType the type of the search from (QUERY_BUILDER_FORM, SEARCH_ALL_FORM, or SIMPLE_SEARCH_FORM)
     * @return <code>true</code> if formType is the one currently configured
     */
    public boolean isUsingSearchForm(String formType)
    {
        /** require [valid_param] formType != null;
        [valid_type] formType.equals(QUERY_BUILDER_FORM) ||
                     formType.equals(SEARCH_ALL_FORM) ||
                     formType.equals(SIMPLE_SEARCH_FORM);   **/
        return searchFormType().equals(formType);
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
        catch (NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (hasDuplicateSortColumns())
        {
            exceptions.addObject(new NSValidation.ValidationException("Duplicate column in sort order array"));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }


    /**
     * Returns the type of search form in use: QUERY_BUILDER_FORM, SEARCH_ALL_FORM, or SIMPLE_SEARCH_FORM.
     *
     * @return the type of search form in use
     */
    public String searchFormType()
    {
        return (String)valueForBindingKey(SEARCH_FORM_TYPE_BINDING);
        /** ensure [valid_result] Result != null;
                   [valid_type] Result.equals(QUERY_BUILDER_FORM) ||
                                Result.equals(SEARCH_ALL_FORM) ||
                                Result.equals(SIMPLE_SEARCH_FORM);   **/
    }



    /**
     * Sets the type of search form in use: QUERY_BUILDER_FORM, SEARCH_ALL_FORM, or SIMPLE_SEARCH_FORM.
     *
     * @param newType the type of search form
     */
    public void setSearchFormType(String newType)
    {
        /** require [valid_param] newType != null;
                   [valid_type] newType.equals(QUERY_BUILDER_FORM) ||
                                newType.equals(SEARCH_ALL_FORM) ||
                                newType.equals(SIMPLE_SEARCH_FORM);   **/

        // If there is a custom template and the form type gets changed then the old template must be replaced
        boolean mustReplaceCustomTemplate = (customTemplate() != null) && ! newType.equals(searchFormType());
        boolean wasUsingCustomTemplate = useCustomTemplate();

        setBindingForKey(newType, SEARCH_FORM_TYPE_BINDING);

        if (mustReplaceCustomTemplate)
        {
            deleteCustomTemplate();
            createCustomTemplate(defaultCustomTemplate());
            // createCustomTemplate sets this so we need to restore it
            setUseCustomTemplate(wasUsingCustomTemplate);
        }

        /** ensure [type_set] searchFormType().equals(newType);                     **/
    }


}
