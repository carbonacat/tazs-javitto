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
    
    // Contains the magic value, and the 4 initial addresses.
    var headerBinary = [];
    // Contains the Title and the Description
    var infoBinary = [];
    // Contains the Challenges, including their address table.
    var challengesContentsBinary = [];
    // Contains the Extra.
    var extraContentsBinary = [];
    
    {
        // Magic value.
        this.writeString(headerBinary, this.MAGIC_STRING);
    }
    
    {
        // Pack's title.
        this.writeUInt16(headerBinary, this.PACK_CONTENT_ADDRESS);
        this.writeString(infoBinary, packSpecifications.title);
    }
    
    {
        // Pack's description.
        this.writeUInt16(headerBinary, this.PACK_CONTENT_ADDRESS + infoBinary.length);
        this.writeString(infoBinary, packSpecifications.description);
    }
    
    {
        var baseAddress = this.PACK_CONTENT_ADDRESS + infoBinary.length;
        var addressTableBinary = [];
        var challengesBinary = [];
    
        // Pack's Challenges
        this.writeUInt16(headerBinary, baseAddress);
        
        // Number of challenges.
        this.writeUInt8(addressTableBinary, packSpecifications.challenges.length);
        
        var addressTableSize = 1 + packSpecifications.challenges.length * 2;
        
        for (var challengeI in packSpecifications.challenges)
        {
            var challengeBinary = [];
            var challengeSpecs = packSpecifications.challenges[challengeI];
            
            {
                // Adding this Challenge to the table.
                this.writeUInt16(addressTableBinary, baseAddress + addressTableSize + challengesBinary.length);
            }
            
            {
                // Challenge's Identifier
                this.writeUInt8(challengesBinary, challengeSpecs.identifier);
            }
    
            {
                // Challenge's Title.
                this.writeUInt16(challengesBinary, this.CHALLENGE_CONTENT_OFFSET);
                this.writeString(challengeBinary, challengeSpecs.title);
            }
            
            {
                // Challenge's Description.
                this.writeUInt16(challengesBinary, this.CHALLENGE_CONTENT_OFFSET + challengeBinary.length);
                this.writeString(challengeBinary, challengeSpecs.description);
            }
            
            {
                // Challenge's Units.
                this.writeUInt16(challengesBinary, this.CHALLENGE_CONTENT_OFFSET + challengeBinary.length);
                
                // Number of units.
                this.writeUInt8(challengeBinary, challengeSpecs.units.length);
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
                    var unitTypeId = this.UNITTYPES[unitTypeKey];
                    
                    if (unitTeamId === undefined)
                        throw new Exception("Unknow team " + unitTeamKey + " for unit #" + unitI);
                    if (unitTypeId === undefined)
                        throw new Exception("Unknown unit type " + unitTypeKey + " for unit #" + unitI);
                        
                    var unitInfo = unitTeamId << 6 | unitTypeId;
                    
                    this.writeUInt8(challengeBinary, unitInfo);
                    this.writeUInt8(challengeBinary, unitX);
                    this.writeUInt8(challengeBinary, unitY);
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
                this.writeUInt16(challengesBinary, allowedUnits);
            }
            
            {
                // Allowed resources
                this.writeUInt16(challengesBinary, challengeSpecs.allowedResources);
            }
    
            challengesBinary = challengesBinary.concat(challengeBinary);
        }
        challengesContentsBinary = addressTableBinary.concat(challengesBinary);
    }
    
    {
        // Pack's Extra.
        this.writeUInt16(headerBinary, this.PACK_CONTENT_ADDRESS + infoBinary.length + challengesContentsBinary.length);
        // TODO: Fills the extra parts here.
        this.writeString(extraContentsBinary, "ORLY?");
    }
    
    var packBinary = headerBinary.concat(infoBinary, challengesContentsBinary, extraContentsBinary);
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
