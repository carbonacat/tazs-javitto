//
// Copyright (C) 2019 Carbonacat
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package net.ccat.tazs.ui;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


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
        mLeftTopString = teamName + Texts.MISC_SEPARATORX + count + (count == 1 ? Texts.UNIT_K_UNITX : Texts.UNIT_K_UNITSX);
        mLeftBottomString = "" + cost;
        mLeftBottomEndsWithBean = true;
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
        mRightTopString = teamName + Texts.MISC_SEPARATORX + count + (count == 1 ? Texts.UNIT_K_UNITX : Texts.UNIT_K_UNITSX);
        mRightBottomString = "" + cost;
        mRightBottomEndsWithBean = true;
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
        mRightBottomEndsWithBean = false;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(NonAnimatedSprite everyUISprite, AdvancedHiRes16Color screen)
    {
        int x = 0;
        int y = 0;
        
        // Background and border.
        screen.drawWindow(x, y, Dimensions.SCREEN_WIDTH, Dimensions.TOPBAR_HEIGHT);
        
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
        if (mLeftBottomEndsWithBean)
            screen.printBean(everyUISprite);
        
        screen.setTextPosition(rightX - screen.textWidth(mRightTopString), y + Dimensions.TOPBAR_PRIMARYLINE_Y_OFFSET);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.print(mRightTopString);
        screen.setTextPosition(rightX - screen.textWidth(mRightBottomString), y + Dimensions.TOPBAR_SECONDARYLINE_Y_OFFSET);
        if (mRightBottomEndsWithBean)
            screen.anticipateBean();
        screen.print(mRightBottomString);
        if (mRightBottomEndsWithBean)
            screen.printBean(everyUISprite);
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
    private boolean mLeftBottomEndsWithBean = false;
    private int mRightBarLength = 0;
    private String mRightTopString = "";
    private String mRightBottomString = "";
    private boolean mRightBottomEndsWithBean = false;
}