package com.github.demetdincoguz.urlshortener;

import spark.Spark;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws UnknownHostException {
        int port = parsePort(args);

        DBTemplate template = DBTemplate.getInstance();
        Spark.setPort(port);

        //Listens http get requests for shortUrls, resolves them to longUrl and redirects
        Spark.get(new RedirectRoute("/:id",template));

        //Listens the homepage requests
        Spark.get(new HomePageRoute("/"));

        //Listens the post requests for longUrls to shorten them
        Spark.post(new UrlShortenerRoute("/longUrl", template, port));
    }

    private static int parsePort(String[] args) {
        if (args.length > 0) {
            return Integer.valueOf(args[0]);
        } else {
            throw new IllegalArgumentException("port parameter is needed to run the application");
        }
    }

}
