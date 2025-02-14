package net.global_village.foundation;

import com.webobjects.foundation.NSBundle;


/**
 * Allows strings to be localized from a myriad of different places.
 * Four string files are searched to determine the localized version of the string:
 * <ol>
 *   <li>a file with the same name as this class
 *   <li>a file of the same name as the bundle that this class is in (as determined by the
 *       NSExecutable key in the Info plist for the class's bundle)
 *   <li>a file named "LocalizedStrings"
 *   <li>a file named by the DIV GVCLocalizedFileNameVariable
 * </ol>
 * The search is done in this class' bundle, unless a DIV exists with the
 * GVCLocalizedBundleVariable key, in which case, it uses the bundle indicated by that DIV.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class Localization
{
    public static final String GVCLocalizedFileNameVariable = "localizedFileName";
    public static final String GVCLocalizedBundleVariable = "localizedBundle";
    public static final String GVCDefaultLocalizedFileName = "Localizable";


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private Localization()
    {
        super();
    }



    /**
     * Helper method to support <code>localizedStringForString</code> and
     * <code>localizedStringExistsForString</code>.<p>
     *
     * Four string files are searched to determine the localized version of the string (all are postpended with the ".strings" extension):
     * <ol>
     *   <li>a file with the same name as <code>anObject</code>'s class
     *   <li>a file of the same name as the bundle that <code>anObject</code>'s class is in (as determined by the NSExecutable key in the Info plist for the class's bundle)
     *   <li>a file named "Localizable"
     *   <li>a file named by the DIV GVCLocalizedFileNameVariable
     * </ol>
     * The search is done in <code>anObject</code>'s bundle, unless a DIV exists with the <code>GVCLocalizedBundleVariable</code> key, in which case, it uses the bundle indicated by that DIV.
     *
     * @param anObject the object that controls where to look for the localized string
     * @param stringToLocalize the string that this will localize
     * @return the localized string or <code>null</code> if it could not be found
     */
    protected static String findLocalizedStringForString(Object anObject, String stringToLocalize)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_stringToLocalize_param] stringToLocalize != null; **/

        String localizedString = null;
        NSBundle localBundle;

        if (DynamicInstanceVariable.instanceVariableExists(anObject, GVCLocalizedBundleVariable))
        {
            localBundle = (NSBundle)DynamicInstanceVariable.instanceVariable(anObject, GVCLocalizedBundleVariable);
        }
        else
        {
            localBundle = NSBundle.bundleForClass(anObject.getClass());
        }

        // First look in a file with the same name as the class of anObject
        if (NSBundleAdditions.localizedStringExistsForKey(localBundle, stringToLocalize, ClassAdditions.unqualifiedClassName(anObject.getClass())))
        {
            localizedString = NSBundleAdditions.localizedStringForKey(localBundle, stringToLocalize, ClassAdditions.unqualifiedClassName(anObject.getClass()));
        }

        // Then look in a file with the same name as the bundle
        if (localizedString == null)
        {
            String classAppName = (String)localBundle.name();
            if (NSBundleAdditions.localizedStringExistsForKey(localBundle, stringToLocalize, classAppName))
            {
                localizedString = NSBundleAdditions.localizedStringForKey(localBundle, stringToLocalize, classAppName);
            }
        }

        // Then look in the default file name
        if (localizedString == null)
        {
            if (NSBundleAdditions.localizedStringExistsForKey(localBundle, stringToLocalize, GVCDefaultLocalizedFileName))
            {
                localizedString = NSBundleAdditions.localizedStringForKey(localBundle, stringToLocalize, GVCDefaultLocalizedFileName);
            }
        }

        // Finally look in the file noted by the DIV GVCLocalizedFileName
        if (localizedString == null)
        {
            if (DynamicInstanceVariable.instanceVariableExists(anObject, GVCLocalizedFileNameVariable))
            {
                String divFileName = (String)DynamicInstanceVariable.instanceVariable(anObject, GVCLocalizedFileNameVariable);
    
                if (NSBundleAdditions.localizedStringExistsForKey(localBundle, stringToLocalize, divFileName))
                {
                    localizedString = NSBundleAdditions.localizedStringForKey(localBundle, stringToLocalize, divFileName);
                }
            }
        }

        return localizedString;
    }



    /**
     * Determines if a localized version of the passed string can be located.
     *
     * @param anObject the object that controls where to look for the localized string.
     * @param stringToLocalize the string for which to search.
     * @return <code>true</code> if the string <code>stringToLocalize</code> exists in any of the string files that we search, <code>false</code> otherwise.
     */
    public static boolean localizedStringExistsForString(Object anObject, String stringToLocalize)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_stringToLocalize_param] stringToLocalize != null; **/

        return findLocalizedStringForString(anObject, stringToLocalize) != null;
    }



    /**
     * Returns a localized version of the passed string.
     *
     * @param anObject the object that controls where to look for the localized string
     * @param stringToLocalize the string that this will localize
     * @return the localized version of <code>stringToLocalize</code>
     */
    public static String localizedStringForString(Object anObject, String stringToLocalize)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_stringToLocalize_param] stringToLocalize != null;
        localizedStringExistsForString(anObject, stringToLocalize); **/

        return findLocalizedStringForString(anObject, stringToLocalize);

        /** ensure [valid_result] Result != null; **/
    }



}
