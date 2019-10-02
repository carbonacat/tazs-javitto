package net.ccat.tazs.ui;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;


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
    
    
    /***** RENDERING *****/
    
    /**
     * Draws a label for a text.
     * @param title The label's title.
     * @param borderColor The color for the border. 0 for transparent.
     * @param backgroundColor The color for under the text. 0 for transparent.
     * @param padding The padding between the border and the text.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param alignment From which point this label will be drawn. (See ORIGIN_* constants).
     * @param screen Where to draw the label.
     */
    public static void drawLabel(String title,
                                 int borderColor, int backgroundColor,
                                 int padding,
                                 int x, int y, int horizontalAlignment, int verticalAlignment,
                                 HiRes16Color screen)
    {
        int titleWidth = screen.textWidth(title);
        int choiceWidth = 1 + padding + titleWidth + padding + 1;
        int choiceHeight = 1 + padding + screen.textHeight() - 1 + padding + 1;
        int choiceX = 0;
        int choiceY = 0;
        
        switch (horizontalAlignment)
        {
        case ALIGNMENT_START:
            choiceX = x;
            break ;
        case ALIGNMENT_END:
            choiceX = x - choiceWidth + 1;
            break ;
        default:
        case ALIGNMENT_CENTER:
            choiceX = x - (choiceWidth - 1) / 2;
            break ;
        }
        switch (verticalAlignment)
        {
        case ALIGNMENT_START:
            choiceY = y;
            break ;
        case ALIGNMENT_END:
            choiceY = y - choiceHeight + 1;
            break ;
        default:
        case ALIGNMENT_CENTER:
            choiceY = y - (choiceHeight - 1) / 2;
            break ;
        }
        
        int fillX = choiceX + 1;
        int fillY = choiceY + 1;
        int fillWidth = choiceWidth - 2;
        int fillHeight = choiceHeight - 2;

        if (borderColor != 0)
        {
            screen.drawHLine(fillX, choiceY, fillWidth, borderColor);
            screen.drawHLine(fillX, choiceY + choiceHeight - 1, fillWidth, borderColor);
            screen.drawVLine(choiceX, fillY, fillHeight, borderColor);
            screen.drawVLine(choiceX + choiceWidth - 1, fillY, fillHeight, borderColor);
        }
        if (backgroundColor != 0)
            screen.fillRect(fillX, fillY, fillWidth, fillHeight, backgroundColor);
        screen.setTextPosition(fillX + padding, fillY + padding);
        screen.print(title);
    }
}