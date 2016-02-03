package com.thirdnf.resourceScheduler;

import com.thirdnf.resourceScheduler.components.AbstractAppointmentComponent;
import com.thirdnf.resourceScheduler.components.AbstractResourceComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * SchedulerLayout is responsible for laying out the components on a time grid.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
@SuppressWarnings(
        {
            "UnusedDeclaration"
        })
public class DayScheduleLayout implements LayoutManager2
{

    // Location map telling us which column each appointment belongs to.
    private List<Resource> _resources = new ArrayList<Resource>();

    // Space to give to the top header.
    private int _topHeader = 25;
    private int _leftHeader = 75;

    private final LocalTime _startTime;

    private final long _totalSeconds;

    /**
     * This is the horizontal gap (in pixels) which specifies the space between
     * columns. They can be changed at any time. This should be a non-negative
     * integer.
     *
     * @serial
     * @see #getHgap()
     * @see #setHgap(int)
     */
    int hgap;
    /**
     * This is the vertical gap (in pixels) which specifies the space between
     * rows. They can be changed at any time. This should be a non negative
     * integer.
     *
     * @serial
     * @see #getVgap()
     * @see #setVgap(int)
     */
    int vgap;

    // X offset location, equal to the inset.left
    private int _x;

    // Y offset location, equal to the inset.top
    private int _y;

    // Our scaling factor assuming 1:1 means one pixel == one second
    private float _scale;

    private float _columnWidth = 0;

    /**
     * Constructor. So far the only thing that must be provided is the
     * increments to use for the layout. I think changing this would require a
     * nearly full re-layout.
     *
     * @param startTime (not null) Time of the day to start.
     * @param endTime (not null) Time of the day to end.
     */
    public DayScheduleLayout(@NotNull LocalTime startTime, @NotNull LocalTime endTime)
    {
        _startTime = startTime;

        // Total number of seconds we are representing
        _totalSeconds = Period.fieldDifference(startTime, endTime).toStandardDuration().getStandardSeconds();

        // Start with a 1:1 scale
        _scale = 1.0f;

        hgap = 0;
        vgap = 0;
    }

    /**
     * Set the top header size to the given number of pixels.
     *
     * @param pixels Number of pixels for the top header
     */
    public void setTopHeader(int pixels)
    {
        _topHeader = pixels;

        // TODO - force a recalculate or re-layout
    }

    /**
     * Get the top header. This is the space from the top of the panel to where
     * the header starts.
     *
     * @return Top Header
     */
    public int getTopHeader()
    {
        return _topHeader;
    }

    /**
     * Set the left header size to the given number of pixels.
     *
     * @param pixels Number of pixels for the left header
     */
    public void setLeftHeader(int pixels)
    {
        _leftHeader = pixels;

        // TODO - force a recalculate or re-layout
    }

    /**
     * Returns the horizontal gap between components.
     *
     * @return The value of horizontal gap
     */
    public int getHgap()
    {
        return hgap;
    }

    /**
     * Sets the horizontal gap between components.
     *
     * @param hgap the horizontal gap between components
     */
    public void setHgap(int hgap)
    {
        this.hgap = hgap;
    }

    /**
     * Returns the vertical gap between components.
     *
     * @return The value of vertical gap
     */
    public int getVgap()
    {
        return vgap;
    }

