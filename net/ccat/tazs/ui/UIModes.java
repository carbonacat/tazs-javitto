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

package net.ccat.tazs.ui;


/**
 * Available UI Modes while within a Battle.
 */
public class UIModes
{
    // Too expensive.
    private static final int TOO_EXPENSIVE = -2;
    // No free Unit remaining.
    private static final int NO_MORE_UNITS = -1;
    // Invalid/Error.
    private static final int INVALID = 0;
    // "Enemy Territory".
    private static final int ENEMY_TERRITORY = 1;
    // Can place a Unit.
    private static final int PLACE = 2;
    // Can remove a Unit.
    private static final int REMOVE = 4;
    // Cannot remove a Unit.
    private static final int CANNOT_REMOVE = 5;
    // In PadMenu.
    private static final int MENU = 6;
    // "No Man's Land.""
    private static final int NOMANSLAND = 7;
}