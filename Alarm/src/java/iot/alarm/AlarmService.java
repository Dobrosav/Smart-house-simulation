/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.alarm;

import entities.Alarm;
import iot.requests.AlarmRequest;
import iot.requests.IOTRequest;
import iot.requests.MusicRequest;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
public class AlarmService extends Thread {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
    private EntityManager em = emf.createEntityManager();
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "MusicPlayer")
    private static Topic musicPlayerTopic;
    @Resource(lookup = "Alarm")
    private static Topic alarmTopic;
    @Resource(lookup = "User")
    private static Topic userTopic;
    
    private static String songName = "Jedino moje";
    
    @Override
    public void run() {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        while (true) {
            synchronized(this) {
                List<Alarm> alarms = em.createQuery("SELECT a FROM Alarm a WHERE a.occurence > 0", Alarm.class).getResultList();
                long nowMilis = System.currentTimeMillis();
                Date now = new Date(nowMilis);
                for (Alarm alarm : alarms) {
                    if (alarm.getTime().before(now)) {
                        if (alarm.getOccurence() > 0) {
                            MusicRequest mrequest = new MusicRequest(songName, 1);
                            mrequest.setToPersist(false);
                            mrequest.setTarget("AlarmService");
                            producer.send(musicPlayerTopic, context.createObjectMessage(mrequest));
                            alarm.setOccurence(alarm.getOccurence() - 1);
                            if ("REPEATING".equals(alarm.getType())) {
                                Calendar calendar = Calendar.getInstance();
                                while(alarm.getTime().before(now) && alarm.getOccurence() > 0) {
                                    calendar.setTime(alarm.getTime());
                                    calendar.add(Calendar.MINUTE, alarm.getPeriod());
                                    alarm.setTime(calendar.getTime());
                                }
                            }
                            em.getTransaction().begin();
                            em.persist(alarm);
                            em.getTransaction().commit();
//                            else {
//                                em.getTransaction().begin();
//                                em.remove(alarm);
//                                em.getTransaction().commit();
//                            }
                        }
                    }
                }
            }
        }
    }
    
    public void listener() {
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(alarmTopic);
        
        while (true) {
            try {
                Message msg = consumer.receive();
                if (msg instanceof ObjectMessage) {
                    ObjectMessage omsg = (ObjectMessage) msg;
                    if (omsg.getObject() instanceof AlarmRequest) {
                        AlarmRequest arequest = (AlarmRequest) omsg.getObject();
                        Alarm a = new Alarm();
                        a.setType(arequest.getType());
                        a.setPeriod(arequest.getPeriod());
                        a.setTime(arequest.getTime());
                        a.setOccurence(arequest.getOccurence());
                        
                        synchronized(this) {
                            em.getTransaction().begin();
                            em.persist(a);
                            em.getTransaction().commit();
                            
                            sendMessage(context, arequest, a.getIdAlarm());
                        }
                    } else if (omsg.getObject() instanceof String) {
                        songName = (String) omsg.getObject();
                    }
                }
            } catch(JMSException e) {}
        }
    }
    
    private void sendMessage(JMSContext context, IOTRequest request, Serializable msg) throws JMSException {
        JMSProducer producer = context.createProducer();
        ObjectMessage omsg = context.createObjectMessage(msg);
        omsg.setStringProperty("source", "Alarm");
        omsg.setIntProperty("replyId", request.getRequestId());
        omsg.setStringProperty("target", request.getTarget());
        omsg.setJMSExpiration(100000);
        System.out.println("Alarm sent " + request.getRequestId() + " " + msg);
        producer.send(userTopic, omsg);
    }
    
    public static void main(String[] args) {
        AlarmService t = new AlarmService();
        t.start();
        t.listener();
    }
    
}
