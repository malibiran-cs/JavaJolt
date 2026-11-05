
package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.Theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CodeBlockPanel extends RoundedPanel {

    public CodeBlockPanel(String heading, String codeText) {

        super(new BorderLayout(0, Theme.GAP_S),
                "panels/panel_inset", 14);

        setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP_M,
                Theme.GAP_M,
                Theme.GAP_M,
                Theme.GAP_M
        ));

        // CODE BLOCK HEADER
        JLabel label = new JLabel(heading);
        label.setFont(
                Theme.FONT_BODY.deriveFont(Font.BOLD, 12f)
        );
        label.setForeground(Theme.ACCENT);

        add(label, BorderLayout.NORTH);

        // CODE DISPLAY
        JTextPane code = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };

        code.setEditable(false);
        code.setOpaque(false);
        code.setFont(Theme.FONT_CODE);
        code.setForeground(Theme.TEXT);
        code.setText(codeText.stripTrailing());

        // Apply syntax highlighting
        highlightJava(code);

        code.setCaretPosition(0);

        // Allow horizontal scrolling for long code lines
        JScrollPane scroller = new JScrollPane(
                code,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scroller.setBorder(null);
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);

        add(scroller, BorderLayout.CENTER);
    }

    private static void highlightJava(JTextPane pane) {

        StyledDocument document =
                pane.getStyledDocument();

        String text = pane.getText();

        SimpleAttributeSet normal =
                new SimpleAttributeSet();

        StyleConstants.setForeground(normal, Theme.TEXT);
        StyleConstants.setFontFamily(normal, Font.MONOSPACED);
        StyleConstants.setFontSize(normal, 15);

        document.setCharacterAttributes(
                0, document.getLength(), normal, true
        );

        // JAVA KEYWORDS
        colorMatches(document, text,
                "\\b(?:public|private|protected|static|final|"
                + "class|void|int|long|double|float|boolean|"
                + "char|String|if|else|for|while|return|"
                + "new|try|catch|throw|throws|true|false|null)\\b",
                new Color(0xF59E8B), true);

        // NUMBERS
        colorMatches(document, text,
                "\\b\\d+(?:\\.\\d+)?\\b",
                new Color(0x86C5FF), false);

        // STRINGS
        colorMatches(document, text,
                "\"(?:\\\\.|[^\"\\\\])*\"",
                new Color(0xA5D6A7), false);

        // COMMENTS
        colorMatches(document, text,
                "(?m)//[^\\n]*$",
                Theme.TEXT_MUTED, false);
    }

    private static void colorMatches(
            StyledDocument document,
            String text,
            String regex,
            Color color,
            boolean bold) {

        SimpleAttributeSet style =
                new SimpleAttributeSet();

        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);

        Matcher matcher =
                Pattern.compile(regex).matcher(text);

        while (matcher.find()) {
            document.setCharacterAttributes(
                    matcher.start(),
                    matcher.end() - matcher.start(),
                    style,
                    false
            );
        }
    }
}
