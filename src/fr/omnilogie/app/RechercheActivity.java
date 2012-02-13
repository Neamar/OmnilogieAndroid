package fr.omnilogie.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			lancerRecherche(query);
		}

		
		//Accrocher un évènement lors d'un clic sur un élément de la liste afin de lancer l'application Omnilogie
		listView = (ListView) findViewById(R.id.rechercher_liste);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Uri uri = Uri.parse("content://fr.omnilogie.app/article/" + urls.get(position));
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
				}
			});
		
		isLoading(true);
	}
	
	
	/**
	 * Lance la recherche sur le texte actuellement entré dans l'EditText.
	 * 
	 * @param texteRecherche le texte recherché
	 */
	protected void lancerRecherche(String texteRecherche)
	{
		//Construire l'URL à accéder :
		// URL de base pour l'API Google, avec 8 résultats
		String baseUrl = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=large&q=";
		// Restreindre au site Omnilogie.fr et même plus spécifiquement aux articles
		String restrainQuery = "site%3Aomnilogie.fr+intitle%3A%22Un+article+d%27Omnilogie.fr%22+";
		// La partie définie par l'utilisateur
		String userQuery = URLEncoder.encode(texteRecherche);
		
		// Et télécharger le résultat
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONfromURL(baseUrl +restrainQuery+userQuery, this);
		
		//Définir le titre de l'activité pendant la recherche
		setTitle("Recherche : " + texteRecherche);
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
			isLoading(false);
			
			//Adaptateur classique pour les données simples :
			ListAdapter adapter = new SimpleAdapter(rechercheActivity, resultats , R.layout.item_recherche_resultat, 
					new String[] { "titre", "fragment" }, 
					new int[] { R.id.item_recherche_resultat_titre, R.id.item_recherche_resultat_fragment });
			

			listView.addFooterView(getFooter(resultats.size()), new Object(), false);
			listView.setAdapter(adapter);
		}
		
		protected View getFooter(int nbItems)
		{
			TextView footer = new TextView(RechercheActivity.this);
			footer.setGravity(Gravity.RIGHT);

			if(nbItems == 0)
			{
				footer.setText("Aucun résultat trouvé.");
			}
			else if(nbItems == 1)
			{
				footer.setText("1 seul résultat.");
			}
			else if(nbItems == 8)
			{
				footer.setText("Trop de résultats, affichage des 8 premiers.");
			}
			else
			{
				footer.setText(nbItems + " résultats trouvés.");
			}
			
			return footer;
		}
	};
}
