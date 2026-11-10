# Report: Number-first Jolt, milestone sounds, buzz experiment and countdown sound

## 1. Summary

Jolt multipliers now use number-first wording such as `3x`, including Results and tutorial text. Newly displayed milestones play their registered sounds; the isolated buzz experiment runs while the correct-answer streak is positive. Countdown audio follows the existing clock, seeks to compensate for late starts/resumes, stops on interruption and retains its decay on a nonlethal natural expiry. Gameplay rules and the perfect-run hold are unchanged.

## 2. Files

Java and asset paths below are relative to `src/edu/batstateu/javajolt/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `ui/swing/screen/QuestionScreen.java` | MODIFIED | Number-first Jolt label and widest-string sample. |
| `ui/swing/screen/ResultsScreen.java` | MODIFIED | Number-first BEST JOLT value. |
| `data/TutorialContent.java` | MODIFIED | Number-first thresholds derived from the same rule constants. |
| `ui/swing/SoundEffect.java` | MODIFIED | Register six new paths and per-effect volumes. |
| `ui/swing/UiSounds.java` | MODIFIED | Offset playback, idempotent looping and safe stop through one shared failure handler. |
| `ui/swing/JoltSounds.java` | NEW | Pure transition mapping and isolated buzz controls. |
| `ui/swing/MainWindow.java` | MODIFIED | Capture the previous message before the worker, dispatch resolved-answer sounds, stop buzz at lifecycle boundaries, and synchronize countdown playback. |
| `ui/swing/assets/sfx/jolt/jolt_spark.wav` | NEW | Unmodified copy of `spark.wav`. |
| `ui/swing/assets/sfx/jolt/jolt_jolt.wav` | NEW | Unmodified copy of `jolt.wav`. |
| `ui/swing/assets/sfx/jolt/jolt_surge.wav` | NEW | Unmodified copy of `surge.wav`. |
| `ui/swing/assets/sfx/jolt/jolt_short_circuit.wav` | NEW | Unmodified copy of `short_circuit.wav`. |
| `ui/swing/assets/sfx/jolt/jolt_buzz.wav` | NEW | Unmodified copy of `buzz.wav`, used only by the experiment. |
| `ui/swing/assets/sfx/timer/timer_countdown_3.wav` | NEW | Unmodified copy of `countdown_3.wav`. |
| `tests/JoltSoundRulesCheck.java` | NEW | Headless-safe coverage of all 36 before/after message combinations; does not play audio. |
| `RUN_TESTS.bat` | MODIFIED | Compile and run the new pure mapping check like the existing checks. |
| `tests/SwingTimerFlowCheck.java` | MODIFIED | Update multiplier expectations without removing assertions. |
| `tests/TutorialLogicCheck.java` | MODIFIED | Recognize suffix multipliers when checking rule-derived numeric text. |
| `ui/swing/assets/sfx/README.txt` | MODIFIED | List all eleven WAVs by group, record the six source names and document playback operations. |
| `PROJECT_RULES.md` | MODIFIED | Register JoltSounds and document sound ownership. |
| `progress-documentations/change-reports/jolt-messages-and-tutorial.md` | MODIFIED | Update player-facing examples to the number-first format. |
| `progress-documentations/change-reports/jolt-audio-and-countdown.md` | NEW | Required finished-work report. |

## 3. Ownership and rule check

SoundEffect owns paths and gains, UiSounds owns cached clips, and stateless JoltSounds owns transition selection and the buzz experiment. MainWindow owns the existing Swing clock and lifecycle hooks. Screens/tutorial content own presentation wording; services and entities never play audio.

JoltSounds is a stateless static facade following UiSounds/ButtonSounds. Its playback calls deliberately follow the established UI-layer exception to static-only-for-calculation; loop state stays in UiSounds' clips. The explicit single-pass and compile/test instructions supersede the historical brief's phase stops and restriction on generated out/ output. Existing line endings are preserved. No libraries, threads, settings, rules, colors or save fields were added or changed.

## 4. Existing code touched

All edits are listed above. ButtonSounds, the existing four effect volumes, the answer feedback calls, GameSession, JoltMessage, ScoreService, damage rules and perfect-hold methods remain unchanged. Natural expiry now stops only the Swing timer; existing interruption callers retain stopCountdown(), which additionally stops the sound. The original WAVs were found in the sibling `../assets/sfx/` directory and copied to final names inside this repository. Each destination SHA256 equals its source; no original-name copies of these six were added inside the repository.

## 5. Player-visible behavior changes

- JOLT and BEST JOLT use `3x` style; the points suffix and layout are unchanged.
- Milestone sounds play only on message changes: spark at 3x, jolt at 6x and surge at 9x. Breaks play short circuit; unchanged messages, NONE and JAVAJOLT have no milestone sound.
- Buzz starts at the configured correct-answer count, does not restart while running, survives the perfect hold, and stops on a break, results, session starts, topic navigation and Exit.
- Countdown playback begins at the configured lead, catches up using an offset and stops on answer submission, Time Stop, tutorial pause, leaving and disposal. Resume recalculates its offset.

## 6. Save compatibility

No persistence code or schema changed. All gameplay walkthroughs use temporary saves. Existing legacy-save, progression, first-completion and replay-save checks remain in the full suite. Pre-existing gameplay-design-note changes and the untracked AGENT_BRIEF are untouched.

## 7. Balance values introduced or changed

No gameplay balance changed. These are cosmetic tuning values.

### How to adjust sounds

In `src/edu/batstateu/javajolt/ui/swing/SoundEffect.java`, the VOLUME TUNING marker is line 4. Exact volume lines:

```java
// line 6
BUTTON_HOVER("ui/button_hover.wav", 1.0f),
// line 7
BUTTON_CLICK("ui/button_click2.wav", 0.4f),
// line 9
ANSWER_CORRECT("ui/answer_correct.wav", 0.65f),
// line 10
ANSWER_INCORRECT("ui/answer_incorrect.wav", 0.65f),
// line 12
JOLT_SPARK("jolt/jolt_spark.wav", 1.0f),
// line 13
JOLT_JOLT("jolt/jolt_jolt.wav", 0.8f),
// line 14
JOLT_SURGE("jolt/jolt_surge.wav", 0.9f),
// line 15
JOLT_SHORT_CIRCUIT("jolt/jolt_short_circuit.wav", 0.5f),
// line 17
JOLT_BUZZ("jolt/jolt_buzz.wav", 2.0f),
// line 19
TIMER_COUNTDOWN("timer/timer_countdown_3.wav", 1.0f);
```

Edit the second argument in steps of about 0.1: 0.0 mutes, 1.0 preserves original loudness, above 1.0 boosts within the line's supported gain. Keep jolt/surge at or below 1.0 to avoid clipping. Buzz is very quiet by nature; if 2.0 is still too quiet, the future fix is re-exporting a louder WAV, not increasing code gain. No WAV was re-exported in this task. Save, then recompile or rerun RUN_JAVAJOLT.bat.

`src/edu/batstateu/javajolt/ui/swing/MainWindow.java:64`:

```java
private static final int COUNTDOWN_SOUND_LEAD_MILLIS = 3000;
```

Larger starts earlier. If the final beep lands after the timer shows zero, increase this value by that gap in milliseconds; if it lands before zero, decrease it. Save and restart after recompiling.

`src/edu/batstateu/javajolt/ui/swing/JoltSounds.java:7`:

```java
private static final int BUZZ_MIN_STREAK = 1;
```

Set it to 2 or 3 to delay the buzz until that many consecutive correct answers.

To remove only the buzz experiment:

1. Delete SoundEffect.JOLT_BUZZ at SoundEffect.java:17 and its preceding EXPERIMENT comment.
2. Delete JoltSounds.BUZZ_MIN_STREAK at line 7, updateBuzz at line 35 and stopBuzz at line 45. Their marked internal calls are UiSounds.loop at line 38, UiSounds.stop at line 41, and UiSounds.stop at line 47.
3. Delete the marked updateBuzz(streak) call in JoltSounds.onAnswerResolved at line 32. Keep its milestone playback.
4. Delete each marked JoltSounds.stopBuzz() call in MainWindow: startLesson line 287; lesson-finished branch of showNextQuestion line 513; dispose line 834; startTutorial line 975; non-question branch of showScreen line 1032. Delete the corresponding EXPERIMENT comments.
5. Remove jolt/jolt_buzz.wav and its README entries if the unused asset is no longer wanted. Keep the general UiSounds playback methods and milestone mapping.

A future JAVAJOLT sound plugs into JoltSounds.soundFor at line 12: add its WAV and SoundEffect constant, then an explicit JAVAJOLT branch in that switch. It is intentionally unmapped now.

## 8. Verification

The requested javac command passed at every checkpoint:

```powershell
javac -encoding UTF-8 -d out -sourcepath src src\edu\batstateu\javajolt\GameApplication.java
cmd /c "RUN_TESTS.bat < NUL"
```

The format checkpoint's complete suite passed. The new pure check also passed with `-Djava.awt.headless=true`:

```text
ALL JOLT SOUND RULE CHECKS PASSED (36)
```

The post-documentation full run passed all eight suites:

```text
ALL PROGRESSION CHECKS PASSED
ALL CONTENT COVERAGE CHECKS PASSED (21 topic/difficulty combinations, 504 questions in bank)
Checks: 491 passed, 0 failed, 491 total.
ALL CHECKS PASSED
ALL TUTORIAL LOGIC CHECKS PASSED (755)
ALL JOLT SOUND RULE CHECKS PASSED (36)
ALL SWING NAVIGATION CHECKS PASSED (55)
ALL SWING TIMER FLOW CHECKS PASSED (54)
ALL 7 ENEMY PORTRAITS PASSED
ALL JAVAJOLT TEST SUITES PASSED
```

The existing retuning script also passed with the suffix parser (747 tutorial assertions in disposable retuned copies). BalanceReport compiled to TEMP and exited 0 with its unchanged gameplay tables. `git diff --check` passed. The final source audit found no edits to ButtonSounds, Theme, JoltMessage, GameSession or gameplay services.

Live observations used actual Java Sound clips and temporary game windows, with no mocked audio or waveform unit tests:

- The game preloaded all ten effects with zero stderr diagnostic bytes; all six new files are 16-bit stereo 44.1 kHz PCM. Existing answer clips are mono and are now described accurately in README.
- 285 graphical checks verified number-first Question/Results labels and the unchanged header geometry. The progress bar and prompt remain at y=90/y=106 at default and maximized sizes.
- Real clip start counts were spark 1 at 3x, jolt 1 at 6x and surge 1 at 9x, unchanged through their intermediate values. Two breaks produced two short-circuit starts; a subsequent no-Jolt mistake produced none.
- Buzz had one start while crossing its full WAV length, remained running during the perfect hold, and was stopped at results, new quiz, breaks, session replacement, Choose another topic and Exit.
- Countdown started once in all nine question-type/difficulty combinations. A 3.4-second probe waited until the lead boundary; short probes began at the matching offset. Answer, Time Stop and next-question observations showed no carryover.
- A tutorial popup stopped playback; resuming below the lead restarted it at the preserved offset. Disposal stopped it again.
- Soft natural expiry retained the decay until the clip reached 3,552,018 microseconds. A lethal expiry entered results and cut the clip at about 3,020,997 microseconds, as permitted.

The audible mix, perceived loudness, loop seam and exact last-beep synchronization could not be judged by listening from this session. Device state and scheduling were observed; the lead and volumes remain available for owner tuning.

## 9. Manual test steps for the owner

1. Run RUN_JAVAJOLT.bat. In a quiz, listen at 3x/6x/9x and confirm the milestones do not repeat at intervening answers. Make two separated mistakes to hear one short circuit per break.
2. Listen to the buzz from the first correct answer, through a perfect hold, then at results and topic/Exit navigation. A new quiz starts without leftover buzz.
3. Let a timer expire. Compare the final beep with the displayed zero; tune the lead using section 7 if needed. Repeat with Time Stop, answer submission and a tutorial pause.
4. Listen to milestone/answer-sound overlap before tuning volumes. The overlap is intentional; existing answer sounds were not changed.

## 10. Docs updated

PROJECT_RULES maps JoltSounds and extends its Sound rule with loop/stop and transition ownership. The sound README lists all eleven WAVs by group, records source names and documents loop/stop/playFrom without rewriting unrelated sections. The previous Jolt report's player-facing examples use the new format. This report follows the brief's twelve headings without introducing phases.

## 11. Deviations from this brief, assumptions made, and risks

No requested graphical/device-state path remains unverified on the available desktop; subjective listening remains for the owner. Existing answer feedback intentionally overlaps milestone sounds and buzz. Natural expiry retains the countdown's decay, while immediate results may cut it. No lingering buzz/countdown path was observed, and the targeted source/test/doc search found no old multiplier examples.

## 12. Noticed but intentionally not changed

The original source WAVs remain outside this Git repository; only their renamed copies were added here. Existing answer/hover/click behavior, ButtonSounds, Jolt rules, Theme values, perfect hold, save behavior, damage and clock presentation remain unchanged. Existing unrelated gameplay-design-note edits and historical gameplay deviations were not refactored.
