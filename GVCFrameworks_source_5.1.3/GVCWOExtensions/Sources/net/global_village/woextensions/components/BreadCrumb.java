package net.global_village.woextensions.components;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * This component creates a bread crumb trail, which looks like this:
 * Main > User Settings > Demographic
 * To use this, pass an NSArray of Crumbs to this component's trail binding.
 * Each NSDictionary's key will be used as the value to display, while the value will
 * be used as an action method on the page.
 *
 * @author Copyright (c) 2007  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */
public class BreadCrumb extends net.global_village.woextensions.WOComponent
{
	public static final String DefaultSeparator = ">";

	protected String separator;
	protected NSArray trail;
	public int index;
	public Crumb crumb;


	/**
	 * The Crumb class, used to pass in to a BreadCrumb component.
	 * Note that Jass doesn't like nested classes, so all contracts are comments.
	 */
	public static class Crumb
	{
		protected String title;
		protected String action;


		/**
	     * Designated constructor.
	     */
	    public Crumb(String aTitle, String anAction)
	    {
	        super();
	        /* require [valid_param] aTitle != null; **/

	        title = aTitle;
	        action = anAction;
	    }



		/**
	     * Set the title.
	     *
	     * @param newTitle the title to set
	     */
	    public void setTitle(String newTitle)
	    {
	        /* require [valid_param] newTitle != null; **/
	    	title = newTitle;
	    }


	    /**
	     * Returns the title.
	     *
	     * @return the title
	     */
	    public String title()
	    {
	        return title;
	        /* ensure [valid_result] Result != null; **/
	    }



	    /**
	     * Set the action.
	     *
	     * @param newAction the action to set
	     */
	    public void setAction(String newAction)
	    {
	    	action = newAction;
	    }


	    /**
	     * Returns the action.
	     *
	     * @return the action
	     */
	    public String action()
	    {
	        return action;
	    }


	}



	/**
     * Designated constructor.
     */
    public BreadCrumb(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Returns the trail array.
     *
     * @return the trail array
     */
    public boolean notFirstCrumb()
    {
        return index > 0;
    }



    /**
     * Performs the action.
     *
     * @return the component the action returns
     */
    public WOActionResults doAction()
    {
        return performParentAction(crumb.action());
    }



    /**
     * Returns the separator bound to separator, or the DefaultSeparator if the binding is null.
     *
     * @return the separator
     */
    public String separator()
    {
    	if (separator == null)
    	{
    		return DefaultSeparator;
    	}
    	else
    	{
    		return separator;
    	}
    }


    /**
     * Set the separator.
     *
     * @param newSeparator the separator to set
     */
    public void setSeparator(String newSeparator)
    {
        /** require [valid_param] newSeparator != null; **/
    	separator = newSeparator;
    }



    /**
     * Returns true iff the current crumb has an action.
     *
     * @return true iff the current crumb has an action
     */
    public boolean hasAction()
    {
        return crumb.action() != null;
    }



    /**
     * Returns the trail array.
     *
     * @return the trail array
     */
    public NSArray trail()
    {
        return trail;
    }


    /**
     * Set the trail array
     *
     * @param newTrail the trail array to set
     */
    public void setTrail(NSArray newTrail)
    {
        /** require [valid_param] newTrail != null; **/
        trail = newTrail;
    }



}
