package net.ccat.tazs.battle.handlers.brawler;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.HandSprite;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


public class BaseBrawlerHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 100;
    public static final float WALK_SPEED = 0.125f;
    public static final float SEEK_DISTANCE_MAX = 250.f;
    public static final float CLOSE_DISTANCE = 10.f;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    public static final float ANGLE_ROTATION_BY_TICK = 8.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 6.f;
    public static final float HAND_RADIUS = 1.f;
    public static final float HAND_POWER = 5.f;
    public static final float UNIT_RADIUS = 4.f;
    public static final float POWER_HP_RATIO = 3.f;
    public static final int COST = 20;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.BRAWLER;
    }
    
    public String name()
    {
        return Texts.UNIT_BRAWLER;
    }
    
    public int startingHealth()
    {
        return HEALTH_INITIAL;
    }
    
    public int cost()
    {
        return COST;
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
    
    
    /***** EVENTS *****/
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        short health = system.unitsHealths[unitIdentifier];
        
        // TODO: Do a proper pushback. [011]
        system.unitsXs[unitIdentifier] += powerX;
        system.unitsYs[unitIdentifier] += powerY;
        if (health > 0)
        {
            float lostHealth = power * POWER_HP_RATIO;
            
            if (health > lostHealth)
                health -= (short)(int)lostHealth;
            else
            {
                system.unitsHandlers[unitIdentifier] = BrawlerDeadHandler.instance;
                system.unitsTimers[unitIdentifier] = 0;
                health = 0;
            }
        }
        system.unitsHealths[unitIdentifier] = health;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE,
                    system.brawlerBodySpriteByTeam[unitTeam], system.handSprite,
                    screen);
    }
    
    protected void drawBrawler(float unitX, float unitY, float unitAngle, float handDistance,
                               NonAnimatedSprite bodySprite, HandSprite handSprite,
                               HiRes16Color screen)
    {
        handSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_X,
                               handY(unitY, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_Y - VideoConstants.BRAWLERBODY_WEAPON_ORIGIN_Y);
        // Is the hand above?
        if (unitAngle < 0)
            handSprite.draw(screen);
        bodySprite.selectFrame(VideoConstants.BRAWLERBODY_FRAME_IDLE);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
        bodySprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        bodySprite.draw(screen);

        // Is the hand below?
        if (unitAngle >= 0)
            handSprite.draw(screen);
    }
    
    protected void drawDeadBrawler(float unitX, float unitY, float unitAngle,
                                   NonAnimatedSprite bodySprite,
                                   int ticks, int ticksMax,
                                   HiRes16Color screen)
    {
        int rawFrame = MathTools.lerpi(ticks, 0, VideoConstants.BRAWLERBODY_FRAME_DEAD_LAST, ticksMax, VideoConstants.BRAWLERBODY_FRAME_DEAD_START);
        int frame = MathTools.clampi(rawFrame, VideoConstants.BRAWLERBODY_FRAME_DEAD_START, VideoConstants.BRAWLERBODY_FRAME_DEAD_LAST);
        
        bodySprite.selectFrame(frame);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
        bodySprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        bodySprite.draw(screen);
    }
}