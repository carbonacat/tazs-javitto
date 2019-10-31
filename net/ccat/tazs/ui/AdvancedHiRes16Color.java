package net.ccat.tazs.ui;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.VideoConstants;


/**
 * Provides additional facilities to the already awesome HiRes16Color.
 */
public class AdvancedHiRes16Color
    extends HiRes16Color
{
    public AdvancedHiRes16Color(pointer palette, pointer font)
    {
        super(palette, font);
    }
    
    
    /***** RENDERING *****/
    
    /**
     * 0000
     * 0000
     * 0000
     * 0000
     */
    public static final int PATTERN_100_0 = 0x0000;
    /**
     * 1000
     * 0000
     * 0000
     * 0000
     */
    public static final int PATTERN_93_6 = 0x8000;
    /**
     * 1000
     * 0000
     * 0010
     * 0000
     */
    public static final int PATTERN_87_12 = 0x8000;
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
     * 0111
     * 1111
     * 1101
     * 1111
     */
    public static final int PATTERN_12_87 = 0x7FDF;
    /**
     * 0111
     * 1111
     * 1111
     * 1111
     */
    public static final int PATTERN_6_93 = 0x7FFF;
    /**
     * 1111
     * 1111
     * 1111
     * 1111
     */
    public static final int PATTERN_0_100 = 0xFFFF;
    
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
     */
    public void fillRectBlended(int x, int y,
                                int width, int height,
                                int offColor, int onColor,
                                int pattern4x4)
    {
        int xEnd = x + width;
        int yEnd = y + height;
        
        // TODO: This is optimizable. [017]
        if (onColor != 0)
            for (int pY = y; pY < yEnd; pY++)
                for (int pX = x; pX < xEnd; pX++)
                {
                    int bitIndex = (pX & 0x3) + ((pY & 0x3) << 2);
                    int bitMask = 1 << bitIndex;
                    
                    if ((pattern4x4 & bitMask) == 0)
                        setPixel(pX, pY, onColor);
                }
        if (offColor != 0)
            for (int pY = y; pY < yEnd; pY++)
                for (int pX = x; pX < xEnd; pX++)
                {
                    int bitIndex = (pX & 0x3) + ((pY & 0x3) << 2);
                    int bitMask = 1 << bitIndex;
                    
                    if ((pattern4x4 & bitMask) == bitMask)
                        setPixel(pX, pY, offColor);
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
     */
    public void drawLabelP(pointer title,
                           int borderColor, int backgroundColor,
                           int padding,
                           int x, int y, int horizontalAlignment, int verticalAlignment)
    {
        int titleWidth = pTextWidth(title);
        int choiceWidth = 1 + padding + titleWidth + padding + 1;
        int choiceHeight = 1 + padding + textHeight() - 1 + padding + 1;
        int choiceX = 0;
        int choiceY = 0;
        
        switch (horizontalAlignment)
        {
        case UITools.ALIGNMENT_START:
            choiceX = x;
            break ;
        case UITools.ALIGNMENT_END:
            choiceX = x - choiceWidth + 1;
            break ;
        default:
        case UITools.ALIGNMENT_CENTER:
            choiceX = x - (choiceWidth - 1) / 2;
            break ;
        }
        switch (verticalAlignment)
        {
        case UITools.ALIGNMENT_START:
            choiceY = y;
            break ;
        case UITools.ALIGNMENT_END:
            choiceY = y - choiceHeight + 1;
            break ;
        default:
        case UITools.ALIGNMENT_CENTER:
            choiceY = y - (choiceHeight - 1) / 2;
            break ;
        }
        
        int fillX = choiceX + 1;
        int fillY = choiceY + 1;
        int fillWidth = choiceWidth - 2;
        int fillHeight = choiceHeight - 2;

        if (borderColor != 0)
        {
            drawHLine(fillX, choiceY, fillWidth, borderColor);
            drawHLine(fillX, choiceY + choiceHeight - 1, fillWidth, borderColor);
            drawVLine(choiceX, fillY, fillHeight, borderColor);
            drawVLine(choiceX + choiceWidth - 1, fillY, fillHeight, borderColor);
        }
        if (backgroundColor != 0)
            fillRect(fillX, fillY, fillWidth, fillHeight, backgroundColor);
        setTextPosition(fillX + padding, fillY + padding);
        printPText(title);
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
     */
    public void drawLabel(String title,
                          int borderColor, int backgroundColor,
                          int padding,
                          int x, int y, int horizontalAlignment, int verticalAlignment)
    {
        int titleWidth = textWidth(title);
        int choiceWidth = 1 + padding + titleWidth + padding + 1;
        int choiceHeight = 1 + padding + textHeight() - 1 + padding + 1;
        int choiceX = 0;
        int choiceY = 0;
        
        switch (horizontalAlignment)
        {
        case UITools.ALIGNMENT_START:
            choiceX = x;
            break ;
        case UITools.ALIGNMENT_END:
            choiceX = x - choiceWidth + 1;
            break ;
        default:
        case UITools.ALIGNMENT_CENTER:
            choiceX = x - (choiceWidth - 1) / 2;
            break ;
        }
        switch (verticalAlignment)
        {
        case UITools.ALIGNMENT_START:
            choiceY = y;
            break ;
        case UITools.ALIGNMENT_END:
            choiceY = y - choiceHeight + 1;
            break ;
        default:
        case UITools.ALIGNMENT_CENTER:
            choiceY = y - (choiceHeight - 1) / 2;
            break ;
        }
        
        int fillX = choiceX + 1;
        int fillY = choiceY + 1;
        int fillWidth = choiceWidth - 2;
        int fillHeight = choiceHeight - 2;

        if (borderColor != 0)
        {
            drawHLine(fillX, choiceY, fillWidth, borderColor);
            drawHLine(fillX, choiceY + choiceHeight - 1, fillWidth, borderColor);
            drawVLine(choiceX, fillY, fillHeight, borderColor);
            drawVLine(choiceX + choiceWidth - 1, fillY, fillHeight, borderColor);
        }
        if (backgroundColor != 0)
            fillRect(fillX, fillY, fillWidth, fillHeight, backgroundColor);
        setTextPosition(fillX + padding, fillY + padding);
        print(title);
    }
    
    /**
     * Renders a simple window.
     * @param x 
     * @param y
     * @param width 
     * @param height
     */
    public void drawWindow(int x, int y, int width, int height)
    {
        drawRect(x, y, width - 1, height - 1, Colors.WINDOW_BORDER);
        fillRect(x + 1, y + 1, width - 2, height - 2, Colors.WINDOW_BACKGROUND);
    }
    
    /**
     * Prints a Bean into the screen, at the screen's text position.
     * Takes care of going back to the line if exceeding the boundaries and advancing the position, like printing regular characters would do.
     * @param everyUISprite
     */
    public void printBean(NonAnimatedSprite everyUISprite)
    {
        if (textX + VideoConstants.BEAN_WIDTH >= textRightLimit)
        {
            textX = textLeftLimit;
            textY += textHeight();
        }
        everyUISprite.selectFrame(VideoConstants.EVERYUI_BEAN_NORMAL_FRAME);
        everyUISprite.setPosition(textX + 3 - VideoConstants.EVERYUI_ORIGIN_X, textY + 2 - VideoConstants.EVERYUI_ORIGIN_Y);
        everyUISprite.draw(this);
        textX += VideoConstants.BEAN_WIDTH + charSpacing;
    }
    
    /**
     * Prints the text stored in the given pointer, up to the next 0.
     * @param textPointer
     */
    public void printPText(pointer textPointer)
    {
        int textByte = System.memory.LDRB(textPointer);
        
        while (textByte != 0)
        {
            putchar(textByte);
            textPointer++;
            textByte = System.memory.LDRB(textPointer);
        }
    }
    
    /**
     * Same than textWidth(String), but for null-terminated pointers.
     */
    public int pTextWidth(pointer textPointer)
    {
		if (font == null)
			return 0L;

		int total = 0L;
		uint w = System.memory.LDRB(font);
		uint h = System.memory.LDRB(font + 1L);
		char index = 0;
		
		while ((index = (char)System.memory.LDRB(textPointer)) != 0)
		{
			index -= (char)System.memory.LDRB(font + 2L);
			uint extra = (((h!=8L) && (h!=16L)) ? 1L : 0L);
			uint hbytes = (h>>3L) + extra;
			pointer bitmap = font + 4L + index * (w * hbytes + 1L);
			int numBytes = System.memory.LDRB( bitmap );
			
			total += numBytes + charSpacing;
		    textPointer++;
		}
		return total;
    }
    
    /**
     * Removes a Bean from the screen's text position.
     */
    public void anticipateBean()
    {
        textX -= VideoConstants.BEAN_WIDTH + charSpacing;
    }
}