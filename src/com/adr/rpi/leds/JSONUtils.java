

package com.adr.rpi.leds;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author adrian
 */
public class JSONUtils {

    private JSONUtils() {
    }

    public static JSONObject getExceptionResult(String msg) {
        JSONObject o = new JSONObject();
        try {
            o.put("exception", msg);
        } catch (JSONException ex) {
        }
        return o;
    }
    
    public static String getExceptionJSONP(String callback, Throwable t) {
        return getJSONP(callback, getExceptionResult(t.getMessage()));
    }

    public static String getExceptionJSONP(String callback, String msg) {
        return getJSONP(callback, getExceptionResult(msg));
    }

    public static String getJSONP(String callback, JSONObject o) {
        if (callback == null || callback.equals("")) {
            return o.toString();
        } else {
            return callback + "(" + o.toString() + ");";
        }
    }
}
