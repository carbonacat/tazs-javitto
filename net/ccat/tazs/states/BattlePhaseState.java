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
import net.ccat.tazs.ui.PadMenuUI;
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
        TAZSGame game = mGame;
        
        game.screen.cameraX = -game.screen.width() * 0.5;
        game.screen.cameraY = -game.screen.height() * 0.5;
        game.padMenuUI.clearChoices();
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, Texts.BATTLE_RETRY);
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, Texts.BATTLE_EXIT);
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
        TAZSGame game = mGame;
        
        if (game.padMenuUI.update())
        {
            int selectedChoice = game.padMenuUI.selectedChoice();
            
            if (selectedChoice == PadMenuUI.CHOICE_UP)
            {
                game.cursorCancelSound.play();
                game.battleMode.onBattleRetry(game);
                game.padMenuUI.hideUntilNextPress();
            }
            else if (selectedChoice == PadMenuUI.CHOICE_DOWN)
            {
                game.cursorCancelSound.play();
                game.battleMode.onBattleExit(game);
                game.padMenuUI.hideUntilNextPress();
            }
            game.battleMode.onPreparationMenuUpdate(game);
        }
        UITools.resetJustPressed();
    }
    
    private void renderUI()
    {
        TAZSGame game = mGame;
        HiRes16Color screen = game.screen;
        
        screen.fillRect(0, Dimensions.HELPBAR_BOX_MIN_Y, Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT - Dimensions.HELPBAR_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextColor(Colors.HELP_INACTIVE);
        screen.setTextPosition(Dimensions.HELPBAR_X, Dimensions.HELPBAR_Y);
        screen.print(Texts.MISC_ERROR);

        game.padMenuUI.draw(screen);
        game.topBarUI.draw(screen);
    }
    
    private TAZSGame mGame;
}