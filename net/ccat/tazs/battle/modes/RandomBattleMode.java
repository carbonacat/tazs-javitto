package net.ccat.tazs.battle.modes;

import femto.input.Button;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.states.BattlePreparationPhaseState;
import net.ccat.tazs.ui.UIModes;


/**
 * A Battle against randomly placed enemies.
 * The Player can place whatever units they want on their side.
 */
public class RandomBattleMode
    extends BattleMode
{
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        // TODO: Eventually will be setup with a proper battle plan. [014]
        for (int remainingCluster = Math.random(1, 16); remainingCluster > 0 ; remainingCluster--)
        {
            float clusterX = 60 + (Math.random() - 0.5f) * 80;
            float clusterY = (Math.random() - 0.5f) * 80;
            
            for (int remainingUnit = Math.random(1, 4); remainingUnit > 0 ; remainingUnit--)
                game.unitsSystem.addUnit(clusterX + (Math.random() - 0.5f) * 20, clusterY + (Math.random() - 0.5f) * 20,
                                          Math.PI,
                                          BrawlerIdleHandler.instance,
                                          Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
            for (int remainingUnit = Math.random(0, 4); remainingUnit > 0 ; remainingUnit--)
                game.unitsSystem.addUnit(clusterX + (Math.random() - 0.5f) * 20, clusterY + (Math.random() - 0.5f) * 20,
                                          Math.PI,
                                          SlapperIdleHandler.instance,
                                          Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        }
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
            if (Button.A.isPressed())
            {
                UnitHandler unitHandler = UnitTypes.idleHandlerForType(game.currentUnitType);
                
                if (game.unitsSystem.addUnit(game.cursorX, game.cursorY, 0,
                                             unitHandler, Teams.PLAYER) != UnitsSystem.IDENTIFIER_NONE)
                {
                    updateTopBarUI(game);
                    // Resets the animation.
                    game.cursorSprite.currentFrame = game.cursorSprite.startFrame;
                }
            }
            if (game.unitsSystem.freeUnits() > 0)
                game.uiMode = UIModes.PLACE;
            else
                game.uiMode = UIModes.NO_MORE_UNITS;
        }
        else if (game.cursorX > game.noMansLandRadius)
            game.uiMode = UIModes.ENEMY_TERRITORY;
        else
            game.uiMode = UIModes.NOMANSLAND;
    }
    
    
    /***** PRIVATE *****/
    
    private void updateTopBarUI(TAZSGame game)
    {
        game.topBarUI.setLeftCountAndCost(Texts.TEAMS_PLAYER, game.unitsSystem.unitsCount(Teams.PLAYER), game.unitsSystem.unitsCost(Teams.PLAYER));
        game.topBarUI.setRightCountAndCost(Texts.TEAMS_ENEMY, game.unitsSystem.unitsCount(Teams.ENEMY), game.unitsSystem.unitsCost(Teams.ENEMY));
    }
}