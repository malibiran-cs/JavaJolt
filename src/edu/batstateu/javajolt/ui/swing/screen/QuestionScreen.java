package edu.batstateu.javajolt.ui.swing.screen;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import edu.batstateu.javajolt.ui.swing.SoundEffect;
import edu.batstateu.javajolt.ui.swing.UiSounds;
import edu.batstateu.javajolt.ui.swing.component.JavaCodeEditor;
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.InputMode;
import edu.batstateu.javajolt.enums.JoltMessage;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.ui.swing.ButtonSounds;
import edu.batstateu.javajolt.ui.swing.SoundEffect;
import edu.batstateu.javajolt.ui.swing.UiSounds;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.MascotView;
import edu.batstateu.javajolt.ui.swing.component.EnemyPortraitView;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;
import edu.batstateu.javajolt.ui.swing.component.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final JLabel countdownLabel = createLabel(" ");
    private final JProgressBar questionProgress =
        new JProgressBar(0, 100);
    private final JLabel answerHeading = createHeading("ANSWER");
    private final JLabel playerNameLabel = createLabel("PLAYER");
    private final JLabel hpLabel = createLabel("HP: --");
    private final JLabel xpLabel = createLabel("XP: --");
    private final JLabel scoreLabel = createLabel("QUIZ SCORE: --");
    private final JLabel joltLabel = createLabel("JOLT: --");
    private final JLabel joltMessageLabel = new JLabel();
    private final JPanel answerPanel = new RoundedPanel(new BorderLayout(Theme.GAP_M, Theme.GAP_M));
    private final JPanel answerContent = new JPanel(new BorderLayout(Theme.GAP_M, Theme.GAP_M));
    private final JPanel actionRow = new JPanel(new BorderLayout(Theme.GAP_M, 0));
    private final JLabel feedbackLabel = new JLabel(" ");
    private final RoundedButton submitButton = new RoundedButton("Submit answer", ButtonStyle.PRIMARY);
    private final RoundedButton nextButton = new RoundedButton("Next question", ButtonStyle.PRIMARY);
    private final RoundedButton topicsButton = new RoundedButton("Choose another topic", ButtonStyle.SECONDARY);
    private final Runnable onTopics;
    private RoundedTextField textField;
    private final MascotView quizMascot = new MascotView();
    private JavaCodeEditor codeArea;
    private ButtonGroup choiceGroup;
    private List<JRadioButton> choiceButtons = List.of();
    private List<String> choiceValues = List.of();
    private final Consumer<PowerUp> onPowerUp; // owner-supplied; the view only proposes
    private final Map<PowerUp, RoundedButton> powerUpButtons =
            new EnumMap<>(PowerUp.class);
    private final JPanel powerUpRow = createPowerUpRow();
    private final JLabel enemyNameLabel = createLabel(" ");
    private final EnemyPortraitView enemyPortrait = new EnemyPortraitView();
    private String currentEnemyName = "";

    public QuestionScreen(
            Consumer<String> onSubmit,
            Runnable onNext,
            Runnable onTopics,
            Consumer<PowerUp> onPowerUp) {
        putClientProperty(ButtonSounds.SILENT_ZONE, Boolean.TRUE);
        this.onPowerUp = onPowerUp;
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

    // One fixed button per power-up, labeled by the enum. The whole row stays
    // invisible until the controller shows it, so today's layout is untouched
    // while nothing is unlocked. Clicks only propose a use; services decide.
    private JPanel createPowerUpRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP_S, 0));
        row.setOpaque(false);
        for (PowerUp powerUp : PowerUp.values()) {
            RoundedButton button =
                    new RoundedButton(powerUp.getLabel(), ButtonStyle.SECONDARY);
            button.addActionListener(event -> onPowerUp.accept(powerUp));
            powerUpButtons.put(powerUp, button);
            row.add(button);
        }
        row.setVisible(false);
        return row;
    }

    private JPanel createBattleArea() {
        JPanel battle = new JPanel(new BorderLayout(Theme.GAP_L, 0));
        battle.setOpaque(false);
        JPanel playerSide = new JPanel(new BorderLayout(0, Theme.GAP_S));
        playerSide.setOpaque(false);

        
        RoundedPanel playerPortrait = new RoundedPanel(
                new BorderLayout(),
                "panels/panel_inset",
                13
        );

        playerPortrait.setPreferredSize(
                new Dimension(
                        Theme.CHARACTER_SIZE,
                        Theme.CHARACTER_SIZE
                )
        );

        quizMascot.setMood(MascotView.Mood.ONLINE);

        playerPortrait.add(quizMascot, BorderLayout.CENTER);


        JPanel playerVitals = new JPanel(new BorderLayout(Theme.GAP_S, 0));
        playerVitals.setOpaque(false);
        hpLabel.setPreferredSize(new Dimension(textWidth("HP: 99/99"), hpLabel.getPreferredSize().height));
        playerVitals.add(hpLabel, BorderLayout.WEST);
        playerVitals.add(xpLabel, BorderLayout.CENTER);

        JPanel playerStats = new JPanel(new GridLayout(2, 1));
        playerStats.setOpaque(false);
        playerStats.add(playerNameLabel);
        playerStats.add(playerVitals);
        playerStats.setPreferredSize(new Dimension(Theme.PLAYER_COLUMN_WIDTH, playerStats.getPreferredSize().height));
        playerSide.add(playerStats, BorderLayout.NORTH);
        playerSide.add(playerPortrait, BorderLayout.CENTER);

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

        countdownLabel.setForeground(Theme.TEXT_MUTED);
        JPanel leftStatus = new JPanel(new GridLayout(2, 1, 0, Theme.GAP_S));
        leftStatus.setOpaque(false);
        leftStatus.add(progressLabel);
        leftStatus.add(countdownLabel);

        scoreLabel.setForeground(Theme.TEXT_MUTED);
        joltLabel.setForeground(Theme.TEXT_MUTED);
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        joltLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JPanel rightStatus = new JPanel(new GridLayout(2, 1, 0, Theme.GAP_S));
        rightStatus.setOpaque(false);
        rightStatus.add(scoreLabel);
        rightStatus.add(joltLabel);
        int rightStatusWidth = Math.max(textWidth("QUIZ SCORE: 99999"),
                textWidth("JOLT: x99   (+999 points)")) + Theme.GAP_S;
        rightStatus.setPreferredSize(new Dimension(rightStatusWidth, rightStatus.getPreferredSize().height));

        JPanel statusRow = new JPanel(new BorderLayout(Theme.GAP_S, 0));
        statusRow.setOpaque(false);
        statusRow.add(leftStatus, BorderLayout.WEST);
        statusRow.add(rightStatus, BorderLayout.EAST);
        joltMessageLabel.setFont(Theme.FONT_JOLT_MESSAGE);
        joltMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        joltMessageLabel.setVerticalAlignment(SwingConstants.CENTER);
        // NOTE: Messages use the existing free space without contributing to header size.
        joltMessageLabel.setPreferredSize(new Dimension(0, 0));
        statusRow.add(joltMessageLabel, BorderLayout.CENTER);
        questionHeader.add(statusRow, BorderLayout.NORTH);
        questionHeader.add(
                questionProgress,
                BorderLayout.SOUTH
        );
        // NOTE: BorderLayout reserves two vertical gaps even with an empty center.
        questionHeader.setPreferredSize(new Dimension(questionHeader.getPreferredSize().width,
                statusRow.getPreferredSize().height + Theme.GAP_S + questionProgress.getPreferredSize().height));

        question.add(
                questionHeader,
                BorderLayout.NORTH
        );

        question.add(promptScroll, BorderLayout.CENTER);

        enemyPortrait.setPreferredSize(new Dimension(Theme.CHARACTER_SIZE, Theme.CHARACTER_SIZE));
        JPanel enemySide = new JPanel(new BorderLayout());
        enemySide.setOpaque(false);
        enemySide.add(enemyPortrait, BorderLayout.CENTER);

        // Enemy name lives under the enemy-side placeholder; invisible until the
        // controller names it, so absent states keep the existing layout height.
        enemyNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        enemyNameLabel.setForeground(Theme.TEXT_MUTED);
        enemyNameLabel.setFont(Theme.FONT_BODY);
        enemyNameLabel.setVisible(false);
        enemySide.add(enemyNameLabel, BorderLayout.SOUTH);

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
        battle.add(powerUpRow, BorderLayout.SOUTH);
        battle.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.GAP_M, 0));
        return battle;
    }

    private int textWidth(String text) {
        return getFontMetrics(Theme.FONT_BODY).stringWidth(text);
    }

    // One line of immediate feedback: the live consecutive-correct streak plus,
    // right after a resolved answer, the points that answer just earned.
    private void refreshJoltLabel(int streak, int lastPoints) {
        if (lastPoints > 0) {
            joltLabel.setText(
                    "JOLT: x" + streak
                    + "   (+" + lastPoints + " points)"
            );
        } else {
            joltLabel.setText("JOLT: x" + streak);
        }
    }

    public void showJoltMessage(JoltMessage message) {
        joltMessageLabel.setForeground(switch (message) {
            case SPARK -> Theme.JOLT_SPARK;
            case JOLT -> Theme.JOLT_JOLT;
            case SURGE -> Theme.JOLT_SURGE;
            case SHORT_CIRCUIT -> Theme.DANGER;
            default -> Theme.TEXT;
        });
        String text = message.getText();
        if (message == JoltMessage.JAVAJOLT) {
            text = String.format("<html><font color='#%06X'>%s</font><font color='#%06X'>%s</font></html>",
                    Theme.TEXT.getRGB() & 0xFFFFFF, text.substring(0, "JAVA".length()),
                    Theme.ACCENT.getRGB() & 0xFFFFFF, text.substring("JAVA".length()));
        }
        joltMessageLabel.setText(text);
    }

    // Battle-side meta display: names the enemy and refreshes the power-up row
    // from view-only state. The controller folds "used" and "not applicable"
    // into one disabled set; armed power-ups read "ready" and stay disabled.
    // The row shows only while an unanswered question is active.
    public void showBattleMeta(
            Topic enemyTopic,
            String enemyName,
            Set<PowerUp> unlocked,
            Set<PowerUp> disabled,
            Set<PowerUp> armed,
            boolean questionActive) {

        enemyPortrait.setTopic(enemyTopic);
        currentEnemyName = enemyName == null ? "" : enemyName;
        enemyNameLabel.setText(currentEnemyName.isBlank() ? " " : currentEnemyName);
        enemyNameLabel.setVisible(!currentEnemyName.isBlank());

        boolean showRow = questionActive && !unlocked.isEmpty();
        powerUpRow.setVisible(showRow);
        if (showRow) {
            for (PowerUp powerUp : PowerUp.values()) {
                RoundedButton button = powerUpButtons.get(powerUp);
                boolean isArmed = armed.contains(powerUp);

                if (isArmed && powerUp == PowerUp.SHIELD) {
                    button.setText("Shield ready");
                } else {
                    button.setText(powerUp.getLabel());
                }

                button.setVisible(unlocked.contains(powerUp));
                button.setEnabled(
                        unlocked.contains(powerUp)
                                && !isArmed
                                && !disabled.contains(powerUp)
                );
            }
        }

        enemyNameLabel.revalidate();
        enemyNameLabel.repaint();
        powerUpRow.revalidate();
        powerUpRow.repaint();
    }

    // Hides the choices a Hint removed, matched against the raw choice values
    // (the buttons display HTML-wrapped text, so matching goes through
    // choiceValues). Answer letters stay fixed; nothing is renumbered. The
    // buttons are rebuilt for every question, so no state carries over.
    public void applyHintRemovedChoices(List<String> removedChoices) {
        if (removedChoices == null || removedChoices.isEmpty()) {
            return;
        }

        for (String removed : removedChoices) {
            for (int i = 0; i < choiceValues.size(); i++) {
                if (choiceValues.get(i).equals(removed)) {
                    JRadioButton button = choiceButtons.get(i);
                    if (button.isSelected() && choiceGroup != null) {
                        choiceGroup.clearSelection();
                    }
                    button.setVisible(false);
                    button.setEnabled(false);
                }
            }
        }

        answerContent.revalidate();
        answerContent.repaint();
    }

    // Writes a power-up result message into the existing feedback line.
    public void showCountdown(int seconds, boolean frozen) {
        countdownLabel.setText("TIME: " + seconds + " s" + (frozen ? " (frozen)" : ""));
    }

    // Refresh only the notice and HP so the player's input stays intact.
    public void showTimeout(Player player) {
        showHp(player);
        countdownLabel.setText("TIME: expired");
        showPowerUpNotice("Time expired. You can still answer this question.");
    }

    // Refresh HP after an instant recovery without rebuilding the answer editor.
    public void showHp(Player player) {
        hpLabel.setText("HP: " + player.getHp() + "/" + player.getMaxHp());
    }

    public void showPowerUpNotice(String message) {
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLabel.setText(message == null || message.isBlank() ? " " : message);
    }

    public void showQuestion(
            Question question,
            int number,
            int total,
            Player player,
            int displayXp,
            int quizScore,
            int streak,
            int lastPoints) {
        quizMascot.setMood(MascotView.Mood.THINKING);
        playerNameLabel.setText(player.getName());
        hpLabel.setText("HP: " + player.getHp() + "/" + player.getMaxHp());
        // displayXp is provisional quiz XP; it commits to the player only at settlement.
        xpLabel.setText("XP: " + displayXp);
        scoreLabel.setText("QUIZ SCORE: " + quizScore);
        refreshJoltLabel(streak, lastPoints);
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

            // Selection uses the existing click sound once; global button
            // sounds do not apply to these JRadioButton answer cards.
            button.addActionListener(event -> {
                UiSounds.play(SoundEffect.BUTTON_CLICK);
                updateChoiceBorders(buttons);
            });

            // Keep hover state independent from the selected state.
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    button.putClientProperty("choiceHovered", Boolean.TRUE);
                    updateChoiceBorders(buttons);
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    button.putClientProperty("choiceHovered", Boolean.FALSE);
                    updateChoiceBorders(buttons);
                }
            });

            // Swing focus decoration is off; draw a visible focus border.
            button.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent event) {
                    updateChoiceBorders(buttons);
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent event) {
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



    private void updateChoiceBorders(List<JRadioButton> buttons) {
        for (JRadioButton button : buttons) {
            boolean selected = button.isSelected();
            boolean hovered = Boolean.TRUE.equals(
                    button.getClientProperty("choiceHovered")
            );
            boolean focused = button.isFocusOwner();

            // A selection remains orange even when the pointer moves away.
            button.setBackground(
                    selected ? new java.awt.Color(0x403128)
                            : hovered || focused ? new java.awt.Color(0x333840)
                            : Theme.INSET
            );

            java.awt.Color borderColor = selected || focused
                    ? Theme.ACCENT
                    : hovered ? new java.awt.Color(0x727C8A)
                            : Theme.INSET_BORDER;

            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 2, true),
                    BorderFactory.createEmptyBorder(14, 18, 14, 18)
            ));
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
            Player player,
            int displayXp,
            int quizScore,
            int streak,
            int lastPoints) {
        quizMascot.setMood(
                correct
                        ? MascotView.Mood.CHARGED
                        : MascotView.Mood.LOW_BATTERY
        );


        quizMascot.flashGlow(correct);

        if (correct) {
        // Successful attack: enemy flashes red.
        enemyPortrait.flashHit();
        } else {
        // Incorrect answer: mascot shakes from damage.
        quizMascot.shakeDamage();
        }


        // Play the appropriate sound after checking an answer.
        UiSounds.play(
                correct
                        ? SoundEffect.ANSWER_CORRECT
                        : SoundEffect.ANSWER_INCORRECT
        );

        // Refresh displayed player statistics
        hpLabel.setText("HP: " + player.getHp() + "/" + player.getMaxHp());

        // displayXp is provisional quiz XP; it commits to the player only at settlement.
        xpLabel.setText("XP: " + displayXp);
        scoreLabel.setText("QUIZ SCORE: " + quizScore);
        refreshJoltLabel(streak, lastPoints);

        // Change the section heading
        answerHeading.setText("ANSWER FEEDBACK");

        answerContent.removeAll();
        actionRow.removeAll();

        answerHeading.setText("ANSWER");
        submitButton.setEnabled(true);

        // Display the new feedback card
        answerContent.add(
                createFeedbackCard(correct, explanation, player, lastPoints),
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
                Player player,
                int lastPoints) {

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


        // FEEDBACK TITLE AND POINTS

        JPanel headline = new JPanel(
                new BorderLayout(Theme.GAP_S, 0)
        );

        headline.setOpaque(false);

        JLabel status = createHeading(
                correct
                        ? "CORRECT ANSWER!"
                        : "INCORRECT ANSWER"
        );

        status.setForeground(
                correct ? Theme.ACCENT : Theme.DANGER
        );

        JLabel outcome = createLabel(
                correct
                        ? (lastPoints > 0
                                ? "+" + lastPoints + " POINTS"
                                : "ANSWER ACCEPTED")
                        : (player.isAlive()
                                ? "KEEP TRYING"
                                : "HP DEPLETED")
        );

        outcome.setFont(
                Theme.FONT_BODY.deriveFont(
                        java.awt.Font.BOLD, 13f
                )
        );

        outcome.setForeground(
                correct ? Theme.ACCENT : Theme.DANGER
        );

        outcome.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        headline.add(status, BorderLayout.CENTER);
        headline.add(outcome, BorderLayout.EAST);

        card.add(headline, BorderLayout.NORTH);

        // EXPLANATION
        String message = correct
                ? "Nice work! Here's why the answer is correct:\n\n"
                : "Here's why that answer wasn't correct:\n\n";

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
                        : (currentEnemyName.isBlank()
                                ? "Out of HP - continue to see your results."
                                : "Defeated by " + currentEnemyName
                                        + "! Continue to see your results.")
        );

        footer.setForeground(
                player.isAlive()
                        ? Theme.TEXT_MUTED
                        : Theme.DANGER
        );

        card.add(footer, BorderLayout.SOUTH);

        return card;
        }

    public void setCheckingAnswer(boolean checking) {
        submitButton.setEnabled(!checking);
    }

    public void setTopicsButtonEnabled(boolean enabled) {
        topicsButton.setEnabled(enabled);
    }

    public void setContinueButtonEnabled(boolean enabled) {
        nextButton.setEnabled(enabled);
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
