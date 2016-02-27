/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thirdnf.resourceScheduler.example;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;

/**
 *
 * @author sis2
 */
public class ConfigureTimeDialog extends JDialog
{

    JSpinner jspHoraInicio, jspHoraFinal;
    JButton btnAceptar, btnCancelar;
    JPanel jpBody, jpAcciones;
    IAcceptListener listener;
    Calendar c;

    public ConfigureTimeDialog(@NotNull Frame owner,
                               @NotNull ExampleScheduleModel model)
    {
        super(owner);
        initComponents();
        initEvents();
        LocalTime start = model.getStartTime();
        LocalTime end = model.getEndTime();
        c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, start.getHourOfDay());
        c.set(Calendar.MINUTE, start.getMinuteOfHour());
        c.set(Calendar.SECOND, start.getSecondOfMinute());
        jspHoraInicio.setValue(c.getTime());

        c.set(Calendar.HOUR_OF_DAY, end.getHourOfDay());
        c.set(Calendar.MINUTE, end.getMinuteOfHour());
        c.set(Calendar.SECOND, end.getSecondOfMinute());
        jspHoraFinal.setValue(c.getTime());
    }

    private void initComponents()
    {
        setTitle("Change the time");
        btnAceptar = new JButton("Aceptar");
        btnCancelar = new JButton("Cancelar");
        jpBody = new JPanel();
        jpAcciones = new JPanel();
        jspHoraInicio = new JSpinner(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        jspHoraFinal = new JSpinner(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        Container content = getContentPane();
        content.setLayout(new BorderLayout());

        jpAcciones.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jpBody.setLayout(new BorderLayout());

        jspHoraInicio.setPreferredSize(new java.awt.Dimension(150, 22));
        jspHoraInicio.setEditor(new JSpinner.DateEditor(jspHoraInicio, "HH:mm"));
        jspHoraFinal.setPreferredSize(new java.awt.Dimension(150, 22));
        jspHoraFinal.setEditor(new JSpinner.DateEditor(jspHoraFinal, "HH:mm"));

        jpBody.add(jspHoraInicio, BorderLayout.NORTH);
        jpBody.add(jspHoraFinal, BorderLayout.SOUTH);

        jpAcciones.add(btnAceptar);
        jpAcciones.add(btnCancelar);

        add(jpBody, BorderLayout.NORTH);
        add(jpAcciones, BorderLayout.SOUTH);
        pack();
    }

    void setOkActionListener(IAcceptListener actionListener)
    {
        listener = actionListener;
    }

    private void initEvents()
    {
        ActionListener actionManager = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (listener != null)
                    listener.handleOkay(getStartTime(), getEndTime());

                setVisible(false);
            }
        };

        btnCancelar.addActionListener(actionManager);
        btnAceptar.addActionListener(actionManager);
    }

    private LocalTime getStartTime()
    {
        Date start = (Date) jspHoraInicio.getValue();
        c.setTime(start);
        LocalTime value = new LocalTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));

        return value;
    }

    private LocalTime getEndTime()
    {
        Date end = (Date) jspHoraFinal.getValue();
        c.setTime(end);
        LocalTime value = new LocalTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));

        return value;
    }

    public static interface IAcceptListener
    {

        void handleOkay(@NotNull LocalTime start, @NotNull LocalTime end);
    }
}
