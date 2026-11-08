package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.MascotView;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class TitleScreen extends Screen {

    public TitleScreen(Runnable onStart, Runnable onExit) {
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("JAVAJOLT");
        title.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 48f));
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("LEARN JAVA  •  EARN XP  •  LEVEL UP");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.ACCENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        MascotView mascot = new MascotView();
        mascot.setMood(MascotView.Mood.ONLINE);
        mascot.setPreferredSize(new Dimension(300, 300));
        mascot.setMaximumSize(new Dimension(300, 300));
        mascot.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton startButton = new RoundedButton("START GAME", ButtonStyle.PRIMARY);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(280, 62));
        startButton.addActionListener(e -> onStart.run());

        RoundedButton exitButton = new RoundedButton("EXIT", ButtonStyle.SECONDARY);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setMaximumSize(new Dimension(280, 62));
        exitButton.addActionListener(e -> onExit.run());

        stack.add(title);
        stack.add(Box.createVerticalStrut(Theme.GAP_S));
        stack.add(subtitle);
        stack.add(Box.createVerticalStrut(Theme.GAP_L));
        stack.add(mascot);
        stack.add(Box.createVerticalStrut(Theme.GAP_L));
        stack.add(startButton);
        stack.add(Box.createVerticalStrut(Theme.GAP_S));
        stack.add(exitButton);

        JPanel centered = new JPanel(new GridBagLayout());
        centered.setOpaque(false);
        centered.add(stack);
        add(centered, BorderLayout.CENTER);
    }
}
