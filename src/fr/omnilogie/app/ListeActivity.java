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
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AbsListView.OnScrollListener;


public class ListeActivity extends ListActivity {
	
	static final int ARTICLES_A_CHARGER = 20;
	int dernierArticle = 0; // id du dernier article chargé
	Boolean updateEnCours = false;
	SimpleAdapter adapter;
	ListeActivity listeActivity = this;
	
	// Tableau qui contiendra les méta-données sur les articles
	ArrayList<HashMap<String, Spanned>> listeArticles = new ArrayList<HashMap<String, Spanned>>();
	ArrayList<HashMap<String, Spanned>> nouveauxArticles;
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setContentView(R.layout.listes);
	  
      tryExpandListView();
	  
      final ListView lv = getListView();
      lv.setTextFilterEnabled(true);
      
      // Initialisation du listener sur un clic
      lv.setOnItemClickListener(new OnItemClickListener() {
      	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
      		@SuppressWarnings("unchecked")
				HashMap<String, Spanned> o = (HashMap<String, Spanned>) lv.getItemAtPosition(position); 
      			String articleID = o.get("id").toString();
      			Log.v("todo", "Load article " + articleID);
      			//TODO charger l'article
			}
		});
      
      // Initialisation du listener de scroll pour catcher la fin de liste atteinte
      lv.setOnScrollListener(new OnScrollListener() {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// pas d'utilité ici
			
		}
		
		// à chaque mouvement, on regarde si le dernier est rendu visible
		public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				//what is the bottom iten that is visible
			    int lastInScreen = firstVisibleItem + visibleItemCount;  
			
				if (lastInScreen == totalItemCount)
				{
					tryExpandListView();
				}
		}
      });
	}
	
	// essaye d'afficher plus d'éléments dans la liste (appel d'un autre thread)
	protected void tryExpandListView()
	{
		if (updateEnCours)
		{
			Log.w("log_tag", "Attention : les articles suivants sont déjà en train d'être chargés.");
		}
		else
		{
			Log.v("log_tag", "Chargement de nouveaux articles...");
			updateEnCours = true;
			Thread thread = new Thread(null, loadMoreItems);
			thread.start();
		}
	}
	
	// télécharge les nouveaux items
	protected Runnable loadMoreItems = new Runnable() {
		
		public void run() {		
			String url = "http://omnilogie.fr/raw/articles.json?start="+dernierArticle
						+"&limit="+ARTICLES_A_CHARGER;
			
			JSONArray jsonArray = JSONfunctions.getJSONArrayfromURL(url);

			// reset la liste des nouveaux articles
			nouveauxArticles = new ArrayList<HashMap<String, Spanned>>();
			  
			  // Insert les éléments JSON dans listeArticles
		      try{
		      	     	
			        for(int i=0;i<jsonArray.length();i++){						
						HashMap<String, Spanned> map = new HashMap<String, Spanned>();	
						JSONObject e = jsonArray.getJSONObject(i);
						
						map.put("mapId", Html.fromHtml(String.valueOf(i+dernierArticle)));
						map.put("id", Html.fromHtml(e.getString("ID")));
			        	map.put("titre", Html.fromHtml(e.getString("T")));
			        	map.put("auteur", Html.fromHtml("par " + e.getString("A")));
			        	
			        	String accroche = e.getString("Q");
			        	if ( accroche.equals("null") ) 
			        	{
			        		accroche = "";
			        	}	
			        	map.put("question", Html.fromHtml(accroche));
			        	map.put("banniere", Html.fromHtml(e.getString("B")));
			        	nouveauxArticles.add(map);
					}
			        dernierArticle += jsonArray.length();
		      }catch(JSONException e)        {
		      	 Log.e("log_tag", "Error parsing data "+e.toString());
		      }
		      
		      // met à jour l'UI
		      runOnUiThread(majListView);
		};
	};
	
	// met à jour la listView dans l'UI
	protected Runnable majListView = new Runnable() {
		
		public void run() {
			
			try {
				
				for ( HashMap<String, Spanned> article : nouveauxArticles )
				{
					listeArticles.add(article);
				}
				
				if(adapter != null)
				{
					//Tell to the adapter that changes have been made, this will cause the list to refresh
					adapter.notifyDataSetChanged();
				}
				else
				{
					//TODO Renommer les 'items'
				  ListAdapter adapter = new SimpleAdapter(listeActivity, listeArticles , R.layout.list_item, 
			              new String[] { "titre", "question", "auteur" }, 
			              new int[] { R.id.item_title, R.id.item_subtitle, R.id.item_extra });

			      setListAdapter(adapter);
				}
	       		
			} catch (Exception e) {
				 Log.e("log_tag", "Erreur pendant l'update UI de la ListView "+e.toString());
			}
	
			// Fin de l'opération
			updateEnCours = false;
		}
	};
	
}
