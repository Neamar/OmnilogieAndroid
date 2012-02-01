package fr.omnilogie.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Récupère et affiche les articles récents dans un ListView.
 * 
 * @author Benoit
 *
 */
public class ListeActivity extends ListActivity {
	
	static final int ARTICLES_A_CHARGER = 20;
	int dernierArticle = 0; // id du dernier article chargé
	Boolean updateEnCours = false;
	ArticleObjectAdapter adapter;
	ListeActivity listeActivity = this;
	
	// Liste avec les méta-données des articles chargés
	ArrayList<ArticleObject> listeArticles = new ArrayList<ArticleObject>();
	// Liste avec les méta-données des articles à ajouter
	ArrayList<ArticleObject> nouveauxArticles = new ArrayList<ArticleObject>();
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setContentView(R.layout.activity_listes);
	  
	  // charge des articles
      tryExpandListView();
	  
      final ListView lv = getListView();
      lv.setTextFilterEnabled(true);
      
      // initialisation du listener sur un clic
      lv.setOnItemClickListener(new OnItemClickListener() {
      	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
			ArticleObject article = listeArticles.get(position);
  			if(article != null)
  			{
  				Log.v("todo", "Load article " + article.id);
  				//TODO charger l'article
  			}
      	}
      });
      
      // initialisation du listener de scroll pour catcher la fin de liste atteinte
      lv.setOnScrollListener(new OnScrollListener() {
		
    	// pas besoin de cette méthode
    	public void onScrollStateChanged(AbsListView view, int scrollState) {
			// vide
		}
		
		// à chaque mouvement, on regarde si le dernier est rendu visible
		public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				// calcul de l'index du dernier item affiché
			    int lastInScreen = firstVisibleItem + visibleItemCount;  
			
			    // si le dernier item affiché est le dernier de la liste
			    // on en charge de nouveaux
				if (lastInScreen == totalItemCount && totalItemCount > 0)
					tryExpandListView();
		}
      });
	}
	
	/**
	 * Essaye d'afficher plus d'éléments dans la liste (appel d'un autre thread).
	 * @return échec, de nouveaux articles sont déjà en train d'être chargés
	 */
	protected boolean tryExpandListView()
	{
		Boolean echec = updateEnCours; 
		
		if(echec)
		{
			Log.w("log_tag", "Attention : les articles suivants sont déjà en train d'être chargés.");
		}
		else
		{
			Log.v("log_tag", "Ajout des articles "+dernierArticle
					+" à "+(dernierArticle+ARTICLES_A_CHARGER) );
			updateEnCours = true;
			Thread thread = new Thread(null, loadMoreItems);
			thread.start();
		}
		
		return echec;
	}
	
	/**
	 * Routine qui télécharge de nouevaux articles puis demande au thread de l'UI d'updater l'UI.
	 * 
	 * Les nouveaux articles
	 */
	protected Runnable loadMoreItems = new Runnable() {
		
		public void run() {		
			String url = "http://omnilogie.fr/raw/articles.json?start="+dernierArticle
						+"&limit="+ARTICLES_A_CHARGER;
			
			// 
			JSONArray jsonArray = JSONfunctions.getJSONArrayfromURL(url);
			
			// Insert les éléments JSON dans listeArticles
		    try{		    	
		    	for(int i=0;i<jsonArray.length();i++){						
			
					ArticleObject nouvelArticle = new ArticleObject();
					nouvelArticle.remplirDepuisJSON( jsonArray.getJSONObject(i) );
					
					nouveauxArticles.add(nouvelArticle);
					dernierArticle++;
		    	}
		    }catch(JSONException e)        {
			  	 Log.e("log_tag", "Error parsing data "+e.toString());
			}
			  
		    // met à jour l'UI sur le thread dédié
			runOnUiThread(majListView);
		};
	};
	
	/**
	 * Ajoute les nouveaux articles dans la liste des articles.
	 * Puis crée/notifie l'adapter de la liste. 
	 */
	protected Runnable majListView = new Runnable() {
		
		public void run() {
			
			try {
				
				listeArticles.addAll(nouveauxArticles);
				nouveauxArticles.clear();
				
				if(adapter != null)
				{
					// indique à l'adapter qu'il faut faire un resfresh UI du contenu de la liste
					adapter.notifyDataSetChanged();
				}
				else
				{
					adapter = new ArticleObjectAdapter(listeActivity,listeArticles);
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
