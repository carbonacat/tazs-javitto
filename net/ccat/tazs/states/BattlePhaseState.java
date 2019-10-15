package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.ui.UITools;


/**
 * A Game Phase that shows each side battling to the other one to the death.
 */
public class BattlePhaseState
    extends State
{
    public BattlePhaseState(TAZSGame game)
    {
        mGame = game;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        mGame.screen.cameraX = -mGame.screen.width() * 0.5;
        mGame.screen.cameraY = -mGame.screen.height() * 0.5;
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        mGame.unitsSystem.onTick();
        
        int winnerTeam = mGame.unitsSystem.winnerTeam();
        
        if (winnerTeam != Teams.TO_BE_DETERMINED)
            Game.changeState(new BattleResultPhaseState(mGame, winnerTeam));
        
        updateUI();
            
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
    
    private void updateUI()
    {
        UITools.resetJustPressed();
    }
    
    private void renderUI()
    {
        HiRes16Color screen = mGame.screen;
        
        screen.fillRect(0, HELP_BOX_MIN_Y, mGame.screen.width(), mGame.screen.height() - HELP_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextColor(Colors.HELP_INACTIVE);
        screen.setTextPosition(HELP_X, HELP_Y);
        screen.print(Texts.MISC_ERROR);
        
        mGame.topBarUI.draw(screen);
    }
    
    private TAZSGame mGame;
    
    // TODO: This is common to a lot of things. [012]
    private static final int HELP_BOX_MIN_Y = 176 - 2 - 6 - 2;
    private static final int HELP_X = 2;
    private static final int HELP_Y = HELP_BOX_MIN_Y + 2;
}