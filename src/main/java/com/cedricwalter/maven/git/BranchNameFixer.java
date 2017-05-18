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


public class BranchNameFixer {

    private boolean release;
    private boolean toUpperCase;
    private boolean toLowerCase;

    private boolean forceNumericalVersion;
    private boolean filterOutBranchQualifier;

    public BranchNameFixer(boolean release, boolean forceNumericalVersion, boolean toUpperCase, boolean toLowerCase, boolean filterOutBranchQualifier) {
        this.release = release;
        this.forceNumericalVersion = forceNumericalVersion;
        this.toUpperCase = toUpperCase;
        this.toLowerCase = toLowerCase;
        this.filterOutBranchQualifier = filterOutBranchQualifier;
    }

    public String fixer(String branchName) {
        String newBranchName = branchName;

        if (!release) {
            newBranchName += "-SNAPSHOT";
        }

        if (filterOutBranchQualifier) {
            // support for common directories
            newBranchName = newBranchName.replace("bugfix/", "");
            newBranchName = newBranchName.replace("feature/", "");
            newBranchName = newBranchName.replace("hotfix/", "");
            newBranchName = newBranchName.replace("release/", "");
        } else {
            newBranchName = newBranchName.replace("/", "-");
        }

        // if some module are creating osgi felix bundle it has to be numerical
        if (forceNumericalVersion) {
            if (!Character.isDigit(newBranchName.charAt(0)))  {
                newBranchName = "0-" + newBranchName;
            }
        }

        if (toUpperCase) {
            newBranchName = newBranchName.toUpperCase();
        }
        if (toLowerCase) {
            newBranchName = newBranchName.toLowerCase();
        }

        return newBranchName;
    }

}
