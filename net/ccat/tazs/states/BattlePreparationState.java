package net.ccat.tazs.states;

import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.resources.Colors;


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
        mGame.unitsSystem.addUnit(-32, 0, 0);
        mGame.unitsSystem.addUnit(-64, -32, 0);
        mGame.unitsSystem.addUnit(64, 16, 3.57);
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