//
// Copyright (C) 2019 Carbonacat
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package net.ccat.tazs.save;


/**
 * Possible values for a SaveCookie's check.
 */
public class SaveStatus
{
    // The cookie is totally empty. Indicates the first launch.
    public static final int EMPTY = 0;
    // The cookie is OK and ready to be used.
    public static final int OK = 1;
    // The cookie is corrupted (wrong sum check, wrong magic value).
    public static final int CORRUPTED = -1;
    // The cookie doesn't have the right version.
    public static final int VERSION_MISMATCH = -2;
}