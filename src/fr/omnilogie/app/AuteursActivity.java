package fr.omnilogie.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AuteursActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Liste des auteurs sur Omnilogie");
		
		JSONArray jsonArray = JSONfunctions.getJSONArrayfromURL("http://omnilogie.fr/raw/auteurs.json");
		
		ArrayList<HashMap<String, String>> auteurs = new ArrayList<HashMap<String, String>>();
		
		for(int i = 0; i < jsonArray.length(); i++)
		{
			HashMap<String, String> auteur = new HashMap<String, String>();  
			JSONObject data;
			try {
				data = jsonArray.getJSONObject(i);
			
				auteur.put("ID", data.getString("ID"));
				auteur.put("Auteur", data.getString("A"));
				auteur.put("NombreArticle", data.getString("N"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			auteurs.add(auteur);
		}
		
		ListAdapter adapter = new SimpleAdapter(this, auteurs , R.layout.item_auteurs_auteur, 
			new String[] { "Auteur", "NombreArticle" }, 
			new int[] { R.id.item_auteurs_auteur_nomAuteur, R.id.item_auteurs_auteur_nbArticles });
		
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

	}
}

