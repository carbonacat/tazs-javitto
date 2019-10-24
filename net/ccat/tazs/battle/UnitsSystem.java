package net.ccat.tazs.battle;

import femto.mode.HiRes16Color;

import net.ccat.tazs.battle.handlers.HandlersTools;
import net.ccat.tazs.resources.sprites.HandSprite;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.sprites.shield.ShieldSprite;
import net.ccat.tazs.resources.sprites.sword.SwordSprite;

import net.ccat.tazs.tools.MathTools;


/**
 * Manages all the Units in a Battle, from their Updates to their Rendering.
 */
class UnitsSystem
{
    public static final int UNITS_MAX = 128;
    public static final int IDENTIFIER_NONE = -1;
    
    
    public UnitsSystem(NonAnimatedSprite everythingSprite)
    {
        this.everythingSprite = everythingSprite;
    }
    
    
    /***** UNITS *****/
    
    /**
     * X coordinates for all Units.
     */
    public float[] unitsXs = new float[UNITS_MAX];
    /**
     * Y coordinates for all Units.
     */
    public float[] unitsYs = new float[UNITS_MAX];
    /**
     * Angles for all Units.
     */
    public float[] unitsAngles = new float[UNITS_MAX];
    
    /**
     * Timers for all Units.
     */
    public short[] unitsTimers = new short[UNITS_MAX];
    /**
     * Health for all Units.
     */
    public short[] unitsHealths = new short[UNITS_MAX];
    /**
     * Targets for all Units.
     */
    public int[] unitsTargetIdentifiers = new int[UNITS_MAX];
    /**
     * Handlers for all Units.
     */
    public UnitHandler[] unitsHandlers = new UnitHandler[UNITS_MAX];
    /**
     * Allegiences for all Units.
     */
    public char[] unitsTeams = new char[UNITS_MAX];
    
    /**
     * Clears any stored Units.
     */
    public void clear()
    {
        mCount = 0;
        for (int i = 0; i < STATS_MAX; i++)
            mStats[i] = 0;
        mSeparatePairUnits = false;
        controlledUnitIdentifier = IDENTIFIER_NONE;
    }
    
    /**
     * Adds a Unit inside the system.
     * Said unit's timer will be set to 0 and its targetIdentifier to IDENTIFIER_NONE.
     * 
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param type The type for this Unit.
     * @param team The unit's team.
     * @return the unit's identifier, or IDENTIFIER_NONE if a Unit couldn't be created.
     */
    public int addUnit(float x, float y,
                       int type, char team)
    {
        if (mCount >= UNITS_MAX)
            return IDENTIFIER_NONE;

        int unitIdentifier = mCount;
        UnitHandler unitHandler = UnitTypes.idleHandlerForType(type);
        
        mCount++;
        unitsXs[unitIdentifier] = x;
        unitsYs[unitIdentifier] = y;
        unitsAngles[unitIdentifier] = defaultAngleForTeam(team);
        unitsHealths[unitIdentifier] = unitHandler.startingHealth();
        unitsTimers[unitIdentifier] = 0;
        unitsTargetIdentifiers[unitIdentifier] = IDENTIFIER_NONE;
        unitsHandlers[unitIdentifier] = unitHandler;
        unitsTeams[unitIdentifier] = team;
        
        mStats[STATS_COUNT_OFFSET + team]++;
        mStats[STATS_COST_OFFSET + team] += unitHandler.cost();
        mStats[STATS_HP_CURRENT_OFFSET + team] += unitHandler.startingHealth();
        mStats[STATS_HP_MAX_OFFSET + team] += unitHandler.startingHealth();
        return unitIdentifier;
    }
    
    /**
     * Removes a Unit from this System.
     * @param unitIdentifier the Unit's identifier.
     * @return true if removed, false elsewhere.
     * 
     * NOTE: Removing a Unit will change some Unit's identifiers.
     */
    public boolean removeUnit(int unitIdentifier)
    {
        if ((unitIdentifier < 0) || (unitIdentifier >= mCount))
            return false;
            
        // Updating stats.
        {
            int unitTeam = unitsTeams[unitIdentifier];
            UnitHandler unitHandler = unitsHandlers[unitIdentifier];
            
            mStats[STATS_COUNT_OFFSET + unitTeam]--;
            mStats[STATS_COST_OFFSET + unitTeam] -= unitHandler.cost();
            mStats[STATS_HP_CURRENT_OFFSET + unitTeam] -= unitsHealths[unitIdentifier];
            mStats[STATS_HP_MAX_OFFSET + unitTeam] -= unitHandler.startingHealth();
        }
        
        mCount--;
        
        int lastUnitIdentifier = mCount;
        
        // Swaps this Unit with the last one, if there is one.
        if ((unitIdentifier != lastUnitIdentifier) && (lastUnitIdentifier >= 0))
        {
            unitsXs[unitIdentifier] = unitsXs[lastUnitIdentifier];
            unitsYs[unitIdentifier] = unitsYs[lastUnitIdentifier];
            unitsAngles[unitIdentifier] = unitsAngles[lastUnitIdentifier];
            unitsHealths[unitIdentifier] = unitsHealths[lastUnitIdentifier];
            unitsTimers[unitIdentifier] = unitsTimers[lastUnitIdentifier];
            unitsTargetIdentifiers[unitIdentifier] = unitsTargetIdentifiers[lastUnitIdentifier];
            unitsHandlers[unitIdentifier] = unitsHandlers[lastUnitIdentifier];
            unitsTeams[unitIdentifier] = unitsTeams[lastUnitIdentifier];
        }
        controlledUnitIdentifier = IDENTIFIER_NONE;
        return true;
    }
    
