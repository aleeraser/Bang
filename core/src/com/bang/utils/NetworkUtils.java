package com.bang.utils;

import java.io.InputStream;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

// import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class NetworkUtils {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static String getHTTP(String _url) {
        String result;

        // try {
        //     URL url = new URL(_url);
        //     InputStream is = url.openStream();
        //     result = IOUtils.toString(is, StandardCharsets.UTF_8);
        //     is.close();
        // } catch (Exception e) {
        //     throw new RuntimeException("Failed to fetch data from HTTP request (GET).", e);
        // }
        // return result;

        try {
            URL obj = new URL(_url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            result = response.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from HTTP request (GET).", e);
        }

        return result;
    }

    public static void postHTTP(String _url) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(_url);

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
