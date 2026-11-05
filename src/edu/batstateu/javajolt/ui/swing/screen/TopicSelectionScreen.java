package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TopicSelectionScreen extends Screen {

    private static final int GAP_M = 12;

    public TopicSelectionScreen(
            List<Topic> availableTopics,
            Consumer<Topic> onTopicSelected,
            Runnable onBack) {

        Topic[] topics = Topic.values();
        Set<Topic> unlocked = Set.copyOf(availableTopics);

        JLabel title = new JLabel("CHOOSE A TOPIC");
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT);
        add(title, BorderLayout.NORTH);

        
        JPanel topicPanel = new JPanel(
                new GridLayout(0, 2, GAP_M, GAP_M)
        );

        topicPanel.setOpaque(false);

        // Create a card for every Java topic
        for (int i = 0; i < topics.length; i++) {
        Topic topic = topics[i];

        topicPanel.add(new TopicCard(
                topic,
                i + 1,
                unlocked.contains(topic),
                onTopicSelected
        ));
        }

        // Center the topic grid without stretching the cards
        JPanel centeredPanel = new JPanel(
                new GridBagLayout()
        );

        centeredPanel.setBackground(Theme.BACKGROUND);
        centeredPanel.add(topicPanel);

        // Allow scrolling when the window becomes smaller
        JScrollPane scrollPane = new JScrollPane(
                centeredPanel
        );

        scrollPane.setBorder(null);

        scrollPane.getViewport().setBackground(
                Theme.BACKGROUND
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        // Make scrolling smoother
        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);


        scrollPane.getViewport().addComponentListener(
        new ComponentAdapter() {

                private int currentColumns = 2;

                @Override
                public void componentResized(ComponentEvent e) {

                int availableWidth =
                        scrollPane.getViewport().getWidth();

                // Switch to one column in narrower windows
                int newColumns = availableWidth >= 700 ? 2 : 1;

                // Only update when the column count changes
                if (newColumns != currentColumns) {

                        currentColumns = newColumns;

                        topicPanel.setLayout(
                        new GridLayout(
                                0,
                                newColumns,
                                GAP_M,
                                GAP_M
                        )
                        );

                        // Refresh the layout
                        topicPanel.revalidate();
                        centeredPanel.revalidate();
                        scrollPane.revalidate();
                        scrollPane.repaint();
                }
                }
        }
        );

        add(scrollPane, BorderLayout.CENTER);


        add(topicPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        RoundedButton back = new RoundedButton("BACK", ButtonStyle.SECONDARY);
        back.addActionListener(e -> onBack.run());

        JLabel progress = new JLabel(
                unlocked.size() + " of " + topics.length + " topics available");
        progress.setFont(Theme.FONT_BODY.deriveFont(13f));
        progress.setForeground(Theme.TEXT_MUTED);

        footer.add(back, BorderLayout.WEST);
        footer.add(progress, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private static String description(Topic topic) {
        return switch (topic) {
            case VARIABLES -> "Store and update values<br>in your program.";
            case OPERATORS -> "Perform calculations<br>and comparisons.";
            case DECISIONS -> "Control logic using<br>if, else, and switch.";
            case LOOPS -> "Repeat instructions with<br>for and while loops.";
            case ARRAYS -> "Organize multiple values<br>in one collection.";
            case EXCEPTION_HANDLING -> "Handle errors safely<br>with try and catch.";
            case OOP -> "Build reusable programs<br>with classes and objects.";
        };
    }

    private static final class TopicCard extends JButton {
        private static final Dimension CARD_SIZE = new Dimension(310, 148);
        private static final Color LOCKED_BACKGROUND = new Color(0x1B1D21);

        TopicCard(Topic topic, int number, boolean available,
                  Consumer<Topic> onTopicSelected) {
            setPreferredSize(CARD_SIZE);
            setLayout(new BorderLayout(0, Theme.GAP_S));
            setBorder(BorderFactory.createEmptyBorder(14, 18, 12, 18));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setEnabled(available);
            setCursor(Cursor.getPredefinedCursor(
                    available ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            setToolTipText(available
                    ? "Open the " + topic.getLabel() + " lesson"
                    : "Complete the previous lesson to unlock this topic");
            if (available) {
                addActionListener(e -> onTopicSelected.accept(topic));
            }

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);

            JLabel numberLabel = new JLabel(String.format("LESSON %02d", number));
            numberLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
            numberLabel.setForeground(Theme.TEXT_MUTED);

            JLabel status = new JLabel(available ? "AVAILABLE" : "LOCKED");
            status.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
            status.setForeground(available ? Theme.ACCENT : Theme.TEXT_MUTED);

            top.add(numberLabel, BorderLayout.WEST);
            top.add(status, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JPanel middle = new JPanel();
            middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
            middle.setOpaque(false);

            JLabel name = new JLabel(topic == Topic.OOP
                    ? "<html>Object-Oriented Programming</html>"
                    : topic.getLabel());
            name.setFont(Theme.FONT_HEADING.deriveFont(Font.BOLD, 18f));
            name.setForeground(available ? Theme.TEXT : Theme.TEXT_MUTED);
            name.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel summary = new JLabel("<html>" + description(topic) + "</html>");
            summary.setFont(Theme.FONT_BODY.deriveFont(13f));
            summary.setForeground(available ? Theme.TEXT_MUTED : new Color(0x717783));
            summary.setAlignmentX(Component.LEFT_ALIGNMENT);

            middle.add(name);
            middle.add(Box.createVerticalStrut(5));
            middle.add(summary);
            add(middle, BorderLayout.CENTER);

            JLabel action = new JLabel(available
                    ? "OPEN LESSON  >"
                    : "COMPLETE PREVIOUS LESSONS");
            action.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
            action.setForeground(available ? Theme.ACCENT : Theme.TEXT_MUTED);
            add(action, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            boolean highlighted = isEnabled()
                    && (getModel().isRollover() || hasFocus());
            g.setColor(!isEnabled() ? LOCKED_BACKGROUND
                    : highlighted ? Theme.INSET : Theme.PANEL);
            g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

            g.setStroke(new BasicStroke(highlighted ? 2f : 1f));
            g.setColor(highlighted ? Theme.ACCENT : Theme.PANEL_BORDER);
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
