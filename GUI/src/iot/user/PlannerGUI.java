/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

/**
 *
 * @author vd10005d
 */
public class PlannerGUI extends JPanel {
    private static final String CREATE = "/planner/event";
    private static final String LIST = "/planner";
    private static final String HOME = "/planner/home";
    private static final String UPDATE = "/planner/event/update";
    
    public static String getCREATE() {
        return CREATE;
    }

    public static String getLIST() {
        return LIST;
    }

    public static String getHOME() {
        return HOME;
    }

    public static String getUPDATE() {
        return UPDATE;
    }
    
    
    private JSpinner dateStart;
    private JSpinner timeStart;
    private JSpinner dateEnd;
    private JSpinner timeEnd;
    private JCheckBox createAlarm;
    private JTextField address;
    private JSpinner idField;
    
    private JButton delete;
    private JButton create;
    private JButton list;
    private JButton home;
    private JButton update;
    
    public PlannerGUI() {
        JPanel startPanel = new JPanel();
        dateStart = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MONTH));
        dateStart.setEditor(new JSpinner.DateEditor(dateStart,"dd.MM.yyyy"));
        timeStart = new JSpinner(new SpinnerDateModel());
        timeStart.setEditor(new JSpinner.DateEditor(timeStart,"HH:mm"));
        startPanel.add(dateStart);
        startPanel.add(timeStart);
        
        JPanel endPanel = new JPanel();
        dateEnd = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MONTH));
        dateEnd.setEditor(new JSpinner.DateEditor(dateEnd,"dd.MM.yyyy"));
        timeEnd = new JSpinner(new SpinnerDateModel());
        timeEnd.setEditor(new JSpinner.DateEditor(timeEnd,"HH:mm"));
        endPanel.add(dateEnd);
        endPanel.add(timeEnd);
        
        createAlarm = new JCheckBox("Set alarm");
        address = new JTextField();
        idField = new JSpinner();
        address.setColumns(20);
        
        JPanel edits = new JPanel(new GridLayout(5, 1));
        edits.add(getLabelComponentPane(startPanel,"Start"));
        edits.add(getLabelComponentPane(endPanel,"End"));
        edits.add(createAlarm);
        edits.add(getLabelComponentPane(address, "Address"));
        edits.add(getLabelComponentPane(idField, "IdChange"));
        
        create = new JButton("Create");
        list = new JButton("List");
        delete = new JButton("Delete");
        home = new JButton("Set home");
        update = new JButton("Update");
        
        setLayout(new BorderLayout());
        add(edits, BorderLayout.NORTH);
        
        JPanel lower = new JPanel(new GridLayout(2, 3));
        lower.add(create);
        lower.add(list);
        lower.add(delete);
        lower.add(home);
        lower.add(update);
        add(lower, BorderLayout.SOUTH);
    }
       
    private JPanel getLabelComponentPane(Component componet, String msg) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(msg);
        panel.add(label, BorderLayout.WEST);
        panel.add(componet, BorderLayout.CENTER);
        
        return panel;
    }
    
    public String getStartDate() {
        Date ddate = (Date) dateStart.getValue();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        
        return format.format(ddate);
    }
    
    public String getStartTime() {
        Date ddate = (Date) timeStart.getValue();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        
        return format.format(ddate);
    }
    
    public String getEndDate() {
        Date ddate = (Date) dateEnd.getValue();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        
        return format.format(ddate);
    }
    
    public String getEndTime() {
        Date ddate = (Date) timeEnd.getValue();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        
        return format.format(ddate);
    }
    
    public int getDuration() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date start = format.parse(getStartDate() + " " + getStartTime());
            Date end = format.parse(getEndDate() + " " + getEndTime());
            
            int res = (int) (Math.abs(end.getTime() - start.getTime()) / 60000);
            return res;
        } catch (ParseException ex) {
            return 0;
        }
    }
    
    public String getAddress() {
        return address.getText();
    }
    
    public void setActionCreate(ActionListener al) {
        create.addActionListener(al);
    }
    
    public int getAlarm() {
        return (createAlarm.isSelected() ? 1 : 0);
    }
    
    public int getId() {
        return (int) idField.getValue();
    }
    
    public void setActionList(ActionListener al) {
        list.addActionListener(al);
    }
    
    public void setActionDelete(ActionListener al) {
        delete.addActionListener(al);
    }
    
    public void setActionHome(ActionListener al) {
        home.addActionListener(al);
    }
    
    public void setActionUpdate(ActionListener al) {
        update.addActionListener(al);
    }
}
