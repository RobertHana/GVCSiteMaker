package net.global_village.eofextensions.tests;

import com.webobjects.eocontrol.*;


/**
 * EOEnterprise object used in testing of EOCopying / CopyableGenericRecord 
 * Root object copies deeply.
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class RootObject extends _RootObject 
{

    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setAValue(new Integer(10));
    }
}
