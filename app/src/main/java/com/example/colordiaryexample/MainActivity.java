package com.example.colordiaryexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    NotificationManager notificationManager;
    ListView dlistview;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    File localFile;
    Bitmap mBitmap;
    NavigationView navigationView;
    String result = "";
    int goodEmotion = 0;
    int badEmotion = 0;
    int badPastEmotion = 0;
    ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();
    List<DiaryInfo> diarySave = new ArrayList<>();
    List<MemberInfo> diaryMember = new ArrayList<>();



    PendingIntent intent;




    private MenuItem item;

    ImageView navi_image;
    TextView navi_title;
    TextView navi_subtitle;
    int count=0;
    public SharedPreferences prefs;
    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


   //     prefs = getSharedPreferences("Pref", MODE_PRIVATE);
   //     checkFirstRun();

        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        //파일 접근권한 부여
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "권한 설정 완료");
        } else {
            Log.d(TAG, "권한 설정 요청");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        //만약 로그인이 되어있지 않다면
        if (user == null) {
            startLoginpActivity();
            finish();
        } else { //로그인 상태일 때
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) { //db에 사용자 정보가 있다면
                            diarySave = getAll();
                            diaryMember = getMember();

                            try {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();

                                localFile = File.createTempFile("images", "jpg");
                                final StorageReference riversRef = storageRef.child("users/" + user.getUid() + "/Profile Image");
                                riversRef.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                // Successfully downloaded data to local file
                                                // ...
                                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                navi_image.setImageBitmap(bitmap);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle failed download
                                        // ...
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else { //db에 사용자 정보가 없다면
                            Log.d(TAG, "No such document");
                            memberInitActivity();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());

                    }
                }
            });
        }

        dlistview = findViewById(R.id.item_view);
        DiaryAdapter adapter = new DiaryAdapter(MainActivity.this, R.layout.list_diary, diarySave);
        dlistview.setAdapter(adapter);


        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DiaryAdapter adapter = (DiaryAdapter) dlistview.getAdapter();
                adapter.diarys = getAll();
                diarySave = adapter.diarys;
                setSupportActionBar(toolbar);
                adapter.notifyDataSetChanged();
                diaryMember = getMember();
                swipeRefreshLayout.setRefreshing(false);

                try {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    localFile = File.createTempFile("images", "jpg");
                    final StorageReference riversRef = storageRef.child("users/" + user.getUid() + "/Profile Image");
                    riversRef.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Successfully downloaded data to local file
                                    // ...
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    navi_image.setImageBitmap(bitmap);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navi_image = findViewById(R.id.navi_image);
        navi_title = findViewById(R.id.navi_title);
        navi_subtitle = findViewById(R.id.navi_subtitle);

     //   diarySave = getAll();
      //  diaryMember = getMember();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);
                mDrawerLayout.closeDrawers();
                int id = item.getItemId();
                String title = item.getTitle().toString();

                if (id == R.id.account) {
                    startMemberDetailActivity();
                } else if (id == R.id.setting) {
                    startShareActivity();
                    finish();
                } else if (id == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    startLoginpActivity();
                    finish();
                } else if (id == R.id.selectedEmotion){
                    startStatisticsActivity();
                    finish();
                }

                return true;


            }
        });


        findViewById(R.id.fab).setOnClickListener(onClickListener);



    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }



/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startLoginpActivity();
            finish();
        } else if (id == R.id.sharing){
            startShareActivity();
        }
        return super.onOptionsItemSelected(item);
    }
