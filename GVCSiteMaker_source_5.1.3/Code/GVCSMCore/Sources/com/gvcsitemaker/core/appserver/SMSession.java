// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.appserver;

import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;


/**
 * <p>Common Session functionality for all GVCSiteMaker applications.  Currently, this provides:</p>
 * <ol>
 * <li>recording the user and whether they are authenticated or not</li>
 * <li>recently visited websites functionality</li>
 * <li>vending editing contexts and managing their locking</li>
 * </ol>
 */
public class SMSession extends net.global_village.woextensions.WOSession implements MultiECLockManager.Session
{
    protected User currentUser;
    // The user currently associated with this session.  May be public (unathenticated) user
    protected boolean isUserAuthenticated; // True if currentUser() has logged in (not public user)
    protected Group internalUsersGroup; // User group for all internal users
    protected Group publicGroup; // Public access user group.

    // Properties for tracking recently visited sites (RVS).
    protected NSMutableArray recentlyVisitedSiteIDs; // List of siteIDs of RVS
    static final public String RecentSitesCookieName = "GVCSiteMakerRecentlyVisitedSites";
    // Name of cookie uses to store RVS
    static final public String RecentSiteDelimiter = "/";
    // Delimiter between siteIDs in RVS cookie

    // For sessions created by a Tools Interoperability launch request
    private boolean isRemoteParticipantSession = false;
    private boolean isRemoteAdministrator = false;
    private RemoteParticipantGroup authenticatedRemoteParticipantGroup;

    /** The website being edited, or null if none */
    protected Website editingWebsite;

    /** Holds a dictionary of dictionaries that will allow pages to restore the
     *  view they had before the user navigated away.
     *  The general structure should be:  page name -> editing object -> view data */
    protected NSMutableDictionary windowViews = new NSMutableDictionary();


    /**
     * Designated constructor.
     */
    public SMSession()
    {
        super();

        // We use cookies to store session and instance number.
        setStoresIDsInURLs(false);
        setStoresIDsInCookies(true);

        // Start the session off with the public (unauthenticated) user.  This user will be replaced with an authenticated user when the log in.
        setIsUserAuthenticated(false);
    }



    /**
     * Overriden to perform auto-login of externally authenticated users and to provide log messages
     * and to lock any editing context's registered with the lock manager.
     */
    public void awake()
    {
        super.awake();

        try {
            DebugOut.println(1, sessionID() + ", User: " + currentUser().userID());

            if (SMApplication.appProperties().booleanPropertyForKey(SMApplication.UsesExternalAuthenticationKey))
            {
                String userIDFromExternalAuthentication = context().request().headerForKey(
                    SMApplication.appProperties().propertyForKey(SMApplication.ExternalAuthenticationUserHeaderKey));
                DebugOut.println(1, sessionID() + ", UserID from external authentication: " +
                    userIDFromExternalAuthentication);

                if (! StringAdditions.isEmpty(userIDFromExternalAuthentication))
                {
                    userIDFromExternalAuthentication = User.canonicalUserID(userIDFromExternalAuthentication);

                    // Check for hacks and forgeries if we already have an authenticated user
                    if (isUserAuthenticated())
                    {
                        if (userIDFromExternalAuthentication.equalsIgnoreCase(currentUser.userID()))
                        {
                            // same user as before, all is good
                        }
                        else
                        {
                            // The user ID changed.  That is bad.  Shut it down.
                            DebugOut.println(1, "*** User identification changed");
                            DebugOut.println(1, "    Previous ID "  + currentUser.userID());
                            DebugOut.println(1, "    new ID "  + userIDFromExternalAuthentication);
                            logout(context().response());
                            throw new RuntimeException("Switch of authenticated user ID detected.");
                        }
                    }
                    // We don't yet have an authenticated user, so fetch or create one.
                    else
                    {
                        User userFromHeaders = User.userForUserID( userIDFromExternalAuthentication,
                                                                   defaultEditingContext() );
                        // Create User on demand
                        if ( userFromHeaders == null )
                        {
                            DebugOut.println(1, "Creating externally authenticated user " +
                                userIDFromExternalAuthentication);
                            NSArray userIDErrors = User.validateUserID(userIDFromExternalAuthentication);
                            if (userIDErrors.count() == 0)
                            {
                                userFromHeaders = User.createUser(defaultEditingContext(), userIDFromExternalAuthentication);
                            }
                            else
                            {
                                DebugOut.println(1, "*** failed to create user; externally supplied ID: "
                                    + userIDFromExternalAuthentication);
                                logout(context().response());
                                throw new RuntimeException("Could not create user with userID: "
                                    + userIDFromExternalAuthentication);
                            }
                        }
                        DebugOut.println(1, "Session assigned to externally authenticated user " +
                            userIDFromExternalAuthentication);
                        setCurrentAuthenticatedUser(userFromHeaders);
                    }
                }
            }
        } catch (RuntimeException e) {
            // The only thing that might happen here is the the DB being down.  Don't throw so we don't
            // deadlock the session.
            DebugOut.println(1, "Session.awake threw: " + e);
            e.printStackTrace(DebugOut.out());
        }
    }



