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
    public static final int CHOICE_NONE = -1;
    
    
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
    
    /**
     * @return the selected choice by the user, after they pressed the corresponding direction long enough.
     */
    public int selectedChoice()
    {
        if (mRemainingSelectionTicks == 0)
            return mSelectedChoice;
        return CHOICE_NONE;
    }
    
    
    /***** UPDATE *****/
    
    /**
     * Updates this UI.
     */
    public void update()
    {
        if (Button.C.justPressed())
            mShown = !mShown;
        if (mShown)
        {
            switch (mSelectedChoice)
            {
            case CHOICE_RIGHT:
                checkPress(Button.Right);
                break;
            case CHOICE_DOWN:
                checkPress(Button.Down);
                break;
            case CHOICE_LEFT:
                checkPress(Button.Left);
                break;
            case CHOICE_UP:
                checkPress(Button.Up);
                break;
            default:
            case CHOICE_NONE:
                mRemainingSelectionTicks = TICKS_UNTIL_SELECTED;
                if (Button.Right.justPressed())
                    mSelectedChoice = CHOICE_RIGHT;
                else if (Button.Down.justPressed())
                    mSelectedChoice = CHOICE_DOWN;
                else if (Button.Left.justPressed())
                    mSelectedChoice = CHOICE_LEFT;
                else if (Button.Up.justPressed())
                    mSelectedChoice = CHOICE_UP;
            }
        }
        else
            mSelectedChoice = CHOICE_NONE;
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
        int borderColor = Colors.PADMENU_BORDER;
        
        if (mSelectedChoice == choice)
        {
            borderColor = (mRemainingSelectionTicks < TICKS_UNTIL_ALMOST_SELECTED) ? Colors.PADMENU_SELECTED_BORDER : Colors.PADMENU_SELECTING_BORDER;
        }
        switch (choice)
        {
        case CHOICE_RIGHT:
            horizontalAlignment = UITools.ALIGNMENT_START;
            choiceX += LABEL_FROM_CENTER;
            break ;
        case CHOICE_DOWN:
            verticalAlignment = UITools.ALIGNMENT_START;
            choiceY += LABEL_FROM_CENTER;
            break ;
        case CHOICE_LEFT:
            horizontalAlignment = UITools.ALIGNMENT_END;
            choiceX -= LABEL_FROM_CENTER;
            break ;
        case CHOICE_UP:
            verticalAlignment = UITools.ALIGNMENT_END;
            choiceY -= LABEL_FROM_CENTER;
            break ;
        }
        UITools.drawLabel(title,
                          borderColor, Colors.PADMENU_BACKGROUND,
                          LABEL_PADDING,
                          choiceX, choiceY, horizontalAlignment, verticalAlignment,
                          screen);
    }
    
    private void checkPress(Button button)
    {
        if (button.isPressed())
            mRemainingSelectionTicks = Math.max(0, mRemainingSelectionTicks - 1);
        else
            mSelectedChoice = CHOICE_NONE;
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

    private int mSelectedChoice = CHOICE_NONE;
    private int mRemainingSelectionTicks;

    
    private static final int LABEL_PADDING = 1;
    private static final int LABEL_MARGIN = 1;
    private static final int LABEL_FROM_CENTER = 9 + LABEL_MARGIN;
    // TODO: This is because the overlay is slow to do.
    private static final int TICKS_UNTIL_SELECTED = 30;
    private static final int TICKS_UNTIL_ALMOST_SELECTED = 15;
}