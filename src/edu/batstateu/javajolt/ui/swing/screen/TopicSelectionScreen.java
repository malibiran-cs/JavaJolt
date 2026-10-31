
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class TopicSelectionScreen extends Screen {

    public TopicSelectionScreen(
            List<Topic> availableTopics,
            Consumer<Topic> onTopicSelected) {

        setLayout(new BorderLayout(0, Theme.GAP_L));

        JLabel title = new JLabel("CHOOSE A TOPIC");
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT);

        add(title, BorderLayout.NORTH);

        JPanel topicPanel = new JPanel(
                new GridLayout(0, 2, Theme.GAP_M, Theme.GAP_M)
        );
        topicPanel.setOpaque(false);

        for (Topic topic : availableTopics) {
            RoundedButton button = new RoundedButton(
                    topic.getLabel(),
                    ButtonStyle.PRIMARY
            );

            button.addActionListener(e ->
                    onTopicSelected.accept(topic)
            );

            topicPanel.add(button);
        }

        add(topicPanel, BorderLayout.CENTER);
    }
}
