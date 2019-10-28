package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.BUTTON_C;
import net.ccat.tazs.resources.texts.BUTTON_PAD;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.tools.Performances;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
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
        Performances.onInit();
        
        TAZSGame game = mGame;
        
        game.centerCameraOn(0, 0);
        game.padMenuUI.clearChoices();
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, Texts.BATTLE_RETRY);
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, Texts.BATTLE_EXIT);
        game.unitsSystem.playerPadAngle = 0;
        game.unitsSystem.playerPadLength = 0;
        game.unitsSystem.playerPrimaryAction = false;
        game.unitsSystem.playerSecondaryAction = false;
    }
    
    public void update()
    {
        Performances.onUpdateStart();
        
        AdvancedHiRes16Color screen = mGame.screen;
        TAZSGame game = mGame;
        
        updatePlayerControl();
        Performances.onUpdatedPlayerControl();
        
        game.unitsSystem.onTick();
        Performances.onTickedUnitsSystem();
        
        checkGameRules();
        Performances.onCheckedGameRules();
        
        updateUI();
        mGame.battleMode.onBattleUpdateUI(game);
        Performances.onUpdatedUI();
            
        mGame.drawSceneBackground();
        Performances.onClearedScreen();
        
        mGame.unitsSystem.draw(screen);
        Performances.onDrawnUnitsSystem();
        
        renderUI();
        Performances.renderPerfBar(screen);
        Performances.onRenderedUI();
        
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
    
    private void updatePlayerControl()
    {
        TAZSGame game = mGame;
        
        game.unitsSystem.playerPadLength = 0;
        game.unitsSystem.playerPrimaryAction = false;
        game.unitsSystem.playerSecondaryAction = false;
        if (!game.padMenuUI.isFocused())
        {
            int controlledUnitIdentifier = game.unitsSystem.controlledUnitIdentifier;
        
            if ((controlledUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
                && (game.unitsSystem.unitsHealths[controlledUnitIdentifier] > 0))
            {
                int x = 0;
                int y = 0;
                float padLength = 0;
                float padAngle = game.unitsSystem.playerPadAngle;
                
                if (Button.Up.isPressed())
                    y--;
                if (Button.Down.isPressed())
                    y++;
                if (Button.Left.isPressed())
                    x--;
                if (Button.Right.isPressed())
                    x++;
                if ((x != 0) || (y != 0))
                {
                    padAngle = angleFromPad(x, y);
                    padLength = 1;
                }
                game.unitsSystem.playerPadLength = padLength;
                game.unitsSystem.playerPadAngle = padAngle;
                game.unitsSystem.playerPrimaryAction = Button.A.isPressed();
                game.unitsSystem.playerSecondaryAction = Button.B.isPressed();
            }
            else
                game.moveCameraWithPad();
        }
    }
    
    private void checkGameRules()
    {
        TAZSGame game = mGame;
        int winnerTeam = game.unitsSystem.winnerTeam();
        
        if (winnerTeam != Teams.TO_BE_DETERMINED)
        {
            game.unitsSystem.playerPadLength = 0;
            game.unitsSystem.playerPrimaryAction = false;
            game.unitsSystem.playerSecondaryAction = false;
            Game.changeState(new BattleResultPhaseState(game, winnerTeam));
        }
    }
    
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
        
        int controlledUnitIdentifier = game.unitsSystem.controlledUnitIdentifier;
            
        if ((controlledUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            && (game.unitsSystem.unitsHealths[controlledUnitIdentifier] > 0))
        {
            
            game.centerCameraSmoothlyOn(game.unitsSystem.unitsXs[controlledUnitIdentifier], game.unitsSystem.unitsYs[controlledUnitIdentifier]);
        }
    }
    
    private void renderUI()
    {
        TAZSGame game = mGame;
        AdvancedHiRes16Color screen = game.screen;
        
        screen.fillRect(0, Dimensions.HELPBAR_BOX_MIN_Y, Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT - Dimensions.HELPBAR_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextColor(Colors.HELP_ACTIVE);
        screen.setTextPosition(Dimensions.HELPBAR_X, Dimensions.HELPBAR_Y);
        if (game.padMenuUI.isShown())
        {
            screen.printPText(BUTTON_PAD.bin());
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.MENU_COMMANDS_HELP);
        }
        else
        {
            screen.printPText(BUTTON_C.bin());
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.MENU);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Runtime.getRuntime().freeMemory());
        }

        game.padMenuUI.draw(screen);
        game.topBarUI.draw(game.everyUISprite, screen);
        game.drawUnitUI(game.unitsSystem.controlledUnitIdentifier);
    }
    
    private float angleFromPad(int x, int y)
    {
        if (x > 0)
        {
            if (y > 0)
                return MathTools.PI_1_4;
            else if (y < 0)
                return -MathTools.PI_1_4;
            else // y == 0
                return 0;
        }
        else if (x < 0)
        {
            if (y > 0)
                return MathTools.PI_3_4;
            else if (y < 0)
                return -MathTools.PI_3_4;
            else // y == 0
                return Math.PI;
        }
        else // x == 0
        {
            if (y > 0)
                return MathTools.PI_1_2;
            else if (y < 0)
                return -MathTools.PI_1_2;
            else // y == 0
                return 0;
        }
    }
    
    private TAZSGame mGame;
}