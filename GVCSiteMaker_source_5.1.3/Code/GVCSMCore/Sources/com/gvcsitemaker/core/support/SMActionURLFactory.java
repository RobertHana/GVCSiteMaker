// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import java.io.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;

/**
 * This class generates custom URLS for accessing Sections, SiteFiles, etc. in the GVC.SiteMaker application.
 */
public class SMActionURLFactory extends Object
{
    // These constants are used to generate URLs which match rewrite rules on the way back in
    public static final String SITE_KEY = "site";
    public static final String SECTION_KEY = "section";
    public static final String SECTION_ID_KEY = "sectionID";
    public static final String SECTION_VERSION_ID_KEY = "versionID";
    public static final String URL_KEY = "url";
    public static final String EMBEDDED_SITE_KEY = "embeddedsite";
    public static final String FILE_KEY = "file";
    public static final String FILE_PASSWORD_KEY = "key";
    public static final String COLUMN_NAME_KEY = "fileColumnName";
    public static final String RECORD_ID_KEY = "recordID";
    public static final String COMPONENT_ID_KEY = "componentID";
    public static final String VERSION_NUMBER = "version";
    public static final String SECURE_ID_KEY = "secureID";
    public static final String SHOW_WARNING_KEY = "showWarning";
    public static final String REDIRECT_PATH = "/" + SMApplication.appProperties().stringPropertyForKey("RedirectPath");


    // This is different than SITE_KEY to that it is not included in IGNORED_FORM_VALUES
    public static final String CONFIG_SITE_KEY = "siteID";

    // The form values are encoded in the URL path and should not be duplicated as form values
    public static NSMutableArray IGNORED_FORM_VALUES = new NSMutableArray(new Object[] {
            "site", "section", "embeddedsite", "file", "key", "fileColumnName", "recordID"});



