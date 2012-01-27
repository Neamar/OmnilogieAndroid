package fr.omnilogie.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ArticleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);
        
        JSONObject datas = getJSONfromURL("http://omnilogie.fr/raw/articles/1208.json");
        try {
			String titre = datas.getString("Titre");
			String accroche = datas.getString("Accroche");
			String omnilogisme = datas.getString("Omnilogisme");
		} catch (JSONException e) {
			// TODO : gérer les erreurs
			e.printStackTrace();
		}
    }
    
    protected JSONObject getJSONfromURL(String url){

    	//initialize
    	InputStream is = null;
    	String result = "";
    	JSONObject jArray = null;

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
    		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
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

    	//try parse the string to a JSON object
    	try{
            	jArray = new JSONObject(result);
    	}catch(JSONException e){
    		Log.e("log_tag", "Error parsing data "+e.toString());
    	}

    	return jArray;
    }
}