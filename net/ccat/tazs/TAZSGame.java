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

package net.ccat.tazs;

import femto.font.TIC80;
import femto.input.Button;
import femto.sound.Mixer;

import net.ccat.tazs.battle.BattleMode;
import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.battle.UnitHandler;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.battle.UnitTypes;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.musics.Musics;
import net.ccat.tazs.resources.musics.MusicPlayerProcedural;
import net.ccat.tazs.resources.palettes.ModifiedNAJI16;
import net.ccat.tazs.resources.sounds.CursorCancelSound;
import net.ccat.tazs.resources.sounds.CursorMoveSound;
import net.ccat.tazs.resources.sounds.CursorSelectSound;
import net.ccat.tazs.resources.sounds.DammitSound;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.resources.sprites.everything.EverythingSprite;
import net.ccat.tazs.resources.sprites.everyui.EveryUISprite;
import net.ccat.tazs.resources.sprites.life.LifeSprite;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.save.SaveCookie;
import net.ccat.tazs.save.SaveStatus;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
import net.ccat.tazs.ui.PadMenuUI;
import net.ccat.tazs.ui.TopBarUI;
import net.ccat.tazs.ui.UIModes;
import net.ccat.tazs.ui.UITools;


/**
 * Contains the game elements.
 */
class TAZSGame
{
    public static final float CAMERA_SPEED_PER_TICK = 16.f;
    
    
    public TAZSGame()
    {
        screen = new AdvancedHiRes16Color(ModifiedNAJI16.palette(), TIC80.font());
        unitsSystem = new UnitsSystem(everythingSprite, everyUISprite);
        menuCursorSprite.playDefault();
        menuCursorSprite.setStatic(true);
        everyUISprite.setStatic(true);

        // Something went wrong if that went havoc!
        while (mAreaCoords.length != AREA_TEAMS_MAX * AREA_SIZE);
        
        Mixer.init(8000);
        
        music.play();
        if (cookie.getStatus() == SaveStatus.OK)
            music.playMusic(Musics.musicPointerForIdentifier(cookie.getMusicIdentifier()));
        else
            music.playMusic(Musics.musicPointerForIdentifier(Musics.MUSIC00));
    }
    
    
    /***** META GAME *****/
    
    SaveCookie cookie = new SaveCookie();
    
    
    /***** RENDERING *****/
    
    public AdvancedHiRes16Color screen;
    
    /**
     * Renders the scene's background.
     */
    public void drawSceneBackground()
    {
        final int drawWidth = Dimensions.SCREEN_WIDTH;
        final int drawHeight = Dimensions.HELPBAR_BOX_MIN_Y - Dimensions.TOPBAR_HEIGHT;
        final int drawY = Dimensions.TOPBAR_HEIGHT;
        
        float minX = screen.cameraX;
        float minY = screen.cameraY + drawY;
        float maxX = minX + drawWidth;
        float maxY = minY + drawHeight;
        float minCellX = minX - (float)(((int)minX) % SCENE_CELL_WIDTH + SCENE_CELL_WIDTH);
        float minCellY = minY - (float)(((int)minY) % SCENE_CELL_HEIGHT + SCENE_CELL_WIDTH);
        
        screen.fillRect(0, drawY, drawWidth, drawHeight, Colors.SCENE_BG, true);
        
        int frameBase = (System.currentTimeMillis() / SCENE_GRASS_MILLIS) % (VideoConstants.TINYGRASS_FRAMES_COUNT + SCENE_GRASS_FRAME_DELAY);
        
        if (frameBase > SCENE_GRASS_FRAME_DELAY)
            frameBase -= SCENE_GRASS_FRAME_DELAY;
        else
            frameBase = 0;
        frameBase += VideoConstants.EVERYTHING_TINYGRASS_FRAME;
        everythingSprite.setMirrored(false);
        everythingSprite.setFlipped(false);
        everythingSprite.selectFrame(frameBase);
        for (float cellX = minCellX; cellX < maxX + SCENE_CELL_WIDTH; cellX += SCENE_CELL_WIDTH)
            for (float cellY= minCellY; cellY < maxY + SCENE_CELL_HEIGHT; cellY += SCENE_CELL_HEIGHT)
            {
                everythingSprite.setPosition(cellX - VideoConstants.EVERYTHING_ORIGIN_X, cellY - VideoConstants.EVERYTHING_ORIGIN_Y);
                everythingSprite.draw(screen);
            }
    }
    
    
    /***** GAME *****/
    
