//
// Copyright (C) 2019 Carbonacat
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package net.ccat.tazs.battle;

import femto.Game;

import net.ccat.tazs.resources.Texts;
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
     * Returns the Type of a Unit that can be Placed in Preparation, using the Game's currentUnitType.
     * 
     * Default implementation will add currentUnitType and delta, wrap it around so it remains correct, and check isUnitTypeAllowed() with the resulting type until it's allowed.
     * If delta is 0 or positive, it'll try the next Unit Type.
     * If delta is negative, it'll try the previous Unit Type.
     * 
     * Delta isn't expected to be different than -1, 0 and 1.
     * 
     * @param game The Game.
     * @param delta A reference type.
     * @return The Type of a Unit.
     */
    public int placeableUnitType(TAZSGame game, int delta)
    {
        int newCurrentUnitType = (game.currentUnitType + delta + UnitTypes.END) % UnitTypes.END;
        
        // Checks the previous or next unit depending on delta until we find a correct one.
        while (!isUnitTypeAllowed(game, newCurrentUnitType))
            if (delta >= 0)
                newCurrentUnitType = (newCurrentUnitType + 1) % UnitTypes.END;
            else
                newCurrentUnitType = (newCurrentUnitType + UnitTypes.END - 1) % UnitTypes.END;
        return newCurrentUnitType;
    }
    
    /**
     * Checks if the given type is allowed.
     * 
     * Default implementation returns true whatever the given type.
     * 
     * @return true if the type is allowed, false if not.
     */
    public boolean isUnitTypeAllowed(TAZSGame game, int type)
    {
        return true;
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
        updateTopBarsWithHealth(game);
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
     * Default implementation will do nothing.
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
     * Called after updating the UI in the Battle Phase.
     * 
     * Default implementation updates the Top Bars.
     * 
     * @param game The Game.
     */
    public void onBattleUpdateUI(TAZSGame game)
    {
        updateTopBarsWithHealth(game);
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
     * Called in Result' init() method.
     * 
     * Default does nothing.
     * 
     * @param game The Game.
     * @param winnerTeam The winning team.
     */
    public void onResultInit(TAZSGame game, int winnerTeam)
    {
    }
    
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
     * Called when the Player uses the PadMenu in the Results.
     * 
     * Default implementation does nothing and returns false.
     * 
     * @param game The Game.
     * @param selectedChoice Which choice they made.
     * @return true if choice was handled, false otherwise.
     */
    public boolean onResultMenuChoice(TAZSGame game, int selectedChoice)
    {
        return false;
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
    
    
    /***** TOOLS *****/
    
    /**
     * Updates the Top Bars using sides' Healths.
     * @param game The Game.
     */
    public void updateTopBarsWithHealth(TAZSGame game)
    {
        game.topBarUI.setLeftBar(game.unitsSystem.unitsHP(Teams.PLAYER), game.unitsSystem.unitsHPMax(Teams.PLAYER));
        game.topBarUI.setRightBar(game.unitsSystem.unitsHP(Teams.ENEMY), game.unitsSystem.unitsHPMax(Teams.ENEMY));
    }
}