/**
 * Copyright (c) 2017-2017 by Cédric Walter - www.cedricwalter.com
 *
 * TVProgram is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TVProgram is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cedricwalter.maven.git;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "pom", threadSafe = true)
public class GitMojo extends AbstractMojo {

    private Log log = getLog();

    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;


    @Parameter(defaultValue = "false")
    private boolean release;

    @Parameter(defaultValue = "false")
    private boolean forceNumericalVersion;

    @Parameter(defaultValue = "false")
    private boolean toUpperCase;

    @Parameter(defaultValue = "false")
    private boolean toLowerCase;

    @Parameter(defaultValue = "true")
    private boolean filterOutBranchQualifier;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            BranchNameFixer branchNameFixer = new BranchNameFixer(release, forceNumericalVersion, toUpperCase, toLowerCase, filterOutBranchQualifier);
            log.info("branch  " + getBranchName());
            String newVersion = branchNameFixer.fixer(getBranchName());

            executeMojo(
                    plugin(
                            groupId("org.codehaus.mojo"),
                            artifactId("versions-maven-plugin"),
                            version("2.3")
                    ),
                    goal("set"),
                    configuration(
                            element(name("generateBackupPoms"), "false"),
                            element(name("newVersion"), newVersion)
                    ),
                    executionEnvironment(
                            mavenProject,
                            mavenSession,
                            pluginManager
                    )
            );

            log.info("All pom changed to " + newVersion);
        } catch (Exception e) {
            //rethrow. this is a deliberate failure
            throw new MojoExecutionException(e.getMessage());
        }
    }

    public String getBranchName() throws IOException {
        File directory = new File(".");

        Repository repository =
                RepositoryCache.open(
                        RepositoryCache.FileKey.lenient(directory, FS.DETECTED),
                        true);

        return repository.getBranch();
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public void setLog(Log log) {
        this.log = log;
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public MavenSession getMavenSession() {
        return mavenSession;
    }

    public void setMavenSession(MavenSession mavenSession) {
        this.mavenSession = mavenSession;
    }

    public BuildPluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(BuildPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public boolean isRelease() {
        return release;
    }

    public void setRelease(boolean release) {
        this.release = release;
    }

    public boolean isForceNumericalVersion() {
        return forceNumericalVersion;
    }

    public void setForceNumericalVersion(boolean forceNumericalVersion) {
        this.forceNumericalVersion = forceNumericalVersion;
    }

    public boolean isToUpperCase() {
        return toUpperCase;
    }

    public void setToUpperCase(boolean toUpperCase) {
        this.toUpperCase = toUpperCase;
    }

    public boolean isToLowerCase() {
        return toLowerCase;
    }

    public void setToLowerCase(boolean toLowerCase) {
        this.toLowerCase = toLowerCase;
    }

    public boolean isFilterOutBranchQualifier() {
        return filterOutBranchQualifier;
    }

    public void setFilterOutBranchQualifier(boolean filterOutBranchQualifier) {
        this.filterOutBranchQualifier = filterOutBranchQualifier;
    }


}
