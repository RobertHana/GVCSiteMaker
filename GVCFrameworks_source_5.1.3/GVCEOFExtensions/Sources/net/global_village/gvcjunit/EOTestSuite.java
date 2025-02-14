package net.global_village.gvcjunit;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;



/**
 * TestSuite to facilitate testing of EOEnterpriseObjects.  It handles forcing models to connect with a common connection dictionary and adaptor type and also the installation of GVCValidation.  It only handles tests in which all the EOModels use the same connection dictionary.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 9$
 */
public class EOTestSuite extends TestSuite
{

    public static final String CONNECTION_DICTIONARIES_KEY = "connectionDictionaries";
    public static final String DATABASE_TYPE_KEY = "databaseToUse";
    public static final String MODELS_TO_IGNORE_KEY = "modelsToIgnore"; 
    public static final String TEST_CONFIGURATION_FILE = "TestConfiguration.plist";
        
    protected NSDictionary testConfigurationDictionary;
    protected EOPrototypeSelector prototypeSwitcher;
    

    /**
     * Designated constuctor.  Note that this does <b>not</b> install EOFValidation.  Subclasses should do that in the <code>public static Test suite()</code> method before creating a suite.
     */
    public EOTestSuite()
    {
        super();
        
        performEarlySuiteInitialization();
        
        // Redirect NSLog.debug to NSLog.out so that the debug information will come out in the test window.  This makes it a lot easier to debug tests.
        NSLog.setDebug(NSLog.out);

        // This can help to debug test configuration
        // NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelDetailed);

        // Make sure all the models are using the correct set of prototypes before starting the tests.
        prototypeSwitcher = new EOPrototypeSelector(databaseType());
        prototypeSwitcher.installModelAddedListener();
        
        // Make sure all the models are connected with the same connection dictionary before starting the tests.
        new ModelConnector(connectionDictionary(), modelNamesToIgnore());
    }



    /**
     * Override to add any processing that needs to happen <em>before</em> the processing
     * done by EOTestSuite's constructor.  This was specifically added to support
     * initialization of EOFValidation before connecting the models and switching
     * the prototypes. 
     */
    public void performEarlySuiteInitialization()
    {
    }



    /**
     * Returns the connection dictionary to be used in this test run for all EOModels 
     * in this Suite of tests.  This can be overridden in sub-classes if a different 
     * connection dictionary is needed.  This class gets the connection dictionary 
     * from the DATABASE_TYPE_KEY in testConfigurationDictionary(). 
     *
     * @return the connection dictionary for all EOModels used in this Suite of tests.
     */
    public NSDictionary connectionDictionary()
    {
        NSDictionary connectionDictionaries =
            (NSDictionary) testConfigurationDictionary().objectForKey(CONNECTION_DICTIONARIES_KEY); 
        
        return (NSDictionary) connectionDictionaries.objectForKey(databaseType());

        /** ensure
        [valid_result] Result != null;
        [url_key_is_present] Result.objectForKey("URL") != null; **/
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
     public String databaseType()
    {
        return (String) testConfigurationDictionary().objectForKey(DATABASE_TYPE_KEY);
        
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a list of the model names to <b>not</b>connect using connectionDictionary().  This
     * can be used if some of the models that will be loaded are not needed for testing, or need
     * to be connected to a different database.
     * 
     * @return a list of the model names to <b>not</b>connect using connectionDictionary()
     */
    public NSArray modelNamesToIgnore()
    {
        return (NSArray) testConfigurationDictionary().objectForKey(MODELS_TO_IGNORE_KEY);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the configuration dictionary for this test run.  This is used to 
     * implement the other methods in this class.  This method can be overridden to
     * get the dictionary from some other source, or the other methods can be 
     * re-implemented in a different manner.  By default, this class gets the
     * dictionary from the TestConfigurationDictionary.plist of the framework in 
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



}
