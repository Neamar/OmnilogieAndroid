package fr.omnilogie.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Activité par défaut.
 * 
 * Toutes les activités de l'application en hérite.
 * 
 * Cette activité définit les menus communs à tous.
 * 
 * Cette activité offre des fonctions permettant d'afficher un ProgressDialog
 * simplement.
 * 
 * @author neamar
 * 
 */
public abstract class DefaultActivity extends Activity {
	protected Boolean displayBackOnActionBar = true;
	
	protected ProgressDialog progressDialog = null;
	protected String toastText;
	
	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Title in action bar brings back to home
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && displayBackOnActionBar) {
			getActionBar().setHomeButtonEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Affiche ou non un spinner indiquant que l'activité est en train de
	 * charger ses données.
	 * 
	 * @param isLoading
	 *            true pour afficher, false une fois le chargement terminé.
	 */
	protected void isLoading(boolean isLoading) {
		try {
			if (isLoading) {
				progressDialog = ProgressDialog.show(DefaultActivity.this, "", "Chargement...",
						true);
			} else if (progressDialog != null) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			// Si on fait une rotation alors que l'icone était affichée, une
			// Exception est lancée.
		}
	}

	/**
	 * Gestion des menus standards, inflaté de res/menu/base.xml
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Récupérer le menu de base défini en XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.base, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Appelé quand un élément de menu est sélectionné.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Traitement des menus standards :
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_aleatoire:
			Intent i = new Intent(this, ArticleActivity.class);
			i.putExtra("titre", "random");
			startActivity(i);
			break;
		case R.id.menu_recherche:
			onSearchRequested();
			break;
		case R.id.menu_rediger:
			// Le lecteur souhaite devenir rédacteur
			AlertDialog dialogRediger = new AlertDialog.Builder(this).create();
			dialogRediger
					.setMessage("Omnilogie a toujours besoin de rédacteurs ! Souhaitez-vous rejoindre l'interface de rédaction du site ?");

			// Rejoindre l'interface de rédaction
			dialogRediger.setButton(DialogInterface.BUTTON_POSITIVE, "C'est parti !",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
									.parse("http://omnilogie.fr/membres/Redaction"));
							startActivity(browserIntent);
						}
					});

			// Retourner à l'article
			dialogRediger.setButton(DialogInterface.BUTTON_NEGATIVE, "Pas maintenant",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			dialogRediger.show();
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	/**
	 * Retourne à {@link HomeActivity} et affiche le message dans un
	 * {@link Toast}.
	 * 
	 * @param message
	 *            à afficher
	 */
	protected void unableToConnect(String message) {

		showToast(message);
		startActivity(new Intent(this, HomeActivity.class));
	}

	/**
	 * Retourne à {@link HomeActivity} et affiche un message standard dans un
	 * {@link Toast}.
	 */
	protected void unableToConnect() {
		final String message = "Problème de connexion. Veuillez réessayer ultérieurement.";
		unableToConnect(message);
	}

	/**
	 * Affiche un toast contenant le message
	 * 
	 * @param message
	 *            à afficher dans le toast
	 */
	protected void showToast(String message) {

		toastText = message;

		Runnable showToastRunnable = new Runnable() {
			public void run() {
				Toast toast = Toast
						.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
				toast.show();
			}
		};

		runOnUiThread(showToastRunnable);

	}

}