    /**
     * Overridden to redirect to HTTPS if HTTP request made.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        if (needsSecureConnection(context.page()) &&
            ! RequestUtils.isHTTPSRequest(context.request()))
        {
            SMActionURLFactory.redirectToSecureMode(response, context);
        }
        else
        {
            super.appendToResponse(response, context);
        }
    }



    /**
     * Overridden to take no action if HTTP request made.
     */
    public WOActionResults invokeAction(WORequest request, WOContext context)
    {
        if (needsSecureConnection(context.page()) &&
            ! RequestUtils.isHTTPSRequest(context.request()))
        {
            return context.page();
        }

        return super.invokeAction(request, context);
    }



    /**
     * Overridden to take no action if HTTP request made.
     */
    public void takeValuesFromRequest(WORequest request, WOContext context)
    {
        if (needsSecureConnection(context.page()) &&
            ! RequestUtils.isHTTPSRequest(context.request()))
        {
            // Skip this phase
        }
        else
        {
            super.takeValuesFromRequest(request, context);
        }
    }



    /**
     * Return <code>true</code> if component needs a secure (https) connection as indicated by
     * implementing <code>SMSecurePage</code>.  Null components are allowed in case context().page()
     * returns null.
     *
     * @param component the component to determine if a secure connection is needed for.
     * @return <code>true</code> if component needs a secure (https) connection
     */
    public boolean needsSecureConnection(WOComponent component)
    {
        return (component != null) && (component instanceof SMSecurePage);
    }



    /**
     * Method to handle terminating session upon logout (forced or requested).
     */
    public void logout(WOResponse response)
    {
        // If using external authentication and there is a cookie name specified, expire the cookie
        if (SMApplication.appProperties().booleanPropertyForKey(SMApplication.UsesExternalAuthenticationKey)
            && (SMApplication.appProperties().hasPropertyForKey(SMApplication.ExternalAuthenticationCookieKey)))
        {
            WOCookie loginCookie = new WOCookie(
                SMApplication.appProperties().stringPropertyForKey(SMApplication.ExternalAuthenticationCookieKey),
                ".", "/", null, 0, true);
            response.addCookie(loginCookie);
        }

        // SCR 1789 Try to avoid deadlock when requests come in for a manually terminated session
        setTimeOut(SMApplication.appProperties().intPropertyForKey("SessionTerminateTimeOutInSeconds"));

        // This prevents WO from returning a cookie from the soon to be expired session
        // and instructs the browser to discard the cookie that it has for the session
        setStoresIDsInCookies(false);
        WOCookie loginCookie = new WOCookie("wosid", ".", "/", null, 0, true);
        response.addCookie(loginCookie);

        defaultEditingContext().revert();
    }




