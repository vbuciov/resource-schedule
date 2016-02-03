package com.thirdnf.resourceScheduler;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import javax.swing.BorderFactory;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.DateTime;

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
public class DaySchedule extends ScheduleView implements MouseListener
{

    private final Duration _increments;
    private ScheduleListener mouseDelegateListener = null;

    //--------------------------------------------------------------------
    /**
     * Basic constructor. This sets up the date label at the top but does not
     * create the inner panel until it has been asked to show a day.
     */
    public DaySchedule()
    {
        _increments = Duration.standardMinutes(15);
        setBackground(Color.white);
        setOpaque(true);
        setBorder(BorderFactory.createEtchedBorder());
        addMouseListener(this);
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
            LocalTime _startTime = _model.getStartTime();
            LocalTime _endTime = _model.getEndTime();

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

            Period period = _increments.toPeriod();
            

            for (LocalTime time = _startTime; time.compareTo(_endTime) < 0 ; time = time.plus(period))
            {
                Integer y = layout.getY(time);
                if (y != null)
                {
                    boolean onTheHour = time.getMinuteOfHour() == 0;

                    if (onTheHour)
                    {
                        graphics.setColor(Color.black);
                    }

                    graphics.drawLine(leftHeader, y, insets.left + width, y);

                    if (onTheHour)
                    {
                        // We want to draw hour markers and right justify them.
                        String timeString = time.toString("h:mm a");

                        Rectangle2D rect = fontMetrics.getStringBounds(timeString, graphics);
                        int stringX = (int) (leftHeader - rect.getWidth() - 10);

                        graphics.drawString(timeString, stringX, y + fontHeight / 2);
                        graphics.setColor(Color.lightGray);
                    }
                }
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
    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    //--------------------------------------------------------------------
    @Override
    public void mousePressed(MouseEvent e)
    {
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
        if (mouseDelegateListener != null && getLayout() instanceof DayScheduleLayout)
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

            LocalTime time = layout.getTime(y);

            // Now convert the time into a date time and send it to the listener
            DateTime dateTime = new DateTime(_model.getCurrentDate().getYear(), _model.getCurrentDate().getMonthOfYear(), _model.getCurrentDate().getDayOfMonth(),
                                             time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute(), 0);

            mouseDelegateListener.actionPerformed(resource, dateTime);
        }
    }

    @Override
    LayoutManager instanceNewLayout(ScheduleModel model)
    {
        return new DayScheduleLayout(model.getStartTime(), model.getEndTime());
    }
}
