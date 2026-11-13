
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.MascotView;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;
import edu.batstateu.javajolt.ui.swing.component.RoundedTextField;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
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

        JPanel header = new JPanel();
        header.setOpaque(false);

        header.setLayout(
                new BoxLayout(header, BoxLayout.Y_AXIS)
        );

        JLabel subtitle = new JLabel(
                "LEARN JAVA  •  EARN XP  •  LEVEL UP"
        );

        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.ACCENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("WELCOME TO JAVAJOLT");

        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(subtitle);

        header.add(
                Box.createVerticalStrut(Theme.GAP_S)
        );

        header.add(title);

        return header;
        }

    
private RoundedPanel createMascotPanel() {

    RoundedPanel panel = new RoundedPanel(
        new BorderLayout(0, Theme.GAP_M)
    );

    panel.setBorder(
        BorderFactory.createEmptyBorder(
            Theme.GAP_L,
            Theme.GAP_L,
            Theme.GAP_L,
            Theme.GAP_L
        )
    );

    JLabel heading = new JLabel(
        "YOUR CODING COMPANION"
    );

    heading.setFont(Theme.FONT_BODY);
    heading.setForeground(Theme.ACCENT);

    panel.add(heading, BorderLayout.NORTH);

    // Center the mascot without stretching it
    JPanel mascotHolder = new JPanel(
        new GridBagLayout()
    );

    mascotHolder.setOpaque(false);

    MascotView mascot = new MascotView();
    mascot.showIdle();

    mascot.setPreferredSize(
        new Dimension(230, 260)
    );

    mascotHolder.add(mascot);

    panel.add(mascotHolder, BorderLayout.CENTER);

    JLabel caption = new JLabel(
        "Learn. Practice. Level up."
    );

    caption.setFont(Theme.FONT_BODY);
    caption.setForeground(Theme.TEXT_MUTED);

    panel.add(caption, BorderLayout.SOUTH);

    return panel;
}


    private JPanel createBody() {
        JPanel body = new JPanel(
                new GridLayout(1, 2, Theme.GAP_L, 0));

        body.setOpaque(false);

        body.add(createMascotPanel());
        body.add(createInfoPanel());

        return body;
    }

    private RoundedPanel createInfoPanel() {
        RoundedPanel panel = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_L));

        panel.setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP_L, Theme.GAP_L,
                Theme.GAP_L, Theme.GAP_L));

        
        JPanel intro = new JPanel(
        new BorderLayout(0, Theme.GAP_S)
        );

        intro.setOpaque(false);

        JLabel introTitle = new JLabel(
        "READY TO START?"
        );

        introTitle.setFont(Theme.FONT_HEADING);
        introTitle.setForeground(Theme.TEXT);

        intro.add(introTitle, BorderLayout.NORTH);
        intro.add(createDescription(), BorderLayout.CENTER);

        intro.setPreferredSize(new Dimension(0, 105));

        panel.add(intro, BorderLayout.NORTH);

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
        JLabel label = new JLabel("YOUR NAME");
        label.setFont(Theme.FONT_BODY);
        label.setForeground(Theme.TEXT_MUTED);

        RoundedButton startButton = new RoundedButton(
                "GET STARTED", ButtonStyle.PRIMARY);

                startButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                startButton.setMaximumSize(new Dimension(200, 52));

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

        nameField.addActionListener(e -> startButton.doClick());

        JPanel input = new JPanel(new BorderLayout());
        input.setOpaque(false);

        // Creates a panel to group the Name label and text field.
        JPanel nameInput = new JPanel();

        // Arranges the label and text field vertically (one above the other).
        nameInput.setLayout(new BoxLayout(nameInput, BoxLayout.Y_AXIS));

        // Makes the panel transparent so the parent panel's background remains visible.
        nameInput.setOpaque(false);

        // Centers the Name label horizontally.
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sets the text field's maximum width to 500px and height to 80px.
        nameField.setFont(Theme.FONT_BODY);
        nameField.setPreferredSize(new Dimension(360, 52));
        nameField.setMaximumSize(new Dimension(360, 52));

        // Centers the text field horizontally.
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adds the Name label and text field to the panel.
        nameInput.add(label);
        nameInput.add(Box.createVerticalStrut(Theme.GAP_S));
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
        content.add(Box.createVerticalStrut(Theme.GAP_L));

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
