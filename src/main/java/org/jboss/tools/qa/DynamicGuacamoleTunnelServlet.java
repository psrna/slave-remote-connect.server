
package org.jboss.tools.qa;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sourceforge.guacamole.GuacamoleException;
import net.sourceforge.guacamole.properties.GuacamoleProperties;
import net.sourceforge.guacamole.net.GuacamoleSocket;
import net.sourceforge.guacamole.net.GuacamoleTunnel;
import net.sourceforge.guacamole.net.InetGuacamoleSocket;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;
import net.sourceforge.guacamole.protocol.ConfiguredGuacamoleSocket;
import net.sourceforge.guacamole.servlet.GuacamoleSession;
import net.sourceforge.guacamole.servlet.GuacamoleHTTPTunnelServlet;

/*
 *  Guacamole - Clientless Remote Desktop
 *  Copyright (C) 2010  Michael Jumper
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


public class DynamicGuacamoleTunnelServlet extends GuacamoleHTTPTunnelServlet {

	String protocol;
	String host;
	String port;
	String username;
	String password;
	String width = null;
	String height =null;
	
	
    @Override
    protected GuacamoleTunnel doConnect(HttpServletRequest request) throws GuacamoleException {

    	System.out.println("@@ CONNECT!! protocol:" + protocol + 
    			                       " hostname:" + host + 
    			                       " port:" + port + 
    			                       " password:" + password + 
    			                       " username:" + username +
    			                       " width:" + width +
    			                       " height:" + height); 
    	
        HttpSession httpSession = request.getSession(true);

        String hostname = GuacamoleProperties.getProperty(GuacamoleProperties.GUACD_HOSTNAME);
        int port = GuacamoleProperties.getProperty(GuacamoleProperties.GUACD_PORT);

        GuacamoleConfiguration config = prepareConfig();
        
        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
                new InetGuacamoleSocket(hostname, port),
                config
        );

        GuacamoleTunnel tunnel = new GuacamoleTunnel(socket);

        // Attach tunnel
        GuacamoleSession session = new GuacamoleSession(httpSession);
        session.attachTunnel(tunnel);
        
        return tunnel;

    }

    private GuacamoleConfiguration prepareConfig(){
    	
    	GuacamoleConfiguration config = new GuacamoleConfiguration();
    	if(protocol != null && protocol.equals("vnc")){
    		config.setProtocol("vnc");
    		config.setParameter("hostname", host);
    		config.setParameter("port", port);
    		config.setParameter("password", password);
    	}else if(protocol != null && protocol.equals("rdp")){
    		config.setProtocol("rdp");
    		config.setParameter("hostname", host);
    		config.setParameter("port", port);
    		config.setParameter("password", password);
    		config.setParameter("username", username);
    		config.setParameter("width", width != null ? width : "800");
    		config.setParameter("height", height != null ? height : "600");
    	}
    	
    	return config;
    }

    @Override
    protected void doGet(HttpServletRequest request,
    		HttpServletResponse response) throws ServletException {
    	
    	super.doGet(request, response);
    	
    	if(request.getParameter("protocol") != null)
    		protocol = request.getParameter("protocol");
    	if(request.getParameter("hostname") != null)
    		host = request.getParameter("hostname");
    	if(request.getParameter("port") != null)
    		port = request.getParameter("port");
    	if(request.getParameter("password") != null)
    		password = request.getParameter("password");
    	if(request.getParameter("username") != null)
    		username = request.getParameter("username");
    	if(request.getParameter("height") != null)
    		height = request.getParameter("height");
    	if(request.getParameter("width") != null)
    		width = request.getParameter("width");

    }
    

}
