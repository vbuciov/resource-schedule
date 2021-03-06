/*
 * Created by JFormDesigner on Thu Jan 13 18:25:41 PST 2011
 */
package com.thirdnf.resourceScheduler.example;

import com.jgoodies.forms.factories.CC;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import com.thirdnf.resourceScheduler.Appointment;
import com.thirdnf.resourceScheduler.DaySchedule;
import com.thirdnf.resourceScheduler.Resource;
import com.thirdnf.resourceScheduler.ScheduleListener;
import com.thirdnf.resourceScheduler.Scheduler;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

/**
 * Just a example application to show how to use the ResourceScheduler. The
 * coding in here is very rough and not polished. I would not necessarily
 * suggest copying this code exactly into your production application. You can
 * if you choose, but some of this was hacked to make it work rather than make
 * it production worth.
 *
 * @author Joshua Gerth
 */
@SuppressWarnings(
        {
            "FieldCanBeLocal"
        })
public class ExampleScheduler extends JFrame
{

    private final JPopupMenu _popupAppointmentMenu;
    private final JPopupMenu _popupResourceMenu;
    private static final LocalDate Today = new LocalDate();
    private static final LocalDate Tomorrow = Today.plusDays(1);

    private final ExampleScheduleModel bsScheduler;

    /**
     * Main entry point. This method is responsible for creating the main window
     * and showing it.
     *
     * @param args (not null) Any args which are passed in. This is currently
     * ignored.
     */
    public static void main(@NotNull String[] args)
    {
        ExampleScheduler mw = new ExampleScheduler();
        mw.pack();
        mw.setVisible(true);
    }

