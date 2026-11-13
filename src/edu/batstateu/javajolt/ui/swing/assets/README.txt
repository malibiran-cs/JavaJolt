Softline UI Kit — Flat Night (selected assets)
=============================================

Source: assets/Softline_UI_Kit_Flat_Night/PNG_2x (in the project root)
These files are copied here so the game can load them from the classpath.
The kit's LICENSE.pdf is in that original folder.

WHY 2x?
-------
The kit is authored at 1x. PNG_2x is the same art at twice the resolution.
The UI draws each image at half its pixel size (96x32 for a large button,
for example), so:

  100% display scale -> the 2x image is downscaled, edges stay smooth
  150% display scale -> still more pixels than the screen needs
  200% display scale -> the 2x image is used 1:1, pixel-perfect

PNG_1x would be soft on a 125%/150% Windows display. SVG would scale forever,
but Java Swing has no built-in SVG renderer, so the 2x PNGs are the best
option without adding a library.

FILES USED BY THE UI
--------------------
buttons/button_large_primary_*    RoundedButton (primary)
buttons/button_large_secondary_*  RoundedButton (secondary)
buttons/button_large_danger_*     RoundedButton (danger)
buttons/button_large_disabled_normal
panels/panel                      RoundedPanel (default panel)
panels/panel_inset                EnemyPortraitView and player portrait frames
panels/title_plate                WelcomeScreen (GREETINGS plate)
panels/divider                    Divider
fields/input_normal               RoundedTextField (not focused)
fields/input_focus                RoundedTextField (focused)
items/star_empty                  BadgeSlot

The *_normal, *_hover and *_pressed suffixes are chosen in
RoundedButton.currentPatch().

9-SLICE MARGINS
---------------
Stretchable art is drawn by NinePatch. The margins below are the 1x values
from the kit's slices.json; NinePatch multiplies them by UiAssets.PIXEL_SCALE
when reading the 2x image.

  button_large_*          10 all sides
  panel                   16 all sides
  panel_inset             13 all sides
  title_plate             14 all sides
  divider                 4 left/right, 0 top/bottom
  input_normal/focus      11 all sides

To add another component, copy its PNG_2x file into the matching folder,
add its slices.json values to the component, and load it with
UiAssets.image(...) or UiAssets.patch(...).


TEAM-OWNED CHARACTER ARTWORK
----------------------------
characters/mascot/{idle,correct,hurt}.png
characters/enemies/{topic}/{idle,correct,hurt}.png

These 24 PNGs were exported from the team's original Aseprite drawings.
Editable sources live in art-source/characters at the project root.
The unified emotion system and renderer are documented in
CHARACTER_ART_INTEGRATION.md.
