package com.gvcsitemaker.core.support;

import java.io.*;
import java.net.*;
import java.util.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.foundation.*;


/**
 * Manages users' searches.  Acts as a bridge between the data structure used to manage creating the search and the output of that search on the URL via the <code>DataAccessParameters</code>.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DataAccessSearchParameters extends DataAccessSearchCriteria
{
    /** Keys that we may find in the URL. */
    public static final String JOIN_CONDITION_CRITERION_KEY = "joinCondition";
    public static final String COLUMN_CRITERION_KEY = "column";
    public static final String COMPARISON_TYPE_CRITERION_KEY = "comparisonType";
    public static final String VALUE_CRITERION_KEY = "value";
    public static final String SEARCH_ALL_KEY = "searchAll";
    public static final String JOIN_ALL_KEY = "joinAll";
    
    public static final String COLUMN_SORT_ORDER_KEY = "sortColumn";
    public static final String DIRECTION_SORT_ORDER_KEY = "sortDirection";

    /** The set of search prefixes that are copied into the URL dictionary.  We use prefixes because search values will have keys that look like: "column1", "value1", "column2", "value2", etc. */
    protected static final NSSet SEARCH_PREFIXES_IN_URL = new NSSet(new String[] {JOIN_CONDITION_CRITERION_KEY, COLUMN_CRITERION_KEY, COMPARISON_TYPE_CRITERION_KEY, VALUE_CRITERION_KEY});
    protected static final NSSet ORDER_PREFIXES_IN_URL = new NSSet(new String[] {COLUMN_SORT_ORDER_KEY, DIRECTION_SORT_ORDER_KEY});

    protected NSMutableDictionary cachedURLDictionary = new NSMutableDictionary();
    
    protected DataAccessParameters dataAccessParameters;
    protected String groupSize;
    protected String searchAllText;
    public String joinAllCondition;
    

    /**
     * Designated constructor.
     */
    public DataAccessSearchParameters(DataAccessParameters aDataAccessParameters)
    {
        super(aDataAccessParameters.dataAccessComponent());

        /** require [valid_param] aDataAccessParameters != null; **/

        dataAccessParameters = aDataAccessParameters;

        // Get the defaults out of the search component. These will not be written to the URL if they are the same as the defaults (IOW, if the user did not change them)
        setGroupSize(dataAccessParameters().dataAccessComponent().groupSize());

        setupFromFormValues();
    }



    /**
     * Returns <code>true</code> if <code>key</code> is prefixed with an entry in <code>prefixes</code>, <code>false</code> otherwise.
     *
     * @param key the key to check
     * @param prefixes the prefixes that are valid for this key
     * @return <code>true</code> if <code>key</code> is prefixed with an entry in <code>prefixes</code>, <code>false</code> otherwise
     */
    protected boolean isPrefixedWith(String key, NSSet prefixes)
    {
        /** require [valid_key_param] key != null; [valid_prefixes_param] prefixes != null; **/

        Enumeration keyPrefixEnumerator = prefixes.objectEnumerator();
        while (keyPrefixEnumerator.hasMoreElements())
        {
            String keyPrefix = (String)keyPrefixEnumerator.nextElement();
            if (key.startsWith(keyPrefix))
            {
                return true;
            }
        }
        return false;
    }

    
    
    /**
     * Returns <code>true</code> if there is an entry in SEARCH_PREFIXES_IN_URL that begins with <code>key</code>, <code>false</code> otherwise.
     *
     * @param key the key to check
     * @return <code>true</code> if there is an entry in SEARCH_PREFIXES_IN_URL that begins with <code>key</code>, <code>false</code> otherwise
     */
    protected boolean hasSearchTermKeyPrefix(String key)
    {
        /** require [valid_param] key != null; **/
        return isPrefixedWith(key, SEARCH_PREFIXES_IN_URL);
    }

    
    
    /**
     * Returns <code>true</code> if there is an entry in SEARCH_PREFIXES_IN_URL that begins with <code>key</code>, <code>false</code> otherwise.
     *
     * @param key the key to check
     * @return <code>true</code> if there is an entry in SEARCH_PREFIXES_IN_URL that begins with <code>key</code>, <code>false</code> otherwise
     */
    protected boolean hasSortOrderKeyPrefix(String key)
    {
        /** require [valid_param] key != null; **/
        return isPrefixedWith(key, ORDER_PREFIXES_IN_URL);
    }



    /**
     * Given a string of the form &lt;key&gt;&lt;number&gt;, returns the &lt;key&gt; part.
     *
     * @param string the string with the key to extract
     * @return the &lt;key&gt; part of a string of the form &lt;key&gt;&lt;number&gt;
     */
    protected String keyPrefix(String string)
    {
        /** require
        [valid_param] (string != null) && (string.length() > 0);
        [string_ends_in_digit] Character.isDigit(string.charAt(string.length()-1));
        [string_is_not_all_digits] ! StringAdditions.isDigits(string); **/

        for (int i = string.length() - 1; i >= 0; i--)
        {
            if ( ! Character.isDigit(string.charAt(i)))
            {
                return string.substring(0, i);
            }
        }
        throw new Error("This is just here to satisfy the compiler, it should never happen.");

        /** ensure [valid_result] Result != null; [result_is_substring] string.startsWith(Result); **/
    }


    /**
     * Given a string of the form &lt;key&gt;&lt;number&gt;, returns the &lt;number&gt; part.
     *
     * @param string the string with the number to extract
     * @return the &lt;number&gt; part of a string of the form &lt;key&gt;&lt;number&gt;
     */
    protected Integer numberPostfix(String string)
    {
        /** require
        [valid_param] (string != null) && (string.length() > 0);
        [string_ends_in_digit] Character.isDigit(string.charAt(string.length()-1));
        [string_is_not_all_digits] ! StringAdditions.isDigits(string); **/

        for (int i = string.length() - 1; i >= 0; i--)
        {
            if ( ! Character.isDigit(string.charAt(i)))
            {
                return new Integer(string.substring(i + 1));
            }
        }
        throw new Error("This is just here to satisfy the compiler, it should never happen.");

        /** ensure [valid_result] Result != null; [result_is_substring] string.endsWith(Result.toString()); **/
    }



    /**
     * Handles parsing of search criterion key-value pairs from the URL.  Point of single choice for URL search terms.
     */
    protected void handleSearchCriterionFormValue(String urlKey, String value)
    {
        /** require
        [param_urlKey_valid] urlKey != null;
        [param_value_value] value != null;
        [urlKey_is_search_term] hasSearchTermKeyPrefix(urlKey); **/

        DebugOut.println(30, "Got a valid search term prefix: " + urlKey + " with value: " + value);
        Integer index = numberPostfix(urlKey);

        // Create new criterion if needed... TODO: this DEFINITELY needs to be looked at
        while( ! hasCriterionAtIndex(index.intValue()))
        {
            newSearchCriterion();
        }

        DataAccessSearchCriterion searchTerm = criterionAtIndex(index.intValue());

        // Validate each part of the criterion
        if ((urlKey.startsWith(COLUMN_CRITERION_KEY)) && (dataAccessParameters().databaseTable().hasColumnNamed(value)))
        {
            searchTerm.setColumn(dataAccessParameters().databaseTable().columnNamed(value));
        }

        else if (urlKey.startsWith(VALUE_CRITERION_KEY))
        {
            // URLs in SM cannot have "/", "+", "%" or "&" in them due to some URL rewriting that happens at the web server level. So we double encoded those values; here we have to double-decode (note that the URL-encoded value of "/" is "%2F", "+" is "%2B", "%" is "%25" and "&" is "%26")
            if ((value.indexOf("%2F") != -1) || (value.indexOf("%2f") != -1) || (value.indexOf("%26") != -1) || (value.indexOf("%2B") != -1) || (value.indexOf("%2b") != -1) || (value.indexOf("%25") != -1))
            {
                try
                {
                    value = URLDecoder.decode(value, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new ExceptionConverter(e);
                }
            }
            searchTerm.setValue(value);
        }

        else if ((urlKey.startsWith(COMPARISON_TYPE_CRITERION_KEY)) &&
                 (DataAccessSearchCriterion.COMPARISON_TYPES.containsObject(value)))
        {
            searchTerm.setComparisonType(value);
        }

        else if ((urlKey.startsWith(JOIN_CONDITION_CRITERION_KEY)) &&
                 (DataAccessSearchCriterion.JOIN_CONDITIONS.containsObject(value)))
        {
            searchTerm.setJoinCondition(value);
        }
    }


    
    /**
     * Handles the special searchAll criteria which does a ORed contains search on all text fields. 
     *
     */
    public void handleSearchAllCriteria()
    {
        String searchAllValue = dataAccessParameters().formValueForKey(SEARCH_ALL_KEY);
        
        if (StringAdditions.isEmpty(searchAllValue))
        {
            return;
        }
    
        DebugOut.println(20, "searchAll criteria found " + searchAllValue);
        
        // We could call removeAllCriteria() here, but we won't so that this can be combined with other 
        // criteria - confusing as that may become!
        NSArray visibleFields = searchMode().visibleFields(); 
        for (int i = 0; i < visibleFields.count(); i++) {
            DataAccessColumn aColumn = (DataAccessColumn) visibleFields.objectAtIndex(i);
            if ((aColumn instanceof DataAccessStringColumn) || (aColumn instanceof DataAccessLookupColumn) || 
                    (aColumn instanceof DataAccessUserColumn))
            {
                DebugOut.println(30, "Adding searchAll criteria for " + aColumn.fieldName());
                addSearchCriterion(DataAccessSearchCriterion.OR_JOIN_CONDITION, 
                        aColumn.column(), 
                        DataAccessSearchCriterion.CONTAINS_COMPARISON_TYPE, 
                        searchAllValue);
            }
        }
    }



    /**
     * Clears any cached values.
     */
    public void clearCachedValues()
    {
        cachedURLDictionary = new NSMutableDictionary();
    }



    /**
     * Handles the special joinAll join which uses the same criteria for all conditions. 
     *
     */
    public void handleJoinAll()
    {
        String condition = dataAccessParameters().formValueForKey(JOIN_ALL_KEY);
        
        if (StringAdditions.isEmpty(condition))
        {
            return;
        }

        condition = condition.toUpperCase();
        
        // Just ignore invalid criteria
        if ( ! DataAccessSearchCriterion.JOIN_CONDITIONS.containsObject(condition.toUpperCase()))
        {
            return;
        }

        DebugOut.println(20, "joinAll condition found " + condition);
        
       Enumeration criterionEnumerator = criterionEnumerator();
        while (criterionEnumerator.hasMoreElements())
        {
            DataAccessSearchCriterion criterion = (DataAccessSearchCriterion)criterionEnumerator.nextElement();
            criterion.setJoinCondition(condition);
        }
    }
    
    
    
    /**
     * Handles parsing of sort order key-value pairs from the URL.  Point of single choice for URL sort order terms.
     */
    protected void handleSortOrderFormValue(String urlKey, String value)
    {
        /** require
        [param_urlKey_valid] urlKey != null;
        [param_value_value] value != null;
        [urlKey_is_sort_order_term] hasSortOrderKeyPrefix(urlKey); **/

        DebugOut.println(30, "Got a valid sort order prefix: " + urlKey + " with value: " + value);
        Integer index = numberPostfix(urlKey);

        // Create new criterion if needed... TODO: this DEFINATELY needs to be looked at
        while( ! hasSortOrderAtIndex(index.intValue()))
        {
            newSortOrder();
        }

        DataAccessSearchSortOrder sortOrderTerm = sortOrderAtIndex(index.intValue());

        // Validate each part of the criterion
        if ((urlKey.startsWith(COLUMN_SORT_ORDER_KEY)) && (dataAccessParameters().databaseTable().hasColumnNamed(value)))
        {
            sortOrderTerm.setSortColumn(dataAccessParameters.databaseTable().columnNamed(value));
        }

        else if (urlKey.startsWith(DIRECTION_SORT_ORDER_KEY))
        {
            sortOrderTerm.setSortDirection(value);
        }
    }


    /**
     * Sets the URL dictionary from the form values dictionary.
     */
    protected void setupFromFormValues()
    {
        DebugOut.println(20, "Setting up search params with form values: " + dataAccessParameters().formValues());

        removeAllCriteria();
        removeAllSortOrders();

        Enumeration keyEnumerator = dataAccessParameters().formValues().allKeys().objectEnumerator();
        while (keyEnumerator.hasMoreElements())
        {
            String urlKey = (String)keyEnumerator.nextElement();
            String value = formValueForKey(urlKey);
            if (value.equals(""))
            {
                value = null;
            }

            // Skip over any values that are not part of the search or sort order
            if ((hasSearchTermKeyPrefix(urlKey)) && (value != null))
            {
                handleSearchCriterionFormValue(urlKey, value);
            }
            else if ((hasSortOrderKeyPrefix(urlKey)) && (value != null))
            {
                handleSortOrderFormValue(urlKey, value);
            }
        }

        handleSearchAllCriteria();

        handleJoinAll();

        // Remove invalid search criteria
        NSMutableArray invalidCriteria = new NSMutableArray();
        Enumeration criterionEnumerator = criterionEnumerator();
        while (criterionEnumerator.hasMoreElements())
        {
            DataAccessSearchCriterion criterion = (DataAccessSearchCriterion)criterionEnumerator.nextElement();
            if ( ! criterion.isValid())
            {
                invalidCriteria.addObject(criterion);
                DebugOut.println(30, "Removed invalid search term: " + criterion);
            }
        }
        removeCriteriaInArray(invalidCriteria);

        // Remove invalid sort orders
        NSMutableArray invalidSortOrders = new NSMutableArray();
        Enumeration sortOrderEnumerator = sortOrderEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessSearchSortOrder sortOrder = (DataAccessSearchSortOrder)sortOrderEnumerator.nextElement();
            if ( ! sortOrder.isValid())
            {
                invalidSortOrders.addObject(sortOrder);
                DebugOut.println(30, "Removed invalid sort order: " + sortOrder);
            }
        }
        removeSortOrdersInArray(invalidSortOrders);

        // Get the group size
        String size = formValueForKey(DataAccessParameters.GROUP_SIZE_KEY);
        if ((size != null) && (StringAdditions.isInteger(size)) && (new Integer(size).intValue() > 0))
        {
            setGroupSize(formValueForKey(DataAccessParameters.GROUP_SIZE_KEY));
        }

        DebugOut.println(20, "Set up search params: " + searchCriteria);

        /** ensure [search_values_set] /# searchCriteria has valid keys and values from formValues #/ true; **/
    }



    /**
     * Returns the search terms as a dictionary appropriate for URL encoding.  Group size, order by and order direction terms are only included if they differ from the default (in DataAccess).
     *
     * @param includeSearchCriteria should the search criteria be included in this URL? Passing <code>false</code> still results in the group size, order by and order direction params being on the URL (if they are different from the default)
     * @return the search terms as a dictionary appropriate for URL encoding
     */
    public NSDictionary urlDictionary(boolean includeSearchCriteria)
    {
        String includeSearchCriteriaKey = includeSearchCriteria ? "true" : "false";

        // Return the cached value if it exists.  The URL dictionary is cached as an optimization as this method is called for each URL produced, but only has two variations.
        NSMutableDictionary urlDictionary = (NSMutableDictionary)cachedURLDictionary.objectForKey(includeSearchCriteriaKey);
        if (urlDictionary != null)
        {
            DebugOut.println(30, "Search params url dictionary (cached): " + urlDictionary);
            return urlDictionary;
        }

        urlDictionary = new NSMutableDictionary();
        
        // Add searchAll parameter, if present
        boolean hasSearchAllText = ! StringAdditions.isEmpty(searchAllText());
        if (hasSearchAllText)
        {
            urlDictionary.setObjectForKey(searchAllText(), SEARCH_ALL_KEY);
        }
        
        // Add joinAll parameter, if present
        boolean hasJoinAllCondition = ! StringAdditions.isEmpty(joinAllCondition()); 
        if (hasJoinAllCondition)
        {
            urlDictionary.setObjectForKey(joinAllCondition(), JOIN_ALL_KEY);
        }

        // Including the search criteria is optional and the searchAllText trumps any criteria present.
        if (includeSearchCriteria && ! hasSearchAllText)
        {
            Enumeration criterionEnumerator = criterionEnumerator();
            int index = 0;
            while (criterionEnumerator.hasMoreElements())
            {
                DataAccessSearchCriterion criterion = (DataAccessSearchCriterion)criterionEnumerator.nextElement();
                /** check [criterion_is_valid] criterion.isValid(); **/

                Column column = criterion.column();
                urlDictionary.setObjectForKey(column.normalizedName(), COLUMN_CRITERION_KEY + index);

                urlDictionary.setObjectForKey(criterion.comparisonType(), COMPARISON_TYPE_CRITERION_KEY + index);

                String value = criterion.value();
                // URLs in SM cannot have "/", "+", "%" or "&" in them (even if they are URL encoded) due to some URL rewriting that happens at the web server level. So we double encode those values and double decode on the other side
                if ((value.indexOf("/") != -1) || (value.indexOf("+") != -1) || (value.indexOf("%") != -1) || (value.indexOf("&") != -1))
                {
                    try
                    {
                        value = URLEncoder.encode(value, "UTF-8");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        throw new ExceptionConverter(e);
                    }
                }
                urlDictionary.setObjectForKey(value, VALUE_CRITERION_KEY + index);

                // The join is omitted if there is a joinAll condition as it takes precedence.
                if ( ! (isFirstCriterion(criterion) || hasJoinAllCondition))
                {
                    urlDictionary.setObjectForKey(criterion.joinCondition(), JOIN_CONDITION_CRITERION_KEY + index);
                }

                index++;
            }
        }

        Enumeration sortOrderEnumerator = sortOrderEnumerator();
        int index = 0;
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessSearchSortOrder sortOrder = (DataAccessSearchSortOrder)sortOrderEnumerator.nextElement();
            /** check [sort_order_is_valid] sortOrder.isValid(); **/

            urlDictionary.setObjectForKey(sortOrder.sortColumn().normalizedName(), COLUMN_SORT_ORDER_KEY + index);
            urlDictionary.setObjectForKey(sortOrder.sortDirection(), DIRECTION_SORT_ORDER_KEY + index);
            index++;
        }

        // Add group size, order by and order direction if they are different from the default
        if ((groupSize() != null) && ( ! (groupSize().equals(dataAccessParameters().dataAccessComponent().groupSize()))))
        {
            urlDictionary.setObjectForKey(groupSize(), DataAccessParameters.GROUP_SIZE_KEY);
        }
        
        cachedURLDictionary.setObjectForKey(urlDictionary, includeSearchCriteriaKey);
        DebugOut.println(30, "Search params url dictionary: " + urlDictionary);

        return urlDictionary;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the single form value for the passed key, or null if there is no value.
     *
     * @return the single form value for the passed key, or null if there is no value.
     */
    public String formValueForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/
        return dataAccessParameters().formValueForKey(aKey);
    }



    /**
     * Returns the DataAccessParameters holding these DataAccessSearchParameters.
     * 
     * @return the DataAccessParameters holding these DataAccessSearchParameters
     */
    public DataAccessParameters dataAccessParameters()
    {
        return dataAccessParameters;
    }

    
    
    /**
     * Returns the DataAccessSearchMode associated with these DataAccessSearchParameters.
     * 
     * @return the DataAccessSearchMode holding associated with these DataAccessSearchParameters
     */
    public DataAccessSearchMode searchMode()
    {
        return (DataAccessSearchMode) dataAccessParameters().dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
    }



    //************** Generic accessors/mutators **************\\


    public String groupSize()
    {
        return groupSize;
        /** ensure [non_null_result_is_valid_integer] Result != null ? StringAdditions.isInteger(Result) : true; **/
    }

    public void setGroupSize(String newGroupSize)
    {
        groupSize = newGroupSize;
    }

    
    public String searchAllText()
    {
        return searchAllText;
    }

    public void setSearchAllText(String newSearchAllText)
    {
        searchAllText = newSearchAllText;
    }

        
    public String joinAllCondition()
    {
        return joinAllCondition;
        /** ensure [valid_condition] (Result == null) || DataAccessSearchCriterion.JOIN_CONDITIONS.containsObject(Result);  **/
    }

    public void setJoinAllCondition(String newJoinAllCondition)
    {
        /** require [valid_param] (newJoinAllCondition == null) ||
                                  DataAccessSearchCriterion.JOIN_CONDITIONS.containsObject(newJoinAllCondition.toUpperCase());  **/
        joinAllCondition = newJoinAllCondition.toUpperCase();
    }



}