    /**
     * Main scheduler example. This window just has some controls to show off
     * some of the features of the Resource Scheduler and an instance of the
     * Resource Scheduler itself.
     */
    public ExampleScheduler()
    {
        initComponents();
        bsScheduler = new ExampleScheduleModel();
        _popupAppointmentMenu = new JPopupMenu();
        _popupResourceMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handleAppointmentEdit(_scheduler.getSelectedAppointment());
            }
        });
        _popupAppointmentMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                bsScheduler.deleteAppointment(_scheduler.getSelectedAppointment());
            }
        });
        _popupAppointmentMenu.add(deleteItem);

        editItem = new JMenuItem("Edit");
        editItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handleResourceEdit(_scheduler.getSelectedResource());
            }
        });
        _popupResourceMenu.add(editItem);

        deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                bsScheduler.deleteResource(_scheduler.getSelectedResource());
            }
        });
        _popupResourceMenu.add(deleteItem);
        
        deleteItem = new JMenuItem("Auto Adjust");
        deleteItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                _scheduler.getCurrentView().autoAdjusting( bsScheduler.indexOf(_scheduler.getSelectedResource()));
            }
        });
        _popupResourceMenu.add(deleteItem);

        _scheduler.addScheduleListener(new ScheduleListener()
        {
            @Override
            public void actionPerformed(@NotNull Resource resource, @NotNull DateTime time)
            {
                handleAddAppointment(resource, time);
            }

            public void appointmentMouseClicked(Appointment appointment, MouseEvent e)
            {
                if (e.getClickCount() == 2)
                    handleAppointmentEdit(appointment);
            }

            public void appointmentMousePressed(Appointment appointment, MouseEvent e)
            {
                //Review for secondary button of mouse
                if (e.isPopupTrigger())
                    _popupAppointmentMenu.show(e.getComponent(),
                                               e.getX(), e.getY());
                handleAppointmentClick(appointment);
            }

            public void resourceMousePressed(Resource source, MouseEvent e)
            {
                //Review for secondary button of mouse
                if (e.isPopupTrigger())
                    _popupResourceMenu.show(e.getComponent(),
                                            e.getX(), e.getY());
            }

            public void resourceMouseClicked(Resource source, MouseEvent e)
            {
                if (e.getClickCount() > 1)
                    handleResourceEdit(source);
            }

            public void appointmentDragReleased(Appointment source, MouseEvent e)
            {
                //When drag operation finish
                handleAppointmentClick(source);
            }
        });

        _scheduler.setComponentFactory(new ExampleComponentFactory());
        _scheduler.setModel(bsScheduler);
    }

    /**
     * Handle when the user clicks on an appointment. This just displays
     * appointment information in the appointment text area.
     *
     * @param appointment (not null) The appointment clicked on
     */
    private void handleAppointmentClick(@NotNull Appointment appointment)
    {
        StringBuilder stringBuilder = new StringBuilder();
        LocalTime time = appointment.getDateTime().toLocalTime();
        Period period = appointment.getDuration().toPeriod();

        stringBuilder.append("Info About: ").append(appointment.getTitle()).append('\n')
                .append("Start time: ").append(time.toString("h:mm a")).append('\n')
                .append("Duration: ").append(period.toString(PeriodFormat.getDefault())).append('\n');
        stringBuilder.append("For Resource: ").append(appointment.getResource());

        _detailsPane.setText(stringBuilder.toString());
    }

    public void handleAppointmentEdit(@NotNull Appointment appointment)
    {
        if (!(appointment instanceof ExampleAppointment))
        {
            System.err.println("Can't edit non-example appointment.");
            return;
        }
        ExampleAppointment exampleAppointment = (ExampleAppointment) appointment;

        AppointmentDialog dialog = new AppointmentDialog(this, exampleAppointment, bsScheduler, DaySchedule.INCREMENTS);
        dialog.setOkayListener(new AppointmentDialog.IOkayListener()
        {
            @Override
            public void handleOkay(@NotNull ExampleAppointment appointment)
            {
                bsScheduler.updateAppointment(appointment);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    public void handleResourceEdit(@NotNull Resource resource)
    {
        if (!(resource instanceof ExampleResource))
        {
            System.err.println("Can't edit a non-example resource");
            return;
        }

        ExampleResource exampleResource = (ExampleResource) resource;

        ResourceDialog dialog = new ResourceDialog(this, exampleResource);
        dialog.setOkayListener(new ResourceDialog.IOkayListener()
        {
            @Override
            public void handleOkay(@NotNull ExampleResource resource, int column)
            {
                bsScheduler.updateResource(resource);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Handle when the user clicks on the print button.
     */
    private void handlePrint()
    {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(_scheduler);
        if (printJob.printDialog())
        {
            try
            {
                printJob.print();
            }
            catch (PrinterException pe)
            {
                System.out.println("Error printing: " + pe);
            }
        }
    }

    /**
     * Method called when the user has clicked the add resource button and wants
     * to add a resource on the given day.
     */
    private void handleAddResource()
    {
        ResourceDialog dialog = new ResourceDialog(this);
        dialog.setOkayListener(new ResourceDialog.IOkayListener()
        {
            @Override
            public void handleOkay(@NotNull ExampleResource resource, int column)
            {
                bsScheduler.addResource(resource, column);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Handle the user clicking on the Today radio button by telling the
     * scheduler the date has changed.
     */
    private void handleSelectToday()
    {
        _scheduler.showDate(Today);
    }

    /**
     * Handle the user clicking on the Tomorrow radio button by telling the
     * scheduler the date has changed.
     */
    private void handleSelectTomorrow()
    {
        _scheduler.showDate(Tomorrow);
    }

    /**
     * Handle an add appointment. This is usually from when the client has
     * clicked the 'add appointment' button, as opposed to responding to a click
     * on the schedule itself.
     */
    private void handleAddAppointment()
    {
        LocalDate date = _todayRadio.isSelected() ? Today : Tomorrow;

        AppointmentDialog dialog = new AppointmentDialog(this, date, bsScheduler, DaySchedule.INCREMENTS);
        dialog.setOkayListener(new AppointmentDialog.IOkayListener()
        {
            @Override
            public void handleOkay(@NotNull ExampleAppointment appointment)
            {
                bsScheduler.addAppointment(appointment);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    private void handleAddAppointment(@Nullable Resource resource, @NotNull DateTime dateTime)
    {
       
        AppointmentDialog dialog = new AppointmentDialog(this, resource, dateTime, bsScheduler, DaySchedule.INCREMENTS);
        dialog.setOkayListener(new AppointmentDialog.IOkayListener()
        {
            @Override
            public void handleOkay(@NotNull ExampleAppointment appointment)
            {
                bsScheduler.addAppointment(appointment);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    private void handledTimeChange(ActionEvent e)
    {
       ConfigureTimeDialog dialog = new ConfigureTimeDialog(this, bsScheduler);
       Dimension desktopSize = this.getSize();
        Dimension frameSize = dialog.getSize();
        dialog.setLocation((desktopSize.width - frameSize.width) / 2,
                (desktopSize.height - frameSize.height) / 2);
        dialog.setVisible(true);
 
       dialog.setOkActionListener (new ConfigureTimeDialog.IAcceptListener()
       {
           public void handleOkay(LocalTime start, LocalTime end)
           {
              bsScheduler.setStartTime(start);
              bsScheduler.setEndTime(end);
           }

       });
       dialog.setVisible(true);
    }

    /**
     * Initialize the components.
     */
    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pBody = new JPanel();
        pDayGroup = new JPanel();
        lbDayInfo = new JLabel();
        _todayRadio = new JRadioButton();
        _tomorrowRadio = new JRadioButton();
        spDetailsPane = new JScrollPane();
        _detailsPane = new JTextPane();
        _scheduler = new Scheduler();
        btnAddResource = new JButton();
        btnAddAppointment = new JButton();
        btnPrint = new JButton();
        btnChangeTime = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 300));
        setTitle("Resource Scheduler Demo");
        Container contentPane = getContentPane();

        contentPane.setLayout(new FormLayout(
                "default:grow",
                "default:grow"));

        //======== panel1 ========
        pBody.setLayout(new FormLayout(
                "2*(default, $lcgap), default:grow",
                "default, $lgap, default:grow, 2*($lgap, default)"));

        //======== panel2 ========
//        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        //---- label1 ----
        lbDayInfo.setText("Date:");
        pDayGroup.add(lbDayInfo);

        //---- _todayRadio ----
        _todayRadio.setText("Today");
        _todayRadio.setSelected(true);
        _todayRadio.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handleSelectToday();
            }
        });
        pDayGroup.add(_todayRadio);

        //---- _tomorrowRadio ----
        _tomorrowRadio.setText("Tomorrow");
        _tomorrowRadio.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handleSelectTomorrow();
            }
        });
        pDayGroup.add(_tomorrowRadio);

        pBody.add(pDayGroup, CC.xywh(1, 1, 3, 1, CC.FILL, CC.FILL));

        spDetailsPane.setMinimumSize(new Dimension(15, 23));

        //---- _detailsPane ----
        _detailsPane.setBorder(new TitledBorder("Appointment Details"));
        spDetailsPane.setViewportView(_detailsPane);

        pBody.add(spDetailsPane, CC.xywh(1, 3, 3, 1, CC.DEFAULT, CC.FILL));
        pBody.add(_scheduler, CC.xywh(5, 1, 1, 7));


        //---- button2 ----
        btnAddResource.setText("Add Resource");
        btnAddResource.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handleAddResource();
            }
        });
        pBody.add(btnAddResource, CC.xy(1, 5));

        //---- button3 ----
        btnAddAppointment.setText("Add Appointment");
        btnAddAppointment.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handleAddAppointment();
            }
        });
        pBody.add(btnAddAppointment, CC.xy(3, 5));
        
        
        //---- btnChangeTime -----
        btnChangeTime.setText("Change Time");
        btnChangeTime.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                handledTimeChange(e);
            }
        });
        pBody.add(btnChangeTime, CC.xy(1, 7));

        //---- button1 ----
        btnPrint.setText("Print");
        btnPrint.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handlePrint();
            }
        });
        pBody.add(btnPrint, CC.xy(3, 7));

        contentPane.add(pBody, new CellConstraints(1, 1, 1, 1, CC.DEFAULT, CC.FILL, new Insets(5, 5, 5, 5)));
        pack();
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        ButtonGroup rgRadioDay = new ButtonGroup();
        rgRadioDay.add(_todayRadio);
        rgRadioDay.add(_tomorrowRadio);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pBody;
    private JPanel pDayGroup;
    private JLabel lbDayInfo;
    private JRadioButton _todayRadio;
    private JRadioButton _tomorrowRadio;
    private JScrollPane spDetailsPane;
    private JTextPane _detailsPane;
    private Scheduler _scheduler;
    private JButton btnAddResource;
    private JButton btnAddAppointment;
    private JButton btnPrint;
    private JButton btnChangeTime;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
