package fr.omnilogie.app;

import java.io.Serializable;
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
public class ArticleObject implements Serializable
{
	/**
	 * Variable pour la sérialisation
	 */
	private static final long serialVersionUID = 4314797233584545914L;

	/**
	 * Identifiant unique de l'article.
	 */
	int id;
	
	String titre;
	String accroche = "";
	String omnilogisme = "";
	String auteur;
	String dateParution = "";
	int auteurId = 0;
	
	String banniere = "file:///android_asset/banniere.png";
	
	/**
	 * Liste des sources.
	 * Une correspondance d'index est effectuée avec sourcesUrl
	 */
	ArrayList<String> sourcesTitre = new ArrayList<String>();
	
	/**
	 * Liste des sources
	 * Une correspondance d'index est effectuée avec sourcesTitre
	 */
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
			id = jsonDatas.getInt("ID");
			titre = jsonDatas.getString("T");
			auteur = jsonDatas.getString("A");
			
			/**
			 * COMPOSANTS OPTIONNELS
			 * 
			 * Ces composants nécessitent de vérifier qu'ils ne sont pas nuls ou vides.
			 */
			if(!jsonDatas.isNull("Q")) // Accroche
				accroche = jsonDatas.getString("Q");
			if(!jsonDatas.isNull("B")) // Bannière
				banniere = jsonDatas.getString("B");
			if(!jsonDatas.isNull("S")) // Date de parution
				dateParution = jsonDatas.getString("S");			
			if(!jsonDatas.isNull("O")) // Omnilogisme
				omnilogisme = jsonDatas.getString("O");
			if(!jsonDatas.isNull("AID")) // Identifiant de l'utilisateur
				auteurId = jsonDatas.getInt("AID");
			
			//Traiter les sources
			if(!jsonDatas.isNull("U"))
			{
				JSONArray sources = jsonDatas.getJSONArray("U");
				for(int i = 0;i < sources.length();i++)
				{
					sourcesUrl.add(sources.getJSONObject(i).getString("URL"));
					
					//Attention, si la source n'a pas été téléchargée depuis le serveur, son titre est
					//vide.
					//Dans ce cas, on reprend directement l'URL.
					if(sources.getJSONObject(i).isNull("Titre"))
						sourcesTitre.add(sources.getJSONObject(i).getString("URL"));
					else
						sourcesTitre.add(sources.getJSONObject(i).getString("Titre"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Indique si l'article contient des sources
	 * @return true si des sources sont présentes
	 */
	public boolean hasSources()
	{
		return (sourcesUrl.size() > 0);
	}
	
	/**
	 * Indique l'URL pour accéder à l'article depuis la version Web.
	 * @return une url compressée sous la forme http://omnilogie.fr/AA
	 */
	public String getShortUrl()
	{
		return "http://omnilogie.fr/" + Integer.toString(id, 35).toUpperCase();
	}
	
	/**
	 * Permet de récupérer l'accroche si elle est définie, sinon le titre
	 * @return accroche ou titre
	 */
	public String accrocheOuTitre()
	{
		return (accroche==""?titre:accroche);
	}
}