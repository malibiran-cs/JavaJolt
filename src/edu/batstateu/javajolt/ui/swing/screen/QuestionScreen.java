package edu.batstateu.javajolt.ui.swing.screen;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import edu.batstateu.javajolt.ui.swing.component.JavaCodeEditor;
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.InputMode;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.CharacterPlaceholder;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;
import edu.batstateu.javajolt.ui.swing.component.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/** Displays a question and collects its answer; the owner supplies game callbacks. */
public class QuestionScreen extends Screen {
        private final JTextPane promptText = new JTextPane();
    private final JLabel progressLabel = new JLabel();
    private final JProgressBar questionProgress =
        new JProgressBar(0, 100);
    private final JLabel answerHeading = createHeading("ANSWER");
    private final JLabel playerNameLabel = createLabel("PLAYER");
    private final JLabel hpLabel = createLabel("HP: --");
    private final JLabel xpScoreLabel = createLabel("XP: --   SCORE: --");
    private final JPanel answerPanel = new RoundedPanel(new BorderLayout(Theme.GAP_M, Theme.GAP_M));
    private final JPanel answerContent = new JPanel(new BorderLayout(Theme.GAP_M, Theme.GAP_M));
    private final JPanel actionRow = new JPanel(new BorderLayout(Theme.GAP_M, 0));
    private final JLabel feedbackLabel = new JLabel(" ");
    private final RoundedButton submitButton = new RoundedButton("Submit answer", ButtonStyle.PRIMARY);
    private final RoundedButton nextButton = new RoundedButton("Next question", ButtonStyle.PRIMARY);
    private final RoundedButton topicsButton = new RoundedButton("Choose another topic", ButtonStyle.SECONDARY);
    private final Runnable onTopics;
    private RoundedTextField textField;
    private JavaCodeEditor codeArea;
    private ButtonGroup choiceGroup;
    private List<JRadioButton> choiceButtons = List.of();
    private List<String> choiceValues = List.of();

