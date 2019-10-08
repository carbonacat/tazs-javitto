package net.ccat.tazs.resources.sprites.brawler;


class BrawlerBodyBSprite
    extends BrawlerBodyBRawSprite
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