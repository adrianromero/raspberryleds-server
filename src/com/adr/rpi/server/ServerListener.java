
package com.adr.rpi.server;

/**
 *
 * @author adrian
 */
public interface ServerListener {

    public void starting();
    public void started();
    public void failure();
    public void stopping();
    public void stopped();
}
