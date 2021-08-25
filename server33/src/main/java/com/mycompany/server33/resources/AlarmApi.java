/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server33.resources;
import iot.requests.AlarmRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author vd180005d
 */

@Path("alarm")
@Stateless
public class AlarmApi {
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    private final String TARGET = "AlarmApi";
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(lookup = "Alarm")
    private javax.jms.Topic alarmTopic;
    @Resource(lookup = "User")
    private javax.jms.Topic userTopic;
    
    @POST
    public Response createAlarm(@QueryParam("Date") String date, @QueryParam("Time") String time,
            @DefaultValue("0") @QueryParam("Period") Integer period, @DefaultValue("1") @QueryParam("Occurence") Integer occurence) {
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            
            String dateTime = date + " " + time;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                      
            AlarmRequest arequest = new AlarmRequest(0, format.parse(dateTime));
            arequest.setPeriod(period);
            arequest.setTarget(TARGET);
            arequest.setOccurence(occurence);
            
            JMSConsumer consumer = context.createConsumer(userTopic);
            producer.send(alarmTopic, context.createObjectMessage(arequest));
        } catch (ParseException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong date format").build();
        }
        return Response.ok("DONE").build();
    }
    
    @POST
    @Path("song")
    public Response setSong(@QueryParam("song") String name) {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();

        producer.send(alarmTopic, context.createObjectMessage(name));
        
        return Response.ok("DONE").build();
    }    
}
