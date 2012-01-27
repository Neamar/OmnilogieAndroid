package fr.omnilogie.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.view.View;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class ListeActivity extends ListActivity {
	
	static final int ARTICLES_A_CHARGER = 20;
	int dernierArticle = 0; // id du dernier article chargé
	
	// Tableau qui contiendra les méta-données sur les articles
	ArrayList<HashMap<String, Spanned>> listeArticles = new ArrayList<HashMap<String, Spanned>>();
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setContentView(R.layout.listes);
	  
      chargerLesArticlesSuivants();
	  
      //TODO Renommer les 'items'
	  ListAdapter adapter = new SimpleAdapter(this, listeArticles , R.layout.list_item, 
              new String[] { "titre", "question", "auteur" }, 
              new int[] { R.id.item_title, R.id.item_subtitle, R.id.item_extra });

      setListAdapter(adapter);     
	  
      final ListView lv = getListView();
      lv.setTextFilterEnabled(true);
      lv.setOnItemClickListener(new OnItemClickListener() {
      	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
      		@SuppressWarnings("unchecked")
				HashMap<String, Spanned> o = (HashMap<String, Spanned>) lv.getItemAtPosition(position); 
      			String articleID = o.get("id").toString();
      			Log.v("todo", "Load article " + articleID);
      			//TODO charger l'article
      			chargerLesArticlesSuivants();
			}
		});
	}
	
	//TODO corriger ça
	protected void chargerLesArticlesSuivants()
	{
		String url = "http://omnilogie.fr/raw/articles.json?start="+dernierArticle
					+"&limit="+ARTICLES_A_CHARGER;
		JSONArray jsonArray = JSONfunctions.getJSONArrayfromURL(url);	  
		  
		  // Insert les éléments JSON dans listeArticles
	      try{
	      	     	
		        for(int i=0;i<jsonArray.length();i++){						
					HashMap<String, Spanned> map = new HashMap<String, Spanned>();	
					JSONObject e = jsonArray.getJSONObject(i);
					
					map.put("mapId", Html.fromHtml(String.valueOf(i+dernierArticle)));
					map.put("id", Html.fromHtml(e.getString("ID")));
		        	map.put("titre", Html.fromHtml(e.getString("T")));
		        	map.put("auteur", Html.fromHtml("par " + e.getString("A")));
		        	map.put("question", Html.fromHtml(e.getString("Q")));
		        	map.put("banniere", Html.fromHtml(e.getString("B")));
		        	listeArticles.add(map);
				}
		        dernierArticle += ARTICLES_A_CHARGER;
	      }catch(JSONException e)        {
	      	 Log.e("log_tag", "Error parsing data "+e.toString());
	      }
	      	      
	}
}
