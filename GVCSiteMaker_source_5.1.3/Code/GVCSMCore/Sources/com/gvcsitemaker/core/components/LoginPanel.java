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
 * Implements a login panel for authenticating users in SiteMaker (using information in the User object).  This login panel is used in SMLoginPage and can be used in sub-classes fo SMLoginPage to implement login functionality.
 */
public class LoginPanel extends WOComponent
{
    protected String userID;			// User ID of User to log in
    protected String password;			// User's password
    protected WOComponent destination;		// Page to display after successful login
    protected String loginFailureMessage;	// Message to show is log in fails
    protected String subTitle;			// Optional message diplayed to left of form submission buttons


    /**
     * Designated constructor.  Sets the destination page to Main.
     */
    public LoginPanel(WOContext aContext)
    {
        super(aContext);

        setUserID("");
        setPassword("");

        // Make the developer's life easier.  :-)
        if (SMApplication.appProperties().booleanPropertyForKey("DevelopmentMode"))
        {
            setUserID(SMApplication.appProperties().stringPropertyForKey("DeveloperID"));
            setPassword(SMApplication.appProperties().stringPropertyForKey("DeveloperPassword"));
        }

        /** ensure
        [user_id_not_null] userID() != null;
        [password_not_null] password() != null;  **/
    }



    /**
     * Attempts to login the user.  A User objects is created if this is an Internal User login, and the User has not previously been created in GVC.SiteMaker.  Returns the destination page if the user was authenticated, or this page if the user was not authenticated.  If the user was authenticated, calls performPostAuthenticationAction() on the destination page if it exists.
     *
     * @return the destination page if the user was authenticated, or this page if the user was not authenticated.
     */
    public WOComponent loginAction()
    {
        WOComponent nextPage = context().page();

        DebugOut.println(3,"Entering...");
        DebugOut.println(3,"UserID:" + userID);

        // Perform basic validation on the data, not authentication.
        validateUserIDAndPassword();

        if (loginFailureMessage() == null)
        {
            User loginUser = User.userForUserID(userID(), session().defaultEditingContext());

            // Authenticate, then send them along.
            if ((loginUser != null) && canAuthenticateUser(loginUser))
            {
                // All is OK, there is nothing to do as we have our authenticated user
            }
            // Automatic creation of Internal Users upon first login: if this user does not exist, and this is an Internal User ID, and we can authenticate  then create the user.
            else if ((loginUser == null) &&
                     User.isInternalUserID(userID()) &&
                     User.canAuthenticateInternalUser(User.canonicalUserID(userID()), password()))
            {
                DebugOut.println(1,"First time login for internal user: " + userID() );
                loginUser = User.createUser(session().defaultEditingContext(),
                                            User.canonicalUserID(userID()));
                session().defaultEditingContext().saveChanges();
                ((SMSession)session()).setCurrentAuthenticatedUser(loginUser);
            }
            else
            {
                // The User does not exist, cannot be authenticated, and cannot be created as an Internal User.  Bail.
                loginUser = null;
                setLoginFailureMessage("Login failed. Please try again.");
            }

            // If we have an authenticated user then record them and move on.
            if (loginUser != null)
            {
                ((SMSession)session()).setCurrentAuthenticatedUser(loginUser);
                nextPage = destination();
            }
        }

        return nextPage;

        /** ensure [valid_next_page] loginFailureMessage() != null ||  Result.equals(destination());  **/
    }



    /**
     * Returns <code>true</code> if the user can be authenticated.  At present this just calls into User, but other authentication mechanisms could be used.
     *
     * @param aUser the User to authenticate.
     *
     * @return <code>true</code> if the user can be authenticated.  
     */
    protected boolean canAuthenticateUser(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/
        
        return aUser.canAuthenticateWithPassword(password());
    }

 

    /**
     * Validates the userID and password entered by the user, setting loginFailureMessage appropriately if the values are invalid.  At present this excludes internal (externally validated) Users.
     */
    protected void validateUserIDAndPassword()
    {
        // This is also the flag as to whether the UserID and Password are valid or not
        setLoginFailureMessage(null);

        if (userID() == null)
        {
            setLoginFailureMessage("You must enter a User Name!");
        }
        else if (password() == null)
        {
            setLoginFailureMessage("You must enter a password!");
        }
        // Ensure the user ID is an e-mail address if this installation is not using Internal Users.  Otherwise anything goes!
        else if ( ! User.isValidUserID(userID()))
        {
            setLoginFailureMessage(SMApplication.appProperties().stringPropertyForKey("UserIDNotEmailAddressMessage"));
        }
    }



    /**
     * Returns the direct action URL for a User to get their password sent to them.
     *
     * @return the direct action URL for a User to get their password sent to them.
     */
    public String sendPasswordURL()
    {
        return SMActionURLFactory.sendPasswordURL(context().request());

        /** ensure [result_not_null] Result != null; **/
    }

    
    /* Generic setters and getters ***************************************/

    public String userID() {
        return userID;
    }
    public void setUserID(String newUserID) {
        userID = newUserID;
    }

    public String password() {
        return password;
    }
    public void setPassword(String newPassword) {
        password = newPassword;
    }

    public WOComponent destination() {
        return destination;
    }
    public void setDestination(WOComponent newDestination) {
        destination = newDestination;
    }

    public String loginFailureMessage() {
        return loginFailureMessage;
    }
    public void setLoginFailureMessage(String newLoginFailureMessage) {
        loginFailureMessage = newLoginFailureMessage;
    }

    public String subTitle() {
        return subTitle;
    }
    public void setSubTitle(String newSubTitle) {
        subTitle = newSubTitle;
    }

}
