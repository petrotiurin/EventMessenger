package com.example.mymodule.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by pettyurin on 23/12/2014.
 */
public class MainServlet extends HttpServlet {

    private DatastoreService datastore;
    public void init() throws ServletException
    {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        Query q = new Query("Event");
        // Use PreparedQuery interface to retrieve results
        PreparedQuery pq = datastore.prepare(q);
        String response_string = "";
        for (Entity result : pq.asIterable()) {
            response_string += "[";
            response_string += result.getProperty("latitude") + ",";
            response_string += result.getProperty("longitude") + ",";
            response_string += result.getProperty("distance");
            response_string += "]";
        }
        responseString(response, response_string);
    }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException{
        // Create new event
        Entity event = new Entity("Event");
        event.setProperty("name",request.getParameter("event"));
        event.setProperty("latitude",request.getParameter("lat"));
        event.setProperty("longitude",request.getParameter("lon"));
        event.setProperty("distance",request.getParameter("dist"));
        datastore.put(event);
        responseString(response, "Event created.");
    }

    private void responseString(HttpServletResponse response, String s)
            throws IOException{
        // Set response content type
        response.setContentType("text/html");
        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println(s);
    }

    public void destroy()
    {
        // do nothing.
    }
}
