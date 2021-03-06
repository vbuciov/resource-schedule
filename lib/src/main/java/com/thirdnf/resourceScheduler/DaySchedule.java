package com.thirdnf.resourceScheduler;

import com.thirdnf.resourceScheduler.components.AbstractAppointmentComponent;
import com.thirdnf.resourceScheduler.components.AbstractResourceComponent;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Panel to show a given day.
 *
 * I'm still working on exactly where the api line here should be drawn between
 * the panel and the layout, but my thinking is that the panel knows of the
 * model but the layout does not. The layout only knows of its size and the
 * components it has been asked to draw. I realize that seems sort of obvious,
 * but hindsight is 20/20.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
public class DaySchedule extends ScheduleView
{

    public static final Duration INCREMENTS = Duration.standardMinutes(15);

    private Appointment selectedAppointment;
    private Resource selectedResource;
    private final Comparator<Appointment> byStartDate;

    //--------------------------------------------------------------------
    /**
     * Basic constructor. This sets up the date label at the top but does not
     * create the inner panel until it has been asked to show a day.
     */
    public DaySchedule()
    {
        setBackground(Color.white);
        setOpaque(true);
        setBorder(BorderFactory.createEtchedBorder());
        addMouseListener(this);
        byStartDate = new Comparator<Appointment>()
        {
            public int compare(Appointment o1, Appointment o2)
            {
                /*int byfinish, bystart;
                
                byfinish = o1.getDateTime().plus(o1.getDuration()).compareTo(o2.getDateTime().plus(o2.getDuration()));
                bystart = o1.getDateTime().compareTo(o2.getDateTime());
                
                return  byfinish > 0 && bystart > 0? 1: byfinish < 0 && bystart < 0? -1: bystart > 0? 1: byfinish > 0? 1: bystart <0 ? -1 : byfinish< 0?-1:0;*/
                
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        };
    }

    //--------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g); //Paint the background according to panel.

        if (_model != null && getLayout() instanceof DayScheduleLayout)
        {
            Graphics2D graphics = (Graphics2D) g;
            Color oldColor = graphics.getColor(); //Save the original value
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Insets insets = getInsets();
            int width = getWidth() - insets.left - insets.right;
            int height = getHeight() - insets.top - insets.bottom;
            FontMetrics fontMetrics = getFontMetrics(getFont());
            int fontHeight = fontMetrics.getHeight();
            DayScheduleLayout layout = (DayScheduleLayout) getLayout();
            int leftHeader = layout.getX(0);
            int columnCount = layout.getColumnCount();
            LocalDateTime _startTime = _model.getInitDate();
            LocalDateTime _endTime = _model.getEndDate();

            // Color in times which they are not available.  This goes through and basically draws from the
            //  top to the first appointment, then from that appointment to the next and so forth and so on
            //  at the end it then draws from the last appointment to the bottom.
            graphics.setColor(Color.lightGray);
            for (int i = 0; i < columnCount; ++i)
            {
                int y1 = layout.getTopHeader();
                int x1 = layout.getX(i);
                int x2 = layout.getX(i + 1);

                Resource resource = layout.getResource(i);
                Iterator<Availability> iterator = resource.getAvailability(_model.getCurrentDate());
                while (iterator.hasNext())
                {
                    Availability availability = iterator.next();
                    // Color this availability white
                    LocalTime startTime = availability.getTime();
                    Duration duration = availability.getDuration();
                    int y2 = layout.getY(startTime);

                    if (y2 > y1)
                        graphics.fillRect(x1, y1, x2 - x1, y2 - y1);

                    LocalTime endTime = startTime.plus(duration.toPeriod());
                    y1 = layout.getY(endTime);
                }

                int y2 = insets.top + height;
                if (y2 > y1)
                {
                    graphics.fillRect(x1, y1, x2 - x1, y2 - y1);
                }
            }

            Period period = INCREMENTS.toPeriod();
            boolean back2AM = false;

            for (LocalDateTime time = _startTime; time.compareTo(_endTime) <= 0; time = time.plus(period))
            {
                Integer y = layout.getY(time.toLocalTime());
                if (y != null)
                {
                    boolean onTheHour = time.getMinuteOfHour() == 0;

                    if (onTheHour)
                    {
                        graphics.setColor(Color.black);
                        // We want to draw hour markers and right justify them.
                        String timeString = time.toString("h:mm a");

                        Rectangle2D rect = fontMetrics.getStringBounds(timeString, graphics);
                        int stringX = (int) (leftHeader - rect.getWidth() - 10);

                        // graphics.drawString(timeString, stringX, y + fontHeight / 2);
                        graphics.drawString(timeString, stringX, y + fontHeight - insets.top);

                    }

                    else if (back2AM)
                        graphics.setColor(Color.black);

                    else
                        graphics.setColor(Color.lightGray);

                    graphics.drawLine(leftHeader, y, insets.left + width, y);
                }

                if (!back2AM)
                {
                    //Only when the final period is 23, can back to origin time
                    if (time.plus(period).toLocalTime().compareTo(LocalTime.MIDNIGHT) == 0)
                    {
                        back2AM = true;
                        period = Period.fieldDifference(time, _endTime);
                    }
                }

                else
                    break;
            }

            // Finally draw the column lines over everything
            graphics.setColor(Color.black);
            for (int i = 0; i < columnCount; ++i)
            {
                int x = layout.getX(i);
                graphics.drawLine(x, insets.top, x, insets.top + height);
            }

            // Reset the graphics, to original values
            graphics.setColor(oldColor);
            graphics.setPaintMode();
        }

    }

