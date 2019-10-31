package net.ccat.tazs.battle.handlers.target;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.texts.UNIT_TARGET;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Base Handler for all Handlers related to the Target.
 */
public class BaseTargetHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 100;
    public static final short HEALTH_DAMAGED = 50;
    
    public static final int COST = 0;
    public static final float INVERSE_WEIGHT = 0;
    public static final int DEATH_TICKS = 64;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.TARGET;
    }
    
    public pointer name()
    {
        return UNIT_TARGET.bin();
    }
    
    public int startingHealth()
    {
        return HEALTH_INITIAL;
    }
    
    public int cost()
    {
        return COST;
    }
    
    public boolean isControlled()
    {
        return false;
    }
    
    public float inverseWeight()
    {
        return INVERSE_WEIGHT;
    }
    
    public boolean isReadyToAttack(UnitsSystem system, int unitIdentifier)
    {
        return false;
    }
    
    
    /***** EVENTS *****/
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
            system.unitsHandlers[unitIdentifier] = TargetDeadHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         int unitTimer,
                         AdvancedHiRes16Color screen)
    {
        drawTarget(unitX, unitY, HEALTH_INITIAL,
                   system.everythingSprite,
                   screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        // Not controllable.
        while (true);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    public static void drawTargetUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        int unitHealth = system.unitsHealths[unitIdentifier];
        
        drawTarget(unitX, unitY, unitHealth, system.everythingSprite, screen);
    }
    
    public static void drawDyingTargetUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        drawDyingTarget(unitX, unitY,
                        unitTimer,
                        system.everythingSprite,
                        screen);
    }
    
    public static void drawTarget(float unitX, float unitY, int unitHealth,
                                  NonAnimatedSprite everythingSprite,
                                  AdvancedHiRes16Color screen)
    {
        boolean isDamaged = unitHealth <= HEALTH_DAMAGED;
        int frame = VideoConstants.EVERYTHING_TARGET_FRAME + (isDamaged ? VideoConstants.TARGET_DAMAGED_FRAME : VideoConstants.TARGET_IDLE_FRAME);
        
        everythingSprite.setMirrored(false);
        everythingSprite.selectFrame(frame);
        everythingSprite.setPosition(unitX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.draw(screen);
    }
    
    public static void drawDyingTarget(float unitX, float unitY,
                                       int unitTimer,
                                       NonAnimatedSprite everythingSprite,
                                       AdvancedHiRes16Color screen)
    {
        int rawFrame = MathTools.lerpi(unitTimer, 0, VideoConstants.TARGET_DEAD_FRAMES_LAST, DEATH_TICKS, VideoConstants.TARGET_DEAD_FRAMES_START);
        int frame = VideoConstants.EVERYTHING_TARGET_FRAME + MathTools.clampi(rawFrame, VideoConstants.TARGET_DEAD_FRAMES_START, VideoConstants.TARGET_DEAD_FRAMES_LAST);
        
        everythingSprite.setMirrored(false);
        everythingSprite.selectFrame(frame);
        everythingSprite.setPosition(unitX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.draw(screen);
    }
}