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

import com.thirdnf.resourceScheduler.components.AbstractAppointmentComponent;
import com.thirdnf.resourceScheduler.components.AbstractResourceComponent;
import com.thirdnf.resourceScheduler.components.BasicComponentFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;

/**
 *
 * @author Victor Manuel Bucio Vargas - vbuciov@gmail.com
 */
public abstract class ScheduleView extends JPanel implements ScheduleModelListener, MouseListener
{
    protected ScheduleModel _model;
    protected ScheduleListener mouseDelegateListener = null;
    private BasicComponentFactory _componentFactory;

    //--------------------------------------------------------------------
    public ScheduleView()
    {
        super(null); //Whiout Layout
        _componentFactory = new BasicComponentFactory();
        _model = null;
    }

    //--------------------------------------------------------------------
    /**
     * Add a schedule listener to be notified when a user clicks anywhere in the
     * panel which is not on an appointment or resource. The values sent are the
     * "time" location of the event and the resource column. From this the
     * listener could pull up a dialog box and ask to add an appointment if they
     * wanted to.
     *
     * @param scheduleListener (not null) the listener to be notified on an
     * appointment click.
     */
    public void setScheduleListener(@NotNull ScheduleListener scheduleListener)
    {
        mouseDelegateListener = scheduleListener;
    }

