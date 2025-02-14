package net.global_village.security.tests;

import junit.framework.TestCase;
import net.global_village.security.Privilege;
import net.global_village.security.User;
import net.global_village.security.UserGroup;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class UserGroupTest extends TestCase
{
    protected EOEditingContext editingContext;

    protected User userOne;
    protected User userTwo;
    
    protected Privilege privilegeOne;
    protected Privilege privilegeTwo;
    protected Privilege privilegeThree;

    protected UserGroup userGroupOne;

    /**
     * Designated constructor.
     *
     * @param name	the method name of the test to be initialized
     */
    public UserGroupTest(String name)
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

        userGroupOne = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                         "GVCUserGroup",
                                                                         "name",
                                                                         SecurityTestNameConstants.kGroupOneName);
        
        userOne = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                               "GVCUser",
                                                               "userName",
                                                               SecurityTestNameConstants.kTestNameOne);
        userTwo = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                               "GVCUser",
                                                               "userName",
                                                               SecurityTestNameConstants.kTestNameTwo);

        privilegeOne = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                         "GVCPrivilege",
                                                                         "name",
                                                                         SecurityTestNameConstants.kPrivilegeOneName);
        privilegeTwo = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                         "GVCPrivilege",
                                                                         "name",
                                                                         SecurityTestNameConstants.kPrivilegeTwoName);
        privilegeThree = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                           "GVCPrivilege",
                                                                           "name",
                                                                           SecurityTestNameConstants.kPrivilegeThreeName);
    }



    /**
     * Test userGroupExists()
     */
    public void testUserGroupExists()
    {
        // occaisionally get errors on this test but usually sweeping will get rid of them.
        assertTrue( ! UserGroup.userGroupExists("Some ficticious group that does not exist", editingContext));
        assertTrue(UserGroup.userGroupExists(userGroupOne.name(), editingContext));

        // Test DBC
        try { UserGroup.userGroupExists(null, editingContext); fail("DBC failed to catch a null string name parameter"); }
        catch (RuntimeException e) {  }

        try { UserGroup.userGroupExists("", editingContext); fail("DBC failed to catch an empty string name parameter");
        } catch (RuntimeException e) { }

        try { UserGroup.userGroupExists(userGroupOne.name(), null); fail("DBC failed to catch a null editing context parameter");
        } catch (Exception e) { }
    }

    

    /**
     * Test userGroup() 
     */
    public void testUserGroup()
    {  
        // null is a valid result now, this old test fails a new precondition
        try
        {
            UserGroup.userGroup("Some ficticious group that does not exist", editingContext);
            fail("Fetching a non existant UserGroup failed to raise an exception");
        }
        catch (RuntimeException e) { }
        assertEquals(UserGroup.userGroup(userGroupOne.name(), editingContext), userGroupOne);

        // Test DBC
        try { UserGroup.userGroup(null, editingContext); fail("DBC failed to catch a null string name parameter"); }
        catch (RuntimeException e) {  }

        try { UserGroup.userGroup("", editingContext); fail("DBC failed to catch an empty string name parameter");
        } catch (RuntimeException e) { }

        try { UserGroup.userGroup(userGroupOne.name(), null); fail("DBC failed to catch a null editing context parameter");
        } catch (Exception e) { }
    }

    

    /**
     * Test hasAsMember()
     */
    public void testHasAsMember()
    {
        //Test for inclusion and exclusion
        assertTrue(userGroupOne.hasAsMember(userOne));
        assertTrue( ! userGroupOne.hasAsMember(userTwo));
        
       // Test DBC preconditions
        try { userGroupOne.hasAsMember(null); fail("DBC did not catch null user parameter"); }
        catch (Exception e) { }
    }



    /**
     * Test hasPrivilege()
     */
    public void testHasPrivilege()
    {
        // Test inclusion and exclusion
        assertTrue(userGroupOne.hasPrivilege(privilegeOne));
        assertTrue( ! userGroupOne.hasPrivilege(privilegeThree));

        // Test for an included privilege
        assertTrue(userGroupOne.hasPrivilege(privilegeTwo));

        // Test DBC
        try { userGroupOne.hasPrivilege(null); fail("DBC failed to catch null privilege parameter"); }
        catch (Exception e) {}
    }



    /**
     * Test compareByName()
     */
    public void testCompareByName()
    {
        // additional groups to compare to
        UserGroup userGroupTwo = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                   "GVCUserGroup",
                                                                                   "name",
                                                                                   SecurityTestNameConstants.kGroupTwoName);
        UserGroup userGroupThree = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                     "GVCUserGroup",
                                                                                     "name",
                                                                                     SecurityTestNameConstants.kGroupThreeName);
        UserGroup userGroupFour = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                    "GVCUserGroup",
                                                                                    "name",
                                                                                    SecurityTestNameConstants.kGroupFourName);
        // new style tests
        assertTrue(userGroupFour.compareByName(userGroupOne) < 0);
        assertTrue(userGroupOne.compareByName(userGroupThree) < 0);
        assertTrue(userGroupThree.compareByName(userGroupTwo) < 0);
        // negative test
        assertTrue( ! (userGroupFour.compareByName(userGroupTwo) > 0));
        // old style tests  
        assertTrue(userGroupOne.compareByName(userGroupTwo) < 0);
        assertTrue(userGroupOne.compareByName(userGroupOne) == 0);
        assertTrue(userGroupTwo.compareByName(userGroupOne) > 0);
        // Test DBC
        try { userGroupOne.compareByName(null); fail("DBC failed to catch null parameter"); }
        catch (Exception e) { }
    }



    /**
     * Test eoShallowDescription()
     */
    public void testEOShallowDescription()
    {
        assertTrue(userGroupOne.eoShallowDescription().equals(SecurityTestNameConstants.kGroupOneName));
        assertTrue( ! userGroupOne.eoShallowDescription().equals(SecurityTestNameConstants.kGroupTwoName));
    }


    
    /**
      * Test activeUsers()
      */
    public void testActiveUsers()
    {
         // userGroupOne contains userOne who has not has it's account disabled userTwo is not in userGroupOne
         NSArray activeUsersInGroupOne = userGroupOne.activeUsers();
         assertTrue("activeUsersInGroupOne.count() > 0", activeUsersInGroupOne.count() > 0); // only one active user currently
         assertTrue("activeUsersInGroupOne.containsObject(userOne)", activeUsersInGroupOne.containsObject(userOne));
         assertTrue("! activeUsersInGroupOne.containsObject(userTwo)", ! activeUsersInGroupOne.containsObject(userTwo));

         // test on group with more then one member now including a disabled member!
         UserGroup userGroupFour = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                     "GVCUserGroup",
                                                                                     "name",
                                                                                     SecurityTestNameConstants.kGroupFourName);
         NSArray activeUsersInGroupFour = userGroupFour.activeUsers();
         assertTrue("activeUsersInGroupFour.count() == 3", activeUsersInGroupFour.count() == 3);
         assertEquals("activeUsersInGroupFour.objectAtIndex(0).equals(userOne)", activeUsersInGroupFour.objectAtIndex(0), userOne);
         assertEquals("activeUsersInGroupFour.lastObject().equals(userTwo)", activeUsersInGroupFour.lastObject(), userTwo);  // userThree is in the middle
    }


    
    /**
     * Test allUserGroup()
     */
    public void testOrderedListOfAllUserGroups()
    {
        UserGroup userGroupTwo = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                             "GVCUserGroup",
                                                                             "name",
                                                                             SecurityTestNameConstants.kGroupTwoName);
        UserGroup userGroupThree = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                     "GVCUserGroup",
                                                                                     "name",
                                                                                     SecurityTestNameConstants.kGroupThreeName);
        UserGroup userGroupFour = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                    "GVCUserGroup",
                                                                                    "name",
                                                                                    SecurityTestNameConstants.kGroupFourName);
        NSArray sortedGroups = UserGroup.orderedListOfAllUserGroups(editingContext);
        assertTrue(sortedGroups.indexOfObject(userGroupFour) < sortedGroups.indexOfObject(userGroupOne));
        assertTrue(sortedGroups.indexOfObject(userGroupOne) < sortedGroups.indexOfObject(userGroupThree));
        assertTrue(sortedGroups.indexOfObject(userGroupThree) < sortedGroups.indexOfObject(userGroupTwo));
    }
}

