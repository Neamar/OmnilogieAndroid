package fr.omnilogie.app;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleActivity extends DefaultActivity implements CallbackObject {

	/**
	 * L'article qui est affiché sur cette activité. Cet objet contient toutes
	 * les données nécessaires, traitées depuis le JSON récupéré et facilement
	 * accessibles.
	 */
	protected ArticleObject article = new ArticleObject();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		if (savedInstanceState != null) {
			// Nous sommes en train de restaurer : pas besoin de tout recharger
			// il suffit de récupérer l'article du Bundle fourni.
			article = (ArticleObject) savedInstanceState.getSerializable("article");

			// Le simple fait qu'un article soit présent ne signifie pas
			// forcément qu'il
			// a été chargé : vérifier qu'il n'est pas vide.
			if (article.id != -1) {
				runOnUiThread(remplirUIAvecDatas);
				return;
			}
		}

		// Rien en mémoire, il faut charger.
		String articleToDisplay;

		// Quel article doit-on afficher ?
		// La réponse est dans le paramètre titre.
		if (getIntent().hasExtra("titre")) {
			articleToDisplay = getIntent().getStringExtra("titre");
			if (articleToDisplay == null)
				articleToDisplay = Integer.toString(getIntent().getIntExtra("titre", 1));
		} else {
			// Le paramètre titre n'est pas fourni
			// L'activité a été lancée depuis le site web
			articleToDisplay = getIntent().getData().toString()
					.replace("http://omnilogie.fr/O/", "");
		}

		// Si le nom de l'article finit par un "?", le supprimer car il ferait
		// bugger l'URL.
		// L'API se chargera de retrouver l'article quand même.
		if (articleToDisplay.lastIndexOf('?') == articleToDisplay.length() - 1)
			articleToDisplay = articleToDisplay.substring(0, articleToDisplay.length() - 1);
		// Télécharge le contenu de l'article de manière asynchrone.
		// La méthode callback est appelée après la récupération.
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONfromURL("http://omnilogie.fr/raw/articles/" + articleToDisplay
				+ ".json", this);

		isLoading(true);
	}

	/**
	 * Termine la préparation de la vue, une fois que les données distantes sont
	 * récupérées. Doit être lancé dans le thread UI (ex.
	 * runOnUiThread(initialiseViewWithData);)
	 * 
	 */
	protected Runnable remplirUIAvecDatas = new Runnable() {

		public void run() {
			isLoading(false);
			// Définir le titre de l'activité.
			// Parser à la recherche d'entités HTML qui doivent être rendues à
			// l'écran (&oelig;, ...)
			setTitle(Html.fromHtml(article.titre));

			// Créer le HTML depuis le fichier template
			// situé dans assets/article.html
			String html = "";
			try {
				InputStream fin = getAssets().open("article.html");
				byte[] buffer = new byte[fin.available()];
				fin.read(buffer);
				fin.close();

				html = new String(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}

			WebView webView = ((WebView) findViewById(R.id.article));

			// Remplacer les placeholders de contenu
			html = html.replace("{{banniere}}", article.banniere)
					.replace("{{titre}}", article.titre).replace("{{accroche}}", article.accroche)
					.replace("{{omnilogisme}}", article.omnilogisme)
					.replace("{{auteur}}", article.auteur)
					.replace("{{dateParution}}", article.dateParution);

			// Remplacer les placeholders de style
			int largeurBanniere = Math.min(690, webView.getWidth());
			int hauteurBanniere = Math.min(95, largeurBanniere * 95 / 690);
			if (largeurBanniere != 0) {
				html = html.replace("{{largeur_banniere}}", Integer.toString(largeurBanniere))
						.replace("{{hauteur_banniere}}", Integer.toString(hauteurBanniere));
			} else {
				// Nous n'avons pas encore accès à la taille (c'est le cas lors
				// d'une restauration)
				// Dans ce cas, faire au mieux, de toute façon le placeholder ne
				// servirait à rien.
				html = html.replace("width:{{largeur_banniere}}px; height:{{hauteur_banniere}}px;",
						"max-width:100%");
			}

			/*
			 * Afficher le contenu de l'article
			 */

			// Sur une seule colonne, pour éviter au maximum de devoir scroller
			// horizontalement
			// Dans certains cas toutefois, on ne peut rien y faire et le scroll
			// horizontal apparaît
			// (par exemple, sur une balise <pre> contenant du texte trop long)
			webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

			// Intercepter directement les liens vers les omnilogismes.
			// En théorie, on pourrait ne rien faire : l'utilisateur se verrait
			// alors présenter un choix
			// entre tous ses navigateurs et Omnilogie. Toutefois, ce n'est pas
			// très intuitif !
			WebViewClient webClient = new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					if (url.startsWith("http://omnilogie.fr/O/")) {
						// Il s'agit d'un lien vers un autre article : ouvrir
						// directement cette activité
						// avec les nouveaux paramètres
						Intent i = new Intent(view.getContext(), ArticleActivity.class);
						i.putExtra("titre", url.substring(22));
						startActivity(i);
					} else {
						Uri uri = Uri.parse(url);
						Intent i = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(i);
					}

					return true;
				}
			};
			webView.setWebViewClient(webClient);

			// Il faut spécifier l'URL de base du site afin que les images
			// (indiquées en chemin relatif)
			// soient disponibles.
			webView.loadDataWithBaseURL("http://omnilogie.fr", html, "text/html", "UTF-8", null);
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				ArticleActivity.this.invalidateOptionsMenu();
			}
		}
	};

	@TargetApi(11)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);

		// Récupérer le menu pour les articles :
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.article, menu);

		// Ajouter le menu pour les sources de l'article
		if (article.hasSources()) {
			// Créer le sous-menu.
			// On lui passe l'id -1 car l'on souhaite conserver les id de 0 à n
			// pour les véritables sources
			// Cela permettra de les mapper aisément de l'index du menu à
			// l'index du tableau contenant les liens
			// Pour le groupe, on utilise 0 car aucun autre menu n'est présent.
			SubMenu sources = menu.addSubMenu(0, -1, 0, "Sources");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				sources.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			}
			sources.getItem().setIcon(R.drawable.sources);

			// Ajouter chacun des titres des sources en faisant attention à bien
			// établir une
			// fonction identité entre l'id du menu et l'index du tableau de
			// liens articles.sourcesUrl.
			for (int i = 0; i < article.sourcesTitre.size(); i++) {
				sources.add(0, i, i, article.sourcesTitre.get(i));
			}
		}
		return r;
	}

	/**
	 * Enregistre l'article actuellement affiché pour pouvoir le réafficher
	 * rapidement si l'écran est pivoté.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("article", article);
	}

	/**
	 * Appelé quand un élément de menu est sélectionné.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		// A-t-on cliqué sur une source ?
		// Leurs identifiants sont compris entre 0 et la taille du tableau des
		// sources
		if (id >= 0 && id < article.sourcesTitre.size()) {
			// Si oui, on lance le navigateur vers l'URL
			try {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.sourcesUrl
						.get(id)));
				startActivity(browserIntent);
			} catch (Exception e) {
				// Certaines des sources ne sont pas de véritables URLs, mais de
				// simples string.
				// Dans ce cas là, tant pis, on catche et on continue.
				e.printStackTrace();
			}
		}

		// Traitement des menus standards :
		switch (id) {
		case R.id.menu_partager:
			onShareButtonClick();
			break;
		case R.id.menu_autres_auteur:
			onOtherBySameAuthor();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Appelé lors de l'appui sur le bouton Partager Envoie un Intent à toutes
	 * les applications recevant l'action SEND pour du text/plain.
	 */
	protected void onShareButtonClick() {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");

		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				Html.fromHtml(article.accrocheOuTitre()));
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.getShortUrl() + " : "
				+ Html.fromHtml(article.titre));
		startActivity(Intent.createChooser(shareIntent, "Partager cet article via..."));
	}

	/**
	 * Appui sur le menu "autres articles du même auteur". Charge l'activité
	 * Liste avec le paramètre auteur.
	 */
	protected void onOtherBySameAuthor() {
		Intent i = new Intent(this, ListeActivity.class);
		i.putExtra("auteur", Integer.toString(article.auteurId));
		startActivity(i);
	}

	/**
	 * Méthode de callback utilisée pour traiter le JSON une fois récupéré.
	 * 
	 * @param objet
	 *            JSON récupéré contenant les données de l'article
	 */
	public void callback(Object objet) {
		JSONObject jsonDatas = (JSONObject) objet;
		if (jsonDatas != null) {
			// Remplir notre article avec les données fournies
			article.remplirDepuisJSON(jsonDatas);

		} else {
			// Article introuvable ou pas de connexion internet
			// Simuler un article "spécial".
			article.id = -1;
			article.titre = "Article introuvable";
			article.accroche = "Impossible de charger l'omnilogisme demandé !";
			article.omnilogisme = "Vérifiez votre connexion Internet. Il est aussi possible que l'article ait été supprimé, ou qu'une grenouille de l'espace s'en soit servi comme éponge.";
			article.auteur = "OmniScient";
			article.auteurId = 50;
			article.dateParution = "premier jour !";
		}

		runOnUiThread(remplirUIAvecDatas);
	}
}
