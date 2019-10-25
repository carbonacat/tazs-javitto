package net.ccat.tazs.ui;

import femto.input.Button;
import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.VideoConstants;


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
     * 1000
     * 0000
     * 0000
     * 0000
     */
    public static final int PATTERN_1_16 = 0x8000;
    /**
     * 1010
     * 0101
     * 1010
     * 0101
     */
    public static final int PATTERN_50_50 = 0x5A5A;
    /**
     * 0101
     * 1111
     * 0101
     * 1111
     */
    public static final int PATTERN_25_75_GRID = 0x5F5F;
    /**
     * 0111
     * 1101
     * 0111
     * 1101
     */
    public static final int PATTERN_25_75_HEX = 0x7D7D;
    
    /**
     * 1010
     * 0000
     * 1010
     * 0000
     */
    public static final int PATTERN_75_25_GRID = 0xA0A0;
    /**
     * 1000
     * 0010
     * 1000
     * 0010
     */
    public static final int PATTERN_75_25_HEX = 0x8282;
    
    /**
     * Fills the given rect with a 50%-50% blend of the two colors.
     * Any color that is 0 is going to be transparent instead.
     * @param x The X coordinate of the top-left point.
     * @param y The Y coordinate of the top-left point.
     * @param width The rect's width.
     * @param height The rect's height.
     * @param offColor The color for a point that is OFF in the pattern.
     * @param onColor The color for a point that is ON in the pattern.
     * @param pattern4x4 The pattern for the blending. The 16 lowest bits are used as a 4x4 grid to determine which color to use.
     * @param screen Where to fill the rect.
     */
    public static void fillRectBlended(int x, int y,
                                       int width, int height,
                                       int offColor, int onColor,
                                       int pattern4x4,
                                       HiRes16Color screen)
    {
        int xEnd = x + width;
        int yEnd = y + height;
        
        // TODO: This is optimizable. [017]
        if (offColor != 0)
            for (int pY = y; pY < yEnd; pY++)
                for (int pX = x; pX < xEnd; pX++)
                {
                    int bitIndex = (pX & 0x3) + ((pY & 0x3) << 2);
                    int bitMask = 1 << bitIndex;
                    
                    if ((pattern4x4 & bitMask) == 0)
                        screen.setPixel(pX, pY, onColor);
                }
        if (onColor != 0)
            for (int pY = y; pY < yEnd; pY++)
                for (int pX = x; pX < xEnd; pX++)
                {
                    int bitIndex = (pX & 0x3) + ((pY & 0x3) << 2);
                    int bitMask = 1 << bitIndex;
                    
                    if ((pattern4x4 & bitMask) == bitMask)
                        screen.setPixel(pX, pY, offColor);
                }
    }
    
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
    
    /**
     * Renders a simple window.
     * @param x 
     * @param y
     * @param width 
     * @param height
     * @param screen The target screen.
     */
    public static void drawWindow(int x, int y, int width, int height,
                                  HiRes16Color screen)
    {
        screen.drawRect(x, y, width - 1, height - 1, Colors.WINDOW_BORDER);
        screen.fillRect(x + 1, y + 1, width - 2, height - 2, Colors.WINDOW_BACKGROUND);
    }
    
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
    
    /**
     * @return true or false depending on the blinking value.
     */
    public static boolean blinkingValue()
    {
        return (System.currentTimeMillis() & BLINK_MASK) == BLINK_MASK;
    }
    
    /**
     * Prints a Bean into the screen, at the screen's text position.
     * Takes care of going back to the line if exceeding the boundaries and advancing the position, like printing regular characters would do.
     * @param everyUISprite
     * @param screen
     */
    public static void printBean(NonAnimatedSprite everyUISprite, HiRes16Color screen)
    {
        if (screen.textX + VideoConstants.BEAN_WIDTH >= screen.textRightLimit)
        {
            screen.textX = screen.textLeftLimit;
            screen.textY += screen.textHeight();
        }
        everyUISprite.selectFrame(VideoConstants.EVERYUI_BEAN_NORMAL_FRAME);
        everyUISprite.setPosition(screen.textX + 3 - VideoConstants.EVERYUI_ORIGIN_X, screen.textY + 2 - VideoConstants.EVERYUI_ORIGIN_X);
        everyUISprite.draw(screen);
        screen.textX += VideoConstants.BEAN_WIDTH + screen.charSpacing;
    }
    
    /**
     * Removes a Bean from the screen's text position.
     * @param screen
     */
    public static void anticipateBean(HiRes16Color screen)
    {
        screen.textX -= VideoConstants.BEAN_WIDTH + screen.charSpacing;
    }
    
    
    /***** PRIVATE *****/
    
    
    private static final int BLINK_MASK = 0x80;
}