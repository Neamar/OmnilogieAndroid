package fr.omnilogie.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contient toutes les données pour un article.
 * 
 * Structure abstraite, simple conteneur ne gérant pas l'affichage.
 * @author neamar
 *
 */
public class ArticleObject
{
	String titre;
	String accroche = "";
	String omnilogisme;
	
	String banniere = "file:///android_asset/banniere.png";
	
	ArrayList<String> sourcesTitre = new ArrayList<String>();
	ArrayList<String> sourcesUrl = new ArrayList<String>();
	
	/**
	 * Construit l'objet depuis des paramètres fournis en JSON
	 * @param jsonDatas du json au format {Titre, Accroche?, Banniere?, Omnilogisme, Sources:[]}
	 */
	public void remplirDepuisJSON(JSONObject jsonDatas)
	{
		try {
			/**
			 * COMPOSANTS OBLIGATOIRES
			 * 
			 * Ces paramètres sont forcément définis et non nuls.
			 */
			
			titre = jsonDatas.getString("Titre");
			omnilogisme = jsonDatas.getString("Omnilogisme");
			
			/**
			 * COMPOSANTS OPTIONNELS
			 * 
			 * Ces composants nécessitent de vérifier qu'ils ne sont pas nuls ou vides.
			 */
			if(!jsonDatas.isNull("Accroche"))
				accroche = jsonDatas.getString("Accroche");
			if(!jsonDatas.isNull("Banniere"))
				banniere = jsonDatas.getString("Banniere");
			
			//Traiter les sources
			JSONArray sources = jsonDatas.getJSONArray("Sources");
			for(int i = 0;i < sources.length();i++)
			{
				sourcesUrl.add(sources.getJSONObject(i).getString("URL"));
				
				if(sources.getJSONObject(i).isNull("Titre"))
					sourcesTitre.add(sources.getJSONObject(i).getString("URL"));
				else
					sourcesTitre.add(sources.getJSONObject(i).getString("Titre"));
				
			}
		} catch (JSONException e) {
			// TODO : gérer les erreurs
			e.printStackTrace();
		}
	}
	
	public Boolean hasSources()
	{
		return (sourcesUrl.size() > 0);
		
	}
}