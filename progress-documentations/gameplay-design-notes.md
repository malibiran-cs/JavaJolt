# JavaJolt — Gameplay Design Notes

**Draft:** 0.1  
**Scope:** Core mechanics only. Audio, music, animation, visual effects, and other presentation work are intentionally out of scope for this draft.  
**Purpose:** Organize the proposed reward, punishment, progression, and enemy mechanics into a design that can later be translated into implementation tasks.

> This is a design draft, not an instruction to implement every listed feature. Existing behavior is identified separately from proposed behavior. Open decisions should be settled before code is changed.

---

## 1. Core Design Direction

JavaJolt should remain a Java-learning quiz game first. The quiz is the main activity and the player's ability to complete lessons is the primary measure of success. Enemies, HP, rewards, streaks, and power-ups should make answering questions more engaging without turning the learning activity into a distraction from learning.

The intended experience is not one in which the game constantly explains how accomplished the player should feel. The player should be allowed to recognize their own improvement. The mechanics should provide concrete feedback and meaningful progression, while leaving room for the player's personal experience to supply part of the satisfaction.

The design can be understood through five connected systems:

1. **Long-term progression:** XP and levels give the player a persistent reason to keep completing lessons.
2. **Tactical rewards:** Power-ups give level progression a practical effect during later quizzes.
3. **Run performance:** Score and streaks give the player a measurable way to perform better in an individual quiz.
4. **Survival and pressure:** HP, enemy attacks, and possible status effects make mistakes matter.
5. **Learning progression:** Topics and difficulty tiers remain the actual content path. Game mechanics must respect that path rather than bypass it.

The enemy does not necessarily need to be a conventional opponent with its own HP bar. It can function as the source of pressure and obstacles. The player's objective remains to answer questions and clear the lesson; completing the quiz can represent overcoming the enemy.

## 2. Existing Project Behavior

The following describes the uploaded project version, not the proposed future design.

| System | Current behavior | Design implication |
|---|---|---|
| HP | Player starts with 10 HP. HP resets at the beginning of each quiz. A wrong answer deals 2 damage. The quiz ends when the player is no longer alive. | HP is currently a simple mistake budget, not a developed combat system. |
| Passing | A player must remain alive and reach at least 70% accuracy. | Accuracy and HP are two separate pass conditions, but the accuracy condition currently tends to become decisive first. |
| XP | Each correct answer awards XP while the topic/difficulty pair has not yet been completed. The amount is calculated from the quiz question count, targeting roughly 25 XP per quiz, with a per-answer range of 1–5 XP. | Repeat attempts after first completion no longer award ordinary XP. Failed attempts before first completion can still award XP. |
| Completion bonus | Passing a topic/difficulty for the first time awards 50 additional XP. | The completion bonus is protected against repeat awards. |
| Level | Level 1 is below 100 XP. Level 2 begins at 100 XP; each later level requires another 50 XP. | Level currently communicates progress but does not unlock a mechanical benefit. |
| Score | A correct answer awards 10 points. `GameSession` tracks the score for one quiz. `Player.score` accumulates points across quiz attempts and is now persisted. The player also stores a best score for each topic/difficulty pair. | Per-quiz scores remain the basis for future topic records; the cumulative score remains a separate overall total. High scores are saved but not yet displayed. |
| Persistence | Name, XP, cumulative score, current difficulty, completed topic/difficulty pairs, and best score per topic/difficulty are saved. HP remains session-only. | New score fields default safely when loading older save files that do not contain them. |
| Topic progression | Topics unlock in enum order. Previously completed topics remain available for replay. Completing every topic in a difficulty advances the player to the next difficulty. | The learning path is intentionally linear and should remain the authority for content access. |
| Enemy | The Swing question screen has a character placeholder on the enemy side, but the code has no enemy gameplay entity, enemy attack logic, or enemy-specific abilities. | Enemy variety is currently a visual concept, not a mechanical system. |
| Timer | There is no answer timer or timed penalty in the current gameplay logic. | Time pressure would be a new mechanic, not an adjustment to an existing one. |
| Badges | The main menu displays milestone progress for completing 1, 3, and 7 Beginner lessons. These are derived from completion progress rather than a separate badge inventory. | A broader badge/achievement system is still a design opportunity. |

