package net.ccat.tazs.battle;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.resources.sprites.brawler.BrawlerBodyASprite;
import net.ccat.tazs.resources.sprites.brawler.BrawlerBodyBSprite;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.sprites.HandSprite;
import net.ccat.tazs.resources.sprites.slapper.SlapperBodyASprite;
import net.ccat.tazs.resources.sprites.slapper.SlapperBodyBSprite;

import net.ccat.tazs.tools.MathTools;


/**
 * Manages all the Units in a Battle, from their Updates to their Rendering.
 */
class UnitsSystem
{
    public static final int UNITS_MAX = 128;
    public static final int IDENTIFIER_NONE = -1;
    
    
    public UnitsSystem()
    {
        brawlerBodySpriteByTeam[Teams.PLAYER] = new BrawlerBodyASprite();
        brawlerBodySpriteByTeam[Teams.ENEMY] = new BrawlerBodyBSprite();
        slapperBodySpriteByTeam[Teams.PLAYER] = new SlapperBodyASprite();
        slapperBodySpriteByTeam[Teams.ENEMY] = new SlapperBodyBSprite();
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
    }
    
    /**
     * Adds a Unit inside the system.
     * Said unit's timer will be set to 0 and its targetIdentifier to IDENTIFIER_NONE.
     * 
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param angle Where the units is looking at, in Radiants.
     * @param team The unit's team.
     * @param handler The Handler for this unit.
     * @return the unit's identifier, or IDENTIFIER_NONE if a Unit couldn't be created.
     */
    public int addUnit(float x, float y, float angle,
                       UnitHandler handler, char team)
    {
        if (mCount >= UNITS_MAX)
            return IDENTIFIER_NONE;

        int unitIdentifier = mCount;
        
        mCount++;
        unitsXs[unitIdentifier] = x;
        unitsYs[unitIdentifier] = y;
        unitsAngles[unitIdentifier] = MathTools.wrapAngle(angle);
        unitsHealths[unitIdentifier] = handler.startingHealth();
        unitsTimers[unitIdentifier] = 0;
        unitsTargetIdentifiers[unitIdentifier] = IDENTIFIER_NONE;
        unitsHandlers[unitIdentifier] = handler;
        unitsTeams[unitIdentifier] = team;
        
        mStats[STATS_COUNT_OFFSET + team]++;
        mStats[STATS_COST_OFFSET + team] += handler.cost();
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
     * Finds the unit closest from the given point, of the given allegience.
     * @param x The X coordinate.
     * @param y the Y coordinate.
     * @param team The team that must match.
     * @param maxDistance The max distance the closest unit can have.
     * @param ignoreDead If true, the dead (health = 0) will be ignored.
     * @return the found Unit's identifier, or IDENTIFIER_NONE if none found.
     */
    public int findClosestUnit(float x, float y, char team, float maxDistance, boolean ignoreDead)
    {
        int closestUnitIdentifier = IDENTIFIER_NONE;
        float closestUnitDistanceSquared = maxDistance * maxDistance;
        
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            if ((!ignoreDead || (unitsHealths[unitIdentifier] > 0)) &&
                (unitsTeams[unitIdentifier] == team))
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
    
    
    /***** STATS *****/
    
    /**
     * @return A team from Teams.
     */
    public int winnerTeam()
    {
        int playerAliveUnitsCount = 0;
        int enemyAliveUnitsCount = 0;
        
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            if (unitsHealths[unitIdentifier] > 0)
                switch (unitsTeams[unitIdentifier])
                {
                case Teams.PLAYER:
                    playerAliveUnitsCount++;
                    break;
                case Teams.ENEMY:
                    enemyAliveUnitsCount++;
                    break;
                }
        if ((playerAliveUnitsCount > 0) && (enemyAliveUnitsCount > 0))
            return Teams.TO_BE_DETERMINED;
        if (playerAliveUnitsCount > 0)
            return Teams.PLAYER;
        if (enemyAliveUnitsCount > 0)
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
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Called when a tick happens.
     */
    public void onTick()
    {
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            unitsHandlers[unitIdentifier].onTick(this, unitIdentifier);
    }
    
    
    /***** RENDERING *****/
    
    public final NonAnimatedSprite[] brawlerBodySpriteByTeam = new NonAnimatedSprite[TEAM_MAX];
    public final NonAnimatedSprite[] slapperBodySpriteByTeam = new NonAnimatedSprite[TEAM_MAX];
    public final HandSprite handSprite = new HandSprite();
    
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
    
    private int mCount = 0;
    private int[] mStats = new int[STATS_MAX];

    private static final float FIND_DISTANCE_MAX = 5.f;
    private static final float FAR = 999;
    private static final float FAR_SQUARED = FAR * FAR;
    
    private static final int TEAM_MAX = 2;
    private static final int STATS_COUNT_OFFSET = 0;
    private static final int STATS_COST_OFFSET = TEAM_MAX;
    private static final int STATS_MAX = STATS_COST_OFFSET + TEAM_MAX;
}