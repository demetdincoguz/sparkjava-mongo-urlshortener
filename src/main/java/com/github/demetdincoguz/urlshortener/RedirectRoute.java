package com.github.demetdincoguz.urlshortener;

import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Redirects the given short url to its actual url
 */
public class RedirectRoute extends Route{

    private DBTemplate template;

    public RedirectRoute(String path,DBTemplate template) {
        super(path);
        this.template = template;
    }

    @Override
    public Object handle(Request request, Response response) {
        System.out.println("ID: " + request.params(":id"));
        // get from mongo
        String longUrl = template.getUrl(request.url());
        if ("".equals(longUrl)) {
            halt(HttpStatus.SC_NOT_FOUND, "Url not found ");
            return "not found";
        } else {
            response.status(HttpStatus.SC_MOVED_PERMANENTLY);
            response.header("Location", longUrl);
            return longUrl;
        }

    }
}