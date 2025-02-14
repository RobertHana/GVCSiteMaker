package net.global_village.woextensions.tests;

import junit.framework.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.woextensions.*;

/**
 * Describe class here.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class RequestUtilitiesTest extends TestCase
{

    public RequestUtilitiesTest(String name)
    {
        super(name);
    }

    final public void testOpera()
    {
        //TODO Implement
    }

    final public void testInternetExplorer()
    {
        //TODO Implement 
    }

    final public void testNetscape()
    {
        //TODO Implement 
    }

    final public void testMozilla()
    {
        // Grub robot
        String grubAgentString = "Mozilla/4.0 (compatible; grub-client-1.5.3; Crawl your own stuff with http://grub.org)";
        testUserAgentString(grubAgentString, "4", RequestUtilities.mozillaUserAgent);

        // Inktomi Robot
        String inktomiAgentString = "Mozilla/5.0 (Slurp/si; slurp@inktomi.com; http://www.inktomi.com/slurp.html)";
        testUserAgentString(inktomiAgentString, "5", RequestUtilities.mozillaUserAgent);
        
        // This appeared one day...
        String mysteryAgentString = "Mozilla";
        testUserAgentString(mysteryAgentString, null, RequestUtilities.mozillaUserAgent);

        //TODO Implement OS tests 
        //TODO Implement isUserOSWindows().
        //TODO Implement isUserOSMac().
        //TODO Implement humanReadableUserOSNameFor().
        //TODO Implement userOSFromRequest().
    }

    final public void testGecko()
    {
        //TODO Implement 
    }

        //TODO Implement isUserOSWindows().
        //TODO Implement isUserOSMac().
        //TODO Implement humanReadableUserAgentNameFor().
        //TODO Implement humanReadableUserOSNameFor().
        //TODO Implement userAgentFromRequest().
        //TODO Implement userAgentNameFromRequest().
        //TODO Implement userOSFromRequest().
        //TODO Implement userAgentMajorVersionNumberFromRequest().


    final public void testIsHTTPSRequest()
    {
        //TODO Implement isHTTPSRequest().
    }

    final public void testHostNameFromRequest()
    {
        //TODO Implement hostNameFromRequest().
    }



    /**
     * Utility method for testing user agent strings
     * @param userAgentString the full user agent string
     * @param expectedVersion the expected major version number
     * @param expectedUserAgent the expected (internal) name for the user agent
     */
    public void testUserAgentString(String userAgentString, String expectedVersion, String expectedUserAgent)
    {
        // Grub robot
        WORequest testRequest = requestWithUserAgent(userAgentString); 
        assertEquals(expectedVersion, RequestUtilities.userAgentMajorVersionNumberFromRequest(testRequest));
        assertEquals(userAgentString.toLowerCase(), RequestUtilities.userAgentFromRequest(testRequest));
        assertEquals(DefaultValueRetrieval.defaultString(RequestUtilities.class, 
                                                         RequestUtilities.userAgentNameKeyPathPrefix + 
                                                         expectedUserAgent),
                     RequestUtilities.userAgentNameFromRequest(testRequest));
    }


    /**
     * Utility method to aid in testing.  Returns a minimal request with the 
     * user agent header set.
     * 
     * @param userAgent the value to place in the headers as the User Agent
     * @return a minimal request with the user agent header set
     */
    public WORequest requestWithUserAgent(String userAgent)
    {
        return new WORequest("GET", "/", "HTTP/1.1",
            new NSDictionary(userAgent, RequestUtilities.userAgentKey),
        NSData.EmptyData, NSDictionary.EmptyDictionary);
    }
    
    
    public void testOrderedLanguagesFromHeader()
    {
        // Basic tests
        assertEquals(new NSArray("en"), RequestUtilities.orderedLanguagesFromHeader("en"));
        assertEquals(new NSArray("fr"), RequestUtilities.orderedLanguagesFromHeader("fr")); 
        
        // Test parsing out q values
        assertEquals(new NSArray(new Object[] {"da", "en-gb", "en"}), RequestUtilities.orderedLanguagesFromHeader("da, en-gb;q=0.8, en;q=0.7"));
 
        // Test sorting on q values
        assertEquals(new NSArray(new Object[] {"en", "da", "en-gb"}), RequestUtilities.orderedLanguagesFromHeader("da;q=0.9, en-gb;q=0.8, en;q=1"));
 
        // Test handling of whitespace
        assertEquals(new NSArray(new Object[] {"da", "en-gb", "en"}), RequestUtilities.orderedLanguagesFromHeader("da, en-gb ; q = 0.8  , en;q=0.7"));
    }
    
    
    
    public void testHumanReadableNameForISOName()
    {
        // Basic test
        assertEquals("English", RequestUtilities.humanReadableNameForISOName("en"));
        
        // Test returning English when unknown language is found
        assertEquals("English", RequestUtilities.humanReadableNameForISOName("foo"));
        
        // Test skipping country when unknown country found
        assertEquals("English", RequestUtilities.humanReadableNameForISOName("en-xx"));
        
        // Test non-default language and country
        assertEquals("French-VIRGIN_ISLANDS_U.S.", RequestUtilities.humanReadableNameForISOName("fr-vi"));
    }
    
    
    
}
