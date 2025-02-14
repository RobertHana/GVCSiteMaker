/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.util;

import java.io.*;
import java.util.Vector;

/**
 * This class provides a collection of static methods for file
 * operations. <BR> 
 * Note that this class doesn't subclass
 * <code>java.io.File</code>.
 * <P> 
 * CONVENTION: A directory name must allways end with with a
 * directory separator.  Hence the following are valid directory names
 * "usr/", "/", while "usr/local" is not.  The type of the directory
 * separator may vary. By default the system dependent string must be
 * used. However, most of the classes allow one to customize this
 * string.
 * 
 * @author 	Michael Plath
 * @version 0.9, 10/27/99 
 */
public class FileTools extends Object {

    /**
     * Method declaration
     *
     *
     * @param directoryList
     *
     * @see
     */
    protected static void resolveSpecialDirEntries(Vector directoryList) {
	int iDir = directoryList.size() - 1;

	while (iDir >= 0) {
	    String filename = (String) directoryList.get(iDir);

	    if (filename.compareTo(".") == 0) {
		directoryList.remove(iDir);
	    } 

	    --iDir;
	} 
    } 

    /**
     * Store the lines of a textfile into a
     * <code>java.util.vector</code> object 
     */
    public static Vector contentAsVector(String filename) 
	    throws FileNotFoundException, IOException {
	BufferedReader in = null;
	Vector	       out = new Vector();

	in = 
	    new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

	String line;

	while ((line = in.readLine()) != null) {
	    out.addElement(line);
	} 

	return (out);
    } 

    /**
     * Separate from a directory+filename notation the directory name.
     * The notation is assumed system dependend.
     * @see #getDirname(java.lang.String, java.lang.String)
     */
    public static String getDirname(String filename) {
	return getDirname(filename, File.separator);
    } 

    /**
     * Separate from a directory+filename notation the directory name.
     * The notation uses a customized directory separator.
     * @param file file
     * @param separator directory separator
     * @result directory name
     */
    public static String getDirname(String filename, String separator) {

	// If the filename ends with a separator then the filename
	// belongs to a directory
	if (filename.endsWith(separator)) {
	    return (filename);
	} 

	int i_lastSeparator = filename.lastIndexOf(separator);

	if (i_lastSeparator < 0) {
	    return ("");
	} 

	return (filename.substring(0, i_lastSeparator + 1));
    } 

    /**
     * Return only the directory name of a <code>java.io.File</code> object.
     * The notation is assumed system dependend.
     * @see #getDirname(java.lang.String, java.lang.String)
     * @see java.io.File
     */
    public static String getDirname(File file) {
	return getDirname(file.getPath(), File.separator);
    } 

    /**
     * Return only the directory name of a java.io.File object.
     * The notation uses a customized directory separator.
     * @param file file
     * @param separator directory separator
     * @see #getDirname(java.lang.String, java.lang.String)
     * @see java.io.File
     */
    public static String getDirname(File file, String separator) {
	return getDirname(file.getPath(), separator);
    } 

    /**
     * Separate from a directory+filename notation the file name.
     * The notation is assumed system dependend.
     * @see #getFilename(java.lang.String, java.lang.String)
     * @see java.io.File
     */
    public static String getFilename(String filename) {
	return getFilename(filename, File.separator);
    } 

    /**
     * Separate from a directory+filename notation the file name.
     * The notation uses a customized directory separator.
     * @param file file
     * @param separator directory separator
     * @return filename
     */
    public static String getFilename(String filename, String separator) {
	int i_lastSeparator = filename.lastIndexOf(separator);

	if (i_lastSeparator < 0) {
	    return ("");
	} 

	return (filename.substring(i_lastSeparator + 1, filename.length()));
    } 

    /**
     * Return only the directory name of a <code>java.io.File</code> object.
     * The notation is assumed system dependend.
     * @see #getFilename(java.lang.String, java.lang.String)
     * @see java.io.File
     */
    public static String getFilename(File file) {
	return getFilename(file.getPath(), File.separator);
    } 

    /**
     * Return only the file name of a <code>java.io.File</code> object.
     * The notation uses a customized directory separator.
     * @param file file
     * @param separator directory separator
     * @see #getFilename(java.lang.String, java.lang.String)
     * @see java.io.File
     */
    public static String getFilename(File file, String separator) {
	return getFilename(file.getPath(), separator);
    } 

    /**
     * Convert an absolute path to a path relative to a certain base path.
     * The notation is assumed system dependend.
     * @see java.io.File
     * @see #getRelativePath(java.lang.String, java.lang.String, java.lang.String)
     */
    public static String getRelativePath(String basePath, String path) {
	return getRelativePath(basePath, path, File.separator);
    } 

