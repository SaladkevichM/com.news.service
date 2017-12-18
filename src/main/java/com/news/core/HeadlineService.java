package com.news.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.news.beans.Article;
import com.news.ext.ServiceCaller;
import com.news.util.Utility;
import java.io.IOException;

import org.apache.http.HttpException;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

/**
 * Pulling news service
 */
public class HeadlineService implements ServiceURL {

    private Logger logger = Logger.getLogger(HeadlineService.class.getName());
    private ObjectMapper mapper = new ObjectMapper();
    private ServiceCaller serviceCaller;

    /**
     * Constructor receives ServiceCaller object
     * 
     * @param caller
     */
    public HeadlineService(ServiceCaller caller) {
        serviceCaller = caller;
    }

    /**
     * Fetch articles from source. Do pagination by @page & @pageSize
     */
    public Map<String, String> getNews(Map<String, String> params, Integer pageSize, Integer page) {

        Map<String, String> result = new HashMap<>();
        result.put("code", String.valueOf(Response.Status.OK.getStatusCode()));

        try {

            String json = serviceCaller.sendRequest(populateURL(params));
            List<Article> roll = new ObjectMapper().readValue(Utility.subItem(json, "articles"),
                    new TypeReference<List<Article>>() {});

            // newest posts are shown on top
            roll.sort((a1, a2) -> a2.getPublishedAt().compareTo(a1.getPublishedAt()));
            result.put("result", getCustomJson(Utility.getPage(roll, page, pageSize)));

        } catch (ParseException | IOException | HttpException internalException) {

            logger.log(Level.SEVERE, "HeadlineService.getNews()", internalException);
            result.put("code",
                    String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
            result.put("result", Utility.createError(500, internalException.getMessage()));
        }

        return result;
    }

    /**
     * Create API request URL from .properties file
     * 
     * @return String API_URL
     * @throws HttpException
     */
    public String getServiceURL() throws HttpException {
        return Utility.getProperty("headlines_url") + Utility.getProperty("apikey_prefix")
                + Utility.getProperty("apikey_token");
    }

    /**
     * Get custom JSON string from List<Article>
     * 
     * @return String json
     * @throws JsonProcessingException
     */
    private String getCustomJson(List<Article> articles) throws JsonProcessingException {
        Map<Map<String, String>, List<Article>> groups =
                articles.stream().collect(Collectors.groupingBy(Article::getSource));
        return mapper.writeValueAsString(groups);
    }

}
