package com.github.demetdincoguz.urlshortener;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.utility.StringUtil;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.servlet.SparkApplication;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class UrlShortener {

    private static String urlBase = "http://localhost:4567/";

    public static void main(String[] args) {
        final Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(UrlShortener.class,"/templates/");

        Spark.get(new Route("/:id") {
            @Override
            public Object handle(Request request, Response response) {
                System.out.println("ID: " + request.params(":id"));
                // get from mongo
                try {
                    DBTemplate template = DBTemplate.getInstance();
                    String longUrl = template.getUrl(request.url());
                    if ("".equals(longUrl)) {
                        halt(404);
                    } else {
                        response.redirect(longUrl);
                    }
                    return "";
                } catch (UnknownHostException e) {
                    return "Unknown host";
                }
            }
        });
        Spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Template template = configuration.getTemplate("shortenUrl.ftl");
                    StringWriter writer = new StringWriter();

                    template.process(null,writer);
                    return writer;
                } catch (Exception e) {
                    halt(500);
                }
                return "";
            }
        });

        Spark.post(new Route("/longUrl") {
            @Override
            public Object handle(Request request, Response response) {

                final String longUrl = request.queryParams("longUrl");

                if (longUrl == null) {
                    return "Why dont you enter one?";
                } else {
                    try {
                        String shortUrl = shortenUrl(longUrl);
                        DBTemplate template = DBTemplate.getInstance();
                        if(template.saveUrl(shortUrl,longUrl))
                            return "Your new url  is " + shortUrl;
                        else{
                            return "Short url could not be created, please check your url and retry";
                        }
                    } catch (Exception e) {
                        return "Please check your url and try again";
                    }
                }
            }
        });
    }

    private static String shortenUrl(String longUrl) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
