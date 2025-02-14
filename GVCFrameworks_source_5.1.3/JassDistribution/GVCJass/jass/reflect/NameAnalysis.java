/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import jass.GlobalFlags;
import jass.Jass;
import jass.parser.*;
import jass.visitor.*;
import jass.runtime.util.*;
import jass.util.SourceFile;

/**
 * This clazz analysis the names found by the jass visitors. Different
 * jobs are done: get the filename of a given type, look up if a given
 * type is a 'real' type, ...  The loop-up-methods of this class are
 * called often, so the informations are cached.  
 */
public class NameAnalysis
{
    protected static String[] classpathEntries;
    protected static Object[] classpathFiles;
    protected static Hashtable cache = new Hashtable();
    protected static Hashtable classpath_lookup = new Hashtable();
    protected static int hit, nohit;
    protected static String separator;

    /**
     * The Constructor reads the classpath variable and creates an
     * array with the contents (files and directories) 
     */
    public NameAnalysis()
    {
        // construct a Stringtokenizer with the classpath as
        // content. Use the platform dependent spath seperator (';' or
        // ':') as delimiter.  No delimiters are returned.

        String classpath = System.getProperty("java.class.path");
        String jassClasspath = System.getProperty("jass.class.path");
        if (jassClasspath != null)
        {
            classpath = classpath + File.pathSeparator + jassClasspath;
        }

        StringTokenizer tokenizer =
            new StringTokenizer(classpath, File.pathSeparator, false);
        classpathEntries = new String[tokenizer.countTokens()];

        // Add all the classpath entries to classpath array
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
        {
            String filename = tokenizer.nextToken();
            classpathEntries[i] = filename;
        }

        /*
         * // convert file seperators to platform dependent file seperators ...
         * for (int j = 0; j < classpathEntries.length; j++)
         * {
         * classpathEntries[j] = classpathEntries[j].replace('/',File.separatorChar);
         * // \\ stands for single backslash
         * classpathEntries[j] = classpathEntries[j].replace('\\',File.separatorChar);
         * }
         */

        // get Files for the classpath entries
        classpathFiles = new Object[classpathEntries.length];
        for (int l = 0; l < classpathEntries.length; l++)
        {
            try
            {
                File f = new File(classpathEntries[l]);
                // if file is an archive, construct new ZipFile ...
                if (f.isFile()
                    && (f.getName().endsWith(".zip")
                        || f.getName().endsWith(".jar")))
                {
                    classpathFiles[l] = new ZipFile(f);
                }
                else
                {
                    classpathFiles[l] = new File(classpathEntries[l]);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (GlobalFlags.VERBOSE > 2)
        {
            dumpCP();
        }
    }

    /**
     * Method looks if a given class has a corresponding '.jass'-File
     * in the classpath
     *
     * @param classname the full qualified classname
     * @return true if '.jass'-file exsits; false otherwise 
     */
    public static boolean isJass(String classname)
    {
        try
        {
            SourceFile[] fs = getSourceFilesForClassName(classname);

            for (int i = 0; i < fs.length; i++)
            {
                if (searchClassName(classname, fs[i]))
                {
                    return fs[i].hasJassExtension();
                }
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            System.exit(1);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }

    /**
     * Searches the full path name for a class in the classpath.
     * @param classname the full qualified class name to look up
     * @return a string of the full path if class is present (without
     *         extension!); null else 
     */
    public static SourceFile expandFullPath(String classname)
        throws IOException
    {
        SourceFile[] fs = getSourceFilesForClassName(classname);

        for (int i = 0; i < fs.length; i++)
        {
            try
            {
                if (searchClassName(classname, fs[i]))
                {
                    return fs[i];
                    // fname.substring(0,fname.lastIndexOf('.'));
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return null;
    }

    /**
     * clear the cache
     */
    public static void clearCache()
    {
        cache.clear();
    }

    /**
     * Returns a Class object for the given FQ'ed class name.  This
     * works around a strangeness in Class.forName() which requires
     * that a nested class be referenced with a "$", even though, in
     * code, one should use a "."
     * 
     * @param className the FQ'ed name of the class
     * @return a Class object for the given FQ'ed class name
     * @exception ClassNotFoundException if the class wasn't found
     */
    public static java.lang.Class classForName(String className)
        throws ClassNotFoundException
    {
        do
        {
            try
            {
                java.lang.Class c = java.lang.Class.forName(className);
                return c;
            }
            catch (ClassNotFoundException e)
            {
                StringBuffer classNameBuffer = new StringBuffer(className);
                int dotIndex = className.lastIndexOf('.');
                if (dotIndex != -1)
                    className =
                        classNameBuffer
                            .replace(dotIndex, dotIndex + 1, "$")
                            .toString();
            }
        }
        while (className.indexOf('.') != -1);
        throw new ClassNotFoundException();
    }

    /**
     * Searches the full qualified classname for a classname in a
     * given context.  No duplicate appearence in the classpath is
     * checked!  If the class name is already full qualified, the
     * method implements the identity.
     * @param classname the classname to expand
     * @param c the context in which the expansion should be done
     * @return the full qualified class name; null if expanding is
     *         impossible (compile time error) 
     */
    public static String expandTypeName(String classname, Class c)
    {
        // get informations from the context
        String[] importedTypes = c.getImportedTypes();
        String packageName = c.getPackageName();

        try
        {
            // already looked up earlier ?
            if (cache.containsKey(classname))
            {
                hit++;
                return (String)cache.get(classname);
            }
            else
            {
                nohit++;
            }

            // follow the type expansion of the Java Language
            //   Specification ...  
            // 1.) check intern declaration
            if (lookUpInClassPath(c.getName() + "." + classname))
            {
                cache.put(classname, c.getName() + "." + classname);
                return c.getName() + "." + classname;
            }

            // 1.a) check outer intern declaration
            String outerclazz = c.getName();
            while (outerclazz.lastIndexOf('.') != -1)
            {
                outerclazz =
                    outerclazz.substring(0, outerclazz.lastIndexOf('.'));
                String innerclazz = outerclazz + "." + classname;
                if (lookUpInClassPath(innerclazz))
                {
                    cache.put(classname, innerclazz);
                    return innerclazz;
                }
            }

            // 2.) check single-import-declaration ...
            for (int i = 0; i < importedTypes.length; i++)
            {
                boolean tryAgain;
                String temp = classname;
                do
                {
                    tryAgain = false;
                    if (importedTypes[i].endsWith("." + temp))
                    {
                        // insert in Cache
                        cache.put(classname, importedTypes[i]);
                        return importedTypes[i];
                    }

                    if (temp.lastIndexOf('.') != -1)
                    {
                        temp = temp.substring(0, temp.lastIndexOf('.'));
                        tryAgain = true;
                    }
                }
                while (tryAgain);
            }

            // 3.) check same package ...
            if (packageName != null)
            {
                if (lookUpInClassPath(packageName + "." + classname))
                {
                    // insert in Cache
                    cache.put(classname, packageName + "." + classname);

                    return packageName + "." + classname;
                }
            }
            else
            {
                if (lookUpInClassPath(classname))
                {
                    // insert in Cache
                    cache.put(classname, classname);
                    return classname;
                }
            }

            // 4.) check import on demand ...
            // 4.1.) check java.lang.*
            if (lookUpInClassPath("java.lang." + classname))
            {
                // insert in Cache
                cache.put(classname, "java.lang." + classname);
                return "java.lang." + classname;
            }

            // 4.2.) check the rest of the imports ...
            for (int i = 0; i < importedTypes.length; i++)
            {
                // examine import-on-demand
                if (importedTypes[i].endsWith(".*"))
                {
                    String searchFor =
                        importedTypes[i].substring(
                            0,
                            importedTypes[i].length() - 1)
                            + classname;

                    if (lookUpInClassPath(searchFor))
                    {
                        // insert in Cache
                        cache.put(classname, searchFor);

                        return searchFor;
                    }
                }
            }

            // only as a last resort, if the type name looks like it might be FQ'ed, then just return it, otherwise, return null
            if (classname.indexOf(".") >= 0)
            {
                return classname;
            }
            else
            {
                return null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        
        // we never get here, but fool the compiler
        return null;
    }

    /**
     * dump statistcal information: number of cache look-ups, the rate
     * of hits and the number of fakes.
     */
    private static void printStatistic()
    {
        System.out.println("jass.reflect.NameAnalysis Statistics:");
        System.out.println("Overall lookups    : " + (hit + nohit));
        System.out.println(
            "Namecache hits (%) : " + ((double)hit) / ((double) (hit + nohit)));
    }

    /**
     * Get the outer part of a name. Inner classes are seperated by 
     * "$" from there surrounding class's name. This method will 
     * return the name part of the most 'outer' class, the part until
     * the first "$".
     */
    private static String getOuterName(String s)
    {
        if (s.indexOf("$") < 0)
        {
            return s;
        }
        return s.substring(0, s.indexOf("$"));
    }

    /**
     * Get the inner part of a name. Inner classes are seperated by 
     * "$" from there surrounding class' name. This method will 
     * return the name part of the most 'inner' class, the part behind
     * the last "$".
     */
    private static String getInnerName(String s)
    {
        if (s.indexOf("$") < 0)
        {
            return s;
        }
        return s.substring(s.indexOf("$") + 1);
    }

    /**
     * Treat classnames of the form "Test$Test" as illegal.
     *
     * @param classname the full qualified class name.
     * @return ture, if classname is of the illegal form.
     */
    private static boolean illegalClassname(String classname)
    {
        String help = getOuterName(classname);
        if (help.indexOf(".") > -1)
        {
            help = help.substring(help.lastIndexOf(".") + 1);
        }
        if (classname.indexOf("$") > -1
            && getInnerName(classname).equals(help))
        {
            return true;
        }
        return false;
    }

    /**
     * Searches a class or java file in the classpath.
     *
     * @param classname the full qualified class name to look up 
     * @return true if class is present in classpath
     */
    public static boolean lookUpInClassPath(String classname)
        throws IOException
    {
        // Terrible hack to handle Windows uppercase filenames !

        // possible values:
        // Test
        // TestInner$Test
        // TestInnerInnner$TestInner$Test
        // ...

        if (classpath_lookup.containsKey(classname))
        {
            hit++;
            return classpath_lookup.get(classname) == Boolean.TRUE;
        }
        else
        {
            nohit++;

            // next is to find names like: Test$Test (and reject them!)
            if (illegalClassname(classname))
            {
                classpath_lookup.put(classname, Boolean.FALSE);
                return false;
            }

            // Look for source files that contain the classname.  The while loop handles the possibility that we are looking for a nested class
            String innerclazz = classname;
            while (!innerclazz.equals(""))
            {
                File[] fs = getSourceFilesForClassName(innerclazz);
                for (int i = 0; i < fs.length; i++)
                {
                    // It is a source file
                    try
                    {
                        boolean found = searchClassName(classname, fs[i]);
                        if (found)
                        {
                            classpath_lookup.put(classname, Boolean.TRUE);
                            return true;
                        }
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                if (innerclazz.lastIndexOf('.') != -1)
                    innerclazz =
                        innerclazz.substring(0, innerclazz.lastIndexOf('.'));
                else
                    innerclazz = ""; // Exit condition
            }

            try
            {
                NameAnalysis.classForName(classname);
                classpath_lookup.put(classname.replace('$', '.'), Boolean.TRUE);
                return true;
            }
            catch (ClassNotFoundException cnf)
            {
                classpath_lookup.put(classname, Boolean.FALSE);
                return false;
            }
        }
    }

    /**
     * Is the classname used in the given file?
     *
     * @param name name of the class that want to find
     * @param file the file that should be parsed
     *
     * @return true, if the filename was found.
     *
     * @throws IOException if there were problemes during file access
     * @throws ParseException if there were syntactical errors during
     *                        reading the file
     *
     * @see jass.visitor.ClassNameVisitor
     */
    private static boolean searchClassName(String name, File file)
        throws IOException, ParseException
    {
        //System.out.println("Searching for class name " + name + " in " + file); 
        // create a ClassNameVistor and search the sorce
        // code of the file
        FileInputStream fin = new java.io.FileInputStream(file);

        JassParser.ReInit(fin);

        JassCompilationUnit unit = JassParser.CompilationUnit();
        ClassNameVisitor classVisitor = new ClassNameVisitor();

        // start parsing ...
        unit.jjtAccept(classVisitor, null);

        boolean classNameWasFound = classVisitor.containsClassName(name);

        // System.out.println("Class name " + name + (classNameWasFound? " found" : " not found"));
        return classNameWasFound;
    }

    /**
     * Generate an array of source files, that may contain the
     * class we are looking for.
     *
     * @param name the name of the class, we are looking for
     *
     * @return an array of Files, taht may contain the class
     *
     * @throws IOException
     */
    public static SourceFile[] getSourceFilesForClassName(String name)
        throws IOException
    {
        // convert package name to file name
        name = name.replace('.', File.separatorChar);

        Vector sourceFilesVector = new Vector();

        for (int i = 0; i < classpathFiles.length; i++)
        {

            // file or archive ?
            if (classpathFiles[i] instanceof java.io.File)
            {
                if (((File)classpathFiles[i]).isDirectory())
                {

                    // try to find good looking source file (first jass than java) ....
                    String directoryName =
                        ((File)classpathFiles[i]).getAbsolutePath();

                    // System.out.println("Directory: " + directoryName);
                    SourceFile jassFile =
                        new SourceFile(
                            directoryName,
                            getOuterName(name)
                                + "."
                                + SourceFile.JASS_EXTENSION);
                    String jassFilename = jassFile.getAbsolutePath();

                    // System.out.print("Try to validate " + jassFilename + " ... ");
                    if (FileTools.validatePath(jassFilename))
                    {

                        // System.out.println("sucessfull");
                        sourceFilesVector.addElement(jassFile);
                    }
                    else
                    {

                        // System.out.println("failed");
                        SourceFile javaFile =
                            new SourceFile(
                                directoryName,
                                getOuterName(name)
                                    + "."
                                    + SourceFile.JAVA_EXTENSION);
                        String javaFilename = javaFile.getAbsolutePath();

                        if (FileTools.validatePath(javaFilename))
                        {
                            sourceFilesVector.addElement(javaFile);
                        }
                    }
                }
            }
        }

        // Store vector as an array and return array
        SourceFile[] sourceFilesArray =
            new SourceFile[sourceFilesVector.size()];

        sourceFilesVector.copyInto(sourceFilesArray);

        return sourceFilesArray;
    }

    /**
     * dump the classpath we are searching
     */
    private static void dumpCP()
    {
        System.out.println("Classpath ...");

        for (int i = 0; i < classpathFiles.length; i++)
        {
            Object classpathFile = classpathFiles[i];

            if (classpathFile instanceof File)
            {
                System.out.println(
                    "* " + ((File)classpathFile).getAbsolutePath());
            }
            else if (classpathFile instanceof ZipFile)
            {
                System.out.println(
                    "* " + ((ZipFile)classpathFiles[i]).getName());
            }
            else
            {
                System.out.println(
                    "* <unknown type>: " + classpathFile.toString());
            }
        }
    }

    /**
     * dump classs path entries
     */
    private static void dumpCPEntries()
    {
        System.out.println("Classpath entries ...");

        for (int i = 0; i < classpathEntries.length; ++i)
        {
            System.out.println("* " + classpathEntries[i]);
        }
    }

}

/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/
