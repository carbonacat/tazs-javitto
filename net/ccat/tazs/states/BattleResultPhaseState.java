package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.resources.sprites.ResultSummarySprite;
import net.ccat.tazs.tools.MathTools;


/**
 * A Game Phase that shows the results of a Battle.
 */
public class BattleResultPhaseState
    extends State
{
    public BattleResultPhaseState(TAZSGame game, int winnerTeam)
    {
        mGame = game;
        mWinnerTeam = winnerTeam;
        mSummarySprite.setStatic(true);
        mPlayerLosses = game.unitsSystem.countDeadUnits(Teams.PLAYER);
        mPlayerUnitsCount = game.unitsSystem.unitsCount(Teams.PLAYER);
        mPlayerUnitsCost = game.unitsSystem.unitsCost(Teams.PLAYER);
        mEnemyLosses = game.unitsSystem.countDeadUnits(Teams.ENEMY);
        mEnemyUnitsCount = game.unitsSystem.unitsCount(Teams.ENEMY);
        mEnemyUnitsCost = game.unitsSystem.unitsCost(Teams.ENEMY);
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        mGame.screen.cameraX = -mGame.screen.width() * 0.5;
        mGame.screen.cameraY = -mGame.screen.height() * 0.5;
        if (mWinnerTeam == Teams.PLAYER)
            mSummarySprite.playVictory();
        else if (mWinnerTeam == Teams.ENEMY)
            mSummarySprite.playDefeat();
        else
            mSummarySprite.playDraw();
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        mGame.unitsSystem.onTick();
        
        if (Button.A.justPressed())
            Game.changeState(new TitleScreenState(mGame));
        
        mLogoY = Math.min(mLogoY + LOGO_Y_SPEED, LOGO_Y_FINAL);
        if (Button.B.isPressed())
            mStatsY = Math.max(mStatsY - STATS_Y_SPEED, STATS_Y_VISIBLE);
        else
            mStatsY = Math.min(mStatsY + STATS_Y_SPEED, STATS_Y_HIDDEN);

        screen.clear(Colors.SCENE_BG);
        mGame.unitsSystem.draw(screen);
        renderUI();
        screen.flush();
    }
    
    public void shutdown()
    {
        mGame = null;
    }
 
 
    /***** PRIVATE *****/
    
    private void renderUI()
    {
        HiRes16Color screen = mGame.screen;

        // Summary logo
        screen.drawRect(LOGO_X, mLogoY, LOGO_WIDTH, LOGO_HEIGHT, Colors.WINDOW_BORDER);
        screen.fillRect(LOGO_X + 1, mLogoY + 1, LOGO_WIDTH - 1, LOGO_HEIGHT - 1, Colors.WINDOW_BACKGROUND);
        mSummarySprite.draw(screen, LOGO_X + 2, mLogoY + 2);
        
        mGame.topBarUI.draw(screen);
        
        // Stats screen.
        
        screen.drawRect(STATS_X, mStatsY, STATS_WIDTH, STATS_HEIGHT, Colors.WINDOW_BORDER);
        screen.fillRect(STATS_X + 1, mStatsY + 1, STATS_WIDTH - 1, STATS_HEIGHT - 1, Colors.WINDOW_BACKGROUND);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.setTextPosition(STATS_TEAMS_FIRST_X_START, mStatsY + STATS_TEAMNAME_Y_OFFSET);
        screen.print(Texts.TEAMS_PLAYER);
        screen.setTextPosition(STATS_TEAMS_SECOND_X_START, mStatsY + STATS_TEAMNAME_Y_OFFSET);
        screen.print(Texts.TEAMS_ENEMY);
        
        screen.setTextPosition(STATS_LABEL_X, mStatsY + STATS_COST_Y_OFFSET);
        screen.print(Texts.RESULT_COST_);
        screen.setTextPosition(STATS_TEAMS_FIRST_X_START, mStatsY + STATS_COST_Y_OFFSET);
        renderStatBar(mPlayerUnitsCost, mPlayerUnitsCost + mEnemyUnitsCost,
                      STATS_TEAMS_FIRST_X_START, STATS_TEAMS_FIRST_X_LAST, mStatsY + STATS_COST_Y_OFFSET,
                        Colors.TEAM_PLAYER_STAT_COLOR, screen);
        screen.print(mPlayerUnitsCost);
        screen.print(Texts.MISC_DOLLAR);
        screen.setTextPosition(STATS_TEAMS_SECOND_X_START, mStatsY + STATS_COST_Y_OFFSET);
        renderStatBar(mEnemyUnitsCost, mPlayerUnitsCost + mEnemyUnitsCost,
                      STATS_TEAMS_SECOND_X_START, STATS_TEAMS_SECOND_X_LAST, mStatsY + STATS_COST_Y_OFFSET,
                        Colors.TEAM_ENEMY_STAT_COLOR, screen);
        screen.print(mEnemyUnitsCost);
        screen.print(Texts.MISC_DOLLAR);
        
        screen.setTextPosition(STATS_LABEL_X, mStatsY + STATS_DESTRUCTIONS_Y_OFFSET);
        screen.print(Texts.RESULT_DESTRUCTIONS_);
        screen.setTextPosition(STATS_TEAMS_FIRST_X_START, mStatsY + STATS_DESTRUCTIONS_Y_OFFSET);
        screen.print(Texts.MISC_UNKNOWN);
        screen.setTextPosition(STATS_TEAMS_SECOND_X_START, mStatsY + STATS_DESTRUCTIONS_Y_OFFSET);
        screen.print(Texts.MISC_UNKNOWN);
        
        screen.setTextPosition(STATS_LABEL_X, mStatsY + STATS_LOSSES_Y_OFFSET);
        screen.print(Texts.RESULT_LOSSES_);
        screen.setTextPosition(STATS_TEAMS_FIRST_X_START, mStatsY + STATS_LOSSES_Y_OFFSET);
        renderStatBar(mPlayerLosses, mPlayerLosses + mEnemyLosses,
                      STATS_TEAMS_FIRST_X_START, STATS_TEAMS_FIRST_X_LAST, mStatsY + STATS_LOSSES_Y_OFFSET,
                        Colors.TEAM_PLAYER_STAT_COLOR, screen);
        screen.print(mPlayerLosses);
        renderStatBar(mEnemyLosses, mPlayerLosses + mEnemyLosses,
                      STATS_TEAMS_SECOND_X_START, STATS_TEAMS_SECOND_X_LAST, mStatsY + STATS_LOSSES_Y_OFFSET,
                        Colors.TEAM_ENEMY_STAT_COLOR, screen);
        screen.setTextPosition(STATS_TEAMS_SECOND_X_START, mStatsY + STATS_LOSSES_Y_OFFSET);
        screen.print(mEnemyLosses);
        
        
        screen.fillRect(0, HELP_BOX_MIN_Y, mGame.screen.width(), mGame.screen.height() - HELP_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextColor(Colors.HELP_ACTIVE);
        screen.setTextPosition(HELP_X, HELP_Y);
        screen.print(Texts.BUTTON_A);
        screen.print(Texts.MISC_SEPARATOR);
        screen.print(Texts.RESULT_EXIT);
        screen.print(Texts.MISC_BIG_SEPARATOR);
        screen.print(Texts.BUTTON_B);
        screen.print(Texts.MISC_SEPARATOR);
        screen.print(Texts.RESULT_STATS);
    }
    
    
    private void renderStatBar(int value, int valueMax,
                               int xMin, int xMax, int y,
                               int color,
                               HiRes16Color screen)
    {
        int barXMax = MathTools.lerpi(value, 0, xMin, valueMax, xMax);
        
        screen.fillRect(xMin, y + STATS_BAR_Y_OFFSET, barXMax - xMin + 2, STATS_BAR_THICKNESS, color);
    }
    
    
    private TAZSGame mGame;
    private int mWinnerTeam;
    private int mPlayerLosses;
    private int mPlayerUnitsCount;
    private int mPlayerUnitsCost;
    private int mEnemyLosses;
    private int mEnemyUnitsCount;
    private int mEnemyUnitsCost;
    private int mLogoY = LOGO_Y_INITIAL;
    private int mStatsY = STATS_Y_HIDDEN;
    private ResultSummarySprite mSummarySprite = new ResultSummarySprite();
    
    // TODO: This is common to a lot of things. [012]
    private static final int HELP_BOX_MIN_Y = 176 - 2 - 6 - 2;
    private static final int HELP_X = 2;
    private static final int HELP_Y = HELP_BOX_MIN_Y + 2;
    
    private static final int LOGO_WIDTH = 54;
    private static final int LOGO_HEIGHT = 17;
    private static final int LOGO_Y_INITIAL = -LOGO_HEIGHT;
    private static final int LOGO_Y_FINAL = 27;
    private static final int LOGO_Y_SPEED = 2;
    private static final int LOGO_X = 83;
    
    private static final int STATS_WIDTH = 216;
    private static final int STATS_HEIGHT = 32;
    private static final int STATS_Y_HIDDEN = 176;
    private static final int STATS_Y_VISIBLE = 47;
    private static final int STATS_Y_SPEED = 4;
    private static final int STATS_X = 2;
    private static final int STATS_LABEL_X = 5;
    private static final int STATS_TEAMNAME_Y_OFFSET = 3;
    private static final int STATS_TEAMS_FIRST_X_START = 85;
    private static final int STATS_TEAMS_FIRST_X_LAST = 147;
    private static final int STATS_TEAMS_SECOND_X_START = 151;
    private static final int STATS_TEAMS_SECOND_X_LAST = 214;
    private static final int STATS_COST_Y_OFFSET = 10;
    private static final int STATS_DESTRUCTIONS_Y_OFFSET = 17;
    private static final int STATS_LOSSES_Y_OFFSET = 24;
    private static final int STATS_BAR_THICKNESS = 1;
    private static final int STATS_BAR_Y_OFFSET = 2;
}