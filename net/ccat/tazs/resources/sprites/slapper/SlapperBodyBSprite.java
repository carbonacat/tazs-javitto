package net.ccat.tazs.resources.sprites.slapper;


class SlapperBodyBSprite
    extends SlapperBodyBRawSprite
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