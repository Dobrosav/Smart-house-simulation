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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author vd180005d
 */
public class UserGUI extends JPanel {
    private static final String USER = "/user";
    
    public static String getUSER() {
        return USER;
    }
    
    private JTextField username;
    private JPasswordField password;
    private JButton register;
    
    public UserGUI() {
        username = new JTextField();
        password = new JPasswordField();
        register = new JButton("Register");
        username.setColumns(20);
        password.setColumns(20);
        JPanel edits = new JPanel(new GridLayout(2, 1));
        edits.add(getLabelComponentPane(username, "Username"));
        edits.add(getLabelComponentPane(password, "Password"));
        
        setLayout(new BorderLayout());
        
        add(edits, BorderLayout.NORTH);
        add(register, BorderLayout.SOUTH);
    }
    
    private JPanel getLabelComponentPane(Component componet, String msg) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(msg);
        panel.add(label, BorderLayout.WEST);
        panel.add(componet, BorderLayout.CENTER);

        return panel;
    }
    
    public String getUsername() {
        return username.getText();
    }
    
    public String getPassword() {
        return new String(password.getPassword());
    }
    
    public void setActionRegister(ActionListener al) {
        register.addActionListener(al);
    }
}
