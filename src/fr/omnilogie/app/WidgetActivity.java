package fr.omnilogie.app;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Activity en charge de programmer les mises à jour du widget.
 * Ces mises à jour sont réalisées par {@link WidgetAlarmService} via une alarme ou directement.
 * 
 * 
 * @author Benoit
 *
 */
public class WidgetActivity extends AppWidgetProvider {
	
	/**
	 * Sauvegarde du contexte d'appel de l'update du widget pour pouvoir y accéder lorsqu'on aura récupérer les données 
	 */
	public static Context context;
	
	/**
	 * Sauvegarde des ID des instances du widget pour pouvoir y accéder lorsqu'on aura récupérer les données
	 */
	public static int appWidgetIds[];
	
	/**
	 * Référence vers le service de mise à jour du widget  
	 */
	private PendingIntent service = null;
	
	@Override
	// Routine de mise à jour du widget, appelée suivant l'attribut updatePeriodMillis du Widget Provider.
	public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )	{		
				
		// Sauvegarde les paramètres de mise à jour		
		WidgetActivity.context = context;
		WidgetActivity.appWidgetIds = appWidgetIds;		
				
		final Intent intent = new Intent(context, WidgetAlarmService.class);  
		
		// Si c'est le premier lancement, on lance le service directement
		if (service == null)  
		{
			Log.v("omni_widget", "Mise à jour du widget");
			
			context.startService(intent);			
			service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		}
		
		// On crée une alarme se déclenchant à 00h(15+randomOffset) heure de Paris le lendemain
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
		final Calendar nextUpdateTime = new GregorianCalendar(TimeZone.getTimeZone("France"));
		nextUpdateTime.add(Calendar.DAY_OF_YEAR, 1);		
		nextUpdateTime.set(Calendar.HOUR_OF_DAY, 23); //TODO charger dynamiquement l'offset de Paris (00h00 Paris -> 23h00 GMT)
		nextUpdateTime.set(Calendar.MINUTE, 1 + new Random().nextInt(5)); 
		nextUpdateTime.set(Calendar.SECOND, new Random().nextInt(60)); 
		nextUpdateTime.set(Calendar.MILLISECOND, 0);
		
		Log.v("omni_widget", "Prochaine mise à jour du widget le "+ nextUpdateTime.getTime().toString());
		
		alarmManager.set(AlarmManager.RTC, nextUpdateTime.getTime().getTime(), service); 
		
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
