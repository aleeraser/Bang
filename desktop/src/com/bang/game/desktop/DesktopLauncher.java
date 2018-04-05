package com.bang.game.desktop;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bang.game.Bang;

public class DesktopLauncher {
	public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        System.setProperty("java.rmi.server.hostname", getPriorityIP());
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.height = 700;
		config.width = 1200;
		config.resizable = false;
		new LwjglApplication(new Bang(), config);
	}
	
	public static String getPriorityIP() {
		String ip;
		
		ip = getSpecificIP("130.[0-9]+.[0-9]+.[0-9]+");
		if (ip != null) return ip;
		
		ip = getSpecificIP("10.103.[0-9]+.[0-9]+");
		if (ip != null) return ip;
		
		ip = getSpecificIP("192.168.[0-9]+.[0-9]+");
		if (ip != null) return ip;
		
		return "127.0.0.1";
	}
	
	public static String getSpecificIP(String pattern) {
		SocketException exception = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String ip = i.getHostAddress();
                    if (ip.matches(pattern)) {
                        return (ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            exception = e;
        }

        return null;
	}
}
