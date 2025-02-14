package jass.util;

import java.io.File;

/**
 * This class is used to store additional Jass-specific type
 * information to a standard file type.
 *
 * @author Michael Plath, Michael Moeller
 * @version %I%, %G% 
 */
public class SourceFile extends File {

    /** The extension that a Jass file should have */
    public static final String JASS_EXTENSION = "jass";

    /** The extension of a Java SourceFile */
    public static final String JAVA_EXTENSION = "java";

    protected static final int    UNKNOWN_TYPE = 0;
    protected static final int    JASS_TYPE = 1;
    protected static final int    JAVA_TYPE = 2;
    protected int type = UNKNOWN_TYPE;

    /**
     * Creates a new File instance from a parent abstract pathname and a 
     * child pathname string and determines the type by extension.
     *
     * If parent is null then the new File instance is created as if by 
     * invoking the single-argument File constructor on the given child 
     * pathname string.
     * 
     * @param parent The parent pathname string
     * @param child The child pathname string
     *
     * @see java.io.File
     */
    public SourceFile(File parent, String child) {
	super(parent, child);
	setType(child);
    }

    /**
     * Creates a new File instance by converting the given pathname
     * string into an abstract pathname and determining the type by
     * extension. If the given string is the empty string, then the
     * result is the empty abstract pathname.
     *
     * @param pathname  A pathname string
     *
     * @see java.io.File
     */
    public SourceFile(String pathname) {
	super(pathname);
	setType(pathname);
    }

    /**
     * Creates a new File instance from a parent pathname string and a
     * child pathname string.
     *
     * If parent is null then the new File instance is created as if
     * by invoking the single-argument File constructor on the given
     * child pathname string.
     *
     * Otherwise the parent pathname string is taken to denote a
     * directory, and the child pathname string is taken to denote
     * either a directory or a file. If the child pathname string is
     * absolute then it is converted into a relative pathname in a
     * system-dependent way. If parent is the empty string then the
     * new File instance is created by converting child into an
     * abstract pathname and resolving the result against a
     * system-dependent default directory. Otherwise each pathname
     * string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     *
     * @param parent  The parent pathname string
     * @param child  The child pathname string
     *
     * @see java.io.File
     */
    public SourceFile(String parent, String child) {
	super(parent, child);
	setType(child);
    }


    /**
     * Set the type of the file by checking if it ends withe the
     * Java or Jass extension
     *
     * @param child  the filename to look at
     */
    private void setType(String child) {
	if (child.endsWith("." + JASS_EXTENSION)) {
	    this.type = JASS_TYPE;
	}
	else if (child.endsWith("." + JAVA_EXTENSION)) {
	    this.type = JAVA_TYPE;
	}
	else {
	    this.type = UNKNOWN_TYPE;
	}
    }

    /**
     * Has this file the Jass extension?
     *
     * @return true, if it has the jass extension
     */
    public boolean hasJassExtension() {
	return (this.type == JASS_TYPE);
    }

    /**
     * Has this file the Java extension?
     *
     * @return true, if it has the java extension
     */
    public boolean hasJavaExtension() {
	return (this.type == JAVA_TYPE);
    }

    /**
     * Has this file the neither Java nor Jass extension?
     *
     * @return true, if it has neither java nor jass extension
     */
    public boolean hasUnknownExtension() {
	return (this.type == UNKNOWN_TYPE);
    }


    /**
     * Return true if we are able to create a destination file
     * for this SourceFile.
     *
     * We are able to create a Sourcefile either if this file
     * has the Jass extension, by replacing it with the Java 
     * extension, or if a destination directory is given.
     *
     * @param destinationDir  destination directory
     *
     * @return true, if we can create a filename for the destionation
     */
    public boolean isDestionationAvailable(File destinationDir) {
	if (hasJassExtension()) {
	    return true;
	}
	if (destinationDir == null || 
	    hasUnknownExtension() ||
	    !destinationDir.isDirectory()) {
	    return false;
	}
	return (! getParentFile().equals(destinationDir));
    }

