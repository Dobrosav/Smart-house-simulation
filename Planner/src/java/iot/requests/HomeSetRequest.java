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
public class HomeSetRequest extends IOTRequest{
    
    private String address;
        
    public HomeSetRequest(int userId, String address) {
        super(userId);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
