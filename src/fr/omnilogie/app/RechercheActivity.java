package fr.omnilogie.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Cette activité permet de faire des recherches en utilisant Google.
 * 
 * Documentation de l'API Google : https://developers.google.com/web-search/docs/#fonje
 * 
 * @author neamar
 *
 */
public class RechercheActivity extends SpecialActivity implements CallbackObject {
	/**
	 * ArrayList utilisé pour les items de la liste.
	 * Contient des objets Spanned, afin de décoder les entités HTML et de supprimer les
	 * balises inclues.
	 */
	private ArrayList<HashMap<String, Spanned>> resultats;
	
	/**
	 * Liste des URLs.
	 * Ces URLs sont tronquées de la partie commune "http://omnilogie.fr/O/" et ne contiennent plus
	 * que le titre sous forme d'URL.
	 * 
	 * Les index de cette liste sont liés aux index de `resultats`.
	 */
	private ArrayList<String> urls;
	
	/**
	 * La listView qui affiche les résultats.
	 * Doit être stocké en propriété afin qu'on puisse y accéder depuis le thread.
	 */
	protected ListView listView;
	
	/**
	 * Référence à l'instance courante, pour le thread.
	 */
	protected RechercheActivity rechercheActivity = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Initialisation standard
		super.onCreate(savedInstanceState);
		setTitle("Rechercher dans les omnilogismes");
		setContentView(R.layout.activity_recherche);
		
		//Accrocher un évènement au clic sur le bouton rechercher
		ImageButton buttonRecherche = (ImageButton) findViewById(R.id.rechercher_button);
		buttonRecherche.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				lancerRecherche();
			}
		});
		
		//Accrocher un évènement lorsque l'on appuie sur Entrée
		EditText textRecherche = (EditText) findViewById(R.id.rechercher_text);
		textRecherche.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
				{
					lancerRecherche();
					return true;
				}
				
				return false;
			}
		});
		
		//Accrocher un évènement lors d'un clic sur un élément de la liste afin de lancer l'application Omnilogie
		listView = (ListView) findViewById(R.id.rechercher_liste);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Uri uri = Uri.parse("content://fr.omnilogie.app/article/" + urls.get(position));
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
				}
			});
	}
	
	
	/**
	 * Lance la recherche sur le texte actuellement entré dans l'EditText.
	 */
	protected void lancerRecherche()
	{
		String textRecherche = ((EditText) findViewById(R.id.rechercher_text)).getText().toString();

		//Construire l'URL à accéder :
		// URL de base pour l'API Google, avec 8 résultats
		String baseUrl = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=large&q=";
		// Restreindre au site Omnilogie.fr et même plus spécifiquement aux articles
		String restrainQuery = "site%3Aomnilogie.fr+intitle%3A%22Un+article+d%27Omnilogie.fr%22+";
		// La partie définie par l'utilisateur
		String userQuery = URLEncoder.encode(textRecherche);
		
		// Et télécharger le résultat
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONfromURL(baseUrl +restrainQuery+userQuery, this);
		
		//Définir le titre de l'activité pendant la recherche
		setTitle("Recherche : " + textRecherche);
	}
	
	/**
	 * Méthode de callback utilisée pour traiter le JSON une fois celui-ci récupéré.
	 * On arrive à cette méthode après la fonction lancerRecherche().
	 * 
	 * @param objet JSON récupéré contenant les données de l'article
	 */
	public void callback(Object objet) {
		JSONObject jsonDatas = (JSONObject) objet;
		if(jsonDatas != null)
		{
			resultats = new ArrayList<HashMap<String,Spanned>>();
			urls = new ArrayList<String>();
			try {
				//Lire le tableau.
				//Cf. https://developers.google.com/web-search/docs/#fonje pour les détails
				JSONArray jsonArray = jsonDatas.getJSONObject("responseData").getJSONArray("results");
				
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONObject jsonResult = jsonArray.getJSONObject(i);
					
					HashMap<String,Spanned> resultat = new HashMap<String,Spanned>();
					
					resultat.put("titre", Html.fromHtml(Html.toHtml(decodeJson(jsonResult, "titleNoFormatting")).replace("| Un article d'Omnilogie.fr", "")));
					resultat.put("fragment", decodeJson(jsonResult, "content"));
					
					//Enregistrer l'URL et le résultat :
					urls.add(jsonResult.getString("unescapedUrl").replace("http://omnilogie.fr/O/", ""));
					resultats.add(resultat);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Mettre à jour l'activité
			runOnUiThread(remplirUIAvecDatas);
		}
	}
	
	/**
	 * Renvoie un élément du JSON dans l'encodage correct, sous forme de Spanned.
	 * @param jsonObject l'objet dans lequel on souhaite lire
	 * @param key la clé à récupérer 
	 * @return Un objet Spanned utilisable dans les TextEdit
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	protected Spanned decodeJson(JSONObject jsonObject, String key) throws UnsupportedEncodingException, JSONException
	{
		return Html.fromHtml(new String(jsonObject.getString(key).getBytes("ISO-8859-1"), "UTF-8"));
	}
	
	/**
	 * Termine la préparation de la vue, une fois que les données distantes ont été récupérées.
	 */
	protected Runnable remplirUIAvecDatas = new Runnable() {

		public void run()
		{
			//Adaptateur classique pour les données simples :
			ListAdapter adapter = new SimpleAdapter(rechercheActivity, resultats , R.layout.item_recherche_resultat, 
					new String[] { "titre", "fragment" }, 
					new int[] { R.id.item_recherche_resultat_titre, R.id.item_recherche_resultat_fragment });
			
			listView.setAdapter(adapter);
		}
	};
}
