package net.global_village.eofextensions;

import com.webobjects.eocontrol.*;

/**
 * EOEditingContext that clears undo stack after a successful save.  This is used as a work around for a bug in EOF. There is a rather serious bug when validateForDelete() fails to allow a deletion.  This will occur if you are using the Deny delete rule, the relationship is mandatory, or you have a custom validateForDelete() method.  The error occurs in this scenario:<br>
 * 1. The editing context has multiple generations, meaning that saveChanges() has been called one or more times after one or more EOs has been created / inserted / updated.<br>
 * 2. An EO is deleted from the editing context by deleteObject().<br>
 * 3. saveChanges() fails due to an NSValidation.ValidationException raised in validateForDelete.<br>
 *
 * This result of this appears to be that undo() is called on the editing context's undo manager too many times.  Instead of rolling back to the state when saveChanges() was called it rolls back past several of previous saveChanges()!  The result of this is that the editing context shows a historical state that does not match the object store or the database.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class ForgetfulEC extends EOEditingContext
{


    /**
     * Designated constructor.  Creates a new ForgetfulEC object with anObjectStore as its parent
     * object store.
     *
     * @param anObjectStore parent object store
     */
    public ForgetfulEC(EOObjectStore anObjectStore)
    {
        super(anObjectStore);
    }



    /**
     * Creates a new ForgetfulEC object with the default parent object store as its parent object
     * store.
     */
    public ForgetfulEC()
    {
        super();
    }



    /**
    * Overridden to clear undo stack after a successful save.
    */
    public void saveChanges()
    {
        super.saveChanges();
        if (undoManager() != null)
        {
            undoManager().removeAllActions();
        }
    }

}
