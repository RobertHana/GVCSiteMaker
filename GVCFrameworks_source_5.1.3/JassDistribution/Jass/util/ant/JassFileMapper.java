package jass.util.ant;

import org.apache.tools.ant.util.FileNameMapper;
import jass.util.SourceFile;

/**
 * This class is used to map files with the Jass extension (".jass" or 
 * ".java") to the destination filename. Since the destination file
 * is a Java source file it will have the ".java" extension.
 */
public class JassFileMapper implements FileNameMapper {

    /**
     * Ignored. Since we only map Jass source files we do not need
     * a setFrom filter.
     */
    public void setFrom(String from) {
    }

    /**
     * Ignored. Since we only map to Java source files we do not need
     * a setTo filter.
     */
    public void setTo(String to) {
    }


    /**
     * Maps a Jass source file to a Java source file. Jass source files 
     * have extensions ".jass" or ".java". If the source file has none
     * of this, the return value will be null. In any other case a possible
     * ".jass" extension will be replaced by a ".java" extension and
     * this result will be the only value in the returned String array.
     *
     * @param sourceFileName  the name of the source file relative to some 
     *                        given basedirectory.
     * @see jass.reflect.SourceFile.createDestination
     */
    public String[] mapFileName(String sourceFileName) {
	SourceFile from = new SourceFile(sourceFileName);
	if (from.hasUnknownExtension()) {
	    return null;
	}
	SourceFile to = from.createDestination(null, null);
	return new String[] { to.toString() };
    }

}
