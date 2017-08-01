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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;

public class BranchNameDetector {

    public static String getBranchName(String pathname) throws IOException {
        if (inTravis()) {
            return getTravisBranch();
        } else {
            File directory = new File(pathname);

            Repository repository =
                    RepositoryCache.open(
                            RepositoryCache.FileKey.lenient(directory, FS.DETECTED),
                            true);
            return repository.getBranch();
        }
    }

    private static boolean inTravis() {
        // see https://docs.travis-ci.com/user/environment-variables/
        String travis = System.getProperty("TRAVIS");

        return !"".equals(travis) && "true".equals(travis);
    }

    private static String getTravisBranch() {
        // for push builds, or builds not triggered by a pull request, this is the name of the branch.
        // for builds triggered by a pull request this is the name of the branch targeted by the pull request.
        return System.getProperty("TRAVIS_BRANCH");
    }

}
