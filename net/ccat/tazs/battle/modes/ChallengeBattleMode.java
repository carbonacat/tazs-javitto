package net.ccat.tazs.battle.modes;

import femto.Game;
import femto.input.Button;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.states.BattlePreparationPhaseState;
import net.ccat.tazs.states.ChallengesListState;
import net.ccat.tazs.ui.UIModes;


/**
 * A Battle against randomly placed enemies.
 * The Player can place whatever units they want on their side.
 */
public abstract class ChallengeBattleMode
    extends BattleMode
{
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        updateTopBarUI(game);
    }
    
    public void onPreparationCursorUpdate(TAZSGame game)
    {
        // Finding a Unit that is hovered.
        game.focusedUnitIdentifier = game.unitsSystem.findUnit(game.cursorX, game.cursorY);
        
        if ((game.focusedUnitIdentifier != UnitsSystem.IDENTIFIER_NONE) && (game.unitsSystem.unitsTeams[game.focusedUnitIdentifier] == Teams.PLAYER))
        {
            if (Button.B.isPressed())
            {
                UnitHandler unitHandler = game.unitsSystem.unitsHandlers[game.focusedUnitIdentifier];
                
                game.unitsSystem.removeUnit(game.focusedUnitIdentifier);
                updateTopBarUI(game);
                game.focusedUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
            }
            game.uiMode = UIModes.REMOVE;
        }
        else if (game.cursorX < -game.noMansLandRadius)
        {
            UnitHandler unitHandler = UnitTypes.idleHandlerForType(game.currentUnitType);
            boolean tooExpensive = isTooExpensive(game, unitHandler);

            if (!tooExpensive && Button.A.isPressed())
            {
                
                if (game.unitsSystem.addUnit(game.cursorX, game.cursorY, 0,
                                             unitHandler, Teams.PLAYER) != UnitsSystem.IDENTIFIER_NONE)
                {
                    updateTopBarUI(game);
                    // Resets the animation.
                    game.cursorSprite.currentFrame = game.cursorSprite.startFrame;
                }
            }
            if (tooExpensive)
                game.uiMode = UIModes.TOO_EXPENSIVE;
            else if (game.unitsSystem.freeUnits() == 0)
                game.uiMode = UIModes.NO_MORE_UNITS;
            else
                game.uiMode = UIModes.PLACE;
        }
        else if (game.cursorX > game.noMansLandRadius)
            game.uiMode = UIModes.ENEMY_TERRITORY;
        else
            game.uiMode = UIModes.NOMANSLAND;
    }
    
    public void onPreparationExit(TAZSGame game)
    {
        Game.changeState(new ChallengesListState(game));
    }
    
    
    /***** INFORMATION *****/
    
    /**
     * @return The name of this Challenge.
     */
    public abstract String name();
    /**
     * @return The summary of this Challenge.
     */
    public abstract String summary();
    
    /**
     * @return the maximal cost.
     */
    public abstract int allowedCost();
    
    /**
     * @return true if the given unit would be considered too expensive.
     */
    public boolean isTooExpensive(TAZSGame game, UnitHandler unitHandler)
    {
        return (allowedCost() < game.unitsSystem.unitsCost(Teams.PLAYER) + unitHandler.cost());
    }
    
    
    public void updateTopBarUI(TAZSGame game)
    {
        game.topBarUI.setLeftCountAndCost(Texts.TEAMS_PLAYER, game.unitsSystem.unitsCount(Teams.PLAYER), allowedCost() - game.unitsSystem.unitsCost(Teams.PLAYER));
        game.topBarUI.setRightNameAndSummary(name(), summary());
    }
}