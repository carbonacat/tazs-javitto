package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.BATTLE_EXIT;
import net.ccat.tazs.resources.texts.BATTLE_RETRY;
import net.ccat.tazs.resources.texts.BUTTON_C;
import net.ccat.tazs.resources.texts.BUTTON_PAD;
import net.ccat.tazs.resources.texts.MENU;
import net.ccat.tazs.resources.texts.MENU_COMMANDS_HELP;
import net.ccat.tazs.resources.texts.MISC_SEPARATOR;
import net.ccat.tazs.resources.texts.PERCENT;
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
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, BATTLE_RETRY.bin());
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, BATTLE_EXIT.bin());
        game.unitsSystem.playerPadAngle = 0;
        game.unitsSystem.playerPadLength = 0;
        game.unitsSystem.playerPrimaryAction = false;
        game.unitsSystem.playerSecondaryAction = false;
        System.gc();
    }
    
    public void update()
    {
        Performances.onUpdateStart();
        
        AdvancedHiRes16Color screen = mGame.screen;
        TAZSGame game = mGame;
        
        updatePlayerControl();
        Performances.onUpdatedPlayerControl();
        
        if (mUpdateRateByFrame > 0)
        {
            mUpdateCounter += mUpdateRateByFrame;
            while (mUpdateCounter > UPDATE_RATE_100)
            {
                mUpdateCounter -= UPDATE_RATE_100;
                game.unitsSystem.onTick();
            }
        }
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
            else if ((Button.A.isPressed()) && (Button.B.isPressed()))
            {
                if ((Button.Down.justPressed()) && (mUpdateRateByFrame > 0))
                    mUpdateRateByFrame = -mUpdateRateByFrame;
                if ((Button.Up.justPressed()) && (mUpdateRateByFrame < 0))
                    mUpdateRateByFrame = -mUpdateRateByFrame;
                if (Button.Left.justPressed())
                    mUpdateRateByFrame = previousUpdateRate(mUpdateRateByFrame);
                if (Button.Right.justPressed())
                    mUpdateRateByFrame = nextUpdateRate(mUpdateRateByFrame);
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
            game.centerCameraSmoothlyOn(game.unitsSystem.unitsXs[controlledUnitIdentifier], game.unitsSystem.unitsYs[controlledUnitIdentifier]);
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
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(MENU_COMMANDS_HELP.bin());
        }
        else
        {
            screen.printPText(BUTTON_C.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(MENU.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.print(Runtime.getRuntime().freeMemory());
        }

        game.padMenuUI.draw(screen);
        game.topBarUI.draw(game.everyUISprite, screen);
        game.drawUnitUI(game.unitsSystem.controlledUnitIdentifier);

        game.everyUISprite.setPosition(Dimensions.TIME_ICON_X - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.TIME_ICON_Y - VideoConstants.EVERYUI_ORIGIN_Y);
        if (mUpdateRateByFrame < 0)
            game.everyUISprite.selectFrame(UITools.blinkingValue() ? VideoConstants.EVERYUI_TIME_PAUSE_FRAMES_START : VideoConstants.EVERYUI_TIME_PAUSE_FRAMES_LAST);
        else
            game.everyUISprite.selectFrame(UITools.blinkingValue() ? VideoConstants.EVERYUI_TIME_PLAY_FRAMES_START : VideoConstants.EVERYUI_TIME_PLAY_FRAMES_LAST);
        game.everyUISprite.draw(screen);
        if (mUpdateRateByFrame != UPDATE_RATE_DEFAULT)
        {
            int updateRateByFrame = Math.abs((int)mUpdateRateByFrame);
            
            screen.setTextPosition(Dimensions.TIME_TEXT_X, Dimensions.TIME_TEXT_Y);
            screen.setTextColor(Colors.TIME_TEXT_COLOR);
            if (updateRateByFrame > UPDATE_RATE_12)
                screen.print((updateRateByFrame * 100) / (int)UPDATE_RATE_DEFAULT);
            else
                screen.print((float)(100 * updateRateByFrame) / ((float)UPDATE_RATE_DEFAULT));
            screen.printPText(PERCENT.bin());
        }
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
    
    private static byte previousUpdateRate(byte updateRate)
    {
        if (updateRate < 0)
            return -previousUpdateRate(-updateRate);
        switch (updateRate)
        {
        case UPDATE_RATE_12:
            return UPDATE_RATE_12;
        case UPDATE_RATE_25:
            return UPDATE_RATE_12;
        case UPDATE_RATE_50:
            return UPDATE_RATE_25;
        case UPDATE_RATE_100:
            return UPDATE_RATE_50;
        case UPDATE_RATE_200:
            return UPDATE_RATE_100;
        case UPDATE_RATE_400:
            return UPDATE_RATE_200;
        case UPDATE_RATE_800:
            return UPDATE_RATE_400;
        }
        return UPDATE_RATE_DEFAULT;
    }
    private static byte nextUpdateRate(byte updateRate)
    {
        if (updateRate < 0)
            return -nextUpdateRate(-updateRate);
        switch (updateRate)
        {
        case UPDATE_RATE_12:
            return UPDATE_RATE_25;
        case UPDATE_RATE_25:
            return UPDATE_RATE_50;
        case UPDATE_RATE_50:
            return UPDATE_RATE_100;
        case UPDATE_RATE_100:
            return UPDATE_RATE_200;
        case UPDATE_RATE_200:
            return UPDATE_RATE_400;
        case UPDATE_RATE_400:
            return UPDATE_RATE_800;
        case UPDATE_RATE_800:
            return UPDATE_RATE_800;
        }
        return UPDATE_RATE_DEFAULT;
    }

    
    private TAZSGame mGame;
    private byte mUpdateRateByFrame = UPDATE_RATE_DEFAULT;
    private byte mUpdateCounter = 0;
    
    
    // 1 -> 6.25%
    // 2 -> 12.5%
    // 16 -> 100%
    // 64 -> 400%
    private static final byte UPDATE_RATE_12 = 1;
    private static final byte UPDATE_RATE_25 = 2;
    private static final byte UPDATE_RATE_50 = 4;
    private static final byte UPDATE_RATE_100 = 8;
    private static final byte UPDATE_RATE_200 = 16;
    private static final byte UPDATE_RATE_400 = 32;
    private static final byte UPDATE_RATE_800 = 64;
    private static final byte UPDATE_RATE_DEFAULT = UPDATE_RATE_100;
}