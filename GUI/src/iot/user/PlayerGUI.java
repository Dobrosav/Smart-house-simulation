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
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Dinbo-PC
 */
public class PlayerGUI extends JPanel{
    private static final String HISTORY = "/music/history";
    private static final String PLAY = "/music/play";
    
    
    private JTextField songNameField;
    private JTextField userField;
    private JButton send;
    private JButton history;
    private JList list;
    
    public PlayerGUI() {
        setLayout(new BorderLayout());
        songNameField = new JTextField(20);
        userField = new JTextField(20);
        
        JPanel lowerPanel = new JPanel(new GridLayout(1, 2));
        send = new JButton("Play"); 
        history = new JButton("History");
        lowerPanel.add(send);
        lowerPanel.add(history);
        
        JPanel edits = new JPanel(new GridLayout(2, 1));
        edits.add(getLabelComponentPane(songNameField, "Song Name"));
        edits.add(getLabelComponentPane(userField, "User History"));
        
        this.add(edits, BorderLayout.CENTER);
//        this.add(getLabelComponentPane(userField, "User"), BorderLayout.CENTER);
        this.add(lowerPanel, BorderLayout.SOUTH);
        
    }
    
    private JPanel getLabelComponentPane(Component componet, String msg) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(msg);
        panel.add(label, BorderLayout.WEST);
        panel.add(componet, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void setActionSend(ActionListener action) {
        send.addActionListener(action);
    }
    
    public void setActionHistory(ActionListener action) {
        history.addActionListener(action);
    }
    
    public String getSongName() {
        return songNameField.getText();
    }

    public static String getHISTORY() {
        return HISTORY;
    }

    public static String getPLAY() {
        return PLAY;
    }

    public String getUser() {
        return userField.getText();
    }
    
    
}
