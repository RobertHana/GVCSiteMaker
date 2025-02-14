// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.pages;
import java.util.*;

import com.gvcsitemaker.core.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;



/**
 * Locates any pieces of deleted Websites (Sections and uploaded files at present) and removes them. This was done to accomodate Websites deleted with ealier versions which had defects in the deletion process.
 */
public class RemoveWebsitePieces extends WOComponent 
{

    /** @TypeInfo Section */
	public NSArray orphanedSections;
	public Section aSection;

    /** @TypeInfo SiteFile */
	public NSArray orphanedFiles;
	public SiteFile aFile;


    public RemoveWebsitePieces(WOContext context) 
    {
        super(context);
        findOrphans();  // Refresh list
    }



    public void findOrphans()
    {
        orphanedSections = EOUtilities.objectsWithQualifierFormat(session().defaultEditingContext(),
                                                                 "Section",
                                                                 "website = %@",
                                                                 new NSArray(NSKeyValueCoding.NullValue));
        orphanedFiles = EOUtilities.objectsWithQualifierFormat(session().defaultEditingContext(),
                                                               "SiteFile",
                                                               "website = %@",
                                                               new NSArray(NSKeyValueCoding.NullValue));
    }



    public WOComponent deleteOrphanedSections()
    {
        Enumeration sectionEnumeration = orphanedSections.objectEnumerator();
        while (sectionEnumeration.hasMoreElements())
        {
            Section thisSection = (Section)sectionEnumeration.nextElement();
            session().defaultEditingContext().deleteObject(thisSection);

        }

        session().defaultEditingContext().saveChanges();
        findOrphans();  // Refresh list
        return this;
    }



    public WOComponent deleteOrphanedFiles()
    {
        Enumeration siteFileEnumeration = orphanedFiles.objectEnumerator();
        while (siteFileEnumeration.hasMoreElements())
        {
            SiteFile thisSiteFile = (SiteFile)siteFileEnumeration.nextElement();
            session().defaultEditingContext().deleteObject(thisSiteFile);

        }

        session().defaultEditingContext().saveChanges();
        findOrphans();  // Refresh list
        return this;
    }

}
