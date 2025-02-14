package net.global_village.woextensions;

import java.util.*;

import com.webobjects.appserver.*;
import com.webobjects.appserver._private.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * RequestUtilities provides easy access to information contained in a WORequest.  Browser detection (via JavaScript) is detailed at <a href="http://www.mozilla.org/docs/web-developer/sniffer/browser_type.html">this site</a> is useful as it contains an extensive list of browser versions and how they are detected.  Since we are primarily concerned with CSS <A HREF="http://www.webreview.com/style/css1/charts/mastergrid.shtml">this link</a> lists the major browser changes that effected CSS.  The details of the User-Agent header are listed   <A HREF="http://www.w3.org/Protocols/HTTP/HTRQ_Headers.html#user-agent">here</A>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 16$
 */
public class RequestUtilities extends Object
{
    // note all UserAgents and OS strings are in lower case where mixed case is the actual contained in the header
    public static final String operaUserAgent = "opera";
    public static final String ieUserAgent = "msie";
    public static final String mozillaUserAgent = "mozilla";
    public static final String geckoBasedUserAgent = "gecko";
    public static final String safariUserAgent = "safari";
    public static final String firefoxUserAgent = "firefox";

    public static final String netscapeVersionString = "netscape";

    public static final String macOSIdentifierString = "mac";
    public static final String windowsIdentifierString = "win";  // we are treating all windows versions alike currently

    public static final String defaultKey = "default";

    public static final String userAgentNameKeyPathPrefix = "RequestUtilities.browsers.";
    public static final String userOSKeyPathPrefix = "RequestUtilities.operatingSystems.";
    public static String userAgentKey = "User-Agent";


    protected static NSDictionary countriesISO3166;
    protected static NSDictionary languagesISO639;
    protected static String defaultLanguageISOCode = "en";


    /**
     * Returns <code>true</code> if the user agent from the request argument is from <code>Opera</code>, <code>false</code> otherwise.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return <code>true</code> if the user agent from the request argument is <code>Opera</code>, <code>false</code> otherwise.
     */
    public static boolean isUserAgentOpera(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent = RequestUtilities.userAgentFromRequest(request);
        return (userAgent.indexOf(RequestUtilities.operaUserAgent) != -1);
    }



    /**
     * Returns <code>true</code> if the user agent from the request argument is from <code>Internet Explorer</code>, <code>false</code> otherwise.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return <code>true</code> if the user agent from the request argument is from <code>Internet Explorer</code>, <code>false</code> otherwise
     */
    public static boolean isUserAgentInternetExplorer(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent = RequestUtilities.userAgentFromRequest(request);
        return (userAgent.indexOf(RequestUtilities.ieUserAgent) != -1);
    }



    /**
     * Returns <code>true</code> if the user agent from the request argument is from <code>Netscape</code>, <code>false</code> otherwise.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return <code>true</code> if the user agent from the request argument is from <code>Netscape</code>, <code>false</code> otherwise
     */
    public static boolean isUserAgentNetscape(WORequest request)
    {
        /** require [valid_param] request != null; **/

        // All netscapes are based on Mozilla but perhaps not all of them contain "netscape" in their user-agent string, in fact most old ones do not, so the best we can do is check ie and other specific browser tests before this one.
        return RequestUtilities.isUserAgentMozillaCompatible(request);
    }



    /**
     * Returns <code>true</code> if the user agent mentions mozilla, <code>false</code> otherwise.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return <code>true</code> if the user agent mentions mozilla, <code>false</code> otherwise
     */
    public static boolean isUserAgentMozillaCompatible(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent = RequestUtilities.userAgentFromRequest(request);
        return (userAgent.indexOf(RequestUtilities.mozillaUserAgent) != -1);
    }



    public static boolean isUserAgentSafari(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent = RequestUtilities.userAgentFromRequest(request);
        return ((userAgent.indexOf(RequestUtilities.safariUserAgent) != -1) && isUserOSMac(request));
    }



    public static boolean isUserAgentFirefox(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent = RequestUtilities.userAgentFromRequest(request);
        return (userAgent.indexOf(RequestUtilities.firefoxUserAgent) != -1);
    }



