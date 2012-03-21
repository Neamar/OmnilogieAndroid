package fr.omnilogie.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends DefaultActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		/*
		 * BOUTON : Article du jour 
		 */
		ImageButton buttonArticle = ((ImageButton) findViewById(R.id.home_article_button));
		buttonArticle.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/article/last"));
				startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Liste des articles
		 */
		ImageButton buttonListe = ((ImageButton) findViewById(R.id.home_liste_button));
		buttonListe.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/liste"));
				startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Liste des auteurs
		 */
		ImageButton buttonAuteurs = ((ImageButton) findViewById(R.id.home_auteurs_button));
		buttonAuteurs.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), AuteursActivity.class);
				startActivity(myIntent);
			}
		});
		
		/*
		 * BOUTON : Article au hasard
		 */
		ImageButton buttonAleatoire = ((ImageButton) findViewById(R.id.home_aleatoire_button));
		buttonAleatoire.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/article/random"));
				startActivity(i);
			}
		});
		
		/*
		 * BOUTON : Top articles
		 */
		ImageButton buttonTop = ((ImageButton) findViewById(R.id.home_top_button));
		buttonTop.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Uri uri = Uri.parse("content://fr.omnilogie.app/top");
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
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
			AlertDialog d = new AlertDialog.Builder(HomeActivity.this)
				.setTitle("Crédits")
				.setMessage(new SpannableString(getResources().getText(R.string.home_credits)))
				.setPositiveButton(android.R.string.ok, null)
				.show();
			
			//Il faut "ruser" pour avoir un lien cliquable
			//@see http://stackoverflow.com/questions/1997328/android-clickable-hyperlinks-in-alertdialog
			((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected String getVersionName()
	{
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return "test";
	}
}