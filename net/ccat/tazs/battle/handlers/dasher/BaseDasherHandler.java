package net.ccat.tazs.battle.handlers.dasher;

import femto.Sprite;

import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.texts.UNIT_DASHER;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Base Handler for all Handlers related to the Dasher.
 */
public class BaseDasherHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 200;
    public static final float WALK_SPEED = 1.000f;
    public static final float ANGLE_ROTATION_BY_TICK = 24.f / 256.f;
    public static final float DASH_SPEED = 2.f;
    public static final float DASH_RADIUS = 6.f;
    public static final float DASH_POWER = 5.f;
    public static final int DASH_TIMER_INIT = 1;
    public static final int DASH_TIMER_END = 30;
    public static final int DASH_TIMER_RESTED = 60;
    public static final int DASH_HITS = 2;
    public static final int RUN_TIMER_CYCLE = 16;
    public static final float DASH_ANGLE_MAX = Math.PI * 0.5f;
    
    public static final float CLOSE_DISTANCE = ((DASH_TIMER_END - DASH_TIMER_INIT) / 2) * DASH_SPEED + DASH_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 50;
    public static final float INVERSE_WEIGHT = 0.50;
    public static final int DEATH_TICKS = 30;
    public static final int RECONSIDER_TICKS = -64;
    
    public static final int UI_TIMER_WRAPPER = DASH_TIMER_RESTED;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.DASHER;
    }
    
    public pointer name()
    {
        return UNIT_DASHER.bin();
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
    
    
    /***** EVENTS *****/
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
        {
            // TODO: Implement Control.
            //system.unitsHandlers[unitIdentifier] = DasherControlledHandler.instance;
            //return true;
        }
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        // Only 1 HP of damage when dashing.
        if ((unitTimer >= DASH_TIMER_INIT) && (unitTimer <= DASH_TIMER_END))
        {
            int unitHealth = system.unitsHealths[unitIdentifier];
            
            if (unitHealth > 0)
            {
                unitHealth--;
                if (unitHealth == 0)
                {
                    system.unitsTimers[unitIdentifier] = 0;
                    system.unitsHandlers[unitIdentifier] = DasherDeathHandler.instance;
                }
                system.unitsHealths[unitIdentifier] = unitHealth;
            }
        }
        else
        {
            // TODO: Automatic Dash when going to be hit while attack is ready.
            if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
                system.unitsHandlers[unitIdentifier] = DasherDeathHandler.instance;
        }
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         int unitTimer,
                         AdvancedHiRes16Color screen)
    {
        unitTimer = unitTimer % UI_TIMER_WRAPPER;
        
        if ((unitTimer >= DASH_TIMER_INIT) && (unitTimer <= DASH_TIMER_END))
            drawDashingDasherBody(unitX, unitY, unitAngle, unitTimer % UI_TIMER_WRAPPER,
                                  system.everythingSprite, baseFrameForTeam(unitTeam),
                                  screen);
        else
            drawRunningDasherBody(unitX, unitY, unitAngle, unitTimer % UI_TIMER_WRAPPER,
                                  system.everythingSprite, baseFrameForTeam(unitTeam),
                                  screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        HandlersTools.drawControlCircle(system, unitIdentifier, screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * @param unitTeam
     * @return The base frame for a given team.
     */
    public static int baseFrameForTeam(int unitTeam)
    {
        if (unitTeam == Teams.PLAYER)
            return VideoConstants.EVERYTHING_DASHERBODY_A_FRAME;
        if (unitTeam == Teams.ENEMY)
            return VideoConstants.EVERYTHING_DASHERBODY_B_FRAME;
        // Shouldn't happen!
        while (true);
        return VideoConstants.EVERYTHING_DASHERBODY_A_FRAME;
    }
    
    /**
     * Renders a Running Dasher using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawRunningDasherUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        
        drawRunningDasherBody(unitX, unitY, unitAngle, unitTimer,
                          system.everythingSprite, baseFrameForTeam(unitTeam),
                          screen);
    }
    
    /**
     * Renders a Dying Dasher using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingDasherUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];

        drawDyingDasherBody(unitX, unitY, unitAngle,
                            unitTimer,
                            system.everythingSprite, baseFrameForTeam(unitTeam),
                            screen);
    }
    
    /**
     * Renders a Dashing Dasher using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDashingDasherUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        drawDashingDasherBody(unitX, unitY, unitAngle,
                              unitTimer,
                              system.everythingSprite, baseFrameForTeam(unitTeam),
                              screen);
    }
    
    /**
     * Renders a Running Dasher Body.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param unitTimer
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawRunningDasherBody(float unitX, float unitY, float unitAngle,
                                             int unitTimer,
                                             NonAnimatedSprite everythingSprite, int baseFrame,
                                             AdvancedHiRes16Color screen)
    {
        everythingSprite.selectFrame(baseFrame + runningFrameForTimer(unitTimer));
        everythingSprite.setPosition(unitX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        everythingSprite.draw(screen);
    }
    
    /**
     * Renders a Dashing Dasher Body.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param unitTimer
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawDashingDasherBody(float unitX, float unitY, float unitAngle,
                                             int unitTimer,
                                             NonAnimatedSprite everythingSprite, int baseFrame,
                                             AdvancedHiRes16Color screen)
    {
        float deltaX = Math.cos(unitAngle) * DASH_SPEED;
        float deltaY = Math.sin(unitAngle) * DASH_SPEED;

        everythingSprite.selectFrame(baseFrame);
        everythingSprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
            
        if ((unitTimer & 0x2) == 0x2)
        {
            everythingSprite.setPosition(unitX - deltaX - deltaX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - deltaY - deltaY - VideoConstants.EVERYTHING_ORIGIN_Y);
            everythingSprite.draw(screen);
        }
        if ((unitTimer & 0x1) == 0x1)
        {
            everythingSprite.setPosition(unitX - deltaX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - deltaY - VideoConstants.EVERYTHING_ORIGIN_Y);
            everythingSprite.draw(screen);
        }
        everythingSprite.setPosition(unitX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.draw(screen);
    }
    
    /**
     * Renders a Dying Dasher Body.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param unitTimer
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawDyingDasherBody(float unitX, float unitY, float unitAngle,
                                           int unitTimer,
                                           NonAnimatedSprite everythingSprite, int baseFrame,
                                           AdvancedHiRes16Color screen)
    {
        int rawFrame = MathTools.lerpi(unitTimer, 0, VideoConstants.DASHERBODY_DEAD_FRAMES_LAST, DEATH_TICKS, VideoConstants.DASHERBODY_DEAD_FRAMES_START);
        int frame = baseFrame + MathTools.clampi(rawFrame, VideoConstants.DASHERBODY_DEAD_FRAMES_START, VideoConstants.DASHERBODY_DEAD_FRAMES_LAST);
        
        everythingSprite.selectFrame(frame);
        everythingSprite.setPosition(unitX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        everythingSprite.draw(screen);
    }
    
    
    /***** ATTACK *****/
    
    
    
    /***** TOOLS *****/
    
    public static int runningFrameForTimer(int unitTimer)
    {
        int wrappedTimer = unitTimer % RUN_TIMER_CYCLE;
        
        if (wrappedTimer < 0)
        wrappedTimer += RUN_TIMER_CYCLE;
        return MathTools.lerpi(wrappedTimer, 0, VideoConstants.DASHERBODY_RUN_FRAMES_START, RUN_TIMER_CYCLE - 1, VideoConstants.DASHERBODY_RUN_FRAMES_END);
    }
}