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
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import javafx.scene.control.DatePicker;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Dinbo-PC
 */
public class AlarmGUI extends JPanel {
    private static final String CHANGE_SONG = "/alarm/song";
    private static final String SET_ALARM = "/alarm";

    public static String getSET_ALARM() {
        return SET_ALARM;
    }
    
    public static String GetCHANGE_SONG() {
        return CHANGE_SONG;
    }
    
    private JTextField songNameChange;
    private JSpinner date;
    private JSpinner time;
    private JSpinner occurence;
    private JSpinner periodTime;
    private JCheckBox period;
    
    private JButton setAlarm;
    private JButton setSong;
    
    public AlarmGUI() {
        songNameChange = new JTextField("Jedino moje");
        songNameChange.setColumns(20);
        date = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MONTH));
        date.setEditor(new JSpinner.DateEditor(date,"dd.MM.yyyy"));
        time = new JSpinner(new SpinnerDateModel());
        time.setEditor(new JSpinner.DateEditor(time,"HH:mm"));
        periodTime = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        periodTime.setEnabled(false);
        
        period = new JCheckBox("Period");
        period.addItemListener( e -> {
            if (e.getStateChange() == 1) {
                occurence.setEnabled(true);
                periodTime.setEnabled(true);
            }
            else {
                occurence.setEnabled(false);
                periodTime.setEnabled(false);
                periodTime.setValue(0);
                occurence.setValue(1);
            }
        });
        occurence = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        occurence.setValue(1);
        occurence.setEnabled(false);
        setAlarm = new JButton("Set alarm");
        setSong = new JButton("Set song");
        
        
        
        JPanel edits = new JPanel(new GridLayout(6, 1));
        edits.add(getLabelComponentPane(time, "Time"));
        edits.add(getLabelComponentPane(date, "date"));
        edits.add(period);
        
        edits.add(getLabelComponentPane(occurence, "Occurence"));
        edits.add(getLabelComponentPane(songNameChange, "New Song name"));
        edits.add(getLabelComponentPane(periodTime, "Period amount"));
        setLayout(new BorderLayout());
        add(edits, BorderLayout.NORTH);
        
        JPanel lower = new JPanel(new GridLayout(1, 2));
        lower.add(setAlarm);
        lower.add(setSong);
        
        add(lower, BorderLayout.SOUTH);
    }
          
    private JPanel getLabelComponentPane(Component componet, String msg) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(msg);
        panel.setLayout(new GridLayout(1, 2));
        panel.add(label, BorderLayout.WEST);
        panel.add(componet, BorderLayout.CENTER);

        return panel;
    }      
    
    public void setActionAlarm(ActionListener al) {
        setAlarm.addActionListener(al);
    }
    
    public void setActionSong(ActionListener al) {
        setSong.addActionListener(al);
    }
    
    public String getDate() {
        Date ddate = (Date) date.getValue();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        
        return format.format(ddate);
    }
    
    public String getTime() {
        Date ddate = (Date) time.getValue();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        
        return format.format(ddate);
    }
    
    public int getPeriod() {
        return (period.isSelected() ? 1 : 0);
    }
    
    public int getOccurence() {
        return (int) occurence.getValue();
    }
        
    public String getSong() {
        return songNameChange.getText();
    }

    public int getPeriodTime() {
        return (int) periodTime.getValue();
    }
    
    
}