    /**
     * @return The number of Units that can still be added.
     */
    public int freeUnits()
    {
        return UNITS_MAX - mCount;
    }
    
    /**
     * Finds a Unit at the given coordinates.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return the found Unit's identifier, or IDENTIFIER_NONE if none found.
     */
    public int findUnit(float x, float y)
    {
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
        {
            float relativeX = x - unitsXs[unitIdentifier];
            float relativeY = y - unitsYs[unitIdentifier];
            
            if ((tools.MathTools.abs(relativeX) <= FIND_DISTANCE_MAX) && (tools.MathTools.abs(relativeY) < FIND_DISTANCE_MAX))
                return unitIdentifier;
        }
        return IDENTIFIER_NONE;
    }
    
    /**
     * Finds the closest alive unit of a given allegience.
     * @param x The X coordinate.
     * @param y the Y coordinate.
     * @param team The team that must match.
     * @param maxDistance The max distance the closest unit can have.
     * @return the found Unit's identifier, or IDENTIFIER_NONE if none found.
     */
    public int findClosestLivingUnit(float x, float y, char team, float maxDistance)
    {
        int closestUnitIdentifier = IDENTIFIER_NONE;
        float closestUnitDistanceSquared = maxDistance * maxDistance;
        
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            if ((unitsHealths[unitIdentifier] > 0) && (unitsTeams[unitIdentifier] == team))
            {
                float relativeX = x - unitsXs[unitIdentifier];
                float relativeY = y - unitsYs[unitIdentifier];
                float distanceSquared = relativeX * relativeX + relativeY * relativeY;
                
                if (distanceSquared < closestUnitDistanceSquared)
                {
                    closestUnitDistanceSquared = distanceSquared;
                    closestUnitIdentifier = unitIdentifier;
                }
            }
        return closestUnitIdentifier;
    }
    
    /**
     * Finds a Unit that died the most recently (0 as timer, or max one otherwise).
     * @param team The team the Unit must belongs too.
     * @return The unit's identifier, or IDENTIFIER_NONE if none matched.
     */
    public int findUnitThatJustDied(char team)
    {
        int bestTimer = -1;
        int bestIdentifier = IDENTIFIER_NONE;
        
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            if ((unitsHealths[unitIdentifier] == 0) && (unitsTeams[unitIdentifier] == team))
            {
                int candidateTimer = unitsTimers[unitIdentifier];
                
                // Have they just died?
                if (candidateTimer == 0)
                    return unitIdentifier;
                if (bestTimer < candidateTimer)
                {
                    bestIdentifier = unitIdentifier;
                    bestTimer = candidateTimer;
                }
            }
        return bestIdentifier;
    }
    
    /**
     * Finds a Unit that is Controlled.
     * @return The unit's identifier, or IDENTIFIER_NONE if none matched.
     */
    public int findControlledUnitIdentifier()
    {
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            if (unitsHandlers[unitIdentifier].isControlled())
                return unitIdentifier;
        return IDENTIFIER_NONE;
    }
    
    
    /***** SAVING/RESTORE *****/
    
    public void save()
    {
        int saveIndex = 0;
        
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
        {
            UnitHandler unitHandler = unitsHandlers[unitIdentifier];
            int info = (unitsTeams[unitIdentifier] << SAVE_UNIT_INFO_TEAM_SHIFT) | (unitHandler.unitType() << SAVE_UNIT_INFO_TYPE_SHIFT);
            
            if (unitHandler.isControlled())
                info |= SAVE_UNIT_INFO_CONTROLLED_MASK;
            mSave[saveIndex + SAVE_UNIT_INFO_OFFSET] = info;
            mSave[saveIndex + SAVE_UNIT_X_OFFSET] = (int)unitsXs[unitIdentifier];
            mSave[saveIndex + SAVE_UNIT_Y_OFFSET] = (int)unitsYs[unitIdentifier];
            saveIndex += SAVE_UNIT_SIZE;
        }
        mSaveCount = mCount;
    }
    
