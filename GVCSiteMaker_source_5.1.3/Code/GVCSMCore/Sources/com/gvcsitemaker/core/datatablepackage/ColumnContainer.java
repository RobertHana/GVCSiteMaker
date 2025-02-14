// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

/**
 * Container that represents VirtualColumn objects in the package.
 */
public class ColumnContainer extends BaseContainer
{
    protected String restrictingValue;
    protected String type;
    protected String lookupColumn;
    protected String lookupTable;
    protected String lookupColumnType;
    protected String expression;


    /*** Generic getters/setters ****/

    public String restrictingValue()
    {
        return restrictingValue;
    }



    public void setRestrictingValue(String newRestrictingValue)
    {
        restrictingValue = newRestrictingValue;
    }



    public String type()
    {
        return type;
    }



    public void setType(String newType)
    {
        type = newType;
    }



    public String lookupColumn()
    {
        return lookupColumn;
    }



    public void setLookupColumn(String newLookupColumn)
    {
        lookupColumn = newLookupColumn;
    }



    public String lookupTable()
    {
        return lookupTable;
    }



    public void setLookupTable(String newLookupTable)
    {
        lookupTable = newLookupTable;
    }



    public String lookupColumnType()
    {
        return lookupColumnType;
    }



    public void setLookupColumnType(String newLookupColumnType)
    {
        lookupColumnType = newLookupColumnType;
    }



    public String expression()
    {
        return expression;
    }



    public void setExpression(String newExpression)
    {
        expression = newExpression;
    }
}
