/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.requests;
/**
 *
 * @author Dinbo-PC
 */
public class EventDeleteRequest extends IOTRequest {
    
    private int eventId;

    public EventDeleteRequest(int userId) {
        super(userId);
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }
}
