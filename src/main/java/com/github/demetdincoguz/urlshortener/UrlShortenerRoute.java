package com.github.demetdincoguz.urlshortener;

import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Stores and returns the short url for any given url
 */
public class UrlShortenerRoute extends Route {

    private String urlBase;

    private DBTemplate template;

    public UrlShortenerRoute(String path, DBTemplate template, int port) {
        super(path);
        this.template = template;
        this.urlBase = String.format("http://localhost:%s/", String.valueOf(port));
    }

    @Override
    public Object handle(Request request, Response response) {
        final String longUrl = request.queryParams("longUrl");

        if (longUrl == null) {
            return "Why dont you enter one?";
        } else {
            try {
                String shortUrl = shortenUrl(longUrl);
                if(template.saveUrl(shortUrl,longUrl)) {
                    response.body(shortUrl);
                    response.status(HttpStatus.SC_CREATED);
                    return shortUrl;
                } else {
                    return errorResponse(response);
                }
            } catch (Exception e) {
                return errorResponse(response);
            }
        }
    }

    private Object errorResponse(Response response) {
        response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return "Short url could not be created, please retry later";
    }

    private String shortenUrl(String longUrl) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytesOfMessage = longUrl.getBytes("UTF-8");

        // get bytes from string
        Checksum checksum = new CRC32();

        // update the current checksum with the specified array of bytes
        checksum.update(bytesOfMessage, 0, bytesOfMessage.length);

        // get the current checksum value
        long checksumValue = checksum.getValue();
        return new StringBuilder().append(urlBase).append(checksumValue).toString();

    }
}
