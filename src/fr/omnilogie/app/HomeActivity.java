package fr.omnilogie.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		/*
		 * BOUTON : Article du jour 
		 */
		Button buttonArticle = ((Button) findViewById(R.id.home_article));
		buttonArticle.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
		    	Uri uri = Uri.parse("content://fr.omnilogie.app/article/last");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
	  			startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Liste des articles
		 */
		Button buttonListe = ((Button) findViewById(R.id.home_liste));
		buttonListe.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
		    	Uri uri = Uri.parse("content://fr.omnilogie.app/liste");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
	  			startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Liste des auteurs
		 */
		Button buttonAuteurs = ((Button) findViewById(R.id.home_auteurs));
		buttonAuteurs.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), AuteursActivity.class);
                startActivity(myIntent);
			}
		});
	}
}

