package com.thirdnf.resourceScheduler.example;

import com.thirdnf.resourceScheduler.Appointment;
import com.thirdnf.resourceScheduler.Resource;
import com.thirdnf.resourceScheduler.components.AbstractAppointmentComponent;
import com.thirdnf.resourceScheduler.components.AbstractResourceComponent;
import com.thirdnf.resourceScheduler.components.BasicComponentFactory;
import org.jetbrains.annotations.NotNull;


/**
 * This is the example component factory which is used to create custom resource and appointment components.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
public class ExampleComponentFactory extends BasicComponentFactory
{
    @Override
    @NotNull
    public AbstractResourceComponent makeResourceComponent(@NotNull Resource resource)
    {
        ExampleResourceComponent component = new ExampleResourceComponent(resource);
        return component;
    }

    @Override
    @NotNull
    public AbstractAppointmentComponent makeAppointmentComponent(@NotNull Appointment appointment)
    {
        ExampleAppointmentComponent component = new ExampleAppointmentComponent(appointment);
        return component;
    }
}
