package com.example.root.adquisiciontp1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    private int azimuth = 0;
    private SensorManager sensorManager = null;
    private Sensor accelerometer;
    private Sensor magnetometer;

    boolean haveAccelerometer = false;
    boolean haveMagnetometer = false;

    private TextView azimuthText;
    private TextView timer;
    private TextView directionEditText;
    private ImageView compassArrow;
    private ImageView left_indicator;
    private ImageView right_indicator;
    private ImageView compassFeet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.azimuthText = (TextView)findViewById(R.id.azimuthView);
        this.timer = (TextView)findViewById(R.id.timer);
        this.directionEditText = (TextView)findViewById(R.id.directionTogo);
        this.compassArrow = (ImageView)findViewById(R.id.compass_arrow);
        this.compassFeet = (ImageView)findViewById(R.id.compass_feet);
        this.left_indicator = (ImageView)findViewById(R.id.left_indicator);
        this.right_indicator = (ImageView)findViewById(R.id.right_indicator);

        if ( getIntent().getSerializableExtra("navigationPlan") != null) {

            List<Pair<Long,Long>> result = (List<Pair<Long,Long>>) getIntent().getSerializableExtra("navigationPlan");
            this.startNavigationCount(result);

        }

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

            if (!(azimuthText.getText().toString().matches("") || directionEditText.getText().toString().matches(""))){
                if (!azimuthText.getText().toString().equals(directionEditText.getText().toString())){

                    int direction = Integer.parseInt(directionEditText.getText().toString());
                    float alpha;
                    //DE ACA A QUE TERMINAN LOS IFS ES UN ASCO, HAY QUE VER COMO LO RE-ARMAMOS.. IT WORKS
                    if (abs(direction-azimuth) >= 5){

                        if (direction <= 180){
                            if ((azimuth - direction) >= 0 && (azimuth - direction) <= 180){
                                alpha = (float)(azimuth-direction) / 180f;
                                right_indicator.setAlpha(alpha);
                                left_indicator.setAlpha(0L);

                            }else{
                                if (azimuth > direction){
                                    alpha = (float) (360-azimuth + direction) / 180f;
                                }else{
                                    alpha = (float) (direction-azimuth) / 180f;
                                }
                                left_indicator.setAlpha(alpha);
                                right_indicator.setAlpha(0L);

                            }
                        }else{

                            if (azimuth > (direction - 180) && azimuth < direction){
                                alpha = (float)(direction-azimuth) / 180f;
                                right_indicator.setAlpha(alpha);
                                left_indicator.setAlpha(0L);
                            }else{
                                if (azimuth > direction){
                                    alpha = (float) (azimuth - direction) / 180f;
                                }else{
                                    alpha = (float) (360-direction+azimuth) / 180f;
                                }
                                right_indicator.setAlpha(0L);
                                left_indicator.setAlpha(alpha);
                            }
                        }
                    }else{
                        right_indicator.setAlpha(0L);
                        left_indicator.setAlpha(0L);
                    }
                }
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

    public void openNavigationWindow(View view){
        Intent intent = new Intent(this, navigationActivity.class);
        startActivity(intent);

    }

    protected void startNavigationCount(List<Pair<Long,Long>> navigationList){

        if (!navigationList.isEmpty()){

            Pair<Long,Long> navPair;
            navPair = navigationList.get(0);
            this.directionEditText.setText(navPair.getLeft().toString());
            Long timeOfTimer = navPair.getRight();
            final CounterTimer globalTimer = new CounterTimer(timeOfTimer*1000, 1000, navigationList);
            globalTimer.start();
        }else{
            timer.setText("Finalizado");
        }
    }

    public class CounterTimer extends CountDownTimer{

        private List<Pair<Long,Long>> navigation;

         CounterTimer(long millisInfuture, long countDowninterval, List<Pair<Long,Long>> navigation){
            super(millisInfuture,countDowninterval);
            this.navigation = navigation;
        }

        @Override
        public void onTick(long millisUntillFinished){

            timer.setText("Segundos restantes: "+String.valueOf((millisUntillFinished/1000)));
        }

        @Override
        public void onFinish(){
            this.navigation.remove(0);
            startNavigationCount(this.navigation);
        }
    }
}
