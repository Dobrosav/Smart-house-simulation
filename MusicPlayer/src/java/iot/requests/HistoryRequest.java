/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.requests;

import java.util.List;

/**
 *
 * @author Dinbo-PC
 */
public class HistoryRequest extends IOTRequest {
    
    private List<String> playedSongs;
    
    public HistoryRequest(int userId) {
        super(userId);
    }

    public void setPlayedSongs(List<String> playedSongs) {
        this.playedSongs = playedSongs;
    }

    public List<String> getPlayedSongs() {
        return playedSongs;
    }
    
    
}
