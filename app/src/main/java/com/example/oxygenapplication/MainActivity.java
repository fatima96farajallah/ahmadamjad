package com.example.oxygenapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference ref;
    TextView oxygen, heartRate;
    boolean isUserFine = false;
    Swimmer swimmer;
    double lastSpO2 = 0;
    double lastHeartRate = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oxygen = findViewById(R.id.Oxygen);
        heartRate = findViewById(R.id.HeartRate);
        database = FirebaseDatabase.getInstance();
        swimmer = new Swimmer();
        ref = database.getReference("Data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastHeartRate = swimmer.getHeartRate();
                lastSpO2 = swimmer.getOxygen();
                swimmer.setOxygen( dataSnapshot.child("SpO2").getValue(int.class));
                swimmer.setHeartRate( dataSnapshot.child("HeartRate").getValue(int.class));
                heartRate.setText("Heart Rate " + "\n" + swimmer.getHeartRate());
                oxygen.setText(("Oxygen \n" + swimmer.getOxygen()));

                isUserFine =  isInGoodHealthy(swimmer.getOxygen(),lastSpO2, swimmer.getHeartRate(),lastHeartRate);

                if (!isUserFine) {
                    showNotification("انذار", "شخص ما يتعرض للغرق","");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Oxygen_HeartRate_Value", "Hi there, you have an Error", databaseError.toException());
            }
        });
    }



    boolean isInGoodHealthy(double SpO2Value, double lastSpO2Value , double heartRateValue, double lastHeartRateValue){
        if(isOxygenInRange(SpO2Value,lastSpO2Value) && isHeartRateInRange(heartRateValue,lastHeartRateValue)){
            return true;
        }
        return false;
    }

    boolean isOxygenInRange(double currentOxygenValue, double lastOxygenValue){
        if(currentOxygenValue < 90 && lastOxygenValue > currentOxygenValue && lastOxygenValue - currentOxygenValue > 5 ){
            return false;
        }else{
            return true;
        }
    }

    boolean isHeartRateInRange(double currentHeartRate, double lastHeartRate){
        if(currentHeartRate < 60 && lastHeartRate > currentHeartRate && lastHeartRate - currentHeartRate > 10 ){
            return false;
        }else if (currentHeartRate > 80 && lastHeartRate < currentHeartRate && currentHeartRate - lastHeartRate  > 10){
            return false;
        }else{
            return true;
        }
    }

    private void showNotification(String title,String content,String discrption) {

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendinggIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(discrption,"Notification",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("nothing");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);


        }
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this,discrption);
        noBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendinggIntent);
        notificationManager.notify(new Random().nextInt(),noBuilder.build());

    }

}
