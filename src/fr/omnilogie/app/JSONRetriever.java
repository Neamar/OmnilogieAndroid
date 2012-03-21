package fr.omnilogie.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * Classe permettant de récupérer des éléments JSON de manière asynchrone.
 * 
 * @author Benoit
 *
 */
public class JSONRetriever {
	/**
	 * L'objet à "réveiller" une fois le chargement terminé.
	 * Dans la plupart des cas, il s'agira d'une activité implémentant CallbackObject.
	 */
	protected CallbackObject callbackObject;
	
	/**
	 * L'URL à télécharger
	 */
	protected String url;
	
	/**
	 * Récupère un élément JSONObject à l'url spécifiée, de manière asynchrone.
	 * 
	 * @param url l'URL à télécharger
	 * @param callbackObject l'object CallbackObject à appeler à la fin de l'opération
	 * @return (via la méthode callback du paramètre callbackObject) le JSONObject récupéré
	 */
	public void getJSONfromURL(String url, CallbackObject callbackObject){
		this.url = url;
		this.callbackObject = callbackObject;
		
		Thread thread = new Thread(null, downloadJSONObject);
		thread.setPriority(Thread.NORM_PRIORITY-1);
		thread.start();
	}

	
	/**
	 * Récupère un élément JSONArray à l'url spécifiée, de manière asynchrone.
	 * 
	 * @param url l'URL à télécharger
	 * @param callbackObject l'object CallbackObject à appeler à la fin de l'opération
	 * @return (via la méthode callback du paramètre callbackObject) le JSONArray récupéré
	 */
	public void getJSONArrayfromURL(String url, CallbackObject callbackObject){
		this.url = url;
		this.callbackObject = callbackObject;
		
		Thread thread = new Thread(null, downloadJSONArray);
		thread.setPriority(Thread.NORM_PRIORITY-1);
		thread.start();
	}
	
	/**
	 * Récupère la chaîne de caractères JSON à l'url spécifiée.
	 * 
	 * @see http://p-xr.com/android-tutorial-how-to-parse-read-json-data-into-a-android-listview/
	 * @author Androidpxr | p-xr.com
	 * @param url url source au format String
	 * @return String récupéré contenant le JSON
	 */
	protected String retrieveJSONResult(String url)
	{
		InputStream is = null;
		String result = "";
		
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch(Exception e) {
			Log.e("omni", e.toString());
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
			Log.e("omni", e.toString());
		}
		
		return result;
	}
	
	/**
	 * Routine de téléchargement d'un JSONArray.
	 * L'url et l'objet sur lequel effectuer le callback doivent être spécifiés.
	 */
	protected Runnable downloadJSONArray = new Runnable()
	{
		public void run() {
			if(callbackObject != null && url != null)
			{
				JSONArray jArray = null;
				
				try{
					String result = retrieveJSONResult(url);
					
					if(result != null && result.length() > 0)
						jArray = new JSONArray(result);
				}catch(Exception e){
					Log.e("omni", e.toString());
				}
				
				callbackObject.callback(jArray);
			}
		}
	};
	
	/**
	 * Routine de téléchargement d'un JSONObject.
	 * L'url et l'objet sur lequel effectuer le callback doivent être spécifiés.
	 */
	protected Runnable downloadJSONObject = new Runnable() {
		public void run() {
			if(callbackObject != null && url != null)
			{
				JSONObject jObject = null;
				
				try{
					String result = retrieveJSONResult(url);
					
					if(result != null && result.length() > 0)
						jObject = new JSONObject(result);
				}catch(Exception e){
					Log.e("omni", e.toString());
				}
				
				callbackObject.callback(jObject);
			}
		}
	};
}
