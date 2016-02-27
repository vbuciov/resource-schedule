package com.thirdnf.resourceScheduler;

import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.EventListener;

public interface ScheduleListener extends EventListener
{

    /**
     * Deal with an action performed (likely a user click) on the given resource
     * at the given time. Implementations would likely prompt to add an
     * appointment.
     *
     * @param resource (not null) Resource clicked in.
     * @param time (not null) Time selected.
     */
    void actionPerformed(@NotNull Resource resource, @NotNull DateTime time);

    /**
     * Deal with clicked performed over Appointment
     * @param source Appointment related to Event.
     * @param e Information of Event
     */
    void appointmentMouseClicked(Appointment source, MouseEvent e);

    void appointmentMousePressed(Appointment source, MouseEvent e);
    
    void appointmentDragReleased(Appointment source, MouseEvent e);

    void resourceMousePressed(Resource source, MouseEvent e);

    void resourceMouseClicked(Resource source, MouseEvent e);
}