    //--------------------------------------------------------------------
    /**
     * Handled the Schedule Content Time changes
     *
     * @param e The information of event
     */
    @Override
    public void scheduleContentChange(ScheduleModelTimeEvent e)
    {
        //Reconfigure Layout Range.
        if (getLayout() instanceof DayScheduleLayout)
        {
            DayScheduleLayout layout = (DayScheduleLayout) getLayout();

            switch (e.getType())
            {
                case ScheduleModelTimeEvent.START_CHANGED:
                    layout.setStartTime(_model.getInitDate());
                    break;

                case ScheduleModelTimeEvent.END_CHANGED:
                    layout.setEndTime(_model.getEndDate());
                    break;
            }

        }

        super.scheduleContentChange(e);
    }

    //--------------------------------------------------------------------
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getSource() instanceof AbstractAppointmentComponent)
        {
            mouseDelegateListener.appointmentMouseClicked(((AbstractAppointmentComponent) e.getSource()).getAppointment(), e);
        }

        else if (e.getSource() instanceof AbstractResourceComponent)
        {
            selectedResource = ((AbstractResourceComponent) e.getSource()).getResource();
            mouseDelegateListener.resourceMouseClicked(selectedResource, e);
        }
    }

    //--------------------------------------------------------------------
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getSource() instanceof AbstractAppointmentComponent)
        {
            repaint(); //Is Selected new element, we have to repaint
            selectedAppointment = ((AbstractAppointmentComponent) e.getSource()).getAppointment();
            selectedResource = selectedAppointment.getResource();
            mouseDelegateListener.appointmentMousePressed(selectedAppointment, e);
        }

        else if (e.getSource() instanceof AbstractResourceComponent)
        {
            selectedResource = ((AbstractResourceComponent) e.getSource()).getResource();
            mouseDelegateListener.resourceMousePressed(selectedResource, e);
        }
    }

    //--------------------------------------------------------------------
    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    //--------------------------------------------------------------------
    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    //--------------------------------------------------------------------
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (mouseDelegateListener != null)
        {
            if (e.getSource() instanceof AbstractAppointmentComponent)
            {
                mouseDelegateListener.appointmentDragReleased(((AbstractAppointmentComponent) e.getSource()).getAppointment(), e);
            }

            else if (getLayout() instanceof DayScheduleLayout)
            {
                int x = e.getX();
                int y = e.getY();

                DayScheduleLayout layout = (DayScheduleLayout) getLayout();

                // Determine the resource clicked
                Resource resource = null;
                int columns = layout.getColumnCount();
                int x1 = layout.getX(0);

                for (int i = 0; i < columns; ++i)
                {
                    int x2 = layout.getX(i + 1);
                    if (x > x1 && x < x2)
                    {
                        resource = layout.getResource(i);
                    }
                    x1 = x2;
                }

                if (resource == null)
                {
                    return;
                }

                LocalDateTime time = layout.getTime(y);

                // Now convert the time into a date time and send it to the listener
                DateTime dateTime = new DateTime(_model.getCurrentDate().getYear(), _model.getCurrentDate().getMonthOfYear(), _model.getCurrentDate().getDayOfMonth(),
                                                 time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute(), 0);

                mouseDelegateListener.actionPerformed(resource, dateTime);
            }
        }
    }

    //--------------------------------------------------------------------
    public Appointment getSelectedAppointment()
    {
        return selectedAppointment;
    }

    //--------------------------------------------------------------------
    public Resource getSelectedResource()
    {
        return selectedResource;
    }

    //--------------------------------------------------------------------
    @Override
    public void setDate(LocalDate value)
    {
        super.setDate(value);

        if (getLayout() instanceof DayScheduleLayout)
        {
            DayScheduleLayout layout = (DayScheduleLayout) getLayout();
            layout.setRangeTime(_model.getInitDate(), _model.getEndDate());
        }

    }

    //--------------------------------------------------------------------
    @Override
    LayoutManager instanceNewLayout(ScheduleModel model)
    {
        return new DayScheduleLayout(model.getInitDate(), model.getEndDate());
    }

    //--------------------------------------------------------------------
    /**
     *
     * @param column
     */
    public void autoAdjusting(int column)
    {
        if (column >= 0 && column < _model.getResourceCount())
        {
            Resource selected = _model.getResourceAt(column);
            List<Appointment> appointments = new ArrayList<Appointment>();
            Appointment current;
            LocalDateTime init_adjust = _model.getInitDate();

            //1.- 
            for (int i = 0; i < _model.getAppoitmentCount(); i++)
            {
                current = _model.getAppointmentAt(i);
                if (current.getResource() == selected && _model.isInCurrentDateRange(current))
                    appointments.add(current);
            }

            //2.- 
            appointments.sort(byStartDate);
            
            //3.- 
            for (int j = 0; j < appointments.size(); j++)
            {
                current = appointments.get(j);
                
                if (current.getDateTime().compareTo(_model.getInitDate()) > 0)
                    current.setDateTime(init_adjust);
                
                init_adjust = current.getDateTime().plus(current.getDuration()).plusMinutes(1);
                
                //If there's listener interesting in changes, we notify but don't repaint yet
                if (_model instanceof AbstractScheduleModel)
                    ((AbstractScheduleModel)_model).justNotifyAppointmentUpdated(current);
            }
            
            
            //So many appointments change, so we need to repaint.
            revalidate();
            
            repaint();
        }
    }
}
