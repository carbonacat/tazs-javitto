package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.BrawlerBodySprite;
import net.ccat.tazs.resources.sprites.HandSprite;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


public class BaseBrawlerHandler
    implements UnitHandler
{
    public static final float WALK_SPEED = 0.125f;
    public static final float SEEK_DISTANCE_MAX = 250.f;
    public static final float CLOSE_DISTANCE = 5.f;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    public static final float ANGLE_ROTATION_BY_TICK = 4.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 5.f;
    public static final float HAND_RADIUS = 1.f;
    public static final float UNIT_RADIUS = 4.f;
    
    
    public BaseBrawlerHandler(boolean isAllied)
    {
        mIsAllied = isAllied;
    }
    
    
    /***** INFORMATION *****/
    
    public boolean isAllied()
    {
        return mIsAllied;
    }
    
    
    /***** PARTS POSITIONS *****/
    
    protected float handX(float unitX, float unitAngle, float handDistance)
    {
        return unitX + handDistance * Math.cos(unitAngle);
    }
    protected float handY(float unitY, float unitAngle, float handDistance)
    {
        return unitY + handDistance * Math.sin(unitAngle);
    }
    
    
    /***** RENDERING *****/
    
    protected void drawBrawler(float unitX, float unitY, float unitAngle, float handDistance,
                               BrawlerBodySprite bodySprite, HandSprite handSprite,
                               HiRes16Color screen)
    {
        handSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_X,
                               handY(unitY, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_Y - VideoConstants.BRAWLER_BODY_WEAPON_ORIGIN_Y);
        // Is the hand above?
        if (unitAngle < 0)
            handSprite.draw(screen);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLER_BODY_ORIGIN_X, unitY - VideoConstants.BRAWLER_BODY_ORIGIN_Y);
        bodySprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        bodySprite.draw(screen);
        
        int unitPixelX = (int)(unitX - VideoConstants.BRAWLER_BODY_SHIRT_X - screen.cameraX) + (bodySprite.isMirrored() ? 1 : 0);
        int unitPixelY = (int)(unitY - VideoConstants.BRAWLER_BODY_SHIRT_Y - screen.cameraY);
        int primaryColor = mIsAllied ? Colors.UNITS_ALLIES_PRIMARY : Colors.UNITS_ENEMIES_PRIMARY;
        int secondaryColor = mIsAllied ? Colors.UNITS_ALLIES_SECONDARY : Colors.UNITS_ENEMIES_SECONDARY;
        
        screen.setPixel(unitPixelX, unitPixelY, primaryColor);
        screen.setPixel(unitPixelX + 1, unitPixelY, secondaryColor);
        unitPixelY++;
        screen.setPixel(unitPixelX, unitPixelY, secondaryColor);
        screen.setPixel(unitPixelX + 1, unitPixelY, primaryColor);
        unitPixelY++;
        screen.setPixel(unitPixelX, unitPixelY, primaryColor);
        screen.setPixel(unitPixelX + 1, unitPixelY, secondaryColor);
        
        // Is the hand below?
        if (unitAngle >= 0)
            handSprite.draw(screen);
    }
    
    
    /***** PRIVATE *****/
    
    private boolean mIsAllied;
}