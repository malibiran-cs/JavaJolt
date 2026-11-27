package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.*;
import edu.batstateu.javajolt.ui.swing.component.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.function.Consumer;
import javax.swing.*;

/** JavaJolt control center. UI state is synchronized with the existing preference store. */
public final class SettingsScreen extends Screen {
    private final ThemeOptionCard dark = new ThemeOptionCard(AppSettings.ThemeMode.DARK);
    private final ThemeOptionCard light = new ThemeOptionCard(AppSettings.ThemeMode.LIGHT);
    private final VolumeSlider volume = new VolumeSlider();
    private final JLabel volumeLabel = label("100%", 18, true, Theme.ACCENT);
    private final ToggleSwitch sound = new ToggleSwitch("All sound effects");
    private final ToggleSwitch buttons = new ToggleSwitch("Button sounds");
    private final ToggleSwitch answers = new ToggleSwitch("Answer feedback");
    private final ToggleSwitch countdown = new ToggleSwitch("Countdown sounds");
    private final ToggleSwitch motion = new ToggleSwitch("Reduced motion");
    private final JLabel status = label("Preferences saved automatically",12,true,Theme.TEXT_MUTED);
    private final JLabel themeSummary = label("",13,true,Theme.TEXT);
    private final JLabel audioSummary = label("",13,true,Theme.TEXT);
    private final JLabel motionSummary = label("",13,true,Theme.TEXT);
    private final JTextArea dialogue = new JTextArea("Your adventure, your rules! Make JavaJolt feel right for you.");
    private final MascotView mascot = new MascotView();
    private final Timer idle = new Timer(1700,e->mascot.showIdle());
    private final RoundedButton preview = new RoundedButton("TEST SOUND",ButtonStyle.SECONDARY);
    private final JScrollPane scroll;
    private boolean loading;

    public SettingsScreen(Runnable onBack) {
        setBorder(BorderFactory.createEmptyBorder(20,28,20,28));
        setLayout(new BorderLayout(0,14));
        JPanel header = transparent(new BorderLayout(16,0));
        JPanel headings = column();
        headings.add(label("SETTINGS  /  CONTROL CENTER",28,true,Theme.ACCENT));
        headings.add(Box.createVerticalStrut(4));
        headings.add(label("Make your learning adventure your own.",14,false,Theme.TEXT_MUTED));
        header.add(headings,BorderLayout.CENTER);
        RoundedButton back = new RoundedButton("BACK TO DASHBOARD",ButtonStyle.SECONDARY);
        back.addActionListener(e->onBack.run());header.add(back,BorderLayout.EAST);
        add(header,BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(16,0));content.setOpaque(false);
        JPanel sidebar = column();
        JPanel main = column();
        JPanel appearance = appearanceCard(), audio = audioCard(), accessibility = accessibilityCard();
        main.add(appearance);main.add(Box.createVerticalStrut(12));main.add(audio);main.add(Box.createVerticalStrut(12));main.add(accessibility);
        JPanel footer=transparent(new BorderLayout(10,0));
        footer.add(status,BorderLayout.CENTER);
        RoundedButton reset=new RoundedButton("RESET DEFAULTS",ButtonStyle.DANGER);
        reset.addActionListener(e->{
            if(JOptionPane.showConfirmDialog(this,"Restore the original appearance, audio and motion settings?\nYour player progress and achievements will not change.","Reset preferences",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION){
                restoreDefaults();
            }
        });
        footer.add(reset,BorderLayout.EAST);add(footer,BorderLayout.SOUTH);
        sidebar.add(navigation(appearance,audio,accessibility));sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(mascotCard());sidebar.add(Box.createVerticalStrut(12));sidebar.add(summaryCard());
        JPanel left = transparent(new BorderLayout());left.add(sidebar,BorderLayout.NORTH);
        left.setPreferredSize(new Dimension(280,sidebar.getPreferredSize().height));
        JPanel right = transparent(new BorderLayout());right.add(main,BorderLayout.NORTH);
        content.add(left,BorderLayout.WEST);content.add(right,BorderLayout.CENTER);
        // Track the viewport width to avoid horizontal scrolling at the minimum window size.
        JPanel viewportContent = new ScrollContent(content);
        scroll=new JScrollPane(viewportContent,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());scroll.setOpaque(false);scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
            @Override protected void paintTrack(Graphics g,JComponent c,Rectangle b){g.setColor(Theme.BACKGROUND);g.fillRect(b.x,b.y,b.width,b.height);}
            @Override protected void paintThumb(Graphics g,JComponent c,Rectangle b){g.setColor(Theme.INSET_BORDER);g.fillRoundRect(b.x+2,b.y+2,Math.max(0,b.width-4),Math.max(0,b.height-4),8,8);}
            @Override protected JButton createDecreaseButton(int orientation){return emptyArrow();}
            @Override protected JButton createIncreaseButton(int orientation){return emptyArrow();}
            private JButton emptyArrow(){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10,0));add(scroll,BorderLayout.CENTER);
        idle.setRepeats(false);
        addHierarchyListener(e->{if((e.getChangeFlags()&HierarchyEvent.SHOWING_CHANGED)!=0&&!isShowing()){idle.stop();mascot.showIdle();}});
        refreshFromPreferences();attachListeners();
    }

