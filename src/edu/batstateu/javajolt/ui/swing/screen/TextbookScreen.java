
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TextbookScreen extends Screen {

    private static final Pattern KEYWORD_PATTERN = Pattern.compile(
            "\\b(?:public|private|protected|static|final|class|"
            + "void|int|double|float|boolean|char|long|short|byte|"
            + "String|if|else|for|while|do|switch|case|break|"
            + "continue|return|new|try|catch|finally|throw|"
            + "throws|true|false|null)\\b"
    );

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");

    private static final Pattern STRING_PATTERN =
            Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\"");

    private final List<LessonPage> pages;

    private final JLabel title = new JLabel();
    private final JLabel pageCounter = new JLabel();
    private final JTextPane content = new JTextPane() {
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }
    };

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

            displayFormattedContent(
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

        displayFormattedContent(
                String.join(
                        System.lineSeparator(),
                        page.body()
                )
        );

        previousButton.setEnabled(pageIndex > 0);

        nextButton.setEnabled(
                pageIndex < pages.size() - 1
        );
    }

    private void displayFormattedContent(String text) {
        StyledDocument document = content.getStyledDocument();

        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setForeground(normal, Theme.TEXT);
        StyleConstants.setFontFamily(normal, Theme.FONT_CODE.getFamily());
        StyleConstants.setFontSize(normal, Theme.FONT_CODE.getSize());

        SimpleAttributeSet keyword = new SimpleAttributeSet();
        StyleConstants.setForeground(keyword, new Color(0xF59E8B));
        StyleConstants.setBold(keyword, true);

        SimpleAttributeSet string = new SimpleAttributeSet();
        StyleConstants.setForeground(string, new Color(0xA5D6A7));

        SimpleAttributeSet comment = new SimpleAttributeSet();
        StyleConstants.setForeground(comment, Theme.TEXT_MUTED);
        StyleConstants.setItalic(comment, true);

        SimpleAttributeSet number = new SimpleAttributeSet();
        StyleConstants.setForeground(number, new Color(0x86C5FF));

        try {
            document.remove(0, document.getLength());
            document.insertString(0, text, normal);

            String[] lines = text.split("\n", -1);
            int offset = 0;

            for (String line : lines) {
                String trimmed = line.trim();

                boolean codeLine = line.startsWith("    ")
                        && (line.contains(";")
                            || line.contains("{")
                            || line.contains("}")
                            || trimmed.startsWith("//"));

                if (codeLine) {
                    Matcher matcher = KEYWORD_PATTERN.matcher(line);

                    while (matcher.find()) {
                        document.setCharacterAttributes(
                                offset + matcher.start(),
                                matcher.end() - matcher.start(),
                                keyword,
                                false
                        );
                    }

                    matcher = NUMBER_PATTERN.matcher(line);

                    while (matcher.find()) {
                        document.setCharacterAttributes(
                                offset + matcher.start(),
                                matcher.end() - matcher.start(),
                                number,
                                false
                        );
                    }

                    matcher = STRING_PATTERN.matcher(line);

                    while (matcher.find()) {
                        document.setCharacterAttributes(
                                offset + matcher.start(),
                                matcher.end() - matcher.start(),
                                string,
                                false
                        );
                    }

                    int commentStart = line.indexOf("//");

                    if (commentStart >= 0
                            && countQuotes(line.substring(0, commentStart)) % 2 == 0) {
                        document.setCharacterAttributes(
                                offset + commentStart,
                                line.length() - commentStart,
                                comment,
                                false
                        );
                    }
                }

                offset += line.length() + 1;
            }

            content.setCaretPosition(0);

        } catch (BadLocationException e) {
            throw new IllegalStateException(
                    "Could not format textbook content", e
            );
        }
    }

    private int countQuotes(String text) {
        int count = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '"'
                    && (i == 0 || text.charAt(i - 1) != '\\')) {
                count++;
            }
        }

        return count;
    }
}
