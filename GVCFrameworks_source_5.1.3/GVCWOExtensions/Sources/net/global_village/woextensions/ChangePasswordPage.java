package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.security.*;


/**
 * This class provides the basis of a commonly require page in WebObjects applications.  It allows an end user to change his/her password and updates the database.  It relies on ValidatingPage, and GVCJSecurity.User.  Any application which uses this page must have user() method in the Session class of their project and that method must return GVCJSecurity.User or a subclass of it.  The user of this class can override isNewPasswordValid() to provide custom verification of the new password.  pageToProceedToAfterSave() should also be overridden, it is in ValidatingPage.  CustomInfo.plist contains default error messages but your application can provide custom error messages.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class ChangePasswordPage extends ValidatingPage
{
    protected String replacementPassword;
    protected String confirmReplacementPassword;
    private NSSelector userSelector = new NSSelector("user", new Class[] {});

    protected User currentUser;
    

    /**
     * Designated constructor.
     */
    public ChangePasswordPage(WOContext aContext)
    {
        super(aContext);

        try
        {
            User tempUser = (User) userSelector.invoke(session());
            currentUser = (User) privateCopyOfObject(tempUser);
        }
        catch (java.lang.Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }



    /**
     * Accessor for replacementPassword
     *
     * @return the user's desired replacementPassword
     */
    public String replacementPassword()
    {
        return replacementPassword;
    }



    /**
     * Mutator for replacementPassword.
     *
     * @param newReplacementPassword new version of replacementPassword
     */
    public void setReplacementPassword(String newReplacementPassword)
    {
        replacementPassword = newReplacementPassword;
    }

    

    /**
     * Accessor for confirmReplacementPassword
     *
     * @return the user's entry of a duplicate of replacementPassword
     */
    public String confirmReplacementPassword()
    {
        return confirmReplacementPassword;
    }

    

    /**
     * Mutator for confrimReplacementPassword.
     *
     * @param newConfirmReplacementPassword new version of confirmReplacementPassword
     */
    public void setConfirmReplacementPassword(String newConfirmReplacementPassword)
    {
        confirmReplacementPassword = newConfirmReplacementPassword;
    }



    /**
     * Checks that replacementPassword equals confirmReplacementPassword and differs from the user's original password.  Override this to provide custom password validation.  Records validation errors for display in ValidationErrorsPanel.
     *
     * @return whether new password entered is valid
     */
    public boolean isNewPasswordValid()
    {
        boolean isNewPasswordValid = false;

        // There currently is not a special error when null is entered
        if (((replacementPassword() == null) || (confirmReplacementPassword() == null)) || ( ! replacementPassword().equals(confirmReplacementPassword())))
        {
            String unequalError = "";
            if (DefaultValueRetrieval.defaultValueExists(this,
                                                         "Application.changePasswordPage.passwordAndConfirmationDoNotMatch"))
            {
                unequalError = DefaultValueRetrieval.defaultString(this,
                                                                   "Application.changePasswordPage.passwordAndConfirmationDoNotMatch");
            }
            else if (DefaultValueRetrieval.defaultValueExists(net.global_village.woextensions.WOApplication.class,
                                                              "Application.changePasswordPage.passwordAndConfirmationDoNotMatch"))
            {
                // The error message was not set in the subclassed application. Therefore the framework's value will be used
                unequalError = DefaultValueRetrieval.defaultString(net.global_village.woextensions.WOApplication.class,
                                                                   "Application.changePasswordPage.passwordAndConfirmationDoNotMatch");
            }
            addError(unequalError);
        }
        else if (replacementPassword().equals(currentUser().password()))
        {
            String equalError = "";
            if (DefaultValueRetrieval.defaultValueExists(this,
                                                         "Application.changePasswordPage.newPasswordMustBeNew"))
            {
                equalError = DefaultValueRetrieval.defaultString(this,
                                                                 "Application.changePasswordPage.newPasswordMustBeNew");
            }
            else if (DefaultValueRetrieval.defaultValueExists(net.global_village.woextensions.WOApplication.class,
                                                              "Application.changePasswordPage.newPasswordMustBeNew"))
            {
                // The error message was not set in the subclassed application. Therefore the framework's value will be used
                equalError = DefaultValueRetrieval.defaultString(net.global_village.woextensions.WOApplication.class,
                                                                 "Application.changePasswordPage.newPasswordMustBeNew");
            }
            addError(equalError);
        }
        else
        {
            isNewPasswordValid = true;
        }

        return isNewPasswordValid;
    }

    

    /**
     * Changes password if the new password is valid.
     *
     * @return net.global_village.woextensions.WOComponent either this page with errors panel or pageToProceedToAfterSave()
     */
    public net.global_village.woextensions.WOComponent changePassword()
    {
        net.global_village.woextensions.WOComponent resultingPage;

        if (isNewPasswordValid())
        {
            currentUser().changePasswordTo(replacementPassword());
            resultingPage = (net.global_village.woextensions.WOComponent) attemptSave();
            if (hasErrors())
            {
                editingContext().revert();
            }
        }
        else
        {
            resultingPage = (net.global_village.woextensions.WOComponent) context().page();
        }
        
        return resultingPage;
    }

    
    
    /**
     * Determines whether to show a Cancel button in UI.
     *
     * @return <code>true</code> if user can cancel changing password, <code>false</code> otherwise.
     */
    public boolean isCancelButtonShown()
    {     
        return ! currentUser().mustChangePassword().booleanValue();
    }



    /**
     * Uses NSSelector to get current User of system.  Programs that use this class must also have a session().user() method as noted above. 
     *
     * @return the current user of the program
     */
    protected User currentUser()
    {
        return currentUser;
    }



    /** invariant [has_current_user] currentUser != null; **/



}
