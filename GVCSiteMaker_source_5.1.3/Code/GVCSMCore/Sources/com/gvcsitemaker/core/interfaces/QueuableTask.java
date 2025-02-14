// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.interfaces;

import com.gvcsitemaker.core.*;

/**
 * Interface for Tasks that will be queued. 
 */
public interface QueuableTask 
{
    /**
     * The Task to be peformed.  Returns true if Task is performed successfully, false otherwise
     *
     * @return true if Task is performed successfully, false otherwise
     */
	public boolean performTask();
	
	
	
    /**
     * Returns true if this Task is the same as the otherTask.  
     * 
     * @return true if this Task is the same as the otherTask
     */
    public boolean isEqualToTask(Task otherTask);	
	
	
}
