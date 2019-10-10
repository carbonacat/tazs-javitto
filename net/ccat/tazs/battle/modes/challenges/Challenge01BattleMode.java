package net.ccat.tazs.battle.modes.challenges;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.resources.Texts;


/**
 * A Simple Challenge where the Player has to defeat an army with 
 */
public class Challenge01BattleMode
    extends ChallengeBattleMode
{
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        game.unitsSystem.addUnit(40, -10,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(40, 10,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(60, -10,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(60, 10,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
    }
    
    
    /***** INFORMATION *****/
    
    public String name()
    {
        return Texts.CHALLENGES_01_NAME;
    }
    
    public String summary()
    {
        return Texts.CHALLENGES_01_SUMMARY;
    }
}