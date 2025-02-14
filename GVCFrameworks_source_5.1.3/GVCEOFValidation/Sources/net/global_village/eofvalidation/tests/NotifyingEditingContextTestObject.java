package net.global_village.eofvalidation.tests;

import com.webobjects.foundation.*;

/**
 * Test object for NotifyingEditingContext.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class NotifyingEditingContextTestObject extends _NotifyingEditingContextTestObject
{
    // Use int's so that we can tell if it was called more than once
    public int calledHasInserted = 0;
    public int calledWillDelete = 0;
    public int calledHasDeleted = 0;
    public int calledWillUpdate = 0;
    public int calledHasUpdated = 0;
    public int calledHasReverted = 0;




    public void hasInserted()
    {
        calledHasInserted++;
    }



    public void willDelete()
    {
        calledWillDelete++;
    }


    public void hasDeleted()
    {
        calledHasDeleted++;
    }



    public void willUpdate()
    {
        calledWillUpdate++;

        // This is used to test willUpdate causing updates to other objects.
        if (relatedObject() != null)
        {
            relatedObject().setStringValue(new NSTimestamp().toString());
        }
    }


    public void hasUpdated()
    {
        calledHasUpdated++;
    }



    public void hasReverted()
    {
        calledHasReverted++;
    }



    public void reset()
    {
        calledHasInserted = 0;
        calledWillDelete = 0;
        calledHasDeleted = 0;
        calledWillUpdate = 0;
        calledHasUpdated = 0;
        calledHasReverted = 0;
    }


}
