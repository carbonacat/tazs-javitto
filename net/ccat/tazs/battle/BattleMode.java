package net.ccat.tazs.battle;

import net.ccat.tazs.states.BattlePreparationPhaseState;


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