package net.ccat.tazs.battle;


import net.ccat.tazs.resources.Colors;

/**
 * Gathers all the supported Teams.
 */
class Teams
{
    /**
     * Battle still ongoing when returned by UnitsSystem's winnerTeam().
     */
    public static final byte TO_BE_DETERMINED = -2;
    /**
     * Everyone died when returned by UnitsSystem's winnerTeam().
     */
    public static final byte NONE = -1;
    /**
     * Player's team.
     */
    public static final byte PLAYER = 0;
    /**
     * Enemy's team.
     */
    public static final byte ENEMY = 1;
    
    
    /***** TOOLS *****/
    
    /**
     * @param team The Team's identifier.
     * @return the usual color for the Team.
     */
    public static final int colorForTeam(int team)
    {
        switch (team)
        {
            default:
            case TO_BE_DETERMINED: return Colors.TEAM_UNKNOWN;
            case NONE: return Colors.TEAM_UNKNOWN;
            case PLAYER: return Colors.TEAM_PLAYER;
            case ENEMY: return Colors.TEAM_ENEMY;
        }
    }
    /**
     * @param team The Team's identifier.
     * @return the usual, darker color for the Team.
     */
    public static final int darkerColorForTeam(int team)
    {
        switch (team)
        {
            default:
            case TO_BE_DETERMINED: return Colors.TEAM_UNKNOWN_DARKER;
            case NONE: return Colors.TEAM_UNKNOWN_DARKER;
            case PLAYER: return Colors.TEAM_PLAYER_DARKER;
            case ENEMY: return Colors.TEAM_ENEMY_DARKER;
        }
    }
    
    /**
     * @param team
     * @return the ID of the opposite team.
     */
    public static final int oppositeTeam(int team)
    {
        switch (team)
        {
            default:
            case TO_BE_DETERMINED: return TO_BE_DETERMINED;
            case NONE: return NONE;
            case PLAYER: return ENEMY;
            case ENEMY: return PLAYER;
        }
    }
}