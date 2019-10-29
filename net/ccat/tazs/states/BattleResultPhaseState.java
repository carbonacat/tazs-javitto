package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.BUTTON_B;
import net.ccat.tazs.resources.texts.BUTTON_C;
import net.ccat.tazs.resources.texts.BUTTON_PAD;
import net.ccat.tazs.resources.texts.MENU;
import net.ccat.tazs.resources.texts.MENU_COMMANDS_HELP;
import net.ccat.tazs.resources.texts.MISC_BIG_SEPARATOR;
import net.ccat.tazs.resources.texts.MISC_SEPARATOR;
import net.ccat.tazs.resources.texts.MISC_UNKNOWN;
import net.ccat.tazs.resources.texts.RESULT_COST_;
import net.ccat.tazs.resources.texts.RESULT_DESTRUCTIONS_;
import net.ccat.tazs.resources.texts.RESULT_EXIT;
import net.ccat.tazs.resources.texts.RESULT_LOSSES_;
import net.ccat.tazs.resources.texts.RESULT_RETRY;
import net.ccat.tazs.resources.texts.RESULT_STATS;
import net.ccat.tazs.resources.texts.TEAMS_ENEMY;
import net.ccat.tazs.resources.texts.TEAMS_PLAYER;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.resources.sprites.ResultSummarySprite;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.tools.Performances;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
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
        Performances.onInit();
        
        TAZSGame game = mGame;
        
        if (mWinnerTeam == Teams.PLAYER)
            mSummarySprite.playVictory();
        else if (mWinnerTeam == Teams.ENEMY)
            mSummarySprite.playDefeat();
        else
            mSummarySprite.playDraw();
        game.padMenuUI.clearChoices();
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, RESULT_RETRY.bin());
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, RESULT_EXIT.bin());
        
        mGame.battleMode.onResultInit(mGame, mWinnerTeam);
        
        mStatsAreShown = true;
        mLastLosingDyingUnitIdentifier = game.unitsSystem.findUnitThatJustDied(Teams.oppositeTeam(mWinnerTeam));
    }
    
    public void update()
    {
        Performances.onUpdateStart();
        
        AdvancedHiRes16Color screen = mGame.screen;
        
        mGame.unitsSystem.onTick();
        updateUI();

        mGame.drawSceneBackground();
        mGame.unitsSystem.draw(screen);
        renderUI();
        
        screen.flush();
        Performances.onFlushedScreen();
        
        Performances.onUpdateEnd();
    }
    
    public void shutdown()
    {
        mGame = null;
        
        Performances.onShutdown();
    }
 
 
    /***** PRIVATE *****/
    
    private void updateUI()
    {
        TAZSGame game = mGame;
        
        if (game.padMenuUI.update())
        {
            int selectedChoice = game.padMenuUI.selectedChoice();
            
            if (!game.battleMode.onResultMenuChoice(game, selectedChoice))
            {
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
            }
                
            game.battleMode.onPreparationMenuUpdate(game);
        }
        else if (game.moveCameraWithPad())
            mLastLosingDyingUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
        else if (mLastLosingDyingUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            game.centerCameraSmoothlyOn(game.unitsSystem.unitsXs[mLastLosingDyingUnitIdentifier], game.unitsSystem.unitsYs[mLastLosingDyingUnitIdentifier]);
        mLogoY = Math.min(mLogoY + Dimensions.RESULT_LOGO_Y_SPEED, Dimensions.RESULT_LOGO_Y_FINAL);
        if (Button.B.justPressed())
            mStatsAreShown = !mStatsAreShown;
        if (mStatsAreShown)
            mStatsY = Math.max(mStatsY - Dimensions.RESULT_STATS_Y_SPEED, Dimensions.RESULT_STATS_Y_VISIBLE);
        else
            mStatsY = Math.min(mStatsY + Dimensions.RESULT_STATS_Y_SPEED, Dimensions.RESULT_STATS_Y_HIDDEN);
        UITools.resetJustPressed();
    }
    
    private void renderUI()
    {
        TAZSGame game = mGame;
        AdvancedHiRes16Color screen = game.screen;

        // Summary logo
        screen.drawRect(Dimensions.RESULT_LOGO_X, mLogoY, Dimensions.RESULT_LOGO_WIDTH, Dimensions.RESULT_LOGO_HEIGHT, Colors.WINDOW_BORDER);
        screen.fillRect(Dimensions.RESULT_LOGO_X + 1, mLogoY + 1, Dimensions.RESULT_LOGO_WIDTH - 1, Dimensions.RESULT_LOGO_HEIGHT - 1, Colors.WINDOW_BACKGROUND);
        mSummarySprite.draw(screen, Dimensions.RESULT_LOGO_X + 2, mLogoY + 2);
        
        game.topBarUI.draw(game.everyUISprite, screen);
        
        // Stats screen.
        
        screen.drawRect(Dimensions.RESULT_STATS_X, mStatsY, Dimensions.RESULT_STATS_WIDTH, Dimensions.RESULT_STATS_HEIGHT, Colors.WINDOW_BORDER);
        screen.fillRect(Dimensions.RESULT_STATS_X + 1, mStatsY + 1, Dimensions.RESULT_STATS_WIDTH - 1, Dimensions.RESULT_STATS_HEIGHT - 1, Colors.WINDOW_BACKGROUND);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, mStatsY + Dimensions.RESULT_STATS_TEAMNAME_Y_OFFSET);
        screen.printPText(TEAMS_PLAYER.bin());
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, mStatsY + Dimensions.RESULT_STATS_TEAMNAME_Y_OFFSET);
        screen.printPText(TEAMS_ENEMY.bin());
        
        screen.setTextPosition(Dimensions.RESULT_STATS_LABEL_X, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET);
        screen.printPText(RESULT_COST_.bin());
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET);
        renderStatBar(mPlayerUnitsCost, mPlayerUnitsCost + mEnemyUnitsCost,
                      Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, Dimensions.RESULT_STATS_TEAMS_FIRST_X_LAST, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET,
                        Colors.TEAM_PLAYER_STAT, screen);
        screen.print(mPlayerUnitsCost);
        screen.printBean(game.everyUISprite);
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET);
        renderStatBar(mEnemyUnitsCost, mPlayerUnitsCost + mEnemyUnitsCost,
                      Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, Dimensions.RESULT_STATS_TEAMS_SECOND_X_LAST, mStatsY + Dimensions.RESULT_STATS_COST_Y_OFFSET,
                        Colors.TEAM_ENEMY_STAT, screen);
        screen.print(mEnemyUnitsCost);
        screen.printBean(game.everyUISprite);
        
        screen.setTextPosition(Dimensions.RESULT_STATS_LABEL_X, mStatsY + Dimensions.RESULT_STATS_DESTRUCTIONS_Y_OFFSET);
        screen.printPText(RESULT_DESTRUCTIONS_.bin());
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_FIRST_X_START, mStatsY + Dimensions.RESULT_STATS_DESTRUCTIONS_Y_OFFSET);
        screen.printPText(MISC_UNKNOWN.bin());
        screen.setTextPosition(Dimensions.RESULT_STATS_TEAMS_SECOND_X_START, mStatsY + Dimensions.RESULT_STATS_DESTRUCTIONS_Y_OFFSET);
        screen.printPText(MISC_UNKNOWN.bin());
        
        screen.setTextPosition(Dimensions.RESULT_STATS_LABEL_X, mStatsY + Dimensions.RESULT_STATS_LOSSES_Y_OFFSET);
        screen.printPText(RESULT_LOSSES_.bin());
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
            screen.printPText(BUTTON_PAD.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(MENU_COMMANDS_HELP.bin());
        }
        else
        {
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.printPText(BUTTON_B.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(RESULT_STATS.bin());
            screen.printPText(MISC_BIG_SEPARATOR.bin());
            screen.printPText(BUTTON_C.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(MENU.bin());
        }
        
        game.padMenuUI.draw(screen);
        game.drawUnitUI(game.unitsSystem.controlledUnitIdentifier);
    }
    
    
    private void renderStatBar(int value, int valueMax,
                               int xMin, int xMax, int y,
                               int color,
                               AdvancedHiRes16Color screen)
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
    private int mLastLosingDyingUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
    private boolean mStatsAreShown = true;
    private boolean mWasPressingDirection = false;
    private ResultSummarySprite mSummarySprite = new ResultSummarySprite();
}