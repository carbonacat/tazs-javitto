package net.ccat.tazs.resources.challenges;


/**
 * A Static Class that facilitates the reading of a Challenge Pack.
 */
public class ChallengePackReader
{
    /**
     * @param packPointer
     * @return A pointer at the pack's title.
     */
    public static final pointer titleFromPack(pointer packPointer)
    {
        return packPointer + (int)System.memory.LDRH(packPointer + PACK_TITLE_ADDRESS_OFFSET);
    }
    
    /**
     * @param packPointer
     * @return A pointer at the pack's title.
     */
    public static final pointer descriptionFromPack(pointer packPointer)
    {
        return packPointer + (int)System.memory.LDRH(packPointer + PACK_DESCRIPTION_ADDRESS_OFFSET);
    }
    
    /**
     * @param packPointer
     * @return A pointer at the list of challenges - aka challengesPointer.
     */
    public static final pointer challengesFromPack(pointer packPointer)
    {
        return packPointer + (int)System.memory.LDRH(packPointer + PACK_CHALLENGES_ADDRESS_OFFSET);
    }
    
    /**
     * @param challengesPointer - A pointer got from challengesFromPack().
     * @return How many challenges there is inside this pack.
     */
    public static final int countFromChallenges(pointer challengesPointer)
    {
        return (int)System.memory.LDRB(challengesPointer);
    }
    
    
    
    private static final int PACK_TITLE_ADDRESS_OFFSET = 16;
    private static final int PACK_DESCRIPTION_ADDRESS_OFFSET = 18;
    private static final int PACK_CHALLENGES_ADDRESS_OFFSET = 20;
}