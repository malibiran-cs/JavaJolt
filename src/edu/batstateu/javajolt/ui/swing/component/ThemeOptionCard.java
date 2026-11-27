package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.AppSettings.ThemeMode;
import edu.batstateu.javajolt.ui.swing.Theme;
import java.awt.*;
import javax.swing.JToggleButton;

/** Theme selection card with a miniature preview of the selected palette. */
public final class ThemeOptionCard extends JToggleButton {
    private final ThemeMode mode;
    public ThemeOptionCard(ThemeMode mode) {
        super(mode == ThemeMode.DARK ? "Dark mode" : "Light mode");
        this.mode=mode;
        setPreferredSize(new Dimension(160,94));
        setMinimumSize(new Dimension(100,94));
        setOpaque(false); setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
        setRolloverEnabled(true); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getAccessibleContext().setAccessibleName(getText());
    }
    public ThemeMode getMode(){ return mode; }
    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g=(Graphics2D)graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(); if(w<40||h<50)return;
            g.setColor(getModel().isRollover()?Theme.SECONDARY_HOVER:Theme.INSET);
            g.fillRoundRect(1,1,w-2,h-2,14,14);
            g.setColor(isSelected()||isFocusOwner()?Theme.ACCENT:Theme.INSET_BORDER);
            g.setStroke(new BasicStroke(isSelected()?2f:1f));g.drawRoundRect(2,2,w-5,h-5,13,13);
            boolean light=mode==ThemeMode.LIGHT;
            int px=10,py=9,pw=w-20,ph=h-43;
            g.setColor(new Color(light?0xF3F4F6:0x141518));g.fillRoundRect(px,py,pw,ph,8,8);
            g.setColor(Theme.ACCENT);g.fillRoundRect(px+7,py+7,Math.max(12,pw/3),5,5,5);
            int side=Math.max(12,pw/4);
            g.setColor(new Color(light?0xD5D9E0:0x2A2D33));g.fillRoundRect(px+7,py+17,side,ph-23,5,5);
            int cx=px+side+12,cw=pw-side-19;
            g.setColor(new Color(light?0xFFFFFF:0x30333A));g.fillRoundRect(cx,py+17,cw,ph-23,5,5);
            g.setColor(new Color(light?0x626978:0x8B919E));g.fillRoundRect(cx+5,py+23,Math.max(1,cw-12),3,3,3);
            g.setColor(Theme.ACCENT);g.fillRoundRect(cx+5,py+31,Math.max(1,cw/2),6,4,4);
            g.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD,13f));g.setColor(Theme.TEXT);
            g.drawString(getText(),12,h-13);
            if(isSelected()){g.setColor(Theme.ACCENT);g.fillOval(w-28,h-29,17,17);g.setColor(Theme.TEXT_ON_ACCENT);g.setStroke(new BasicStroke(2));g.drawLine(w-24,h-20,w-21,h-17);g.drawLine(w-21,h-17,w-15,h-24);}
        }finally{g.dispose();}
    }
}
