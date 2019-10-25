package net.ccat.tazs.battle.modes.challenges;

import net.ccat.tazs.resources.Texts;


/**
 * A Simple Challenge where the Player has to defeat a few guys with the same amount of money.
 */
public class Challenge02BattleMode
    extends ChallengeBattleMode
{
    public Challenge02BattleMode(int identifier)
    {
        super(identifier);
    }
    
    
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        game.unitsSystem.addUnit(40, -20, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(40, 20, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(80, -20, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(80, 20, UnitTypes.BRAWLER, Teams.ENEMY);
        super.onPreparationInit(game);
    }
    
    
    /***** INFORMATION *****/
    
    public String name()
    {
        return Texts.CHALLENGES_02_NAME;
    }
    
    public String summary()
    {
        return Texts.CHALLENGES_02_SUMMARY;
    }
    
    public int allowedCost()
    {
        return 24;
    }
}