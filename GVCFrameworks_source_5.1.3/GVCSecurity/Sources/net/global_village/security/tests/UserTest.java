package net.global_village.security.tests;


import junit.framework.TestCase;
import net.global_village.foundation.GVCBoolean;
import net.global_village.foundation.ExceptionConverter;
import net.global_village.security.Privilege;
import net.global_village.security.User;
import net.global_village.security.UserGroup;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;


/**
 *
 * @author Copyright (c) 2001-6  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class UserTest extends TestCase
{
    EOEditingContext editingContext;
    
    protected Privilege privilegeOne;
    protected Privilege includedPrivilege;
    protected Privilege privilegeNotInAnyGroup;
    protected User testUser;
    protected SimpleTestUser testUser2;


    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public UserTest(String name)
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

        privilegeOne = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                         "GVCPrivilege",
                                                                         "name",
                                                                         SecurityTestNameConstants.kPrivilegeOneName);

        includedPrivilege = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                              "GVCPrivilege",
                                                                              "name",
                                                                              SecurityTestNameConstants.kPrivilegeFourName);

        privilegeNotInAnyGroup = (Privilege) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                   "GVCPrivilege",
                                                                                   "name",
                                                                                   SecurityTestNameConstants.kPrivilegeFiveName);

        testUser = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                "GVCUser",
                                                                "userName",
                                                                SecurityTestNameConstants.kTestUserName);

        testUser2 = (SimpleTestUser) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                           "SimpleTestUser",
                                                                           "userName",
                                                                           SecurityTestNameConstants.kTestUserName2);
    }



    /**
     * Test userExists()
     */
    public void testUserExists()
    {
        //Test userExists(String aUserName, String entityName, EOEditingContext ec)
        assertTrue("User.userExists(SecurityTestNameConstants.kTestUserName, editingContext)",
                User.userExists(SecurityTestNameConstants.kTestUserName, editingContext));

        assertTrue("User.userExists(SecurityTestNameConstants.kTestUserName, editingContext, \"GVCUser\")",
                User.userExists(SecurityTestNameConstants.kTestUserName, editingContext, "GVCUser"));

        assertFalse("User.userExists(SecurityTestNameConstants.kTestUserName, editingContext, \"SimpleTestUser\")",
                User.userExists(SecurityTestNameConstants.kTestUserName, editingContext, "SimpleTestUser"));

        assertTrue("User.userExists(SecurityTestNameConstants.kTestUserName2, editingContext)",
                User.userExists(SecurityTestNameConstants.kTestUserName2, editingContext));

        assertTrue("User.userExists(SecurityTestNameConstants.kTestUserName2, editingContext, \"SimpleTestUser\")",
                User.userExists(SecurityTestNameConstants.kTestUserName2, editingContext, "SimpleTestUser"));

        // Error conditions
        assertTrue("Incorrect userName",  ! (User.userExists("Incorrect userName", 
                                   editingContext)));

        assertFalse("User.userExists(SecurityTestNameConstants.kTestUserName, editingContext, \"SimpleTestUser\")",
                User.userExists(SecurityTestNameConstants.kTestUserName, editingContext, "SimpleTestUser"));

        // Test DBC preconditions
        try
        {
            User.userExists(null, editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.userExists("", editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.userExists(SecurityTestNameConstants.kTestUserName, null);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        //Test userExists(String aUserName, String aPassword, String entityName, EOEditingContext ec)
        assertTrue("userExists test for SecurityTestNameConstants.kTestUserName, SecurityTestNameConstants.kTestPassword", User.userExists(SecurityTestNameConstants.kTestUserName, 
                                                  SecurityTestNameConstants.kTestPassword,
                                                  editingContext));

        assertTrue("userExists test for SecurityTestNameConstants.kTestUserName2 SecurityTestNameConstants.kTestPassword2", User.userExists(SecurityTestNameConstants.kTestUserName2, 
                               SecurityTestNameConstants.kTestPassword2,
                               editingContext));

        // Error conditions
        assertTrue("userExists(user,password) with incorrect password",
               ! (User.userExists(SecurityTestNameConstants.kTestUserName, 
                                   "Incorrect Password",
                                   editingContext)));

        assertTrue("userExists(user,password) with incorrect username",
               ! (User.userExists("Incorrect userName", 
                                   SecurityTestNameConstants.kTestPassword,
                                   editingContext)));

        assertTrue("userExists(user, password) with incorrect username and password",
               ! (User.userExists("Incorrect userName", 
                                   "Incorrect Password",
                                   editingContext)));

        // Test DBC preconditions
        try
        {
            User.userExists(null, SecurityTestNameConstants.kTestPassword, editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.userExists("", SecurityTestNameConstants.kTestPassword, editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.userExists(SecurityTestNameConstants.kTestUserName, null, editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.userExists(SecurityTestNameConstants.kTestUserName, "", editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.userExists(SecurityTestNameConstants.kTestUserName, SecurityTestNameConstants.kTestPassword, null);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }        
    }



    /**
     * Test user()
     */
    public void testUser()
    {
        //Test user(String aUserName, EOEditingContext ec)
        assertEquals(testUser, User.user(SecurityTestNameConstants.kTestUserName, editingContext));

        assertEquals(testUser, User.user(SecurityTestNameConstants.kTestUserName, editingContext, "GVCUser"));

        assertEquals(testUser2, User.user(SecurityTestNameConstants.kTestUserName2, editingContext));

        assertEquals(testUser2, User.user(SecurityTestNameConstants.kTestUserName2, editingContext, "SimpleTestUser"));

        // Error conditions
        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, editingContext, "SimpleTestUser");
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user("Incorrect Username", editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        // Test DBC preconditions
        try
        {
            User.user(null, editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user("", editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, null, editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, "", editingContext);
            fail("userExists failed to raise exception");
        } catch(RuntimeException e) { }


        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, null);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        //Test user(String aUserName, String aPassword, String entityName, EOEditingContext ec)
        assertEquals(User.user(SecurityTestNameConstants.kTestUserName,
                               SecurityTestNameConstants.kTestPassword,
                               editingContext), testUser);

        assertEquals(User.user(SecurityTestNameConstants.kTestUserName2,
                               SecurityTestNameConstants.kTestPassword2,
                               editingContext), testUser2);

        // Error conditions
        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, 
                      "Incorrect Password",
                      editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user("Incorrect Username", 
                      SecurityTestNameConstants.kTestPassword,
                      editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user("Incorrect Username", 
                      "Incorrect Password",
                      editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        // Test DBC preconditions
        try
        {
            User.user(null, SecurityTestNameConstants.kTestPassword, editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user("", SecurityTestNameConstants.kTestPassword, editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, null, editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }

        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, "", editingContext);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }


        try
        {
            User.user(SecurityTestNameConstants.kTestUserName, SecurityTestNameConstants.kTestPassword, null);
            fail("user failed to raise exception");
        } catch(RuntimeException e) { }        
    }



    /**
     * Test hasPrivilege()
     */
    public void testHasPrivilege()
    {
        assertTrue(testUser.hasPrivilege(privilegeOne));
        assertTrue(testUser.hasPrivilege(includedPrivilege));
        assertTrue( ! (testUser.hasPrivilege(privilegeNotInAnyGroup)));

        // Test DBC preconditions
        try
        {
            testUser.hasPrivilege(null);
            fail("testUser.hasPrivilege(null) failed to raise exception");
        }
        catch(Exception e) { }
    }



    /**
     * Test hasPrivilegeNamed()
     */
    public void testHasPrivilegeNamed()
    {
        assertTrue(testUser.hasPrivilegeNamed(privilegeOne.name()));
        assertTrue(testUser.hasPrivilegeNamed(includedPrivilege.name()));
        assertTrue( ! (testUser.hasPrivilegeNamed(privilegeNotInAnyGroup.name())));

        // Test DBC preconditions
        try
        {
            testUser.hasPrivilegeNamed(null);
            fail("testUser.hasPrivilegeNamed(null) failed to raise exception");
        } catch(Exception e) { }

        try
        {
            testUser.hasPrivilegeNamed("Name of a privilege that does not exist");
            fail("testUser.hasPrivilegeNamed(Name of a privilege that does not exist) failed to raise exception");
        } catch(Exception e) { }
    }


     
    /**
     * Test validateUserName()
     */
    public void testValidateUserName()
    {
        // Test that userName must be unique
        User userOne = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                    "GVCUser",
                                                                    "userName",
                                                                    SecurityTestNameConstants.kTestNameOne);
        testUser.setUserName(userOne.userName());
        try
        {
            editingContext.saveChanges();

            // If that didn't fail, set it back for next time... (this doesn't seem to actually work).
            testUser.setUserName(SecurityTestNameConstants.kTestNameOne);
            editingContext.saveChanges();

            fail("validateUserName test failed to raise exception");
        } catch(Exception e) { }
    }



    /**
     * Test validateForSave()
     */
    public void testValidateForSave()
    {
        // Test that an invalid state is not possible: mustChangePassword may only be true when canChangePassword is false;
        testUser.setUserName(SecurityTestNameConstants.kTestUserName);
        testUser.setPassword("bogus");
        testUser.setMustChangePassword(new GVCBoolean(true));
        testUser.setCanChangePassword(new GVCBoolean(false));

        try
        {
            editingContext.saveChanges();
            fail("validateForSave test failed to raise exception");
        } catch(Exception e) { }
    }



     /**
      * Test compare()
      */
     public void testcompare()
     {
         NSArray allUsers = EOUtilities.objectsForEntityNamed(editingContext, "GVCUser");
         NSArray sortedUsers;

         try
         {
             sortedUsers = allUsers.sortedArrayUsingComparator(User.DefaultComparator);
         }
         catch (com.webobjects.foundation.NSComparator.ComparisonException e)
         {
             throw new ExceptionConverter(e);
         }

         User nameOne = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                     "GVCUser",
                                                                     "userName",
                                                                     SecurityTestNameConstants.kTestNameOne);
         User nameThree = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                       "GVCUser",
                                                                       "userName",
                                                                       SecurityTestNameConstants.kTestNameThree);
         User nameTwo = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                     "GVCUser",
                                                                     "userName",
                                                                     SecurityTestNameConstants.kTestNameTwo);
         assertTrue(sortedUsers.indexOfObject(nameOne) < sortedUsers.indexOfObject(nameThree));
         assertTrue(sortedUsers.indexOfObject(nameThree) < sortedUsers.indexOfObject(nameTwo));
     }



    /**
     * Test functionality of the changePasswordTo method
     *
     */
    public void testChangePasswordTo()
    {
        testUser = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                "GVCUser",
                                                                "userName",
                                                                "Test User");
        String oldPassword = testUser.password();
        String newPassword = "new password";

        try
        {
            testUser.changePasswordTo(oldPassword);
            fail("changePasswordTo failed to raise exception");
        } catch(Exception e) { }

        try
        {
            testUser.changePasswordTo(null);
            fail("changePasswordTo failed to raise exception");
        } catch(Exception e) { }

        testUser.changePasswordTo(newPassword);
        editingContext.saveChanges();

        assertTrue( ! testUser.password().equals(oldPassword));
        assertEquals(testUser.password(), newPassword);
        assertTrue( ! testUser.mustChangePassword().booleanValue());

        // Return the database to the proper state
        testUser.changePasswordTo(oldPassword);
        editingContext.saveChanges();
    }



    /**
     * Test functionality of the usersInGroups() method
     *
     */
    public void testUsersInGroups()
    {
        // have testUser
        User testUserOne = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                        "GVCUser",
                                                                        "userName",
                                                                        SecurityTestNameConstants.kTestNameOne);
        User testUserTwo = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                        "GVCUser",
                                                                        "userName",
                                                                        SecurityTestNameConstants.kTestNameTwo);
        User testUserThree = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                          "GVCUser",
                                                                          "userName",
                                                                          SecurityTestNameConstants.kTestNameThree);
        User disabledTestUser = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                             "GVCUser",
                                                                             "userName",
                                                                             SecurityTestNameConstants.kDisabledUserName);
        UserGroup groupOne = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                               "GVCUserGroup",
                                                                               "name",
                                                                               SecurityTestNameConstants.kGroupOneName);
        UserGroup groupFour = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                                "GVCUserGroup",
                                                                                "name",
                                                                                SecurityTestNameConstants.kGroupFourName);
        NSArray arrayOfGroups = new NSArray(new UserGroup[] {groupOne, groupFour});
        NSArray expectedResults = new NSArray(new User[] { testUserOne, testUserTwo, testUserThree, disabledTestUser});
        NSArray actualResults = User.usersInGroups(arrayOfGroups);

        assertTrue("testUserOne is not found", actualResults.containsObject(testUserOne));
        assertTrue("testUserTwo is not found", actualResults.containsObject(testUserTwo));
        assertTrue("testUserThree is not found", actualResults.containsObject(testUserThree));
        assertTrue("disabledTestUser is not found", actualResults.containsObject(disabledTestUser));
        assertEquals("expectedResults count equals actualResults count ", expectedResults.count(), actualResults.count());
    }


    /**
      * Test activeUsers()
      */
     public void testActiveUsers()
    {
         // Now have added a disabled user to group four! and these tests for the static version of the method
         User disabledTestUser = (User) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                              "GVCUser",
                                                                              "userName",
                                                                              SecurityTestNameConstants.kDisabledUserName);
         NSArray users = new NSArray(new User[] {testUser, disabledTestUser});
         NSArray expectedResults = new NSArray(new User[] {testUser});
         NSArray results = User.activeUsers(expectedResults);
         assertEquals("all users were active", results, expectedResults);
         results = User.activeUsers(users);
         assertTrue("all users are still active", results.count() == expectedResults.count());
         results = User.activeUsers(new NSArray());
         assertTrue("empty array in empty array out", results.count() == 0);
    }
}
