package com.bang.utils;

import java.io.InputStream;

import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class NetworkUtils {

    private static final String USER_AGENT = "Mozilla/5.0";

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

    public static void postHTTP(String _url, String param, String val) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        String url = _url + "&" + URLEncoder.encode(param, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
        UIUtils.print(url);
        HttpPost httppost = new HttpPost(url);

        CloseableHttpResponse response = (CloseableHttpResponse)httpclient.execute(httppost);
        // HttpEntity entity = response.getEntity();

        // if (entity != null) {
        //     InputStream instream = entity.getContent();
        //     try {
        //         // do something useful
        //     } finally {
        //         instream.close();
        //     }
        // }

        // return "Done.";
    }

    public static String postHTTP_JSON(String _url, JSONObject data) throws Exception {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(_url);
            StringEntity params = new StringEntity(data.toString());

            request.addHeader("content-type", "application/json");
            request.setEntity(params);

            Integer code = httpClient.execute(request).getStatusLine().getStatusCode();

            UIUtils.print(code.toString());

            if (code != 200) throw new RuntimeException("POST request failed.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to perform POST to remote server (" + _url + ").", e);
        } finally {
            httpClient.close();
        }

        return "OK";
    }
}
