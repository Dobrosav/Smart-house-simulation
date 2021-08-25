/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iot.musicplayer;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author vd180005d
 */
public class YouTubeFacade {

    public static class YouTubeFailToFindException extends Exception {

        private String failedKeyWord;
        
        private YouTubeFailToFindException(String keyword) {
            failedKeyWord = keyword;
        }

        public String getFailedKeyWord() {
            return failedKeyWord;
        }
    }
    
    private static final String API_KEY = "AIzaSyAinfPjmbFWRRNXbq5gUUJZ6On5dXQIleE";
    private static final String APPLICATION_NAME = "planner";

    private static final Collection<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    public static String getUriToVideo(String keyword) throws YouTubeFailToFindException {
        try {
            YouTube youtubeService = YouTubeFacade.getService();
            YouTube.Search.List request = youtubeService.search().list("snippet").setType("videos");
            SearchListResponse response = request.setMaxResults(25L).setQ(keyword).execute();
            System.out.print(response);
            List<String> list = parseResponse(response);
            
            if (list.isEmpty())
                throw new YouTubeFailToFindException(keyword);
            String s="https://www.youtube.com/watch?v=" + list.get(0);
            return s;
        } catch (GeneralSecurityException | IOException e) {
            throw new YouTubeFailToFindException(keyword);
        }
    }
    
    private static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        return new YouTube.Builder(httpTransport, JSON_FACTORY, (HttpRequest hr) -> {
        })
            .setApplicationName(APPLICATION_NAME)
            .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
            .build();
    }
        
    private static List<String> parseResponse(SearchListResponse response) {
        List<SearchResult> responseList = response.getItems();
        List<String> idList = new ArrayList();
        
        for (SearchResult result : responseList) {
            if ("youtube#video".equals(result.getId().getKind())) {
                idList.add(result.getId().getVideoId());
            }
        }
        return idList;
    }
    
}
