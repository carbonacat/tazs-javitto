package net.ccat.tazs.ui;

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
    
    
    /***** RENDERING *****/
    
    public void draw(HiRes16Color screen)
    {
        mPadMenuSprite.draw(screen, mX - VideoConstants.PAD_MENU_ORIGIN_X, mY - VideoConstants.PAD_MENU_ORIGIN_X);   
    }
    
    
    /***** PRIVATE *****/
    
    private int mX;
    private int mY;
    private PadMenuSprite mPadMenuSprite;
}