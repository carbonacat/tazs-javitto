package net.ccat.tazs;

import femto.font.TIC80;
import femto.mode.HiRes16Color;
import femto.sound.Mixer;

import net.ccat.tazs.battle.BattleMode;
import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.battle.UnitTypes;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.palettes.ModifiedNAJI16;
import net.ccat.tazs.resources.sounds.CursorCancelSound;
import net.ccat.tazs.resources.sounds.CursorMoveSound;
import net.ccat.tazs.resources.sounds.CursorSelectSound;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.ui.PadMenuUI;
import net.ccat.tazs.ui.TopBarUI;
import net.ccat.tazs.ui.UIModes;


/**
 * Contains the game elements.
 */
class TAZSGame
{
    public TAZSGame()
    {
        screen = new HiRes16Color(ModifiedNAJI16.palette(), TIC80.font());
        unitsSystem = new UnitsSystem();
        cursorSprite = new CursorSprite();
        menuCursorSprite = new MenuCursorSprite();
        menuCursorSprite.playDefault();
        menuCursorSprite.setStatic(true);
        padMenuUI = new PadMenuUI();
        topBarUI = new TopBarUI();
        cursorMoveSound = new CursorMoveSound();
        cursorSelectSound = new CursorSelectSound();
        cursorCancelSound = new CursorCancelSound();
        
        // Something went wrong if that went havoc!
        while (mAreaCoords.length != AREA_TEAMS_MAX * AREA_SIZE);
        
        Mixer.init(8000);
    }
    
    
    /***** RENDERING *****/
    
    public HiRes16Color screen;
    
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

        screen.fillCircle(minX, minY, 2, 15, false);
        screen.fillCircle(maxX, minY, 2, 15, false);
        screen.fillCircle(maxX, maxY, 2, 15, false);
        screen.fillCircle(minX, maxY, 2, 15, false);
        
        for (float cellX = minCellX; cellX < maxX + SCENE_CELL_WIDTH; cellX += SCENE_CELL_WIDTH)
            for (float cellY= minCellY; cellY < maxY + SCENE_CELL_HEIGHT; cellY += SCENE_CELL_HEIGHT)
                
                screen.fillCircle(cellX, cellY, 2, 8, false);
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
    
    public PadMenuUI padMenuUI;
    public TopBarUI topBarUI;
    public int uiMode = UIModes.INVALID;
    public int currentUnitType = UnitTypes.BRAWLER;
    
    // Cursor's in-game coordinates.
    public float cursorX;
    public float cursorY;
    
    /**
     * Makes the camera follows smoothly the given coordinates.
     * 
     * @param x
     * @param y
     */
    public void moveCamera(float x, float y)
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
    public void moveCameraSmoothly(float x, float y)
    {
        screen.cameraX = (screen.cameraX * CAMERA_FOLLOW_OLD_MULTIPLIER + (x - Dimensions.SCREEN_WIDTH_2) * CAMERA_FOLLOW_NEW_MULTIPLIER) / CAMERA_FOLLOW_DIVIDER;
        screen.cameraY = (screen.cameraY * CAMERA_FOLLOW_OLD_MULTIPLIER + (y - Dimensions.SCREEN_HEIGHT_2) * CAMERA_FOLLOW_NEW_MULTIPLIER) / CAMERA_FOLLOW_DIVIDER;
    }
    
    
    /***** COMMON RESOURCES *****/
    
    public CursorSprite cursorSprite;
    public MenuCursorSprite menuCursorSprite;
    public CursorMoveSound cursorMoveSound;
    public CursorSelectSound cursorSelectSound;
    public CursorCancelSound cursorCancelSound;
    
    
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
}
