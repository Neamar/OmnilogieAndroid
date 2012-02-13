package fr.omnilogie.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.EditText;
import android.widget.ImageButton;

public class RechercheActivity extends SpecialActivity implements CallbackObject {
	protected JSONArray results;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Rechercher dans les omnilogismes");
		setContentView(R.layout.activity_recherche);
		
		ImageButton buttonRecherche = (ImageButton) findViewById(R.id.rechercher_button);
		
		buttonRecherche.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				lancerRecherche();
			}
		});

	}
	
	/**
	 * Termine la préparation de la vue, une fois que les données distantes sont récupérées.
	 * Doit être lancé dans le thread UI (ex. runOnUiThread(initialiseViewWithData);)
	 *  
	 */
	protected Runnable remplirUIAvecDatas = new Runnable() {

		public void run()
		{
			try {
				Log.e("wtf", results.getJSONObject(0).getString("url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	
	/**
	 * Lance la recherche sur le texte actuellement entré dans l'EditText.
	 */
	protected void lancerRecherche()
	{
		EditText textRecherche = (EditText) findViewById(R.id.rechercher_text);

		// URL de base pour l'API Google
		String baseUrl = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		
		// Restreindre aux articles
		String restrainQuery = "site%3Aomnilogie.fr+intitle%3A%22Un+article+d%27Omnilogie.fr%22+";
		
		String userQuery = URLEncoder.encode(textRecherche.getText().toString());
		
		JSONRetriever jsonRetriver = new JSONRetriever();
		jsonRetriver.getJSONfromURL(baseUrl +restrainQuery+userQuery, this);
	}
	
	/**
	 * Méthode de callback utilisée pour traiter le JSON une fois récupéré.
	 * 
	 * @param objet JSON récupéré contenant les données de l'article
	 */
	public void callback(Object objet) {
		JSONObject jsonDatas = (JSONObject) objet;
		if(jsonDatas != null)
		{
			try {
				results = jsonDatas.getJSONObject("responseData").getJSONArray("results");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			runOnUiThread(remplirUIAvecDatas);
		}
		
		
	}
}
