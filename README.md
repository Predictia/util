util
====

General purpose library with helper classes for working with tabular data, dates, and dealing with unreliable resources: controlled timeout and retry support. Check the API for more details:

http://predictia.github.io/util/apidocs/index.html

Import as maven dependency
--------------------------

You will need to add to your project's pom.xml file the repository:

    <repositories>
       <repository>
          <id>predictia-public-releases</id>
          <url>https://raw.github.com/Predictia/maven-repo/master/releases</url>
       </repository>
    </repositories>


And the dependency itself

    <dependencies>
      <dependency>
        <groupId>es.predictia</groupId>
        <artifactId>util</artifactId>
        <version>0.0.67</version>
      </dependency>
    </dependencies>
