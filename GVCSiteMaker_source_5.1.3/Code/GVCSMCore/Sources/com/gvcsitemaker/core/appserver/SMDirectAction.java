// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.appserver;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WODirectAction;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.woextensions.*;
import net.global_village.woextensions.tests.*;



/**
 * Common DirectAction functionality for all GVCSiteMaker applications.  Currently, this provides associating the default action with the Main page, login, and standard error pages.
 */
public class SMDirectAction extends WODirectAction 
{
    public static final String DESTINATION_KEY = "destination";

    /**
     * Designated constructor.
     */
    public SMDirectAction(WORequest aRequest)
    {
        super(aRequest);
    }



    /**
     * Overridden to provide a better error message when the action name is 
     * not known. 
     */
    public WOActionResults performActionNamed(String actionName)
    {
        WOActionResults result = null;

        try
        {
          result = super.performActionNamed(actionName);
        }
        catch(Exception e)
        {
            if ((e instanceof NSForwardException) &&
                (((NSForwardException)e).originalException() instanceof NoSuchMethodException))
            {
                e = new RuntimeException("'" + actionName + "' was not found.  " +
                "Did you type it incorrectly?");
            }
            
            result = WOApplication.application().handleException(e, context());
        }
        catch(Throwable t)
        {
            result = WOApplication.application().handleException(new NSForwardException(t), context());
        }        

        return result;
        /** ensure [valid_result] Result != null;  **/
      }
     


    /**
     * Returns the page to be displayed (Main) when a request is made for the default action.
     *
     * @return the page to be displayed (Main) when a request is made for the default action.
     */
    public WOActionResults defaultAction()
    {
        return pageWithName("Main");
    }

  

    /**
     * Returns the LoginPage.
     *
     * @param destination (passed on URL) - destination URL to redirect to after successful login.  The Main page is shown if this is not given.
     * @param message (passed on URL) - optional message to display on login page.
     * @return the LoginPage.
     */
    public WOActionResults loginAction()
    {
        String destinationURL = (String)RequestUtils.cleanedFormValueForKey(request(), DESTINATION_KEY);
        if (destinationURL == null || ( ! ((SMApplication)Application.application()).urlHasValidDomain(destinationURL)))
        {
            DebugOut.println(1, "Invalid domain, redirecting to main page URL");
            destinationURL = SMActionURLFactory.mainPageURL();
        }

        // Don't make an already authenticated user login again
        if (((SMSession)session()).isUserAuthenticated())
        {
            if (destinationURL != null)
            {
                DebugOut.println(1, "User already logged in, redirecting to " + destinationURL);
                WORedirect redirectToDestination = new WORedirect(context());
                redirectToDestination.setUrl(destinationURL);
                return redirectToDestination;
            }
            else
            {
                DebugOut.println(1, "User already logged in, no destination, returning Main");
                return pageWithName("Main");
            }
        }
        else
        {
            SMLoginPage loginPage = (SMLoginPage)pageWithName("LoginPage");

            String message = (String)RequestUtils.cleanedFormValueForKey(request(), "message");
            loginPage.setMessage(message);

            if (destinationURL != null)
            {
                DebugOut.println(1, "After login redirecting to " + destinationURL);
                WORedirect redirectToDestination = new WORedirect(context());
                redirectToDestination.setUrl(destinationURL);
                loginPage.setDestination(redirectToDestination);
            }
            else
            {
                DebugOut.println(1, "After login redirecting to Main");
            }
            
            return loginPage;
        }


        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Logs out user and goes to main page
     *
     * @return main page
     */
    public WOActionResults logoutAction()
    {
        String destinationURL = (String)RequestUtils.cleanedFormValueForKey(request(), DESTINATION_KEY);
        if (destinationURL == null || ( ! ((SMApplication)Application.application()).urlHasValidDomain(destinationURL)))
        {
            destinationURL = SMActionURLFactory.mainPageURL();
        }

        // If the user is not logged in, just redirect
        if (! ((SMSession)session()).isUserAuthenticated())
        {
            DebugOut.println(1, "User not logged in, redirecting to " + destinationURL);
            WORedirect redirectToDestination = new WORedirect(context());
            redirectToDestination.setUrl(destinationURL);
            return redirectToDestination;
        }
        else
        {
            WOComponent nextPage = pageWithName("LogoutPage");
            nextPage.takeValueForKey(destinationURL, "destinationUrl");
            return nextPage;
        }
        /** ensure [result_not_null] Result != null; **/
    } 

    
    
    /**
     * Returns the page to be displayed when a request has an expired or non-existent session.  This
     * is called by other direct actions, it is not an action itself.
     *
     * @return	the page to be displayed when a request has an expired or non-existent session
     */
    public WOComponent canNotRestoreSessionError()
    {
        ErrorPageBase errorPage = (ErrorPageBase) pageWithName("ErrorPage");
        errorPage.setMessage("This operation cannot be completed.");
        errorPage.setReason("Your session cannot be restored.");

        return errorPage;
    }



    /**
     * Returns a quick response that can be used by scripts to determine if the application is alive or not.
     *
     * @return a quick response that can be used by scripts to determine if the application is alive or not.
     */
    public WOActionResults pingAction()
    {
        WOResponse pingResponse = new Response(context());
        pingResponse.appendContentString("<html><body>" + WOApplication.application().name() + " is responding.");

        // Check for database access
        EOEditingContext ec = new EOEditingContext();
        ec.lock();
        try
        {
            OrgUnit.rootUnit(ec);
        }
        catch (Throwable t)
        {
            pingResponse.appendContentString("<br><br><b>Database access failed:</b><br>");
            pingResponse.appendContentString(t.getMessage());
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }

        pingResponse.appendContentString("</body></html>");
        pingResponse.setHeader("text/html", "content-type");

        return pingResponse;

        /** ensure [return_not_null] Result != null; **/
    }

    
    
    /**
     * Returns session() downcast to SMSession.  Creates a session if none exists.
     * 
     * @return session() downcast to SMSession
     */
    public SMSession smSession()
    {
        return (SMSession)session();
        /** ensure [return_not_null] Result != null; **/
    }

}
