package net.ccat.tazs.battle.modes.challenges;


/**
 * A Simple Challenge where the Player has to defeat an army with 
 */
public class ChallengeFromBinaryBattleMode
    extends ChallengeBattleMode
{
    public ChallengeFromBinaryBattleMode(int identifier, pointer data)
    {
        super(identifier);
        
        pointer infoPointer = data;
        
        mName = infoPointer;
        mSummary = mName;
        
        // Skip the first string.
        while (System.memory.LDRB(infoPointer) != 0)
            infoPointer++;
        infoPointer++;
        
        mSummary = infoPointer;
        
        // Skips the second string.
        while (System.memory.LDRB(infoPointer) != 0)
            infoPointer++;
        infoPointer++;
        
        mAllowedCost = 256 * (int)System.memory.LDRB(infoPointer) + (int)System.memory.LDRB(infoPointer + 1);
        mBattlePlan = infoPointer + 2;
    }
    
    
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        pointer battlePlan = mBattlePlan;
        
        // Reading the number of units.
        int count = System.memory.LDRB(battlePlan++);
        
        while (count > 0)
        {
            // Team & Type.
            int unitInfo = System.memory.LDRB(battlePlan++);
            int unitX = (byte)System.memory.LDRB(battlePlan++);
            int unitY = (byte)System.memory.LDRB(battlePlan++);
            int unitTeam = (unitInfo & 0xC0) >> 6;
            int unitType = (unitInfo & 0x0F);
            
            game.unitsSystem.addUnit(unitX, unitY, unitType, unitTeam);
            count--;
        }
        super.onPreparationInit(game);
    }
    
    
    /***** INFORMATION *****/
    
    public pointer name()
    {
        return mName;
    }
    
    public pointer summary()
    {
        return mSummary;
    }
    
    public int allowedCost()
    {
        return mAllowedCost;
    }
    
    private pointer mName;
    private pointer mSummary;
    private int mAllowedCost;
    private pointer mBattlePlan;
}