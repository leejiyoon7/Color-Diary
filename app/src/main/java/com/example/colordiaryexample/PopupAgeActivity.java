package com.example.colordiaryexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;

public class PopupAgeActivity extends Activity {

    String age;
    TextView textView;
    TextView textView6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_age);

        textView = findViewById(R.id.text_title);
        textView6 = findViewById(R.id.textView6);

        textView.setText(" 검색 하고 싶은 연령대를 입력 해주세요");
        textView6.setText(" ");


    }

    public void mOnClose(View v){
        //데이터 전달하기
        age = ((Spinner)findViewById(R.id.add_age)).getSelectedItem().toString();
        Intent intent = new Intent(this,ShareActivity.class);
        intent.putExtra("age",age);
        setResult(1,intent);
        finish();


   /*     Resources res = getResources();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Title")
                .setContentText("content")
                .setTicker("ticker")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res,R.mipmap.ic_launcher))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager mNotificaationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificaationManager.notify(0,builder.build());*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}