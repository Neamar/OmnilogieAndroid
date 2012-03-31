package fr.omnilogie.app;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Classe en charge de mettre à jour le ou les instances du Widget.
 *
 * @author Benoit
 *
 */
public class WidgetProvider extends AppWidgetProvider implements CallbackObject {

	/**
	 * Sauvegarde des infos pour mettre à jour le widget quand les données auront été récupérées.
	 */
	private Context context;
	private int appWidgetIds[];	
	private AppWidgetManager appWidgetManager;


	@Override
	// Routine de mise à jour du widget, appelée suivant l'attribut updatePeriodMillis du Widget Provider.
	public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )	{
		if(context == null) context = this.context;
		if(appWidgetManager == null) appWidgetManager = this.appWidgetManager;
		if(appWidgetIds == null) appWidgetIds = this.appWidgetIds;
		
		// Sauvegarde les paramètres de mise à jour
		this.context = context;
		this.appWidgetIds = appWidgetIds;
		this.appWidgetManager = appWidgetManager;

		// Récupération asynchrone des données sur le dernier article paru.
		// La méthode callback est appelée quand la récupération est terminée.
		String url = "http://omnilogie.fr/raw/articles.json?start=0&limit=1";
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONArrayfromURL(url, this);

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * Récupère les données du JSON, les insère sur le widget et assigne l'action sur le clic.
	 *
	 * @param objet contenant les informations sur l'article dans un {@link JSONArray} possédant un unique élément.
	 */
	public void callback(Object objet)
	{
		if(objet != null && context != null && appWidgetIds != null && appWidgetIds.length > 0 && appWidgetManager != null)
		{
			JSONArray jsonArray = (JSONArray) objet;

			try{
				// Récupère l'article du JSON
				ArticleObject article = new ArticleObject();
				article.remplirDepuisJSON( jsonArray.getJSONObject(0) );

				Intent i = new Intent(context, UpdateService.class);
				i.putExtra("titre", article.titre);
				i.putExtra("accroche", article.accroche);
				i.putExtra("id", article.id);
				context.startService(i);

			} catch(JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static class UpdateService extends Service {
		@Override
		public void onStart(Intent intent, int startId) {
			Spanned titre;
			Spanned accroche;
			
			if(intent.hasExtra("titre"))
				titre = Html.fromHtml(intent.getStringExtra("titre"));
			else
				titre = Html.fromHtml("Erreur de chargement");
			
			if(intent.hasExtra("accroche"))
				accroche = Html.fromHtml(intent.getStringExtra("accroche"));
			else
				accroche = Html.fromHtml("Vérifiez votre connexion.");
			
			int id = intent.getIntExtra("id", -1);
					
			RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.activity_widget);
			
			// Affichage des informations de l'article sur le widget
			views.setTextViewText(R.id.widget_title, titre);
			views.setTextViewText(R.id.widget_subtitle, accroche);
			
			// Mise à jour au clic sur le bouton
			Intent intentUpdate = new Intent(this, UpdateService.class);
			PendingIntent pendingIntentUpdate = PendingIntent.getService(this, 0, intentUpdate, 0);
			
			//L'identifiant n'est pas encore disponible
			if(id == -1)
			{
				views.setOnClickPendingIntent(R.id.layout_widget, pendingIntentUpdate);
			}
			else
			{
				views.setOnClickPendingIntent(R.id.buttonRefresh, pendingIntentUpdate);
				
				//Affichage de l'activité
				Intent intentDisplay = new Intent();
				intentDisplay.setComponent(new ComponentName("fr.omnilogie.app","fr.omnilogie.app.ArticleActivity"));
				intentDisplay.putExtra("titre", id);
				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentDisplay, PendingIntent.FLAG_CANCEL_CURRENT); 
				views.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);
			}
			
			ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(thisWidget, views);
		}

		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}

