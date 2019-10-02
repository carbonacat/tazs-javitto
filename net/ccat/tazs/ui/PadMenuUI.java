package net.ccat.tazs.ui;

import femto.input.Button;
import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.PadMenuSprite;
import net.ccat.tazs.resources.VideoConstants;


/**
 * Handles the Pad Menu, which is usable with C and the Pad.
 */
class PadMenuUI
{
    /**
     * Available choices.
     */
    public static final int CHOICE_RIGHT = 0;
    public static final int CHOICE_DOWN = 1;
    public static final int CHOICE_LEFT = 2;
    public static final int CHOICE_UP = 3;
    
    
    public PadMenuUI()
    {
        mPadMenuSprite = new PadMenuSprite();
        mPadMenuSprite.setStatic(true);
        mPadMenuSprite.playDefault();
    }
    
    
    /***** CONFIGURATION *****/
    
    /**
     * Sets the position of the Menu's center.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     */
    public void setPosition(int x, int y)
    {
        mX = x;
        mY = y;
    }
    
    /**
     * @param shown The new show state.
     */
    public void setShown(boolean shown)
    {
        mShown = shown;
    }
    
    /**
     * @return True if visible, false elsewhere.
     */
    public boolean isShown()
    {
        return mShown;
    }
    
    
    /***** CHOICES *****/
    
    /**
     * Removes all choices.
     */
    public void clearChoices()
    {
        mRightTitle = null;
        mDownTitle = null;
        mLeftTitle = null;
        mUpTitle = null;
    }
    
    /**
     * Enables and sets the target choice's title.
     * @param choice The choice's identifier.
     * @param title The text to show.
     */
    public void setChoice(int choice, String title)
    {
        switch (choice)
        {
        case CHOICE_RIGHT:
            mRightTitle = title;
            break ;
        case CHOICE_DOWN:
            mDownTitle = title;
            break ;
        case CHOICE_LEFT:
            mLeftTitle = title;
            break ;
        case CHOICE_UP:
            mUpTitle = title;
            break ;
        }
    }
    
    /**
     * Enables and sets the target choice's title.
     * @param choice The choice's identifier.
     * @param title The text to show.
     */
    public void unsetChoice(int choice)
    {
        setChoice(choice, null);
    }
    
    
    /***** UPDATE *****/
    
    /**
     * Updates this UI.
     */
    public void update()
    {
        if (Button.C.justPressed())
            mShown = !mShown;
    }
    
    
    /***** RENDERING *****/
    
    /**
     * Renders this UI into the given screen.
     * @param screen The target.
     */
    public void draw(HiRes16Color screen)
    {
        if (mShown)
        {
            UITools.fillRectBlended(0, 0, screen.width(), screen.height(),
                                    Colors.PADMENU_OVERLAY_COLOR, 0,
                                    UITools.PATTERN_25_75_HEX,
                                    screen);
            mPadMenuSprite.draw(screen, mX - VideoConstants.PAD_MENU_ORIGIN_X, mY - VideoConstants.PAD_MENU_ORIGIN_X);
            if (mRightTitle != null)
                drawChoice(CHOICE_RIGHT, mRightTitle, screen);
            if (mDownTitle != null)
                drawChoice(CHOICE_DOWN, mDownTitle, screen);
            if (mLeftTitle != null)
                drawChoice(CHOICE_LEFT, mLeftTitle, screen);
            if (mUpTitle != null)
                drawChoice(CHOICE_UP, mUpTitle, screen);
        }
    }
    
    
    /***** PRIVATE *****/
    
    private void drawChoice(int choice, String title, HiRes16Color screen)
    {
        int horizontalAlignment = UITools.ALIGNMENT_CENTER;
        int verticalAlignment = UITools.ALIGNMENT_CENTER;
        int choiceX = mX;
        int choiceY = mY;
        
        switch (choice)
        {
        case CHOICE_RIGHT:
            horizontalAlignment = UITools.ALIGNMENT_START;
            choiceX += CHOICE_FROM_CENTER;
            break ;
        case CHOICE_DOWN:
            verticalAlignment = UITools.ALIGNMENT_START;
            choiceY += CHOICE_FROM_CENTER;
            break ;
        case CHOICE_LEFT:
            horizontalAlignment = UITools.ALIGNMENT_END;
            choiceX -= CHOICE_FROM_CENTER;
            break ;
        case CHOICE_UP:
            verticalAlignment = UITools.ALIGNMENT_END;
            choiceY -= CHOICE_FROM_CENTER;
            break ;
        }
        UITools.drawLabel(title,
                          Colors.PADMENU_BORDER, Colors.PADMENU_BACKGROUND,
                          CHOICE_PADDING,
                          choiceX, choiceY, horizontalAlignment, verticalAlignment,
                          screen);
    }
    
    private int mX;
    private int mY;
    private boolean mShown;
    private PadMenuSprite mPadMenuSprite;
    // I miss C++
    private String mRightTitle;
    private String mDownTitle;
    private String mLeftTitle;
    private String mUpTitle;
    
    
    private static final int CHOICE_PADDING = 1;
    private static final int CHOICE_MARGIN = 1;
    private static final int CHOICE_FROM_CENTER = 9 + CHOICE_MARGIN;
}