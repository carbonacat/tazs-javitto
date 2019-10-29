let tazs = {};


tazs.challengesDestFolder = "net/ccat/tazs/resources/challenges/";

tazs.UNITTYPES =
{
    "brawler": 0,
    "slapper": 1,
    "sworder": 2,
    "shieldbearer": 3,
    "pikebearer": 4,
    "archer": 5,
    "target": 6
};

tazs.TEAMS =
{
    "player": 0,
    "enemy": 1,
    "left": 0,
    "right": 1
};

tazs.MAGIC_STRING = "TAZS-CHALPACK-1";
tazs.NULLCHAR = "\x00";
tazs.NULLSHORT = "\x00\x00";

tazs.PACK_CONTENT_ADDRESS = 24;
tazs.CHALLENGE_CONTENT_OFFSET = 11;

tazs.fromUInt16 = function(value)
{
    return String.fromCharCode(value & 0xFF) + String.fromCharCode((value >> 8) & 0xFF);
}
tazs.fromUInt8 = function(value)
{
    return String.fromCharCode(value & 0xFF);
}


/**
 * Compiles the given challenge specifications into a binary file usable by the game.
 * 
 * @param filename The name of the file, without the folder and the BIN. Usually something like "Challenge01".
 * @param packSpecifications A JSON containing the specifications for all the challenges inside this pack.
 */
tazs.compileChallengePack = function(filename, packSpecifications)
{
    let targetFilePath = this.challengesDestFolder + filename + ".bin";
    // The full binary, we'll built piece by piece.
    var packBinary = "";
    var contentsBinary = "";
    
    {
        // Magic value.
        packBinary += this.MAGIC_STRING;
        packBinary += this.NULLCHAR;
    }
    
    {
        // Pack's title.
        packBinary += this.fromUInt16(this.PACK_CONTENT_ADDRESS);
        contentsBinary += packSpecifications.title;
        contentsBinary += this.NULLCHAR;
    }
    
    {
        // Pack's description.
        packBinary += this.fromUInt16(this.PACK_CONTENT_ADDRESS + contentsBinary.length);
        contentsBinary += packSpecifications.description;
        contentsBinary += this.NULLCHAR;
    }
    
    {
        // Pack's Challenges
        packBinary += this.fromUInt16(this.PACK_CONTENT_ADDRESS + contentsBinary.length);
        contentsBinary += this.fromUInt8(packSpecifications.challenges.length);
        for (var challengeI in packSpecifications.challenges)
        {
            var challengeContentsBinary = "";
            var challengeSpecs = packSpecifications.challenges[challengeI];
            
            {
                // Challenge's Identifier
                contentsBinary += this.fromUInt8(challengeSpecs.identifier);
            }
    
            {
                // Challenge's Title.
                contentsBinary += this.fromUInt16(this.CHALLENGE_CONTENT_OFFSET);
                challengeContentsBinary += challengeSpecs.title;
                challengeContentsBinary += this.NULLCHAR;
            }
            
            {
                // Challenge's Description.
                contentsBinary += this.fromUInt16(this.CHALLENGE_CONTENT_OFFSET + challengeContentsBinary.length);
                challengeContentsBinary += challengeSpecs.description;
                challengeContentsBinary += this.NULLCHAR;
            }
            
            {
                // Challenge's Units.
                contentsBinary += this.fromUInt16(this.CHALLENGE_CONTENT_OFFSET + challengeContentsBinary.length);
                // TODO: Fills the units here.
            }
            
            {
                // Allowed Units.
                var allowedUnits = 0;
                
                for (var unitI in challengeSpecs.allowedUnits)
                {
                    var unitKey = challengeSpecs.allowedUnits[unitI];
                    var unitId = this.UNITTYPES[unitKey];
                    
                    if (unitId === undefined)
                        throw new Exception("Unknown unit type " + unitKey + ".");
                    allowedUnits = allowedUnits | (1 << unitId);
                }
                contentsBinary += this.fromUInt16(allowedUnits);
            }
            
            {
                // Allowed resources
                contentsBinary += this.fromUInt16(challengeSpecs.allowedResources);
            }
    
            contentsBinary += challengeContentsBinary;
        }
    }
    
    // Pack's Extra.
    packBinary += this.fromUInt16(this.PACK_CONTENT_ADDRESS + contentsBinary.length);
    // TODO: Fills the extra parts here.
    
    packBinary += contentsBinary;
    
    write(targetFilePath, packBinary);
    log("Wrote " + packBinary.length + "B into " + targetFilePath + ".");
};

(function()
{
    let challengesPackSpecifications =
    {
        // Shown in the top of the screen.
        "title": "Challenges",
        // Flavor text.
        "description": "A set of funny challenges to resolve.",
        // The challenges themselves.
        "challenges":
        [
            {
                // Identifies a challenge. Will be crucial later for saving their done state.
                "identifier": 99,
                // Title of said challenge.
                "title": "A Bunch of Guys",
                // Flavor text.
                "description": "Best be careful.",
                // How much beans can be used for that challenge.
                "allowedResources": 60,
                // The units the Player can add. See `tazs.UNITTYPES` for the possible values.
                "allowedUnits": ["brawler", "target"],
                // The pre-positionned unit.
                "units":
                [
                    [
                        // The Unit's team. See `tazs.TEAMS` for possible values.
                        "player",
                        // The Unit's type. See `tazs.UNITTYPES` for possible values.
                        "brawler",
                        // Coordinates. 0, 0 is usually the center of the scene. Wrapped into [128-127].
                        40, -10
                    ],
                    ["player", "brawler", 40, -10]
                ]
            }
        ]
    };
    
    tazs.compileChallengePack("ChallengesPack", challengesPackSpecifications);
})();
