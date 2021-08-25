/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 *
 * @author vd180005d
 */
public class GUI {
    private static final String SERVER = "http://localhost:8080/server33/resources/";
    
    private JFrame root;
    private JTextPane browser;
    
    private JTextField username;
    private JPasswordField password;
    
    
    public GUI() {
        root = new JFrame("IOT");
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        root.setSize(700,700);
        root.setResizable(false);
        
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(getInfoPane());
        topPanel.add(new JLabel());
        root.setLayout(new BorderLayout());
        root.add(topPanel, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new GridLayout(1, 2, 5, 5));
        center.add(getTabbedPane());
        center.add(getBrowser());
        root.add(center, BorderLayout.CENTER);
        
        root.setVisible(true);
    }
    
    private JPanel getBrowser() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        browser = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(browser, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        browser.setEditable(false);
//        browser.setLineWrap(true);
//        browser.setContentType("text/html");
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panel = new JPanel();
        tabbedPane.addTab("Alarm", getAlarmPanel());
        //tabbedPane.addTab("Player", getPlayerPanel());
        tabbedPane.addTab("Planner", getPlannerPanel());
        tabbedPane.addTab("User", getUserPanel());
        
        return tabbedPane;
    }
    
    private JPanel getPlannerPanel() {
        PlannerGUI planner = new PlannerGUI();
        
        planner.setActionCreate(al -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            try {
                URL url = new URL(SERVER + "planner/event" + "?date=" + planner.getStartDate()
                + "&time=" + planner.getStartTime()
                + "&duration=" + planner.getDuration()
                + "&alarm=" + planner.getAlarm()
                + (planner.getAddress().isEmpty() ? "" : "&destination=" + planner.getAddress()));
                
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "POST");
                browser.setText(b.getResponse());
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        
        planner.setActionList(al -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            try {
                URL url = new URL(SERVER + "planner");
                
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "GET");
                String response = b.getResponse();
                if(!b.isError())
                    browser.setText(xmlTransform(response));
                else 
                    browser.setText(response);
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            } catch (TransformerException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        planner.setActionDelete(al -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            try {
                URL url = new URL(SERVER + "planner/event" + "?id=" + planner.getId());
                
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "DELETE");
                String response = b.getResponse();
                browser.setText(response);
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        
        planner.setActionHome(al -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            if (planner.getAddress().isEmpty()) {
                browser.setText("No address");
                return;
            }
            try {
                URL url = new URL(SERVER + "planner/home"+ "?address=" + planner.getAddress().replace(" ", "%20"));
                
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "POST");
                String response = b.getResponse();
                browser.setText(response);
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        
        planner.setActionUpdate(al -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            try {
                URL url = new URL(SERVER + "planner/event/update" + "?id=" + planner.getId()
                + "&date=" + planner.getStartDate()
                + "&time=" + planner.getStartTime()
                + "&duration=" + planner.getDuration()
                + "&alarm=" + planner.getAlarm()
                + (planner.getAddress().isEmpty() ? "" : "&destination=" + planner.getAddress()));
                
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "POST");
                String response = b.getResponse();
                browser.setText(response);
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        
        return planner;
    }
    
    private JPanel getAlarmPanel() {
        AlarmGUI alarm = new AlarmGUI();
        alarm.setActionAlarm(al -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            try {
                URL url = new URL(SERVER+"alarm"+ "?Date=" + alarm.getDate()
                + "&Time=" + alarm.getTime()
                + "&Period=" + alarm.getPeriodTime()
                + "&Occurence=" + alarm.getOccurence());
                
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "POST");
                browser.setText(b.getResponse());
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        
        alarm.setActionSong(al -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            if (alarm.getSong().isEmpty()) {
                browser.setText("No song entered");
                return;
            }
            try {
                URL url = new URL(SERVER + "/alarm/song"+ "?song=" + alarm.getSong().replace(" ", "%20"));
                
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "POST");
                browser.setText(b.getResponse());
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        return alarm;
    }
    
 /*   private JPanel getPlayerPanel() {
        PlayerGUI pgui = new PlayerGUI();
        
        pgui.setActionHistory((ActionEvent e) -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            if (pgui.getUser().isEmpty()) {
                browser.setText("No selected user");
                return;
            }
            try {
                URL url = new URL(SERVER + "/music/history" + "?user=" + pgui.getUser().replace(" ", "%20"));
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "GET");
                browser.setText(xmlTransform(b.getResponse()));
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            } catch (TransformerException ex) {
                browser.setText("Format error");
            }
        });
        
        pgui.setActionSend((ActionEvent e) -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            try {
                URL url = new URL(SERVER + "/music/play"+ "?song=" + pgui.getSongName().replace(" ", "%20"));
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "GET");
                browser.setText(b.getResponse());
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        
        return pgui;
    }*/
    
    private JPanel getUserPanel() {
        UserGUI guser = new UserGUI();
        guser.setActionRegister(e -> {
            if (username.getText().isEmpty()) {
                browser.setText("No username");
                return;
            }
            if (guser.getUsername().isEmpty()) {
                browser.setText("Missing info");
                return;
            }
            try {
                URL url = new URL(SERVER +"user"  + "?name=" + guser.getUsername()+ "&pass=" + guser.getPassword());
                Browser b = new Browser(username.getText(), new String(password.getPassword()), url, "POST");
                browser.setText(b.getResponse());
            } catch(IOException t) {
                browser.setText("User error  " + t.toString());
            }
        });
        
        return guser;
    }
    
    private JPanel getInfoPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(3, 1));
        
        username = new JTextField("dodo");
        password = new JPasswordField("123");
        
        username.setColumns(10);
        password.setColumns(10);
        
        pane.add(getLabelComponentPane(username, "Username"));
        pane.add(getLabelComponentPane(password, "Password"));
        
        return pane;
    }
    
    private JPanel getLabelComponentPane(Component componet, String msg) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(msg);
        panel.add(label, BorderLayout.WEST);
        panel.add(componet, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String xmlTransform(String raw) throws TransformerException {
        Document document = parseXmlFile(raw);
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        return xmlString;
    }
    
    private Document parseXmlFile(String in) {
        
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void main(String[] args) throws MalformedURLException, IOException {
        new GUI();
//        Browser b = new Browser("Bodin", "123", new URL("http://localhost:8080/WebServer/music/history?user=Bodin"), null, "GET");
//        System.out.println(b.getResponse());
    }
    
}