    /**
     * Create a SourceFile where to generate the output to.
     * This will always create a file withe the Java extension. If a 
     * destination directory is provided, then also the package
     * information is needed, to create the file.
     *
     * CAUTION: This method does not take care whether we overwrite 
     * this file itself. To check please compare the result and this
     * SourceFile.
     * 
     * @param destinationDir  the destination directory (or null, if
     *                        same directory should be used)
     * @param thePackage      the package information (only required,
     *                        if destination directory was given)
     * @return the SourceFile where to produce the output to.
     */
    public SourceFile createDestination(File destinationDir, 
					String thePackage) {
	String file = getName();
	if (hasJassExtension()) {
	    file = file.substring(0, file.lastIndexOf(".")) 
		+ "." + JAVA_EXTENSION;
	}
	if (destinationDir == null) {
	    return new SourceFile(getParent(), file);
	}
	if (thePackage == null) {
	    return new SourceFile(destinationDir, file);
	}
	String subdir = thePackage.replace('.',File.separatorChar);
	return new SourceFile(destinationDir, 
			      subdir + File.separatorChar + file );
    }



    /**
     * Guess the package of this SourceFile from its filename and the
     * current dirctory.
     * 
     * We consider the call of Jass was done from the root directory of 
     * the implementation. Therefore the relative path from the current
     * directory to this file will be the package information.
     *
     * @return a package String (packages seperated by ".")
     * 
     * @see guessPackage(java.io.File)
     */
    public String guessPackage() {
	return guessPackage(new File("."));
    }

    /**
     * Guess the package of this SourceFile from its filename and the
     * given root dirctory.
     * 
     * We consider the rootDir is the root directory of the implementation.
     * Therefore the relative path from the rootDir to this file will be 
     * the package information.
     *
     * @param rootDir  the root directory of the implemention (root of the 
     *                 source files)
     * @return a package String (packages seperated by ".")
     */
    public String guessPackage(File rootDir) {
	try {
	    return guessClassName(rootDir, 
				  getParentFile().getCanonicalPath());
	} catch (java.io.IOException e) {
	    return null;
	}
    }

    /**
     * Guess the class name that is specified in this SourceFile from
     * its filename and the current dirctory.
     *  
     * We consider the call of Jass was done from the root directory of 
     * the implementation. Therefore the relative path from the current
     * directory to this file will be the package information.
     *
     * @return a full qualified class name
     */
    public String guessClassName() {
	return guessClassName(new File("."));
    }


    /**
     * Guess the class name that is specified in this File from the filename 
     * and the given root dirctory.
     *
     * We consider the rootDir is the root directory of the implementation.
     * Therefore the relative path from the rootDir to this file will be 
     * the package information.
     *
     * @param rootDir  the root directory of the implemention (root of the 
     *                 source files)
     * @return a full qualified class name
     */
    public String guessClassName(File rootDir) {
	try {
	    return guessClassName(rootDir, getCanonicalPath());
	} catch (java.io.IOException e) {
	    return null;
	}
    }

	
    /**
     * Internal method for guessing the package or class name of this
     * file. 
     *
     * @param rootDir    the root directory of the implemention (root of
     *                   the source files)
     * @param canonical  the canonical form of this File (to get the class), 
     *                   or this File's directory (to get the package).
     * @return a full qualified class name or package
     */
    private static String guessClassName(File rootDir, String canonical) {
	try {
	    int rootLength = rootDir.getCanonicalPath().length();
	    String thePackage = canonical
		.substring(rootLength)
		.replace(File.separatorChar,'.');
	    int begin = thePackage.startsWith(".") ? 1 : 0;
	    int end = thePackage.endsWith(".") 
		? thePackage.length()-1 
		: thePackage.length();
	    return thePackage.substring(begin, end);
	} catch (java.io.IOException e) {
	    return null;
	} catch (IndexOutOfBoundsException e) {
	    return null;
	}
    }

    /**
     * Create all subdirectories, that are needed to be able to create
     * this file. This will only create new directories if they are 
     * needed.
     */
    public void createSubdirs() { //throws IOException {
	File dir = this.getParentFile();
	if (dir != null && !dir.exists()) {
	    dir.mkdirs();
	}
    }
	
}


