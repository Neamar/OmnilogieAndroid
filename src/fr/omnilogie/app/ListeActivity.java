﻿package fr.omnilogie.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.net.Uri;
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
public class ListeActivity extends ListActivity implements CallbackObject {
	
	static final int ARTICLES_A_CHARGER = 20;
	
	String baseUrl;
	
	int dernierArticle = 0; // id du dernier article chargé
	Boolean updateEnCours = false;
	Boolean updateAvailable = true;
	ArticleObjectAdapter adapter;
	
	private View loadingFooter;
	
	// Liste avec les méta-données des articles chargés
	ArrayList<ArticleObject> listeArticles = new ArrayList<ArticleObject>();
	// Liste avec les méta-données des articles à ajouter
	ArrayList<ArticleObject> nouveauxArticles = new ArrayList<ArticleObject>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listes);
		
		//Quelle liste afficher ?
		//Choisir en fonction de l'URI.
		Uri uri = getIntent().getData();
		if(uri.getPath().startsWith("/auteur/"))
		{
			baseUrl = "http://omnilogie.fr/raw/auteurs/" + uri.getLastPathSegment() + ".json";
			setTitle("Articles de l'auteur");
		}
		else
		{
			baseUrl = "http://omnilogie.fr/raw/articles.json";
			setTitle("Derniers articles parus");
		}
		
		tryExpandListView();
		
		final ListView lv = getListView();

		// ajout du footer
		loadingFooter = getLayoutInflater().inflate(R.layout.item_liste_loading, null);
		lv.addFooterView(loadingFooter);
		
		// ajout de l'adapter
		adapter = new ArticleObjectAdapter(this ,listeArticles);
		setListAdapter(adapter);
		
		// chargement des articles (autre thread)
		tryExpandListView();
		
		// initialisation du listener sur un clic
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
			ArticleObject article = listeArticles.get(position);
			if(article != null)
			{
				Uri uri = Uri.parse("content://fr.omnilogie.app/article/" + article.id);
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
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
				if (lastInScreen == totalItemCount && totalItemCount > 0 && updateAvailable)
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

			String url = baseUrl + "?start="+dernierArticle+"&limit="+ARTICLES_A_CHARGER;
			
			JSONRetriever jsonRetriever = new JSONRetriever();
			jsonRetriever.getJSONArrayfromURL(url, this);
		}
		
		return echec;
	}
	
	/**
	 * Ajoute les nouveaux articles dans la liste des articles.
	 * Si aucun nouvel article, on désactive l'ajout le chargement de nouveaux articles.
	 * Puis crée/notifie l'adapter de la liste.
	 */
	protected Runnable majListView = new Runnable() {
		
		public void run() {
			
			try {
				
				if(nouveauxArticles.size() == 0)
				{
					// aucun nouvel article : on désactive le chargement d'articles
					ListView lv = getListView();
					lv.removeFooterView(loadingFooter);
					updateAvailable = false;
				}
				else
				{
					listeArticles.addAll(nouveauxArticles);
					nouveauxArticles.clear();
				}
				
				// indique à l'adapter qu'il faut faire un resfresh UI du contenu de la liste
				adapter.notifyDataSetChanged();
				
			} catch (Exception e) {
				 Log.e("log_tag", "Erreur pendant l'update UI de la ListView "+e.toString());
			}
	
			// Fin de l'opération
			updateEnCours = false;
		}
	};

	/**
	 * Méthode de callback utilisée pour traité le JSON une fois récupéré.
	 * 
	 * @param JSONArray récupéré 
	 */
	public void callback(Object o) {
		if(o != null)
		{
			JSONArray jsonArray = (JSONArray) o;
			if(jsonArray != null)
			{
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
			}
		}
		
	}
	
}
