package net.ccat.tazs.resources.sprites.brawler;


class BrawlerBodyASprite
    extends BrawlerBodyARawSprite
    implements NonAnimatedSprite
{
    /***** SPRITE *****/
    
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