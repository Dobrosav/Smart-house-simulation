/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.planner;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import entities.Alarm;
import entities.Event;
import entities.User;
import iot.requests.AlarmRequest;
import iot.requests.EventDeleteRequest;
import iot.requests.EventRequest;
import iot.requests.HomeSetRequest;
import iot.requests.IOTRequest;
import iot.requests.EventListRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author vd180005d
 */
public class Planner extends Thread {
    
    private static final String TARGET = "planner";
    private static final String API_KEY = "AIzaSyAinfPjmbFWRRNXbq5gUUJZ6On5dXQIleE";
    private static GeoApiContext geoContext;
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
    private EntityManager em = emf.createEntityManager();
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "Planner")
    private static Topic plannerTopic;
    @Resource(lookup = "User")
    private static Topic userTopic;
    @Resource(lookup = "Alarm")
    private static Topic alarmTopic;
    
    
    public Planner() {
        geoContext = new GeoApiContext.Builder().apiKey(API_KEY).build();
    }

    @Override
    public void run() {
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(plannerTopic);
        
        while (true) {
            try {
                Message msg = consumer.receive();
                if (msg instanceof ObjectMessage) {
                    ObjectMessage omsg = (ObjectMessage) msg;
                    if (omsg.getObject() instanceof HomeSetRequest) {
                        HomeSetRequest hsrequest = (HomeSetRequest) omsg.getObject();
                        sendHomeResponse(context, hsrequest);
                    }
                    else if (omsg.getObject() instanceof EventRequest) {
                        EventRequest erequest = (EventRequest) omsg.getObject();
                        if (erequest.getId_update() == -1)
                            saveEvent(context, erequest);
                        else updateEvent(context, erequest);
                    }
                    else if (omsg.getObject() instanceof EventDeleteRequest) {
                        EventDeleteRequest edrequest = (EventDeleteRequest) omsg.getObject();
                        deleteEvent(context, edrequest);
                    }
                    else if (omsg.getObject() instanceof EventListRequest) {
                        EventListRequest lerequest = (EventListRequest) omsg.getObject();
                        sendListEvents(lerequest);
                    }
                }
                else {
                    break;
                }
            } catch(JMSException e) {} catch (ApiException ex) {
                Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        geoContext.shutdown();
    }
        
    public long calculateDistance(String src, String dst) throws ApiException, InterruptedException, IOException {
        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(geoContext);
        DistanceMatrix result = req.origins(src).destinations(dst).mode(TravelMode.DRIVING).await();
         
        long s = result.rows[0].elements[0].duration.inSeconds;
         
        return s;
    }
    
    private void sendHomeResponse(JMSContext context, HomeSetRequest hsrequest) throws JMSException, ApiException, InterruptedException, IOException {
        JMSProducer producer = context.createProducer();
        
        String msg = "Unkown Error";
        try {
            setHome(hsrequest);
            msg = "OK";
             } 
        catch (Throwable t){
            msg = "OK";
        } finally {
            sendMessage(context, hsrequest, msg);
        }
    }
    
    private void setHome(HomeSetRequest hsrequest) throws ApiException, InterruptedException, IOException {
        User user = getUser(hsrequest.getUserId());
        calculateDistance(hsrequest.getAddress(), hsrequest.getAddress());
        user.setHome(hsrequest.getAddress());
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }
     
   private User getUser(int id) {
        List<User> users = em.createNamedQuery("User.findByIdUser", User.class).setParameter("idUser", id).getResultList();
        assert users.size() == 1; // Will always be true for logged in user
            
        User user = users.get(0);
        return user;
    }
    
    public static void main(String[] args) {
        Thread t = new Planner();
        t.start();
        
    }

    private void saveEvent(JMSContext context, EventRequest erequest) throws JMSException {
        List<Event> events = em.createQuery("SELECT e FROM Event e WHERE e.userId.idUser = :idUser ORDER BY e.start", Event.class)
                .setParameter("idUser", erequest.getUserId())
                .getResultList();
        
        if (isDateTaken(events, erequest.getStart(), erequest.getEnd())) {
            sendMessage(context, erequest, "Event date taken");
            return;
        }
        
        Event previousEvent = getPreviousEvent(events, erequest.getStart());
        Event nextEvent = getNextEvent(events, erequest.getEnd());
        String previousAddress;
        if (previousEvent == null) {
            previousAddress = getUser(erequest.getUserId()).getHome();
        }
        else {
            previousAddress = previousEvent.getDestinacija();
        }
        if (previousAddress.isEmpty()) {
            sendMessage(context, erequest, "No home address");
            return;
        }
        
        Event newEvent = new Event();
        newEvent.setDestinacija(erequest.getDestination());
        newEvent.setStart(erequest.getStart());
        newEvent.setEnd(erequest.getEnd());
        newEvent.setUserId(getUser(erequest.getUserId()));
        newEvent.setAlarmId(null);
        
        try {
            if ((previousEvent != null && !checkCanReach(previousEvent, newEvent)) ||
                    nextEvent != null && !checkCanReach(newEvent, nextEvent)) {
                sendMessage(context, erequest, "Cant reach from/to another event");
                return;
            }
            
            updateNextEventAlarm(newEvent, nextEvent);
            if (erequest.isSetAlarm()) {
                createAlarmForEvent(context, newEvent, (int) calculateDistance(previousAddress, newEvent.getDestinacija()));
            }
            em.getTransaction().begin();
            em.persist(newEvent);
            em.getTransaction().commit();
            
            
        } catch(Throwable t) {
            sendMessage(context, erequest, "Address failed");
            return;
        }
        
        sendMessage(context, erequest, "OK");
    }
    
    private boolean isDateTaken(List<Event> events, Date start, Date end) {
        boolean isTaken = false;
        for (Event event : events) {
            if (end.before(event.getStart()) || end.equals(event.getStart())
                    || start.after(event.getEnd()) || start.equals(event.getEnd()))
                continue;
            return true;
        }
        return isTaken;
    }

    private Event getPreviousEvent(List<Event> events, Date start) {
        Event address = null;
        for(Event event : events) {
            if (event.getEnd().before(start) || event.getEnd().equals(start)) {
                address = event;
            }
        }
        return address;
    }
    
    private Event getNextEvent(List<Event> events, Date end) {
        Event address = null;
        for(Event event : events) {
            if (event.getStart().after(end) || event.getStart().equals(end)) {
                return event;
            }
        }
        return address;
    }
    
    private void sendMessage(JMSContext context, IOTRequest request, Serializable msg) throws JMSException {
        JMSProducer producer = context.createProducer();
        ObjectMessage omsg = context.createObjectMessage(msg);
        omsg.setStringProperty("source", "Planner");
        omsg.setIntProperty("replyId", request.getRequestId());
        omsg.setStringProperty("target", request.getTarget());
        omsg.setJMSExpiration(100000);
        System.out.println("Planner sent " + request.getRequestId() + " " + msg);
        producer.send(userTopic, omsg);
    }

    private boolean checkCanReach(Event previousEvent, Event newEvent) throws ApiException, InterruptedException, IOException {
        if (previousEvent.getStart().after(newEvent.getStart())) {
            return false;
        }
        
        long seconds = calculateDistance(previousEvent.getDestinacija(), newEvent.getDestinacija());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previousEvent.getEnd());
        calendar.add(Calendar.SECOND, (int) seconds);
        if (calendar.getTime().after(newEvent.getStart()))
            return false;
        return true;
    }

    private void deleteEvent(JMSContext context, EventDeleteRequest edrequest) throws JMSException {
        final int eventId = edrequest.getEventId();
        final int userId = edrequest.getUserId();
        
        List<Event> events = em.createQuery("SELECT e FROM Event e WHERE e.userId.idUser = :idUser and e.idDogadjaj = :idEvent", Event.class)
                .setParameter("idEvent", eventId)
                .setParameter("idUser", userId)
                .getResultList();
        if (events.size() > 0) {
            Event event = events.get(0);
            em.getTransaction().begin();
            em.remove(event);
            em.getTransaction().commit();
            sendMessage(context, edrequest, "OK");
        } else {
            sendMessage(context, edrequest, "Not found");
        }
    }

    private void updateNextEventAlarm(Event newEvent, Event nextEvent) throws ApiException, InterruptedException, IOException {
        if (nextEvent == null || nextEvent.getAlarmId() == null)
            return;
        
        List<Alarm> alarms = em.createNamedQuery("Alarm.findByIdAlarm", Alarm.class)
                .setParameter("idAlarm", nextEvent.getAlarmId())
                .getResultList();
        
        if (alarms.size() > 0) {
            Alarm alarm = alarms.get(0);
            Date time = nextEvent.getStart();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            calendar.add(Calendar.SECOND, -(int) calculateDistance(newEvent.getDestinacija(), nextEvent.getDestinacija()));
            alarm.setTime(calendar.getTime());
            em.getTransaction().begin();
            em.persist(alarm);
            em.getTransaction().commit();
        }
    }

    private void createAlarmForEvent(JMSContext context, Event event, int secondsToArrive) throws IOException, JMSException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getStart());
        calendar.add(Calendar.SECOND, secondsToArrive);
        AlarmRequest arequest = new AlarmRequest(event.getUserId().getIdUser(), calendar.getTime());
        arequest.setTarget(TARGET);
        arequest.setPeriod(0);
        arequest.setOccurence(1);
                
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(userTopic, "source = 'Alarm' and replyId = " + arequest.getRequestId() + " and target = '" + TARGET + "'");

        producer.send(alarmTopic, arequest);
        
        Message msg = consumer.receive(5000);
        if (msg == null) {
            throw new IOException();
        }
        if (msg instanceof ObjectMessage) {
            ObjectMessage omsg = (ObjectMessage) msg;
            if (omsg.getObject() instanceof Integer) {
                List<Alarm> alarms = (List<Alarm>) em.createNamedQuery("Alarm.findByIdAlarm", Alarm.class)
                        .setParameter("idAlarm", omsg.getObject())
                        .getResultList();
                
                assert alarms.size() > 0;
                event.setAlarmId(alarms.get(0));
            }
        }
    }

    private void sendListEvents(EventListRequest lerequest) throws JMSException {
        List<Event> events = em.createQuery("SELECT e FROM Event e WHERE e.userId.idUser = :idUser")
                .setParameter("idUser", lerequest.getUserId())
                .getResultList();
        
        sendMessage(connectionFactory.createContext(), lerequest, (Serializable) events);
    }

    private void updateEvent(JMSContext context, EventRequest erequest) throws JMSException {
        Event event = em.find(Event.class, erequest.getId_update());
        if (event == null) {
            sendMessage(context, erequest, "No event found");
            return;
        }
        
        List<Event> events = em.createQuery("SELECT e FROM Event e WHERE e.userId.idUser = :idUser and e.idDogadjaj != :idDog ORDER BY e.start", Event.class)
                .setParameter("idUser", erequest.getUserId())
                .setParameter("idDog", event.getIdDogadjaj())
                .getResultList();
        
        if (isDateTaken(events, erequest.getStart(), erequest.getEnd())) {
            sendMessage(context, erequest, "Event date taken");
            return;
        }
        
        Event previousEvent = getPreviousEvent(events, erequest.getStart());
        Event nextEvent = getNextEvent(events, erequest.getEnd());
        String previousAddress;
        if (previousEvent == null) {
            previousAddress = getUser(erequest.getUserId()).getHome();
        }
        else {
            previousAddress = previousEvent.getDestinacija();
        }
        if (previousAddress.isEmpty()) {
            sendMessage(context, erequest, "No home address");
            return;
        }
        
        Event newEvent = new Event();
        newEvent.setDestinacija(erequest.getDestination());
        newEvent.setStart(erequest.getStart());
        newEvent.setEnd(erequest.getEnd());
        newEvent.setUserId(getUser(erequest.getUserId()));
        newEvent.setAlarmId(null);
        
        try {
            if ((previousEvent != null && !checkCanReach(previousEvent, newEvent)) ||
                    nextEvent != null && !checkCanReach(newEvent, nextEvent)) {
                sendMessage(context, erequest, "Cant reach from/to another event");
                return;
            }
            
            updateNextEventAlarm(newEvent, nextEvent);
            if (erequest.isSetAlarm() && event.getAlarmId() == null) {
                createAlarmForEvent(context, event, (int) calculateDistance(previousAddress, newEvent.getDestinacija()));
            }
            else if (!erequest.isSetAlarm()) {
                if (event.getAlarmId() != null) {
                    event.getAlarmId().setOccurence(0);
                    em.getTransaction().begin();
                    em.persist(event.getAlarmId());
                    em.getTransaction().commit();
                }
                event.setAlarmId(null);
            }
            
            event.setDestinacija(newEvent.getDestinacija());
            event.setEnd(newEvent.getEnd());
            event.setStart(newEvent.getStart());
            
            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            
            
        } catch(Throwable t) {
            sendMessage(context, erequest, "Address failed");
            return;
        }
        
        sendMessage(context, erequest, "OK");
    
    }
}
