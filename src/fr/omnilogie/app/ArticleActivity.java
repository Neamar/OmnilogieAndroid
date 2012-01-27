package fr.omnilogie.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.WebView;;

public class ArticleActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);
		
		JSONObject datas = JSONfunctions.getJSONfromURL("http://omnilogie.fr/raw/articles/1209.json");
		try {
			//Commencer par charger la bannière si nécessaire
			if(!datas.isNull("Banniere"))
			{
				ImageDownloader downloader = new ImageDownloader((ImageView) findViewById(R.id.banniere));
				downloader.execute(datas.getString("Banniere"));
			}
			
			//Gérer l'affichage du titre.
			//Celui-ci peut contenir des entités HTML : il faut donc le convertir en texte Spanned
			((TextView) findViewById(R.id.titre)).setText(Html.fromHtml(datas.getString("Titre")));
			
			//Afficher l'accroche. Si elle n'existe pas, masquer le composant afin de gagner de la place.
			if(datas.isNull("Accroche"))
			{
				((TextView) findViewById(R.id.accroche)).setVisibility(View.GONE);
			}
			else
			{
				((TextView) findViewById(R.id.accroche)).setText(Html.fromHtml(datas.getString("Accroche")));
			}

			//Rendre le contenu de l'article.
			//Il faut spécifier une URL de base afin que les images soient disponibles.
			((WebView) findViewById(R.id.contenu)).loadDataWithBaseURL("http://omnilogie.fr", preparerArticle(datas.getString("Omnilogisme")), "text/html", "UTF-8", null);
		} catch (JSONException e) {
			// TODO : gérer les erreurs
			e.printStackTrace();
		}
	}
	
	protected String preparerArticle(String article)
	{
		article = "<html>\n" + 
				"	<head>\n" + 
				"		<link rel=\"stylesheet\" media=\"all\" href=\"/CSS/omni.min.css\" />\n" + 
				"	</head>\n" + 
				"	<body>\n" + 
				"		<section id=\"content\">\n" + 
				"			<div class=\"omnilogisme\">\n" + 
				article +
				"			</div>\n" + 
				"		</section>\n" + 
				"	</body>\n" + 
				"</html>";
		
		return article;
	}
}