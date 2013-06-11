
package com.adr.rpi.servlet;

import com.adr.rpi.leds.JSONUtils;
import com.adr.web.common.utils.StringUtils;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceLed;
import com.pi4j.device.piface.PiFaceRelay;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author adrian
 */
public class RaspberryServlet extends CORSHttpServlet  {
    
    private static Logger logger = Logger.getLogger(RaspberryServlet.class.getName());


    public RaspberryServlet() {

    }
    
//    /**
//     * Handles the HTTP
//     * <code>GET</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        setCORSHeaders(request, response);
//        response.setContentType("application/json");
//
//        try {
//            JSONObject o = new JSONObject();
//            try {
//                o.put("result", ds.readWeight());
//            } catch (JSONException ex) {
//            }
//            response.getWriter().println(JSONUtils.getJSONP(request.getParameter("callback"), o));
//        } catch (ScaleException ex) {
//            Logger.getLogger(RaspberryServlet.class.getName()).log(Level.SEVERE, null, ex);
//            response.getWriter().println(JSONUtils.getExceptionJSONP(request.getParameter("callback"), ex));
//        }
//    }
    
    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        setCORSHeaders(request, response);
        response.setContentType("application/json");        
        
        String content;
        try (Reader r = request.getReader()) {
            content = StringUtils.readReader(r);
        }  
        
        try {  
        
            JSONObject result = processRequest(request, response, "POST", getContentAsJSON(content));
            response.getWriter().println(JSONUtils.getJSONP(request.getParameter("callback"), result));
        } catch (JSONException jsonex) {
            logger.log(Level.SEVERE, null, jsonex);
            response.getWriter().println(JSONUtils.getExceptionJSONP(request.getParameter("callback"), jsonex));
        }
    }          
    
    protected JSONObject processRequest(HttpServletRequest request, HttpServletResponse response, String method, JSONObject json) throws JSONException {
        
        PiFace piface = (PiFace) this.getServletContext().getAttribute("piface");
        
        JSONObject result = new JSONObject();    
        
        doLedAction(piface, PiFaceLed.LED0, json, result);
        doLedAction(piface, PiFaceLed.LED1, json, result);
        doLedAction(piface, PiFaceLed.LED2, json, result);
        doLedAction(piface, PiFaceLed.LED3, json, result);
        doLedAction(piface, PiFaceLed.LED4, json, result);
        doLedAction(piface, PiFaceLed.LED5, json, result);
        doLedAction(piface, PiFaceLed.LED6, json, result);
        doLedAction(piface, PiFaceLed.LED7, json, result);
        
        doRelayAction(piface, PiFaceRelay.K0, json, result);
        doRelayAction(piface, PiFaceRelay.K1, json, result);
            
        result.put("result", "success");        
        return result;             
    }
    
    protected void doLedAction(PiFace piface, PiFaceLed led, JSONObject json, JSONObject result) throws JSONException {
        
        JSONObject jsonaction = json.optJSONObject(led.toString());
        if (jsonaction == null) {
            jsonaction = new JSONObject();
            jsonaction.put("action",  json.optString(led.toString()));
        }

        String action = jsonaction.getString("action");
        if ("pulse".equals(action)) {
            piface.getLed(led).pulse(jsonaction.optLong("duration", 250L));
            result.put(led.toString(), piface.getLed(led).isOn());
        } else if ("blink".equals(action)) {
            piface.getLed(led).blink(jsonaction.optLong("delay", 250L), jsonaction.optLong("duration", 0L));
            result.put(led.toString(), piface.getLed(led).isOn());
        } else if ("toggle".equals(action)) {
            piface.getLed(led).toggle();
            result.put(led.toString(), piface.getLed(led).isOn());
        } else if ("on".equals(action)) {
            piface.getLed(led).on();
            result.put(led.toString(), piface.getLed(led).isOn());
        } else if ("off".equals(action)) {
            piface.getLed(led).off();
            result.put(led.toString(), piface.getLed(led).isOn());
        } else if ("get".equals(action)) {
            result.put(led.toString(), piface.getLed(led).isOn());
        }   
    }    
    protected void doRelayAction(PiFace piface, PiFaceRelay relay, JSONObject json, JSONObject result) throws JSONException {
        
        JSONObject jsonaction = json.optJSONObject(relay.toString());
        if (jsonaction == null) {
            jsonaction = new JSONObject();
            jsonaction.put("action",  json.optString(relay.toString()));
        }

        String action = jsonaction.getString("action");
        if ("pulse".equals(action)) {
            piface.getRelay(relay).pulse(jsonaction.optInt("duration", 250));
        } else if ("toggle".equals(action)) {
            piface.getRelay(relay).toggle();
        } else if ("open".equals(action)) {
            piface.getRelay(relay).open();
        } else if ("close".equals(action)) {
            piface.getRelay(relay).close();
        } else if ("get".equals(action)) {
            result.put(relay.toString(), piface.getRelay(relay).isOpen());            
        }
    }

    protected JSONObject getContentAsJSON(String content) throws JSONException {
        if (content == null || content.equals("")) {
            return new JSONObject();
        } else {
            return new JSONObject(content);
        }
    }    
}
