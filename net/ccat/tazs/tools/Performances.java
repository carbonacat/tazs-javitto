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

package net.ccat.tazs.tools;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * A Class that provides performances measurement.
 */
public class Performances
{
    /***** LIFECYCLE *****/
    
    /**
     * Called on State's init().
     */
    public static void onInit()
    {
        mStartMillis = System.currentTimeMillis();
        mLastMeasureMillis = mStartMillis;
        
        mOutsideMillis = 0;
        mPlayerControlUpdatingMillis = 0;
        mUnitsSystemTickingMillis = 0;
        mGameRulesChecking = 0;
        mUIUpdatingMillis = 0;
        mScreenClearingMillis = 0;
        mUnitsDrawingMillis = 0;
        mUIRenderingMillis = 0;
        mScreenFlushingMillis = 0;
        mFrameMillis = 0;
    }
    
    /**
     * Called immediately at the begining of State's update().
     */
    public static void onUpdateStart()
    {
        mOutsideMillis = measureMillis(mOutsideMillis);
    }
    
    /**
     * Called after the Player Controls were updated.
     */
    public static void onUpdatedPlayerControl()
    {
        mPlayerControlUpdatingMillis = measureMillis(mPlayerControlUpdatingMillis);
    }
    
    /**
     * Called after the Units System was ticked.
     */
    public static void onTickedUnitsSystem()
    {
        mUnitsSystemTickingMillis = measureMillis(mUnitsSystemTickingMillis);
    }
    
    /**
     * Called after the Game Rules were checked.
     */
    public static void onCheckedGameRules()
    {
        mGameRulesChecking = measureMillis(mGameRulesChecking);
    }
    
    /**
     * Called after the UI was updated (vanilla + BattleMode's onBattleUpdate()).
     */
    public static void onUpdatedUI()
    {
        mUIUpdatingMillis = measureMillis(mUIUpdatingMillis);
    }
    
    /**
     * Called after the Screen was cleared.
     */
    public static void onClearedScreen()
    {
        mScreenClearingMillis = measureMillis(mScreenClearingMillis);
    }
    
    /**
     * Called after the Units were drawn.
     */
    public static void onDrawnUnitsSystem()
    {
        mUnitsDrawingMillis = measureMillis(mUnitsDrawingMillis);
    }
    
    /**
     * Called after the UI was rendered.
     * Note that it's best to call it after rendering the Performance Bar.
     */
    public static void onRenderedUI()
    {
        mUIRenderingMillis = measureMillis(mUIRenderingMillis);
    }
    
    /**
     * Called after the screen was flushed.
     */
    public static void onFlushedScreen()
    {
        mScreenFlushingMillis = measureMillis(mScreenFlushingMillis);
    }
    
    /**
     * Called at the end of a State's update().
     */
    public static void onUpdateEnd()
    {
        long nowMillis = System.currentTimeMillis();
        int frameMillis = 0;
        long frameEndMillis = mStartMillis + FRAME_MILLIS;
        
        while (nowMillis < frameEndMillis)
            nowMillis = System.currentTimeMillis();
        // Smoothing out that stat.
        mFrameMillis = (mFrameMillis * STATS_SMOOTHING_MULTIPLIER + (nowMillis - mStartMillis) * STATS_MILLIS_MULTIPLIER + STATS_SMOOTHING_MULTIPLIER) / STATS_SMOOTHING_DIVIDER;
        mStartMillis = nowMillis;
        mLastMeasureMillis = nowMillis;
    }
    
    /**
     * Called at the end of a State's shutdown().
     */
    public static void onShutdown()
    {
        mLastMeasureMillis = 0;
        mStartMillis = 0;
        
        mOutsideMillis = 0;
        mPlayerControlUpdatingMillis = 0;
        mUnitsSystemTickingMillis = 0;
        mGameRulesChecking = 0;
        mUIUpdatingMillis = 0;
        mScreenClearingMillis = 0;
        mUnitsDrawingMillis = 0;
        mUIRenderingMillis = 0;
        mScreenFlushingMillis = 0;
        mFrameMillis = 0;
    }
    
    
    /***** RENDERING *****/
    
