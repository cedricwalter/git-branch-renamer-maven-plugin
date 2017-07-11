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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BranchNameDetectorTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Test
    public void withTravis_getBranchName_expectSet() throws IOException {
        // Arrange
        String expectedBranchName = "master";
        System.setProperty("TRAVIS", "true");
        System.setProperty("TRAVIS_BRANCH", expectedBranchName);

        // Act
        String branchName = BranchNameDetector.getBranchName("anyPath");

        // Assert
        assertEquals(expectedBranchName, branchName);
    }

    @Test
    public void withGit_getBranchName_expectSet() throws IOException, GitAPIException {
        // Arrange
        File directory = folder.newFolder();
        Git git = Git.init().setDirectory(directory).call();

        String expectedBranchName = "master";

        System.setProperty("TRAVIS", "");

        // Act
        String branchName = BranchNameDetector.getBranchName(directory.getPath());

        // Assert
        assertEquals(expectedBranchName, branchName);
    }

}