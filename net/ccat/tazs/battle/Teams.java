package net.ccat.tazs.battle;


/**
 * Gathers all the supported Teams.
 */
class Teams
{
    /**
     * Battle still ongoing when returned by UnitsSystem's winnerTeam().
     */
    public static final char TO_BE_DETERMINED = -2;
    /**
     * Everyone died when returned by UnitsSystem's winnerTeam().
     */
    public static final char NONE = -1;
    /**
     * Player's team.
     */
    public static final char PLAYER = 0;
    /**
     * Enemy's team.
     */
    public static final char ENEMY = 1;
}