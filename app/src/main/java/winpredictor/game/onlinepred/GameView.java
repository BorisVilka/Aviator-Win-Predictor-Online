package winpredictor.game.onlinepred;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameView extends SurfaceView {

    private SurfaceHolder holder;
    private Paint bitmapPaint, text;
    public boolean paused;
    private Bitmap plane, bg;
    private int cor;
    private boolean front;
    public int score;
    List<Model> models = new ArrayList<>();
    Bitmap[] arr = new Bitmap[5];

    public GameView(Context context, AttributeSet set) {
        super(context,set);
        paused = false;
        score = 0;
        front = false;

        text = new Paint();
        text.setTextSize(80);
        text.setColor(Color.WHITE);
        if(Build.VERSION.SDK_INT>=26) {
            text.setTypeface(context.getResources().getFont(R.font.font));
        }
        plane = BitmapFactory.decodeResource(getResources(),R.drawable.plane);
        plane = Bitmap.createScaledBitmap(plane,plane.getWidth()/16,plane.getHeight()/16,true);
        bg = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        bg = Bitmap.createScaledBitmap(bg,bg.getWidth()/2,bg.getHeight()/2,true);

        arr[0] = BitmapFactory.decodeResource(getResources(),R.drawable.m1);
        arr[0] = Bitmap.createScaledBitmap(arr[0],arr[0].getWidth()/8,arr[0].getHeight()/8,true);

        arr[1] = BitmapFactory.decodeResource(getResources(),R.drawable.m8);
        arr[1] = Bitmap.createScaledBitmap(arr[1],arr[1].getWidth()/8,arr[1].getHeight()/8,true);

        arr[2] = BitmapFactory.decodeResource(getResources(),R.drawable.m6);
        arr[2] = Bitmap.createScaledBitmap(arr[2],arr[2].getWidth()/8,arr[2].getHeight()/8,true);

        arr[3] = BitmapFactory.decodeResource(getResources(),R.drawable.numb1);
        arr[3] = Bitmap.createScaledBitmap(arr[3],arr[3].getWidth()/8,arr[3].getHeight()/8,true);

        arr[4] = BitmapFactory.decodeResource(getResources(),R.drawable.numb3);
        arr[4] = Bitmap.createScaledBitmap(arr[4],arr[4].getWidth()/8,arr[4].getHeight()/8,true);


        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                cor = canvas.getWidth()-plane.getWidth();
                if (canvas != null) {
                    draw(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

        });

        Thread updateThread = new Thread() {
            public void run() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!paused) {
                            update.run();
                        }
                    }
                }, 0, 16);
            }
        };

        updateThread.start();

    }

    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                front = true;
                break;
            case MotionEvent.ACTION_UP:
                front = false;
                break;
        }
        postInvalidate();
        return true;
    }

    Random random = new Random();
    Runnable update = new Runnable() { //draws and updates bird and pipes
        @Override
        public void run() {
           try {
                Canvas canvas = holder.lockCanvas();
                if (front) {
                    if(cor>=0) cor -= 10;
                } else {
                    if(cor<canvas.getWidth()-plane.getWidth()) cor+=10;
                }
                canvas.drawBitmap(bg,0,0,bitmapPaint);
                for(int j = 0;j<models.size();j++) {
                    Model i = models.get(j);
                    canvas.drawBitmap(arr[i.id],i.x,i.y,bitmapPaint);
                    i.x-= i.vect==1 ? -5 : 5;
                    i.y+=5;
                    if(i.y>=arr[i.id].getHeight()+canvas.getHeight()) {
                        models.remove(j);
                        j--;
                    }
                    if(Math.abs(i.y-canvas.getHeight()+50)<=plane.getHeight() && Math.abs(i.x-cor)<=plane.getWidth()) {
                        score+=5;
                        models.remove(j);
                        j--;
                    }
                }
                if(models.size()<3) {
                    int v = random.nextInt(2)+1;
                    models.add(new Model((v==1 ? -arr[0].getWidth() : canvas.getWidth()+arr[0].getWidth()),random.nextInt(canvas.getHeight()),v,random.nextInt(arr.length)));
                }
                canvas.drawText("Score: "+score,canvas.getWidth()-350,110,text);
                canvas.drawBitmap(plane,cor,canvas.getHeight()-plane.getHeight()-50,bitmapPaint);
                holder.unlockCanvasAndPost(canvas);
            } catch (Exception ignored) {

            }
        }
    };
    EndListener listener;
    public void setEnd(EndListener listener) {
        this.listener = listener;
    }
    public void togglePause() {
        paused = !paused;
    }

    private static class Model {
        float x,y;
        int vect, id;
        public Model(float x, float y, int vect, int id) {
            this.x =x;
            this.y = y;
            this.vect = vect;
            this.id = id;
        }
    }
    public static interface EndListener {
        void end();
    }
}
