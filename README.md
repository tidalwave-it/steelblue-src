![Maven Central](https://img.shields.io/maven-central/v/it.tidalwave.steelblue/steelblue.svg)
[![Build Status](https://img.shields.io/jenkins/s/http/services.tidalwave.it/ci/job/SteelBlue_Build_from_Scratch.svg)](http://services.tidalwave.it/ci/view/SteelBlue)
[![Test Status](https://img.shields.io/jenkins/t/http/services.tidalwave.it/ci/job/SteelBlue.svg)](http://services.tidalwave.it/ci/view/SteelBlue)
[![Coverage](https://img.shields.io/jenkins/coverage/jacoco?jobUrl=https%3A%2F%2Fservices.tidalwave.it%2Fci%2Fview%2FSteelBlue%2Fjob%2FSteelBlue%2F)](http://services.tidalwave.it/ci/view/SteelBlue)
SteelBlue
================================
SteelBlue contains a set of UI abstractions based on DCI roles and JavaFX bindings. It allows to create rich client applications in a way
that is mostly independent of the UI technology; so testing is easier, and it is possible to create applications with multiple user interfaces.
SteelBlue requires and is tested with JDKs in the range [21, 22) and targets JDK 11.
It is released under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).
Please have a look at the [project website](https://tidalwave.bitbucket.io/steelblue) for a quick introduction with samples, tutorials, JavaDocs and build reports.
Bootstrapping
-------------
In order to build the project, run from the command line:
```shell
mkdir steelblue
cd steelblue
git clone https://bitbucket.org/tidalwave/steelblue-src .
mvn -DskipTests
```
The project can be opened with a recent version of the [IntelliJ IDEA](https://www.jetbrains.com/idea/),
[Apache NetBeans](https://netbeans.apache.org/) or [Eclipse](https://www.eclipse.org/ide/) IDEs.
Contributing
------------
Pull requests are accepted via [Bitbucket](https://bitbucket.org/tidalwave/steelblue-src) or [GitHub](https://github.com/tidalwave-it/steelblue-src). There are some guidelines which will make
applying pull requests easier:
* No tabs: please use spaces for indentation.
* Respect the code style.
* Create minimal diffs — disable 'on save' actions like 'reformat source code' or 'organize imports' (unless you use the IDEA specific configuration for
  this project).
* Provide [TestNG](https://testng.org/doc/) tests for your changes and make sure your changes don't break any existing tests by running
```mvn clean test```. You can check whether there are currently broken tests at the [Continuous Integration](http://services.tidalwave.it/ci/view/SteelBlue) page.
If you plan to contribute on a regular basis, please consider filing a contributor license agreement. Contact us for
 more information.
Additional Resources
--------------------
* [Issue tracking](http://services.tidalwave.it/jira/browse/STB)
* [Continuous Integration](http://services.tidalwave.it/ci/view/SteelBlue)
* [Tidalwave Homepage](http://tidalwave.it)
