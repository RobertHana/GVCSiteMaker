// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.user;


import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * This page allows the creation of users in singly or from batches in an uploaded file.
 */
public class CreateUserPage extends WOComponent implements SMSecurePage
{
    // For batches
    public NSData fileData;
    public String filePath;
    //private static final String INVALID_BATCH_FILE_ERROR_KEY = "invalidBatchFileErrorKey";

    // For single
    protected String newUserID;

    /** @TypeInfo java.lang.String */
    protected NSArray errorMessages;
    public String anErrorMessage;



    /**
     * Designated constructor.
     */
    public CreateUserPage(WOContext aContext)
    {
        super(aContext);
        errorMessages = NSArray.EmptyArray;
    }


    /**
     * Action method to validate and create a single user.  If the user was created, shows the Main page.  If the User ID was invalid, redisplays this page with an error message.
     *
     * @return the Main page if user was created, or this page if not
     */
    public WOComponent createNewUser()
    {
        WOComponent nextPage = context().page();

        // Validates newUserID() as a User ID
        setErrorMessages(User.validateUserID(newUserID()));

        if ( ! hasErrorMessages())
        {
            if ( User.userForUserID(newUserID(), session().defaultEditingContext()) == null)
            {
                User.createUser(session().defaultEditingContext(), newUserID() );
                session().defaultEditingContext().saveChanges();
                nextPage = returnToAdminMainPage();
            }
            else
            {
                setErrorMessages( new NSArray(User.USER_ID_EXISTS_ERROR));
            }
        }

        return nextPage;

        /** ensure [valid_result] Result != null;  
                   [correct_page] hasErrorMessages() || (Result instanceof com.gvcsitemaker.admin.commonwidgets.Main);  **/
    }



    /**
     * Action method to initiate creation of users from a batch file.  If a file was uploaded and all users are valid, shows the ConfirmUserCreationPage to finish the process.  If nothing was uploaded, or if there are validation errors at the file contents level, redisplays this page with an error message.
     *
     * @return the ConfirmUserCreationPage page if upload is valid, or this page if not
     */
    public WOComponent createNewUsersFromBatchFile()
    {
        WOComponent nextPage = context().page();
        
        NSArray newUsers = SMFileUtils.arrayOfObjectsFromBatchFileData(fileData,
                                                                       User.BATCH_FILE_KEY_ARRAY);
        DebugOut.println(1, "uploaded data = \n" + newUsers);
        if ( (newUsers != null) &&
             User.validateArrayOfUserDictionaries(newUsers, session().defaultEditingContext()) )
        {
            DebugOut.println(1, "validated data = \n" + newUsers);
            nextPage = pageWithName("ConfirmUserCreationPage");
            ((ConfirmUserCreationPage)nextPage).setUserArray(newUsers);
        }
        else
        {
            setErrorMessages(new NSArray("The batch file you have selected is not the proper format " +
                                      "or it does not contain the proper number of fields."));
        }

        return nextPage;

        /** ensure [valid_result] Result != null;  [correct_page] hasErrorMessages() || (Result instanceof ConfirmUserCreationPage);  **/
    }

    

    /**
     * Action method to remove any pending changes from the editing context and return to the Main page.
     *
     * @return the Main page
     */
    public WOComponent returnToAdminMainPage()
    {
        session().defaultEditingContext().revert();
        return pageWithName("Main");
    }


    
    /**
     * Returns <code>true</code> if there have been any validation errors recorded.
     *
     * @return <code>true</code> if there have been any validation errors recorded
     */
    public boolean hasErrorMessages()
    {
        return errorMessages().count() > 0;
    }




    /* ********** Generic setters and getters ************** */

    public String newUserID() {
        return newUserID;
    }
    public void setNewUserID(String newNewUserID) {
        newUserID = newNewUserID;
    }

    /** @TypeInfo java.lang.String */
    public NSArray errorMessages() {
        return errorMessages;
    }
    public void setErrorMessages(NSArray newErrorMessages) {
        errorMessages = newErrorMessages;
    }


    /** invariant [errorMessages_set]  errorMessages != null;  **/
}