    public UnitsSystem unitsSystem;
    public int focusedUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
    public BattleMode battleMode;

    /**
     * @param x
     * @param y
     * @return a Team in the area that contains the position, or Teams.NONE if none matched.
     */
    public int areaTeamAtPosition(int x, int y)
    {
        int areaIndex = 0;
        
        for (int team = 0; team < AREA_TEAMS_MAX; team++)
        {
            if ((x >= mAreaCoords[areaIndex + AREAS_MIN_X_OFFSET])
                && (y >= mAreaCoords[areaIndex + AREAS_MIN_Y_OFFSET])
                && (x <= mAreaCoords[areaIndex + AREAS_MAX_X_OFFSET])
                && (y <= mAreaCoords[areaIndex + AREAS_MAX_Y_OFFSET]))
                return team;
            areaIndex += AREA_SIZE;
        }
        return Teams.NONE;
    }
    
    /**
     * @param team The team identifier.
     * @return The min-x coordinate for the corresponding area.
     */
    public int areaMinX(int team)
    {
        return mAreaCoords[team * AREA_SIZE + AREAS_MIN_X_OFFSET];
    }
    /**
     * @param team The team identifier.
     * @return The min-y coordinate for the corresponding area.
     */
    public int areaMinY(int team)
    {
        return mAreaCoords[team * AREA_SIZE + AREAS_MIN_Y_OFFSET];
    }
    /**
     * @param team The team identifier.
     * @return The max-x coordinate for the corresponding area.
     */
    public int areaMaxX(int team)
    {
        return mAreaCoords[team * AREA_SIZE + AREAS_MAX_X_OFFSET];
    }
    /**
     * @param team The team identifier.
     * @return The max-y coordinate for the corresponding area.
     */
    public int areaMaxY(int team)
    {
        return mAreaCoords[team * AREA_SIZE + AREAS_MAX_Y_OFFSET];
    }
    
    
    /***** UI *****/
    
    public PadMenuUI padMenuUI = new PadMenuUI();
    public TopBarUI topBarUI = new TopBarUI();
    public int uiMode = UIModes.INVALID;
    public int currentUnitType = UnitTypes.BRAWLER;
    
