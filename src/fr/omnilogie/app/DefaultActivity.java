package fr.omnilogie.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DefaultActivity extends Activity {
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
	    	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://fr.omnilogie.app/article/random"));
  			startActivity(i);
			break;
		case R.id.menu_rediger:
			//Le lecteur souhaite devenir rédacteur
		    AlertDialog ad = new AlertDialog.Builder(this).create();  
		    ad.setMessage("Omnilogie a toujours besoin de rédacteurs ! Souhaitez-vous rejoindre l'interface de rédaction du site ?");
		    
		    //Rejoindre l'interface de rédaction
		    ad.setButton(DialogInterface.BUTTON_POSITIVE, "C'est parti !", new DialogInterface.OnClickListener() {  
		        public void onClick(DialogInterface dialog, int which) {  
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://omnilogie.fr/membres/Redaction"));
					startActivity(browserIntent);                     
		        }  
		    });
		    
		    //Retourner à l'article
		    ad.setButton(DialogInterface.BUTTON_NEGATIVE, "Pas maintenant", new DialogInterface.OnClickListener() {  
		        public void onClick(DialogInterface dialog, int which) {  
		            dialog.dismiss();                      
		        }  
		    }); 
		    
		    ad.show();  
		default:
			break;
		}
		
		return super.onContextItemSelected(item);
	}
}
