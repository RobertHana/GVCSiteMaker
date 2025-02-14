package net.global_village.foundation;

import java.io.*;
import java.util.*;

import com.webobjects.foundation.*;


/**
 * Convenience and bug fixes for NSBundle.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 14$
 */
public class NSBundleAdditions
{
    protected static final String StringsFileExtension = ".strings";


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private NSBundleAdditions()
    {
        super();
    }



    /**
     * Returns any bundle except for the given bundle.  This is used by the unit tests to return a bundle in which the test resources should not be found.
     *
     * @param exceptBundle the bundle that we shouldn't return
     * @return any bundle but the given bundle
     */
    public static NSBundle anyBundle(NSBundle exceptBundle)
    {
        /** require [valid_param] exceptBundle != null; **/

        Enumeration bundles = NSBundle.frameworkBundles().objectEnumerator();

        while (bundles.hasMoreElements())
        {
            NSBundle aBundle = (NSBundle)bundles.nextElement();
            if ( ! aBundle.equals(exceptBundle))
            {
                JassAdditions.post("NSBundleAdditions", "anyBundle", ! aBundle.equals(exceptBundle));
                return aBundle;
            }
        }

        throw new RuntimeException("anyBundle() could not return a valid result - possibly there is only one bundle!");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the localized string table <code>tableName</code>, or <code>null</code> if the file could not be found.<p>
     *
     * This does not use <code>Collection.collectionWithContentsOfFile()</code> because the path returned by <code>resourcePathForLocalizedResourceNamed()</code> is not fully qualified (and there is no way to get the FQ name!).<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for <code>tableName</code>.
     * @param tableName the string table for which to search.
     * @return a string table or <code>null</code>.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    protected static NSDictionary findTableWithName(NSBundle bundle, String tableName)
    {
        /** require [valid_bundle_param] bundle != null; [valid_tableName_param] tableName != null; **/

        NSDictionary table = null;
        String tablePath = bundle.resourcePathForLocalizedResourceNamed(tableName, null);

        if (tablePath != null)
        {
            byte[] tableBytes = bundle.bytesForResourcePath(tablePath);
            String tableString = new String(tableBytes).trim();

            // If the file is a .strings file, propertyListFromString() doesn't recongnize it without the "{" and "}"
            if ( ! (tableString.startsWith("{") || tableString.startsWith("<?xml")) )
            {
                throw new Error("The table " + tableName + " must be wrapped in '{' and '}'");
            }

            try
            {
                table = (NSDictionary)NSPropertyListSerialization.propertyListFromString(tableString);
            }
            catch (IllegalArgumentException e)
            {
                // If this happens, try adding {} around the table or removing them.  Usage has been inconsistent.
                throw new RuntimeException("String table " + tableName + " in bundle " + bundle.name() + " has a syntax error: " + e.getMessage());
            }
            // Since we are only using this to get .strings files and the CustomInfo.plist, this will always return a dictionary.
        }

        return table;
    }



    /**
     * Returns <code>true</code> if the string table <code>tableName</code> could be found, <code>false</code> otherwise.<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for <code>tableName</code>.
     * @param tableName the string table.
     * @return <code>true</code> if the string table <code>tableName</code> could be found, <code>false</code> otherwise.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    public static boolean tableExistsWithName(NSBundle bundle, String tableName)
    {
        /** require [valid_bundle_param] bundle != null; [valid_tableName_param] tableName != null; **/

        return findTableWithName(bundle, tableName) != null;
    }



    /**
     * Returns the string table <code>tableName</code>.<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for <code>tableName</code>.
     * @param tableName the string table.
     * @return a string table
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    public static NSDictionary tableWithName(NSBundle bundle, String tableName)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_tableName_param] tableName != null;
        [table_exists] tableExistsWithName(bundle, tableName); **/

