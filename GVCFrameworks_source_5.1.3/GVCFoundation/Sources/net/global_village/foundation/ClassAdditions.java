package net.global_village.foundation;

import com.webobjects.foundation.NSArray;


/**
 * Class utilities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class ClassAdditions
{


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private ClassAdditions()
    {
        super();
    }



    /**
     * Returns true if the params represent the same class, irrespective of class loaders.
     *
     * @param class1 one of the classes to compare
     * @param class2 one of the classes to compare
     * @return <code>true</code> if the two classes represent the same classes (even if they were loaded by different ClassLoaders), <code>false</code> otherwise
     */
    public static boolean classesAreEqual(java.lang.Class class1, java.lang.Class class2)
    {
        /** require [valid_class1_param] class1 != null; [valid_class2_param] class2 != null; **/

        // WOW, lame as this seems, its the only thing that seems to work...
        return class1.toString().equals(class2.toString());
    }



    /**
     * Returns true if the params represent the same class.
     *
     * @param classString1 one of the classes to compare
     * @param classString2 one of the classes to compare
     * @return <code>true</code> if the two classes represent the same classes (even if they were loaded by different ClassLoaders), <code>false</code> otherwise
     */
    public static boolean classesAreEqual(String classString1, String classString2)
    {
        /** require
        [valid_classString1_param] classString1 != null;
        [valid_classString2_param] classString2 != null; **/

        return classString1.equals(classString2);
    }



    /**
     * Convenience method for getting just the classname (not fully qualified).
     *
     * @param aClass the Class whose unqualified name we are getting
     * @return <code>aClass</code>'s unqualified name (e.g. String vs java.lang.String)
     */
    public static String unqualifiedClassName(java.lang.Class aClass)
    {
        /** require [valid_param] aClass != null; **/

        NSArray classNameComponents = NSArray.componentsSeparatedByString(aClass.getName(), ".");
        return (String)classNameComponents.lastObject();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * A hack to get around a limitation in Jass that doesn't allow the NSArray.class construction.  This method uses <code>Class.forName()</code> and assumes that the class name passed in exists.  A <code>RuntimeException</code> is thrown if it doesn't.
     *
     * @param className the class name to return
     * @return the class represented by the param
     * @exception RuntimeException if the class doesn't exist
     */
    public static Class unsafeClassForName(String className)
    {
        /** require [valid_param] className != null; **/

        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new ExceptionConverter(e);
        }

        /** ensure [valid_result] Result != null; **/
    }



}
