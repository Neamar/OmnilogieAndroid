package fr.omnilogie.app;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Activity en charge d'animer le widget de l'application.
 * La fréquence de mise à jour du widget est configuré dans widget_provider.xml avec le paramètre 
 * updatePeriodMillis.
 * 
 * 
 * @author Benoit
 *
 */
public class WidgetActivity extends AppWidgetProvider {
	
	/**
	 * Sauvegarde du contexte d'appel de l'update du widget
	 */
	public static Context context;
	
	/**
	 * Sauvegarde de l'appWidgetManager
	 */
	protected AppWidgetManager appWidgetManager;
	
	/**
	 * Sauvegarde des ID des instances du widget
	 */
	public static int appWidgetIds[];
	
	private PendingIntent service = null;
	
	@Override
	// Routine de mise à jour du widget, appelée suivant l'attribut updatePeriodMillis du Widget Provider.
	public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )	{		
				
		// Sauvegarde les paramètres de mise à jour		
		WidgetActivity.context = context;
		WidgetActivity.appWidgetIds = appWidgetIds;		
				
		final Intent intent = new Intent(context, WidgetAlarmService.class);  
		
		if (service == null)  
		{  
			Log.v("omni_widget", "Mise à jour du widget");
			
			context.startService(intent);			
			service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		}
		else
		{
			final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
			final Calendar nextUpdateTime = new GregorianCalendar(TimeZone.getTimeZone("Paris"));
			nextUpdateTime.add(Calendar.HOUR_OF_DAY, 24);
			nextUpdateTime.set(Calendar.HOUR_OF_DAY, 0);
			nextUpdateTime.set(Calendar.MINUTE, 15);  //TODO ajouter un seed pour les minutes 
			nextUpdateTime.set(Calendar.SECOND, 0); 
			nextUpdateTime.set(Calendar.MILLISECOND, 0);
			
			Log.v("omni_widget", "Prochaine mise à jour du widget le "+ nextUpdateTime.getTime().toString());
			
			alarmManager.set(AlarmManager.RTC, nextUpdateTime.getTime().getTime(), service); 
		}
		
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
	
	@Override
	public void onDisabled(Context context)
	{
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
		  
		alarmManager.cancel(service);  
        
        super.onDisabled(context);
	}
	
}
