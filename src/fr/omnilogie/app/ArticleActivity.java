package fr.omnilogie.app;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class ArticleActivity extends Activity {
	
	/**
	 * L'article qui est affiché sur cette activité.
	 * Cet objet contient toutes les données nécessaires, traitées depuis le JSON récupéré et facilement accessibles.
	 */
	protected ArticleObject article = new ArticleObject();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		
		JSONObject jsonDatas = JSONfunctions.getJSONfromURL("http://omnilogie.fr/raw/articles/1215.json");

		//Remplir notre article avec les données fournies
		article.remplirDepuisJSON(jsonDatas);

		//Créer le HTML
		String html = "";
		try
		{
			InputStream fin = getAssets().open("article.html");
			byte[] buffer = new byte[fin.available()];
			fin.read(buffer);
			fin.close();
			
			html = new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Remplacer les templates :
		html = html.replace("{{banniere}}", article.banniere);
		html = html.replace("{{titre}}", article.titre);
		html = html.replace("{{accroche}}", article.accroche);
		html = html.replace("{{omnilogisme}}", article.omnilogisme);
		
		//Afficher le contenu de l'article
		//Il faut spécifier une URL de base afin que les images soient disponibles.
		WebView webView = ((WebView) findViewById(R.id.article));
		
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		webView.loadDataWithBaseURL("http://omnilogie.fr", html, "text/html", "UTF-8", null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Récupérer le menu de base
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.article, menu);

		//Ajouter le menu pour les sources
		if(article.hasSources())
		{
			SubMenu sources = menu.addSubMenu("Sources");
			sources.getItem().setIcon(R.drawable.sources);
			
			for(int i = 0; i < article.sourcesTitre.size(); i++)
			{
				sources.add(article.sourcesTitre.get(i));
			}
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
		case R.id.menu_partager:
			onShareButtonClick();
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	public void onShareButtonClick()
	{
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);  
	    shareIntent.setType("text/plain");  

	    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, article.accrocheOuTitre());  
	    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.getShortUrl() + " : " + article.titre);  
	    startActivity(Intent.createChooser(shareIntent, "Partager cet article via..."));  
	}
}

