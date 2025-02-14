package net.global_village.eofextensions.tests;

import com.webobjects.eocontrol.*;

import net.global_village.eofextensions.*;
import net.global_village.testeomodelbase.*;


/**
 * Subclass of GVCFacade which implements newInstance to enable testing of copy, creatNew, and revert in GVCFacadeTest.java
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */  
public class TestFacade extends GVCFacade
{
    /**
     * Designated Constructor
     */
    public TestFacade(EOEditingContext ec, SimpleTestEntity aTestEntity)
    {
        super(ec, aTestEntity);
    }
 


    /**
     * Constructor
     */
    public TestFacade()
    {
        this(new EOEditingContext(), null);
    }



    /**
     * Constructor
     */
    public TestFacade(EOEditingContext ec)
    {
        this(ec, null);
    }



    /**
     * Constructor
     */
    public TestFacade(SimpleTestEntity aTestEntity)
    {
        this(new EOEditingContext(), aTestEntity);
    }


    
    /**
     * Returns a new instance of the EnterpriseObject.  The TestFacade simply uses SimpleTestEntity
     *
     * @return new instance of the EnterpriseObject
     */
    public EOEnterpriseObject newInstance()
    {
        return new SimpleTestEntity();
    }
}
