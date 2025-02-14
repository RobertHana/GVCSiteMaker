package net.global_village.eofextensions.tests;

import com.webobjects.eocontrol.*;

import net.global_village.testeomodelbase.*;


/**
 * Class used in tests of GVCEOObject
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class TestClassWithEOs extends com.webobjects.eocontrol.EOCustomObject
{
    protected SimpleTestEntity simpleTestEntity;
    protected SimpleRelationshipTestEntity simpleRelationshipTestEntity;
    protected EOEditingContext editingContext;


    /**
     * Designated constructor.
     */
    public TestClassWithEOs()
    {
        super();
        editingContext = new EOEditingContext();
        simpleTestEntity = new SimpleTestEntity();
        editingContext.insertObject(simpleTestEntity);

        simpleRelationshipTestEntity = new SimpleRelationshipTestEntity();
        editingContext.insertObject(simpleRelationshipTestEntity);        
    }


    
    /**
     * simpleTestEntity accessor method.
     */
    public SimpleTestEntity simpleTestEntity()
    {
        return simpleTestEntity;
    }


    /**
     * simpleTestEntity accessor method.
     */
    public SimpleRelationshipTestEntity simpleRelationshipTestEntity()
    {
        return simpleRelationshipTestEntity;
    }    



}
