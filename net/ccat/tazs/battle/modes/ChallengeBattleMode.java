package net.ccat.tazs.battle.modes;

import femto.Game;
import femto.input.Button;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.RESULT_NEXT_CHALLENGE;
import net.ccat.tazs.states.BattlePreparationPhaseState;
import net.ccat.tazs.states.ChallengesListState;
import net.ccat.tazs.ui.PadMenuUI;
import net.ccat.tazs.ui.UIModes;


/**
 * A Battle against randomly placed enemies.
 * The Player can place whatever units they want on their side.
 */
public abstract class ChallengeBattleMode
    extends BattleMode
{
    public ChallengeBattleMode(int identifier)
    {
        this.mIdentifier = identifier;
    }
    
    
    /***** INFORMATION *****/
    
    public int getIdentifier()
    {
        return mIdentifier;
    }
    
    
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        updateTopBarUI(game);
    }
    
    public boolean isUnitTypeAllowed(TAZSGame game, int type)
    {
        return (type != UnitTypes.TARGET);
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
            else if (Button.A.justPressed())
            {
                UnitHandler unitHandler = game.unitsSystem.unitsHandlers[game.focusedUnitIdentifier];
                
                if (unitHandler.isControlled())
                    unitHandler.onPlayerControl(game.unitsSystem, game.focusedUnitIdentifier, false);
                else
                {
                    for (int unitIdentifier = 0; unitIdentifier < game.unitsSystem.mCount; unitIdentifier++)
                        game.unitsSystem.unitsHandlers[unitIdentifier].onPlayerControl(game.unitsSystem, unitIdentifier, false);
                    unitHandler.onPlayerControl(game.unitsSystem, game.focusedUnitIdentifier, true);
                }
            }
            game.uiMode = UIModes.REMOVE;
        }
        else
        {
            int teamUnderCursor = game.areaTeamAtPosition((int)game.cursorX, (int)game.cursorY);
            
            if (teamUnderCursor == Teams.PLAYER)
            {
                boolean tooExpensive = isTooExpensive(game, UnitTypes.idleHandlerForType(game.currentUnitType));
    
                if (!tooExpensive && Button.A.isPressed())
                {
                    
                    if (game.unitsSystem.addUnit(game.cursorX, game.cursorY,
                                                 game.currentUnitType, Teams.PLAYER) != UnitsSystem.IDENTIFIER_NONE)
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
            else if (teamUnderCursor == Teams.ENEMY)
                game.uiMode = UIModes.ENEMY_TERRITORY;
            else
                game.uiMode = UIModes.NOMANSLAND;
        }
    }
    
    public void onPreparationExit(TAZSGame game)
    {
        Game.changeState(new ChallengesListState(game, getIdentifier()));
    }
    
    
    /***** RESULT *****/
    
    public void onBattleExit(TAZSGame game)
    {
        Game.changeState(new ChallengesListState(game, getIdentifier()));
    }
    
    
    /***** RESULT *****/
    
    public void onResultInit(TAZSGame game, int winnerTeam)
    {
        if (winnerTeam == Teams.PLAYER)
            game.padMenuUI.setChoiceX(PadMenuUI.CHOICE_RIGHT, RESULT_NEXT_CHALLENGE.bin());
    }
    
    public boolean onResultMenuChoice(TAZSGame game, int selectedChoice)
    {
        if (selectedChoice == PadMenuUI.CHOICE_RIGHT)
        {
            Game.changeState(new ChallengesListState(game, getIdentifier() + 1));
            game.cursorSelectSound.play();
            return true;
        }
        return false;
    }
    
    public void onResultExit(TAZSGame game)
    {
        Game.changeState(new ChallengesListState(game, getIdentifier()));
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
    
    
    /***** PRIVATE *****/
    
    private void updateTopBarUI(TAZSGame game)
    {
        int playerCost = game.unitsSystem.unitsCost(Teams.PLAYER);
        int enemyCost = game.unitsSystem.unitsCost(Teams.ENEMY);
        
        game.topBarUI.setLeftCountAndCost(Texts.TEAMS_PLAYERX, game.unitsSystem.unitsCount(Teams.PLAYER), allowedCost() - playerCost);
        game.topBarUI.setRightNameAndSummary(name(), summary());
        updateTopBarsWithHealth(game);
    }
    
    private int mIdentifier;
}