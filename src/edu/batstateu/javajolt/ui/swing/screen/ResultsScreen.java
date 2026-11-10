
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ResultsScreen extends Screen {

    public ResultsScreen(
            GameSession session,
            boolean passed,
            int xpEarned,
            int requiredAccuracy,
            Runnable onRetry,
            Runnable onTopics,
            Runnable onMenu,
            String progressNotice) {

        // HEADER
        JLabel heading = createLabel(
                passed ? "LESSON COMPLETE!" : "KEEP PRACTICING!",
                Theme.FONT_TITLE,
                passed ? Theme.ACCENT : Theme.DANGER
        );

        JLabel subtitle = createLabel(
                session.getTopic().getLabel()
                        + " • "
                        + session.getDifficulty().getLabel(),
                Theme.FONT_BODY,
                Theme.TEXT_MUTED
        );

        JPanel header = new JPanel(
                new GridLayout(2, 1, 0, Theme.GAP_S)
        );

        header.setOpaque(false);
        header.add(heading);
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        // RESULTS PANEL
        RoundedPanel results = new RoundedPanel(
                new BorderLayout(Theme.GAP_L, Theme.GAP_L)
        );

        results.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_XL,
                        Theme.GAP_XL,
                        Theme.GAP_XL,
                        Theme.GAP_XL
                )
        );

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);


        String resultMessage;

        if (passed && xpEarned > 0) {

        resultMessage =
                "Lesson passed! Your XP has been added.";

        } else if (passed) {

        resultMessage =
                "Lesson passed! No repeat XP awarded.";

        } else {

        resultMessage =
                "Lesson not passed. Review and try again.";
        }

        JLabel message = createLabel(
                resultMessage,
                Theme.FONT_BODY,
                Theme.TEXT
        );


        body.add(message);
        body.add(Box.createVerticalStrut(Theme.GAP_L));

        // STATISTICS
        // Accuracy reuses the session's existing calculation (correct over
        // answered); the card and bar both show the counts they agree with.
        int accuracyPercent = session.getAccuracyPercent();
        int correctCount = session.getCorrectCount();
        int answeredCount = session.getAnsweredCount();

        JPanel stats = new JPanel(
                new GridLayout(1, 3, Theme.GAP_M, 0)
        );

        stats.setOpaque(false);

        stats.add(createStatCard(
                "ACCURACY",
                correctCount + "/" + answeredCount + " (" + accuracyPercent + "%)"
        ));

        stats.add(createStatCard(
                "CORRECT",
                session.getCorrectCount()
                        + " / "
                        + session.getQuestions().size()
        ));

        stats.add(createStatCard(
                "XP EARNED",
                "+" + Math.max(0, xpEarned)
        ));


        body.add(stats);
        body.add(Box.createVerticalStrut(Theme.GAP_M));

        // =====================================
        // QUIZ SCORE, PERSONAL BEST AND BEST STREAK
        // =====================================

        int quizScore = session.getScoreEarned();

        int personalBest = session.getPlayer().getHighScore(
                session.getTopic(),
                session.getDifficulty()
        );

        int bestStreak = session.getBestStreak();

        JPanel scoreStats = new JPanel(
                new GridLayout(1, 3, Theme.GAP_M, 0)
        );

        scoreStats.setOpaque(false);

        // Current quiz score
        scoreStats.add(createStatCard(
                "QUIZ SCORE",
                String.valueOf(quizScore)
        ));

        // Highest score for this topic and difficulty
        scoreStats.add(createStatCard(
                "PERSONAL BEST",
                String.valueOf(personalBest)
        ));

        // Highest consecutive-correct run of this attempt
        scoreStats.add(createStatCard(
                "BEST JOLT",
                bestStreak + "x"
        ));

        body.add(scoreStats);
        body.add(Box.createVerticalStrut(Theme.GAP_L));


        // ACCURACY BAR
        JLabel accuracyLabel = createLabel(
                "LESSON ACCURACY - " + requiredAccuracy + "% TO PASS",
                Theme.FONT_BODY,
                Theme.TEXT_MUTED
        );

        body.add(accuracyLabel);
        body.add(Box.createVerticalStrut(Theme.GAP_S));

        JProgressBar accuracyBar = new JProgressBar(0, 100);

        accuracyBar.setValue(
                accuracyPercent
        );

        accuracyBar.setForeground(
                accuracyPercent >= requiredAccuracy
                        ? Theme.ACCENT
                        : Theme.DANGER
        );

        accuracyBar.setBackground(Theme.INSET);
        accuracyBar.setBorderPainted(false);
        accuracyBar.setStringPainted(true);

        accuracyBar.setString(
                accuracyPercent + "% | Passing: "
                        + requiredAccuracy + "%"
        );

        body.add(accuracyBar);

        
        body.add(Box.createVerticalStrut(Theme.GAP_M));

        JLabel resultStatus = createLabel(
                passed ? "STATUS: PASSED" : "STATUS: NOT PASSED",
                Theme.FONT_BODY.deriveFont(Font.BOLD, 14f),
                passed ? Theme.ACCENT : Theme.DANGER
        );

        body.add(resultStatus);


        if (!passed) {

        boolean outOfHp =
                !session.getPlayer().isAlive();

        boolean lowAccuracy =
                accuracyPercent < requiredAccuracy;

        String failureReason;

        if (outOfHp && lowAccuracy) {

                failureReason =
                        "WHY YOU FAILED: HP reached zero AND "
                        + "accuracy was below "
                        + requiredAccuracy + "%.";

        } else if (outOfHp) {

                failureReason =
                        "WHY YOU FAILED: HP reached zero. "
                        + "Survive the quiz to pass.";

        } else {

                failureReason =
                        "WHY YOU FAILED: Accuracy was "
                        + accuracyPercent
                        + "%; at least "
                        + requiredAccuracy
                        + "% is required.";
        }

        body.add(
                Box.createVerticalStrut(Theme.GAP_S)
        );

        body.add(
                createLabel(
                        failureReason,
                        Theme.FONT_BODY,
                        Theme.DANGER
                )
        );
        }


        // New level / newly unlocked power-ups; rendered only when the
        // controller produced a notice, so ordinary runs show nothing extra.
        if (progressNotice != null && !progressNotice.isBlank()) {
            body.add(Box.createVerticalStrut(Theme.GAP_M));
            body.add(createLabel(
                    progressNotice,
                    Theme.FONT_BODY.deriveFont(Font.BOLD, 14f),
                    Theme.ACCENT
            ));
        }

        body.add(Box.createVerticalStrut(Theme.GAP_L));

        JLabel mistakes = createLabel(
                "Mistakes: " + session.getMistakeCount(),
                Theme.FONT_BODY,
                Theme.TEXT_MUTED
        );

        body.add(mistakes);

        // Defeat names the quiz opponent when the player ran out of HP.
        if (!session.getPlayer().isAlive()) {
            body.add(Box.createVerticalStrut(Theme.GAP_S));
            body.add(createLabel(
                    "Defeated by " + session.getEnemy().getName(),
                    Theme.FONT_BODY,
                    Theme.DANGER
            ));
        }

        results.add(body, BorderLayout.CENTER);

        add(results, BorderLayout.CENTER);

        // ACTION BUTTONS
        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        Theme.GAP_S,
                        0
                )
        );

        actions.setOpaque(false);

        RoundedButton retryButton = new RoundedButton(
                "Try Again",
                ButtonStyle.SECONDARY
        );

        RoundedButton menuButton = new RoundedButton(
                "Main Menu",
                ButtonStyle.SECONDARY
        );

        RoundedButton topicsButton = new RoundedButton(
                "Choose Topic",
                ButtonStyle.PRIMARY
        );

        retryButton.addActionListener(
                e -> onRetry.run()
        );

        menuButton.addActionListener(
                e -> onMenu.run()
        );

        topicsButton.addActionListener(
                e -> onTopics.run()
        );

        actions.add(retryButton);
        actions.add(menuButton);
        actions.add(topicsButton);

        add(actions, BorderLayout.SOUTH);
    }

    // REUSABLE STAT CARD
    private RoundedPanel createStatCard(
            String title,
            String value) {

        RoundedPanel card = new RoundedPanel(
                new GridLayout(2, 1, 0, Theme.GAP_S)
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_M,
                        Theme.GAP_M,
                        Theme.GAP_M,
                        Theme.GAP_M
                )
        );

        card.add(createLabel(
                title,
                Theme.FONT_BODY.deriveFont(Font.BOLD, 12f),
                Theme.TEXT_MUTED
        ));

        card.add(createLabel(
                value,
                Theme.FONT_HEADING,
                Theme.TEXT
        ));

        return card;
    }

    private JLabel createLabel(
            String text,
            Font font,
            Color color) {

        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);

        return label;
    }
}