    // Cursor's in-game coordinates.
    public float cursorX;
    public float cursorY;
    
    
    // TODO: Maybe not at the right place here.
    /**
     * Renders the Unit's UI if applicable.
     * @param unitIdentifier The Unit. IDENTIFIER_NONE won't do anything.
     */
    public void drawUnitUI(int unitIdentifier)
    {
        if (unitIdentifier != UnitsSystem.IDENTIFIER_NONE)
        {
            UnitHandler unitHandler = unitsSystem.unitsHandlers[unitIdentifier];
            int unitHealth = unitsSystem.unitsHealths[unitIdentifier];
            int unitStartingHealth = unitHandler.startingHealth();
            int borderColor = Colors.CONTROLLED_UNIT_LIFEBAR_BORDER;
            
            if (unitHealth <= 0)
            {
                everyUISprite.selectFrame(VideoConstants.EVERYUI_LIFE_DISABLED_FRAME);
                borderColor = Colors.CONTROLLED_UNIT_LIFEBAR_BORDER_DEAD;
            }
            else if (unitHealth < unitStartingHealth / 4)
            {
                everyUISprite.selectFrame(UITools.blinkingValue() ? VideoConstants.EVERYUI_LIFE_DANGER_FRAMES_LAST : VideoConstants.EVERYUI_LIFE_DANGER_FRAMES_START);
                borderColor = Colors.CONTROLLED_UNIT_LIFEBAR_BORDER_DANGER;
            }
            else
                everyUISprite.selectFrame(VideoConstants.EVERYUI_LIFE_NORMAL_FRAME);
            everyUISprite.setPosition(Dimensions.CONTROLLED_UNIT_LIFE_X - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.CONTROLLED_UNIT_LIFE_Y - VideoConstants.EVERYUI_ORIGIN_Y);
            everyUISprite.draw(screen);
            
            int barWidth = unitStartingHealth / Dimensions.CONTROLLED_UNIT_LIFEBAR_HEALTH_DIVIDER;
            int lifeBarWidth = MathTools.clampi(unitHealth / Dimensions.CONTROLLED_UNIT_LIFEBAR_HEALTH_DIVIDER, 0, barWidth);
            
            screen.drawVLine(Dimensions.CONTROLLED_UNIT_LIFEBAR_X, Dimensions.CONTROLLED_UNIT_LIFEBAR_Y + 1,
                             Dimensions.CONTROLLED_UNIT_LIFEBAR_INSIDE_HEIGHT, borderColor);
            screen.drawVLine(Dimensions.CONTROLLED_UNIT_LIFEBAR_X + barWidth + 1, Dimensions.CONTROLLED_UNIT_LIFEBAR_Y + 1,
                             Dimensions.CONTROLLED_UNIT_LIFEBAR_INSIDE_HEIGHT, borderColor);
            screen.drawHLine(Dimensions.CONTROLLED_UNIT_LIFEBAR_X + 1, Dimensions.CONTROLLED_UNIT_LIFEBAR_Y,
                             barWidth, borderColor);
            screen.drawHLine(Dimensions.CONTROLLED_UNIT_LIFEBAR_X + 1, Dimensions.CONTROLLED_UNIT_LIFEBAR_Y + Dimensions.CONTROLLED_UNIT_LIFEBAR_INSIDE_HEIGHT + 1,
                             barWidth, borderColor);
            screen.drawHLine(Dimensions.CONTROLLED_UNIT_LIFEBAR_X + 1, Dimensions.CONTROLLED_UNIT_LIFEBAR_Y + 1,
                             lifeBarWidth, Colors.CONTROLLED_UNIT_LIFEBAR_FILL_HIGHER);
            screen.drawHLine(Dimensions.CONTROLLED_UNIT_LIFEBAR_X + 1, Dimensions.CONTROLLED_UNIT_LIFEBAR_Y + 2,
                             lifeBarWidth, Colors.CONTROLLED_UNIT_LIFEBAR_FILL_MIDDLE);
            screen.drawHLine(Dimensions.CONTROLLED_UNIT_LIFEBAR_X + 1, Dimensions.CONTROLLED_UNIT_LIFEBAR_Y + 3,
                             lifeBarWidth, Colors.CONTROLLED_UNIT_LIFEBAR_FILL_LOWER);
                             
            if ((unitHealth <= 0) || (!unitHandler.isReadyToAttack(unitsSystem, unitIdentifier))) // TODO: Check for readiness.
                everyUISprite.selectFrame(VideoConstants.EVERYUI_ATK_DISABLED_FRAME);
            else
                everyUISprite.selectFrame(VideoConstants.EVERYUI_ATK_NORMAL_FRAME);
            everyUISprite.setPosition(Dimensions.CONTROLLED_UNIT_ATK_X - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.CONTROLLED_UNIT_ATK_Y - VideoConstants.EVERYUI_ORIGIN_Y);
            everyUISprite.draw(screen);
        }
    }
    
    
    /***** CAMERA *****/
    
