
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.CodeBlockPanel;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
    private final Topic topic;

    private final JLabel title = new JLabel();
    private final JLabel pageCounter = new JLabel();
    private final JTextPane content = new JTextPane();
    private final JTextPane quickReference = new JTextPane();

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
        this.topic = topic;

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


        // LEFT: TEXTBOOK CONTENT
        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.setBorder(
        BorderFactory.createLineBorder(Theme.INSET_BORDER)
        );

        scrollPane.getViewport().setBackground(Theme.INSET);


        // RIGHT: QUICK REFERENCE PANEL
        quickReference.setEditable(false);
        quickReference.setFont(
        new Font(Font.SANS_SERIF, Font.PLAIN, 16)
        );

        quickReference.setBackground(Theme.PANEL);
        quickReference.setForeground(Theme.TEXT);

        quickReference.setBorder(
        BorderFactory.createEmptyBorder(
                Theme.GAP_M,
                Theme.GAP_M,
                Theme.GAP_M,
                Theme.GAP_M
        )
        );

        quickReference.setText(
        """
        QUICK REFERENCE

        Important lesson concepts will appear here.
        """
        );

        JScrollPane referenceScroll =
        new JScrollPane(quickReference);

        referenceScroll.setBorder(
        BorderFactory.createLineBorder(Theme.PANEL_BORDER)
        );

        referenceScroll.getViewport().setBackground(Theme.PANEL);


        // TWO-COLUMN LAYOUT
        JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        scrollPane,
        referenceScroll
        );

        splitPane.setResizeWeight(0.65);
        splitPane.setDividerLocation(0.65);
        splitPane.setDividerSize(8);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);


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

                quickReference.setText(
                "QUICK REFERENCE\n\nNo lesson material available."
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

        displayLessonBlocks(page.body());

        updateQuickReference(page);

        previousButton.setEnabled(pageIndex > 0);

        nextButton.setEnabled(
                pageIndex < pages.size() - 1
        );
    }


        private void displayLessonBlocks(List<String> lines) {

        content.setText("");

        StyledDocument document = content.getStyledDocument();

        // Style for ordinary explanations
        SimpleAttributeSet proseStyle = new SimpleAttributeSet();

        StyleConstants.setFontFamily(
                proseStyle, Font.SANS_SERIF
        );

        StyleConstants.setFontSize(proseStyle, 16);

        StyleConstants.setForeground(
                proseStyle, Theme.TEXT
        );

        List<String> prose = new ArrayList<>();
        StringBuilder javaCode = new StringBuilder();

        try {

                for (String line : lines) {

                if (isJavaCodeLine(line)) {

                        // Display any explanation before the code
                        if (!prose.isEmpty()) {
                        appendProseBlock(
                                document, prose, proseStyle
                        );
                        prose.clear();
                        }

                        // Remove the four-space console indentation
                        javaCode.append(line.substring(4))
                                .append('\n');

                } else {

                        // Finish the current Java code block
                        if (!javaCode.isEmpty()) {

                        appendCodeBlock(
                                document,
                                javaCode.toString(),
                                proseStyle
                        );

                        javaCode.setLength(0);
                        }

                        prose.add(line);
                }
                }

                // Display any remaining content
                if (!javaCode.isEmpty()) {
                appendCodeBlock(
                        document,
                        javaCode.toString(),
                        proseStyle
                );
                }

                if (!prose.isEmpty()) {
                appendProseBlock(
                        document, prose, proseStyle
                );
                }

                content.setCaretPosition(0);

        } catch (BadLocationException e) {
                throw new IllegalStateException(
                        "Could not render textbook page", e
                );
        }
        }

        private boolean isJavaCodeLine(String line) {

        if (!line.startsWith("    ")) {
                return false;
        }

        String trimmed = line.trim();

        // Avoid treating textbook diagrams as Java code
        if (trimmed.contains("->")) {
                return false;
        }

        return trimmed.contains(";")
                || trimmed.endsWith("{")
                || trimmed.equals("}")
                || trimmed.startsWith("//")
                || trimmed.matches(
                        "^(if|for|while|switch|catch)\\s*\\(.*\\)$"
                );
        }


        private void appendProseBlock(
                StyledDocument document,
                List<String> lines,
                SimpleAttributeSet style)
                throws BadLocationException {

        // Reuse the paragraph formatter from Step 5C
        String text = formatLessonBody(lines);

        if (!text.isBlank()) {
                document.insertString(
                        document.getLength(),
                        text + "\n\n",
                        style
                );
        }
        }

        private void appendCodeBlock(
                StyledDocument document,
                String text,
                SimpleAttributeSet style)
                throws BadLocationException {

        String code = text.stripTrailing();

        int lineCount = code.split("\n", -1).length;

        // Use our new reusable component
        CodeBlockPanel codePanel = new CodeBlockPanel(
                "JAVA EXAMPLE",
                code
        );

        codePanel.setPreferredSize(
                new Dimension(
                        480,
                        Math.max(96, 66 + 23 * lineCount)
                )
        );

        // Insert the actual Swing component into the textbook
        content.setCaretPosition(document.getLength());
        content.insertComponent(codePanel);

        // Add spacing after the code
        document.insertString(
                document.getLength(),
                "\n\n",
                style
        );
        }


    private void displayFormattedContent(String text) {
        StyledDocument document = content.getStyledDocument();

        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setForeground(normal, Theme.TEXT);
        StyleConstants.setFontFamily(normal, Font.SANS_SERIF);
        StyleConstants.setFontSize(normal, 16);

        SimpleAttributeSet codeStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(codeStyle, Font.MONOSPACED);
        StyleConstants.setFontSize(codeStyle, 15);
        StyleConstants.setForeground(codeStyle, Theme.TEXT);
        StyleConstants.setBackground(codeStyle, Theme.PANEL);

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

                SimpleAttributeSet paragraphStyle =
                new SimpleAttributeSet();

                StyleConstants.setLineSpacing(
                        paragraphStyle, 0.20f
                );

                document.setParagraphAttributes(
                        0,
                        document.getLength(),
                        paragraphStyle,
                        false
                );

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
                document.setCharacterAttributes(
                        offset,
                        line.length(),
                        codeStyle,
                        false
                );
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

    
        private String formatLessonBody(List<String> lines) {

        StringBuilder formatted = new StringBuilder();

        boolean previousWasProse = false;

        for (String line : lines) {

                // Preserve paragraph separation
                if (line.isBlank()) {

                if (formatted.length() > 0
                        && !formatted.toString().endsWith("\n\n")) {

                        formatted.append("\n\n");
                }

                previousWasProse = false;
                continue;
                }

                // Indented lines are treated as code,
                // examples, tables, or special formatting.
                boolean isProse =
                        !Character.isWhitespace(line.charAt(0));

                if (isProse && previousWasProse) {

                // Join console-wrapped explanation lines
                formatted.append(" ").append(line.trim());

                } else {

                if (formatted.length() > 0
                        && formatted.charAt(formatted.length() - 1)
                        != '\n') {

                        formatted.append("\n");
                }

                formatted.append(line);
                }

                previousWasProse = isProse;
        }

        return formatted.toString().stripTrailing();
        }



        private void styleQuickReference() {

        StyledDocument document =
                quickReference.getStyledDocument();

        String text = quickReference.getText();

        // DEFAULT TEXT
        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setFontFamily(normal, Font.SANS_SERIF);
        StyleConstants.setFontSize(normal, 15);
        StyleConstants.setForeground(normal, Theme.TEXT);

        document.setCharacterAttributes(
                0, document.getLength(), normal, true
        );

        // MAIN TITLE
        SimpleAttributeSet titleStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(titleStyle, Font.SANS_SERIF);
        StyleConstants.setFontSize(titleStyle, 20);
        StyleConstants.setBold(titleStyle, true);
        StyleConstants.setForeground(titleStyle, Theme.ACCENT);

        // SECTION HEADINGS
        SimpleAttributeSet headingStyle = new SimpleAttributeSet();
        StyleConstants.setFontSize(headingStyle, 13);
        StyleConstants.setBold(headingStyle, true);
        StyleConstants.setForeground(headingStyle, Theme.ACCENT);

        // PAGE INFORMATION
        SimpleAttributeSet mutedStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(mutedStyle, Theme.TEXT_MUTED);
        StyleConstants.setFontSize(mutedStyle, 13);

        // CURRENT FOCUS
        SimpleAttributeSet focusStyle = new SimpleAttributeSet();
        StyleConstants.setBold(focusStyle, true);
        StyleConstants.setForeground(focusStyle, Theme.TEXT);
        StyleConstants.setFontSize(focusStyle, 17);

        // JAVA CODE
        SimpleAttributeSet codeStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(codeStyle, Font.MONOSPACED);
        StyleConstants.setForeground(
                codeStyle, new Color(0xA5D6A7)
        );
        StyleConstants.setBackground(codeStyle, Theme.INSET);
        StyleConstants.setFontSize(codeStyle, 15);

        String[] lines = text.split("\n", -1);

        int offset = 0;
        String previousLine = "";

        for (String line : lines) {

                SimpleAttributeSet selectedStyle = null;

                if (line.equals("LESSON SNAPSHOT")) {
                selectedStyle = titleStyle;

                } else if (line.equals("CURRENT FOCUS")
                        || line.equals("KEY IDEA")
                        || line.equals("CODE TO REMEMBER")
                        || line.equals("STUDY TIP")) {

                selectedStyle = headingStyle;

                } else if (line.contains("  |  Page ")) {
                selectedStyle = mutedStyle;

                } else if (previousLine.equals("CURRENT FOCUS")) {
                selectedStyle = focusStyle;

                } else if (previousLine.equals("CODE TO REMEMBER")) {
                selectedStyle = codeStyle;
                }

                if (selectedStyle != null && !line.isEmpty()) {
                document.setCharacterAttributes(
                        offset,
                        line.length(),
                        selectedStyle,
                        false
                );
                }

                previousLine = line.trim();
                offset += line.length() + 1;
        }
        }


        private void updateQuickReference(LessonPage page) {

        // Find the first explanatory paragraph
        StringBuilder keyIdea = new StringBuilder();
        boolean readingParagraph = false;

        for (String line : page.body()) {

                String trimmed = line.trim();

                if (trimmed.isEmpty() || line.startsWith("    ")) {
                if (readingParagraph) {
                        break;
                }
                continue;
                }

                if (keyIdea.length() > 0) {
                keyIdea.append(" ");
                }

                keyIdea.append(trimmed);
                readingParagraph = true;
        }

        // Find the first recognizable Java code line
        String example = null;

        for (String line : page.body()) {

                String trimmed = line.trim();

                if (line.startsWith("    ")
                        && (trimmed.endsWith(";")
                        || trimmed.startsWith("//")
                        || trimmed.endsWith("{"))) {

                example = trimmed;
                break;
                }
        }

        // Keep the reference concise
        String summary = keyIdea.toString();

        if (summary.length() > 160) {
                int cut = summary.lastIndexOf(' ', 157);

                if (cut < 0) {
                cut = 157;
                }

                summary = summary.substring(0, cut) + "...";
        }

        if (summary.isBlank()) {
                summary = "Review the explanation on this page.";
        }

        String tip = (example != null)
                ? "Try writing this code without looking at it."
                : "Explain this concept in your own words.";

        // Build the reference panel
        StringBuilder notes = new StringBuilder();

        notes.append("LESSON SNAPSHOT\n\n");

        notes.append(topic.getLabel())
                .append("  |  Page ")
                .append(pageIndex + 1)
                .append(" of ")
                .append(pages.size())
                .append("\n\n");

        notes.append("CURRENT FOCUS\n");
        notes.append(page.title()).append("\n\n");

        notes.append("KEY IDEA\n");
        notes.append(summary).append("\n\n");

        if (example != null) {
                notes.append("CODE TO REMEMBER\n");
                notes.append(example).append("\n\n");
        }

        notes.append("STUDY TIP\n");
        notes.append(tip);

        quickReference.setText(notes.toString());

        styleQuickReference();

        quickReference.setCaretPosition(0);
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
