package fr.omnilogie.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
 * Cette activité offre des fonctions permettant d'afficher un ProgressDialog simplement.
 * @author neamar
 *
 */
public abstract class DefaultActivity extends Activity {
	protected ProgressDialog progressDialog = null;
	protected String toastText;
	
	/**
	 * Affiche ou non un spinner indiquant que l'activité est en train de charger ses données.
	 * 
	 * @param isLoading true pour afficher , false une fois le chargement terminé.
	 */
	protected void isLoading(boolean isLoading)
	{
		if(isLoading)
		{
			progressDialog = ProgressDialog.show(DefaultActivity.this, "", "Chargement...", true);
		}
		else if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
	}
	
	/**
	 * Gestion des menus standards, inflaté de res/menu/base.xml
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Récupérer le menu de base défini en XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.base, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Appelé quand un élément de menu est sélectionné.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{		
		//Traitement des menus standards :
		switch(item.getItemId()) {
		case R.id.menu_aleatoire:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/article/random")));
			break;
		case R.id.menu_bug:
			AlertDialog dialogBug = new AlertDialog.Builder(this).create();  
			dialogBug.setMessage("Vous allez être redirigé vers la page web de contact d'Omnilogie, depuis laquelle vous pourrez faire votre rapport de bug (ou suggestion d'idée). Merci pour votre contribution !");
			
			//Rejoindre l'interface de rédaction
			dialogBug.setButton(DialogInterface.BUTTON_POSITIVE, "Signaler bug ou remarque", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://omnilogie.fr/Contact#Application Android"));
					startActivity(browserIntent);
				}
			});
			
			//Annuler la soumission
			dialogBug.setButton(DialogInterface.BUTTON_NEGATIVE, "Annuler", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			dialogBug.show();
			break;
		case R.id.menu_rediger:
			//Le lecteur souhaite devenir rédacteur
			AlertDialog dialogRediger = new AlertDialog.Builder(this).create();  
			dialogRediger.setMessage("Omnilogie a toujours besoin de rédacteurs ! Souhaitez-vous rejoindre l'interface de rédaction du site ?");
			
			//Rejoindre l'interface de rédaction
			dialogRediger.setButton(DialogInterface.BUTTON_POSITIVE, "C'est parti !", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://omnilogie.fr/membres/Redaction"));
					startActivity(browserIntent);
				}
			});
			
			//Retourner à l'article
			dialogRediger.setButton(DialogInterface.BUTTON_NEGATIVE, "Pas maintenant", new DialogInterface.OnClickListener() {  
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
	 * Retourne à {@link HomeActivity} et affiche le message dans un {@link Toast}.
	 * @param message à afficher 
	 */
	protected void unableToConnect(String message) {
		
		showToast(message);		
		startActivity(new Intent(this, HomeActivity.class));
	}
	
	/**
	 * Retourne à {@link HomeActivity} et affiche un message standard dans un {@link Toast}.
	 */
	protected void unableToConnect() {
		final String message = "Problème de connexion. Veuillez réessayer ultérieurement.";
		unableToConnect(message);
	}
	
	/**
	 * Affiche un toast contenant le message
	 * 
	 * @param message à afficher dans le toast
	 */
	protected void showToast(String message) {
		
		toastText = message;
		
		Runnable showToastRunnable = new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
				toast.show();
			}
		};
		
		runOnUiThread(showToastRunnable);
		
	}
	
}
