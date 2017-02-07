package lab2_201_8.uwaterloo.ca.lab2_201_8;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import android.widget.Toast;
import android.view.Gravity;
import android.util.Log;
import android.content.Context;

import ca.uwaterloo.sensortoy.LineGraphView;

public class Lab2_201_8 extends AppCompatActivity {
    LineGraphView graph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_201_8);

        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_lab2_201_8);


        graph = new LineGraphView(getApplicationContext(),100, Arrays.asList("x","y","z"));
        layout.addView(graph);
        graph.setVisibility(View.VISIBLE);


        TextView decided = new TextView(this);
        decided.setGravity(Gravity.CENTER_HORIZONTAL);
        decided.setTextSize(50);
        layout.addView(decided);


        SensorManager senManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelSensor = senManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        final accelSensorHandler accelHandler = new accelSensorHandler(graph,decided);
        senManager.registerListener(accelHandler, accelSensor, senManager.SENSOR_DELAY_GAME);


        /*  Button a = new Button(this);
        layout.addView(a);
        a.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v1){
                csvGenerator generateRecordFile = new csvGenerator(getApplicationContext(),accelHandler);
            }
        }); */


    }
}



class accelSensorHandler implements SensorEventListener{
    float[][] mostRecent = new float [100][3];
    LineGraphView graph;
    TextView tv1;
    int delay1=0,delay2=0;
    int flag1=0,flag2=0;
    float FILTER_CONSTANT= 45.0f;
    public accelSensorHandler(LineGraphView graph1, TextView tv){
        this.graph=graph1;
        this.tv1 = tv;
    }


    /*public void tabulate(PrintWriter printWriter){

        for(int j=0;j<100;j++){
            printWriter.println(String.format("%f,%f,%f",mostRecent[j][0],mostRecent[j][1],mostRecent[j][2]));
        }
    }*/

    public void onAccuracyChanged(Sensor s , int i){ }

    public void onSensorChanged(SensorEvent se){
        if(se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
        for(int i=1;i<100;i++){
            mostRecent[i-1][0] = mostRecent[i][0];
            mostRecent[i-1][1] = mostRecent[i][1];
            mostRecent[i-1][2] = mostRecent[i][2];
        }
            mostRecent[99][0] += (se.values[0] - mostRecent[99][0]) / FILTER_CONSTANT;
            mostRecent[99][1] += (se.values[1] - mostRecent[99][1]) / FILTER_CONSTANT;
            mostRecent[99][2] += (se.values[2] - mostRecent[99][2]) / FILTER_CONSTANT;
            graph.addPoint(mostRecent[99]);
            switch (flag1){
                case 0:
                   // Log.d("0","0");
                    delay1++;
                    if (mostRecent[99][0]-mostRecent[95][0]<-0.7){
                        flag1=-1;
                    }
                    else if (mostRecent[99][0]-mostRecent[95][0]>0.7){
                        flag1=1;
                    }

                    if (delay1>60 && delay2>60){
                        tv1.setText("UNKNOWN");
                        delay2=0;
                        delay1=0;
                    }

                    break;

                case 1:
                //    Log.d("1","1");
                    delay1 ++;
                    if (mostRecent[99][0]-mostRecent[95][0]<-0.8){
                        flag1=2;
                        delay1=0;
                    }
                    if(delay1 > 15) {
                        flag1 = 0;
                        delay1 = 0;
                    }
                    break;
                case 2:
               //     Log.d("2","2");
                    delay1 ++;
                    if (mostRecent[99][0]-mostRecent[96][0]>0.3){
                        tv1.setText("RIGHT");
                        flag1 = 0;
                        delay1=0;
                    }
                    if(delay1 > 15) {
                        flag1 = 0;
                        delay1 = 0;
                    }
                    break;

                case -1:
              //      Log.d("-1","-1");
                    delay1++;
                    if (mostRecent[99][0]-mostRecent[95][0]>0.7){
                        flag1=-2;
                        delay1=0;
                    }
                    if(delay1 > 15) {
                        flag1 = 0;
                        delay1 = 0;
                    }
                    break;
                case -2:
                //    Log.d("-2","-2");
                    delay1++;
                    if (mostRecent[99][0]-mostRecent[95][0]<-0.3){
                        tv1.setText("LEFT");
                        delay1=0;
                        flag1 = 0;
                    }
                    if(delay1 > 15) {
                        flag1 = 0;
                        delay1 = 0;
                    }
                    break;
            }


            switch (flag2){
                case 0:
                    delay2++;
                    if (mostRecent[99][2]-mostRecent[96][2]>0.5){
                    flag2=1;
                    }
                    else if (mostRecent[99][2]-mostRecent[95][2]<-0.5){
                    flag2=-1;
                    }
                    if (delay2>60 && delay1>60){
                        tv1.setText("UNKNOWN");
                        delay1=0;
                        delay2=0;
                    }
                    break;
                case 1:
                    delay2++;
                    if (mostRecent[99][2]-mostRecent[95][2]<-0.5){
                        flag2=2;
                        delay2=0;
                    }
                    if(delay2>15){
                        delay2=0;
                        flag2=0;
                    }
                    break;
                case 2:
                    delay2++;
                    if (mostRecent[99][2]-mostRecent[94][2]>0.45){
                        flag2=0;
                        delay2=0;
                        tv1.setText("DOWN");
                    }
                    if(delay2>15){
                        delay2=0;
                        flag2=0;
                    }
                    break;


                case -1:
                    delay2++;
                    if (mostRecent[99][2]-mostRecent[95][2]>0.5){
                        flag2=-2;
                        delay2=0;
                    }
                    if(delay2>15){
                        delay2=0;
                        flag2=0;
                    }
                    break;
                case -2:
                    delay2++;
                    if (mostRecent[99][2]-mostRecent[96][2]<-0.3){
                        flag2=0;
                        delay2=0;
                        tv1.setText("UP");
                    }
                    if(delay2>15){
                        delay2=0;
                        flag2=0;
                    }
                    break;
            }
        }
    }
}


/*class csvGenerator{
    public  csvGenerator(Context context,accelSensorHandler accelHandler){

        File file = null;
        PrintWriter printWriter = null;

        try{
            file = new File (context.getExternalFilesDir("ACC_SEN"), "Acc_sen.csv");
            printWriter = new PrintWriter(file);
            accelHandler.tabulate(printWriter);
            CharSequence textSuccess = "Record Generated";
            int duration = Toast.LENGTH_SHORT;
            Toast toast =Toast.makeText(context,textSuccess,duration);
            toast.setGravity(Gravity.BOTTOM,0,0);
            toast.show();
        }
        catch(IOException e){
            CharSequence textFailed = "Failed";
            int duration = Toast.LENGTH_SHORT;
            Toast toast =Toast.makeText(context,textFailed,duration);
            toast.setGravity(Gravity.BOTTOM,0,0);
            toast.show();
            Log.d("Acc_sen", "File Write Fail: " + e.toString());
        }
        finally{
            if(printWriter != null){
                printWriter.flush();
                printWriter.close();
            }

        }
    }
}*/
