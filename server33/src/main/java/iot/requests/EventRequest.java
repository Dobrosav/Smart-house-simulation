/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.requests;

import java.util.Date;

/**
 *
 * @author vd180005d
 */
public class EventRequest extends IOTRequest{
    
    private Date start;
    private Date end;
    private boolean setAlarm = false;
    private String destination;
    
    private int id_update = -1;
    
    public EventRequest(int userId) {
        super(userId);
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setSetAlarm(boolean setAlarm) {
        this.setAlarm = setAlarm;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public boolean isSetAlarm() {
        return setAlarm;
    }

    public String getDestination() {
        return destination;
    }

    public void setId_update(int id_update) {
        this.id_update = id_update;
    }

    public int getId_update() {
        return id_update;
    }
    
    
}