    /**
     * Returns <code>true</code> if the user agent from the request argument contains Gecko but not the safariUserAgent as
     initially Safari was billing itself as "like Gecko" but it does not use Gecko at all.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return <code>true</code> if the user agent from the request argument contains Gecko else returns false
     */
    public static boolean isUserAgentUsingGecko(WORequest request)
    {
        /** require [valid_param] request != null; **/
        boolean result = false;

        String userAgent = RequestUtilities.userAgentFromRequest(request);

        if (userAgent.indexOf(RequestUtilities.geckoBasedUserAgent) != -1)
        {
            if (userAgent.indexOf(RequestUtilities.safariUserAgent) == -1)
            {
                // This omits safari which uses KHTML to render pages and not Gecko,  user agents have been known to change, Safari's original user-agent string included the phrase "like Gecko" to get past browser detection code thus the extra test.
                result = true;
            }
        }

        return result;
    }




    /**
     * Returns <code>true</code> if the user OS from the request argument from <code>Windows</code>, <code>false</code> otherwise.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return <code>true</code> if the user agent from the request argument is from <code>Windows</code>, <code>false</code> otherwise
     */
    public static boolean isUserOSWindows(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent = RequestUtilities.userAgentFromRequest(request);
        return (userAgent.indexOf(RequestUtilities.windowsIdentifierString) != -1);
    }



    /**
     * Returns <code>true</code> if the user OS from the request argument from <code>Mac</code>, <code>false</code> otherwise.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return <code>true</code> if the user agent from the request argument is from <code>Mac</code>, <code>false</code> otherwise
     */
    public static boolean isUserOSMac(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent = RequestUtilities.userAgentFromRequest(request);
        return (userAgent.indexOf(RequestUtilities.macOSIdentifierString) != -1);
    }



   /**
     * Returns the human readable user agent name that corresponds to the <code>userAgentName<code> argument from this framework's <code>CustomInfo.plist</code>
     *
     * @param userAgentName the key to look for
     * @return the human readable user agent name that corresponds to the <code>userAgentName<code> argument from this framework's <code>CustomInfo.plist</code>
     */
    public static String humanReadableUserAgentNameFor(String userAgentName)
    {
        /** require
        [valid_param] userAgentName != null;
        [default_value_exists] DefaultValueRetrieval.defaultValueExists(net.global_village.foundation.ClassAdditions.unsafeClassForName("net.global_village.woextensions.RequestUtilities"), userAgentNameKeyPathPrefix + userAgentName); **/

        return DefaultValueRetrieval.defaultString(RequestUtilities.class, userAgentNameKeyPathPrefix + userAgentName);
    }



    /**
     * Returns the human readable user OS name that corresponds to the <code>userOSName<code> argument from this framework's <code>CustomInfo.plist</code>
     *
     * @param userOSName the key to look for
     * @return the human readable OS name that corresponds to the <code>userOSName<code> argument from this framework's <code>CustomInfo.plist</code>
     */
    public static String humanReadableUserOSNameFor(String userOSName)
    {
        /** require
        [valid_param] userOSName != null;
        [default_value_exists] DefaultValueRetrieval.defaultValueExists(net.global_village.foundation.ClassAdditions.unsafeClassForName("net.global_village.woextensions.RequestUtilities"), userOSKeyPathPrefix + userOSName); **/

        return DefaultValueRetrieval.defaultString(RequestUtilities.class, userOSKeyPathPrefix + userOSName);
    }





