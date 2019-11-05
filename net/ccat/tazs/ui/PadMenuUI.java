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

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.PadMenuSprite;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


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
     * @return True if visible, false elsewhere.
     */
    public boolean isShown()
    {
        return mFocused && !mHideUntilNextPress;
    }
    
    /**
     * @return True if focused.
     */
    public boolean isFocused()
    {
        return mFocused;
    }
    
    /**
     * Hides this PadMenu until the User presses again the C button.
     */
    public void hideUntilNextPress()
    {
        mHideUntilNextPress = true;
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
    public void setChoice(int choice, pointer title)
    {
        switch (choice)
        {
        case CHOICE_RIGHT:
            mRightTitle = title;
            mRightIsEnabled = true;
            break ;
        case CHOICE_DOWN:
            mDownTitle = title;
            mDownIsEnabled = true;
            break ;
        case CHOICE_LEFT:
            mLeftTitle = title;
            mLeftIsEnabled = true;
            break ;
        case CHOICE_UP:
            mUpTitle = title;
            mUpIsEnabled = true;
            break ;
        }
    }
    
    /**
     * Enables or disables the target choice.
     * @param choice The choice's identifier.
     * @param enabled True to enable, false to disable.
     */
    public void setEnabledChoice(int choice, boolean enabled)
    {
        switch (choice)
        {
        case CHOICE_RIGHT:
            mRightIsEnabled = enabled;
            break ;
        case CHOICE_DOWN:
            mDownIsEnabled = enabled;
            break ;
        case CHOICE_LEFT:
            mLeftIsEnabled = enabled;
            break ;
        case CHOICE_UP:
            mUpIsEnabled = enabled;
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
        if ((mHideUntilNextPress) || (mRemainingSelectionTicks > 0))
            return CHOICE_NONE;
        return mSelectedChoice;
    }
    
    
    /***** UPDATE *****/
    
    /**
     * Updates this UI.
     * @return True if the focus is on this UI, false elsewhere.
     */
    public boolean update()
    {
        if (mHideUntilNextPress)
        {
            if (!Button.C.isPressed())
            {
                mHideUntilNextPress = false;
                mFocused = false;
            }
            mSelectedChoice = CHOICE_NONE;
        }
        else
        {
            mFocused = Button.C.isPressed();
            if (mFocused)
            {
                if (mRemainingSelectionTicks == 0)
                {
                    mRemainingSelectionTicks = TICKS_UNTIL_SELECTED;
                    if (Button.Right.justPressed())
                        mSelectedChoice = mRightIsEnabled ? CHOICE_RIGHT : CHOICE_NONE;
                    else if (Button.Down.justPressed())
                        mSelectedChoice = mDownIsEnabled ? CHOICE_DOWN : CHOICE_NONE;
                    else if (Button.Left.justPressed())
                        mSelectedChoice = mLeftIsEnabled ? CHOICE_LEFT : CHOICE_NONE;
                    else if (Button.Up.justPressed())
                        mSelectedChoice = mUpIsEnabled ? CHOICE_UP : CHOICE_NONE;
                    else
                    {
                        mRemainingSelectionTicks = 0;
                        mSelectedChoice = CHOICE_NONE;
                    }
                }
                else
                    mRemainingSelectionTicks--;
            }
            else
                mSelectedChoice = CHOICE_NONE;
        }
        return mFocused;
    }
    
    
    /***** RENDERING *****/
    
    /**
     * Renders this UI into the given screen.
     * @param screen The target.
     */
    public void draw(AdvancedHiRes16Color screen)
    {
        if (mFocused && !mHideUntilNextPress)
        {
            screen.fillRectBlended(0, 0, Dimensions.SCREEN_WIDTH, Dimensions.HELPBAR_BOX_MIN_Y - 1,
                                   Colors.PADMENU_OVERLAY, 0,
                                   AdvancedHiRes16Color.PATTERN_75_25_HEX);
            mPadMenuSprite.draw(screen, mX - VideoConstants.PAD_MENU_ORIGIN_X, mY - VideoConstants.PAD_MENU_ORIGIN_X);
            if (mRightTitle != null)
                drawChoice(CHOICE_RIGHT, mRightTitle, mRightIsEnabled, screen);
            if (mDownTitle != null)
                drawChoice(CHOICE_DOWN, mDownTitle, mDownIsEnabled, screen);
            if (mLeftTitle != null)
                drawChoice(CHOICE_LEFT, mLeftTitle, mLeftIsEnabled, screen);
            if (mUpTitle != null)
                drawChoice(CHOICE_UP, mUpTitle, mUpIsEnabled, screen);
        }
    }
    
    
    /***** PRIVATE *****/
    
    private void drawChoice(int choice, pointer title, boolean enabled, AdvancedHiRes16Color screen)
    {
        int horizontalAlignment = UITools.ALIGNMENT_CENTER;
        int verticalAlignment = UITools.ALIGNMENT_CENTER;
        int choiceX = mX;
        int choiceY = mY;
        int borderColor = Colors.PADMENU_BORDER;
        
        if (mSelectedChoice == choice)
            borderColor = Colors.PADMENU_SELECTED_BORDER;
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
        screen.setTextColor(enabled ? Colors.PADMENU_TEXT : Colors.PADMENU_TEXT_DISABLED);
        screen.drawLabelP(title,
                          borderColor, enabled ? Colors.PADMENU_BACKGROUND : Colors.PADMENU_BACKGROUND_DISABLED,
                          LABEL_PADDING,
                          choiceX, choiceY, horizontalAlignment, verticalAlignment);
    }
    
    
    private int mX;
    private int mY;
    private boolean mFocused;
    private boolean mHideUntilNextPress;
    private PadMenuSprite mPadMenuSprite;
    // I miss C++
    private pointer mRightTitle;
    private boolean mRightIsEnabled;
    private pointer mDownTitle;
    private boolean mDownIsEnabled;
    private pointer mLeftTitle;
    private boolean mLeftIsEnabled;
    private pointer mUpTitle;
    private boolean mUpIsEnabled;

    private int mSelectedChoice = CHOICE_NONE;
    private int mRemainingSelectionTicks = 0;

    
    private static final int LABEL_PADDING = 1;
    private static final int LABEL_MARGIN = 1;
    private static final int LABEL_FROM_CENTER = 9 + LABEL_MARGIN;
    private static final int TICKS_UNTIL_SELECTED = 1;
}