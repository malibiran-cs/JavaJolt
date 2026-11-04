
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.CharacterPlaceholder;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;
import edu.batstateu.javajolt.ui.swing.component.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class WelcomeScreen extends Screen {

    private final RoundedTextField nameField = new RoundedTextField();

    {
        nameField.setFont(Theme.FONT_HEADING);
    }

    private final Consumer<String> onContinue;

    public WelcomeScreen(Consumer<String> onContinue) {
        this.onContinue = onContinue;

        add(createGreeting(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    private JPanel createGreeting() {
        JLabel greeting = new JLabel("GREETINGS", SwingConstants.CENTER);
        greeting.setFont(Theme.FONT_TITLE);
        greeting.setForeground(Theme.TEXT);

        // Creates a rounded panel to hold the greeting label.
        // BorderLayout allows components to be arranged inside the panel.
        RoundedPanel plate = new RoundedPanel(
                new BorderLayout(), "panels/panel", 14);
        
        // Adds padding (empty space) between the panel's edges and its content.
        // GAP_M controls the top and bottom spacing.
        // GAP_XL controls the left and right spacing.
        plate.setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP_M, Theme.GAP_XL,
                Theme.GAP_M, Theme.GAP_XL));

        plate.add(greeting);

        JPanel holder = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 0));

        holder.setOpaque(false);
        holder.add(plate);

        return holder;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(
                new GridLayout(1, 2, Theme.GAP_L, 0));

        body.setOpaque(false);

        body.add(new CharacterPlaceholder());
        body.add(createInfoPanel());

        return body;
    }

    private RoundedPanel createInfoPanel() {
        RoundedPanel panel = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_L));

        panel.setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP_L, Theme.GAP_L,
                Theme.GAP_L, Theme.GAP_L));

        panel.add(createDescription(), BorderLayout.NORTH);
        panel.add(createNameInput(), BorderLayout.CENTER);

        return panel;
    }

    private JTextArea createDescription() {
        JTextArea description = new JTextArea(
                "A gamified way to learn Java, from the basics all the way to object-oriented programming.");

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

        RoundedButton startButton = new RoundedButton(
                "GET STARTED", ButtonStyle.PRIMARY);

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();

            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter your name first!",
                        "Name Required",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            onContinue.accept(playerName);
        });

        JPanel input = new JPanel(new BorderLayout());
        input.setOpaque(false);

        // Creates a panel to group the Name label and text field.
        JPanel nameInput = new JPanel();

        // Arranges the label and text field vertically (one above the other).
        nameInput.setLayout(new BoxLayout(nameInput, BoxLayout.Y_AXIS));

        // Makes the panel transparent so the parent panel's background remains visible.
        nameInput.setOpaque(false);

        // Centers the Name label horizontally.
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sets the text field's maximum width to 500px and height to 80px.
        nameField.setMaximumSize(new Dimension(500, 80));

        // Centers the text field horizontally.
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adds the Name label and text field to the panel.
        nameInput.add(label);
        nameInput.add(nameField);

        // Creates another panel to group the name input and Start button.
        JPanel content = new JPanel();

        // Arranges the components vertically.
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Makes the panel transparent.
        content.setOpaque(false);

        // Adds the name input section.
        content.add(nameInput);

        // Adds 40px of vertical space between the text field and button.
        content.add(Box.createVerticalStrut(40));

        // Adds the Get Started button.
        content.add(startButton);

        // Places the content panel at the top of the input panel.
        input.add(content, BorderLayout.NORTH);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_L, 0, 0, 0
                )
        );
        return input;
    }
}
