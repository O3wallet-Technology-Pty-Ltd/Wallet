/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

/**
 *
 * @author
 */
public class HttpGetClient {

    public static String getFee(String url) throws IOException {
        String result = "";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        // Get the response
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        }
        return result;
    }

    public static String getValuesFromUrl(String url) throws IOException {
        String result = "";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        // Get the response
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        }
        return result;
    }
    public static String getValuesFromUrlWithBase64Auth(String url,String encoding) throws IOException {
        String result = "";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization",  encoding);
        HttpResponse response = client.execute(request);
        // Get the response
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        }
        return result;
    }

    public static String makeLtcToBtcTransaction(String url, String urlParameters) throws IOException {

        String result = "";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity(urlParameters);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(params);

        HttpResponse response = client.execute(httpPost);

        // Get the response
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        }
        return result;
    }
    
    public static String placeOrderTransaction(String url, String urlParameters) throws IOException {

        String result = "";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity(urlParameters);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(params);
        HttpResponse response = client.execute(httpPost);
        // Get the response
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        }
        return result;
    }
    
    public static String placeOrderTransaction(String url, String urlParameters,String encoding) throws IOException {

        String result = "";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity params = new StringEntity(urlParameters);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization",  encoding);
        httpPost.setEntity(params);

        HttpResponse response = client.execute(httpPost);

        // Get the response
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        }
        return result;
    }
}
