package fr.omnilogie.app;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Classe en charge de mettre à jour le ou les instances du Widget.
 *
 * @author Benoit
 *
 */
public class WidgetActivity extends AppWidgetProvider implements CallbackObject {

	/**
	 * Sauvegarde des infos pour mettre à jour le widget quand les données auront été récupérées.
	 */
	private Context context;
	private int appWidgetIds[];	
	private AppWidgetManager appWidgetManager;


	@Override
	// Routine de mise à jour du widget, appelée suivant l'attribut updatePeriodMillis du Widget Provider.
	public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )	{

		// Sauvegarde les paramètres de mise à jour
		this.context = context;
		this.appWidgetIds = appWidgetIds;
		this.appWidgetManager = appWidgetManager;

		Log.v("omni_widget", "Récupération des données pour le widget...");

		// Récupération asynchrone des données sur le dernier article paru.
		// La méthode callback est appelée quand la récupération est terminée.
		String url = "http://omnilogie.fr/raw/articles.json?start=0&limit=1";
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONArrayfromURL(url, this);

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	// Routine d'initialisation du widget, appelée lors de la création d'une instance du widget.
	public void onEnabled(Context context) {

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.activity_widget);
		remoteViews.setTextViewText(R.id.widget_title, "Chargement...");

		Log.v("omni_widget", "Chargement du widget");

		super.onEnabled(context);
	}

	/**
	 * Récupère les données du JSON, les insère sur le widget et assigne l'action sur le clic.
	 *
	 * @param objet contenant les informations sur l'article dans un {@link JSONArray} possédant un unique élément.
	 */
	public void callback(Object objet) {

		if(objet != null && context != null && appWidgetIds != null & appWidgetIds.length > 0 && appWidgetManager != null)
		{
			JSONArray jsonArray = (JSONArray) objet;

			if(jsonArray != null)
			{
				Log.v("omni_widget", "Données pour le widget récupérées et insérées");

				try{
					// Récupère l'article du JSON
					ArticleObject article = new ArticleObject();
					article.remplirDepuisJSON( jsonArray.getJSONObject(0) );

					// Récupère une référence vers la vue du widget
					RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.activity_widget);

					// Configuration de l'action sur l'event clic
					Intent intent = new Intent();
					intent.setComponent(new ComponentName("fr.omnilogie.app","fr.omnilogie.app.ArticleActivity"));
					intent.putExtra("titre", Integer.toString(article.id));
					PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
					remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);

					// Affichage des informations de l'article sur le widget
					remoteViews.setTextViewText(R.id.widget_title, Html.fromHtml(article.titre));
					remoteViews.setTextViewText(R.id.widget_subtitle, Html.fromHtml(article.accroche));

					// Appel à l'AppWidgetManager pour mettre à jour les différentes instances du widget
					final int N = this.appWidgetIds.length;
					for (int i=0; i<N; i++) {
						int appWidgetId = this.appWidgetIds[i];
						this.appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
					}

				}catch(JSONException e) {
					Log.e("omni_widget", "Erreur à la mise à jour du widget "+e.toString());
				}

			}
		}
	}


}
