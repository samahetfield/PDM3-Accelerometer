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
        import android.widget.TextView;

public class DoingExer extends AppCompatActivity implements SensorEventListener {

    Chronometer cronometer;
    Button play, pausa;
    long time=0;

    private boolean doing = false;
    private int ejercicio_completo = 0;
    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

   // private TextView currentX, currentY, currentZ;
    TextView series, repeticiones;
    int num_repes, num_repes_max, num_series;

    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_exer);
        //initializeViews();

        cronometer = findViewById(R.id.crono);
        play = findViewById(R.id.play);
        pausa = findViewById(R.id.pause);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setEnabled(false);
                cronometer.setBase(SystemClock.elapsedRealtime());
                cronometer.start();
                doing = true;
            }
        });

        pausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausa.setEnabled(false);
                play.setEnabled(true);
                cronometer.stop();
                doing = false;
            }
        });



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        Intent i = getIntent();

        String repes_aux = i.getStringExtra("Repeticiones");
        String series_aux = i.getStringExtra("Series");

        num_repes = Integer.parseInt(repes_aux);
        num_series = Integer.parseInt(series_aux);
        num_repes_max = num_repes;
        series = findViewById(R.id.series_ejer);
        repeticiones = findViewById(R.id.repeticiones_ejer);

        repeticiones.setText(i.getStringExtra("Repeticiones"));
        series.setText(i.getStringExtra("Series"));

    }
/*
    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

    }
*/
    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
       // displayCleanValues();
        // display the current x,y,z accelerometer values
        //displayCurrentValues();
        // display the max x,y,z accelerometer values
        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        if(doing){
            // if the change is below 2, it is just plain noise
            if (deltaX < 2)
                deltaX = 0;
            if (deltaY < 2)
                deltaY = 0;
            if ((deltaX > 10) || (deltaY > 10) || (deltaZ > 10)) {
                ejercicio_completo++;
                v.vibrate(500);
            }

            if(ejercicio_completo == 2 && num_repes != 0){
                num_repes--;

                repeticiones.setText(Integer.toString(num_repes));
                ejercicio_completo = 0;
            }
            else if(num_repes == 0 && num_series != 0){
                num_series--;
                num_repes = num_repes_max;

                repeticiones.setText(Integer.toString(num_repes));
                series.setText(Integer.toString(num_series));

            }

            if(num_series == 0){
                cronometer.stop();
                doing = false;
            }
        }

    }
/*
    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    */
}