### 2.1 Replay XP behavior — decision implemented

**Completed topic/difficulty pairs no longer award ordinary XP on replay.** `LessonRunnerService.submitAnswer()` now checks the player's saved completion progress before awarding per-answer XP. The 50 XP completion bonus remains protected by the existing first-completion check.

One distinction remains intentional: failed attempts on a topic/difficulty pair that has not yet been completed can still award per-answer XP. The chosen rule prevents farming by repeatedly replaying already-cleared content; it does not yet eliminate all XP gains from unsuccessful first-clear attempts. The question selector's question-variety behavior is separate from this reward rule.

### 2.2 The current HP/pass-mark relationship

With the current quiz selection target of up to 12 questions, 70% accuracy requires at least 9 correct answers in a full 12-question quiz. That means 4 mistakes are already enough to make passing impossible, while the current HP rule permits 4 mistakes and only eliminates the player on the fifth.

In a normal 12-question quiz, HP therefore does not usually determine whether the player can pass; the accuracy requirement has already done so. HP can still end a failed run early and stop further questions, but it is not yet a meaningful parallel survival challenge.

This is not necessarily a defect. It does mean that simply increasing maximum HP may have little effect until damage sources, pass rules, or enemy mechanics become more substantial.

The long-term design goal of making HP a genuine survival condition is sound: a player should be able to lose because of enemy pressure, not only because their accuracy fell below the pass mark. However, the current accuracy calculation uses the total quiz question count as its denominator, including questions left unanswered after an early defeat. That means an HP defeat can also lower the final accuracy result. When enemy attacks are introduced, test whether HP can meaningfully become the deciding loss condition without making the accuracy system misleading. No HP values or pass rules were changed in this update; reminders were added beside the relevant gameplay rules.

### 2.3 Score has two scopes — persistence implemented

`GameSession.scoreEarned` records points earned in the current quiz. `Player.score` accumulates points across quizzes and is now saved between launches. The question screen displays this cumulative total, while the results screen displays the current quiz's score.

The player also stores the best `GameSession.scoreEarned` for each topic/difficulty pair. These records are persisted now as groundwork for a future high-score screen; no leaderboard UI or ranking rules have been introduced. A quiz attempt can establish a high score whether it passes or fails, because this record measures score performance rather than lesson completion.

## 3. Reward System

### 3.1 XP: make persistent progression produce a concrete benefit

XP should represent long-term learning progress. The current level display already gives XP a visible destination, but the level currently has no effect on gameplay. A level-up can become more meaningful by unlocking a fixed set of power-ups at selected level milestones.

The proposed direction is deliberately limited:

- Define a small, fixed pool of power-ups. There is no need for a large item catalogue, random loot, crafting, or an inventory economy.
- Unlock each power-up at a selected level threshold.
- Once unlocked, the power-up remains available on future quizzes.
- Limit its use during a quiz. The exact rule still needs to be decided: one use of each unlocked power-up per quiz, or one total power-up activation per quiz.
- Reset the usage limit when a new quiz session begins.

Possible examples—not a final list—include:

| Power-up | Possible purpose | Learning/balance consideration |
|---|---|---|
| Hint | Give a conceptual clue or remove one incorrect option. | It should help the player reason, not simply reveal the answer. |
| Time Stop | Pause an active answer timer. | Useful only if a timer is implemented. |
| Point Boost | Increase the score from one correct answer or a short window. | Keep it focused on score so it does not create an XP multiplication loop. |
| Shield | Block or reduce the next enemy attack. | Gives the player a reason to use an ability strategically rather than automatically. |

Because the unlocked set can be calculated from the player's current level, permanent unlocks may not need their own saved inventory. Temporary usage should live in the current quiz session. This is one way to keep the system manageable.