    public QuestionScreen(Consumer<String> onSubmit, Runnable onNext, Runnable onTopics) {
        this.onTopics = onTopics;
        add(createBattleArea(), BorderLayout.NORTH);
        answerPanel.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_L, Theme.GAP_L, Theme.GAP_L, Theme.GAP_L));
        answerContent.setOpaque(false);
        actionRow.setOpaque(false);
        answerPanel.add(answerContent, BorderLayout.CENTER);
        add(answerPanel, BorderLayout.CENTER);
        submitButton.addActionListener(event -> onSubmit.accept(readAnswer()));
        nextButton.addActionListener(event -> onNext.run());
        topicsButton.addActionListener(event -> this.onTopics.run());
        feedbackLabel.setForeground(Theme.TEXT);
        feedbackLabel.setFont(Theme.FONT_BODY);
    }

    private JPanel createBattleArea() {
        JPanel battle = new JPanel(new BorderLayout(Theme.GAP_L, 0));
        battle.setOpaque(false);
        JPanel playerSide = new JPanel(new BorderLayout(0, Theme.GAP_S));
        playerSide.setOpaque(false);
        CharacterPlaceholder player = new CharacterPlaceholder();
        player.setPreferredSize(new Dimension(Theme.CHARACTER_SIZE, Theme.CHARACTER_SIZE));
        JPanel playerStats = new JPanel(new GridLayout(3, 1));
        playerStats.setOpaque(false);
        playerStats.add(playerNameLabel);
        playerStats.add(hpLabel);
        playerStats.add(xpScoreLabel);
        playerSide.add(playerStats, BorderLayout.NORTH);
        playerSide.add(player, BorderLayout.CENTER);

        JPanel question = new JPanel(new BorderLayout(0, Theme.GAP_S));
        question.setOpaque(false);
        progressLabel.setForeground(Theme.TEXT_MUTED);
        progressLabel.setFont(Theme.FONT_BODY);
        promptText.setForeground(Theme.TEXT);
        promptText.setFont(Theme.FONT_HEADING);
        promptText.setEditable(false);
        promptText.setFocusable(false);
        promptText.setOpaque(false);
        promptText.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane promptScroll = new JScrollPane(promptText);
        promptScroll.setPreferredSize(new Dimension(
                Theme.QUESTION_PROMPT_WIDTH, Theme.QUESTION_PROMPT_HEIGHT));
        promptScroll.setBorder(BorderFactory.createEmptyBorder());
        promptScroll.setOpaque(false);
        promptScroll.getViewport().setOpaque(false);
        
        JPanel questionHeader = new JPanel(
                new BorderLayout(0, Theme.GAP_S)
        );

        questionHeader.setOpaque(false);

        questionProgress.setForeground(Theme.ACCENT);
        questionProgress.setBackground(Theme.INSET);
        questionProgress.setBorderPainted(false);
        questionProgress.setStringPainted(false);

        questionProgress.setPreferredSize(
                new Dimension(1, 8)
        );

        questionHeader.add(
                progressLabel,
                BorderLayout.NORTH
        );

        questionHeader.add(
                questionProgress,
                BorderLayout.SOUTH
        );

        question.add(
                questionHeader,
                BorderLayout.NORTH
        );

        question.add(promptScroll, BorderLayout.CENTER);

        CharacterPlaceholder enemy = new CharacterPlaceholder();
        enemy.setPreferredSize(new Dimension(Theme.CHARACTER_SIZE, Theme.CHARACTER_SIZE));
        JPanel enemySide = new JPanel(new BorderLayout());
        enemySide.setOpaque(false);
        enemySide.add(enemy, BorderLayout.CENTER);

        JPanel playerSection = new JPanel(new BorderLayout(Theme.GAP_M, 0));
        playerSection.setOpaque(false);
        playerSection.add(playerSide, BorderLayout.CENTER);
        playerSection.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.EAST);
        battle.add(question, BorderLayout.CENTER);
        JPanel enemySection = new JPanel(new BorderLayout(Theme.GAP_M, 0));
        enemySection.setOpaque(false);
        enemySection.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        enemySection.add(enemySide, BorderLayout.CENTER);
        battle.add(playerSection, BorderLayout.WEST);
        battle.add(enemySection, BorderLayout.EAST);
        battle.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.GAP_M, 0));
        return battle;
    }

    public void showQuestion(Question question, int number, int total, Player player) {
        playerNameLabel.setText(player.getName());
        hpLabel.setText("HP: " + player.getHp());
        xpScoreLabel.setText("XP: " + player.getXp() + "   SCORE: " + player.getScore());
        progressLabel.setText("QUESTION " + number + " OF " + total);
        questionProgress.setValue(
        total == 0
                ? 0
                : (int) Math.round(
                        100.0 * number / total
                )
        );
        renderQuestionPrompt(question.getQuestion());
        promptText.setCaretPosition(0);
        answerContent.removeAll();
        actionRow.removeAll();
        textField = null;
        codeArea = null;
        choiceGroup = null;
        choiceButtons = List.of();
        installQuizShortcut();
        choiceValues = List.of();
        feedbackLabel.setText(" ");
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nextButton.setVisible(false);
        submitButton.setVisible(true);
        answerContent.add(createAnswerEditor(question), BorderLayout.CENTER);
        answerContent.add(answerHeading, BorderLayout.NORTH);
        actionRow.add(feedbackLabel, BorderLayout.NORTH);
        actionRow.add(createCenteredButton(submitButton), BorderLayout.CENTER);
        answerContent.add(actionRow, BorderLayout.SOUTH);
        answerPanel.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_L, Theme.GAP_L, Theme.GAP_L, Theme.GAP_L));
        answerContent.revalidate();
        answerContent.repaint();
    }

    
        private void installQuizShortcut() {

        getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ENTER,
                        InputEvent.CTRL_DOWN_MASK
                ),
                "submit-or-continue"
        );

        getActionMap().put(
                "submit-or-continue",
                new AbstractAction() {

                        @Override
                        public void actionPerformed(
                                ActionEvent event) {

                        if (nextButton.isVisible()
                                && nextButton.isEnabled()) {

                                nextButton.doClick();

                        } else if (submitButton.isVisible()
                                && submitButton.isEnabled()) {

                                submitButton.doClick();
                        }
                        }
                }
        );

        submitButton.setToolTipText(
                "Submit answer (Ctrl+Enter)"
        );

        nextButton.setToolTipText(
                "Continue (Ctrl+Enter)"
        );
        }

        private void renderQuestionPrompt(String rawPrompt) {

        StyledDocument document =
                promptText.getStyledDocument();

        // Style for ordinary question instructions
        SimpleAttributeSet prose = new SimpleAttributeSet();

        StyleConstants.setFontFamily(
                prose, Theme.FONT_HEADING.getFamily()
        );

        StyleConstants.setFontSize(prose, 19);
        StyleConstants.setBold(prose, true);
        StyleConstants.setForeground(prose, Theme.TEXT);
        StyleConstants.setSpaceBelow(prose, 6);

        // Style for Java code snippets
        SimpleAttributeSet code = new SimpleAttributeSet();

        StyleConstants.setFontFamily(
                code, Theme.FONT_CODE.getFamily()
        );

        StyleConstants.setFontSize(
                code, Theme.FONT_CODE.getSize()
        );

        StyleConstants.setForeground(
                code, new java.awt.Color(0xC8D4F0)
        );

        StyleConstants.setLeftIndent(code, Theme.GAP_M);
        StyleConstants.setSpaceAbove(code, 2);
        StyleConstants.setSpaceBelow(code, 2);

        String prompt = rawPrompt == null
                ? ""
                : rawPrompt.replace("\r\n", "\n");

        try {
                document.remove(0, document.getLength());

                for (String line : prompt.split("\n", -1)) {

                SimpleAttributeSet style =
                        looksLikeCode(line) ? code : prose;

                int start = document.getLength();

                document.insertString(
                        start,
                        line + "\n",
                        style
                );

                document.setParagraphAttributes(
                        start,
                        line.length() + 1,
                        style,
                        false
                );
                }

        } catch (BadLocationException e) {
                throw new IllegalStateException(
                        "Unable to render question", e
                );
        }
        }

        private boolean looksLikeCode(String line) {

        String trimmed = line.strip();

        if (trimmed.isEmpty()) {
                return false;
        }

        return line.startsWith("    ")
                || trimmed.endsWith(";")
                || trimmed.endsWith("{")
                || trimmed.equals("}")
                || trimmed.startsWith("} else")
                || trimmed.startsWith("@Override")
                || trimmed.startsWith("//");
        }


    private JPanel createAnswerEditor(Question question) {
        if (question.getInputMode() == InputMode.MULTI_LINE) {
            return createCodeEditor(question);
        }
        if (!question.getChoices().isEmpty()) {
            return createChoiceEditor(question.getChoices());
        }
        return createIdentificationEditor();
    }

    
