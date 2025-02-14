// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import net.global_village.foundation.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.*;


/**
 * Implements a customized popup to select an OrgUnit (<code>selectedUnit()</code>) from a list of 
 * units.  The list can exclude (<code>excludeNonSystemWideUnits()</code>) non-system wide units 
 * and / or a supplied list of units (<code>excludedUnits()</code>).  The list can be rooted at one 
 * or more units (<code>rootOrgUnits()</code>).
 */
public class UnitChooser extends WOComponent
{
    // Bindings
    protected OrgUnit selectedUnit;
    protected NSArray rootOrgUnits = null;
    protected boolean excludeNonSystemWideUnits;
    /** @TypeInfo OrgUnit */
    protected NSArray excludedUnits;

    protected OrgUnit orgUnitItem;
    public String onChange;
    protected NSArray orderedOrgUnitsInHierarchy = null;
    protected int rootDepth;    
    protected String indentString;
    public Integer size;
    
    
    /**
     * Designated constructor
     */
    public UnitChooser(WOContext aContext)
    {
        super(aContext);
        
        // Defaults
        excludeNonSystemWideUnits = true;
        excludedUnits = NSArray.EmptyArray;
        
        // We cannot use nbsp's in Mac IE 5 due to a bug in the browser that makes select option's 
        // disappear when displaying an option with a nbsp on a UTF-8 encoded page
        indentString = "&nbsp;&nbsp;";
        WORequest request = aContext.request();
        String browserVersion = RequestUtilities.userAgentMajorVersionNumberFromRequest(request);
        if (((RequestUtilities.isUserAgentInternetExplorer(request)) && (RequestUtilities.isUserOSMac(request))) &&
            (browserVersion != null) && (browserVersion.equals("5")))
        {
            indentString = "..";
        }
    }



    /**
     * Returns the OrgUnit's unitName(), preceeded by sufficient non-breaking spaces to indent it to 
     * it's appropriate position in the OrgUnit hierarchy.  The appropriate position is relative to 
     * the highest (smallest depth) OrgUnit in the hierarchy.
     *
     * @return the OrgUnit's unitName(), preceeded by sufficient non-breaking spaces to indent it to it's appropriate position in the OrgUnit hierarchy.
     */
    public String formattedOrgUnitName()
    {
        StringBuffer formattedOrgUnitName = new StringBuffer(getOrgUnitItem().unitName().length() +
                                                             getOrgUnitItem().depth().intValue() * 2);
        int i = getOrgUnitItem().depth().intValue();
        for ( ; i > rootDepth(); i--)
        {
            formattedOrgUnitName.append(indentString());
        }
        formattedOrgUnitName.append(getOrgUnitItem().unitName());

        return formattedOrgUnitName.toString();
    }



    /** @TypeInfo OrgUnit */
    public NSArray orderedOrgUnitsInHierarchy()
    {
        if (orderedOrgUnitsInHierarchy == null)
        {
            NSMutableArray allOrgUnitsInHierarchy = new NSMutableArray(
                OrgUnit.allUnitsAtOrBelow(getRootOrgUnits(), excludeNonSystemWideUnits()));
            allOrgUnitsInHierarchy.removeObjectsInArray(excludedUnits());    
            orderedOrgUnitsInHierarchy = NSArrayAdditions.sortedArrayUsingComparator(
                allOrgUnitsInHierarchy, OrgUnit.HierarchicalComparator);
                

            // Find the depth of the highest node in the hierarchy.  This is needed when a user is admin
            // of several units at different depths.  The first unit in the list might not be the highest.
            rootDepth = Integer.MAX_VALUE;
            for (int i = 0; i < orderedOrgUnitsInHierarchy.count(); i++)
            {
                OrgUnit thisUnit = (OrgUnit) orderedOrgUnitsInHierarchy.objectAtIndex(i);
                if (thisUnit.depth().intValue() < rootDepth)
                {
                    rootDepth = thisUnit.depth().intValue();
                }
            }
        }
        
        return orderedOrgUnitsInHierarchy;
    }


    /* ********** Generic setters and getters ************** */

    public OrgUnit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(OrgUnit newSelectedUnit) {
        selectedUnit = newSelectedUnit;
    }


    public OrgUnit getOrgUnitItem() {
        return orgUnitItem;
    }

    public void setOrgUnitItem(OrgUnit newOrgUnitItem) {
        orgUnitItem = newOrgUnitItem;
    }

    /** @TypeInfo OrgUnit */
    public NSArray getRootOrgUnits() {
        return rootOrgUnits;
    }
    public void setRootOrgUnits(NSArray newRootOrgUnits) {
        rootOrgUnits = newRootOrgUnits;
    }


    public boolean excludeNonSystemWideUnits() {
        return excludeNonSystemWideUnits;
    }

    public void setExcludeNonSystemWideUnits(boolean newExcludeNonSystemWideUnits) {
        excludeNonSystemWideUnits = newExcludeNonSystemWideUnits;
    }

    public int rootDepth() {
        return rootDepth;
    }
    
    public String indentString() {
        return indentString;
    }

    /** @TypeInfo OrgUnit */
    public NSArray excludedUnits() {
        return excludedUnits;
    }
    public void setExcludedUnits(NSArray newExcludedUnits) {
        excludedUnits = newExcludedUnits;
    }

}
