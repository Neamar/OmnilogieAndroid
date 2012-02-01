package fr.omnilogie.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AuteursActivity extends ListActivity {
	private ArrayList<HashMap<String, String>> auteurs = new ArrayList<HashMap<String, String>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Liste des auteurs sur Omnilogie");
		
		JSONArray jsonArray = JSONfunctions.getJSONArrayfromURL("http://omnilogie.fr/raw/auteurs.json");
		
		for(int i = 0; i < jsonArray.length(); i++)
		{
			HashMap<String, String> auteur = new HashMap<String, String>();  
			JSONObject data;
			try {
				data = jsonArray.getJSONObject(i);
			
				auteur.put("ID", data.getString("ID"));
				auteur.put("Auteur", data.getString("A"));
				auteur.put("NombreArticle", data.getString("N") + " articles publiés");
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

			
		 lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	String auteur = auteurs.get(position).get("ID");
		    	
		    	Uri uri = Uri.parse("content://fr.omnilogie.app/auteur/" + auteur);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
	  			startActivity(i);
			    }
			  });

	}
}

