package net.global_village.eofextensions.tests;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.gvcjunit.*;


/**
 * Test the ModelConnector functionality.
 *
 * @author Copyright (c) 2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */
public class ModelConnectorTest extends GVCJUnitEOTestCase
{
    public static final String CONNECTION_DICTIONARIES_KEY = "connectionDictionaries";
    public static final String DATABASE_TYPE_KEY = "databaseToUse";
    public static final String MODELS_TO_IGNORE_KEY = "modelsToIgnore"; 
    public static final String TEST_CONFIGURATION_FILE = "TestConfiguration.plist";

    protected NSDictionary testConfigurationDictionary;


    /*
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
     public ModelConnectorTest(String name)
     {
         super(name);
     }



     /**
      * Returns the configuration dictionary for this test run.  This is used to 
      * implement the other methods in this class.  This method can be overridden to
      * get the dictionary from some other source, or the other methods can be 
      * re-implemented in a different manner.  By default, this class gets the
      * dictionary from the TestConfiguration.plist of the framework in 
      * the working directory when the tests are run.<br>
      * <br>
      * The dictionary is expected to have this format:
      * <code>
      * {
      *     databaseToUse = "<database type>";
      *     connectionDictionaries = {
      *         <database type1> = {
      *             URL = ...
      *             etc
      *         };
      *         <database type2> = {
      *             URL = ...
      *             etc
      *         };
      *     };
      *
      *     modelsToIgnore = ();
      * };
      * </code>
      *
      * @return the configuration dictionary for this test run
      */
     protected NSDictionary testConfigurationDictionary()
     {
         if (testConfigurationDictionary == null)
         {
             // Load the defaults first    
             NSBundle bundle = NSBundleAdditions.bundleForClass(EOTestSuite.class);
             NSDictionary configDictionary = NSBundleAdditions.tableWithName(bundle, TEST_CONFIGURATION_FILE);
                  
             // Allow overrides from the framework being tested.
             bundle = NSBundle.mainBundle();
             if (NSBundleAdditions.tableExistsWithName(bundle, TEST_CONFIGURATION_FILE))
             {
                 configDictionary = new NSMutableDictionary(configDictionary);
                 ((NSMutableDictionary)configDictionary).addEntriesFromDictionary(
                 NSBundleAdditions.tableWithName(bundle, TEST_CONFIGURATION_FILE));
             }

             // Validate dictionary contents
             if (configDictionary.objectForKey(DATABASE_TYPE_KEY) == null)
             {
                 throw new IllegalArgumentException(DATABASE_TYPE_KEY + 
                     " key missing from testConfigurationDictionary()");
             }
             
             if (configDictionary.objectForKey(CONNECTION_DICTIONARIES_KEY) == null)
             {
                 throw new IllegalArgumentException(CONNECTION_DICTIONARIES_KEY + 
                     " key missing from testConfigurationDictionary()");
             }
             
             if (((NSDictionary)configDictionary.objectForKey(CONNECTION_DICTIONARIES_KEY)).
                 objectForKey(configDictionary.objectForKey(DATABASE_TYPE_KEY)) == null)
             {
                 throw new IllegalArgumentException(DATABASE_TYPE_KEY + " '" + 
                     configDictionary.objectForKey(configDictionary.objectForKey(DATABASE_TYPE_KEY)) + 
                     "' has no corresponding entry in " +
                     CONNECTION_DICTIONARIES_KEY + " from testConfigurationDictionary()");
             }
             
             if (configDictionary.objectForKey(MODELS_TO_IGNORE_KEY) == null)
             {
                 throw new IllegalArgumentException(MODELS_TO_IGNORE_KEY + 
                     " key missing from testConfigurationDictionary()");
             }
             
             testConfigurationDictionary = configDictionary;
         }
         
         return testConfigurationDictionary;
         
         /** ensure
         [valid_result] Result != null;
         [database_type_key_is_present] Result.objectForKey(DATABASE_TYPE_KEY) != null;
         [connection_dictionaries_key_is_present] Result.objectForKey(CONNECTION_DICTIONARIES_KEY) != null;
         [database_type_has_connection_dictionary] ((NSDictionary)Result.objectForKey(CONNECTION_DICTIONARIES_KEY)).
                 objectForKey(Result.objectForKey(DATABASE_TYPE_KEY)) != null;
         [modelsToIgnore_key_is_present] Result.objectForKey(MODELS_TO_IGNORE_KEY) != null; **/
     }


     /**
      * Returns a list of the model names to <b>not</b>connect using connectionDictionary().  This
      * can be used if some of the models that will be loaded are not needed for testing, or need
      * to be connected to a different database.
      * 
      * @return a list of the model names to <b>not</b>connect using connectionDictionary()
      */
     protected NSArray modelNamesToIgnore()
     {
         return (NSArray) testConfigurationDictionary().objectForKey(MODELS_TO_IGNORE_KEY);

         /** ensure [valid_result] Result != null; **/
     }


     /**
      * Returns the database type (e.g. DB2, Oracle, FrontBase, OpenBase, mySQL etc.)
      * that is to be used for this test run.  This should match the database name 
      * as used in the EO<database name>Prototypes prototypes entity for this 
      * database and also the the name used in testConfigurationDictionary()if that 
      * is relevant.  Remeber - case counts!  This class gets the database type from
      * the DATABASE_TYPE_KEY in testConfigurationDictionary(). 
      *
      * @return the database type that is to be used for this test run
      */
     protected String databaseType()
     {
         return (String) testConfigurationDictionary().objectForKey(DATABASE_TYPE_KEY);
         /** ensure [valid_result] Result != null;  **/
     }



     /**
      * Returns the connection dictionary to be used in this test run for all EOModels 
      * in this Suite of tests.  This can be overridden in sub-classes if a different 
      * connection dictionary is needed.  This class gets the connection dictionary 
      * from the DATABASE_TYPE_KEY in testConfigurationDictionary(). 
      *
      * @return the connection dictionary for all EOModels used in this Suite of tests.
      */
     protected NSDictionary connectionDictionary()
     {
         NSDictionary connectionDictionaries =
             (NSDictionary) testConfigurationDictionary().objectForKey(CONNECTION_DICTIONARIES_KEY); 

         return (NSDictionary) connectionDictionaries.objectForKey(databaseType());

         /** ensure
         [valid_result] Result != null;
         [url_key_is_present] Result.objectForKey("URL") != null; **/
     }



     /**
     * Test connectModelsNamed
     */
    public void testConnectModelsNamed()
    {
        NSBundle bundle = NSBundle.bundleForClass(getClass());

        EOModelGroup modelGroup = new EOModelGroup();
        new ModelConnector(connectionDictionary(), NSArray.EmptyArray, modelGroup);
        modelGroup.addModelWithPathURL(bundle.pathURLForResourcePath("TestModel1.eomodeld"));
        
        // Disregard jdbc2info
        NSMutableDictionary testModel1Dictionary = new NSMutableDictionary( modelGroup.modelNamed("TestModel1").connectionDictionary());
        testModel1Dictionary.removeObjectForKey("jdbc2info");
        
        assertEquals(connectionDictionary(), testModel1Dictionary);
    }



}
