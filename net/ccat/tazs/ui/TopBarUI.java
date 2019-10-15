package net.ccat.tazs.ui;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.Texts;


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
     * Sets the Right Team's name and summary.
     * @param teamName
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
        int width = Dimensions.SCREEN_WIDTH;
        int height = Dimensions.TOPBAR_HEIGHT;
        
        // Background and border.
        UITools.drawWindow(x, y, width, height, screen);
        
        // Texts.
        screen.setTextPosition(x + 2, y + 2);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.print(mLeftTopString);
        screen.setTextPosition(x + 2, y + 9);
        screen.print(mLeftBottomString);
        
        screen.setTextPosition(x + width - 2 - screen.textWidth(mRightTopString), y + 2);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.print(mRightTopString);
        screen.setTextPosition(x + width - 2 - screen.textWidth(mRightBottomString), y + 9);
        screen.print(mRightBottomString);
    }
    
    
    
    /***** PRIVATE *****/
    
    private String mLeftTopString = "";
    private String mLeftBottomString = "";
    private int mLeftBarMin = 1;
    private int mLeftBarMax = 1;
    private String mRightTopString = "";
    private String mRightBottomString = "";
}