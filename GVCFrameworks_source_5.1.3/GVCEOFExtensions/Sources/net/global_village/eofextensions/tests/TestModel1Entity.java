
package net.global_village.eofextensions.tests;


import com.webobjects.eocontrol.*;

/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class TestModel1Entity extends EOCustomObject 
{

    protected String name;


    public String name() {
        willRead();
        return name;
    }

    public void setName(String value) {
        willChange();
        name = value;
    }
}
