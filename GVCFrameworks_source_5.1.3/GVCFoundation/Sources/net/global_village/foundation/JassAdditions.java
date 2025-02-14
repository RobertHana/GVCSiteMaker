package net.global_village.foundation;


/**
 * Jass utilities.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */
public class JassAdditions
{


    /**
     * Private constructor because this object should never be instantiated.
     */
    private JassAdditions()
    {
        super();
    }



    /**
     * Throws a <code>RuntimeException</code> if <code>condition</code> is <code>false</code>.  Use this method when a static method calls an instance method in the precondition (which isn't allowed in Jass).  Note: This method throws java.lang.RuntimeException rather than jass.runtime.PreconditionException so that the frameworks can be built without Jass being present.
     *
     * @param className the class in which the precondition was tested
     * @param methodName the method in which the precondition was tested
     * @param condition the precondition to test
     */
    public static void check(String className, String methodName, boolean condition)
    {
        if ( ! condition)
            throw new RuntimeException("CHECK: " + className + "." + methodName + "()");
    }



    /**
     * Throws a <code>RuntimeException</code> if <code>condition</code> is <code>false</code>.  Use this method when a static method calls an instance method in the precondition (which isn't allowed in Jass).  Note: This method throws java.lang.RuntimeException rather than jass.runtime.PreconditionException so that the frameworks can be built without Jass being present. 
     *
     * @param className the class in which the precondition was tested
     * @param methodName the method in which the precondition was tested
     * @param condition the precondition to test
     */
    public static void pre(String className, String methodName, boolean condition)
    {
        if ( ! condition)
            throw new RuntimeException("PRE-CONDITION: " + className + "." + methodName + "()");
    }



    /**
     * Throws a <code>PostconditionException</code> if <code>condition</code> is <code>false</code>.  Use this method when a static method calls an instance method in the postcondition (which isn't allowed in Jass).  Note: This method throws java.lang.RuntimeException rather than jass.runtime.PreconditionException so that the frameworks can be built without Jass being present. 
     *
     * @param className the class in which the precondition was tested
     * @param methodName the method in which the precondition was tested
     * @param condition the precondition to test
     */
    public static void post(String className, String methodName, boolean condition)
    {
        if ( ! condition)
            throw new RuntimeException("POST-CONDITION: " + className +  "." + methodName + "()");
    }



}
