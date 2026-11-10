package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.entity.TutorialStep;
import edu.batstateu.javajolt.ui.swing.Theme;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// One modal teaching step. Window close asks the application's existing exit confirmation.
public class TutorialPopup extends JDialog {
    private boolean confirmed;

    public TutorialPopup(Window owner, TutorialStep step, int number, int total,
            boolean replay, Runnable onWindowClose) {
        super(owner, "Tutorial", Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                onWindowClose.run();
            }
        });
        RoundedPanel content = new RoundedPanel(new BorderLayout(Theme.GAP_M, Theme.GAP_M));
        content.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_L, Theme.GAP_L, Theme.GAP_L, Theme.GAP_L));
        content.add(createHeading(step.title(), number, total), BorderLayout.NORTH);
        content.add(createBody(step.body()), BorderLayout.CENTER);
        content.add(createButtons(replay), BorderLayout.SOUTH);
        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel createHeading(String title, int number, int total) {
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, Theme.GAP_S));
        heading.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_HEADING);
        titleLabel.setForeground(Theme.TEXT);
        JLabel progress = new JLabel("Step " + number + " of " + total);
        progress.setFont(Theme.FONT_BODY);
        progress.setForeground(Theme.TEXT_MUTED);
        heading.add(titleLabel);
        heading.add(progress);
        return heading;
    }

    private JScrollPane createBody(String text) {
        JTextArea body = new JTextArea(text, Theme.TUTORIAL_BODY_ROWS, Theme.TUTORIAL_BODY_COLUMNS);
        body.setFont(Theme.FONT_BODY);
        body.setForeground(Theme.TEXT);
        body.setEditable(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setOpaque(false);
        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    private JPanel createButtons(boolean replay) {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP_S, 0));
        buttons.setOpaque(false);
        if (replay) {
            RoundedButton exit = new RoundedButton("Exit tutorial", ButtonStyle.SECONDARY);
            exit.addActionListener(event -> dispose());
            buttons.add(exit);
        }
        RoundedButton confirm = new RoundedButton("Got it", ButtonStyle.PRIMARY);
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
