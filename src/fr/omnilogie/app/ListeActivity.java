package fr.omnilogie.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class ListeActivity extends ListActivity {
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setContentView(R.layout.listes);
	  
	  // Tableau qui contiendra les méta-données sur les articles
	  ArrayList<HashMap<String, String>> listeArticles = new ArrayList<HashMap<String, String>>();
      
	  JSONArray jsonArray = JSONfunctions.getJSONArrayfromURL("http://omnilogie.fr/raw/articles.json?limit=20");
	  //JSONObject json = JSONfunctions.getJSONfromURL("http://api.geonames.org/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username=demo");
	  
	  // Insert les éléments JSON dans listeArticles
      try{
      	     	
	        for(int i=0;i<jsonArray.length();i++){						
				HashMap<String, String> map = new HashMap<String, String>();	
				JSONObject e = jsonArray.getJSONObject(i);
				
				map.put("mapId", String.valueOf(i));
				map.put("id", e.getString("ID"));
	        	map.put("titre", e.getString("T"));
	        	map.put("auteur", e.getString("A"));
	        	map.put("question", e.getString("Q"));
	        	listeArticles.add(map);			
			}		
      }catch(JSONException e)        {
      	 Log.e("log_tag", "Error parsing data "+e.toString());
      }
      
      ListAdapter adapter = new SimpleAdapter(this, listeArticles , R.layout.list_item, 
                      new String[] { "titre", "question", "auteur" }, 
                      new int[] { R.id.item_title, R.id.item_subtitle, R.id.item_extra });
      
      setListAdapter(adapter);
      
      
      
      final ListView lv = getListView();
      lv.setTextFilterEnabled(true);
      lv.setOnItemClickListener(new OnItemClickListener() {
      	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
      		@SuppressWarnings("unchecked")
				HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position); 
      			String articleID = o.get("id");
      			Log.v("todo", "Load article " + articleID);
      			//TODO charger l'article
			}
		});
	}
}
