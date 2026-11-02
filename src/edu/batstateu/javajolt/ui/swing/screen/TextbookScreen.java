
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextbookScreen extends Screen {

    private final List<LessonPage> pages;

    private final JLabel title = new JLabel();
    private final JLabel pageCounter = new JLabel();
    private final JTextArea content = new JTextArea();

    private final RoundedButton previousButton =
            new RoundedButton("Previous", ButtonStyle.SECONDARY);

    private final RoundedButton nextButton =
            new RoundedButton("Next", ButtonStyle.SECONDARY);

    private int pageIndex = 0;

    public TextbookScreen(
            Topic topic,
            List<LessonPage> pages,
            Runnable onStartQuiz,
            Runnable onBack) {

        this.pages = List.copyOf(pages);

        // HEADER
        JLabel topicLabel =
                new JLabel("STUDY: " + topic.getLabel());

        topicLabel.setFont(Theme.FONT_HEADING);
        topicLabel.setForeground(Theme.ACCENT);

        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT);

        JPanel header =
                new JPanel(new BorderLayout(0, Theme.GAP_S));

        header.setOpaque(false);
        header.add(topicLabel, BorderLayout.NORTH);
        header.add(title, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // READING AREA
        content.setEditable(false);
        content.setFont(Theme.FONT_CODE);
        content.setBackground(Theme.INSET);
        content.setForeground(Theme.TEXT);
        content.setCaretColor(Theme.TEXT);
        content.setLineWrap(false);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_M,
                        Theme.GAP_M,
                        Theme.GAP_M,
                        Theme.GAP_M
                )
        );

        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        Theme.INSET_BORDER
                )
        );

        add(scrollPane, BorderLayout.CENTER);

        // PAGE NAVIGATION
        JPanel footer =
                new JPanel(new BorderLayout(Theme.GAP_S, 0));

        footer.setOpaque(false);

        JPanel navigation =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT, Theme.GAP_S, 0));

        navigation.setOpaque(false);

        previousButton.addActionListener(e ->
                showPage(pageIndex - 1));

        nextButton.addActionListener(e ->
                showPage(pageIndex + 1));

        pageCounter.setFont(Theme.FONT_BODY);
        pageCounter.setForeground(Theme.TEXT);

        navigation.add(previousButton);
        navigation.add(pageCounter);
        navigation.add(nextButton);

        // ACTION BUTTONS
        JPanel actions =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT, Theme.GAP_S, 0));

        actions.setOpaque(false);

        RoundedButton backButton =
                new RoundedButton(
                        "Back to Topics",
                        ButtonStyle.SECONDARY
                );

        backButton.addActionListener(e -> onBack.run());

        RoundedButton quizButton =
                new RoundedButton(
                        "Start Quiz",
                        ButtonStyle.PRIMARY
                );

        quizButton.addActionListener(e -> onStartQuiz.run());

        actions.add(backButton);
        actions.add(quizButton);

        footer.add(navigation, BorderLayout.WEST);
        footer.add(actions, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);

        // SHOW FIRST PAGE
        showPage(0);
    }

    private void showPage(int index) {

        if (pages.isEmpty()) {
            title.setText("No textbook pages available");
            pageCounter.setText("0 / 0");

            content.setText(
                    "No study material is available for this topic yet."
            );

            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            return;
        }

        pageIndex = Math.max(
                0,
                Math.min(index, pages.size() - 1)
        );

        LessonPage page = pages.get(pageIndex);

        title.setText(page.title());

        pageCounter.setText(
                (pageIndex + 1) + " / " + pages.size()
        );

        content.setText(
                String.join(
                        System.lineSeparator(),
                        page.body()
                )
        );

        content.setCaretPosition(0);

        previousButton.setEnabled(pageIndex > 0);

        nextButton.setEnabled(
                pageIndex < pages.size() - 1
        );
    }
}
