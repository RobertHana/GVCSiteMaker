package net.global_village.foundation.tests;

import org.apache.log4j.*;

import junit.framework.*;

import net.global_village.foundation.*;


/**
 * Tests for LoggerFactory.
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class LoggerFactoryTest extends TestCase
{

    
    /**
     * Constructor for BooleanFormatterTest.
     * @param name of test
     */
    public LoggerFactoryTest(String name)
    {
        super(name);
    }



    /*
     * Test for StringBuffer format(Object, StringBuffer, FieldPosition)
     */
    public void testMakeLogger()
    {
        Logger logger = LoggerFactory.makeLogger();
        assertEquals(getClass().getName(), logger.getName());
        
    }

}
