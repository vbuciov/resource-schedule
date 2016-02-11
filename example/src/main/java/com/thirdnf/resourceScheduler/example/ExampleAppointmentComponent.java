package com.thirdnf.resourceScheduler.example;

import com.thirdnf.resourceScheduler.Appointment;
import com.thirdnf.resourceScheduler.components.BasicAppointmentComponent;
import java.awt.Color;
import java.awt.Graphics;
import org.jetbrains.annotations.NotNull;




public class ExampleAppointmentComponent extends BasicAppointmentComponent 
{
    /**
     * Constructor given an appointment to wrap.
     *
     * @param appointment (not null) The appointment to wrap.
     */
    public ExampleAppointmentComponent(@NotNull Appointment appointment)
    {
        super(appointment);
    }


    @Override
    public void paintComponent(Graphics g)
    {
        // If our resource is a Demo Resource (and it really should be) then get its color.
        // We do this here so that if the resource is updated we will pick up its new color
        if (_appointment instanceof ExampleAppointment) {
            Color c = ((ExampleAppointment)_appointment).getCategory().getColor();
            setBackground(c);
        }

        super.paintComponent(g);
    }
}
