package fr.omnilogie.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AuteursActivity extends SpecialActivity implements CallbackObject {
	private ListView listView;
	
	private ArrayList<HashMap<String, String>> auteurs = new ArrayList<HashMap<String, String>>();
	private Hashtable<String, Integer> idFromAuteur = new Hashtable<String, Integer>();
	AuteursActivity auteursActivity = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Liste des auteurs sur Omnilogie");
		setContentView(R.layout.activity_auteurs);
		
		JSONRetriever jsonretriever = new JSONRetriever();
		jsonretriever.getJSONArrayfromURL("http://omnilogie.fr/raw/auteurs.json", this);
		
		listView = ((ListView) findViewById(R.id.auteurs));
		listView.setTextFilterEnabled(true);
			
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String nomAuteur = ((TextView) view.findViewById(R.id.item_auteurs_auteur_nomAuteur)).getText().toString();
				String auteur = Integer.toString(idFromAuteur.get(nomAuteur));
				
				Uri uri = Uri.parse("content://fr.omnilogie.app/auteur/" + auteur);
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
				}
			});

		isLoading(true);
	}
	
	
	/**
	 * Termine la préparation de la vue, une fois que les données distantes sont récupérées.
	 * Doit être lancé dans le thread UI (ex. runOnUiThread(initialiseViewWithData);)
	 * 
	 */
	protected Runnable remplirUIAvecDatas = new Runnable() {

		public void run(){
			isLoading(false);
			
			ListAdapter adapter = new SimpleAdapter(auteursActivity, auteurs , R.layout.item_auteurs_auteur, 
					new String[] { "Auteur", "NombreArticle" }, 
					new int[] { R.id.item_auteurs_auteur_nomAuteur, R.id.item_auteurs_auteur_nbArticles });
			
			listView.setAdapter(adapter);
		}
	};
	
	/**
	 * Méthode de callback utilisée pour traiter le JSON une fois récupéré.
	 * 
	 * @param objet JSONArray récupéré contenant la liste des auteurs
	 */
	public void callback(Object objet) {
		if(objet != null)
		{
			JSONArray jsonDatas = (JSONArray) objet;
			if(jsonDatas != null)
			{
				//Remplir notre article avec les données fournies
				for(int i = 0; i < jsonDatas.length(); i++)
				{
					HashMap<String, String> auteur = new HashMap<String, String>();  
					JSONObject data;
					try {
						data = jsonDatas.getJSONObject(i);
					
						auteur.put("ID", data.getString("ID"));
						auteur.put("Auteur", data.getString("A"));
						auteur.put("NombreArticle", data.getString("N") + (data.getString("N").equals("1")?" article publié":" articles publiés"));
						
						idFromAuteur.put(data.getString("A"), data.getInt("ID"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					auteurs.add(auteur);
				}
				
				runOnUiThread(remplirUIAvecDatas);
			}
		}
	}
}

