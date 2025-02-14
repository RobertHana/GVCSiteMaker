package com.gvcsitemaker.appserver;

import java.util.Date;

import org.sakaiproject.portlets.IMSLTIUtil;

import com.gvcsitemaker.core.RemoteParticipantGroup;
import com.gvcsitemaker.core.User;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.components.PageScaffold;
import com.gvcsitemaker.core.support.SectionDisplay;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSTimestamp;

import net.global_village.woextensions.Response;
import net.global_village.woextensions.WODirectAction;


/**
 * A degenerate direct action class to handle Tools Interoperability Launch URLs.  These URLs can't
 * have GET params (after the "?") on the URL, since the TI spec does a POST, so we need
 * to create URLs that can pass the site name without a GET.  The resulting URL looks like:
 * .../WebObjects/GVCSiteMaker.woa/wa/TILaunch/<site id>
 *
 * @author Copyright (c) 2008  Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 4$
 */
public class TILaunch extends WODirectAction
{
	public static final long MAX_REQUEST_TIME_IN_MS = 100000;


    /**
     * Designated constructor.
     *
     * @param aRequest the request by the user on the server/app
     */
    public TILaunch(WORequest aRequest)
    {
        super(aRequest);
    }



    /**
     * Processes a Tools Interoperability Launch Request for access to a GVC.SiteMaker site.
     * 
     * @see PageScaffold
     *
     * @param anActionName name of the action to perform
     * @return XML response to Tools Interoperability Launch Request
     */
    public WOActionResults performActionNamed(String anActionName)
    {
        /** require
        [valid_param] anActionName != null;
        [action_name_is_nonzero_length] anActionName.length() > 0; **/

    	// need to make sure this never, ever raises
    	try
    	{
	        // There should never, ever be an existing session
	        if (existingSession() != null)
	        {
	            return failureResult("Inconsistent state: there should be no existing session for this request", "InvalidLaunchData");
	        }

	        // Create the session for this launch request and mark it as a remote participation session so that
	        // additional security checks can be made
	        smSession().setIsRemoteParticipantSession();

	        String xWsse = request().headerForKey("x-wsse");

	        int usernameTokenStartIndex = xWsse.indexOf("UsernameToken Username=\"");
	        int usernameTokenEndIndex = xWsse.indexOf("\"", usernameTokenStartIndex + 24);
	        String usernameToken = xWsse.substring(usernameTokenStartIndex + 24, usernameTokenEndIndex);

	        int passwordDigestStartIndex = xWsse.indexOf("PasswordDigest=\"");
	        int passwordDigestEndIndex = xWsse.indexOf("\"", passwordDigestStartIndex + 16);
	        String passwordDigest = xWsse.substring(passwordDigestStartIndex + 16, passwordDigestEndIndex);

	        int nonceStartIndex = xWsse.indexOf("Nonce=\"");
	        int nonceEndIndex = xWsse.indexOf("\"", nonceStartIndex + 7);
	        String nonce = xWsse.substring(nonceStartIndex + 7, nonceEndIndex);

	        int createdStartIndex = xWsse.indexOf("Created=\"");
	        int createdEndIndex = xWsse.indexOf("\"", createdStartIndex + 9);
	        String created = xWsse.substring(createdStartIndex + 9, createdEndIndex);

	        if (DebugOut.debugLevel() >= 20)
	        {
	        	System.out.println("FORM VALUES: " + request().formValues());
	        	System.out.println("QUERY STRING: " + request().queryString());
	        	System.out.println("CONTENT STRING: " + request().contentString());
	        	System.out.println("HEADER X_WSSE: " + xWsse);
	        	System.out.println("  username token: " + usernameToken);
	        	System.out.println("  passwordDigest: " + passwordDigest);
	        	System.out.println("  nonce: " + nonce);
	        	System.out.println("  created: " + created);
	        }


	        String launchData = request().contentString();
	        // Validate the request
	        // TODO Use Digester instead of nasty substr hacks
	        if (launchData == null)
	        {
	            return failureResult("No launchData provided", "InvalidLaunchData");
	        }
	        if (launchData.indexOf("<eid>") == -1)
	        {
	            return failureResult("No eid provided", "InvalidLaunchData");
	        }
	        if (launchData.indexOf("</eid>") == -1)
	        {
	            return failureResult("Malformd XML, eid not terminated", "InvalidLaunchData");
	        }
	        if (launchData.indexOf("<role>") == -1)
	        {
	            return failureResult("No role provided", "InvalidLaunchData");
	        }
	        if (launchData.indexOf("</role>") == -1)
	        {
	            return failureResult("Malformd XML, role not terminated", "InvalidLaunchData");
	        }

	        if (anActionName == null)
	        {
	            return failureResult("No site ID provided", "InvalidLaunchData");
	        }

	        // Extract needed values from XML and validate
	        String userID = launchData.substring(launchData.indexOf("<eid>") + "<eid>".length(),
                    launchData.indexOf("</eid>"));
	        if (User.validateUserID(userID).count() > 0)
	        {
	            return failureResult("Invalid User ID format", "InvalidLaunchData");
	        }

	        String role = launchData.substring(launchData.indexOf("<role>") + "<role>".length(),
                    launchData.indexOf("</role>"));
	        if (role == null)
	        {
	            return failureResult("Role was not specified", "InvalidLaunchData");
	        }

	        DebugOut.println(20, "  eid: " + userID);
	        DebugOut.println(20, "  role: " + role);


	        // Find the requested site
	        EOEditingContext ec = ((Session)session()).defaultEditingContext();
	        Website site = Website.websiteForSiteID(anActionName, ec);
	        if (site == null)
	        {
	            return failureResult("Site ID invalid", "InvalidLaunchData");
	        }

	        // Authenticate the request
	        RemoteParticipantGroup group = site.remoteParticipantGroup();
	        if (group == null)
	        {
	            return failureResult("Inconsistent Remote Participant Group", "InvalidLaunchData");
	        }
	        String calculatedDigest = IMSLTIUtil.BASE64SHA1(nonce + created + group.secret());
	        DebugOut.println(20, "calculated Digest: " + calculatedDigest);
	        if (! calculatedDigest.equals(passwordDigest))
	        {
	            return failureResult("Password digest did not match", "BadPasswordDigest");
	        }

	        Date authDate = IMSLTIUtil.parseISO8601(created);
    		if (authDate == null)
	        {
	            return failureResult("Failed to parse the created date", "BadPasswordDigest");
	        }
	        if (Math.abs(net.global_village.foundation.Date.timeIntervalFromNowUntil(new NSTimestamp(authDate))) > MAX_REQUEST_TIME_IN_MS)
	        {
	        	return failureResult("The request created date is not within the acceptable range", "BadPasswordDigest");
	        }


	        // Fetch or create user and initialize session
	        User remoteUser = User.userForUserID(userID, session().defaultEditingContext());
	        if (remoteUser == null)
	        {
	            remoteUser = User.createUser(session().defaultEditingContext(), userID);
	        }
	        smSession().setAuthenticatedRemoteParticipant(remoteUser, group, role.equals("Instructor"));

	        // This response goes back to the tool which will then open the URL in a frameset.  Because of this
	        // the session values passed back in cookies are discarded.  They get appended to the URL here for the 
	        // initial request from the frameset.  After that, these values will be in the cookies.
	        StringBuffer sessionValues = new StringBuffer("&amp;");
	        sessionValues.append("wosid=");
	        sessionValues.append(session().sessionID());
	        sessionValues.append("&amp;");
	        sessionValues.append("woinst=");
	        sessionValues.append(new Integer(request().applicationNumber()));

	        // Include optional style override from RemoteParticipantGroup
	        if (group.sectionStyle() != null)
	        {
	            sessionValues.append("&amp;");
	            sessionValues.append(SectionDisplay.SECTION_STYLE_OVERRIDE_FLAG);
	            sessionValues.append("=");
	            sessionValues.append(group.sectionStyle().styleID());
	        }

	        return successResult(site.siteURL(request()) + sessionValues.toString());
    	}
    	catch (Throwable e)
    	{
    		System.out.println("ERROR: TILaunch threw an unexpected exception: ");
    		e.printStackTrace();
            return failureResult("TILaunch threw an unexpected exception", "InvalidLaunchData");
    	}

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Creates a Success result for a Tools Interoperability Launch Request for access to a GVC.SiteMaker site.
     * 
     * @param launchUrl the URL to pass back to the tool that requested the launch
     * 
     * @return a Success XML response to a Tools Interoperability Launch Request
     */
    public WOActionResults successResult(String launchUrl)
    {
        // TODO use a real XML tool to generate this
        StringBuffer response = new StringBuffer();
        response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        response.append("<launchUrl>");
        response.append(launchUrl);
        response.append("</launchUrl>\n");

        WOResponse woResponse = new Response(context());
        woResponse.setContent(response.toString());

        return woResponse;
    }



    /**
     * Creates a Failure result for a Tools Interoperability Launch Request for access to a GVC.SiteMaker site.
     * 
     * @param reason message to pass back to the tool that requested the launch
     * @param code error code to pass back to the tool that requested the launch
     * 
     * @return a Failure XML response to a Tools Interoperability Launch Request
     */
    public WOActionResults failureResult(String reason, String code)
    {
    	DebugOut.println(20, "TI failure: " + reason);

    	// TODO use a real XML tool to generate this
        StringBuffer response = new StringBuffer();
        response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        response.append("<ltierror>\n");
        response.append("<code>" + code + "</code>\n");
        response.append("<description>" + reason + "</description>\n");
        response.append("</ltierror>\n");

        WOResponse woResponse = new Response(context());
        woResponse.setContent(response.toString());

        return woResponse;
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
