/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.http;

import com.o3.bitcoin.util.ResponseResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author
 */
public class HttpPostClient {
 
    public static String post(String url, Map<String,String> params) throws UnsupportedEncodingException, IOException {
        String resString = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        String key;
        String value;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        Iterator itr = params.keySet().iterator();
        while(itr.hasNext())
        {
            key = (String)itr.next();
            value= params.get(key);
            nameValuePairs.add(new BasicNameValuePair(key, value));
        }
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
          System.out.println(line);
            resString += line;
          }

        return resString;
    }
    
    private static String createJsonString(Map<String,String> params) {
        String urlParameters;
        urlParameters = "{";
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if(index > 0)
                urlParameters += ",\""+ entry.getKey() +"\":\""+entry.getValue()+"\"";
            else
                urlParameters += "\""+ entry.getKey() +"\":\""+entry.getValue()+"\"";
            index = 1;
        }
        urlParameters += "}";
        return urlParameters;
    }
    
    public static ResponseResult postJson(String url, Map<String,String> params) throws IOException {
        String result = "";
        ResponseResult resResult = new ResponseResult();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity json = new StringEntity(createJsonString(params));
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(json);
        HttpResponse response = client.execute(httpPost);
        // Get the response
        System.out.println("Status Code="+response.getStatusLine().getStatusCode());
        resResult.setStatusCode(response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        resResult.setResponseText(result);
        return resResult;
    }
}
