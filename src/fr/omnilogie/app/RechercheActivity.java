package fr.omnilogie.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Cette activité permet de faire des recherches en utilisant Google.
 * 
 * Documentation de l'API Google : https://developers.google.com/web-search/docs/#fonje
 * 
 * @author neamar
 *
 */
public class RechercheActivity extends SpecialActivity implements CallbackObject {
	private ArrayList<HashMap<String, Spanned>> resultats;
	private ArrayList<String> urls;
	protected ListView listView;
	protected RechercheActivity rechercheActivity = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Rechercher dans les omnilogismes");
		setContentView(R.layout.activity_recherche);
		listView = (ListView) findViewById(R.id.rechercher_liste);
		
		ImageButton buttonRecherche = (ImageButton) findViewById(R.id.rechercher_button);
		
		buttonRecherche.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				lancerRecherche();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Uri uri = Uri.parse("content://fr.omnilogie.app/article/" + urls.get(position));
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
				}
			});

	}
	
	
	/**
	 * Lance la recherche sur le texte actuellement entré dans l'EditText.
	 */
	protected void lancerRecherche()
	{
		EditText textRecherche = (EditText) findViewById(R.id.rechercher_text);

		// URL de base pour l'API Google
		String baseUrl = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=large&q=";
		
		// Restreindre aux articles
		String restrainQuery = "site%3Aomnilogie.fr+intitle%3A%22Un+article+d%27Omnilogie.fr%22+";
		
		String userQuery = URLEncoder.encode(textRecherche.getText().toString());
		
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONfromURL(baseUrl +restrainQuery+userQuery, this);
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
			resultats = new ArrayList<HashMap<String,Spanned>>();
			urls = new ArrayList<String>();
			try {
				JSONArray jsonArray = jsonDatas.getJSONObject("responseData").getJSONArray("results");
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONObject jsonResult = jsonArray.getJSONObject(i);
					
					HashMap<String,Spanned> resultat = new HashMap<String,Spanned>();
					
					resultat.put("titre", Html.fromHtml(Html.toHtml(decodeJson(jsonResult, "titleNoFormatting")).replace("| Un article d'Omnilogie.fr", "")));
					resultat.put("fragment", decodeJson(jsonResult, "content"));
					
					urls.add(jsonResult.getString("unescapedUrl").replace("http://omnilogie.fr/O/", ""));
					
					resultats.add(resultat);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			runOnUiThread(remplirUIAvecDatas);
		}
	}
	
	protected Spanned decodeJson(JSONObject jsonObject, String key) throws UnsupportedEncodingException, JSONException
	{
		return Html.fromHtml(new String(jsonObject.getString(key).getBytes("ISO-8859-1"), "UTF-8"));
	}
	
	/**
	 * Termine la préparation de la vue, une fois que les données distantes ont été récupérées.
	 *
	 */
	protected Runnable remplirUIAvecDatas = new Runnable() {

		public void run()
		{
			ListAdapter adapter = new SimpleAdapter(rechercheActivity, resultats , R.layout.item_recherche_resultat, 
					new String[] { "titre", "fragment" }, 
					new int[] { R.id.item_recherche_resultat_titre, R.id.item_recherche_resultat_fragment });
			
			listView.setAdapter(adapter);
		}
	};
}
