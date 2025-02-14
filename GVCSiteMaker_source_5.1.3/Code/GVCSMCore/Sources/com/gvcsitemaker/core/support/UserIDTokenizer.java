// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.foundation.*;


/**
 * Trivial tokenizer to return individual user IDs from a string containing multiple users IDs separated
 * by commas or whitespace.  Invalid user IDS are not returned, but are available through a separate
 * API along with a message indicating what was invalid.  In order to avoid spurious error messages,
 * user IDs of less than two characters are silently ignored.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class UserIDTokenizer extends Object
{
    private NSMutableSet setOfValidUserIDs;
    private NSMutableDictionary invalidUserIDDictionary;
    

    /**
     * Designated contstructor.
     * 
     * @param userIDs the String of userIDs to parse and separate.
     */
    public UserIDTokenizer(String userIDs)
    {
        super();

        // Use a set so that when people paste in duplicate names they are ignored.
        setOfValidUserIDs = new NSMutableSet();
        invalidUserIDDictionary = new NSMutableDictionary();

        if ((userIDs != null) && (userIDs.length() > 0))
        {
            DebugOut.println(20, "Validating userIDs: " + userIDs);
            StringTokenizer userIDTokenizer = new StringTokenizer(userIDs, ", \t\n\r\f");
            while (userIDTokenizer.hasMoreTokens())
            {
                String nextUserID = userIDTokenizer.nextToken();

                // People paste in junk so just ignore hyphens, etc.
                if (nextUserID.length() > 1)
                {
                    DebugOut.println(20, "Processing userID: " + nextUserID);

                    NSArray userValidationErrors = User.validateUserID(nextUserID);

                    if (userValidationErrors.count() > 0)
                    {
                        DebugOut.println(20, "...UserID failed validation");
                        invalidUserIDDictionary.setObjectForKey(userValidationErrors, nextUserID);
                    }
                    else
                    {
                        setOfValidUserIDs.addObject(User.canonicalUserID(nextUserID));
                    }
                }
                else
                {
                    DebugOut.println(20, "Skipping userID: " + nextUserID);
                }
            }
        }
    }



    /**
     * Returns the list of valid user IDs.
     * 
     * @return the list of valid user IDs.
     */
    public NSArray validUserIDs()
    {
        return setOfValidUserIDs.allObjects();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the email address of every valid user ID we've found.
     *
     * @return the email address of every valid user ID we've found
     */
    public NSArray emailAddressesOfValidUserIDs()
    {
        NSMutableArray emailAddressesOfValidUserIDs = new NSMutableArray();
        Enumeration userIDEnumeration = validUserIDs().objectEnumerator();
        while (userIDEnumeration.hasMoreElements())
        {
            String userID = (String)userIDEnumeration.nextElement();
            emailAddressesOfValidUserIDs.addObject(User.emailAddressFromUserID(userID));
        }

        return emailAddressesOfValidUserIDs;

        /** ensure
        [valid_result] Result != null;
        [one_email_per_userID] Result.count() == validUserIDs().count(); **/
    }



    /**
     * Returns <code>true</code> if any invalid user IDs were detected.
     *  
     * @return <code>true</code> if any invalid user IDs were detected
     */
    public boolean foundInvalidUserIDs()
    {
        return invalidUserIDDictionary.count() > 0;    
    }


    /**
     * Returns the list of invalid user IDs found.
     * 
     * @return the list of invalid user IDs found
     */
    public NSArray invalidUserIDs()
    {
        return invalidUserIDDictionary.allKeys();

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the error messages for a given invalid user ID or null if there are no validation errros
     * for that ID.
     * 
     * @param userID the user ID to return error messages for
     * @return the error messages for <code>userID</code>
     */
    public NSArray errorsFromInvalidUserID(String userID)
    {
        return (NSArray)invalidUserIDDictionary.objectForKey(userID);
    }



    /**
     * Private API for dictionary associating invalid user IDs with validation messages.
     * 
     * @return dictionary associating invalid user IDs with validation messages
     */
    protected NSMutableDictionary invalidUserIDDictionary()
    {
        return invalidUserIDDictionary;
    }



    /**
     * Private API for set of valid user IDs.
     * 
     * @return set of valid user IDs
     */
    protected NSMutableSet setOfValidUserIDs()
    {
        return setOfValidUserIDs;
    }


    /** invariant [lists_set] validUserIDs() != null && invalidUserIDs() != null; **/


}
