package net.ccat.tazs.states;

import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.handlers.BrawlerIdleHandler;
import net.ccat.tazs.resources.Colors;
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
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        mGame.unitsSystem.onTick();
        
        screen.clear(Colors.SCENE_BG_COLOR);
        mGame.unitsSystem.draw(screen);
        screen.flush();
    }
    
    public void shutdown()
    {
        mGame = null;
    }
 
 
    /***** PRIVATE *****/
    
    private TAZSGame mGame;
}