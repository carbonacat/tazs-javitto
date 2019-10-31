package net.ccat.tazs.resources;


/**
 * Constants related to Video Resources.
 */
class VideoConstants
{
    /***** BEAN *****/
    
    public static final int BEAN_WIDTH = 7;
    
    
    /***** BOW *****/
    
    public static final int BOW_IDLE_FRAME = 0;
    public static final int BOW_LOAD_FRAMES_START = 1;
    public static final int BOW_LOAD_FRAMES_LAST = 4;
    public static final int BOW_FIRE_FRAMES_START = 5;
    public static final int BOW_FIRE_FRAMES_LAST = 9;
    public static final int BOW_FADED_FRAME = 10;
    
    
    /***** BRAWLER *****/
    
    public static final float BRAWLERBODY_HAND_OFFSET_Y = 5;
    public static final float BRAWLERBODY_AIM_OFFSET_Y = 7;
    public static final float BRAWLERBODY_WEAPON_OFFSET_Y = 3;
    public static final float BRAWLERBODY_SHIELD_OFFSET_Y = 1;
    public static final int BRAWLERBODY_IDLE_FRAME = 0;
    public static final int BRAWLERBODY_DEAD_FRAMES_START = 1;
    public static final int BRAWLERBODY_DEAD_FRAMES_LAST = 6;
    
    
    /***** DASH GUY *****/
    
    public static final int DASHERBODY_RUN_FRAMES_START = 0;
    public static final int DASHERBODY_RUN_FRAMES_END = 3;
    public static final int DASHERBODY_DEAD_FRAMES_START = 4;
    public static final int DASHERBODY_DEAD_FRAMES_LAST = 9;
    
    
    /***** CURSOR *****/
    
    public static final float CURSOR_ORIGIN_X = 3;
    public static final float CURSOR_ORIGIN_Y = 3;
    
    
    /***** EVERYTHING *****/
    
    public static final float EVERYTHING_ORIGIN_X = 15;
    public static final float EVERYTHING_ORIGIN_Y = 15;
    public static final int EVERYTHING_BOW_FRAME = 69; // TODO: Should be 68, as Aseprite says it's 69. I'm not sure what's happening.
    public static final int EVERYTHING_BRAWLERBODY_A_FRAME = 0;
    public static final int EVERYTHING_BRAWLERBODY_B_FRAME = 7;
    public static final int EVERYTHING_DASHCONVEYOR_FRAME = 100; // TODO: Should be 99, as Aseprite says it's 49. I'm not sure what's happening.
    public static final int EVERYTHING_DASHERBODY_B_FRAME = 80; // TODO: Should be 79, as Aseprite says it's 49. I'm not sure what's happening.
    public static final int EVERYTHING_DASHERBODY_A_FRAME = 90; // TODO: Should be 89, as Aseprite says it's 49. I'm not sure what's happening.
    public static final int EVERYTHING_HAND_FRAME = 49; // TODO: Should be 48, as Aseprite says it's 49. I'm not sure what's happening.
    public static final int EVERYTHING_PIKE_FRAME = 64; // TODO: Should be 63 as Aseprite says it's 64. I'm not sure what's happening.
    public static final int EVERYTHING_SHIELD_FRAME = 56; // TODO: Should be 55, as Aseprite says it's 56. I'm not sure what's happening.
    public static final int EVERYTHING_SLAPPERBODY_A_FRAME = 14;
    public static final int EVERYTHING_SLAPPERBODY_B_FRAME = 26;
    public static final int EVERYTHING_SWORD_FRAME = 50; // TODO: Should be 49, as Aseprite says it's 50. I'm not sure what's happening.
    public static final int EVERYTHING_TARGET_FRAME = 42;
    public static final int EVERYTHING_TINYGRASS_FRAME = 38;

    
    /***** EVERYUI *****/
    
    public static final float EVERYUI_ORIGIN_X = 14;
    public static final float EVERYUI_ORIGIN_Y = 14;
    public static final int EVERYUI_ATK_NORMAL_FRAME = 12;
    public static final int EVERYUI_ATK_DISABLED_FRAME = 13;
    public static final int EVERYUI_BEAN_NORMAL_FRAME = 0;
    public static final int EVERYUI_BEAN_GOLDEN_FRAME = 1;
    public static final int EVERYUI_BLOOD_CHECK_FRAME = 14;
    public static final int EVERYUI_LIFE_NORMAL_FRAME = 8;
    public static final int EVERYUI_LIFE_DANGER_FRAMES_START = 9;
    public static final int EVERYUI_LIFE_DANGER_FRAMES_LAST = 10;
    public static final int EVERYUI_LIFE_DISABLED_FRAME = 11;
    public static final int EVERYUI_TARGET_FRAMES_START = 2;
    public static final int EVERYUI_TARGET_FRAMES_END = 3;
    public static final int EVERYUI_TIME_PLAY_FRAMES_START = 4;
    public static final int EVERYUI_TIME_PLAY_FRAMES_LAST = 5;
    public static final int EVERYUI_TIME_PAUSE_FRAMES_START = 6;
    public static final int EVERYUI_TIME_PAUSE_FRAMES_LAST = 7;
    
    
    /***** MENU CURSOR *****/
    
    public static final int MENU_CURSOR_ORIGIN_X = 12;
    public static final int MENU_CURSOR_ORIGIN_Y = 2;
    
    
    /***** PAD MENU *****/
    
    public static final int PAD_MENU_ORIGIN_X = 8;
    public static final int PAD_MENU_ORIGIN_Y = 8;
    
    
    /***** PIKE *****/
    
    public static final int PIKE_0DEG_FRAME = 0;
    public static final int PIKE_90DEG_FRAME = 4;
    
    
    /***** SHIELD *****/
    
    public static final int SHIELD_BACK_FRAME = 0;
    public static final int SHIELD_FRONT_FRAME = 4;
    // What to add to either SHIELD_FRAME_BACK or SHIELD_FRAME_FRONT to get the frame where the shield is on the ground.
    public static final int SHIELD_FALLEN_FRAME_INCREMENT = 3;
    
    
    /***** SLAPPER *****/
    
    public static final float SLAPPERBODY_HAND_OFFSET_Y = 5;
    public static final float SLAPPERBODY_AIM_OFFSET_Y = 7;
    public static final float SLAPPERBODY_WEAPON_OFFSET_Y = 3;
    public static final float SLAPPERBODY_SHIELD_OFFSET_Y = 1;
    public static final int SLAPPERBODY_IDLE_FRAME = 0;
    public static final int SLAPPERBODY_DEAD_FRAMES_START = 1;
    public static final int SLAPPERBODY_DEAD_FRAMES_LAST = 11;
    
    
    /***** SWORD *****/
    
    public static final int SWORD_VERTICAL_FRAME = 0;
    public static final int SWORD_HORIZONTAL_FRAME = 4;
    public static final int SWORD_FADED_FRAME = 5;
    
    
    /***** TARGET *****/
    
    public static final int TARGET_IDLE_FRAME = 0;
    public static final int TARGET_DAMAGED_FRAME = 1;
    public static final int TARGET_DEAD_FRAMES_START = 1;
    public static final int TARGET_DEAD_FRAMES_LAST = 5;
    
    
    /***** TINY GRASS *****/
    
    public static final int TINYGRASS_IDLE_FRAME = 0;
    public static final int TINYGRASS_FRAMES_START = 0;
    public static final int TINYGRASS_FRAMES_LAST = 3;
    public static final int TINYGRASS_FRAMES_COUNT = TINYGRASS_FRAMES_LAST + 1;
}