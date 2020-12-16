package com.example.colordiaryexample;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {
    ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();
    List<DiaryInfo> diarySave = new ArrayList<>();
    List<DiaryInfo> diarySaving = new ArrayList<>();
    List<MemberInfo> diaryMember = new ArrayList<>();
    int goodEmotion = 0;
    int badEmotion = 0;
    int badPastEmotion = 0;
    int a[] = {0,0,0,0,0,0,0,0};
    int b[] = {0,0,0,0,0,0,0,0};
    TextView textviewRed;
    TextView textviewOrange;
    TextView textviewYellow;
    TextView textviewGreen;
    TextView textviewBlue;
    TextView textviewPurple;
    TextView textviewPink;
    TextView textviewWhite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
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

        textviewRed = findViewById(R.id.textView5);
        textviewOrange = findViewById(R.id.textView6);
        textviewYellow= findViewById(R.id.textView7);
        textviewGreen = findViewById(R.id.textView8);
        textviewBlue = findViewById(R.id.textView9);
        textviewPurple = findViewById(R.id.textView10);
        textviewPink = findViewById(R.id.textView11);
        textviewWhite = findViewById(R.id.textView12);


        textviewRed.setBackgroundResource(R.drawable.red);
        textviewOrange.setBackgroundResource(R.drawable.orange);
        textviewYellow.setBackgroundResource(R.drawable.yellow);
        textviewGreen.setBackgroundResource(R.drawable.green);
        textviewBlue.setBackgroundResource(R.drawable.blue);
        textviewPurple.setBackgroundResource(R.drawable.purple);
        textviewPink.setBackgroundResource(R.drawable.pink);
        textviewWhite.setBackgroundResource(R.drawable.white);

        diarySave = getCall(formatDate,formatDay ,formatPast,formatTime);


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
                                        b[0]++;
                                        break;
                                    case "자신감없음":
                                        badEmotion++;
                                        b[1]++;
                                        break;
                                    case "우울":
                                        badEmotion++;
                                        b[2]++;
                                        break;
                                    case "불안정":
                                        badEmotion++;
                                        b[3]++;
                                        break;
                                    case "긴장":
                                        badEmotion++;
                                        b[4]++;
                                        break;
                                    case "지침":
                                        badEmotion++;
                                        b[5]++;
                                        break;
                                    case "혼란스러움":
                                        badEmotion++;
                                        b[6]++;

                                        break;
                                    case "화남":
                                        badEmotion++;
                                        b[7]++;
                                        break;
                                }

                            }
                            EmotionSave emotionCount = (EmotionSave)getApplicationContext();
                            Log.d("개수세기",""+badEmotion);

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
                                        a[0]++;
                                        break;
                                    case "자신감없음":
                                        badPastEmotion++;
                                        a[1]++;
                                        break;
                                    case "우울":
                                        badPastEmotion++;
                                        a[2]++;
                                        break;
                                    case "불안정":
                                        badPastEmotion++;
                                        a[3]++;
                                        break;
                                    case "긴장":
                                        badPastEmotion++;
                                        a[4]++;
                                        break;
                                    case "지침":
                                        badPastEmotion++;
                                        a[5]++;
                                        break;
                                    case "혼란스러움":
                                        badPastEmotion++;
                                        a[6]++;
                                        break;
                                    case "화남":
                                        badPastEmotion++;
                                        a[7]++;
                                        break;
                                }

                            }
                            EmotionSave emotionCount = (EmotionSave)getApplicationContext();

                            Log.d("테스트해봄 12346789", ""+(emotionCount.getState()+emotionCount.getBadstate()));
                            for(int i = 0;i<a.length;i++){
                                Log.d("음음",""+a[i]);
                            }
                            int result = emotionCount.getState()+emotionCount.getBadstate();
                            emotionCount.setBad1(a[0],b[0]);emotionCount.setBad2(a[1],b[1]);emotionCount.setBad3(a[2],b[2]);emotionCount.setBad4(a[3],b[3]);emotionCount.setBad5(a[4],b[4]);emotionCount.setBad6(a[5],b[5]);emotionCount.setBad7(a[6],b[6]);emotionCount.setBad8(a[7],b[7]);
                            for(int i=0;i<8;i++){
                                Log.d("a의수", ""+a[i]);
                                Log.d("b의수", ""+b[i]);
                            }

                            textviewRed.setText("빨강 / 무기력  "+emotionCount.getBad1()+" 회\n빈혈, 감기, 무기력증, 우울증 중상에 도움을 줍니다.\n 추진력이 필요할 때 도움을 줘요.");
                            textviewOrange.setText("주황 / 자신감없음 "+emotionCount.getBad2()+" 회\n활기가 넘치고 자신감을 충전해 주는 색이에요.\n머리가 복잡하고 진행이 잘 안될 때 도움을 줘요.");
                            textviewYellow.setText("노랑 / 우울 "+emotionCount.getBad3()+" 회\n창의력을 자극하여 더 좋은 선택을 할 수 있게 도와줘요.\n의욕이 없을 때나 중요한 결정사항이 있을 때 추천해요.");
                            textviewGreen.setText("초록 / 불안정 "+emotionCount.getBad4()+" 회\n스트레스와 긴장 완화에 효과가 있어요.\n상처받고 안정이 필요할 때 도움을 줘요.");
                            textviewBlue.setText("파랑 / 긴장 "+emotionCount.getBad5()+" 회\n긴장을 풀어주고 진정시켜줘요.\n누군가와 관계 개선을 할 때 추천해요.");
                            textviewPurple.setText("보라 / 지침 "+emotionCount.getBad6()+" 회\n비전과 직관성을 이끌어 줘요.\n창의력과 신중함이 필요할 때 도움을 줘요.");
                            textviewPink.setText("핑크 / 혼란스러움 "+emotionCount.getBad7()+" 회\n포근하고 온순한 기운을 전달해 줘요.\n마음이 혼란할 때 추천해요.");
                            textviewWhite.setText("하양 / 화남 "+emotionCount.getBad8()+" 회\n몸이 필요로 하는 에너지를 공급해 줘요.\n화가 난 감정을 다스리는 데 도움을 줘요.");


                            BarChart mBarChart = (BarChart) findViewById(R.id.barchart);


                            mBarChart.addBar(new BarModel("무기력",emotionCount.getBad1(), 0xFFffbfbf));
                            mBarChart.addBar(new BarModel("자신감 없음",emotionCount.getBad2(),  0xFFfedb99));
                            mBarChart.addBar(new BarModel("우울",emotionCount.getBad3(), 0xFFffffbf));
                            mBarChart.addBar(new BarModel("불안정",emotionCount.getBad4(), 0xFFdffebf));
                            mBarChart.addBar(new BarModel("긴장",emotionCount.getBad5(), 0xFFdedeff));
                            mBarChart.addBar(new BarModel("지침",emotionCount.getBad6(),  0xFFe9cdfe));
                            mBarChart.addBar(new BarModel("혼란스러움",emotionCount.getBad7(), 0xFFffe1f1));
                            mBarChart.addBar(new BarModel("화남",emotionCount.getBad8(),  0xFFcccccc));


                            mBarChart.startAnimation();

                        } else {


                        }

                    }
                });



        return diarySave;
    }
}