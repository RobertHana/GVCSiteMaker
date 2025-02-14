// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;


/**
 * Implements a change password panel for users managed by SiteMaker.  At present it does not handle changing the password of users who are athenticated externally to SiteMaker, e.g. 'internal users' authenticated by Kerberos, LDAP etc.  It could be extended to handle this, though no provision has been made to do so.  This component is used in ChangePasswordPage.
 */
public class ChangePasswordPanel extends net.global_village.woextensions.WOComponent
{

    protected String message;		// Message about success or failure of changing password
    protected String userID;			// User ID of User to change passwork for
    protected String oldPassword;	// Current password
    protected String password;		// New password
    protected String secondPassword;	// Confirmation of new password.


    /**
     * Designated constructor.
     */
    public ChangePasswordPanel(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * This is a non-synchronizing component.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Attempts to change the user's password.  Returns this page with an explanatory message regardless of the outcome.
     *
     * @return this page with an explanatory message regardless of the outcome.
     */
    public WOComponent changePassword()
    {
        DebugOut.println(1, "userID() is " + userID());

        // Do not allow null passwords or user IDs
        if ((password() == null) || (password().length() < 1) ||
            (secondPassword() == null) || (secondPassword().length() < 1) )
        {
            setMessage(((SMSession)session()).localizedStringInContext("blankPasswordNotAllowed", context()));
        }
        else if ((userID() == null) || (userID().length() < 1))
        {
            setMessage("You must enter your User ID.");
        }
        else 
        {
            session().defaultEditingContext().revert();
            User loginUser = User.userForUserID(userID(), session().defaultEditingContext());
            DebugOut.println(1, "loginUser is " + loginUser);

            // Make sure the userID is valid
            if (loginUser == null)
            {
                setMessage("The User ID or password you entered were not correct.  Please try again.");
            }
            else if (loginUser.isInternalUser())
            {
                setMessage(SMApplication.appProperties().stringPropertyForKey("CantChangeInternalUserPasswordMessage"));
            }
            else if ( ! loginUser.canAuthenticateWithPassword(oldPassword()))
            {
                setMessage("The User ID or password you entered were not correct.  Please try again.");
            }
            // Now check that the new passwords match
            else if ( ! password().equals(secondPassword()))
            {
                setMessage("New passwords do not match. Try again");
            }
            // If we've made it here, then we change things.
            else
            {
                loginUser.setPassword(password());
                DebugOut.println(30,"Saving changes to user" + loginUser.userID());
                session().defaultEditingContext().saveChanges();
                setMessage("Password successfully changed.");
            }
        }

        return context().page();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the userID of the user having their password changed.  If this has not been set, it is given the value bound to it by its parent.
     *
     * @return the userID of the user having their password changed.
     */
    public String userID()
    {
        if (userID == null)
        {
            userID = (String) valueForBinding("userID");
        }

        return userID;
    }



    /* Generic setters and getters ***************************************/

    public void setUserID(String newUserID) {
        userID = newUserID;
    }

    public String message() {
        return message;
    }
    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public String password() {
        return password;
    }
    public void setPassword(String newPassword) {
        password = newPassword;
    }

    public String oldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String newOldPassword) {
        oldPassword = newOldPassword;
    }

    public String secondPassword() {
        return secondPassword;
    }
    public void setSecondPassword(String newSecondPassword) {
        secondPassword = newSecondPassword;
    }

}
