package net.ccat.tazs.resources;


/**
 * Gathers all Dimensions, mostly related to UI.
 */
public class Dimensions
{
    /***** CONTROLLED UNIT *****/
    
    public static final int CONTROLLED_UNIT_ATK_X = SCREEN_WIDTH - 2;
    public static final int CONTROLLED_UNIT_ATK_Y = TOPBAR_HEIGHT + 1;
    public static final int CONTROLLED_UNIT_LIFEBAR_X = 17;
    public static final int CONTROLLED_UNIT_LIFEBAR_Y = TOPBAR_HEIGHT + 2;
    public static final int CONTROLLED_UNIT_LIFE_X = 1;
    public static final int CONTROLLED_UNIT_LIFE_Y = TOPBAR_HEIGHT + 1;
    public static final int CONTROLLED_UNIT_LIFEBAR_HEALTH_DIVIDER = 5;
    public static final int CONTROLLED_UNIT_LIFEBAR_INSIDE_HEIGHT = 3;
    
    
    /***** HELP BAR *****/
    
    public static final int HELPBAR_BOX_MIN_Y = 176 - 2 - 6 - 2;
    public static final int HELPBAR_X = 2;
    public static final int HELPBAR_Y = HELPBAR_BOX_MIN_Y + 2;
    
    
    /***** PAD MENU *****/
    
    public static final int PADMENU_X = SCREEN_WIDTH_2;
    public static final int PADMENU_Y = SCREEN_HEIGHT_2;
    
    
    /***** PREPARATION *****/
    
    public static final int PREPARATION_CURSOR_MIN_X = -105;
    public static final int PREPARATION_CURSOR_MIN_Y = -75;
    public static final int PREPARATION_CURSOR_MAX_X = 105;
    public static final int PREPARATION_CURSOR_MAX_Y = 75;
    public static final int PREPARATION_AREA_LINE_SPACE = 16;
    
    
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
    public static final int SCREEN_WIDTH_2 = SCREEN_WIDTH / 2;
    public static final int SCREEN_HEIGHT_2 = SCREEN_HEIGHT / 2;
    
    
    /***** TIME INDICATOR *****/
    
    public static final int TIME_ICON_X = 3;
    public static final int TIME_ICON_Y = HELPBAR_BOX_MIN_Y - 2 - 5 + 2;
    public static final int TIME_TEXT_X = TIME_ICON_X + 5;
    public static final int TIME_TEXT_Y = HELPBAR_BOX_MIN_Y - 2 - 5;
    
    
    /***** TITLE *****/
    
    public static final int TITLE_CURSOR_Y_SPEED = 4;
    public static final int TITLE_MENU_ENTRY_X = 20;
    public static final int TITLE_MENU_ENTRY_CURSOR_X = TITLE_MENU_ENTRY_X - VideoConstants.MENU_CURSOR_ORIGIN_X;
    public static final int TITLE_MENU_ENTRY_CHECK_X = TITLE_MENU_ENTRY_X - 18;
    public static final int TITLE_MENU_ENTRY_Y_START = 100;
    public static final int TITLE_MENU_ENTRY_HEIGHT = 8;
    public static final int TITLE_SUBTITLE_Y = 87;
    public static final int TITLE_VERSION_X = 1;
    public static final int TITLE_VERSION_Y = SCREEN_HEIGHT - 6;
    
    public static final int TITLE_LOGO_ZOMBIE_X = 98;
    public static final int TITLE_LOGO_ZOMBIE_Y = 42;
    public static final int TITLE_LOGO_ZOMBIE_XMIN = 32;
    public static final int TITLE_LOGO_ZOMBIE_YMIN = 19;
    public static final int TITLE_LOGO_ZOMBIE_WIDTH = 134;
    public static final int TITLE_LOGO_ZOMBIE_HEIGHT = 46;
    
    public static final int TITLE_LOGO_TOTALLY_X = 93;
    public static final int TITLE_LOGO_TOTALLY_Y_START = -50;
    public static final int TITLE_LOGO_TOTALLY_Y_FINAL = 16;
    
    public static final int TITLE_LOGO_SIMULATOR_Y = 69;
    public static final int TITLE_LOGO_SIMULATOR_X_START = 300;
    public static final int TITLE_LOGO_SIMULATOR_X_FINAL = 134;
    
    
    /***** TOP BAR *****/
    
    public static final int TOPBAR_HEIGHT = 17;
    public static final int TOPBAR_MARGIN = 2;
    public static final int TOPBAR_PRIMARYLINE_Y_OFFSET = 2;
    public static final int TOPBAR_BAR_Y_OFFSET = 8;
    public static final int TOPBAR_SECONDARYLINE_Y_OFFSET = 10;
    public static final int TOPBAR_BAR_LENGTH_MAX = 107;
    
    
    /***** UNITS *****/
    
    public static final int UNIT_CONTROL_RADIUS = 10;
    public static final float UNIT_CONTROL_DIRECTION_LENGTH = 12.5f;
    
    
    /***** UNIT BOX *****/
    
    public static final int UNITBOX_WIDTH = 50;
    public static final int UNITBOX_HEIGHT = 10;
    public static final int UNITBOX_X = SCREEN_WIDTH - UNITBOX_WIDTH;
    public static final int UNITBOX_Y = SCREEN_HEIGHT - UNITBOX_HEIGHT;
    public static final int UNITBOX_UNIT_X = SCREEN_WIDTH - 8;
    public static final int UNITBOX_UNIT_Y = SCREEN_HEIGHT - 5;
    
    
    /***** WINDOW *****/
    
    public static final int WINDOW_PADDING = 1;
}