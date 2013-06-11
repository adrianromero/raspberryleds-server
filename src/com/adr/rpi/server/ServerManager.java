

package com.adr.rpi.server;

import java.util.Map;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author adrian
 */
public interface ServerManager {

    public static final int STARTING = 0;
    public static final int STARTED = 1;
    public static final int STOPPED = 2;
    public static final int STOPPING = 3;
    public static final int FAILURE = 4;

    public void init(int port, Map<String, Object> attributes);
    public void addServlet(String path, HttpServlet servlet);
    public void addServlet(String path, String classname);
    public void setListener(ServerListener listener);
    public void start();
    public void stop();
    public int getState();
    public String printName();
    public String printStatus();
}
