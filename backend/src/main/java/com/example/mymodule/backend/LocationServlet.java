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
public class LocationServlet extends HttpServlet {

    private DatastoreService datastore;
    public void init() throws ServletException
    {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {

        Double latitude = Double.parseDouble(request.getParameter("lat"));
        Double longitude = Double.parseDouble(request.getParameter("lon"));
        Query q = new Query("Event");
        PreparedQuery pq = datastore.prepare(q);
        for (Entity result : pq.asIterable()) {
            Double eventLat = Double.parseDouble((String) result.getProperty("latitude"));
            Double eventLon = Double.parseDouble((String) result.getProperty("longitude"));
            Double eventDist = Double.parseDouble((String) result.getProperty("distance"));
            Double dist = distanceOnUnitSphere(latitude, longitude,
                                               eventLat, eventLon);
            if (dist <= eventDist) {
                responseString(response, (String) result.getProperty("name"));
//                responseString(response, ""+dist);
            }
            else responseString(response, "Away from any event.");
            responseString(response, ""+dist);
        }
    }

    private Double distanceOnUnitSphere(Double lat1, Double lon1,
                                        Double lat2, Double lon2){
        Double pi_to_rad = Math.PI/180.0;
        // phi = 90 - latitude
        Double phi1 = (90.0 - lat1)*pi_to_rad;
        Double phi2 = (90.0 - lat2)*pi_to_rad;

        // theta = longitude
        Double theta1 = lon1*pi_to_rad;
        Double theta2 = lon2*pi_to_rad;

        Double cos = (Math.sin(phi1)*Math.sin(phi2)*Math.cos(theta1 - theta2) +
                Math.cos(phi1)*Math.cos(phi2));
        Double arc = Math.acos( cos )*6373;
        return arc;
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
