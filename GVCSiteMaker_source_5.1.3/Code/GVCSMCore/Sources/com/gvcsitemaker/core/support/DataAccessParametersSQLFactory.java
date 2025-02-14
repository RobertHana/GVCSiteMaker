package com.gvcsitemaker.core.support;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


/**
 * Basically an inner class of DataAccessParameters, used to construct the sql.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DataAccessParametersSQLFactory
{
    // random junk to make multiple joins to the same table unique (see searchSQL)
    protected static final String UNIQUE_TABLE_STRING = "___fijemgkd__819423968";

    protected DataAccessParameters dataAccessParameters;

    protected NSMutableSet joinColumns = new NSMutableSet();


    /**
     * Designated constructor.
     *
     * @param aDataAccessParameters the related DataAccessParameters
     */
    public DataAccessParametersSQLFactory(DataAccessParameters aDataAccessParameters)
    {
        super();

        /** require [valid_aDataAccessParameters] aDataAccessParameters != null; **/
        dataAccessParameters = aDataAccessParameters;
    }



	/**
     * Returns the where clause for the given criteria.  Adds to joinColumns any columns used in the where clause.
     *
     * @param criterionEnumerator an enumerator containing DataAccessSearchCriterion objects
     * @return SQL created from the search criteria
     */
    protected String buildWhereClause(Enumeration criterionEnumerator)
    {
        StringBuffer searchSQL = null;
        while (criterionEnumerator.hasMoreElements())
        {
            DataAccessSearchCriterion searchCriterion = (DataAccessSearchCriterion)criterionEnumerator.nextElement();

            try
            {
                String joinCondition = searchCriterion.joinCondition();

                // Invert the qualifier if necessary
                boolean shouldInvert = false;
                if ((joinCondition != null) && (joinCondition.equals(DataAccessSearchCriterion.OR_NOT_JOIN_CONDITION)))
                {
                	shouldInvert = true;
                }

                String tempSQL = searchCriterion.sql(dataAccessParameters.session().currentUser(), shouldInvert);

                // The qualifier will only be null the first time through... the join condition should also be null the first time
                if (searchSQL == null)
                {
                    /** check [no_join_condition] joinCondition == null; **/
                    searchSQL = new StringBuffer(tempSQL);
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.AND_JOIN_CONDITION))
                {
                    searchSQL = searchSQL.append("\n  intersect\n    " + tempSQL);
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.AND_NOT_JOIN_CONDITION))
                {
                    searchSQL = searchSQL.append("\n  except\n    " + tempSQL);
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.OR_JOIN_CONDITION))
                {
                    searchSQL = searchSQL.append("\n  union\n    " + tempSQL);
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.OR_NOT_JOIN_CONDITION))
                {
                	searchSQL = searchSQL.append("\n  union\n    " + tempSQL);
                }
                else
                {
                    // Should never get here
                    throw new Error("Invalid join condition.");
                }
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }
        return searchSQL.toString();
    }



	/**
     * Returns SQL created from the search criteria.
     *
     * @return SQL created from the search criteria
     */
    String searchSQL()
    {
        StringBuffer searchSQL = null;

        // Use the stored search if we restrict to the stored criteria
        // Otherwise, don't use the stored search if we have criteria on the URL, or if we should find all
        // or if the setting to use the stored search is off.
        if (dataAccessComponent().restrictToDefaultResults() || 
        	(dataAccessComponent().useDefaultSearch() &&
        	 ! dataAccessParameters.shouldFindAll() &&
        	 ! dataAccessParameters.searchParameters().hasCriteria()) )
        {
        	DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
        	searchSQL = new StringBuffer("(");
        	searchSQL.append(buildWhereClause(searchMode.searchCriteriaWithParameters(dataAccessParameters).criterionEnumerator()));
        	searchSQL.append(")");
        }

        // Get the params on the URL.  If we don't restrict to the stored criteria,
        // then the URL takes precendence (and searchSQL should be null)
        if (dataAccessParameters.searchParameters().hasCriteria())
        {
            if (dataAccessComponent().restrictToDefaultResults() && (searchSQL != null))
            {
            	searchSQL.append("\n  intersect (\n");
            	searchSQL.append(buildWhereClause(dataAccessParameters.searchParameters().criterionEnumerator()));
            	searchSQL.append("\n  )");
            }
            else
            {
            	/** [search_sql_must_be_null] check searchSQL == null; **/
            	searchSQL = new StringBuffer("(");
            	searchSQL.append(buildWhereClause(dataAccessParameters.searchParameters().criterionEnumerator()));
            	searchSQL.append(")");
            }
        }


        // Order by clause. Use the sort orderings that are on the URL, if they exist; if they don't, use the default
        Enumeration sortOrderEnumerator = null;
        if (dataAccessParameters.searchParameters().hasSortOrders())
        {
            sortOrderEnumerator = dataAccessParameters.searchParameters().sortOrderEnumerator();
        }
        else
        {
            DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
            sortOrderEnumerator = searchMode.sortOrderEnumerator();
        }

        NSMutableArray selectColumns = new NSMutableArray();
        boolean firstTime = true;
        StringBuffer sortSQL = null;
        while (sortOrderEnumerator.hasMoreElements())
        {
        	DataAccessSearchSortOrder sortOrder = (DataAccessSearchSortOrder)sortOrderEnumerator.nextElement();

        	if (firstTime)
        	{
        		firstTime = false;
        		sortSQL = new StringBuffer("\nORDER BY\n  ");
        	}
        	else
        	{
        		sortSQL.append(",\n  ");
        	}

        	Column column = sortOrder.sortColumn();
        	if (column.normalizedName().equals("RecordID"))
        	{
        		sortSQL.append("  vr.virtual_row_id");
        	}
        	else
        	{
	        	selectColumns.addObject(column);
	        	joinColumns.addObject(column);

	        	sortSQL.append("\"" + column.normalizedName() + "\"");
	        	if (sortOrder.sortDirection().equals(DataAccessSearchSortOrder.ORDER_DIRECTION_DESCENDING))
	        	{
		        	sortSQL.append(" desc");
	        	}
        	}
        }


        // Now that we have the order by and the where clause, we can construct the minimal select list
        EOModel model = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        EOAdaptor adaptor = EOAdaptor.adaptorWithModel(model);
        EOEntity columnEntity = model.entityNamed("VirtualColumn");
        EOSQLExpression columnExpression = new EOSQLExpressionFactory(adaptor).createExpression(columnEntity);

        StringBuffer sql = new StringBuffer("SELECT\n  vr.virtual_row_id,\n  vr.virtual_table_id");
        Enumeration columnEnumerator = selectColumns.objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
        	VirtualColumn column = (VirtualColumn)columnEnumerator.nextElement();
        	String normalizedName = "\"" + column.normalizedName() + "\"";
            Object columnPK = column.primaryKeyValue();
            if (columnPK instanceof NSData)
            {
                EOAttribute columnAttribute = columnEntity.attributeNamed("columnID");
                columnPK = columnExpression.formatValueForAttribute(columnPK, columnAttribute);
            }

        	if (column instanceof VirtualUserColumn)
        	{
	        	sql.append(",\n  (SELECT user_id FROM sm_user u, virtual_field vf WHERE virtual_row_id = vr.virtual_row_id AND u.user_pkey = vf.user_fkey AND vf.virtual_column_id = ");
	        	sql.append(columnPK + ") " + normalizedName);
        	}
        	else if (column instanceof VirtualLookupColumn)
        	{
            	Column lookupColumn = ((VirtualLookupColumn)column).lookupColumn();
            	String lookupColumnTypeName = lookupColumn.type().name();
                Object lookupColumnPK = lookupColumn.primaryKeyValue();
                if (lookupColumnPK instanceof NSData)
                {
                    EOAttribute columnAttribute = columnEntity.attributeNamed("columnID");
                    lookupColumnPK = columnExpression.formatValueForAttribute(lookupColumnPK, columnAttribute);
                }
	        	sql.append(",\n  (SELECT ");
	        	sql.append(lookupColumnTypeName);
	        	sql.append("_value FROM virtual_field WHERE virtual_column_id = ");
	        	sql.append(lookupColumnPK);
	        	sql.append(" AND virtual_field_id IN (SELECT lookup_value_id FROM virtual_field WHERE virtual_row_id = vr.virtual_row_id AND virtual_column_id = ");
	        	sql.append(columnPK);
	        	sql.append(")) ");
	        	sql.append(normalizedName);
        	}
        	else
        	{
            	String columnTypeName = column.type().name();
	        	sql.append(",\n  (SELECT ");
	        	sql.append(columnTypeName);
	        	sql.append("_value FROM virtual_field WHERE virtual_row_id = vr.virtual_row_id AND virtual_column_id = ");
	        	sql.append(columnPK + ") " + normalizedName);
        	}
        }

        sql.append("\nFROM\n  virtual_row vr\nWHERE\n  vr.virtual_table_id = ");

        EOEntity entity = model.entityNamed("VirtualRow");
        EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).createExpression(entity);
        String formattedValue = expression.formatValueForAttribute(
                dataAccessComponent().databaseTable().tableID(),
                entity.attributeNamed("virtualTableID"));
        sql.append(formattedValue);

    	if (searchSQL != null)
    	{
    		sql.append(" AND\n  vr.virtual_row_id IN (\n    ");
    		sql.append(searchSQL);
    		sql.append("\n  )");

        	// only log if we don't log the whole statement below
        	if (DebugOut.debugLevel() < 40)
        	{
        		DebugOut.println(20, "User search SQL: " + searchSQL);
        	}
    	}

        // append the order by clause
        if (sortSQL != null)
        {
        	sql.append(sortSQL);

        	// only log if we don't log the whole statement below
        	if (DebugOut.debugLevel() < 40)
        	{
        		DebugOut.println(20, "User sort SQL: " + sortSQL);
        	}
        }

        DebugOut.println(40, "Full SQL: " + sql);
        return sql.toString();
    }



	/**
     * Returns SQL to count rows, restricted by the stored search (if selected by the user), but
     * not the params on the URL.
     *
     * @return SQL to count rows
     */
    String countSQL()
    {
        StringBuffer searchSQL = null;

        // Use the stored search if we restrict to the stored criteria
        // Otherwise, don't use the stored search if we have criteria on the URL, or if we should find all
        // or if the setting to use the stored search is off.
        if (dataAccessComponent().restrictToDefaultResults())
        {
        	DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
        	searchSQL = new StringBuffer(buildWhereClause(searchMode.searchCriteriaWithParameters(dataAccessParameters).criterionEnumerator()));
        }

        // Now that we have the order by and the where clause, we can construct the minimal select list
        StringBuffer sql = new StringBuffer("SELECT\n  COUNT(*)");
        sql.append("\nFROM\n  virtual_row vr");
        sql.append("\nWHERE\n  vr.virtual_table_id = ");

        EOModel model = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        EOEntity entity = model.entityNamed("VirtualRow");
        EOAdaptor adaptor = EOAdaptor.adaptorWithModel(model);
        EOSQLExpression expression = new EOSQLExpressionFactory(adaptor).createExpression(entity);
        String formattedValue = expression.formatValueForAttribute(
                dataAccessComponent().databaseTable().tableID(),
                entity.attributeNamed("virtualTableID"));
    	sql.append(formattedValue);

        // append the where clause
        if (searchSQL != null)
        {
    		sql.append(" AND\n  vr.virtual_row_id IN (\n    ");
    		sql.append(searchSQL);
    		sql.append("\n  )");

        	// only log if we don't log the whole statement below
        	if (DebugOut.debugLevel() < 40)
        	{
        		DebugOut.println(20, "User search SQL: " + searchSQL);
        	}
        }

        DebugOut.println(40, "Full SQL: " + sql);
        return sql.toString();
    }



    /**
     * Returns the DataAccess PageComponent that this object holds parameters for.
     *
     * @return the DataAccess PageComponent that this object holds parameters for
     */
    public DataAccess dataAccessComponent()
    {
        return dataAccessParameters.dataAccessComponent();
        /** ensure [valid_result] Result != null; **/
    }

    

	/**
     * Returns the where clause for the given criteria.  Adds to joinColumns any columns used in the where clause.
     *
     * @param criterionEnumerator an enumerator containing DataAccessSearchCriterion objects
     * @return SQL created from the search criteria
     */
    protected String oldBuildWhereClause(Enumeration criterionEnumerator)
    {
        StringBuffer searchSQL = null;
        while (criterionEnumerator.hasMoreElements())
        {
            DataAccessSearchCriterion searchCriterion = (DataAccessSearchCriterion) criterionEnumerator.nextElement();
            joinColumns.addObject(searchCriterion.column());

            try
            {
                String tempSQL = searchCriterion.sql(dataAccessParameters.session().currentUser(), false);

                // Invert the qualifier if necessary
                String joinCondition = searchCriterion.joinCondition();
                if ((joinCondition != null) && (joinCondition.equals(DataAccessSearchCriterion.AND_NOT_JOIN_CONDITION) ||
                                                joinCondition.equals(DataAccessSearchCriterion.OR_NOT_JOIN_CONDITION)))
                {
                    tempSQL = "NOT(" + tempSQL + ")";
                }

                // The qualifier will only be null the first time through... the join condition should also be null the first time
                if (searchSQL == null)
                {
                    /** check [no_join_condition] joinCondition == null; **/
                    searchSQL = new StringBuffer("(" + tempSQL);
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.AND_JOIN_CONDITION) ||
                         joinCondition.equals(DataAccessSearchCriterion.AND_NOT_JOIN_CONDITION))
                {
                    searchSQL = searchSQL.append(" and\n  " + tempSQL);
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.OR_JOIN_CONDITION) ||
                         joinCondition.equals(DataAccessSearchCriterion.OR_NOT_JOIN_CONDITION))
                {
                    searchSQL = searchSQL.append(" or\n  " + tempSQL);
                }
                else
                {
                    // Should never get here
                    throw new Error("Invalid join condition.");
                }
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }
        if (searchSQL != null)
        {
        	searchSQL.append(")");
        }
        return searchSQL.toString();
    }



	/**
     * Returns SQL created from the search criteria.
     *
     * @return SQL created from the search criteria
     */
    String oldSearchSQL()
    {
        StringBuffer searchSQL = null;

        // Use the stored search if we restrict to the stored criteria
        // Otherwise, don't use the stored search if we have criteria on the URL, or if we should find all
        // or if the setting to use the stored search is off.
        if (dataAccessComponent().restrictToDefaultResults() || 
        	(dataAccessComponent().useDefaultSearch() &&
        	 ! dataAccessParameters.shouldFindAll() &&
        	 ! dataAccessParameters.searchParameters().hasCriteria()) )
        {
        	DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
        	searchSQL = new StringBuffer(buildWhereClause(searchMode.searchCriteriaWithParameters(dataAccessParameters).criterionEnumerator()));
        }

        // Get the params on the URL.  If we don't restrict to the stored criteria,
        // then the URL takes precendence (and searchSQL should be null)
        if (dataAccessParameters.searchParameters().hasCriteria())
        {
            if (dataAccessComponent().restrictToDefaultResults() && (searchSQL != null))
            {
            	searchSQL.append(" and (");
            	searchSQL.append(buildWhereClause(dataAccessParameters.searchParameters().criterionEnumerator()));
            	searchSQL.append(")");
            }
            else
            {
            	/** [search_sql_must_be_null] check searchSQL == null; **/
            	searchSQL = new StringBuffer("(");
            	searchSQL.append(buildWhereClause(dataAccessParameters.searchParameters().criterionEnumerator()));
            	searchSQL.append(")");
            }
        }


        // Order by clause. Use the sort orderings that are on the URL, if they exist; if they don't, use the default
        Enumeration sortOrderEnumerator = null;
        if (dataAccessParameters.searchParameters().hasSortOrders())
        {
            sortOrderEnumerator = dataAccessParameters.searchParameters().sortOrderEnumerator();
        }
        else
        {
            DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
            sortOrderEnumerator = searchMode.sortOrderEnumerator();
        }

        NSMutableArray selectColumns = new NSMutableArray();
        boolean firstTime = true;
        StringBuffer sortSQL = null;
        while (sortOrderEnumerator.hasMoreElements())
        {
        	DataAccessSearchSortOrder sortOrder = (DataAccessSearchSortOrder)sortOrderEnumerator.nextElement();

        	if (firstTime)
        	{
        		firstTime = false;
        		sortSQL = new StringBuffer("\norder by\n  ");
        	}
        	else
        	{
        		sortSQL.append(",\n  ");
        	}

        	Column column = sortOrder.sortColumn();
        	if (column.normalizedName().equals("RecordID"))
        	{
        		sortSQL.append("  vr.VIRTUAL_ROW_ID");
        	}
        	else
        	{
	        	selectColumns.addObject(column);
	        	joinColumns.addObject(column);

	        	sortSQL.append("\"" + column.normalizedName() + "\"");
	        	if (sortOrder.sortDirection().equals(DataAccessSearchSortOrder.ORDER_DIRECTION_DESCENDING))
	        	{
		        	sortSQL.append(" desc");
	        	}
        	}
        }


        // Now that we have the order by and the where clause, we can construct the minimal select list
        StringBuffer sql = new StringBuffer("select\n  vr.VIRTUAL_ROW_ID,\n  vr.VIRTUAL_TABLE_ID");
        Enumeration columnEnumerator = selectColumns.objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
        	Column column = (Column)columnEnumerator.nextElement();
        	String normalizedName = "\"" + column.normalizedName() + "\"";
        	if (column instanceof VirtualUserColumn)
        	{
	        	sql.append(",\n  ");
	        	sql.append(normalizedName);
	        	sql.append(".user_id");
	        	sql.append(normalizedName);
        	}
        	else
        	{
            	String columnTypeName = column.type().name();
	        	if (column instanceof VirtualLookupColumn)
	        	{
	        		Column lookupColumn = ((VirtualLookupColumn)column).lookupColumn();
	            	columnTypeName = lookupColumn.type().name();
	        	}

	        	sql.append(",\n  ");
	        	sql.append(normalizedName);
	        	sql.append(".");
	        	sql.append(columnTypeName + "_value ");
	        	sql.append(normalizedName);
        	}
        }

        sql.append("\nfrom\n  virtual_row vr");

    	columnEnumerator = joinColumns.objectEnumerator();
    	NSMutableArray usedColumns = new NSMutableArray();
        while (columnEnumerator.hasMoreElements())
        {
        	Column column = (Column)columnEnumerator.nextElement();
        	usedColumns.addObject(column);

        	String normalizedName = "\"" + column.normalizedName() + "\"";
        	if ((column instanceof VirtualLookupColumn) || (column instanceof VirtualUserColumn))
        	{
        		normalizedName = "\"" + column.normalizedName() + UNIQUE_TABLE_STRING + "\"";
        	}

        	sql.append("\n  left outer join virtual_field ");
        	sql.append(normalizedName);
        	sql.append(" on (vr.virtual_row_id = ");
        	sql.append(normalizedName);
        	sql.append(".virtual_row_id and ");
        	sql.append(normalizedName);
        	sql.append(".virtual_column_id = ");
        	sql.append(column.columnID());
        	sql.append(")");

        	// TODO move this whole while block into a static method like searchSQL
        	if (column instanceof VirtualLookupColumn)
        	{
            	String lookupNormalizedName = "\"" + column.normalizedName() + "\"";
            	sql.append("\n  left outer join virtual_field ");
            	sql.append(lookupNormalizedName);
            	sql.append(" on (");
            	sql.append(normalizedName);
            	sql.append(".lookup_value_id = ");
            	sql.append(lookupNormalizedName);
            	sql.append(".virtual_field_id)");
        	}
        	else if (column instanceof VirtualUserColumn)
        	{
            	String userNormalizedName = "\"" + column.normalizedName() + "\"";
            	sql.append("\n  left outer join sm_user ");
            	sql.append(userNormalizedName);
            	sql.append(" on (");
            	sql.append(normalizedName);
            	sql.append(".user_fkey = ");
            	sql.append(userNormalizedName);
            	sql.append(".user_pkey)");
        	}
        }

        sql.append("\nwhere\n  vr.virtual_table_id = ");
    	sql.append(dataAccessComponent().databaseTable().tableID());

        // append the where clause
        if (searchSQL != null)
        {
        	sql.append(" and\n  (");
        	sql.append(searchSQL);
        	sql.append(")");
            DebugOut.println(20, "User search SQL: " + searchSQL);
        }

        // append the where clause
        if (sortSQL != null)
        {
        	sql.append(sortSQL);
            DebugOut.println(20, "User sort SQL: " + sortSQL);
        }

        DebugOut.println(40, "Full SQL: " + sql);
        return sql.toString();
    }



}
