package com.thirdnf.resourceScheduler;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;

import javax.swing.event.EventListenerList;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * Abstract schedule model to take care of listeners and fire methods.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 * @author Victor Bucio - vbuciov@gmail.com
 */
@SuppressWarnings(
        {
            "UnusedDeclaration"
        })
public abstract class AbstractScheduleModel implements ScheduleModel
{

    // We are going to use the EventListenerList which allows multiple events to share one list.
    protected final EventListenerList _listenerList = new EventListenerList();
    private LocalDate _currentDate;
    private LocalTime start, end;

    //--------------------------------------------------------------------
    public AbstractScheduleModel()
    {
        _currentDate = new LocalDate();
        start = new LocalTime(0, 0, 0); // Default is 12 am 
        end = new LocalTime(23, 59, 59); // 23:45 pm
    }

    //--------------------------------------------------------------------
    public AbstractScheduleModel(LocalDate when)
    {
        _currentDate = when;
        start = new LocalTime(0, 0, 0); // Default is 12 am 
        end = new LocalTime(23, 59, 59); // 23:59 pm, 1 minute before 00 am
    }

    //--------------------------------------------------------------------
    public AbstractScheduleModel(LocalDate when, LocalTime start, LocalTime end)
    {
        _currentDate = when;
        this.start = start;
        this.end = end;
    }
    
    //--------------------------------------------------------------------
    public boolean isInCurrentDateRange (Appointment value)
    {
         LocalDateTime startDateTime = value.getDateTime();
            LocalDateTime endDateTime = value.getDateTime().plus(value.getDuration());
        
        return startDateTime.compareTo(getEndDate()) < 0 &&
                endDateTime.compareTo(getInitDate())>0;
    }
    
     //--------------------------------------------------------------------
 
    /**
     *
     * @param value The appointment
     * @param init The init Date Time
     * @param limit The limit Date Time
     * @return
     */
        public boolean isInDateRange (Appointment value, 
                                  LocalDateTime init,
                                   LocalDateTime limit)
    {
         LocalDateTime startDateTime = value.getDateTime();
            LocalDateTime endDateTime = value.getDateTime().plus(value.getDuration());
        
        return startDateTime.compareTo(limit) < 0 &&
                    endDateTime.compareTo(init) > 0;
    }

    //--------------------------------------------------------------------
    @Override
    public LocalDate getCurrentDate()
    {
        return _currentDate;
    }

    //--------------------------------------------------------------------
    public void setCurrentDate(LocalDate value)
    {
        LocalDate oldValue = _currentDate;
        this._currentDate = value;

        if (oldValue != null && !value.equals(oldValue))
            fireCurrentDateChange(oldValue, value);

    }

    //--------------------------------------------------------------------
    @NotNull
    @Override
    public LocalTime getStartTime()
    {
        return start;
    }

    //--------------------------------------------------------------------
    public void setStartTime(LocalTime value)
    {
        LocalTime oldValue = start;
        start = value;

        if (oldValue != null && !value.equals(oldValue))
            fireStartTimeChange(oldValue, value);
    }

    //--------------------------------------------------------------------
    @Override
    public LocalTime getEndTime()
    {
        return end;
    }

    //--------------------------------------------------------------------
    public void setEndTime(LocalTime value)
    {
        LocalTime oldValue = end;
        end = value;

        if (oldValue != null && !value.equals(oldValue))
            fireEndTimeChange(oldValue, value);
    }

    //--------------------------------------------------------------------
    /**
     * Get the current date with the end time
     *
     * @return the Init Date Time
     */
    public LocalDateTime getInitDate()
    {
        LocalDateTime init_value = new LocalDateTime(_currentDate.getYear(),
                                                     _currentDate.getMonthOfYear(),
                                                     _currentDate.getDayOfMonth(),
                                                     start.getHourOfDay(),
                                                     start.getMinuteOfHour(),
                                                     start.getSecondOfMinute());

        return init_value;
    }

