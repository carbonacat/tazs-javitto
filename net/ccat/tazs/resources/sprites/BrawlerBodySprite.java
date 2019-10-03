package net.ccat.tazs.resources.sprites;

import femto.mode.HiRes16Color;


/**
 * Provides the base methods for manipulating a BrawlerBody Sprite.
 */
interface BrawlerBodySprite
{
    void setPosition(float x, float y);
    void setFlipped(boolean flipped);
    void setMirrored(boolean mirrored);
    void draw(HiRes16Color screen);
}