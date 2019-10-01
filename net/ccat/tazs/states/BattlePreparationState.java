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
        
        // TODO: Eventually will be setup with a proper battle plan.
        for (int i = 0; i < 10; i++)
            mGame.unitsSystem.addUnit((Math.random() - 0.0f) * 100, (Math.random() - 0.5f) * 80,
                                      Math.PI,
                                      BrawlerIdleHandler.ennemyInstance) != battle.UnitsSystem.IDENTIFIER_NONE;
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
            
        int newCursorMode;
            
        if (mCursorX < 0)
        {
            if (Button.A.justPressed())
                mGame.unitsSystem.addUnit(mCursorX, mCursorY, 0, BrawlerIdleHandler.alliedInstance);
            if (mGame.unitsSystem.freeUnits() > 0)
                newCursorMode = CURSOR_MODE_PLACEABLE;
            else
                newCursorMode = CURSOR_MODE_INVALID;
        }
        else
            newCursorMode = CURSOR_MODE_INVALID;
        if (newCursorMode != mCursorMode)
        {
            mCursorMode = newCursorMode;
            switch (mCursorMode)
            {
            case CURSOR_MODE_INVALID:
                mGame.cursorSprite.playInvalid();
                break;
            case CURSOR_MODE_PLACEABLE:
                mGame.cursorSprite.playPlace();
                break;
            case CURSOR_MODE_REMOVEABLE:
                mGame.cursorSprite.playDelete();
                break;
            case CURSOR_MODE_TARGET:
                mGame.cursorSprite.playTarget();
                break;
            }
        }
        mGame.cursorSprite.setPosition(mCursorX - VideoConstants.CURSOR_ORIGIN_X, mCursorY - VideoConstants.CURSOR_ORIGIN_Y);
    }
    
    private TAZSGame mGame;
    
    private float mCursorX;
    private float mCursorY;
    private int mCursorMode = CURSOR_MODE_INVALID;
    
    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
    private static final int CURSOR_MODE_INVALID = 0;
    private static final int CURSOR_MODE_PLACEABLE = 1;
    private static final int CURSOR_MODE_REMOVEABLE = 2;
    private static final int CURSOR_MODE_TARGET = 3;
}