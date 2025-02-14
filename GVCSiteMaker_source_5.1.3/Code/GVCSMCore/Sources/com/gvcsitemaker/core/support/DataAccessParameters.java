package com.gvcsitemaker.core.support;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;
import net.global_village.woextensions.*;
import net.global_village.woextensions.tests.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;

/**
 * DataAccessParameters provides the parameters which control the display of a DataAccess section.
 * These paramters are taken from the URL and from defaults in the DataAccess PageComponent.  When
 * this class is instantiated, it copies all the appropriate (as defined by KEY_PREFIXES_ACCEPTED_IN_URL)
 * params into a "URL dictionary", which is used by methods that generate the params to add it's own
 * params to.  Each time the URL dictionary is "used" (by calling <code>url()</code>), it is reset
 * with the appropriate form values so that the next URL can be generated without params from one URL
 * getting into another's.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class DataAccessParameters extends Object
{
    /** Keys that we may find in a URL. */
    public static final String MODE_KEY = "mode";
    public static final String ACTION_KEY = "action";
    public static final String DUPLICATE_ACTION_VALUE = "duplicate";
    public static final String NEXT_MODE_KEY = "nextMode";
    public static final String RECORD_ID_KEY = "recordID";
    public static final String GROUP_SIZE_KEY = "groupSize";
    public static final String FIND_ALL_KEY = "findAll";

    /** These are the values we got from the request.  These don't change once set, and since we only pass along those keys with acceptable prefixes, we keep the <code>urlDictionary</code> in sync with these values. */
    protected NSDictionary formValues;

    /** Methods add key/values to this dictionary, which is then URL encoded to give the final URL. The values stored here are transient per URL, meaning they go away and are re-populated with the <code>formValues</code> each time the <code>url()</code> method is called. */
    protected NSMutableDictionary urlDictionary = null;

    protected NSMutableDictionary daFormValueDictionary = null;

    protected DataAccessSearchParameters searchParameters;

    /** The set of key prefixes that are copied into the URL dictionary.  The rest of the keys in the URL are handled by other classes, most notably DataAccessSearchParameters.  We use prefixes because search values will have keys that look like: "column1", "value1", "column2", "value2", etc. */
    protected static final NSSet KEY_PREFIXES_ACCEPTED_IN_URL = new NSSet(new String[] {MODE_KEY, RECORD_ID_KEY, SectionDisplay.SECTION_STYLE_OVERRIDE_FLAG, SectionDisplay.CONFIG_FLAG, FIND_ALL_KEY});

    protected DataAccess dataAccessComponent;
    protected PageScaffold pageScaffold;
    protected SMSession session;

    protected EOEnterpriseObject currentRow;

    /** For optimization of allRows() and countOfRowsInTable(). */
    protected NSArray cachedRows = null;
    protected Number cachedCountOfRowsInTable = null;


    /**
     * Returns the name of the DataAccess mode from the URL
     *
     * @return the name of the DataAccess mode from the URL
     */
    public static String modeNameFrom(WORequest aRequest)
    {
        return (String)aRequest.formValueForKey(MODE_KEY);
    }



    /**
     * Designated constructor.
     */
    public DataAccessParameters(WOContext aContext, DataAccess aDataAccessComponent)
    {
        super();

        /** require
        [valid_aContext] aContext != null;
        [valid_dataAccessComponent] aDataAccessComponent != null; **/

        dataAccessComponent = aDataAccessComponent;
        pageScaffold =  (PageScaffold)aContext.page();
        session = (SMSession)aContext.session();
        formValues = (aContext.request().formValues());

        searchParameters = new DataAccessSearchParameters(this);

        resetURLDictionary();

        /** ensure [search_params_set] /# Can't have this in the invariant due to the invariant being evaluated too soon... #/ searchParameters != null; **/
    }



    /**
     * Returns <code>true</code> if there is an entry in KEY_PREFIXES_ACCEPTED_IN_URL that begins with <code>key</code>,
     * <code>false</code> otherwise.
     *
     * @param key the key to check
     * @return <code>true</code> if there is an entry in KEY_PREFIXES_ACCEPTED_IN_URL that begins with <code>key</code>,
     *      <code>false</code> otherwise
     */
    protected boolean isValidKeyPrefix(String key)
    {
        /** require [valid_param] key != null; **/
        Enumeration keyPrefixEnumerator = KEY_PREFIXES_ACCEPTED_IN_URL.objectEnumerator();
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
     * Clears any cached values.
     */
    public void clearCachedValues()
    {
        daFormValueDictionary = null;
        cachedCountOfRowsInTable = null;
        searchParameters().clearCachedValues();
    }


    /**
     * Sets the URL dictionary from the form values dictionary. Since form values have the form key = NSArray(value),
     * we unwrap the value from the array before setting it in the URL dictionary.
     *
     * @param aFormValues the form values to set
     */
    public void resetURLDictionary()
    {
        // Use a copy of the cached value if we have one
        if (daFormValueDictionary != null)
        {
            urlDictionary = new NSMutableDictionary(daFormValueDictionary);
        }
        else
        {
            urlDictionary = new NSMutableDictionary();

            // Add formValues
            Enumeration keyEnumerator = formValues().keyEnumerator();
            while (keyEnumerator.hasMoreElements())
            {
                String key = (String)keyEnumerator.nextElement();
                if (isValidKeyPrefix(key))
                {
                    urlDictionary.setObjectForKey(formValueForKey(key), key);
                }
            }

            // Cache this for later
            daFormValueDictionary = new NSMutableDictionary(urlDictionary);
        }

        /** ensure [form_values_set] /# urlDictionary has valid keys and values from formValues #/ true; **/
    }



    /**
     * Returns the name of the DataAccess mode from the URL, or from the Section's default if not specified on the URL.
     *
     * @return the name of the DataAccess mode from the URL, or from the Section's default if not specified on the URL
     */
    public String modeName()
    {
        String modeName = formValueForKey(MODE_KEY);

        return dataAccessComponent().validatedModeName(modeName);

        /** ensure [valid_mode] DataAccessMode.isValidMode(Result); **/
    }



    /**
     * Returns the name of the next DataAccess mode to display data in from the URL, or from the Section's default if not specified on the URL.
     *
     * @return the name of the next DataAccess mode to display data in from the URL, or from the Section's default if not specified on the URL
     */
    public String nextModeName()
    {
        String nextModeName = (formValueForKey(NEXT_MODE_KEY) == null) ? dataAccessComponent().defaultMode() : formValueForKey(NEXT_MODE_KEY);

        if ( ! DataAccessMode.isValidMode(nextModeName))
        {
            nextModeName = dataAccessComponent().defaultMode();
        }

        return nextModeName;

        /** ensure [valid_mode] DataAccessMode.isValidMode(Result); **/
    }



    /**
     * Sets the value for the given key into the URL.
     *
     * @param value the value to set
     * @param key the key to set
     */
    public void addToURL(String value, String key)
    {
        /** require [valid_value_param] value != null; [valid_key_param] key != null; **/
        urlDictionary.setObjectForKey(value, key);
    }



    /**
     * Sets the value for the given key into the URL.
     *
     * @param value the value to set
     * @param key the key to set
     */
    public void removeFromURL(String key)
    {
        /** require [valid_key_param] key != null; **/
        urlDictionary.removeObjectForKey(key);
    }



    /**
     * Returns the complete URL, taken from the dictionary of form values, and starts a new URL dictionary so that params from one URL don't get mixed into the params of another.  NOTE: this violates the strict semantics of functions (which aren't supposed to have side effects)!!!
     *
     * @param includeSearchCriteria should the search criteria be included in this URL?
     * @return the complete URL, optionally including search criteria
     */
    public String url(boolean includeSearchCriteria)
    {
        NSMutableDictionary tempDictionary = urlDictionary;
        tempDictionary.addEntriesFromDictionary(searchParameters.urlDictionary(includeSearchCriteria));

        // Reset the urlDictionary so that params from one URL don't get into another's
        resetURLDictionary();
        return NSDictionaryAdditions.urlEncodedDictionary(tempDictionary);

        /** ensure
        [valid_result] Result != null;
        [form_values_are_reset] /# urlDictionary has valid keys and values from formValues() #/ true; **/
    }


    /**
     * Returns the complete URL, taken from the dictionary of form values, and starts a new URL dictionary so that params from one URL don't get mixed into the params of another.  NOTE: this violates the strict semantics of functions (which aren't supposed to have side effects)!!!
     *
     * @return the complete URL
     */
    public String url()
    {
        return url(true);
        /** ensure
        [valid_result] Result != null;
        [form_values_are_reset] /# urlDictionary has valid keys and values from formValues() #/ true; **/
    }



    /**
     * Returns the parameters for the URL to show after adding a new record.  If the nextMode is specified on the URL it will be used, if is not present then Single Record will be the next mode.
     *
     * @return the parameters for the URL to show after adding a new record
     */
    public String postAddParameters()
    {
        String modeName = (formValueForKey(NEXT_MODE_KEY) == null) ? DataAccessMode.SINGLE_MODE : formValueForKey(NEXT_MODE_KEY);
        addToURL(modeName, MODE_KEY);

        // If a record was added it will become the new focused record.  This requires the adding code to call setCurrentRecord()...
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to redisplay the current section, groups of records, etc.
     *
     * @return the parameters for the URL to redisplay the current section, groups of records, etc.
     */
    public String currentModeAndRecordParameters()
    {
        String modeName = (formValueForKey(MODE_KEY) == null) ? dataAccessComponent().defaultMode() : formValueForKey(MODE_KEY);
        addToURL(modeName, MODE_KEY);

        // If there is a current row, keep it as the focused record.
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the first record in Single mode.
     *
     * @return the parameters for the URL to display the first record in Single mode
     */
    public String firstRecordInSingleModeParameters()
    {
        addToURL(DataAccessMode.SINGLE_MODE, MODE_KEY);
        if (hasRows())
        {
            addRecordIDToURL(firstRow());
        }
        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the previous record in Single mode.
     *
     * @return the parameters for the URL to display the prevoius record in Single mode
     */
    public String previousRecordInSingleModeParameters()
    {
        /** require
        [has_rows] hasRows();
        [has_current_row] currentRow() != null;
        [is_not_firstt_row] ! isFirstRow(); **/

        addToURL(DataAccessMode.SINGLE_MODE, MODE_KEY);
        addRecordIDToURL(previousRow());
        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the next record in Single mode.
     *
     * @return the parameters for the URL to display the next record in Single mode
     */
    public String nextRecordInSingleModeParameters()
    {
        /** require
        [has_rows] hasRows();
        [has_current_row] currentRow() != null;
        [is_not_last_row] ! isLastRow(); **/

        addToURL(DataAccessMode.SINGLE_MODE, MODE_KEY);
        addRecordIDToURL(nextRow());
        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the last record in Single mode.
     *
     * @return the parameters for the URL to display the last record in Single mode.
     */
    public String lastRecordInSingleModeParameters()
    {
        /** require [has_rows] hasRows(); **/

        addToURL(DataAccessMode.SINGLE_MODE, MODE_KEY);
        addRecordIDToURL(lastRow());
        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the current section, groups of records, etc. in the next mode.
     *
     * @return the parameters for the URL to display the current section, groups of records, etc. in the next mode
     */
    public String nextModeAndCurrentRecordParameters()
    {
        addToURL(nextModeName(), MODE_KEY);

        // If there is a current row, keep it as the focused record.
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to use when cancelling the addition of records.  The mode to display is determined in priority of:<br/>
     * <ul>
     * <li>List Mode if the default mode is add and there is either no nextMode or nextMode=add</li>
     * <li>nextMode</li>
     * <li>The default mode</li>
     * </ul>
     *
     * @return the parameters for the URL to use when cancelling the addition of records
     */
    public String cancelAddParameters()
    {
        String nextModeName = nextModeName();

        // Cancelling the addition of records when there is no nextMode and the Add Form is the default mode should result in the list of records display rather than redisplaying the Add Form.
        if (nextModeName.equals(DataAccessMode.ADD_MODE) &&
            dataAccessComponent().defaultMode().equals(DataAccessMode.ADD_MODE))
        {
            nextModeName = DataAccessMode.LIST_MODE;
        }

        addToURL(nextModeName, MODE_KEY);

        // If there is a row indentified in the URL, keep it as the focused record.
        if (formValueForKey(RECORD_ID_KEY) != null)
        {
            addToURL(formValueForKey(RECORD_ID_KEY), RECORD_ID_KEY);
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to use when cancelling the import of records.  The mode to
     * display is determined in priority of:<br/>
     * <ul>
     * <li>List Mode if the default mode is Import and there is either no nextMode or nextMode=import</li>
     * <li>nextMode</li>
     * <li>The default mode</li>
     * </ul>
     *
     * @return the parameters for the URL to use when cancelling the importation of records
     */
    public String cancelImportParameters()
    {
        String nextModeName = nextModeName();

        // Cancelling the importation of records when there is no nextMode and the Import Form is the
        // default mode should result in the list of records display rather than redisplaying the Import Form.
        if (nextModeName.equals(DataAccessMode.IMPORT_MODE) &&
            dataAccessComponent().defaultMode().equals(DataAccessMode.IMPORT_MODE))
        {
            nextModeName = DataAccessMode.LIST_MODE;
        }

        addToURL(nextModeName, MODE_KEY);

        // If there is a row indentified in the URL, keep it as the focused record.
        if (formValueForKey(RECORD_ID_KEY) != null)
        {
            addToURL(formValueForKey(RECORD_ID_KEY), RECORD_ID_KEY);
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the current record in Single Record mode with all other parameters intact.
     *
     * @return the parameters for the URL to display the current record in Single Record mode with all other parameters intact
     */
    public String currentRecordInSingleModeParameters()
    {
        addToURL(DataAccessMode.SINGLE_MODE, MODE_KEY);

        // If there is no current row the default will be shown.
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the current record in List of Records mode with all other parameters intact.
     *
     * @return the parameters for the URL to display the current record in List of Records mode with all other parameters intact
     */
    public String currentRecordInListModeParameters()
    {
        addToURL(DataAccessMode.LIST_MODE, MODE_KEY);

        // If there is no current row the default will be shown.
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the currentRow() record in Single Record mode with nextMode of the current mode and all other parameters intact.
     *
     * @return the parameters for the URL to display the currentRow() record in Single Record mode with nextMode of the current mode and all other parameters intact
     */
    public String recordInSingleModeWithNextModeParameters()
    {
        addToURL(DataAccessMode.SINGLE_MODE, MODE_KEY);
        addToURL(modeName(), NEXT_MODE_KEY);

        addRecordIDToURL();

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the current record in the current mode after removing any user supplier qualifier (search paramters).
     *
     * @return the parameters for the URL to display the current record in the current mode after removing any user supplier qualifier (search paramters)
     */
    public String currentModeAndRecordWithoutQualifierParameters()
    {
        String modeName = (formValueForKey(MODE_KEY) == null) ? dataAccessComponent().defaultMode() : formValueForKey(MODE_KEY);
        addToURL(modeName, MODE_KEY);

        // If there is no current row the default will be shown.
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        // Indicate that we do not want the default search
        addToURL("true", FIND_ALL_KEY);

        return url(false);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to add a new record and then return to the current mode.
     *
     * @return the parameters for the URL to add a new record and then return to the current mode
     */
    public String addRecordAndReturnToCurrentModeParameters()
    {
        addToURL(DataAccessMode.ADD_MODE, MODE_KEY);
        String modeName = (formValueForKey(MODE_KEY) == null) ? dataAccessComponent().defaultMode() : formValueForKey(MODE_KEY);
        addToURL(modeName, NEXT_MODE_KEY);

        // Add this in case the Add is cancelled.
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to import records and then return to the current mode.
     *
     * @return the parameters for the URL to import records and then return to the current mode
     */
    public String importRecordsAndReturnToCurrentModeParameters()
    {
        addToURL(DataAccessMode.IMPORT_MODE, MODE_KEY);
        String modeName = (formValueForKey(MODE_KEY) == null) ? dataAccessComponent().defaultMode() : formValueForKey(MODE_KEY);

        // Prevent looping back to Import from Import
        modeName = modeName.equals(DataAccessMode.IMPORT_MODE) ? dataAccessComponent().defaultMode() : modeName;

        addToURL(modeName, NEXT_MODE_KEY);

        // Add this in case the Add is cancelled.
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to add a new record based on the current record and then return to the current mode.
     *
     * @return the parameters for the URL to add a new record based on the current record and then return to the current mode
     */
    public String duplicateRecordAndReturnToCurrentModeParameters()
    {
        addToURL(DUPLICATE_ACTION_VALUE, ACTION_KEY);
        return addRecordAndReturnToCurrentModeParameters();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to go to Search for Records mode.
     *
     * @return the parameters for the URL to go to Search for Records mode
     */
    public String searchForRecordsParameters()
    {
        addToURL(DataAccessMode.SEARCH_MODE, MODE_KEY);

        // Cancel find all mode
        findSelected();

        // If there is a current row maintain it so we can return to it if needed
        if (recordID() != null)
        {
            addRecordIDToURL();
        }

        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameter for the current focused record, if any.
     * Returns it as recordID=<primary key>.  Returns null otherwise.
     *
     * @return the parameter for the current focused record, if any.  Returns null otherwise
     */
    public String recordID()
    {
        String recordID = null;

        if (currentRow() != null)
        {
            recordID = recordID(currentRow());
        }
        else if (formValueForKey(RECORD_ID_KEY) != null)
        {
            recordID = RECORD_ID_KEY + "=" + formValueForKey(RECORD_ID_KEY);
        }

        return recordID;
    }



    /**
     * Returns the recordID parameter for the passed object.
     *
     * @param eoObject the record whose ID will be returned
     * @return the recordID parameter for the passed object.
     */
    public String recordID(EOEnterpriseObject eoObject)
    {
        /** require [valid_param] eoObject != null; **/

        Object pk = SMEOUtils.primaryKeyForObject(eoObject);
        if (pk instanceof NSData)
        {
            String stringPK = ERXStringUtilities.byteArrayToHexString(((NSData)pk).bytes());
            return RECORD_ID_KEY + "=" + stringPK;
        }
        else
        {
            return RECORD_ID_KEY + "=" + pk;
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Sets the record ID in the URL dictionary.
     *
     * @param eoObject the record whose ID will be set
     */
    public void addRecordIDToURL(EOEnterpriseObject eoObject)
    {
        /** require [valid_param] eoObject != null; **/
        Object pk = SMEOUtils.primaryKeyForObject(eoObject);
        if (pk instanceof NSData)
        {
            String stringPK = ERXStringUtilities.byteArrayToHexString(((NSData)pk).bytes());
            addToURL(stringPK, RECORD_ID_KEY);
        }
        else
        {
            addToURL(pk.toString(), RECORD_ID_KEY);
        }
    }



    /**
     * Returns the parameter for the current focused record, if any.  Returns it as recordID=<primary key>.  Returns null otherwise.
     *
     * @return the parameter for the current focused record, if any.  Returns null otherwise
     */
    public void addRecordIDToURL()
    {
        if (currentRow() != null)
        {
            addRecordIDToURL(currentRow());
        }
        else if (formValueForKey(RECORD_ID_KEY) != null)
        {
            addToURL(formValueForKey(RECORD_ID_KEY), RECORD_ID_KEY);
        }
    }



    /**
     * Returns the row specified in the URL, or null if: none was specified,
     * the specified row does not exist, or the specified row is not within the
     * mandatory default search.
     *
     * @return the row specified in the URL, or null if: none was specified, the specified row does not exist, or the specified row is not within the mandatory default search
     */
    public EOEnterpriseObject recordFromUrl()
    {
        EOEnterpriseObject recordFromUrl = null;
        String recordIdFromUrl = formValueForKey(RECORD_ID_KEY);

        if (recordIdFromUrl != null)
        {
            boolean isHex = (recordIdFromUrl.length() == 48) && (ERXStringUtilities.hexStringToByteArray(recordIdFromUrl).length == 24);
            boolean isInteger = (recordIdFromUrl.length() < 48) && (StringAdditions.isInteger(recordIdFromUrl));

            if (isInteger || isHex)
            {
                Object rowPK;
                if (SMApplication.smApplication().isUsingIntegerPKs())
                {
                    rowPK = new Integer(recordIdFromUrl);
                }
                else
                {
                    if (isInteger)
                    {
                        recordIdFromUrl = ERXStringUtilities.leftPad(recordIdFromUrl, '0', 48);
                    }
                    rowPK = new NSData(ERXStringUtilities.hexStringToByteArray(recordIdFromUrl));
                }

                try
                {
                    recordFromUrl = databaseTable().objectWithPrimaryKey(rowPK);

                    // Use the default row if the result set doesn't include the current row
                    if ( ! allRows().containsObject(recordFromUrl))
                    {
                        recordFromUrl = null;
                    }
                }
                catch (com.webobjects.eoaccess.EOObjectNotAvailableException e)
                {
                    recordFromUrl = null;
                }
            }
        }

        return recordFromUrl;
    }



    /**
     * Adds the group size to the URL dictionary.
     *
     * @param groupSize the group size to use
     */
    public void addGroupSizeToURL(Integer groupSize)
    {
        /** require [valid_param] groupSize != null; **/
        addToURL(groupSize.toString(), GROUP_SIZE_KEY);
    }


    /**
     * Adds the group size to the URL dictionary.  First checks for a size in the form values.  If it exists, we use that.  Otherwise, we use the default from the data access component (which adds nothing to the URL).
     */
    public void addGroupSizeToURL()
    {
        String groupSizeString = formValueForKey(GROUP_SIZE_KEY);
        if ((groupSizeString != null) && (StringAdditions.isInteger(groupSizeString)))
        {
            Integer groupSize = new Integer(groupSizeString);
            addGroupSizeToURL(groupSize);
        }
    }



    /**
     * Returns the primaryKey for the currentRow() as a String.
     *
     * @return the primaryKey for the currentRow() as a String.
     */
    public String currentRowPK()
    {
        /** require [valid_param] currentRow() != null; **/

        if (SMApplication.smApplication().isUsingIntegerPKs())
        {
            return SMEOUtils.primaryKeyForObject(currentRow()).toString();
        }
        else
        {
            return ERXStringUtilities.byteArrayToHexString(((NSData)SMEOUtils.primaryKeyForObject(currentRow())).bytes());
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns true iff the query to the data can be optimized into raw SQL.  At the moment, that
     * means that it cannot contain any calculated fields.
     *
     * @return true iff the query to the data can be optimized into raw SQL
     */
    protected boolean queryCanBeOptimized()
    {
        // Check the stored search for calculated fields only if we restrict to the stored criteria.
        // Otherwise, don't check the stored search if we have criteria on the URL, or if we should find all
        // or if the setting to use the stored search is off.
        if (dataAccessComponent().restrictToDefaultResults() ||
        	(dataAccessComponent().useDefaultSearch() &&
        	 ! shouldFindAll() &&
        	 ! searchParameters().hasCriteria()) )
        {
            DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
            Enumeration criteriaEnumerator = searchMode.orderedCriteria().objectEnumerator();
            while (criteriaEnumerator.hasMoreElements())
            {
            	DataAccessColumnSearchValue searchValue = (DataAccessColumnSearchValue)criteriaEnumerator.nextElement();
            	if (searchValue.column() instanceof VirtualCalculatedColumn)
            	{
            		return true;
            	}
            }
        }

        // Check any query on the URL for calculated fields
        if (searchParameters().hasCriteria())
        {
            Enumeration criteriaEnumerator = searchParameters().criterionEnumerator();
            while (criteriaEnumerator.hasMoreElements())
            {
            	DataAccessSearchCriterion searchValue = (DataAccessSearchCriterion)criteriaEnumerator.nextElement();
            	if (searchValue.column() instanceof VirtualCalculatedColumn)
            	{
            		return true;
            	}
            }
        }

        // Check the sort orderings
        Enumeration sortOrderingsEnumerator;
        if (searchParameters().hasSortOrders())
        {
        	sortOrderingsEnumerator = searchParameters().sortOrderEnumerator();
        }
        else
        {
            DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
            sortOrderingsEnumerator = searchMode.sortOrderEnumerator();
        }
        while (sortOrderingsEnumerator.hasMoreElements())
        {
        	DataAccessSearchSortOrder sortOrder = (DataAccessSearchSortOrder)sortOrderingsEnumerator.nextElement();
        	if (sortOrder.sortColumn() instanceof VirtualCalculatedColumn)
        	{
        		return true;
        	}
        }

        return false;
    }



    /**
     * Returns the displayed rows of the database table.  These have possibly been restricted by
     * either the stored search or a user search (or both) and are sorted by the configured search
     * columns and directions (ascending or descending).
     *
     * @return the ordered displayed rows of the database table
     */
    protected NSArray allRowsWithQualifier()
    {
        EOQualifier qualifier = null;
        // Check the stored search if we restrict to the stored criteria
        // Otherwise, don't check the stored search if we have criteria on the URL, or if we should find all
        // or if the setting to use the stored search is off.
        if (dataAccessComponent().restrictToDefaultResults() ||
        	(dataAccessComponent().useDefaultSearch() &&
        	 ! shouldFindAll() &&
        	 ! searchParameters().hasCriteria()) )
        {
            DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
            qualifier = searchMode.storedSearchQualifierWithParameters(this);
        }

        // The current user is needed for comparison to some fields
        User currentUser = session().currentUser();
        DebugOut.println(30, "searchParameters().hasCriteria() " + searchParameters().hasCriteria());

        if (searchParameters().hasCriteria())
        {
            if (dataAccessComponent().restrictToDefaultResults() && (qualifier != null))
            {
                qualifier = new EOAndQualifier(new NSArray(new Object[]
                                                           {qualifier, searchParameters().searchQualifier(currentUser)}));
            }
            else
            {
            	/** [search_sql_must_be_null] check searchSQL == null; **/
                qualifier = searchParameters().searchQualifier(currentUser);
            }
        }

        NSArray allRows = databaseTable().objectsWithQualifier(qualifier);

        // Sort the rows.  Use the sort orderings that are on the URL, if they exist; if they don't, use the default
        NSArray sortOrderings;
        if (searchParameters().hasSortOrders())
        {
            sortOrderings = searchParameters().sortOrdering();
        }
        else
        {
            DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
            sortOrderings = searchMode.storedSortOrder();
        }

        return EOSortOrdering.sortedArrayUsingKeyOrderArray(allRows, sortOrderings);
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the displayed rows of the database table.  These have possibly been restricted by
     * either the stored search or a user search (or both) and are sorted by the configured search
     * columns and directions (ascending or descending).
     *
     * @return the ordered displayed rows of the database table
     */
    protected NSArray allRowsWithSQL()
    {
        DataAccessParametersSQLFactory sqlFactory = new DataAccessParametersSQLFactory(this);
        String sql = sqlFactory.searchSQL();
        NSMutableArray columnNames = new NSMutableArray(new Object[] {"virtualRowID", "virtualTableID"});
        NSArray rawRows = EOUtilities.rawRowsForSQL(editingContext(), "VirtualTables", sql, columnNames);

        NSMutableArray allRows = new NSMutableArray();
        Enumeration rawRowsEnumerator = rawRows.objectEnumerator();
        while (rawRowsEnumerator.hasMoreElements())
        {
        	NSDictionary row = (NSDictionary)rawRowsEnumerator.nextElement();
        	Object virtualRow = EOUtilities.objectFromRawRow(editingContext(), "VirtualRow", row);
        	allRows.addObject(virtualRow);
        }

        return allRows;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the displayed rows of the database table.  These have possibly been restricted by either the stored search or a user search (or both) and are sorted by the configured search columns and directions (ascending or descending).
     *
     * @return the ordered displayed rows of the database table
     */
    public NSArray allRows()
    {
        // HACK
        // Hack for SCR 369/1158, a bug allows this to be null
        if (dataAccessComponent().databaseTable() == null)
        {
            throw new RuntimeException("This section cannot be displayed as configuration of " +
            "this section is incomplete: the Data Access section has not been linked to a Data Table.");
        }

        if (cachedRows == null)
        {
            if (dataAccessComponent().canBrowseOrSearchRecords())
            {
            	if ( ! queryCanBeOptimized())
            	{
            		cachedRows = allRowsWithSQL();
            	}
            	else
                {
					cachedRows = allRowsWithQualifier();
                }
            }
            else
            {
                cachedRows = new NSArray();
            }

        }

        return cachedRows;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the total number of rows in the table that the user can see.  This means that if the section is not restricted to the default search, this will return the number of rows in the table.  If the section is restricted to the default search, this will return the number of rows found by the default search.
     *
     * @return the number of rows in the table
     */
    public int countOfRowsInTable()
    {
        /*EOQualifier qualifier = null;

        if (dataAccessComponent().restrictToDefaultResults())
        {
            DataAccessSearchMode searchMode = (DataAccessSearchMode)dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
            qualifier = searchMode.storedSearchQualifierWithParameters(this);
        }

        return databaseTable().objectsWithQualifier(qualifier).count(); */

        if (cachedCountOfRowsInTable == null)
    	{
    		if (dataAccessComponent().restrictToDefaultResults())
    		{
    			DataAccessParametersSQLFactory sqlFactory = new DataAccessParametersSQLFactory(this);
    			String sql = sqlFactory.countSQL();
    			NSMutableArray columnName = new NSMutableArray(new Object[] {"count"});
    			NSArray rawRow = EOUtilities.rawRowsForSQL(editingContext(), "VirtualTables", sql, columnName);
    			cachedCountOfRowsInTable = ((Number)((NSDictionary)rawRow.objectAtIndex(0)).valueForKey("count"));
    		}
    		else
    		{
    			cachedCountOfRowsInTable = new Integer(((VirtualTable)databaseTable()).rows().count());
    		}
    	}
    	return cachedCountOfRowsInTable.intValue();
    }



    /**
     * Returns <code>true</code> if there are any rows in the current result set.
     *
     * @return <code>true</code> if there are any rows in the current result set
     */
    public boolean hasRows()
    {
        return allRows().count() > 0;
    }



    /**
     * Returns <code>true</code> if the default search should be ignored.
     *
     * @return <code>true</code> if the default search should be ignored.
     */
    public boolean shouldFindAll()
    {
        //return formValues().valueForKey(FIND_ALL_KEY) != null;
        return urlDictionary.valueForKey(FIND_ALL_KEY) != null;
    }



    /**
     * Sets the parameters up to not restrict rows to those selected from user criteria or default
     * search.
     */
    public void findAll()
    {
        searchParameters().removeAllCriteria();
        // true is not a magic value, anything will do, but it looks good.
        addToURL("true", FIND_ALL_KEY);
        /** ensure [should_find_all] shouldFindAll();  **/
    }



    /**
     * Sets the parameters up to only find selected (from user criteria or default search) records.
     */
    public void findSelected()
    {
        removeFromURL(FIND_ALL_KEY);
        /** ensure [should_not_find_all] ! shouldFindAll();  **/
    }



    /**
     * Returns the ordered, filtered, group of records to display in the current group.
     *
     * @return the ordered, filtered list of records to display in the current group
     */
    public NSArray recordsInCurrentGroup()
    {
        int groupSize = cannonicalGroupSize();

        NSArray groupRows = allRows();
        if (groupRows.count() > groupSize)
        {
            int recordIndex = 0;

            EOEnterpriseObject currentRecord = recordFromUrl();
            if (currentRecord != null)
            {
                recordIndex = groupRows.indexOfIdenticalObject(currentRecord);
            }

            int groupNumber = recordIndex / groupSize;
            int startIndex = groupNumber * groupSize;

            if (startIndex + groupSize > groupRows.count())
            {
                groupSize = groupRows.count() - startIndex;
            }
            /** check [range_not_too_big] startIndex + groupSize <= groupRows.count(); **/

            NSRange range = new NSRange(startIndex, groupSize);
            groupRows = groupRows.subarrayWithRange(range);
        }
        return groupRows;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the DataAccess PageComponent that this object holds parameters for.
     *
     * @return the DataAccess PageComponent that this object holds parameters for
     */
    public DataAccess dataAccessComponent()
    {
        return dataAccessComponent;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the appropriate child of dataAccessComponent() for the mode indicated by modeName().
     *
     * @return the appropriate child of dataAccessComponent() for the mode indicated by modeName()
     */
    public DataAccessMode modeComponentForMode()
    {
        return dataAccessComponent().componentForMode(modeName());
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the form values dictinary from which this object gets its parameters.
     *
     * @return the form values dictinary from which this object gets its parameters
     */
    public NSDictionary formValues()
    {
        return formValues;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Sets the form values that this will use and then resets the URL dictionary and search parameters so that the next call to <code>url()</code> will return the results from the new form values.
     *
     * @param newFormValues the form values to use
     */
    public void setFormValues(NSDictionary newFormValues)
    {
        /** require [valid_param] newFormValues != null;  **/
        formValues = newFormValues;
        daFormValueDictionary = null;   // Clear cached values in case something changed on the URL that we care about (note that DAParameters - this class - doesn't care about any of the search params, that is handled below)
        resetURLDictionary();
        searchParameters().setupFromFormValues();  // This doesn't use the cached dictionary, but uses the form values directly
    }



    /**
     * Returns the single form value for the passed key, or null if there is no value.
     *
     * @return the single form value for the passed key, or null if there is no value
     */
    public String formValueForKey(String aKey)
    {
        /** require [valid_param] aKey != null; **/
        Object formValueForKey = formValues().objectForKey(aKey);
        if (formValueForKey instanceof NSArray)
        {
            // If there is more than one form value for this key, it is indeterministic which one we get...
            formValueForKey = (((NSArray)formValueForKey).lastObject());
        }

        return (String) formValueForKey;
    }



    /**
     * Returns the Row/Record to focus on if none is specified.
     *
     * @return the Row/Record to focus on if none is specified
     */
    public EOEnterpriseObject defaultRow()
    {
        /** require [filtered_table_has_rows] hasRows(); **/

        return (EOEnterpriseObject)allRows().objectAtIndex(0);

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> if currentRow() is the first row from the result set.
     *
     * @return <code>true</code> if currentRow() is the first row from the result set
     */
    public boolean isFirstRow()
    {
        /** require
        [has_rows] hasRows();
        [has_current_row] currentRow() != null; **/

        return allRows().objectAtIndex(0).equals(currentRow());
    }



    /**
     * Returns <code>true</code> if currentRow() is the last row from the result set.
     *
     * @return <code>true</code> if currentRow() is the last row from the result set
     */
    public boolean isLastRow()
    {
        /** require
        [has_rows] hasRows();
        [has_current_row] currentRow() != null; **/

        return allRows().lastObject().equals(currentRow());
    }



    /**
     * Returns the first row from the result set.
     *
     * @return the first row from the result set
     */
    public EOEnterpriseObject firstRow()
    {
        /** require [has_rows] hasRows(); **/
        return (EOEnterpriseObject)allRows().objectAtIndex(0);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the previous row from the result set.
     *
     * @return the previous row from the result set
     */
    public EOEnterpriseObject previousRow()
    {
        /** require
        [has_rows] hasRows();
        [has_current_row] currentRow() != null;
        [is_not_first_row] ! isFirstRow(); **/

        return (EOEnterpriseObject)allRows().objectAtIndex(allRows().indexOfObject(currentRow()) - 1);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the next row from the result set.
     *
     * @return the next row from the result set
     */
    public EOEnterpriseObject nextRow()
    {
        /** require
        [has_rows] hasRows();
        [has_current_row] currentRow() != null;
        [is_not_last_row] ! isLastRow(); **/

        return (EOEnterpriseObject)allRows().objectAtIndex(allRows().indexOfObject(currentRow()) + 1);

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the last row from the result set.
     *
     * @return the last row from the result set
     */
    public EOEnterpriseObject lastRow()
    {
        /** require [has_rows] hasRows(); **/
        return (EOEnterpriseObject)allRows().lastObject();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if current group is the first group of records in the found set, <code>false</code> otherwise.
     *
     * @return <code>true</code> if current group is the first group of records in the found set
     */
    public boolean isFirstGroup()
    {
        /** require [has_rows] hasRows(); **/

        return allRows().objectAtIndex(0).equals(recordsInCurrentGroup().objectAtIndex(0));
    }



    /**
     * Returns <code>true</code> if current group is the last group of records in the found set, <code>false</code> otherwise.
     *
     * @return <code>true</code> if current group is the last group of records in the found set
     */
    public boolean isLastGroup()
    {
        /** require [has_rows] hasRows(); **/

        return allRows().lastObject().equals(recordsInCurrentGroup().lastObject());
    }



    /**
     * Returns the parameters for the URL to display the first group of records in List of Records mode with all other parameters intact.
     *
     * @return the parameters for the URL to display the first group of records in List of Records mode with all other parameters intact
     */
    public String firstGroupInListModeParameters()
    {
        addToURL(DataAccessMode.LIST_MODE, MODE_KEY);
        if (hasRows())
        {
            addRecordIDToURL(firstRecordInFirstGroup());
        }
        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the previous group of records in List of Records mode with all other parameters intact.
     *
     * @return the parameters for the URL to display the previous group of records in List of Records mode with all other parameters intact.
     */
    public String previousGroupInListModeParameters()
    {
        /** require
        [has_rows] hasRows();
        [not_last_group] ! isFirstGroup(); **/

        addToURL(DataAccessMode.LIST_MODE, MODE_KEY);
        addRecordIDToURL(firstRecordInPreviousGroup());
        return url();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the parameters for the URL to display the next group of records in List of Records mode with all other parameters intact.
     *
     * @return the parameters for the URL to display the next group of records in List of Records mode with all other parameters intact.
     */
    public String nextGroupInListModeParameters()
    {
        /** require
        [has_rows] hasRows();
        [not_last_group] ! isLastGroup(); **/

        addToURL(DataAccessMode.LIST_MODE, MODE_KEY);
        addRecordIDToURL(firstRecordInNextGroup());
        return url();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the parameters for the URL to display the last group of records in List of Records mode with all other parameters intact.
     *
     * @return the parameters for the URL to display the last group of records in List of Records mode with all other parameters intact.
     */
    public String lastGroupInListModeParameters()
    {
        /** require
        [has_rows] hasRows();
        [not_last_group] ! isLastGroup(); **/

        addToURL(DataAccessMode.LIST_MODE, MODE_KEY);
        addRecordIDToURL(firstRecordInLastGroup());
        return url();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the first Row/Record in the current group.
     *
     * @return the first Row/Record in the current group
     */
    public EOEnterpriseObject firstRecordInCurrentGroup()
    {
        /** require [has_rows] hasRows(); **/

        return (EOEnterpriseObject)recordsInCurrentGroup().objectAtIndex(0);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the first Row/Record in the first group.
     *
     * @return the first Row/Record in the first group
     */
    public EOEnterpriseObject firstRecordInFirstGroup()
    {
        /** require [has_rows] hasRows(); **/

        return (EOEnterpriseObject)allRows().objectAtIndex(0);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the first Row/Record in the previous group.
     *
     * @return the first Row/Record in the previous group
     */
    public EOEnterpriseObject firstRecordInPreviousGroup()
    {
        /** require
        [has_rows] hasRows();
        [not_first_group] ! isFirstGroup(); **/

        NSArray allRows = allRows();
        int groupSize = cannonicalGroupSize();
        int index = allRows.indexOfIdenticalObject(recordsInCurrentGroup().objectAtIndex(0));
        /** check [row_was_found] index != NSArray.NotFound; **/

        EOEnterpriseObject object;
        if (index > groupSize)
        {
            object = (EOEnterpriseObject)allRows.objectAtIndex(index - groupSize);
        }
        else
        {
            object = (EOEnterpriseObject)allRows.objectAtIndex(0);
        }
        return object;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the first Row/Record in the next group.
     *
     * @return the first Row/Record in the next group
     */
    public EOEnterpriseObject firstRecordInNextGroup()
    {
        /** require
        [has_rows] hasRows();
        [not_last_group] ! isLastGroup(); **/

        NSArray allRows = allRows();
        int groupSize = cannonicalGroupSize();
        int index = allRows.indexOfIdenticalObject(recordsInCurrentGroup().objectAtIndex(0));
        /** check [row_was_found] index != NSArray.NotFound; **/

        return (EOEnterpriseObject)allRows.objectAtIndex(index + groupSize);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the first Row/Record in the last group.  Returns null if there are no rows.
     *
     * @return the first Row/Record in the last group
     */
    public EOEnterpriseObject firstRecordInLastGroup()
    {
        /** require
        [has_rows] hasRows(); **/

        NSArray allRows = allRows();
        int groupSize = cannonicalGroupSize();
        int groups = allRows.count() < groupSize ? 0 : (allRows.count() - 1) / groupSize;

        return (EOEnterpriseObject)allRows.objectAtIndex(groupSize * groups);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * If the search params have a group size defined, then return that, otherwise return the default group size from the search component.
     *
     * @return the actual group size to use
     */
    public int cannonicalGroupSize()
    {
        /** require [has_default_group_size] dataAccessComponent().groupSize() != null; **/

        String groupSizeString = searchParameters.groupSize();
        if ((groupSizeString != null) && StringAdditions.isInteger(groupSizeString))
        {
            return new Integer(groupSizeString).intValue();
        }
        else
        {
            return dataAccessComponent().groupSizeInt();
        }
    }



    /**
     * Returns <code>true</code> if there is an action specified in the URL and the action is DUPLICATE_ACTION_VALUE
     *
     * @return <code>true</code> if there is an action specified in the URL and the action is DUPLICATE_ACTION_VALUE
     */
    public boolean isDuplicateAction()
    {
        String action = formValueForKey(ACTION_KEY);
        return (action != null) && action.equalsIgnoreCase(DUPLICATE_ACTION_VALUE);
    }



    /**
     * Selects the current row either from the recordID in the URL, or the default record if the URL does not specify one.  The currentRow() is left as null if there are no rows in the table.
     */
    public void selectCurrentRow()
    {
        EOEnterpriseObject row = null;
        if (hasRows())
        {
            row = recordFromUrl();

            if (row == null)
            {
                row = defaultRow();
            }
        }

        setCurrentRow(row);
    }



    /**
     * Returns the Row/Record that this DataAccess section is currently focused on.
     *
     * @return the Row/Record that this DataAccess section is currently focused on.
     */
    public EOEnterpriseObject currentRow()
    {
        return currentRow;
    }



    /**
     * Sets the Row/Record that this DataAccess section is currently focused on.
     *
     * @param newRow the Row/Record to focus on
     */
    public void setCurrentRow(EOEnterpriseObject newRow)
    {
        currentRow = (newRow == null) ? null : EOUtilities.localInstanceOfObject(editingContext(), newRow);
    }



    /**
     * Returns the object that manages user searches.
     *
     * @return the <code>DataAccessSearchParameters</code>
     */
    public DataAccessSearchParameters searchParameters()
    {
        return searchParameters;
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Sets the DataAccessSearchParameters used by this.
     *
     * @param aDataAccessSearchParameters the new DataAccessSearchParameters
     */
    public void setSearchParameters(DataAccessSearchParameters aDataAccessSearchParameters)
    {
        /** require [valid_param] aDataAccessSearchParameters != null; **/
        searchParameters = aDataAccessSearchParameters;
    }



    /**
     * Returns the Row/Record that this DataAccess section is currently focused on.
     *
     * @return the Row/Record that this DataAccess section is currently focused on.
     */
    public EOEditingContext editingContext()
    {
        return dataAccessComponent().editingContext();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the Row/Record that this DataAccess section is currently focused on.
     *
     * @return the Row/Record that this DataAccess section is currently focused on
     */
    public Table databaseTable()
    {
        return dataAccessComponent().databaseTable();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the PageScaffold holding the DataAccess ComponentPrimitives these
     * DataAccessParameters were created for.  This is only valid for component action requests and
     * not for direct action requests such as to display a DA file.
     *
     * @return the PageScaffold holding the DataAccess ComponentPrimitives these
     * DataAccessParameters were created for.
     */
    public PageScaffold pageScaffold()
    {
        /** require [has_pageScaffold] hasPageScaffold();   **/
        return pageScaffold;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> if these DataAccessParameters were created from a PageScaffold
     * holding the DataAccess ComponentPrimitives.  Returns <code>false</code> if there is no
     * PageScaffold as is the case when DataAccessParameters are created from a direct action.
     *
     * @return <code>true</code> if these DataAccessParameters reference a PageScaffold
     */
    public boolean hasPageScaffold()
    {
        return pageScaffold != null;
    }



    /**
     * Returns the Session of the User who made the request creating these DataAccessParameters.
     *
     * @return the Session of the User who made the request creating these DataAccessParameters.
     */
    public SMSession session()
    {
        return session;
    }



    /**
     * Returns a user-readable string representation of this object.
     *
     * @return a user-readable string representation of this object
     */
    public String toString()
    {
        return "form values: " + formValues + " -- search params: " + searchParameters.toString();

        /** ensure [valid_result] Result != null; **/
    }



    /** invariant
    [valid_dataAccessComponent] dataAccessComponent() != null;
    [valid_session] session() != null;
    [valid_formValues] formValues() != null; **/


}