    public void restore()
    {
        int saveIndex = 0;
        
        clear();
        for (int unitIdentifier = 0; unitIdentifier < mSaveCount; unitIdentifier++)
        {
            int info = mSave[saveIndex + SAVE_UNIT_INFO_OFFSET];
            int unitTeam = ((info & SAVE_UNIT_INFO_TEAM_MASK) >> SAVE_UNIT_INFO_TEAM_SHIFT);
            int unitType = ((info & SAVE_UNIT_INFO_TYPE_MASK) >> SAVE_UNIT_INFO_TYPE_SHIFT);
            float unitX = mSave[saveIndex + SAVE_UNIT_X_OFFSET];
            float unitY = mSave[saveIndex + SAVE_UNIT_Y_OFFSET];
            
            int actualUnitIdentifier = addUnit(unitX, unitY, unitType, unitTeam);
            
            if ((info & SAVE_UNIT_INFO_CONTROLLED_MASK) == SAVE_UNIT_INFO_CONTROLLED_MASK)
                UnitTypes.idleHandlerForType(unitType).onPlayerControl(this, actualUnitIdentifier, true);
            saveIndex += SAVE_UNIT_SIZE;
        }
        mTicks = 0;
    }
    
    
    
    /***** STATS *****/
    
    /**
     * @return A team from Teams.
     */
    public int winnerTeam()
    {
        int playerCurrentHP = unitsHP(Teams.PLAYER);
        int enemyCurrentHP = unitsHP(Teams.ENEMY);
        
        if (playerCurrentHP > 0)
        {
            if (enemyCurrentHP > 0)
                return Teams.TO_BE_DETERMINED;
            return Teams.PLAYER;
        }
        if (enemyCurrentHP > 0)
            return Teams.ENEMY;
        return Teams.NONE;
    }
    
    public int unitsCount(int team)
    {
        return mStats[STATS_COUNT_OFFSET + team];
    }
    public int unitsCost(int team)
    {
        return mStats[STATS_COST_OFFSET + team];
    }
    public int unitsHP(int team)
    {
        return mStats[STATS_HP_CURRENT_OFFSET + team];
    }
    public int unitsHPMax(int team)
    {
        return mStats[STATS_HP_MAX_OFFSET + team];
    }
    
    /**
     * @param team The team to count units from.
     * @return The corresponding number of units.
     */
    public int countDeadUnits(int team)
    {
        int count = 0;
        
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            if (unitsHealths[unitIdentifier] <= 0)
            {
                int unitTeam = unitsTeams[unitIdentifier];
                
                if (unitTeam == team)
                    count++;
            }
        return count;
    }
    
    
    /***** CONTROL *****/
    
    // TODO: I put those here by convenience, until I find somewhere fitter.
    /**
     * The last angle of the pad. 
     */
    public float playerPadAngle;
    /**
     * The length of the pad. Varies from 0 to 1.
     */
    public float playerPadLength;
    
    /**
     * True if the Player's action control is pressed, false elsewhere.
     */
    public boolean playerPrimaryAction;
    public boolean playerSecondaryAction;
    
    /**
     * The current Controlled Unit's identifier.
     * Controlled Handlers must update this field from their onTick(), as it's reset otherwise.
     */
    public int controlledUnitIdentifier = IDENTIFIER_NONE;
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Called when a tick happens.
     */
    public void onTick()
    {
        mTicks++;
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            unitsHandlers[unitIdentifier].onTick(this, unitIdentifier);
        // Updating some stats.
        for (int team = 0; team < TEAM_MAX; team++)
            mStats[STATS_HP_CURRENT_OFFSET + team] = 0;
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
        {
            int teamHPIndex = STATS_HP_CURRENT_OFFSET + unitsTeams[unitIdentifier];
            
            mStats[teamHPIndex] = mStats[teamHPIndex] + unitsHealths[unitIdentifier];
        }
        separateAllAliveUnits();
    }
    
