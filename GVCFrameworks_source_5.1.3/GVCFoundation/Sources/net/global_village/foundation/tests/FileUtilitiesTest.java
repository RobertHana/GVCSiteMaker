package net.global_village.foundation.tests;

import java.io.*;

import junit.framework.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Tests for FileUtilities.
 *
 * @see net.global_village.foundation.FileUtilities
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class FileUtilitiesTest extends TestCase
{


    public FileUtilitiesTest(String name)
    {
        super(name);
    }



    /**
     * Test basic functionality.
     */
    public void testBasic()
    {
        try
        {
            InputStream testDelimitedData = NSBundle.bundleForClass(FileUtilities.class).inputStreamForResourcePath("Tests/TabDelimitedFile.csv");
            NSArray testData = FileUtilities.dataFromDelimitedFile(testDelimitedData, ",", null);
            assertEquals(7, testData.count());

            // Test row with all fields
            NSDictionary row2 = (NSDictionary)testData.objectAtIndex(1);
            assertEquals("02851033B713", row2.objectForKey("Ad Key"));
            assertEquals("AdControlBatchMessages", row2.objectForKey("Used In Tests"));
            assertEquals("Suspended PAD Show", row2.objectForKey("Comments"));

            // Test row with missing fields
            NSDictionary row6 = (NSDictionary)testData.objectAtIndex(5);
            System.out.println(row6);
            assertEquals("02917948M713", row6.objectForKey("Ad Key"));
            assertEquals("", row6.objectForKey("Used In Tests"));
            assertEquals("", row6.objectForKey("Comments"));
        }
        catch (IOException e)
        {
            fail("Threw IOException " + e.getLocalizedMessage());
        }
        catch (IllegalArgumentException e)
        {
            fail("Threw IllegalArgumentException " + e.getLocalizedMessage());
        }
    }

}
