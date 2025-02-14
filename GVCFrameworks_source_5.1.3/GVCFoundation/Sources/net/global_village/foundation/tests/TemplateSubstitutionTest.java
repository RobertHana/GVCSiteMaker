package net.global_village.foundation.tests;

import java.math.*;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Tests for net.global_village.eofExtensions.TemplateSubstitution
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class TemplateSubstitutionTest extends TestCase
{
    NSMutableDictionary address;
    NSMutableDictionary person;

    String templateWithDefaultDelimiters;
    String templateWithCustomDelimiters;
    String templateWithDataConversions;


    /**
     * Designated constuctor.
     *
     * @param name name for this test.
     */
    public TemplateSubstitutionTest(String name)
    {
        super(name);
    }



    /**
     * Create test fixture.
     */
    public void setUp()
    {
        address = new NSMutableDictionary();
        person = new NSMutableDictionary();
        person.takeValueForKey("James", "firstName");
        person.takeValueForKey("Brown", "lastName");
        person.takeValueForKey(new Integer(24), "age");
        person.takeValueForKey(new BigDecimal(65.5), "weight");
        person.takeValueForKey(new NSTimestamp(1976, 7, 4, 12, 0, 0, NSTimeZone.timeZoneWithName("EST", true)), "birthDate");

        address.takeValueForKey("123 AnyStreet", "streetAddress");
        address.takeValueForKey("Vancouver", "city");
        address.takeValueForKey("Canada", "country");
        person.takeValueForKey(address, "address");

        templateWithDefaultDelimiters = "Mr. <<firstName>> <<lastName>>\r<<address.streetAddress>>\r<<address.city>>, <<address.country>>";

        templateWithCustomDelimiters = "Mr. [firstName] [lastName]\r[address.streetAddress]\r[address.city], [address.country]";

        templateWithDataConversions = "Mr. <<firstName>> <<lastName>>\rAge: <<age>>\rWeight <<weight>>\rBirthDate <<birthDate>>";

    }



    /**
     * Tests Design by Contract of this class' methods.
     */
    public void testDBC()
    {
        NSMutableDictionary genericObject  = new NSMutableDictionary();;
        try
        {
            TemplateSubstitution.substituteValuesInStringFromDictionary(null, genericObject, "[", "]");
            fail("Missing precondition for null template");
        }
        catch (RuntimeException e) { }

        try
        {
            TemplateSubstitution.substituteValuesInStringFromDictionary("[template]", null, "[", "]");
            fail("Missing precondition for null object");
        }
        catch (RuntimeException e) { }

        try
        {
            TemplateSubstitution.substituteValuesInStringFromDictionary("[template]", genericObject, null, "]");
            fail("Missing precondition for null start delimiter");
        }
        catch (RuntimeException e) { }

        try
        {
            TemplateSubstitution.substituteValuesInStringFromDictionary("[template]", genericObject, "[", null);
            fail("Missing precondition for null end delimiter");
        }
        catch (RuntimeException e) { }

        try
        {
            TemplateSubstitution.substituteValuesInStringFromDictionary(null, genericObject);
            fail("Missing precondition for null template");
        }
        catch (RuntimeException e) { }

        try
        {
            TemplateSubstitution.substituteValuesInStringFromDictionary("<<template>>", null);
            fail("Missing precondition for null object");
        }
        catch (RuntimeException e) { }

    }


    /**
     * Tests various odd conditions which ought to work or be handled with exceptions.
     */
    public void testOddConditions()
    {

        assertTrue("Test of no substitution", (TemplateSubstitution.substituteValuesInStringFromDictionary("There are no keywords here.", person)).equals("There are no keywords here."));

        assertTrue("Test of all substitution", (TemplateSubstitution.substituteValuesInStringFromDictionary("<<address.city>>", person)).equals("Vancouver"));

        try
        {
            TemplateSubstitution.substituteValuesInStringFromDictionary("<<city.address", person);
            fail("Test of malformed template 1");
        }
        catch (RuntimeException r) { }

        assertTrue("Test of malformed template 2",
               TemplateSubstitution.substituteValuesInStringFromDictionary("city.address>>", person).
               equals("city.address>>"));

        // This acutally looks up the key <<city.address, which returns null.  Need better null handling?
        assertTrue("Test of malformed template 3", TemplateSubstitution.substituteValuesInStringFromDictionary("<<<<city.address>>", person).equals(""));
    }



    /**
     * Tests template substitution.
     */
    public void testTemplateSubstitution()
    {
        try
        {
            assertTrue((TemplateSubstitution.substituteValuesInStringFromDictionary(templateWithCustomDelimiters, person)).equals(templateWithCustomDelimiters));

            assertTrue((TemplateSubstitution.substituteValuesInStringFromDictionary(templateWithCustomDelimiters, person, "[", "]")).equals("Mr. James Brown\r123 AnyStreet\rVancouver, Canada"));

            assertTrue((TemplateSubstitution.substituteValuesInStringFromDictionary(templateWithDefaultDelimiters, person)).equals("Mr. James Brown\r123 AnyStreet\rVancouver, Canada"));

            assertTrue((TemplateSubstitution.substituteValuesInStringFromDictionary(templateWithDataConversions, person)).equals("Mr. James Brown\rAge: 24\rWeight 65.5\rBirthDate 1976-07-04 16:00:00 Etc/GMT"));
        }
        catch (RuntimeException e)
        {
            fail("Raised exception " + e.getClass() + " " + e.getMessage());
        }
    }



    /**
     * Test missing or null key
     */
    public void testMissingOrNullKey()
    {
        try
        {
            assertTrue((TemplateSubstitution.
                    substituteValuesInStringFromDictionary("test <<IQ>>", person)).
                   equals("test "));
        }
        catch (RuntimeException e)
        {
            fail("Using missing or null key raised exception " + e.getClass() + " " + e.getMessage());
        }
    }



}