    //--------------------------------------------------------------------
    /**
     * Set the model that this panel should use.
     *
     * @param model (not null) The model that should be used.
     */
    public void setModel(@NotNull ScheduleModel model)
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Cannot set a null ScheduleModel");
        }

        if (_model != model)
        {
            if (_model != null)
                _model.removeScheduleModelListener(this);
            _model = model;

            setLayout(instanceNewLayout(_model));
            scheduleChange(new ScheduleModelEvent(model, ScheduleModelEvent.ALL_ESTRUCTURE));

            // Tie into the model's notification about resource and appointment changes
            _model.addScheduleModelListener(this);
        }
    }

    //--------------------------------------------------------------------
    public ScheduleModel getModel()
    {
        return _model;
    }

    //--------------------------------------------------------------------
    /**
     * Set the date which is shown. This will trigger a complete redraw of the
     * panel.
     *
     * @param date (not null) Date to show.
     */
    public void setDate(@NotNull LocalDate date)
    {
        _model.setCurrentDate(date);

        if (!(_model instanceof AbstractScheduleModel))
            scheduleChange(new ScheduleModelEvent(_model, ScheduleModelEvent.ALL_ESTRUCTURE));
    }

    //--------------------------------------------------------------------
    /**
     * Set the component factory for drawing the components. By default it draws
     * basic components.
     *
     * @param componentFactory (not null) New component factory to use when
     * drawing appointments and resources.
     */
    public void setComponentFactory(@NotNull BasicComponentFactory componentFactory)
    {
        _componentFactory = componentFactory;
    }

    //--------------------------------------------------------------------
    /**
     * Handled the Schedule changes
     *
     * @param e The information of event
     */
    public void scheduleChange(ScheduleModelEvent e)
    {
        switch (e.getType())
        {
            case ScheduleModelEvent.ALL_ESTRUCTURE:
                onStructureChanged(e);
                break;

            case ScheduleModelEvent.INSERT_RESOURCE:
                onResourceAdded(e);
                break;

            case ScheduleModelEvent.UPDATE_RESOURCE:
                onResourceUpdated(e);
                break;

            case ScheduleModelEvent.DELETE_RESOURCE:
                onResourceRemoved(e);
                break;

            case ScheduleModelEvent.INSERT_APPOINTMENT:
                onAppointmentAdded(e);
                break;

            case ScheduleModelEvent.UPDATE_APPOINTMENT:
                onAppointmentUpdated(e);
                break;

            case ScheduleModelEvent.DELETE_APPOINTMENT:
                onAppointmentRemoved(e);
                break;
        }
    }

    //--------------------------------------------------------------------
    /**
     * Handled the Schedule Content Date changes
     *
     * @param e The information of event
     */
    public void scheduleContentChange(ScheduleModelDateEvent e)
    {
        switch (e.getType())
        {
            case ScheduleModelDateEvent.CONTENTS_CHANGED:
                onStructureChanged(e);
                break;
        }
    }

    //--------------------------------------------------------------------
    /**
     * Handled the Schedule Content Time changes
     *
     * @param e The information of event
     */
    public void scheduleContentChange(ScheduleModelTimeEvent e)
    {
        switch (e.getType())
        {
            case ScheduleModelTimeEvent.START_CHANGED:
            case ScheduleModelTimeEvent.END_CHANGED:
                onStructureChanged(e);
                break;
        }
    }

    //--------------------------------------------------------------------
    /**
     * Add a resource to the panel by wrapping it in a component and then adding
     * it to the layout.
     *
     * @param resource (not null) The resource to add.
     * @param index Location to add resource, -1 is an add
     */
    private void addWrapperResource(@NotNull Resource resource, int index)
    {
        // Get the next available column.
        if (index < 0)
            index = _model.getResourceCount();

        // Wrap the resource in a component
        AbstractResourceComponent wrapResource = _componentFactory.makeResourceComponent(resource);
        wrapResource.setInputListener(this);

        add(wrapResource, new Integer(index));
    }

    //--------------------------------------------------------------------
    /**
     * Add a resource to the panel by wrapping it in a component and then adding
     * it to the layout.
     *
     * @param resource (not null) The resource to add.
     * @param index Location to add resource, -1 is an add
     */
    private AbstractResourceComponent addWrapperAndGetResource(@NotNull Resource resource, int index)
    {
        // Get the next available column.
        if (index < 0)
            index = _model.getResourceCount();

        // Wrap the resource in a component
        AbstractResourceComponent wrapResource = _componentFactory.makeResourceComponent(resource);
        wrapResource.setInputListener(this);

        add(wrapResource, new Integer(index));

        return wrapResource;
    }

    //--------------------------------------------------------------------
    /**
     * Private method to add an appointment to the panel. This takes the
     * appointment given and wraps it in a component and adds the component to
     * the inner panel.
     *
     * @param appointment (not null) Appointment to add.
     */
    private void addWrapperAppointment(@NotNull Appointment appointment)
    {
        AbstractAppointmentComponent wrappAppointment = _componentFactory.makeAppointmentComponent(appointment);
        wrappAppointment.setInputListener(this);

        add(wrappAppointment);
    }

    //--------------------------------------------------------------------
    /**
     * Private method to add an appointment to the panel. This takes the
     * appointment given and wraps it in a component and adds the component to
     * the inner panel.
     *
     * @param appointment (not null) Appointment to add.
     */
    private AbstractAppointmentComponent addWrapperAndGetAppointment(@NotNull Appointment appointment)
    {
        AbstractAppointmentComponent wrappAppointment = _componentFactory.makeAppointmentComponent(appointment);
        wrappAppointment.setInputListener(this);

        add(wrappAppointment);

        return wrappAppointment;
    }

    //--------------------------------------------------------------------
    /**
     * Helper method to find component which represents the given resource. We
     * could (and maybe should) do this with a map, but I cringe at yet another
     * structure to hold what is essentially duplicate data. I don't think the
     * number of components will be excessive and I don't expect this to be
     * called a lot anyhow. Later we could add a map in and easily update this
     * method.
     *
     * @param resource (not null) The resource to find the component for.
     * @return (nullable) Component for this resource or null if it can't find
     * one.
     */
    @Nullable
    private AbstractResourceComponent getResourceComponent(@NotNull Resource resource)
    {
        // We have to find the one to remove ... we could use a map but I don't think there are going to be a
        //  lot so this is more straight forward
        int count = getComponentCount();

        for (int index = 0; index < count; ++index)
        {
            Component component = getComponent(index);
            if (component instanceof AbstractResourceComponent)
            {
                AbstractResourceComponent resourceComponent = (AbstractResourceComponent) component;
                if (resourceComponent.getResource().equals(resource))
                {
                    return resourceComponent;
                }
            }
        }

        return null;
    }

    //--------------------------------------------------------------------
    /**
     * Helper method to find a component which represents the given appointment.
     * See the javadoc for {@link #getResourceComponent(Resource)} for more
     * details.
     *
     * @param appointment (not null) Appointment to find the component for
     * @return (nullable) Component for the appointment or null if it could not
     * be found.
     */
    @Nullable
    private AbstractAppointmentComponent getAppointmentComponent(@NotNull Appointment appointment)
    {
        // We have to find the one to remove ... we could use a map but I don't think there are going to be a
        //  lot so this is more straight forward
        int count = getComponentCount();

        for (int index = 0; index < count; ++index)
        {
            Component component = getComponent(index);
            if (component instanceof AbstractAppointmentComponent)
            {
                AbstractAppointmentComponent appointmentComponent = (AbstractAppointmentComponent) component;
                if (appointmentComponent.getAppointment().equals(appointment))
                {
                    return appointmentComponent;
                }
            }
        }
        return null;
    }

    //---------------------------------------------------------------------
    private void onStructureChanged(ScheduleModelEvent e)
    {
        removeAll();
        _model.visitResources(new ResourceVisitor()
        {
            public boolean visitedResource(Resource resource)
            {
                addWrapperResource(resource, -1);
                return true;
            }
        }, _model.getCurrentDate());

        _model.visitAppointments(new AppointmentVisitor()
        {
            public boolean visitedAppointment(Appointment appointment)
            {
                addWrapperAppointment(appointment);
                return true;
            }
        }, _model.getInitDate()
         , _model.getEndDate());

        // Force recalculate the layout to redraw
        revalidate();
        // We have added a column so we need to repaint our background as well
        repaint();
    }

    //---------------------------------------------------------------------
    private void onStructureChanged(ScheduleModelTimeEvent e)
    {
        removeAll();
        _model.visitResources(new ResourceVisitor()
        {
            public boolean visitedResource(Resource resource)
            {
                addWrapperResource(resource, -1);
                return true;
            }
        }, _model.getCurrentDate());

        _model.visitAppointments(new AppointmentVisitor()
        {
            public boolean visitedAppointment(Appointment appointment)
            {
                addWrapperAppointment(appointment);
                return true;
            }
        }, _model.getInitDate()
         , _model.getEndDate());

        // Force recalculate the layout to redraw
        revalidate();
        // We have added a column so we need to repaint our background as well
        repaint();
    }

    //---------------------------------------------------------------------
    private void onStructureChanged(ScheduleModelDateEvent e)
    {
        removeAll();
        _model.visitResources(new ResourceVisitor()
        {
            public boolean visitedResource(Resource resource)
            {
                addWrapperResource(resource, -1);
                return true;
            }
        }, e.getNew_value());

        _model.visitAppointments(new AppointmentVisitor()
        {
            public boolean visitedAppointment(Appointment appointment)
            {
                addWrapperAppointment(appointment);
                return true;
            }
        }, _model.getInitDate()
         , _model.getEndDate());

        // Force recalculate the layout to redraw
        revalidate();
        // We have added a column so we need to repaint our background as well
        repaint();
    }

    //---------------------------------------------------------------------
    private void onResourceAdded(ScheduleModelEvent e)
    {
        AbstractResourceComponent wrapp = null;
        for (int i = e.getFirtIndex(); i <= e.getLastIndex(); i++)
        {
            wrapp = addWrapperAndGetResource(_model.getResourceAt(i), i);

            if (wrapp != null)
                wrapp.repaint();
        }
        
        revalidate();

        // We have added a column so we need to repaint our background as well
        repaint();
    }

    //---------------------------------------------------------------------
    private void onResourceRemoved(ScheduleModelEvent e)
    {
        for (int i = e.getFirtIndex(); i <= e.getLastIndex(); i++)
        {
            // First remove the component
            Component component = getResourceComponent(_model.getResourceAt(i));
            if (component != null)
            {
                remove(component);
            }
        }

        // Force the layout to redraw
        revalidate();

        // We have removed a column so we need to repaint our background as well
        repaint();
    }

    //--------------------------------------------------------------------
    private void onResourceUpdated(ScheduleModelEvent e)
    {
        for (int i = e.getFirtIndex(); i <= e.getLastIndex(); i++)
        {
            Component component = getResourceComponent(_model.getResourceAt(i));
            if (component != null)
                component.repaint();
        }

        // Force the layout to redraw
        revalidate();

        // Availability may have changed
        //repaint(); //Only the resource
    }

    //--------------------------------------------------------------------
    private void onAppointmentAdded(ScheduleModelEvent e)
    {
        AbstractAppointmentComponent wrapp;

        for (int i = e.getFirtIndex(); i <= e.getLastIndex(); i++)
        {
            Appointment theNew = _model.getAppointmentAt(i);
            
            //Draw the appointment only if is in the CurrentDate Range
            if (_model.isInCurrentDateRange(theNew))
            {
                wrapp = addWrapperAndGetAppointment(theNew);

                // Repaint to remove the old one.
                //repaint();
                /*if (wrapp != null)
                    wrapp.repaint();*/
            }
        }

        //Do layout all elemento to fix overlaps
        revalidate();
        
        repaint();
    }

    //--------------------------------------------------------------------
    private void onAppointmentRemoved(ScheduleModelEvent e)
    {
        for (int i = e.getFirtIndex(); i <= e.getLastIndex(); i++)
        {
            Component component = getAppointmentComponent(_model.getAppointmentAt(i));
            if (component != null)
            {
                remove(component);
            }
        }

        // This may cause a conflicted appointment to change sizes so invalidate everything.
        revalidate();

        // Repaint to remove the old one.
        repaint();
    }

    //--------------------------------------------------------------------
    private void onAppointmentUpdated(ScheduleModelEvent e)
    {
        Appointment actual;
        boolean repaintAll = false;
        for (int i = e.getFirtIndex(); i <= e.getLastIndex(); i++)
        {
            actual = _model.getAppointmentAt(i);
            Component component = getAppointmentComponent(actual);
            if (component != null)
            {
                if (actual.getDateTime().toLocalDate().isEqual(_model.getCurrentDate()) ||
                        actual.getDateTime().plus(actual.getDuration().toPeriod()).toLocalDate().isEqual(_model.getCurrentDate()))
                    component.repaint();

                else
                {
                    repaintAll = true;
                    remove(component);
                }
            }
        }

        // The appointment may have moved so re-layout
        revalidate();

        // Repaint to remove the old one.
        if (repaintAll)
            repaint();
    }
    
   

    //--------------------------------------------------------------------
    /**
     * Called by a print request to the scheduler. This DOES NOT WORK CURRENTLY.
     *
     * @param graphics (not null) Graphics to print to.
     * @param area (not null) Area in which to render this schedule.
     */
    public void print(@NotNull Graphics2D graphics, @NotNull Rectangle area)
    {
        Color oldColor = graphics.getColor();

        graphics.setColor(Color.white);
        graphics.fillRect(area.x, area.y, area.width, area.height);

        graphics.setColor(oldColor);
    }

    //--------------------------------------------------------------------
    abstract LayoutManager instanceNewLayout(ScheduleModel model);

    //--------------------------------------------------------------------
    abstract Appointment getSelectedAppointment();

    //--------------------------------------------------------------------
    abstract Resource getSelectedResource();
    
    //--------------------------------------------------------------------
    abstract void autoAdjusting (int column);
}
