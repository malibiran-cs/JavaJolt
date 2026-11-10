package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.MascotView;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class TitleScreen extends Screen {

    public TitleScreen(Runnable onStart, Runnable onExit) {
        JPanel hero = new RoundedPanel(new GridLayout(1, 2, Theme.GAP_XL, 0));
        hero.setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP_XL, Theme.GAP_XL, Theme.GAP_XL, Theme.GAP_XL));
        hero.setPreferredSize(new Dimension(980, 550));

        // LEFT: the existing pixel mascot artwork.
        JPanel artSide = new JPanel(new BorderLayout(0, Theme.GAP_M));
        artSide.setOpaque(false);

        MascotView mascot = new MascotView();
        mascot.setMood(MascotView.Mood.ONLINE);
        artSide.add(mascot, BorderLayout.CENTER);

        JLabel artCaption = new JLabel("YOUR CODING COMPANION", JLabel.CENTER);
        artCaption.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 12f));
        artCaption.setForeground(Theme.TEXT_MUTED);
        artSide.add(artCaption, BorderLayout.SOUTH);
        hero.add(artSide);

        // RIGHT: branding, a brief pitch, and the main actions.
        JPanel infoSide = new JPanel();
        infoSide.setOpaque(false);
        infoSide.setLayout(new BoxLayout(infoSide, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("WELCOME TO YOUR JAVA ADVENTURE");
        eyebrow.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 12f));
        eyebrow.setForeground(Theme.ACCENT);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logo.setOpaque(false);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setMaximumSize(new Dimension(440, 80));
        JLabel javaText = new JLabel("JAVA");
        javaText.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 53f));
        javaText.setForeground(Theme.TEXT);
        JLabel joltText = new JLabel("JOLT");
        joltText.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 53f));
        joltText.setForeground(Theme.ACCENT);
        logo.add(javaText);
        logo.add(joltText);

        JLabel tagline = new JLabel("LEARN JAVA  •  EARN XP  •  LEVEL UP");
        tagline.setFont(Theme.FONT_BODY.deriveFont(14f));
        tagline.setForeground(Theme.TEXT_MUTED);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel line1 = new JLabel("Learn the language. Face the bugs.");
        JLabel line2 = new JLabel("Build your skills, one battle at a time.");
        for (JLabel line : new JLabel[]{line1, line2}) {
            line.setFont(Theme.FONT_BODY.deriveFont(16f));
            line.setForeground(Theme.TEXT);
            line.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        JLabel features = new JLabel("7 TOPICS  •  3 DIFFICULTIES  •  504 QUESTIONS");
        features.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
        features.setForeground(Theme.ACCENT);
        features.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton start = new RoundedButton("START GAME", ButtonStyle.PRIMARY);
        start.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.setMaximumSize(new Dimension(390, 62));
        start.addActionListener(e -> onStart.run());

        RoundedButton exit = new RoundedButton("EXIT", ButtonStyle.SECONDARY);
        exit.setAlignmentX(Component.LEFT_ALIGNMENT);
        exit.setMaximumSize(new Dimension(390, 62));
        exit.addActionListener(e -> onExit.run());

        infoSide.add(Box.createVerticalStrut(Theme.GAP_L));
        infoSide.add(eyebrow);
        infoSide.add(Box.createVerticalStrut(Theme.GAP_S));
        infoSide.add(logo);
        infoSide.add(tagline);
        infoSide.add(Box.createVerticalStrut(Theme.GAP_L));
        infoSide.add(line1);
        infoSide.add(Box.createVerticalStrut(6));
        infoSide.add(line2);
        infoSide.add(Box.createVerticalStrut(Theme.GAP_M));
        infoSide.add(features);
        infoSide.add(Box.createVerticalGlue());
        infoSide.add(start);
        infoSide.add(Box.createVerticalStrut(Theme.GAP_S));
        infoSide.add(exit);
        infoSide.add(Box.createVerticalStrut(Theme.GAP_L));

        hero.add(infoSide);

        JPanel centered = new JPanel(new GridBagLayout());
        centered.setOpaque(false);
        centered.add(hero);
        add(centered, BorderLayout.CENTER);
    }
}
