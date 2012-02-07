﻿package fr.omnilogie.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class JSONRetriever {
		
	/**
	 * Récupère le JSONObject à l'url spécifié.
	 * 
	 * @param url, au format String
	 * @return le JSONObject
	 */
	public JSONObject getJSONfromURL(String url){
		JSONObject jObject = null;
		
		try{
			String result = retrieveJSONResult(url);
			
			if(result != null && result.length() > 0)
				jObject = new JSONObject(result);            
	    }catch(Exception e){
	            Log.e("log_tag", "Error parsing data "+e.toString());
	    }
		
	    return jObject;
	}
	
	/**
	 * Récupère le JSONArray à l'url spécifié.
	 * 
	 * @param url, au format String
	 * @return le JSONObject
	 */
	public JSONArray getJSONArrayfromURL(String url){
		JSONArray jArray = null;
		
		try{
			String result = retrieveJSONResult(url);
			
			if(result != null && result.length() > 0)
				jArray = new JSONArray(result);            
	    }catch(Exception e){
	            Log.e("log_tag", "Error parsing data "+e.toString());
	    }
		
	    return jArray;
	}
	
	/**
	 * Récupère le JSON au format String à l'url spécifié.
	 * 
	 * @param url, au format String
	 * @return String récupéré contenant le JSON
	 */
	private String retrieveJSONResult(String url)
	{
		InputStream is = null;
		String result = "";
		
		//http post
	    try{
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(url);
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();

	    }catch(Exception e){
	            Log.e("log_tag", "Error in http connection "+e.toString());
	    }
	    
	  //convert response to string
	    try{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();
	    }catch(Exception e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    }
	    
	    return result;
	}
	
}
