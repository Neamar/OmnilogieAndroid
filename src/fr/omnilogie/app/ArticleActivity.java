package fr.omnilogie.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.webkit.WebView;;

public class ArticleActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);
		
		JSONObject datas = JSONfunctions.getJSONfromURL("http://omnilogie.fr/raw/articles/1215.json");
		try {
			//Commencer par charger la bannière si nécessaire
			if(!datas.isNull("Banniere"))
			{
				ImageDownloader downloader = new ImageDownloader((ImageView) findViewById(R.id.banniere));
				downloader.execute(datas.getString("Banniere"));
			}
			
			//Gérer l'affichage du titre.
			//Celui-ci peut contenir des entités HTML : il faut donc le convertir en texte Spanned
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
			((WebView) findViewById(R.id.contenu)).loadDataWithBaseURL("http://omnilogie.fr", preparerArticle(datas.getString("Omnilogisme")), "text/html", "UTF-8", null);
			
			//Traiter les sources
			renderSources(datas.getJSONArray("Sources"));
		} catch (JSONException e) {
			// TODO : gérer les erreurs
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.article, menu);
		return true;
	}
	
	protected void renderSources(JSONArray sources)
	{
		ArrayList<HashMap<String, String>> listeSources = new ArrayList<HashMap<String, String>>();

		// Insert les éléments JSON dans listeArticles
		try {
			
			for(int i = 0;i < sources.length();i++)
			{
				HashMap<String, String> map = new HashMap<String, String>();	
				JSONObject e;
				
				e = sources.getJSONObject(i);
				
				map.put("url", e.getString("URL"));
				if(e.isNull("Titre"))
				{
					map.put("titre", e.getString("URL"));
				}
				else
				{
					map.put("titre", e.getString("Titre"));
				}
				
				listeSources.add(map);
			}		
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ListAdapter adapter = new SimpleAdapter(this, listeSources , R.layout.sources_item, 
			new String[] { "titre" }, 
			new int[] { R.id.sourceItemId });
		
		((ListView) findViewById(R.id.sourcesArticle)).setAdapter(adapter);
	}
	
	protected String preparerArticle(String article)
	{
		String baseHtml = "";
		try
		{
			InputStream fin = getAssets().open("article.html");
			byte[] buffer = new byte[fin.available()];
			fin.read(buffer);
			fin.close();
			
			baseHtml = new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baseHtml.replace("{{article}}", article);
	}
}