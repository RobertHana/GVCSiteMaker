package net.global_village.woextensions.tests;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.woextensions.*;

/**
 * Tests for ApplicationProperties
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class ApplicationPropertiesTest extends TestCase
{
    protected ApplicationProperties testProperties;
    
    /**
     * Designated constructor.
     *
     */
    public ApplicationPropertiesTest(String name)
    {
        super(name);
    }


    /**
     * Common test code.
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        testProperties = new ApplicationProperties();
        testProperties.addPropertiesFromFile("TestCoreInfo.plist", "GVCWOExtensions");
    }


    
    /**
     * Test addPropertiesFromFile()
     */
    public void testAddPropertiesFromFile()
    {
        //Test by adding a non-existing plist
        testProperties.addPropertiesFromFile("CustomInfo.plist", "GVCWOExtensions");
        assertTrue("non-existing plist does not change property list", true); //hard to check since actual property list is protected 

        //Test by adding an existing plist but exactly the same contents
        testProperties.addPropertiesFromFile("TestCoreInfoCopy.plist", "GVCWOExtensions");
        assertTrue("adding duplicate plist does not change property list", true); //hard to check since actual property list is protected

        //Test by adding an existing plist with different contents
        assertTrue("check existing properties", testProperties.stringPropertyForKey("stringProperty").equals("This is the default string value"));
        assertTrue("check existing properties", testProperties.booleanPropertyForKey("booleanProperty") == true);
        assertTrue("check existing properties", testProperties.stringPropertyForKey("customProperty") == null);

        testProperties.addPropertiesFromFile("TestCustomInfo.plist", "GVCWOExtensions");
        assertTrue("same property is overriden", testProperties.stringPropertyForKey("stringProperty").equals("This a customized string value"));
        assertTrue("same property is overriden", testProperties.booleanPropertyForKey("booleanProperty") == false);
        assertTrue("new property is added", testProperties.stringPropertyForKey("customProperty").equals("This property does not exist in the core plist"));

        //Test adding a file with XML
        testProperties.addPropertiesFromFile("TestXMLFile.api", "GVCWOExtensions");
        assertTrue("test stringPropertyForKey from XML", testProperties.stringPropertyForKey("Author").equals("William Shakespeare"));
        assertTrue("test intPropertyForKey from XML", testProperties.intPropertyForKey("Birthdate") == 1564);
        NSArray lines = new NSArray(new Object[]{"It is a tale told by an idiot,", "Full of sound and fury, signifying nothing."});
        assertTrue("test arrayPropertyForKey from XML", testProperties.arrayPropertyForKey("Lines").equals(lines));
    }
   


    /**
     * Test addPropertyForKey()
     */
    public void testAddPropertyForKey()
    {
        testProperties.addPropertyForKey("http://localhost/cgi-bin/WebObjects.exe", "cgiAdaptorURL");
        assertTrue("test hasPropertyForKey for new key", testProperties.hasPropertyForKey("cgiAdaptorURL"));
        assertTrue("test new property is set", testProperties.stringPropertyForKey("cgiAdaptorURL").equals("http://localhost/cgi-bin/WebObjects.exe"));

        //Test that it replaces existing value if same key is used
        testProperties.addPropertyForKey("http://localhost/cgi-bin/WebObjects.exe", "cgiAdaptorURL");
        assertTrue("test hasPropertyForKey for new key", testProperties.hasPropertyForKey("cgiAdaptorURL"));
        assertTrue("test new property is set", testProperties.stringPropertyForKey("cgiAdaptorURL").equals("http://localhost/cgi-bin/WebObjects.exe"));
    }


    
    /**
     * Test all propertyForKeys()
     */
    public void testAllPropertyForKey()
    {
        assertTrue("test arrayPropertyForKey with invalidKey", testProperties.valueForKeyPath("nonExistingKeyPath") == null);
        
        assertTrue("test arrayPropertyForKey", testProperties.arrayPropertyForKey("arrayProperty").equals(new NSArray(new Object[]{"itemOne", "itemTwo",  "itemThree", "itemFour"})));
        
        assertTrue("test booleanPropertyForKey", testProperties.booleanPropertyForKey("booleanProperty") == true);

        assertTrue("test floatValuePropertyForKey", testProperties.floatValuePropertyForKey("floatProperty") == new Float("9.89").floatValue());
        assertTrue("test intProperty", testProperties.intPropertyForKey("intProperty") == 9);
      
        assertTrue("test propertyForKey", testProperties.propertyForKey("stringProperty").equals("This is the default string value"));
        assertTrue("test propertyForKey", testProperties.propertyForKey("arrayProperty").equals(new NSArray(new Object[]{"itemOne", "itemTwo",  "itemThree", "itemFour"})));
        
        assertTrue("test stringPropertyForKey", testProperties.stringPropertyForKey("stringProperty").equals("This is the default string value"));
        assertTrue("test stringPropertyForKey", testProperties.stringPropertyForKey("Application.email.fromAddress").equals("bill@global-village.net"));

        NSDictionary testDictionary = new NSDictionary(new Object[] {"itemOne", "itemTwo",  "itemThree", "itemFour"}, new Object[]  {"keyOne", "keyTwo",  "keyThree", "keyFour"});

        assertTrue("test dictionaryPropertyForKey", testProperties.dictionaryPropertyForKey("dictionaryProperty").equals(testDictionary));

        NSTimestampFormatter dateFormatter = new NSTimestampFormatter();
        dateFormatter.setPattern("%B %e, %Y");
        testProperties.addPropertyForKey(dateFormatter, "dateFormatter");
        assertTrue("test timestampFormatterForKey", testProperties.timestampFormatterForKey("dateFormatter").equals(dateFormatter));
        

        NSNumberFormatter numberFormatter = new NSNumberFormatter();
        numberFormatter.setPattern("#,##0.00");
        testProperties.addPropertyForKey(numberFormatter, "numberFormatter");
        assertTrue("test numberFormatterForKey", testProperties.numberFormatterForKey("numberFormatter").equals(numberFormatter));        
    }


    
    /**
     * Test hasPropertyForKey()
     */
    public void testHasPropertyForKey()
    {
        assertTrue("test hasPropertyForKey arrayProperty", testProperties.hasPropertyForKey("arrayProperty"));
        assertTrue("test hasPropertyForKey booleanProperty", testProperties.hasPropertyForKey("booleanProperty"));
        assertTrue("test hasPropertyForKey floatProperty", testProperties.hasPropertyForKey("floatProperty"));
        assertTrue("test hasPropertyForKey intProperty", testProperties.hasPropertyForKey("intProperty"));
        assertTrue("test hasPropertyForKey dictionaryProperty", testProperties.hasPropertyForKey("dictionaryProperty"));
        assertTrue("test hasPropertyForKey stringProperty", testProperties.hasPropertyForKey("stringProperty"));
        assertTrue("test hasPropertyForKey stringProperty with key path", testProperties.hasPropertyForKey("Application.email.fromAddress"));
    }



    /**
     * Test convertTimestampFormatToFormatter()
     */
    public void testConvertMethods()
    {
        testProperties.createDateFormatter("dateFormat");
        assertTrue("test convertTimestampFormatToFormatter", testProperties.timestampFormatterForKey("dateFormatter").pattern().equals("%B %e, %Y"));

        testProperties.createNumberFormatter("numberFormat");
        assertTrue("test convertNumberFormatToFormatter", testProperties.numberFormatterForKey("numberFormatter").pattern().equals("#,##0.00;#,##0.00;-#,##0.00"));
    }
}
