﻿package fr.omnilogie.app;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
		
		if(savedInstanceState != null)
		{
			//Nous sommes en train de restaurer : pas besoin de tout recharger
			//il suffit de récupérer l'article du Bundle fourni.
			article = (ArticleObject) savedInstanceState.getSerializable("article");
		}
		else
		{
			//Rien en mémoire, il faut charger.
			//Quel article doit-on afficher ? Ce peut-être une ID spécifique ou l'article du jour.
			//Il suffit de lire l'URI avec lequel cette activité a été appelée
			Uri uri = getIntent().getData();
			String articleToDisplay = uri.getLastPathSegment();
			
			JSONObject jsonDatas = JSONfunctions.getJSONfromURL("http://omnilogie.fr/raw/articles/" + articleToDisplay + ".json");
	
			//Remplir notre article avec les données fournies
			article.remplirDepuisJSON(jsonDatas);
		}
		
		
		//Définir le titre de l'activité.
		//Parser à la recherche d'entités HTML qui doivent être rendues à l'écran (&oelig;, ...)
		setTitle(Html.fromHtml(article.titre));
		
		//Créer le HTML depuis le fichier template
		//situé dans assets/article.html
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
		
		//Remplacer les placeholders par le texte de l'article
		html = html.replace("{{banniere}}", article.banniere);
		html = html.replace("{{titre}}", article.titre);
		html = html.replace("{{accroche}}", article.accroche);
		html = html.replace("{{omnilogisme}}", article.omnilogisme);
		html = html.replace("{{auteur}}", article.auteur);
		html = html.replace("{{dateParution}}", article.dateParution);
		
		//Afficher le contenu de l'article
		WebView webView = ((WebView) findViewById(R.id.article));
		
		//Sur une seule colonne, pour éviter au maximum de devoir scroller horizontalement
		//Dans certains cas toutefois, on ne peut rien y faire et le scroll horizontal apparaît
		//(par exemple, sur une balise <pre> contenant du texte trop long)
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		//Il faut spécifier l'URL de base du site afin que les images (indiquées en chemin relatif)
		//soient disponibles.
		webView.loadDataWithBaseURL("http://omnilogie.fr", html, "text/html", "UTF-8", null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Récupérer le menu de base défini en XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.article, menu);

		//Ajouter le menu pour les sources de l'article
		if(article.hasSources())
		{
			//Créer le sous-menu.
			//On lui passe l'id -1 car l'on souhaite conserver les id de 0 à n pour les véritables sources
			//Cela permettra de les mapper aisément de l'index du menu à l'index du tableau contenant les liens
			//Pour le groupe, on utilise 0 car aucun autre menu n'est présent.
			SubMenu sources = menu.addSubMenu(0, -1, 0, "Sources");
			sources.getItem().setIcon(R.drawable.sources);
			
			//Ajouter chacun des titres des sources en faisant attention à bien établir une
			//fonction identité entre l'id du menu et l'index du tableau de liens articles.sourcesUrl.
			for(int i = 0; i < article.sourcesTitre.size(); i++)
			{
				sources.add(0, i, i, article.sourcesTitre.get(i));
			}
		}
		return true;
	}
	
	
	/**
	 * Enregistre l'article actuellement affiché pour pouvoir le réafficher rapidement si
	 * l'écran est pivoté.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putSerializable("article", article);
	}
	
	/**
	 * Appelé quand un élément de menu est sélectionné.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		
		//A-t-on cliqué sur une source ?
		//Leurs identifiants sont compris entre 0 et la taille du tableau des sources
		if(id >= 0 && id < article.sourcesTitre.size())
		{
			//Si oui, on lance le navigateur vers l'URL
			try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.sourcesUrl.get(id)));
			startActivity(browserIntent);
			} catch (Exception e)
			{
				//Certaines des sources ne sont pas de véritables URLs, mais de simples string.
				//Dans ce cas là, tant pis, on catche et on continue.
				e.printStackTrace();
			}
		}
		
		//Traitement des menus standards :
		switch(id) {
		case R.id.menu_partager:
			onShareButtonClick();
		case R.id.menu_autres_auteur:
			onOtherBySameAuthor();
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	/**
	 * Appelé lors de l'appui sur le bouton Partager
	 * Envoie un Intent à toutes les applications recevant l'action SEND pour du text/plain.
	 */
	protected void onShareButtonClick()
	{
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);  
	    shareIntent.setType("text/plain");  

	    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, article.accrocheOuTitre());  
	    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.getShortUrl() + " : " + article.titre);  
	    startActivity(Intent.createChooser(shareIntent, "Partager cet article via..."));  
	}
	
	/**
	 * Appui sur le menu "autres articles du même auteur".
	 * Charge l'activité auteur.
	 */
	protected void onOtherBySameAuthor()
	{
    	Uri uri = Uri.parse("content://fr.omnilogie.app/auteur/" + article.auteurId);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(i);
	}
}

