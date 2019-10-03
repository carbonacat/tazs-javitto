package net.ccat.tazs.resources.sprites;

import femto.mode.HiRes16Color;


/**
 * Provides the base methods for manipulating a BrawlerBody Sprite.
 */
interface BrawlerBodySprite
{
    static final int FRAME_IDLE = 0;
    static final int FRAME_DEAD_START = 1;
    static final int FRAME_DEAD_LAST = 6;
    
    /***** SPRITE *****/
    
    void setPosition(float x, float y);
    void setFlipped(boolean flipped);
    void setMirrored(boolean mirrored);
    void draw(HiRes16Color screen);
    
    
    /***** FRAME MANIPULATION *****/
    
    void selectFrame(int frame);
}