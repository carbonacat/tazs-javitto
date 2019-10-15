package net.ccat.tazs.resources;


/**
 * Gathers all Dimensions, mostly related to UI.
 */
public class Dimensions
{
    /***** HELP BAR *****/
    
    public static final int HELPBAR_BOX_MIN_Y = 176 - 2 - 6 - 2;
    public static final int HELPBAR_X = 2;
    public static final int HELPBAR_Y = HELPBAR_BOX_MIN_Y + 2;
    
    
    /***** PAD MENU *****/
    
    public static final int PADMENU_X = SCREEN_WIDTH / 2;
    public static final int PADMENU_Y = SCREEN_HEIGHT / 2;
    
    
    /***** RESULT PHASE *****/

    public static final int RESULT_LOGO_WIDTH = 54;
    public static final int RESULT_LOGO_HEIGHT = 17;
    public static final int RESULT_LOGO_Y_INITIAL = -RESULT_LOGO_HEIGHT;
    public static final int RESULT_LOGO_Y_FINAL = 27;
    public static final int RESULT_LOGO_Y_SPEED = 2;
    public static final int RESULT_LOGO_X = 83;
    
    public static final int RESULT_STATS_WIDTH = SCREEN_WIDTH - 4;
    public static final int RESULT_STATS_HEIGHT = 32;
    public static final int RESULT_STATS_Y_HIDDEN = SCREEN_HEIGHT;
    public static final int RESULT_STATS_Y_VISIBLE = 47;
    public static final int RESULT_STATS_Y_SPEED = 4;
    public static final int RESULT_STATS_X = 2;
    public static final int RESULT_STATS_LABEL_X = 5;
    public static final int RESULT_STATS_TEAMNAME_Y_OFFSET = 3;
    public static final int RESULT_STATS_TEAMS_SECOND_X_LAST = SCREEN_WIDTH - 6;
    public static final int RESULT_STATS_TEAMS_SECOND_X_START = RESULT_STATS_TEAMS_SECOND_X_LAST - 63;
    public static final int RESULT_STATS_TEAMS_FIRST_X_LAST = RESULT_STATS_TEAMS_SECOND_X_START - 4;
    public static final int RESULT_STATS_TEAMS_FIRST_X_START = RESULT_STATS_TEAMS_FIRST_X_LAST - 63;
    public static final int RESULT_STATS_COST_Y_OFFSET = 10;
    public static final int RESULT_STATS_DESTRUCTIONS_Y_OFFSET = 17;
    public static final int RESULT_STATS_LOSSES_Y_OFFSET = 24;
    public static final int RESULT_STATS_BAR_THICKNESS = 1;
    public static final int RESULT_STATS_BAR_Y_OFFSET = 2;    
    
    
    /***** SCREEN *****/
    
    public static final int SCREEN_WIDTH = 220;
    public static final int SCREEN_HEIGHT = 176;
    
    
    /***** TITLE *****/
    
    public static final int TITLE_CURSOR_Y_SPEED = 2;
    public static final int TITLE_MENU_ENTRY_X = 32;
    public static final int TITLE_MENU_ENTRY_CURSOR_X = TITLE_MENU_ENTRY_X - VideoConstants.MENU_CURSOR_ORIGIN_X;
    public static final int TITLE_MENU_ENTRY_Y_START = 64;
    public static final int TITLE_MENU_ENTRY_HEIGHT = 8;
    public static final int TITLE_SUBTITLE_Y = 48;
    public static final int TITLE_TITLE_Y = 32;
    public static final int TITLE_VERSION_X = 1;
    public static final int TITLE_VERSION_Y = SCREEN_HEIGHT - 6;
    
    
    /***** TOP BAR *****/
    
    public static final int TOPBAR_HEIGHT = 16;
    
    
    /***** UNIT BOX *****/
    
    public static final int UNITBOX_WIDTH = 50;
    public static final int UNITBOX_HEIGHT = 10;
    public static final int UNITBOX_X = SCREEN_WIDTH - UNITBOX_WIDTH;
    public static final int UNITBOX_Y = SCREEN_HEIGHT - UNITBOX_HEIGHT;
    public static final int UNITBOX_UNIT_X = SCREEN_WIDTH - 8;
    public static final int UNITBOX_UNIT_Y = SCREEN_HEIGHT - 5;
}