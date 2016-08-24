/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spectramusic.web;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.utils.SimpleLog;
/**
 *
 * @author John Grosh (jagrosh)
 */
public class YoutubeSearcher {
    private final YouTube youtube;
    private final Search.List search;
    private final Videos.List videoInfo;
    public YoutubeSearcher(String apiKey)
    {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), (HttpRequest request) -> {
        }).setApplicationName("Spectra Music JDA Java Bot").build();
        Search.List tmp = null;
        Videos.List tmp2 = null;
        try {
            tmp = youtube.search().list("id,snippet");
            tmp2 = youtube.videos().list("contentDetails");
        } catch (IOException ex) {
            SimpleLog.getLog("Youtube").fatal("Failed to initialize search or videos: "+ex.toString());
        }
        videoInfo = tmp2;
        search = tmp;
        if(search!=null)
        {
            search.setKey(apiKey);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        }
        if(videoInfo!=null)
        {
            videoInfo.setKey(apiKey);
        }
    }
    
    public List<YoutubeInfo> getResults(String query, int numresults)
    {
        List<YoutubeInfo> infos = new ArrayList<>();
        search.setQ(query);
        search.setMaxResults((long)numresults);
        
        SearchListResponse searchResponse;
        try {
            searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            StringBuilder builder = new StringBuilder();
            searchResultList.stream().forEach(sr -> builder.append(", ").append(sr.getId().getVideoId()));
            List<Video> videos = videoInfo.setId(builder.toString().substring(2)).execute().getItems();
            for(int i=0; i<videos.size(); i++)
            {
                infos.add(new YoutubeInfo(videos.get(i).getId(),
                        searchResultList.get(i).getSnippet().getTitle(),
                        cleanDuration(Duration.parse(videos.get(i).getContentDetails().getDuration()))
                        ));
            }
        } catch (IOException ex) {
            SimpleLog.getLog("Youtube").fatal("Search failure: "+ex.toString());
            return null;
        }
        return infos;
    }
    
    public static String cleanDuration(Duration dur)
    {
        long seconds = dur.getSeconds();
        long hours = seconds / 3600;
        seconds = seconds % 3600;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return (hours>0 ? hours+":" : "")+(minutes>0 ? (minutes<10 ? "0" : "")+minutes+":" : "")+(seconds<10 ? "0" : "")+seconds;
    }
    
    public class YoutubeInfo {
        public final String url;
        public final String title;
        public final String duration;
        public YoutubeInfo(String url, String title, String duration)
        {
            this.url = url;
            this.title = title;
            this.duration = duration;
        }
    }
}
