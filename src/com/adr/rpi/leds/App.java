/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.rpi.leds;

import com.adr.rpi.server.AppConfig;
import com.adr.rpi.server.ServerManager;
import com.adr.rpi.server.ServerManagerJetty;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.Spi;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class App {

    private AppConfig config = null;
    
    private GpioController gpio = null;
    private PiFace piface = null;
    
    private ServerManager server = null;    
    
    public static void main(String[] args) {
        App app = new App(args);
    }
    
    public App(String[] args) {
        
        try {
            config = new AppConfig(args);
            config.load();
            
            // gpio initialization
            gpio = GpioFactory.getInstance();
            piface = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
//            if (false) throw new IOException();
            
            // server initialization
            server = new ServerManagerJetty();
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("piface", piface);
            
            server.init(Integer.parseInt(config.getProperty("server.port")), attributes);
            server.addServlet("/rpi", "com.adr.rpi.servlet.RaspberryServlet");   
            
    //        server.setListener(this);
            server.start();        
         } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }           
            
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // server is stopped at shudown automatically.
                
                // shutting down gpio
                gpio.shutdown();
            }
        });

    }
    
    public PiFace getPiFace() {
        return piface;
    }
}
