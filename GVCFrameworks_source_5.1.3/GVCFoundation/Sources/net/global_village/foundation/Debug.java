package net.global_village.foundation;

import java.io.*;
import java.util.*;

import com.webobjects.foundation.*;


/**
 * Contains utilities useful for debugging.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 8$
 */
public class Debug extends Object
{

    /**
     * Returns details on the JVM configuration (classpath, system properties etc.), useful for debugging.
     */
    static public String dumpJVMConfiguration()
    {
        StringBuffer output = new StringBuffer("\nSystem Properties Follow....\n");

        Properties systemProperties = System.getProperties();
        Enumeration propertyNames = systemProperties.propertyNames();
        while (propertyNames.hasMoreElements())
        {
            String propertyName = (String) propertyNames.nextElement();
            if (propertyName.equals("java.class.path") || propertyName.equals("com.webobjects.classpath"))
            {
                output.append("Classpath\n");
                String pathSeparator = systemProperties.getProperty("path.separator");

                NSArray classPathComponents = NSArray.componentsSeparatedByString(systemProperties.getProperty(propertyName),
                                                                                  pathSeparator);
                Enumeration classpathEnumeration = classPathComponents.objectEnumerator();
                while (classpathEnumeration.hasMoreElements())
                {
                    String jarPath = (String) classpathEnumeration.nextElement();
                    output.append(jarPath + "\n");
                    try
                    {
                        File jarFile = new File(jarPath);
                        output.append((jarFile.isFile() ? "    --> Exists\n" : "*** --> Does not Exist!\n"));
                    }
                    catch (Throwable t)
                    {
                        output.append("Exception validating classpath: " + t + "\n");
                    }
                }
            }
            else
            {
                output.append(propertyName + " = " + systemProperties.getProperty(propertyName) + "\n");
            }
        }

        return output.toString();
    }



    /**
     * Convenience method for adding debug information debugInfo to NSLog.Debug based on the debugLevel specified.
     *
     * @param debugLevel the level that the debugInfo will be appended
     * @param debugInfo the debug information to append
     */
    static public void logOutForLevel(int debugLevel, String debugInfo)
    {
        if (NSLog.debugLoggingAllowedForLevel(debugLevel))
        {
            NSLog.debug.appendln(debugInfo);
        }
    }



    /**
     * Convenience method for adding debug information debugInfo to NSLog.Debug based on the debugGroup specified.
     *
     * @param debugGroup the group that the debugInfo will be appended
     * @param debugInfo the debug information to append
     */
    static public void logOutForGroup(int debugGroup, String debugInfo)
    {
        if (NSLog.debugLoggingAllowedForGroups(debugGroup))
        {
            NSLog.debug.appendln(debugInfo);
        }
    }



    /**
     * Convenience method for adding debug information debugInfo to NSLog.Debug based on the debugLevel and debugGroup specified.
     *
     * @param debugLevel the level that the debugInfo will be appended
     * @param debugGroup the group that the debugInfo will be appended
     * @param debugInfo the debug information to append
     */
    static public void logOut(int debugLevel, int debugGroup, String debugInfo)
    {
        if (NSLog.debugLoggingAllowedForLevelAndGroups(debugLevel, debugGroup))
        {
            NSLog.debug.appendln(debugInfo);
        }
    }
}