    /**
     * Despite the name, this is actually the path used in the cookies for the session ID and instance number.  Returning just the root '/' ensures that the cookies are always sent for any request to the site.  This was needed so that they are sent with URLs which will be rewritten (e.g. http://www.sitemaker.com/MySite).  Note that super.domainForIDCookies() would return: /cgi-bin/WebObjects/SiteMaker.woa
     */
    public String domainForIDCookies()
    {
        return "/";
    }



    /**
     * Sets the current user to be an authenticated user.  This should ONLY be called after a successful login.
     *
     * @param aUser the User to record as being logged into this session
     */
    public void setCurrentAuthenticatedUser(User aUser)
    {
        /** require [a_user_not_null] aUser != null;    **/

        if (aUser.isPublicUser())
        {
            throw new RuntimeException("User authentication refused.");
        }

        if (! currentUser().globalID().equals(aUser.globalID()))
        {
            setCurrentUser((User)EOUtilities.localInstanceOfObject(defaultEditingContext(), aUser));
            UsageLog.newUsageLog(currentUser(), defaultEditingContext());
            // self inserting constructor!

            setIsUserAuthenticated(true);

            // Increase session timeout for logged in users
            setTimeOut(60.0 * SMApplication.appProperties().intPropertyForKey(
                "AuthenticatedUserSessionTimeOutInMinutes"));
            DebugOut.println(18, "TimeOut increased to " + timeOut() + " seconds.");

            defaultEditingContext().saveChanges();
        }

        /** ensure
        [current_user_not_null] currentUser() != null;
        [user_authenticated] isUserAuthenticated(); **/
    }



    /**
     * Sets the current user but does not affect authentication status.
     *
     * @param aUser the User to associate with this session
     */
    protected void setCurrentUser(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/

        currentUser = aUser;

        /** ensure [current_user_not_null] currentUser() != null; **/
    }



    /**
     * Returns an NSArray of the siteIDs of the most recently visited Websites in order of when they were visited (most recent first).  The list is taken directly from a cookie in the request associated with the passed context.
     *
     * @return an NSArray of the siteIDs of the most recently visited Websites in order of when they were visited (most recent first)
     */
    protected void getRecentlyVisitedSiteIDs(WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        // Break the concatenated IDs into an array.
        String recentlyVisitedSiteIDString =
            aContext.request().cookieValueForKey(RecentSitesCookieName);

        // Handle a missing list (first time visit, cookies disabled etc.).
        recentlyVisitedSiteIDs =
            (recentlyVisitedSiteIDString != null)
                ? new NSMutableArray(
                    SMStringUtils.arrayFromStringWithSeparator(
                        URLUtils.urlDecode(recentlyVisitedSiteIDString),
                        RecentSiteDelimiter))
                : new NSMutableArray();
        DebugOut.println(1, "Got recent sites : " + recentlyVisitedSiteIDs());

        /** ensure [recently_visited_site_ids_not_null] recentlyVisitedSiteIDs() != null; **/
    }



