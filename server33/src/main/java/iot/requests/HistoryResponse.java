/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.requests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vd180005d
 */
@XmlRootElement(name = "History")
public class HistoryResponse {
    
    
//    private int foundSongs;
    
    private List<String> songs;
    
    public HistoryResponse() {
        
    }
    
    public HistoryResponse(List<String> songs) {
        this.songs = songs;
//        foundSongs = songs.size();
    }
    
    @XmlElementWrapper(name = "History")
    @XmlElement(name = "song")
    public List<String> getSongs() {
        return songs;
    }
    
    
}
