package com.thirdnf.resourceScheduler.components;

import com.thirdnf.resourceScheduler.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


@SuppressWarnings({"UnusedDeclaration"})
public abstract class AbstractResourceComponent extends JComponent implements MouseListener 
{
    private MouseListener inputListener;
    // The resource this component is wrapping.
    protected final Resource _resource;

    // This is the color which the DaySchedule and other views should use to identify this
    //  resource.  By default this is null which means they don't identify it.  This is not
    //  the same as the background or foreground colors which are used to paint this actual
    //  component.
    protected Color _color;


    /**
     * Constructor given a resource to wrap
     * @param resource (not null) Resource to wrap.
     */
    protected AbstractResourceComponent(@NotNull Resource resource)
    {
        _resource = resource;
        addMouseListener(this);
    }


    /**
     * Simple getter to get the wrapped resource.
     *
     * @return (not null) The wrapped resource
     */
    @NotNull
    public Resource getResource()
    {
        return _resource;
    }


    /**
     * Simple getter to get the color identifier for this resource.
     * @return (nullable) Color for the resource or null if it has not been set.
     */
    @Nullable
    public Color getColor()
    {
        return _color;
    }


    /**
     * Simple setter for this resource.
     *
     * @param color (nullable) Color to set for this resource.
     */
    public void setColor(@Nullable Color color)
    {
        _color = color;
    }


    /**
     * Print this component into the given area using the graphics handle.
     *
     * @param graphics (not null) Graphics handle to draw with
     * @param area (not null) Area for printing
     */
    public abstract void print(@NotNull Graphics2D graphics, Rectangle area);

    public final void mouseClicked(MouseEvent e)
    {
       inputListener.mouseClicked(e);
    }

    public final void mousePressed(MouseEvent e)
    {
       inputListener.mousePressed(e);
    }

    public final void mouseReleased(MouseEvent e)
    {
       //inputListener.mouseReleased(e);
    }

    public final void mouseEntered(MouseEvent e)
    {
       inputListener.mouseEntered(e);
    }

    public final void mouseExited(MouseEvent e)
    {
       inputListener.mouseExited(e);
    }

    public final void setInputListener(MouseListener inputListener)
    {
        this.inputListener = inputListener;
    }
}