    /**
     * Renders the Performances as a bar on the bottom of the screen.
     */
    public static void renderPerfBar(AdvancedHiRes16Color screen)
    {
        if (mFrameMillis > 0)
        {
            int x = 0;
            
            x = renderStatBar(x, mOutsideMillis, Colors.DEBUG_STAT_OUTSIDE, screen);
            x = renderStatBar(x, mPlayerControlUpdatingMillis, Colors.DEBUG_STAT_CONTROLSUPDATING, screen);
            x = renderStatBar(x, mUnitsSystemTickingMillis, Colors.DEBUG_STAT_UNITSTICKING, screen);
            x = renderStatBar(x, mGameRulesChecking, Colors.DEBUG_STAT_GAMERULESCHECKING, screen);
            x = renderStatBar(x, mUIUpdatingMillis, Colors.DEBUG_STATS_UIUPDATING, screen);
            x = renderStatBar(x, mScreenClearingMillis, Colors.DEBUG_STAT_SCREENCLEARING, screen);
            x = renderStatBar(x, mUnitsDrawingMillis, Colors.DEBUG_STAT_UNITSDRAWING, screen);
            x = renderStatBar(x, mUIRenderingMillis, Colors.DEBUG_STATS_UIRENDERING, screen);
            x = renderStatBar(x, mScreenFlushingMillis, Colors.DEBUG_STATS_SCREENFLUSHING, screen);
            screen.drawVLine(x, Dimensions.SCREEN_HEIGHT - 2, 2, Colors.DEBUG_BAR_SEPARATOR);
        }
    }
    
    
    /***** PRIVATE *****/
    
    /**
     * @param previousValue The previous value, for smoothing it out.
     * @return the number of ellapsed milliseconds since the last call.
     * Cannot return more than 32767.
     */
    private static short measureMillis(short previousValue)
    {
        long nowMillis = System.currentTimeMillis();
        short elapsedMillis = (short)(nowMillis - mLastMeasureMillis) * STATS_MILLIS_MULTIPLIER;
        
        mLastMeasureMillis = nowMillis;
        if (elapsedMillis < 0)
            elapsedMillis = 0;
        // +1 for helping having a more fair integer division.
        return (previousValue * STATS_SMOOTHING_MULTIPLIER + elapsedMillis + STATS_SMOOTHING_MULTIPLIER) / STATS_SMOOTHING_DIVIDER;
    }
    
    /**
     * Renders a single stat.
     * 
     * @param x Where to draw the bar.
     * @param statMillis The stat's value.
     * @param barColor The color for the stat's bar.
     * @param screen The target screen.
     * @return The next value for x.
     */
    private static int renderStatBar(int x, short statMillis, int barColor,
                                     AdvancedHiRes16Color screen)
    {
        final int y = Dimensions.SCREEN_HEIGHT - 1;
        final int availableWidth = Dimensions.SCREEN_WIDTH - STATS_COUNT;
        final int barWidth = MathTools.lerpi(statMillis, 0, 0, mFrameMillis, availableWidth);
        
        screen.drawVLine(x, y - 1, 2, Colors.DEBUG_BAR_SEPARATOR);
        x++;
        screen.drawHLine(x, y, barWidth, barColor);
        x += barWidth;
        return x;
    }
    
    private static long mStartMillis;
    private static long mLastMeasureMillis;
    
    private static short mOutsideMillis;
    private static short mPlayerControlUpdatingMillis;
    private static short mUnitsSystemTickingMillis;
    private static short mGameRulesChecking;
    private static short mUIUpdatingMillis;
    private static short mScreenClearingMillis;
    private static short mUnitsDrawingMillis;
    private static short mUIRenderingMillis;
    private static short mScreenFlushingMillis;
    private static short mFrameMillis;
    
    private static final int FPS_LIMIT = 30;
    private static final int FRAME_MILLIS = 1000 / FPS_LIMIT;
    private static final int STATS_COUNT = 10;
    // Helps smoothing things.
    private static final int STATS_MILLIS_MULTIPLIER = FPS_LIMIT;
    private static final int STATS_SMOOTHING_MULTIPLIER = 7;
    private static final int STATS_SMOOTHING_DIVIDER = STATS_SMOOTHING_MULTIPLIER + 1;
}