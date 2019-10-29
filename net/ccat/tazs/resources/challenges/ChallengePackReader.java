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
    public static final pointer titlePointerFromPack(pointer packPointer)
    {
        return packPointer + readH(packPointer + PACK_TITLE_ADDRESS_OFFSET);
    }
    
    /**
     * @param packPointer
     * @return A pointer at the pack's title.
     */
    public static final pointer descriptionPointerFromPack(pointer packPointer)
    {
        return packPointer + readH(packPointer + PACK_DESCRIPTION_ADDRESS_OFFSET);
    }
    
    /**
     * @param packPointer
     * @return How many challenges there is inside this pack.
     */
    public static final int challengesCountFromPack(pointer packPointer)
    {
        return (int)System.memory.LDRB(packPointer + readH(packPointer + PACK_CHALLENGES_ADDRESS_OFFSET));
    }
    
    /**
     * @param packPointer
     * @param challengeIndex - 0 means the first challenge in the pack. Unrelated to Challenge's ID.
     * @return How many challenges there is inside this pack.
     */
    public static final pointer challengePointerFromPack(pointer packPointer, int challengeIndex)
    {
        pointer addressTablePointer = packPointer + readH(packPointer + PACK_CHALLENGES_ADDRESS_OFFSET) + 1;
        pointer challengeAddressPointer = addressTablePointer + challengeIndex * 2;

        return packPointer + readH(challengeAddressPointer);
    }
    
    /**
     * @param challengePointer
     * @return The title for this Challenge.
     */
    public static final pointer titlePointerFromChallenge(pointer challengePointer)
    {
        return challengePointer + readH(challengePointer + CHALLENGE_TITLE_OFFSET_OFFSET);
    }
    
    /**
     * @param challengePointer
     * @return The description for this Challenge.
     */
    public static final pointer descriptionPointerFromChallenge(pointer challengePointer)
    {
        return challengePointer + readH(challengePointer + CHALLENGE_DESCRIPTION_OFFSET_OFFSET);
    }
    
    /**
     * @return The allowed resources for that challenge.
     */
    public static final int allowedResourcesFromChallenge(pointer challengePointer)
    {
        return readH(challengePointer + CHALLENGE_ALLOWED_RESOURCES_OFFSET_OFFSET);
    }
    
    /**
     * @return A uint16 read at the given location.
     */
    public static final int readH(pointer pointer)
    {
        return ((int)System.memory.LDRB(pointer)) + ((int)System.memory.LDRB(pointer + 1) << 8);
    }
    
    
    private static final int PACK_TITLE_ADDRESS_OFFSET = 16;
    private static final int PACK_DESCRIPTION_ADDRESS_OFFSET = 18;
    private static final int PACK_CHALLENGES_ADDRESS_OFFSET = 20;
    private static final int CHALLENGE_TITLE_OFFSET_OFFSET = 1;
    private static final int CHALLENGE_DESCRIPTION_OFFSET_OFFSET = 3;
    private static final int CHALLENGE_ALLOWED_RESOURCES_OFFSET_OFFSET = 9;
}