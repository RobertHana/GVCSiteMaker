// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import java.io.*;

import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOApplication;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/**
 * This class handles reporting and logging of exceptions caught by the application.
 */
public class ErrorLogger extends Object
{


    public static WOResponse handleException(Throwable anException, WOContext aContext)
    {
        EOEditingContext ec = new EOEditingContext();

        // Auto EC locking is not occuring here, so need to lock manually
        ec.lock();
        try
        {
            ErrorLog log = logErrorLocation(ec, aContext, anException, "EXCEPTION" );

            if (aContext.hasSession())
            {
                ErrorPageBase errorPage = (ErrorPageBase) WOApplication.application().pageWithName("ErrorPage", aContext);
                errorPage.setShouldTerminateSession(true);
                errorPage.setErrorLog(log);
                errorPage.setMessage( "An Unexpected Error has occurred. It has been logged.  Thank you for your cooperation.");

                WOResponse aResponse = null;
                if (AjaxUtils.isAjaxRequest(aContext.request()))
                {
                    AjaxUtils.redirectTo(errorPage);
                    aResponse = errorPage.context().response();
                    aContext.session().savePage(errorPage);
                }
                else
                {
                    aResponse = errorPage.generateResponse();
                }
                return aResponse;
            }

            // For sessionless direct action errors
            WOResponse emptyResponse = new Response(aContext);
            emptyResponse.appendContentString("<html><body>Your request produced an error");
            emptyResponse.appendContentString(".</body></html>");
            emptyResponse.setHeader("text/html", "content-type");
            return emptyResponse;
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
    }



    public static WOResponse handlePageRestorationErrorInContext(WOContext aContext)
    {
        // No need to log page restorations errors.
        // logErrorLocation( aContext, null, "PAGE RESTORATION ERROR" );

        ErrorPageBase errorPage = (ErrorPageBase) WOApplication.application().pageWithName("ErrorPage", aContext);
        errorPage.setMessage( "Your action could not be completed.  This may be the result of selecting your browser " +
                "Back button too many times, the length of time since your last completed action, or a server error. " +
                "Please try quitting your browser and going back to the main page and logging in again. " +
                "This error has been logged. Thank you for your cooperation." );

        WOResponse response = null;
        if (AjaxUtils.isAjaxRequest(aContext.request()))
        {
            AjaxUtils.redirectTo(errorPage);
            response = errorPage.context().response();
            aContext.session().savePage(errorPage);
        }
        else
        {
            response = errorPage.generateResponse();
        }

        response.setStatus(500);
        return response;
    }



    public static WOResponse handleSessionRestorationErrorInContext( WOContext aContext ) {
        // No need to log session restorations errors.
        //logErrorLocation( aContext, null, "SESSON RESTORATION ERROR" );

        ErrorPageBase errorPage = (ErrorPageBase) WOApplication.application().pageWithName("ErrorPage", aContext);
        errorPage.setMessage( "Your action could not be completed.  This may be due to the length of time since " +
                "your last completed action or a server error.  Please try quitting your browser and going back " +
                "to the main page and logging in again. This error has been logged. Thank you for your cooperation." );

        WOResponse response = null;
        if (AjaxUtils.isAjaxRequest(aContext.request()))
        {
            AjaxUtils.redirectTo(errorPage);
            response = errorPage.context().response();
            aContext.session().savePage(errorPage);
        }
        else
        {
            response = errorPage.generateResponse();
        }
        response.setStatus(500);
        return response;
    }



    /**
     * Creates a new <code>ErrorLog</code> for <code>anException</code>.
     *
     * @param ec the editing context to create the <code>ErrorLog</code>.  This is assumed to be locked
     * @param aContext optional <code>WOContext</code> with information to be included in the
     * <code>ErrorLog</code>
     * @param anException option exception to log message and stack trace from
     * @param type type of the exception being logged, for informational purposes only
     *
     * @return the newly created <code>ErrorLog</code>
     */
    public static ErrorLog logErrorLocation(EOEditingContext ec,
                                            WOContext aContext,
                                            Throwable anException,
                                            String type)
    {
        /** require [valid_ec] ec != null;    [valid_type] type != null;         **/
        DebugOut.println(0, "Enter Error Logger");

        ErrorLog newLog = ErrorLog.newErrorLog();
        ec.insertObject(newLog);

        newLog.setEventDate(Date.now());
        newLog.setLogType(type);

        if(WOApplication.application() != null)
        {
            newLog.setAppName(WOApplication.application().name());
        }

        if (anException != null)
        {
            newLog.setExceptionMessage(originalException(anException).getMessage());
            newLog.setStackTrace(getStackTrace( originalException(anException)));
        }

        if(aContext != null)
        {
            if (aContext.request() != null)
            {
                newLog.setHostName(RequestUtils.hostNameFromRequest(aContext.request()));
            }

            if(aContext.page() != null)
            {
                newLog.setPageName( aContext.page().name());
            }

            if (aContext.component() != null)
            {
                newLog.setComponentName(aContext.component().name());
            }

            // Avoid needless sesson creation
            if (aContext.hasSession())
            {
                newLog.setUserID(((SMSession) aContext.session()).currentUser().userID());
            }
            else
            {
                newLog.setUserID("unknown");
            }

            newLog.setRequest(aContext.request().toString());
        }

        try
        {
            DebugOut.println( 0, "log item: " + newLog.toString() );
            DebugOut.println( 0, ">>> Trying Save Changes" );
            ec.saveChanges();
            DebugOut.println( 0, "<<< Saved Changes" );
        }
        catch( Exception e )
        {
            DebugOut.println( 0, "*** ERROR:    COULDN'T SAVE ERROR LOG." );
            e.printStackTrace( DebugOut.out() );
            DebugOut.println( 0, newLog.toString() );
        }

        return newLog;
        /** ensure [valid_result] Result != null;       **/
    }



    public static String getStackTrace( Throwable anException ) {
        StringWriter stringifier = new StringWriter();
        PrintWriter printifier = new PrintWriter( stringifier );

        anException.printStackTrace( printifier );

        return stringifier.toString();
    }



    /**
     * Examines <code>anException</code> and unwraps it if it is an NSForwardException.
     *
     * @param anException any exception which may be an NSForwardException
     * @return anException or, if it was an NSForwardException, the exception it
     * was wrapping.
     */
    public static Throwable originalException(Throwable anException)
    {
        /** require [valid_exception] anException != null;  **/

        while (anException instanceof NSForwardException)
        {
            anException = ((NSForwardException)anException).originalException();
        }

        return anException;
        /** ensure [valid_result] Result != null;
                   [valid_exception] ! (Result instanceof NSForwardException);  **/
    }
}
