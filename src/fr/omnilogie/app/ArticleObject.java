package fr.omnilogie.app;

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
	String[] sources = null;
	
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
			//TODO
		} catch (JSONException e) {
			// TODO : gérer les erreurs
			e.printStackTrace();
		}
	}
}