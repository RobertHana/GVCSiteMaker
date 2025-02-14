Running the Tests
As a result of what appears to be a bug in WOLips, when a JUnit launcher is created 
it includes both the classpath to the frameworks in the workspaces and also to 
the instaled versions.  NSBundle can't deal with loading the bundles due to this 
duplication.  In order to run the tests, delete the default user classpath and 
create  your own using only the jar file for this project, JUNIT, JASS, and the 
installed or the workspace versions of the frameworks.

Tests require the JavaJDCBPlugin and the FrontBase (or other) plugin.

Failure to do this will result in odd exceptions from AllTestsSuite<init> as it 
tries to load com.webobjects.eoproject.Application.

If you are getting test failures in net.global_village.woextensions.tests.
LocalizedWOComponentTest.testLocalizedKeyPathForBinding(LocalizedWOComponentTest.java:59), 
check to ensure that the primeApplication call is not terminating due to errors 
 in the WOApplication constructor.

Localization
- for information, see the documentation in WOSession