    /**
     * Returns an NSArray of the siteIDs of the most recently visited Websites in order of when they were visited (most recent first).  This is only valid after calling getRecentlyVisitedSiteIDs(WOContext)
     *
     * @return an NSArray of the siteIDs of the most recently visited Websites in order of when they were visited (most recent first)
     */
    protected NSMutableArray recentlyVisitedSiteIDs()
    {
        /** require [recently_visited_site_ids_not_null] recentlyVisitedSiteIDs != null; **/

        return recentlyVisitedSiteIDs;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns an NSArray of the most recently visited Websites in order of when they were visited (most recent first).  The list is taken directly from a cookie in the request associated with the passed context.  Obsolete websites (ones that no longer exist) are removed from the list.   An updated cookie of the recently visted sites is added to the response so the list is persistent.  This method will fail if aContext.response() != null (not sure why this happens, but I've seen it once.  -ch)
     *
     * @return an NSArray of the most recently visited Websites in order of when they were visited (most recent first)
     */
    public NSArray recentlyVisitedWebsites(WOContext aContext)
    {
        /** require
        [a_context_not_null] aContext != null;
        [a_context_response_not_null] aContext.response() != null; **/

        NSMutableArray recentlyVisitedWebsites = new NSMutableArray();

        // Enumerate a copy as we need to alter the original
        getRecentlyVisitedSiteIDs(aContext);
        NSArray copyOfRecentlyVisitedSiteIDs = new NSArray(recentlyVisitedSiteIDs());
        Enumeration siteIDEnumerator = copyOfRecentlyVisitedSiteIDs.objectEnumerator();
        while (siteIDEnumerator.hasMoreElements())
        {
            String aSiteID = (String) siteIDEnumerator.nextElement();
            Website aWebsite = Website.websiteForSiteID(aSiteID, defaultEditingContext());
            if (aWebsite != null)
            {
                // The site still exists, so add it to the most recently visited list.
                recentlyVisitedWebsites.addObject(aWebsite);
            }
            else
            {
                // The site no longer exists, so remove it from the most recently visited list.
                recentlyVisitedSiteIDs().removeObject(aSiteID);
            }
        }

        // Send a cookie with the updated list back to the browser.
        aContext.response().addCookie(recentlyVisitedWebsitesCookie());

        return recentlyVisitedWebsites;

        /** ensure
        [result_not_null] Result != null;
        [expected_number_of_websites] Result.count() == recentlyVisitedSiteIDs().count(); **/
    }



    /**
     * Adds a Website to the list of most recently visted Websites as the site most recently visited.  If this site is already in the list, it is moved to the start of the list.  If the list is longer than the number of recently visited sites to record, the least recently visited site is removed.  An updated cookie of the recently visted sites is added to the response so the list is persistent.  This method will fail if aContext.response() != null (not sure why this happens, but I've seen it once.  -ch)
     *
     * @param aWebsite the Website to add to the list of most recently visted Websites as the site most recently visited
     */
    public void recordVisitToWebsite(Website aWebsite, WOContext aContext)
    {
        /** require
        [a_website_not_null] aWebsite != null;
        [a_context_not_null] aContext != null;
        [a_context_response_not_null] aContext.response() != null; **/

        // Number of sites to record before rollover
        int maxWebsitesInRecentlyVisitedList =
            SMApplication.appProperties().intPropertyForKey("NumberOfVisitedSitesToList");

        getRecentlyVisitedSiteIDs(aContext);

        DebugOut.println(1, "Recording visit to site : " + aWebsite.siteID());
        // Remove the site from the list if it is already there.  It will then be added back at the start of the list.
        if (recentlyVisitedSiteIDs().containsObject(aWebsite.siteID()))
        {
            recentlyVisitedSiteIDs().removeObject(aWebsite.siteID());
        }

        // Add at the start of the list so that older entries fall off of the end.
        recentlyVisitedSiteIDs().insertObjectAtIndex(aWebsite.siteID(), 0);

        // Trim the list if it exceeds the maximum length.
        if (recentlyVisitedSiteIDs().count() > maxWebsitesInRecentlyVisitedList)
        {
            NSRange mostRecent = new NSRange(0, maxWebsitesInRecentlyVisitedList);
            recentlyVisitedSiteIDs =
                new NSMutableArray(recentlyVisitedSiteIDs().subarrayWithRange(mostRecent));
        }

        // Send a cookie with the updated list back to the browser.
        aContext.response().addCookie(recentlyVisitedWebsitesCookie());

        /** ensure
        [correct_most_recently_visited_site] recentlyVisitedSiteIDs().objectAtIndex(0).equals(aWebsite.siteID());
        [not_too_many_sites_listed] recentlyVisitedSiteIDs().count() <= SMApplication.appProperties().intPropertyForKey("NumberOfVisitedSitesToList"); **/
    }



    /**
     * Returns a WOCookie recording the most recently visited Websites.  The cookie is set to expire two years in the future.
     *
     * @return a WOCookie recording the most recently visited Websites
     */
    public WOCookie recentlyVisitedWebsitesCookie()
    {
        DebugOut.println(1, "Making cookie for : " + recentlyVisitedSiteIDs());

        // The delimeter we are using is legal in cookie values, but we do this in case it becomes problem or the delimiter is changed.
        String encodedCookie =
            URLUtils.urlEncode(
                recentlyVisitedSiteIDs().componentsJoinedByString(RecentSiteDelimiter));

        WOCookie recentSitesCookie = new WOCookie(RecentSitesCookieName, encodedCookie);
        recentSitesCookie.setDomain(null); // Let the browser set the domain
        recentSitesCookie.setPath("/"); // Return for all paths in this domain

        NSTimestamp expiryDate;
        if (recentlyVisitedSiteIDs().count() == 0)
        {
            // There are no recently visited sites, set the expiry date so that the cookie will be erased.
            expiryDate = (new NSTimestamp()).timestampByAddingGregorianUnits(-10, 0, 0, 0, 0, 0);
            recentSitesCookie.setValue("X"); // Make the cookie conform the the specification
        }
        else
        {
            // There are recently visited sites, keep the list around for two years in the future
            expiryDate = (new NSTimestamp()).timestampByAddingGregorianUnits(2, 0, 0, 0, 0, 0);
        }

        recentSitesCookie.setExpires(expiryDate);

        return recentSitesCookie;

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns true if this session was created in response to a Tools Interoperability Launch Request.  See tiLaunchAction()
     * in DirectAction.
     *
     * @return <code>true</code> if this session was created in response to a Tools Interoperability Launch Request
     */
    public boolean isRemoteParticipantSession()
    {
        return isRemoteParticipantSession;
    }



    /**
     * Call this if this session was created in response to a Tools Interoperability Launch Request.  Once set,
     * this can't be changed
     */
    public void setIsRemoteParticipantSession()
    {
        isRemoteParticipantSession = true;
        /** ensure [isRemoteParticipantSession] isRemoteParticipantSession();  **/
    }



    /**
     * Returns the RemoteParticipantGroup that they user was authenticated to from a Tools Interoperability Launch Request.
     * Will return null if not isRemoteParticipantSession() or if the session was created from a Tools Interoperability Launch Request
     * but the request failed authentication.
     *
     * @return the authenticatedRemoteParticipantGroup
     */
    public RemoteParticipantGroup authenticatedRemoteParticipantGroup()
    {
        return authenticatedRemoteParticipantGroup;
        /** ensure [valid_response] isRemoteParticipantSession() || Result == null;  **/
    }



    /**
     * Returns true iff the user is a remote participant that has the Administrator role.
     *
     * @return true iff the user is a remote participant that has the Administrator role
     */
    public boolean isRemoteAdministrator()
    {
        return isRemoteAdministrator;
    }



    /**
     * Sets the User and the RemoteParticipantGroup that they user was authenticated to from a Tools Interoperability
     * Launch Request. Setting this indicates that the user has been authenticated.
     *
     * @param user the User who was authenticated
     * @param group the authenticatedRemoteParticipantGroup to set
     */
    public void setAuthenticatedRemoteParticipant(User user, RemoteParticipantGroup group, boolean isRemoteAdmin)
    {
        /** require
        [valid_user] user != null;
        [isRemoteParticipantSession] isRemoteParticipantSession();
        [valid_group] group != null; **/

    	setCurrentAuthenticatedUser((User) EOUtilities.localInstanceOfObject(defaultEditingContext(), user));
        authenticatedRemoteParticipantGroup = (RemoteParticipantGroup) EOUtilities.localInstanceOfObject(defaultEditingContext(), group);
        isRemoteAdministrator = isRemoteAdmin;
    }



    /* Generic setters and getters ***************************************/
    public User currentUser() {
        if (currentUser == null)
        {
            setCurrentUser(PublicUser.publicUser(defaultEditingContext()));
        }
        return currentUser;
    }

    public boolean isUserAuthenticated() {
        return isUserAuthenticated;
    }
    public void setIsUserAuthenticated(boolean value) {
        isUserAuthenticated = value;
    }

    public Group internalUsersGroup() {
        if (internalUsersGroup == null)
        {
            internalUsersGroup = InternalUsersGroup.group(defaultEditingContext());
        }

        return internalUsersGroup;
    }

    public Group publicGroup(){
        if (publicGroup == null)
        {
            publicGroup = PublicGroup.group(defaultEditingContext());
        }
        return publicGroup;
    }



    /**
     * Returns the website the user is editing, or null if the user is not editing a website.
     *
     * @return the website the user is editing, or null if the user is not editing a website
     */
    public Website editingWebsite()
    {
        return editingWebsite;
    }


    /**
     * Sets the editing website to website.
     *
     * @param website the website to set as being edited
     */
    public void setEditingWebsite(Website website)
    {
        editingWebsite = website;
    }



    /**
     * Returns the mutable dictionary that contains the view data for the session.
     *
     * @return the mutable dictionary that contains the view data for the session
     */
    public NSMutableDictionary windowViews()
    {
        return windowViews;
    }


    /**
     * Returns the mutable dictionary that contains the view data for the given
     * page name and editing object.
     *
     * @param pageName the page name to return the view data for
     * @param editingObject the editing object to return the view data for
     * @return the mutable dictionary that contains the view data for the given page name and editing object
     */
    public NSMutableDictionary viewDataForPageNameAndEditingObject(String pageName, Object editingObject)
    {
        /** require
        [valid_pageName] pageName != null;
        [valid_editingObject] editingObject != null; **/

        NSMutableDictionary viewData = null;

        NSDictionary editingObjectToViewDataDictionary = (NSDictionary)windowViews().objectForKey(pageName);
        if (editingObjectToViewDataDictionary != null)
        {
            viewData = (NSMutableDictionary) editingObjectToViewDataDictionary.objectForKey(editingObject);
        }

        if (viewData == null)
        {
            viewData = new NSMutableDictionary();
            setViewDataForPageNameAndEditingObject(pageName, editingObject, viewData);
        }

        return viewData;

        /** ensure [nonnull_result] Result != null; **/
    }


    /**
     * Returns the mutable dictionary that contains the view data for the given
     * page name and editing object.
     *
     * @param pageName the page name to set the view data for
     * @param editingObject the editing object to set the view data for
     * @param viewData the view data to set
     * @return the mutable dictionary that contains the view data for the given page name and editing object
     */
    public void setViewDataForPageNameAndEditingObject(String pageName, Object editingObject, NSDictionary viewData)
    {
        /** require
        [valid_pageName] pageName != null;
        [valid_editingObject] editingObject != null;
        [valid_viewData] viewData != null; **/

        NSMutableDictionary editingObjectToViewDataDictionary = (NSMutableDictionary)windowViews().objectForKey(pageName);
        if (editingObjectToViewDataDictionary == null)
        {
            editingObjectToViewDataDictionary = new NSMutableDictionary();
            windowViews().setObjectForKey(editingObjectToViewDataDictionary, pageName);
        }

        NSMutableDictionary editingObjectDictionary = (NSMutableDictionary) editingObjectToViewDataDictionary.objectForKey(editingObject);
        if (editingObjectDictionary == null)
        {
            editingObjectDictionary = new NSMutableDictionary();
            editingObjectToViewDataDictionary.setObjectForKey(editingObjectDictionary, editingObject);
        }

        editingObjectDictionary.setObjectForKey(viewData, editingObject);
    }



}
