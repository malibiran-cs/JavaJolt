JavaJolt Sound Effects
======================

Folder:  src/edu/batstateu/javajolt/ui/swing/assets/sfx/

All sound effects live here, grouped in one subfolder per group.
Sounds are played only by the Swing layer (see ui/swing/UiSounds).
Services and entities never play sounds.

CURRENT FILES
-------------
ui/button_hover.wav   Mouse enters a button.
ui/button_click.wav   Previous click sound, kept for reverting.
ui/button_click2.wav  Mouse presses a button (trial; button_click.wav kept for reverting).
ui/answer_correct.wav    Correct-answer feedback.
ui/answer_incorrect.wav  Incorrect-answer feedback.

jolt/jolt_spark.wav          Newly displayed SPARK milestone.
jolt/jolt_jolt.wav           Newly displayed JOLT milestone.
jolt/jolt_surge.wav          Newly displayed SURGE milestone.
jolt/jolt_short_circuit.wav  Newly displayed SHORT CIRCUIT message.

timer/timer_countdown_3.wav  Four beeps at 0, 1, 2 and 3 seconds, followed by decay.

Original file names (kept for provenance and license checks):
  button_hover.wav  <-  lesiakower-minimalist-button-hover-sound-effect-399749.wav
  button_click.wav  <-  u_uev3l0tolc-buttonon-521345.wav
  jolt_spark.wav          <-  spark.wav
  jolt_jolt.wav           <-  jolt.wav
  jolt_surge.wav          <-  surge.wav
  jolt_short_circuit.wav  <-  short_circuit.wav
  timer_countdown_3.wav   <-  countdown_3.wav
License / attribution: to be confirmed by the owner.

NAMING RULES
------------
- Lowercase snake_case: <group>_<event>.wav   (button_hover, answer_correct)
- One subfolder per group: ui/, jolt/, timer/, later gameplay/, etc.
- Do not rename a file without updating its SoundEffect constant.

FORMAT RULES
------------
- WAV, uncompressed 16-bit PCM, 44.1 kHz. The answer files are mono;
  the other current WAVs are stereo.
- Keep effects short (under about 1 second) so they can restart instantly.
- Looping ambience and timed countdown files can be longer.
- Do not re-encode or edit the files in code. Adjust loudness with the
  volume value in SoundEffect, not by changing the WAV.

HOW TO ADD A SOUND
------------------
1. Drop the WAV in the right group folder (create the folder if needed).
2. Add one constant to SoundEffect with the file path and its volume.
3. Play it from the Swing layer with UiSounds.play(SoundEffect.YOUR_SOUND).

UiSounds.loop(effect) starts a continuous loop without restarting a running clip.
UiSounds.stop(effect) stops a loaded clip. UiSounds.playFrom(effect, startMillis)
starts at a millisecond offset; negative offsets clamp to zero, offsets at or
beyond the clip length do nothing.

HOW TO CHANGE VOLUME
--------------------
Open SoundEffect.java and find the line marked "VOLUME TUNING".
Each effect has its own number:
  0.0 = mute, 1.0 = the file's original loudness, above 1.0 = boost.
Save, then recompile or rerun with RUN_JAVAJOLT.bat.
