package com.thirdnf.ResourceScheduler;

import javax.swing.*;
import java.awt.*;


public class AppointmentComponent extends JComponent
{
    private String _text;

    private static final Color Background = new Color(0x11b9f8);


    public AppointmentComponent(String text)
    {
        _text = text;

        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D)g;

        Color oldColor = graphics.getColor();

        RenderingHints renderHints = graphics.getRenderingHints();

        Insets insets = getInsets();

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        // Draw our border
        int arc = 10;

        graphics.setColor(Background);
        graphics.fillRoundRect(insets.left, insets.top, insets.left+width-1, insets.top+height-1, arc, arc);

        graphics.setColor(Color.black);
        graphics.drawRoundRect(insets.left, insets.top, insets.left+width-1, insets.top+height-1, arc, arc);

        FontMetrics fontMetrics = getFontMetrics(getFont());
        int fontHeight = fontMetrics.getHeight();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawString(_text, insets.left + 10, insets.top + fontHeight + 2);

        graphics.setRenderingHints(renderHints);
        graphics.setColor(oldColor);
    }
}
