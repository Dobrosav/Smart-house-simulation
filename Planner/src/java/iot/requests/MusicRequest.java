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
public class MusicRequest extends IOTRequest {
    
    private final String songName;
    
    public MusicRequest(String songName, int userId) {
        super(userId);
        this.songName = songName;
    }
    
    public String getSongName() {
        return songName;
    }
}
