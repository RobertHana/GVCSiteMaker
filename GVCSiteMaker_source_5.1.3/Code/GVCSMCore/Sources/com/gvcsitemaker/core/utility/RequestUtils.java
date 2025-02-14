// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * A collection of URL related methods.
 */
public class RequestUtils extends Object 
{
  
   
    
    /**
     * Returns the host name (the domain name) used in this request.  This may not match the one specified in the cgiAdaptorURL if there are aliases for the web server.
     *
     * @return the host name (the domain name) used in this request.
     */
    static public String hostNameFromRequest(WORequest request)
    {
        /** require [request_not_null] request != null; **/

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

        // Add more options here (e.g. for IIS, NSAPI, etc.

        // Returning null would probably cause too many problems.
        if (hostName == null)
        {
            // Originally I had planned to just raise an exception, figuring this to be rather rare.  It turns out that the app receives some http HEAD requests in which there is no header containing the host name.  Raising an exception is not a helpful response.
            DebugOut.println(1, "Failed to get http host name from headers " + request.headers());
            hostName = SMApplication.appProperties().stringPropertyForKey("DomainName");
        }

        return hostName;

        /** ensure [result_not_null] Result != null; **/
    }


    /**
     * Returns <code>true> if the request was made via https/SSL, <code>false</code> otherwise.
     *
     * @return <code>true> if the request was made via https/SSL, <code>false</code> otherwise.
     */
    static public boolean isHTTPSRequest(WORequest request)
    {
        /** require [request_not_null] request != null; **/
        
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

        if (DebugOut.debugLevel >= 25)
        {
            DebugOut.println(25, "https is = " + request.headerForKey("https"));
            DebugOut.println(25, "serverPort is = " + serverPort);
            DebugOut.println(25, "isHTTPSRequest is = " + isHTTPSRequest);
        }

        return isHTTPSRequest;
    }



    /**
     * Returns the original (i.e. unaltered by rewrite rules etc.) URL sent to the webserver that 
     * triggered this request.  This is useful when redirecting, for example to the login page and 
     * back.  Form values are included, except for special values used in creating the rewritten URLS 
     * (SMActionURLFactory.IGNORED_FORM_VALUES) which are not passed on.
     *
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
                cleanedFormValues.removeObjectsForKeys(SMActionURLFactory.IGNORED_FORM_VALUES); 

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
     * Returns a URL encoded version of the form values from this request.  The form values are first translated into the form key1=value1&key2=value2 etc, and then this is url encoded into x-www-form-urlencoded format.  This is intended to be used when the form values are to be passed as a form value themself (i.e. when using a URL with form values as a form value).  See java.net.URLEncoder for more information.
     *
     * @param request - WORequest to take form values from.
     * @return URL encoded version of the form values from this request..
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

        return URLUtils.urlEncode(formValues.toString());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the form value associated with the key after all trailing slashes and white space have been stripped off.
     *
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
     * Returns true if combinations of the browser info contained in aRequest is included in the list of compliantBrowsers, false otherwise.  
     * Each item in the list of compliantBrowsers can be in any of the following formats, from general to specific: 
     *  a. "<browserName>" - if os or version is irrelevant, ie "safari", "ie", "netscape"
     *  b. "<osName>" - if browser or version is irrelevant, ie "os", "mac"
     *  c. "<browserName>/<osName>" - if version is irrelevant, ie "safari/mac"
     *  d. "<browserName>/<osName>/<browserVersion>" - if all are relevant but not not compliant for higher versions, ie "safari/mac/2"
     *  e. "<browserName>/<osName>/<browserVersion>+" - if all are relevant but also compliant for higher versions, ie "safari/mac/2+"
     *  
     * @param request the WORequest to take form values from.
     * @param compliantBrowsers the list of compliant browsers to check from. 
     * @return true if the browser used for aRequest is included in the list of compliantBrowsers, false otherwise.
     */    
    static public boolean isBrowserEditorCompliant(WORequest aRequest, NSArray compliantBrowsers)
    {
        /** require
        [a_request_not_null] aRequest != null;
        [compliantBrowsers_not_null] compliantBrowsers != null; **/
    	
    		boolean isBrowserEditorCompliant = false;
    		
        //Fetch all the necessary browser information from the user agent of the request
        String browserName = RequestUtilities.userAgentNameFromRequest(aRequest);
        String osName = RequestUtilities.userOSFromRequest(aRequest);
        String browserVersion = RequestUtilities.userAgentMajorVersionNumberFromRequest(aRequest);
        
        DebugOut.println(3,"Sniffed browser info... name:" + browserName + " os:" + osName + " browserVersion:" + browserVersion);
        
        if (compliantBrowsers.containsObject(osName) || compliantBrowsers.containsObject(browserName) || 
        compliantBrowsers.containsObject(browserName + "/" + osName) || compliantBrowsers.containsObject(browserName + "/" + osName + "/" + browserVersion))
        {
        		isBrowserEditorCompliant = true;
        }
        else //check for higher versions
        {
            Enumeration browserEnumerator = compliantBrowsers.objectEnumerator();
            while (browserEnumerator.hasMoreElements())
            {
            		String aBrowser = (String) browserEnumerator.nextElement();
            		String browserAndOsPrefix = browserName + "/" + osName + "/"; 
            		if (aBrowser.startsWith(browserAndOsPrefix) && aBrowser.endsWith("+"))
            		{
            			String minimumBrowserVersion = aBrowser.substring( browserAndOsPrefix.length(), browserAndOsPrefix.length() + 1);

            			if ((minimumBrowserVersion!= null) && StringAdditions.isInteger(minimumBrowserVersion) && (browserVersion != null) && 
            					StringAdditions.isInteger(browserVersion))
            			{
            				isBrowserEditorCompliant = Integer.valueOf(minimumBrowserVersion).intValue() <= Integer.valueOf(browserVersion).intValue();
            			}
           		}
            }
        }
        
    		return isBrowserEditorCompliant;
    }
}
