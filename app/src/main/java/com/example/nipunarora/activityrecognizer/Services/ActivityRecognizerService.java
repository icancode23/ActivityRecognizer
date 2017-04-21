package com.example.nipunarora.activityrecognizer.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.nipunarora.activityrecognizer.Activities.Home;
import com.example.nipunarora.activityrecognizer.R;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nipunarora on 21/04/17.
 */

public class ActivityRecognizerService extends IntentService {
    HashMap<Integer,String>Activities;
    String TAG="ActRecoService";
    int [] activitycodes={DetectedActivity.IN_VEHICLE,DetectedActivity.STILL,DetectedActivity.TILTING,DetectedActivity.RUNNING,DetectedActivity.ON_BICYCLE,DetectedActivity.ON_FOOT,DetectedActivity.UNKNOWN};
    String [] activitystrings={"in a vehicle","still","tilting your device","running","riding a bicycle","on foot","null"};

    public ActivityRecognizerService() {
        super("ActivityRecognizedService");
        buildHash();
        Log.d(TAG,"non-arguement constructor");
    }

    public ActivityRecognizerService(String name) {
        super(name);
        buildHash();
        Log.d(TAG,"arguement constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }
    public void buildNotification(String type)
    {
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.brain)
                .setContentTitle("Activity Recognizer")
                .setContentText("are you "+type)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(10)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        int max=0;
        DetectedActivity most_probable=null;
        for (DetectedActivity d:probableActivities)
        {
            if(d.getConfidence()>max)
            {
                max=d.getConfidence();
                most_probable=d;
            }

        }
        if(most_probable!=null && most_probable.getConfidence()>75 )
        {
            buildNotification(Activities.get(most_probable.getType()));
        }
    }
    private void buildHash()
    {
        Activities=new HashMap<Integer, String>();
        for(int i=0;i<activitycodes.length;++i)
        {
            Activities.put(activitycodes[i],activitystrings[i]);
        }
    }
}
