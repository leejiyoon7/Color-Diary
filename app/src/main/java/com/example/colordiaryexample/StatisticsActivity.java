package com.example.colordiaryexample;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();
    List<DiaryInfo> diarySave = new ArrayList<>();
    List<DiaryInfo> diarySaving = new ArrayList<>();
    List<MemberInfo> diaryMember = new ArrayList<>();
    int goodEmotion = 0;
    int badEmotion = 0;
    int badPastEmotion = 0;
    int a[] = {0,0,0,0,0,0,0,0};
    int b[] = {0,0,0,0,0,0,0,0};
    TextView textviewNumber;

    PendingIntent intent;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);






        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM");
        SimpleDateFormat sdfNowDay = new SimpleDateFormat("dd");
        SimpleDateFormat sdfNowTime = new SimpleDateFormat("HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date); // 현재 달수
        String formatDay = sdfNowDay.format(date); //현재 요일
        String formatTime = sdfNowTime.format(date);
        int MonthPast = Integer.parseInt(formatDate)-1;
        String formatPast = String.valueOf(MonthPast); // 한달전 달수
        Log.d("출력확인",""+formatPast);
        Log.d("출력확인 날짜",""+formatDay);

        diarySave = getCall(formatDate,formatDay ,formatPast,formatTime);


        EmotionSave emotionCount = (EmotionSave)getApplicationContext();


    }

    public List<DiaryInfo> getCall(String start, String day, String past, String time) {
        final List<DiaryInfo> diarySave = new ArrayList<>();

        final ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();

        final List<DiaryInfo> diarySave2 = new ArrayList<>();

        final ArrayList<Map<String, Object>> diarys2 = new ArrayList<Map<String, Object>>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("files/uid");

        db.collection("diarys/"+user.getUid()+"/diarys")
                .whereGreaterThan("db_time","2020/"+start+"/01 00:00:00")
                .whereLessThan("db_time","2020/"+start+"/"+day+" "+time)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                diarys.add(document.getData());
                                Log.d("개수세기","1123131");
                            }
                            for (int i=diarys.size()-1;i>-1;i--) {
                                DiaryInfo diaryInfo = new DiaryInfo((String)diarys.get(i).get("title"),(String)diarys.get(i).get("date"),(String)diarys.get(i).get("content"),(String)diarys.get(i).get("picture"),(String)diarys.get(i).get("db_date"),(String)diarys.get(i).get("db_time"),(String)diarys.get(i).get("pre_emotion"),(String)diarys.get(i).get("birthday"));
                                diarySave.add(diaryInfo);
                            }
                            for(int i=0; i<diarys.size();i++){
                                DiaryInfo diaryInfo = new DiaryInfo((String)diarys.get(i).get("title"),(String)diarys.get(i).get("date"),(String)diarys.get(i).get("content"),(String)diarys.get(i).get("picture"),(String)diarys.get(i).get("db_date"),(String)diarys.get(i).get("db_time"),(String)diarys.get(i).get("pre_emotion"),(String)diarys.get(i).get("birthday"));
                                diarySave.add(diaryInfo);
                                switch(diarySave.get(i).getPre_emotion()){
                                    case "무기력":
                                        badEmotion++;

                                        break;
                                    case "자신감없음":
                                        badEmotion++;

                                        break;
                                    case "우울":
                                        badEmotion++;

                                        break;
                                    case "불안정":
                                        badEmotion++;

                                        break;
                                    case "긴장":
                                        badEmotion++;

                                        break;
                                    case "지침":
                                        badEmotion++;

                                        break;
                                    case "혼란스러움":
                                        badEmotion++;


                                        break;
                                    case "화남":
                                        badEmotion++;

                                        break;
                                }

                            }
                            EmotionSave emotionCount = (EmotionSave)getApplicationContext();


                            emotionCount.setBadstate(badEmotion);
                            Log.d("테스트해봄 1234", ""+(emotionCount.getState()+emotionCount.getBadstate()));
                        } else {


                        }
                        Log.d("테스트",""+badEmotion);
                    }
                });

        db.collection("diarys/"+user.getUid()+"/diarys")
                .whereGreaterThan("db_time","2020/"+past+"/"+day+" 00:00:00")
                .whereLessThan("db_time","2020/"+past+"/32 00:00:00")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                diarys2.add(document.getData());
                            }
                            for (int i=diarys2.size()-1;i>-1;i--) {
                                DiaryInfo diaryInfo = new DiaryInfo((String)diarys2.get(i).get("title"),(String)diarys2.get(i).get("date"),(String)diarys2.get(i).get("content"),(String)diarys2.get(i).get("picture"),(String)diarys2.get(i).get("db_date"),(String)diarys2.get(i).get("db_time"),(String)diarys2.get(i).get("pre_emotion"),(String)diarys2.get(i).get("birthday"));
                                diarySave2.add(diaryInfo);
                            }
                            for(int i=0; i<diarys2.size();i++){
                                DiaryInfo diaryInfo = new DiaryInfo((String)diarys2.get(i).get("title"),(String)diarys2.get(i).get("date"),(String)diarys2.get(i).get("content"),(String)diarys2.get(i).get("picture"),(String)diarys2.get(i).get("db_date"),(String)diarys2.get(i).get("db_time"),(String)diarys2.get(i).get("pre_emotion"),(String)diarys2.get(i).get("birthday"));
                                diarySave2.add(diaryInfo);
                                switch(diarySave2.get(i).getPre_emotion()){
                                    case "무기력":
                                        badPastEmotion++;

                                        break;
                                    case "자신감없음":
                                        badPastEmotion++;

                                        break;
                                    case "우울":
                                        badPastEmotion++;

                                        break;
                                    case "불안정":
                                        badPastEmotion++;

                                        break;
                                    case "긴장":
                                        badPastEmotion++;

                                        break;
                                    case "지침":
                                        badPastEmotion++;

                                        break;
                                    case "혼란스러움":
                                        badPastEmotion++;

                                        break;
                                    case "화남":
                                        badPastEmotion++;

                                        break;
                                }

                            }
                            EmotionSave emotionCount = (EmotionSave)getApplicationContext();
                            emotionCount.setState(badPastEmotion);
                            int result = emotionCount.getState()+emotionCount.getBadstate();
                            textviewNumber = findViewById(R.id.textView2);
                            textviewNumber.setText(""+result+" 일의");

                        } else {


                        }

                    }
                });



        return diarySave;
    }


    public void mOnGraph(View v){

        Intent intent = new Intent(this,GraphActivity.class);
        startActivity(intent);


    }

    public void onBackPressed() {
        //안드로이드 백버튼 막기

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }

}