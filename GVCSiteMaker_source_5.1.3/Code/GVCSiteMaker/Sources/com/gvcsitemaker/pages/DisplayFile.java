// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.core.components.ErrorPage;
import com.gvcsitemaker.core.components.SMAuthComponent;
import com.gvcsitemaker.core.utility.DebugOut;
import com.gvcsitemaker.core.utility.SMFileUtils;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import net.global_village.foundation.ExceptionConverter;
import net.global_village.woextensions.Response;


public class DisplayFile extends WOComponent
{
// There is code in this class that is very similar to DisplayFile (appendToResponse etc.).  This should be refactored into a superclass.


    protected SiteFile file;					// The SiteFile to display.
    protected boolean authOverride;				// Indicates if the request came with a SiteFilePassword allowing access


    /**
     * Designated constructor
     */
    public DisplayFile(WOContext aContext)
    {
        super(aContext);
        setAuthOverride(false);
    }



    /**
     * Overridden to handle authentication for access group protected SiteFiles.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        DebugOut.println(16,"Entering...");

        Session theSession = (Session)session();
    	boolean canRemoteParticipationUserViewSection = false;
    	if (file != null)
    	{
    		canRemoteParticipationUserViewSection = theSession.isRemoteParticipantSession() &&
				theSession.authenticatedRemoteParticipantGroup() != null &&
				theSession.authenticatedRemoteParticipantGroup().parentWebsite().globalID().equals(file.website().globalID()) &&
				file.groups().containsObject(theSession.authenticatedRemoteParticipantGroup());
    	}

    	// No file, no service
        if (file == null)
        {
            DebugOut.println(1,"ERROR: No file set.");
        }
        else if ((file.isViewableByUser(theSession.currentUser())) || authOverride() ||
        		canRemoteParticipationUserViewSection)	// You get the file.
        {
            File fileOnDisk = new File(file.filePath());
            
            if ( ! fileOnDisk.exists())
            {
                fileNotFound().appendToResponse(response, context);
                return;
            }
            
            DebugOut.println(16,"==== ACCESS ALLOWED for user " + ((Session)session()).currentUser().userID());

            response.setHeader("inline; filename=\"" + file.uploadedFilename() + "\"", Response.ContentDispositionHeaderKey);
            response.setHeader(file.mimeType(), Response.ContentTypeHeaderKey);

            // Set the file contents as an InputStream so the download streams, thus conserving memory
            int maxBufferSize = 8 * SMFileUtils.BYTES_PER_MEGABYTE;
            int bufferSize = (fileOnDisk.length() < maxBufferSize) ? (int) fileOnDisk.length() : maxBufferSize;
            try {
                response.setContentStream(new FileInputStream(fileOnDisk), bufferSize, (int) fileOnDisk.length());
            } catch (FileNotFoundException e) {
                throw new ExceptionConverter(e);
            }
        }
        else if (((Session)session()).isUserAuthenticated())	// You get 'permission denied'
        {
            response.setContent(fileError().generateResponse().content());
        }
        else // You get a login screen
        {
            DebugOut.println(16,"==== ACCESS DENIED for user " + ((Session)session()).currentUser().userID());
            DebugOut.println(16,"SiteFile's groups are: " + file.groups());
            SMAuthComponent.redirectToLogin(response, context, "Authentication is required to access this File.");
        }
        DebugOut.println(16,"Leaving...");
    }


    
    /**
     * Returns the error page to display if the user has authenticated, but does not have sufficient 
     * permissions to view this SiteFile.
     *
     * @return the error page to display if the user has authenticated, but does not have sufficient 
     * permissions to view this SiteFile.
     *
     * @return != null
     */
    protected WOComponent fileError()
    {
        ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
        errorPage.setReturnPage(this);
        errorPage.setMessage("Sorry, you don't have permission to view that file.");
        errorPage.setReason("Insufficient privileges.");

        return errorPage;
    }


    
    /**
     * Returns the error page to display if the user has authenticated and has sufficient permissions 
     * to view this SiteFile, but the file has been removed from the uploaded files directory.  This is
     * usually caused by concurrency issues: one instance has deleted it while another is trying to 
     * display it.
     *
     * @return the error page to display if the user has authenticated and has sufficient permissions 
     * to view this SiteFile, but the file has been removed from the uploaded files directory
     *
     * @return != null
     */
    protected WOComponent fileNotFound()
    {
        ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
        errorPage.setReturnPage(this);
        errorPage.setMessage("Sorry, the file you requested has been deleted.");
        errorPage.setReason("File not found.");

        return errorPage;
    }


    /* Generic setters and getters ***************************************/

    public SiteFile file() {
        return file;
    }
    public void setFile(SiteFile newFile) {
        file = newFile;
    }

    public boolean authOverride() {
        return authOverride;
    }
    public void setAuthOverride(boolean newAuthOverride) {
        authOverride = newAuthOverride;
    }

}

