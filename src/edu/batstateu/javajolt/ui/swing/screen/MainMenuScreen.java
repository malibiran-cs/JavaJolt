package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.service.LevelService;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.BadgeSlot;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.CharacterPlaceholder;
import edu.batstateu.javajolt.ui.swing.component.Divider;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuScreen extends Screen {
    private final JLabel expLabel = createLabel("EXP: --");
    private final JLabel levelLabel = createLabel("LEVEL: --");

    public MainMenuScreen(
            Player player,
            Runnable onLessonsClicked,
            Runnable onSave,
            Runnable onProgress,
            Runnable onExit) {

        refresh(player);

        add(createTop(), BorderLayout.NORTH);
        add(createBody(
                onLessonsClicked,
                onSave,
                onProgress,
                onExit
        ), BorderLayout.CENTER);
    }

    public void refresh(Player player) {
        expLabel.setText("EXP: " + player.getXp());
        levelLabel.setText(
                "LEVEL: " + LevelService.calculateLevel(player.getXp())
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
        CharacterPlaceholder character = new CharacterPlaceholder();
        character.setPreferredSize(new Dimension(110, 110));

        JPanel stats = new JPanel(new GridLayout(2, 1));
        stats.setOpaque(false);
        stats.add(expLabel);
        stats.add(levelLabel);

        JPanel header = new JPanel(new BorderLayout(Theme.GAP_L, 0));
        header.setOpaque(false);
        header.add(character, BorderLayout.WEST);
        header.add(stats, BorderLayout.CENTER);
        return header;
    }

    private JPanel createBody(
            Runnable onLessonsClicked,
            Runnable onSave,
            Runnable onProgress,
            Runnable onExit) {
        JPanel body = new JPanel(new GridLayout(1, 2, Theme.GAP_L, 0));
        body.setOpaque(false);
        body.add(createBadgesPanel());
        body.add(createMenuButtons(
                onLessonsClicked,
                onSave,
                onProgress,
                onExit
        ));
        return body;
    }

    private RoundedPanel createBadgesPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_L, Theme.GAP_L, Theme.GAP_L, Theme.GAP_L));
        panel.add(createLabel("BADGES"), BorderLayout.NORTH);
        panel.add(createBadgeRow(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBadgeRow() {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints slot = new GridBagConstraints();
        slot.insets = new Insets(0, Theme.GAP_S, 0, Theme.GAP_S);
        for (int i = 0; i < 3; i++) {
            row.add(new BadgeSlot(), slot);
        }
        return row;
    }

private JPanel createMenuButtons(
        Runnable onLessonsClicked,
        Runnable onSave,
        Runnable onProgress,
        Runnable onExit) {
    JPanel grid = new JPanel(new GridLayout(2, 2, Theme.GAP_M, Theme.GAP_M));
    grid.setOpaque(false);

    RoundedButton lessonsButton =
            new RoundedButton("Lessons", ButtonStyle.PRIMARY);

    RoundedButton progressButton =
            new RoundedButton("Progress", ButtonStyle.SECONDARY);

    RoundedButton saveButton =
            new RoundedButton("Save", ButtonStyle.SECONDARY);

    RoundedButton exitButton =
            new RoundedButton("Exit", ButtonStyle.DANGER);

    // Lessons
    lessonsButton.addActionListener(e -> onLessonsClicked.run());
    progressButton.addActionListener(e -> onProgress.run());
    saveButton.addActionListener(e -> onSave.run());
    exitButton.addActionListener(e -> onExit.run());

    grid.add(lessonsButton);
    grid.add(progressButton);
    grid.add(saveButton);
    grid.add(exitButton);

    GridBagConstraints stretch = new GridBagConstraints();
    stretch.weightx = 1;
    stretch.fill = GridBagConstraints.HORIZONTAL;

    JPanel holder = new JPanel(new GridBagLayout());
    holder.setOpaque(false);
    holder.add(grid, stretch);

    return holder;
}

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_HEADING);
        label.setForeground(Theme.TEXT);
        return label;
    }
}
