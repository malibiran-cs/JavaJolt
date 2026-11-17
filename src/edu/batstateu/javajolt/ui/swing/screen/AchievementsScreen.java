
package edu.batstateu.javajolt.ui.swing.screen;

import java.util.Arrays;
import javax.swing.JProgressBar;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Achievement;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AchievementsScreen extends Screen {

    private static final Color UNLOCKED_COLOR =
            new Color(0x8FD3A3);

    public AchievementsScreen(
            Player player,
            Runnable onBack) {

        // Count unlocked achievements
        int unlocked = 0;

        for (Achievement achievement : Achievement.values()) {
            if (achievement.isUnlocked(player)) {
                unlocked++;
            }
        }

        // =====================================
        // HEADER
        // =====================================

        JPanel header = new JPanel(
                new BorderLayout(Theme.GAP_M, Theme.GAP_M)
        );

        header.setOpaque(false);

        JLabel heading = new JLabel("ACHIEVEMENTS");
        heading.setFont(Theme.FONT_TITLE);
        heading.setForeground(Theme.TEXT);

        JLabel counter = new JLabel(
                unlocked + " / "
                + Achievement.values().length
                + " UNLOCKED"
        );

        counter.setFont(Theme.FONT_BODY);
        counter.setForeground(Theme.ACCENT);

        header.add(heading, BorderLayout.WEST);
        header.add(counter, BorderLayout.EAST);


        // =====================================
        // OVERALL ACHIEVEMENT PROGRESS
        // =====================================

        int totalAchievements = Achievement.values().length;
        int remaining = totalAchievements - unlocked;

        JPanel overview = new JPanel(
                new BorderLayout(0, Theme.GAP_S)
        );

        overview.setOpaque(false);

        JLabel collectionStatus = new JLabel(
                remaining == 0
                        ? "Collection complete! All achievements earned."
                        : remaining + " more achievements to collect"
        );

        collectionStatus.setFont(
                Theme.FONT_BODY.deriveFont(13f)
        );

        collectionStatus.setForeground(Theme.TEXT_MUTED);

        JProgressBar collectionBar = new JProgressBar(
                0, totalAchievements
        );

        collectionBar.setValue(unlocked);
        collectionBar.setForeground(Theme.ACCENT);
        collectionBar.setBackground(Theme.INSET_BORDER);

        collectionBar.setBorderPainted(false);
        collectionBar.setStringPainted(false);

        collectionBar.setPreferredSize(
                new Dimension(1, 10)
        );

        overview.add(
                collectionStatus,
                BorderLayout.NORTH
        );

        overview.add(
                collectionBar,
                BorderLayout.SOUTH
        );

        header.add(
                overview,
                BorderLayout.SOUTH
        );


        add(header, BorderLayout.NORTH);

        // =====================================
        // ACHIEVEMENT GRID
        // =====================================

        JPanel grid = new JPanel(
                new GridLayout(
                        0, 2,
                        Theme.GAP_M,
                        Theme.GAP_M
                )
        );

        grid.setOpaque(false);

        grid.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_S,
                        Theme.GAP_S,
                        Theme.GAP_S,
                        Theme.GAP_S
                )
        );


