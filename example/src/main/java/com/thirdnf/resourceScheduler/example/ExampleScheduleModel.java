package com.thirdnf.resourceScheduler.example;

import com.thirdnf.resourceScheduler.*;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A example model, has its database hard coded and it is only capable of
 * showing two days, Today and Tomorrow. Everything else is blank and will
 * ignore updates.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
public class ExampleScheduleModel extends AbstractScheduleModel
{

    public static final LocalDate Today = new LocalDate();
    public static final LocalDate Tomorrow = Today.plusDays(1);

    private static final List<Resource> TodayResources = new ArrayList<Resource>();
    private static final List<Resource> TomorrowResources = new ArrayList<Resource>();

    private static final List<Appointment> Appointments = new ArrayList<Appointment>();

    // Resources
    private static final ExampleResource Bobby = new ExampleResource("Bobby", new Color(251, 198, 12, 200));
    private static final ExampleResource Johnny = new ExampleResource("Johnny", new Color(12, 251, 160, 200));
    private static final ExampleResource Sally = new ExampleResource("Sally", new Color(166, 251, 12, 200));
    private static final ExampleResource Freddy = new ExampleResource("Freddy", new Color(66, 151, 12, 200));

    // Our categories for our appointments
    private static final ExampleCategory Green = new ExampleCategory("Green", new Color(9, 246, 76, 200));
    private static final ExampleCategory Blue = new ExampleCategory("Blue", new Color(9, 171, 246, 200));

    // This initializes the defaults.
    static
    {
        TodayResources.add(Bobby);
        TodayResources.add(Johnny);
        TodayResources.add(Sally);

        // Freddy is not going to be listed for today, but an appointment today is going to be assigned
        //  to Freddy.  The appointment should show up in the first column for the day.
        // Populate some default appointments
        Appointments.add(ExampleAppointment.create("Appointment1", Green, Bobby, new LocalTime(10, 0, 0), 45));
        Appointments.add(ExampleAppointment.create("Appointment2", Blue, Johnny, new LocalTime(13, 0, 0), 75));
        Appointments.add(ExampleAppointment.create("Appointment3", Blue, Sally, new LocalTime(8, 0, 0), 60));
        Appointments.add(ExampleAppointment.create("Appointment4", Green, Sally, new LocalTime(8, 45, 0), 120));
        Appointments.add(ExampleAppointment.create("Appointment5", Blue, Sally, new LocalTime(10, 45, 0), 30));
        Appointments.add(ExampleAppointment.create("Appointment6", Green, Sally, new LocalTime(12, 30, 0), 40));
        Appointments.add(ExampleAppointment.create("Appointment7", Blue, Freddy, new LocalTime(13, 0, 0), 50));
    }

    //--------------------------------------------------------------------
    @Override
    public void visitAppointments(AppointmentVisitor visitor, @NotNull LocalDate date)
    {
        for (Appointment appointment : Appointments)
        {
            LocalDate appointmentDate = appointment.getDateTime().toLocalDate();
            if (!appointmentDate.equals(date))
            {
                continue;
            }
            visitor.visitAppointment(appointment);
        }
    }

    //--------------------------------------------------------------------
    @Override
    public void visitResources(ResourceVisitor visitor, @NotNull LocalDate date)
    {
        List<Resource> resources;
        if (date.equals(Today))
        {
            resources = TodayResources;
        }
        else if (date.equals(Tomorrow))
        {
            resources = TomorrowResources;
        }
        else
        {
            return;
        }

        for (Resource resource : resources)
        {
            visitor.visitResource(resource);
        }
    }

    //--------------------------------------------------------------------
    public void visitCategories(@NotNull CategoryVisitor visitor)
    {
        visitor.visitCategory(Green);
        visitor.visitCategory(Blue);
    }

    //--------------------------------------------------------------------
    /**
     * Our model has been told to add a resource to its database. This method
     * will add the resource to the underlying database and then trigger a
     * redraw to any components using this model.
     *
     * @param resource (not null) Resource to add
     * @param index Position to add the resource, -1 indicates that it should be
     * added a the end.
     */
    public void addResource(@NotNull Resource resource, int index)
    {
        List<Resource> resources;
        if (getCurrentDate().equals(Today))
        {
            resources = TodayResources;
        }
        else if (getCurrentDate().equals(Tomorrow))
        {
            resources = TomorrowResources;
        }
        else
        {
            return;
        }

        resources.add(resource);
        
        
        if (index < 0 || index > resources.size())
            index = resources.size() - 1;

        fireResourcesAdded(index);
    }

    //--------------------------------------------------------------------
    public void addAppointment(@NotNull Appointment appointment)
    {
        int index;
        Appointments.add(appointment);

        index = Appointments.size() - 1;

        fireAppointmentsAdded(index);
    }

    //--------------------------------------------------------------------
    public void updateAppointment(@NotNull Appointment appointment)
    {
        fireAppointmentsUpdated(Appointments.indexOf(appointment));
    }

    //--------------------------------------------------------------------
    public void updateResource(@NotNull Resource resource)
    {
        List<Resource> resources;
        if (getCurrentDate().equals(Today))
        {
            resources = TodayResources;
        }
        else if (getCurrentDate().equals(Tomorrow))
        {
            resources = TomorrowResources;
        }
        else
        {
            return;
        }

        fireResourcesUpdated(resources.indexOf(resource));
    }

    //--------------------------------------------------------------------
    public void deleteResource(@NotNull Resource resource)
    {
        List<Resource> resources;
        if (getCurrentDate().equals(Today))
        {
            resources = TodayResources;
        }
        else if (getCurrentDate().equals(Tomorrow))
        {
            resources = TomorrowResources;
        }
        else
        {
            return;
        }

        int index = resources.indexOf(resource);
        resources.remove(index);
        fireResourcesRemoved(index);
    }

    //--------------------------------------------------------------------
    public void deleteAppointment(@NotNull Appointment appointment)
    {
        int index = Appointments.indexOf(appointment);
        // Remove it from our list
        Appointments.remove(index);
        // Let any listeners know we have removed this appointment.
        fireAppointmentsRemoved(index);
    }

    public int getResourceCount()
    {
        List<Resource> resources;
        if (getCurrentDate().equals(Today))
        {
            resources = TodayResources;
        }
        else
        {
            resources = TomorrowResources;
        }

        return resources.size();
    }

    public int getAppoitmentCount()
    {
        return Appointments.size();
    }

    public Resource getResourceAt(int index)
    {
        List<Resource> resources;
        if (getCurrentDate().equals(Today))
        {
            resources = TodayResources;
        }
        else
        {
            resources = TomorrowResources;
        }
        return resources.get(index);
    }

    public void setResourceAt(Resource value, int index)
    {
        List<Resource> resources;
        if (getCurrentDate().equals(Today))
        {
            resources = TodayResources;
        }
        else
        {
            resources = TomorrowResources;
        }

        resources.set(index, value);
        fireResourcesAdded(index);
    }

    public Appointment getAppointmentAt(int index)
    {
        return Appointments.get(index);
    }

    public void setAppointmentAt(Appointment value, int index)
    {
        Appointments.set(index, value);
        fireAppointmentsAdded(index);
    }

    public int indexOf(Resource value)
    {
        List<Resource> resources;
        if (getCurrentDate().equals(Today))
        {
            resources = TodayResources;
        }
        else
        {
            resources = TomorrowResources;
        }

        return resources.indexOf(value);
    }

    public int indexOf(Appointment value)
    {
        return Appointments.indexOf(value);
    }

}
