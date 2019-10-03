package net.ccat.tazs.resources.sprites;


class BrawlerBodyBSprite
    extends BrawlerBodyBRawSprite
    implements BrawlerBodySprite
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