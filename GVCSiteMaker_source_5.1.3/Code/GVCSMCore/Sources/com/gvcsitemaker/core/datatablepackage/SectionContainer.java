// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

/**
 * Container that represents a Section in a data table package.
 */
public class SectionContainer
{
    protected String name;
    protected String details;
    private String mimeType;
    protected String normalName;
    protected String typeKey;
    protected boolean isNavigable;
    protected String sectionOrder;
    protected ComponentContainer component;


    /*** Generic getters/setters ****/

    public String name()
    {
        return name;
    }



    public void setName(String newName)
    {
        name = newName;
    }



    public String details()
    {
        return details;
    }



    public void setDetails(String aValue)
    {
        details = aValue;
    }


    public String normalName()
    {
        return normalName;
    }



    public void setNormalName(String newNormalName)
    {
        normalName = newNormalName;
    }



    public String typeKey()
    {
        return typeKey;
    }



    public void setTypeKey(String newTypeKey)
    {
        typeKey = newTypeKey;
    }



    public String mimeType()
    {
        return mimeType;
    }



    public void setMimeType(String newType)
    {
        mimeType = newType;
    }


    public boolean isNavigable()
    {
        return isNavigable;
    }



    public void setIsNavigable(boolean aValue)
    {
        isNavigable = aValue;
    }



    public String sectionOrder()
    {
        return sectionOrder;
    }



    public void setSectionOrder(String aValue)
    {
        sectionOrder = aValue;
    }



    public ComponentContainer component()
    {
        return component;
    }



    public void setComponent(ComponentContainer aValue)
    {
        component = aValue;
    }
}
