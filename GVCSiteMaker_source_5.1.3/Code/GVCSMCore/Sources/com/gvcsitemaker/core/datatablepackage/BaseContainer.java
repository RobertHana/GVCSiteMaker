// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;


/**
 * Superclass for data table package containers, contains common properties among containers.
 */
public class BaseContainer
{
    protected String name;
    protected String textDescription;

    /*** Generic getters/setters ****/

    public String name()
    {
        return name;
    }



    public void setName(String newName)
    {
        name = newName;
    }



    public String textDescription()
    {
        return textDescription;
    }



    public void setTextDescription(String aValue)
    {
        textDescription = aValue;
    }
}