    /**
     * Convert an absolute path to a path relative to a certain base path.
     * Examples:<br>
     * basePath "/usr/", path "/usr/local/bin/" -> "local/bin/"<br>
     * basePath "/usr/local/X11/lib/" path "/usr/local/bin/" -> "../../bin/"<br>
     * @param baseDir base directory
     * @param path
     * @param separator directory separator
     * @result relative path
     */
    public static String getRelativePath(String baseDir, String path, 
					 String separator) {

	// Check if path matches with baseDir.
	// Match sucessive by matching subdirectories one by one.
	// To do this, convert path names to vectors and check
	// each single element of the vector.
	Vector vec_baseDir = StringTools.stringToVector(baseDir, separator);

	resolveSpecialDirEntries(vec_baseDir);

	Vector vec_path = StringTools.stringToVector(path, separator);

	resolveSpecialDirEntries(vec_path);

	int dirMatches = 0;    // Number of directories matched so far
	int numDirsOfBaseDir = vec_baseDir.size();

	if (((String) vec_baseDir.lastElement()).length() == 0) {
	    --numDirsOfBaseDir;
	} 

	int numDirsOfPath = vec_path.size();

	if (((String) vec_path.lastElement()).length() == 0) {
	    --numDirsOfPath;
	} 

	String match = "";

	while (dirMatches < numDirsOfBaseDir && dirMatches < numDirsOfPath 
	       && vec_baseDir.elementAt(dirMatches).equals(vec_path.elementAt(dirMatches))) {
	    match += vec_baseDir.elementAt(dirMatches) + separator;
	    ++dirMatches;
	} 

	// All subdirectory which match do not belong to the target (relative) path.
	// If the base path has a greater depth than the absolute path, the new
	// relative path must ascend the difference of depths.
	// The next step is to add all the remaining directories of the absolute
	// path which do not match.
	String newPath = "";
	int    i;

	// Ascend to higher directories
	for (i = dirMatches; i < numDirsOfBaseDir; ++i) {
	    newPath += ".." + separator;
	} 

	// Add all remaining directories
	for (i = dirMatches; i < numDirsOfPath; ++i) {
	    String directory = (String) vec_path.elementAt(i);

	    if (directory.length() != 0)    // == 0 means 'root directory'
	     {
		newPath += directory + separator;
	    } 
	} 

	// Remove trailing separator, if path is no directory
	if (!path.endsWith(separator)) {
	    newPath = newPath.substring(0, 
					newPath.length() 
					- separator.length());
	} 

	return newPath;
    } 

    /**
     * Check the existence of a path.  The java-methode
     * <code>File.exists()</code> shows weakness, because it checks
     * the existence of a file system dependent. This leads to
     * different behaviour of java programs depending whether a system
     * makes a difference between lower and upper case letters in a
     * path.<br> Consider the following situation: System A does
     * differentiate between upper and lower cases while system B does
     * not. Both systems have a file with a physical path
     * "/PUB/readme.txt".  Then the invokation of the method "(new
     * File("/pub/readme.txt")).exists()" return <code>true</code> on
     * system B while it return <code>false</code> on system A.<br>
     * <br> The notation uses a customized directory separator
     * @param path
     * @param separator directory separator
     * @return true iff path is valid
     * @see java.io.File 
     */
    public static boolean validatePath(String path, String separator) {
	return validatePath(StringTools.replaceSubstring(path, separator, 
		File.separator));
    } 

    /**
     * Check the existence of a path.
     * @see #validatePath(java.lang.String, java.lang.String)
     */
    public static boolean validatePath(String path) {
	String name = null;
	String parent = null;
	String dirEntry[] = null;

	// First look if the according method of java.io.File 'knows' the path. If not
	// we do not have to do any more.
	File   file = new File(path);

	if (!file.exists()) {
	    return false;
	} 

	// Ascend from the path to the root directory.
	// Do a case sensitive comparison: sub-path <-> current directory entries
	// The recursion ends if
	// a) we reach the root diretoy (root means 'no parent') -> path exists
	// b) we detect a difference while comparing -> path does not exist
	name = file.getName();
	parent = file.getParent();

	// System.out.println("#Filename:" + name);
	if (parent == null) {

	    // System.out.println("TRUE");
	    return true;
	} 

	dirEntry = new File(parent).list();

	// System.out.println("#List:" + StringTools.toString(dirEntry));
	for (int i = 0; i < dirEntry.length; ++i) {
	    String currentEntry = dirEntry[i];

	    // Handle special directory name "." as exception:
	    // it allways exists
	    if (name.equals(".") 
		    || currentEntry.compareToIgnoreCase(name) == 0) {
		return validatePath(parent);
	    } 
	} 

	return false;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/
