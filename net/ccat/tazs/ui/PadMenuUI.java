package net.ccat.tazs.ui;

import femto.input.Button;
import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.sprites.PadMenuSprite;
import net.ccat.tazs.resources.VideoConstants;


/**
 * Handles the Pad Menu, which is usable with C and the Pad.
 */
class PadMenuUI
{
    public PadMenuUI()
    {
        mPadMenuSprite = new PadMenuSprite();
        mPadMenuSprite.setStatic(true);
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
    
    
    /***** UPDATE *****/
    
    /**
     * Updates this UI.
     */
    public void update()
    {
        if (Button.C.justPressed())
        {
            mShown = !mShown;
        }
    }
    
    
    /***** RENDERING *****/
    
    /**
     * Renders this UI into the given screen.
     * @param screen The target.
     */
    public void draw(HiRes16Color screen)
    {
        if (mShown)
            mPadMenuSprite.draw(screen, mX - VideoConstants.PAD_MENU_ORIGIN_X, mY - VideoConstants.PAD_MENU_ORIGIN_X);   
    }
    
    
    /***** PRIVATE *****/
    
    private int mX;
    private int mY;
    private boolean mShown;
    private PadMenuSprite mPadMenuSprite;
}