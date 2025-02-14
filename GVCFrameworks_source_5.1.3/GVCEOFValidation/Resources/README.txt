In order to run the tests, do the following:
- create test database (see TestConfiguration.plist in GVCEOFExtensions)
- run DB scripts from GVCTestEOModelBase, GVCTestEOModelSubclass, and this framework
- create a Junit run as normal
- add the .jar from the framework to the User Entries part of the jUnit launcher classpath
- some tests (LocalizationEngineTest in particular) only pass when instrumented for DBC
