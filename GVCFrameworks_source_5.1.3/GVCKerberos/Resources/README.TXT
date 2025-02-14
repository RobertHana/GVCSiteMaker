In order to compile this framework you will need to define a classpath variable 
"JCE" and point it to the jce.jar file in the JDK used to compile.

The jsci*.jar files included with this framework are fakes.  They are empty
except for a few classes containing method signatures only. Their only purpose
is to allow this framework to compile, they are wholly functionless.  In order
to use this framework for anythign beyond supporting a compile, you will need to
obtain real versions of these libraries and purchase a license.  The WedgeTail
libraries are availabe from Vintela at 
http://www.vintela.com/products/vsj/java_kerberos.php
A 60 day evaluation version is available.

Once these libraries are in place, you will then need to edit 
net.global_village.kerberos.tests.WedgeTailTest.java and
net.global_village.kerberos.KerberosAuthenticatorTest.java
to contain information for your server and test account and recompile
before the tests can be run.