package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.resources.sprites.ResultSummarySprite;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.PadMenuUI;
import net.ccat.tazs.ui.UITools;


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
        TAZSGame game = mGame;
        
        if (mWinnerTeam == Teams.PLAYER)
            mSummarySprite.playVictory();
        else if (mWinnerTeam == Teams.ENEMY)
            mSummarySprite.playDefeat();
        else
            mSummarySprite.playDraw();
        game.padMenuUI.clearChoices();
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, Texts.RESULT_RETRY);
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, Texts.RESULT_EXIT);
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        mGame.unitsSystem.onTick();
        updateUI();

        mGame.drawSceneBackground();
        mGame.unitsSystem.draw(screen);
        renderUI();
        screen.flush();
    }
    
    public void shutdown()
    {
        mGame = null;
    }
 
 
    /***** PRIVATE *****/
    
    private void updateUI()
    {
        TAZSGame game = mGame;
        
        if (game.padMenuUI.update())
        {
            int selectedChoice = game.padMenuUI.selectedChoice();
            
            if (selectedChoice == PadMenuUI.CHOICE_UP)
            {
                game.cursorCancelSound.play();
                game.battleMode.onResultRetry(game);
                game.padMenuUI.hideUntilNextPress();
            }
            else if (selectedChoice == PadMenuUI.CHOICE_DOWN)
            {
                game.cursorCancelSound.play();
                game.battleMode.onResultExit(game);
                game.padMenuUI.hideUntilNextPress();
            }
            game.battleMode.onPreparationMenuUpdate(game);
        }
        mLogoY = Math.min(mLogoY + Dimensions.RESULT_LOGO_Y_SPEED, Dimensions.RESULT_LOGO_Y_FINAL);
        if (Button.B.isPressed())
            mStatsY = Math.max(mStatsY - Dimensions.RESULT_STATS_Y_SPEED, Dimensions.RESULT_STATS_Y_VISIBLE);
        else
            mStatsY = Math.min(mStatsY + Dimensions.RESULT_STATS_Y_SPEED, Dimensions.RESULT_STATS_Y_HIDDEN);
        UITools.resetJustPressed();
    }
    
    private void renderUI()
    {
        HiRes16Color screen = mGame.screen;
        TAZSGame game = mGame;

        // Summary logo
        screen.drawRect(Dimensions.RESULT_LOGO_X, mLogoY, Dimensions.RESULT_LOGO_WIDTH, Dimensions.RESULT_LOGO_HEIGHT, Colors.WINDOW_BORDER);
        screen.fillRect(Dimensions.RESULT_LOGO_X + 1, mLogoY + 1, Dimensions.RESULT_LOGO_WIDTH - 1, Dimensions.RESULT_LOGO_HEIGHT - 1, Colors.WINDOW_BACKGROUND);
        mSummarySprite.draw(screen, Dimensions.RESULT_LOGO_X + 2, mLogoY + 2);
        
        game.topBarUI.draw(screen);
        
        // Stats screen.
        
        screen.drawRect(Dimensions.RESULT_STATS_X, mStatsY, Dimensions.RESULT_STATS_WIDTH, Dimensions.RESULT_STATS_HEIGHT, Colors.WINDOW_BORDER);
        screen.fillRect(Dimensions.RESULT_STATS_X + 1, mStatsY + 1, Dimensions.RESULT_STATS_WIDTH - 1, Dimensions.RESULT_STATS_HEIGHT - 1, Colors.WINDOW_BACKGROUND);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, mStatsY + Dimensions.RESULT_STATS_TEAMNAME_Y_OFFSET);
        screen.print(Texts.TEAMS_PLAYER);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, mStatsY + Dimensions.RESULT_STATS_TEAMNAME_Y_OFFSET);
        screen.print(Texts.TEAMS_ENEMY);
        
        screen.setTextPosition(Dimensions.RESULT_STATS_LABEL_X, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET);
        screen.print(Texts.RESULT_COST_);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET);
        renderStatBar(mPlayerUnitsCost, mPlayerUnitsCost + mEnemyUnitsCost,
                      Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, Dimensions.RESULT_STATS_TEAMS_FIRST_X_LAST, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET,
                        Colors.TEAM_PLAYER_STAT, screen);
        screen.print(mPlayerUnitsCost);
        screen.print(Texts.MISC_DOLLAR);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET);
        renderStatBar(mEnemyUnitsCost, mPlayerUnitsCost + mEnemyUnitsCost,
                      Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, Dimensions.RESULT_STATS_TEAMS_SECOND_X_LAST, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET,
                        Colors.TEAM_ENEMY_STAT, screen);
        screen.print(mEnemyUnitsCost);
        screen.print(Texts.MISC_DOLLAR);
        
        screen.setTextPosition(Dimensions.RESULT_STATS_LABEL_X, mStatsY + Dimensions.RESULT_STATS_DESTRUCTIONS_Y_OFFSET);
        screen.print(Texts.RESULT_DESTRUCTIONS_);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, mStatsY + Dimensions.RESULT_STATS_DESTRUCTIONS_Y_OFFSET);
        screen.print(Texts.MISC_UNKNOWN);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, mStatsY + Dimensions.RESULT_STATS_DESTRUCTIONS_Y_OFFSET);
        screen.print(Texts.MISC_UNKNOWN);
        
        screen.setTextPosition(Dimensions.RESULT_STATS_LABEL_X, mStatsY + Dimensions.RESULT_STATS_LOSSES_Y_OFFSET);
        screen.print(Texts.RESULT_LOSSES_);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, mStatsY + Dimensions.RESULT_STATS_LOSSES_Y_OFFSET);
        renderStatBar(mPlayerLosses, mPlayerLosses + mEnemyLosses,
                      Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, Dimensions.RESULT_STATS_TEAMS_FIRST_X_LAST, mStatsY + Dimensions.RESULT_STATS_LOSSES_Y_OFFSET,
                      Colors.TEAM_PLAYER_STAT, screen);
        screen.print(mPlayerLosses);
        renderStatBar(mEnemyLosses, mPlayerLosses + mEnemyLosses,
                      Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, Dimensions.RESULT_STATS_TEAMS_SECOND_X_LAST, mStatsY + Dimensions.RESULT_STATS_LOSSES_Y_OFFSET,
                      Colors.TEAM_ENEMY_STAT, screen);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, mStatsY + Dimensions.RESULT_STATS_LOSSES_Y_OFFSET);
        screen.print(mEnemyLosses);
        
        screen.fillRect(0, Dimensions.HELPBAR_BOX_MIN_Y, Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT - Dimensions.HELPBAR_BOX_MIN_Y, Colors.HELP_BG);
        
        screen.setTextPosition(Dimensions.HELPBAR_X, Dimensions.HELPBAR_Y);
        if (game.padMenuUI.isShown())
        {
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.print(Texts.BUTTON_PAD);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.MENU_COMMANDS_HELP);
        }
        else
        {
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.print(Texts.BUTTON_B);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.RESULT_STATS);
            screen.print(Texts.MISC_BIG_SEPARATOR);
            screen.print(Texts.BUTTON_C);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.MENU);
        }
        
        game.padMenuUI.draw(screen);
    }
    
    
    private void renderStatBar(int value, int valueMax,
                               int xMin, int xMax, int y,
                               int color,
                               HiRes16Color screen)
    {
        int barXMax = MathTools.lerpi(value, 0, xMin, valueMax, xMax);
        
        screen.fillRect(xMin, y + Dimensions.RESULT_STATS_BAR_Y_OFFSET, barXMax - xMin + 2, Dimensions.RESULT_STATS_BAR_THICKNESS, color);
    }
    
    
    private TAZSGame mGame;
    private int mWinnerTeam;
    private int mPlayerLosses;
    private int mPlayerUnitsCount;
    private int mPlayerUnitsCost;
    private int mEnemyLosses;
    private int mEnemyUnitsCount;
    private int mEnemyUnitsCost;
    private int mLogoY = Dimensions.RESULT_LOGO_Y_INITIAL;
    private int mStatsY = Dimensions.RESULT_STATS_Y_HIDDEN;
    private ResultSummarySprite mSummarySprite = new ResultSummarySprite();
}