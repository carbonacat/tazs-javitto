package net.ccat.tazs.battle.modes.challenges;

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
        game.unitsSystem.addUnit(40, -10, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(40, 10, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(60, -10, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(60, 10, UnitTypes.BRAWLER, Teams.ENEMY);
        super.onPreparationInit(game);
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
    
    public int allowedCost()
    {
        return 160;
    }
}