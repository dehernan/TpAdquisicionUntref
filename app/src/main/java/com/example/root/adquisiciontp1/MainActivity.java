package com.example.root.adquisiciontp1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int azimuth = 0;
    private SensorManager sensorManager = null;
    private Sensor accelerometer;
    private Sensor magnetometer;

    boolean haveAccelerometer = false;
    boolean haveMagnetometer = false;

    private TextView azimuthText;
    private EditText directionEditText;
    private ImageView compassArrow;
    private ImageView compassFeet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.azimuthText = (TextView)findViewById(R.id.azimuthView);
        this.directionEditText = (EditText)findViewById(R.id.editText3);
        this.compassArrow = (ImageView)findViewById(R.id.compass_arrow);
        this.compassFeet = (ImageView)findViewById(R.id.compass_feet);
        inicializar();
    }

    private void inicializar(){

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = this.sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        this.haveAccelerometer = this.sensorManager.registerListener( sensorEventListener, this.accelerometer, SensorManager.SENSOR_DELAY_GAME );
        this.magnetometer = this.sensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
        this.haveMagnetometer = this.sensorManager.registerListener( sensorEventListener, this.magnetometer, SensorManager.SENSOR_DELAY_GAME );

    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        float[] accelerometer_data = new float[3]; // accelerometro
        float[] magnetic_field_data = new float[3]; // magnetometro
        float[] rMat = new float[9];
        float[] iMat = new float[9];
        float[] orientation = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] data;
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometer_data = event.values.clone();
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    magnetic_field_data = event.values.clone();
                    break;
            }

            if ( SensorManager.getRotationMatrix( rMat, iMat, accelerometer_data, magnetic_field_data ) ) {
                azimuth= (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
                azimuthText.setText(Integer.toString(azimuth));
                compassArrow.setRotation((float)(-(azimuth - 153.5)));
            }

            String compassFeetDirectionString = directionEditText.getText().toString();
            if (! compassFeetDirectionString.matches("")) {
                float compassFeetDirectionFloat = Float.valueOf(compassFeetDirectionString);
                compassFeet.setRotation(( - compassFeetDirectionFloat + 360) % 360);

            } else {
                compassFeet.setRotation((float) 0);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
}