    /**
     * Returns the user agent (browser) from the passed in request.  This is the full User Agent (in all lower case).  userAgentNameFromRequest() returns a simplified and more readable version of the User Agent.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return user agent/browser name in original form.
     */
    public static String userAgentFromRequest(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgent  = request.headerForKey(RequestUtilities.userAgentKey);

        if (userAgent == null)
        {
            userAgent = RequestUtilities.defaultKey;
        }
        else
        {
            userAgent = userAgent.toLowerCase();
        }

        return userAgent;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the user agent (browser) from the passed in request.  The returned value is retrieved from the <code>CustomInfo.plist</code> to be more human readable than what is contained in the HTTP request.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return user agent/browser name.
     */
    public static String userAgentNameFromRequest(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userAgentName;

        if (RequestUtilities.isUserAgentOpera(request))
        {
            userAgentName = RequestUtilities.humanReadableUserAgentNameFor(RequestUtilities.operaUserAgent);
        }
        else if (RequestUtilities.isUserAgentSafari(request))
        {
            userAgentName = RequestUtilities.humanReadableUserAgentNameFor(RequestUtilities.safariUserAgent);
        }
        else if (RequestUtilities.isUserAgentFirefox(request))
        {
            userAgentName = RequestUtilities.humanReadableUserAgentNameFor(RequestUtilities.firefoxUserAgent);
        }
        else if (RequestUtilities.isUserAgentInternetExplorer(request))
        {
            userAgentName = RequestUtilities.humanReadableUserAgentNameFor(RequestUtilities.ieUserAgent);
        }
        else if (RequestUtilities.isUserAgentMozillaCompatible(request))
        {
            // This is a mozilla based browser that is neither ie nor Opera so we're assuming it is Netscape
            userAgentName = RequestUtilities.humanReadableUserAgentNameFor(RequestUtilities.mozillaUserAgent);
        }
        else // Not IE nor Opera nor even based on Mozilla (Netscape) so return default
        {
            userAgentName = RequestUtilities.humanReadableUserAgentNameFor(RequestUtilities.defaultKey);
        }

        return userAgentName;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the user OS from the passed in request.  Currently we only recognize Macintosh and Widows lumping all other OS'es into the default category.  The returned value is retrieved from the <code>CustomInfo.plist</code> to be more human readable than what is contained in the HTTP request.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return OS name.
     */
    public static String userOSFromRequest(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String userOS;

        if (RequestUtilities.isUserOSWindows(request))
        {
            userOS = RequestUtilities.humanReadableUserOSNameFor(RequestUtilities.windowsIdentifierString);
        }
        else if (RequestUtilities.isUserOSMac(request))
        {
            userOS = RequestUtilities.humanReadableUserOSNameFor(RequestUtilities.macOSIdentifierString);
        }
        else // Use the default value
        {
            userOS = RequestUtilities.humanReadableUserOSNameFor(RequestUtilities.defaultKey);
        }

        return userOS;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the user agent version number (browser version number) from the passed in request.  The major version number is the number(s) before the decimal point.  (Currently it will not handle greater then single digit version numbers correctly)  Although RFC 1945 specifies a standard it proved necessary to have slightly different code for each browser.  Currently we determine if the userAgent is IE, Netscape, Opera, or other then return a version number as appropriate. <code>null</code> is a valid version number.  If more exact versions are required another method will be written which this method could then be modified to call removing all numbers after the decimal point.
     *
     * @param request the <code>WORequest</code> where the result will be taken from
     * @return user agent/browser major version.
     */
    public static String userAgentMajorVersionNumberFromRequest(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String majorVersionNumber = null;
        char possibleVersionNumber = 'a';  // needs default non digit value
        String userAgent = RequestUtilities.userAgentFromRequest(request);

        try
        {
            if (RequestUtilities.isUserAgentOpera(request))
            {
                int index = userAgent.indexOf(operaUserAgent);
                possibleVersionNumber = userAgent.charAt( index + operaUserAgent.length() + 1);
            }
            else if (RequestUtilities.isUserAgentInternetExplorer(request))
            {
                int index = userAgent.indexOf(ieUserAgent);

                // version number is after ieUserAgent and a space.
                possibleVersionNumber = userAgent.charAt( index + RequestUtilities.ieUserAgent.length() + 1);
            }
            else if (RequestUtilities.isUserAgentMozillaCompatible(request))
            {
                // This user-agent includes the phrase "mozilla" and is neither opera nor ie.
                int index = userAgent.indexOf(netscapeVersionString);
                int userAgentLength = userAgent.length();

                if (index != -1)
                {
                    if (RequestUtilities.isUserAgentUsingGecko(request))
                    {
                        // Gecko based Netscape so major version number should be six plus
                        if (userAgentLength > ( index + netscapeVersionString.length() + 1))
                        {
                            possibleVersionNumber = userAgent.charAt( index + netscapeVersionString.length() + 1);
                        }
                    }
                    else
                    {
                        // original Netscape detection logic 1-4.7
                        if (userAgentLength > ( index + netscapeVersionString.length()))
                        {
                            possibleVersionNumber = userAgent.charAt( index + netscapeVersionString.length());
                        }
                    }
                }
                else
                {
                    // special case for Netscape 4.8
                    // this also is true for any mozilla compatiable browsers which do not have "netscape" in it's user-agent
                    // recalculate index based on mozilla user-agent and return mozilla version number
                    index = userAgent.indexOf(mozillaUserAgent);
                    if (userAgentLength > ( index + mozillaUserAgent.length() + 1))
                    {
                        possibleVersionNumber = userAgent.charAt( index + mozillaUserAgent.length() + 1);
                    }
                }
            }
        }
        // Non-standard user agent names like "MSIE" may cause an exception
        catch (RuntimeException e)
        {
            NSLog.err.appendln("Could not get major version from " + userAgent);
            NSLog.err.appendln(e);
        }

        // now check that we've found a valid possible version number
        if (Character.isDigit(possibleVersionNumber))
        {
            Character versionNumber = new Character(possibleVersionNumber);
            majorVersionNumber = versionNumber.toString();
        }

        return majorVersionNumber;
    }



    /**
     * Returns <code>true</code> if the request was made via https/SSL, <code>false</code> otherwise.
     *
     * @param request the WORequest to check for HTTPS state
     * @return <code>true</code> if the request was made via https/SSL, <code>false</code> otherwise.
     */
    static public boolean isHTTPSRequest(WORequest request)
    {
        boolean isHTTPSRequest = false;

        // Depending on the adaptor the incoming port can be found in one of two places.
        String serverPort = request.headerForKey("SERVER_PORT");
        if (serverPort == null)
        {
            serverPort = request.headerForKey("x-webobjects-server-port");
        }

        // Apache and some other web servers use this to indicate HTTPS mode.
        String httpsMode = request.headerForKey("https");

        // If either the https header is 'on' or the server port is 443 then we consider this to be an HTTP request.
        isHTTPSRequest = ( ((httpsMode != null) && httpsMode.equalsIgnoreCase("on")) ||
                           ((serverPort != null) && serverPort.equals("443")) );

        return isHTTPSRequest;
    }



    /**
     * Returns the host name (the domain name) used in this request.  This may not match the one specified in the cgiAdaptorURL if there are aliases for the web server.
     *
     * @param request the request to get the hostname from
     * @return the host name (the domain name) used in this request.
     */
    static public String hostNameFromRequest(WORequest request)
    {
        /** require [valid_param] request != null; **/

        // First try the WebObjects one
        String hostName = request.headerForKey("x-webobjects-server-name");

        // If that is not there, try the standard one used by the cgi-bin adaptor.
        if (hostName == null)
        {
            hostName = request.headerForKey("Host");
        }

        // If that is not there, try the one used by the mod_webobjects adaptor
        if (hostName == null)
        {
            hostName = request.headerForKey("http_host");
        }

        // Add more options here (e.g. for IIS, NSAPI, etc.), if neccessary...

        return hostName;

        /** ensure [valid_result] Result != null; **/
     }



    /**
     * Returns the original (i.e. unaltered by rewrite rules etc.) URL sent to the webserver that
     * triggered this request.  This is useful when redirecting, for example to the login page and
     * back.  Form values are included.  The scheme and domain name are not included.
     *
     * @param request WORequest to take form values from
     * @return the original (i.e. unaltered by rewrite rules etc.) URL sent to the webserver that
     * triggered this request
     */
    public static String originalURLFromRequest(WORequest request)
    {
        /** require [request_not_null] request != null; **/

        // Note: this method keeps breaking.  It seems that different versions of Apache do different
        // things.  Some do not send script_url, others send it with the form values stripped off,
        // others send it intact.  Some versions have the form values on the end of request_uri,
        // others do not.  WTF?!!?
        String originalURLFromRequest = request.headerForKey("request_uri");

        if (originalURLFromRequest == null)
        {
            // In WO 5.2.2 some requests result in null formValues().  Handle this to avoid exceptions
            boolean hasFormValues = (request.formValues() != null) && (request.formValues().count() > 0);

            originalURLFromRequest = request.headerForKey("script_url");

            if (originalURLFromRequest == null)
            {
                throw new RuntimeException("Can't determine original URL from request headers.  Is mod_rewrite properly configured?");
            }

            // Last gasp hammer to kill a fly attempt to ensure that form values get passed.
            if ((originalURLFromRequest.indexOf("?") == -1) && hasFormValues)
            {
                NSMutableDictionary cleanedFormValues = new NSMutableDictionary(request.formValues());
                cleanedFormValues.removeObjectsForKeys(NSArray.EmptyArray);  // Future expansion

                if (cleanedFormValues.count() > 0)
                {
                    originalURLFromRequest += "?" + NSDictionaryAdditions.urlEncodedDictionary(cleanedFormValues);
                }
            }
        }

        return originalURLFromRequest;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a URL encoded version of the form values from this request.  The form values are first
     * translated into the form key1=value1&key2=value2 etc, and then this is url encoded into
     * x-www-form-urlencoded format.  This is intended to be used when the form values are to be
     * passed as a form value themself (i.e. when using a URL with form values as a form value).
     * See java.net.URLEncoder for more information.
     *
     *
     * @param request WORequest to take form values from
     * @return URL encoded version of the form values from this request
     */
    static public String urlEncodedFormValues(WORequest request)
    {
        /** require [request_not_null] request != null; **/

        StringBuffer formValues = new StringBuffer(1024);
        Enumeration keyEnumerator = request.formValueKeys().objectEnumerator();
        while (keyEnumerator.hasMoreElements())
        {
            // Add on the connector if this is not the first form value.
            if (formValues.length() != 0)
            {
                formValues.append("&");
            }

            String aKey = (String)keyEnumerator.nextElement();
            Object anObject = request.formValuesForKey(aKey).objectAtIndex(0);  // Fails for multiple values

            formValues.append(aKey);
            formValues.append("=");
            formValues.append(anObject.toString());
        }

        return WOURLEncoder.encode(formValues.toString(), "UTF-8");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the form value associated with the key after all trailing slashes and white space have been stripped off.
     *
     * @param aRequest WORequest to take form value from
     * @param aKey key identifying form value
     * @return the form value associated with the key after all trailing slashes and white space have been stripped off.
     */
    static public Object cleanedFormValueForKey(WORequest aRequest, String aKey)
    {
        /** require
        [a_key_not_null] aKey != null;
        [a_request_not_null] aRequest != null; **/

        Object formValue = aRequest.formValueForKey(aKey);

        if ((formValue != null) && String.class.isInstance(formValue))
        {
            String stringValue = ((String) formValue).trim();

            while (stringValue.endsWith("/"))
            {
                stringValue = stringValue.substring(0, stringValue.length() - 1);
            }
            formValue = stringValue;
        }

        return formValue;
    }


    /**
     * Returns the browser languages from the accept-language header ordered, and translated into
     * longer names.  See humanReadableNameForISOName for how this translation is done.
     * Unlike the WORequest method of this name, country names are included.  This
     * allows the basic localization to handle different dialects. The defaultLanguage() is included
     * at the end if not specified in the request.  This ensures that the user at least seems something
     * intelligible (assuming it is defined in the default language).
     *
     * @param request the request to get the accept-language header from
     * @return browser languages from the accept-language header orderd, and translated into longer names
     */
    static public NSArray browserLanguages(WORequest request)
    {
        /** require [valid_param] request != null; **/

        NSArray orderedLanguages = orderedLanguagesFromHeader(rawBrowserLanguages(request));
        NSMutableArray languages = new NSMutableArray(orderedLanguages.count());
        for (int i = 0;  i < orderedLanguages.count(); i++)
        {
            languages.addObject(humanReadableNameForISOName((String)orderedLanguages.objectAtIndex(i)));
        }

        if ( ! languages.contains(defaultLanguage()))
        {
            languages.addObject(defaultLanguage());
        }

        return languages;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the raw accept-language header sent in this request. If the header can't be found,
     * returns defaultLanguageISOCode() as the default.
     *
     * @param request the request to get the accept-language header from
     * @return the accept-language header sent in this request.
     */
    static public String rawBrowserLanguages(WORequest request)
    {
        /** require [valid_param] request != null; **/

        String header = request.headerForKey("accept-language");

        if (header == null)
        {
            header = request.headerForKey("Accept-Language");
        }

        if (header == null)
        {
            header = request.headerForKey("HTTP_ACCEPT_LANGUAGE");
        }

        if (header == null)
        {
            header = defaultLanguageISOCode();
        }

        return header;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the raw languages from the passed accept-language header, ordered by their
     * descending q values (explicit or implied).
     *
     * @param header the accept-language header to get the languages and weighting order from
     * @return the raw languages ordered by their descending q values
     */
    static public NSArray orderedLanguagesFromHeader(String header)
    {
        /** require [valid_param] header != null; **/

        final Double maximum = new Double(1.0);
        NSArray rawLanguages = NSArray.componentsSeparatedByString(header, ",");
        NSMutableDictionary weightedLanguages = new NSMutableDictionary(rawLanguages.count());
        for (int i = 0; i < rawLanguages.count(); i++)
        {
            String language = ((String)rawLanguages.objectAtIndex(i)).trim();
            String languageCode = language;
            Double weight = maximum;

            // Extract explicit weighting
            if (language.indexOf(";") > -1)
            {
                languageCode = language.substring(0, language.indexOf(";")).trim();
                weight = Double.valueOf(language.substring(language.indexOf("=") + 1));
            }

            weightedLanguages.setObjectForKey(languageCode, weight);
        }

        NSArray sortedWeights = NSArrayAdditions.sortedArrayUsingComparator(weightedLanguages.allKeys(), NSComparator.DescendingNumberComparator);
        return weightedLanguages.objectsForKeys(sortedWeights, defaultLanguageISOCode());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the human readable name (in English) for the passed ISO language  and country name.
     * The format is language-country, e.g. English-Canada.  The country is optional. If no language
     * is recognized, English is returned.  The codes are translated into names using the dictionaries
     * from languagesISO639() and countriesISO3166().
     *
     * @param isoName the ISO language / country pair from the accept-language header
     * @return the human readable name for the passed ISO language  and country name
     */
    static public String humanReadableNameForISOName(String isoName)
    {
        /** require [valid_param] isoName != null; **/

        String humanLanguage = null;
        String humanCountry = null;
        if (isoName.indexOf("-") == -1)
        {
            humanLanguage = (String) languagesISO639().objectForKey(isoName.toLowerCase());
        }
        else
        {
            humanLanguage = (String) languagesISO639().objectForKey(isoName.substring(0, isoName.indexOf("-")).trim().toLowerCase());
            humanCountry = (String) countriesISO3166().objectForKey(isoName.substring(isoName.indexOf("-") + 1).trim().toUpperCase());
        }

       StringBuffer humanReadableName = new StringBuffer();
        if (humanLanguage == null)
        {
            humanReadableName.append(defaultLanguage());
        }
        else
        {
            humanReadableName.append(humanLanguage);
            if (humanCountry != null)
            {
                humanReadableName.append("-");
                humanReadableName.append(humanCountry);
            }
        }

        return humanReadableName.toString();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a dictionary of counties keyed on ISO 3166 country code.  The entries in
     * the dictionary are taken from the CountriesISO3166.plist resource in this framework.
     * These values are intended for use with localization, but may also be used for other purposes.
     *
     * @return dictionary of counties keyed on ISO 3166 country code
     */
    public static NSDictionary countriesISO3166()
    {
        if (countriesISO3166 == null)
        {
            String dictionaryString = ResourceManagerAdditions.stringFromResource("CountriesISO3166.plist", "GVCWOExtensions");

            if (dictionaryString == null)
            {
                throw new RuntimeException("Failed to load resource CountriesISO3166.plist from GVCWOExtensions framework.");
            }

            countriesISO3166 = NSPropertyListSerialization.dictionaryForString(dictionaryString);
        }

        return countriesISO3166;
        /** ensure [valid_result] (Result != null) && (Result.count() > 0);   **/
    }



    /**
     * Returns a dictionary of languages keyed on ISO 639 language code.  The entries in
     * the dictionary are taken from the LanguagesISO639.plist resource in this framework.
     * These values are intended for use with localization, but may also be used for other purposes.
     *
     * @return dictionary of languages keyed on ISO 639 country code
     */
    public static NSDictionary languagesISO639()
    {
        if (languagesISO639 == null)
        {
            String dictionaryString = ResourceManagerAdditions.stringFromResource("LanguagesISO639.plist", "GVCWOExtensions");

            if (dictionaryString == null)
            {
                throw new RuntimeException("Failed to load resource LanguagesISO639.plist from GVCWOExtensions framework.");
            }

            languagesISO639 = NSPropertyListSerialization.dictionaryForString(dictionaryString);
        }

        return languagesISO639;
        /** ensure [valid_result] (Result != null) && (Result.count() > 0);   **/
    }



    /**
     * The default language is used if the browser does not supply one or if none of the languages
     * requested by the browser are available.
     *
     * @return the ISO 639 code for the default language
     */
    public static String defaultLanguageISOCode()
    {
        return defaultLanguageISOCode;
        /** ensure [valid_result] (Result != null) && (languagesISO639().objectForKey(Result) != null);   **/

    }



    /**
     * The default language is used if the browser does not supply one or if none of the languages
     * requested by the browser are available.
     *
     * @return the human recognizable name for the default language
     */
    public static String defaultLanguage()
    {
        return (String)languagesISO639().objectForKey(defaultLanguageISOCode());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * The default language is used if the browser does not supply one or if none of the languages
     * requested by the browser are available.
     *
     * @param code the ISO 639 code for the language to use as the default
     */
    public void setDefaultLanguageISOCode(String code)
    {
        /** require [valid_code](code != null) && (languagesISO639().objectForKey(code) != null);  **/
        defaultLanguageISOCode = code;
        /** ensure [language_set] (defaultLanguageISOCode() != null) && (defaultLanguageISOCode().equals(code));  **/
    }


}
