package com.thirdnf.resourceScheduler;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * Instances of this interface can be used as models for the Scheduler. You
 * really should not be implementing this interface directly, but rather just
 * extending {@link AbstractScheduleModel} which will take care of the listeners
 * for you.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
@SuppressWarnings(
        {
            "UnusedDeclaration"
        })
public interface ScheduleModel
{

    //--------------------------------------------------------------------
    /**
     * Visit all appointments for the given date time. The Resource Scheduler
     * uses Joda DateTime rather than Java's DateTime for the flexibility.
     *
     * @param visitor (not null) The visitor to visit with every appointment for
     * the given day
     * @param init The init of day to visit the appointments for.
     * @param limit (not null) The limit day to visit the appointments for.
     */
    void visitAppointments(AppointmentVisitor visitor,
                           @NotNull LocalDateTime init,
                           @NotNull LocalDateTime limit);
    
    //--------------------------------------------------------------------
    /**
     * Determines if appointment is in the current date time
     * uses Joda DateTime rather than Java's DateTime for the flexibility.
     *
     * @param value  The appoint to comprobe
     * @return Indicates wherever or not
     */
    boolean isInCurrentDateRange (Appointment value);

    //--------------------------------------------------------------------
    /**
     * Visit all appointments for the given date time. The Resource Scheduler
     * uses Joda DateTime rather than Java's DateTime for the flexibility.
     *
     * @param visitor (not null) The visitor to visit with every appointment for
     * the given day
     * @param limit (not null) The limit day to visit the resources for.
     */
    void visitResources(ResourceVisitor visitor, @NotNull LocalDate limit);

    //--------------------------------------------------------------------
    /**
     * Get the start time for a particular date. Currently there is a not-null
     * requirement on this, but eventually this should allow nulls and just
     * determine the start time automatically from the start times of all the
     * available resources.
     *
     * @return (not null) The time the day starts for the given day.
     */
    @NotNull
    LocalTime getStartTime();

    //--------------------------------------------------------------------
    /**
     * Set the start time for a particular date.
     *
     * @param value the new start time
     */
    void setStartTime(LocalTime value);

    //--------------------------------------------------------------------
    /**
     * Get the end time for the day for layout calculate
     *
     * @return (not null) The time
     */
    LocalTime getEndTime();

    //--------------------------------------------------------------------
    /**
     * Set the end time for the day for layout calculate
     *
     * @param value the new ent time
     */
    void setEndTime(LocalTime value);

    //--------------------------------------------------------------------
    /**
     * Get the current date for appointments manage.
     *
     * @return the current date.
     */
    LocalDate getCurrentDate();

    //--------------------------------------------------------------------
    /**
     * Set the current date for appointments manage
     *
     * @param value The new current date
     */
    void setCurrentDate(LocalDate value);

    //--------------------------------------------------------------------
    /**
     * Get the current date with the end time
     *
     * @return the Init Date Time
     */
    LocalDateTime getInitDate();

    //--------------------------------------------------------------------
    /**
     * Get the current date with the end time
     *
     * @return the Final Date Time
     */
    LocalDateTime getEndDate();

    //--------------------------------------------------------------------
    /**
     * Get the current size of Resources
     *
     * @return the size
     */
    int getResourceCount();

    //--------------------------------------------------------------------
    /**
     * Get the current size of Appoitments
     *
     * @return the size
     */
    int getAppoitmentCount();

    //--------------------------------------------------------------------
    /**
     * Get the index of Resource
     *
     * @param value Resource
     * @return the index
     */
    int indexOf(Resource value);

    //--------------------------------------------------------------------
    /**
     * Get the index of Appointment
     *
     * @param value Appointment
     * @return the index
     */
    int indexOf(Appointment value);

    //--------------------------------------------------------------------
    /**
     * Get the resource at index especified
     *
     * @param index to get a Resource
     * @return the resource at index
     */
    Resource getResourceAt(int index);

    //--------------------------------------------------------------------
    /**
     * Set Resource at index especified
     *
     * @param value The Resource to set at index
     * @param index The index
     */
    //void setResourceAt(Resource value, int index);

    //--------------------------------------------------------------------
    /**
     * Get the Appointment at index especified
     *
     * @param index to get a Appointment
     * @return the resource
     */
    Appointment getAppointmentAt(int index);

    //--------------------------------------------------------------------
    /**
     * Set Appointment at index especified
     *
     * @param value The Appointment to set at index
     * @param index The index
     */
    //void setAppointmentAt(Appointment value, int index);

    //--------------------------------------------------------------------
    /**
     * Method to request that the given listener be notified when a changes at
     * resources or appointment has been added or removed from the model.
     *
     * @param listener (not null) Listener to notify when a change ocuured.
     */
    void addScheduleModelListener(@NotNull ScheduleModelListener listener);

    //--------------------------------------------------------------------
    /**
     * Method to request that the given listener be removed from notification
     * when a resource has been either added or removed from the model.
     *
     * @param listener (not null) Listener to notify when a resource has been
     * added or removed.
     */
    void removeScheduleModelListener(@NotNull ScheduleModelListener listener);

    //--------------------------------------------------------------------
    /**
     * Method to request that the given listener be notified when a changes at
     * resources or appointment has been added or removed from the model.
     *
     * @param listener (not null) Listener to notify when a resource has been
     * added or removed.
     */
    void addResourceChangeListener(ResourceChangeListener listener);

    //--------------------------------------------------------------------
    /**
     * Method to request that the given listener be removed from notification
     * when a resource has been either added or removed from the model.
     *
     * @param listener (not null) Listener to notify when a resource has been
     * added or removed.
     */
    void removeResourceChangeListener(ResourceChangeListener listener);

    //--------------------------------------------------------------------
    /**
     * Request that the given listener be notified when an appointment has been
     * either added or removed from the model.
     *
     * @param listener (not null) Listener to notify when a resource has been
     * added or removed.
     */
    void addAppointmentChangeListener(@NotNull AppointmentChangeListener listener);

    //--------------------------------------------------------------------
    /**
     * Request that the given listener be removed from notification when an
     * appointment has been either added or removed from the model.
     *
     * @param listener (not null) Listener to notify when a resource has been
     * added or removed.
     */
    void removeAppointmentChangeListener(@NotNull AppointmentChangeListener listener);
}
