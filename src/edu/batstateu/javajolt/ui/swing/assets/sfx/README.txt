JavaJolt Sound Effects
======================

Folder:  src/edu/batstateu/javajolt/ui/swing/assets/sfx/

All sound effects live here, grouped in one subfolder per group.
Sounds are played only by the Swing layer (see ui/swing/UiSounds).
Services and entities never play sounds.

CURRENT FILES
-------------
ui/button_hover.wav   Mouse enters a button.
ui/button_click2.wav  Mouse presses a button (trial; button_click.wav kept for reverting).

Original file names (kept for provenance and license checks):
  button_hover.wav  <-  lesiakower-minimalist-button-hover-sound-effect-399749.wav
  button_click.wav  <-  u_uev3l0tolc-buttonon-521345.wav
License / attribution: to be confirmed by the owner.

NAMING RULES
------------
- Lowercase snake_case: <group>_<event>.wav   (button_hover, answer_correct)
- One subfolder per group: ui/, later gameplay/, etc.
- Do not rename a file without updating its SoundEffect constant.

FORMAT RULES
------------
- WAV, uncompressed 16-bit PCM (the current files are 44.1 kHz stereo).
- Keep effects short (under about 1 second) so they can restart instantly.
- Do not re-encode or edit the files in code. Adjust loudness with the
  volume value in SoundEffect, not by changing the WAV.

HOW TO ADD A SOUND
------------------
1. Drop the WAV in the right group folder (create the folder if needed).
2. Add one constant to SoundEffect with the file path and its volume.
3. Play it from the Swing layer with UiSounds.play(SoundEffect.YOUR_SOUND).

HOW TO CHANGE VOLUME
--------------------
Open SoundEffect.java and find the line marked "VOLUME TUNING".
Each effect has its own number:
  0.0 = mute, 1.0 = the file's original loudness, above 1.0 = boost.
Save, then recompile or rerun with RUN_JAVAJOLT.bat.
