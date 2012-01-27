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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.WebView;;

public class ArticleActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);
		
		JSONObject datas = getJSONfromURL("http://omnilogie.fr/raw/articles/1209.json");
		try {
			//Commencer par charger la bannière si nécessaire
			ImageDownloader downloader = new ImageDownloader((ImageView) findViewById(R.id.banniere));
			downloader.execute(datas.getString("Banniere"));
			
			//Gérer l'affichage du titre.
			//CElui-ci peut contenir des entités HTML : il faut donc le convertir en texte Spanned
			((TextView) findViewById(R.id.titre)).setText(Html.fromHtml(datas.getString("Titre")));
			
			//Afficher l'accroche. Si elle n'existe pas, masquer le composant afin de gagner de la place.
			if(datas.isNull("Accroche"))
			{
				((TextView) findViewById(R.id.accroche)).setVisibility(View.GONE);
			}
			else
			{
				((TextView) findViewById(R.id.accroche)).setText(Html.fromHtml(datas.getString("Accroche")));
			}

			//Rendre le contenu de l'article.
			//Il faut spécifier une URL de base afin que les images soient disponibles.
			((WebView) findViewById(R.id.contenu)).loadDataWithBaseURL("http://omnilogie.fr", datas.getString("Omnilogisme"), "text/html", "UTF-8", null);
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