package com.thirdnf.resourceScheduler.components;

import com.thirdnf.resourceScheduler.AbstractScheduleModel;
import com.thirdnf.resourceScheduler.Appointment;
import com.thirdnf.resourceScheduler.DayScheduleLayout;
import com.thirdnf.resourceScheduler.Resource;
import com.thirdnf.resourceScheduler.ScheduleView;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;

/**
 * This is the base class for an appointment component which are actually drawn
 * on the panel. At minimum it needs to be a JComponent but the drawing of the
 * component is entirely up to the implementation.
 *
 * @see BasicAppointmentComponent
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
public abstract class AbstractAppointmentComponent extends JComponent
{

    // The appointment this component is wrapping.
    private MouseInputListener resizeListener;
    private int cursor;
    protected final Appointment _appointment;

    private int amountPixels;
    private Point startPoint = null;
    private boolean dragged;

    /**
     * Constructor given an appointment to wrap.
     *
     * @param appointment (not null) The appointment to wrap
     */
    protected AbstractAppointmentComponent(@NotNull Appointment appointment)
    {
        _appointment = appointment;
        initEvents();
        setBorder(new ResizableBorder(8));
        amountPixels = 15;//30 Seconds
        dragged = false;
    }

    private void initEvents()
    {
        resizeListener = new MouseInputAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent me)
            {
                Appointment_mouseMoved(me);
            }

            @Override
            public void mousePressed(MouseEvent me)
            {
                Appointment_mousePressed(me);
            }

            @Override
            public void mouseDragged(MouseEvent me)
            {
                dragged = true;
                Appointment_mouseDragged(me);
            }

            @Override
            public void mouseReleased(MouseEvent me)
            {
                Appointment_mouseReleased(me);
                dragged = false;
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                if (!isDragged())
                    setCursor(Cursor.getDefaultCursor());
            }

        };

        addMouseListener(resizeListener);
        addMouseMotionListener(resizeListener);
    }

    //--------------------------------------------------------------------
    private void Appointment_mouseMoved(MouseEvent me)
    {
        if (hasFocus())
        {
            ResizableBorder border = (ResizableBorder) getBorder();
            setCursor(Cursor.getPredefinedCursor(border.getCursor(me)));
        }
    }

    //--------------------------------------------------------------------
    private void Appointment_mousePressed(MouseEvent me)
    {
        ResizableBorder border = (ResizableBorder) getBorder();
        cursor = border.getCursor(me);
        startPoint = me.getPoint();
        setCursor(Cursor.getPredefinedCursor(cursor));
        requestFocus();
        repaint();

        //My Container is a ScheduleView, so requieres repaint.
        if (getParent() != null)
        {
            JComponent container = (JComponent) getParent();
            //container.revalidate(); Not recalculate positions by Layout
            container.repaint();
        }
    }

    //--------------------------------------------------------------------
    private void Appointment_mouseDragged(MouseEvent me)
    {
        if (startPoint != null)
        {
            JComponent container = (JComponent) getParent();
            if (container.getLayout() instanceof DayScheduleLayout)
            {
                DayScheduleLayout layout = (DayScheduleLayout) container.getLayout();
                int x = getX();
                int y = getY();
                int w = getWidth();
                int h = getHeight();

                int dx = me.getX() - startPoint.x;
                int dy = me.getY() - startPoint.y;

                switch (cursor)
                {
                    case Cursor.N_RESIZE_CURSOR:
                        if (!(h - dy < amountPixels) && y + dy > layout.getTopHeader())
                            setBounds(x, y + dy, w, h - dy);
                        break;

                    case Cursor.S_RESIZE_CURSOR:
                        if (!(h + dy < amountPixels) && y + h + dy < container.getHeight())
                        {
                            setBounds(x, y, w, h + dy);
                            startPoint = me.getPoint();
                        }
                        break;

                    case Cursor.W_RESIZE_CURSOR:
                        if (!(w - dx < amountPixels))
                        {
                            //setBounds(x + dx, y, w - dx, h); is not posible
                        }
                        break;

                    case Cursor.E_RESIZE_CURSOR:
                        if (!(w + dx < amountPixels))
                        {
                            /* setBounds(x, y, w + dx, h);
                             startPoint = me.getPoint(); is not posible*/
                        }
                        break;

                    case Cursor.NW_RESIZE_CURSOR:
                        if (!(w - dx < amountPixels) && !(h - dy < amountPixels))
                        {
                            // setBounds(x + dx, y + dy, w - dx, h - dy); is not posible
                        }
                        break;

                    case Cursor.NE_RESIZE_CURSOR:
                        if (!(w + dx < amountPixels) && !(h - dy < amountPixels))
                        {
                            /*setBounds(x, y + dy, w + dx, h - dy);
                             startPoint = new Point(me.getX(), startPoint.y); is not posible*/
                        }
                        break;

                    case Cursor.SW_RESIZE_CURSOR:
                        if (!(w - dx < amountPixels) && !(h + dy < amountPixels))
                        {
                            /*setBounds(x + dx, y, w - dx, h + dy);
                             startPoint = new Point(startPoint.x, me.getY()); is not posible*/
                        }
                        break;

                    case Cursor.SE_RESIZE_CURSOR:
                        if (!(w + dx < amountPixels) && !(h + dy < amountPixels))
                        {
                            /*setBounds(x, y, w + dx, h + dy);
                             startPoint = me.getPoint();is not posible*/
                        }
                        break;

                    case Cursor.MOVE_CURSOR:
                        if (y + dy + 1 > layout.getTopHeader() + container.getInsets().top && y + h + dy < container.getHeight())
                        {
                            Rectangle bounds = getBounds();
                            bounds.translate(dx, dy);
                            setBounds(bounds);
                        }
                        break;
                }
            }
        }
    }

    //--------------------------------------------------------------------
    private void Appointment_mouseReleased(MouseEvent me)
    {
        if (startPoint != null && dragged)
        {
            ScheduleView container = (ScheduleView) getParent();
            int x = getX();
            int y = getY();
            //int w = getWidth();
            int h = getHeight();

            switch (cursor)
            {
                case Cursor.N_RESIZE_CURSOR:
                    componentResizedNorth(y, container);
                    break;

                case Cursor.S_RESIZE_CURSOR:
                    componentResizedSouth(y + h, container);
                    break;

                case Cursor.W_RESIZE_CURSOR:
                    /* TODO */
                    break;

                case Cursor.E_RESIZE_CURSOR:
                    /* TODO */
                    break;

                case Cursor.NW_RESIZE_CURSOR:
                    /* TODO */
                    break;

                case Cursor.NE_RESIZE_CURSOR:
                    /* TODO */
                    break;

                case Cursor.SW_RESIZE_CURSOR:
                    /* TODO */
                    break;

                case Cursor.SE_RESIZE_CURSOR:
                    /* TODO */
                    break;

                case Cursor.MOVE_CURSOR:
                    componentMoved(x, y, container);
                    break;
            }

            //container.revalidate(); The container going to revalidate, when updated notify
        }

        startPoint = null;
    }

    //--------------------------------------------------------------------
    public void componentMoved(int x, int y, ScheduleView container)
    {
        DayScheduleLayout layout = (DayScheduleLayout) container.getLayout();
        
        Resource resource = null;
        int columns = layout.getColumnCount();
        int x1 = layout.getX(0);

        //While not found a resource
        for (int i = 0; i < columns && resource == null; ++i)
        {
            int x2 = layout.getX(i + 1);
            if (x > x1 && x < x2)
                resource = layout.getResource(i);
            x1 = x2;
        }

        //Change of resource
        if (resource != null && _appointment.getResource() != resource)
            _appointment.setResource(resource);

        LocalTime start_time = layout.getTime(y);

        _appointment.setDateTime(new LocalDateTime(_appointment.getDateTime().getYear(),
                                                   _appointment.getDateTime().getMonthOfYear(),
                                                   _appointment.getDateTime().getDayOfMonth(),
                                                   start_time.getHourOfDay(),
                                                   start_time.getMinuteOfHour(),
                                                   start_time.getSecondOfMinute(), 0));
        
        if (container.getModel() instanceof AbstractScheduleModel)
            ((AbstractScheduleModel)container.getModel()).fireAppointmentsUpdated(container.getModel().indexOf(_appointment));
    }

    //--------------------------------------------------------------------
    public void componentResizedNorth(int y, ScheduleView container)
    {
        DayScheduleLayout layout = (DayScheduleLayout) container.getLayout();
        LocalTime start_time = layout.getTime(y);
        LocalDateTime end_time = _appointment.getDateTime().plus(_appointment.getDuration().toPeriod());

        _appointment.setDateTime(new LocalDateTime(_appointment.getDateTime().getYear(),
                                                   _appointment.getDateTime().getMonthOfYear(),
                                                   _appointment.getDateTime().getDayOfMonth(),
                                                   start_time.getHourOfDay(),
                                                   start_time.getMinuteOfHour(),
                                                   start_time.getSecondOfMinute()));
        _appointment.setDuration(Period.fieldDifference(start_time, end_time.toLocalTime()).toStandardDuration());
        
        if (container.getModel() instanceof AbstractScheduleModel)
            ((AbstractScheduleModel)container.getModel()).fireAppointmentsUpdated(container.getModel().indexOf(_appointment));

    }

    //--------------------------------------------------------------------
    public void componentResizedSouth(int y, ScheduleView container)
    {
        DayScheduleLayout layout = (DayScheduleLayout) container.getLayout();
        LocalTime start_time = _appointment.getDateTime().toLocalTime();
        LocalTime end_time = layout.getTime(y);

        _appointment.setDateTime(new LocalDateTime(_appointment.getDateTime().getYear(),
                                                   _appointment.getDateTime().getMonthOfYear(),
                                                   _appointment.getDateTime().getDayOfMonth(),
                                                   start_time.getHourOfDay(),
                                                   start_time.getMinuteOfHour(),
                                                   start_time.getSecondOfMinute()));
        _appointment.setDuration(Period.fieldDifference(start_time, end_time).toStandardDuration());
        
        if (container.getModel() instanceof AbstractScheduleModel)
            ((AbstractScheduleModel)container.getModel()).fireAppointmentsUpdated(container.getModel().indexOf(_appointment));
    }

    /**
     * Get the appointment from the component.
     *
     * @return (not null) The appointment this component was wrapping.
     */
    @NotNull
    public Appointment getAppointment()
    {
        return _appointment;
    }

    /**
     * Print this component into the given area using the graphics handle.
     *
     * @param graphics (not null) Graphics handle to draw with
     * @param area (not null) Area for printing
     */
    public abstract void print(@NotNull Graphics2D graphics, Rectangle area);

    public boolean isDragged()
    {
        return dragged;
    }

}
