
package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.Theme;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JavaCodeEditor extends JTextPane {

    private static final Pattern TOKENS = Pattern.compile(
            "(?<comment>//[^\\n]*|/\\*[\\s\\S]*?\\*/)"
            + "|(?<string>\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*')"
            + "|(?<keyword>\\b(?:abstract|assert|boolean|break|byte|case|catch|"
            + "char|class|continue|default|do|double|else|enum|extends|final|"
            + "finally|float|for|if|implements|import|instanceof|int|interface|"
            + "long|new|package|private|protected|public|return|short|static|"
            + "super|switch|this|throw|throws|try|void|while|true|false|null)\\b)"
            + "|(?<number>\\b\\d+(?:\\.\\d+)?\\b)"
    );

    private final SimpleAttributeSet normal =
            color(Theme.TEXT);

    private final SimpleAttributeSet keyword =
            color(Theme.ACCENT);

    private final SimpleAttributeSet string =
            color(new Color(0xA5D6A7));

    private final SimpleAttributeSet comment =
            color(Theme.TEXT_MUTED);

    private final SimpleAttributeSet number =
            color(new Color(0x89B4FA));

    private boolean lineWrap = false;
    private int tabSize = 4;

    public JavaCodeEditor(
            String text,
            int rows,
            int columns) {

        setFont(Theme.FONT_CODE);
        setBackground(Theme.INSET);
        setForeground(Theme.TEXT);
        setCaretColor(Theme.ACCENT);

        setText(text);
        installTabKey();

        getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        scheduleHighlight();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        scheduleHighlight();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        // Ignore formatting-only changes.
                    }
                }
        );

        scheduleHighlight();
    }

    private static SimpleAttributeSet color(Color value) {
        SimpleAttributeSet style = new SimpleAttributeSet();

        StyleConstants.setForeground(style, value);

        return style;
    }

    private void scheduleHighlight() {
        SwingUtilities.invokeLater(this::highlightSyntax);
    }

    private void highlightSyntax() {

        StyledDocument document = getStyledDocument();
        String source = getText();

        if (source.isEmpty()) {
            return;
        }

        // Reset text to default styling
        document.setCharacterAttributes(
                0,
                source.length(),
                normal,
                true
        );

        Matcher matcher = TOKENS.matcher(source);

        while (matcher.find()) {

            SimpleAttributeSet style;

            if (matcher.group("comment") != null) {
                style = comment;

            } else if (matcher.group("string") != null) {
                style = string;

            } else if (matcher.group("keyword") != null) {
                style = keyword;

            } else {
                style = number;
            }

            document.setCharacterAttributes(
                    matcher.start(),
                    matcher.end() - matcher.start(),
                    style,
                    false
            );
        }
    }

    // Keep compatibility with QuestionScreen
    public int getLineCount() {
        return getDocument()
                .getDefaultRootElement()
                .getElementCount();
    }

    public void setLineWrap(boolean wrap) {
        lineWrap = wrap;
        revalidate();
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return lineWrap
                && super.getScrollableTracksViewportWidth();
    }

    public void setTabSize(int size) {
        tabSize = Math.max(1, size);
    }

    private void installTabKey() {

        getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
                "indent-code"
        );

        getActionMap().put(
                "indent-code",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        replaceSelection(" ".repeat(tabSize));
                    }
                }
        );
    }
}
