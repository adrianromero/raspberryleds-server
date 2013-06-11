
package com.adr.rpi.server;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;

/**
 *
 * @author adrian
 */
public class ServerManagerJetty implements ServerManager, Listener {

    private final static Logger logger = Logger.getLogger(ServerManagerJetty.class.getName());

    private Server srv = null;
    private ServletContextHandler context = null;
    private ServerListener listener;

    private int srvport = 0;
    private Throwable failure = null;

    @Override
    public void init(int svrport, Map<String, Object> attributes) {

        this.srvport = svrport;

        srv = new Server(srvport);
        srv.addLifeCycleListener(this);

        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        for (Entry<String, Object> entry: attributes.entrySet()) {
            context.setAttribute(entry.getKey(), entry.getValue());
        }
        srv.setHandler(context);

        srv.setStopAtShutdown(true);
     }

    @Override
    public void setListener(ServerListener listener) {
        this.listener = listener;
    }

    @Override
    public void addServlet(String path, HttpServlet servlet) {
        logger.log(Level.INFO, "Adding servlet to: {0}", path);
        context.addServlet(new ServletHolder(servlet), path);
    }
    
    @Override
    public void addServlet(String path, String classname) {
        logger.log(Level.INFO, "Adding servlet to: {0}", path);
        context.addServlet(classname, path);
    }

    @Override
    public void start() {
        if (srv.isStopped()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        srv.start();                       
                    } catch (Exception ex) {
                        // Logged by Jetty and managed by Lifecycle listener
                    }
                }
            });
            t.start();
        }
    }

    @Override
    public void stop() {
        if (srv.isRunning()) {
              
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        srv.stop();
                    } catch (Exception ex) {
                        // Logged by Jetty and managed by Lifecycle listener
                    }
                }
            });
            t.start();
        }
    }

    @Override
    public int getState() {
        if (srv.isFailed()) {
            return ServerManager.FAILURE;
        } else if (srv.isStarting()) {
            return ServerManager.STARTING;
        } else if (srv.isStarted()) {
            return ServerManager.STARTED;
        } else if (srv.isStopped()) {
            return ServerManager.STOPPED;
        } else if (srv.isStopping()) {
            return ServerManager.STOPPING;
        } else {
            return ServerManager.FAILURE;
        }
    }

    @Override
    public String printName() {

        StringBuilder status = new StringBuilder();
        status.append("Jetty web server version ");
        status.append(Server.getVersion());
        return status.toString();
    }

    @Override
    public String printStatus() {
        StringBuilder status = new StringBuilder();
        status.append(srv.getState());
        if (srv.isRunning()) {
            status.append(". Listening on: http://");
            try {
                status.append(java.net.InetAddress.getLocalHost().getCanonicalHostName());
            } catch (UnknownHostException ex) {
                status.append("<unknown address>");
            }
            status.append(":");
            status.append(Integer.toString(srvport));                      
        }
        status.append(".");
        if (failure != null) {
            status.append(" ");
            status.append(failure.getMessage());
            status.append(".");
        }
        return status.toString();
    }

    @Override
    public void lifeCycleStarting(LifeCycle lc) {
        logger.info("Jetty Starting.");
        failure = null;
        if (listener != null) {
            listener.starting();
        }
    }

    @Override
    public void lifeCycleStarted(LifeCycle lc) {
        logger.info("Jetty Started.");
        failure = null;
        if (listener != null) {
            listener.started();
        }
    }

    @Override
    public void lifeCycleFailure(LifeCycle lc, Throwable thrwbl) {
        logger.log(Level.WARNING, "Jetty Failed.", thrwbl);
        failure = thrwbl;
        if (listener != null) {
            listener.failure();
        }
    }

    @Override
    public void lifeCycleStopping(LifeCycle lc) {
        logger.info("Jetty Stopping.");
        failure = null;
        if (listener != null) {
            listener.stopping();
        }
    }

    @Override
    public void lifeCycleStopped(LifeCycle lc) {
        logger.info("Jetty Stopped.");
        failure = null;
        if (listener != null) {
            listener.stopped();
        }
    }
}
