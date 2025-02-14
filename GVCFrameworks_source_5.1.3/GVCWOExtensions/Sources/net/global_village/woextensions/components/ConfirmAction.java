package net.global_village.woextensions.components;

import com.webobjects.appserver.*;


/**
 * A component that allows user to confirm or deny a specific action. This is used in conjuction with a class implementing Confirmable.  When the user confirms or denies the action, the appropriate method on objectToNotify() is invoked and the result returned as the next page.  As this is a component you will either need to display it on the page needing confirmation (wrapped in a WOConditional so that it only displays when needed), or on a page of its own.
 * <br><br>
 * Below is an example on how to use this component:<br><br>
 * <pre>
 * public class Main extends WOComponent implements Confirmable  //Make sure that your page implements Confirmable
 * {
 *    ...
 *   public WOComponent actionNeedingConfirmation()
 *   {
 *       PageWithConfirmAction confirmPage = (PageWithConfirmAction) pageWithName("PageWithConfirmAction");
 *       confirmPage.setMessage("Are you sure you want to foo this bar?");
 *       confirmPage.setObjectToNotify(this);
 *       return confirmPage;
 *   }
 *
 *   public WOComponent confirmAction()
 *   {
 *       //code to invoke when action is confirmed
 *       return context().page();
 *   }
 *
 *   public WOComponent denyAction()
 *   {
 *       //code to invoke when action is denied
 *       return context().page();
 *   }
 *    ...
 * }
 *
 * WOD File of PageWithConfirmAction
 * ConfirmAction: ConfirmAction {
 *    message = message;                     // Default is Are you sure you want to do this?"
 *    confirmString = "Yes, foo it.";        // Default is "Yes"
 *    denyString = "No. Don't foo it.";      // Default is "No"
 *    objectToNotify = pageToNotify;         // No default, must be set.
 * }
 * </pre>
 * <br><br>
 * For the case where a page has more than one action to confirm, use inner classes that implement Confirmable.  For example:
 * <br><br>
 * <pre>
 * public class AssignPersonnelPage extends BasePage
 * {
 *     private class ConfirmDeletion implements Confirmable
 *     {
 *         public WOComponent confirmAction()
 *         {
 *             return doDelete();  // method on AssignPersonnelPage
 *         }
 *
 *         public WOComponent denyAction()
 *         {
 *             return cancelChanges();  // method on AssignPersonnelPage
 *         }
 *     }
 *
 *     private class ConfirmNotification implements Confirmable
 *     {
 *         public WOComponent confirmAction()
 *         {
 *             return sendEmailNoitification();  // method on AssignPersonnelPage
 *         }
 *
 *         public WOComponent denyAction()
 *         {
 *             return cancelChanges();  // method on AssignPersonnelPage
 *         }
 *     }
 *
 *     public WOComponent doDelete()
 *     {
 *         // Do whatever
 *         return resetPage();    
 *      }
 *
 *   public WOComponent actionNeedingConfirmation()
 *   {
 *       PageWithConfirmAction confirmPage = (PageWithConfirmAction) pageWithName("PageWithConfirmAction");
 *       confirmPage.setMessage("Are you sure you want to send notification of fooing this bar?");
 *       confirmPage.setObjectToNotify(new ConfirmNotification());
 *       return confirmPage;
 *   }
 *
 * etc.
 * </pre>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class ConfirmAction extends WOComponent
{
    protected String confirmString;
    protected String denyString;
    protected String message;

    static final public String DefaultMessage = "Are you sure you want to do this?";
    static final public String DefaultConfirmString = "Yes";
    static final public String DefaultDenyString = "No";


    /**
     * Designated constructor.  Sets defaults for message, confirmString, and denyString.
     */
    public ConfirmAction(WOContext aContext)
    {
        super(aContext);
        /** require
        [valid_param] aContext != null;
        [context_has_page] aContext.page() != null; **/
    }



    /**
     * This method is overridden so variables are not synchronized with bindings.
     *
     * @return <code>false</code>
     */
     public boolean synchronizesVariablesWithBindings()
     {
         return false;
     }



    /**
     * Invokes confirmAction() method on objectToNotify() when the action is confirmed and returns the result of confirmAction().
     *
     * @return returns the component to display when the action is confirmed
     */
    public com.webobjects.appserver.WOComponent notifyOfConfirmation()
    {
        return objectToNotify().confirmAction();

        /** ensure [valid_result] Result != null; **/
    }


    
    /**
     * Invokes denyAction() method on objectToNotify() when the action is denied and returns the result of denyAction().
     *
     * @return returns the component to display when the action is denied
     */
    public com.webobjects.appserver.WOComponent notifyOfDenial()
    {
        return objectToNotify().denyAction();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the object to notify when the user confirms or denies the action.
     *
     * @return the object to notify when the user confirms or denies the action.
     */
    public Confirmable objectToNotify()
    {
        return (Confirmable)valueForBinding("objectToNotify");

        /** ensure [valid_result] Result != null; **/
    }

    

    /**
     * The string to display in the confirm button.
     *
     * @return string to display in the confirm button.
     */
    public String confirmString()
    {
        return (valueForBinding("confirmString") == null) ? DefaultConfirmString : (String)valueForBinding("confirmString");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * The string to display in the deny button.
     *
     * @return string to display in the deny button.
     */
    public String denyString()
    {
        return (valueForBinding("denyString") == null) ? DefaultDenyString : (String)valueForBinding("denyString");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * The string to display as the question.
     *
     * @return string to display as the question.
     */
    public String message()
    {
        return (valueForBinding("message") == null) ? DefaultMessage : (String)valueForBinding("message");

        /** ensure [valid_result] Result != null; **/
    }



}
