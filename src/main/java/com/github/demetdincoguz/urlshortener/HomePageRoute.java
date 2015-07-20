package com.github.demetdincoguz.urlshortener;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.StringWriter;

/**
 * Creates the home page
 */
public class HomePageRoute extends Route{
    public HomePageRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        final Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(HomePageRoute.class,"/templates/");

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
}
