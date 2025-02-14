// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.utility.pages;
import java.io.*;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * A nastly little page to collect hacks to clean up SiteFiles from damage from stupidity and bugs in previous versions.  The two problems present are files in the upload directory for which a SiteFile no longer exists, and SiteFiles for which no file exists in the Upload directory.  This can be eliminated once UMich is cleaned up (or can it? :-).
 */

public class SiteFileCleanupHack extends WOComponent
{
    /** @TypeInfo SiteFile */
    public NSArray allSiteFiles;
    /** @TypeInfo SiteFile */
	public NSMutableArray missingFiles;
    /** @TypeInfo SiteFile */
	public NSArray siteFiles;
	public SiteFile aSiteFile;

	public String destinationPath;

    /** @TypeInfo java.lang.String */
	public NSMutableArray errors;
	public String anError;



    public SiteFileCleanupHack(WOContext aContext)
    {
           super(aContext);
    }



    public WOComponent generateCommands()
    {
        siteFiles = EOUtilities.objectsForEntityNamed(session().defaultEditingContext(), "SiteFile");
        return null;
    }



    /**
     * Copies all the in use uploaded files for SiteFiles from where they were uploaded to a new location (destinationPath).  This was done so that unused, uploaded files could be removed from the file system.  The intended usage pattern is:
     * 1. Copy the used files to a temporary location.
     * 2. Delete the entire uploaded files directory.
     * 3. Manually (in the shell) move the copied files from the temporary location back to the original location.
     */
    public WOComponent copyFiles()
    {
        errors = new NSMutableArray();

        if (destinationPath == null)
        {
            errors.addObject("The destination path is required");
        }
        else if ( ! (new File(destinationPath).isDirectory()))
        {
            errors.addObject("The destination path does not exist");
        }

        allSiteFiles = EOUtilities.objectsForEntityNamed(session().defaultEditingContext(), "SiteFile");
        Enumeration theSiteFiles = allSiteFiles.objectEnumerator();
        int copiedFiles = 0;
        int skippedFiles = 0;
        while (theSiteFiles.hasMoreElements())
        {
            SiteFile thisSiteFile = (SiteFile) theSiteFiles.nextElement();

            if (new File(thisSiteFile.filePath()).isFile() )
            {
                DebugOut.println(1, "Copying " + thisSiteFile.filePath());
                SMFileUtils.copyFile(thisSiteFile.filePath() ,
                                     NSPathUtilities.stringByAppendingPathComponent(destinationPath, thisSiteFile.filePathFragment()));
                copiedFiles++;
            }
            else
            {
                DebugOut.println(1, "Skipping " + thisSiteFile.filePath());
                skippedFiles++;
            }
        }
        errors.addObject(copiedFiles + " files copied");
        errors.addObject(skippedFiles + " missing files skipped");
        DebugOut.println(1, copiedFiles + " files copied");
        DebugOut.println(1, skippedFiles + " missing files skipped");

        return null;
    }

    public WOComponent refresh()
    {
        return context().page();
    }


    /**
     * This finds, by Org Unit and Website, SiteFiles whose uploaded file is no longer present. These SiteFiles either need to be manually deleted or replaced.
     */
    public WOComponent findMissingFiles()
    {
        NSMutableArray sortOrderings = new NSMutableArray();
        sortOrderings.addObject(EOSortOrdering.sortOrderingWithKey("website.parentOrgUnit.unitName", EOSortOrdering.CompareAscending));
        sortOrderings.addObject(EOSortOrdering.sortOrderingWithKey("website.siteID", EOSortOrdering.CompareAscending));
        sortOrderings.addObject(EOSortOrdering.sortOrderingWithKey("uploadedFilename", EOSortOrdering.CompareAscending));

        EOFetchSpecification fetchSpec = new EOFetchSpecification("SiteFile", null, sortOrderings);
        allSiteFiles = session().defaultEditingContext().objectsWithFetchSpecification(fetchSpec);

        missingFiles = new NSMutableArray();
        Enumeration siteFiles = allSiteFiles.objectEnumerator();
        while (siteFiles.hasMoreElements())
        {
            SiteFile aSiteFile = (SiteFile) siteFiles.nextElement();
            if ( ! new File(aSiteFile.filePath()).isFile())
            {
                missingFiles.addObject(aSiteFile);
            }
        }

        return this;
    }



    /**
     * This forces each Website and OrgUnit to recalculate fileSizeUsage.  It is not very efficient and the same result is recaculated more often than needed at the OrgUnit level.
     */
    public WOComponent recalculateQuotaUsage()
    {
        EOFetchSpecification fetchSpec = new EOFetchSpecification("Website", null, null);
        NSArray allWebsites = session().defaultEditingContext().objectsWithFetchSpecification(fetchSpec);

        Enumeration websites = allWebsites.objectEnumerator();
        while (websites.hasMoreElements())
        {
            Website aWebsite = (Website) websites.nextElement();
            aWebsite.updateFileSizeUsage();
        }
        session().defaultEditingContext().saveChanges();

        return this;
    }

}
