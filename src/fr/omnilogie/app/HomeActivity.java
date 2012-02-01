package fr.omnilogie.app;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		Button buttonArticle = ((Button) findViewById(R.id.home_article));
		buttonArticle.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), ArticleActivity.class);
                startActivity(myIntent);
			}
		});
		
		Button buttonListe = ((Button) findViewById(R.id.home_liste));
		buttonListe.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), ListeActivity.class);
                startActivity(myIntent);
			}
		});
		
		Button buttonAuteurs = ((Button) findViewById(R.id.home_auteurs));
		buttonAuteurs.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), AuteursActivity.class);
                startActivity(myIntent);
			}
		});
	}
}

