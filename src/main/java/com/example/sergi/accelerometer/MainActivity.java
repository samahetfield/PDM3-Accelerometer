package com.example.sergi.accelerometer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergi.accelerometer.db.AppDatabase;
import com.example.sergi.accelerometer.db.Rutina;

import java.util.List;

public class MainActivity extends AppCompatActivity{
    private AppDatabase mDb;
    private static int contador_ejercicio = 0;
    private String user_connected;
    private List<Rutina> rutina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInMemoryDatabase(getApplicationContext());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        user_connected = extras.getString("USER");

        if(intent.hasExtra("action")){
            if (extras.getString("action").equals("PasaEjer")){
                pasaEjer(new View(getApplicationContext()));
            }
            else if (extras.getString("action").equals("AnteriorEjer")){
                anteriorEjer(new View(getApplicationContext()));
            }
        }
        else{
            rutina = mDb.rutModel().getRutinasFromUser(user_connected);

            Rutina rut = rutina.get(contador_ejercicio%rutina.size());
            String ejer = rut.id_ejercicio;

            String imagename = mDb.ejerModel().getImageName(ejer);

            ImageView image = (ImageView) findViewById(R.id.image_ej);

            int id = getResources().getIdentifier(imagename, "mipmap", getPackageName());
            image.setImageResource(id);

            TextView series = (TextView) findViewById(R.id.num_series);
            series.setText(rut.series);
            TextView repes = (TextView) findViewById(R.id.num_repeticiones);
            repes.setText(rut.repeticiones);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected void pasaEjer(View v){
        contador_ejercicio=(contador_ejercicio+1) % rutina.size();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("USER", user_connected);
        intent.putExtras(extras);

        startActivity(intent);
    }

    protected  void anteriorEjer(View v){
        contador_ejercicio = (((contador_ejercicio-1)%(rutina.size()))+rutina.size())%rutina.size();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("USER", user_connected);
        intent.putExtras(extras);

        startActivity(intent);
    }


    protected void playEjer(View v){
        Intent i = new Intent(getApplicationContext(), DoingExer.class);
        Bundle extras = new Bundle();
        extras.putString("Repeticiones", rutina.get(contador_ejercicio).repeticiones);
        extras.putString("Series", rutina.get(contador_ejercicio).series);
        i.putExtras(extras);
        startActivity(i);

    }


}
