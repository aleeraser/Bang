package com.bang.utils;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class NetworkUtils {

    static String baseURL = "http://marullo.cs.unibo.it:5002";

    public static String getBaseURL() {
        // return "http://emilia.cs.unibo.it:5002";
        // return "http://marullo.cs.unibo.it:5002";
        // return "http://localhost:5002";
        return baseURL;
    }

    public static void setBaseURL(String newBaseURL) {
        baseURL = newBaseURL;
    }

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

    public static String getHTTP(String _url, String[] params, String[] vals) {
        String result;

        try {
            String url_s = _url;

            for (int i = 0; i < params.length; i++) {
                url_s += "&" + URLEncoder.encode(params[i], "UTF-8") + "=" + URLEncoder.encode(vals[i], "UTF-8");
            }

            URL url = new URL(url_s);
            InputStream is = url.openStream();
            result = IOUtils.toString(is, StandardCharsets.UTF_8);
            is.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from HTTP request (GET).", e);
        }

        return result;
    }

    public static JSONObject postHTTP(String _url, String param, String val) throws Exception {
        String url = _url + "&" + URLEncoder.encode(param, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
        JSONObject res = executePOST(url);
        return res;
    }

    public static JSONObject postHTTP(String _url, String[] params, String[] vals) throws Exception {
        String url = _url;

        for (int i = 0; i < params.length; i++) {
            url += "&" + URLEncoder.encode(params[i], "UTF-8") + "=" + URLEncoder.encode(vals[i], "UTF-8");
        }

        JSONObject res = executePOST(url);
        return res;
    }

    public static String postHTTP(String _url, JSONObject data) throws Exception {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(_url);
            StringEntity params = new StringEntity(data.toString());

            request.addHeader("content-type", "application/json");
            request.setEntity(params);

            Integer code = httpClient.execute(request).getStatusLine().getStatusCode();

            if (code != 200)
                throw new RuntimeException("POST request failed.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to perform POST to remote server (" + _url + ").", e);
        } finally {
            httpClient.close();
        }

        return "OK";
    }

    private static JSONObject executePOST(String url) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        String result = "";

        if (entity != null) {
            InputStream is = entity.getContent();
            try {
                result = IOUtils.toString(is, StandardCharsets.UTF_8);
            } finally {
                is.close();
            }
        }

        JSONObject res = new JSONObject(result);
        return res;
    }

    public static ArrayList<String> findAllIps() {
        SocketException exception = null;
        ArrayList<String> ips = new ArrayList<String>();
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String ip = i.getHostAddress();

                    ips.add(ip);
                }
            }
            return ips;
        } catch (SocketException e) {
            e.printStackTrace();
            exception = e;
        }

        return ips;
    }
}
