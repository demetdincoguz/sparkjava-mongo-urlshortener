package com.github.demetdincoguz.urlshortener;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class UrlShortenerIntegrationTest {

    private static CloseableHttpClient httpClient;
    private static final String PORT = "9091";

    @BeforeClass
    public static void beforeClass() throws UnknownHostException {
        Main.main(new String[] {PORT});
        httpClient =  HttpClients.custom().disableRedirectHandling().build();
    }

    @Test(groups = "integration")
    public void shouldRedirect() throws IOException {

        String longUrl = "https://github.com/demetdincoguz/sparkjava-mongo-urlshortener";
        HttpPost httpPost = new HttpPost(String.format("http://localhost:%s/longUrl", PORT));

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("longUrl", longUrl));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 201);
        String actualShortUrl = IOUtils.toString(httpResponse.getEntity().getContent());

        HttpGet httpGet = new HttpGet(actualShortUrl);
        httpResponse = httpClient.execute(httpGet);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 301);
        String actualLongUrl = IOUtils.toString(httpResponse.getEntity().getContent());

        Assert.assertEquals(actualLongUrl, longUrl);
    }

}