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

tazs.writeUInt16 = function(data, value)
{
    data.push(value & 0xFF);
    data.push((value >> 8) & 0xFF);
}
tazs.writeString = function(data, value)
{
    for (var charI = 0; charI < value.length; charI++)
        data.push(value.charCodeAt(charI));
    data.push(0);
}
tazs.writeUInt8 = function(data, value)
{
    data.push(value & 0xFF);
}
tazs.writeInt8 = function(data, value)
{
    if (value >= 0)
        data.push(value & 0xFF);
    else
        data.push((256 + value) & 0xFF);
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
    
    var packBinary = [];
    var packLength = 0;
    var contentsBinary = [];
    var contentsLength = 0;
    
    {
        // Magic value.
        this.writeString(packBinary, this.MAGIC_STRING);
    }
    
    {
        // Pack's title.
        this.writeUInt16(packBinary, this.PACK_CONTENT_ADDRESS);
        this.writeString(contentsBinary, packSpecifications.title);
    }
    
    {
        // Pack's description.
        this.writeUInt16(packBinary, this.PACK_CONTENT_ADDRESS + contentsBinary.length);
        this.writeString(contentsBinary, packSpecifications.description);
    }
    
    {
        // Pack's Challenges
        this.writeUInt16(packBinary, this.PACK_CONTENT_ADDRESS + contentsBinary.length);
        
        // Number of challenges.
        this.writeUInt8(contentsBinary, packSpecifications.challenges.length);
        
        for (var challengeI in packSpecifications.challenges)
        {
            var challengeContentsBinary = [];
            var challengeSpecs = packSpecifications.challenges[challengeI];
            
            {
                // Challenge's Identifier
                this.writeUInt8(contentsBinary, challengeSpecs.identifier);
            }
    
            {
                // Challenge's Title.
                this.writeUInt16(contentsBinary, this.CHALLENGE_CONTENT_OFFSET);
                this.writeString(challengeContentsBinary, challengeSpecs.title);
            }
            
            {
                // Challenge's Description.
                this.writeUInt16(contentsBinary, this.CHALLENGE_CONTENT_OFFSET + challengeContentsBinary.length);
                this.writeString(challengeContentsBinary, challengeSpecs.description);
            }
            
            {
                // Challenge's Units.
                this.writeUInt16(contentsBinary, this.CHALLENGE_CONTENT_OFFSET + challengeContentsBinary.length);
                
                // Number of units.
                this.writeUInt8(challengeContentsBinary, challengeSpecs.units.length);
                for (var unitI in challengeSpecs.units)
                {
                    var unitSpec = challengeSpecs.units[unitI];
                    
                    if (unitSpec.length != 4)
                        throw new Exception("Wrong unit format for unit #" + unitI);
                    
                    var unitTeamKey = unitSpec[0];
                    var unitTypeKey = unitSpec[1];
                    var unitX = unitSpec[2];
                    var unitY = unitSpec[3];
                    var unitTeamId = this.TEAMS[unitTeamKey];
                    var unitTypeId = this.TEAMS[unitTeamKey];
                    
                    if (unitTeamId === undefined)
                        throw new Exception("Unknow team " + unitTeamKey + " for unit #" + unitI);
                    if (unitTypeId === undefined)
                        throw new Exception("Unknown unit type " + unitTypeKey + " for unit #" + unitI);
                        
                    var unitInfo = unitTeamId << 6 | unitTypeId;
                    
                    this.writeUInt8(challengeContentsBinary, unitInfo);
                    this.writeUInt8(challengeContentsBinary, unitX);
                    this.writeUInt8(challengeContentsBinary, unitY);
                }
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
                this.writeUInt16(contentsBinary, allowedUnits);
            }
            
            {
                // Allowed resources
                this.writeUInt16(contentsBinary, challengeSpecs.allowedResources);
            }
    
            contentsBinary = contentsBinary.concat(challengeContentsBinary);
        }
    }
    
    // Pack's Extra.
    this.writeUInt16(packBinary, this.PACK_CONTENT_ADDRESS + contentsBinary.length);
    // TODO: Fills the extra parts here.
    
    packBinary = packBinary.concat(contentsBinary);
    
    var finalBinary = new Uint8Array(packBinary.length);
    
    for (var byteI = 0; byteI < packBinary.length; byteI++)
        finalBinary[byteI] = packBinary[byteI];

    write(targetFilePath, finalBinary);
    
    log("Wrote " + finalBinary.length + "B into " + targetFilePath + ".");
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
                "identifier": 1,
                // Title of said challenge.
                "title": "A Bunch of Guys",
                // Flavor text.
                "description": "Best be careful.",
                // How much beans can be used for that challenge.
                "allowedResources": 60,
                // The units the Player can add. See `tazs.UNITTYPES` for the possible values.
                "allowedUnits": ["brawler", "slapper"],
                // The pre-positionned unit.
                "units":
                [
                    [
                        // The Unit's team. See `tazs.TEAMS` for possible values.
                        "enemy",
                        // The Unit's type. See `tazs.UNITTYPES` for possible values.
                        "brawler",
                        // Coordinates. 0, 0 is usually the center of the scene. Wrapped into [128-127].
                        40, -10
                    ],
                    ["enemy", "target", 40, 10]
                ]
            },
            {
                "identifier": 2,
                "title": "A Bunch of Guys 2",
                "description": "Hmm.",
                "allowedResources": 40,
                "allowedUnits": ["brawler", "slapper"],
                "units":
                [
                    ["enemy", "brawler", 40, -20],
                    ["enemy", "brawler", 40, 20],
                    ["enemy", "brawler", 80, -20],
                    ["enemy", "brawler", 80, 20]
                ]
            },
            {
                "identifier": 3,
                "title": "A Bunch of Guys 3",
                "description": "Hmm.",
                "allowedResources": 30,
                "allowedUnits": ["brawler", "slapper"],
                "units":
                [
                    ["enemy", "brawler", 40, -30],
                    ["enemy", "brawler", 40, 30],
                    ["enemy", "brawler", 100, -30],
                    ["enemy", "brawler", 100, 30]
                ]
            },
            {
                "identifier": 4,
                "title": "A Bunch of Guys 4",
                "description": "Hmm.",
                "allowedResources": 60,
                "allowedUnits": ["brawler", "slapper", "sworder"],
                "units":
                [
                    ["enemy", "brawler", 40, -30],
                    ["enemy", "brawler", 40, -30],
                    ["enemy", "brawler", 40, 30],
                    ["enemy", "brawler", 40, 30],
                    ["enemy", "brawler", 100, -30],
                    ["enemy", "brawler", 100, -30],
                    ["enemy", "brawler", 100, 30],
                    ["enemy", "brawler", 100, 30]
                ]
            },
            {
                "identifier": 5,
                "title": "Party",
                "description": "Hehe",
                "allowedResources": 50,
                "allowedUnits": ["brawler", "slapper", "sworder", "shieldbearer"],
                "units":
                [
                    ["enemy", "brawler", 75, -25],
                    ["enemy", "brawler", 75, 25],
                    ["enemy", "sworder", 50, 0],
                    ["enemy", "brawler", 25, -25],
                    ["enemy", "brawler", 25, 25]
                ]
            }
        ]
    };
    
    tazs.compileChallengePack("ChallengesPack", challengesPackSpecifications);
})();