        return findTableWithName(bundle, tableName);
    }



    /**
     * Returns a localized version of <code>key</code> in table <code>&lt;tableName&gt;.strings</code>.<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for the table.
     * @param key the string to localize.
     * @param tableName the string table to search.
     * @return a localized version of <code>key</code> or <code>null</code> if <code>key</code> could not be found.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    protected static String findLocalizedStringForKey(NSBundle bundle, String key, String tableName)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_key_param] key != null;
        [valid_tableName_param] tableName != null; **/

        String mungedTableName = tableName + StringsFileExtension;
          String localizedString = null;
        if (tableExistsWithName(bundle, mungedTableName))
        {
            NSDictionary table = tableWithName(bundle, mungedTableName);
            localizedString = (String)table.objectForKey(key);
        }
        return localizedString;
    }


    /**
     * Returns <code>true</code> if a localized version of <code>key</code> exists in table <code>tableName</code>, <code>false</code> otherwise.<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for the table.
     * @param key the string for which to search.
     * @param tableName the string table to search.
     * @return <code>true</code> if a localized version of <code>key</code> exists in table <code>tableName</code>, <code>false</code> otherwise.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    public static boolean localizedStringExistsForKey(NSBundle bundle, String key, String tableName)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_key_param] key != null;
        [valid_tableName_param] tableName != null; **/

        return findLocalizedStringForKey(bundle, key, tableName) != null;
    }


    /**
     * Returns a localized version of <code>key</code> in table <code>tableName</code>.<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for the table.
     * @param key the string to localize.
     * @param tableName the string table to search.
     * @return a localized version of <code>key</code>.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    public static String localizedStringForKey(NSBundle bundle, String key, String tableName)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_key_param] key != null;
        [valid_tableName_param] tableName != null;
        [localized_string_exists] localizedStringExistsForKey(bundle, key, tableName); **/

        return findLocalizedStringForKey(bundle, key, tableName);

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Note: the semantics for this method are different than that of WO4.5!  See the description and contract for more info.<p>
     *
     * Returns a localized version of <code>key</code> in table <code>tableName</code>.<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for the table.
     * @param key the string to localize.
     * @param defaultValue the value to return if the localized string for <code>key</code> can't be found in the table.
     * @param tableName the string table to search.
     * @return a localized version of <code>key</code>. If <code>key</code> could not be localized, returns <code>defaultValue</code>.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    public static String localizedStringForKey(NSBundle bundle, String key, String defaultValue, String tableName)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_key_param] key != null;
        [valid_defaultValue_param] defaultValue != null;
        [valid_tableName_param] tableName != null; **/

        String rv;

        if (localizedStringExistsForKey(bundle, key, tableName))
        {
            rv = localizedStringForKey(bundle, key, tableName);
        }
        else
        {
            rv = defaultValue;
        }

        return rv;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the localized list <code>listName</code>, or <code>null</code> if the file could not be found.<p>
     *
     * This does not use <code>Collection.collectionWithContentsOfFile()</code> because the path returned by
     * <code>resourcePathForLocalizedResourceNamed()</code> is not fully qualified (and there is no way to get the FQ name!).<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for <code>listName</code>
     * @param listName the plist file to search for
     * @return a NSArray of the file contents or <code>null</code>.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    protected static NSArray findListWithName(NSBundle bundle, String listName)
    {
        /** require [valid_bundle_param] bundle != null; [valid_listName_param] listName != null; **/

        NSArray list = null;
        String listPath = bundle.resourcePathForLocalizedResourceNamed(listName, null);

        if (listPath != null)
        {
            byte[] listBytes = bundle.bytesForResourcePath(listPath);
            String tableString = new String(listBytes).trim();

            // If the file is a .strings file, propertyListFromString() doesn't recognize it without the "(" and ")"
            if ( ! tableString.startsWith("("))
            {
                throw new Error("The list " + listName + " must be wrapped in '(' and ')'");
            }

            try
            {
                list = (NSArray)NSPropertyListSerialization.propertyListFromString(tableString);
            }
            catch (IllegalArgumentException e)
            {
                throw new RuntimeException("List " + listName + " in bundle " + bundle.name() + " has a syntax error: " + e.getMessage());
            }
        }

        return list;
    }



    /**
     * Returns <code>true</code> if the plist file <code>listName</code> could be found, <code>false</code> otherwise.<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for <code>listName</code>.
     * @param listName name of the plist file
     * @return <code>true</code> if the string table <code>tableName</code> could be found, <code>false</code> otherwise.
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    public static boolean listExistsWithName(NSBundle bundle, String listName)
    {
        /** require [valid_bundle_param] bundle != null; [valid_listName_param] listName != null; **/

        return findListWithName(bundle, listName) != null;
    }



    /**
     * Returns the localized list <code>listName</code>.<p>
     *
     * This does not use <code>Collection.collectionWithContentsOfFile()</code> because the path returned by
     * <code>resourcePathForLocalizedResourceNamed()</code> is not fully qualified (and there is no way to get the FQ name!).<p>
     *
     * Note: uses the <em>server's</em> locale for localization.  This is not appropriate for web apps.
     *
     * @param bundle the bundle in which to look for <code>listName</code>
     * @param listName the plist file to search for
     * @return a NSArray of the file contents
     *
     * @deprecated not functional as of WO 5.4, default resources now in Default.lproj for WOResourceManager and NSBundle can't find them
     */
    public static NSArray listWithName(NSBundle bundle, String listName)
    {
        /** require
        [valid_bundle_param] bundle != null;
        [valid_listName_param] listName != null;
        [list_exists] listExistsWithName(bundle, listName); **/

        return findListWithName(bundle, listName);
    }



    /**
     * Replacement for the infoDictionary() method, which is now deprecated.  Looks for a file called <bundlename>.plist and returns the dictionary created from that file.
     *
     * @param bundle the bundle in which to look for the file.
     * @return the dictionary created from a file.  Returns an empty dictionary if the file does not exist.
     */
    public static NSDictionary infoDictionary(NSBundle bundle)
    {
        /** require [valid_param] bundle != null; **/

        NSDictionary theDictionary;

        // For compatability first try <bundle name>.plist
        if (NSBundleAdditions.tableExistsWithName(bundle, bundle.name() + ".plist"))
        {
            theDictionary = NSBundleAdditions.tableWithName(bundle, bundle.name() + ".plist");
        }
        // If that is not there try Info.plist which is more properly the WO5 successor to the info dictionary
        else if (NSBundleAdditions.tableExistsWithName(bundle, "CustomInfo.plist"))
        {
            theDictionary = NSBundleAdditions.tableWithName(bundle, "CustomInfo.plist");
        }
        else
        {
            theDictionary = new NSDictionary();
        }

        return theDictionary;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Given a path to a framework, this converts it to a path to a java zip file in that framework (a java zip file may or may not exist in the framework - that makes no difference to this method).
     *
     * @param frameworkPath the path that this will convert to point to the java zip
     * @return path to the Java classes that may (or may not) exist in the given framework
     */
    protected static String frameworkPathToJavaZipPath(String frameworkPath)
    {
        /** require [valid_param] frameworkPath != null; **/

        String stringTail = NSPathUtilities.lastPathComponent(frameworkPath);
        stringTail = NSPathUtilities.stringByDeletingPathExtension(stringTail);
        stringTail = stringTail.toLowerCase();
        stringTail = NSPathUtilities.stringByAppendingPathExtension(stringTail, "zip");

        String javaZipPath = NSPathUtilities.stringByAppendingPathComponent(frameworkPath, "Resources");
        javaZipPath = NSPathUtilities.stringByAppendingPathComponent(javaZipPath, "Java");
        javaZipPath = NSPathUtilities.stringByAppendingPathComponent(javaZipPath, stringTail);

        return javaZipPath;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Fix a problem with <code>bundleForClass</code> which causes a call to a class in the main bundle to return null.  We catch that and return <code>mainBundle()</code> if so.
     *
     * @param aClass the class for which this returns the bundle
     * @return the bundle for the given class
     * @see com.webobjects.foundation.NSBundle#bundleForClass(Class)
     */
    public static NSBundle bundleForClass(java.lang.Class aClass)
    {
        /** require [valid_param] aClass != null; **/

        if (NSBundle.bundleForClass(aClass) == null)
        {
            return NSBundle.mainBundle();
        }
        else
        {
            return NSBundle.bundleForClass(aClass);
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a string containing the contents of the resource at resourcePath.  If resourcePath does not
     * indicate a resource, or is null, the empty string is returned.
     *
     * @param bundle NSBundle to get resource from
     * @param resourcePath path to the resource relative to the top level resources directory
     * @return a string containing the contents of the resource at resourcePath
     */
    public static String contentsOfResourceAtPath(NSBundle bundle, String resourcePath) {
        /** require [valid_bundle] bundle != null;   **/
        try {
            return new String(bundle.bytesForResourcePath(resourcePath), _NSStringUtilities.UTF8_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new ExceptionConverter(e);
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if there is a resource at resourcePath.
     *
     * @param bundle NSBundle to get resource from
     * @param resourcePath path to the resource relative to the top level resources directory
     * @return <code>true</code> if there is a resource at resourcePath
     */
    public static boolean hasResourceAtPath(NSBundle bundle, String resourcePath) {
        /** require [valid_bundle] bundle != null;   **/
        return bundle._pathURLForResourcePath(resourcePath, false) != null;
    }


}
