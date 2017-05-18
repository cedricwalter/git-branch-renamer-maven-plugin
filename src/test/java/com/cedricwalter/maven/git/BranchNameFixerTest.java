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

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BranchNameFixerTest {

    @Test
    public void withBranchName_fixer_expectSnapshot () throws IOException {
        // Arrange
        boolean release = false;
        boolean forceNumericalVersion = false;
        boolean toUpperCase = false;
        boolean toLowerCase = false;
        boolean filterOutBranchQualifier = true;

        BranchNameFixer branchNameFixer = new BranchNameFixer(release, forceNumericalVersion, toUpperCase, toLowerCase, filterOutBranchQualifier);

        String branchName = "feature/ID-AddNewLogin";

        // Act
        String newVersion = branchNameFixer.fixer(branchName);

        // Assert
        assertEquals("ID-AddNewLogin-SNAPSHOT", newVersion);
    }



    @Test
    public void withBranchNameAndRelease_fixer_expectRelease () throws IOException {
        // Arrange
        boolean release = true;
        boolean forceNumericalVersion = false;
        boolean toUpperCase = false;
        boolean toLowerCase = false;
        boolean filterOutBranchQualifier = true;
        BranchNameFixer branchNameFixer = new BranchNameFixer(release, forceNumericalVersion, toUpperCase, toLowerCase, filterOutBranchQualifier);

        String branchName = "feature/ID-AddNewLogin";

        // Act
        String newVersion = branchNameFixer.fixer(branchName);

        // Assert
        assertEquals("ID-AddNewLogin", newVersion);
    }

    @Test
    public void withBranchNameAndReleaseAndNumerical_fixer_expectRelease () throws IOException {
        // Arrange
        boolean release = true;
        boolean forceNumericalVersion = true;
        boolean toUpperCase = false;
        boolean toLowerCase = false;
        boolean filterOutBranchQualifier = true;
        BranchNameFixer branchNameFixer = new BranchNameFixer(release, forceNumericalVersion, toUpperCase, toLowerCase, filterOutBranchQualifier);

        String branchName = "feature/ID-AddNewLogin";

        // Act
        String newVersion = branchNameFixer.fixer(branchName);

        // Assert
        assertEquals("0-ID-AddNewLogin", newVersion);
    }

    @Test
    public void withBranchNameAllLowerCase_fixer_expectSnapshot () throws IOException {
        // Arrange
        boolean release = false;
        boolean forceNumericalVersion = false;
        boolean toUpperCase = false;
        boolean toLowerCase = true;
        boolean filterOutBranchQualifier = true;

        BranchNameFixer branchNameFixer = new BranchNameFixer(release, forceNumericalVersion, toUpperCase, toLowerCase, filterOutBranchQualifier);

        String branchName = "feature/ID-AddNewLogin";

        // Act
        String newVersion = branchNameFixer.fixer(branchName);

        // Assert
        assertEquals("id-addnewlogin-snapshot", newVersion);
    }

    @Test
    public void withBranchNameAllUpperCase_fixer_expectSnapshot () throws IOException {
        // Arrange
        boolean release = false;
        boolean forceNumericalVersion = false;
        boolean toUpperCase = true;
        boolean toLowerCase = false;
        boolean filterOutBranchQualifier = true;

        BranchNameFixer branchNameFixer = new BranchNameFixer(release, forceNumericalVersion, toUpperCase, toLowerCase, filterOutBranchQualifier);

        String branchName = "feature/ID-AddNewLogin";

        // Act
        String newVersion = branchNameFixer.fixer(branchName);

        // Assert
        assertEquals("ID-ADDNEWLOGIN-SNAPSHOT", newVersion);
    }

    @Test
    public void withBranchNameAllUpperCaseFilterOut_fixer_expectSnapshot () throws IOException {
        // Arrange
        boolean release = false;
        boolean forceNumericalVersion = false;
        boolean toUpperCase = true;
        boolean toLowerCase = false;
        boolean filterOutBranchQualifier = false;

        BranchNameFixer branchNameFixer = new BranchNameFixer(release, forceNumericalVersion, toUpperCase, toLowerCase, filterOutBranchQualifier);

        String branchName = "feature/ID-AddNewLogin";

        // Act
        String newVersion = branchNameFixer.fixer(branchName);

        // Assert
        assertEquals("FEATURE-ID-ADDNEWLOGIN-SNAPSHOT", newVersion);
    }
}