package com.example.colordiaryexample;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class PopupActivity extends Activity {
    String preEmotion;
    TextView textView;
    TextView textView6;

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);




        Intent intent = getIntent();
        String data= intent.getStringExtra("preEmotion");

        if(data.equals("긍정")){
            setContentView(R.layout.activity_popup);
        }else if(data.equals("부정")){
            setContentView(R.layout.activity_badpopup);
        }

        textView = findViewById(R.id.text_title);
        textView6 = findViewById(R.id.textView6);


        //  Intent intent = getIntent();
        //  String data = intent.getStringExtra("emotion");


        textView.setText("당신의 감정은 "+data+"적이네요! ");
        textView6.setText("당신의 자세한 감정을 말해주세요");
        Log.d("ffffffffffff",""+data);

    }

    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = getIntent();


        String title = intent.getStringExtra("title");
        String data= intent.getStringExtra("preEmotion");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference washingtonRef = db.collection("diarys/"+user.getUid()+"/diarys").document(title);

        preEmotion = ((Spinner)findViewById(R.id.add_preEmotion)).getSelectedItem().toString();
        washingtonRef
                .update("pre_emotion", preEmotion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ttttttt", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("fffffff", "Error updating document", e);
                    }
                });

        //액티비티(팝업) 닫기
        if(data.equals("부정")) {
            NotificationSomethings();
        }
        finish();


     //   Intent intent1 = new Intent(this,MainActivity.class);
    //   startActivity(intent1);

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


    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivityForResult(intent,1);
    }

    public void insertString(String[] string,String[] string2){
        string[0] = "페르시아 격언";
        string[1] = "콜린 파월";
        string[2] = "셸리";
        string[3] = "시브 카에라";
        string[4] = "로잘린 카터";
        string[5] = "쇼펜 하우어";
        string[6] = "가브리엘 샤넬";
        string[7] = "프리드리히 니체";
        string[8] = "사뮈엘 베케트";
        string[9] = "랄프 왈도 에머슨";
        string[10] = "안토니오 그람시";
        string[11] = "공자";
        string[12] = "샤롤 드골";
        string[13] = "루이스 E.분";
        string[14] = "윈스턴 처칠";
        string[15] = "엘레노어 루즈벨트";
        string[16] = "작가 미상";
        string[17] = "로잘린 카터";
        string[18] = "마크 트웨인";
        string[19] = "엘버트 하버드";
        string[20] = "A.단테";
        string2[0] = "이 또한 지나가리라.";
        string2[1] = "지속적인 긍정적 사고는 능력을 배로 높인다.";
        string2[2] = "겨울이 오면 봄은 멀지 않다";
        string2[3] = "긍정적인 생각과 합쳐진 긍정적인 행동은 성공을 불러온다.";
        string2[4] = "자신의 능력을 믿어야 한다. 그리고 끝까지 굳세게 밀고 나가라";
        string2[5] = "우리는 다른 사람과 같아지기 위해 인생의 3/4를 빼앗기고 있다.";
        string2[6] = "가장 용감한 행동은 자신을 위해 생각하고 그것을 큰소리로 외치는 것이다.";
        string2[7] = "스스로를 경멸하는 사람은, 경멸하는 자신을 존중한다.";
        string2[8] = "또 실패했는가? 괜찮다. 다시 실행해라. 그리고 더 나은 실패를 해라";
        string2[9] = "나에 대한 자신감을 잃으면 온 세상이 나의 적이 된다.";
        string2[10] = "이성으로 비관해도 의지로써 낙관해라";
        string2[11] = "스스로 자신을 존경하면 다른 사람도 그대를 존경할 것이다.";
        string2[12] = "할 수 있다고 믿는 사람은 그렇게 되고, 할 수 없다고 믿는 사람도 역시 그렇게 된다";
        string2[13] = "인생에서 가장 슬픈 세가지, 할 수 있었는데, 해야 했는데, 해야만 했는데";
        string2[14] = "비관론자는 어떤 기회가 찾아와도 어려움만 보고, 낙관론자는 어떤 난관이 찾아와도 기회를 본다";
        string2[15] = "남들이 당신을 어떻게 생각할까 너무 걱정하지마라. 남들은 그렇게 당신에 대해 많이 생각하지 않는다";
        string2[16] = "스스로 알을 깨면 병아리가 되지만, 남이 깨주면 프라이가 된다.";
        string2[17] = "자신의 능력을 믿어야 한다. 그리고 끝까지 굳세게 밀고 나가라";
        string2[18] = "우리가 가진 15개의 재능으로 칭찬 받으려 하기보다, 가지지도 않은 한가지 재능으로 돋보이려 안달한다.";
        string2[19] = "당신이 저지를 수 있는 가장 큰 실수는 실수를 할까 두려워 하는 것이다";
        string2[20] = "너의 길을 가라. 남들이 무엇이라 하든지 내버려 두라";


    }

    public void NotificationSomethings() {

        Random rnd = new Random();
        int p = rnd.nextInt(20);
        Log.d("랜덤변수값", ""+p);

        String[] title = new String[20];
        String[] text = new String[20];
        insertString(title,text);



        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("notificationId", "성공"); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle(title[p])
                .setContentText(text[p])
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }
}