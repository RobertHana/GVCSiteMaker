// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.pages;
import java.io.*;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.utility.appserver.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;

public class BindingEditor extends WOComponent 
{
    protected PageComponent component;
    public String componentID;
    protected String shadowBindings;
    public PageComponent aChild;
    private EOEditingContext ec;
    protected String sqlString;
    static char ToHexTable[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private int sequence = 0;
    
    public BindingEditor(WOContext context) 
    {
        super(context);
        ec = ((Session)session()).newEditingContext();
        ec.revert();
    }


    public WOComponent findComponent() 
    {
        setComponent((PageComponent) EOUtilities.objectMatchingKeyAndValue(ec, 
                                                                           "PageComponent", 
                                                                           "componentId", 
                                                                           componentID));
        return this;
    }


    public WOComponent saveBindings() 
    {
        ec.saveChanges();
        return this;
    }


    public WOComponent editParent() {
        setComponent(component().toParent());
        return this;
    }

    public WOComponent editChild() {
        setComponent(aChild);
        return this;
    }

    public WOComponent showSQL() {
        
        StringBuffer sb = new StringBuffer();
        sb.append("DEFINE CLOB @'PageComp");
        sb.append(++sequence);
        sb.append("' LENGTH ");
        sb.append(shadowBindings().length());
        sb.append(" VALUE ");
        for (int i = 0; i < shadowBindings().length(); i++)
        {
            sb.append(ToHexTable[ (shadowBindings().charAt(i) >> 4) & 0x0F ]);
            sb.append(ToHexTable[ shadowBindings().charAt(i) & 0x0F ]);
        }
        sb.append(";");
        sb.append("<br />");
        sb.append("UPDATE SM_PAGE_COMP SET bindings = @'PageComp");
        sb.append(sequence);
        sb.append("' WHERE COMPONENT_ID = ");
        sb.append(component().componentId());
        sb.append(";");
        
        try
        {
            String utf8String = new String(sb.toString().getBytes("UTF-8"));
            setSqlString(utf8String);
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }


    public PageComponent component() {
        return component;
    }
    public void setComponent(PageComponent newComponent) {
        component = newComponent;
        if (component != null)
        {
            setShadowBindings(component.bindings());
        }
    }

    public String shadowBindings() {
        return shadowBindings;
    }
    public void setShadowBindings(String newShadowBindings) {
        shadowBindings = newShadowBindings;
    }


    public String sqlString() {
        return sqlString;
    }
    public void setSqlString(String newSqlString) {
        sqlString = newSqlString;
    }

}
