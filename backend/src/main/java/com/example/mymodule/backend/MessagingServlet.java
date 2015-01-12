package com.example.mymodule.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.mymodule.backend.OfyService.ofy;

/**
 * Created by pettyurin on 10/01/2015.
 */
public class MessagingServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());
    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {
        String msg = request.getParameter("message");
        sendMessage(msg);
    }

    private void sendMessage(String untrimmed_message) throws IOException {
        if(untrimmed_message == null || untrimmed_message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        String[] s = untrimmed_message.split(":",2);
        String regId = s[0];
        String message = s[1];
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).list();
        for(RegistrationRecord record : records) {
            if (record.getRegId().equals(regId)) continue;
            Result result = sender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                }
                else {
                    log.warning("Error when sending message : " + error);
                }
            }
        }
    }


}
