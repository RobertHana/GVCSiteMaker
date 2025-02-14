// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.utility.conversion;

import java.util.*;

import net.global_village.eofvalidation.EOEditingContext;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Create Section versions for Mixed Media Text Components
 */
public class MixedMediaVersioningPage extends WOComponent
{


    /**
     * Designated constructor.  
     */
    public MixedMediaVersioningPage(WOContext context)
    {
        super(context);
        updateComponentOrder();
    }



    /**
     * Updates the component_order for each section version that belongs to a
     * MixedMediaTextContnentPane.
     */
    public void updateComponentOrder()
    {
        EOEditingContext ec = new EOEditingContext();
        EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("details = 'Auto-created first version'", NSArray.EmptyArray);
        EOFetchSpecification fs = new EOFetchSpecification("SectionVersion", qual, NSArray.EmptyArray);
        NSArray versions = ec.objectsWithFetchSpecification(fs);
        Enumeration versionEnumerator = versions.objectEnumerator();
        while (versionEnumerator.hasMoreElements())
        {
            SectionVersion version = (SectionVersion)versionEnumerator.nextElement();
            version.setComponentOrder(((MixedMediaTextContentPane)version.component()).order());
        }
        ec.saveChanges();
    }



}
