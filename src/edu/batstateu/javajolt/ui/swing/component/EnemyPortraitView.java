package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Graphics2D;
import java.util.Objects;

/** Displays one of the seven team-drawn enemies in the shared portrait frame. */
public final class EnemyPortraitView extends CharacterPortraitView {
    private static final NinePatch FRAME =
            UiAssets.patch("panels/panel_inset", 13, 13, 13, 13);

    private Topic topic = Topic.VARIABLES;

    public EnemyPortraitView() {
        super(false);
    }

    public void setTopic(Topic topic) {
        this.topic = Objects.requireNonNull(topic, "Enemy topic cannot be null");
        setToolTipText(topic.getLabel() + " enemy");
        repaint();
    }

    @Override
    public String spritePath(CharacterEmotion emotion) {
        return CharacterSpriteCatalog.enemy(topic, emotion);
    }

    @Override
    protected void paintFrame(Graphics2D graphics) {
        FRAME.paint(graphics, getWidth(), getHeight());
    }
}
