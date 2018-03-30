package com.bang.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bang.game.Bang;

public class DesktopLauncher {
	public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        System.setProperty("java.rmi.server.hostname", "192.168.1.42");
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.height = 700;
		config.width = 1200;
		config.resizable = false;
		new LwjglApplication(new Bang(), config);
	}
}
