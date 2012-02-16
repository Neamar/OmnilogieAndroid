package fr.omnilogie.app;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Service ayant pour rôle de mettre à jour (de manière asynchrone) les instances du widget.
 *
 * @author Benoit
 *
 */
public class WidgetAlarmService extends Service implements CallbackObject {

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.v("omni_widget", "Récupération des données pour le widget...");

		// Récupération asynchrone des données sur le dernier article paru.
		// La méthode callback est appelée quand la récupération est terminée.
		String url = "http://omnilogie.fr/raw/articles.json?start=0&limit=1";
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONArrayfromURL(url, this);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	/**
	 * Récupère les données du JSON, les insère sur le widget et assigne l'action sur le clic.
	 *
	 * @param objet contenant les informations sur l'article dans un {@link JSONArray} possédant un unique élément.
	 */
	public void callback(Object objet) {

		if(objet != null && WidgetActivity.appWidgetIds != null && WidgetActivity.context != null)
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
					RemoteViews remoteViews = new RemoteViews(WidgetActivity.context.getPackageName(), R.layout.activity_widget);

					// Configuration de l'action sur l'event clic
					Uri uri = Uri.parse("content://fr.omnilogie.app/article/" + article.id);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.setComponent(new ComponentName("fr.omnilogie.app","fr.omnilogie.app.ArticleActivity"));
					PendingIntent pendingIntent = PendingIntent.getActivity(WidgetActivity.context, 0, intent, 0);
					remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);

					// Affichage des informations de l'article sur le widget
					remoteViews.setTextViewText(R.id.widget_title, Html.fromHtml(article.titre));
					remoteViews.setTextViewText(R.id.widget_subtitle, Html.fromHtml(article.accroche));

					// Appel à l'AppWidgetManager pour mettre à jour les différentes instances du widget
					final int N = WidgetActivity.appWidgetIds.length;
					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
					for (int i=0; i<N; i++) {
						int appWidgetId = WidgetActivity.appWidgetIds[i];
						appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
					}

				}catch(JSONException e) {
					Log.e("omni_widget", "Erreur à la mise à jour du widget "+e.toString());
				}

			}
		}
	}


}
