package fr.omnilogie.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends DefaultActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		TextView disclaimer = (TextView) findViewById(R.id.disclaimer);
		disclaimer.append(" – " + getVersionName());
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
		 * BOUTON : Top articles
		 */
		RelativeLayout buttonTop = ((RelativeLayout) findViewById(R.id.home_top));
		buttonTop.setOnClickListener(new View.OnClickListener() {
			
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
				.setMessage(getResources().getText(R.string.home_credits))
				.setPositiveButton(android.R.string.ok, null)
				.show();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected String getVersionName()
	{
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "test";
	}
}