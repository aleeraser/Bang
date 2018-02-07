package com.bang.utils;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class NetworkUtils {

	public static String getHTTP(String _url) {
        String result;
        try {
            URL url = new URL(_url);
            InputStream is = url.openStream();
            result = IOUtils.toString(is, StandardCharsets.UTF_8);
            is.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from HTTP request (GET).", e);
        }
        return result;
    }
	
}
