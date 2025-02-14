package net.global_village.anttasks.eogenerator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;



/**
 * Syntatic sugar sub-class of Path so that a path structure can be called 
 * ReferencedModels and the elements in the path structure can be called Model 
 * instead of pathelement.  
 * 
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ReferencedModels extends Path
{

    /**
     * Syntatic sugar sub-class of PathElement so that elements in the path 
     * structure can be called Model instead of pathelement.  
     */
    public class Model extends Path.PathElement
    {
    }
    
    
    
    public ReferencedModels(Project project, String path)
    {
        super(project, path);
    }



    public ReferencedModels(Project project)
    {
        super(project);
    }



    /** 
     * Renamed version of createPathElement so that that elements in the path 
     * structure can be called Model instead of pathelement.
     * 
     * @see org.apache.tools.ant.types.Path#createPathElement()
     */
    public PathElement createModel() throws BuildException
    {
        return super.createPathElement();
    }

}
