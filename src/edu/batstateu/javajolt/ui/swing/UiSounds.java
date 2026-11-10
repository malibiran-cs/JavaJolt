package edu.batstateu.javajolt.ui.swing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class UiSounds {
    private static final String CLASSPATH_ROOT = "/edu/batstateu/javajolt/ui/swing/assets/sfx/";
    private static final Path SOURCE_ASSET_PATH = Path.of(
            "src", "edu", "batstateu", "javajolt", "ui", "swing", "assets", "sfx");
    private static final List<Path> FALLBACK_ROOTS = List.of(
            SOURCE_ASSET_PATH,
            Path.of("JavaJolt").resolve(SOURCE_ASSET_PATH),
            Path.of("JavaJolt-main").resolve(SOURCE_ASSET_PATH));
    private static final float MUTED_VOLUME = 0.0f;
    private static final double DECIBELS_PER_AMPLITUDE_DECADE = 20.0;
    private static final EnumMap<SoundEffect, Clip> CLIPS = new EnumMap<>(SoundEffect.class);

    private UiSounds() {
    }

    public static void preload() {
        for (SoundEffect effect : SoundEffect.values()) {
            if (!CLIPS.containsKey(effect)) {
                load(effect);
            }
        }
    }

    public static void play(SoundEffect effect) {
        Clip clip = CLIPS.get(effect);
        if (clip == null) {
            return;
        }
        try {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (RuntimeException e) {
            markUnavailable(effect, clip, e);
        }
    }

    private static void load(SoundEffect effect) {
        Clip clip = null;
        try (InputStream stream = new BufferedInputStream(open(effect.getResourcePath()));
                AudioInputStream audio = AudioSystem.getAudioInputStream(stream)) {
            clip = AudioSystem.getClip();
            clip.open(audio);
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(gainForVolume(effect.getVolume(), gain.getMinimum(), gain.getMaximum()));
            CLIPS.put(effect, clip);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | RuntimeException e) {
            markUnavailable(effect, clip, e);
        }
    }

    private static float gainForVolume(float volume, float minimum, float maximum) {
        if (volume <= MUTED_VOLUME) {
            return minimum;
        }
        double decibels = DECIBELS_PER_AMPLITUDE_DECADE * Math.log10(volume);
        return (float) Math.max(minimum, Math.min(maximum, decibels));
    }

    private static InputStream open(String file) throws IOException {
        InputStream packaged = UiSounds.class.getResourceAsStream(CLASSPATH_ROOT + file);
        if (packaged != null) {
            return packaged;
        }
        for (Path root : FALLBACK_ROOTS) {
            Path path = root.resolve(file);
            if (Files.isRegularFile(path)) {
                return Files.newInputStream(path);
            }
        }
        throw new IOException("sound file not found: " + file);
    }

    private static void markUnavailable(SoundEffect effect, Clip clip, Exception cause) {
        // NOTE: Audio is cosmetic; a null cache entry prevents retries and repeated warnings.
        CLIPS.put(effect, null);
        if (clip != null) {
            try {
                clip.close();
            } catch (RuntimeException e) {
                cause.addSuppressed(e);
            }
        }
        System.err.println("Sound " + effect + " unavailable: " + cause);
    }
}
