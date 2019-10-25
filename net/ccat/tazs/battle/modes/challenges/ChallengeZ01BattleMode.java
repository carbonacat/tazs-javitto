package net.ccat.tazs.battle.modes.challenges;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.resources.Texts;


/**
 * A Debug Challenge where the Player has to defeat a small army, but while controlling the first unit they place.
 * Temporary!
 */
public class ChallengeZ01BattleMode
    extends ChallengeBattleMode
{
    public ChallengeZ01BattleMode(int identifier)
    {
        super(identifier);
    }
    
    
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        game.unitsSystem.addUnit(40, 0, UnitTypes.BRAWLER, Teams.ENEMY);
        super.onPreparationInit(game);
    }
    
    public void onPreparationFinished(TAZSGame game)
    {
        super.onPreparationFinished(game);
        for (int unitIdentifier = 0; unitIdentifier < game.unitsSystem.mCount; unitIdentifier++)
            if (game.unitsSystem.unitsTeams[unitIdentifier] == Teams.PLAYER)
            {
                game.unitsSystem.unitsHandlers[unitIdentifier].onPlayerControl(game.unitsSystem, unitIdentifier, true);
                break ;
            }
    }
    
    /***** INFORMATION *****/
    
    public String name()
    {
        return "DBG - Controlling";
    }
    
    public String summary()
    {
        return "Meh";
    }
    
    public int allowedCost()
    {
        return 7;
    }
}