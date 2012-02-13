package fr.omnilogie.app;

import android.view.Menu;

/**
 * Cette classe permet de définir une activité de l'application Omnilogie
 * mais qui ne fait pas partie du "cœur".
 * 
 * En conséquence, elle hérite de l'activité par défaut mais supprime l'élément de menu permettant l'affichage d'un
 * article aléatoire.
 * 
 * Cela évite des comportements étranges (par exemple, un menu "Aléatoire" sur l'activité listant les articles laisse
 * pense qu'il s'agit d'afficher un auteur au hasard).
 * 
 * @author neamar
 *
 */
public class SpecialActivity extends DefaultActivity {
	
	/**
	 * Supprimer le menu "aléatoire".
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		
		menu.removeItem(R.id.menu_aleatoire);

		return r;
	}
}