    //--------------------------------------------------------------------
    /**
     * Get the current date with the end time
     *
     * @return the Fina Date Time
     */
    public LocalDateTime getEndDate()
    {
        LocalDate the_date = end.equals(LocalTime.MIDNIGHT)
                ? _currentDate.plusDays(1) : _currentDate;

        LocalDateTime final_value = new LocalDateTime(the_date.getYear(),
                                                      the_date.getMonthOfYear(),
                                                      the_date.getDayOfMonth(),
                                                      end.getHourOfDay(),
                                                      end.getMinuteOfHour(),
                                                      end.getSecondOfMinute());

        return final_value;
    }

    //--------------------------------------------------------------------
    public void fireCurrentDateChange(LocalDate oldDate, LocalDate newDate)
    {
        fireContentsChanged(new ScheduleModelDateEvent(this, ScheduleModelDateEvent.CONTENTS_CHANGED, oldDate, newDate));
    }

    //--------------------------------------------------------------------
    public void fireStartTimeChange(LocalTime oldStartTime, LocalTime newStartTime)
    {
        fireContentsChanged(new ScheduleModelTimeEvent(this, ScheduleModelTimeEvent.START_CHANGED, oldStartTime, newStartTime));
    }

    //--------------------------------------------------------------------
    public void fireEndTimeChange(LocalTime oldEndtTime, LocalTime newEndTime)
    {
        fireContentsChanged(new ScheduleModelTimeEvent(this, ScheduleModelTimeEvent.END_CHANGED, oldEndtTime, newEndTime));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param Index The index of Resources added
     */
    public void fireResourcesAdded(int Index)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.INSERT_RESOURCE, Index));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param firstIndex The initial index of Resources added
     * @param lastIndex The final index of Resources added
     */
    public void fireResourcesAdded(int firstIndex, int lastIndex)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.INSERT_RESOURCE, firstIndex, lastIndex));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param Index The index of Resources Updated
     */
    public void fireResourcesUpdated(int Index)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.UPDATE_RESOURCE, Index));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param firstIndex The first index of Resources Updated
     * @param lastIndex The Last index of Resources Updated
     */
    public void fireResourcesUpdateded(int firstIndex, int lastIndex)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.UPDATE_RESOURCE, firstIndex, lastIndex));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param Index The index of Resources Removed
     */
    public void fireResourcesRemoved(int Index)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.DELETE_RESOURCE, Index));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param firstIndex The first index of Resources Removed
     * @param lastIndex The last index of Resources Removed
     */
    public void fireResourcesRemoved(int firstIndex, int lastIndex)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.DELETE_RESOURCE, firstIndex, lastIndex));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param Index The index of Appointment added
     */
    public void fireAppointmentsAdded(int Index)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.INSERT_APPOINTMENT, Index));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param firstIndex The first index of Appointments added
     * @param lastIndex The Last index of Appointments added
     */
    public void fireAppointmentsAdded(int firstIndex, int lastIndex)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.INSERT_APPOINTMENT, firstIndex, lastIndex));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param Index The index of Appointments Updated
     */
    public void fireAppointmentsUpdated(int Index)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.UPDATE_APPOINTMENT, Index));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param firstIndex The first index of Appointments Updated
     * @param lastIndex The last index of Appointments Updated
     */
    public void fireAppointmentsUpdateded(int firstIndex, int lastIndex)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.UPDATE_APPOINTMENT, firstIndex, lastIndex));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param Index The index of Appointments Removed
     */
    public void fireAppointmentsRemoved(int Index)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.DELETE_APPOINTMENT, Index));
    }

    //--------------------------------------------------------------------
    /**
     * Protected method which an implementation of the Schedule Model would call
     * every time a new resource was added to the model. This method takes care
     * of calling all the subscribed listeners.
     *
     * @param firstIndex The first index of Appointments Removed
     * @param lastIndex The last index of Appointments Removed
     */
    public void fireAppointmentsRemoved(int firstIndex, int lastIndex)
    {
        fireScheduleModelChanged(new ScheduleModelEvent(this, ScheduleModelEvent.DELETE_APPOINTMENT, firstIndex, lastIndex));
    }

    //--------------------------------------------------------------------
    public void fireContentsChanged(ScheduleModelDateEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = _listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ScheduleModelListener.class)
                ((ScheduleModelListener) listeners[i + 1]).scheduleContentChange(e);
        }
    }

    //--------------------------------------------------------------------
    public void fireContentsChanged(ScheduleModelTimeEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = _listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ScheduleModelListener.class)
                ((ScheduleModelListener) listeners[i + 1]).scheduleContentChange(e);
        }
    }

    //--------------------------------------------------------------------
    public void fireScheduleModelChanged(ScheduleModelEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = _listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ScheduleModelListener.class)
                ((ScheduleModelListener) listeners[i + 1]).scheduleChange(e);

            else if (e.getType() > ScheduleModelEvent.RESOURCE_EVENT
                    && e.getType() < ScheduleModelEvent.APPOINTMENT_EVENT
                    && listeners[i] == ResourceChangeListener.class)
                fireResourceChanged(e, (ResourceChangeListener) listeners[i + 1]);

            else if (e.getType() > ScheduleModelEvent.APPOINTMENT_EVENT
                    && listeners[i] == AppointmentChangeListener.class)
                fireAppointmentChanged(e, (AppointmentChangeListener) listeners[i + 1]);
        }
    }

    //--------------------------------------------------------------------
    /**
     * Report a event related with Resources
     *
     * @param e The event information
     * @param listener The listener of this information
     */
    protected void fireResourceChanged(ScheduleModelEvent e, ResourceChangeListener listener)
    {
        for (int j = e.getFirtIndex(); j <= e.getLastIndex(); j++)
        {
            switch (e.getType())
            {
                case ScheduleModelEvent.INSERT_RESOURCE:
                    listener.resourceAdded(getResourceAt(j), getCurrentDate(), j);
                    break;

                case ScheduleModelEvent.UPDATE_RESOURCE:
                    listener.resourceUpdated(getResourceAt(j));
                    break;

                case ScheduleModelEvent.DELETE_RESOURCE:
                    listener.resourceRemoved(getResourceAt(j), getCurrentDate());
                    break;
            }
        }
    }

    //--------------------------------------------------------------------
    /**
     * Report a event related with Appointments
     *
     * @param e The event information
     * @param listener The listener of this information
     */
    protected void fireAppointmentChanged(ScheduleModelEvent e, AppointmentChangeListener listener)
    {
        for (int j = e.getFirtIndex(); j <= e.getLastIndex(); j++)
        {
            switch (e.getType())
            {
                case ScheduleModelEvent.INSERT_APPOINTMENT:
                    listener.appointmentAdded(getAppointmentAt(j));
                    break;

                case ScheduleModelEvent.UPDATE_APPOINTMENT:
                    listener.appointmentUpdated(getAppointmentAt(j));
                    break;

                case ScheduleModelEvent.DELETE_APPOINTMENT:
                    listener.appointmentRemoved(getAppointmentAt(j));
                    break;

            }
        }
    }

    //--------------------------------------------------------------------
    @Override
    public void addScheduleModelListener(@NotNull ScheduleModelListener listener)
    {
        _listenerList.add(ScheduleModelListener.class, listener);
    }

    //--------------------------------------------------------------------
    @Override
    public void removeScheduleModelListener(@NotNull ScheduleModelListener listener)
    {
        _listenerList.remove(ScheduleModelListener.class, listener);
    }

    //--------------------------------------------------------------------
    @Override
    public void addResourceChangeListener(@NotNull ResourceChangeListener listener)
    {
        _listenerList.add(ResourceChangeListener.class, listener);
    }

    //--------------------------------------------------------------------
    @Override
    public void removeResourceChangeListener(@NotNull ResourceChangeListener listener)
    {
        _listenerList.remove(ResourceChangeListener.class, listener);
    }

    //--------------------------------------------------------------------
    @Override
    public void addAppointmentChangeListener(@NotNull AppointmentChangeListener listener)
    {
        _listenerList.add(AppointmentChangeListener.class, listener);
    }

    //--------------------------------------------------------------------
    @Override
    public void removeAppointmentChangeListener(@NotNull AppointmentChangeListener listener)
    {
        _listenerList.remove(AppointmentChangeListener.class, listener);
    }
}
