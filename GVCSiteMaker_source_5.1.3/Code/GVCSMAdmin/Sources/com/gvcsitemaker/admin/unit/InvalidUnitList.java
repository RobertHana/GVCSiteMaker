// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.unit;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class InvalidUnitList extends WOComponent
{

    protected NSArray invalidOrgUnitArray;
    protected NSDictionary orgUnitItem;


    public InvalidUnitList(WOContext aContext)
    {
        super(aContext);
    }



    public String getUnitName() {
        String uName = (String) getOrgUnitItem().objectForKey( OrgUnit.UNITNAME );

        if( uName == null || uName.length() < 1 )
            return "<I>(unspecified)</I>";
        else
            return uName;
    }

    public String errorString() {
        NSDictionary errorDict = (NSDictionary)orgUnitItem.objectForKey( OrgUnit.ERROR_MESSAGE_KEY );
        String errorMessage = "";
        Enumeration e = errorDict.objectEnumerator();

        if( !e.hasMoreElements() )
            return errorMessage;

        while( e.hasMoreElements() ) {
            errorMessage = errorMessage + "<BR>" + e.nextElement();
        }

        return errorMessage.substring(4);
    }


    /* ********** Generic setters and getters ************** */
    public NSArray getInvalidOrgUnitArray() {
        return invalidOrgUnitArray;
    }

    public void setInvalidOrgUnitArray( NSArray newInvalidOrgUnitArray ) {
        invalidOrgUnitArray = newInvalidOrgUnitArray;
    }

    public NSDictionary getOrgUnitItem() {
        return orgUnitItem;
    }

    public void setOrgUnitItem( NSDictionary newOrgUnitItem ) {
        orgUnitItem = newOrgUnitItem;
    }

}
