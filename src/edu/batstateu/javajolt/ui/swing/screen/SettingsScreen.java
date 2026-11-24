
package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.AppSettings;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.component.ButtonStyle;
import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

/**
 * Settings interface for JavaJolt.
 * Preferences are saved using AppSettings.
 */
public final class SettingsScreen extends Screen {

    private final JComboBox<AppSettings.ThemeMode> themeChoice =
            new JComboBox<>(AppSettings.ThemeMode.values());

    private final JSlider volumeSlider = new JSlider(0, 100);
    private final JLabel volumeLabel = new JLabel();

    private final JCheckBox soundCheck =
            option("Enable all sound effects");

    private final JCheckBox buttonCheck =
            option("Button hover and click sounds");

    private final JCheckBox answerCheck =
            option("Correct / incorrect answer sounds");

    private final JCheckBox timerCheck =
            option("Countdown warning sounds");

    private final JCheckBox motionCheck =
            option("Reduce character animations");

    private final JLabel savedLabel = label(
            "Settings are saved automatically.",
            12, Theme.TEXT_MUTED
    );

    private boolean loading;

    public SettingsScreen(Runnable onBack) {

        // HEADER
        JPanel header = new JPanel(
                new BorderLayout(Theme.GAP_M, 0)
        );
        header.setOpaque(false);

        header.add(
                label("SETTINGS", 28, Theme.ACCENT),
                BorderLayout.WEST
        );

        RoundedButton back = new RoundedButton(
                "BACK TO DASHBOARD",
                ButtonStyle.SECONDARY
        );

        back.addActionListener(e -> onBack.run());
        header.add(back, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // SETTINGS CARDS
        JPanel cards = new JPanel();
        cards.setOpaque(false);
        cards.setLayout(
                new BoxLayout(cards, BoxLayout.Y_AXIS)
        );

        cards.add(appearanceCard());
        cards.add(Box.createVerticalStrut(Theme.GAP_M));

        cards.add(audioCard());
        cards.add(Box.createVerticalStrut(Theme.GAP_M));

        cards.add(accessibilityCard());
        cards.add(Box.createVerticalStrut(Theme.GAP_M));

        // RESET BUTTON
        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.LEFT, Theme.GAP_M, 0)
        );
        footer.setOpaque(false);

        RoundedButton reset = new RoundedButton(
                "RESET DEFAULTS",
                ButtonStyle.DANGER
        );

        reset.addActionListener(e -> {
            AppSettings.resetDefaults();
            refreshFromPreferences();
            savedLabel.setText("Default settings restored.");
        });

        footer.add(reset);
        footer.add(savedLabel);
        cards.add(footer);

        // SCROLLABLE AREA
        JScrollPane scroll = new JScrollPane(cards);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(scroll, BorderLayout.CENTER);

