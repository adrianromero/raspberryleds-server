

package com.adr.rpi.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author adrian
 */
public abstract class CORSHttpServlet extends HttpServlet {

    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(request, response);
    }
    
    protected void setCORSHeaders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String origin = request.getHeader("Origin");

        if (origin != null && !origin.equals("")) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, origin, accept, X-Requested-With");
            response.setHeader("Access-Control-Max-Age", "1000");
        }
    }
}
