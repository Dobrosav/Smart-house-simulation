/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 *
 * @author Dinbo-PC
 */
public class Browser {
    
    private String username;
    private String password;
    private URL url;
    private String method;
    private boolean error = false;
    
    
    public Browser(String username, String password, URL url, String method) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.method = method;
    }
    
    public String getResponse() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod(method);
        
        connection.setRequestProperty("Authorization", getAuth());
        
        InputStream responseStream = null;
        try {
            responseStream = connection.getInputStream();
        } catch (IOException e) {
            responseStream = connection.getErrorStream();
            error = true;
        } finally {
            if (responseStream == null)
                return "Connection timed out";
        }


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));
        StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        bufferedReader.close();
        return response.toString();
    }

    public boolean isError() {
        return error;
    }
    
    public String getAuth() {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedAuth);
        return authHeaderValue;
    }
    
}
