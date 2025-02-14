The *.framework directories are best copied to ~/Library/Frameworks (C:\Documents and Settings\<user name>\Library\Frameworks) but you can place them anywhere if you want to do some more work.  At runtime the *.framework/Resources/Java/*.jar needs to be on the classpath.

GVCAntTasks contains prebuilt jars containing some Ant tasks we use in our build process.  They can either go in the Ant install, or you can provide a classpath to them in your build.xml file.  Each contains a taskdef.properties file defining the tasks.  See the build.xml files from the source distribution for example usage.
