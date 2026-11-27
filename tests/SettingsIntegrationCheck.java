import edu.batstateu.javajolt.ui.swing.*;
import edu.batstateu.javajolt.ui.swing.screen.SettingsScreen;
import edu.batstateu.javajolt.ui.swing.component.*;
import java.awt.*;
import javax.swing.*;

/** Settings controls and preference synchronization. Restores original preferences. */
public class SettingsIntegrationCheck {
    public static void main(String[] args) throws Exception {
        var oldTheme=AppSettings.getThemeMode(); int oldVolume=AppSettings.getMasterVolume();
        boolean oldSound=AppSettings.isSoundEnabled(), oldButtons=AppSettings.isButtonSoundEnabled(), oldAnswers=AppSettings.isAnswerSoundEnabled(), oldCountdown=AppSettings.isCountdownSoundEnabled(), oldMotion=AppSettings.isReducedMotionEnabled();
        try {
            SwingUtilities.invokeAndWait(()->{
                AppSettings.resetDefaults();
                SettingsScreen s=new SettingsScreen(()->{});
                var dark=find(s,ThemeOptionCard.class,"Dark mode");var light=find(s,ThemeOptionCard.class,"Light mode");
                check(dark.isSelected()&&!light.isSelected(),"Default theme selection");
                light.doClick();check(Theme.isLightMode()&&AppSettings.getThemeMode()==AppSettings.ThemeMode.LIGHT&&light.isSelected()&&!dark.isSelected(),"Light applied and saved");
                dark.doClick();check(!Theme.isLightMode()&&dark.isSelected(),"Dark applied");
                var volume=find(s,VolumeSlider.class,null);volume.setValue(47);check(AppSettings.getMasterVolume()==47,"Volume persisted");
                var sound=find(s,ToggleSwitch.class,"All sound effects");var buttons=find(s,ToggleSwitch.class,"Button sounds");var answers=find(s,ToggleSwitch.class,"Answer feedback");var timer=find(s,ToggleSwitch.class,"Countdown sounds");var motion=find(s,ToggleSwitch.class,"Reduced motion");
                buttons.doClick();answers.doClick();timer.doClick();check(!AppSettings.isButtonSoundEnabled()&&!AppSettings.isAnswerSoundEnabled()&&!AppSettings.isCountdownSoundEnabled(),"Individual audio options");
                sound.doClick();check(!AppSettings.isSoundEnabled()&&!buttons.isEnabled()&&!answers.isEnabled()&&!timer.isEnabled(),"Master switch disables subordinate controls");
                sound.doClick();check(!buttons.isSelected()&&!answers.isSelected()&&!timer.isSelected(),"Master switch preserves preferences");
                volume.setValue(0);check(!find(s,RoundedButton.class,"TEST SOUND").isEnabled(),"Preview disabled when muted");
                motion.doClick();check(AppSettings.isReducedMotionEnabled(),"Motion saved");
                MascotView mascot=find(s,MascotView.class,null);check(mascot!=null,"Mascot present");mascot.showHurt();
                try {var f=CharacterPortraitView.class.getDeclaredField("shakeFramesRemaining");f.setAccessible(true);check(f.getInt(mascot)==0,"Reduced motion prevents shake");}catch(ReflectiveOperationException ex){throw new AssertionError(ex);}
                s.refreshFromPreferences();check(volume.getValue()==0&&motion.isSelected(),"Reload preferences");
                s.restoreDefaults();check(AppSettings.getMasterVolume()==100&&AppSettings.isSoundEnabled()&&AppSettings.isButtonSoundEnabled()&&AppSettings.isAnswerSoundEnabled()&&AppSettings.isCountdownSoundEnabled()&&!AppSettings.isReducedMotionEnabled()&&dark.isSelected(),"Restore defaults");
            });
            System.out.println("PASS: Settings theme, preference controls, reduced motion, preview state and reset.");
        } finally {
            SwingUtilities.invokeAndWait(()->{AppSettings.setThemeMode(oldTheme);AppSettings.setMasterVolume(oldVolume);AppSettings.setSoundEnabled(oldSound);AppSettings.setButtonSoundEnabled(oldButtons);AppSettings.setAnswerSoundEnabled(oldAnswers);AppSettings.setCountdownSoundEnabled(oldCountdown);AppSettings.setReducedMotionEnabled(oldMotion);});
        }
    }
    private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    private static <T> T find(Component c,Class<T> type,String text){if(type.isInstance(c)&&(text==null||(c instanceof AbstractButton b&&text.equals(b.getText()))))return type.cast(c);if(c instanceof Container p)for(Component a:p.getComponents()){T found=find(a,type,text);if(found!=null)return found;}return null;}
}
