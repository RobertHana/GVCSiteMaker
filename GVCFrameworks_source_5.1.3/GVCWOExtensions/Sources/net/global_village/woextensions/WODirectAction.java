package net.global_village.woextensions;

import java.lang.reflect.*;

import org.apache.log4j.*;

import com.webobjects.appserver.*;
import com.webobjects.appserver._private.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * This class includes some convenience methods and a bug fix for WODirectAction.
 * This class must be used with GVC's WOApplication
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class WODirectAction extends com.webobjects.appserver.WODirectAction
{

    public static final String DESTINATION_KEY = "destination";

    private final Logger logger = LoggerFactory.makeLogger();


    /**
     * Designated constructor.
     *
     * @param aRequest the request by the user on the server/app
     */
    public WODirectAction(WORequest aRequest)
    {
        super(aRequest);
    }



    /**
     * Hack to prevent IE from crashing app. If the direct action requested is for a 'favicon.ico' then a DBC error will be raised.
     * See http://www.netmechanic.com/news/vol3/server_no1.htm for details.
     *
     * @param anActionName Name of the action to perform
     * @return the result of performing the action (the resulting page)
     */
    public WOActionResults performActionNamed(String anActionName)
    {
        /** require
        [valid_param] anActionName != null;
        [action_name_is_nonzero_length] anActionName.length() > 0; **/

        WOActionResults result;

        if (anActionName.equals("favicon.ico"))
        {
            result = null;
        }
        else
        {
            result = super.performActionNamed(anActionName);
        }

        return result;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * A convenience method so that defaultAction does not need to be overridden.
     *
     * @return the default page as defined in WOApplication's defaultPageName()
     */
    public WOActionResults defaultAction()
    {
        return pageWithName(application().defaultPageName());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns WORedirect to home page with orginalURL as destination form value.  It is
     * up to the login to sense this value and redirect after authentication.
     *
     * @return WORedirect to home page with orginalURL as destination form value
     */
    protected WOActionResults redirectToLogin()
    {
        String originalUrl = "http://" + RequestUtilities.hostNameFromRequest(request()) +
            RequestUtilities.originalURLFromRequest(request());
        String loginUrl = application().frontDoorURL();
        String fullUrl = loginUrl + "?" + DESTINATION_KEY + "=" + WOURLEncoder.encode(originalUrl, "UTF-8");
        WORedirect redirect = (WORedirect)pageWithName(WORedirect.class.getName());
        redirect.setUrl(fullUrl);

        return redirect;
        /** ensure [redirect_returned] Result != null;  **/
    }



    /**
     * Tells the application to terminate.  This is only intended for use during development and testing.
     * The application is only terminated if <code>testingDirectActionsAreEnabled()</code> returns
     * <code>true</code>.  Otherwise the default action is performed
     *
     * @see net.global_village.woextensions.WODirectAction#defaultAction()
     * @see #testingDirectActionsAreEnabled()
     *
     * @return static HTML or default action
     */
    public WOActionResults shutdownAction()
    {
         if (testingDirectActionsAreEnabled())
        {
            WOApplication application = (WOApplication)WOApplication.application();
            logger.warn("Application terminated by call to shutdown direct action");
            application._terminateFromMonitor();

            WOResponse response = new WOResponse();
            response.appendContentString("<html><body>");
            response.appendContentString(application.name());
            response.appendContentString(", instance ");
            response.appendContentString(new Integer(request().applicationNumber()).toString());
            response.appendContentString(", port ");
            response.appendContentString(application.port().toString());
            response.appendContentString(" terminated on request at ");
            response.appendContentString(new NSTimestampFormatter("%Y-%m-%d %H:%M:%S").format(new NSTimestamp()));
            response.appendContentString(".</body></html>");
            response.setHeader("text/html", "content-type");

            return response;
        }

         logger.error("Shutdown direct action called, but disabled");
        return defaultAction();
    }



    /**
     * Mimics JavaMonitor telling the application to terminate.  This is only intended for use during development and testing.
     * The application is only terminated if <code>testingDirectActionsAreEnabled()</code> returns <code>true</code>.
     * Otherwise the default action is performed
     *
     * @see net.global_village.woextensions.WODirectAction#defaultAction()
     * @see #testingDirectActionsAreEnabled()
     *
     * @return static HTML or default action
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public WOActionResults terminateFromMonitorAction()
    {
         if (testingDirectActionsAreEnabled())
        {
            logger.warn("terminateFromMonitor direct action called");
            try
            {
                Method _terminateFromMonitor = com.webobjects.appserver.WOApplication.class.getDeclaredMethod("_terminateFromMonitor", new Class[] {});
                _terminateFromMonitor.setAccessible(true);
                _terminateFromMonitor.invoke(WOApplication.application(), new Object[]{});
            }
            catch (Exception e)
            {
                logger.error("Error calling _terminateFromMonitor", e);
                return defaultAction();
            }

            WOResponse response = new WOResponse();
            response.appendContentString("<html><body>");
            response.appendContentString(WOApplication.application().name());
            response.appendContentString(" terminated by JavaMonitor.</body></html>");
            response.setHeader("text/html", "content-type");
            return response;
        }

         logger.error("Shutdown direct action called, but disabled");
        return defaultAction();
    }



    /**
     * Mimics JavaMonitor telling the application to refuse new sessions.  This is only intended for use during development and testing.
     * Sessions are refused only if <code>testingDirectActionsAreEnabled()</code> returns <code>true</code>.
     * Otherwise the default action is performed
     *
     * @see net.global_village.woextensions.WODirectAction#defaultAction()
     * @see #testingDirectActionsAreEnabled()
     *
     * @return static HTML or default action
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public WOActionResults refuseNewSessionsAction()
    {
         if (testingDirectActionsAreEnabled())
        {
            logger.warn("refuseNewSessions direct action called");
            WOApplication.application().refuseNewSessions(true);

            WOResponse response = new WOResponse();
            response.appendContentString("<html><body>");
            response.appendContentString(WOApplication.application().name());
            response.appendContentString(" refusing new sessions on request.</body></html>");
            response.setHeader("text/html", "content-type");
            return response;
        }

        logger.error("refuseNewSessions direct action called, but disabled");
        return defaultAction();
    }



    /**
     * Returns value of <code>TestingDirectActionsEnabled</code> property.
     *
     * @return <code>true</code> if the direct actions for testing are enabled
     */
    protected boolean testingDirectActionsAreEnabled()
    {
        if ( ! application().properties().hasPropertyForKey(net.global_village.woextensions.WOApplication.TestingDirectActionsEnabled))
        {
            return false;
        }
        return application().properties().booleanPropertyForKey(net.global_village.woextensions.WOApplication.TestingDirectActionsEnabled);
    }



    /**
     * @return WOApplication.application() downcast to net.global_village.woextensions.WOApplication
     */
    public net.global_village.woextensions.WOApplication application()
    {
        return  (net.global_village.woextensions.WOApplication) com.webobjects.appserver.WOApplication.application();
    }



}
