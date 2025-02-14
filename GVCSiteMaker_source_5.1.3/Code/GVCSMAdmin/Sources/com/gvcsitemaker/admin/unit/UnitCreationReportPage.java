// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.unit;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class UnitCreationReportPage extends WOComponent implements SMSecurePage
{

    protected NSArray _unitArray, _validUnitArray, _invalidUnitArray;


    public UnitCreationReportPage(WOContext aContext)
    {
        super(aContext);
    }




    public CreateUnitPage returnToUnitCreation() {
        return (CreateUnitPage)pageWithName("CreateUnitPage");
    }

    public NSArray validUnitArray() {
        if( _validUnitArray != null )
            return _validUnitArray;

        NSDictionary dict = ArrayExtras.splitArrayBasedOnKey( getUnitArray(), OrgUnit.ERROR_MESSAGE_KEY);
        _validUnitArray = (NSArray)dict.objectForKey("valid");
        _invalidUnitArray = (NSArray)dict.objectForKey("invalid");

        return _validUnitArray;
    }

    public NSArray invalidUnitArray() {
        if( _invalidUnitArray != null )
            return _invalidUnitArray;

        NSDictionary dict = ArrayExtras.splitArrayBasedOnKey( getUnitArray(), OrgUnit.ERROR_MESSAGE_KEY);
        _validUnitArray = new NSArray(dict.objectForKey("valid"));
        _invalidUnitArray = new NSArray(dict.objectForKey("invalid"));
	
        return _invalidUnitArray;
    }


    /* ********** Generic setters and getters ************** */
    public NSArray getUnitArray() {
        return _unitArray;
    }

    public void setUnitArray( NSArray newUnitArray ) {
        _unitArray = newUnitArray;
    }

}