    private JPanel navigation(JPanel... sections){
        JPanel body=column();String[] names={"01   Appearance","02   Audio","03   Accessibility"};
        for(int i=0;i<names.length;i++){
            JPanel target=sections[i];RoundedButton b=new RoundedButton(names[i],ButtonStyle.SECONDARY);
            b.setHorizontalAlignment(SwingConstants.LEFT);b.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD,14f));
            b.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
            b.addActionListener(e->{Rectangle rect=SwingUtilities.convertRectangle(target.getParent(),target.getBounds(),scroll.getViewport().getView());scroll.getViewport().setViewPosition(new Point(0,Math.min(rect.y,Math.max(0,scroll.getViewport().getViewSize().height-scroll.getViewport().getExtentSize().height))));});
            body.add(b);if(i<names.length-1)body.add(Box.createVerticalStrut(5));
        }return card("PREFERENCES",null,body);
    }
    private JPanel mascotCard(){
        JPanel body=column();mascot.setPreferredSize(new Dimension(180,140));mascot.setMaximumSize(new Dimension(Integer.MAX_VALUE,140));mascot.setAlignmentX(CENTER_ALIGNMENT);body.add(mascot);
        dialogue.setFont(Theme.FONT_BODY.deriveFont(13f));dialogue.setForeground(Theme.TEXT);dialogue.setOpaque(false);dialogue.setEditable(false);dialogue.setFocusable(false);dialogue.setLineWrap(true);dialogue.setWrapStyleWord(true);dialogue.setRows(2);dialogue.setColumns(17);
        dialogue.setBorder(BorderFactory.createEmptyBorder(9,10,8,10));
        RoundedPanel bubble=new RoundedPanel(new BorderLayout(),"panels/panel_inset",16);bubble.add(dialogue);body.add(bubble);
        return card("JOLT'S CORNER","Your learning companion",body);
    }
    private JPanel summaryCard(){JPanel body=column();body.add(themeSummary);body.add(Box.createVerticalStrut(6));body.add(audioSummary);body.add(Box.createVerticalStrut(6));body.add(motionSummary);return card("ACTIVE PROFILE",null,body);}
    private JPanel appearanceCard(){
        JPanel body=column();ButtonGroup group=new ButtonGroup();group.add(dark);group.add(light);
        JPanel options=transparent(new GridLayout(1,2,10,0));options.add(dark);options.add(light);body.add(options);
        return card("01  APPEARANCE","Choose the visual style of your adventure.",body);
    }
    private JPanel audioCard(){
        JPanel body=column();JPanel top=transparent(new BorderLayout(8,0));top.add(label("Master volume",15,true,Theme.TEXT),BorderLayout.WEST);top.add(volumeLabel,BorderLayout.EAST);body.add(top);
        JPanel sliderRow=transparent(new BorderLayout(10,0));sliderRow.add(volume,BorderLayout.CENTER);preview.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD,12f));preview.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));sliderRow.add(preview,BorderLayout.EAST);body.add(sliderRow);body.add(Box.createVerticalStrut(6));
        body.add(optionRow("All sound effects","Master switch for game audio",sound));
        body.add(optionRow("Button sounds","Hover and click feedback",buttons));
        body.add(optionRow("Answer feedback","Correct and incorrect answer cues",answers));
        body.add(optionRow("Countdown sounds","Quiz timer warnings",countdown));
        return card("02  AUDIO","Control the soundscape of JavaJolt.",body);
    }
    private JPanel accessibilityCard(){JPanel body=column();body.add(optionRow("Reduced motion","Minimize character floating, shaking and flashes",motion));return card("03  ACCESSIBILITY","Play comfortably, at your own pace.",body);}
    private static JPanel optionRow(String title,String description,ToggleSwitch toggle){
        JPanel row=transparent(new BorderLayout(8,0));row.setBorder(BorderFactory.createEmptyBorder(2,0,2,0));
        JPanel text=column();text.add(label(title,14,true,Theme.TEXT));text.add(label(description,12,false,Theme.TEXT_MUTED));row.add(text,BorderLayout.CENTER);row.add(toggle,BorderLayout.EAST);return row;
    }
    private static JPanel card(String title,String subtitle,JPanel body){
        RoundedPanel card=new RoundedPanel(new BorderLayout(0,8));card.setBorder(BorderFactory.createEmptyBorder(11,18,11,18));
        JPanel head=column();head.add(label(title,15,true,Theme.ACCENT));if(subtitle!=null){head.add(Box.createVerticalStrut(3));head.add(label(subtitle,12,false,Theme.TEXT_MUTED));}
        card.add(head,BorderLayout.NORTH);card.add(body,BorderLayout.CENTER);card.setAlignmentX(LEFT_ALIGNMENT);return card;
    }
    private static JPanel transparent(LayoutManager layout){JPanel p=new JPanel(layout);p.setOpaque(false);return p;}
    private static JPanel column(){JPanel p=new JPanel();p.setOpaque(false);p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));return p;}
    private static JLabel label(String text,int size,boolean bold,Color color){JLabel l=color.equals(Theme.ACCENT) ? new JLabel(text){@Override public Color getForeground(){return Theme.isLightMode()?new Color(0xA34217):Theme.ACCENT;}} : new JLabel(text);l.setFont(Theme.FONT_BODY.deriveFont(bold?Font.BOLD:Font.PLAIN,(float)size));l.setForeground(color);l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private void attachListeners(){
        dark.addActionListener(e->changeTheme(dark.getMode()));light.addActionListener(e->changeTheme(light.getMode()));
        volume.addChangeListener(e->{volumeLabel.setText(volume.getValue()+"%");if(!loading&&!volume.getValueIsAdjusting()){AppSettings.setMasterVolume(volume.getValue());changed("Volume saved.",volume.getValue()==0?"Going quiet? Time to focus!":"Find the perfect volume for your next mission.");}});
        bind(sound,AppSettings::setSoundEnabled,"Sound effects saved.","Your soundscape is yours to control.");
        bind(buttons,AppSettings::setButtonSoundEnabled,"Button sounds saved.","Every click, just the way you like it.");
        bind(answers,AppSettings::setAnswerSoundEnabled,"Answer sounds saved.","Ready for your next quiz!");
        bind(countdown,AppSettings::setCountdownSoundEnabled,"Countdown sounds saved.","Focus on the challenge ahead.");
        bind(motion,AppSettings::setReducedMotionEnabled,"Motion preference saved.","Comfort comes first. Let's learn at your pace!");
        preview.addActionListener(e->{UiSounds.play(SoundEffect.JOLT_SPARK);say("That's a little spark of JavaJolt!");});
    }
    private void bind(ToggleSwitch toggle,Consumer<Boolean> setter,String saved,String message){toggle.addActionListener(e->{if(!loading){setter.accept(toggle.isSelected());changed(saved,message);}});}
    private void changeTheme(AppSettings.ThemeMode mode){if(!loading){AppSettings.setThemeMode(mode);changed("Theme preference saved.",mode==AppSettings.ThemeMode.LIGHT?"A brighter look for a brighter mind!":"Back to the classic JavaJolt look!");}}
    private void changed(String saved,String message){updateSummary();status.setText(saved);say(message);repaint();}
    private void say(String message){dialogue.setText(message);idle.stop();if(AppSettings.isReducedMotionEnabled()){mascot.showIdle();}else{mascot.showCorrect();idle.restart();}}
    private void updateSummary(){
        themeSummary.setText("Theme  /  "+(dark.isSelected()?"Dark":"Light"));audioSummary.setText("Audio  /  "+(!sound.isSelected()||volume.getValue()==0?"Muted":volume.getValue()+"%"));motionSummary.setText("Motion  /  "+(motion.isSelected()?"Reduced":"Full"));
        boolean enabled=sound.isSelected();buttons.setEnabled(enabled);answers.setEnabled(enabled);countdown.setEnabled(enabled);preview.setEnabled(enabled&&volume.getValue()>0);
    }
    public void restoreDefaults(){AppSettings.resetDefaults();refreshFromPreferences();changed("Default preferences restored.","Back to the classics! Your progress is safe.");}
    public void refreshFromPreferences(){loading=true;try{dark.setSelected(AppSettings.getThemeMode()==AppSettings.ThemeMode.DARK);light.setSelected(!dark.isSelected());volume.setValue(AppSettings.getMasterVolume());volumeLabel.setText(volume.getValue()+"%");sound.setSelected(AppSettings.isSoundEnabled());buttons.setSelected(AppSettings.isButtonSoundEnabled());answers.setSelected(AppSettings.isAnswerSoundEnabled());countdown.setSelected(AppSettings.isCountdownSoundEnabled());motion.setSelected(AppSettings.isReducedMotionEnabled());updateSummary();}finally{loading=false;}}
    private static final class ScrollContent extends JPanel implements Scrollable{
        ScrollContent(JPanel content){super(new BorderLayout());setOpaque(false);add(content,BorderLayout.NORTH);}
        public Dimension getPreferredScrollableViewportSize(){return getPreferredSize();}
        public int getScrollableUnitIncrement(Rectangle visible,int orientation,int direction){return 20;}
        public int getScrollableBlockIncrement(Rectangle visible,int orientation,int direction){return Math.max(20,visible.height-40);}
        public boolean getScrollableTracksViewportWidth(){return true;}
        public boolean getScrollableTracksViewportHeight(){return false;}
    }
}
