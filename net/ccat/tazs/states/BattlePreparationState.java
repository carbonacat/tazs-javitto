package net.ccat.tazs.states;

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
    }
    
    public void shutdown()
    {
        mGame = null;
    }
    
    public void update()
    {
        mGame.screen.clear(Colors.SCENE_BG_COLOR);
        mGame.screen.flush();
    }
 
 
    /***** PRIVATE *****/
    
    private TAZSGame mGame;
}