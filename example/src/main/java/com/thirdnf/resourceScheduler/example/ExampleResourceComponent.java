package com.thirdnf.resourceScheduler.example;

import com.thirdnf.resourceScheduler.Resource;
import com.thirdnf.resourceScheduler.components.BasicResourceComponent;
import java.awt.Color;
import java.awt.Graphics;
import org.jetbrains.annotations.NotNull;

/**
 * Instances of this will be the resources displayed along the top.  This extends the
 * {@link BasicResourceComponent} and adds a mouse listener with some context menus.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
public class ExampleResourceComponent extends BasicResourceComponent 
{
    /**
     * Constructor given a resource to wrap.
     *
     * @param resource (not null)
     */
    public ExampleResourceComponent(@NotNull Resource resource)
    {
        super(resource);
    }


    @Override
    public void paintComponent(Graphics g)
    {
        // If our resource is a Demo Resource (and it really should be) then get its color.
        // We do this here so that if the resource is updated we will pick up its new color
        if (_resource instanceof ExampleResource) {
            Color c = ((ExampleResource)_resource).getColor();
            setBackground(c);
        }

        super.paintComponent(g);
    }
}
