package fr.omnilogie.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AbsListView.OnScrollListener;


public class ListeActivity extends ListActivity {
	
	static final int ARTICLES_A_CHARGER = 20;
	
	String baseUrl;
	
	int dernierArticle = 0; // id du dernier article chargé
	Boolean updateEnCours = false;
	ArticleObjectAdapter adapter;
	ListeActivity listeActivity = this;
	
	// Tableau qui contiendra les méta-données sur les articles
	ArrayList<ArticleObject> listeArticles = new ArrayList<ArticleObject>();
	ArrayList<ArticleObject> nouveauxArticles;
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_listes);
	  
	  //Quelle liste afficher ?
	  //Choisir en fonction de l'URI.
	  Uri uri = getIntent().getData();
	  if(uri.getPath().startsWith("auteur/"))
		  baseUrl = "http://omnilogie.fr/raw/auteurs/" + uri.getLastPathSegment() + ".json";
	  else
		  baseUrl = "http://omnilogie.fr/raw/articles.json";
	  
	  Log.e("todo", baseUrl);
      tryExpandListView();
	  
      final ListView lv = getListView();
      lv.setTextFilterEnabled(true);
      
      // Initialisation du listener sur un clic
      lv.setOnItemClickListener(new OnItemClickListener() {
      	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
			ArticleObject article = listeArticles.get(position);
  			if(article != null)
  			{
  				Bundle bundle = new Bundle();
  				bundle.putInt("id", article.id);

  				Intent newIntent = new Intent(getApplicationContext(), ArticleActivity.class);
  				newIntent.putExtras(bundle);
  				startActivityForResult(newIntent, 0);
  			}
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
			String url = baseUrl + "?start="+dernierArticle+"&limit="+ARTICLES_A_CHARGER;
			
			JSONArray jsonArray = JSONfunctions.getJSONArrayfromURL(url);

			// reset la liste des nouveaux articles
			nouveauxArticles = new ArrayList<ArticleObject>();
			  
			  // Insert les éléments JSON dans listeArticles
		      try{
		      	     	
			        for(int i=0;i<jsonArray.length();i++){						

						ArticleObject nouvelArticle = new ArticleObject();
						nouvelArticle.remplirDepuisJSON( jsonArray.getJSONObject(i) );
						
						nouveauxArticles.add(nouvelArticle);
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
				
				for ( ArticleObject article : nouveauxArticles )
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
				  adapter = new ArticleObjectAdapter(listeActivity,listeArticles);
					  
		//			  new SimpleAdapter(listeActivity, listeArticles , R.layout.list_item, 
		//	              new String[] { "titre", "question", "auteur" }, 
		//	              new int[] { R.id.list_item_title, R.id.list_item_subtitle, R.id.list_item_extra });

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
