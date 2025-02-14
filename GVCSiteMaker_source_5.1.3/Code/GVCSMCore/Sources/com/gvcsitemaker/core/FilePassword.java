// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class FilePassword extends _FilePassword 
{

    //                                       milliseconds x seconds per minute x minutes per hour x hours per day
    static final long millisecondsPerDay =     1000    *        60        *        60       *      24;

    /**
     * Factory method to create new instances of FilePassword.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of FilePassword or a subclass.
     */
    public static FilePassword newFilePassword()
    {
        return (FilePassword) SMEOUtils.newInstanceOf("FilePassword");

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns the expiration date for a new FilePassword.
     *
     * @return the expiration date for a new FilePassword.
     */
    public static NSTimestamp getExpiration()
    {
        Long daysTilExpiration = new Long(SMApplication.appProperties().stringPropertyForKey("FilePasswordExpirationTimeInDays"));
        long milliSecondsFromNow = (daysTilExpiration.longValue() * millisecondsPerDay);
        NSTimestamp expiration = Date.accurateToSecond(new NSTimestamp(milliSecondsFromNow, new NSTimestamp()));

        JassAdditions.post("FilePassword", "getExpiration() [date_in_future]", expiration.getTime() > (System.currentTimeMillis()));

        return expiration;

        /** ensure [return_not_null] Result != null;   **/
    }



    /**
     * Overridden to generate password and set expiration date.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);

        setPassword(SMStringUtils.randomKeyWithLength(17));
        setExpirationDate(getExpiration());
    }



}
