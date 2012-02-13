package fr.omnilogie.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class HomeActivity extends DefaultActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		/*
		 * BOUTON : Article du jour 
		 */
		RelativeLayout buttonArticle = ((RelativeLayout) findViewById(R.id.home_article));
		buttonArticle.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/article/last"));
				startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Liste des articles
		 */
		RelativeLayout buttonListe = ((RelativeLayout) findViewById(R.id.home_liste));
		buttonListe.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/liste"));
				startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Liste des auteurs
		 */
		RelativeLayout buttonAuteurs = ((RelativeLayout) findViewById(R.id.home_auteurs));
		buttonAuteurs.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), AuteursActivity.class);
				startActivity(myIntent);
			}
		});
		
		/*
		 * BOUTON : Article au hasard
		 */
		RelativeLayout buttonAleatoire = ((RelativeLayout) findViewById(R.id.home_aleatoire));
		buttonAleatoire.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/article/random"));
				startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Article au hasard
		 */
		RelativeLayout buttonRecherche = ((RelativeLayout) findViewById(R.id.home_recherche));
		buttonRecherche.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				onSearchRequested();
			}
		});
	}
	
	/**
	 * Ajouter un bouton pour l'affichage des auteurs de l'application
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		
		MenuItem creditsMenu = menu.add("Crédits");
		creditsMenu.setIcon(R.drawable.credits);

		return r;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == 0)
		{
			//Afficher les crédits
			new AlertDialog.Builder(HomeActivity.this)
				.setTitle("Crédits")
				.setMessage(getResources().getText(R.string.credits))
				.setPositiveButton(android.R.string.ok, null)
				.show();
		}
		
		return super.onOptionsItemSelected(item);
	}
}