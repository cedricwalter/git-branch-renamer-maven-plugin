
# Introduction
When working with many feature/release/bugfix/hotfix branches, it is a bad idea to start changing the pom version as this
will create merge conflicts when using pull request 

Read more here 
* [Update Maven pom version on GIT checkout in TeamCity](https://www.waltercedric.com/index.php?option=com_content&view=article&id=2206:update-maven-pom-version-on-git-checkout-in-teamcity&catid=129&Itemid=332)
* [maven-release-plugin with GIT](ttps://waltercedric.com/index.php?option=com_content&view=article&id=2212:maven-release-plugin-with-git&catid=356&Itemid=333))

This plugin allow you to keep in ALL branches the same pom version for all your projects:
for example ```MASTER-SNAPSHOT``` and never change it again.

the project version will be derived from branch name automatically when running in your continuous integration server.

branch name ```feature/xxxx```

* ```<version>xxxx-SNAPSHOT</version>```   (default)
* ```<version>xxxx</version>```  (release = true)
* ```<version>0-xxxx-SNAPSHOT</version>```   (forceNumericalVersion = true)
* ```<version>feature-xxxx-SNAPSHOT</version>```   (filterOutBranchQualifier = false)

# Resume
* Deriving Maven artifact version from GIT branch,
* Update pom version automatically,
* Add the ability to use Pull request with any branching workflow model

# Quick Usage
Add to the root pom
```<build>
<plugins>
    <plugin>
        <groupId>com.cedric.walter.maven.git</groupId>
        <artifactId>git-branch-renamer-maven-plugin</artifactId>
        <version>1.0.0</version>
        <inherited>false</inherited> <!-- only run once in root module -->
        <executions>
            <execution>
                <goals>
                    <goal>pom</goal>
                </goals>
                <phase>process-resources</phase>
                <configuration>
                     <!-- default values here for sake of example, all optionnal-->
                     <release>false</release> <!-- you may want to add a profile where it is true or a -D when you want to release your project -->
                     <filterOutBranchQualifier>true</filterOutBranchQualifier>
                     <forceNumericalVersion>false</forceNumericalVersion>
                     <toUpperCase>false</toUpperCase>
                     <toLowerCase>false</toLowerCase>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

# Notes   
  
* You may not want to run this plugin locally, ideally run it only in your CI server using a profile, running it locally will change pom version
* this plugin as default rename version to snapshot, you may want to create another CI build for versioning your project, use a -D like in <release>${create.release}</release>
   
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