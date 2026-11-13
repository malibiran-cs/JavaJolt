# JavaJolt — Team Character Art Integration

This build uses the team's 24 Aseprite drawings (8 characters × 3 emotions),
replacing all older mascot and enemy artwork in the Swing interface.

## Sprite mapping

| Game event | Player mascot | Current topic enemy |
| --- | --- | --- |
| Start / next question | `idle.png` | `idle.png` |
| Correct answer | `correct.png` + green glow | `hurt.png` + red glow and shake |
| Incorrect answer | `hurt.png` + red glow and shake | `correct.png` + green glow |

The three variants stay visible during answer feedback until the next question.
Glow is intentionally brief (~600ms); the hurt shake lasts ~360ms.
The mascot retains its subtle floating idle movement.

## Organization

- Original editable sources: `art-source/characters/` (all `.aseprite` files).
- Runtime PNGs: `src/edu/batstateu/javajolt/ui/swing/assets/characters/`
  - `mascot/{idle,correct,hurt}.png`
  - `enemies/{variables,operators,decisions,loops,arrays,exception,oop}/{idle,correct,hurt}.png`
- `CharacterEmotion.java`: the common `IDLE`, `CORRECT`, `HURT` states.
- `CharacterSpriteCatalog.java`: centralized asset path mapping.
- `CharacterPortraitView.java`: shared image rendering, floating, flash, and shake.
- `MascotView.java`: player-specific image selection and floating enabled.
- `EnemyPortraitView.java`: topic-specific image selection and framed portrait.
- `QuestionScreen.java`: turns game events into the appropriate paired emotes.

Aseprite files cannot be directly loaded by Java Swing. PNGs are exported at
native 128×128 (mascot) / 110×110 (enemy) canvas sizes. The transparent pixel
sprites use nearest-neighbor rendering when resized.

The supplied main mascot sprites had a connected pure-white canvas background.
That exterior background was made transparent without removing white suit pixels.

No changes were made to the game engine's pass/fail logic, score rules, XP,
question banks, audio, or save format.

## Replacing artwork later

Export future Aseprite revisions as transparent PNGs with the same names and
locations. The `.aseprite` files here are source assets for future editing.
After updating a sprite, fully restart the application to clear image caching.

Run `RUN_JAVAJOLT.bat` to play or `RUN_TESTS.bat` to test on Windows with JDK 21+.
