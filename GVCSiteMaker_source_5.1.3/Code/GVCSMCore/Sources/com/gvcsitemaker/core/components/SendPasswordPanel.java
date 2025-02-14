// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;


/**
 * Implements a send password panel for users managed by SiteMaker.  At present it does not handle sending the password of users who are athenticated externally to SiteMaker, e.g. 'internal users' authenticated by Kerberos, LDAP etc.  It could be extended to handle this, though no provision has been made to do so.  This component is used in SendPasswordPage.
 */
public class SendPasswordPanel extends WOComponent {

    protected String message;	// Message about success or failure of sending password message
    protected String userID;		// The User ID of the User to e-mail this to.


    /**
     * Designated constructor.
     */
    public SendPasswordPanel(WOContext aContext)
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
     * Attempts to e-amil the users password.  Returns this page with an explanatory message regardless of the outcome.
     *
     * @return this page with an explanatory message regardless of the outcome.
     */
    public WOComponent sendPassword()
    {
        DebugOut.println(30, "userID() is " + userID());

        if ((userID() == null) || (userID().length() < 1))
        {
            setMessage("You must enter a User Name!");
        }
        else
        {
            User loginUser = User.userForUserID(userID(), session().defaultEditingContext());
            DebugOut.println(30, "loginUser is " + loginUser);

            if (loginUser == null)
            {
                setMessage("The User ID you entered was not correct.  Please try again.");
            }
            else if (loginUser.isInternalUser())
            {
                setMessage(SMApplication.appProperties().stringPropertyForKey("CantSendInternalUserPasswordMessage"));
            }
            else
            {
                SendEmail.sendPasswordToForgetfulUser(context().request(), loginUser.password(), loginUser.userID());
                setMessage("Your password has been mailed to you and should arrive soon.");

            }
        }

        return context().page();
    }



    /* Generic setters and getters ***************************************/

    public String userID() {
        return userID;
    }
    public void setUserID(String newUserID) {
        userID = newUserID;
    }

    public String message() {
        return message;
    }
    public void setMessage(String newMessage) {
        message = newMessage;
    }

}
