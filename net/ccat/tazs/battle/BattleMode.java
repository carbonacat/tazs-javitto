package net.ccat.tazs.battle;

import femto.Game;

import net.ccat.tazs.states.TitleScreenState;


/**
 * Controls how a Battle is initialized, prepared, handled and ended.
 */
public class BattleMode
{
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