/*
 * Created by JFormDesigner on Thu Jan 13 18:25:17 PST 2011
 */
package com.thirdnf.resourceScheduler;

import com.thirdnf.resourceScheduler.components.BasicComponentFactory;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.JPanel;

/**
 * This is the main entry point for the Scheduler. This will have methods on it
 * to determine which view is visible (day, week, month, year) and which date to
 * view.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
@SuppressWarnings(
        {
            "UnusedDeclaration"
        })
public class Scheduler extends JPanel implements Printable
{

    public static final String NotView = "NotView";
    public static final String DayView = "DayView";
    public static final String WeekView = "WeekView";
    public static final String MonthView = "MonthView";
    public static final String YearView = "YearView";
    private final DaySchedule currentScheduleView;

    /**
     * Constructor for the main scheduler panel. This is responsible for
     * deciding which view to show (day, week, month) and handing requests to
     * switch between the views.
     */
    public Scheduler()
    {
        super(new CardLayout(), true);
        currentScheduleView = new DaySchedule();
        add(currentScheduleView, DayView);
        setPreferredSize(new Dimension(500, 400));
        initEvents();
    }

    private void initEvents()
    {
        currentScheduleView.setScheduleListener(new ScheduleListener()
        {
            @Override
            public void actionPerformed(@NotNull Resource resource, @NotNull DateTime time)
            {
                // Guaranteed to return a non-null array
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length - 2; i >= 0; i -= 2)
                {
                    //noinspection ObjectEquality
                    if (listeners[i] == ScheduleListener.class)
                    {
                        ((ScheduleListener) listeners[i + 1]).actionPerformed(resource, time);
                    }
                }
            }

            public void appointmentMouseClicked(Appointment source, MouseEvent e)
            {
                // Guaranteed to return a non-null array
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length - 2; i >= 0; i -= 2)
                {
                    //noinspection ObjectEquality
                    if (listeners[i] == ScheduleListener.class)
                    {
                        ((ScheduleListener) listeners[i + 1]).appointmentMouseClicked(source, e);
                    }
                }
            }

            public void appointmentMousePressed(Appointment source, MouseEvent e)
            {
                // Guaranteed to return a non-null array
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length - 2; i >= 0; i -= 2)
                {
                    //noinspection ObjectEquality
                    if (listeners[i] == ScheduleListener.class)
                    {
                        ((ScheduleListener) listeners[i + 1]).appointmentMousePressed(source, e);
                    }
                }
            }

            public void resourceMousePressed(Resource source, MouseEvent e)
            {
                // Guaranteed to return a non-null array
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length - 2; i >= 0; i -= 2)
                {
                    //noinspection ObjectEquality
                    if (listeners[i] == ScheduleListener.class)
                    {
                        ((ScheduleListener) listeners[i + 1]).resourceMousePressed(source, e);
                    }
                }
            }

            public void resourceMouseClicked(Resource source, MouseEvent e)
            {
                  // Guaranteed to return a non-null array
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length - 2; i >= 0; i -= 2)
                {
                    //noinspection ObjectEquality
                    if (listeners[i] == ScheduleListener.class)
                    {
                        ((ScheduleListener) listeners[i + 1]).resourceMouseClicked(source, e);
                    }
                }
            }

            public void appointmentDragReleased(Appointment source, MouseEvent e)
            {
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length - 2; i >= 0; i -= 2)
                {
                    //noinspection ObjectEquality
                    if (listeners[i] == ScheduleListener.class)
                    {
                        ((ScheduleListener) listeners[i + 1]).appointmentDragReleased(source, e);
                    }
                }
            }
        });

    }

    /**
     * Set the model for the scheduler to use. This will eventually proxy this
     * request down to all panels, but for now we only have the one.
     *
     * @param model (not null) The model to use for the component.
     */
    public void setModel(@NotNull ScheduleModel model)
    {
        currentScheduleView.setModel(model);
    }

    public ScheduleModel getModel()
    {
        return currentScheduleView.getModel();
    }

    /**
     * Returns current selected Appointment
     *
     * @return the current
     */
    public Appointment getSelectedAppointment()
    {
        return currentScheduleView.getSelectedAppointment();
    }

    /**
     * Returns current selected Resource
     *
     * @return the current
     */
    public Resource getSelectedResource()
    {
        return currentScheduleView.getSelectedResource();
    }

    /**
     * Set the component factory that the scheduler should use when creating
     * appointment components or resource components. By default this will use
     * the {@link BasicComponentFactory}.
     *
     * @param componentFactory (not null) Component Factory to use.
     */
    public void setComponentFactory(@NotNull BasicComponentFactory componentFactory)
    {
        currentScheduleView.setComponentFactory(componentFactory);
    }

    /**
     * Request the scheduler show the given date. This will (eventually)
     * automatically select the day view and bring up the requested date.
     *
     * @param date (not null) Date to view.
     */
    public void showDate(@NotNull LocalDate date)
    {
        // TODO - Make sure the day view is loaded
        currentScheduleView.setDate(date);
    }

    /**
     * Add a listener to receive mouse clicks on the scheduler with the resource
     * and time clicked.
     *
     * @param scheduleListener (not null) Listener to notify.
     */
    public void addScheduleListener(@NotNull ScheduleListener scheduleListener)
    {
        listenerList.add(ScheduleListener.class, scheduleListener);
    }

    /**
     * Remove the given listener from receiving notifications on clicks.
     *
     * @param scheduleListener (not null) Listener to remove from the notify
     * list.
     */
    public void removeScheduleListener(@NotNull ScheduleListener scheduleListener)
    {
        listenerList.remove(ScheduleListener.class, scheduleListener);
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException
    {
        if (pageIndex > 0)
        {
            return NO_SUCH_PAGE;
        }

        int width = (int) pageFormat.getImageableWidth();
        int height = (int) pageFormat.getImageableHeight();

        // Create an image the full size of the paper.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D imageGraphics = bufferedImage.createGraphics();

        currentScheduleView.print(imageGraphics, new Rectangle(0, 0, width, height));

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.drawImage(bufferedImage, 0, 0, null);

        // tell the caller that this page is part of the printed document
        return PAGE_EXISTS;
    }

    public DaySchedule getCurrentView()
    {
        return currentScheduleView;
    }
    
    @Override
    public LayoutManager getLayout()
    {
        return currentScheduleView.getLayout();
    }
}
