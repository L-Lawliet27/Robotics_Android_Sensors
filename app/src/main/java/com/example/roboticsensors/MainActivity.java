package com.example.roboticsensors;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    private TextView[][] screenTxt;
    private ConstraintLayout colorLay;
    private List<Sensor> sensorList;
    private Sensor prox, accel;
    private boolean activeSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorList = new ArrayList<>();

        LinearLayout ly = (LinearLayout) findViewById(R.id.pantalla);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        prox = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorList.add(prox);
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorList.add(accel);

        colorLay = findViewById(R.id.main);
        colorLay.setBackgroundColor(Color.WHITE);
        screenTxt = new TextView[sensorList.size()][4];

        for(Sensor s: sensorList){
            int i = sensorList.indexOf(s);
            TextView tv = new TextView(this);
            tv.setText(s.getName());
            ly.addView(tv);

            LinearLayout ln = new LinearLayout(this);
            ly.addView(ln);

            initTxt(i);

            if(s.getType()==Sensor.TYPE_PROXIMITY){
                TextView dist = new TextView(this);
                dist.setText(" Distance: ");
                ln.addView(dist);
                ln.addView(screenTxt[i][0]);
            }else {

                TextView x = new TextView(this);
                TextView y = new TextView(this);
                TextView z = new TextView(this);

                x.setText(" X: ");
                y.setText(" Y: ");
                z.setText(" Z: ");

                ln.addView(x);
                ln.addView(screenTxt[i][0]);
                ln.addView(y);
                ln.addView(screenTxt[i][1]);
                ln.addView(z);
                ln.addView(screenTxt[i][2]);

                TextView dir = new TextView(this);
                dir.setText(" Direction: ");
                ly.addView(dir);
                ly.addView(screenTxt[i][3]);
            }//else
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
        }//for
    }

    private void initTxt(int i){
        for(int j=0; j<4; j++) {
            screenTxt[i][j] = new TextView(this);
            screenTxt[i][j].setText("");
            screenTxt[i][j].setWidth(300);
        }
    }

    private void setAux(int x, int y){
        auxX = x;
        auxY = y;
    }


    int auxY, auxX;
    private void setDir(int x, int y, int z, int i, int j){

        boolean zRange = -10 < z && z < 10;

        if((x==0 && y==0) && (z==10 || z==-10)){
            screenTxt[i][j].setText("PARALLEL");
            if(!activeSensor) colorLay.setBackgroundColor(Color.BLUE);
            setAux(x, y);
        }//if

        if((x==0 || z==0) && (y==10 || y==-10)){
            screenTxt[i][3].setText("PERPENDICULAR - Y");
            if(!activeSensor) colorLay.setBackgroundColor(Color.YELLOW);
            setAux(x, y);
        }//if

        if((y==0 && z==0) && (x==10 || x==-10)){
            screenTxt[i][j].setText("PERPENDICULAR - X");
            if(!activeSensor) colorLay.setBackgroundColor(Color.GREEN);
            setAux(x, y);
        }//if

        if(y==0 && zRange){
            if(x < auxX){
                screenTxt[i][j].setText("Turning Right");
                if(!activeSensor) colorLay.setBackgroundColor(Color.WHITE);
                setAux(x, y);
            } else if(x > auxX){
                screenTxt[i][j].setText("Turning Left");
                if(!activeSensor) colorLay.setBackgroundColor(Color.WHITE);
                setAux(x, y);
            }//else
        }//if


        if(x==0 && zRange){
            if (y < auxY){
                screenTxt[i][j].setText("Turning Downwards");
                if(!activeSensor) colorLay.setBackgroundColor(Color.WHITE);
                setAux(x, y);
            }else if (y > auxY){
                screenTxt[i][j].setText("Turning Upwards");
                if(!activeSensor) colorLay.setBackgroundColor(Color.WHITE);
                setAux(x, y);
            }//else
        }//if
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this){
            switch(event.sensor.getType()){

                case Sensor.TYPE_ACCELEROMETER:
                    int a = sensorList.indexOf(accel);
                    int x = Math.round(event.values[0]);
                    int y = Math.round(event.values[1]);
                    int z = Math.round(event.values[2]);
                    for (int i = 0; i < event.values.length; i++) {
                        screenTxt[a][i].setText(String.valueOf(Math.round(event.values[i])));
                    }//for
                    setDir(x, y, z, a, 3);
                    break;

                case Sensor.TYPE_PROXIMITY:
                    int p = sensorList.indexOf(prox);
                    if(event.values[0] == 0){
                        screenTxt[p][0].setText("CLOSE");
                        colorLay.setBackgroundColor(Color.RED);
                        activeSensor = true;
                    }else{
                        screenTxt[p][0].setText("FAR");
                        colorLay.setBackgroundColor(Color.WHITE);
                        activeSensor = false;
                    }//else
                    break;
            }
        }//sync
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}