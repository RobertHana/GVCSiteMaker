package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.security.*;


/**
 * Serves as the base page for login with User from Security   See WOApplication pageWithName() for related information.  Subclasses need to override constructor and canLoginUser()  They may want to override registerUser().<br>
 * By default all users are allowed to access this page.  Be careful overriding permitsAccess(WOContext aContext).  If a user is denied access to a page and is sent to this page and denied access here, an endless loop could result.  If access is denied, also override loginPageName() to provide a way out. See WOApplication pageWithName()<br>
 *
 * <b>Added accessors for password and userName without which subclass can not access the private variables</b><br>
 *
 * <b>Your application's session must provide a method called <code>setUser(net.global_village.security.User newUser)</code>.  It must take in that exact class of user.</b>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
abstract public class LoginPage extends net.global_village.woextensions.WOComponent
{
    private String loginFailureMessage = null;
    private String password;
    private String userName;
    private NSSelector setUserSelector = new NSSelector("setUser", new Class[] {User.class});


    /**
     * Controls whether a secure connection is needed, currently it is always needed.
     *
     * @return always returns <code>true</code>
     */
    static public boolean needsSecureConnection()
    {
        return true;
    }



    /**
     * Designated constructor.
     */
    public LoginPage(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Accessor for loginFailureMessage.
     *
     * @return the login failure message
     */
    public String loginFailureMessage()
    {
        return loginFailureMessage;
    }



    /**
     * Accessor for loginFailureMessage.
     *
     * @param newLoginFailureMessage the new value for loginFailureMessage
     */
    public void setLoginFailureMessage(String newLoginFailureMessage)
    {
        loginFailureMessage = newLoginFailureMessage;
    }



    /**
     * Accessor for userName.
     *
     * @return userName
     */
    public String userName()
    {
        return userName;
    }



    /**
     * Accessor for userName.
     *
     * @param newUserName the new value for userName
     *
     */
    public void setUserName(String newUserName)
    {
        userName = newUserName;
    }



    /**
     * Accessor for password.
     *
     * @return the password
     */
    public String password()
    {
        return password;
    }



    /**
     * Accessor for password.
     *
     * @param newPassword the new value for password
     */
    public void setPassword(String newPassword)
    {
        password = newPassword;
    }


    
    /**
     * Logs in a user
     *
     * @return the post login page
     */
    public com.webobjects.appserver.WOComponent doLogin()
    {
        com.webobjects.appserver.WOComponent nextPageAfterLogin = null;
        
        if ((userName != null) && (password != null) && (password.length() > 0))
        {
            boolean userExists = User.userExists(userName,
                                                 password,
                                                 session().defaultEditingContext());
            if (userExists)
            {
                User theUser = User.user(userName, password, session().defaultEditingContext());
                
                if (canLoginUser(theUser))
                {
                    registerUser(theUser);
                    nextPageAfterLogin = nextPage();
                }
            }
        }
        
        if (nextPageAfterLogin == null)
        {
            if (loginFailureMessage == null)
            {
                String failureMessage = "Will be fetched from default plist";

                  if (DefaultValueRetrieval.defaultValueExists(this, "Application.loginPage.loginFailed"))
                  {
                      failureMessage = DefaultValueRetrieval.defaultString(this, "Application.loginPage.loginFailed");
                  }
                  else if (DefaultValueRetrieval.defaultValueExists(net.global_village.woextensions.WOApplication.class, "Application.loginPage.loginFailed"))
                  {
                      // The loginFailed was not set in the subclassed application. Therefore the framework's value will be used
                      failureMessage = DefaultValueRetrieval.defaultString(net.global_village.woextensions.WOApplication.class, "Application.loginPage.loginFailed");
                  }
                  setLoginFailureMessage(failureMessage);
            }
            nextPageAfterLogin = (com.webobjects.appserver.WOComponent) context().page();
        }
        
        return nextPageAfterLogin;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Override in sub-class to validate user.
     *
     * @param aUser user you wish to login
     * @return false in this class
     */
    public boolean canLoginUser(User aUser)
    {
        /** require [valid_param] aUser != null; **/

        return false;
    }


    
    /**
     * Override in subclass if the user needs to be registered in a different way... currently just sets the User to be the session user.
     *
     * @param aUser the user to be registered
     */
    public void registerUser(User aUser)
    {
        /** require [valid_param] aUser != null; **/

        try
        {
            setUserSelector.invoke(session(), aUser);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    
    
    /**
     * Returns the page to be displayed after logging in.
     *
     * @return WOComponent the page to be displayed
     *
     * Jass can't handle contracts on abstract methods. Add this to your subclass: ensure Result != null
     */
    abstract public com.webobjects.appserver.WOComponent nextPage();



}
