package net.ccat.tazs.battle.modes.challenges;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.resources.Texts;


/**
 * A Simple Challenge where the Player has to defeat a few guys with the less money.
 */
public class Challenge04BattleMode
    extends ChallengeBattleMode
{
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        game.unitsSystem.addUnit(40, -30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(40, 30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(100, -30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(100, 30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(40, -30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(40, 30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(100, -30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(100, 30,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        super.onPreparationInit(game);
    }
    
    
    /***** INFORMATION *****/
    
    public String name()
    {
        return Texts.CHALLENGES_04_NAME;
    }
    
    public String summary()
    {
        return Texts.CHALLENGES_04_SUMMARY;
    }
    
    public int allowedCost()
    {
        return 160;
    }
}