    /**
     * Makes the camera follows smoothly the given coordinates.
     * 
     * @param x
     * @param y
     */
    public void centerCameraOn(float x, float y)
    {
        screen.cameraX = x - Dimensions.SCREEN_WIDTH_2;
        screen.cameraY = y - Dimensions.SCREEN_HEIGHT_2;
    }
    
    /**
     * Makes the camera follows smoothly the given coordinates.
     * 
     * @param x
     * @param y
     */
    public void centerCameraSmoothlyOn(float x, float y)
    {
        screen.cameraX = (screen.cameraX * CAMERA_FOLLOW_OLD_MULTIPLIER + (x - Dimensions.SCREEN_WIDTH_2) * CAMERA_FOLLOW_NEW_MULTIPLIER) / CAMERA_FOLLOW_DIVIDER;
        screen.cameraY = (screen.cameraY * CAMERA_FOLLOW_OLD_MULTIPLIER + (y - Dimensions.SCREEN_HEIGHT_2) * CAMERA_FOLLOW_NEW_MULTIPLIER) / CAMERA_FOLLOW_DIVIDER;
    }
    
    /**
     * Uses the pad to move the Camera around.
     * @return true if the Camera was moved in such way, false elsewhere.
     */
    public boolean moveCameraWithPad()
    {
        float cameraX = screen.cameraX + Dimensions.SCREEN_WIDTH_2;
        float cameraY = screen.cameraY + Dimensions.SCREEN_HEIGHT_2;
        boolean moved = false;
        
        if (Button.Up.isPressed())
            cameraY -= TAZSGame.CAMERA_SPEED_PER_TICK;
        if (Button.Down.isPressed())
            cameraY += TAZSGame.CAMERA_SPEED_PER_TICK;
        if (Button.Left.isPressed())
            cameraX -= TAZSGame.CAMERA_SPEED_PER_TICK;
        if (Button.Right.isPressed())
            cameraX += TAZSGame.CAMERA_SPEED_PER_TICK;
        centerCameraSmoothlyOn(cameraX, cameraY);
        return (Button.Up.isPressed() || Button.Down.isPressed() || Button.Left.isPressed() || Button.Right.isPressed());
    }
    
    
    /***** COMMON RESOURCES *****/
    
    public final NonAnimatedSprite everythingSprite = new EverythingSprite();
    public final NonAnimatedSprite everyUISprite = new EveryUISprite();
    public CursorSprite cursorSprite = new CursorSprite();
    public MenuCursorSprite menuCursorSprite = new MenuCursorSprite();
    public CursorMoveSound cursorMoveSound = new CursorMoveSound();
    public CursorSelectSound cursorSelectSound = new CursorSelectSound();
    public CursorCancelSound cursorCancelSound = new CursorCancelSound();
    public DammitSound dammitSound = new DammitSound(1);
    public MusicPlayerProcedural music = new MusicPlayerProcedural(3);
    
    
    /***** PRIVATE *****/
    
    public byte[] mAreaCoords = new byte[]
    {
        -100, -70, -5, 70,
        
        5, -70, 100, 70
    };
    
    private static final int AREA_TEAMS_MAX = 2;
    private static final int AREA_SIZE = 4;
    private static final int AREAS_MIN_X_OFFSET = 0;
    private static final int AREAS_MIN_Y_OFFSET = 1;
    private static final int AREAS_MAX_X_OFFSET = 2;
    private static final int AREAS_MAX_Y_OFFSET = 3;
    
    private static final float CAMERA_FOLLOW_OLD_MULTIPLIER = 7;
    private static final float CAMERA_FOLLOW_NEW_MULTIPLIER = 1;
    private static final float CAMERA_FOLLOW_DIVIDER = 8.f;
    
    private static final int SCENE_CELL_WIDTH = 32;
    private static final int SCENE_CELL_HEIGHT = 24;
    private static final int SCENE_GRASS_MILLIS = 100;
    private static final int SCENE_GRASS_FRAME_DELAY = 4;
}
