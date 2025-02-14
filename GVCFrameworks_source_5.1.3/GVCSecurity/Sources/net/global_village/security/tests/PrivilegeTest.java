package net.global_village.security.tests;

import junit.framework.TestCase;
import net.global_village.security.Privilege;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;


/**
 * Test privileges.
 *
 * @author Copyright (c) 2001-6  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class PrivilegeTest extends TestCase
{
    protected EOEditingContext editingContext;

    protected Privilege testPrivilege;
    protected Privilege privilegeOne;
    protected Privilege privilegeTwo;


    /**
     * Designated constructor.
     *
     * @param name	the method name of the test to be initialized
     */
    public PrivilegeTest(String name)
    {
        super(name);
    }

    

    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        editingContext = new EOEditingContext();

        testPrivilege = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                          "GVCPrivilege",
                                                                          "name",
                                                                          SecurityTestNameConstants.kTestPrivilegeName);
        privilegeOne = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                         "GVCPrivilege",
                                                                         "name",
                                                                         SecurityTestNameConstants.kPrivilegeOneName);
        privilegeTwo = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                         "GVCPrivilege",
                                                                         "name",
                                                                         SecurityTestNameConstants.kPrivilegeTwoName);
    }



    /**
      * Test privilege()
     */
    public void testPrivilege()
    {
        try { Privilege.privilege("Some privilege that does not exist", editingContext); fail("Exception not thrown when privilege not found."); }
        catch (RuntimeException e) { }

        assertEquals(Privilege.privilege(testPrivilege.name(), editingContext), testPrivilege);

        // Test DBC
        try { Privilege.privilege(null, editingContext); fail("DBC failed to catch a null string name parameter"); }
        catch (RuntimeException e) {  }

        try { Privilege.privilege("", editingContext); fail("DBC failed to catch an empty string name parameter");
        } catch (RuntimeException e) { }

        try { Privilege.privilege(testPrivilege.name(), null); fail("DBC failed to catch a null editing context parameter");
        } catch (Exception e) { }
    }



    /**
     * Test privilegeExists()
     */
    public void testUserGroupExists()
    {
        assertTrue( ! Privilege.privilegeExists("Some ficticious privilege that does not exist", editingContext));
        assertTrue(Privilege.privilegeExists(testPrivilege.name(), editingContext));

        // Test DBC
        try { Privilege.privilegeExists(null, editingContext); fail("DBC failed to catch a null string name parameter"); }
        catch (RuntimeException e) {  }

        try { Privilege.privilegeExists("", editingContext); fail("DBC failed to catch an empty string name parameter");
        } catch (RuntimeException e) { }

        try { Privilege.privilegeExists(testPrivilege.name(), null); fail("DBC failed to catch a null editing context parameter");
        } catch (Exception e) { }
    }



    /**
     * Test includesPrivilege()
     */
    public void testIncludesPrivilege()
    {
        //Test included and non included privileges
        assertTrue(testPrivilege.includesPrivilege(privilegeOne));
        testPrivilege.removeFromIncludedPrivileges(privilegeOne);
        assertTrue( ! testPrivilege.includesPrivilege(privilegeOne));

        // Test that a non included privilege (2) may be added, and shows as being included
        assertTrue( ! testPrivilege.includesPrivilege(privilegeTwo));
        testPrivilege.addToIncludedPrivileges(privilegeTwo);
        assertTrue(testPrivilege.includesPrivilege(privilegeTwo));

        // Test DBC preconditions
        try { testPrivilege.includesPrivilege(null); fail("DBC did not catch null privilege parameter"); }
        catch (Exception e) { }

        // Ensure that no changes to the database are made
        editingContext.revert();
    }



    /**
     * Test compareByName()
     */
    public void testCompareByName()
    {
        assertTrue(privilegeOne.compareByName(privilegeTwo) < 0);
        assertTrue(privilegeOne.compareByName(privilegeOne) == 0);
        assertTrue(privilegeTwo.compareByName(privilegeOne) > 0);

        // Test DBC
        try { privilegeOne.compareByName(null); fail("DBC failed to catch null parameter"); }
        catch (Exception e) { }
    }



    /**
     * Test fetchFromDatabase()
     */
    public void testFetchFromDatabase()
    {
        NSArray allPrivileges = null;

        editingContext.parentObjectStore().invalidateAllObjects();

        // Privilege has a to many relationship with itself, this code checks that a fetch will succeed.
        try { allPrivileges = EOUtilities.objectsForEntityNamed(editingContext, "GVCPrivilege"); }
        catch (RuntimeException e) { fail("Exception raised on fetching privileges."); }

        assertTrue(allPrivileges.containsObject(testPrivilege));
    }



}