private JPanel createIdentificationEditor() {

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);

    // Main answer card
    RoundedPanel card = new RoundedPanel(
            new BorderLayout(0, Theme.GAP_M),
            "panels/panel_inset",
            16
    );

    card.setBorder(
            BorderFactory.createEmptyBorder(
                    Theme.GAP_L,
                    Theme.GAP_L,
                    Theme.GAP_L,
                    Theme.GAP_L
            )
    );

    // Answer heading
    JLabel title = createLabel("TYPE YOUR ANSWER");
    title.setForeground(Theme.ACCENT);

    card.add(title, BorderLayout.NORTH);

    // Answer input
    textField = new RoundedTextField();

    textField.setFont(Theme.FONT_HEADING);
    textField.setHorizontalAlignment(SwingConstants.LEFT);

    textField.setPreferredSize(
            new Dimension(
                    Theme.ANSWER_FIELD_WIDTH,
                    Theme.ANSWER_FIELD_HEIGHT
            )
    );

    // Pressing Enter submits the answer
    textField.addActionListener(event -> {
        if (submitButton.isVisible()
                && submitButton.isEnabled()) {
            submitButton.doClick();
        }
    });

    // Instructions
    JLabel hint = createLabel(
            "Enter a short answer, then press Enter or Submit Answer."
    );

    hint.setForeground(Theme.TEXT_MUTED);

    // Organize input and instructions
    JPanel input = new JPanel(
            new BorderLayout(0, Theme.GAP_S)
    );

    input.setOpaque(false);

    input.add(textField, BorderLayout.NORTH);
    input.add(hint, BorderLayout.SOUTH);

    card.add(input, BorderLayout.CENTER);

    // Position answer card toward the top
    JPanel top = new JPanel(new BorderLayout());

    top.setOpaque(false);

    top.setBorder(
            BorderFactory.createEmptyBorder(
                    Theme.GAP_M, 0, 0, 0
            )
    );

    top.add(card, BorderLayout.CENTER);

    wrapper.add(top, BorderLayout.NORTH);

    return wrapper;
}



    private JPanel createChoiceEditor(List<String> choices) {

        // Two-column grid for multiple-choice cards
        JPanel grid = new JPanel(
                new GridLayout(
                        0, 2,
                        Theme.GAP_M,
                        Theme.GAP_M
                )
        );

        grid.setOpaque(false);

        int rows = (choices.size() + 1) / 2;

        grid.setPreferredSize(
                new Dimension(
                        800,
                        rows * 82
                        + Math.max(0, rows - 1) * Theme.GAP_M
                )
        );

        choiceGroup = new ButtonGroup();

        java.util.ArrayList<JRadioButton> buttons =
                new java.util.ArrayList<>();

        for (String choice : choices) {

            JRadioButton button = new JRadioButton(
                    "<html><div style='width:300px'>"
                            + escapeHtml(choice)
                            + "</div></html>"
            );

            // Text and appearance
            button.setFont(Theme.FONT_BODY);
            button.setForeground(Theme.TEXT);

            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setBorderPainted(true);

            button.setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            button.setCursor(
                    java.awt.Cursor.getPredefinedCursor(
                            java.awt.Cursor.HAND_CURSOR
                    )
            );

            // Keep existing single-answer selection
            choiceGroup.add(button);
            buttons.add(button);
            grid.add(button);

            // Update all card styles when selected
            button.addActionListener(
                    event -> updateChoiceBorders(buttons)
            );

            // Hover appearance
            button.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent event) {
                    if (!button.isSelected()) {
                        button.setBackground(
                                Theme.SECONDARY_HOVER
                        );
                    }
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    updateChoiceBorders(buttons);
                }
            });
        }

        // Preserve answer retrieval compatibility
        choiceButtons = List.copyOf(buttons);
        choiceValues = List.copyOf(choices);

        updateChoiceBorders(buttons);

        // Center the cards without stretching them
        JPanel centered = new JPanel(
                new java.awt.FlowLayout(
                        java.awt.FlowLayout.CENTER,
                        0,
                        Theme.GAP_M
                )
        );

        centered.setOpaque(false);
        centered.add(grid);

        // Allow scrolling for longer answer lists
        JScrollPane scroller = new JScrollPane(
                centered,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroller.setBorder(null);
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);

        scroller.getVerticalScrollBar()
                .setUnitIncrement(16);

        JPanel wrapper = new JPanel(
                new BorderLayout()
        );

        wrapper.setOpaque(false);
        wrapper.add(scroller, BorderLayout.CENTER);

        return wrapper;
    }


    
