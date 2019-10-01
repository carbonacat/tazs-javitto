package net.ccat.tazs.states;

import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.handlers.BrawlerIdleHandler;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.resources.VideoConstants;
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
        mGame.screen.cameraX = mGame.sceneXMin;
        mGame.screen.cameraY = mGame.sceneYMin;
        mCursorX = 0;
        mCursorY = 0;
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
        if (Button.Up.isPressed())
            mCursorY = Math.max(mCursorY - CURSOR_PIXELS_PER_TICK, mGame.sceneYMin);
        if (Button.Down.isPressed())
            mCursorY = Math.min(mCursorY + CURSOR_PIXELS_PER_TICK, mGame.sceneYMax);
        if (Button.Left.isPressed())
            mCursorX = Math.max(mCursorX - CURSOR_PIXELS_PER_TICK, mGame.sceneXMin);
        if (Button.Right.isPressed())
            mCursorX = Math.min(mCursorX + CURSOR_PIXELS_PER_TICK, mGame.sceneXMax);
        mGame.cursorSprite.setPosition(mCursorX - VideoConstants.CURSOR_ORIGIN_X, mCursorY - VideoConstants.CURSOR_ORIGIN_Y);
    }
    
    private TAZSGame mGame;
    
    private float mCursorX;
    private float mCursorY;
    
    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
}