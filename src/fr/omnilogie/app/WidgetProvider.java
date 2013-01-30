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
import android.widget.RemoteViews;

/**
 * Classe en charge de mettre à jour le ou les instances du Widget.
 * 
 * @author Benoit
 * 
 */
public class WidgetProvider extends AppWidgetProvider implements CallbackObject {

	protected Context context;

	@Override
	// Routine de mise à jour du widget, appelée suivant l'attribut
	// updatePeriodMillis du Widget Provider.
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		this.context = context;
		// Récupération asynchrone des données sur le dernier article paru.
		// La méthode callback est appelée quand la récupération est terminée.
		String url = "http://omnilogie.fr/raw/articles.json?start=0&limit=1";
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONArrayfromURL(url, this);

		setViewsContent("Chargement en cours", "", -1);

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * Récupère les données du JSON, les insère sur le widget et assigne
	 * l'action sur le clic.
	 * 
	 * @param objet
	 *            contenant les informations sur l'article dans un
	 *            {@link JSONArray} possédant un unique élément.
	 */
	public void callback(Object objet) {
		if (objet != null) {
			JSONArray jsonArray = (JSONArray) objet;

			try {
				// Récupère l'article du JSON
				ArticleObject article = new ArticleObject();
				article.remplirDepuisJSON(jsonArray.getJSONObject(0));

				setViewsContent(article.titre, article.accroche, article.id);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			setViewsContent("Échec du chargement", "Touchez pour réessayer.", -1);
		}
	}

	/**
	 * Met à jour le widget
	 * 
	 * @param titre
	 *            titre à afficher
	 * @param accroche
	 *            accroche à afficher
	 * @param id
	 *            l'identifiant de l'article à ouvrir. Si cet identifiant n'est
	 *            pas disponible, passer -1
	 */
	public void setViewsContent(String titre, String accroche, int id) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_widget);

		// Affichage des informations de l'article sur le widget
		views.setTextViewText(R.id.widget_title, Html.fromHtml(titre));
		views.setTextViewText(R.id.widget_subtitle, Html.fromHtml(accroche));

		// Mise à jour au clic sur le bouton
		Intent intentUpdate = new Intent(context, UpdateService.class);
		PendingIntent pendingIntentUpdate = PendingIntent.getService(context, 0, intentUpdate, 0);

		// L'identifiant n'est pas encore disponible
		if (id == -1) {
			views.setOnClickPendingIntent(R.id.layout_widget, pendingIntentUpdate);
		} else {
			views.setOnClickPendingIntent(R.id.buttonRefresh, pendingIntentUpdate);

			// Affichage de l'activité
			Intent intentDisplay = new Intent();
			intentDisplay.setComponent(new ComponentName("fr.omnilogie.app",
					"fr.omnilogie.app.ArticleActivity"));
			intentDisplay.putExtra("titre", id);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentDisplay,
					PendingIntent.FLAG_CANCEL_CURRENT);
			views.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);
		}

		// Appel à l'AppWidgetManager pour mettre à jour les différentes
		// instances du widget
		int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
				new ComponentName(context, WidgetProvider.class));
		for (int i = 0; i < appWidgetIds.length; i++) {
			int appWidgetId = appWidgetIds[i];
			AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
		}
	}

	public static class UpdateService extends Service {
		@Override
		public void onStart(Intent intent, int startId) {
			WidgetProvider wp = new WidgetProvider();
			wp.onUpdate(this, null, null);

			super.onStart(intent, startId);
		}

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

	}
}
