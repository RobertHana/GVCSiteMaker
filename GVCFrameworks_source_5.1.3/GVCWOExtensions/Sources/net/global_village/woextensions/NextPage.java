package net.global_village.woextensions;

import java.util.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * Holds all the information needed to go to a given page.  Use this to go back to the original page
 * when you have a page that can be accessed from multiple pages.
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */
public class NextPage
{
    protected String nextPageName;
    protected NSArray orderedDictionariesOfKeysAndValuesToSetInNextPage;


    /**
     * Designated constructor.
     *
     * @param aNextPageName the name of next page to display
     * @param aValuesToSetInNextPage ordered array of dictionaries containing keys and values to set in the next page
     */
    public NextPage(String aNextPageName, NSArray aValuesToSetInNextPage)
    {
        super();
        /** require
        [valid_aNextPageName_param] aNextPageName != null;
        [valid_aValuesToSetInNextPage_param] aValuesToSetInNextPage != null; **/

        nextPageName = aNextPageName;
        orderedDictionariesOfKeysAndValuesToSetInNextPage = aValuesToSetInNextPage;
    }


    /**
     * Cover constructor with a dictionary of parameters.
     *
     * @param aNextPageName the name of next page to display
     * @param aValuesToSetInNextPage keys and values to set in the next page
     */
    public NextPage(String aNextPageName, NSDictionary aValuesToSetInNextPage)
    {
        super();
        /** require
        [valid_aNextPageName_param] aNextPageName != null;
        [valid_aValuesToSetInNextPage_param] aValuesToSetInNextPage != null; **/

        nextPageName = aNextPageName;
        orderedDictionariesOfKeysAndValuesToSetInNextPage = new NSArray(aValuesToSetInNextPage);
    }


    /**
     * Cover constructor with no values to set in next page.
     *
     * @param aNextPageName the name of next page to display.
     */
    public NextPage(String aNextPageName)
    {
        this(aNextPageName, NSDictionary.EmptyDictionary);
        /** require [valid_param] aNextPageName != null; **/
    }



    /**
     * Returns the next page to display.  Instantiates the page with the application.
     *
     * @param aContext the context in which to display this page
     * @return the next page to display
     */
    public com.webobjects.appserver.WOComponent nextPage(WOContext aContext)
    {
        /** require [valid_param] aContext != null; **/
        com.webobjects.appserver.WOComponent nextPage = WOApplication.application().pageWithName(nextPageName, aContext);

        Enumeration dictionariesToSetInNextPageEnumerator = orderedDictionariesOfKeysAndValuesToSetInNextPage.objectEnumerator();
        while (dictionariesToSetInNextPageEnumerator.hasMoreElements())
        {
            NSDictionary valuesToSetInNextPage = (NSDictionary)dictionariesToSetInNextPageEnumerator.nextElement();
            Enumeration valuesToSetInNextPageEnumerator = valuesToSetInNextPage.keyEnumerator();
            while (valuesToSetInNextPageEnumerator.hasMoreElements())
            {
                String key = (String)valuesToSetInNextPageEnumerator.nextElement();
                Object value = valuesToSetInNextPage.valueForKey(key);
                nextPage.takeValueForKeyPath(value, key);
            }
        }

        return nextPage;
    }


    /** invariant
    [has_nextPageName] nextPageName != null;
    [has_valuesToSetInNextPage] orderedDictionariesOfKeysAndValuesToSetInNextPage != null; **/


}
