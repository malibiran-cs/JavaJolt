import edu.batstateu.javajolt.enums.JoltMessage;
import edu.batstateu.javajolt.ui.swing.JoltSounds;
import edu.batstateu.javajolt.ui.swing.SoundEffect;

// Pure UI sound selection; never opens an audio device or a Swing window.
public class JoltSoundRulesCheck {
    public static void main(String[] args) {
        JoltMessage[] messages = {JoltMessage.NONE, JoltMessage.SPARK, JoltMessage.JOLT,
                JoltMessage.SURGE, JoltMessage.JAVAJOLT, JoltMessage.SHORT_CIRCUIT};
        SoundEffect[] changedSounds = {null, SoundEffect.JOLT_SPARK, SoundEffect.JOLT_JOLT,
                SoundEffect.JOLT_SURGE, null, SoundEffect.JOLT_SHORT_CIRCUIT};
        int checks = 0;
        for (JoltMessage before : messages) {
            for (int i = 0; i < messages.length; i++) {
                JoltMessage after = messages[i];
                SoundEffect expected = before == after ? null : changedSounds[i];
                if (JoltSounds.soundFor(before, after) != expected) {
                    throw new AssertionError("Unexpected Jolt sound: " + before + " -> " + after);
                }
                checks++;
            }
        }
        System.out.println("ALL JOLT SOUND RULE CHECKS PASSED (" + checks + ")");
    }
}
