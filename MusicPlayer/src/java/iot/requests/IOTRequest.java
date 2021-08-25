/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.requests;

import java.io.Serializable;

/**
 *
 * @author Dinbo-PC
 */
public class IOTRequest implements Serializable{
    
    private static int GLOBAL_ID = 0;
    private int userId;
    private int requestId = GLOBAL_ID++;
    private String target = "";
    
    public IOTRequest(int userId) {
        this.userId = userId;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }
    
    
}