    // TODO: Don't belong here?
    public void separateAllAliveUnits()
    {
        int startingIndex = mSeparatePairUnits ? 0 : 1;
        
        mSeparatePairUnits = !mSeparatePairUnits;
        for (int firstUnitIdentifier = startingIndex; firstUnitIdentifier < mCount; firstUnitIdentifier += 2)
            if (unitsHealths[firstUnitIdentifier] > 0)
            {
                float firstX = unitsXs[firstUnitIdentifier];
                float firstY = unitsYs[firstUnitIdentifier];
                float firstRadius = HandlersTools.UNIT_RADIUS;
                
                for (int secondUnitIdentifier = firstUnitIdentifier + 1; secondUnitIdentifier < mCount; secondUnitIdentifier++)
                    if (unitsHealths[secondUnitIdentifier] > 0)
                        {
                            float secondX = unitsXs[secondUnitIdentifier];
                            float secondY = unitsYs[secondUnitIdentifier];
                            float secondRadius = HandlersTools.UNIT_RADIUS;
                            float bothRadius = firstRadius + secondRadius;
                            float firstToSecondX = secondX - firstX;
                            float firstToSecondY = secondY - firstY;
                            float penetrationXAbs = MathTools.abs(firstToSecondX) - bothRadius;
                            float penetrationYAbs = MathTools.abs(firstToSecondY) - bothRadius;
                        
                            if ((penetrationXAbs < 0) && (penetrationYAbs < 0))
                            {
                                float firstInverseWeight = unitsHandlers[firstUnitIdentifier].inverseWeight();
                                float secondInverseWeight = unitsHandlers[secondUnitIdentifier].inverseWeight();
                                float bothInverseWeight = Math.max(firstInverseWeight + secondInverseWeight, 0.00390625f);

                                // Let's separate them.
                                if (penetrationXAbs > penetrationYAbs)
                                {
                                    float separationX = penetrationXAbs * (firstToSecondX > 0 ? -0.5f : 0.5f);
                                    
                                    firstX -= separationX * firstInverseWeight / bothInverseWeight;
                                    secondX += separationX * secondInverseWeight / bothInverseWeight;
                                    unitsXs[secondUnitIdentifier] = secondX;
                                }
                                else
                                {
                                    float separationY = penetrationYAbs * (firstToSecondY > 0 ? -0.5f : 0.5f);
                                    
                                    firstY -= separationY * firstInverseWeight / bothInverseWeight;
                                    secondY += separationY * secondInverseWeight / bothInverseWeight;
                                    unitsYs[secondUnitIdentifier] = secondY;
                                }
                            }
                        }
                unitsXs[firstUnitIdentifier] = firstX;
                unitsYs[firstUnitIdentifier] = firstY;
            }
    }
    
    public int ticks()
    {
        return mTicks;
    }
    
    
    /***** RENDERING *****/
    
    public NonAnimatedSprite everythingSprite;
    public final HandSprite handSprite = new HandSprite();
    public final SwordSprite swordSprite = new SwordSprite();
    public final ShieldSprite shieldSprite = new ShieldSprite();
    
    /**
     * Renders all the Units.
     * @param screen The screen to render into.
     */
    public void draw(HiRes16Color screen)
    {
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            unitsHandlers[unitIdentifier].draw(this, unitIdentifier, screen);
    }
    
    
    /***** PRIVATE *****/
    
    /**
     * @param unitTeam
     * @return The default angle that Units of this team faces.
     */
    private static float defaultAngleForTeam(int unitTeam)
    {
        if (unitTeam == Teams.PLAYER)
            return 0;
        if (unitTeam == Teams.ENEMY)
            return Math.PI;
        // Not supposed to happen.
        while (true);
        return 0;
    }
    
    
    private int mCount = 0;
    private int[] mStats = new int[STATS_MAX];
    private int mTicks = 0;
    
    private byte[] mSave = new byte[UNITS_MAX * SAVE_UNIT_SIZE];
    private int mSaveCount = 0;
    
    private boolean mSeparatePairUnits = false;

    private static final float FIND_DISTANCE_MAX = 5.f;
    private static final float FAR = 999;
    private static final float FAR_SQUARED = FAR * FAR;
    
    private static final int TEAM_MAX = 2;
    private static final int STATS_COUNT_OFFSET = 0;
    private static final int STATS_COST_OFFSET = TEAM_MAX;
    private static final int STATS_HP_MAX_OFFSET = STATS_COST_OFFSET + TEAM_MAX;
    private static final int STATS_HP_CURRENT_OFFSET = STATS_HP_MAX_OFFSET + TEAM_MAX;
    private static final int STATS_MAX = STATS_HP_CURRENT_OFFSET + TEAM_MAX;
    
    private static final int SAVE_UNIT_SIZE = 3;
    private static final int SAVE_UNIT_INFO_OFFSET = 0;
    private static final int SAVE_UNIT_INFO_TYPE_MASK = 0xF;
    private static final int SAVE_UNIT_INFO_TYPE_SHIFT = 0;
    private static final int SAVE_UNIT_INFO_TEAM_MASK = 0xC0;
    private static final int SAVE_UNIT_INFO_TEAM_SHIFT = 6;
    private static final int SAVE_UNIT_INFO_CONTROLLED_MASK = 0x20;
    private static final int SAVE_UNIT_X_OFFSET = 1;
    private static final int SAVE_UNIT_Y_OFFSET = 2;
}