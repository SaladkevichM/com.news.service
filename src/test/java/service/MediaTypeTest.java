package service;

import static org.junit.Assert.*;

import com.news.util.Utility;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

/**
 * Test case for MediaType JSON
 * @author Mikita_Saladkevich
 *
 */
public class MediaTypeTest {

    HttpUriRequest requestHeadlines;
    HttpUriRequest requestSources;

    @Before
    public void setUp() {
        requestHeadlines = new HttpGet(Utility.getProperty("headlines_test_url") + "?sources=" + Utility.getProperty("default_sources"));
        requestSources = new HttpGet(Utility.getProperty("sources_test_url"));
    }

    @Test
    public void testHeadlines() throws ClientProtocolException, IOException {
        // When
        HttpResponse response = HttpClientBuilder.create().build().execute(requestHeadlines);

        // Then
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(MediaType.APPLICATION_JSON, mimeType);
    }
    
    @Test
    public void testSources() throws ClientProtocolException, IOException {
        // When
        HttpResponse response = HttpClientBuilder.create().build().execute(requestSources);

        // Then
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(MediaType.APPLICATION_JSON, mimeType);
    }

}