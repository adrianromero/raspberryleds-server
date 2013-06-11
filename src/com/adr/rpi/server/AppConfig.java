

package com.adr.rpi.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class AppConfig {

    private static final String CONFIG_FILE = "rpibase.properties";
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());

    private Properties m_propsconfig;
    private File configfile;
    private File filename;

    public AppConfig(String[] args) {
        if (args.length == 0) {
            init(getDefaultConfig());
        } else {
            init(new File(args[0]));
        }
    }

    public AppConfig(File configfile) {
        init(configfile);
    }

    private void init(File configfile) {
        this.configfile = configfile;
        m_propsconfig = new Properties();

        String dirname = System.getProperty("dirname.path");
        filename = new File(dirname == null ? "./" : dirname);
    }

    public File getFileName() {
        return filename;
    }

    private File getDefaultConfig() {
        return new File(filename, CONFIG_FILE);
    }

    public String getProperty(String sKey) {
        return m_propsconfig.getProperty(sKey);
    }

    public void load() {

        loadDefault();

        logger.log(Level.INFO, "Reading configuration file: {0}", configfile.getAbsolutePath());
        try (InputStream in = new FileInputStream(configfile)) {
            m_propsconfig.load(in);
        } catch (IOException ex){
            logger.log(Level.INFO, "Cannot read configuration file: {0}. Loading defaults.", configfile.getAbsolutePath());
        }
    }

    public void loadDefault() {

        m_propsconfig = new Properties();
        
        m_propsconfig.setProperty("application.version", "0.0.0");

        m_propsconfig.setProperty("server.application", "jetty"); // or tjws
        m_propsconfig.setProperty("server.port", "7110");
        m_propsconfig.setProperty("server.token", "");        
    }

    public String getText() {
        StringBuilder s = new StringBuilder();
        s.append("File: ");
        s.append(configfile.getAbsolutePath());
        s.append("\n");
        
        s.append("\n## Version ##\n");
        printProperty(s, "application.version");

        s.append("\n## Web server ##\n");
        printProperty(s, "server.application"); // jetty or tjws
        printProperty(s, "server.port");
        printProperty(s, "server.token");
        return s.toString();
    }

    public void printProperty(StringBuilder s, String prop) {
        s.append(prop);
        s.append(" = ");
        s.append(m_propsconfig.getProperty(prop, ""));
        s.append("\n");
    }
}
