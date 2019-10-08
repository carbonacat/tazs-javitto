package net.ccat.tazs.resources.sprites;

import femto.mode.HiRes16Color;


/**
 * Provides the base methods for manipulating a Sprite frame by frame.
 */
interface NonAnimatedSprite
{
    /***** SPRITE *****/
    
    void setPosition(float x, float y);
    void setFlipped(boolean flipped);
    void setMirrored(boolean mirrored);
    void draw(HiRes16Color screen);
    
    
    /***** FRAME MANIPULATION *****/
    
    void selectFrame(int frame);
}