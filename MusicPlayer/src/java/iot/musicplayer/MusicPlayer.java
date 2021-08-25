/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.musicplayer;

import entities.MusicPlayed;
import entities.User;
import iot.requests.HistoryRequest;
import iot.requests.MusicRequest;
import javax.jms.ConnectionFactory;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
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
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author vd180005d
 */
public class MusicPlayer extends Thread {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MusicPlayerPU");
    private EntityManager em = emf.createEntityManager();
    //private static  ChromeDriver ch;
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "MusicPlayer")
    private static Topic musicPlayerTopic;
    @Resource(lookup = "User")
    private static Topic userTopic;
    
    @Override
    public void run() {
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(musicPlayerTopic);
        
        while(true) {
            try {
                Message msg = consumer.receive();
                System.out.println("Message recieved");
                if (msg instanceof ObjectMessage) {
                    ObjectMessage omsg = (ObjectMessage) msg;
                    if (omsg.getObject() instanceof MusicRequest) {
                        MusicRequest mrequest = (MusicRequest) omsg.getObject();
                          // persistMusicPlayed(mrequest);                    
                       // playMusic(mrequest);
                            Desktop d=Desktop.getDesktop();
                            d.browse(new URI("https://www.youtube.com/watch?v=qlf0yk6J17M"));
                           persistMusicPlayed();
                        }
                    else if (omsg.getObject() instanceof HistoryRequest) {
                        HistoryRequest hrequest = (HistoryRequest) omsg.getObject();
                        System.out.println("History Recieved");
                        sendHistory(context, hrequest);
                    }
                }
            } catch (JMSException ex) {
                Logger.getLogger(MusicPlayer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(MusicPlayer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MusicPlayer.class.getName()).log(Level.SEVERE, null, ex);
            } 
 
}
}
    private void playMusic(MusicRequest mrequest) throws YouTubeFacade.YouTubeFailToFindException, URISyntaxException, IOException {
        String url = YouTubeFacade.getUriToVideo(mrequest.getSongName());
        System.out.println(url);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        }
        else {
            System.out.println("Hello my world");
        }
    }

    private void persistMusicPlayed() {
        assert em != null;
        List<User> userList = em.createNamedQuery("User.findByIdUser", User.class).setParameter("idUser", 1).getResultList();
        assert userList.size() == 1;
        
        User user = userList.get(0);
        
        MusicPlayed music = new MusicPlayed();
        music.setUserId(user);
        music.setSongName("Jedino Moje");
        em.getTransaction().begin();
        em.persist(music);
        em.getTransaction().commit();
    }
    
    private void sendHistory(JMSContext context, HistoryRequest hrequest) throws JMSException {
        JMSProducer producer = context.createProducer();
        List<MusicPlayed> playedSong = em.createQuery("SELECT m FROM MusicPlayed m WHERE m.userId.idUser = :idMusicPlayed", MusicPlayed.class)
                .setParameter("idMusicPlayed", hrequest.getUserId())
                .getResultList();
       
        ArrayList list = new ArrayList();
        playedSong.forEach(mp -> {list.add(mp.getSongName());});
        hrequest.setPlayedSongs(list);
        ObjectMessage omsg = context.createObjectMessage(hrequest);
        omsg.setIntProperty("replyId", hrequest.getRequestId());
        omsg.setStringProperty("source", "MusicPlayer");
        omsg.setStringProperty("target", hrequest.getTarget());
        omsg.setJMSExpiration(10000);
        producer.send(userTopic, omsg);
    }
    
    public static void main(String[] args) throws YouTubeFacade.YouTubeFailToFindException, URISyntaxException, IOException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\vd180005d\\Downloads\\chromedriver_win32\\chromedriver.exe");
        new MusicPlayer().start();
//        ch.close();
    }
}
