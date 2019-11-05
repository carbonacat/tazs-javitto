# Totally Accurate Zombie Simulator - Javitto Edition

A recreation (and homage) of Totally Accurate Battle Simator as an entry for the Java of the Dead Pokitto Jam.
- https://itch.io/jam/java-of-the-dead


## Description

TAZS is a game / toy where the Player has to place their Units against an opposite side comprised of other units.
It runs on the Pokitto (https://www.pokitto.com/).


## Code License

The code is licensed under the Apache 2.0.
A copy of that license can be found in the LICENSE file.


## Graphics and Audio License

All the graphics and audio files are licensed under a Creative Commons Attribution-NonCommercial 4.0 International License.
A copy of that license can be found inside the BY-NC-4.0.txt file.
The source of those files can be found in https://github.com/carbonacat/tazs-workshop


## Credits

- Jonne Valola and the Pokitto Oy company (https://pokitto.com)
- FManga for creating FemtoIDE, his implication on the whole jam and his advices (https://github.com/felipemanga).
- Vampirics (https://github.com/vampirics), Torbuntu (https://github.com/torbuntu) for their support and advices.
- Pharap for the various advices, including how to make this code open-source.
- And everyone else that participated to the jam and made it awesome!!


## Structure

- `bin` - Contains the output binary.
- `net/ccat/tazs` - Most of the source code and graphics.
  - `battle` - The core of the game.
    - `modes` - Defines game modes, such as Sandbox, Quick Battle, Challenges.
    - `handlers` - Defines all the Units Handlers, like for the Brawler units.
  - `resources` - Everything that is used by the code, from constants to audio files.
    - `challenges` - The Challenges Pack Binary and its reader class.
    - `musics` - The Musics' Binaries and their helper classes.
    - `palettes` - The unique palette.
    - `sounds` - The few sounds used inside this game.
    - `sprites` - All the sprites.
      - `everything` and `everyui` are aggregations of sprites inside a single one in order to conserve memory.
    - `texts` - All the texts, ending with a null character.
      - Those are to have strings with 0 RAM cost for each uses.
    - `Colors.java` - All the colors.
    - `Dimensions.java` - All UI-related dimensions.
    - `Texts.java` - The few texts that are still in string format.
    - `VideoConstants.java` - All the general graphics-related constants, including some dimensions, frames indexes, sprites' origins and sizes.
  - `save` - Everything related to saving things/settings/etc.
  - `states` - All the application States
    - `BattlePhaseState.java` - Running when in the Battle Phase (aka when both armies attack each other, and also when the Player is controlling a Unit)
    - `BattlePreparationPhaseState.java` - Running when the Player puts their units on the soon-to-be battleground.
    - `BattleResultPhaseState.java` - Running when the Result Phase is shown, displaying stats and things like that.
    - `ChallengesListState.java` - Running when the Player is looking at the list of challenges.
    - `SettingsState.java` - Running when the Player is adjusting the settings.
    - `TitleScreenState.java` - The very first state - The Title screen, also.
  - `tools` - A collection of various utility static methods.
  - `ui` - UI-related classes, including the pieces of Game UI, as well as some UI-specific tools.
  - `TAZSGame.java` - The singleton-ish java class that contains most of the battle data.
- `scripts/update-binaries.js` - A JS file that will refresh the binary resources, such as the Challenges Pack and the Musics.
- `Main.java` - Entry point.