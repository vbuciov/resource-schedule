/*
 * Copyright 2016 sis2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirdnf.resourceScheduler;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 *
 * @author Victor Manuel Bucio Vargas vbuciov@gmail.com
 */
public class DefaultScheduleModel extends AbstractScheduleModel
{

    private List<Resource> Resources;
    private List<Appointment> Appointments;

    //--------------------------------------------------------------------
    public DefaultScheduleModel()
    {
        Resources = new ArrayList<Resource>();
        Appointments = new ArrayList<Appointment>();
    }

    //--------------------------------------------------------------------
    @Override
    public void visitAppointments(AppointmentVisitor visitor,
                                  @NotNull LocalDateTime init,
                                  @NotNull LocalDateTime limit)
    {
        for (Appointment appointment : Appointments)
        {
            if (!isInDateRange(appointment, init, limit))
                continue;

            visitor.visitedAppointment(appointment);
        }
    }

    //--------------------------------------------------------------------
    @Override
    public void visitResources(ResourceVisitor visitor, @NotNull LocalDate limit)
    {
        for (Resource resource : Resources)
        {
            visitor.visitedResource(resource);
        }
    }

    //--------------------------------------------------------------------
    public int getResourceCount()
    {
        return Resources.size();
    }

    //--------------------------------------------------------------------
    public int getAppoitmentCount()
    {
        return Appointments.size();
    }

    //--------------------------------------------------------------------
    /**
     * Our model has been told to add a resource to its database. This method
     * will add the resource to the underlying database and then trigger a
     * redraw to any components using this model.
     *
     * @param resource (not null) Resource to add
     * @return the index of Resources added
     */
    public int addResource(@NotNull Resource resource)
    {
        int index;

        Resources.add(resource);
        index = Resources.size() - 1;

        fireResourcesAdded(index);

        return index;
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
        if (index < 0 || index >= Resources.size())
        {
            Resources.add(resource);
            index = Resources.size() - 1;
        }

        else
            Resources.add(index, resource);

        fireResourcesAdded(index);
    }

    //--------------------------------------------------------------------
    public boolean addResources(List<Resource> resources)
    {
        int first;
        boolean success;

        first = Resources.size() - 1;

        success = Resources.addAll(resources);

        fireResourcesAdded(first, Resources.size() - 1);

        return success;
    }

    //--------------------------------------------------------------------
    public int addAppointment(@NotNull Appointment appointment)
    {
        int index;

        Appointments.add(appointment);
        index = Appointments.size() - 1;

        fireAppointmentsAdded(index);

        return index;
    }

    //--------------------------------------------------------------------
    public void deleteResource(@NotNull Resource resource)
    {
        int index = Resources.indexOf(resource);

        //First remove the RENDER wrapper component
        fireResourcesRemoved(index);

        // Remove it from logical list
        deleteAppointmentsByResource(Resources.remove(index));
    }

    //--------------------------------------------------------------------
    private void deleteAppointmentsByResource(@NotNull Resource resource)
    {
        for (int i = 0; i < Appointments.size(); i++)
        {
            if (Appointments.get(i).getResource() == resource)
            {
                //removes the RENDER component Let any listeners know we have removed this appointment.
                fireAppointmentsRemoved(i);
            }
        }
    }

    //--------------------------------------------------------------------
    public void deleteAppointment(@NotNull Appointment appointment)
    {
        int index = Appointments.indexOf(appointment);

        //First remove the RENDER wrapper component
        fireAppointmentsRemoved(index);

        // Remove it from logical list
        Appointments.remove(index);
    }

    //--------------------------------------------------------------------
    public Resource getResourceAt(int index)
    {
        return Resources.get(index);
    }

    //--------------------------------------------------------------------
    public void setResourceAt(Resource value, int index)
    {
        if (Resources.get(index) != value)
            Resources.set(index, value);
        fireResourcesUpdated(index);
    }

    //--------------------------------------------------------------------
    public Appointment getAppointmentAt(int index)
    {
        return Appointments.get(index);
    }

    //--------------------------------------------------------------------
    public void setAppointmentAt(Appointment value, int index)
    {
        if (Appointments.get(index) != value)
            Appointments.set(index, value);
        fireAppointmentsUpdated(index);
    }

    //--------------------------------------------------------------------
    public int indexOf(Resource value)
    {
        return Resources.indexOf(value);
    }

    //--------------------------------------------------------------------
    public int indexOf(Appointment value)
    {
        return Appointments.indexOf(value);
    }

    //--------------------------------------------------------------------
    public void resetElement(@NotNull Appointment appointment)
    {
        fireAppointmentsUpdated(Appointments.indexOf(appointment));
    }

    //--------------------------------------------------------------------
    public void resetElement(@NotNull Resource resource)
    {
        fireResourcesUpdated(Resources.indexOf(resource));
    }
}