private JPanel createCodeEditor(Question question) {

    JPanel editor = new JPanel(
            new BorderLayout(0, Theme.GAP_M)
    );

    editor.setOpaque(false);

    // =====================================
    // EDITOR HEADER
    // =====================================

    JPanel toolbar = new JPanel(new BorderLayout());
    toolbar.setOpaque(false);

    JLabel title = createLabel("JAVA CODE EDITOR");
    title.setForeground(Theme.ACCENT);

    JLabel instruction = createLabel(
            "Edit the template, then select Submit answer."
    );

    instruction.setForeground(Theme.TEXT_MUTED);

    toolbar.add(title, BorderLayout.WEST);
    toolbar.add(instruction, BorderLayout.EAST);

    editor.add(toolbar, BorderLayout.NORTH);

    // =====================================
    // MAIN CODE EDITOR
    // =====================================

    codeArea = new JavaCodeEditor(
            question.getTemplate(),
            Theme.CODE_EDITOR_ROWS,
            Theme.CODE_EDITOR_COLUMNS
    );
    codeArea.setFont(Theme.FONT_CODE);
    codeArea.setForeground(Theme.TEXT);
    codeArea.setCaretColor(Theme.ACCENT);

    codeArea.setSelectionColor(
            new java.awt.Color(0x504037)
    );

    codeArea.setSelectedTextColor(Theme.TEXT);
    codeArea.setBackground(Theme.INSET);

    codeArea.setBorder(
            BorderFactory.createEmptyBorder(
                    Theme.GAP_M,
                    Theme.GAP_M,
                    Theme.GAP_M,
                    Theme.GAP_M
            )
    );

    // Preserve Java code indentation
    codeArea.setLineWrap(false);
    codeArea.setTabSize(4);

    // =====================================
    // LINE NUMBER GUTTER
    // =====================================

    JTextArea lineNumbers = new JTextArea();

    lineNumbers.setEditable(false);
    lineNumbers.setFocusable(false);

    lineNumbers.setFont(Theme.FONT_CODE);
    lineNumbers.setForeground(Theme.TEXT_MUTED);
    lineNumbers.setBackground(Theme.PANEL);

    lineNumbers.setColumns(3);

    lineNumbers.setBorder(
            BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 0, 1,
                            Theme.INSET_BORDER
                    ),
                    BorderFactory.createEmptyBorder(
                            Theme.GAP_M,
                            Theme.GAP_S,
                            Theme.GAP_M,
                            Theme.GAP_S
                    )
            )
    );

    // Update line numbers when code changes
    Runnable refreshLineNumbers = () -> {

        int lineCount = codeArea.getLineCount();

        StringBuilder numbers = new StringBuilder();

        for (int i = 1; i <= lineCount; i++) {

            if (i > 1) {
                numbers.append('\n');
            }

            numbers.append(i);
        }

        lineNumbers.setText(numbers.toString());
    };

    codeArea.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void refresh() {
                    javax.swing.SwingUtilities.invokeLater(
                            refreshLineNumbers
                    );
                }

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    refresh();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    refresh();
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    refresh();
                }
            }
    );

    refreshLineNumbers.run();

    // =====================================
    // SCROLLABLE CODE AREA
    // =====================================

    JScrollPane scroll = new JScrollPane(
            codeArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );

    // Keep line numbers beside the code
    scroll.setRowHeaderView(lineNumbers);

    scroll.setBorder(
            BorderFactory.createLineBorder(
                    Theme.INSET_BORDER
            )
    );

    scroll.getViewport().setBackground(Theme.INSET);

    scroll.getVerticalScrollBar()
            .setUnitIncrement(16);

    editor.add(scroll, BorderLayout.CENTER);

    // =====================================
    // REQUIREMENTS FOOTER
    // =====================================

    StringBuilder hints =
            new StringBuilder("Expected output: ")
                    .append(question.getExpectedOutput());

    if (!question.getRequiredConcepts().isEmpty()) {

        hints.append("    |    Required: ")
                .append(String.join(
                        ", ",
                        question.getRequiredConcepts()
                ));
    }

    if (!question.getRequiredImports().isEmpty()) {

        hints.append("    |    Imports: ")
                .append(String.join(
                        ", ",
                        question.getRequiredImports()
                ));
    }

    JLabel requirements = createLabel(
            "<html>"
                    + escapeHtml(hints.toString())
                    + "</html>"
    );

    requirements.setForeground(Theme.TEXT_MUTED);

    editor.add(requirements, BorderLayout.SOUTH);

    return editor;
}



    private void updateChoiceBorders(
            List<JRadioButton> buttons) {

        for (JRadioButton button : buttons) {

            boolean selected = button.isSelected();

            // Selected answers receive an orange tint
            button.setBackground(
                    selected
                            ? new java.awt.Color(0x403128)
                            : Theme.INSET
            );

            // Selected answers receive an orange border
            button.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    selected
                                            ? Theme.ACCENT
                                            : Theme.INSET_BORDER,
                                    2,
                                    true
                            ),
                            BorderFactory.createEmptyBorder(
                                    14, 18, 14, 18
                            )
                    )
            );
        }
    }


    private String readAnswer() {
        if (codeArea != null) return codeArea.getText();
        if (textField != null) return textField.getText();
        for (int i = 0; i < choiceButtons.size(); i++) {
            if (choiceButtons.get(i).isSelected()) {
                String choice = choiceValues.get(i).trim();
                int dot = choice.indexOf('.');
                return dot > 0 ? choice.substring(0, dot).trim() : choice;
            }
        }
        return "";
    }


        public void showAnswerResult(
                boolean correct,
                String explanation,
                Player player) {

        // Refresh displayed player statistics
        hpLabel.setText("HP: " + player.getHp());

        xpScoreLabel.setText(
                "XP: " + player.getXp()
                + "   SCORE: " + player.getScore()
        );

        // Change the section heading
        answerHeading.setText("ANSWER FEEDBACK");

        answerContent.removeAll();
        actionRow.removeAll();

        answerHeading.setText("ANSWER");
        submitButton.setEnabled(true);

        // Display the new feedback card
        answerContent.add(
                createFeedbackCard(correct, explanation, player),
                BorderLayout.CENTER
        );

        answerContent.add(
                answerHeading,
                BorderLayout.NORTH
        );

        // Show the continuation button
        nextButton.setText(
                player.isAlive()
                        ? "Continue"
                        : "View results"
        );

        nextButton.setVisible(true);

        actionRow.add(
                createCenteredButton(nextButton),
                BorderLayout.CENTER
        );

        answerContent.add(
                actionRow,
                BorderLayout.SOUTH
        );

        answerContent.revalidate();
        answerContent.repaint();
        }


        private JPanel createFeedbackCard(
                boolean correct,
                String explanation,
                Player player) {

        RoundedPanel card = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_M),
                "panels/panel_inset",
                16
        );

        // Orange for correct, red for incorrect
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                correct
                                        ? Theme.ACCENT
                                        : Theme.DANGER,
                                2,
                                true
                        ),
                        BorderFactory.createEmptyBorder(
                                Theme.GAP_L,
                                Theme.GAP_L,
                                Theme.GAP_L,
                                Theme.GAP_L
                        )
                )
        );

        // FEEDBACK TITLE
        JLabel status = createHeading(
                correct
                        ? "CORRECT ANSWER!"
                        : "NOT QUITE!"
        );

        status.setForeground(
                correct
                        ? Theme.ACCENT
                        : Theme.DANGER
        );

        card.add(status, BorderLayout.NORTH);

        // EXPLANATION
        String message = correct
                ? "Well done!\n\n"
                : "Review the explanation and "
                        + "try the next question.\n\n";

        String explanationText =
                explanation == null || explanation.isBlank()
                        ? "No explanation available."
                        : explanation;

        JTextArea details = new JTextArea(
                message + explanationText
        );

        details.setFont(Theme.FONT_BODY);
        details.setForeground(Theme.TEXT);

        details.setOpaque(false);
        details.setEditable(false);
        details.setFocusable(false);

        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setCaretPosition(0);

        // Scroll if explanation is lengthy
        JScrollPane scroll = new JScrollPane(details);

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        card.add(scroll, BorderLayout.CENTER);

        // HP STATUS
        JLabel footer = createLabel(
                player.isAlive()
                        ? "HP remaining: " + player.getHp()
                        : "Out of HP - continue to see your results."
        );

        footer.setForeground(Theme.TEXT_MUTED);

        card.add(footer, BorderLayout.SOUTH);

        return card;
        }

    public void setCheckingAnswer(boolean checking) {
        submitButton.setEnabled(!checking);
    }

    public void showLessonResult(GameSession session, boolean passed) {
        answerContent.removeAll();
        String result = "LESSON " + (passed ? "COMPLETE!" : "OVER")
                + "\n\nTopic: " + session.getTopic().getLabel()
                + " (" + session.getDifficulty().getLabel() + ")\nCorrect: "
                + session.getCorrectCount() + " / " + session.getQuestions().size() + "\nMistakes: "
                + session.getMistakeCount() + "\nAccuracy: " + session.getAccuracyPercent()
                + "%\nScore: " + session.getScoreEarned() + "\nResult: "
                + (passed ? "PASSED" : "NOT PASSED - try this lesson again!")
                + (session.getPlayer().isAlive() ? "" : "\nYou ran out of HP before finishing.");
        answerContent.add(createResultArea(result), BorderLayout.CENTER);
        answerContent.add(answerHeading, BorderLayout.NORTH);
        actionRow.removeAll();
        actionRow.add(createCenteredButton(topicsButton), BorderLayout.CENTER);
        answerContent.add(actionRow, BorderLayout.SOUTH);
        promptText.setText("LESSON RESULTS");
        answerContent.revalidate();
        answerContent.repaint();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY);
        label.setForeground(Theme.TEXT);
        return label;
    }

    private JLabel createHeading(String text) {
        JLabel label = createLabel(text);
        label.setFont(Theme.FONT_HEADING);
        return label;
    }

    private JPanel createCenteredButton(RoundedButton button) {
        JPanel holder = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        holder.setOpaque(false);
        holder.add(button);
        return holder;
    }

    private JScrollPane createResultArea(String text) {
        JTextArea result = new JTextArea(text);
        result.setFont(Theme.FONT_BODY);
        result.setForeground(Theme.TEXT);
        result.setBackground(Theme.INSET);
        result.setLineWrap(true);
        result.setWrapStyleWord(true);
        result.setEditable(false);
        result.setFocusable(false);
        result.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_M, Theme.GAP_M, Theme.GAP_M));
        result.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(result);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.INSET);
        return scroll;
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }

}
