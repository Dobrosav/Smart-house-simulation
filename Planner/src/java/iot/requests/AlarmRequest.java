/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.requests;

import java.util.Date;

/**
 *
 * @author Dinbo-PC
 */
public class AlarmRequest extends IOTRequest {
    
    private Date time;
    private int period = 0;
    private int occurence = Integer.MAX_VALUE;
    
    public AlarmRequest(int userId, Date time) {
        super(userId);
        this.time = time;
    }

    public void setPeriod(int period) {
        this.period = period;
        if (period == 0) {
            occurence = 1;
        }
    }

    public Date getTime() {
        return time;
    }

    public int getPeriod() {
        return period;
    }
    
    public String getType() {
        if (period == 0) {
            return "ONCE";
        }
        return "REPEATING";
    }

    public int getOccurence() {
        return occurence;
    }

    public void setOccurence(int occurence) {
        this.occurence = occurence;
    }
    
    
}
