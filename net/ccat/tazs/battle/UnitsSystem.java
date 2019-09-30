package net.ccat.tazs.battle;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.sprites.BrawlerBodySprite;
import net.ccat.tazs.resources.sprites.HandSprite;
import net.ccat.tazs.resources.VideoConstants;
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
        mXs = new float[UNITS_MAX];
        mYs = new float[UNITS_MAX];
        mAngles = new float[UNITS_MAX];
        mTimers = new short[UNITS_MAX];
        mHandlers = new UnitHandler[UNITS_MAX];
        mBrawlerBodySprite = new BrawlerBodySprite();
        mHandSprite = new HandSprite();
    }
    
    
    /***** UNITS *****/
    
    /**
     * Clears any stored Units.
     */
    public void clear()
    {
        mCounts = 0;
    }
    
    /**
     * Adds a Unit inside the system, at [x, y] and looking at [angle].
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param angle Where the units is looking at, in Radiants.
     * @return the unit's identifier, or IDENTIFIER_NONE if a Unit couldn't be created.
     */
    public int addUnit(float x, float y, float angle)
    {
        if (mCounts >= UNITS_MAX)
            return IDENTIFIER_NONE;

        int unitIdentifier = mCounts;
        
        mCounts++;
        mXs[unitIdentifier] = x;
        mYs[unitIdentifier] = y;
        mAngles[unitIdentifier] = MathTools.wrapAngle(angle);
        return unitIdentifier;
    }
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Called when a tick happens.
     */
    public void onTick()
    {
        for (int unitIdentifier = 0; unitIdentifier < mCounts; unitIdentifier++)
        {
            int unitTimer = mTimers[unitIdentifier];
            float unitX = mXs[unitIdentifier];
            float unitY = mYs[unitIdentifier];
            float unitAngle = mAngles[unitIdentifier];
            
            // Random walk
            unitTimer--;
            if (unitTimer <= 0)
            {
                unitTimer = 128;
                unitAngle = MathTools.wrapAngle(unitAngle + Math.random() - 0.5f);
            }
            unitX += Math.cos(unitAngle) * 0.125f;
            unitY += -Math.sin(unitAngle) * 0.125f;

            // Updating the changed state.
            mTimers[unitIdentifier] = unitTimer;
            mXs[unitIdentifier] = unitX;
            mYs[unitIdentifier] = unitY;
            mAngles[unitIdentifier] = unitAngle;
        }
    }
    
    
    /***** RENDERING *****/
    
    /**
     * Renders all the Units.
     * @param screen The screen to render into.
     */
    public void draw(HiRes16Color screen)
    {
        for (int unitIdentifier = 0; unitIdentifier < mCounts; unitIdentifier++)
        {
            float unitX = mXs[unitIdentifier];
            float unitY = mYs[unitIdentifier];
            float unitAngle = mAngles[unitIdentifier];
            float handDistance = 3;
            
            mHandSprite.setPosition(unitX + handDistance * Math.cos(unitAngle) - VideoConstants.HAND_SPRITE_ORIGIN_X,
                                    unitY - handDistance * Math.sin(unitAngle) - VideoConstants.HAND_SPRITE_ORIGIN_Y - VideoConstants.BRAWLER_BODY_SPRITE_WEAPON_ORIGIN_Y);
            // Is the hand above?
            if (unitAngle < Math.PI)
                mHandSprite.draw(screen);
            mBrawlerBodySprite.setPosition(unitX - VideoConstants.BRAWLER_BODY_SPRITE_ORIGIN_X, unitY - VideoConstants.BRAWLER_BODY_SPRITE_ORIGIN_Y);
            mBrawlerBodySprite.setMirrored(unitAngle > MathTools.PI_1_2 && unitAngle < MathTools.PI_3_2);
            mBrawlerBodySprite.draw(screen);
            // Is the hand below?
            if (unitAngle > Math.PI)
                mHandSprite.draw(screen);
        }
    }
    
    
    /***** PRIVATE *****/
    
    private float[] mXs;
    private float[] mYs;
    private float[] mAngles;
    private short[] mTimers;
    private UnitHandler[] mHandlers;
    private int mCounts = 0;
    
    private BrawlerBodySprite mBrawlerBodySprite;
    private HandSprite mHandSprite;
}