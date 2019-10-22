package net.ccat.tazs.resources.sprites.target;


class TargetSprite
    extends TargetRawSprite
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