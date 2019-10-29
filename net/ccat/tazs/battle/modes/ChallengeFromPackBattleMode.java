package net.ccat.tazs.battle.modes;

import net.ccat.tazs.resources.challenges.ChallengePackReader;


/**
 * A Battle against randomly placed enemies.
 * The Player can place whatever units they want on their side.
 */
public abstract class ChallengeFromPackBattleMode
    extends ChallengeBattleMode
{
    public ChallengeFromPackBattleMode(int identifier, pointer challengePointer)
    {
        super(identifier);
        mChallengePointer = challengePointer;
        mChallengeName = ChallengePackReader.titlePointerFromChallenge(challengePointer);
        mChallengeDescription = ChallengePackReader.descriptionPointerFromChallenge(challengePointer);
        mAllowedCost = ChallengePackReader.allowedResourcesFromChallenge(challengePointer);
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
    
    
    /***** INFORMATION *****/
    
    public pointer battleTitle()
    {
        return mChallengeName;
    }
    
    public pointer battleSummary()
    {
        return mChallengeDescription;
    }
    
    public int allowedCost()
    {
        return mAllowedCost;
    }
    
    
    /***** PRIVATE *****/
    
    private pointer mChallengePointer;
    private pointer mChallengeName;
    private pointer mChallengeDescription;
    private int mAllowedCost;
}