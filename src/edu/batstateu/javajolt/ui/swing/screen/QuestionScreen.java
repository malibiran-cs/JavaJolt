package edu.batstateu.javajolt.ui.swing.screen;

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
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/** Displays a question and collects its answer; the owner supplies game callbacks. */
public class QuestionScreen extends Screen {
    private final JTextArea promptText = new JTextArea();
    private final JLabel progressLabel = new JLabel();
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
    private JTextArea codeArea;
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
        promptText.setLineWrap(true);
        promptText.setWrapStyleWord(true);
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
        question.add(progressLabel, BorderLayout.NORTH);
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
        promptText.setText(question.getQuestion());
        promptText.setCaretPosition(0);
        answerContent.removeAll();
        actionRow.removeAll();
        textField = null;
        codeArea = null;
        choiceGroup = null;
        choiceButtons = List.of();
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

    private JPanel createAnswerEditor(Question question) {
        if (question.getInputMode() == InputMode.MULTI_LINE) {
            return createCodeEditor(question);
        }
        if (!question.getChoices().isEmpty()) {
            return createChoiceEditor(question.getChoices());
        }
        JPanel holder = new JPanel(new java.awt.GridBagLayout());
        holder.setOpaque(false);
        textField = new RoundedTextField();
        textField.setFont(Theme.FONT_HEADING);
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setPreferredSize(new Dimension(Theme.ANSWER_FIELD_WIDTH, Theme.ANSWER_FIELD_HEIGHT));
        holder.add(textField);
        return holder;
    }

    private JPanel createChoiceEditor(List<String> choices) {
    JPanel grid = new JPanel(
        new GridLayout(
            0, 2,
            Theme.GAP_M,
            Theme.GAP_M
        )
    );
        grid.setOpaque(false);

        JPanel holder = new JPanel(new java.awt.GridBagLayout());
        holder.setOpaque(false);
        holder.add(grid);
        
        //grid.setOpaque(false);
        choiceGroup = new ButtonGroup();
        java.util.ArrayList<JRadioButton> buttons = new java.util.ArrayList<>();
        for (String choice : choices) {
            JRadioButton button = new JRadioButton("<html><div style='width:" + Theme.CHOICE_TEXT_WIDTH
                    + "px'>" + escapeHtml(choice) + "</div></html>");
            button.setFont(Theme.FONT_BODY);
            button.setForeground(Theme.TEXT);
            button.setOpaque(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.INSET_BORDER),
                    BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_M, Theme.GAP_M, Theme.GAP_M)));
            button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Theme.ACCENT),
                            BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_M, Theme.GAP_M, Theme.GAP_M)));
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(button.isSelected()
                                    ? Theme.ACCENT : Theme.INSET_BORDER),
                            BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_M, Theme.GAP_M, Theme.GAP_M)));
                }
            });
            button.addActionListener(event -> updateChoiceBorders(buttons));
            choiceGroup.add(button);
            buttons.add(button);
            grid.add(button);
        }
        choiceButtons = List.copyOf(buttons);
        choiceValues = List.copyOf(choices);
        //return grid;
        return holder;
    }

    private JPanel createCodeEditor(Question question) {
        JPanel editor = new JPanel(new BorderLayout(0, Theme.GAP_S));
        editor.setOpaque(false);
        StringBuilder hints = new StringBuilder("Expected output: ").append(question.getExpectedOutput());
        if (!question.getRequiredConcepts().isEmpty()) {
            hints.append("    |    Required: ").append(String.join(", ", question.getRequiredConcepts()));
        }
        if (!question.getRequiredImports().isEmpty()) {
            hints.append("    |    Imports: ").append(String.join(", ", question.getRequiredImports()));
        }
        JLabel hintLabel = createLabel("<html>" + escapeHtml(hints.toString()) + "</html>");
        editor.add(hintLabel, BorderLayout.NORTH);
        codeArea = new JTextArea(question.getTemplate(), Theme.CODE_EDITOR_ROWS, Theme.CODE_EDITOR_COLUMNS);
        codeArea.setFont(Theme.FONT_CODE);
        codeArea.setForeground(Theme.TEXT);
        codeArea.setCaretColor(Theme.ACCENT);
        codeArea.setBackground(Theme.INSET);
        codeArea.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_M, Theme.GAP_M, Theme.GAP_M));
        codeArea.setTabSize(4);
        editor.add(new JScrollPane(codeArea), BorderLayout.CENTER);
        return editor;
    }

    private void updateChoiceBorders(List<JRadioButton> buttons) {
        for (JRadioButton button : buttons) {
            java.awt.Color borderColor = button.isSelected() ? Theme.ACCENT : Theme.INSET_BORDER;
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_M, Theme.GAP_M, Theme.GAP_M)));
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

    public void showAnswerResult(boolean correct, String explanation, Player player) {
        submitButton.setEnabled(true);
        answerContent.removeAll();
        String status = correct ? "CORRECT!" : player.isAlive() ? "NOT QUITE.  HP LEFT: " + player.getHp() : "NOT QUITE.  OUT OF HP!";
        answerContent.add(createResultArea(status + "\n\n" + explanation), BorderLayout.CENTER);
        answerContent.add(answerHeading, BorderLayout.NORTH);
        actionRow.removeAll();
        actionRow.add(createCenteredButton(nextButton), BorderLayout.CENTER);
        nextButton.setVisible(true);
        answerContent.add(actionRow, BorderLayout.SOUTH);
        answerContent.revalidate();
        answerContent.repaint();
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
