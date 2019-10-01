package net.ccat.tazs.states;

import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.handlers.BrawlerIdleHandler;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Battle Preparation Phase, where the Player sets up their armies.
 */
class BattlePreparationState
    extends State
{
    public BattlePreparationState(TAZSGame game)
    {
        mGame = game;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        mGame.unitsSystem.clear();
        
        boolean canAddUnit = true;
        
        while (canAddUnit)
        {
            canAddUnit = mGame.unitsSystem.addUnit((Math.random() - 1.f) * 100, (Math.random() - 0.5f) * 80,
                                                   0,
                                                   BrawlerIdleHandler.alliedInstance) != battle.UnitsSystem.IDENTIFIER_NONE;
            canAddUnit = mGame.unitsSystem.addUnit((Math.random() - 0.0f) * 100, (Math.random() - 0.5f) * 80,
                                                   Math.PI,
                                                   BrawlerIdleHandler.ennemyInstance) != battle.UnitsSystem.IDENTIFIER_NONE;
        }
        mGame.screen.cameraX = -mGame.screen.width() * 0.5f;
        mGame.screen.cameraY = -mGame.screen.height() * 0.5f;
        mGame.cursorSprite.setPosition(0, 0);
        mGame.cursorSprite.playInvalid();
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        updateCursor();
        mGame.unitsSystem.onTick();
        
        screen.clear(Colors.SCENE_BG_COLOR);
        mGame.unitsSystem.draw(screen);
        mGame.cursorSprite.draw(screen);
        screen.flush();
    }
    
    public void shutdown()
    {
        mGame = null;
    }
 
 
    /***** PRIVATE *****/
    
    private void updateCursor()
    {
        float cursorX = mGame.cursorSprite.x;
        float cursorY = mGame.cursorSprite.y;

        if (Button.Up.isPressed())
            cursorY -= CURSOR_PIXELS_PER_TICK;
        if (Button.Down.isPressed())
            cursorY += CURSOR_PIXELS_PER_TICK;
        if (Button.Left.isPressed())
            cursorX -= CURSOR_PIXELS_PER_TICK;
        if (Button.Right.isPressed())
            cursorX += CURSOR_PIXELS_PER_TICK;
        mGame.cursorSprite.x = cursorX;
        mGame.cursorSprite.y = cursorY;
    }
    
    private TAZSGame mGame;
    
    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
}