*/
    //로그아웃 버튼 클릭시
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fab:

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final ArrayList<Map<String, Object>> diaryM = new ArrayList<Map<String, Object>>();
                    DocumentReference docRef = db.collection("users").document(user.getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    diaryM.add(document.getData());
                                    MemberInfo memberInfo = new MemberInfo((String)diaryM.get(0).get("name"), (String)diaryM.get(0).get("phoneNumber"), (String)diaryM.get(0).get("birthDay"),(String)diaryM.get(0).get("address"),(String)diaryM.get(0).get("profileLink"));
                                    Intent intent = new Intent(MainActivity.this, AddDiaryActivity.class);
                                    intent.putExtra("birthday",memberInfo.getBirthDay());
                                    Log.d("ffffffffffffffffffffff",memberInfo.getBirthDay());
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });




            }
        }
    };


    //로그인 액티비티로 넘어감
    private void startLoginpActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //회원정보 등록 액티비티로 넘어감
    private void memberInitActivity() {
        Intent intent = new Intent(this, MemberInitActivity.class);
        startActivity(intent);
    }
    //공유페이지로 넘어감
    private void startShareActivity() {
        Intent intent = new Intent(this, ShareActivity.class);
        startActivity(intent);
    }
    private void startMemberDetailActivity() {
        Intent intent = new Intent(this, MemberDetailActivity.class);
        startActivity(intent);
        finish();
    }
    private void startStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);

        startActivity(intent);
    }



    @Override
    public void onResume() {
        super.onResume();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DiaryAdapter adapter = (DiaryAdapter) dlistview.getAdapter();
        adapter.diarys = getAll();
        this.diarySave = adapter.diarys;
        adapter.notifyDataSetChanged();


    }

    public List<DiaryInfo> getAll() {
        final List<DiaryInfo> diarySave = new ArrayList<>();
        final List<DiaryInfo> diarySaving = new ArrayList<>();
        final ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("files/uid");

        diaryMember = getMember();

        db.collection("diarys/"+user.getUid()+"/diarys")
              //  .orderBy("db_date",Query.Direction.DESCENDING)
               .orderBy("db_time",Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                diarys.add(document.getData());
                            }
                            for (int i=diarys.size()-1;i>-1;i--) {
                                DiaryInfo diaryInfo = new DiaryInfo((String)diarys.get(i).get("title"),(String)diarys.get(i).get("date"),(String)diarys.get(i).get("content"),(String)diarys.get(i).get("picture"),(String)diarys.get(i).get("db_date"),(String)diarys.get(i).get("db_time"),(String)diarys.get(i).get("pre_emotion"),(String)diarys.get(i).get("birthday"));
                                diarySave.add(diaryInfo);
                               // Log.d("갯수확인", String.valueOf(i));
                            }

                           // Log.d("퀘스트값", diarySave.get(0).getPre_emotion());
                            try {
                                result = diarySave.get(0).getPre_emotion();
                                dlistview = findViewById(R.id.item_view);
                                Toolbar toolbar = findViewById(R.id.toolbar);
                                DiaryAdapter adapter = new DiaryAdapter(MainActivity.this, R.layout.list_diary, diarySave);
                                dlistview.setAdapter(adapter);
                                Log.d("result값", result);
                                switch (result) {
                                    case "무기력":
                                        toolbar.setBackgroundColor(Color.parseColor("#f28783"));
                                        dlistview.setBackgroundResource(R.drawable.red);
                                        break;
                                    case "자신감없음":
                                        toolbar.setBackgroundColor(Color.parseColor("#f7aa6f"));
                                        dlistview.setBackgroundResource(R.drawable.orange);
                                        break;
                                    case "우울":
                                        toolbar.setBackgroundColor(Color.parseColor("#ebeb63"));
                                        dlistview.setBackgroundResource(R.drawable.yellow);
                                        break;
                                    case "불안정":
                                        toolbar.setBackgroundColor(Color.parseColor("#b8fc81"));
                                        dlistview.setBackgroundResource(R.drawable.green);
                                        break;
                                    case "긴장":
                                        toolbar.setBackgroundColor(Color.parseColor("#a8a6f7"));
                                        dlistview.setBackgroundResource(R.drawable.blue);
                                        break;
                                    case "지침":
                                        toolbar.setBackgroundColor(Color.parseColor("#ea94f7"));
                                        dlistview.setBackgroundResource(R.drawable.purple);
                                        break;
                                    case "혼란스러움":
                                        toolbar.setBackgroundColor(Color.parseColor("#f7b0f6"));
                                        dlistview.setBackgroundResource(R.drawable.pink);
                                        break;
                                    case "화남":
                                        toolbar.setBackgroundColor(Color.parseColor("#d0c7d1"));
                                        dlistview.setBackgroundResource(R.drawable.white);
                                        break;
                                    default:
                                        toolbar.setBackgroundColor(Color.parseColor("#EFAAAA"));
                                        dlistview.setBackgroundColor(Color.parseColor("#F6E5CC"));
                                        break;
                                }
                            }catch (IndexOutOfBoundsException e){
                                Log.d("모모모","없음");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                    }
                });

        return diarySave;
    }

    public List<DiaryInfo> getCall() {
        final List<DiaryInfo> diarySave = new ArrayList<>();

        final ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("files/uid");

        db.collection("diarys/"+user.getUid()+"/diarys")
                //  .orderBy("db_date",Query.Direction.DESCENDING)
                .orderBy("db_time",Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                diarys.add(document.getData());
                            }
                            for (int i=diarys.size()-1;i>-1;i--) {
                                DiaryInfo diaryInfo = new DiaryInfo((String)diarys.get(i).get("title"),(String)diarys.get(i).get("date"),(String)diarys.get(i).get("content"),(String)diarys.get(i).get("picture"),(String)diarys.get(i).get("db_date"),(String)diarys.get(i).get("db_time"),(String)diarys.get(i).get("pre_emotion"),(String)diarys.get(i).get("birthday"));
                                diarySave.add(diaryInfo);
                           }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                    }
                });

        return diarySave;
    }


        public List<MemberInfo> getMember(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final List<MemberInfo> diaryMember = new ArrayList<>();
        final ArrayList<Map<String, Object>> diaryM = new ArrayList<Map<String, Object>>();

        DocumentReference docRef = db.collection("users").document(user.getUid());



        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        diaryM.add(document.getData());
                        MemberInfo memberInfo = new MemberInfo((String)diaryM.get(0).get("name"), (String)diaryM.get(0).get("phoneNumber"), (String)diaryM.get(0).get("birthDay"),(String)diaryM.get(0).get("address"),(String)diaryM.get(0).get("profileLink"));

                        navi_image = findViewById(R.id.navi_image);
                        navi_title = findViewById(R.id.navi_title);
                        navi_subtitle = findViewById(R.id.navi_subtitle);

                        navi_title.setText(memberInfo.getName());
                        navi_subtitle.setText(memberInfo.getPhoneNumber());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        return diaryMember;
    }
    


}