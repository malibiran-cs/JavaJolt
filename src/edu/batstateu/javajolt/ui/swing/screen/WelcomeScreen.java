package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.CharacterPlaceholder;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;
import edu.batstateu.javajolt.ui.swing.component.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class WelcomeScreen extends Screen {
    private final RoundedTextField nameField = new RoundedTextField();

    public WelcomeScreen() {
        add(createGreeting(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    private JPanel createGreeting() {
        JLabel greeting = new JLabel("GREETINGS", SwingConstants.CENTER);
        greeting.setFont(Theme.FONT_TITLE);
        greeting.setForeground(Theme.TEXT_ON_ACCENT);

        RoundedPanel plate = new RoundedPanel(new BorderLayout(), "panels/panel", 14);
        plate.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_XL, Theme.GAP_M, Theme.GAP_XL));
        plate.add(greeting);

        // FlowLayout keeps the plate at its natural size instead of stretching it.
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        holder.setOpaque(false);
        holder.add(plate);
        return holder;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, Theme.GAP_L, 0));
        body.setOpaque(false);
        body.add(new CharacterPlaceholder());
        body.add(createInfoPanel());
        return body;
    }

    private RoundedPanel createInfoPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(0, Theme.GAP_L));
        panel.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_L, Theme.GAP_L, Theme.GAP_L, Theme.GAP_L));
        panel.add(createDescription(), BorderLayout.NORTH);
        panel.add(createNameInput(), BorderLayout.SOUTH);
        return panel;
    }

    private JTextArea createDescription() {
        JTextArea description = new JTextArea("A gamified way to learn Java, from the basics all the way to object-oriented programming.");
        description.setFont(Theme.FONT_BODY);
        description.setForeground(Theme.TEXT_MUTED);
        description.setOpaque(false);
        description.setEditable(false);
        description.setFocusable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        return description;
    }

    private JPanel createNameInput() {
        JLabel label = new JLabel("Name:");
        label.setFont(Theme.FONT_HEADING);
        label.setForeground(Theme.TEXT);

        JPanel input = new JPanel(new BorderLayout(0, Theme.GAP_S));
        input.setOpaque(false);
        input.add(label, BorderLayout.NORTH);
        input.add(nameField, BorderLayout.CENTER);
        return input;
    }
}
