
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.MascotView;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Allows players to choose whether to take
 * the optional JavaJolt tutorial.
 */
public final class TutorialChoiceScreen extends Screen {

    public TutorialChoiceScreen(
            Runnable onTutorial,
            Runnable onSkip) {

        JPanel card = new RoundedPanel(
                new GridLayout(1, 2, Theme.GAP_XL, 0)
        );

        card.setPreferredSize(new Dimension(940, 510));

        card.setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP_XL,
                Theme.GAP_XL,
                Theme.GAP_XL,
                Theme.GAP_XL
        ));

        // LEFT SIDE - MASCOT

        JPanel mascotSide = new JPanel(
                new BorderLayout(0, Theme.GAP_M)
        );

        mascotSide.setOpaque(false);

        JLabel mascotHeading = createLabel(
                "YOUR JAVA COMPANION",
                13, true, Theme.ACCENT
        );

        mascotSide.add(
                mascotHeading,
                BorderLayout.NORTH
        );

        MascotView mascot = new MascotView();
        mascot.showIdle();

        mascotSide.add(
                mascot,
                BorderLayout.CENTER
        );

        mascotSide.add(
                createLabel(
                        "Learn at your own pace.",
                        14, false, Theme.TEXT_MUTED
                ),
                BorderLayout.SOUTH
        );

        card.add(mascotSide);

        // RIGHT SIDE - TUTORIAL CHOICE

        JPanel choices = new JPanel();

        choices.setOpaque(false);
        choices.setLayout(
                new BoxLayout(choices, BoxLayout.Y_AXIS)
        );

        JLabel eyebrow = createLabel(
                "BEFORE YOUR FIRST BATTLE",
                12, true, Theme.ACCENT
        );

        JLabel heading = createLabel(
                "YOUR ADVENTURE,",
                29, true, Theme.TEXT
        );

        JLabel heading2 = createLabel(
                "YOUR PACE.",
                29, true, Theme.ACCENT
        );

        JLabel description = createLabel(
                "New to JavaJolt? Try our guided practice.",
                15, false, Theme.TEXT
        );

        JLabel description2 = createLabel(
                "Already know the ropes? Jump straight in.",
                15, false, Theme.TEXT_MUTED
        );

        JLabel safety = createLabel(
                "PRACTICE MODE - SAVED PROGRESS STAYS SAFE",
                12, true, Theme.ACCENT
        );

        JLabel replay = createLabel(
                "Replay the tutorial anytime from your dashboard.",
                12, false, Theme.TEXT_MUTED
        );

        for (JLabel label : new JLabel[]{
                eyebrow, heading, heading2,
                description, description2,
                safety, replay
        }) {
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        // START TUTORIAL BUTTON

        RoundedButton tutorial = new RoundedButton(
                "START TUTORIAL",
                ButtonStyle.PRIMARY
        );

        tutorial.addActionListener(
                e -> onTutorial.run()
        );

        tutorial.setAlignmentX(Component.LEFT_ALIGNMENT);
        tutorial.setMaximumSize(
                new Dimension(380, 56)
        );

        // SKIP TUTORIAL BUTTON

        RoundedButton skip = new RoundedButton(
                "SKIP TO DASHBOARD",
                ButtonStyle.SECONDARY
        );

        skip.addActionListener(
                e -> onSkip.run()
        );

        skip.setAlignmentX(Component.LEFT_ALIGNMENT);
        skip.setMaximumSize(
                new Dimension(380, 56)
        );

        // ASSEMBLE RIGHT SIDE

        choices.add(Box.createVerticalGlue());
        choices.add(eyebrow);
        choices.add(Box.createVerticalStrut(12));
        choices.add(heading);
        choices.add(heading2);

        choices.add(Box.createVerticalStrut(24));
        choices.add(description);
        choices.add(Box.createVerticalStrut(6));
        choices.add(description2);

        choices.add(Box.createVerticalStrut(22));
        choices.add(safety);

        choices.add(Box.createVerticalStrut(26));
        choices.add(tutorial);
        choices.add(Box.createVerticalStrut(10));
        choices.add(skip);

        choices.add(Box.createVerticalStrut(14));
        choices.add(replay);
        choices.add(Box.createVerticalGlue());

        card.add(choices);

        // CENTER THE COMPLETE SCREEN

        JPanel centered = new JPanel(
                new GridBagLayout()
        );

        centered.setOpaque(false);
        centered.add(card);

        add(centered, BorderLayout.CENTER);
    }

    private static JLabel createLabel(
            String text,
            int size,
            boolean bold,
            java.awt.Color color) {

        JLabel label = new JLabel(text);

        label.setForeground(color);

        label.setFont(
                Theme.FONT_BODY.deriveFont(
                        bold ? Font.BOLD : Font.PLAIN,
                        (float) size
                )
        );

        return label;
    }
}
