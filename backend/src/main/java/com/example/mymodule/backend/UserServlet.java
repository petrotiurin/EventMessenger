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
 * Created by pettyurin on 24/12/2014.
 */
public class UserServlet extends HttpServlet {

    private DatastoreService datastore;
    public void init() throws ServletException
    {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {

        String event = request.getParameter("event");
        String username = request.getParameter("username");
        if (event == null){
            // delete user

            Query.Filter usernameFilter =
                    new Query.FilterPredicate("name",
                            Query.FilterOperator.EQUAL,
                            username);
            Query q = new Query("User").setFilter(usernameFilter);
            PreparedQuery pq = datastore.prepare(q);
            try {
                datastore.delete(pq.asSingleEntity().getKey());
                responseString(response, "User deleted");
            } catch (PreparedQuery.TooManyResultsException e){
                responseString(response, "More than one user with matching username");
            }
        } else {
            Query.Filter eventFilter =
                    new Query.FilterPredicate("name",
                            Query.FilterOperator.EQUAL,
                            event);
            Query q = new Query("Event").setFilter(eventFilter);
            PreparedQuery pq = datastore.prepare(q);
            try {
                Entity user = new Entity("User", pq.asSingleEntity().getKey());
                user.setProperty("name",username);
                datastore.put(user);
                responseString(response, "User created");
            } catch (PreparedQuery.TooManyResultsException e){
                responseString(response, "More than one event with matching name.");
            }
        }
    }

    private void responseString(HttpServletResponse response, String s)
            throws IOException{
        // Set response content type
        response.setContentType("text/html");
        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println(s);
    }

}