### 3.2 HP as a possible progression reward

Increasing maximum HP as the player gains levels is a possible reward, but it should be treated as a balancing decision rather than the default XP benefit.

For HP progression to matter, HP must be relevant to the threats the player faces. If the only failure condition is still a 70% accuracy threshold, extra health may let the player survive longer without making a failed quiz passable. If enemies gain different attacks, status effects, or timed pressure, a larger health pool can become a more meaningful defensive resource.

A conservative approach is to keep starting HP fixed for the first version of enemy mechanics, then evaluate whether level-based maximum HP is actually needed. If HP does scale, define a maximum HP value separately from current HP and restore the player to that maximum at the start of each quiz.

### 3.3 Score: make performance measurable

Score should be distinct from XP:

- **XP** measures long-term progression and should unlock permanent benefits.
- **Score** measures performance in a particular quiz or attempt.
- **Streak** describes the player's current run of consecutive correct answers and can modify score or trigger milestones.

A streak mechanic is a natural extension of the existing +10 points per correct answer:

- Correct answer: increase the current streak.
- Incorrect answer: reset the current streak.
- Reaching selected streak milestones can grant bonus score or a streak badge/achievement.
- A best-streak value can be shown in the results for that attempt.

The score reward should be easy to understand and should not encourage careless guessing. A simple milestone bonus is likely easier to balance than a complicated multiplier at the beginning.

### 3.4 High scores by topic

A high score for each topic—and potentially each difficulty—would give score a concrete purpose beyond the immediate results screen. It would also give players a reason to replay completed material even when its completion bonus is no longer available.

The persistence groundwork is now in place: `SaveData` carries a topic/difficulty high-score map, and `SaveService` encodes and decodes it. High scores are recorded after each completed quiz attempt. The UI, ranking presentation, and any tie-breaking rules remain future work.

## 4. Punishment and Enemy System

### 4.1 Enemy identity should affect mechanics, not only appearance

The proposed theme is that each Java topic has an enemy identity. That enemy can evolve as the player progresses from Beginner to Intermediate to Advanced. The creature remains recognizably the same topic enemy, but its later forms become more threatening or gain more complex behavior.

This creates two distinct design axes:

- **Topic determines enemy identity:** for example, the Variables topic might have a pig-like enemy, while Loops might have a snake-like enemy.
- **Difficulty determines the enemy's evolution:** the same topic enemy becomes stronger or gains a more demanding ability at later difficulties.

The project also has `ChallengeTier` values—Foundational, Application, and Challenge—which organize question difficulty within a quiz. These should not be confused with the main Beginner/Intermediate/Advanced progression. The main difficulty can define the enemy form; challenge tier can provide a natural ramp in question complexity during the quiz.

### 4.2 Enemy damage can vary by difficulty

The simplest first step is to retain the existing rule that a wrong answer causes an enemy attack, but make the damage depend on the current difficulty or enemy profile.

This would make the difficulty label mechanically meaningful without requiring a complex combat system. It also gives the HP system a more direct purpose. Exact damage values should be tuned after testing, not selected solely because they look proportionate on paper.

### 4.3 Give selected enemies signature effects

After basic damage variation works, enemies can receive one recognizable behavior each. Examples from the current concept:

| Enemy concept | Signature behavior | Important rule to define |
|---|---|---|
| Variables enemy | Higher direct damage as difficulty increases. | How damage changes between Beginner, Intermediate, and Advanced. |
| Loops enemy | Poison: additional HP loss over a defined number of subsequent turns. | Duration, damage per turn, whether another poison hit refreshes or stacks it, and how it is removed. |
| Another enemy | Stun: the enemy gets an extra attack or temporarily restricts an action. | Avoid skipping a learning question entirely; define exactly what action is restricted. |

Each effect needs a clear trigger, duration, consequence, and end condition. Avoid stacking several effects on one enemy in the first implementation. One distinct signature mechanic is enough to make an enemy feel different.

