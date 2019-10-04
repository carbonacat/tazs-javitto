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
        
        // Stats screen.
        
        screen.drawRect(STATS_X, mStatsY, STATS_WIDTH, STATS_HEIGHT, Colors.WINDOW_BORDER);
        screen.fillRect(STATS_X + 1, mStatsY + 1, STATS_WIDTH - 1, STATS_HEIGHT - 1, Colors.WINDOW_BACKGROUND);
        
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
    
    private TAZSGame mGame;
    private int mWinnerTeam;
    private int mLogoY = LOGO_Y_INITIAL;
    private int mStatsY = STATS_Y_HIDDEN;
    private ResultSummarySprite mSummarySprite = new ResultSummarySprite();
    
    // TODO: This is common to a lot of things.
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
    private static final int STATS_HEIGHT = 47;
    private static final int STATS_Y_HIDDEN = 176;
    private static final int STATS_Y_VISIBLE = 47;
    private static final int STATS_Y_SPEED = 4;
    private static final int STATS_X = 2;
}