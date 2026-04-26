package com.bang.game.desktop;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bang.game.Bang;

public class DesktopLauncher {
	public static void main (String[] arg) {
        try{
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            System.setProperty("java.rmi.server.hostname", getPriorityIP());
            config.setWindowedMode(1200, 700);
            config.setResizable(false);
            new Lwjgl3Application(new Bang(), config);
        }catch (Exception e) {
            //e.printStackTrace();
            Gdx.app.exit();
        }
	}
	
	public static String getPriorityIP() {
		String ip;
		
		ip = getSpecificIP("130.[0-9]+.[0-9]+.[0-9]+");
		if (ip != null) return ip;
		
		ip = getSpecificIP("10.1033.[0-9]+.[0-9]+");
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
            //e.printStackTrace();
            exception = e;
        }

        return null;
	}
}
