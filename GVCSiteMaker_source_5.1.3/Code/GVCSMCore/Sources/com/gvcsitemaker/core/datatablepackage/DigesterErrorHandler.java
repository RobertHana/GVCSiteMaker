// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

import org.xml.sax.*;

/**
 * By default, when Digester encounters DTD parsing errors, it just adds it to the application logs.  This error handler is needed for the exceptions to be thrown instead of just logged. 
 */
public class DigesterErrorHandler implements org.xml.sax.ErrorHandler 
{
    public void warning(SAXParseException ex) throws SAXParseException 
    {
       // stop processing on warnings
        throw ex;
    }

    
    
    public void error(SAXParseException ex) throws SAXParseException 
    {
       // stop processing on errors
        throw ex;
    }

    
    
    public void fatalError(SAXParseException ex) throws SAXParseException 
    {
       // stop processing on fatal errors
        throw ex;
    }
  }