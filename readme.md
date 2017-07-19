[![Build Status](https://travis-ci.org/cedricwalter/git-branch-renamer-maven-plugin.svg?branch=master)](https://travis-ci.org/cedricwalter/git-branch-renamer-maven-plugin)


# Introduction
When working with many feature/release/bugfix/hotfix branches, it is a bad idea to start changing the pom version as this
will create merge conflicts when using pull request 

Read more here 
* [Update Maven pom version on GIT checkout in TeamCity](https://www.waltercedric.com/index.php?option=com_content&view=article&id=2206:update-maven-pom-version-on-git-checkout-in-teamcity&catid=129&Itemid=332)
* [maven-release-plugin with GIT](https://waltercedric.com/index.php?option=com_content&view=article&id=2212:maven-release-plugin-with-git&catid=356&Itemid=333)

This plugin allow you to keep in ALL branches the same pom version for all your projects:
for example ```MASTER-SNAPSHOT``` and never change it again.

the project version will be derived from branch name automagically when running in your continuous integration server.

Example if your branch name is named ```feature/xxxx```

* ```<version>xxxx-SNAPSHOT</version>```   (default)
* ```<version>xxxx</version>```  (release = true)
* ```<version>0-xxxx-SNAPSHOT</version>```   (forceNumericalVersion = true) useful for Apache Felix bundles
* ```<version>feature-xxxx-SNAPSHOT</version>```   (filterOutBranchQualifier = false)

# Bonus
* Add the ability to use Pull request with any branching workflow model
* Remove all non portable build step (bash/shell) in jenkins/teamcity/bamboo/...
* Centralized code in project object model (pom)

# Resume
* This plugin derive Maven artifact version from GIT branch name,
* Update pom version automatically,
* Can set up a Systen variable
* Can write a file containing calculated version
* Support travis

# Caveat
You need to run this code in an own maven step like ```mvn clean```, then your build in ```mvn deploy```.
This is because Maven has read and cache the reactor content with the old version name, 
the plugin properly change version on disk but there is no easy way to reload all projects in code. 
(Pull request welcomed if you find how) 

# Travis
Travis do not checkout the whole git, but only the branch. This plugin can detect Travis environment 
and use the value provided from environment ${TRAVIS_BRANCH}


# Quick Usage
Add to the root pom
```<build>
<plugins>
    <plugin>
        <groupId>com.cedricwalter</groupId>
        <artifactId>git-branch-renamer-maven-plugin</artifactId>
        <version>1.0.0</version>
        <inherited>false</inherited> <!-- only run once in root module -->
        <executions>
            <execution>
                <goals>
                    <goal>pom</goal>
                </goals>
                <phase>pre-clean</phase>
                <configuration>
                     <!-- default values here for sake of example, all are optionnal -->
                     <release>false</release> <!-- you may want to add a profile where it is true or a -D when you want to release your project -->
                     <filterOutBranchQualifier>true</filterOutBranchQualifier>
                     <forceNumericalVersion>false</forceNumericalVersion>
                     <toUpperCase>false</toUpperCase>
                     <toLowerCase>false</toLowerCase>
                     
                     <!-- if true this will set a system property variable, the variable set will be only valid in the current process vm (maven) -->
                     <setVariable>false</setVariable>
                     <!-- name of the system property variable -->
                     <versionFromGitBranch>versionFromGitBranch</versionFromGitBranch>
                     
                     <!-- if true this will create a file in target/version.txt which contains new version number -->
                     <setFile>true</setFile>
                     <fileName>version.txt</fileName>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

# Notes
  
* You may not want to run this plugin locally, ideally run it only in your CI server using a profile, running it locally will change pom version
```
    <profiles>
        <profile>
            <id>rename-pom-version-like-branch</id>
            <activation>
                <os>
                    <family>unix</family>
                </os>
            </activation>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.cedricwalter</groupId>
                        <artifactId>git-branch-renamer-maven-plugin</artifactId>
                        <version>1.0.0</version>
                        <inherited>false</inherited>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>pom</goal>
                                </goals>
                                <phase>pre-clean</phase>
                                <configuration>
                                    <release>false</release>
                                    <forceNumericalVersion>false</forceNumericalVersion>
                                    <toUpperCase>false</toUpperCase>
                                    <toLowerCase>false</toLowerCase>
                                    <filterOutBranchQualifier>true</filterOutBranchQualifier>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
```

* this plugin as default rename version to snapshot, you may want to create another CI build for versioning your project, 
use a -DreleaseNow=true like in <release>${releaseNow}</release>
```
    <profiles>
        <profile>
            <id>rename-pom-version-like-branch</id>
            <activation>
                <os>
                    <family>unix</family>
                </os>
            </activation>
            <properties>
               <releaseNow>false</releaseNow>
            </properties>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.cedricwalter</groupId>
                        <artifactId>git-branch-renamer-maven-plugin</artifactId>
                        <version>1.0.0</version>
                        <inherited>false</inherited>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>pom</goal>
                                </goals>
                                <phase>pre-clean</phase>
                                <configuration>
                                    <release>${releaseNow}</release>
                                    <forceNumericalVersion>false</forceNumericalVersion>
                                    <toUpperCase>false</toUpperCase>
                                    <toLowerCase>false</toLowerCase>
                                    <filterOutBranchQualifier>true</filterOutBranchQualifier>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
```   
   
   
   
# A bit of history
   
In the last 5 years I was doing the same in a build step (shell script) in Jetbrain Teamcity (https://www.jetbrains.com/teamcity/) 
or Atlassian Bamboo (https://www.atlassian.com/software/bamboo). This plugin is portable, stay in pom.

The method below is still valid!
   
```echo 'Change the version in pom.xml files...' 
branch=$(git rev-parse --abbrev-ref HEAD) 

echo "osgi bundle do not accept non numerical version number, so use numerical OSGI qualifier for branch uniqueness" 
version="0-$branch-SNAPSHOT" 

echo "version is ${version}" 

echo "filter out any eventual Branch prefixes" 
# e.g. /bugfix /feature /release 
version="$(echo $version | sed 's/bugfix\///g')" 
version="$(echo $version | sed 's/feature\///g')" 
version="$(echo $version | sed 's/hotfix\///g')" 
version="$(echo $version | sed 's/0-release\///g')" 

/opt/maven/apache-maven-3.3.9/bin/mvn versions:set -DgenerateBackupPoms=false -DnewVersion="$version" 
echo 'Changed version in pom.xml files to ${version}' 
exit 0 ``` 