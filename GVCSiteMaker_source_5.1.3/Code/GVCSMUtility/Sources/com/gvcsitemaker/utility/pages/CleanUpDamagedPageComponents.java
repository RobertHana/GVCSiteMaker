// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.pages;
import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Due to a bug in GVCSiteMaker PageComponents can retain references to deleted SiteFiles (SCR 833).  There have also been instances of PageComponent becoming Orphaned when their Section is deleted, though this seems to have been fixed with an alteration to the EOModel (cascading delete from Section to PageComponent and PageComponent to child PageComponents) and can not be reproduced at present.
 */
public class CleanUpDamagedPageComponents extends WOComponent 
{

    /** @TypeInfo PageComponent */
	public NSMutableArray componentsWithDanglingSiteFileReferences;

    /** @TypeInfo PageComponent */
	public NSMutableArray orphanedPageComponents;

	public PageComponent aComponent;		// used for WORepetitions


    public CleanUpDamagedPageComponents(WOContext context)
    {
        super(context);
        EODatabaseContext.setDefaultDelegate(null);  // Turn off error reporting to make things run faster.
    }


    /**
     * The lists of page components needs to be regenerated each time.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        findDamagedPageComponents();
        super.appendToResponse(aResponse, aContext);
    }



    /**
     * Returns the Section this PageComponent is embedded in.  This is a safe version of sectionUsedIn() that handles orphans.
     */
    public Section componentsSection()
    {
        PageComponent parent = aComponent;
        Section componentsSection = null;

        while ( (parent != null) && (componentsSection == null))
        {
            componentsSection = parent.section();

            try
            {
                parent = parent.toParent();

                if ((parent != null) && isDummyFaultEO(parent))
                {
                    parent = null;
                }
            }
            catch (IllegalStateException e)
            {
                //  Missing parent
                parent = null;
            }
        }

        return componentsSection;
    }



    /**
     * Compiles the list of all damaged PageComponents
     */
    public void findDamagedPageComponents()
    {
        componentsWithDanglingSiteFileReferences = new NSMutableArray();
        orphanedPageComponents = new NSMutableArray();

        DebugOut.println(1, "Finding all PageComponents");
        NSArray allPageComponents = EOUtilities.objectsForEntityNamed(session().defaultEditingContext(), "PageComponent");
        DebugOut.println(1, "Found " + allPageComponents.count() + " PageComponents");
        int i = 1;

        Enumeration pageComponents = allPageComponents.objectEnumerator();
        while (pageComponents.hasMoreElements())
        {
            DebugOut.println(1, "Checking #" + (i++));
            PageComponent pageComponent = (PageComponent) pageComponents.nextElement();

            // If this PageComponent has no section and no parent it is an orphan.  Its children will be deleted by cascade delete rules in the EOModel.  This fails with an exception if toParent is a dangling reference.  To work around this we could copy PageComponent into Conversion.eomodeld.
            if ((pageComponent.section() == null) && (pageComponent.toParent() == null) )
            {
                orphanedPageComponents.addObject(pageComponent);
            }

            // Detect PageComponents with a related file which no longer exists.
            SiteFile relatedFile = pageComponent.relatedFile();
            if ((relatedFile != null) && isDummyFaultEO(relatedFile))
            {
                componentsWithDanglingSiteFileReferences.addObject(pageComponent);
            }
        }
    }



    /**
     * Used in a WOConditional to prevent orphans from being fixed when there are dangling site file references (deletion of orphans fails if any of them has such a dangling reference.
     */
    public boolean hasDanglingSiteFileReferences()
    {
        return componentsWithDanglingSiteFileReferences.count() > 0;
    }



    /**
     * Fixes Dangling SiteFile References by setting the SiteFile FK to null.
     */
    public WOComponent fixDanglingSiteFileReferences()
    {
        EOEditingContext ec =  ((PageComponent)componentsWithDanglingSiteFileReferences.lastObject()).editingContext();
        Enumeration pageComponents = componentsWithDanglingSiteFileReferences.objectEnumerator();
        while (pageComponents.hasMoreElements())
        {
            PageComponent pageComponent = (PageComponent) pageComponents.nextElement();
            pageComponent.editingContext().forgetObject(pageComponent.relatedFile());
            DebugOut.println(1, "Fixing Dangling SiteFile Reference for " + pageComponent.componentType() + " " + pageComponent.componentId());

            // Fix for SCR 1299.  If this is not done, the invalidateAllObjects() below raises an unable to decrement snapshot count for this object.
            pageComponent.editingContext().forgetObject(pageComponent.relatedFile());
            pageComponent.setRelatedFileId(null);
            pageComponent.editingContext().saveChanges();
        }

        // Clean up the ec so that the relationship for the just nullified FK is in a sane state.
        ec.invalidateAllObjects();

        return this;
    }



    /**
     * Fixes Orphaned PageComponents by deleting them.
     */
    public WOComponent fixOrphanedPageComponents()
    {
        Enumeration pageComponents = orphanedPageComponents.objectEnumerator();
        int i = 1;
        while (pageComponents.hasMoreElements())
        {
            PageComponent pageComponent = (PageComponent) pageComponents.nextElement();
            EOEditingContext ec = pageComponent.editingContext();
            DebugOut.println(1, "Deleting Orphaned PageComponent #" + (i++) + ", " + pageComponent.componentType() + " " + pageComponent.componentId());
            ec.deleteObject(pageComponent);
            ec.saveChanges();
        }

        return this;
    }

    
    /**
     * Detecting WO 5 dummy fault EOs.  The code below is a not very robust translation of code posted here:
     * From http://wodeveloper.com/omniLists/eof/2001/November/msg00023.html
     */


    /**
     * As of WO4.5, EOF resolves faults for objects that don't exist in the database to a dummy object with mostly empty attributes (except those
    that are set in -init), whereas earlier versions of EOF threw a _fireFault exception.  This method detects whether the receiver is such a dummy object.
     *
     *  A distinguishing feature of these dummy EOs is that they don't have a corresponding database context snapshot, so we use this to detect its dummy-ness.  This seems more robust than checking if all or most attributes are empty, since these can be changed by client code.
     *
     *  Dummy fault EOs should be removed from the editingContext (using #{-forgetObject:}) before it is invalidated (by EOEditingContext #{-invalidateAllObjects} or #{-invalidateObjectWithGlobalID:}), otherwise an unrecoverable "%{decrementSnapshotCountForGlobalID:: unable to decrement snapshot count for object with global ID}" exception is thrown by EOF.
     *
     * See also: http://www.omnigroup.com/mailman/archive/eof/2001-May/001988.html andfollowing.
     */
    public boolean isDummyFaultEO(EOEnterpriseObject anObject)
    {
        boolean isDummyFaultEO = false;

        // This method will fail if the object is still a fault.  We fire the fault so that the correct result is returned.
        if (anObject.isFault())
        {
            anObject.willRead();
        }

        EOEditingContext ec = anObject.editingContext();
        EOGlobalID globalID = ec.globalIDForObject(anObject);

        // NB. objects with temporary globalIDs legitimately have no DB snapshots, since these are by definition not yet saved to the database.
        if (globalID != null && ! globalID.isTemporary())
        {
            // Find the EODatabaseContext instance associated with anObject, or null if no databaseContext association can be found.
            EOObjectStoreCoordinator rootStore = (EOObjectStoreCoordinator)ec.rootObjectStore();
            EODatabaseContext dbContext = (EODatabaseContext)rootStore.objectStoreForObject(anObject);

            isDummyFaultEO = (dbContext.snapshotForGlobalID(globalID) == null);
        }

        return isDummyFaultEO;
    }


}
