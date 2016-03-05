package com.pv.m_eager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * @author imi
 */
public class AppHeadService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();
        showMainHead();
        final ClipboardManager clipboard= (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (clipboard.hasPrimaryClip()) {
                    ClipDescription description = clipboard.getPrimaryClipDescription();
                    android.content.ClipData data = clipboard.getPrimaryClip();
                    if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        Toast.makeText(getApplicationContext(), String.valueOf(data.getItemAt(0).getText()), Toast.LENGTH_SHORT).show();
                    } else if(data !=null && description!=null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                        Toast.makeText(getApplicationContext(), String.valueOf(data.getItemAt(0).getText()), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showMainHead(){
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        chatHead = new ImageView(this);
        chatHead.setImageResource(android.R.drawable.ic_dialog_dialer);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);

        chatHead.setOnTouchListener(new HeadTouchListener(params));
        chatHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Clicked chat head", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class HeadTouchListener implements View.OnTouchListener{
        private WindowManager.LayoutParams mParams;
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        private HeadTouchListener(WindowManager.LayoutParams params){
            this.mParams = params;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    initialX = mParams.x;
                    initialY = mParams.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                    mParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(chatHead,mParams);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("stop_service", false)){
            // If it's a call from the notification, stop the service.
            stopSelf();
        }else{
            // Make the service run in foreground so that the system does not shut it down.
            Intent notificationIntent = new Intent(this, AppHeadService.class);
            notificationIntent.putExtra("stop_service", true);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle("Kill Service");
            notificationBuilder.setTicker("Click to close");
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setAutoCancel(false);
            notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
            startForeground(86, notificationBuilder.build());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
