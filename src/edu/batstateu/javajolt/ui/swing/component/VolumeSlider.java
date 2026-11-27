package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.Theme;
import java.awt.*;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/** Accent-colored slider; retains Swing's mouse and keyboard behavior. */
public final class VolumeSlider extends JSlider {
    public VolumeSlider(){
        super(0,100);setOpaque(false);setPreferredSize(new Dimension(240,30));
        getAccessibleContext().setAccessibleName("Master volume");
        setUI(new BasicSliderUI(this){
            @Override protected Dimension getThumbSize(){return new Dimension(18,18);}
            @Override public void paintTrack(Graphics graphics){
                Graphics2D g=(Graphics2D)graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int y=trackRect.y+trackRect.height/2-4;
                g.setColor(Theme.INSET_BORDER);g.fillRoundRect(trackRect.x,y,trackRect.width,8,8,8);
                g.setColor(Theme.ACCENT);g.fillRoundRect(trackRect.x,y,Math.max(0,xPositionForValue(slider.getValue())-trackRect.x),8,8,8);g.dispose();
            }
            @Override public void paintThumb(Graphics graphics){
                Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.ACCENT);g.fillOval(thumbRect.x+1,thumbRect.y+1,16,16);
                g.setColor(Theme.TEXT_ON_ACCENT);g.fillOval(thumbRect.x+6,thumbRect.y+6,6,6);g.dispose();
            }
            @Override public void paintFocus(Graphics g){g.setColor(Theme.ACCENT);g.drawRoundRect(1,1,slider.getWidth()-3,slider.getHeight()-3,8,8);}
            @Override protected void scrollDueToClickInTrack(int direction){ Point mouse=slider.getMousePosition(); if(mouse!=null) slider.setValue(valueForXPosition(mouse.x)); else super.scrollDueToClickInTrack(direction); }
        });
    }
}