        refreshFromPreferences();
        attachListeners();
    }

    private JPanel appearanceCard() {

        JPanel body = new JPanel(
                new FlowLayout(FlowLayout.LEFT, Theme.GAP_M, 0)
        );
        body.setOpaque(false);

        body.add(label(
                "Application theme:", 16, Theme.TEXT
        ));

        themeChoice.setFont(Theme.FONT_BODY);
        themeChoice.setBackground(Theme.INSET);
        themeChoice.setForeground(Theme.TEXT);
        themeChoice.setPreferredSize(
                new Dimension(180, 38)
        );

        body.add(themeChoice);

        return card("APPEARANCE", body);
    }

    private JPanel audioCard() {

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(
                new BoxLayout(body, BoxLayout.Y_AXIS)
        );

        JPanel sliderRow = new JPanel(
                new BorderLayout(Theme.GAP_M, 0)
        );
        sliderRow.setOpaque(false);

        sliderRow.add(
                label("Master volume", 16, Theme.TEXT),
                BorderLayout.WEST
        );

        volumeLabel.setForeground(Theme.ACCENT);
        volumeLabel.setFont(
                Theme.FONT_BODY.deriveFont(Font.BOLD)
        );

        sliderRow.add(volumeLabel, BorderLayout.EAST);

        body.add(sliderRow);

        volumeSlider.setOpaque(false);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);

        body.add(volumeSlider);
        body.add(Box.createVerticalStrut(Theme.GAP_S));

        body.add(soundCheck);
        body.add(buttonCheck);
        body.add(answerCheck);
        body.add(timerCheck);

        return card("AUDIO", body);
    }

    private JPanel accessibilityCard() {

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);

        body.add(motionCheck, BorderLayout.NORTH);

        body.add(
                label(
                        "Reduces character movement after integration.",
                        12, Theme.TEXT_MUTED
                ),
                BorderLayout.SOUTH
        );

        return card("ACCESSIBILITY", body);
    }

    private static JPanel card(
            String heading, JPanel body) {

        RoundedPanel card = new RoundedPanel(
                new BorderLayout(0, Theme.GAP_M)
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        Theme.GAP_M,
                        Theme.GAP_L,
                        Theme.GAP_M,
                        Theme.GAP_L
                )
        );

        card.add(
                label(heading, 14, Theme.ACCENT),
                BorderLayout.NORTH
        );

        card.add(body, BorderLayout.CENTER);

        card.setAlignmentX(LEFT_ALIGNMENT);

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        card.getPreferredSize().height
                )
        );

        return card;
    }

    private static JLabel label(
            String text,
            int size,
            java.awt.Color color) {

        JLabel result = new JLabel(text);

        result.setFont(
                Theme.FONT_BODY.deriveFont(
                        Font.BOLD, (float) size
                )
        );

        result.setForeground(color);
        return result;
    }

    private static JCheckBox option(String text) {

        JCheckBox result = new JCheckBox(text);

        result.setOpaque(false);
        result.setForeground(Theme.TEXT);
        result.setFont(Theme.FONT_BODY);
        result.setFocusPainted(false);

        return result;
    }

    // LOAD SAVED PREFERENCES
    public void refreshFromPreferences() {

        loading = true;

        try {
            themeChoice.setSelectedItem(
                    AppSettings.getThemeMode()
            );

            volumeSlider.setValue(
                    AppSettings.getMasterVolume()
            );

            volumeLabel.setText(
                    volumeSlider.getValue() + "%"
            );

            soundCheck.setSelected(
                    AppSettings.isSoundEnabled()
            );

            buttonCheck.setSelected(
                    AppSettings.isButtonSoundEnabled()
            );

            answerCheck.setSelected(
                    AppSettings.isAnswerSoundEnabled()
            );

            timerCheck.setSelected(
                    AppSettings.isCountdownSoundEnabled()
            );

            motionCheck.setSelected(
                    AppSettings.isReducedMotionEnabled()
            );

        } finally {
            loading = false;
        }
    }

    // SAVE CHANGES AUTOMATICALLY
    private void attachListeners() {

        themeChoice.addActionListener(e -> {
            if (!loading) {
                AppSettings.setThemeMode(
                        (AppSettings.ThemeMode)
                                themeChoice.getSelectedItem()
                );

                savedLabel.setText(
                        "Theme preference saved."
                );
            }
        });

        volumeSlider.addChangeListener(e -> {

            volumeLabel.setText(
                    volumeSlider.getValue() + "%"
            );

            if (!loading &&
                    !volumeSlider.getValueIsAdjusting()) {

                AppSettings.setMasterVolume(
                        volumeSlider.getValue()
                );
            }
        });

        soundCheck.addActionListener(e ->
                AppSettings.setSoundEnabled(
                        soundCheck.isSelected()
                )
        );

        buttonCheck.addActionListener(e ->
                AppSettings.setButtonSoundEnabled(
                        buttonCheck.isSelected()
                )
        );

        answerCheck.addActionListener(e ->
                AppSettings.setAnswerSoundEnabled(
                        answerCheck.isSelected()
                )
        );

        timerCheck.addActionListener(e ->
                AppSettings.setCountdownSoundEnabled(
                        timerCheck.isSelected()
                )
        );

        motionCheck.addActionListener(e ->
                AppSettings.setReducedMotionEnabled(
                        motionCheck.isSelected()
                )
        );
    }
}
