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
    
    
    private static final int PACK_TITLE_ADDRESS_OFFSET = 16;
    private static final int PACK_DESCRIPTION_ADDRESS_OFFSET = 18;
}