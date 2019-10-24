package net.ccat.tazs.resources;


/**
 * Constants related to Video Resources.
 */
class VideoConstants
{
    /***** BRAWLER *****/
    
    public static final float BRAWLERBODY_HAND_OFFSET_Y = 5;
    public static final float BRAWLERBODY_WEAPON_OFFSET_Y = 3;
    public static final float BRAWLERBODY_SHIELD_OFFSET_Y = 1;
    public static final int BRAWLERBODY_FRAME_IDLE = 0;
    public static final int BRAWLERBODY_FRAME_DEAD_START = 1;
    public static final int BRAWLERBODY_FRAME_DEAD_LAST = 6;
    
    
    /***** CURSOR *****/
    
    public static final float CURSOR_ORIGIN_X = 3;
    public static final float CURSOR_ORIGIN_Y = 3;
    
    
    /***** EVERYTHING *****/
    
    public static final float EVERYTHING_ORIGIN_X = 25;
    public static final float EVERYTHING_ORIGIN_Y = 25;
    public static final int EVERYTHING_BRAWLERBODY_A_FRAME = 0;
    public static final int EVERYTHING_BRAWLERBODY_B_FRAME = 7;
    public static final int EVERYTHING_SLAPPERBODY_A_FRAME = 14;
    public static final int EVERYTHING_SLAPPERBODY_B_FRAME = 26;
    public static final int EVERYTHING_TINYGRASS_FRAME = 38;
    public static final int EVERYTHING_TARGET_FRAME = 42;
    public static final int EVERYTHING_HAND_FRAME = 49; // TODO: Should be 48, as Aseprite says it's 49. I'm not sure what's happening.
    public static final int EVERYTHING_SWORD_FRAME = 50; // TODO: Should be 49, as Aseprite says it's 50. I'm not sure what's happening.
    
    
    /***** MENU CURSOR *****/
    
    public static final int MENU_CURSOR_ORIGIN_X = 12;
    public static final int MENU_CURSOR_ORIGIN_Y = 2;
    
    
    /***** PAD MENU *****/
    
    public static final int PAD_MENU_ORIGIN_X = 8;
    public static final int PAD_MENU_ORIGIN_Y = 8;
    
    
    /***** SHIELD *****/
    
    public static final int SHIELD_ORIGIN_X = 8;
    public static final int SHIELD_ORIGIN_Y = 7;
    public static final int SHIELD_FRAME_BACK = 0;
    public static final int SHIELD_FRAME_FRONT = 4;
    // What to add to either SHIELD_FRAME_BACK or SHIELD_FRAME_FRONT to get the frame where the shield is on the ground.
    public static final int SHIELD_FRAME_FALLEN_INCREMENT = 3;
    
    
    /***** SLAPPER *****/
    
    public static final float SLAPPERBODY_HAND_OFFSET_Y = 5;
    public static final int SLAPPERBODY_FRAME_IDLE = 0;
    public static final int SLAPPERBODY_FRAME_DEAD_START = 1;
    public static final int SLAPPERBODY_FRAME_DEAD_LAST = 11;
    
    
    /***** SWORD *****/
    
    public static final int SWORD_FRAME_VERTICAL = 0;
    public static final int SWORD_FRAME_HORIZONTAL = 4;
    
    
    /***** TARGET *****/
    
    public static final int TARGET_FRAME_IDLE = 0;
    public static final int TARGET_FRAME_DAMAGED = 1;
    public static final int TARGET_FRAME_DEAD_START = 1;
    public static final int TARGET_FRAME_DEAD_LAST = 5;
    
    
    /***** TINY GRASS *****/
    
    public static final int TINYGRASS_FRAME_IDLE = 0;
    public static final int TINYGRASS_FRAME_START = 0;
    public static final int TINYGRASS_FRAME_LAST = 3;
    public static final int TINYGRASS_FRAME_COUNT = TINYGRASS_FRAME_LAST + 1;
}