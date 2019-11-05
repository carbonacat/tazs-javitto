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

import femto.input.Button;


/**
 * A collection of tools related to UI.
 */
class UITools
{
    /***** LAYOUT *****/
    
    /**
     * Constants for alignments.
     * START is Left & Top.
     * END is Right & Bottom.
     */
    public static final int ALIGNMENT_START = 0x1;
    public static final int ALIGNMENT_CENTER = 0x0;
    public static final int ALIGNMENT_END = 0x2;
    public static final int ALIGNMENT_MASK = 0x3;
    
    
    /***** MISC *****/
    
    /**
     * @return true or false depending on the blinking value.
     */
    public static boolean blinkingValue()
    {
        return (System.currentTimeMillis() & BLINK_MASK) == BLINK_MASK;
    }
    
    /**
     * Resets the justPressed state of every button.
     */
    public static void resetJustPressed()
    {
        Button.A.justPressed();
        Button.B.justPressed();
        Button.C.justPressed();
        Button.Right.justPressed();
        Button.Down.justPressed();
        Button.Left.justPressed();
        Button.Up.justPressed();
    }
    
    
    /***** PRIVATE *****/
    
    private static final int BLINK_MASK = 0x80;
}