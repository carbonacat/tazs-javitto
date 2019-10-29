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
        return packPointer + readUnsigned16(packPointer + PACK_TITLE_ADDRESS_OFFSET);
    }
    
    /**
     * @param packPointer
     * @return A pointer at the pack's title.
     */
    public static final pointer descriptionPointerFromPack(pointer packPointer)
    {
        return packPointer + readUnsigned16(packPointer + PACK_DESCRIPTION_ADDRESS_OFFSET);
    }
    
    /**
     * @param packPointer
     * @return How many challenges there is inside this pack.
     */
    public static final int challengesCountFromPack(pointer packPointer)
    {
        return (int)System.memory.LDRB(packPointer + readUnsigned16(packPointer + PACK_CHALLENGES_ADDRESS_OFFSET));
    }
    
    /**
     * @param packPointer
     * @param challengeIndex - 0 means the first challenge in the pack. Unrelated to Challenge's ID.
     * @return How many challenges there is inside this pack.
     */
    public static final pointer challengePointerFromPack(pointer packPointer, int challengeIndex)
    {
        pointer addressTablePointer = packPointer + readUnsigned16(packPointer + PACK_CHALLENGES_ADDRESS_OFFSET) + 1;
        pointer challengeAddressPointer = addressTablePointer + challengeIndex * 2;

        return packPointer + readUnsigned16(challengeAddressPointer);
    }
    
    /**
     * @param challengePointer
     * @return The title for this Challenge.
     */
    public static final pointer titlePointerFromChallenge(pointer challengePointer)
    {
        return challengePointer + readUnsigned16(challengePointer + CHALLENGE_TITLE_OFFSET_OFFSET);
    }
    
    /**
     * @param challengePointer
     * @return The description for this Challenge.
     */
    public static final pointer descriptionPointerFromChallenge(pointer challengePointer)
    {
        return challengePointer + readUnsigned16(challengePointer + CHALLENGE_DESCRIPTION_OFFSET_OFFSET);
    }
    
    /**
     * @return The allowed resources for that challenge.
     */
    public static final int allowedResourcesFromChallenge(pointer challengePointer)
    {
        return readUnsigned16(challengePointer + CHALLENGE_ALLOWED_RESOURCES_OFFSET_OFFSET);
    }
    
    public static final pointer unitsPointerFromChallenge(pointer challengePointer)
    {
        return challengePointer + readUnsigned16(challengePointer + CHALLENGE_UNITS_OFFSET_OFFSET);
    }
    
    /**
     * @param challengeUnitsPointer The pointer given by unitsPointerFromChallenge.
     * @return How many units are pre-positionned in the given challenge's units.
     */
    public static final int countFromChallengeUnits(pointer challengeUnitsPointer)
    {
        return (int)System.memory.LDRB(challengeUnitsPointer);
    }
    
    /**
     * @param challengeUnitsPointer The pointer given by unitsPointerFromChallenge.
     * @param unitI Which unit.
     * @return The X coordinate for that unit.
     */
    public static final int xFromChallengeUnit(pointer challengeUnitsPointer, int unitI)
    {
        return readSigned8(challengeUnitsPointer + 1 + unitI * CHALLENGE_UNIT_SIZE + CHALLENGE_UNIT_X_OFFSET);
    }
    
    /**
     * @param challengeUnitsPointer The pointer given by unitsPointerFromChallenge.
     * @param unitI Which unit.
     * @return The Y coordinate for that unit.
     */
    public static final int yFromChallengeUnit(pointer challengeUnitsPointer, int unitI)
    {
        return readSigned8(challengeUnitsPointer + 1 + unitI * CHALLENGE_UNIT_SIZE + CHALLENGE_UNIT_Y_OFFSET);
    }
    
    /**
     * @param challengeUnitsPointer The pointer given by unitsPointerFromChallenge.
     * @param unitI Which unit.
     * @return The Team for that unit.
     */
    public static final byte teamFromChallengeUnit(pointer challengeUnitsPointer, int unitI)
    {
        int info = (int)System.memory.LDRB(challengeUnitsPointer + 1 + unitI * CHALLENGE_UNIT_SIZE + CHALLENGE_UNIT_INFO_OFFSET);
        
        return (info & CHALLENGE_UNIT_INFO_TEAM_MASK) >> CHALLENGE_UNIT_INFO_TEAM_SHIFT;
    }
    
    /**
     * @param challengeUnitsPointer The pointer given by unitsPointerFromChallenge.
     * @param unitI Which unit.
     * @return The Type for that unit.
     */
    public static final int typeFromChallengeUnit(pointer challengeUnitsPointer, int unitI)
    {
        int info = (int)System.memory.LDRB(challengeUnitsPointer + 1 + unitI * CHALLENGE_UNIT_SIZE + CHALLENGE_UNIT_INFO_OFFSET);
        
        System.out.println("info=" + info);
        return info & CHALLENGE_UNIT_INFO_TYPE_MASK;
    }
            
    
    /***** PRIVATE *****/
    
    /**
     * @return A int8 read at the given location.
     */
    private static final int readSigned8(pointer pointer)
    {
        int value = (int)System.memory.LDRB(pointer);
        
        if (value <= 127)
            return value;
        return value - 256;
    }
    
    /**
     * @return A uint16 read at the given location.
     */
    private static final int readUnsigned16(pointer pointer)
    {
        return ((int)System.memory.LDRB(pointer)) + ((int)System.memory.LDRB(pointer + 1) << 8);
    }
    
    
    private static final int PACK_TITLE_ADDRESS_OFFSET = 16;
    private static final int PACK_DESCRIPTION_ADDRESS_OFFSET = 18;
    private static final int PACK_CHALLENGES_ADDRESS_OFFSET = 20;
    private static final int CHALLENGE_TITLE_OFFSET_OFFSET = 1;
    private static final int CHALLENGE_DESCRIPTION_OFFSET_OFFSET = 3;
    private static final int CHALLENGE_UNITS_OFFSET_OFFSET = 5;
    private static final int CHALLENGE_ALLOWED_RESOURCES_OFFSET_OFFSET = 9;
    private static final int CHALLENGE_UNIT_SIZE = 3;
    private static final int CHALLENGE_UNIT_INFO_OFFSET = 0;
    private static final int CHALLENGE_UNIT_INFO_TEAM_MASK = 0xC0;
    private static final int CHALLENGE_UNIT_INFO_TEAM_SHIFT = 6;
    private static final int CHALLENGE_UNIT_INFO_TYPE_MASK = 0x0F;
    private static final int CHALLENGE_UNIT_X_OFFSET = 1;
    private static final int CHALLENGE_UNIT_Y_OFFSET = 2;
}