package net.ccat.tazs.battle.modes.challenges;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.sworder.SworderSeekHandler;
import net.ccat.tazs.resources.Texts;


/**
 * A Simple Challenge where the Player has to defeat a pack of 4 Brawlers surrounding a Sworder.
 */
public class Challenge05BattleMode
    extends ChallengeBattleMode
{
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        game.unitsSystem.addUnit(75, -25,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(75, 25,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(50, 0,
                                 Math.PI,
                                 SworderSeekHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(25, -25,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        game.unitsSystem.addUnit(25, 25,
                                 Math.PI,
                                 BrawlerIdleHandler.instance,
                                 Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
        super.onPreparationInit(game);
    }
    
    
    /***** INFORMATION *****/
    
    public String name()
    {
        return Texts.CHALLENGES_05_NAME;
    }
    
    public String summary()
    {
        return Texts.CHALLENGES_05_SUMMARY;
    }
    
    public int allowedCost()
    {
        return 200;
    }
}