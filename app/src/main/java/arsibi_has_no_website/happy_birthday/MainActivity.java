package arsibi_has_no_website.happy_birthday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Sensor mProximitySensor;
    SensorManager manager;
    Uri notification;
    MediaPlayer mp;
    Runnable r;
    Thread t;
    String s="";
    long delay=0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        DrawingView dv= new DrawingView(getApplicationContext());
        setContentView(dv);
        new Thread(dv).start();
        manager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor=manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.birthday);
        r=new Runnable() {
            @Override
            public void run() {
                long curr = System.currentTimeMillis();
                 delay = 0;
                while (delay < 5000&&!Thread.interrupted()) {
                    delay = System.currentTimeMillis() - curr;
                    if(delay>5000)
                        delay=5000;
                    s=String.format("%.3f",10-(float)delay/1000);
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e){
                        t=new Thread(this);
                        return;
                    }
                }
                if(!Thread.interrupted()){
                    mp.start();
                    mp.setLooping(true);
                    mp.seekTo(0);
                }
                t=new Thread(this);
            }
        };
        t=new Thread(r);
        Toast.makeText(getApplicationContext(),"Figure  out what to do :*.If anyone asks we're not lovers",Toast.LENGTH_LONG).show();Toast.makeText(getApplicationContext(),"Figure  out what to do :*.If anyone asks we're not lovers",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
            if(event.values[0]<event.sensor.getMaximumRange()){
                if(!mp.isPlaying()) {
                    if(!t.isAlive())
                        t.start();
                }
            }
            else {
                if (t.isAlive()) {
                    t.interrupt();
                    t = new Thread(r);
                    delay=0;
                }
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        manager.registerListener(this,mProximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void onPause(){
        super.onPause();
        manager.registerListener(this,mProximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    class DrawingView extends View implements Runnable{
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                invalidate();
            }
        };
        Bitmap b;
        Paint p;
        Paint paint;
        Paint textPaint;
        Context context;
        DrawingView(Context c){
            super(c);
            context=c;
            b= BitmapFactory.decodeResource(getResources(),R.drawable.heart);
            p=new Paint();
            paint=new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            textPaint=new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,40,getResources().getDisplayMetrics()));
            p.setDither(true);
            p.setAntiAlias(true);
        }

        @Override
        public void run() {
            while(true)
            try{
                Thread.sleep(1000/60);
                handler.sendEmptyMessage(0);
            }catch (InterruptedException e){}
        }
        float x=0;
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(0,0,getWidth(),getHeight(),paint);
            canvas.drawBitmap(b,200,0,p);
            canvas.drawRect(200,0,b.getWidth()+200,b.getHeight()*2*(1-((float)delay/5000)),paint);
            if(delay==5000){
                canvas.drawText("Happy Birthday",100,b.getHeight()+100,textPaint);
            }
        }
    }
}
