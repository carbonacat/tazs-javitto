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