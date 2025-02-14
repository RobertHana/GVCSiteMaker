package net.global_village.gvcjunit;

import junit.framework.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;


/**
 * TestCase that provides an editing context.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class GVCJUnitEOTestCase extends TestCase
{
    protected EOEditingContext editingContext;


    /**
     * Designated constructor.
     * @param testCase name of the test case.
     */
    public GVCJUnitEOTestCase(String testCase)
    {
        super(testCase);
    }



	/**
 	 * Creates an ForgetfulEC for use as <code>editingContext()</code>, locks it,
     * and sets it to continue validation after an errror.
 	 */
    protected void setUp() throws java.lang.Exception
    {
        super.setUp();
        editingContext = new ForgetfulEC();
		editingContext.lock();
        editingContext.setStopsValidationAfterFirstError(false);
        /** ensure [has_ec] editingContext != null;
                   [continues_validation] ! editingContext.stopsValidationAfterFirstError();  **/
    }



    /**
     * Reverts, unlocks, and disposes of editingContext().
     */
	protected void tearDown()  throws java.lang.Exception
	 {
		editingContext.revert();
		editingContext.unlock();
        editingContext.dispose();
		editingContext = null;
		super.tearDown();
        /** ensure [has_no_ec] editingContext == null;   **/
	}



    /**
     * Returns the <code>EOEditingContext</code> to use for tests in this case.
     * This is locked for you (@see setUp()).
     *
     * @return the <code>EOEditingContext</code> to use for tests in this case
     */
    public EOEditingContext editingContext()
    {
        return editingContext;
        /** ensure [has_ec] Result != null;   **/
    }



    /**
     * Generates a globally unique string.
     */
    public String globallyUniqueString()
    {
        String localIP = null;
        String uniqueString = null;
        String uniqueThang = null;

        try
        {
            localIP = java.net.InetAddress.getLocalHost().getHostAddress();
        }
        catch (java.net.UnknownHostException e)
        {
            throw new RuntimeException("GVCJUnitEOTestCase.globallyUniqueString() couldn't get IP!");
        }

        uniqueThang = (new java.rmi.server.UID()).toString(); // nasty, but seems to work ok
        uniqueString = localIP + " " + uniqueThang;

        return uniqueString;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Saves changes on <code>editingContext</code> and calls fail <code>fail(message)</code>
     * if an exception is thrown.
     *
     * @param message the message to pass to <code>fail()</code>
     */
    public void saveChangesShouldntThrow(String message)
    {
        /** require [valid_param] message != null; **/

        try
        {
            editingContext.saveChanges();
        }
        catch (Exception e)
        {
            NSLog.out.appendln(e);
            fail(message);
        }
    }



    /**
     * Saves changes on <code>editingContext</code> and calls fail <code>fail(message)</code>
     * if an exception is not thrown.
     *
     * @param message the message to pass to <code>fail()</code>
     */
    public void saveChangesShouldThrow(String message)
    {
        /** require [valid_param] message != null; **/

        try
        {
            editingContext.saveChanges();
            fail(message);
        }
        catch (Exception e) { }
    }

}
