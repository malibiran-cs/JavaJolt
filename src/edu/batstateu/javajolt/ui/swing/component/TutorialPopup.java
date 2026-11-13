
package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.entity.TutorialStep;
import edu.batstateu.javajolt.ui.swing.Theme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public final class TutorialPopup extends JDialog {

    private boolean confirmed;

    public TutorialPopup(
            Window owner,
            TutorialStep step,
            int number,
            int total,
            boolean replay,
            Runnable onWindowClose) {

        super(owner, "JavaJolt - Guided Tutorial",
                Dialog.ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                onWindowClose.run();
            }
        });

        RoundedPanel content = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_L)
        );

        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP_L, Theme.GAP_L,
                Theme.GAP_L, Theme.GAP_L
        ));

        content.add(
                createHeader(step.title(), number, total),
                BorderLayout.NORTH
        );

        content.add(
                createLessonArea(step.body()),
                BorderLayout.CENTER
        );

        content.add(
                createButtons(replay, number == total),
                BorderLayout.SOUTH
        );

        setContentPane(content);
        setPreferredSize(new Dimension(930, 550));

        pack();

        setMinimumSize(new Dimension(780, 470));
        setLocationRelativeTo(owner);
    }

    private JPanel createHeader(
            String title, int number, int total) {

        JPanel header = new JPanel(
                new BorderLayout(0, Theme.GAP_S)
        );
        header.setOpaque(false);

        JPanel titleRow = new JPanel(
                new BorderLayout(Theme.GAP_S, 0)
        );
        titleRow.setOpaque(false);

        JPanel headingText = new JPanel();
        headingText.setOpaque(false);
        headingText.setLayout(
                new BoxLayout(headingText, BoxLayout.Y_AXIS)
        );

        JLabel eyebrow = new JLabel(
                "JAVAJOLT / GUIDED TRAINING"
        );

        eyebrow.setFont(
                Theme.FONT_BODY.deriveFont(Font.BOLD, 12f)
        );
        eyebrow.setForeground(Theme.ACCENT);

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                Theme.FONT_TITLE.deriveFont(Font.BOLD, 26f)
        );
        titleLabel.setForeground(Theme.TEXT);

        headingText.add(eyebrow);
        headingText.add(Box.createVerticalStrut(5));
        headingText.add(titleLabel);

        JLabel progressLabel = new JLabel(
                "Step " + number + " of " + total
        );
        progressLabel.setFont(
                Theme.FONT_BODY.deriveFont(Font.BOLD, 14f)
        );
        progressLabel.setForeground(Theme.ACCENT);

        titleRow.add(headingText, BorderLayout.CENTER);
        titleRow.add(progressLabel, BorderLayout.EAST);

        JProgressBar progress = new JProgressBar(
                0, Math.max(1, total)
        );
        progress.setValue(
                Math.min(number, Math.max(1, total))
        );
        progress.setForeground(Theme.ACCENT);
        progress.setBackground(Theme.INSET_BORDER);
        progress.setBorderPainted(false);
        progress.setStringPainted(false);
        progress.setPreferredSize(new Dimension(1, 8));

        header.add(titleRow, BorderLayout.CENTER);
        header.add(progress, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createLessonArea(String text) {

        JPanel lesson = new JPanel(
                new BorderLayout(Theme.GAP_M, 0)
        );
        lesson.setOpaque(false);

        // LEFT: TEAM MASCOT

        RoundedPanel mascotPanel = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_S),
                "panels/panel_inset",
                13
        );

        mascotPanel.setPreferredSize(
                new Dimension(230, 290)
        );

        mascotPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_M, Theme.GAP_M,
                        Theme.GAP_M, Theme.GAP_M
                )
        );

        JLabel mascotTitle = new JLabel(
                "YOUR JAVA COMPANION",
                JLabel.CENTER
        );

        mascotTitle.setFont(
                Theme.FONT_BODY.deriveFont(Font.BOLD, 12f)
        );
        mascotTitle.setForeground(Theme.ACCENT);

        MascotView mascot = new MascotView();
        mascot.showIdle();

        mascotPanel.add(
                mascotTitle, BorderLayout.NORTH
        );
        mascotPanel.add(
                mascot, BorderLayout.CENTER
        );

        JLabel caption = new JLabel(
                "LEARN ONE STEP AT A TIME",
                JLabel.CENTER
        );
        caption.setFont(
                Theme.FONT_BODY.deriveFont(11f)
        );
        caption.setForeground(Theme.TEXT_MUTED);

        mascotPanel.add(caption, BorderLayout.SOUTH);

        // RIGHT: MISSION BRIEFING

        RoundedPanel guidePanel = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_M),
                "panels/panel_inset",
                13
        );

        guidePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_L, Theme.GAP_L,
                        Theme.GAP_L, Theme.GAP_L
                )
        );

        JLabel guideHeading = new JLabel(
                "MISSION BRIEFING"
        );
        guideHeading.setFont(
                Theme.FONT_BODY.deriveFont(Font.BOLD, 13f)
        );
        guideHeading.setForeground(Theme.ACCENT);

        guidePanel.add(
                guideHeading, BorderLayout.NORTH
        );

        JTextArea body = new JTextArea(text);
        body.setEditable(false);
        body.setFocusable(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setOpaque(false);
        body.setForeground(Theme.TEXT);
        body.setFont(
                Theme.FONT_BODY.deriveFont(16f)
        );
        body.setBorder(
                BorderFactory.createEmptyBorder()
        );
        body.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.getVerticalScrollBar()
                .setUnitIncrement(14);

        guidePanel.add(scroll, BorderLayout.CENTER);

        lesson.add(mascotPanel, BorderLayout.WEST);
        lesson.add(guidePanel, BorderLayout.CENTER);

        return lesson;
    }

    private JPanel createButtons(
            boolean replay, boolean lastStep) {

        JPanel buttons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT, Theme.GAP_S, 0
                )
        );

        buttons.setOpaque(false);

        if (replay) {
            RoundedButton exit = new RoundedButton(
                    "Exit tutorial",
                    ButtonStyle.SECONDARY
            );

            exit.addActionListener(
                    event -> dispose()
            );

            buttons.add(exit);
        }

        RoundedButton confirm = new RoundedButton(
                "Got it",
                ButtonStyle.PRIMARY
        );

        confirm.addActionListener(event -> {
            confirmed = true;
            dispose();
        });

        buttons.add(confirm);

        getRootPane().setDefaultButton(confirm);

        return buttons;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
