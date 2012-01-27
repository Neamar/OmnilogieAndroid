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
import android.widget.TextView;

public class ArticleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);
        
        JSONObject datas = getJSONfromURL("http://omnilogie.fr/raw/articles/1208.json");
        try {
			((TextView) findViewById(R.id.titre)).setText(datas.getString("Titre"));
			((TextView) findViewById(R.id.titre)).setText(datas.getString("Accroche"));
			String omnilogisme = datas.getString("Omnilogisme");
		} catch (JSONException e) {
			// TODO : gérer les erreurs
			e.printStackTrace();
		}
    }
    
    protected JSONObject getJSONfromURL(String url){

    	//Initialiser la lecture
    	InputStream is = null;
    	String result = "";
    	JSONObject jArray = null;

    	//Effectuer la requête POST
    	try{
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost(url);
    		HttpResponse response = httpclient.execute(httppost);
    		HttpEntity entity = response.getEntity();
    		is = entity.getContent();

    	}catch(Exception e){
    		Log.e("log_tag", "Error in http connection "+e.toString());
    	}

    	//Convertir la réponse sous forme de String
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

    	//Transformer le résultat en objet JSON
    	try{
            	jArray = new JSONObject(result);
    	}catch(JSONException e){
    		Log.e("log_tag", "Error parsing data "+e.toString());
    	}

    	return jArray;
    }
}