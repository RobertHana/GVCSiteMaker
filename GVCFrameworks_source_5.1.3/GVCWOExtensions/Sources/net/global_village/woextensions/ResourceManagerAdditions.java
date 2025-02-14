package net.global_village.woextensions;

import java.io.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * Utility methods for use with WOResourceManager
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */  
public class ResourceManagerAdditions extends Object
{


    /**
     * Returns the named, localized resource as a String or null if the resource can not be found.
     *
     * @param resourceName the file name of the resource to load
     * @param framework optional framework to read the read from, null if resource is in the application
     * @param languages list of languages in specific order or null for default order
     * @return the named resource as a String or null if the resource can not be found
     */
    static public String stringFromResource(String resourceName, String framework, NSArray languages)
    {
        /** require [valid_resourceName_param] resourceName != null;  **/

        String stringFromResource = null;
        WOResourceManager rm = WOApplication.application().resourceManager();

        // Try this first as bytesForResourceNamed throws a null pointer exception if the file can not be found.
        InputStream plistStream = rm.inputStreamForResourceNamed(resourceName, framework, languages);
        if (plistStream != null)
        {
            stringFromResource = new String(rm.bytesForResourceNamed(resourceName, framework, languages));
        }
        else if (NSLog.debugLoggingAllowedForLevelAndGroups(NSLog.DebugLevelInformational,
		                                                    NSLog.DebugGroupResources))
        {
            System.err.println("Cannot locate resource named " + resourceName + " in framework " +
                             framework + ".  Returning null");
        }

        return stringFromResource;
    }


    /**
     * Returns the named resource as a String or null if the resource can not be found.
     *
 `   * @deprecated use stringFromResource(String, String, NSArray)
     * @param resourceName the file name of the resource to load
     * @param framework optional framework to read the read from.  Should be null if resource is in the application
     * @return the named resource as a String or null if the resource can not be found
     */
    static public String stringFromResource(String resourceName, String framework)
    {
        /** require [valid_resourceName_param] resourceName != null; **/
        return stringFromResource(resourceName, framework, null);
     }


}