// Sort achievements: unlocked first, locked afterward
        populateAchievementGrid(grid, player, "All");


                // =====================================
                // SCROLLABLE CONTENT
                // =====================================

        JPanel gridHost = new JPanel(
                new BorderLayout()
        );

        gridHost.setOpaque(false);
        gridHost.add(grid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(
                gridHost,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

                        
        // =====================================
        // ACHIEVEMENT FILTERS
        // =====================================

        JPanel filterRow = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        Theme.GAP_M,
                        0
                )
        );

        filterRow.setOpaque(false);

        JLabel filterLabel = new JLabel("SHOW");

        filterLabel.setFont(
                Theme.FONT_BODY.deriveFont(
                        Font.BOLD, 12f
                )
        );

        filterLabel.setForeground(Theme.TEXT_MUTED);

        JComboBox<String> filterBox = new JComboBox<>(
                new String[]{
                        "All",
                        "Unlocked",
                        "Locked"
                }
        );

        filterBox.setFont(
                Theme.FONT_BODY.deriveFont(14f)
        );

        filterBox.setBackground(Theme.INSET);
        filterBox.setForeground(Theme.TEXT);

        // Fix the invisible arrow on the dark background
        filterBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected javax.swing.JButton createArrowButton() {
                javax.swing.JButton b = new javax.swing.JButton("\u25BC"); // Downward triangle
                b.setForeground(Theme.TEXT);     // Makes the arrow visible
                b.setBackground(Theme.INSET);    // Matches dropdown background
                b.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Removes ugly box
                return b;
            }
        });

        // Update cards whenever the filter changes
        filterBox.addActionListener(e -> {

        String selected =
                (String) filterBox.getSelectedItem();

        populateAchievementGrid(
                grid,
                player,
                selected
        );

        scroll.getVerticalScrollBar().setValue(0);
        });

        filterRow.add(filterLabel);
        filterRow.add(filterBox);

        // Place filters above the scrollable gallery
        JPanel center = new JPanel(
                new BorderLayout(0, Theme.GAP_M)
        );

        center.setOpaque(false);

        center.add(filterRow, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);


        // =====================================
        // BACK BUTTON
        // =====================================

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );

        actions.setOpaque(false);

        RoundedButton backButton = new RoundedButton(
                "Back to Main Menu",
                ButtonStyle.SECONDARY
        );

        backButton.addActionListener(
                e -> onBack.run()
        );

        actions.add(backButton);

        add(actions, BorderLayout.SOUTH);
    }

    // =====================================
    // REUSABLE ACHIEVEMENT CARD
    // =====================================


        private void populateAchievementGrid(
                JPanel grid,
                Player player,
                String filter) {

        // Clear the previously displayed cards
        grid.removeAll();

        Achievement[] ordered = Achievement.values();

        // Unlocked achievements first
        Arrays.sort(
                ordered,
                (first, second) -> Boolean.compare(
                        second.isUnlocked(player),
                        first.isUnlocked(player)
                )
        );

        int displayed = 0;

        for (Achievement achievement : ordered) {

                boolean unlocked =
                        achievement.isUnlocked(player);

                // Apply the selected filter
                if ("Unlocked".equals(filter) && !unlocked) {
                continue;
                }

                if ("Locked".equals(filter) && unlocked) {
                continue;
                }

                grid.add(
                        createAchievementCard(
                                achievement,
                                unlocked,
                                achievement.getProgress(player),
                                achievement.getTarget()
                        )
                );

                displayed++;
        }

        // Display a message for empty results
        if (displayed == 0) {

                JLabel empty = new JLabel(
                        "No " + filter.toLowerCase()
                                + " achievements to display."
                );

                empty.setFont(Theme.FONT_BODY);
                empty.setForeground(Theme.TEXT_MUTED);

                empty.setBorder(
                        BorderFactory.createEmptyBorder(
                                Theme.GAP_L,
                                Theme.GAP_M,
                                Theme.GAP_L,
                                Theme.GAP_M
                        )
                );

                grid.add(empty);
        }

        grid.revalidate();
        grid.repaint();
        }


    private RoundedPanel createAchievementCard(
            Achievement achievement,
            boolean unlocked,
            int progress,
            int target) {

        RoundedPanel card = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_S)
        );

        card.setPreferredSize(
                new Dimension(320, 176)
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                unlocked
                                        ? UNLOCKED_COLOR
                                        : Theme.PANEL_BORDER,
                                1,
                                true
                        ),
                        BorderFactory.createEmptyBorder(
                                Theme.GAP_M,
                                Theme.GAP_M,
                                Theme.GAP_M,
                                Theme.GAP_M
                        )
                )
        );

        // Achievement title and status
            JPanel titleRow = new JPanel(
                    new BorderLayout(Theme.GAP_S, 0)
            );

            titleRow.setOpaque(false);

            JLabel title = new JLabel(
                    achievement.getTitle()
            );

            title.setFont(
                    Theme.FONT_BODY.deriveFont(Font.BOLD)
            );

            title.setForeground(
                    unlocked
                            ? Theme.TEXT
                            : Theme.TEXT_MUTED
            );

            JLabel status = new JLabel(
                    unlocked ? "UNLOCKED" : "LOCKED"
            );

            status.setFont(
                    Theme.FONT_BODY.deriveFont(
                            Font.BOLD, 11f
                    )
            );

            status.setForeground(
                    unlocked
                            ? UNLOCKED_COLOR
                            : Theme.TEXT_MUTED
            );

            titleRow.add(title, BorderLayout.CENTER);
            titleRow.add(status, BorderLayout.EAST);

            card.add(titleRow, BorderLayout.NORTH);

            // Achievement description
            JTextArea description = new JTextArea(
                    achievement.getDescription()
            );

            description.setFont(
                    Theme.FONT_BODY.deriveFont(14f)
            );

            description.setForeground(Theme.TEXT_MUTED);

            description.setOpaque(false);
            description.setEditable(false);
            description.setFocusable(false);

            description.setLineWrap(true);
            description.setWrapStyleWord(true);

            card.add(description, BorderLayout.CENTER);


            // =====================================
            // ACHIEVEMENT PROGRESS
            // =====================================

            JPanel progressPanel = new JPanel(
                    new BorderLayout(0, 6)
            );

            progressPanel.setOpaque(false);

            JPanel progressText = new JPanel(
                    new BorderLayout()
            );

            progressText.setOpaque(false);

            // XP achievements display XP units
            String unit = achievement.name().startsWith("XP_")
                    ? " XP"
                    : "";

            JLabel amount = new JLabel(
                    progress + " / " + target + unit
            );

            amount.setFont(
                    Theme.FONT_BODY.deriveFont(12f)
            );

            amount.setForeground(
                    unlocked
                            ? UNLOCKED_COLOR
                            : Theme.ACCENT
            );

            // Calculate completion percentage
            int percentage = target == 0
                    ? 0
                    : (int) Math.round(
                            100.0 * progress / target
                    );

            JLabel percentageLabel = new JLabel(
                    percentage + "%"
            );

            percentageLabel.setFont(
                    Theme.FONT_BODY.deriveFont(12f)
            );

            percentageLabel.setForeground(
                    Theme.TEXT_MUTED
            );

            progressText.add(
                    amount,
                    BorderLayout.WEST
            );

            progressText.add(
                    percentageLabel,
                    BorderLayout.EAST
            );

            // Progress bar
            JProgressBar bar = new JProgressBar(
                    0, target
            );

            bar.setValue(progress);

            bar.setForeground(
                    unlocked
                            ? UNLOCKED_COLOR
                            : Theme.ACCENT
            );

            bar.setBackground(Theme.INSET_BORDER);
            bar.setBorderPainted(false);
            bar.setStringPainted(false);

            bar.setPreferredSize(
                    new Dimension(1, 8)
            );

            progressPanel.add(
                    progressText,
                    BorderLayout.NORTH
            );

            progressPanel.add(
                    bar,
                    BorderLayout.SOUTH
            );

            // Attach progress section to card
            card.add(
                    progressPanel,
                    BorderLayout.SOUTH
            );


        return card;
    }
}