For this type of quiz game, a stun should not casually erase a question or prevent the player from engaging with the learning content. A more suitable interpretation could be an extra enemy attack, a temporary restriction on power-up use, or another bounded consequence that does not remove the question itself.

### 4.4 Enemies can change the rules of an encounter

More advanced enemy mechanics could affect the conditions of answering a question, such as:

- A timer or attack countdown that creates pressure.
- A limited change to the available choices.
- A follow-up or bonus question after a particular event.
- Periodic damage that the player must plan around.

These are higher-complexity ideas. They should be introduced only when their consequences for question selection, accuracy, scoring, and lesson completion are understood.

For example, adding an extra question changes the quiz's question count and potentially its accuracy denominator and XP calculation. Hiding choices must never make the correct answer impossible to select. Periodic damage should have a clear warning or trigger, rather than feeling like arbitrary punishment.

## 5. Time Pressure Without Undermining Learning

A timer can create tension, but JavaJolt is still intended to teach Java. The player needs enough time to read, reason, and learn from the explanation. A timer that rewards only fast guessing would work against that purpose.

A possible progression is to introduce pressure gradually:

- **Beginner:** use a generous or soft timer. The player may still answer after it expires; the timer could trigger a limited enemy consequence or remove a speed bonus rather than invalidate the answer.
- **Intermediate:** use a more noticeable countdown and a defined consequence for timeout.
- **Advanced:** apply stricter timing or combine the timer with an enemy's signature behavior.

This is a design option, not a finalized rule. The exact timer should also account for question type. A multiple-choice question and a multiline code-writing question do not demand the same amount of time. A single fixed duration for every question would be unfair, especially for code questions.

The beginner experience does not have to be consequence-free to be welcoming. The goal is to create manageable pressure without punishing a learner simply for taking time to understand a concept.

## 6. How the Systems Connect

The systems should be designed as one loop rather than separate features added independently.

1. **Start a quiz:** create the `GameSession`, reset HP, identify the topic/difficulty enemy, and reset temporary streak and power-up usage.
2. **Present a question:** show the current question and, if applicable, its time or enemy-pressure state.
3. **Resolve the answer:** determine whether the answer is correct.
4. **On a correct answer:** award score, award XP only if the topic/difficulty pair has not yet been completed, increase streak, and update any enemy pressure mechanic that a correct answer should counter.
5. **On an incorrect answer:** apply the normal damage rule, reset streak, and trigger the enemy's defined attack or status effect.
6. **Apply ongoing effects:** update poison, timers, shields, or other temporary effects according to their durations and triggers.
7. **Continue or end:** proceed until all questions are answered or the player's HP reaches zero.
8. **Evaluate the lesson:** apply the existing pass requirements, unless those requirements are deliberately revised.
9. **Grant completion progress:** on the first successful completion of a topic/difficulty pair, record it, award the one-time completion bonus, and unlock the next progression step.
10. **Show results and save:** report the current attempt's performance, XP gained, score, streak, and any newly unlocked reward. Save only the values intended to persist.

The enemy is therefore a pressure and punishment system; the quiz remains the central objective. The game does not need to add a separate enemy-HP victory condition unless that condition contributes a meaningful decision or mechanic.

## 7. Implementation Boundaries

The current `PROJECT_RULES.md` already defines the most important architectural constraints:

- Gameplay rules belong in services/entities, not Swing screens.
- `Player` and `GameSession` hold state; services apply rules.
- `SaveService` is the only class that reads or writes the save file.
- Screens display state and report player actions; they do not directly calculate XP, damage, status effects, or progression.

A likely responsibility map for future work is:

