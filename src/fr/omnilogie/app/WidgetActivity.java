package fr.omnilogie.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Activity animant le desktop widget.
 * 
 * @author Benoit
 *
 */
public class WidgetActivity extends AppWidgetProvider implements CallbackObject {
	public static WidgetActivity Widget = null;
	public static Context context;
	public static AppWidgetManager appWidgetManager;
	public static int appWidgetIds[];
	
	@Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )    {		
		if (null == context) context = WidgetActivity.context;
	    if (null == appWidgetManager) appWidgetManager = WidgetActivity.appWidgetManager;
	    if (null == appWidgetIds) appWidgetIds = WidgetActivity.appWidgetIds;

	    WidgetActivity.Widget = this;
	    WidgetActivity.context = context;
	    WidgetActivity.appWidgetManager = appWidgetManager;
	    WidgetActivity.appWidgetIds = appWidgetIds;
	    		
	    String url = "http://omnilogie.fr/raw/articles.json?start=0&limit=1";
		
		JSONRetriever jsonRetriever = new JSONRetriever();
		jsonRetriever.getJSONArrayfromURL(url, this);
    }
	
	public static class UpdateService extends Service {
        @Override
        public void onStart(Intent intent, int startId) {
        	WidgetActivity.Widget.onUpdate(context, appWidgetManager, appWidgetIds);
        	Toast.makeText(context, "Update Widget", Toast.LENGTH_SHORT).show();
        }

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}
    }

	public void callback(Object objet) {
		
		if(objet != null)
		{
			JSONArray jsonArray = (JSONArray) objet;
			if(jsonArray != null)
			{
				// Insert les éléments JSON dans listeArticles
				try{
					ArticleObject article = new ArticleObject();
					article.remplirDepuisJSON( jsonArray.getJSONObject(0) );
					
					//Intent intent = new Intent(context, UpdateService.class);
					//PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
					
					RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.activity_widget);
					
					remoteViews.setTextViewText(R.id.widget_title, Html.fromHtml(article.titre));
					remoteViews.setTextViewText(R.id.widget_subtitle, Html.fromHtml(article.accroche));

					// Tell the widget manager
					final int N = appWidgetIds.length;
			        for (int i=0; i<N; i++) {
			            int appWidgetId = appWidgetIds[i]; 
			            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
			        }
						
				}catch(JSONException e) {
					Log.e("log_tag", "Error parsing data "+e.toString());
				}
				
			}
		}		
	}
	
}
