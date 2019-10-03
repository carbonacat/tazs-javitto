package net.ccat.tazs.resources.sprites;


class BrawlerBodyASprite
    extends BrawlerBodyARawSprite
    implements BrawlerBodySprite
{
    void updateAnimation()
    {
        // Nothing to do.
    }
    
    
    /***** FRAME MANIPULATION *****/
    
    void selectFrame(int frame)
    {
        currentFrame = frame;
    }
}