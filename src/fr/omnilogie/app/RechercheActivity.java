package fr.omnilogie.app;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;

public class RechercheActivity extends SpecialActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recherche);
		
		ImageButton buttonRecherche = (ImageButton) findViewById(R.id.rechercher_button);
		
		buttonRecherche.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				lancerRecherche();
			}
		});

	}
	
	/**
	 * Lance la recherche sur le texte actuellement entré dans l'EditText.
	 */
	protected void lancerRecherche()
	{
		EditText textRecherche = (EditText) findViewById(R.id.rechercher_text);
		WebView webView = (WebView) findViewById(R.id.rechercher_web);
		
		webView.loadUrl("http://www.google.fr/search?hl=fr&q=site%3Aomnilogie.fr+intitle%3A%22Un+article+d%27Omnilogie.fr%22+pi&oq=site%3Aomnilogie.fr+intitle%3A%22Un+article+d%27Omnilogie.fr");
	}
}