    /**
     * Returns the URL for SiteMaker domain (e.g. http://www.GVCSiteMaker.com).  The rewrite rules will redirect this to the default direct action for SiteMaker.
     *
     * @return the URL for SiteMaker domain (e.g. http://www.GVCSiteMaker.com).
     */
    public static String mainPageURL()
    {
        return insecureProtocol() + domainName();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL for the GVC.SiteMaker administration application.
     *
     * @return the URL for the GVC.SiteMaker administration application.
     */
    public static String adminMainPageURL()
    {
        return secureProtocol() + domainName() + adaptorPath() + "/" +
        SMApplication.appProperties().stringPropertyForKey("AdministrationName");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL for the Manage Pending Website Requests page in the GVC.SiteMaker
     * administration application.
     *
     * @return the URL for the Manage Pending Website Requests page
     */
    public static String managePendingRequestsURL()
    {
        return secureProtocol() + domainName() + adaptorPath() + "/" +
        SMApplication.appProperties().stringPropertyForKey("AdministrationName") +
        ".woa" + directActionFragment() + "managePendingRequests" ;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL for GVC.SiteMaker Site (section) Style Administration.
     *
     * @return the URL for GVC.SiteMaker Site (section) Style Administration.
     */
    public static String siteStyleAdminMainPageURL(WORequest request)
    {
        /** require [request_not_null] request != null; **/

        return secureDirectActionUrl(request, "manageSiteStyles");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the direct action URL for the LoginPage with a parameter of destination which is the URL to show after a successful login.
     *
     * @param request - WORequest from unauthenticated user to create secure login URL for.
     * @param message - optional message to display on login page
     *
     * @return the direct action URL for the LoginPage with a parameter of destination which is the URL to show after a successful login.
     */
    public static String secureLoginURLForRequest(WORequest request, String message)
    {
        /** require [request_not_null] request != null; **/

        String hostName = RequestUtils.hostNameFromRequest(request);
        String requestedURL = RequestUtils.originalURLFromRequest(request);
        boolean useSSL = RequestUtils.isHTTPSRequest(request);

        String url = secureDirectActionUrl(request, "login");

        // Encode original URL so that it can be included in the URL, this includes any form values on the original URL
        url += "?" + SMDirectAction.DESTINATION_KEY + "=" + URLUtils.urlEncode(httpProtocol(useSSL) + hostName + requestedURL);

        // Pass along login message in URL if one is specified.
        if (message != null)
        {
            url += "&message=" + URLUtils.urlEncode(message);
        }

        return url;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to logout and go to destination
     *
     * @param request WORequest to use for logout
     * @param destination the URL to show after logout
     *
     * @return the URL to logout and go to destination
     */
    public static  String logoutURL(WORequest request, String destination)
    {
        /** require [request_not_null] request != null; **/

        String url = directActionUrl(request, "logout");
        // Encode original URL so that it can be included in the URL, this includes any form values on the original URL
        url += "?" + SMDirectAction.DESTINATION_KEY + "=" + URLUtils.urlEncode(destination);

        return url;
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the originally requested URL with a sheme of HTTPS and the REDIRECT_PATH
     * added if not present.
     *
     * @param request - WORequest from HTTP URL
     *
     * @return the originally requested URL with a sheme of HTTPS
    */
    public static String secureURLWithSecureIDForRequest(WORequest aRequest)
    {
        /** require [request_not_null] aRequest != null; **/

        StringBuffer secureURL = new StringBuffer(255);
        secureURL.append(secureProtocol());
        secureURL.append(RequestUtils.hostNameFromRequest(aRequest));

        // Add REDIRECT_PATH only if not already present
        if ( ! RequestUtils.originalURLFromRequest(aRequest).startsWith(REDIRECT_PATH))
        {
            secureURL.append(REDIRECT_PATH);
        }

        secureURL.append(RequestUtils.originalURLFromRequest(aRequest));

        DebugOut.println(3,"Secure URL with secure ID: " + secureURL.toString());
        return secureURL.toString();
        /** ensure [result_not_null] Result != null;  **/
    }



    /**
     * Returns the originally requested URL with a sheme of HTTPS.
     *
     * @param request - WORequest from HTTP URL
     *
     * @return the originally requested URL with a sheme of HTTPS
    */
    public static String secureURLForRequest(WORequest aRequest)
    {
        /** require [request_not_null] aRequest != null; **/

        StringBuffer secureURL = new StringBuffer(255);
        secureURL.append(secureProtocol());
        secureURL.append(RequestUtils.hostNameFromRequest(aRequest));
        secureURL.append(RequestUtils.originalURLFromRequest(aRequest));

        return secureURL.toString();
        /** ensure [result_not_null] Result != null;  **/
    }



    /**
     * Sets response to redirect to originally requested URL but with HTTPS.
     *
     * @param response - the response to change into a redirect
     * @param context - the WOContext to take the original URL from.
     */
    public static void redirectToSecureMode(WOResponse response, WOContext context)
    {
        /** require [valid_response] response != null;  [valid_context] context != null; **/

        DebugOut.println(3,"Request not secure, switching to secure mode");
        WORedirect secureRedirect = new WORedirect(context);
        secureRedirect.setUrl(SMActionURLFactory.secureURLForRequest(context.request()));
        secureRedirect.appendToResponse(response, context);
    }



    /**
     * Returns the URL for a user to change thier password.  The user's ID is optionally included so that they do not need to type it again.
     *
     * @param userID optional userID of the user to change the password for.
     *
     * @return the URL for a user to change thier password.
     */
    public static  String changePasswordURL(WORequest request, String userID)
    {
        /** require [request_not_null] request != null; **/

        String wholePath = secureDirectActionUrl(request, "changePassword");

        if (userID != null)
        {
            wholePath += "?userID=" +userID;
        }

        return wholePath;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL for a user to get their password sent to them.
     *
     * @return the URL for a user to get their password sent to them.
     */
    public static  String sendPasswordURL(WORequest request)
    {
        /** require [request_not_null] request != null; **/

        return secureDirectActionUrl(request, "sendPassword");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating Website URLs using http:// and the domain defined in GVCSiteMaker.plist as DomainName.
     *
     * @param siteName the siteID of the site to create a URL for
     *
     * @return URL to access a SiteMaker Website using http:// and the domain defined in GVCSiteMaker.plist as DomainName.
     */
    public static String siteURL(String siteName)
    {
        /** require [site_name_not_null] siteName != null; **/

        return siteURL(siteName, null);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating Website URLs using http:// and the domain specified in the request.
     *
     * @param siteName the siteID of the site to create a URL for
     * @param aRequest the request to take the domain name from
     *
     * @return URL to access a SiteMaker Website using http:// and the domain specified in the request.
     */
    public static String siteURL(String siteName, WORequest aRequest)
    {
        /** require [site_name_not_null] siteName != null; **/

        return siteURL(aRequest, siteName, insecureProtocol(), (aRequest == null ? domainName() :
                                                      RequestUtils.hostNameFromRequest(aRequest)));

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating Website URLs using http:// and the domain specified in the request.
     *
     * @param siteName the siteID of the site to create a URL for
     * @param useSSL <code>true</code> if the URL should use https://
     * @param aRequest the request to take the domain name from
     *
     * @return URL to access a SiteMaker Website using http:// and the domain specified in the request.
     */
    public static String siteURL(String siteName, WORequest aRequest, boolean useSSL)
    {
        /** require [site_name_not_null] siteName != null; **/

        return siteURL(aRequest, siteName, httpProtocol(useSSL), (aRequest == null ? domainName() :
                                              RequestUtils.hostNameFromRequest(aRequest)));

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Workhorse method for creating Website URLs.  The returns URL includes any config and style
     * override flags.
     *
     * @param siteName the siteID of the site to create a URL for
     * @param scheme either http:// or https://
     * @param domainName the domain name to use in the URL with a trailing slash.
     *
     * @return the full URL to access a SiteMaker Website
     */
    public static String siteURL(WORequest aRequest, String siteName, String scheme, String domainName)
    {
        /** require
        [site_name_not_null] siteName != null;
        [scheme_not_null] scheme != null;
        [scheme_valid] (scheme.equals(secureProtocol()) || scheme.equals(insecureProtocol()));
        [domain_name_not_null] domainName != null; **/

        String urlForSite = new String(scheme + domainName + "/" + siteName.toLowerCase());

        if (aRequest != null)
        {
            // Only propogate this if it is valid.
            if (SectionDisplay.hasValidPublicationOverrideFlag(aRequest))
            {
                urlForSite += SectionDisplay.configFlagFromRequest(aRequest);
            }

            urlForSite += SectionDisplay.sectionStyleOverrideFlag(aRequest);
        }

        return urlForSite;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Workhorse method for creating Website URLs but which does not include the config and style
     * override flags that siteURL includes
     *
     * @param siteName the siteID of the site to create a URL for
     * @param useSSL <code>true</code> if the URL should use https://
     * @param aRequest the request to take the domain name from
     *
     * @return the full URL, excluding special flags, to access a SiteMaker Website
     */
    public static String baseSiteURL(String siteName, WORequest aRequest, boolean useSSL)
    {
        /** require [site_name_not_null] siteName != null; **/

        String urlForSite = new String(httpProtocol(useSSL) +
                (aRequest == null ? domainName() : RequestUtils.hostNameFromRequest(aRequest)) + "/"
                + siteName.toLowerCase());

        return urlForSite;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating SiteFile URLs using the domain defined in
     * GVCSiteMaker.plist as DomainName. http:// or https:// is used depending on
     * <code>useSSL</code>
     *
     * @param siteName the siteID of the site to create a URL for
     * @param useSSL <code>true</code> if the URL should use https://
     * @param fileName the name that the SiteFile was uploaded as
     *
     * @return the full URL to access a SiteMaker SiteFile
     */
    public static String fileURL(String siteName, String fileName, boolean useSSL)
    {
        /** require
        [site_name_not_null] siteName != null;
        [file_name_not_null] fileName != null; **/

        return fileURL(siteName, fileName, httpProtocol(useSSL), domainName());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating SiteFile URLs using the domain specified
     * in the request.  http:// or https:// is used depending on <code>useSSL</code>
     *
     * @param siteName the siteID of the site to create a URL for
     * @param fileName the name that the SiteFile was uploaded as
     * @param aRequest the request to take the domain name from
     *
     * @return the full URL to access a SiteMaker SiteFile
     */
    public static String fileURL(String siteName, String fileName,
                                 boolean useSSL, WORequest aRequest)
    {
        /** require
        [site_name_not_null] siteName != null;
        [file_name_not_null] fileName != null; **/

        return fileURL(siteName, fileName, httpProtocol(useSSL), (aRequest == null ? domainName() :
            RequestUtils.hostNameFromRequest(aRequest)));

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Workhorse method for creating SiteFile URLs.
     *
     * @param siteName the siteID of the site to create a URL for
     * @param fileName the name that the SiteFile was uploaded as
     * @param scheme either http:// or https://
     * @param domainName the domain name to use in the URL with a trailing slash.
     *
     * @return the full URL to access a SiteMaker SiteFile
     */
    public static String fileURL(String siteName, String fileName, String scheme,
                                 String domainName)
    {
        /** require
        [site_name_not_null] siteName != null;
        [file_name_not_null] fileName != null;
        [scheme_not_null] scheme != null;
        [valid_scheme] (scheme.equals(secureProtocol()) || scheme.equals(insecureProtocol()));
        [domain_name_not_null] domainName != null;
         **/

        return new String(scheme + domainName + "/" + siteName.toLowerCase() +
            SMApplication.appProperties().stringPropertyForKey("FilePath") + fileName);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating relative SiteFile URLs using the siteName and fileName
     *
     * @param siteName the siteID of the site to create a URL for
     * @param fileName the name that the SiteFile was uploaded as
     *
     * @return the relative SiteFile URLs using the siteName and fileName
     */
    public static String relativeFileURL(String siteName, String fileName)
    {
        /** require
        [site_name_not_null] siteName != null;
        [file_name_not_null] fileName != null;
        **/

        return new String("/" + siteName.toLowerCase() + SMApplication.appProperties().stringPropertyForKey("FilePath") + fileName);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating the complete or absolute URL to show either a section of a site, or a section of a site embedded within another
     * site.
     *
     * @param aWebsite - the Website to generate a URL for
     * @param aSection - the Section to display
     * @param aRequest - the WORequest to get the domain from
     *
     * @return complete or absolute URL to show either a section of a site, or a section of a site embedded within another site.
     */
    public static String sectionURL(Website aWebsite, Section aSection, WORequest aRequest)
    {
        /** require
        [a_website_not_null] aWebsite != null;
        [a_section_not_null] aSection != null;
        **/

        return sectionURL(aWebsite, aSection, false, aRequest);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating the complete or absolute URL to show a Sections' version
     *
     * @param aWebsite - the Website to generate a URL for
     * @param aSection - the Section containing the Version to display
     * @param aVersion - the Version to display
     * @param aRequest - the WORequest to get the domain from
     *
     * @return complete or absolute URL to show either a section of a site, or a section of a site embedded within another site.
     */
    public static String sectionURL(Website aWebsite, Section aSection, SectionVersion aVersion, WORequest aRequest)
    {
        /**
        [a_website_not_null] aWebsite != null;
        [a_section_not_null] aSection != null;
        [a_section_not_null] aVersion != null;
        **/

        return sectionURL(aWebsite, aSection, false, aRequest) + "&version=" + aVersion.versionNumber();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method for creating the URL to show either a section of a site, or a section of a site embedded within another
     * site.  http:// or https:// and domain name is included depending on <code>isRelativeURL</code>
     * URLs to show a section of a site look like
     * <br>
     *  when isRelativeURL is false - <code>http://domain/WebsiteID/sectionname</code>
     *  when isRelativeURL is true - <code>/WebsiteID/sectionname</code>
     * <br>
     * URLs of the second form look like
     * <br>
     *  when isRelativeURL is false - <code>http://domain/WebsiteID/EmbeddedWebSiteID/sectionname</code>
     *  when isRelativeURL is true - <code>/WebsiteID/EmbeddedWebSiteID/sectionname</code>
     * <br>
     * where EmbeddedWebSiteID is the ID of the site which directly contains the section sectioname<br />
     * If the request URL contains an publication override flag or a section style override flag, this
     * is duplicated on this URL so that the override status is maintained.
     *
     * @param aWebsite the Website to generate a URL for
     * @param aSection the Section to display
     * @param isRelativeURL <code>true</code> if the URL should be relative thus, should not include protocol and domain name
     * @param aRequest the WORequest to get the domain from
     *
     * @return URL of to show either a section of a site, or a section of a site embedded within another site.
     */
    public static String sectionURL(Website aWebsite, Section aSection, boolean isRelativeURL, WORequest aRequest)
    {
        /** require
        [a_website_not_null] aWebsite != null;
        [a_section_not_null] aSection != null;
        **/

        String urlForSection;
        String websiteURL;
        String siteID = aWebsite.siteID().toLowerCase();

        // Add an element to the path as a flag to any Web Single Signon implementation being used
        // to ignore secure requests to public sections
        if (aSection.requiresPublicHttpsAccess() && ! aSection.requiresSSLConnection())
        {
            siteID = SMApplication.appProperties().stringPropertyForKey("RedirectPath") + "/" + siteID;
        }

        if (isRelativeURL)
        {
            websiteURL = "/" + siteID;
        }
        else
        {
            websiteURL= SMActionURLFactory.baseSiteURL(siteID,
                    aRequest,
                    aSection.requiresSSLConnection() || aSection.requiresPublicHttpsAccess());
        }

        // This is a section directly in this site, not an embedded section.
        String sectionEncodedName = null;

        try
        {
            sectionEncodedName = java.net.URLEncoder.encode(aSection.normalName(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ExceptionConverter(e);
        }

        if (aWebsite.sections().containsObject(aSection))
        {
            // Handle special URL for home section.
            urlForSection = websiteURL + "/" + sectionEncodedName;
        }
        else
        {
            urlForSection = websiteURL +
                "/" + aSection.website().siteID().toLowerCase() +
                "/" + sectionEncodedName;
        }

        // Avoid adding on the flags if the section is an external reference as the external site
        // will not use these flags and may be affected by them.
        if ((aRequest != null) && ! aSection.isExternalReference())
        {
            // Only propogate this if it is valid.
            if (SectionDisplay.hasValidPublicationOverrideFlag(aRequest))
            {
                urlForSection += SectionDisplay.configFlagFromRequest(aRequest);
            }

            urlForSection += SectionDisplay.sectionStyleOverrideFlag(aRequest);
        }

        return urlForSection;


        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns URL of to show contents of a site.
     *
     * @param aWebsite - the Website to generate a URL for
     * @param aSection - the Section to display
     * @param aRequest - the WORequest to get the domain from
     *
     * @return URL of to show contents of a site.
     */
    public static String sectionContentsURL(Website aWebsite, Section aSection, WORequest aRequest)
    {
        /** require 	[a_website_not_null] aWebsite != null;
         		   	[a_section_not_null] aSection != null;
         **/

        String urlForSection = directActionUrl(aRequest, "downloadSectionContents")  +
							"?" + SITE_KEY + "="  + aSection.website().siteID().toLowerCase() + "&" +
							SECTION_KEY + "=" + aSection.normalName();

        return urlForSection;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns URL of to show the passed section in the Edit Section page and to
     * return to the currently displayed URL.
     *
     * @param aSection the SectionVersion to edit
     * @param context the WOContext to get the domain and current URL from
     *
     * @return URL of to show the passed section in the Edit Section page
     */
    public static String editSectionURL(SectionVersion aSectionVersion, WOContext context)
    {
        /** require   [SectionVersion_not_null] aSectionVersion != null;  **/

        Object versionPK = EOEditingContextAdditions.primaryKey(aSectionVersion.editingContext(), aSectionVersion, "versionPKey");
        if ( ! SMApplication.smApplication().isUsingIntegerPKs())
        {
            versionPK = ERXStringUtilities.byteArrayToHexString(((NSData)versionPK).bytes());
        }
        String displayedUrl = URLUtils.urlEncode(displayedURL(context.request()));

        // An unauthenticated user should indicate public editors / contributors.
        // Avoid switching to https to prevent problems with Cosign etc.
        String urlForSection = ((SMSession)context.session()).isUserAuthenticated() ?
                secureDirectActionUrl(context.request(), "editSection") :
                directActionUrl(context.request(), "editSection");

        urlForSection += "?" + SECTION_VERSION_ID_KEY + "="  + versionPK + "&" + URL_KEY + "=" + displayedUrl;

        return urlForSection;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns URL to show the Versions Page for the passed section.
     *
     * @param aSection the Section with the versions to view
     * @param showWarning
     * @param context the WOContext to get the domain and current URL from
     * @param returnUrl the URL that we should return to when done with the section versions page. This URL should NOT be URL-encoded, it will be encoded in this method
     *
     * @return URL to show the Versions Page for the passed section.
     */
    public static String viewSectionVersionsURLWithReturnURL(Section aSection, boolean showWarning, WOContext context, String returnUrl)
    {
        /** require [a_section_not_null] aSection != null;  **/

        Object sectionPK = EOEditingContextAdditions.primaryKey(aSection.editingContext(), aSection, "sectionPKey");
        if ( ! SMApplication.smApplication().isUsingIntegerPKs())
        {
            sectionPK = ERXStringUtilities.byteArrayToHexString(((NSData)sectionPK).bytes());
        }
        returnUrl = URLUtils.urlEncode(returnUrl);
        String warningParam = showWarning ? "&showWarning=Y" : "";

        // An unauthenticated user should indicate public editors.
        // Avoid switching to https to prevent problems with Cosign etc.
        String urlForSection = ((SMSession)context.session()).isUserAuthenticated() ?
                secureDirectActionUrl(context.request(), "viewVersions") :
                directActionUrl(context.request(), "viewVersions");

        urlForSection += "?" + SECTION_ID_KEY + "="  + sectionPK + "&" + URL_KEY + "=" + returnUrl + warningParam;
        return urlForSection;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns URL to show the Versions Page for the passed section.
     *
     * @param aSection the Section with the versions to view
     * @param showWarning
     * @param context the WOContext to get the domain and current URL from
     *
     * @return URL to show the Versions Page for the passed section.
     */
    public static String viewSectionVersionsURL(Section aSection, boolean showWarning, WOContext context)
    {
        /** require   [a_section_not_null] aSection != null;  **/

        String displayedUrl = displayedURL(context.request());
        return viewSectionVersionsURLWithReturnURL(aSection, showWarning, context, displayedUrl);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL displayed based on aRequest
     *
     * @param aRequest the WORequest to get the domain and current URL from
     * @return the URL displayed based on aRequest
     */
    public static String displayedURL(WORequest aRequest)
    {
        String displayedUrl = protocolFromRequest(aRequest) +
                              (aRequest == null ? domainName() : RequestUtils.hostNameFromRequest(aRequest)) +
                              RequestUtils.originalURLFromRequest(aRequest);

        return displayedUrl;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to the user info page.
     *
     * @param aRequest the WORequest to get the domain and current URL from
     * @return the URL to the user info page
     */
    public static String userInfoPageURL(WORequest request)
    {
        /** require [request_not_null] request != null; **/
        String applicationName = SMApplication.appProperties().stringPropertyForKey("ApplicationName");
        return secureDirectActionUrlWithApplicationName(request, applicationName, "userInfoPage");
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to the list of configurable sites.
     *
     * @param aRequest the WORequest to get the domain and current URL from
     * @return the URL to the list of configurable sites
     */
    public static String configSiteURL(WORequest request)
    {
        /** require [request_not_null] request != null; **/

        String applicationName = SMApplication.appProperties().stringPropertyForKey("ApplicationName");
        return secureDirectActionUrlWithApplicationName(request, applicationName, "configSite") +
            SectionDisplay.sectionStyleOverrideFlag(request);

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns the URL to configure a specific site.
     *
     * @param aRequest the WORequest to get the domain and current URL from
     * @param siteID the siteID of the Website to configure
     * @return the URL to configure a specific site.
     */
    public static String configSiteURL(WORequest request, String siteID)
    {
        /** require [site_id_not_null] siteID != null;
                    [request_not_null] request != null; **/

        String applicationName = SMApplication.appProperties().stringPropertyForKey("ApplicationName");
        return secureDirectActionUrlWithApplicationName(request, applicationName, "configSite") +
            "?" + CONFIG_SITE_KEY + "=" + siteID +
            SectionDisplay.sectionStyleOverrideFlag(request);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to manage files for a specific site.
     *
     * @param aRequest the WORequest to get the domain and current URL from
     * @param siteID the siteID of the Website to manage files for.
     * @return the URL to manage files for a specific site.
     */
    public static String configSiteFileMgmtURL(WORequest request, String siteID)
    {
        /** require [site_id_not_null] siteID != null;
                    [request_not_null] request != null; **/

        return secureDirectActionUrl(request, "configSiteFileMgmt") + "?" + CONFIG_SITE_KEY + "=" + siteID +
            SectionDisplay.sectionStyleOverrideFlag(request);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to the support page.
     *
     * @param aRequest the WORequest to get the domain and current URL from
     * @return the URL to configure a specific site.
     */
    public static String supportPageURL(WORequest request)
    {
        /** require [site_id_not_null] siteID != null;
                    [request_not_null] request != null; **/

        String applicationName = SMApplication.appProperties().stringPropertyForKey("ApplicationName");
        return directActionUrlWithApplicationName(request, applicationName, "support");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the direct action URL to invoke the EditLive WYSIWYG editor
     *
     * @param request WORequest used to determine if this URL should be secure or not
     * @return the direct action URL to invoke the EditLive WYSIWYG editor
     */
    public static String editLiveUrl(WORequest request)
    {
        /** require [request_not_null] request != null;     **/

        return RequestUtils.isHTTPSRequest(request) ? secureDirectActionUrl(request, "editLive") : directActionUrl("editLive");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the direct action URL for a Tools Interoperability Launch Request for a specific Website.
     *
     * @param site the Website to access via Tools Interoperability
     * @param request WORequest used to determine host name etc
     * @return the direct action URL for a Tools Interoperability Launch Request for a specific Website
     */
    public static String tiLaunchRequestURL(WORequest request, Website site)
    {
        /** require
        [request_not_null] request != null;
        [site_not_null] site != null; **/
        //return SMActionURLFactory.secureDirectActionUrl(request, "TILaunch/" + site.siteID().toLowerCase());
        return SMActionURLFactory.directActionUrl(request, "TILaunch/" + site.siteID().toLowerCase());
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns an HTTPS action URL.  This can be used to implement a secure hyperlink with a WOGenericContainer.
     * <br>
     * To make a secure hyperlink, change the WOD definition for a regular WOHyperlink to this:
     * <br><br><pre>
     * Hyperlink1: WOGenericContainer {
     *    elementName = "a";
     *    href = actionURL;			// See below.
     *    invokeAction = requestSite;   // Must use this.  Can not use pageName etc.
     * }
     * <\pre><br><br>
     *
     * Any other bindings (e.g. onMouseOver, onClick, class will be copied into the HTML.  To support these secure hyperlinks you will need to add a single method to each page using them.  This method can be shared by all secure hyperlinks on the page:
     * <br><br><pre>
     * public String actionURL()
     * {
     *     return SMActionURLFactory.secureActionURLInContext(context());
     * }
     * <\pre><br><br>
     *
     * @param aContext - the WOContext to generate this URL for.
     * @return an HTTPS action URL.
     */
    public static String secureActionURLInContext(WOContext aContext)
    {
        /** require [a_context_not_null] aContext != null; **/

        String scheme = SMApplication.appProperties().stringPropertyForKey("SecureProtocol");
        String hostName = RequestUtils.hostNameFromRequest(aContext.request());
        return scheme + hostName + aContext.componentActionURL();

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns the Domain Name used for this GVCSiteMaker installation.
     *
     * @return the Domain Name used for this GVCSiteMaker installation.
     */
   public static String domainName()
    {
        return SMApplication.appProperties().stringPropertyForKey("DomainName");

       /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns the Application Name (as used in Monitor) for the GVC.SiteMaker application for this GVCSiteMaker installation.  This is used for form the Direct Action URLs and is only for the SiteMaker application as SMAdmin does not have any direct actions in this factory.
     *
     * @return the Application Name (as used in Monitor) for the GVC.SiteMaker application for this GVCSiteMaker installation.
     */
    public static String applicationName()
    {
        return SMApplication.appProperties().stringPropertyForKey("ApplicationName");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the path below the domain to the WebObjects adaptor.  e.g. /cgi-bin/WebObjects.
     *
     * @return the path below the domain to the WebObjects adaptor.
     */
   public static String adaptorPath()
    {
       return SMApplication.appProperties().stringPropertyForKey("AdaptorPath");

       /** ensure [result_not_null] Result != null; **/
    }



   /**
     * Returns the part that goes after ApplicationName.woa and before the action name.  This is usually just /wa/.  Its primary purpose is to allow other WODirectAction sub-classes to be substituted for DirectAction.java by changing this to /wa/MyDA/.
     *
     * @return the part that goes after ApplicationName.woa and before the action name.
     */
    public static String directActionFragment()
    {
        return SMApplication.appProperties().stringPropertyForKey("DirectActionFragment");

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns the part that goes after the domain name and before the action name.
     * This is something like /cgi-bin/WebObjects/AppName.woa/wa/.
     *
     * @param applicationName the name of the application to perform the direct action on
     * @return the part that goes after the domain name and before the action name.
     */
    public static String directActionPathWithApplicationName(String applicationName)
    {
        /** require [applicationName_not_null] applicationName != null; **/

        return adaptorPath() + "/" + applicationName + ".woa" + directActionFragment();

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns the part that goes after the domain name and before the action name.
     * This is something like /cgi-bin/WebObjects/AppName.woa/wa/.
     *
     * @return the part that goes after the domain name and before the action name.
     */
    public static String directActionPath()
    {
        return directActionPathWithApplicationName(applicationName());
        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns a (http) full Direct Action URL for the named action.
     *
     * @param request the current request
     * @param applicationName the application name to perform the direct action on
     * @param actionName the name of the action to perform
     * @return a (http) full Direct Action URL for the named action
     */
    public static String directActionUrlWithApplicationName(WORequest request, String applicationName, String actionName)
    {
        /** require
        [request_not_null] request != null;
        [applicationName_not_null] applicationName != null;
        [action_name_not_null] actionName != null; **/

        String hostName = RequestUtils.hostNameFromRequest(request);
        return insecureProtocol() + hostName + directActionPathWithApplicationName(applicationName) + actionName;

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns a full Direct Action URL for the named action.
     *
     * @return  a full Direct Action URL for the named action.
     */
    public static String directActionUrl(String actionName)
    {
        /** require [action_name_not_null] actionName != null; **/

        return insecureProtocol() + domainName() + directActionPath() + actionName;

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns a insecure (http) full Direct Action URL for the named action.
     *
     * @return a insecure (http) full Direct Action URL for the named action
     */
    public static String directActionUrl(WORequest request, String actionName)
    {
        /** require [request_not_null] request != null;
                    [action_name_not_null] actionName != null;
         **/
        return directActionUrlWithApplicationName(request, applicationName(), actionName);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns a secure (https) full Direct Action URL for the named action.
     *
     * @param request the current request
     * @param applicationName the application name to perform the direct action on
     * @param actionName the name of the action to perform
     * @return a secure (https) full Direct Action URL for the named action
     */
    public static String secureDirectActionUrlWithApplicationName(WORequest request, String applicationName, String actionName)
    {
        /** require
        [request_not_null] request != null;
        [applicationName_not_null] applicationName != null;
        [action_name_not_null] actionName != null; **/

        String hostName = RequestUtils.hostNameFromRequest(request);
        return secureProtocol() + hostName + directActionPathWithApplicationName(applicationName) + actionName;

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns a secure (https) full Direct Action URL for the named action.
     *
     * @param request the current request
     * @param actionName the name of the action to perform
     * @return a secure (https) full Direct Action URL for the named action
     */
    public static String secureDirectActionUrl(WORequest request, String actionName)
    {
        /** require
        [request_not_null] request != null;
        [action_name_not_null] actionName != null; **/

        return secureDirectActionUrlWithApplicationName(request, applicationName(), actionName);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience methods returning the URI scheme used in the request, http:// or https://.
     *
     * @param request WORequest examined to determine if http or https was used
     * @return Returns the URI scheme to use, http:// or https://
     */
    public static String protocolFromRequest(WORequest request)
    {
        /** require [request_not_null] request != null;     **/

        return httpProtocol(RequestUtils.isHTTPSRequest(request));

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URI scheme to use for insecure requests.  Usually this is http://, but could be changed to https:// to make all requests secure.
     *
     * @return the the URI scheme to use for insecure requests.
     */
    public static String insecureProtocol()
    {
        return SMApplication.appProperties().stringPropertyForKey("InsecureProtocol");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URI scheme to use for secure requests.  Usually this is https://, but could be changed to http:// to make all requests insecure.
     *
     * @return the the URI scheme to use for secure requests.
     */
    public static String secureProtocol()
    {
        return SMApplication.appProperties().stringPropertyForKey("SecureProtocol");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience methods returning the URI scheme to use, http:// or https://.
     *
     * @param useSSL <code>true</code> if the https:// protocol should be used
     * @return Returns the URI scheme to use, http:// or https://
     */
    public static String httpProtocol(boolean useSSL)
    {
        return useSSL ? secureProtocol() : insecureProtocol();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns URL of to return contents of a MixedMediaPane with no adornments.
     *
     * @param paneRecordId - the recordId of the MixedMediaPane to return
     *
     * @return URL of to return contents of a MixedMediaPane with no adornments.
     */
    public static String paneContentsURL(WORequest aRequest, String paneRecordId)
    {
		/** require
         [pane_record_id_not_null] paneRecordId != null;
         [aRequest_not_null] aRequest != null;
         **/

        return directActionUrl(aRequest, "displayPaneContents?" + COMPONENT_ID_KEY + "="  + paneRecordId);
    		//return new String(httpProtocol(false) + domainName() + directActionPath() + "displayPaneContents?" + COMPONENT_ID_KEY + "="  + paneRecordId);

    		/** ensure [result_not_null] Result != null; **/
    	}



    /**
     * Returns the direct action URL for TinyMCE external link list which includes the siteID to determine which sections and uploaded files to display in the list.
     *
     * @return the direct action URL for TinyMCE external link list which includes the siteID to determine which sections and uploaded files to display in the list.
     */
    public static String linkListURL(WORequest aRequest, Website aWebsite, Section aSection)
    {
        /** require
        [aWebsite_not_null] aWebsite != null;
        [aSection_not_null] aSection != null;
        [aRequest_not_null] aRequest != null;
        **/

        return directActionPath() + "insertableLinksList?" + SITE_KEY + "=" + aWebsite.siteID() + "&" + SECTION_KEY + "=" + aSection.normalName();

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns the direct action URL for TinyMCE external image list which includes the siteID to determine which uploaded image files to display in the list.
     *
     * @return the direct action URL for TinyMCE external image list which includes the siteID to determine which uploaded image files to display in the list.
     */
    public static String imageListURL(WORequest aRequest, Website aWebsite, Section aSection)
    {
        /** require
        [aWebsite_not_null] aWebsite != null;
        [aSection_not_null] aSection != null;
        [aRequest_not_null] aRequest != null;
        **/

        return directActionPath() + "insertableImagesList?" + SMActionURLFactory.SITE_KEY + "=" + aWebsite.siteID() + "&" + SECTION_KEY + "=" + aSection.normalName();

        /** ensure [return_not_null] Result != null; **/
    }
}

