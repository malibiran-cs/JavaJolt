
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JavaJolt-themed exit confirmation.
 *
 * This class displays the dialog and returns the
 * selected option. MainWindow handles saving.
 */
public final class ExitDialog extends JDialog {

    public enum Choice {
        SAVE_AND_EXIT,
        EXIT_WITHOUT_SAVING,
        CANCEL
    }

    private Choice choice = Choice.CANCEL;

    private ExitDialog(Window owner, boolean canSave) {

        super(
                owner,
                "Exit JavaJolt",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel backdrop = new JPanel(new BorderLayout());
        backdrop.setBackground(Theme.BACKGROUND);

        backdrop.setBorder(
                BorderFactory.createEmptyBorder(
                        14, 14, 14, 14
                )
        );

        RoundedPanel card = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_L)
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        32, 36, 32, 36
                )
        );

        // HEADING AND DESCRIPTION

        JPanel message = new JPanel();
        message.setOpaque(false);

        message.setLayout(
                new BoxLayout(message, BoxLayout.Y_AXIS)
        );

        JLabel heading = label(
                "EXIT JAVAJOLT?",
                Theme.FONT_HEADING,
                Theme.ACCENT
        );

        JLabel description = label(
                canSave
                        ? "Save your progress before leaving?"
                        : "Leave JavaJolt?",
                Theme.FONT_BODY,
                Theme.TEXT
        );

        JLabel note = label(
                canSave
                        ? "Unfinished quiz attempts are not saved."
                        : "No player profile is ready to save yet.",
                Theme.FONT_BODY.deriveFont(13f),
                Theme.TEXT_MUTED
        );

        message.add(heading);
        message.add(Box.createVerticalStrut(18));
        message.add(description);
        message.add(Box.createVerticalStrut(12));
        message.add(note);

        // EXIT BUTTONS

        JPanel buttons = new JPanel(
                new GridLayout(0, 1, 0, Theme.GAP_S)
        );

        buttons.setOpaque(false);

        if (canSave) {

            RoundedButton save = button(
                    "Yes",
                    ButtonStyle.PRIMARY,
                    Choice.SAVE_AND_EXIT
            );

            buttons.add(save);
            getRootPane().setDefaultButton(save);
        }

        buttons.add(button(
                "No",
                ButtonStyle.DANGER,
                Choice.EXIT_WITHOUT_SAVING
        ));

        buttons.add(button(
                "Cancel",
                ButtonStyle.SECONDARY,
                Choice.CANCEL
        ));

        card.add(message, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        backdrop.add(card, BorderLayout.CENTER);

        setContentPane(backdrop);

        setPreferredSize(
                new Dimension(
                        560,
                        canSave ? 400 : 340
                )
        );

        pack();
        setLocationRelativeTo(owner);
    }

    private static JLabel label(
            String text,
            Font font,
            java.awt.Color color) {

        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);

        return label;
    }

    private RoundedButton button(
            String text,
            ButtonStyle style,
            Choice value) {

        RoundedButton button = new RoundedButton(
                text, style
        );

        button.addActionListener(event -> {
            choice = value;
            dispose();
        });

        return button;
    }

    /**
     * Show the dialog and return the player's choice.
     * Closing the dialog counts as CANCEL.
     */
    public static Choice showChoice(
            Window owner,
            boolean canSave) {

        ExitDialog dialog = new ExitDialog(
                owner, canSave
        );

        dialog.setVisible(true);

        return dialog.choice;
    }
}