| Concern | Likely owner | Notes |
|---|---|---|
| Current/max HP and persistent XP | `Player` | Keep state changes behind clear methods. HP remains session-only unless deliberately changed. |
| Quiz score, current/best streak, used power-ups, temporary effects | `GameSession` | These reset with a new quiz unless a specific value is intended to persist. |
| XP awards, score awards, streak rules, pass evaluation | Existing gameplay service or a focused reward service | Do not place these calculations in Swing screens. Extract a new service when the responsibilities become too broad. |
| Enemy profile and attack/effect resolution | A focused gameplay service if the behavior warrants it | The existing project has no enemy model yet. Avoid building a large combat framework before the first enemy behavior is settled. |
| Level calculation and level-based unlock rules | `LevelService` or a focused progression/power-up service | Keep level thresholds defined in one place. |
| Persistent high scores or permanent achievement records | `SaveData` and `SaveService` | Any new saved field must preserve compatibility with older saves. |
| Displaying XP, score, streak, HP, abilities, and results | Swing screens | Screens should render values supplied by the game layer. |

This is a responsibility guide, not a request to create all of these classes immediately. Follow the existing project rule against speculative abstractions: implement the smallest coherent version, then extract a focused service when a real responsibility appears.

## 8. Decisions to Settle Before Implementation

The following questions have direct consequences for the code and should be decided deliberately:

1. **Replay XP:** DECIDED — completed topic/difficulty pairs no longer award ordinary XP. Failed attempts before the first successful completion still award per-answer XP; decide later whether this needs a stricter rule.
2. **Power-up limit:** Is the rule one use of each unlocked ability per quiz, or one total ability use per quiz?
3. **HP progression:** Should max HP increase with levels, or should HP stay fixed while enemy attacks become more varied?
4. **Score scope:** IMPLEMENTED — the current total score persists across launches, and the best per-quiz score is saved separately for each topic/difficulty pair. Decide later how these should be presented in the UI.
5. **Streak consequence:** Does a streak grant only score, or can it also affect enemy pressure or grant a defensive benefit?
6. **Enemy identity:** Does each topic get one enemy with difficulty-based evolutions, and should each enemy have only one signature mechanic initially?
7. **Timer policy:** Is the timer a hard deadline, a soft pressure mechanic, or a source of optional speed bonuses? How does it differ for code questions?
8. **Pass conditions:** Should success continue to require both 70% accuracy and surviving, especially if HP and enemy attacks become more important?
9. **High-score presentation:** STORAGE DECIDED — best score is stored per topic/difficulty pair. Decide later whether the UI should show topic records, difficulty records, an overall ranking, or a combination.

## 9. Suggested Development Order

A staged implementation will make balancing and debugging easier:

**Stage 1 — Clarify the existing economy.** The replay-XP rule and score persistence are now implemented. Keep the current HP/pass-mark rules unchanged for now, with a code reminder to revisit them when enemy pressure mechanics are designed.

**Stage 2 — Add a small performance loop.** Implement streak tracking and decide how the already-persisted cumulative and per-topic high scores should be displayed. Keep the underlying per-quiz score as the performance measure.

**Stage 3 — Make one enemy mechanically distinct.** Start with difficulty-based damage or one simple signature effect. Test its trigger, duration, and interaction with HP before defining the rest of the enemy roster.

**Stage 4 — Introduce limited power-ups.** Add a small fixed pool with level-based unlocks and clearly defined per-quiz usage limits. Ensure abilities help players make decisions rather than bypass the Java question.

**Stage 5 — Test time pressure.** Start with a soft timer or limited timeout consequence. Test separately against multiple-choice, identification, and code questions before making it stricter.

**Stage 6 — Expand enemy variety and polish presentation.** Once the mechanics are enjoyable and balanced, build out the remaining topic enemies, badges, animations, sound effects, and other presentation work around the established rules.

---

## 10. Design Summary

JavaJolt's progression should give XP a practical purpose, its score should represent measurable quiz performance, and its enemies should create distinct forms of pressure. Streaks can connect moment-to-moment performance to score rewards, while power-ups can connect persistent levels to tactical choices in later quizzes.

The central constraint is that all of these mechanics must serve the learning loop. The player should still be rewarded for understanding Java, have a fair chance to think through questions, and progress through topics and difficulties in the intended order. The best first version is not the one with the most mechanics; it is the smallest set of connected mechanics that makes each quiz feel like a meaningful encounter.
