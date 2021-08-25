/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server33.resources;

import entities.Event;
import entities.User;
import iot.requests.EventRequest;
import iot.requests.HomeSetRequest;
import iot.requests.EventDeleteRequest;
import iot.requests.EventListRequest;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ejb.Stateless;
/**
 *
 * @author vd180005d
 */
@Path("planner")
@Stateless
public class Planner {
    private final String TARGET = "planner";
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(lookup = "Planner")
    private Topic plannerTopic;
    @Resource(lookup = "User")
    private Topic userTopic;
    @GET
    public Response listEvents(@Context HttpHeaders httpHeader) throws JMSException {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        int userId = getUserId(httpHeader);
        
        EventListRequest elrequest = new EventListRequest(userId);
        elrequest.setTarget(TARGET);
        JMSConsumer consumer = context.createConsumer(userTopic, "source = 'Planner' and replyId = " + elrequest.getRequestId() + " and target = '" + TARGET + "'");
        producer.send(plannerTopic, context.createObjectMessage(elrequest));
        
        Message msg = consumer.receive(5000);
        if (msg instanceof ObjectMessage) {
            ObjectMessage omsg = (ObjectMessage) msg;
            if (omsg.getObject() instanceof List) {
                List<Event> response = (List<Event>) omsg.getObject();
                return Response.ok(new GenericEntity<List<Event>>(response){}).build();
            }
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Service unavailable").build();
    }
    
    @DELETE
    @Path("event")
    public Response deleteEvent(@Context HttpHeaders httpHeader, @QueryParam("id") int id) throws JMSException {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        int userId = getUserId(httpHeader);
        
        EventDeleteRequest edrequest = new EventDeleteRequest(userId);
        edrequest.setTarget(TARGET);
        edrequest.setEventId(id);
        JMSConsumer consumer = context.createConsumer(userTopic, "source = 'Planner' and replyId = " + edrequest.getRequestId() + " and target = '" + TARGET + "'");
        producer.send(plannerTopic, context.createObjectMessage(edrequest));
        
        Message msg = consumer.receive(5000);
        if (msg instanceof ObjectMessage) {
            ObjectMessage omsg = (ObjectMessage) msg;
            if (omsg.getObject() instanceof String) {
                String response = (String) omsg.getObject();
                if ("OK".equals(response)) {
                    return Response.ok("DONE").build();
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
                }
            }
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Service unavailable").build();
    }
    
    @POST
    @Path("event")
    public Response setNewEvent(@Context HttpHeaders httpHeader, @QueryParam("date") String date, @QueryParam("time") String time,
            @QueryParam("duration") int duration, @DefaultValue("") @QueryParam("destination") String address, @DefaultValue("0") @QueryParam("alarm") int alarm) {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        int userId = getUserId(httpHeader);
        String home = getUserHome(userId);
        
        if (home == null && address.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No home address").build();
        }
        else if (address.equals("")) {
            address = home;
        }
        
        String dateTime = date + " " + time;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        
        try {
            Date start = format.parse(dateTime);
            Date end = addTimeMinutes(start, duration);
            
            EventRequest erequest = new EventRequest(userId);
            erequest.setStart(start);
            erequest.setEnd(end);
            erequest.setSetAlarm(alarm == 1);
            erequest.setDestination(address);
            erequest.setTarget(TARGET);
            JMSConsumer consumer = context.createConsumer(userTopic, "source = 'Planner' and replyId = " + erequest.getRequestId() + " and target = '" + TARGET + "'");
            producer.send(plannerTopic, context.createObjectMessage(erequest));
            
            Message msg = consumer.receive(5000);
            if (msg instanceof ObjectMessage) {
                String response = (String) ((ObjectMessage) msg).getObject();
                if ("OK".equals(response)) {
                    return Response.ok("DONE").build();
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
                }
            }
        } catch(ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong format date").build();
        } catch (JMSException ex) {}
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Service unavailable").build();
    }
    
    @POST
    @Path("event/update")
    public Response updateEvent(@Context HttpHeaders httpHeader, @QueryParam("id") int eventId, @QueryParam("date") String date, @QueryParam("time") String time,
            @QueryParam("duration") int duration, @DefaultValue("") @QueryParam("destination") String address, @DefaultValue("0") @QueryParam("alarm") int alarm) {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        int userId = getUserId(httpHeader);
        String home = getUserHome(userId);
        
        if (home == null && address.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No home address").build();
        }
        else if (address.equals("")) {
            address = home;
        }
        
        String dateTime = date + " " + time;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        
        try {
            Date start = format.parse(dateTime);
            Date end = addTimeMinutes(start, duration);
            
            EventRequest erequest = new EventRequest(userId);
            erequest.setStart(start);
            erequest.setEnd(end);
            erequest.setSetAlarm(alarm == 1);
            erequest.setDestination(address);
            erequest.setTarget(TARGET);
            erequest.setId_update(eventId);
            JMSConsumer consumer = context.createConsumer(userTopic, "source = 'Planner' and replyId = " + erequest.getRequestId() + " and target = '" + TARGET + "'");
            producer.send(plannerTopic, context.createObjectMessage(erequest));
            
            Message msg = consumer.receive(5000);
            if (msg instanceof ObjectMessage) {
                String response = (String) ((ObjectMessage) msg).getObject();
                if ("OK".equals(response)) {
                    return Response.ok("DONE").build();
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
                }
            }
        } catch(ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong format date").build();
        } catch (JMSException ex) {}
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Service unavailable").build();
    }
    
    @POST
    @Path("home")
    public Response setHomeAddress(@Context HttpHeaders httpHeader, @QueryParam("address") String address) {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        int userId = getUserId(httpHeader);
        
        try {
            HomeSetRequest hsrequest = new HomeSetRequest(userId, address);
            hsrequest.setTarget(TARGET);
            JMSConsumer consumer = context.createConsumer(userTopic, "source = 'Planner' and replyId = " + hsrequest.getRequestId() + " and target = '" + TARGET + "'");
            
            ObjectMessage omsg = context.createObjectMessage(hsrequest);
            producer.send(plannerTopic, omsg);
            
            Message msg = consumer.receive(5000);
            if (msg instanceof ObjectMessage) {
                ObjectMessage romsg = (ObjectMessage) msg;
                if (romsg.getObject() instanceof String) {
                    String response = (String) romsg.getObject();
                    if ("OK".equals(response)) {
                        return Response.ok("DONE").build();
                    }
                    else {
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
                    }
                }
            }
        } catch(JMSException ex) {}
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Service unavailable").build();
    }
    
    private int getUserId(HttpHeaders httpHeader) {
        List<String> headerValues = httpHeader.getRequestHeaders().get("Authorization");
        assert headerValues != null;
        String authHeaderValue = headerValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
        
        assert decodedAuthHeaderValue.length() > 1;
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        return getUserId(username);
    }

    
    
    private int getUserId(String username) {
        List<User> id = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getResultList();
        if (id.size() < 1)
            return -1;
        User user = id.get(0);
        return user.getIdUser();
    }
    
    private String getUserHome(int userId) {
        List<User> id = em.createNamedQuery("User.findByIdUser", User.class).setParameter("idUser", userId).getResultList();
        if (id.size() < 1)
            return "";
        User user = id.get(0);
        return user.getHome();
    }
    
    private Date addTimeMinutes(Date start, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}
