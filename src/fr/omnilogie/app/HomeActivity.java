package fr.omnilogie.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class HomeActivity extends Activity {
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
	}
}