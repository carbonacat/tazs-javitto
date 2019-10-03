package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


public class BrawlerIdleHandler
    implements UnitHandler
{
    static final BrawlerIdleHandler alliedInstance = new BrawlerIdleHandler(true);
    static final BrawlerIdleHandler ennemyInstance = new BrawlerIdleHandler(false);
    
    
    public BrawlerIdleHandler(boolean isAllied)
    {
        mIsAllied = isAllied;
    }
    
    
    /***** INFORMATION *****/
    
    public boolean isAllied()
    {
        return mIsAllied;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        // Random walk
        unitTimer--;
        if (unitTimer <= 0)
        {
            targetIdentifier = system.findClosestUnit(unitX, unitY, !mIsAllied);
            system.unitsTargetIdentifiers[unitIdentifier] = targetIdentifier;
            unitTimer = 128;
        }
        if (targetIdentifier != UnitsSystem.IDENTIFIER_NONE)
        {
            float relativeX = system.unitsXs[targetIdentifier] - unitX;
            float relativeY = system.unitsYs[targetIdentifier] - unitY;
            float squaredDistance = relativeX * relativeX + relativeY * relativeY;
            
            // Updating the angle
            if (squaredDistance > 0)
            {
                float targetAngle = Math.atan2(relativeY, relativeX);
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            }
            if (squaredDistance > CLOSE_DISTANCE_SQUARED)
            {
                unitX += Math.cos(unitAngle) * WALK_SPEED;
                unitY += Math.sin(unitAngle) * WALK_SPEED;
            }
        }

        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
        system.unitsXs[unitIdentifier] = unitX;
        system.unitsYs[unitIdentifier] = unitY;
        system.unitsAngles[unitIdentifier] = unitAngle;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        float handDistance = 3;
        
        system.handSprite.setPosition(unitX + handDistance * Math.cos(unitAngle) - VideoConstants.HAND_ORIGIN_X,
                                unitY + handDistance * Math.sin(unitAngle) - VideoConstants.HAND_ORIGIN_Y - VideoConstants.BRAWLER_BODY_WEAPON_ORIGIN_Y);
        // Is the hand above?
        if (unitAngle < Math.PI)
            system.handSprite.draw(screen);
        system.brawlerBodySprite.setPosition(unitX - VideoConstants.BRAWLER_BODY_ORIGIN_X, unitY - VideoConstants.BRAWLER_BODY_ORIGIN_Y);
        system.brawlerBodySprite.setMirrored(unitAngle > MathTools.PI_1_2 && unitAngle < MathTools.PI_3_2);
        system.brawlerBodySprite.draw(screen);
        
        int unitPixelX = (int)(unitX - VideoConstants.BRAWLER_BODY_SHIRT_X - screen.cameraX) + (system.brawlerBodySprite.isMirrored() ? 1 : 0);
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
        if (unitAngle > Math.PI)
            system.handSprite.draw(screen);
    }
    
    
    /***** PRIVATE *****/
    
    private boolean mIsAllied;
    
    private static final float WALK_SPEED = 0.125f;
    private static final float CLOSE_DISTANCE = 5.f;
    private static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    private static final float ANGLE_ROTATION_BY_TICK = 4.f / 256.f;
}