package net.ccat.tazs.resources.sprites.everyui;


class EveryUISprite
    extends EveryUIRawSprite
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