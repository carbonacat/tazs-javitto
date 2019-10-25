package net.ccat.tazs.battle.modes.challenges;

import net.ccat.tazs.resources.Texts;


/**
 * A Simple Challenge where the Player has to defeat a few guys with the less money.
 */
public class Challenge03BattleMode
    extends ChallengeBattleMode
{
    public Challenge03BattleMode(int identifier)
    {
        super(identifier);
    }
    
    
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        game.unitsSystem.addUnit(40, -30, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(40, 30, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(100, -30, UnitTypes.BRAWLER, Teams.ENEMY);
        game.unitsSystem.addUnit(100, 30, UnitTypes.BRAWLER, Teams.ENEMY);
        super.onPreparationInit(game);
    }
    
    
    /***** INFORMATION *****/
    
    public String name()
    {
        return Texts.CHALLENGES_03_NAME;
    }
    
    public String summary()
    {
        return Texts.CHALLENGES_03_SUMMARY;
    }
    
    public int allowedCost()
    {
        return 16;
    }
}