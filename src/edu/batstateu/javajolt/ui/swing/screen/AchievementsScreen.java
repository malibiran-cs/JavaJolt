
package edu.batstateu.javajolt.ui.swing.screen;

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
                new BorderLayout(Theme.GAP_M, 0)
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

        for (Achievement achievement : Achievement.values()) {

            boolean isUnlocked =
                    achievement.isUnlocked(player);

            grid.add(
                    createAchievementCard(
                            achievement,
                            isUnlocked
                    )
            );
        }

        // =====================================
        // SCROLLABLE CONTENT
        // =====================================

        JScrollPane scroll = new JScrollPane(
                grid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

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

    private RoundedPanel createAchievementCard(
            Achievement achievement,
            boolean unlocked) {

        RoundedPanel card = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_S)
        );

        card.setPreferredSize(
                new Dimension(320, 132)
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

        return card;
    }
}
