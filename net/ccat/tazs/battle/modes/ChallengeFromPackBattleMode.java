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
        mAllowedUnitTypes = ChallengePackReader.allowedUnitTypesFromChallenge(challengePointer);
    }
    
    
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        pointer challengeUnitsPointer = ChallengePackReader.unitsPointerFromChallenge(mChallengePointer);
        int unitsCount = ChallengePackReader.countFromChallengeUnits(challengeUnitsPointer);
        
        for (int unitI = 0; unitI < unitsCount; unitI++)
        {
            int unitX = ChallengePackReader.xFromChallengeUnit(challengeUnitsPointer, unitI);
            int unitY = ChallengePackReader.yFromChallengeUnit(challengeUnitsPointer, unitI);
            int unitTeam = ChallengePackReader.teamFromChallengeUnit(challengeUnitsPointer, unitI);
            int unitType = ChallengePackReader.typeFromChallengeUnit(challengeUnitsPointer, unitI);
            
            game.unitsSystem.addUnit(unitX, unitY, unitType, unitTeam);
        }
        
        updateTopBarUI(game);
    }
    
    public boolean isUnitTypeAllowed(TAZSGame game, int type)
    {
        int expectedMask = 1 << type;
        
        return (mAllowedUnitTypes & expectedMask) == expectedMask;
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
    
    public int protectedUnitsCount()
    {
        return ChallengePackReader.countFromChallengeUnits(ChallengePackReader.unitsPointerFromChallenge(mChallengePointer));
    }
    
    
    /***** PRIVATE *****/
    
    private pointer mChallengePointer;
    private pointer mChallengeName;
    private pointer mChallengeDescription;
    private int mAllowedCost;
    private int mAllowedUnitTypes;
}