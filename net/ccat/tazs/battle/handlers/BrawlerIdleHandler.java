package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


public class BrawlerIdleHandler
    implements UnitHandler
{
    static final BrawlerIdleHandler instance = new BrawlerIdleHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        
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
        
        system.handSprite.setPosition(unitX + handDistance * Math.cos(unitAngle) - VideoConstants.HAND_SPRITE_ORIGIN_X,
                                unitY - handDistance * Math.sin(unitAngle) - VideoConstants.HAND_SPRITE_ORIGIN_Y - VideoConstants.BRAWLER_BODY_SPRITE_WEAPON_ORIGIN_Y);
        // Is the hand above?
        if (unitAngle < Math.PI)
            system.handSprite.draw(screen);
        system.brawlerBodySprite.setPosition(unitX - VideoConstants.BRAWLER_BODY_SPRITE_ORIGIN_X, unitY - VideoConstants.BRAWLER_BODY_SPRITE_ORIGIN_Y);
        system.brawlerBodySprite.setMirrored(unitAngle > MathTools.PI_1_2 && unitAngle < MathTools.PI_3_2);
        system.brawlerBodySprite.draw(screen);
        // Is the hand below?
        if (unitAngle > Math.PI)
            system.handSprite.draw(screen);
    }
}