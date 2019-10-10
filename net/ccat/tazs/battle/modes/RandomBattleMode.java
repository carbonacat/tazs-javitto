package net.ccat.tazs.battle.modes;

import femto.Game;
import femto.input.Button;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.states.BattlePreparationPhaseState;
import net.ccat.tazs.states.TitleScreenState;
import net.ccat.tazs.ui.UIModes;


/**
 * A Battle against randomly placed enemies.
 * The Player can place whatever units they want on their side.
 */
public class RandomBattleMode
    extends ChallengeBattleMode
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
    
    public void onPreparationExit(TAZSGame game)
    {
        Game.changeState(new TitleScreenState(game));
    }
    
    
    /***** RESULT *****/
    
    public void onResultExit(TAZSGame game)
    {
        Game.changeState(new TitleScreenState(game));
    }
    
    
    /***** INFORMATION *****/
    
    public String name()
    {
        return "";
    }
    
    public String summary()
    {
        return "";
    }
    
    public int allowedCost()
    {
        return 0;
    }
    
    public boolean isTooExpensive(TAZSGame game, UnitHandler unitHandler)
    {
        return false;
    }
    
    public void updateTopBarUI(TAZSGame game)
    {
        game.topBarUI.setLeftCountAndCost(Texts.TEAMS_PLAYER, game.unitsSystem.unitsCount(Teams.PLAYER), game.unitsSystem.unitsCost(Teams.PLAYER));
        game.topBarUI.setRightCountAndCost(Texts.TEAMS_ENEMY, game.unitsSystem.unitsCount(Teams.ENEMY), game.unitsSystem.unitsCost(Teams.ENEMY));
    }
}