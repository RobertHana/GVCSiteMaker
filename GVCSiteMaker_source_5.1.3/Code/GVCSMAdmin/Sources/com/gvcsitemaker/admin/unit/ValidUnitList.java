// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.unit;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class ValidUnitList extends WOComponent
{

    protected NSArray validOrgUnitArray;
    protected NSDictionary orgUnitItem;


    public ValidUnitList(WOContext aContext)
    {
        super(aContext);
    }

    

    /* ********** Generic setters and getters ************** */
    public NSArray getValidOrgUnitArray() {
        return validOrgUnitArray;
    }

    public void setValidOrgUnitArray(NSArray newValidOrgUnitArray) {
        validOrgUnitArray = newValidOrgUnitArray;
    }

    public NSDictionary getOrgUnitItem() {
        return orgUnitItem;
    }

    public void setOrgUnitItem(NSDictionary newOrgUnitItem) {
        orgUnitItem = newOrgUnitItem;
    }

}
