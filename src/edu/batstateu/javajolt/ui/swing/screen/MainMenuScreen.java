package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.enums.Achievement;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.service.LevelService;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.CharacterPlaceholder;
import edu.batstateu.javajolt.ui.swing.component.Divider;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.enums.Difficulty;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuScreen extends Screen {
    private final JLabel expLabel = createLabel("EXP: --");
    private final JLabel levelLabel = createLabel("LEVEL: --");

    private final JLabel playerNameLabel = createLabel("PLAYER");

    private final JProgressBar xpProgressBar =
            new JProgressBar();

    private final JLabel xpProgressLabel =
            new JLabel();

        private int countCompleted(
        Player player,
        Difficulty difficulty) {

    int count = 0;

    for (Topic topic : Topic.values()) {
        if (player.isComplete(topic, difficulty)) {
            count++;
        }
    }

    return count;
}

    private final JLabel firstLessonStatus = new JLabel("LOCKED");
    private final JLabel threeLessonsStatus = new JLabel("LOCKED");
    private final JLabel beginnerMasterStatus = new JLabel("LOCKED");

        private final JLabel beginnerProgressLabel =
                new JLabel("0 / 7 COMPLETED");

        private final JLabel intermediateProgressLabel =
                new JLabel("0 / 7 COMPLETED");

        private final JLabel advancedProgressLabel =
                new JLabel("0 / 7 COMPLETED");

        public MainMenuScreen(
                Player player,
                Runnable onLessonsClicked,
                Runnable onSave,
                Runnable onProgress,
                Runnable onAchievements,
                Runnable onExit) {

        refreshInternal(player);

        add(createTop(), BorderLayout.NORTH);
        add(createBody(
                onLessonsClicked,
                onSave,
                onProgress,
                onAchievements,
                onExit
        ), BorderLayout.CENTER);
    }

    public void refresh(Player player) {
                refreshInternal(player);
        }

        private void refreshInternal(Player player) {

        int xp = Math.max(0, player.getXp());

        int level = LevelService.calculateLevel(xp);

        // Calculate XP needed for the current level
        int currentLevelXp = Math.toIntExact(
        LevelService.totalXpForLevel(level)
        );

        int nextLevelXp = Math.toIntExact(
                LevelService.totalXpForLevel(level + 1)
        );

        int xpEarned = xp - currentLevelXp;
        int xpRequired = nextLevelXp - currentLevelXp;

        playerNameLabel.setText(player.getName());

        levelLabel.setText("LEVEL " + level);

        expLabel.setText(xp + " TOTAL XP");

        xpProgressBar.setMaximum(xpRequired);
        xpProgressBar.setValue(xpEarned);

        xpProgressLabel.setText(
                (nextLevelXp - xp)
                + " XP UNTIL LEVEL " + (level + 1)
        );


        // Update learning progress for each difficulty
        int completedLessons = countCompleted(
                player, Difficulty.BEGINNER
        );

        beginnerProgressLabel.setText(
                completedLessons + " / "
                + Topic.values().length + " COMPLETED"
        );

        intermediateProgressLabel.setText(
                countCompleted(player, Difficulty.INTERMEDIATE)
                + " / " + Topic.values().length + " COMPLETED"
        );

        advancedProgressLabel.setText(
                countCompleted(player, Difficulty.ADVANCED)
                + " / " + Topic.values().length + " COMPLETED"
        );


        // Update achievement progress

        updateMilestone(
                firstLessonStatus,
                Achievement.FIRST_STEPS,
                player
        );

        updateMilestone(
                threeLessonsStatus,
                Achievement.STEADY_LEARNER,
                player
        );

        updateMilestone(
                beginnerMasterStatus,
                Achievement.BEGINNER_MASTER,
                player
        );

    }


        private void updateMilestone(
                JLabel status,
                Achievement achievement,
                Player player) {

        boolean unlocked =
                achievement.isUnlocked(player);

        int progress =
                achievement.getProgress(player);

        int target =
                achievement.getTarget();

        status.setText(
                unlocked
                        ? "UNLOCKED"
                        : progress + " / " + target
        );

        status.setForeground(
                unlocked
                        ? new java.awt.Color(0x8FD3A3)
                        : Theme.TEXT_MUTED
        );
        }
    private JPanel createTop() {
        JPanel top = new JPanel(new BorderLayout(0, Theme.GAP_L));
        top.setOpaque(false);
        top.add(createHeader(), BorderLayout.CENTER);
        top.add(new Divider(), BorderLayout.SOUTH);
        return top;
    }


    private JPanel createHeader() {

        // PLAYER AVATAR
        CharacterPlaceholder character =
                new CharacterPlaceholder();

        character.setPreferredSize(
                new Dimension(110, 110)
        );

        // PLAYER INFORMATION
        JPanel stats = new JPanel();

        stats.setLayout(
                new BoxLayout(stats, BoxLayout.Y_AXIS)
        );

        stats.setOpaque(false);

        playerNameLabel.setFont(Theme.FONT_HEADING);
        playerNameLabel.setForeground(Theme.TEXT);

        levelLabel.setFont(Theme.FONT_BODY);
        levelLabel.setForeground(Theme.ACCENT);

        expLabel.setFont(Theme.FONT_BODY);
        expLabel.setForeground(Theme.TEXT_MUTED);

        xpProgressLabel.setFont(Theme.FONT_BODY);
        xpProgressLabel.setForeground(Theme.TEXT_MUTED);

        // XP PROGRESS BAR
        xpProgressBar.setForeground(Theme.ACCENT);
        xpProgressBar.setBackground(Theme.INSET);

        xpProgressBar.setBorderPainted(false);
        xpProgressBar.setStringPainted(false);

        xpProgressBar.setPreferredSize(
                new Dimension(360, 12)
        );

        xpProgressBar.setMaximumSize(
                new Dimension(420, 12)
        );

        // ARRANGE COMPONENTS
        stats.add(playerNameLabel);

        stats.add(
                Box.createVerticalStrut(Theme.GAP_S)
        );

        stats.add(levelLabel);

        stats.add(
                Box.createVerticalStrut(Theme.GAP_S)
        );

        stats.add(expLabel);

        stats.add(
                Box.createVerticalStrut(Theme.GAP_S)
        );

        stats.add(xpProgressBar);

        stats.add(
                Box.createVerticalStrut(Theme.GAP_S)
        );

        stats.add(xpProgressLabel);

        JPanel header = new JPanel(
                new BorderLayout(Theme.GAP_L, 0)
        );

        header.setOpaque(false);

        header.add(character, BorderLayout.WEST);
        header.add(stats, BorderLayout.CENTER);

        return header;
    }



        private JPanel createBody(
                Runnable onLessonsClicked,
                Runnable onSave,
                Runnable onProgress,
                Runnable onAchievements,
                Runnable onExit) {

        JPanel body = new JPanel(
                new BorderLayout(0, Theme.GAP_M)
        );

        body.setOpaque(false);

        // Learning overview at the top
        body.add(
                createProgressOverview(),
                BorderLayout.NORTH
        );

        // Preserve existing achievements and navigation
        JPanel columns = new JPanel(
                new GridLayout(1, 2, Theme.GAP_L, 0)
        );

        columns.setOpaque(false);

        columns.add(createBadgesPanel());

        columns.add(createMenuButtons(
                onLessonsClicked,
                onSave,
                onProgress,
                onAchievements,
                onExit
        ));

        body.add(columns, BorderLayout.CENTER);

        return body;
        }

        
        private JPanel createProgressOverview() {

        JPanel overview = new JPanel(
                new GridLayout(1, 3, Theme.GAP_M, 0)
        );

        overview.setOpaque(false);

        overview.add(createProgressCard(
                "BEGINNER",
                beginnerProgressLabel
        ));

        overview.add(createProgressCard(
                "INTERMEDIATE",
                intermediateProgressLabel
        ));

        overview.add(createProgressCard(
                "ADVANCED",
                advancedProgressLabel
        ));

        return overview;
        }


        private RoundedPanel createProgressCard(
                String title,
                JLabel value) {

        RoundedPanel card = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_S)
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_M,
                        Theme.GAP_L,
                        Theme.GAP_M,
                        Theme.GAP_L
                )
        );

        JLabel heading = new JLabel(title);

        heading.setFont(
                Theme.FONT_BODY.deriveFont(
                        java.awt.Font.BOLD, 12f
                )
        );

        heading.setForeground(Theme.TEXT_MUTED);

        value.setFont(Theme.FONT_HEADING);
        value.setForeground(Theme.ACCENT);

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
        }




    private RoundedPanel createBadgesPanel() {

        RoundedPanel panel = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_L)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_L,
                        Theme.GAP_L,
                        Theme.GAP_L,
                        Theme.GAP_L
                )
        );

        // HEADER
        JPanel header = new JPanel();
        header.setLayout(
                new BoxLayout(header, BoxLayout.Y_AXIS)
        );
        header.setOpaque(false);

        JLabel title = createLabel("ACHIEVEMENTS");

        JLabel subtitle = new JLabel(
                "Complete lessons to unlock milestones."
        );

        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(Theme.GAP_S));
        header.add(subtitle);

        panel.add(header, BorderLayout.NORTH);

        // ACHIEVEMENT CARDS
        JPanel achievements = new JPanel(
                new GridLayout(3, 1, 0, Theme.GAP_S)
        );

        achievements.setOpaque(false);

        achievements.add(
                createMilestoneRow(
                        "FIRST STEPS",
                        "Complete your first Beginner lesson",
                        firstLessonStatus
                )
        );

        achievements.add(
                createMilestoneRow(
                        "STEADY LEARNER",
                        "Complete 3 Beginner lessons",
                        threeLessonsStatus
                )
        );

        achievements.add(
                createMilestoneRow(
                        "BEGINNER MASTER",
                        "Complete all 7 Beginner lessons",
                        beginnerMasterStatus
                )
        );

        panel.add(achievements, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMilestoneRow(
            String name,
            String description,
            JLabel status) {

        JPanel card = new JPanel(
                new BorderLayout(Theme.GAP_M, 0)
        );

        card.setBackground(Theme.INSET);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Theme.INSET_BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                12, 14, 12, 14
                        )
                )
        );

        JPanel information = new JPanel();
        information.setLayout(
                new BoxLayout(information, BoxLayout.Y_AXIS)
        );

        information.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(Theme.FONT_BODY.deriveFont(
                java.awt.Font.BOLD
        ));
        nameLabel.setForeground(Theme.TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(
                Theme.FONT_BODY.deriveFont(12f)
        );
        descriptionLabel.setForeground(Theme.TEXT_MUTED);

        information.add(nameLabel);
        information.add(
                Box.createVerticalStrut(Theme.GAP_S)
        );
        information.add(descriptionLabel);

        status.setFont(
                Theme.FONT_BODY.deriveFont(
                        java.awt.Font.BOLD, 12f
                )
        );

        status.setForeground(Theme.TEXT_MUTED);

        card.add(information, BorderLayout.CENTER);
        card.add(status, BorderLayout.EAST);

        return card;
    }



        private JPanel createMenuButtons(
                Runnable onLessonsClicked,
                Runnable onSave,
                Runnable onProgress,
                Runnable onAchievements,
                Runnable onExit) {

    // Main navigation card
    RoundedPanel panel = new RoundedPanel(
            new BorderLayout(0, Theme.GAP_L)
    );

    panel.setBorder(
            BorderFactory.createEmptyBorder(
                    Theme.GAP_L,
                    Theme.GAP_L,
                    Theme.GAP_L,
                    Theme.GAP_L
            )
    );

    // HEADER
    JPanel header = new JPanel();
    header.setLayout(
            new javax.swing.BoxLayout(
                    header,
                    javax.swing.BoxLayout.Y_AXIS
            )
    );
    header.setOpaque(false);

    JLabel heading = new JLabel("CONTINUE LEARNING");
    heading.setFont(Theme.FONT_HEADING);
    heading.setForeground(Theme.TEXT);

    JLabel subtitle = new JLabel(
            "Choose your next Java learning activity."
    );
    subtitle.setFont(Theme.FONT_BODY);
    subtitle.setForeground(Theme.TEXT_MUTED);

    header.add(heading);
    header.add(
            javax.swing.Box.createVerticalStrut(Theme.GAP_S)
    );
    header.add(subtitle);

    panel.add(header, BorderLayout.NORTH);

    // BUTTONS
    RoundedButton lessonsButton =
            new RoundedButton(
                    "Explore Lessons",
                    ButtonStyle.PRIMARY
            );

    RoundedButton progressButton =
            new RoundedButton(
                    "View Progress",
                    ButtonStyle.SECONDARY
            );

    RoundedButton saveButton =
            new RoundedButton(
                    "Save Game",
                    ButtonStyle.SECONDARY
            );

    RoundedButton achievementsButton =
            new RoundedButton(
                    "Achievements",
                    ButtonStyle.SECONDARY
            );

    RoundedButton exitButton =
            new RoundedButton(
                    "Exit Game",
                    ButtonStyle.DANGER
            );

    // Preserve existing button functionality
    lessonsButton.addActionListener(
            e -> onLessonsClicked.run()
    );

    progressButton.addActionListener(
            e -> onProgress.run()
    );

    saveButton.addActionListener(
            e -> onSave.run()
    );

    achievementsButton.addActionListener(
            e -> onAchievements.run()
    );

    exitButton.addActionListener(
            e -> onExit.run()
    );

    // Main button
    JPanel buttons = new JPanel(
            new BorderLayout(0, Theme.GAP_M)
    );
    buttons.setOpaque(false);

    lessonsButton.setPreferredSize(
            new Dimension(320, 65)
    );

    buttons.add(lessonsButton, BorderLayout.NORTH);

    // Supporting buttons
    JPanel secondaryButtons = new JPanel(
            new GridLayout(2, 2, Theme.GAP_S, Theme.GAP_S)
    );
    secondaryButtons.setOpaque(false);

    secondaryButtons.add(progressButton);
    secondaryButtons.add(saveButton);
    secondaryButtons.add(achievementsButton);
    secondaryButtons.add(exitButton);

    buttons.add(secondaryButtons, BorderLayout.CENTER);

    JPanel holder = new JPanel(new GridBagLayout());
    holder.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.CENTER;

    holder.add(buttons, gbc);

    panel.add(holder, BorderLayout.CENTER);

    return panel;
}


    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_HEADING);
        label.setForeground(Theme.TEXT);
        return label;
    }
}
