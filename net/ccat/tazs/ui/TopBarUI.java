package net.ccat.tazs.ui;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.tools.MathTools;


/**
 * The TopBar, showing information about the battle.
 */
public class TopBarUI
{
    /***** CONFIGURATION *****/
    
    /**
     * Sets the Left Team's name and its Unit count and full cost.
     * @param teamName
     * @param count
     * @param cost
     */
    public void setLeftCountAndCost(String teamName, int count, int cost)
    {
        mLeftTopString = teamName + Texts.MISC_SEPARATOR + count + (count == 1 ? Texts.UNIT_K_UNIT : Texts.UNIT_K_UNITS);
        mLeftBottomString = "" + cost + Texts.MISC_DOLLAR;
    }
    /**
     * Sets the Left Team's bar.
     * @param barValue
     * @param barMax
     */
    public void setLeftBar(int bar, int barMax)
    {
        mLeftBarLength = barFromRatio(bar, barMax);
    }
    
    /**
     * Sets the Right Team's name and its Unit count and full cost.
     * @param teamName
     * @param count
     * @param cost
     */
    public void setRightCountAndCost(String teamName, int count, int cost)
    {
        mRightTopString = teamName + Texts.MISC_SEPARATOR + count + (count == 1 ? Texts.UNIT_K_UNIT : Texts.UNIT_K_UNITS);
        mRightBottomString = "" + cost + Texts.MISC_DOLLAR;
    }
    
    /**
     * Sets the Right Team's bar.
     * @param barValue
     * @param barMax
     */
    public void setRightBar(int bar, int barMax)
    {
        mRightBarLength = barFromRatio(bar, barMax);
    }
    
    /**
     * Sets the Right Team's name and summary.
    //  * @param teamName
     * @param summary
     */
    public void setRightNameAndSummary(String teamName, String summary)
    {
        mRightTopString = teamName;
        mRightBottomString = summary;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(HiRes16Color screen)
    {
        int x = 0;
        int y = 0;
        
        // Background and border.
        UITools.drawWindow(x, y, Dimensions.SCREEN_WIDTH, Dimensions.TOPBAR_HEIGHT, screen);
        
        int leftX = x + Dimensions.TOPBAR_MARGIN;
        int rightX = x + Dimensions.SCREEN_WIDTH - Dimensions.TOPBAR_MARGIN;
        
        // Bars
        screen.drawHLine(leftX, y + Dimensions.TOPBAR_BAR_Y_OFFSET, Dimensions.TOPBAR_BAR_LENGTH_MAX, Colors.TEAM_STAT_BACKGROUND);
        screen.drawHLine(leftX, y + Dimensions.TOPBAR_BAR_Y_OFFSET, mLeftBarLength, Colors.TEAM_PLAYER_STAT);
        screen.drawHLine(rightX, y + Dimensions.TOPBAR_BAR_Y_OFFSET, -Dimensions.TOPBAR_BAR_LENGTH_MAX, Colors.TEAM_STAT_BACKGROUND);
        screen.drawHLine(rightX, y + Dimensions.TOPBAR_BAR_Y_OFFSET, -mRightBarLength, Colors.TEAM_ENEMY_STAT);
        
        // Texts.
        screen.setTextPosition(leftX, y + Dimensions.TOPBAR_PRIMARYLINE_Y_OFFSET);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.print(mLeftTopString);
        screen.setTextPosition(leftX, y + Dimensions.TOPBAR_SECONDARYLINE_Y_OFFSET);
        screen.print(mLeftBottomString);
        
        screen.setTextPosition(rightX - screen.textWidth(mRightTopString), y + Dimensions.TOPBAR_PRIMARYLINE_Y_OFFSET);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.print(mRightTopString);
        screen.setTextPosition(rightX - screen.textWidth(mRightBottomString), y + Dimensions.TOPBAR_SECONDARYLINE_Y_OFFSET);
        screen.print(mRightBottomString);
    }
    
    
    
    /***** PRIVATE *****/
    
    private int barFromRatio(int value, int max)
    {
        if (max == 0)
            return 0;
        return MathTools.clampi(MathTools.lerpi(value, 0, 0, max, Dimensions.TOPBAR_BAR_LENGTH_MAX), 0, Dimensions.TOPBAR_BAR_LENGTH_MAX);
    }
    
    private String mLeftTopString = "";
    private int mLeftBarLength = 0;
    private String mLeftBottomString = "";
    private int mRightBarLength = 0;
    private String mRightTopString = "";
    private String mRightBottomString = "";
}