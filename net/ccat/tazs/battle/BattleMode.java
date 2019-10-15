package net.ccat.tazs.battle;

import femto.Game;

import net.ccat.tazs.states.BattlePhaseState;
import net.ccat.tazs.states.BattlePreparationPhaseState;
import net.ccat.tazs.states.TitleScreenState;


/**
 * Controls how a Battle is initialized, prepared, handled and ended.
 */
public class BattleMode
{
    /***** LAUNCH *****/
    
    
    /**
     * Called when the BattleMode is launched.
     * 
     * Default implementation will switch to the BattlePreparationPhaseState.
     * 
     * @param game The Game.
     */
    public void onLaunch(TAZSGame game)
    {
        Game.changeState(new BattlePreparationPhaseState(game, false));
    }
    
    
    /***** PREPARATION *****/
    
    /**
     * Called when the BattlePreparationPhase is initialized.
     * Great for pre-placing units.
     * 
     * Default implementation will do nothing.
     * 
     * @param game The Game.
     */
    public void onPreparationInit(TAZSGame game)
    {
    }
    
    /**
     * Called when the BattlePreparationPhase is initialized, but from a Retry.
     * 
     * Default implementation will restore units.
     * 
     * @param game The Game.
     */
    public void onPreparationRetry(TAZSGame game)
    {
        game.unitsSystem.restore();
    }
    
    /**
     * Called when the BattlePreparationPhase is updating its input, within the Menu.
     * 
     * Default implementation will do nothing.
     * 
     * @param game The Game.
     */
    public void onPreparationMenuUpdate(TAZSGame game)
    {
    }
    
    /**
     * Called when the BattlePreparationPhase is updating its input, within the Cursor.
     * 
     * Default implementation does nothing.
     * 
     * @param game The Game.
     */
    public void onPreparationCursorUpdate(TAZSGame game)
    {
    }
    
    /**
     * Called when the Player wants to launch the battle.
     * 
     * Default implementation saves the battle scene and switches to the BattlePhaseState.
     * 
     * @param game The Game.
     */
    public void onPreparationFinished(TAZSGame game)
    {
        game.unitsSystem.save();
        Game.changeState(new BattlePhaseState(game));
    }
    
    /**
     * Called when the Player wants to exit the Preparation without battling.
     * 
     * Default implementation switches to the TitleScreenState.
     * 
     * @param game The Game.
     */
    public void onPreparationExit(TAZSGame game)
    {
        Game.changeState(new TitleScreenState(game));
    }
    
    
    /***** BATTLE *****/
    
    /**
     * Called when the Player wants to exit the Battle.
     * 
     * Default implementation switches to the TitleScreenState.
     * 
     * @param game The Game.
     */
    public void onBattleExit(TAZSGame game)
    {
        Game.changeState(new TitleScreenState(game));
    }
    
    /**
     * Called when the Player wants to retry the same battle.
     * 
     * Default implementation switches to the BattlePreparationPhaseState in retry mode.
     * 
     * @param game The Game.
     */
    public void onBattleRetry(TAZSGame game)
    {
        Game.changeState(new BattlePreparationPhaseState(game, true));
    }
    
    
    /***** RESULT *****/
    
    /**
     * Called when the Player wants to exit the Result Screen.
     * 
     * Default implementation switches to the TitleScreenState.
     * 
     * @param game The Game.
     */
    public void onResultExit(TAZSGame game)
    {
        Game.changeState(new TitleScreenState(game));
    }
    
    /**
     * Called when the Player wants to retry the same battle.
     * 
     * Default implementation switches to the BattlePreparationPhaseState in retry mode.
     * 
     * @param game The Game.
     */
    public void onResultRetry(TAZSGame game)
    {
        Game.changeState(new BattlePreparationPhaseState(game, true));
    }
    
    /**
     * Called when going to render the UnitBox's previewed Unit.
     * 
     * Default implementation always returns the Player's team.
     * 
     * @param preparationPhase The Phase.
     * @return the Teams' for the UnitBox.
     */
    public int teamForUnitBox(TAZSGame game)
    {
        return Teams.PLAYER;
    }
}