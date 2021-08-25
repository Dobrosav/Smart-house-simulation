/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server33.resources;
import entities.User;
import iot.requests.HistoryRequest;
import iot.requests.MusicRequest;
import iot.requests.HistoryResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author vd180005d
 */
@Path("music")
@Stateless
public class MusicApi {
    private final String TARGET = "MusicApi";
    
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(lookup = "MusicPlayer")
    private javax.jms.Topic musicPlayerTopic;
    @Resource(lookup = "User")
    private javax.jms.Topic userTopic;
    
    @GET
    @Path("history")
    public Response getHistory(@QueryParam("user") String username) throws InterruptedException {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        
        int userId = getUserId(username);
        
        if (userId == -1)
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        
        HistoryRequest hrequest = new HistoryRequest(userId);
        hrequest.setTarget(TARGET);
        ObjectMessage omsg = context.createObjectMessage(hrequest);
        producer.send(musicPlayerTopic, omsg);
        
        try {
            JMSConsumer consumer = context.createConsumer(userTopic, "source = 'MusicPlayer' AND replyId = " + hrequest.getRequestId() + " and target = '" + TARGET + "'");
            Message msg = consumer.receive(5000);
            if (msg instanceof ObjectMessage) {
                ObjectMessage romsg = (ObjectMessage) msg;

                if (romsg.getObject() instanceof HistoryRequest) {
                    List<String> songs = ((HistoryRequest) romsg.getObject()).getPlayedSongs();

                    HistoryResponse hresponse = new HistoryResponse(songs);
                    return Response.status(Response.Status.OK).type(MediaType.TEXT_XML).entity(hresponse).build();
                }
            }
        } catch(JMSException e) {}
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Service unavailable").build();
    }
    
    @GET
    @Path("play")
    public Response playSong(@QueryParam("song") String s, @Context HttpHeaders httpHeader) throws JMSException {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        int userId = getUserId(httpHeader);
        
        MusicRequest request = new MusicRequest(s, userId);
        request.setTarget(TARGET);
        ObjectMessage msg = context.createObjectMessage(request);

        producer.send(musicPlayerTopic, msg);
        return Response.ok("DONE").build();
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
}