    /**
     * Sets the vertical gap between components.
     *
     * @param vgap the vertical gap between components
     */
    public void setVgap(int vgap)
    {
        this.vgap = vgap;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager has cached
     * information it should be discarded.
     */
    @Override
    public void invalidateLayout(Container target)
    {
    }

    @Override
    public void addLayoutComponent(@NotNull Component comp, @Nullable Object constraints)
    {
        // Appointments are added without constraints
        if (comp instanceof AbstractAppointmentComponent && constraints != null)
        {
            throw new IllegalArgumentException("Constraints for appointments must be null.");
        }

        //Resources are added with constraint index (specify a column)
        if (comp instanceof AbstractResourceComponent)
        {
            AbstractResourceComponent resourceComponent = (AbstractResourceComponent) comp;

            int column = -1;
            if (constraints instanceof Integer)
                column = (Integer) constraints;

            // An invalid index so default to an add
            if (column >= 0 && column < _resources.size())
                _resources.add(column, resourceComponent.getResource());

            // This may be an insert
            else
                _resources.add(resourceComponent.getResource());
        }
    }

    @Deprecated
    public void addLayoutComponent(String name, Component comp)
    {
        // Not used by this class
    }

    @Override
    public void removeLayoutComponent(Component comp)
    {
        // In addition to removing the component we need to also remove the entry from the resource list
        //  if this is a resource.
        if (comp instanceof AbstractResourceComponent)
        {
            AbstractResourceComponent resourceComponent = (AbstractResourceComponent) comp;
            _resources.remove(resourceComponent.getResource());
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent)
    {
        // TODO - Calculate this for real
        //return new Dimension(400, 300);
        synchronized (parent.getTreeLock())
        {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = (int) Math.ceil(1 / _scale); //rows;
            int ncols = getColumnCount(); //cols;

            if (nrows > 0)
            {
                ncols = (ncomponents + nrows - 1) / nrows;
            }
            else
            {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            int w = 0;
            int h = 0;
            for (int i = 0; i < ncomponents; i++)
            {
                Component comp = parent.getComponent(i);
                Dimension d = comp.getPreferredSize();
                if (w < d.width)
                {
                    w = d.width;
                }
                if (h < d.height)
                {
                    h = d.height;
                }
            }
            return new Dimension(insets.left + insets.right + ncols * w + (ncols - 1) * hgap,
                                 insets.top + insets.bottom + nrows * h + (nrows - 1) * vgap);
        }

        /*synchronized (parent.getTreeLock())
         {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         int w = 0;
         int h = 0;

         for (int i = 0; i < ncomponents; i++)
         {
         Component comp = parent.getComponent(i);
         Dimension d = comp.getPreferredSize();
         if (d.width > w)
         {
         w = d.width;
         }
         if (d.height > h)
         {
         h = d.height;
         }
         }
         return new Dimension(insets.left + insets.right + w + hgap * 2,
         insets.top + insets.bottom + h + vgap * 2);
         }*/
    }

    @Override
    public Dimension minimumLayoutSize(Container parent)
    {
        // TODO - Calculate this off the minimum height for the fonts and some minimum width
        // return new Dimension(300, 300);
        synchronized (parent.getTreeLock())
        {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = (int) Math.ceil(1 / _scale); //rows;
            int ncols = getColumnCount(); //cols;

            if (nrows > 0)
            {
                ncols = (ncomponents + nrows - 1) / nrows;
            }
            else
            {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            int w = 0;
            int h = 0;
            for (int i = 0; i < ncomponents; i++)
            {
                Component comp = parent.getComponent(i);
                Dimension d = comp.getMinimumSize();
                if (w < d.width)
                {
                    w = d.width;
                }
                if (h < d.height)
                {
                    h = d.height;
                }
            }
            return new Dimension(insets.left + insets.right + ncols * w + (ncols - 1) * hgap,
                                 insets.top + insets.bottom + nrows * h + (nrows - 1) * vgap);
        }

        /*synchronized (parent.getTreeLock())
         {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         int w = 0;
         int h = 0;

         for (int i = 0; i < ncomponents; i++)
         {
         Component comp = parent.getComponent(i);
         Dimension d = comp.getMinimumSize();
         if (d.width > w)
         {
         w = d.width;
         }
         if (d.height > h)
         {
         h = d.height;
         }
         }
         return new Dimension(insets.left + insets.right + w + hgap * 2,
         insets.top + insets.bottom + h + vgap * 2);
         }*/
    }

    @Override
    public Dimension maximumLayoutSize(Container target)
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target)
    {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target)
    {
        return 0.5f;
    }

    @Override
    public void layoutContainer(Container target)
    {
        synchronized (target.getTreeLock())
        {
            int columnCount = recomputeLayout(target);

            // In order to do intersection checking in the column we need to sort the appointments by their
            //  column first.  The suppress warnings is here due to a bug/feature in java that can't deal
            //  with generic array creation.
            @SuppressWarnings(
                    {
                        "unchecked"
                    })
            List<AbstractAppointmentComponent>[] columns = (List<AbstractAppointmentComponent>[]) new List[columnCount];

            int componentCount = target.getComponentCount();
            for (int i = 0; i < componentCount; ++i)
            {
                Component component = target.getComponent(i);
                if (!component.isVisible())
                {
                    continue;
                }

                if (component instanceof AbstractResourceComponent)
                {
                    AbstractResourceComponent resourceComponent = (AbstractResourceComponent) component;
                    Resource resource = resourceComponent.getResource();

                    // These are placed at the top of their row
                    int column = _resources.indexOf(resource);

                    int x = _x + _leftHeader + (int) (_columnWidth * column);

                    int width = (int) _columnWidth;

                    // One rowHeight is one increment
                    int height = _topHeader;

                    component.setBounds(x, _y, width, height);
                }

                else if (component instanceof AbstractAppointmentComponent)
                {
                    AbstractAppointmentComponent appointmentComponent = (AbstractAppointmentComponent) component;

                    //Is dragged the component?
                    if (!appointmentComponent.isDragged())
                    {
                        Appointment appointment = appointmentComponent.getAppointment();
                        Duration duration = appointment.getDuration();
                        LocalTime time = appointment.getDateTime().toLocalTime();
                        int y = getY(time);
                        int width = (int) _columnWidth;
                        int height = (int) Math.floor(_scale * duration.getStandardSeconds());

                        Resource resource = appointment.getResource();
                        int column = resource == null ? -1 : _resources.indexOf(resource);

                        // Give them to column 0 .... I can't think of something better to do
                        //  at the moment.
                        if (column == -1)
                            column = 0;

                        int x = getX(column);

                        appointmentComponent.setBounds(x + 8, y, width - 16, height);

                        if (columns[column] == null)
                            columns[column] = new ArrayList<AbstractAppointmentComponent>();
                        columns[column].add(appointmentComponent);
                    }
                }
                else
                {
                    System.out.println("Don't know how to layout component: " + component);
                }
            }

            // Now, fix up the appointments in each column
            for (List<AbstractAppointmentComponent> list : columns)
            {
                if (list != null)
                {
                    fixOverlaps(list);
                }
            }
        }
    }

    /**
     * This is under construction so I would not suggest using it. It asks the
     * layout to paint the components on the given graphics. The layout knows
     * how to layout the appointments so it seems like the best candidate for
     * drawing this.
     *
     * @param target (not null) The component which has all the real components
     * added to it.
     * @param graphics (not null) The graphics reference to draw to.
     * @param area (not null) The area in the graphics this method owns and can
     * draw to.
     */
    public void print(@NotNull Component target, @NotNull Graphics2D graphics, @NotNull Rectangle area)
    {
        System.out.println("DaySchedule layout for area");
    }

    /**
     * This is an internal method to go through and fix up any appointments so
     * they don't draw on top of each other.
     * <p>
     * This method is not complete, right now it doesn't do a good job if the
     * overlap gets complicated. It won't draw them on top of each other, but it
     * doesn't make optimal use of space and the algorithm seems overly
     * complicated.
     *
     * @param appointments (not null) List of appointments to fix.
     */
    private void fixOverlaps(@NotNull List<AbstractAppointmentComponent> appointments)
    {
        // Run through each shape and create a list of all other shapes which intersect it, this can also
        //  be used later.
        Map<AbstractAppointmentComponent, List<AbstractAppointmentComponent>> hitMap = new HashMap<AbstractAppointmentComponent, List<AbstractAppointmentComponent>>();
        Map<AbstractAppointmentComponent, Integer> columnMap = new HashMap<AbstractAppointmentComponent, Integer>();

        for (AbstractAppointmentComponent appointment : appointments)
        {
            Rectangle rectangle = appointment.getBounds();
            List<AbstractAppointmentComponent> list = null;

            // Check if this guy intersects any other one in this list
            for (AbstractAppointmentComponent checkComponent : appointments)
            {
                if (appointment == checkComponent)
                {
                    // Skip the self check
                    continue;
                }

                Rectangle checkRectangle = checkComponent.getBounds();

                if (rectangle.intersects(checkRectangle))
                {
                    if (list == null)
                    {
                        list = new ArrayList<AbstractAppointmentComponent>();
                    }
                    list.add(checkComponent);
                }
            }

            if (list != null)
            {
                hitMap.put(appointment, list);
            }
        }

        int maxColumn = 0;
        for (AbstractAppointmentComponent appointment : appointments)
        {
            List<AbstractAppointmentComponent> list = hitMap.get(appointment);

            if (list == null)
            {
                // No conflicts, it can keep its full size
                continue;
            }

            // Find a free column
            int column = 0;
            boolean done = false;
            while (!done)
            {
                done = true;

                for (AbstractAppointmentComponent checkComponent : list)
                {
                    Integer testColumn = columnMap.get(checkComponent);

                    if (testColumn != null && testColumn == column)
                    {
                        // This column is taken
                        column += 1;
                        done = false;

                        maxColumn = Math.max(maxColumn, column);
                    }
                }
            }
            columnMap.put(appointment, column);
        }

        if (maxColumn == 0)
        {
            return;
        }

        // Now adjust the sizes
        for (AbstractAppointmentComponent appointment : appointments)
        {
            Integer column = columnMap.get(appointment);
            if (column == null)
            {
                continue;
            }
            Rectangle rectangle = appointment.getBounds();
            float width = ((float) (rectangle.width)) / (float) (maxColumn + 1);
            int x = rectangle.x + (int) (column * width);

            appointment.setBounds(x, rectangle.y, (int) width, rectangle.height);
        }
    }

    /**
     * Internal method to recompute how wide the columns and rows should be.
     * This is usually only called after a panel resize or on startup.
     *
     * @param target (not null) the containing panel.
     * @return Number of columns
     */
    private int recomputeLayout(@NotNull Container target)
    {
        // Cache the number of columns ... this seems sort of stupid
        int columns = _resources.size();
        if (columns == 0)
            columns = 1;

        Insets insets = target.getInsets();
        _x = insets.left;
        _y = insets.top;

        int width = target.getWidth() - insets.left - insets.right;
        int height = target.getHeight() - insets.top - insets.bottom;

        _scale = (float) (height - _topHeader) / (float) _totalSeconds;
        _columnWidth = (float) (width - _leftHeader) / (float) columns;

        return columns;
    }

    /**
     * Public method to ask the layout where a given time location should be
     * placed. I'm not 100% sure if this breaks the abstraction of the layout by
     * exposing this. To access this the caller needs to get the layout and then
     * cast it to a ScheduleLayout. Doesn't seem ideal.
     *
     * @param time (not null) Time in question to ask for the y location of.
     * @return The y location on the current panel for the given time.
     */
    public int getY(@NotNull LocalTime time)
    {
        // Get the seconds which have passed from the start time to the time they are asking about.
        long seconds = Period.fieldDifference(_startTime, time).toStandardDuration().getStandardSeconds();

        return _y + _topHeader + (int) (_scale * (float) seconds);
    }

    /**
     * Translate a y position back into a time offset.
     * <p>
     * There seems to be a bit of an error here by like one pixel that I'm not
     * sure the reason for.
     *
     * @param y Value to translate into a time offset.
     * @return (not null) Time representing the given y
     */
    public LocalTime getTime(int y)
    {
        // Turn the y into seconds
        int seconds = Math.round((float) (y - (_topHeader + _y)) / _scale);

        // Turn seconds into a time after start time
        return _startTime.plus(Duration.standardSeconds(seconds).toPeriod());
    }

    public float getScale()
    {
        return _scale;
    }

    /**
     * Get the x location in the current panel for the given column id. This
     * gives the x location of the columns left hand side. Use (column + 1) to
     * find the right hand location.
     *
     * @param column The column to get the x value for.
     * @return The x location of the column start position.
     */
    public int getX(int column)
    {
        return _x + _leftHeader + (int) (column * _columnWidth);
    }

    /**
     * Get the resource for the given column.
     *
     * @param column Column to get the resource for
     * @return (not null) The resource.
     */
    @NotNull
    public Resource getResource(int column)
    {
        return _resources.get(column);
    }

    /**
     * Get the number of columns that the layout has determined it needs to
     * draw. This will be equal to the number of resources available on a given
     * day plus a possible extra column for unassigned appointments, or
     * appointments which are assigned to resources which are not available on
     * that day.
     *
     * @return Number of columns in the layout.
     */
    public int getColumnCount()
    {
        return _resources.size();
    }
}
