package com.thirdnf.resourceScheduler.components;

import com.thirdnf.resourceScheduler.Appointment;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


/**
 * This is a very basic implementation of the abstract appointment component.  This one is ready to
 * go but does not support mouse listening for when a user clicks on a component.  This class
 * could easily be extended with a class that did add that functionality.
 * <p>
 * It would be nice if this component rendered itself similar to how the Google appointments
 * are drawn in their calendar.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
public class BasicAppointmentComponent extends AbstractAppointmentComponent
{
    protected static Color BackgroundColor;
    int arc = 10;
   
    static
    {
        BackgroundColor = new Color(9, 171, 246, 200);
    }

    /**
     * Constructor given an appointment to wrap.
     *
     * @param appointment (not null) The appointment to wrap.
     */
    public BasicAppointmentComponent(@NotNull Appointment appointment)
    {
        super(appointment);

        // The preferred size is pretty much just ignored for right now.
        setPreferredSize(new Dimension(100, 100));
        setBounds(50, 50, 200, 150);

        // Default our background color to blue
        setBackground(BackgroundColor);
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D)g;
        Color oldColor = graphics.getColor();
        RenderingHints renderHints = graphics.getRenderingHints();

        Insets insets = getInsets();
        int width = getWidth() /*- insets.left - insets.right*/;
        int height = getHeight() /*- insets.top - insets.bottom*/;

        // Draw our Component FACE
        graphics.setColor(getBackground());
        graphics.fillRoundRect(insets.left, insets.top, width - insets.left, height - insets.top, arc, arc);

        /*
        The border is drawing by the Border Component.
        graphics.setColor(Color.gray);
        graphics.drawRoundRect(insets.left + 1, insets.top + 1, width - insets.left - 1, height - insets.top - 1, arc, arc);*/

        FontMetrics fontMetrics = getFontMetrics(getFont());
        int fontHeight = fontMetrics.getHeight() - fontMetrics.getDescent();

        graphics.setColor(Color.black);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
        //**********************************************************************
        // String adjusting
        int off = 1;
        for(String val: _appointment.getTitle().split("\n"))
        {
            graphics.drawString(val, insets.left + 2, insets.top + fontHeight * off);
            off ++;
        }
        //**********************************************************************
       
        graphics.setRenderingHints(renderHints);
        graphics.setColor(oldColor);
    }


    @Override
    public void print(@NotNull Graphics2D graphics, Rectangle area)
    {
        Color oldColor = graphics.getColor();

        graphics.setColor(getBackground());
        graphics.fillRect(area.x, area.y, area.width, area.height);

        graphics.setColor(oldColor);
    }

    public int getArc()
    {
        return arc;
    }

    public void setArc(int arc)
    {
        this.arc = arc;
    }
}


