package net.ccat.tazs.save;

import femto.Cookie;


/**
 * A Cookie that saves the followings:
 * - Settings
 * - Campaign Progression
 * - Challenges
 * - Unlocked Units
 */
public class SaveCookie
    extends Cookie
{
    public SaveCookie()
    {
        super();
        // Totally Accurate Zombie Simulator
        begin("ToAcZoSi");
    }
    
    /***** TOOLS *****/
    
    /**
     * @return the status for this cookie. See SaveStatus for possible values.
     */
    public int getStatus()
    {
        if ((magicValue == 0) && (version == 0) && (checksum == 0) && (launches == 0))
            return SaveStatus.EMPTY;
        if (magicValue != MAGIC_VALUE)
            return SaveStatus.CORRUPTED;
        if (computeChecksum() != checksum)
            return SaveStatus.CORRUPTED;
        // Additional checks could be done for various stuff, like campaign stage, etc.
        return SaveStatus.OK;
    }
    
    /**
     * Totally clear this cookie.
     * Doesn't save it, just clear that class.
     */
    public void clear()
    {
        magicValue = MAGIC_VALUE;
        version = VERSION;
        launches = 1;
        
        settings00 = settings01 = settings02 = settings03 = 0;
        
        unlockedStuff00 = unlockedStuff01 = unlockedStuff02 = unlockedStuff03 = 0;
        
        campaignStore00 = campaignStore01 = campaignStore02 = campaignStore03 = 0;
        
        challenges00 = challenges01 = challenges02 = challenges03 = challenges04 = challenges05 = challenges06 = challenges07 = 0;
    
        extra00 = extra01 = extra02 = extra03 = extra04 = extra05 = extra06 = extra07 = 0;
        
        checksum = computeChecksum();
    }
    
    public boolean isChallengeDone(int challengeIdentifier)
    {
        if ((challengeIdentifier >= 0) && (challengeIdentifier <= 7))
            return (challenges00 & (1 << (challengeIdentifier - 0))) != 0;
        if ((challengeIdentifier >= 8) && (challengeIdentifier <= 15))
            return (challenges01 & (1 << (challengeIdentifier - 8))) != 0;
        if ((challengeIdentifier >= 16) && (challengeIdentifier <= 23))
            return (challenges02 & (1 << (challengeIdentifier - 16))) != 0;
        if ((challengeIdentifier >= 24) && (challengeIdentifier <= 31))
            return (challenges03 & (1 << (challengeIdentifier - 24))) != 0;
        if ((challengeIdentifier >= 32) && (challengeIdentifier <= 39))
            return (challenges04 & (1 << (challengeIdentifier - 32))) != 0;
        if ((challengeIdentifier >= 40) && (challengeIdentifier <= 47))
            return (challenges05 & (1 << (challengeIdentifier - 40))) != 0;
        if ((challengeIdentifier >= 48) && (challengeIdentifier <= 55))
            return (challenges06 & (1 << (challengeIdentifier - 48))) != 0;
        if ((challengeIdentifier >= 56) && (challengeIdentifier <= 63))
            return (challenges07 & (1 << (challengeIdentifier - 56))) != 0;
        return false;
    }
    
    public void markChallengeAsDone(int challengeIdentifier)
    {
        if ((challengeIdentifier >= 0) && (challengeIdentifier <= 7))
            challenges00 |= (1 << (challengeIdentifier - 0));
        else if ((challengeIdentifier >= 8) && (challengeIdentifier <= 15))
            challenges01 |= (1 << (challengeIdentifier - 8));
        else if ((challengeIdentifier >= 16) && (challengeIdentifier <= 23))
            challenges02 |= (1 << (challengeIdentifier - 16));
        else if ((challengeIdentifier >= 24) && (challengeIdentifier <= 31))
            challenges03 |= (1 << (challengeIdentifier - 24));
        else if ((challengeIdentifier >= 32) && (challengeIdentifier <= 39))
            challenges04 |= (1 << (challengeIdentifier - 32));
        else if ((challengeIdentifier >= 40) && (challengeIdentifier <= 47))
            challenges05 |= (1 << (challengeIdentifier - 40));
        else if ((challengeIdentifier >= 48) && (challengeIdentifier <= 55))
            challenges06 |= (1 << (challengeIdentifier - 48));
        else if ((challengeIdentifier >= 56) && (challengeIdentifier <= 63))
            challenges07 |= (1 << (challengeIdentifier - 56));
        checksum = computeChecksum();
    }
    
    
    /***** PRIVATE *****/
    
    private byte computeChecksum()
    {
        return (launches
                + settings00 + settings01 + settings02 + settings03
                + unlockedStuff00 + unlockedStuff01 + unlockedStuff02 + unlockedStuff03
                + campaignStore00 + campaignStore01 + campaignStore02 + campaignStore03
                + challenges00 + challenges01 + challenges02 + challenges03 + challenges04 + challenges05 + challenges06 + challenges07
                + extra00 + extra01 + extra02 + extra03 + extra04 + extra05 + extra06 + extra07);
    }
    
    // Must match MAGIC_VALUE else the Cookie is corrupted.
    private byte magicValue;
    // Must match VERSION else the Cookie is unreadable.
    private byte version;
    // A simple addition of every other value (launches, settings*, unlocked*, campaign*, challenges*), just to be sure nothing went wrong.
    private byte checksum;
    // How much times the game was launched.
    private byte launches;
    
    // Settings storage.
    private byte settings00;
    private byte settings01;
    private byte settings02;
    private byte settings03;
    
    // Unlocked stuff storage.
    private byte unlockedStuff00;
    private byte unlockedStuff01;
    private byte unlockedStuff02;
    private byte unlockedStuff03;
    
    // Campaign storage.
    private byte campaignStore00;
    private byte campaignStore01;
    private byte campaignStore02;
    private byte campaignStore03;
    
    // Challenges storage.
    private byte challenges00;
    private byte challenges01;
    private byte challenges02;
    private byte challenges03;
    private byte challenges04;
    private byte challenges05;
    private byte challenges06;
    private byte challenges07;
    
    // Extra, reserved storage.
    private byte extra00;
    private byte extra01;
    private byte extra02;
    private byte extra03;
    private byte extra04;
    private byte extra05;
    private byte extra06;
    private byte extra07;
    
    private static final byte MAGIC_VALUE = 0xA2; // Accurate 2ombie :p
    private static final byte VERSION = 0x01; // Shouldn't change much.
}