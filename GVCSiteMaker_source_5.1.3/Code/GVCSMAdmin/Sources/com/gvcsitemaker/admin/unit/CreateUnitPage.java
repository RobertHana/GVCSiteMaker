// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.unit;
import com.gvcsitemaker.admin.commonwidgets.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

public class CreateUnitPage extends WOComponent implements SMSecurePage
{

    protected OrgUnit _unit;
    public NSData fileData;
    public String filePath;
    public OrgUnit parentOrgUnit;
    public NSDictionary errorMessages;
    private static final String INVALID_BATCH_FILE_ERROR_KEY = "invalidBatchFileErrorKey";


    public CreateUnitPage(WOContext aContext)
    {
        super(aContext);
    }



    public WOComponent createNewUnit() {
        errorMessages = OrgUnit.createOrgUnit( getUnit(),
                                               parentOrgUnit,
                                               session().defaultEditingContext() );

        if( errorMessages == null ) {
            return pageWithName("Main");
        }
        else {
            return null;
        }
    }

    public ConfirmUnitCreationPage createNewUnitsFromBatchFile() {
        NSArray newUnits = SMFileUtils.arrayOfObjectsFromBatchFileData(fileData,
                                                                      OrgUnit.BATCH_FILE_KEY_ARRAY);
        DebugOut.println(1, "uploaded data = \n" + newUnits);
        if( newUnits != null ) {
            	OrgUnit.validateArrayOfOrgUnitDictionaries(newUnits, session().defaultEditingContext());
            DebugOut.println(1, "validated data = \n" + newUnits);

            ConfirmUnitCreationPage nextPage = (ConfirmUnitCreationPage)pageWithName("ConfirmUnitCreationPage");
            nextPage.setUnitArray(newUnits);
            return nextPage;
        }

        NSMutableDictionary errorDict = new NSMutableDictionary();
        errorDict.setObjectForKey("The batch file you have selected is not the proper format " +
                                  "or it does not contain the proper number of fields",
                                  INVALID_BATCH_FILE_ERROR_KEY);
        errorMessages = errorDict;
        return null;
    }

    public boolean displayNullUnitNameErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.NULL_UNIT_NAME_ERROR_KEY) != null));
    }

    public boolean displayUnitNameExistsErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.UNIT_NAME_EXISTS_ERROR_KEY) != null));
    }

    public boolean displayNullEncompassingUnitErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.NULL_ENCOMPASSING_UNIT_ERROR_KEY) != null));
    }

    public boolean displayNullFilespaceQuotaErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.NULL_FILESPACE_QUOTA_ERROR_KEY) != null));
    }

    public boolean displayInvalidFilespaceQuotaErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(OrgUnit.INVALID_FILESPACE_QUOTA_ERROR_KEY) != null));
    }

    public boolean displayInvalidBatchFileErrorMessage() {
        return ((errorMessages != null) && 
                (errorMessages.objectForKey(INVALID_BATCH_FILE_ERROR_KEY) != null));
    }

    // the following methods are not required, but i'm using them so we
    // can make use of the constants defined for the keys.  then, if the keys
    // get changed for some reason in the future, the code will not suddenly
    // be broken.
    public String nullUnitNameErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.NULL_UNIT_NAME_ERROR_KEY);
    }

    public String unitNameExistsErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.UNIT_NAME_EXISTS_ERROR_KEY);
    }

    public String nullEncompassingUnitErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.NULL_ENCOMPASSING_UNIT_ERROR_KEY);
    }

    public String nullFilespaceQuotaErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.NULL_FILESPACE_QUOTA_ERROR_KEY);
    }

    public String invalidFilespaceQuotaErrorMessage() {
        return (String)errorMessages.objectForKey(OrgUnit.INVALID_FILESPACE_QUOTA_ERROR_KEY);
    }

    public String invalidBatchFileErrorMessage() {
        return (String)errorMessages.objectForKey(INVALID_BATCH_FILE_ERROR_KEY);
    }


    /* ********** Generic setters and getters ************** */
    public OrgUnit getUnit() {
        if( _unit == null)
        {
            setUnit(OrgUnit.newOrgUnit() );
            // Need to do this or UI elements are bound to null values and exceptions are raised.  If _unit was inserted into an ec, this would not be needed.
            _unit.setInPublicList("Y");

        }
	    
        return _unit;
    }

    public void setUnit( OrgUnit newUnit ) {
        _unit = newUnit;
    }

    
    public Main returnToAdminMainPage()
    {
        Main nextPage = (Main)pageWithName("Main");

        session().defaultEditingContext().revert();

        return nextPage;
    }

}
