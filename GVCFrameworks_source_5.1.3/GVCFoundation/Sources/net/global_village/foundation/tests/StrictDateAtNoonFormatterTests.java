package net.global_village.foundation.tests;

import java.text.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;



/**
 * Describe class here. 
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class StrictDateAtNoonFormatterTests extends StrictDateFormatterTest
{

    
    public StrictDateAtNoonFormatterTests(String name)
    {
        super(name);
    }



    /**
     * Setup test
     *
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        testFormatter = new StrictDateAtNoonFormatter("MM dd yyyy");
    }

    
    
    /**
     * Test that parsing produces a date at noon
     */
    public void testDateIsAtNoot()
    {
        try
        {
            NSTimestamp noonDate = (NSTimestamp)testFormatter.parseObject("10 15 2007");
            assertEquals(noonDate, Date.noon(noonDate));
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }
    }
    

    
    /**
     * This test is skipped as this formatter is not reversible.
     */
    public void testReversibility()
    {
    }

    
}
